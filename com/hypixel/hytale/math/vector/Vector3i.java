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
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3l;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Vector3i {
    @Nonnull
    public static final BuilderCodec<Vector3i> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(Vector3i.class, Vector3i::new).metadata(UIDisplayMode.COMPACT)).appendInherited(new KeyedCodec<Integer>("X", Codec.INTEGER), (o, i) -> {
        o.x = i;
    }, o -> o.x, (o, p) -> {
        o.x = p.x;
    }).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<Integer>("Y", Codec.INTEGER), (o, i) -> {
        o.y = i;
    }, o -> o.y, (o, p) -> {
        o.y = p.y;
    }).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<Integer>("Z", Codec.INTEGER), (o, i) -> {
        o.z = i;
    }, o -> o.z, (o, p) -> {
        o.z = p.z;
    }).addValidator(Validators.nonNull()).add()).build();
    public static final Vector3i ZERO = new Vector3i(0, 0, 0);
    public static final Vector3i UP;
    public static final Vector3i POS_Y;
    public static final Vector3i DOWN;
    public static final Vector3i NEG_Y;
    public static final Vector3i FORWARD;
    public static final Vector3i NEG_Z;
    public static final Vector3i NORTH;
    public static final Vector3i BACKWARD;
    public static final Vector3i POS_Z;
    public static final Vector3i SOUTH;
    public static final Vector3i RIGHT;
    public static final Vector3i POS_X;
    public static final Vector3i EAST;
    public static final Vector3i LEFT;
    public static final Vector3i NEG_X;
    public static final Vector3i WEST;
    public static final Vector3i ALL_ONES;
    public static final Vector3i MIN;
    public static final Vector3i MAX;
    public static final Vector3i[] BLOCK_SIDES;
    public static final Vector3i[] BLOCK_EDGES;
    public static final Vector3i[] BLOCK_CORNERS;
    public static final Vector3i[][] BLOCK_PARTS;
    public static final Vector3i[] CARDINAL_DIRECTIONS;
    public int x;
    public int y;
    public int z;
    private transient int hash;

    public Vector3i() {
        this(0, 0, 0);
    }

    public Vector3i(@Nonnull Vector3i v) {
        this(v.x, v.y, v.z);
    }

    public Vector3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.hash = 0;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
        this.hash = 0;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
        this.hash = 0;
    }

    public int getZ() {
        return this.z;
    }

    public void setZ(int z) {
        this.z = z;
        this.hash = 0;
    }

    @Nonnull
    public Vector3i assign(@Nonnull Vector3i v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.hash = v.hash;
        return this;
    }

    @Nonnull
    public Vector3i assign(int v) {
        this.x = v;
        this.y = v;
        this.z = v;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3i assign(@Nonnull int[] v) {
        this.x = v[0];
        this.y = v[1];
        this.z = v[2];
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3i assign(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3i add(@Nonnull Vector3i v) {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3i add(int x, int y, int z) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3i addScaled(@Nonnull Vector3i v, int s) {
        this.x += v.x * s;
        this.y += v.y * s;
        this.z += v.z * s;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3i subtract(@Nonnull Vector3i v) {
        this.x -= v.x;
        this.y -= v.y;
        this.z -= v.z;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3i subtract(int x, int y, int z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3i negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3i scale(int s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3i scale(double s) {
        this.x = (int)((double)this.x * s);
        this.y = (int)((double)this.y * s);
        this.z = (int)((double)this.z * s);
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3i scale(@Nonnull Vector3i p) {
        this.x *= p.x;
        this.y *= p.y;
        this.z *= p.z;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3i cross(@Nonnull Vector3i v) {
        int x0 = this.y * v.z - this.z * v.y;
        int y0 = this.z * v.x - this.x * v.z;
        int z0 = this.x * v.y - this.y * v.x;
        return new Vector3i(x0, y0, z0);
    }

    @Nonnull
    public Vector3i cross(@Nonnull Vector3i v, @Nonnull Vector3i res) {
        res.assign(this.y * v.z - this.z * v.y, this.z * v.x - this.x * v.z, this.x * v.y - this.y * v.x);
        return this;
    }

    public int dot(@Nonnull Vector3i other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public double distanceTo(@Nonnull Vector3i v) {
        return Math.sqrt(this.distanceSquaredTo(v));
    }

    public double distanceTo(int x, int y, int z) {
        return Math.sqrt(this.distanceSquaredTo(x, y, z));
    }

    public int distanceSquaredTo(@Nonnull Vector3i v) {
        int x0 = v.x - this.x;
        int y0 = v.y - this.y;
        int z0 = v.z - this.z;
        return x0 * x0 + y0 * y0 + z0 * z0;
    }

    public int distanceSquaredTo(int x, int y, int z) {
        int dx = x - this.x;
        int dy = y - this.y;
        int dz = z - this.z;
        return dx * dx + dy * dy + dz * dz;
    }

    @Nonnull
    public Vector3i normalize() {
        return this.setLength(1);
    }

    public double length() {
        return Math.sqrt(this.squaredLength());
    }

    public int squaredLength() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    @Nonnull
    public Vector3i setLength(int newLen) {
        return this.scale((double)newLen / this.length());
    }

    @Nonnull
    public Vector3i clampLength(int maxLength) {
        double length = this.length();
        if ((double)maxLength > length) {
            return this;
        }
        return this.scale((double)maxLength / length);
    }

    @Nonnull
    public Vector3i dropHash() {
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3i clone() {
        return new Vector3i(this.x, this.y, this.z);
    }

    @Nonnull
    public Vector3d toVector3d() {
        return new Vector3d(this.x, this.y, this.z);
    }

    @Nonnull
    public Vector3f toVector3f() {
        return new Vector3f(this.x, this.y, this.z);
    }

    @Nonnull
    public Vector3l toVector3l() {
        return new Vector3l(this.x, this.y, this.z);
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Vector3i vector3i = (Vector3i)o;
        return vector3i.x == this.x && vector3i.y == this.y && vector3i.z == this.z;
    }

    public int hashCode() {
        if (this.hash == 0) {
            this.hash = (int)HashUtil.hash(this.x, this.y, this.z);
        }
        return this.hash;
    }

    @Nonnull
    public String toString() {
        return "Vector3i{x=" + this.x + ", y=" + this.y + ", z=" + this.z + "}";
    }

    @Nonnull
    public static Vector3i max(@Nonnull Vector3i a, @Nonnull Vector3i b) {
        return new Vector3i(Math.max(a.x, b.x), Math.max(a.y, b.y), Math.max(a.z, b.z));
    }

    @Nonnull
    public static Vector3i min(@Nonnull Vector3i a, @Nonnull Vector3i b) {
        return new Vector3i(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.min(a.z, b.z));
    }

    @Nonnull
    public static Vector3i directionTo(@Nonnull Vector3i from, @Nonnull Vector3i to) {
        return to.clone().subtract(from).normalize();
    }

    @Nonnull
    public static Vector3i add(@Nonnull Vector3i one, @Nonnull Vector3i two) {
        return new Vector3i().add(one).add(two);
    }

    @Nonnull
    public static Vector3i add(@Nonnull Vector3i one, @Nonnull Vector3i two, @Nonnull Vector3i three) {
        return new Vector3i().add(one).add(two).add(three);
    }

    static {
        POS_Y = UP = new Vector3i(0, 1, 0);
        NEG_Y = DOWN = new Vector3i(0, -1, 0);
        NEG_Z = FORWARD = new Vector3i(0, 0, -1);
        NORTH = FORWARD;
        POS_Z = BACKWARD = new Vector3i(0, 0, 1);
        SOUTH = BACKWARD;
        POS_X = RIGHT = new Vector3i(1, 0, 0);
        EAST = RIGHT;
        NEG_X = LEFT = new Vector3i(-1, 0, 0);
        WEST = LEFT;
        ALL_ONES = new Vector3i(1, 1, 1);
        MIN = new Vector3i(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
        MAX = new Vector3i(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
        BLOCK_SIDES = new Vector3i[]{UP, DOWN, FORWARD, BACKWARD, LEFT, RIGHT};
        BLOCK_EDGES = new Vector3i[]{Vector3i.add(UP, FORWARD), Vector3i.add(DOWN, FORWARD), Vector3i.add(UP, BACKWARD), Vector3i.add(DOWN, BACKWARD), Vector3i.add(UP, LEFT), Vector3i.add(DOWN, LEFT), Vector3i.add(UP, RIGHT), Vector3i.add(DOWN, RIGHT), Vector3i.add(FORWARD, LEFT), Vector3i.add(FORWARD, RIGHT), Vector3i.add(BACKWARD, LEFT), Vector3i.add(BACKWARD, RIGHT)};
        BLOCK_CORNERS = new Vector3i[]{Vector3i.add(UP, FORWARD, LEFT), Vector3i.add(UP, FORWARD, RIGHT), Vector3i.add(DOWN, FORWARD, LEFT), Vector3i.add(DOWN, FORWARD, RIGHT), Vector3i.add(UP, BACKWARD, LEFT), Vector3i.add(UP, BACKWARD, RIGHT), Vector3i.add(DOWN, BACKWARD, LEFT), Vector3i.add(DOWN, BACKWARD, RIGHT)};
        BLOCK_PARTS = new Vector3i[][]{BLOCK_SIDES, BLOCK_EDGES, BLOCK_CORNERS};
        CARDINAL_DIRECTIONS = new Vector3i[]{NORTH, SOUTH, EAST, WEST};
    }
}

