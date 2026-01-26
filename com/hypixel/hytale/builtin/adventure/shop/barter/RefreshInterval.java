/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.shop.barter;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class RefreshInterval {
    public static final BuilderCodec<RefreshInterval> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(RefreshInterval.class, RefreshInterval::new).append(new KeyedCodec<Integer>("Days", Codec.INTEGER), (interval, i) -> {
        interval.days = i;
    }, interval -> interval.days).addValidator(Validators.greaterThanOrEqual(1)).add()).build();
    protected int days = 1;

    public RefreshInterval(int days) {
        this.days = days;
    }

    protected RefreshInterval() {
    }

    public int getDays() {
        return this.days;
    }

    @Nonnull
    public String toString() {
        return "RefreshInterval{days=" + this.days + "}";
    }
}

