/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pkix.jcajce;

class AnnotatedException
extends Exception {
    private Throwable _underlyingException;

    public AnnotatedException(String string, Throwable throwable) {
        super(string);
        this._underlyingException = throwable;
    }

    public AnnotatedException(String string) {
        this(string, null);
    }

    @Override
    public Throwable getCause() {
        return this._underlyingException;
    }
}

