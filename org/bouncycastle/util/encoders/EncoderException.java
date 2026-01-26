/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.encoders;

public class EncoderException
extends IllegalStateException {
    private Throwable cause;

    EncoderException(String string, Throwable throwable) {
        super(string);
        this.cause = throwable;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

