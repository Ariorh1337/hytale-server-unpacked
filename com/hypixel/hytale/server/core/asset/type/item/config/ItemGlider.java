/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.item.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.io.NetworkSerializable;

public class ItemGlider
implements NetworkSerializable<com.hypixel.hytale.protocol.ItemGlider> {
    public static final BuilderCodec<ItemGlider> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ItemGlider.class, ItemGlider::new).appendInherited(new KeyedCodec<Float>("TerminalVelocity", Codec.FLOAT), (o, i) -> {
        o.terminalVelocity = i.floatValue();
    }, o -> Float.valueOf(o.terminalVelocity), (o, p) -> {
        o.terminalVelocity = p.terminalVelocity;
    }).documentation("The maximum speed the player can fall while gliding.").add()).appendInherited(new KeyedCodec<Float>("FallSpeedMultiplier", Codec.FLOAT), (o, i) -> {
        o.fallSpeedMultiplier = i.floatValue();
    }, o -> Float.valueOf(o.fallSpeedMultiplier), (o, p) -> {
        o.fallSpeedMultiplier = p.fallSpeedMultiplier;
    }).documentation("The rate at which the fall speed is incremented.").add()).appendInherited(new KeyedCodec<Float>("HorizontalSpeedMultiplier", Codec.FLOAT), (o, i) -> {
        o.horizontalSpeedMultiplier = i.floatValue();
    }, o -> Float.valueOf(o.horizontalSpeedMultiplier), (o, p) -> {
        o.horizontalSpeedMultiplier = p.horizontalSpeedMultiplier;
    }).documentation("The rate at which the horizontal move speed is incremented.").add()).appendInherited(new KeyedCodec<Float>("Speed", Codec.FLOAT), (o, i) -> {
        o.speed = i.floatValue();
    }, o -> Float.valueOf(o.speed), (o, p) -> {
        o.speed = p.speed;
    }).documentation("The horizontal movement speed of the glider.").add()).build();
    protected float terminalVelocity;
    protected float fallSpeedMultiplier;
    protected float horizontalSpeedMultiplier;
    protected float speed;

    public float getTerminalVelocity() {
        return this.terminalVelocity;
    }

    public float getFallSpeedMultiplier() {
        return this.fallSpeedMultiplier;
    }

    public float getHorizontalSpeedMultiplier() {
        return this.horizontalSpeedMultiplier;
    }

    public float getSpeed() {
        return this.speed;
    }

    @Override
    public com.hypixel.hytale.protocol.ItemGlider toPacket() {
        com.hypixel.hytale.protocol.ItemGlider packet = new com.hypixel.hytale.protocol.ItemGlider();
        packet.terminalVelocity = this.terminalVelocity;
        packet.fallSpeedMultiplier = this.fallSpeedMultiplier;
        packet.horizontalSpeedMultiplier = this.horizontalSpeedMultiplier;
        packet.speed = this.speed;
        return packet;
    }
}

