/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.vector.relative;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.vector.Vector2l;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RelativeVector2l {
    @Nonnull
    public static final BuilderCodec<RelativeVector2l> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(RelativeVector2l.class, RelativeVector2l::new).append(new KeyedCodec<Vector2l>("Vector", Vector2l.CODEC), (o, i) -> {
        o.vector = i;
    }, RelativeVector2l::getVector).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<Boolean>("Relative", Codec.BOOLEAN), (o, i) -> {
        o.relative = i;
    }, RelativeVector2l::isRelative).addValidator(Validators.nonNull()).add()).build();
    private Vector2l vector;
    private boolean relative;

    public RelativeVector2l(@Nonnull Vector2l vector, boolean relative) {
        this.vector = vector;
        this.relative = relative;
    }

    protected RelativeVector2l() {
    }

    @Nonnull
    public Vector2l getVector() {
        return this.vector;
    }

    public boolean isRelative() {
        return this.relative;
    }

    @Nonnull
    public Vector2l resolve(@Nonnull Vector2l vector) {
        return this.relative ? vector.clone().add(vector) : vector.clone();
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RelativeVector2l that = (RelativeVector2l)o;
        if (this.relative != that.relative) {
            return false;
        }
        return Objects.equals(this.vector, that.vector);
    }

    public int hashCode() {
        int result = this.vector != null ? this.vector.hashCode() : 0;
        result = 31 * result + (this.relative ? 1 : 0);
        return result;
    }

    @Nonnull
    public String toString() {
        return "RelativeVector2l{vector=" + String.valueOf(this.vector) + ", relative=" + this.relative + "}";
    }
}

