/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.spawn;

import com.hypixel.hytale.codec.lookup.BuilderCodecMapCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.UUID;
import javax.annotation.Nonnull;

public interface ISpawnProvider {
    @Nonnull
    public static final BuilderCodecMapCodec<ISpawnProvider> CODEC;

    default public Transform getSpawnPoint(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        UUIDComponent uuidComponent = componentAccessor.getComponent(ref, UUIDComponent.getComponentType());
        if (!1.$assertionsDisabled && uuidComponent == null) {
            throw new AssertionError();
        }
        World world = componentAccessor.getExternalData().getWorld();
        return this.getSpawnPoint(world, uuidComponent.getUuid());
    }

    @Deprecated(forRemoval=true)
    default public Transform getSpawnPoint(@Nonnull Entity entity) {
        return this.getSpawnPoint(entity.getWorld(), entity.getUuid());
    }

    public Transform getSpawnPoint(@Nonnull World var1, @Nonnull UUID var2);

    @Deprecated
    public Transform[] getSpawnPoints();

    public boolean isWithinSpawnDistance(@Nonnull Vector3d var1, double var2);

    static {
        if (1.$assertionsDisabled) {
            // empty if block
        }
        CODEC = new BuilderCodecMapCodec(true);
    }
}

