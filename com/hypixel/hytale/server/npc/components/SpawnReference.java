/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.components;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.entity.reference.InvalidatablePersistentRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public abstract class SpawnReference
implements Component<EntityStore> {
    protected static final BuilderCodec<SpawnReference> BASE_CODEC = ((BuilderCodec.Builder)BuilderCodec.abstractBuilder(SpawnReference.class).append(new KeyedCodec<InvalidatablePersistentRef>("SpawnMarker", InvalidatablePersistentRef.CODEC), (reference, entityReference) -> {
        reference.reference = entityReference;
    }, reference -> reference.reference).add()).build();
    public static final float MARKER_LOST_TIMEOUT = 30.0f;
    protected InvalidatablePersistentRef reference = new InvalidatablePersistentRef();
    private float markerLostTimeoutCounter;

    public InvalidatablePersistentRef getReference() {
        return this.reference;
    }

    public boolean tickMarkerLostTimeoutCounter(float dt) {
        float f;
        this.markerLostTimeoutCounter -= dt;
        return f <= 0.0f;
    }

    public void refreshTimeoutCounter() {
        this.markerLostTimeoutCounter = 30.0f;
    }

    @Override
    public abstract Component<EntityStore> clone();
}

