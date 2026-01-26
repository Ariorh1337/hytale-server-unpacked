/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component.metric;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class ArchetypeChunkData {
    @Nonnull
    public static final Codec<ArchetypeChunkData> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ArchetypeChunkData.class, ArchetypeChunkData::new).append(new KeyedCodec<T[]>("ComponentTypes", Codec.STRING_ARRAY), (data, o) -> {
        data.componentTypes = o;
    }, data -> data.componentTypes).add()).append(new KeyedCodec<Integer>("EntityCount", Codec.INTEGER), (data, o) -> {
        data.entityCount = o;
    }, data -> data.entityCount).add()).build();
    @Nonnull
    private String[] componentTypes = new String[0];
    private int entityCount;

    public ArchetypeChunkData() {
    }

    public ArchetypeChunkData(@Nonnull String[] componentTypes, int entityCount) {
        this.componentTypes = componentTypes;
        this.entityCount = entityCount;
    }

    @Nonnull
    public String[] getComponentTypes() {
        return this.componentTypes;
    }

    public int getEntityCount() {
        return this.entityCount;
    }
}

