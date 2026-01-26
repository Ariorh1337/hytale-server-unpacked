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
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Objects;

final class CleanerJava6
implements Cleaner {
    private static final MethodHandle CLEAN_METHOD;
    private static final InternalLogger logger;

    CleanerJava6() {
    }

    static boolean isSupported() {
        return CLEAN_METHOD != null;
    }

    @Override
    public CleanableDirectBuffer allocate(int capacity) {
        return new CleanableDirectBufferImpl(ByteBuffer.allocateDirect(capacity));
    }

    @Override
    @Deprecated
    public void freeDirectBuffer(ByteBuffer buffer) {
        CleanerJava6.freeDirectBufferStatic(buffer);
    }

    private static void freeDirectBufferStatic(ByteBuffer buffer) {
        if (!buffer.isDirect()) {
            return;
        }
        if (System.getSecurityManager() == null) {
            try {
                CleanerJava6.freeDirectBuffer0(buffer);
            }
            catch (Throwable cause) {
                PlatformDependent0.throwException(cause);
            }
        } else {
            CleanerJava6.freeDirectBufferPrivileged(buffer);
        }
    }

    private static void freeDirectBufferPrivileged(final ByteBuffer buffer) {
        Throwable cause = AccessController.doPrivileged(new PrivilegedAction<Throwable>(){

            @Override
            public Throwable run() {
                try {
                    CleanerJava6.freeDirectBuffer0(buffer);
                    return null;
                }
                catch (Throwable cause) {
                    return cause;
                }
            }
        });
        if (cause != null) {
            PlatformDependent0.throwException(cause);
        }
    }

    private static void freeDirectBuffer0(ByteBuffer buffer) throws Throwable {
        CLEAN_METHOD.invokeExact(buffer);
    }

    static {
        MethodHandle clean;
        logger = InternalLoggerFactory.getInstance(CleanerJava6.class);
        Throwable error = null;
        ByteBuffer direct = ByteBuffer.allocateDirect(1);
        try {
            Object mayBeCleanerField = AccessController.doPrivileged(new PrivilegedAction<Object>(){

                @Override
                public Object run() {
                    try {
                        Class<?> cleanerClass = Class.forName("sun.misc.Cleaner");
                        Class<?> directBufClass = Class.forName("sun.nio.ch.DirectBuffer");
                        MethodHandles.Lookup lookup = MethodHandles.lookup();
                        MethodHandle clean = lookup.findVirtual(cleanerClass, "clean", MethodType.methodType(Void.TYPE));
                        MethodHandle nullTest = lookup.findStatic(Objects.class, "nonNull", MethodType.methodType(Boolean.TYPE, Object.class));
                        clean = MethodHandles.guardWithTest(nullTest.asType(MethodType.methodType(Boolean.TYPE, cleanerClass)), clean, nullTest.asType(MethodType.methodType(Void.TYPE, cleanerClass)));
                        clean = MethodHandles.filterArguments(clean, 0, lookup.findVirtual(directBufClass, "cleaner", MethodType.methodType(cleanerClass)));
                        clean = MethodHandles.explicitCastArguments(clean, MethodType.methodType(Void.TYPE, ByteBuffer.class));
                        return clean;
                    }
                    catch (Throwable cause) {
                        return cause;
                    }
                }
            });
            if (mayBeCleanerField instanceof Throwable) {
                throw (Throwable)mayBeCleanerField;
            }
            clean = (MethodHandle)mayBeCleanerField;
            clean.invokeExact(direct);
        }
        catch (Throwable t) {
            clean = null;
            error = t;
        }
        if (error == null) {
            logger.debug("java.nio.ByteBuffer.cleaner(): available");
        } else {
            logger.debug("java.nio.ByteBuffer.cleaner(): unavailable", error);
        }
        CLEAN_METHOD = clean;
    }

    private static final class CleanableDirectBufferImpl
    implements CleanableDirectBuffer {
        private final ByteBuffer buffer;

        private CleanableDirectBufferImpl(ByteBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public ByteBuffer buffer() {
            return this.buffer;
        }

        @Override
        public void clean() {
            CleanerJava6.freeDirectBufferStatic(this.buffer);
        }
    }
}

