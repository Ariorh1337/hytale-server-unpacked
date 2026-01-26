/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.jwt;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.PrivateKey;
import com.google.crypto.tink.jwt.JwtSignatureParameters;
import com.google.crypto.tink.jwt.JwtSignaturePublicKey;
import com.google.errorprone.annotations.Immutable;
import java.util.Optional;
import javax.annotation.Nullable;

@Immutable
public abstract class JwtSignaturePrivateKey
extends Key
implements PrivateKey {
    @Override
    public abstract JwtSignaturePublicKey getPublicKey();

    public Optional<String> getKid() {
        return this.getPublicKey().getKid();
    }

    @Override
    public abstract JwtSignatureParameters getParameters();

    @Override
    @Nullable
    public Integer getIdRequirementOrNull() {
        return this.getPublicKey().getIdRequirementOrNull();
    }
}

