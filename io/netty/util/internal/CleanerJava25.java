/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal;

import io.netty.util.internal.CleanableDirectBuffer;
import io.netty.util.internal.Cleaner;
import io.netty.util.internal.PlatformDependent0;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.ByteBuffer;

final class CleanerJava25
implements Cleaner {
    private static final InternalLogger logger;
    private static final MethodHandle INVOKE_ALLOCATOR;

    CleanerJava25() {
    }

    static boolean isSupported() {
        return INVOKE_ALLOCATOR != null;
    }

    @Override
    public CleanableDirectBuffer allocate(int capacity) {
        try {
            return INVOKE_ALLOCATOR.invokeExact(capacity);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Throwable e) {
            throw new IllegalStateException("Unexpected allocation exception", e);
        }
    }

    @Override
    public void freeDirectBuffer(ByteBuffer buffer) {
        throw new UnsupportedOperationException("Cannot clean arbitrary ByteBuffer instances");
    }

    static {
        Throwable error;
        MethodHandle method;
        boolean suitableJavaVersion;
        if (System.getProperty("org.graalvm.nativeimage.imagecode") != null) {
            String v = System.getProperty("java.specification.version");
            try {
                suitableJavaVersion = Integer.parseInt(v) >= 25;
            }
            catch (NumberFormatException e) {
                suitableJavaVersion = false;
            }
            logger = null;
        } else {
            suitableJavaVersion = PlatformDependent0.javaVersion() >= 25;
            logger = InternalLoggerFactory.getInstance(CleanerJava25.class);
        }
        if (suitableJavaVersion) {
            try {
                Class<?> arenaCls = Class.forName("java.lang.foreign.Arena");
                Class<?> memsegCls = Class.forName("java.lang.foreign.MemorySegment");
                Class<CleanableDirectBufferImpl> bufCls = CleanableDirectBufferImpl.class;
                MethodHandles.Lookup lookup = MethodHandles.lookup();
                MethodHandle ofShared = lookup.findStatic(arenaCls, "ofShared", MethodType.methodType(arenaCls));
                Object shared = ofShared.invoke();
                ((AutoCloseable)shared).close();
                MethodHandle allocate = lookup.findVirtual(arenaCls, "allocate", MethodType.methodType(memsegCls, Long.TYPE));
                MethodHandle asByteBuffer = lookup.findVirtual(memsegCls, "asByteBuffer", MethodType.methodType(ByteBuffer.class));
                MethodHandle address = lookup.findVirtual(memsegCls, "address", MethodType.methodType(Long.TYPE));
                MethodHandle bufClsCtor = lookup.findConstructor(bufCls, MethodType.methodType(Void.TYPE, AutoCloseable.class, ByteBuffer.class, Long.TYPE));
                MethodHandle allocateInt = MethodHandles.explicitCastArguments(allocate, MethodType.methodType(memsegCls, arenaCls, Integer.TYPE));
                MethodHandle ctorArenaMemsegMemseg = MethodHandles.explicitCastArguments(MethodHandles.filterArguments(bufClsCtor, 1, asByteBuffer, address), MethodType.methodType(bufCls, arenaCls, memsegCls, memsegCls));
                MethodHandle ctorArenaMemsegNull = MethodHandles.permuteArguments(ctorArenaMemsegMemseg, MethodType.methodType(bufCls, arenaCls, memsegCls, memsegCls), 0, 1, 1);
                MethodHandle ctorArenaMemseg = MethodHandles.insertArguments(ctorArenaMemsegNull, 2, new Object[]{null});
                MethodHandle ctorArenaArenaInt = MethodHandles.collectArguments(ctorArenaMemseg, 1, allocateInt);
                MethodHandle ctorArenaNullInt = MethodHandles.permuteArguments(ctorArenaArenaInt, MethodType.methodType(bufCls, arenaCls, arenaCls, Integer.TYPE), 0, 0, 2);
                MethodHandle ctorArenaInt = MethodHandles.insertArguments(ctorArenaNullInt, 1, new Object[]{null});
                method = MethodHandles.foldArguments(ctorArenaInt, ofShared);
                error = null;
            }
            catch (Throwable throwable) {
                method = null;
                error = throwable;
            }
        } else {
            method = null;
            error = new UnsupportedOperationException("java.lang.foreign.MemorySegment unavailable");
        }
        if (logger != null) {
            if (error == null) {
                logger.debug("java.nio.ByteBuffer.cleaner(): available");
            } else {
                logger.debug("java.nio.ByteBuffer.cleaner(): unavailable", error);
            }
        }
        INVOKE_ALLOCATOR = method;
    }

    private static final class CleanableDirectBufferImpl
    implements CleanableDirectBuffer {
        private final AutoCloseable closeable;
        private final ByteBuffer buffer;
        private final long memoryAddress;

        CleanableDirectBufferImpl(AutoCloseable closeable, ByteBuffer buffer, long memoryAddress) {
            this.closeable = closeable;
            this.buffer = buffer;
            this.memoryAddress = memoryAddress;
        }

        @Override
        public ByteBuffer buffer() {
            return this.buffer;
        }

        @Override
        public void clean() {
            try {
                this.closeable.close();
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (Exception e) {
                throw new IllegalStateException("Unexpected close exception", e);
            }
        }

        @Override
        public boolean hasMemoryAddress() {
            return true;
        }

        @Override
        public long memoryAddress() {
            return this.memoryAddress;
        }
    }
}

