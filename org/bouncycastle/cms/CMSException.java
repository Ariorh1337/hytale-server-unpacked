/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

public class CMSException
extends Exception {
    Exception e;

    public CMSException(String string) {
        super(string);
    }

    public CMSException(String string, Exception exception) {
        super(string);
        this.e = exception;
    }

    public Exception getUnderlyingException() {
        return this.e;
    }

    @Override
    public Throwable getCause() {
        return this.e;
    }
}

