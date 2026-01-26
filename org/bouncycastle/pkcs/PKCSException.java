/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pkcs;

public class PKCSException
extends Exception {
    private Throwable cause;

    public PKCSException(String string, Throwable throwable) {
        super(string);
        this.cause = throwable;
    }

    public PKCSException(String string) {
        super(string);
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

