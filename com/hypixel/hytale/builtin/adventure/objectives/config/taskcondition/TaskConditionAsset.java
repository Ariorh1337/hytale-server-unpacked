/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.objectives.config.taskcondition;

import com.hypixel.hytale.builtin.adventure.objectives.config.taskcondition.SoloInventoryCondition;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Set;
import java.util.UUID;

public abstract class TaskConditionAsset {
    public static final CodecMapCodec<TaskConditionAsset> CODEC = new CodecMapCodec("Type");

    protected TaskConditionAsset() {
    }

    public abstract boolean isConditionFulfilled(ComponentAccessor<EntityStore> var1, Ref<EntityStore> var2, Set<UUID> var3);

    public abstract void consumeCondition(ComponentAccessor<EntityStore> var1, Ref<EntityStore> var2, Set<UUID> var3);

    public abstract boolean equals(Object var1);

    public abstract int hashCode();

    static {
        CODEC.register("SoloInventory", (Class<TaskConditionAsset>)SoloInventoryCondition.class, (Codec<TaskConditionAsset>)SoloInventoryCondition.CODEC);
    }
}

