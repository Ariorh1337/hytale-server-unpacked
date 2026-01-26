/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util;

import java.io.IOException;

public class Exceptions {
    public static IllegalArgumentException illegalArgumentException(String string, Throwable throwable) {
        return new IllegalArgumentException(string, throwable);
    }

    public static IllegalStateException illegalStateException(String string, Throwable throwable) {
        return new IllegalStateException(string, throwable);
    }

    public static IOException ioException(String string, Throwable throwable) {
        return new IOException(string, throwable);
    }
}

