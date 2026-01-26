/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec;

import com.hypixel.hytale.codec.Codec;

public interface WrappedCodec<T> {
    public Codec<T> getChildCodec();
}

