/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.vector;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.schema.metadata.ui.UIDisplayMode;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.util.HashUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Vector3l {
    @Nonnull
    public static final BuilderCodec<Vector3l> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(Vector3l.class, Vector3l::new).metadata(UIDisplayMode.COMPACT)).appendInherited(new KeyedCodec<Long>("X", Codec.LONG), (o, i) -> {
        o.x = i;
    }, o -> o.x, (o, p) -> {
        o.x = p.x;
    }).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<Long>("Y", Codec.LONG), (o, i) -> {
        o.y = i;
    }, o -> o.y, (o, p) -> {
        o.y = p.y;
    }).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<Long>("Z", Codec.LONG), (o, i) -> {
        o.z = i;
    }, o -> o.z, (o, p) -> {
        o.z = p.z;
    }).addValidator(Validators.nonNull()).add()).build();
    public static final Vector3l ZERO = new Vector3l(0L, 0L, 0L);
    public static final Vector3l UP;
    public static final Vector3l POS_Y;
    public static final Vector3l DOWN;
    public static final Vector3l NEG_Y;
    public static final Vector3l FORWARD;
    public static final Vector3l NEG_Z;
    public static final Vector3l NORTH;
    public static final Vector3l BACKWARD;
    public static final Vector3l POS_Z;
    public static final Vector3l SOUTH;
    public static final Vector3l RIGHT;
    public static final Vector3l POS_X;
    public static final Vector3l EAST;
    public static final Vector3l LEFT;
    public static final Vector3l NEG_X;
    public static final Vector3l WEST;
    public static final Vector3l ALL_ONES;
    public static final Vector3l MIN;
    public static final Vector3l MAX;
    public static final Vector3l[] BLOCK_SIDES;
    public static final Vector3l[] BLOCK_EDGES;
    public static final Vector3l[] BLOCK_CORNERS;
    public static final Vector3l[][] BLOCK_PARTS;
    public static final Vector3l[] CARDINAL_DIRECTIONS;
    public long x;
    public long y;
    public long z;
    private transient int hash;

    public Vector3l() {
        this(0L, 0L, 0L);
    }

    public Vector3l(@Nonnull Vector3l v) {
        this(v.x, v.y, v.z);
    }

    public Vector3l(long x, long y, long z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.hash = 0;
    }

    public long getX() {
        return this.x;
    }

    public void setX(long x) {
        this.x = x;
        this.hash = 0;
    }

    public long getY() {
        return this.y;
    }

    public void setY(long y) {
        this.y = y;
        this.hash = 0;
    }

    public long getZ() {
        return this.z;
    }

    public void setZ(long z) {
        this.z = z;
        this.hash = 0;
    }

    @Nonnull
    public Vector3l assign(@Nonnull Vector3l v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.hash = v.hash;
        return this;
    }

    @Nonnull
    public Vector3l assign(long v) {
        this.x = v;
        this.y = v;
        this.z = v;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3l assign(@Nonnull long[] v) {
        this.x = v[0];
        this.y = v[1];
        this.z = v[2];
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3l assign(long x, long y, long z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3l add(@Nonnull Vector3l v) {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3l add(long x, long y, long z) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3l addScaled(@Nonnull Vector3l v, long s) {
        this.x += v.x * s;
        this.y += v.y * s;
        this.z += v.z * s;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3l subtract(@Nonnull Vector3l v) {
        this.x -= v.x;
        this.y -= v.y;
        this.z -= v.z;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3l subtract(long x, long y, long z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3l negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3l scale(long s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3l scale(double s) {
        this.x = (long)((double)this.x * s);
        this.y = (long)((double)this.y * s);
        this.z = (long)((double)this.z * s);
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3l scale(@Nonnull Vector3l p) {
        this.x *= p.x;
        this.y *= p.y;
        this.z *= p.z;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3l cross(@Nonnull Vector3l v) {
        long x0 = this.y * v.z - this.z * v.y;
        long y0 = this.z * v.x - this.x * v.z;
        long z0 = this.x * v.y - this.y * v.x;
        return new Vector3l(x0, y0, z0);
    }

    @Nonnull
    public Vector3l cross(@Nonnull Vector3l v, @Nonnull Vector3l res) {
        res.assign(this.y * v.z - this.z * v.y, this.z * v.x - this.x * v.z, this.x * v.y - this.y * v.x);
        return this;
    }

    public long dot(@Nonnull Vector3l other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public double distanceTo(@Nonnull Vector3l v) {
        return Math.sqrt(this.distanceSquaredTo(v));
    }

    public double distanceTo(long x, long y, long z) {
        return Math.sqrt(this.distanceSquaredTo(x, y, z));
    }

    public long distanceSquaredTo(@Nonnull Vector3l v) {
        long x0 = v.x - this.x;
        long y0 = v.y - this.y;
        long z0 = v.z - this.z;
        return x0 * x0 + y0 * y0 + z0 * z0;
    }

    public long distanceSquaredTo(long x, long y, long z) {
        long dx = x - this.x;
        long dy = y - this.y;
        long dz = z - this.z;
        return dx * dx + dy * dy + dz * dz;
    }

    @Nonnull
    public Vector3l normalize() {
        return this.setLength(1L);
    }

    public double length() {
        return Math.sqrt(this.squaredLength());
    }

    public long squaredLength() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    @Nonnull
    public Vector3l setLength(long newLen) {
        return this.scale((double)newLen / this.length());
    }

    @Nonnull
    public Vector3l clampLength(long maxLength) {
        double length = this.length();
        if ((double)maxLength > length) {
            return this;
        }
        return this.scale((double)maxLength / length);
    }

    @Nonnull
    public Vector3l dropHash() {
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3l clone() {
        return new Vector3l(this.x, this.y, this.z);
    }

    @Nonnull
    public Vector3i toVector3i() {
        return new Vector3i(MathUtil.floor(this.x), MathUtil.floor(this.y), MathUtil.floor(this.z));
    }

    @Nonnull
    public Vector3d toVector3d() {
        return new Vector3d(this.x, this.y, this.z);
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Vector3l vector3l = (Vector3l)o;
        return vector3l.x == this.x && vector3l.y == this.y && vector3l.z == this.z;
    }

    public int hashCode() {
        if (this.hash == 0) {
            this.hash = (int)HashUtil.hash(this.x, this.y, this.z);
        }
        return this.hash;
    }

    @Nonnull
    public String toString() {
        return "Vector3l{x=" + this.x + ", y=" + this.y + ", z=" + this.z + "}";
    }

    @Nonnull
    public static Vector3l max(@Nonnull Vector3l a, @Nonnull Vector3l b) {
        return new Vector3l(Math.max(a.x, b.x), Math.max(a.y, b.y), Math.max(a.z, b.z));
    }

    @Nonnull
    public static Vector3l min(@Nonnull Vector3l a, @Nonnull Vector3l b) {
        return new Vector3l(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.min(a.z, b.z));
    }

    @Nonnull
    public static Vector3l directionTo(@Nonnull Vector3l from, @Nonnull Vector3l to) {
        return to.clone().subtract(from).normalize();
    }

    @Nonnull
    public static Vector3l add(@Nonnull Vector3l one, @Nonnull Vector3l two) {
        return new Vector3l().add(one).add(two);
    }

    @Nonnull
    public static Vector3l add(@Nonnull Vector3l one, @Nonnull Vector3l two, @Nonnull Vector3l three) {
        return new Vector3l().add(one).add(two).add(three);
    }

    static {
        POS_Y = UP = new Vector3l(0L, 1L, 0L);
        NEG_Y = DOWN = new Vector3l(0L, -1L, 0L);
        NEG_Z = FORWARD = new Vector3l(0L, 0L, -1L);
        NORTH = FORWARD;
        POS_Z = BACKWARD = new Vector3l(0L, 0L, 1L);
        SOUTH = BACKWARD;
        POS_X = RIGHT = new Vector3l(1L, 0L, 0L);
        EAST = RIGHT;
        NEG_X = LEFT = new Vector3l(-1L, 0L, 0L);
        WEST = LEFT;
        ALL_ONES = new Vector3l(1L, 1L, 1L);
        MIN = new Vector3l(Long.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE);
        MAX = new Vector3l(Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE);
        BLOCK_SIDES = new Vector3l[]{UP, DOWN, FORWARD, BACKWARD, LEFT, RIGHT};
        BLOCK_EDGES = new Vector3l[]{Vector3l.add(UP, FORWARD), Vector3l.add(DOWN, FORWARD), Vector3l.add(UP, BACKWARD), Vector3l.add(DOWN, BACKWARD), Vector3l.add(UP, LEFT), Vector3l.add(DOWN, LEFT), Vector3l.add(UP, RIGHT), Vector3l.add(DOWN, RIGHT), Vector3l.add(FORWARD, LEFT), Vector3l.add(FORWARD, RIGHT), Vector3l.add(BACKWARD, LEFT), Vector3l.add(BACKWARD, RIGHT)};
        BLOCK_CORNERS = new Vector3l[]{Vector3l.add(UP, FORWARD, LEFT), Vector3l.add(UP, FORWARD, RIGHT), Vector3l.add(DOWN, FORWARD, LEFT), Vector3l.add(DOWN, FORWARD, RIGHT), Vector3l.add(UP, BACKWARD, LEFT), Vector3l.add(UP, BACKWARD, RIGHT), Vector3l.add(DOWN, BACKWARD, LEFT), Vector3l.add(DOWN, BACKWARD, RIGHT)};
        BLOCK_PARTS = new Vector3l[][]{BLOCK_SIDES, BLOCK_EDGES, BLOCK_CORNERS};
        CARDINAL_DIRECTIONS = new Vector3l[]{NORTH, SOUTH, EAST, WEST};
    }
}

