/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.proc;

import com.nimbusds.jose.proc.BadJOSEException;

public class BadJWEException
extends BadJOSEException {
    public BadJWEException(String message) {
        super(message);
    }

    public BadJWEException(String message, Throwable cause) {
        super(message, cause);
    }
}

