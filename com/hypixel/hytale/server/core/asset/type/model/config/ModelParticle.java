/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.model.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.protocol.Direction;
import com.hypixel.hytale.protocol.EntityPart;
import com.hypixel.hytale.protocol.Vector3f;
import com.hypixel.hytale.server.core.asset.type.particle.config.ParticleSystem;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import javax.annotation.Nonnull;

public class ModelParticle
implements NetworkSerializable<com.hypixel.hytale.protocol.ModelParticle> {
    public static final BuilderCodec<ModelParticle> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ModelParticle.class, ModelParticle::new).append(new KeyedCodec<String>("SystemId", Codec.STRING), (particle, s) -> {
        particle.systemId = s;
    }, particle -> particle.systemId).addValidator(Validators.nonNull()).addValidator(ParticleSystem.VALIDATOR_CACHE.getValidator()).add()).append(new KeyedCodec<EntityPart>("TargetEntityPart", new EnumCodec<EntityPart>(EntityPart.class)), (particle, o) -> {
        particle.targetEntityPart = o;
    }, particle -> particle.targetEntityPart).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<String>("TargetNodeName", Codec.STRING), (particle, s) -> {
        particle.targetNodeName = s;
    }, particle -> particle.targetNodeName).add()).append(new KeyedCodec<Color>("Color", ProtocolCodecs.COLOR), (particle, o) -> {
        particle.color = o;
    }, particle -> particle.color).add()).append(new KeyedCodec<Double>("Scale", Codec.DOUBLE), (particle, o) -> {
        particle.scale = o.floatValue();
    }, particle -> particle.scale).addValidator(Validators.greaterThan(0.0)).add()).append(new KeyedCodec<Vector3f>("PositionOffset", ProtocolCodecs.VECTOR3F), (particle, s) -> {
        particle.positionOffset = s;
    }, particle -> particle.positionOffset).add()).append(new KeyedCodec<Direction>("RotationOffset", ProtocolCodecs.DIRECTION), (particle, s) -> {
        particle.rotationOffset = s;
    }, particle -> particle.rotationOffset).add()).append(new KeyedCodec<Boolean>("DetachedFromModel", Codec.BOOLEAN), (modelParticle, aBoolean) -> {
        modelParticle.detachedFromModel = aBoolean;
    }, modelParticle -> modelParticle.detachedFromModel).documentation("To indicate if the spawned particle should be attached to the model and follow it, or spawn in world space.").add()).build();
    public static final ArrayCodec<ModelParticle> ARRAY_CODEC = new ArrayCodec<ModelParticle>(CODEC, ModelParticle[]::new);
    protected String systemId;
    @Nonnull
    protected EntityPart targetEntityPart = EntityPart.Self;
    protected String targetNodeName;
    protected Color color;
    protected float scale = 1.0f;
    protected Vector3f positionOffset;
    protected Direction rotationOffset;
    protected boolean detachedFromModel;

    public ModelParticle(String systemId, EntityPart targetEntityPart, String targetNodeName, Color color, float scale, Vector3f positionOffset, Direction rotationOffset, boolean detachedFromModel) {
        this.systemId = systemId;
        this.targetEntityPart = targetEntityPart;
        this.targetNodeName = targetNodeName;
        this.color = color;
        this.scale = scale;
        this.positionOffset = positionOffset;
        this.rotationOffset = rotationOffset;
        this.detachedFromModel = detachedFromModel;
    }

    public ModelParticle(ModelParticle other) {
        this.systemId = other.systemId;
        this.targetEntityPart = other.targetEntityPart;
        this.targetNodeName = other.targetNodeName;
        this.color = other.color;
        this.scale = other.scale;
        this.positionOffset = other.positionOffset;
        this.rotationOffset = other.rotationOffset;
        this.detachedFromModel = other.detachedFromModel;
    }

    protected ModelParticle() {
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.ModelParticle toPacket() {
        com.hypixel.hytale.protocol.ModelParticle packet = new com.hypixel.hytale.protocol.ModelParticle();
        packet.systemId = this.systemId;
        packet.targetEntityPart = this.targetEntityPart;
        packet.targetNodeName = this.targetNodeName;
        packet.color = this.color;
        packet.scale = this.scale;
        packet.positionOffset = this.positionOffset;
        packet.rotationOffset = this.rotationOffset;
        packet.detachedFromModel = this.detachedFromModel;
        return packet;
    }

    public String getSystemId() {
        return this.systemId;
    }

    public EntityPart getTargetEntityPart() {
        return this.targetEntityPart;
    }

    public String getTargetNodeName() {
        return this.targetNodeName;
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

    public boolean isDetachedFromModel() {
        return this.detachedFromModel;
    }

    public ModelParticle scale(float scale) {
        this.scale *= scale;
        if (this.positionOffset != null) {
            this.positionOffset.x *= scale;
            this.positionOffset.y *= scale;
            this.positionOffset.z *= scale;
        }
        return this;
    }

    @Nonnull
    public String toString() {
        return "ModelParticle{systemId='" + this.systemId + "', targetEntityPart=" + String.valueOf((Object)this.targetEntityPart) + ", targetNodeName='" + this.targetNodeName + "', color=" + String.valueOf(this.color) + ", scale=" + this.scale + ", positionOffset=" + String.valueOf(this.positionOffset) + ", rotationOffset=" + String.valueOf(this.rotationOffset) + ", detachedFromModel=" + this.detachedFromModel + "}";
    }

    public ModelParticle clone() {
        return new ModelParticle(this);
    }
}

