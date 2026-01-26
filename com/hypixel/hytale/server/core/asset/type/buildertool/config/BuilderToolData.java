/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.buildertool.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.protocol.ItemBuilderToolData;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolState;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.BuilderTool;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class BuilderToolData
implements NetworkSerializable<ItemBuilderToolData> {
    public static final BuilderToolData DEFAULT = new BuilderToolData();
    public static final BuilderCodec<BuilderToolData> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(BuilderToolData.class, BuilderToolData::new).addField(new KeyedCodec<T[]>("UI", new ArrayCodec<String>(Codec.STRING, String[]::new)), (builderToolData, o) -> {
        builderToolData.ui = o;
    }, builderToolData -> builderToolData.ui)).append(new KeyedCodec<T[]>("Tools", new ArrayCodec(BuilderTool.CODEC, BuilderTool[]::new)), (builderToolData, o) -> {
        builderToolData.tools = o;
    }, builderToolData -> builderToolData.tools).addValidator(Validators.nonEmptyArray()).add()).build();
    protected String[] ui;
    protected BuilderTool[] tools;

    public BuilderToolData() {
    }

    public BuilderToolData(String[] ui, BuilderTool[] tools) {
        this.ui = ui;
        this.tools = tools;
    }

    public String[] getUi() {
        return this.ui;
    }

    public BuilderTool[] getTools() {
        return this.tools;
    }

    @Override
    @Nonnull
    public ItemBuilderToolData toPacket() {
        ItemBuilderToolData packet = new ItemBuilderToolData();
        packet.ui = this.ui;
        packet.tools = new BuilderToolState[this.tools.length];
        for (int i = 0; i < this.tools.length; ++i) {
            packet.tools[i] = this.tools[i].toPacket();
        }
        return packet;
    }

    @Nonnull
    public String toString() {
        return "BuilderToolData{ui=" + Arrays.toString(this.ui) + ", tools=" + Arrays.toString(this.tools) + "}";
    }
}

