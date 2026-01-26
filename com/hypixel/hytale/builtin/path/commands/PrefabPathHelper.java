/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.path.commands;

import com.hypixel.hytale.builtin.path.PathPlugin;
import com.hypixel.hytale.builtin.path.entities.PatrolPathMarkerEntity;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.entity.nameplate.Nameplate;
import com.hypixel.hytale.server.core.modules.entity.component.DisplayNameComponent;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.UUID;
import javax.annotation.Nonnull;

public final class PrefabPathHelper {
    private PrefabPathHelper() {
    }

    public static void addMarker(@Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> playerRef, @Nonnull UUID pathId, @Nonnull String pathName, double pauseTime, float obsvAngleDegrees, short targetIndex, int worldgenId) {
        Vector3f playerBodyRotation;
        World world = store.getExternalData().getWorld();
        PatrolPathMarkerEntity waypoint = new PatrolPathMarkerEntity(world);
        waypoint.initialise(pathId, pathName, targetIndex, pauseTime, obsvAngleDegrees * ((float)Math.PI / 180), worldgenId, store);
        TransformComponent transformComponent = store.getComponent(playerRef, TransformComponent.getComponentType());
        assert (transformComponent != null);
        Vector3d playerPosition = transformComponent.getPosition().clone();
        PatrolPathMarkerEntity waypointEntity = world.spawnEntity(waypoint, playerPosition, playerBodyRotation = transformComponent.getRotation().clone());
        if (waypointEntity == null) {
            return;
        }
        Ref<EntityStore> waypointRef = waypointEntity.getReference();
        if (waypointRef == null || !waypointRef.isValid()) {
            return;
        }
        TransformComponent waypointTransformComponent = store.getComponent(waypointRef, TransformComponent.getComponentType());
        Vector3f waypointRotation = waypointTransformComponent.getRotation();
        waypointRotation.assign(playerBodyRotation);
        Model model = PathPlugin.get().getPathMarkerModel();
        store.putComponent(waypointRef, ModelComponent.getComponentType(), new ModelComponent(model));
        String displayName = PatrolPathMarkerEntity.generateDisplayName(worldgenId, waypointEntity);
        Message displayNameMessage = Message.raw(displayName);
        store.putComponent(waypointRef, DisplayNameComponent.getComponentType(), new DisplayNameComponent(displayNameMessage));
        store.putComponent(waypointRef, Nameplate.getComponentType(), new Nameplate(displayName));
    }
}

