/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.vector.relative;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RelativeVector3i {
    @Nonnull
    public static final BuilderCodec<RelativeVector3i> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(RelativeVector3i.class, RelativeVector3i::new).append(new KeyedCodec<Vector3i>("Vector", Vector3i.CODEC), (o, i) -> {
        o.vector = i;
    }, RelativeVector3i::getVector).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<Boolean>("Relative", Codec.BOOLEAN), (o, i) -> {
        o.relative = i;
    }, RelativeVector3i::isRelative).addValidator(Validators.nonNull()).add()).build();
    private Vector3i vector;
    private boolean relative;

    public RelativeVector3i(@Nonnull Vector3i vector, boolean relative) {
        this.vector = vector;
        this.relative = relative;
    }

    protected RelativeVector3i() {
    }

    @Nonnull
    public Vector3i getVector() {
        return this.vector;
    }

    public boolean isRelative() {
        return this.relative;
    }

    @Nonnull
    public Vector3i resolve(@Nonnull Vector3i vector) {
        return this.relative ? vector.clone().add(vector) : vector.clone();
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RelativeVector3i that = (RelativeVector3i)o;
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
        return "RelativeVector3i{vector=" + String.valueOf(this.vector) + ", relative=" + this.relative + "}";
    }
}

