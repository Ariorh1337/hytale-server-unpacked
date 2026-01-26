/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.buildertool.config.args;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolArg;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolArgType;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolOptionArg;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.args.ToolArg;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class OptionArg
extends ToolArg<String> {
    public static final BuilderCodec<OptionArg> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(OptionArg.class, OptionArg::new, ToolArg.DEFAULT_CODEC).addField(new KeyedCodec<String>("Default", Codec.STRING), (optionArg, o) -> {
        optionArg.value = o;
    }, optionArg -> (String)optionArg.value)).addField(new KeyedCodec<T[]>("Options", Codec.STRING_ARRAY), (optionArg, o) -> {
        optionArg.options = o;
    }, optionArg -> optionArg.options)).build();
    protected String[] options;

    public OptionArg() {
    }

    public OptionArg(String value, String[] options) {
        this.value = value;
        this.options = options;
    }

    @Override
    @Nonnull
    public Codec<String> getCodec() {
        return Codec.STRING;
    }

    @Override
    @Nonnull
    public String fromString(@Nonnull String str) {
        for (String option : this.options) {
            if (!str.equalsIgnoreCase(option)) continue;
            return option;
        }
        try {
            int index = Integer.parseInt(str);
            if (index >= 0 && index < this.options.length) {
                return this.options[index];
            }
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
        throw new IllegalArgumentException();
    }

    @Nonnull
    public BuilderToolOptionArg toOptionArgPacket() {
        return new BuilderToolOptionArg((String)this.value, this.options);
    }

    @Override
    protected void setupPacket(@Nonnull BuilderToolArg packet) {
        packet.argType = BuilderToolArgType.Option;
        packet.optionArg = this.toOptionArgPacket();
    }

    @Override
    @Nonnull
    public String toString() {
        return "OptionArg{options=" + Arrays.toString(this.options) + "} " + super.toString();
    }
}

