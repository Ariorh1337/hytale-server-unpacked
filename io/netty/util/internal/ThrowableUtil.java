/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

public final class ThrowableUtil {
    private ThrowableUtil() {
    }

    public static <T extends Throwable> T unknownStackTrace(T cause, Class<?> clazz, String method) {
        cause.setStackTrace(new StackTraceElement[]{new StackTraceElement(clazz.getName(), method, null, -1)});
        return cause;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    public static String stackTraceToString(Throwable cause) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream pout = new PrintStream(out);
        cause.printStackTrace(pout);
        pout.flush();
        try {
            String string = out.toString();
            return string;
        }
        finally {
            try {
                out.close();
            }
            catch (IOException iOException) {}
        }
    }

    @Deprecated
    public static boolean haveSuppressed() {
        return true;
    }

    public static void addSuppressed(Throwable target, Throwable suppressed) {
        if (suppressed != null) {
            target.addSuppressed(suppressed);
        }
    }

    public static void addSuppressedAndClear(Throwable target, List<Throwable> suppressed) {
        ThrowableUtil.addSuppressed(target, suppressed);
        suppressed.clear();
    }

    public static void addSuppressed(Throwable target, List<Throwable> suppressed) {
        for (Throwable t : suppressed) {
            ThrowableUtil.addSuppressed(target, t);
        }
    }

    public static Throwable[] getSuppressed(Throwable source) {
        return source.getSuppressed();
    }
}

