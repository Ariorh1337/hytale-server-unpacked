/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.keyderivation;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.keyderivation.KeyDerivationParameters;

public abstract class KeyDerivationKey
extends Key {
    @Override
    public abstract KeyDerivationParameters getParameters();
}

