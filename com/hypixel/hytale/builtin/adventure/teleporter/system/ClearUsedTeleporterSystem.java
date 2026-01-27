/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.teleporter.system;

import com.hypixel.hytale.builtin.adventure.teleporter.interaction.server.UsedTeleporter;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Vector2d;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.UUID;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class ClearUsedTeleporterSystem
extends EntityTickingSystem<EntityStore> {
    @Override
    public void tick(float dt, int index, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        TransformComponent transformComponent;
        UsedTeleporter usedTeleporter;
        World world = store.getExternalData().getWorld();
        if (ClearUsedTeleporterSystem.shouldClear(world, usedTeleporter = archetypeChunk.getComponent(index, UsedTeleporter.getComponentType()), transformComponent = archetypeChunk.getComponent(index, TransformComponent.getComponentType()))) {
            Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
            commandBuffer.removeComponent(ref, UsedTeleporter.getComponentType());
        }
    }

    private static boolean shouldClear(World entityWorld, UsedTeleporter usedTeleporter, @Nullable TransformComponent transformComponent) {
        if (transformComponent == null) {
            return true;
        }
        UUID destinationWorldUuid = usedTeleporter.getDestinationWorldUuid();
        if (destinationWorldUuid != null && !entityWorld.getWorldConfig().getUuid().equals(destinationWorldUuid)) {
            return true;
        }
        Vector3d entityPosition = transformComponent.getPosition();
        Vector3d destinationPosition = usedTeleporter.getDestinationPosition();
        double deltaY = Math.abs(entityPosition.y - destinationPosition.y);
        double distanceXZsq = Vector2d.distanceSquared(entityPosition.x, entityPosition.z, destinationPosition.x, destinationPosition.z);
        return deltaY > usedTeleporter.getClearOutY() || distanceXZsq > usedTeleporter.getClearOutXZ();
    }

    @Override
    @NullableDecl
    public Query<EntityStore> getQuery() {
        return UsedTeleporter.getComponentType();
    }
}

