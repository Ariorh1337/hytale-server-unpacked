/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.particle.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.map.Int2ObjectMapCodec;
import com.hypixel.hytale.codec.schema.metadata.ui.UIDefaultCollapsedState;
import com.hypixel.hytale.codec.schema.metadata.ui.UIEditorSectionStart;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.codec.validation.validator.MapKeyValidator;
import com.hypixel.hytale.protocol.ParticleScaleRatioConstraint;
import com.hypixel.hytale.protocol.ParticleUVOption;
import com.hypixel.hytale.protocol.Size;
import com.hypixel.hytale.protocol.SoftParticle;
import com.hypixel.hytale.server.core.asset.common.CommonAssetValidator;
import com.hypixel.hytale.server.core.asset.type.particle.config.ParticleAnimationFrame;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import javax.annotation.Nonnull;

public class Particle
implements NetworkSerializable<com.hypixel.hytale.protocol.Particle> {
    public static final BuilderCodec<Particle> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(Particle.class, Particle::new).append(new KeyedCodec<String>("Texture", Codec.STRING), (particle, s) -> {
        particle.texture = s;
    }, particle -> particle.texture).addValidator(Validators.nonNull()).addValidator(CommonAssetValidator.TEXTURE_PARTICLES).metadata(new UIEditorSectionStart("Material")).add()).addField(new KeyedCodec<Size>("FrameSize", ProtocolCodecs.SIZE), (particle, o) -> {
        particle.frameSize = o;
    }, particle -> particle.frameSize)).append(new KeyedCodec<SoftParticle>("SoftParticles", new EnumCodec<SoftParticle>(SoftParticle.class)), (particle, o) -> {
        particle.softParticle = o;
    }, particle -> particle.softParticle).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<Float>("SoftParticlesFadeFactor", Codec.FLOAT), (particle, f) -> {
        particle.softParticlesFadeFactor = f.floatValue();
    }, particle -> Float.valueOf(particle.softParticlesFadeFactor)).addValidator(Validators.range(Float.valueOf(0.1f), Float.valueOf(2.0f))).add()).appendInherited(new KeyedCodec<Boolean>("UseSpriteBlending", Codec.BOOLEAN), (particle, s) -> {
        particle.useSpriteBlending = s;
    }, particle -> particle.useSpriteBlending, (particle, parent) -> {
        particle.useSpriteBlending = parent.useSpriteBlending;
    }).add()).append(new KeyedCodec<ParticleAnimationFrame>("Animation", new Int2ObjectMapCodec<ParticleAnimationFrame>(ParticleAnimationFrame.CODEC, Int2ObjectOpenHashMap::new)), (particle, o) -> {
        particle.animation = o;
    }, particle -> particle.animation).addValidator(Validators.nonNull()).addValidator(Validators.nonEmptyMap()).addValidator(new MapKeyValidator<Integer>(Validators.range(0, 100))).metadata(new UIEditorSectionStart("Animation")).metadata(UIDefaultCollapsedState.UNCOLLAPSED).add()).append(new KeyedCodec<ParticleAnimationFrame>("CollisionAnimationFrame", ParticleAnimationFrame.CODEC), (particle, o) -> {
        particle.collisionAnimationFrame = o;
    }, particle -> particle.collisionAnimationFrame).add()).append(new KeyedCodec<ParticleUVOption>("UVOption", new EnumCodec<ParticleUVOption>(ParticleUVOption.class)), (particle, o) -> {
        particle.uvOption = o;
    }, particle -> particle.uvOption).addValidator(Validators.nonNull()).metadata(new UIEditorSectionStart("Initial Frame")).add()).append(new KeyedCodec<ParticleScaleRatioConstraint>("ScaleRatioConstraint", new EnumCodec<ParticleScaleRatioConstraint>(ParticleScaleRatioConstraint.class)), (particle, o) -> {
        particle.scaleRatioConstraint = o;
    }, particle -> particle.scaleRatioConstraint).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<ParticleAnimationFrame>("InitialAnimationFrame", ParticleAnimationFrame.CODEC), (particle, o) -> {
        particle.initialAnimationFrame = o;
    }, particle -> particle.initialAnimationFrame).metadata(UIDefaultCollapsedState.UNCOLLAPSED).add()).build();
    protected String texture;
    protected Size frameSize;
    @Nonnull
    protected ParticleUVOption uvOption = ParticleUVOption.None;
    @Nonnull
    protected ParticleScaleRatioConstraint scaleRatioConstraint = ParticleScaleRatioConstraint.OneToOne;
    @Nonnull
    protected SoftParticle softParticle = SoftParticle.Enable;
    protected float softParticlesFadeFactor = 1.0f;
    protected boolean useSpriteBlending;
    protected ParticleAnimationFrame initialAnimationFrame;
    protected ParticleAnimationFrame collisionAnimationFrame;
    protected Int2ObjectMap<ParticleAnimationFrame> animation;

    public Particle(String texture, Size frameSize, ParticleUVOption uvOption, ParticleScaleRatioConstraint scaleRatioConstraint, SoftParticle softParticle, float softParticlesFadeFactor, boolean useSpriteBlending, ParticleAnimationFrame initialAnimationFrame, ParticleAnimationFrame collisionAnimationFrame, Int2ObjectMap<ParticleAnimationFrame> animation) {
        this.texture = texture;
        this.frameSize = frameSize;
        this.uvOption = uvOption;
        this.scaleRatioConstraint = scaleRatioConstraint;
        this.softParticle = softParticle;
        this.softParticlesFadeFactor = softParticlesFadeFactor;
        this.useSpriteBlending = useSpriteBlending;
        this.initialAnimationFrame = initialAnimationFrame;
        this.collisionAnimationFrame = collisionAnimationFrame;
        this.animation = animation;
    }

    protected Particle() {
    }

    public String getTexture() {
        return this.texture;
    }

    public Size getFrameSize() {
        return this.frameSize;
    }

    public ParticleUVOption getUvOption() {
        return this.uvOption;
    }

    public ParticleScaleRatioConstraint getScaleRatioConstraint() {
        return this.scaleRatioConstraint;
    }

    public SoftParticle getSoftParticle() {
        return this.softParticle;
    }

    public float getSoftParticlesFadeFactor() {
        return this.softParticlesFadeFactor;
    }

    public boolean isUseSpriteBlending() {
        return this.useSpriteBlending;
    }

    public ParticleAnimationFrame getInitialAnimationFrame() {
        return this.initialAnimationFrame;
    }

    public ParticleAnimationFrame getCollisionAnimationFrame() {
        return this.collisionAnimationFrame;
    }

    public Int2ObjectMap<ParticleAnimationFrame> getAnimation() {
        return this.animation;
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.Particle toPacket() {
        com.hypixel.hytale.protocol.Particle packet = new com.hypixel.hytale.protocol.Particle();
        packet.texturePath = this.texture;
        packet.frameSize = this.frameSize;
        packet.uvOption = this.uvOption;
        packet.scaleRatioConstraint = this.scaleRatioConstraint;
        packet.softParticles = this.softParticle;
        packet.softParticlesFadeFactor = this.softParticlesFadeFactor;
        packet.useSpriteBlending = this.useSpriteBlending;
        if (this.initialAnimationFrame != null) {
            packet.initialAnimationFrame = this.initialAnimationFrame.toPacket();
        }
        if (this.collisionAnimationFrame != null) {
            packet.collisionAnimationFrame = this.collisionAnimationFrame.toPacket();
        }
        if (this.animation != null) {
            packet.animationFrames = new Int2ObjectOpenHashMap<com.hypixel.hytale.protocol.ParticleAnimationFrame>();
            for (Int2ObjectMap.Entry entry : this.animation.int2ObjectEntrySet()) {
                packet.animationFrames.put(entry.getIntKey(), ((ParticleAnimationFrame)entry.getValue()).toPacket());
            }
        }
        return packet;
    }

    @Nonnull
    public String toString() {
        return "Particle{texture='" + this.texture + "', frameSize=" + String.valueOf(this.frameSize) + ", uvOption=" + String.valueOf((Object)this.uvOption) + ", scaleRatioConstraint=" + String.valueOf((Object)this.scaleRatioConstraint) + ", softParticle=" + String.valueOf((Object)this.softParticle) + ", softParticlesFadeFactor=" + this.softParticlesFadeFactor + ", useSpriteBlending=" + this.useSpriteBlending + ", initialAnimationFrame=" + String.valueOf(this.initialAnimationFrame) + ", collisionAnimationFrame=" + String.valueOf(this.collisionAnimationFrame) + ", animation=" + String.valueOf(this.animation) + "}";
    }
}

