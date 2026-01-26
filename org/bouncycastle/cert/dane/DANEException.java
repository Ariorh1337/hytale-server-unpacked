/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.dane;

public class DANEException
extends Exception {
    private Throwable cause;

    public DANEException(String string, Throwable throwable) {
        super(string);
        this.cause = throwable;
    }

    public DANEException(String string) {
        super(string);
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

