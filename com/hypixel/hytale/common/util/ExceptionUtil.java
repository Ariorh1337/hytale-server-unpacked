/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.common.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.annotation.Nonnull;

public class ExceptionUtil {
    @Nonnull
    public static String combineMessages(Throwable thrown, @Nonnull String joiner) {
        StringBuilder sb = new StringBuilder();
        for (Throwable throwable = thrown; throwable != null; throwable = throwable.getCause()) {
            if (throwable.getCause() == throwable) {
                return sb.toString();
            }
            if (throwable.getMessage() == null) continue;
            sb.append(throwable.getMessage()).append(joiner);
        }
        sb.setLength(sb.length() - joiner.length());
        return sb.toString();
    }

    public static String toStringWithStack(@Nonnull Throwable t) {
        String string;
        StringWriter out = new StringWriter();
        try {
            t.printStackTrace(new PrintWriter(out));
            string = out.toString();
        }
        catch (Throwable throwable) {
            try {
                try {
                    out.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException iOException) {
                return t.toString();
            }
        }
        out.close();
        return string;
    }
}

