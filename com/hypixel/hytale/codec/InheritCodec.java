/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.RawJsonInheritCodec;
import javax.annotation.Nullable;
import org.bson.BsonDocument;

public interface InheritCodec<T>
extends Codec<T>,
RawJsonInheritCodec<T> {
    @Nullable
    public T decodeAndInherit(BsonDocument var1, T var2, ExtraInfo var3);

    public void decodeAndInherit(BsonDocument var1, T var2, T var3, ExtraInfo var4);
}

