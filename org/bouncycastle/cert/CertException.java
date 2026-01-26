/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert;

public class CertException
extends Exception {
    private Throwable cause;

    public CertException(String string, Throwable throwable) {
        super(string);
        this.cause = throwable;
    }

    public CertException(String string) {
        super(string);
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

