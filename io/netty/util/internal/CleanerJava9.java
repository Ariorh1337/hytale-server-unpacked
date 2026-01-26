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

final class CleanerJava9
implements Cleaner {
    private static final InternalLogger logger;
    private static final MethodHandle INVOKE_CLEANER;

    CleanerJava9() {
    }

    static boolean isSupported() {
        return INVOKE_CLEANER != null;
    }

    @Override
    public CleanableDirectBuffer allocate(int capacity) {
        return new CleanableDirectBufferImpl(ByteBuffer.allocateDirect(capacity));
    }

    @Override
    @Deprecated
    public void freeDirectBuffer(ByteBuffer buffer) {
        CleanerJava9.freeDirectBufferStatic(buffer);
    }

    private static void freeDirectBufferStatic(ByteBuffer buffer) {
        if (System.getSecurityManager() == null) {
            try {
                INVOKE_CLEANER.invokeExact(buffer);
            }
            catch (Throwable cause) {
                PlatformDependent0.throwException(cause);
            }
        } else {
            CleanerJava9.freeDirectBufferPrivileged(buffer);
        }
    }

    private static void freeDirectBufferPrivileged(final ByteBuffer buffer) {
        Throwable error = AccessController.doPrivileged(new PrivilegedAction<Throwable>(){

            @Override
            public Throwable run() {
                try {
                    INVOKE_CLEANER.invokeExact(buffer);
                }
                catch (Throwable e) {
                    return e;
                }
                return null;
            }
        });
        if (error != null) {
            PlatformDependent0.throwException(error);
        }
    }

    static {
        Throwable error;
        MethodHandle method;
        logger = InternalLoggerFactory.getInstance(CleanerJava9.class);
        if (PlatformDependent0.hasUnsafe()) {
            final ByteBuffer buffer = ByteBuffer.allocateDirect(1);
            Object maybeInvokeMethod = AccessController.doPrivileged(new PrivilegedAction<Object>(){

                @Override
                public Object run() {
                    try {
                        Class<?> unsafeClass = PlatformDependent0.UNSAFE.getClass();
                        MethodHandles.Lookup lookup = MethodHandles.lookup();
                        MethodHandle invokeCleaner = lookup.findVirtual(unsafeClass, "invokeCleaner", MethodType.methodType(Void.TYPE, ByteBuffer.class));
                        invokeCleaner = invokeCleaner.bindTo(PlatformDependent0.UNSAFE);
                        invokeCleaner.invokeExact(buffer);
                        return invokeCleaner;
                    }
                    catch (Throwable e) {
                        return e;
                    }
                }
            });
            if (maybeInvokeMethod instanceof Throwable) {
                method = null;
                error = (Throwable)maybeInvokeMethod;
            } else {
                method = (MethodHandle)maybeInvokeMethod;
                error = null;
            }
        } else {
            method = null;
            error = new UnsupportedOperationException("sun.misc.Unsafe unavailable");
        }
        if (error == null) {
            logger.debug("java.nio.ByteBuffer.cleaner(): available");
        } else {
            logger.debug("java.nio.ByteBuffer.cleaner(): unavailable", error);
        }
        INVOKE_CLEANER = method;
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
            CleanerJava9.freeDirectBufferStatic(this.buffer);
        }
    }
}

