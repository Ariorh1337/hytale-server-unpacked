/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.blocktype.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import javax.annotation.Nonnull;

public class BlockMovementSettings
implements NetworkSerializable<com.hypixel.hytale.protocol.BlockMovementSettings> {
    public static final BuilderCodec<BlockMovementSettings> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(BlockMovementSettings.class, BlockMovementSettings::new).append(new KeyedCodec<Boolean>("IsClimbable", Codec.BOOLEAN), (blockMovementSettings, o) -> {
        blockMovementSettings.isClimbable = o;
    }, blockMovementSettings -> blockMovementSettings.isClimbable).add()).append(new KeyedCodec<Boolean>("IsBouncy", Codec.BOOLEAN), (blockMovementSettings, o) -> {
        blockMovementSettings.isBouncy = o;
    }, blockMovementSettings -> blockMovementSettings.isBouncy).add()).append(new KeyedCodec<Float>("BounceVelocity", Codec.FLOAT), (blockMovementSettings, o) -> {
        blockMovementSettings.bounceVelocity = o.floatValue();
    }, blockMovementSettings -> Float.valueOf(blockMovementSettings.bounceVelocity)).add()).append(new KeyedCodec<Float>("ClimbUpSpeedMultiplier", Codec.FLOAT), (blockMovementSettings, o) -> {
        blockMovementSettings.climbUpSpeedMultiplier = o.floatValue();
    }, blockMovementSettings -> Float.valueOf(blockMovementSettings.climbUpSpeedMultiplier)).add()).append(new KeyedCodec<Float>("ClimbDownSpeedMultiplier", Codec.FLOAT), (blockMovementSettings, o) -> {
        blockMovementSettings.climbDownSpeedMultiplier = o.floatValue();
    }, blockMovementSettings -> Float.valueOf(blockMovementSettings.climbDownSpeedMultiplier)).add()).append(new KeyedCodec<Float>("ClimbLateralSpeedMultiplier", Codec.FLOAT), (blockMovementSettings, o) -> {
        blockMovementSettings.climbLateralSpeedMultiplier = o.floatValue();
    }, blockMovementSettings -> Float.valueOf(blockMovementSettings.climbLateralSpeedMultiplier)).add()).append(new KeyedCodec<Float>("Drag", Codec.FLOAT), (blockMovementSettings, o) -> {
        blockMovementSettings.drag = o.floatValue();
    }, blockMovementSettings -> Float.valueOf(blockMovementSettings.drag)).add()).append(new KeyedCodec<Float>("Friction", Codec.FLOAT), (blockMovementSettings, o) -> {
        blockMovementSettings.friction = o.floatValue();
    }, blockMovementSettings -> Float.valueOf(blockMovementSettings.friction)).add()).append(new KeyedCodec<Float>("TerminalVelocityModifier", Codec.FLOAT), (blockMovementSettings, o) -> {
        blockMovementSettings.terminalVelocityModifier = o.floatValue();
    }, blockMovementSettings -> Float.valueOf(blockMovementSettings.terminalVelocityModifier)).add()).append(new KeyedCodec<Float>("HorizontalSpeedMultiplier", Codec.FLOAT), (blockMovementSettings, o) -> {
        blockMovementSettings.horizontalSpeedMultiplier = o.floatValue();
    }, blockMovementSettings -> Float.valueOf(blockMovementSettings.horizontalSpeedMultiplier)).add()).append(new KeyedCodec<Float>("JumpForceMultiplier", Codec.FLOAT), (blockMovementSettings, o) -> {
        blockMovementSettings.jumpForceMultiplier = o.floatValue();
    }, blockMovementSettings -> Float.valueOf(blockMovementSettings.jumpForceMultiplier)).add()).build();
    private boolean isClimbable;
    private boolean isBouncy;
    private float bounceVelocity;
    private float drag = 0.82f;
    private float friction = 0.18f;
    private float climbUpSpeedMultiplier = 1.0f;
    private float climbDownSpeedMultiplier = 1.0f;
    private float climbLateralSpeedMultiplier = 1.0f;
    private float terminalVelocityModifier = 1.0f;
    private float horizontalSpeedMultiplier = 1.0f;
    private float jumpForceMultiplier = 1.0f;

    public BlockMovementSettings(boolean isClimbable, boolean isBouncy, float bounceVelocity, float drag, float friction, float climbUpSpeed, float climbDownSpeed, float climbLateralSpeedMultiplier, float terminalVelocityModifier, float horizontalSpeedMultiplier, float jumpForceMultiplier) {
        this.isClimbable = isClimbable;
        this.isBouncy = isBouncy;
        this.bounceVelocity = bounceVelocity;
        this.climbUpSpeedMultiplier = climbUpSpeed;
        this.climbDownSpeedMultiplier = climbDownSpeed;
        this.climbLateralSpeedMultiplier = climbLateralSpeedMultiplier;
        this.drag = drag;
        this.friction = friction;
        this.terminalVelocityModifier = terminalVelocityModifier;
        this.horizontalSpeedMultiplier = horizontalSpeedMultiplier;
        this.jumpForceMultiplier = jumpForceMultiplier;
    }

    protected BlockMovementSettings() {
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.BlockMovementSettings toPacket() {
        com.hypixel.hytale.protocol.BlockMovementSettings packet = new com.hypixel.hytale.protocol.BlockMovementSettings();
        packet.isClimbable = this.isClimbable;
        packet.isBouncy = this.isBouncy;
        packet.bounceVelocity = this.bounceVelocity;
        packet.climbUpSpeedMultiplier = this.climbUpSpeedMultiplier;
        packet.climbDownSpeedMultiplier = this.climbDownSpeedMultiplier;
        packet.climbLateralSpeedMultiplier = this.climbLateralSpeedMultiplier;
        packet.drag = this.drag;
        packet.friction = this.friction;
        packet.terminalVelocityModifier = this.terminalVelocityModifier;
        packet.horizontalSpeedMultiplier = this.horizontalSpeedMultiplier;
        packet.jumpForceMultiplier = this.jumpForceMultiplier;
        return packet;
    }

    public boolean isClimbable() {
        return this.isClimbable;
    }

    public boolean isBouncy() {
        return this.isBouncy;
    }

    public float getBounceVelocity() {
        return this.bounceVelocity;
    }

    public float getDrag() {
        return this.drag;
    }

    public float getFriction() {
        return this.friction;
    }

    public float getClimbUpSpeedMultiplier() {
        return this.climbUpSpeedMultiplier;
    }

    public float getClimbDownSpeedMultiplier() {
        return this.climbDownSpeedMultiplier;
    }

    public float getClimbLateralSpeedMultiplier() {
        return this.climbLateralSpeedMultiplier;
    }

    public float getTerminalVelocityModifier() {
        return this.terminalVelocityModifier;
    }

    public float getHorizontalSpeedMultiplier() {
        return this.horizontalSpeedMultiplier;
    }

    public float jumpForceMultiplier() {
        return this.jumpForceMultiplier;
    }

    @Nonnull
    public String toString() {
        return "BlockMovementSettings{isClimbable=" + this.isClimbable + "isBouncy=" + this.isBouncy + "bounceSpeed=" + this.bounceVelocity + ", climbUpSpeedMultiplier=" + this.climbUpSpeedMultiplier + ", climbDownSpeedMultiplier=" + this.climbDownSpeedMultiplier + ", climbLateralSpeedMultiplier=" + this.climbLateralSpeedMultiplier + ", drag=" + this.drag + ", friction=" + this.friction + ", terminalVelocityModifier=" + this.terminalVelocityModifier + ", horizontalSpeedMultiplier=" + this.horizontalSpeedMultiplier + ", jumpForceMultiplier=" + this.jumpForceMultiplier + "}";
    }
}

