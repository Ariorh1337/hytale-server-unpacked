/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.path;

public class CertPathValidationException
extends Exception {
    private final Exception cause;

    public CertPathValidationException(String string) {
        this(string, null);
    }

    public CertPathValidationException(String string, Exception exception) {
        super(string);
        this.cause = exception;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

