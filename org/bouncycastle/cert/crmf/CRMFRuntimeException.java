/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.crmf;

public class CRMFRuntimeException
extends RuntimeException {
    private Throwable cause;

    public CRMFRuntimeException(String string, Throwable throwable) {
        super(string);
        this.cause = throwable;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

