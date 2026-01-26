/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.instances.config;

import com.hypixel.hytale.builtin.instances.config.InstanceDiscoveryConfig;
import com.hypixel.hytale.builtin.instances.config.WorldReturnPoint;
import com.hypixel.hytale.builtin.instances.removal.RemovalCondition;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InstanceWorldConfig {
    public static final String ID = "Instance";
    @Nonnull
    public static final BuilderCodec<InstanceWorldConfig> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(InstanceWorldConfig.class, InstanceWorldConfig::new).append(new KeyedCodec<T[]>("RemovalConditions", new ArrayCodec<RemovalCondition>(RemovalCondition.CODEC, RemovalCondition[]::new)), (o, i) -> {
        o.removalConditions = i;
    }, o -> o.removalConditions).documentation("The set of conditions that have to be met to remove the world.\n\nIf no conditions are provided (e.g. a empty list) then the world will not be removed automatically").addValidator(Validators.nonNull()).add()).append(new KeyedCodec<WorldReturnPoint>("ReturnPoint", WorldReturnPoint.CODEC), (o, i) -> {
        o.returnPoint = i;
    }, o -> o.returnPoint).documentation("The location to send players to when leaving this world.").add()).append(new KeyedCodec<Boolean>("PreventReconnection", Codec.BOOLEAN), (o, i) -> {
        o.preventReconnection = i;
    }, o -> o.preventReconnection).documentation("Whether to prevent reconnecting into this world.\n\nPlayers that reconnect back into the world will forced out of the world to the return point (or their own).").add()).append(new KeyedCodec<InstanceDiscoveryConfig>("Discovery", InstanceDiscoveryConfig.CODEC), (o, i) -> {
        o.discovery = i;
    }, o -> o.discovery).documentation("Optional discovery configuration for displaying an event title when a player enters this instance for the first time.").add()).build();
    @Nonnull
    private RemovalCondition[] removalConditions = RemovalCondition.EMPTY;
    @Nullable
    private WorldReturnPoint returnPoint;
    private boolean preventReconnection = false;
    @Nullable
    private InstanceDiscoveryConfig discovery;

    @Nullable
    public static InstanceWorldConfig get(@Nonnull WorldConfig config) {
        return config.getPluginConfig().get(InstanceWorldConfig.class);
    }

    @Nonnull
    public static InstanceWorldConfig ensureAndGet(@Nonnull WorldConfig config) {
        return config.getPluginConfig().computeIfAbsent(InstanceWorldConfig.class, v -> new InstanceWorldConfig());
    }

    public boolean shouldPreventReconnection() {
        return this.preventReconnection;
    }

    @Nonnull
    public RemovalCondition[] getRemovalConditions() {
        return this.removalConditions;
    }

    public void setRemovalConditions(RemovalCondition ... removalConditions) {
        this.removalConditions = removalConditions;
    }

    @Nullable
    public WorldReturnPoint getReturnPoint() {
        return this.returnPoint;
    }

    public void setReturnPoint(@Nullable WorldReturnPoint returnPoint) {
        this.returnPoint = returnPoint;
    }

    @Nullable
    public InstanceDiscoveryConfig getDiscovery() {
        return this.discovery;
    }

    public void setDiscovery(@Nullable InstanceDiscoveryConfig discovery) {
        this.discovery = discovery;
    }
}

