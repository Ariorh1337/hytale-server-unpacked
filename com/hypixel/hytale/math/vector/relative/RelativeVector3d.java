/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.vector.relative;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.vector.Vector3d;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RelativeVector3d {
    @Nonnull
    public static final BuilderCodec<RelativeVector3d> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(RelativeVector3d.class, RelativeVector3d::new).append(new KeyedCodec<Vector3d>("Vector", Vector3d.CODEC), (o, i) -> {
        o.vector = i;
    }, RelativeVector3d::getVector).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<Boolean>("Relative", Codec.BOOLEAN), (o, i) -> {
        o.relative = i;
    }, RelativeVector3d::isRelative).addValidator(Validators.nonNull()).add()).build();
    private Vector3d vector;
    private boolean relative;

    public RelativeVector3d(@Nonnull Vector3d vector, boolean relative) {
        this.vector = vector;
        this.relative = relative;
    }

    protected RelativeVector3d() {
    }

    @Nonnull
    public Vector3d getVector() {
        return this.vector;
    }

    public boolean isRelative() {
        return this.relative;
    }

    @Nonnull
    public Vector3d resolve(@Nonnull Vector3d vector) {
        return this.relative ? vector.clone().add(vector) : vector.clone();
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RelativeVector3d that = (RelativeVector3d)o;
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
        return "RelativeVector3d{vector=" + String.valueOf(this.vector) + ", relative=" + this.relative + "}";
    }
}

