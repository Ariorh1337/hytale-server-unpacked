/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.AbstractReferenceCountedByteBuf;
import io.netty.buffer.AdaptiveByteBufAllocator;
import io.netty.buffer.AllocateBufferEvent;
import io.netty.buffer.AllocateChunkEvent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.ChunkInfo;
import io.netty.buffer.FreeBufferEvent;
import io.netty.buffer.FreeChunkEvent;
import io.netty.buffer.ReallocateBufferEvent;
import io.netty.buffer.ReturnChunkEvent;
import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.NettyRuntime;
import io.netty.util.Recycler;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;
import io.netty.util.concurrent.MpscIntQueue;
import io.netty.util.internal.ObjectPool;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.RefCnt;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ThreadExecutorMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.StampedLock;
import java.util.function.IntSupplier;

final class AdaptivePoolingAllocator {
    private static final int LOW_MEM_THRESHOLD = 0x20000000;
    private static final boolean IS_LOW_MEM = Runtime.getRuntime().maxMemory() <= 0x20000000L;
    private static final boolean DISABLE_THREAD_LOCAL_MAGAZINES_ON_LOW_MEM = SystemPropertyUtil.getBoolean("io.netty.allocator.disableThreadLocalMagazinesOnLowMemory", true);
    static final int MIN_CHUNK_SIZE = 131072;
    private static final int EXPANSION_ATTEMPTS = 3;
    private static final int INITIAL_MAGAZINES = 1;
    private static final int RETIRE_CAPACITY = 256;
    private static final int MAX_STRIPES = IS_LOW_MEM ? 1 : NettyRuntime.availableProcessors() * 2;
    private static final int BUFS_PER_CHUNK = 8;
    private static final int MAX_CHUNK_SIZE = IS_LOW_MEM ? 0x200000 : 0x800000;
    private static final int MAX_POOLED_BUF_SIZE = MAX_CHUNK_SIZE / 8;
    private static final int CHUNK_REUSE_QUEUE = Math.max(2, SystemPropertyUtil.getInt("io.netty.allocator.chunkReuseQueueCapacity", NettyRuntime.availableProcessors() * 2));
    private static final int MAGAZINE_BUFFER_QUEUE_CAPACITY = SystemPropertyUtil.getInt("io.netty.allocator.magazineBufferQueueCapacity", 1024);
    private static final int[] SIZE_CLASSES = new int[]{32, 64, 128, 256, 512, 640, 1024, 1152, 2048, 2304, 4096, 4352, 8192, 8704, 16384, 16896, 32768, 65536};
    private static final int SIZE_CLASSES_COUNT = SIZE_CLASSES.length;
    private static final byte[] SIZE_INDEXES = new byte[SIZE_CLASSES[SIZE_CLASSES_COUNT - 1] / 32 + 1];
    private final ChunkAllocator chunkAllocator;
    private final ChunkRegistry chunkRegistry;
    private final MagazineGroup[] sizeClassedMagazineGroups;
    private final MagazineGroup largeBufferMagazineGroup;
    private final FastThreadLocal<MagazineGroup[]> threadLocalGroup;

    AdaptivePoolingAllocator(ChunkAllocator chunkAllocator, final boolean useCacheForNonEventLoopThreads) {
        this.chunkAllocator = ObjectUtil.checkNotNull(chunkAllocator, "chunkAllocator");
        this.chunkRegistry = new ChunkRegistry();
        this.sizeClassedMagazineGroups = AdaptivePoolingAllocator.createMagazineGroupSizeClasses(this, false);
        this.largeBufferMagazineGroup = new MagazineGroup(this, chunkAllocator, new HistogramChunkControllerFactory(true), false);
        boolean disableThreadLocalGroups = IS_LOW_MEM && DISABLE_THREAD_LOCAL_MAGAZINES_ON_LOW_MEM;
        this.threadLocalGroup = disableThreadLocalGroups ? null : new FastThreadLocal<MagazineGroup[]>(){

            @Override
            protected MagazineGroup[] initialValue() {
                if (useCacheForNonEventLoopThreads || ThreadExecutorMap.currentExecutor() != null) {
                    return AdaptivePoolingAllocator.createMagazineGroupSizeClasses(AdaptivePoolingAllocator.this, true);
                }
                return null;
            }

            @Override
            protected void onRemoval(MagazineGroup[] groups) throws Exception {
                if (groups != null) {
                    for (MagazineGroup group : groups) {
                        group.free();
                    }
                }
            }
        };
    }

    private static MagazineGroup[] createMagazineGroupSizeClasses(AdaptivePoolingAllocator allocator, boolean isThreadLocal) {
        MagazineGroup[] groups = new MagazineGroup[SIZE_CLASSES.length];
        for (int i = 0; i < SIZE_CLASSES.length; ++i) {
            int segmentSize = SIZE_CLASSES[i];
            groups[i] = new MagazineGroup(allocator, allocator.chunkAllocator, new SizeClassChunkControllerFactory(segmentSize), isThreadLocal);
        }
        return groups;
    }

    private static Queue<Chunk> createSharedChunkQueue() {
        return PlatformDependent.newFixedMpmcQueue(CHUNK_REUSE_QUEUE);
    }

    ByteBuf allocate(int size, int maxCapacity) {
        return this.allocate(size, maxCapacity, Thread.currentThread(), null);
    }

    private AdaptiveByteBuf allocate(int size, int maxCapacity, Thread currentThread, AdaptiveByteBuf buf) {
        AdaptiveByteBuf allocated = null;
        if (size <= MAX_POOLED_BUF_SIZE) {
            MagazineGroup[] magazineGroups;
            int index = AdaptivePoolingAllocator.sizeClassIndexOf(size);
            if (!FastThreadLocalThread.currentThreadWillCleanupFastThreadLocals() || IS_LOW_MEM || (magazineGroups = this.threadLocalGroup.get()) == null) {
                magazineGroups = this.sizeClassedMagazineGroups;
            }
            if (index < magazineGroups.length) {
                allocated = magazineGroups[index].allocate(size, maxCapacity, currentThread, buf);
            } else if (!IS_LOW_MEM) {
                allocated = this.largeBufferMagazineGroup.allocate(size, maxCapacity, currentThread, buf);
            }
        }
        if (allocated == null) {
            allocated = this.allocateFallback(size, maxCapacity, currentThread, buf);
        }
        return allocated;
    }

    private static int sizeIndexOf(int size) {
        return size + 31 >> 5;
    }

    static int sizeClassIndexOf(int size) {
        int sizeIndex = AdaptivePoolingAllocator.sizeIndexOf(size);
        if (sizeIndex < SIZE_INDEXES.length) {
            return SIZE_INDEXES[sizeIndex];
        }
        return SIZE_CLASSES_COUNT;
    }

    static int[] getSizeClasses() {
        return (int[])SIZE_CLASSES.clone();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private AdaptiveByteBuf allocateFallback(int size, int maxCapacity, Thread currentThread, AdaptiveByteBuf buf) {
        Magazine magazine;
        if (buf != null) {
            Chunk chunk = buf.chunk;
            if (chunk == null || chunk == Magazine.MAGAZINE_FREED || (magazine = chunk.currentMagazine()) == null) {
                magazine = this.getFallbackMagazine(currentThread);
            }
        } else {
            magazine = this.getFallbackMagazine(currentThread);
            buf = magazine.newBuffer();
        }
        AbstractByteBuf innerChunk = this.chunkAllocator.allocate(size, maxCapacity);
        Chunk chunk = new Chunk(innerChunk, magazine, false, chunkSize -> true);
        this.chunkRegistry.add(chunk);
        try {
            chunk.readInitInto(buf, size, size, maxCapacity);
        }
        finally {
            chunk.release();
        }
        return buf;
    }

    private Magazine getFallbackMagazine(Thread currentThread) {
        Magazine[] mags = this.largeBufferMagazineGroup.magazines;
        return mags[(int)currentThread.getId() & mags.length - 1];
    }

    void reallocate(int size, int maxCapacity, AdaptiveByteBuf into) {
        AdaptiveByteBuf result = this.allocate(size, maxCapacity, Thread.currentThread(), into);
        assert (result == into) : "Re-allocation created separate buffer instance";
    }

    long usedMemory() {
        return this.chunkRegistry.totalCapacity();
    }

    protected void finalize() throws Throwable {
        try {
            super.finalize();
        }
        finally {
            this.free();
        }
    }

    private void free() {
        this.largeBufferMagazineGroup.free();
    }

    static int sizeToBucket(int size) {
        return HistogramChunkController.sizeToBucket(size);
    }

    static {
        if (MAGAZINE_BUFFER_QUEUE_CAPACITY < 2) {
            throw new IllegalArgumentException("MAGAZINE_BUFFER_QUEUE_CAPACITY: " + MAGAZINE_BUFFER_QUEUE_CAPACITY + " (expected: >= " + 2 + ')');
        }
        int lastIndex = 0;
        for (int i = 0; i < SIZE_CLASSES_COUNT; ++i) {
            int sizeClass = SIZE_CLASSES[i];
            assert ((sizeClass & 5) == 0) : "Size class must be a multiple of 32";
            int sizeIndex = AdaptivePoolingAllocator.sizeIndexOf(sizeClass);
            Arrays.fill(SIZE_INDEXES, lastIndex + 1, sizeIndex + 1, (byte)i);
            lastIndex = sizeIndex;
        }
    }

    static interface ChunkAllocator {
        public AbstractByteBuf allocate(int var1, int var2);
    }

    static final class AdaptiveByteBuf
    extends AbstractReferenceCountedByteBuf {
        private final ObjectPool.Handle<AdaptiveByteBuf> handle;
        private int startIndex;
        private AbstractByteBuf rootParent;
        Chunk chunk;
        private int length;
        private int maxFastCapacity;
        private ByteBuffer tmpNioBuf;
        private boolean hasArray;
        private boolean hasMemoryAddress;

        AdaptiveByteBuf(ObjectPool.Handle<AdaptiveByteBuf> recyclerHandle) {
            super(0);
            this.handle = ObjectUtil.checkNotNull(recyclerHandle, "recyclerHandle");
        }

        void init(AbstractByteBuf unwrapped, Chunk wrapped, int readerIndex, int writerIndex, int startIndex, int size, int capacity, int maxCapacity) {
            AllocateBufferEvent event;
            this.startIndex = startIndex;
            this.chunk = wrapped;
            this.length = size;
            this.maxFastCapacity = capacity;
            this.maxCapacity(maxCapacity);
            this.setIndex0(readerIndex, writerIndex);
            this.hasArray = unwrapped.hasArray();
            this.hasMemoryAddress = unwrapped.hasMemoryAddress();
            this.rootParent = unwrapped;
            this.tmpNioBuf = null;
            if (PlatformDependent.isJfrEnabled() && AllocateBufferEvent.isEventEnabled() && (event = new AllocateBufferEvent()).shouldCommit()) {
                event.fill(this, AdaptiveByteBufAllocator.class);
                event.chunkPooled = wrapped.pooled;
                Magazine m = wrapped.magazine;
                event.chunkThreadLocal = m != null && m.allocationLock == null;
                event.commit();
            }
        }

        private AbstractByteBuf rootParent() {
            AbstractByteBuf rootParent = this.rootParent;
            if (rootParent != null) {
                return rootParent;
            }
            throw new IllegalReferenceCountException();
        }

        @Override
        public int capacity() {
            return this.length;
        }

        @Override
        public int maxFastWritableBytes() {
            return Math.min(this.maxFastCapacity, this.maxCapacity()) - this.writerIndex;
        }

        @Override
        public ByteBuf capacity(int newCapacity) {
            ReallocateBufferEvent event;
            if (this.length <= newCapacity && newCapacity <= this.maxFastCapacity) {
                this.ensureAccessible();
                this.length = newCapacity;
                return this;
            }
            this.checkNewCapacity(newCapacity);
            if (newCapacity < this.capacity()) {
                this.length = newCapacity;
                this.trimIndicesToCapacity(newCapacity);
                return this;
            }
            if (PlatformDependent.isJfrEnabled() && ReallocateBufferEvent.isEventEnabled() && (event = new ReallocateBufferEvent()).shouldCommit()) {
                event.fill(this, AdaptiveByteBufAllocator.class);
                event.newCapacity = newCapacity;
                event.commit();
            }
            Chunk chunk = this.chunk;
            AdaptivePoolingAllocator allocator = chunk.allocator;
            int readerIndex = this.readerIndex;
            int writerIndex = this.writerIndex;
            int baseOldRootIndex = this.startIndex;
            int oldCapacity = this.length;
            AbstractByteBuf oldRoot = this.rootParent();
            allocator.reallocate(newCapacity, this.maxCapacity(), this);
            oldRoot.getBytes(baseOldRootIndex, this, 0, oldCapacity);
            chunk.releaseSegment(baseOldRootIndex);
            this.readerIndex = readerIndex;
            this.writerIndex = writerIndex;
            return this;
        }

        @Override
        public ByteBufAllocator alloc() {
            return this.rootParent().alloc();
        }

        @Override
        public ByteOrder order() {
            return this.rootParent().order();
        }

        @Override
        public ByteBuf unwrap() {
            return null;
        }

        @Override
        public boolean isDirect() {
            return this.rootParent().isDirect();
        }

        @Override
        public int arrayOffset() {
            return this.idx(this.rootParent().arrayOffset());
        }

        @Override
        public boolean hasMemoryAddress() {
            return this.hasMemoryAddress;
        }

        @Override
        public long memoryAddress() {
            this.ensureAccessible();
            return this._memoryAddress();
        }

        @Override
        long _memoryAddress() {
            AbstractByteBuf root = this.rootParent;
            return root != null ? root._memoryAddress() + (long)this.startIndex : 0L;
        }

        @Override
        public ByteBuffer nioBuffer(int index, int length) {
            this.checkIndex(index, length);
            return this.rootParent().nioBuffer(this.idx(index), length);
        }

        @Override
        public ByteBuffer internalNioBuffer(int index, int length) {
            this.checkIndex(index, length);
            return (ByteBuffer)this.internalNioBuffer().position(index).limit(index + length);
        }

        private ByteBuffer internalNioBuffer() {
            if (this.tmpNioBuf == null) {
                this.tmpNioBuf = this.rootParent().nioBuffer(this.startIndex, this.maxFastCapacity);
            }
            return (ByteBuffer)this.tmpNioBuf.clear();
        }

        @Override
        public ByteBuffer[] nioBuffers(int index, int length) {
            this.checkIndex(index, length);
            return this.rootParent().nioBuffers(this.idx(index), length);
        }

        @Override
        public boolean hasArray() {
            return this.hasArray;
        }

        @Override
        public byte[] array() {
            this.ensureAccessible();
            return this.rootParent().array();
        }

        @Override
        public ByteBuf copy(int index, int length) {
            this.checkIndex(index, length);
            return this.rootParent().copy(this.idx(index), length);
        }

        @Override
        public int nioBufferCount() {
            return this.rootParent().nioBufferCount();
        }

        @Override
        protected byte _getByte(int index) {
            return this.rootParent()._getByte(this.idx(index));
        }

        @Override
        protected short _getShort(int index) {
            return this.rootParent()._getShort(this.idx(index));
        }

        @Override
        protected short _getShortLE(int index) {
            return this.rootParent()._getShortLE(this.idx(index));
        }

        @Override
        protected int _getUnsignedMedium(int index) {
            return this.rootParent()._getUnsignedMedium(this.idx(index));
        }

        @Override
        protected int _getUnsignedMediumLE(int index) {
            return this.rootParent()._getUnsignedMediumLE(this.idx(index));
        }

        @Override
        protected int _getInt(int index) {
            return this.rootParent()._getInt(this.idx(index));
        }

        @Override
        protected int _getIntLE(int index) {
            return this.rootParent()._getIntLE(this.idx(index));
        }

        @Override
        protected long _getLong(int index) {
            return this.rootParent()._getLong(this.idx(index));
        }

        @Override
        protected long _getLongLE(int index) {
            return this.rootParent()._getLongLE(this.idx(index));
        }

        @Override
        public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
            this.checkIndex(index, length);
            this.rootParent().getBytes(this.idx(index), dst, dstIndex, length);
            return this;
        }

        @Override
        public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
            this.checkIndex(index, length);
            this.rootParent().getBytes(this.idx(index), dst, dstIndex, length);
            return this;
        }

        @Override
        public ByteBuf getBytes(int index, ByteBuffer dst) {
            this.checkIndex(index, dst.remaining());
            this.rootParent().getBytes(this.idx(index), dst);
            return this;
        }

        @Override
        protected void _setByte(int index, int value) {
            this.rootParent()._setByte(this.idx(index), value);
        }

        @Override
        protected void _setShort(int index, int value) {
            this.rootParent()._setShort(this.idx(index), value);
        }

        @Override
        protected void _setShortLE(int index, int value) {
            this.rootParent()._setShortLE(this.idx(index), value);
        }

        @Override
        protected void _setMedium(int index, int value) {
            this.rootParent()._setMedium(this.idx(index), value);
        }

        @Override
        protected void _setMediumLE(int index, int value) {
            this.rootParent()._setMediumLE(this.idx(index), value);
        }

        @Override
        protected void _setInt(int index, int value) {
            this.rootParent()._setInt(this.idx(index), value);
        }

        @Override
        protected void _setIntLE(int index, int value) {
            this.rootParent()._setIntLE(this.idx(index), value);
        }

        @Override
        protected void _setLong(int index, long value) {
            this.rootParent()._setLong(this.idx(index), value);
        }

        @Override
        protected void _setLongLE(int index, long value) {
            this.rootParent().setLongLE(this.idx(index), value);
        }

        @Override
        public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
            this.checkIndex(index, length);
            ByteBuffer tmp = (ByteBuffer)this.internalNioBuffer().clear().position(index);
            tmp.put(src, srcIndex, length);
            return this;
        }

        @Override
        public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
            this.checkIndex(index, length);
            ByteBuffer tmp = (ByteBuffer)this.internalNioBuffer().clear().position(index);
            tmp.put(src.nioBuffer(srcIndex, length));
            return this;
        }

        @Override
        public ByteBuf setBytes(int index, ByteBuffer src) {
            this.checkIndex(index, src.remaining());
            ByteBuffer tmp = (ByteBuffer)this.internalNioBuffer().clear().position(index);
            tmp.put(src);
            return this;
        }

        @Override
        public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
            this.checkIndex(index, length);
            if (length != 0) {
                ByteBufUtil.readBytes(this.alloc(), this.internalNioBuffer().duplicate(), index, length, out);
            }
            return this;
        }

        @Override
        public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
            ByteBuffer buf = this.internalNioBuffer().duplicate();
            buf.clear().position(index).limit(index + length);
            return out.write(buf);
        }

        @Override
        public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
            ByteBuffer buf = this.internalNioBuffer().duplicate();
            buf.clear().position(index).limit(index + length);
            return out.write(buf, position);
        }

        @Override
        public int setBytes(int index, InputStream in, int length) throws IOException {
            this.checkIndex(index, length);
            AbstractByteBuf rootParent = this.rootParent();
            if (rootParent.hasArray()) {
                return rootParent.setBytes(this.idx(index), in, length);
            }
            byte[] tmp = ByteBufUtil.threadLocalTempArray(length);
            int readBytes = in.read(tmp, 0, length);
            if (readBytes <= 0) {
                return readBytes;
            }
            this.setBytes(index, tmp, 0, readBytes);
            return readBytes;
        }

        @Override
        public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
            try {
                return in.read(this.internalNioBuffer(index, length));
            }
            catch (ClosedChannelException ignored) {
                return -1;
            }
        }

        @Override
        public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
            try {
                return in.read(this.internalNioBuffer(index, length), position);
            }
            catch (ClosedChannelException ignored) {
                return -1;
            }
        }

        @Override
        public int setCharSequence(int index, CharSequence sequence, Charset charset) {
            return this.setCharSequence0(index, sequence, charset, false);
        }

        private int setCharSequence0(int index, CharSequence sequence, Charset charset, boolean expand) {
            if (charset.equals(CharsetUtil.UTF_8)) {
                int length = ByteBufUtil.utf8MaxBytes(sequence);
                if (expand) {
                    this.ensureWritable0(length);
                    this.checkIndex0(index, length);
                } else {
                    this.checkIndex(index, length);
                }
                return ByteBufUtil.writeUtf8(this, index, length, sequence, sequence.length());
            }
            if (charset.equals(CharsetUtil.US_ASCII) || charset.equals(CharsetUtil.ISO_8859_1)) {
                int length = sequence.length();
                if (expand) {
                    this.ensureWritable0(length);
                    this.checkIndex0(index, length);
                } else {
                    this.checkIndex(index, length);
                }
                return ByteBufUtil.writeAscii(this, index, sequence, length);
            }
            byte[] bytes = sequence.toString().getBytes(charset);
            if (expand) {
                this.ensureWritable0(bytes.length);
            }
            this.setBytes(index, bytes);
            return bytes.length;
        }

        @Override
        public int writeCharSequence(CharSequence sequence, Charset charset) {
            int written = this.setCharSequence0(this.writerIndex, sequence, charset, true);
            this.writerIndex += written;
            return written;
        }

        @Override
        public int forEachByte(int index, int length, ByteProcessor processor) {
            this.checkIndex(index, length);
            int ret = this.rootParent().forEachByte(this.idx(index), length, processor);
            return this.forEachResult(ret);
        }

        @Override
        public int forEachByteDesc(int index, int length, ByteProcessor processor) {
            this.checkIndex(index, length);
            int ret = this.rootParent().forEachByteDesc(this.idx(index), length, processor);
            return this.forEachResult(ret);
        }

        @Override
        public ByteBuf setZero(int index, int length) {
            this.checkIndex(index, length);
            this.rootParent().setZero(this.idx(index), length);
            return this;
        }

        @Override
        public ByteBuf writeZero(int length) {
            this.ensureWritable(length);
            this.rootParent().setZero(this.idx(this.writerIndex), length);
            this.writerIndex += length;
            return this;
        }

        private int forEachResult(int ret) {
            if (ret < this.startIndex) {
                return -1;
            }
            return ret - this.startIndex;
        }

        @Override
        public boolean isContiguous() {
            return this.rootParent().isContiguous();
        }

        private int idx(int index) {
            return index + this.startIndex;
        }

        @Override
        protected void deallocate() {
            FreeBufferEvent event;
            if (PlatformDependent.isJfrEnabled() && FreeBufferEvent.isEventEnabled() && (event = new FreeBufferEvent()).shouldCommit()) {
                event.fill(this, AdaptiveByteBufAllocator.class);
                event.commit();
            }
            if (this.chunk != null) {
                this.chunk.releaseSegment(this.startIndex);
            }
            this.tmpNioBuf = null;
            this.chunk = null;
            this.rootParent = null;
            if (this.handle instanceof Recycler.EnhancedHandle) {
                Recycler.EnhancedHandle enhancedHandle = (Recycler.EnhancedHandle)this.handle;
                enhancedHandle.unguardedRecycle(this);
            } else {
                this.handle.recycle(this);
            }
        }
    }

    private static final class SizeClassedChunk
    extends Chunk {
        private static final int FREE_LIST_EMPTY = -1;
        private final int segmentSize;
        private final MpscIntQueue freeList;

        SizeClassedChunk(AbstractByteBuf delegate, Magazine magazine, boolean pooled, int segmentSize, final int[] segmentOffsets, ChunkReleasePredicate shouldReleaseChunk) {
            super(delegate, magazine, pooled, shouldReleaseChunk);
            this.segmentSize = segmentSize;
            int segmentCount = segmentOffsets.length;
            assert (delegate.capacity() / segmentSize == segmentCount);
            assert (segmentCount > 0) : "Chunk must have a positive number of segments";
            this.freeList = MpscIntQueue.create(segmentCount, -1);
            this.freeList.fill(segmentCount, new IntSupplier(){
                int counter;

                @Override
                public int getAsInt() {
                    return segmentOffsets[this.counter++];
                }
            });
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void readInitInto(AdaptiveByteBuf buf, int size, int startingCapacity, int maxCapacity) {
            int startIndex = this.freeList.poll();
            if (startIndex == -1) {
                throw new IllegalStateException("Free list is empty");
            }
            this.allocatedBytes += this.segmentSize;
            SizeClassedChunk chunk = this;
            ((Chunk)chunk).retain();
            try {
                buf.init(this.delegate, chunk, 0, 0, startIndex, size, startingCapacity, maxCapacity);
                chunk = null;
            }
            finally {
                if (chunk != null) {
                    this.allocatedBytes -= this.segmentSize;
                    ((Chunk)chunk).releaseSegment(startIndex);
                }
            }
        }

        @Override
        public int remainingCapacity() {
            int remainingCapacity = super.remainingCapacity();
            if (remainingCapacity > this.segmentSize) {
                return remainingCapacity;
            }
            int updatedRemainingCapacity = this.freeList.size() * this.segmentSize;
            if (updatedRemainingCapacity == remainingCapacity) {
                return remainingCapacity;
            }
            this.allocatedBytes = this.capacity() - updatedRemainingCapacity;
            return updatedRemainingCapacity;
        }

        @Override
        boolean releaseFromMagazine() {
            Magazine mag = this.magazine;
            this.detachFromMagazine();
            if (!mag.offerToQueue(this)) {
                return super.releaseFromMagazine();
            }
            return false;
        }

        @Override
        boolean releaseSegment(int startIndex) {
            boolean released = this.release();
            boolean segmentReturned = this.freeList.offer(startIndex);
            assert (segmentReturned) : "Unable to return segment " + startIndex + " to free list";
            return released;
        }
    }

    private static class Chunk
    implements ChunkInfo {
        protected final AbstractByteBuf delegate;
        protected Magazine magazine;
        private final AdaptivePoolingAllocator allocator;
        private final ChunkReleasePredicate chunkReleasePredicate;
        private final RefCnt refCnt = new RefCnt();
        private final int capacity;
        private final boolean pooled;
        protected int allocatedBytes;

        Chunk() {
            this.delegate = null;
            this.magazine = null;
            this.allocator = null;
            this.chunkReleasePredicate = null;
            this.capacity = 0;
            this.pooled = false;
        }

        Chunk(AbstractByteBuf delegate, Magazine magazine, boolean pooled, ChunkReleasePredicate chunkReleasePredicate) {
            AllocateChunkEvent event;
            this.delegate = delegate;
            this.pooled = pooled;
            this.capacity = delegate.capacity();
            this.attachToMagazine(magazine);
            this.allocator = magazine.group.allocator;
            this.chunkReleasePredicate = chunkReleasePredicate;
            if (PlatformDependent.isJfrEnabled() && AllocateChunkEvent.isEventEnabled() && (event = new AllocateChunkEvent()).shouldCommit()) {
                event.fill(this, AdaptiveByteBufAllocator.class);
                event.pooled = pooled;
                event.threadLocal = magazine.allocationLock == null;
                event.commit();
            }
        }

        Magazine currentMagazine() {
            return this.magazine;
        }

        void detachFromMagazine() {
            if (this.magazine != null) {
                this.magazine = null;
            }
        }

        void attachToMagazine(Magazine magazine) {
            assert (this.magazine == null);
            this.magazine = magazine;
        }

        boolean releaseFromMagazine() {
            return this.release();
        }

        boolean releaseSegment(int ignoredSegmentId) {
            return this.release();
        }

        private void retain() {
            RefCnt.retain(this.refCnt);
        }

        protected boolean release() {
            boolean deallocate = RefCnt.release(this.refCnt);
            if (deallocate) {
                this.deallocate();
            }
            return deallocate;
        }

        protected void deallocate() {
            Magazine mag = this.magazine;
            int chunkSize = this.delegate.capacity();
            if (!this.pooled || this.chunkReleasePredicate.shouldReleaseChunk(chunkSize) || mag == null) {
                this.detachFromMagazine();
                this.onRelease();
                this.allocator.chunkRegistry.remove(this);
                this.delegate.release();
            } else {
                RefCnt.resetRefCnt(this.refCnt);
                this.delegate.setIndex(0, 0);
                this.allocatedBytes = 0;
                if (!mag.trySetNextInLine(this)) {
                    this.detachFromMagazine();
                    if (!mag.offerToQueue(this)) {
                        boolean released = RefCnt.release(this.refCnt);
                        this.onRelease();
                        this.allocator.chunkRegistry.remove(this);
                        this.delegate.release();
                        assert (released);
                    } else {
                        this.onReturn(false);
                    }
                } else {
                    this.onReturn(true);
                }
            }
        }

        private void onReturn(boolean returnedToMagazine) {
            ReturnChunkEvent event;
            if (PlatformDependent.isJfrEnabled() && ReturnChunkEvent.isEventEnabled() && (event = new ReturnChunkEvent()).shouldCommit()) {
                event.fill(this, AdaptiveByteBufAllocator.class);
                event.returnedToMagazine = returnedToMagazine;
                event.commit();
            }
        }

        private void onRelease() {
            FreeChunkEvent event;
            if (PlatformDependent.isJfrEnabled() && FreeChunkEvent.isEventEnabled() && (event = new FreeChunkEvent()).shouldCommit()) {
                event.fill(this, AdaptiveByteBufAllocator.class);
                event.pooled = this.pooled;
                event.commit();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void readInitInto(AdaptiveByteBuf buf, int size, int startingCapacity, int maxCapacity) {
            int startIndex = this.allocatedBytes;
            this.allocatedBytes = startIndex + startingCapacity;
            Chunk chunk = this;
            chunk.retain();
            try {
                buf.init(this.delegate, chunk, 0, 0, startIndex, size, startingCapacity, maxCapacity);
                chunk = null;
            }
            finally {
                if (chunk != null) {
                    this.allocatedBytes = startIndex;
                    chunk.release();
                }
            }
        }

        public int remainingCapacity() {
            return this.capacity - this.allocatedBytes;
        }

        @Override
        public int capacity() {
            return this.capacity;
        }

        @Override
        public boolean isDirect() {
            return this.delegate.isDirect();
        }

        @Override
        public long memoryAddress() {
            return this.delegate._memoryAddress();
        }
    }

    private static final class ChunkRegistry {
        private final LongAdder totalCapacity = new LongAdder();

        private ChunkRegistry() {
        }

        public long totalCapacity() {
            return this.totalCapacity.sum();
        }

        public void add(Chunk chunk) {
            this.totalCapacity.add(chunk.capacity());
        }

        public void remove(Chunk chunk) {
            this.totalCapacity.add(-chunk.capacity());
        }
    }

    private static final class Magazine {
        private static final AtomicReferenceFieldUpdater<Magazine, Chunk> NEXT_IN_LINE = AtomicReferenceFieldUpdater.newUpdater(Magazine.class, Chunk.class, "nextInLine");
        private static final Chunk MAGAZINE_FREED = new Chunk();
        private static final Recycler<AdaptiveByteBuf> EVENT_LOOP_LOCAL_BUFFER_POOL = new Recycler<AdaptiveByteBuf>(){

            @Override
            protected AdaptiveByteBuf newObject(Recycler.Handle<AdaptiveByteBuf> handle) {
                return new AdaptiveByteBuf(handle);
            }
        };
        private Chunk current;
        private volatile Chunk nextInLine;
        private final MagazineGroup group;
        private final ChunkController chunkController;
        private final StampedLock allocationLock;
        private final Queue<AdaptiveByteBuf> bufferQueue;
        private final ObjectPool.Handle<AdaptiveByteBuf> handle;
        private final Queue<Chunk> sharedChunkQueue;

        Magazine(MagazineGroup group, boolean shareable, Queue<Chunk> sharedChunkQueue, ChunkController chunkController) {
            this.group = group;
            this.chunkController = chunkController;
            if (shareable) {
                this.allocationLock = new StampedLock();
                this.bufferQueue = PlatformDependent.newFixedMpmcQueue(MAGAZINE_BUFFER_QUEUE_CAPACITY);
                this.handle = new ObjectPool.Handle<AdaptiveByteBuf>(){

                    @Override
                    public void recycle(AdaptiveByteBuf self) {
                        bufferQueue.offer(self);
                    }
                };
            } else {
                this.allocationLock = null;
                this.bufferQueue = null;
                this.handle = null;
            }
            this.sharedChunkQueue = sharedChunkQueue;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean tryAllocate(int size, int maxCapacity, AdaptiveByteBuf buf, boolean reallocate) {
            if (this.allocationLock == null) {
                return this.allocate(size, maxCapacity, buf, reallocate);
            }
            long writeLock = this.allocationLock.tryWriteLock();
            if (writeLock != 0L) {
                try {
                    boolean bl = this.allocate(size, maxCapacity, buf, reallocate);
                    return bl;
                }
                finally {
                    this.allocationLock.unlockWrite(writeLock);
                }
            }
            return this.allocateWithoutLock(size, maxCapacity, buf);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private boolean allocateWithoutLock(int size, int maxCapacity, AdaptiveByteBuf buf) {
            Chunk curr = NEXT_IN_LINE.getAndSet(this, null);
            if (curr == MAGAZINE_FREED) {
                this.restoreMagazineFreed();
                return false;
            }
            if (curr == null) {
                curr = this.sharedChunkQueue.poll();
                if (curr == null) {
                    return false;
                }
                curr.attachToMagazine(this);
            }
            boolean allocated = false;
            int remainingCapacity = curr.remainingCapacity();
            int startingCapacity = this.chunkController.computeBufferCapacity(size, maxCapacity, true);
            if (remainingCapacity >= size) {
                curr.readInitInto(buf, size, Math.min(remainingCapacity, startingCapacity), maxCapacity);
                allocated = true;
            }
            try {
                if (remainingCapacity >= 256) {
                    this.transferToNextInLineOrRelease(curr);
                    curr = null;
                }
            }
            finally {
                if (curr != null) {
                    curr.releaseFromMagazine();
                }
            }
            return allocated;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private boolean allocate(int size, int maxCapacity, AdaptiveByteBuf buf, boolean reallocate) {
            int remainingCapacity;
            int startingCapacity = this.chunkController.computeBufferCapacity(size, maxCapacity, reallocate);
            Chunk curr = this.current;
            if (curr != null) {
                remainingCapacity = curr.remainingCapacity();
                if (remainingCapacity > startingCapacity) {
                    curr.readInitInto(buf, size, startingCapacity, maxCapacity);
                    return true;
                }
                this.current = null;
                if (remainingCapacity >= size) {
                    try {
                        curr.readInitInto(buf, size, remainingCapacity, maxCapacity);
                        boolean bl = true;
                        return bl;
                    }
                    finally {
                        curr.releaseFromMagazine();
                    }
                }
                if (remainingCapacity < 256) {
                    curr.releaseFromMagazine();
                } else {
                    this.transferToNextInLineOrRelease(curr);
                }
            }
            assert (this.current == null);
            curr = NEXT_IN_LINE.getAndSet(this, null);
            if (curr != null) {
                if (curr == MAGAZINE_FREED) {
                    this.restoreMagazineFreed();
                    return false;
                }
                remainingCapacity = curr.remainingCapacity();
                if (remainingCapacity > startingCapacity) {
                    curr.readInitInto(buf, size, startingCapacity, maxCapacity);
                    this.current = curr;
                    return true;
                }
                if (remainingCapacity >= size) {
                    try {
                        curr.readInitInto(buf, size, remainingCapacity, maxCapacity);
                        boolean bl = true;
                        return bl;
                    }
                    finally {
                        curr.releaseFromMagazine();
                    }
                }
                curr.releaseFromMagazine();
            }
            if ((curr = this.sharedChunkQueue.poll()) == null) {
                curr = this.chunkController.newChunkAllocation(size, this);
            } else {
                curr.attachToMagazine(this);
                remainingCapacity = curr.remainingCapacity();
                if (remainingCapacity == 0 || remainingCapacity < size) {
                    if (remainingCapacity < 256) {
                        curr.releaseFromMagazine();
                    } else {
                        this.transferToNextInLineOrRelease(curr);
                    }
                    curr = this.chunkController.newChunkAllocation(size, this);
                }
            }
            this.current = curr;
            try {
                remainingCapacity = curr.remainingCapacity();
                assert (remainingCapacity >= size);
                if (remainingCapacity > startingCapacity) {
                    curr.readInitInto(buf, size, startingCapacity, maxCapacity);
                    curr = null;
                } else {
                    curr.readInitInto(buf, size, remainingCapacity, maxCapacity);
                }
            }
            finally {
                if (curr != null) {
                    curr.releaseFromMagazine();
                    this.current = null;
                }
            }
            return true;
        }

        private void restoreMagazineFreed() {
            Chunk next = NEXT_IN_LINE.getAndSet(this, MAGAZINE_FREED);
            if (next != null && next != MAGAZINE_FREED) {
                next.releaseFromMagazine();
            }
        }

        private void transferToNextInLineOrRelease(Chunk chunk) {
            if (NEXT_IN_LINE.compareAndSet(this, null, chunk)) {
                return;
            }
            Chunk nextChunk = NEXT_IN_LINE.get(this);
            if (nextChunk != null && nextChunk != MAGAZINE_FREED && chunk.remainingCapacity() > nextChunk.remainingCapacity() && NEXT_IN_LINE.compareAndSet(this, nextChunk, chunk)) {
                nextChunk.releaseFromMagazine();
                return;
            }
            chunk.releaseFromMagazine();
        }

        boolean trySetNextInLine(Chunk chunk) {
            return NEXT_IN_LINE.compareAndSet(this, null, chunk);
        }

        void free() {
            this.restoreMagazineFreed();
            long stamp = this.allocationLock != null ? this.allocationLock.writeLock() : 0L;
            try {
                if (this.current != null) {
                    this.current.releaseFromMagazine();
                    this.current = null;
                }
            }
            finally {
                if (this.allocationLock != null) {
                    this.allocationLock.unlockWrite(stamp);
                }
            }
        }

        public AdaptiveByteBuf newBuffer() {
            AdaptiveByteBuf buf;
            if (this.handle == null) {
                buf = EVENT_LOOP_LOCAL_BUFFER_POOL.get();
            } else {
                buf = this.bufferQueue.poll();
                if (buf == null) {
                    buf = new AdaptiveByteBuf(this.handle);
                }
            }
            buf.resetRefCnt();
            buf.discardMarks();
            return buf;
        }

        boolean offerToQueue(Chunk chunk) {
            return this.group.offerToQueue(chunk);
        }

        public void initializeSharedStateIn(Magazine other) {
            this.chunkController.initializeSharedStateIn(other.chunkController);
        }
    }

    private static final class HistogramChunkController
    implements ChunkController,
    ChunkReleasePredicate {
        private static final int MIN_DATUM_TARGET = 1024;
        private static final int MAX_DATUM_TARGET = 65534;
        private static final int INIT_DATUM_TARGET = 9;
        private static final int HISTO_BUCKET_COUNT = 16;
        private static final int[] HISTO_BUCKETS = new int[]{16384, 24576, 32768, 49152, 65536, 98304, 131072, 196608, 262144, 393216, 524288, 786432, 0x100000, 0x1C0000, 0x200000, 0x300000};
        private final MagazineGroup group;
        private final boolean shareable;
        private final short[][] histos = new short[][]{new short[16], new short[16], new short[16], new short[16]};
        private final ChunkRegistry chunkRegistry;
        private short[] histo = this.histos[0];
        private final int[] sums = new int[16];
        private int histoIndex;
        private int datumCount;
        private int datumTarget = 9;
        private boolean hasHadRotation;
        private volatile int sharedPrefChunkSize = 131072;
        private volatile int localPrefChunkSize = 131072;
        private volatile int localUpperBufSize;

        private HistogramChunkController(MagazineGroup group, boolean shareable) {
            this.group = group;
            this.shareable = shareable;
            this.chunkRegistry = group.allocator.chunkRegistry;
        }

        @Override
        public int computeBufferCapacity(int requestedSize, int maxCapacity, boolean isReallocation) {
            if (!isReallocation) {
                this.recordAllocationSize(requestedSize);
            }
            int startCapLimits = requestedSize <= 32768 ? 65536 : requestedSize * 2;
            int startingCapacity = Math.min(startCapLimits, this.localUpperBufSize);
            startingCapacity = Math.max(requestedSize, Math.min(maxCapacity, startingCapacity));
            return startingCapacity;
        }

        private void recordAllocationSize(int bufferSizeToRecord) {
            int bucket;
            if (bufferSizeToRecord == 0) {
                return;
            }
            int n = bucket = HistogramChunkController.sizeToBucket(bufferSizeToRecord);
            this.histo[n] = (short)(this.histo[n] + 1);
            if (this.datumCount++ == this.datumTarget) {
                this.rotateHistograms();
            }
        }

        static int sizeToBucket(int size) {
            int index = HistogramChunkController.binarySearchInsertionPoint(Arrays.binarySearch(HISTO_BUCKETS, size));
            return index >= HISTO_BUCKETS.length ? HISTO_BUCKETS.length - 1 : index;
        }

        private static int binarySearchInsertionPoint(int index) {
            if (index < 0) {
                index = -(index + 1);
            }
            return index;
        }

        static int bucketToSize(int sizeBucket) {
            return HISTO_BUCKETS[sizeBucket];
        }

        private void rotateHistograms() {
            int sizeBucket;
            short[][] hs = this.histos;
            for (int i = 0; i < 16; ++i) {
                this.sums[i] = (hs[0][i] & 0xFFFF) + (hs[1][i] & 0xFFFF) + (hs[2][i] & 0xFFFF) + (hs[3][i] & 0xFFFF);
            }
            int sum = 0;
            for (int count : this.sums) {
                sum += count;
            }
            int targetPercentile = (int)((double)sum * 0.99);
            for (sizeBucket = 0; sizeBucket < this.sums.length && this.sums[sizeBucket] <= targetPercentile; targetPercentile -= this.sums[sizeBucket], ++sizeBucket) {
            }
            this.hasHadRotation = true;
            int percentileSize = HistogramChunkController.bucketToSize(sizeBucket);
            int prefChunkSize = Math.max(percentileSize * 8, 131072);
            this.localUpperBufSize = percentileSize;
            this.localPrefChunkSize = prefChunkSize;
            if (this.shareable) {
                for (Magazine mag : this.group.magazines) {
                    HistogramChunkController statistics = (HistogramChunkController)mag.chunkController;
                    prefChunkSize = Math.max(prefChunkSize, statistics.localPrefChunkSize);
                }
            }
            if (this.sharedPrefChunkSize != prefChunkSize) {
                this.datumTarget = Math.max(this.datumTarget >> 1, 1024);
                this.sharedPrefChunkSize = prefChunkSize;
            } else {
                this.datumTarget = Math.min(this.datumTarget << 1, 65534);
            }
            this.histoIndex = this.histoIndex + 1 & 3;
            this.histo = this.histos[this.histoIndex];
            this.datumCount = 0;
            Arrays.fill(this.histo, (short)0);
        }

        int preferredChunkSize() {
            return this.sharedPrefChunkSize;
        }

        @Override
        public void initializeSharedStateIn(ChunkController chunkController) {
            int sharedPrefChunkSize;
            HistogramChunkController statistics = (HistogramChunkController)chunkController;
            statistics.localPrefChunkSize = sharedPrefChunkSize = this.sharedPrefChunkSize;
            statistics.sharedPrefChunkSize = sharedPrefChunkSize;
        }

        @Override
        public Chunk newChunkAllocation(int promptingSize, Magazine magazine) {
            int size = Math.max(promptingSize * 8, this.preferredChunkSize());
            int minChunks = size / 131072;
            if (131072 * minChunks < size) {
                size = 131072 * (1 + minChunks);
            }
            size = Math.min(size, MAX_CHUNK_SIZE);
            if (!this.hasHadRotation && this.sharedPrefChunkSize == 131072) {
                this.sharedPrefChunkSize = size;
            }
            ChunkAllocator chunkAllocator = this.group.chunkAllocator;
            Chunk chunk = new Chunk(chunkAllocator.allocate(size, size), magazine, true, this);
            this.chunkRegistry.add(chunk);
            return chunk;
        }

        @Override
        public boolean shouldReleaseChunk(int chunkSize) {
            int givenChunks = chunkSize / 131072;
            int preferredSize = this.preferredChunkSize();
            int preferredChunks = preferredSize / 131072;
            int deviation = Math.abs(givenChunks - preferredChunks);
            return deviation != 0 && ThreadLocalRandom.current().nextDouble() * 20.0 < (double)deviation;
        }
    }

    private static final class HistogramChunkControllerFactory
    implements ChunkControllerFactory {
        private final boolean shareable;

        private HistogramChunkControllerFactory(boolean shareable) {
            this.shareable = shareable;
        }

        @Override
        public ChunkController create(MagazineGroup group) {
            return new HistogramChunkController(group, this.shareable);
        }
    }

    private static final class SizeClassChunkController
    implements ChunkController {
        private final ChunkAllocator chunkAllocator;
        private final int segmentSize;
        private final int chunkSize;
        private final ChunkRegistry chunkRegistry;
        private final int[] segmentOffsets;

        private SizeClassChunkController(MagazineGroup group, int segmentSize, int chunkSize, int[] segmentOffsets) {
            this.chunkAllocator = group.chunkAllocator;
            this.segmentSize = segmentSize;
            this.chunkSize = chunkSize;
            this.chunkRegistry = group.allocator.chunkRegistry;
            this.segmentOffsets = segmentOffsets;
        }

        @Override
        public int computeBufferCapacity(int requestedSize, int maxCapacity, boolean isReallocation) {
            return Math.min(this.segmentSize, maxCapacity);
        }

        @Override
        public void initializeSharedStateIn(ChunkController chunkController) {
        }

        @Override
        public Chunk newChunkAllocation(int promptingSize, Magazine magazine) {
            AbstractByteBuf chunkBuffer = this.chunkAllocator.allocate(this.chunkSize, this.chunkSize);
            assert (chunkBuffer.capacity() == this.chunkSize);
            SizeClassedChunk chunk = new SizeClassedChunk(chunkBuffer, magazine, true, this.segmentSize, this.segmentOffsets, size -> false);
            this.chunkRegistry.add(chunk);
            return chunk;
        }
    }

    private static final class SizeClassChunkControllerFactory
    implements ChunkControllerFactory {
        private static final int MIN_SEGMENTS_PER_CHUNK = 32;
        private final int segmentSize;
        private final int chunkSize;
        private final int[] segmentOffsets;

        private SizeClassChunkControllerFactory(int segmentSize) {
            this.segmentSize = ObjectUtil.checkPositive(segmentSize, "segmentSize");
            this.chunkSize = Math.max(131072, segmentSize * 32);
            int segmentsCount = this.chunkSize / segmentSize;
            this.segmentOffsets = new int[segmentsCount];
            for (int i = 0; i < segmentsCount; ++i) {
                this.segmentOffsets[i] = i * segmentSize;
            }
        }

        @Override
        public ChunkController create(MagazineGroup group) {
            return new SizeClassChunkController(group, this.segmentSize, this.chunkSize, this.segmentOffsets);
        }
    }

    private static interface ChunkReleasePredicate {
        public boolean shouldReleaseChunk(int var1);
    }

    private static interface ChunkController {
        public int computeBufferCapacity(int var1, int var2, boolean var3);

        public void initializeSharedStateIn(ChunkController var1);

        public Chunk newChunkAllocation(int var1, Magazine var2);
    }

    private static interface ChunkControllerFactory {
        public ChunkController create(MagazineGroup var1);
    }

    private static final class MagazineGroup {
        private final AdaptivePoolingAllocator allocator;
        private final ChunkAllocator chunkAllocator;
        private final ChunkControllerFactory chunkControllerFactory;
        private final Queue<Chunk> chunkReuseQueue;
        private final StampedLock magazineExpandLock;
        private final Magazine threadLocalMagazine;
        private volatile Magazine[] magazines;
        private volatile boolean freed;

        MagazineGroup(AdaptivePoolingAllocator allocator, ChunkAllocator chunkAllocator, ChunkControllerFactory chunkControllerFactory, boolean isThreadLocal) {
            this.allocator = allocator;
            this.chunkAllocator = chunkAllocator;
            this.chunkControllerFactory = chunkControllerFactory;
            this.chunkReuseQueue = AdaptivePoolingAllocator.createSharedChunkQueue();
            if (isThreadLocal) {
                this.magazineExpandLock = null;
                this.threadLocalMagazine = new Magazine(this, false, this.chunkReuseQueue, chunkControllerFactory.create(this));
            } else {
                this.magazineExpandLock = new StampedLock();
                this.threadLocalMagazine = null;
                Magazine[] mags = new Magazine[1];
                for (int i = 0; i < mags.length; ++i) {
                    mags[i] = new Magazine(this, true, this.chunkReuseQueue, chunkControllerFactory.create(this));
                }
                this.magazines = mags;
            }
        }

        public AdaptiveByteBuf allocate(int size, int maxCapacity, Thread currentThread, AdaptiveByteBuf buf) {
            Magazine[] mags;
            boolean reallocate = buf != null;
            Magazine tlMag = this.threadLocalMagazine;
            if (tlMag != null) {
                if (buf == null) {
                    buf = tlMag.newBuffer();
                }
                boolean allocated = tlMag.tryAllocate(size, maxCapacity, buf, reallocate);
                assert (allocated) : "Allocation of threadLocalMagazine must always succeed";
                return buf;
            }
            long threadId = currentThread.getId();
            int expansions = 0;
            do {
                mags = this.magazines;
                int mask = mags.length - 1;
                int index = (int)(threadId & (long)mask);
                int m = mags.length << 1;
                for (int i = 0; i < m; ++i) {
                    Magazine mag = mags[index + i & mask];
                    if (buf == null) {
                        buf = mag.newBuffer();
                    }
                    if (!mag.tryAllocate(size, maxCapacity, buf, reallocate)) continue;
                    return buf;
                }
            } while (++expansions <= 3 && this.tryExpandMagazines(mags.length));
            if (!reallocate && buf != null) {
                buf.release();
            }
            return null;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private boolean tryExpandMagazines(int currentLength) {
            if (currentLength >= MAX_STRIPES) {
                return true;
            }
            long writeLock = this.magazineExpandLock.tryWriteLock();
            if (writeLock != 0L) {
                Magazine[] mags;
                try {
                    mags = this.magazines;
                    if (mags.length >= MAX_STRIPES || mags.length > currentLength || this.freed) {
                        boolean bl = true;
                        return bl;
                    }
                    Magazine firstMagazine = mags[0];
                    Magazine[] expanded = new Magazine[mags.length * 2];
                    int l = expanded.length;
                    for (int i = 0; i < l; ++i) {
                        Magazine m = new Magazine(this, true, this.chunkReuseQueue, this.chunkControllerFactory.create(this));
                        firstMagazine.initializeSharedStateIn(m);
                        expanded[i] = m;
                    }
                    this.magazines = expanded;
                }
                finally {
                    this.magazineExpandLock.unlockWrite(writeLock);
                }
                for (Magazine magazine : mags) {
                    magazine.free();
                }
            }
            return true;
        }

        boolean offerToQueue(Chunk buffer) {
            if (this.freed) {
                return false;
            }
            boolean isAdded = this.chunkReuseQueue.offer(buffer);
            if (this.freed && isAdded) {
                this.freeChunkReuseQueue();
            }
            return isAdded;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void free() {
            this.freed = true;
            if (this.threadLocalMagazine != null) {
                this.threadLocalMagazine.free();
            } else {
                long stamp = this.magazineExpandLock.writeLock();
                try {
                    Magazine[] mags;
                    for (Magazine magazine : mags = this.magazines) {
                        magazine.free();
                    }
                }
                finally {
                    this.magazineExpandLock.unlockWrite(stamp);
                }
            }
            this.freeChunkReuseQueue();
        }

        private void freeChunkReuseQueue() {
            Chunk chunk;
            while ((chunk = this.chunkReuseQueue.poll()) != null) {
                chunk.release();
            }
        }
    }
}

