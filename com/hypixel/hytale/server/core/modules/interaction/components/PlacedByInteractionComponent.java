/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import java.util.UUID;
import javax.annotation.Nullable;

public class PlacedByInteractionComponent
implements Component<ChunkStore> {
    public static final BuilderCodec<PlacedByInteractionComponent> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(PlacedByInteractionComponent.class, PlacedByInteractionComponent::new).appendInherited(new KeyedCodec<UUID>("WhoPlacedUuid", Codec.UUID_BINARY), (o, i) -> {
        o.whoPlacedUuid = i;
    }, o -> o.whoPlacedUuid, (o, parent) -> {
        o.whoPlacedUuid = parent.whoPlacedUuid;
    }).add()).build();
    private UUID whoPlacedUuid;

    public static ComponentType<ChunkStore, PlacedByInteractionComponent> getComponentType() {
        return InteractionModule.get().getPlacedByComponentType();
    }

    public PlacedByInteractionComponent() {
    }

    public PlacedByInteractionComponent(UUID whoPlacedUuid) {
        this.whoPlacedUuid = whoPlacedUuid;
    }

    public UUID getWhoPlacedUuid() {
        return this.whoPlacedUuid;
    }

    @Override
    @Nullable
    public Component<ChunkStore> clone() {
        PlacedByInteractionComponent clone = new PlacedByInteractionComponent();
        clone.whoPlacedUuid = this.whoPlacedUuid;
        return clone;
    }
}

