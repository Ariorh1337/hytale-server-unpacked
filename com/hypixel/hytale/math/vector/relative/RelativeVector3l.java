/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.vector.relative;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.vector.Vector3l;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RelativeVector3l {
    @Nonnull
    public static final BuilderCodec<RelativeVector3l> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(RelativeVector3l.class, RelativeVector3l::new).append(new KeyedCodec<Vector3l>("Vector", Vector3l.CODEC), (o, i) -> {
        o.vector = i;
    }, RelativeVector3l::getVector).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<Boolean>("Relative", Codec.BOOLEAN), (o, i) -> {
        o.relative = i;
    }, RelativeVector3l::isRelative).addValidator(Validators.nonNull()).add()).build();
    private Vector3l vector;
    private boolean relative;

    public RelativeVector3l(@Nonnull Vector3l vector, boolean relative) {
        this.vector = vector;
        this.relative = relative;
    }

    protected RelativeVector3l() {
    }

    @Nonnull
    public Vector3l getVector() {
        return this.vector;
    }

    public boolean isRelative() {
        return this.relative;
    }

    @Nonnull
    public Vector3l resolve(@Nonnull Vector3l vector) {
        return this.relative ? vector.clone().add(vector) : vector.clone();
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RelativeVector3l that = (RelativeVector3l)o;
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
        return "RelativeVector3l{vector=" + String.valueOf(this.vector) + ", relative=" + this.relative + "}";
    }
}

