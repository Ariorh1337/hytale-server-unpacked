/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert;

public class CertRuntimeException
extends RuntimeException {
    private Throwable cause;

    public CertRuntimeException(String string, Throwable throwable) {
        super(string);
        this.cause = throwable;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

