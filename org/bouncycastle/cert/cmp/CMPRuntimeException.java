/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.cmp;

public class CMPRuntimeException
extends RuntimeException {
    private Throwable cause;

    public CMPRuntimeException(String string, Throwable throwable) {
        super(string);
        this.cause = throwable;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

