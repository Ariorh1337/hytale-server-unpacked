/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.buildertool.config.args;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.protocol.Rotation;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolArg;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolArgType;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolRotationArg;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.args.ToolArg;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.args.ToolArgException;
import javax.annotation.Nonnull;

public class BrushRotationArg
extends ToolArg<Rotation> {
    public static final Codec<Rotation> ROTATION_CODEC = new EnumCodec<Rotation>(Rotation.class);
    public static final BuilderCodec<BrushRotationArg> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(BrushRotationArg.class, BrushRotationArg::new, ToolArg.DEFAULT_CODEC).append(new KeyedCodec<Rotation>("Default", ROTATION_CODEC), (arg, o) -> {
        arg.value = o;
    }, arg -> (Rotation)((Object)((Object)arg.value))).documentation("Represents the amount of rotation to be applied to a brush shape").addValidator(Validators.nonNull()).add()).build();

    public BrushRotationArg() {
    }

    public BrushRotationArg(Rotation value) {
        this.value = value;
    }

    @Override
    @Nonnull
    public Codec<Rotation> getCodec() {
        return ROTATION_CODEC;
    }

    @Override
    @Nonnull
    public Rotation fromString(@Nonnull String str) throws ToolArgException {
        return Rotation.valueOf(str);
    }

    @Nonnull
    public BuilderToolRotationArg toRotationArgPacket() {
        return new BuilderToolRotationArg((Rotation)((Object)this.value));
    }

    @Override
    protected void setupPacket(@Nonnull BuilderToolArg packet) {
        packet.argType = BuilderToolArgType.Rotation;
        packet.rotationArg = this.toRotationArgPacket();
    }

    @Override
    @Nonnull
    public String toString() {
        return "BrushRotationArg{} " + super.toString();
    }
}

