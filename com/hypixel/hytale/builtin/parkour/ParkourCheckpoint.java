/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.parkour;

import com.hypixel.hytale.builtin.parkour.ParkourPlugin;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public class ParkourCheckpoint
implements Component<EntityStore> {
    public static final BuilderCodec<ParkourCheckpoint> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(ParkourCheckpoint.class, ParkourCheckpoint::new).append(new KeyedCodec<Integer>("CheckpointIndex", Codec.INTEGER), (parkourCheckpoint, integer) -> {
        parkourCheckpoint.index = integer;
    }, parkourCheckpoint -> parkourCheckpoint.index).add()).build();
    protected int index;

    public static ComponentType<EntityStore, ParkourCheckpoint> getComponentType() {
        return ParkourPlugin.get().getParkourCheckpointComponentType();
    }

    public ParkourCheckpoint(int index) {
        this.index = index;
    }

    protected ParkourCheckpoint() {
    }

    public int getIndex() {
        return this.index;
    }

    @Override
    @Nonnull
    public Component<EntityStore> clone() {
        ParkourCheckpoint parkourCheckpoint = new ParkourCheckpoint();
        parkourCheckpoint.index = this.index;
        return parkourCheckpoint;
    }
}

