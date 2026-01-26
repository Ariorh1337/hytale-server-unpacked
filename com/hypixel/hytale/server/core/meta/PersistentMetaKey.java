/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.meta;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.server.core.meta.MetaKey;
import javax.annotation.Nonnull;

public class PersistentMetaKey<T>
extends MetaKey<T> {
    private final String key;
    private final Codec<T> codec;

    PersistentMetaKey(int id, String key, Codec<T> codec) {
        super(id);
        this.key = key;
        this.codec = codec;
    }

    public String getKey() {
        return this.key;
    }

    public Codec<T> getCodec() {
        return this.codec;
    }

    @Override
    @Nonnull
    public String toString() {
        return "PersistentMetaKey{key=" + this.key + "codec=" + String.valueOf(this.codec) + "}";
    }
}

