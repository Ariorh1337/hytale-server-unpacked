/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.system.arguments.types;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.server.core.command.system.arguments.types.RelativeInteger;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nonnull;

public class RelativeIntegerRange {
    @Nonnull
    public static final BuilderCodec<RelativeIntegerRange> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(RelativeIntegerRange.class, RelativeIntegerRange::new).append(new KeyedCodec<RelativeInteger>("Min", RelativeInteger.CODEC), (o, i) -> {
        o.min = i;
    }, o -> o.min).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<RelativeInteger>("Max", RelativeInteger.CODEC), (o, i) -> {
        o.max = i;
    }, o -> o.max).addValidator(Validators.nonNull()).add()).build();
    private RelativeInteger min;
    private RelativeInteger max;

    public RelativeIntegerRange(@Nonnull RelativeInteger min, @Nonnull RelativeInteger max) {
        this.min = min;
        this.max = max;
    }

    protected RelativeIntegerRange() {
    }

    public RelativeIntegerRange(int min, int max) {
        this.min = new RelativeInteger(min, false);
        this.max = new RelativeInteger(max, false);
    }

    public int getNumberInRange(int base) {
        if (this.min.getRawValue() == this.max.getRawValue()) {
            return this.min.resolve(base);
        }
        return ThreadLocalRandom.current().nextInt(this.min.resolve(base), this.max.resolve(base) + 1);
    }

    @Nonnull
    public String toString() {
        return "{ Minimum: " + String.valueOf(this.min) + ", Maximum: " + String.valueOf(this.max) + " }";
    }
}

