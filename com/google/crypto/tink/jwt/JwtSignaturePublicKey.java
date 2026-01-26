/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.jwt;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.jwt.JwtSignatureParameters;
import java.util.Optional;

public abstract class JwtSignaturePublicKey
extends Key {
    public abstract Optional<String> getKid();

    @Override
    public abstract JwtSignatureParameters getParameters();
}

