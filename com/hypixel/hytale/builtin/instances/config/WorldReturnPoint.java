/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.instances.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.vector.Transform;
import java.util.UUID;
import javax.annotation.Nonnull;

public class WorldReturnPoint {
    public static final BuilderCodec<WorldReturnPoint> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(WorldReturnPoint.class, WorldReturnPoint::new).documentation("A world/location pair that is used as a place to return players to.")).append(new KeyedCodec<UUID>("World", Codec.UUID_BINARY), (o, i) -> {
        o.world = i;
    }, o -> o.world).documentation("The UUID of the world to return the player to.").addValidator(Validators.nonNull()).add()).append(new KeyedCodec<Transform>("ReturnPoint", Transform.CODEC), (o, i) -> {
        o.returnPoint = i;
    }, o -> o.returnPoint).documentation("The location to send the player to.").addValidator(Validators.nonNull()).add()).append(new KeyedCodec<Boolean>("ReturnOnReconnect", Codec.BOOLEAN), (o, i) -> {
        o.returnOnReconnect = i;
    }, o -> o.returnOnReconnect).documentation("Whether this point should be triggered when a player reconnects into a world.").add()).build();
    private UUID world;
    private Transform returnPoint;
    private boolean returnOnReconnect = false;

    public WorldReturnPoint() {
    }

    public WorldReturnPoint(UUID world, Transform returnPoint, boolean returnOnReconnect) {
        this.world = world;
        this.returnPoint = returnPoint;
        this.returnOnReconnect = returnOnReconnect;
    }

    public UUID getWorld() {
        return this.world;
    }

    public void setWorld(UUID world) {
        this.world = world;
    }

    public Transform getReturnPoint() {
        return this.returnPoint;
    }

    public void setReturnPoint(Transform returnPoint) {
        this.returnPoint = returnPoint;
    }

    public boolean isReturnOnReconnect() {
        return this.returnOnReconnect;
    }

    public void setReturnOnReconnect(boolean returnOnReconnect) {
        this.returnOnReconnect = returnOnReconnect;
    }

    @Nonnull
    public WorldReturnPoint clone() {
        return new WorldReturnPoint(this.world, this.returnPoint, this.returnOnReconnect);
    }
}

