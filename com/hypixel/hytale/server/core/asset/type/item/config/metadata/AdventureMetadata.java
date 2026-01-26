/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.item.config.metadata;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class AdventureMetadata {
    public static final String KEY = "Adventure";
    public static final BuilderCodec<AdventureMetadata> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(AdventureMetadata.class, AdventureMetadata::new).appendInherited(new KeyedCodec<Boolean>("Cursed", Codec.BOOLEAN), (meta, s) -> {
        meta.cursed = s;
    }, meta -> meta.cursed ? Boolean.TRUE : null, (meta, parent) -> {
        meta.cursed = parent.cursed;
    }).add()).build();
    public static final KeyedCodec<AdventureMetadata> KEYED_CODEC = new KeyedCodec<AdventureMetadata>("Adventure", CODEC);
    private boolean cursed;

    public boolean isCursed() {
        return this.cursed;
    }

    public void setCursed(boolean cursed) {
        this.cursed = cursed;
    }
}

