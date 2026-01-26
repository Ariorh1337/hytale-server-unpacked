/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.hybrid;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.PrivateKey;
import com.google.crypto.tink.hybrid.HybridParameters;
import com.google.crypto.tink.hybrid.HybridPublicKey;
import com.google.crypto.tink.util.Bytes;
import com.google.errorprone.annotations.Immutable;
import javax.annotation.Nullable;

@Immutable
public abstract class HybridPrivateKey
extends Key
implements PrivateKey {
    @Override
    public abstract HybridPublicKey getPublicKey();

    public final Bytes getOutputPrefix() {
        return this.getPublicKey().getOutputPrefix();
    }

    @Override
    @Nullable
    public Integer getIdRequirementOrNull() {
        return this.getPublicKey().getIdRequirementOrNull();
    }

    @Override
    public HybridParameters getParameters() {
        return this.getPublicKey().getParameters();
    }
}

