/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp;

public class TSPException
extends Exception {
    Throwable underlyingException;

    public TSPException(String string) {
        super(string);
    }

    public TSPException(String string, Throwable throwable) {
        super(string);
        this.underlyingException = throwable;
    }

    public Exception getUnderlyingException() {
        return (Exception)this.underlyingException;
    }

    @Override
    public Throwable getCause() {
        return this.underlyingException;
    }
}

