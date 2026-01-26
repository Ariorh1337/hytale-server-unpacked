/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.openssl;

import org.bouncycastle.openssl.PEMException;

public class EncryptionException
extends PEMException {
    private Throwable cause;

    public EncryptionException(String string) {
        super(string);
    }

    public EncryptionException(String string, Throwable throwable) {
        super(string);
        this.cause = throwable;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

