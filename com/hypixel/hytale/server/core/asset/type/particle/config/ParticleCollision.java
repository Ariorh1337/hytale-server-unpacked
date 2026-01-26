/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.particle.config;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.protocol.ParticleCollisionAction;
import com.hypixel.hytale.protocol.ParticleCollisionBlockType;
import com.hypixel.hytale.protocol.ParticleRotationInfluence;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import javax.annotation.Nonnull;

public class ParticleCollision
implements NetworkSerializable<com.hypixel.hytale.protocol.ParticleCollision> {
    public static final BuilderCodec<ParticleCollision> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ParticleCollision.class, ParticleCollision::new).append(new KeyedCodec<ParticleCollisionBlockType>("BlockType", new EnumCodec<ParticleCollisionBlockType>(ParticleCollisionBlockType.class)), (particleCollision, o) -> {
        particleCollision.blockType = o;
    }, particleCollision -> particleCollision.blockType).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<ParticleCollisionAction>("Action", new EnumCodec<ParticleCollisionAction>(ParticleCollisionAction.class)), (particleCollision, o) -> {
        particleCollision.action = o;
    }, particleCollision -> particleCollision.action).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<ParticleRotationInfluence>("ParticleRotationInfluence", new EnumCodec<ParticleRotationInfluence>(ParticleRotationInfluence.class)), (particleCollision, o) -> {
        particleCollision.particleRotationInfluence = o;
    }, particleCollision -> particleCollision.particleRotationInfluence).add()).build();
    @Nonnull
    private ParticleCollisionBlockType blockType = ParticleCollisionBlockType.None;
    @Nonnull
    private ParticleCollisionAction action = ParticleCollisionAction.Expire;
    private ParticleRotationInfluence particleRotationInfluence;

    public ParticleCollision(ParticleCollisionBlockType blockType, ParticleCollisionAction action, ParticleRotationInfluence particleRotationInfluence) {
        this.blockType = blockType;
        this.action = action;
        this.particleRotationInfluence = particleRotationInfluence;
    }

    protected ParticleCollision() {
    }

    public ParticleCollisionBlockType getParticleMapCollision() {
        return this.blockType;
    }

    public ParticleCollisionAction getType() {
        return this.action;
    }

    public ParticleRotationInfluence getParticleRotationInfluence() {
        return this.particleRotationInfluence;
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.ParticleCollision toPacket() {
        com.hypixel.hytale.protocol.ParticleCollision packet = new com.hypixel.hytale.protocol.ParticleCollision();
        packet.blockType = this.blockType;
        packet.action = this.action;
        packet.particleRotationInfluence = this.particleRotationInfluence != null ? this.particleRotationInfluence : ParticleRotationInfluence.None;
        return packet;
    }

    @Nonnull
    public String toString() {
        return "ParticleCollision{blockType=" + String.valueOf((Object)this.blockType) + ", action=" + String.valueOf((Object)this.action) + ", particleRotationInfluence=" + String.valueOf((Object)this.particleRotationInfluence) + "}";
    }
}

