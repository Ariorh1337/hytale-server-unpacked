/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.daead;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.daead.DeterministicAeadParameters;
import com.google.crypto.tink.util.Bytes;

public abstract class DeterministicAeadKey
extends Key {
    public abstract Bytes getOutputPrefix();

    @Override
    public abstract DeterministicAeadParameters getParameters();
}

