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

public class RelativeInteger {
    @Nonnull
    public static final BuilderCodec<RelativeInteger> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(RelativeInteger.class, RelativeInteger::new).append(new KeyedCodec<Integer>("Value", Codec.INTEGER), (o, i) -> {
        o.value = i;
    }, RelativeInteger::getRawValue).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<Boolean>("Relative", Codec.BOOLEAN), (o, i) -> {
        o.isRelative = i;
    }, RelativeInteger::isRelative).addValidator(Validators.nonNull()).add()).build();
    private int value;
    private boolean isRelative;

    public RelativeInteger(int value, boolean isRelative) {
        this.value = value;
        this.isRelative = isRelative;
    }

    protected RelativeInteger() {
    }

    @Nullable
    public static RelativeInteger parse(@Nonnull String input, @Nonnull ParseResult parseResult) {
        boolean relative = input.contains("~");
        input = input.replaceAll(Pattern.quote("~"), "");
        try {
            int value = input.isBlank() ? 0 : Integer.parseInt(input);
            return new RelativeInteger(value, relative);
        }
        catch (Exception e) {
            parseResult.fail(Message.raw("Invalid integer: " + input));
            return null;
        }
    }

    public int getRawValue() {
        return this.value;
    }

    public boolean isRelative() {
        return this.isRelative;
    }

    public int resolve(int baseValue) {
        return this.isRelative ? baseValue + this.value : this.value;
    }

    @Nonnull
    public String toString() {
        return (this.isRelative ? "~" : "") + this.value;
    }
}

