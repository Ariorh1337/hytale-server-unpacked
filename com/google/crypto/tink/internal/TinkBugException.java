/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.internal;

public final class TinkBugException
extends RuntimeException {
    public TinkBugException(String message) {
        super(message);
    }

    public TinkBugException(String message, Throwable cause) {
        super(message, cause);
    }

    public TinkBugException(Throwable cause) {
        super(cause);
    }

    public static <T> T exceptionIsBug(ThrowingSupplier<T> t) {
        try {
            return t.get();
        }
        catch (Exception e) {
            throw new TinkBugException(e);
        }
    }

    public static void exceptionIsBug(ThrowingRunnable v) {
        try {
            v.run();
        }
        catch (Exception e) {
            throw new TinkBugException(e);
        }
    }

    public static interface ThrowingSupplier<T> {
        public T get() throws Exception;
    }

    public static interface ThrowingRunnable {
        public void run() throws Exception;
    }
}

