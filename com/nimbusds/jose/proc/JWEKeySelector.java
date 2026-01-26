/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.proc;

import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.Key;
import java.util.List;

public interface JWEKeySelector<C extends SecurityContext> {
    public List<? extends Key> selectJWEKeys(JWEHeader var1, C var2) throws KeySourceException;
}

