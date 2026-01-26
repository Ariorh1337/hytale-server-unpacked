/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.fluidfx.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.server.core.asset.type.particle.config.ParticleSystem;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import java.lang.ref.SoftReference;
import javax.annotation.Nonnull;

public class FluidParticle
implements NetworkSerializable<com.hypixel.hytale.protocol.FluidParticle> {
    public static final BuilderCodec<FluidParticle> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(FluidParticle.class, FluidParticle::new).documentation("Particle System that can be spawned in relation to a fluid.")).append(new KeyedCodec<String>("SystemId", Codec.STRING), (particle, s) -> {
        particle.systemId = s;
    }, particle -> particle.systemId).documentation("The id of the particle system.").addValidator(Validators.nonNull()).addValidator(ParticleSystem.VALIDATOR_CACHE.getValidator()).add()).append(new KeyedCodec<Color>("Color", ProtocolCodecs.COLOR), (particle, o) -> {
        particle.color = o;
    }, particle -> particle.color).documentation("The colour used if none was specified in the particle settings.").add()).append(new KeyedCodec<Float>("Scale", Codec.FLOAT), (particle, f) -> {
        particle.scale = f.floatValue();
    }, particle -> Float.valueOf(particle.scale)).documentation("The scale of the particle system.").add()).build();
    protected String systemId;
    protected Color color;
    protected float scale = 1.0f;
    private SoftReference<com.hypixel.hytale.protocol.FluidParticle> cachedPacket;

    public FluidParticle(String systemId, Color color, float scale) {
        this.systemId = systemId;
        this.color = color;
        this.scale = scale;
    }

    protected FluidParticle() {
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

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.FluidParticle toPacket() {
        com.hypixel.hytale.protocol.FluidParticle cached;
        com.hypixel.hytale.protocol.FluidParticle fluidParticle = cached = this.cachedPacket == null ? null : this.cachedPacket.get();
        if (cached != null) {
            return cached;
        }
        com.hypixel.hytale.protocol.FluidParticle packet = new com.hypixel.hytale.protocol.FluidParticle();
        packet.systemId = this.systemId;
        packet.color = this.color;
        packet.scale = this.scale;
        this.cachedPacket = new SoftReference<com.hypixel.hytale.protocol.FluidParticle>(packet);
        return packet;
    }

    @Nonnull
    public String toString() {
        return "FluidParticle{systemId='" + this.systemId + "', color=" + String.valueOf(this.color) + ", scale=" + this.scale + "}";
    }
}

