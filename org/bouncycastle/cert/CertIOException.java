/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert;

import java.io.IOException;

public class CertIOException
extends IOException {
    private Throwable cause;

    public CertIOException(String string, Throwable throwable) {
        super(string);
        this.cause = throwable;
    }

    public CertIOException(String string) {
        super(string);
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

