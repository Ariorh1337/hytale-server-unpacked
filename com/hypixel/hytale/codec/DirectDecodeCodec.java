/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import org.bson.BsonValue;

public interface DirectDecodeCodec<T>
extends Codec<T> {
    public void decode(BsonValue var1, T var2, ExtraInfo var3);
}

