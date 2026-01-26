/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.buildertool.config.args;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.protocol.packets.buildertools.BrushShape;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolArg;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolArgType;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolBrushShapeArg;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.args.ToolArg;
import javax.annotation.Nonnull;

public class BrushShapeArg
extends ToolArg<BrushShape> {
    public static final EnumCodec<BrushShape> BRUSH_SHAPE_CODEC = new EnumCodec<BrushShape>(BrushShape.class);
    public static final BuilderCodec<BrushShapeArg> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(BrushShapeArg.class, BrushShapeArg::new, ToolArg.DEFAULT_CODEC).addField(new KeyedCodec<BrushShape>("Default", BRUSH_SHAPE_CODEC), (shapeArg, o) -> {
        shapeArg.value = o;
    }, shapeArg -> (BrushShape)((Object)((Object)shapeArg.value)))).build();

    public BrushShapeArg() {
    }

    public BrushShapeArg(BrushShape value) {
        this.value = value;
    }

    @Override
    @Nonnull
    public Codec<BrushShape> getCodec() {
        return BRUSH_SHAPE_CODEC;
    }

    @Override
    @Nonnull
    public BrushShape fromString(@Nonnull String str) {
        return BrushShape.valueOf(str);
    }

    @Nonnull
    public BuilderToolBrushShapeArg toBrushShapeArgPacket() {
        return new BuilderToolBrushShapeArg((BrushShape)((Object)this.value));
    }

    @Override
    protected void setupPacket(@Nonnull BuilderToolArg packet) {
        packet.argType = BuilderToolArgType.BrushShape;
        packet.brushShapeArg = this.toBrushShapeArgPacket();
    }

    @Override
    @Nonnull
    public String toString() {
        return "BrushShapeArg{} " + super.toString();
    }
}

