/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jwt.proc;

import com.nimbusds.jwt.proc.BadJWTException;

public class ExpiredJWTException
extends BadJWTException {
    public ExpiredJWTException(String message) {
        super(message);
    }
}

