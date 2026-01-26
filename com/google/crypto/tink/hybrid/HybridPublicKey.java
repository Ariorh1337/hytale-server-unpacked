/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.hybrid;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.hybrid.HybridParameters;
import com.google.crypto.tink.util.Bytes;
import com.google.errorprone.annotations.Immutable;

@Immutable
public abstract class HybridPublicKey
extends Key {
    public abstract Bytes getOutputPrefix();

    @Override
    public abstract HybridParameters getParameters();
}

