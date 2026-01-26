/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.cmp;

public class CMPException
extends Exception {
    private Throwable cause;

    public CMPException(String string, Throwable throwable) {
        super(string);
        this.cause = throwable;
    }

    public CMPException(String string) {
        super(string);
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

