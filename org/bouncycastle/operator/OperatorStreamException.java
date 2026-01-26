/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator;

import java.io.IOException;

public class OperatorStreamException
extends IOException {
    private Throwable cause;

    public OperatorStreamException(String string, Throwable throwable) {
        super(string);
        this.cause = throwable;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

