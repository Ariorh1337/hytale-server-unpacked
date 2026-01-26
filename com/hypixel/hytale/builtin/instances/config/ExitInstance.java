/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.instances.config;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.type.gameplay.respawn.HomeOrSpawnPoint;
import com.hypixel.hytale.server.core.asset.type.gameplay.respawn.RespawnController;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class ExitInstance
implements RespawnController {
    @Nonnull
    public static final BuilderCodec<ExitInstance> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(ExitInstance.class, ExitInstance::new).append(new KeyedCodec<RespawnController>("Fallback", RespawnController.CODEC), (o, i) -> {
        o.fallback = i;
    }, o -> o.fallback).addValidator(Validators.nonNull()).add()).build();
    @Nonnull
    private RespawnController fallback = HomeOrSpawnPoint.INSTANCE;

    @Override
    public CompletableFuture<Void> respawnPlayer(@Nonnull World world, @Nonnull Ref<EntityStore> playerReference, @Nonnull ComponentAccessor<EntityStore> commandBuffer) {
        try {
            InstancesPlugin.exitInstance(playerReference, commandBuffer);
            return CompletableFuture.completedFuture(null);
        }
        catch (Exception e) {
            PlayerRef playerRefComponent = commandBuffer.getComponent(playerReference, PlayerRef.getComponentType());
            assert (playerRefComponent != null);
            ((HytaleLogger.Api)InstancesPlugin.get().getLogger().at(Level.WARNING).withCause(e)).log(playerRefComponent.getUsername() + " failed to leave an instance");
            return this.fallback.respawnPlayer(world, playerReference, commandBuffer);
        }
    }
}

