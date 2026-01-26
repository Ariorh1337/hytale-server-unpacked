/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.aead;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.aead.AeadParameters;
import com.google.crypto.tink.util.Bytes;

public abstract class AeadKey
extends Key {
    public abstract Bytes getOutputPrefix();

    @Override
    public abstract AeadParameters getParameters();
}

