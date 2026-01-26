/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.mac;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.mac.MacParameters;
import com.google.crypto.tink.util.Bytes;

public abstract class MacKey
extends Key {
    public abstract Bytes getOutputPrefix();

    @Override
    public abstract MacParameters getParameters();
}

