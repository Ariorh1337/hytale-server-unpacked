/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.buildertool.config.args;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolArg;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.args.ToolArgException;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import javax.annotation.Nonnull;

public abstract class ToolArg<T>
implements NetworkSerializable<BuilderToolArg> {
    public static final CodecMapCodec<ToolArg> CODEC = new CodecMapCodec("Type");
    public static final BuilderCodec<ToolArg> DEFAULT_CODEC = ((BuilderCodec.Builder)BuilderCodec.abstractBuilder(ToolArg.class).addField(new KeyedCodec<Boolean>("Required", Codec.BOOLEAN), (shapeArg, o) -> {
        shapeArg.required = o;
    }, shapeArg -> shapeArg.required)).build();
    protected boolean required = true;
    protected T value;

    public T getValue() {
        return this.value;
    }

    public boolean isRequired() {
        return this.required;
    }

    public abstract Codec<T> getCodec();

    @Nonnull
    public abstract T fromString(@Nonnull String var1) throws ToolArgException;

    protected abstract void setupPacket(BuilderToolArg var1);

    @Override
    @Nonnull
    public BuilderToolArg toPacket() {
        BuilderToolArg packet = new BuilderToolArg();
        packet.required = this.required;
        this.setupPacket(packet);
        return packet;
    }

    @Nonnull
    public String toString() {
        return "ToolArg{required=" + this.required + ", value=" + String.valueOf(this.value) + "}";
    }
}

