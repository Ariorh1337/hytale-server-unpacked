/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator;

public class OperatorException
extends Exception {
    private Throwable cause;

    public OperatorException(String string, Throwable throwable) {
        super(string);
        this.cause = throwable;
    }

    public OperatorException(String string) {
        super(string);
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

