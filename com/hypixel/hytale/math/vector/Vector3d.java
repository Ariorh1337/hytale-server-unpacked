/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.vector;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.schema.metadata.ui.UIDisplayMode;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.codec.Vector3dArrayCodec;
import com.hypixel.hytale.math.util.HashUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.util.TrigMathUtil;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Vector3d {
    @Nonnull
    public static final BuilderCodec<Vector3d> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(Vector3d.class, Vector3d::new).metadata(UIDisplayMode.COMPACT)).appendInherited(new KeyedCodec<Double>("X", Codec.DOUBLE), (o, i) -> {
        o.x = i;
    }, o -> o.x, (o, p) -> {
        o.x = p.x;
    }).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<Double>("Y", Codec.DOUBLE), (o, i) -> {
        o.y = i;
    }, o -> o.y, (o, p) -> {
        o.y = p.y;
    }).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<Double>("Z", Codec.DOUBLE), (o, i) -> {
        o.z = i;
    }, o -> o.z, (o, p) -> {
        o.z = p.z;
    }).addValidator(Validators.nonNull()).add()).build();
    @Deprecated
    public static final Vector3dArrayCodec AS_ARRAY_CODEC = new Vector3dArrayCodec();
    public static final Vector3d ZERO = new Vector3d(0.0, 0.0, 0.0);
    public static final Vector3d UP;
    public static final Vector3d POS_Y;
    public static final Vector3d DOWN;
    public static final Vector3d NEG_Y;
    public static final Vector3d FORWARD;
    public static final Vector3d NEG_Z;
    public static final Vector3d NORTH;
    public static final Vector3d BACKWARD;
    public static final Vector3d POS_Z;
    public static final Vector3d SOUTH;
    public static final Vector3d RIGHT;
    public static final Vector3d POS_X;
    public static final Vector3d EAST;
    public static final Vector3d LEFT;
    public static final Vector3d NEG_X;
    public static final Vector3d WEST;
    public static final Vector3d ALL_ONES;
    public static final Vector3d MIN;
    public static final Vector3d MAX;
    public static final Vector3d[] BLOCK_SIDES;
    public static final Vector3d[] BLOCK_EDGES;
    public static final Vector3d[] BLOCK_CORNERS;
    public static final Vector3d[][] BLOCK_PARTS;
    public static final Vector3d[] CARDINAL_DIRECTIONS;
    public double x;
    public double y;
    public double z;
    private transient int hash;

    public Vector3d() {
        this(0.0, 0.0, 0.0);
    }

    public Vector3d(@Nonnull Vector3d v) {
        this(v.x, v.y, v.z);
    }

    public Vector3d(@Nonnull Vector3i v) {
        this(v.x, v.y, v.z);
    }

    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.hash = 0;
    }

    public Vector3d(float yaw, float pitch) {
        this();
        this.assign(yaw, pitch);
    }

    public Vector3d(@Nonnull Random random, double length) {
        this(random.nextFloat() * ((float)Math.PI * 2), random.nextFloat() * ((float)Math.PI * 2));
        this.scale(length);
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
        this.hash = 0;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
        this.hash = 0;
    }

    public double getZ() {
        return this.z;
    }

    public void setZ(double z) {
        this.z = z;
        this.hash = 0;
    }

    @Nonnull
    public Vector3d assign(@Nonnull Vector3d v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.hash = v.hash;
        return this;
    }

    @Nonnull
    public Vector3d assign(double v) {
        this.x = v;
        this.y = v;
        this.z = v;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3d assign(@Nonnull double[] v) {
        this.x = v[0];
        this.y = v[1];
        this.z = v[2];
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3d assign(@Nonnull float[] v) {
        this.x = v[0];
        this.y = v[1];
        this.z = v[2];
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3d assign(double yaw, double pitch) {
        double len = TrigMathUtil.cos(pitch);
        double x = len * (double)(-TrigMathUtil.sin(yaw));
        double y = TrigMathUtil.sin(pitch);
        double z = len * (double)(-TrigMathUtil.cos(yaw));
        return this.assign(x, y, z);
    }

    @Nonnull
    public Vector3d assign(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3d add(@Nonnull Vector3d v) {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3d add(@Nonnull Vector3i v) {
        this.x += (double)v.x;
        this.y += (double)v.y;
        this.z += (double)v.z;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3d add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3d add(double value) {
        this.x += value;
        this.y += value;
        this.z += value;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3d addScaled(@Nonnull Vector3d v, double s) {
        this.x += v.x * s;
        this.y += v.y * s;
        this.z += v.z * s;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3d subtract(@Nonnull Vector3d v) {
        this.x -= v.x;
        this.y -= v.y;
        this.z -= v.z;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3d subtract(@Nonnull Vector3i v) {
        this.x -= (double)v.x;
        this.y -= (double)v.y;
        this.z -= (double)v.z;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3d subtract(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3d subtract(double value) {
        this.x -= value;
        this.y -= value;
        this.z -= value;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3d negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3d scale(double s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3d scale(@Nonnull Vector3d p) {
        this.x *= p.x;
        this.y *= p.y;
        this.z *= p.z;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3d cross(@Nonnull Vector3d v) {
        double x0 = this.y * v.z - this.z * v.y;
        double y0 = this.z * v.x - this.x * v.z;
        double z0 = this.x * v.y - this.y * v.x;
        return new Vector3d(x0, y0, z0);
    }

    @Nonnull
    public Vector3d cross(@Nonnull Vector3d v, @Nonnull Vector3d res) {
        res.assign(this.y * v.z - this.z * v.y, this.z * v.x - this.x * v.z, this.x * v.y - this.y * v.x);
        return this;
    }

    public double dot(@Nonnull Vector3d other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public double distanceTo(@Nonnull Vector3d v) {
        return Math.sqrt(this.distanceSquaredTo(v));
    }

    public double distanceTo(@Nonnull Vector3i v) {
        return Math.sqrt(this.distanceSquaredTo(v));
    }

    public double distanceTo(double x, double y, double z) {
        return Math.sqrt(this.distanceSquaredTo(x, y, z));
    }

    public double distanceSquaredTo(@Nonnull Vector3d v) {
        double x0 = v.x - this.x;
        double y0 = v.y - this.y;
        double z0 = v.z - this.z;
        return x0 * x0 + y0 * y0 + z0 * z0;
    }

    public double distanceSquaredTo(@Nonnull Vector3i v) {
        double x0 = (double)v.x - this.x;
        double y0 = (double)v.y - this.y;
        double z0 = (double)v.z - this.z;
        return x0 * x0 + y0 * y0 + z0 * z0;
    }

    public double distanceSquaredTo(double x, double y, double z) {
        return (x -= this.x) * x + (y -= this.y) * y + (z -= this.z) * z;
    }

    @Nonnull
    public Vector3d normalize() {
        return this.setLength(1.0);
    }

    public double length() {
        return Math.sqrt(this.squaredLength());
    }

    public double squaredLength() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    @Nonnull
    public Vector3d setLength(double newLen) {
        return this.scale(newLen / this.length());
    }

    @Nonnull
    public Vector3d clampLength(double maxLength) {
        double length = this.length();
        if (maxLength > length) {
            return this;
        }
        return this.scale(maxLength / length);
    }

    @Nonnull
    public Vector3d rotateX(float angle) {
        double cos = TrigMathUtil.cos(angle);
        double sin = TrigMathUtil.sin(angle);
        double cy = this.y * cos - this.z * sin;
        double cz = this.y * sin + this.z * cos;
        this.y = cy;
        this.z = cz;
        return this;
    }

    @Nonnull
    public Vector3d rotateY(float angle) {
        double cos = TrigMathUtil.cos(angle);
        double sin = TrigMathUtil.sin(angle);
        double cx = this.x * cos + this.z * sin;
        double cz = this.x * -sin + this.z * cos;
        this.x = cx;
        this.z = cz;
        return this;
    }

    @Nonnull
    public Vector3d rotateZ(float angle) {
        double cos = TrigMathUtil.cos(angle);
        double sin = TrigMathUtil.sin(angle);
        double cx = this.x * cos - this.y * sin;
        double cy = this.x * sin + this.y * cos;
        this.x = cx;
        this.y = cy;
        return this;
    }

    @Nonnull
    public Vector3d floor() {
        this.x = MathUtil.fastFloor(this.x);
        this.y = MathUtil.fastFloor(this.y);
        this.z = MathUtil.fastFloor(this.z);
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3d ceil() {
        this.x = MathUtil.fastCeil(this.x);
        this.y = MathUtil.fastCeil(this.y);
        this.z = MathUtil.fastCeil(this.z);
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3d clipToZero(double epsilon) {
        this.x = MathUtil.clipToZero(this.x, epsilon);
        this.y = MathUtil.clipToZero(this.y, epsilon);
        this.z = MathUtil.clipToZero(this.z, epsilon);
        this.hash = 0;
        return this;
    }

    public boolean closeToZero(double epsilon) {
        return MathUtil.closeToZero(this.x, epsilon) && MathUtil.closeToZero(this.y, epsilon) && MathUtil.closeToZero(this.z, epsilon);
    }

    public boolean isInside(int x, int y, int z) {
        double dx = this.x - (double)x;
        double dy = this.y - (double)y;
        double dz = this.z - (double)z;
        return dx >= 0.0 && dx < 1.0 && dy >= 0.0 && dy < 1.0 && dz >= 0.0 && dz < 1.0;
    }

    public boolean isFinite() {
        return Double.isFinite(this.x) && Double.isFinite(this.y) && Double.isFinite(this.z);
    }

    @Nonnull
    public Vector3d dropHash() {
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3d clone() {
        return new Vector3d(this.x, this.y, this.z);
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Vector3d vector3d = (Vector3d)o;
        return vector3d.x == this.x && vector3d.y == this.y && vector3d.z == this.z;
    }

    public boolean equals(@Nullable Vector3d o) {
        if (o == null) {
            return false;
        }
        return o.x == this.x && o.y == this.y && o.z == this.z;
    }

    public int hashCode() {
        if (this.hash == 0) {
            this.hash = (int)HashUtil.hash(Double.doubleToLongBits(this.x), Double.doubleToLongBits(this.y), Double.doubleToLongBits(this.z));
        }
        return this.hash;
    }

    @Nonnull
    public String toString() {
        return "Vector3d{x=" + this.x + ", y=" + this.y + ", z=" + this.z + "}";
    }

    @Nonnull
    public static Vector3d max(@Nonnull Vector3d a, @Nonnull Vector3d b) {
        return new Vector3d(Math.max(a.x, b.x), Math.max(a.y, b.y), Math.max(a.z, b.z));
    }

    @Nonnull
    public static Vector3d min(@Nonnull Vector3d a, @Nonnull Vector3d b) {
        return new Vector3d(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.min(a.z, b.z));
    }

    @Nonnull
    public static Vector3d lerp(@Nonnull Vector3d a, @Nonnull Vector3d b, double t) {
        return Vector3d.lerpUnclamped(a, b, MathUtil.clamp(t, 0.0, 1.0));
    }

    @Nonnull
    public static Vector3d lerpUnclamped(@Nonnull Vector3d a, @Nonnull Vector3d b, double t) {
        return new Vector3d(a.x + t * (b.x - a.x), a.y + t * (b.y - a.y), a.z + t * (b.z - a.z));
    }

    @Nonnull
    public static Vector3d directionTo(@Nonnull Vector3d from, @Nonnull Vector3d to) {
        return to.clone().subtract(from).normalize();
    }

    @Nonnull
    public static Vector3d directionTo(@Nonnull Vector3i from, @Nonnull Vector3d to) {
        return to.clone().subtract(from).normalize();
    }

    @Nonnull
    public static Vector3d add(@Nonnull Vector3d one, @Nonnull Vector3d two) {
        return new Vector3d().add(one).add(two);
    }

    @Nonnull
    public static Vector3d add(@Nonnull Vector3d one, @Nonnull Vector3d two, @Nonnull Vector3d three) {
        return new Vector3d().add(one).add(two).add(three);
    }

    @Nonnull
    public static String formatShortString(@Nullable Vector3d v) {
        if (v == null) {
            return "";
        }
        return v.x + "/" + v.y + "/" + v.z;
    }

    @Nonnull
    public Vector3i toVector3i() {
        return new Vector3i(MathUtil.floor(this.x), MathUtil.floor(this.y), MathUtil.floor(this.z));
    }

    @Nonnull
    public Vector3f toVector3f() {
        return new Vector3f((float)this.x, (float)this.y, (float)this.z);
    }

    static {
        POS_Y = UP = new Vector3d(0.0, 1.0, 0.0);
        NEG_Y = DOWN = new Vector3d(0.0, -1.0, 0.0);
        NEG_Z = FORWARD = new Vector3d(0.0, 0.0, -1.0);
        NORTH = FORWARD;
        POS_Z = BACKWARD = new Vector3d(0.0, 0.0, 1.0);
        SOUTH = BACKWARD;
        POS_X = RIGHT = new Vector3d(1.0, 0.0, 0.0);
        EAST = RIGHT;
        NEG_X = LEFT = new Vector3d(-1.0, 0.0, 0.0);
        WEST = LEFT;
        ALL_ONES = new Vector3d(1.0, 1.0, 1.0);
        MIN = new Vector3d(-1.7976931348623157E308, -1.7976931348623157E308, -1.7976931348623157E308);
        MAX = new Vector3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        BLOCK_SIDES = new Vector3d[]{UP, DOWN, FORWARD, BACKWARD, LEFT, RIGHT};
        BLOCK_EDGES = new Vector3d[]{Vector3d.add(UP, FORWARD), Vector3d.add(DOWN, FORWARD), Vector3d.add(UP, BACKWARD), Vector3d.add(DOWN, BACKWARD), Vector3d.add(UP, LEFT), Vector3d.add(DOWN, LEFT), Vector3d.add(UP, RIGHT), Vector3d.add(DOWN, RIGHT), Vector3d.add(FORWARD, LEFT), Vector3d.add(FORWARD, RIGHT), Vector3d.add(BACKWARD, LEFT), Vector3d.add(BACKWARD, RIGHT)};
        BLOCK_CORNERS = new Vector3d[]{Vector3d.add(UP, FORWARD, LEFT), Vector3d.add(UP, FORWARD, RIGHT), Vector3d.add(DOWN, FORWARD, LEFT), Vector3d.add(DOWN, FORWARD, RIGHT), Vector3d.add(UP, BACKWARD, LEFT), Vector3d.add(UP, BACKWARD, RIGHT), Vector3d.add(DOWN, BACKWARD, LEFT), Vector3d.add(DOWN, BACKWARD, RIGHT)};
        BLOCK_PARTS = new Vector3d[][]{BLOCK_SIDES, BLOCK_EDGES, BLOCK_CORNERS};
        CARDINAL_DIRECTIONS = new Vector3d[]{NORTH, SOUTH, EAST, WEST};
    }
}

