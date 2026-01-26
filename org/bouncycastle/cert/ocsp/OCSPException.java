/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.ocsp;

public class OCSPException
extends Exception {
    private Throwable cause;

    public OCSPException(String string) {
        super(string);
    }

    public OCSPException(String string, Throwable throwable) {
        super(string);
        this.cause = throwable;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

