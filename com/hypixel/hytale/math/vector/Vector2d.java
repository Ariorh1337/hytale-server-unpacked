/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.vector;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.schema.metadata.ui.UIDisplayMode;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.codec.Vector2dArrayCodec;
import com.hypixel.hytale.math.util.HashUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.util.TrigMathUtil;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Vector2d {
    @Nonnull
    public static final BuilderCodec<Vector2d> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(Vector2d.class, Vector2d::new).metadata(UIDisplayMode.COMPACT)).appendInherited(new KeyedCodec<Double>("X", Codec.DOUBLE), (o, i) -> {
        o.x = i;
    }, o -> o.x, (o, p) -> {
        o.x = p.x;
    }).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<Double>("Y", Codec.DOUBLE), (o, i) -> {
        o.y = i;
    }, o -> o.y, (o, p) -> {
        o.y = p.y;
    }).addValidator(Validators.nonNull()).add()).build();
    @Deprecated
    public static final Vector2dArrayCodec AS_ARRAY_CODEC = new Vector2dArrayCodec();
    public static final Vector2d ZERO = new Vector2d(0.0, 0.0);
    public static final Vector2d UP;
    public static final Vector2d POS_Y;
    public static final Vector2d DOWN;
    public static final Vector2d NEG_Y;
    public static final Vector2d RIGHT;
    public static final Vector2d POS_X;
    public static final Vector2d LEFT;
    public static final Vector2d NEG_X;
    public static final Vector2d ALL_ONES;
    public static final Vector2d[] DIRECTIONS;
    public double x;
    public double y;
    private transient int hash;

    public Vector2d() {
        this(0.0, 0.0);
    }

    public Vector2d(@Nonnull Vector2d v) {
        this(v.x, v.y);
    }

    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
        this.hash = 0;
    }

    public Vector2d(@Nonnull Random random, double length) {
        float yaw = random.nextFloat() * ((float)Math.PI * 2);
        float pitch = random.nextFloat() * ((float)Math.PI * 2);
        this.x = TrigMathUtil.sin(pitch) * TrigMathUtil.cos(yaw);
        this.y = TrigMathUtil.cos(pitch);
        this.scale(length);
        this.hash = 0;
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

    @Nonnull
    public Vector2d assign(@Nonnull Vector2d v) {
        this.x = v.x;
        this.y = v.y;
        this.hash = v.hash;
        return this;
    }

    @Nonnull
    public Vector2d assign(double v) {
        this.x = v;
        this.y = v;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector2d assign(@Nonnull double[] v) {
        this.x = v[0];
        this.y = v[1];
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector2d assign(@Nonnull float[] v) {
        this.x = v[0];
        this.y = v[1];
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector2d assign(double x, double y) {
        this.x = x;
        this.y = y;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector2d add(@Nonnull Vector2d v) {
        this.x += v.x;
        this.y += v.y;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector2d add(double x, double y) {
        this.x += x;
        this.y += y;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector2d addScaled(@Nonnull Vector2d v, double s) {
        this.x += v.x * s;
        this.y += v.y * s;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector2d subtract(@Nonnull Vector2d v) {
        this.x -= v.x;
        this.y -= v.y;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector2d subtract(double x, double y) {
        this.x -= x;
        this.y -= y;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector2d negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector2d scale(double s) {
        this.x *= s;
        this.y *= s;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector2d scale(@Nonnull Vector2d p) {
        this.x *= p.x;
        this.y *= p.y;
        this.hash = 0;
        return this;
    }

    public double dot(@Nonnull Vector2d other) {
        return this.x * other.x + this.y * other.y;
    }

    public double distanceTo(@Nonnull Vector2d v) {
        return Math.sqrt(this.distanceSquaredTo(v));
    }

    public double distanceTo(double x, double y) {
        return Math.sqrt(this.distanceSquaredTo(x, y));
    }

    public double distanceSquaredTo(@Nonnull Vector2d v) {
        double x0 = v.x - this.x;
        double y0 = v.y - this.y;
        return x0 * x0 + y0 * y0;
    }

    public double distanceSquaredTo(double x, double y) {
        return (x -= this.x) * x + (y -= this.y) * y;
    }

    @Nonnull
    public Vector2d normalize() {
        return this.setLength(1.0);
    }

    public double length() {
        return Math.sqrt(this.squaredLength());
    }

    public double squaredLength() {
        return this.x * this.x + this.y * this.y;
    }

    @Nonnull
    public Vector2d setLength(double newLen) {
        return this.scale(newLen / this.length());
    }

    @Nonnull
    public Vector2d clampLength(double maxLength) {
        double length = this.length();
        if (maxLength > length) {
            return this;
        }
        return this.scale(maxLength / length);
    }

    @Nonnull
    public Vector2d floor() {
        this.x = Math.floor(this.x);
        this.y = Math.floor(this.y);
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector2d ceil() {
        this.x = Math.ceil(this.x);
        this.y = Math.ceil(this.y);
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector2d clipToZero(double epsilon) {
        this.x = MathUtil.clipToZero(this.x, epsilon);
        this.y = MathUtil.clipToZero(this.y, epsilon);
        this.hash = 0;
        return this;
    }

    public boolean closeToZero(double epsilon) {
        return MathUtil.closeToZero(this.x, epsilon) && MathUtil.closeToZero(this.y, epsilon);
    }

    public boolean isFinite() {
        return Double.isFinite(this.x) && Double.isFinite(this.y);
    }

    @Nonnull
    public Vector2d dropHash() {
        this.hash = 0;
        return this;
    }

    @Nonnull
    public static Vector2d max(@Nonnull Vector2d a, @Nonnull Vector2d b) {
        return new Vector2d(Math.max(a.x, b.x), Math.max(a.y, b.y));
    }

    @Nonnull
    public static Vector2d min(@Nonnull Vector2d a, @Nonnull Vector2d b) {
        return new Vector2d(Math.min(a.x, b.x), Math.min(a.y, b.y));
    }

    @Nonnull
    public static Vector2d lerp(@Nonnull Vector2d a, @Nonnull Vector2d b, double t) {
        return Vector2d.lerpUnclamped(a, b, MathUtil.clamp(t, 0.0, 1.0));
    }

    @Nonnull
    public static Vector2d lerpUnclamped(@Nonnull Vector2d a, @Nonnull Vector2d b, double t) {
        return new Vector2d(a.x + t * (b.x - a.x), a.y + t * (b.y - a.y));
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Vector2d.distanceSquared(x1, y1, x2, y2));
    }

    public static double distanceSquared(double x1, double y1, double x2, double y2) {
        return (x1 -= x2) * x1 + (y1 -= y2) * y1;
    }

    @Nonnull
    public Vector2d clone() {
        return new Vector2d(this.x, this.y);
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Vector2d vector2d = (Vector2d)o;
        return vector2d.x == this.x && vector2d.y == this.y;
    }

    public int hashCode() {
        if (this.hash == 0) {
            this.hash = (int)HashUtil.hash(Double.doubleToLongBits(this.x), Double.doubleToLongBits(this.y));
        }
        return this.hash;
    }

    @Nonnull
    public String toString() {
        return "Vector2d{x=" + this.x + ", y=" + this.y + "}";
    }

    static {
        POS_Y = UP = new Vector2d(0.0, 1.0);
        NEG_Y = DOWN = new Vector2d(0.0, -1.0);
        POS_X = RIGHT = new Vector2d(1.0, 0.0);
        NEG_X = LEFT = new Vector2d(-1.0, 0.0);
        ALL_ONES = new Vector2d(1.0, 1.0);
        DIRECTIONS = new Vector2d[]{UP, DOWN, LEFT, RIGHT};
    }
}

