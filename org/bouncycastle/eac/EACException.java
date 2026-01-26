/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.eac;

public class EACException
extends Exception {
    private Throwable cause;

    public EACException(String string, Throwable throwable) {
        super(string);
        this.cause = throwable;
    }

    public EACException(String string) {
        super(string);
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

