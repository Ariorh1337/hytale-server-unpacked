/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.proc;

import com.nimbusds.jose.proc.BadJOSEException;

public class BadJWSException
extends BadJOSEException {
    public BadJWSException(String message) {
        super(message);
    }

    public BadJWSException(String message, Throwable cause) {
        super(message, cause);
    }
}

