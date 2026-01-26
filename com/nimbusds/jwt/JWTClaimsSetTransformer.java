/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jwt;

import com.nimbusds.jwt.JWTClaimsSet;

public interface JWTClaimsSetTransformer<T> {
    public T transform(JWTClaimsSet var1);
}

