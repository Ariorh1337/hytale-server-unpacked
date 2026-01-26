/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.system.arguments.types;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.command.system.arguments.types.RelativeInteger;
import javax.annotation.Nonnull;

public class RelativeVector3i {
    @Nonnull
    public static final RelativeVector3i ZERO = new RelativeVector3i(new RelativeInteger(0, false), new RelativeInteger(0, false), new RelativeInteger(0, false));
    @Nonnull
    public static final BuilderCodec<RelativeVector3i> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(RelativeVector3i.class, RelativeVector3i::new).append(new KeyedCodec<RelativeInteger>("X", RelativeInteger.CODEC), (vec, val) -> {
        vec.x = val;
    }, vec -> vec.x).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<RelativeInteger>("Y", RelativeInteger.CODEC), (vec, val) -> {
        vec.y = val;
    }, vec -> vec.y).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<RelativeInteger>("Z", RelativeInteger.CODEC), (vec, val) -> {
        vec.z = val;
    }, vec -> vec.z).addValidator(Validators.nonNull()).add()).build();
    private RelativeInteger x;
    private RelativeInteger y;
    private RelativeInteger z;

    public RelativeVector3i(RelativeInteger x, RelativeInteger y, RelativeInteger z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    protected RelativeVector3i() {
    }

    @Nonnull
    public Vector3i resolve(int xBase, int yBase, int zBase) {
        return new Vector3i(this.x.resolve(xBase), this.y.resolve(yBase), this.z.resolve(zBase));
    }

    @Nonnull
    public Vector3i resolve(@Nonnull Vector3i base) {
        return this.resolve(base.x, base.y, base.z);
    }

    @Nonnull
    public String toString() {
        return "{" + String.valueOf(this.x) + ", " + String.valueOf(this.y) + ", " + String.valueOf(this.z) + "}";
    }

    public boolean isRelativeX() {
        return this.x.isRelative();
    }

    public boolean isRelativeY() {
        return this.y.isRelative();
    }

    public boolean isRelativeZ() {
        return this.z.isRelative();
    }
}

