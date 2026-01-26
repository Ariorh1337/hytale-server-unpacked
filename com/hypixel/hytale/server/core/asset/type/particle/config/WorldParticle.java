/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.particle.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.protocol.Direction;
import com.hypixel.hytale.protocol.Vector3f;
import com.hypixel.hytale.server.core.asset.type.particle.config.ParticleSystem;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import javax.annotation.Nonnull;

public class WorldParticle
implements NetworkSerializable<com.hypixel.hytale.protocol.WorldParticle> {
    public static final String SYSTEM_ID_DOC = "The id of the particle system.";
    public static final String COLOR_DOC = "The colour used if none was specified in the particle settings.";
    public static final String SCALE_DOC = "The scale of the particle system.";
    public static final String POSITION_OFFSET_DOC = "The position offset from the spawn position.";
    public static final String ROTATION_OFFSET_DOC = "The rotation offset from the spawn rotation.";
    public static final BuilderCodec<WorldParticle> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(WorldParticle.class, WorldParticle::new).documentation("Particle System that can be spawned in the world.")).append(new KeyedCodec<String>("SystemId", Codec.STRING), (particle, s) -> {
        particle.systemId = s;
    }, particle -> particle.systemId).documentation("The id of the particle system.").addValidator(Validators.nonNull()).addValidator(ParticleSystem.VALIDATOR_CACHE.getValidator()).add()).append(new KeyedCodec<Color>("Color", ProtocolCodecs.COLOR), (particle, o) -> {
        particle.color = o;
    }, particle -> particle.color).documentation("The colour used if none was specified in the particle settings.").add()).append(new KeyedCodec<Float>("Scale", Codec.FLOAT), (particle, f) -> {
        particle.scale = f.floatValue();
    }, particle -> Float.valueOf(particle.scale)).documentation("The scale of the particle system.").add()).append(new KeyedCodec<Vector3f>("PositionOffset", ProtocolCodecs.VECTOR3F), (particle, s) -> {
        particle.positionOffset = s;
    }, particle -> particle.positionOffset).documentation("The position offset from the spawn position.").add()).append(new KeyedCodec<Direction>("RotationOffset", ProtocolCodecs.DIRECTION), (particle, s) -> {
        particle.rotationOffset = s;
    }, particle -> particle.rotationOffset).documentation("The rotation offset from the spawn rotation.").add()).build();
    public static final ArrayCodec<WorldParticle> ARRAY_CODEC = new ArrayCodec<WorldParticle>(CODEC, WorldParticle[]::new);
    protected String systemId;
    protected Color color;
    protected float scale = 1.0f;
    protected Vector3f positionOffset;
    protected Direction rotationOffset;

    public WorldParticle(String systemId, Color color, float scale, Vector3f positionOffset, Direction rotationOffset) {
        this.systemId = systemId;
        this.color = color;
        this.scale = scale;
        this.positionOffset = positionOffset;
        this.rotationOffset = rotationOffset;
    }

    protected WorldParticle() {
    }

    public String getSystemId() {
        return this.systemId;
    }

    public Color getColor() {
        return this.color;
    }

    public float getScale() {
        return this.scale;
    }

    public Vector3f getPositionOffset() {
        return this.positionOffset;
    }

    public Direction getRotationOffset() {
        return this.rotationOffset;
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.WorldParticle toPacket() {
        com.hypixel.hytale.protocol.WorldParticle packet = new com.hypixel.hytale.protocol.WorldParticle();
        packet.systemId = this.systemId;
        packet.color = this.color;
        packet.scale = this.scale;
        packet.positionOffset = this.positionOffset;
        packet.rotationOffset = this.rotationOffset;
        return packet;
    }

    @Nonnull
    public String toString() {
        return "WorldParticle{systemId='" + this.systemId + "', color=" + String.valueOf(this.color) + ", scale=" + this.scale + ", positionOffset=" + String.valueOf(this.positionOffset) + ", rotationOffset=" + String.valueOf(this.rotationOffset) + "}";
    }
}

