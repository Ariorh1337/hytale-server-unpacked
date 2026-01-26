/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.spawning.blockstates;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.spawning.SpawningPlugin;
import javax.annotation.Nonnull;

public class SpawnMarkerBlockReference
implements Component<EntityStore> {
    public static final BuilderCodec<SpawnMarkerBlockReference> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(SpawnMarkerBlockReference.class, SpawnMarkerBlockReference::new).append(new KeyedCodec<Vector3i>("BlockPosition", Vector3i.CODEC), (reference, o) -> {
        reference.blockPosition = o;
    }, reference -> reference.blockPosition).add()).build();
    private Vector3i blockPosition;
    private float originLostTimeout = 30.0f;

    public static ComponentType<EntityStore, SpawnMarkerBlockReference> getComponentType() {
        return SpawningPlugin.get().getSpawnMarkerBlockReferenceComponentType();
    }

    private SpawnMarkerBlockReference() {
    }

    public SpawnMarkerBlockReference(Vector3i blockPosition) {
        this.blockPosition = blockPosition;
    }

    public Vector3i getBlockPosition() {
        return this.blockPosition;
    }

    public void refreshOriginLostTimeout() {
        this.originLostTimeout = 30.0f;
    }

    public boolean tickOriginLostTimeout(float dt) {
        float f;
        this.originLostTimeout -= dt;
        return f <= 0.0f;
    }

    @Override
    @Nonnull
    public Component<EntityStore> clone() {
        SpawnMarkerBlockReference reference = new SpawnMarkerBlockReference();
        reference.blockPosition = this.blockPosition;
        return reference;
    }
}

