/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.lookup;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.lookup.StringCodecMapCodec;

public class BuilderCodecMapCodec<T>
extends StringCodecMapCodec<T, BuilderCodec<? extends T>> {
    public BuilderCodecMapCodec() {
    }

    public BuilderCodecMapCodec(boolean allowDefault) {
        super(allowDefault);
    }

    public BuilderCodecMapCodec(String id) {
        super(id);
    }

    public BuilderCodecMapCodec(String key, boolean allowDefault) {
        super(key, allowDefault);
    }

    public T getDefault() {
        return ((BuilderCodec)this.getDefaultCodec()).getDefaultValue();
    }
}

