/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.trail.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.math.vector.Vector2i;
import com.hypixel.hytale.protocol.Range;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import javax.annotation.Nonnull;

public class Animation {
    public static final BuilderCodec<Animation> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(Animation.class, Animation::new).appendInherited(new KeyedCodec<Vector2i>("FrameSize", Vector2i.CODEC), (animation, b) -> {
        animation.frameSize = b;
    }, animation -> animation.frameSize, (animation, parent) -> {
        animation.frameSize = parent.frameSize;
    }).add()).appendInherited(new KeyedCodec<Range>("FrameRange", ProtocolCodecs.RANGE), (animation, b) -> {
        animation.frameRange = b;
    }, animation -> animation.frameRange, (animation, parent) -> {
        animation.frameRange = parent.frameRange;
    }).add()).appendInherited(new KeyedCodec<Integer>("FrameLifeSpan", Codec.INTEGER), (animation, i) -> {
        animation.frameLifeSpan = i;
    }, animation -> animation.frameLifeSpan, (animation, parent) -> {
        animation.frameLifeSpan = parent.frameLifeSpan;
    }).add()).build();
    private Vector2i frameSize;
    private Range frameRange;
    private int frameLifeSpan;

    public Vector2i getFrameSize() {
        return this.frameSize;
    }

    public Range getFrameRange() {
        return this.frameRange;
    }

    public int getFrameLifeSpan() {
        return this.frameLifeSpan;
    }

    @Nonnull
    public String toString() {
        return "Animation{frameSize=" + String.valueOf(this.frameSize) + ", frameRange=" + String.valueOf(this.frameRange) + ", frameLifeSpan=" + this.frameLifeSpan + "}";
    }
}

