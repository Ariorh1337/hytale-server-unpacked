/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.beds.sleep.systems.player;

import com.hypixel.hytale.builtin.beds.sleep.components.PlayerSomnolence;
import com.hypixel.hytale.builtin.mounts.MountedComponent;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.protocol.BlockMountType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WakeUpOnDismountSystem
extends RefChangeSystem<EntityStore, MountedComponent> {
    @Nonnull
    private final ComponentType<EntityStore, MountedComponent> mountedComponentType;
    @Nonnull
    private final ComponentType<EntityStore, PlayerSomnolence> playerSomnolenceComponentType;

    public WakeUpOnDismountSystem(@Nonnull ComponentType<EntityStore, MountedComponent> mountedComponentType, @Nonnull ComponentType<EntityStore, PlayerSomnolence> playerSomnolenceComponentType) {
        this.mountedComponentType = mountedComponentType;
        this.playerSomnolenceComponentType = playerSomnolenceComponentType;
    }

    @Override
    @Nonnull
    public ComponentType<EntityStore, MountedComponent> componentType() {
        return this.mountedComponentType;
    }

    @Override
    @Nonnull
    public Query<EntityStore> getQuery() {
        return this.mountedComponentType;
    }

    @Override
    public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull MountedComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
    }

    @Override
    public void onComponentSet(@Nonnull Ref<EntityStore> ref, @Nullable MountedComponent oldComponent, @Nonnull MountedComponent newComponent, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
    }

    @Override
    public void onComponentRemoved(@Nonnull Ref<EntityStore> ref, @Nonnull MountedComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        if (component.getBlockMountType() == BlockMountType.Bed) {
            commandBuffer.putComponent(ref, this.playerSomnolenceComponentType, PlayerSomnolence.AWAKE);
        }
    }
}

