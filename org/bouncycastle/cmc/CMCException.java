/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cmc;

public class CMCException
extends Exception {
    private final Throwable cause;

    public CMCException(String string) {
        this(string, null);
    }

    public CMCException(String string, Throwable throwable) {
        super(string);
        this.cause = throwable;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

