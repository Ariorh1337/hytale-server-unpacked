/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp.ers;

public class ERSException
extends Exception {
    private final Throwable cause;

    public ERSException(String string) {
        this(string, null);
    }

    public ERSException(String string, Throwable throwable) {
        super(string);
        this.cause = throwable;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

