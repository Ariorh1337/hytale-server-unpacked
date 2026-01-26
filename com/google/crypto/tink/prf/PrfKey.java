/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.prf;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.prf.PrfParameters;

public abstract class PrfKey
extends Key {
    @Override
    public abstract PrfParameters getParameters();
}

