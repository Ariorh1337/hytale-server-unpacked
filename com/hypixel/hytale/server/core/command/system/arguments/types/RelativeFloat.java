/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.system.arguments.types;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.ParseResult;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RelativeFloat {
    @Nonnull
    public static final BuilderCodec<RelativeFloat> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(RelativeFloat.class, RelativeFloat::new).append(new KeyedCodec<Float>("Value", Codec.FLOAT), (o, i) -> {
        o.value = i.floatValue();
    }, RelativeFloat::getRawValue).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<Boolean>("Relative", Codec.BOOLEAN), (o, i) -> {
        o.isRelative = i;
    }, RelativeFloat::isRelative).addValidator(Validators.nonNull()).add()).build();
    private float value;
    private boolean isRelative;

    public RelativeFloat(float value, boolean isRelative) {
        this.value = value;
        this.isRelative = isRelative;
    }

    protected RelativeFloat() {
    }

    @Nullable
    public static RelativeFloat parse(@Nonnull String input, @Nonnull ParseResult parseResult) {
        boolean relative = input.contains("~");
        input = input.replaceAll(Pattern.quote("~"), "");
        try {
            float value = input.isBlank() ? 0.0f : Float.parseFloat(input);
            return new RelativeFloat(value, relative);
        }
        catch (Exception e) {
            parseResult.fail(Message.raw("Invalid float: " + input));
            return null;
        }
    }

    public float getRawValue() {
        return this.value;
    }

    public boolean isRelative() {
        return this.isRelative;
    }

    public float resolve(float baseValue) {
        return this.isRelative ? baseValue + this.value : this.value;
    }

    @Nonnull
    public String toString() {
        return (this.isRelative ? "~" : "") + this.value;
    }
}

