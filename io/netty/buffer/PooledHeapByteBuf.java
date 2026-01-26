/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.HeapByteBufUtil;
import io.netty.buffer.PooledByteBuf;
import io.netty.util.Recycler;
import io.netty.util.internal.ObjectPool;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

class PooledHeapByteBuf
extends PooledByteBuf<byte[]> {
    private static final Recycler<PooledHeapByteBuf> RECYCLER = new Recycler<PooledHeapByteBuf>(){

        @Override
        protected PooledHeapByteBuf newObject(Recycler.Handle<PooledHeapByteBuf> handle) {
            return new PooledHeapByteBuf((ObjectPool.Handle<? extends PooledHeapByteBuf>)handle, 0);
        }
    };

    static PooledHeapByteBuf newInstance(int maxCapacity) {
        PooledHeapByteBuf buf = RECYCLER.get();
        buf.reuse(maxCapacity);
        return buf;
    }

    PooledHeapByteBuf(ObjectPool.Handle<? extends PooledHeapByteBuf> recyclerHandle, int maxCapacity) {
        super(recyclerHandle, maxCapacity);
    }

    @Override
    public final boolean isDirect() {
        return false;
    }

    @Override
    protected byte _getByte(int index) {
        return HeapByteBufUtil.getByte((byte[])this.memory, this.idx(index));
    }

    @Override
    protected short _getShort(int index) {
        return HeapByteBufUtil.getShort((byte[])this.memory, this.idx(index));
    }

    @Override
    protected short _getShortLE(int index) {
        return HeapByteBufUtil.getShortLE((byte[])this.memory, this.idx(index));
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        return HeapByteBufUtil.getUnsignedMedium((byte[])this.memory, this.idx(index));
    }

    @Override
    protected int _getUnsignedMediumLE(int index) {
        return HeapByteBufUtil.getUnsignedMediumLE((byte[])this.memory, this.idx(index));
    }

    @Override
    protected int _getInt(int index) {
        return HeapByteBufUtil.getInt((byte[])this.memory, this.idx(index));
    }

    @Override
    protected int _getIntLE(int index) {
        return HeapByteBufUtil.getIntLE((byte[])this.memory, this.idx(index));
    }

    @Override
    protected long _getLong(int index) {
        return HeapByteBufUtil.getLong((byte[])this.memory, this.idx(index));
    }

    @Override
    protected long _getLongLE(int index) {
        return HeapByteBufUtil.getLongLE((byte[])this.memory, this.idx(index));
    }

    @Override
    public final ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        this.checkDstIndex(index, length, dstIndex, dst.capacity());
        if (dst.hasMemoryAddress() && PlatformDependent.hasUnsafe()) {
            PlatformDependent.copyMemory((byte[])this.memory, this.idx(index), dst.memoryAddress() + (long)dstIndex, (long)length);
        } else if (dst.hasArray()) {
            this.getBytes(index, dst.array(), dst.arrayOffset() + dstIndex, length);
        } else {
            dst.setBytes(dstIndex, (byte[])this.memory, this.idx(index), length);
        }
        return this;
    }

    @Override
    public final ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        this.checkDstIndex(index, length, dstIndex, dst.length);
        System.arraycopy(this.memory, this.idx(index), dst, dstIndex, length);
        return this;
    }

    @Override
    public final ByteBuf getBytes(int index, ByteBuffer dst) {
        int length = dst.remaining();
        this.checkIndex(index, length);
        dst.put((byte[])this.memory, this.idx(index), length);
        return this;
    }

    @Override
    public final ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        this.checkIndex(index, length);
        out.write((byte[])this.memory, this.idx(index), length);
        return this;
    }

    @Override
    protected void _setByte(int index, int value) {
        HeapByteBufUtil.setByte((byte[])this.memory, this.idx(index), value);
    }

    @Override
    protected void _setShort(int index, int value) {
        HeapByteBufUtil.setShort((byte[])this.memory, this.idx(index), value);
    }

    @Override
    protected void _setShortLE(int index, int value) {
        HeapByteBufUtil.setShortLE((byte[])this.memory, this.idx(index), value);
    }

    @Override
    protected void _setMedium(int index, int value) {
        HeapByteBufUtil.setMedium((byte[])this.memory, this.idx(index), value);
    }

    @Override
    protected void _setMediumLE(int index, int value) {
        HeapByteBufUtil.setMediumLE((byte[])this.memory, this.idx(index), value);
    }

    @Override
    protected void _setInt(int index, int value) {
        HeapByteBufUtil.setInt((byte[])this.memory, this.idx(index), value);
    }

    @Override
    protected void _setIntLE(int index, int value) {
        HeapByteBufUtil.setIntLE((byte[])this.memory, this.idx(index), value);
    }

    @Override
    protected void _setLong(int index, long value) {
        HeapByteBufUtil.setLong((byte[])this.memory, this.idx(index), value);
    }

    @Override
    protected void _setLongLE(int index, long value) {
        HeapByteBufUtil.setLongLE((byte[])this.memory, this.idx(index), value);
    }

    @Override
    public final ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        this.checkSrcIndex(index, length, srcIndex, src.capacity());
        if (src.hasMemoryAddress() && PlatformDependent.hasUnsafe()) {
            PlatformDependent.copyMemory(src.memoryAddress() + (long)srcIndex, (byte[])this.memory, this.idx(index), (long)length);
        } else if (src.hasArray()) {
            this.setBytes(index, src.array(), src.arrayOffset() + srcIndex, length);
        } else {
            src.getBytes(srcIndex, (byte[])this.memory, this.idx(index), length);
        }
        return this;
    }

    @Override
    public final ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        this.checkSrcIndex(index, length, srcIndex, src.length);
        System.arraycopy(src, srcIndex, this.memory, this.idx(index), length);
        return this;
    }

    @Override
    public final ByteBuf setBytes(int index, ByteBuffer src) {
        int length = src.remaining();
        this.checkIndex(index, length);
        src.get((byte[])this.memory, this.idx(index), length);
        return this;
    }

    @Override
    public final int setBytes(int index, InputStream in, int length) throws IOException {
        this.checkIndex(index, length);
        return in.read((byte[])this.memory, this.idx(index), length);
    }

    @Override
    public final ByteBuf copy(int index, int length) {
        this.checkIndex(index, length);
        ByteBuf copy = this.alloc().heapBuffer(length, this.maxCapacity());
        return copy.writeBytes((byte[])this.memory, this.idx(index), length);
    }

    @Override
    final ByteBuffer duplicateInternalNioBuffer(int index, int length) {
        this.checkIndex(index, length);
        return ByteBuffer.wrap((byte[])this.memory, this.idx(index), length).slice();
    }

    @Override
    public final boolean hasArray() {
        return true;
    }

    @Override
    public final byte[] array() {
        this.ensureAccessible();
        return (byte[])this.memory;
    }

    @Override
    public final int arrayOffset() {
        return this.offset;
    }

    @Override
    public final boolean hasMemoryAddress() {
        return false;
    }

    @Override
    public final long memoryAddress() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected final ByteBuffer newInternalNioBuffer(byte[] memory) {
        return ByteBuffer.wrap(memory);
    }
}

