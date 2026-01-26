/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.eac;

import java.io.IOException;

public class EACIOException
extends IOException {
    private Throwable cause;

    public EACIOException(String string, Throwable throwable) {
        super(string);
        this.cause = throwable;
    }

    public EACIOException(String string) {
        super(string);
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

