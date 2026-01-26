/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto.opts;

import com.nimbusds.jose.JWEDecrypterOption;
import com.nimbusds.jose.JWSSignerOption;
import com.nimbusds.jose.shaded.jcip.Immutable;

@Immutable
public final class AllowWeakRSAKey
implements JWSSignerOption,
JWEDecrypterOption {
    private static final AllowWeakRSAKey SINGLETON = new AllowWeakRSAKey();

    public static AllowWeakRSAKey getInstance() {
        return SINGLETON;
    }

    private AllowWeakRSAKey() {
    }

    public String toString() {
        return "AllowWeakRSAKey";
    }
}

