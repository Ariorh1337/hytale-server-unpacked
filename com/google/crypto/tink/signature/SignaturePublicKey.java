/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.signature;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.signature.SignatureParameters;
import com.google.crypto.tink.util.Bytes;
import com.google.errorprone.annotations.Immutable;

@Immutable
public abstract class SignaturePublicKey
extends Key {
    public abstract Bytes getOutputPrefix();

    @Override
    public abstract SignatureParameters getParameters();
}

