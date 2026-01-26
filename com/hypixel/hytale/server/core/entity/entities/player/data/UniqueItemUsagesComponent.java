/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.entity.entities.player.data;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UniqueItemUsagesComponent
implements Component<EntityStore> {
    @Nonnull
    public static final BuilderCodec<UniqueItemUsagesComponent> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(UniqueItemUsagesComponent.class, UniqueItemUsagesComponent::new).append(new KeyedCodec<T[]>("UniqueItemUsed", new ArrayCodec<String>(Codec.STRING, String[]::new)), (playerMemories, usages) -> {
        if (usages == null) {
            return;
        }
        Collections.addAll(playerMemories.usedUniqueItems, usages);
    }, playerMemories -> (String[])playerMemories.usedUniqueItems.toArray(String[]::new)).add()).build();
    private final Set<String> usedUniqueItems = new HashSet<String>();

    public static ComponentType<EntityStore, UniqueItemUsagesComponent> getComponentType() {
        return EntityModule.get().getUniqueItemUsagesComponentType();
    }

    @Override
    @Nullable
    public Component<EntityStore> clone() {
        UniqueItemUsagesComponent component = new UniqueItemUsagesComponent();
        component.usedUniqueItems.addAll(this.usedUniqueItems);
        return component;
    }

    public boolean hasUsedUniqueItem(String itemId) {
        return this.usedUniqueItems.contains(itemId);
    }

    public void recordUniqueItemUsage(String itemId) {
        this.usedUniqueItems.add(itemId);
    }
}

