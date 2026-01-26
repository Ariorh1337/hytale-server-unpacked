/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pkcs;

import java.io.IOException;

public class PKCSIOException
extends IOException {
    private Throwable cause;

    public PKCSIOException(String string, Throwable throwable) {
        super(string);
        this.cause = throwable;
    }

    public PKCSIOException(String string) {
        super(string);
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

