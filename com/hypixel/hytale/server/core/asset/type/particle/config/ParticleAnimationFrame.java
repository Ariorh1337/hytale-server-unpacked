/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.particle.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.protocol.Range;
import com.hypixel.hytale.protocol.RangeVector2f;
import com.hypixel.hytale.protocol.RangeVector3f;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import javax.annotation.Nonnull;

public class ParticleAnimationFrame
implements NetworkSerializable<com.hypixel.hytale.protocol.ParticleAnimationFrame> {
    public static final int UNASSIGNED_OPACITY = -1;
    public static final BuilderCodec<ParticleAnimationFrame> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ParticleAnimationFrame.class, ParticleAnimationFrame::new).addField(new KeyedCodec<Range>("FrameIndex", ProtocolCodecs.RANGE), (animationFrame, s) -> {
        animationFrame.frameIndex = s;
    }, animationFrame -> animationFrame.frameIndex)).addField(new KeyedCodec<RangeVector2f>("Scale", ProtocolCodecs.RANGE_VECTOR2F), (animationFrame, o) -> {
        animationFrame.scale = o;
    }, animationFrame -> animationFrame.scale)).addField(new KeyedCodec<RangeVector3f>("Rotation", ProtocolCodecs.RANGE_VECTOR3F), (animationFrame, o) -> {
        animationFrame.rotation = o;
    }, animationFrame -> animationFrame.rotation)).addField(new KeyedCodec<Color>("Color", ProtocolCodecs.COLOR), (animationFrame, o) -> {
        animationFrame.color = o;
    }, animationFrame -> animationFrame.color)).append(new KeyedCodec<Float>("Opacity", Codec.FLOAT), (animationFrame, f) -> {
        animationFrame.opacity = f.floatValue();
    }, animationFrame -> Float.valueOf(animationFrame.opacity)).addValidator(Validators.or(Validators.range(Float.valueOf(0.0f), Float.valueOf(1.0f)), Validators.equal(Float.valueOf(-1.0f)))).add()).build();
    protected Range frameIndex;
    protected RangeVector2f scale;
    protected RangeVector3f rotation;
    protected Color color;
    protected float opacity = -1.0f;

    public ParticleAnimationFrame(Range frameIndex, RangeVector2f scale, RangeVector3f rotation, Color color, float opacity) {
        this.frameIndex = frameIndex;
        this.scale = scale;
        this.rotation = rotation;
        this.color = color;
        this.opacity = opacity;
    }

    protected ParticleAnimationFrame() {
    }

    public Range getFrameIndex() {
        return this.frameIndex;
    }

    public RangeVector2f getScale() {
        return this.scale;
    }

    public RangeVector3f getRotation() {
        return this.rotation;
    }

    public Color getColor() {
        return this.color;
    }

    public float getOpacity() {
        return this.opacity;
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.ParticleAnimationFrame toPacket() {
        com.hypixel.hytale.protocol.ParticleAnimationFrame packet = new com.hypixel.hytale.protocol.ParticleAnimationFrame();
        packet.frameIndex = this.frameIndex;
        packet.scale = this.scale;
        packet.rotation = this.rotation;
        packet.color = this.color;
        packet.opacity = this.opacity;
        return packet;
    }

    @Nonnull
    public String toString() {
        return "ParticleAnimationFrame{frameIndex=" + String.valueOf(this.frameIndex) + ", scale=" + String.valueOf(this.scale) + ", rotation=" + String.valueOf(this.rotation) + ", color=" + String.valueOf(this.color) + ", opacity=" + this.opacity + "}";
    }
}

