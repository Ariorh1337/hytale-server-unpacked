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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Vector2i {
    @Nonnull
    public static final BuilderCodec<Vector2i> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(Vector2i.class, Vector2i::new).metadata(UIDisplayMode.COMPACT)).appendInherited(new KeyedCodec<Integer>("X", Codec.INTEGER), (o, i) -> {
        o.x = i;
    }, o -> o.x, (o, p) -> {
        o.x = p.x;
    }).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<Integer>("Y", Codec.INTEGER), (o, i) -> {
        o.y = i;
    }, o -> o.y, (o, p) -> {
        o.y = p.y;
    }).addValidator(Validators.nonNull()).add()).build();
    public static final Vector2i ZERO = new Vector2i(0, 0);
    public static final Vector2i UP;
    public static final Vector2i POS_Y;
    public static final Vector2i DOWN;
    public static final Vector2i NEG_Y;
    public static final Vector2i RIGHT;
    public static final Vector2i POS_X;
    public static final Vector2i LEFT;
    public static final Vector2i NEG_X;
    public static final Vector2i ALL_ONES;
    public static final Vector2i[] DIRECTIONS;
    public int x;
    public int y;
    private transient int hash;

    public Vector2i() {
        this(0, 0);
    }

    public Vector2i(@Nonnull Vector2i v) {
        this(v.x, v.y);
    }

    public Vector2i(int x, int y) {
        this.x = x;
        this.y = y;
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

    @Nonnull
    public Vector2i assign(@Nonnull Vector2i v) {
        this.x = v.x;
        this.y = v.y;
        this.hash = v.hash;
        return this;
    }

    @Nonnull
    public Vector2i assign(int v) {
        this.x = v;
        this.y = v;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector2i assign(@Nonnull int[] v) {
        this.x = v[0];
        this.y = v[1];
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector2i assign(int x, int y) {
        this.x = x;
        this.y = y;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector2i add(@Nonnull Vector2i v) {
        this.x += v.x;
        this.y += v.y;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector2i add(int x, int y) {
        this.x += x;
        this.y += y;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector2i addScaled(@Nonnull Vector2i v, int s) {
        this.x += v.x * s;
        this.y += v.y * s;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector2i subtract(@Nonnull Vector2i v) {
        this.x -= v.x;
        this.y -= v.y;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector2i subtract(int x, int y) {
        this.x -= x;
        this.y -= y;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector2i negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector2i scale(int s) {
        this.x *= s;
        this.y *= s;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector2i scale(@Nonnull Vector2i p) {
        this.x *= p.x;
        this.y *= p.y;
        this.hash = 0;
        return this;
    }

    public int dot(@Nonnull Vector2i other) {
        return this.x * other.x + this.y * other.y;
    }

    public double distanceTo(@Nonnull Vector2i v) {
        return Math.sqrt(this.distanceSquaredTo(v));
    }

    public double distanceTo(int x, int y) {
        return Math.sqrt(this.distanceSquaredTo(x, y));
    }

    public int distanceSquaredTo(@Nonnull Vector2i v) {
        int x0 = v.x - this.x;
        int y0 = v.y - this.y;
        return x0 * x0 + y0 * y0;
    }

    public int distanceSquaredTo(int x, int y) {
        return (x -= this.x) * x + (y -= this.y) * y;
    }

    @Nonnull
    public Vector2i normalize() {
        return this.setLength(1);
    }

    public double length() {
        return Math.sqrt(this.squaredLength());
    }

    public double squaredLength() {
        return this.x * this.x + this.y * this.y;
    }

    @Nonnull
    public Vector2i setLength(int newLen) {
        double scale = (double)newLen / this.length();
        this.x = (int)((double)this.x * scale);
        this.y = (int)((double)this.y * scale);
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector2i clampLength(int maxLength) {
        double length = this.length();
        if ((double)maxLength > length) {
            return this;
        }
        double scale = (double)maxLength / length;
        this.x = (int)((double)this.x * scale);
        this.y = (int)((double)this.y * scale);
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector2i dropHash() {
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector2i clone() {
        return new Vector2i(this.x, this.y);
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Vector2i vector2i = (Vector2i)o;
        return vector2i.x == this.x && vector2i.y == this.y;
    }

    public int hashCode() {
        if (this.hash == 0) {
            this.hash = (int)HashUtil.hash(this.x, this.y);
        }
        return this.hash;
    }

    @Nonnull
    public String toString() {
        return "Vector2i{x=" + this.x + ", y=" + this.y + "}";
    }

    @Nonnull
    public static Vector2i max(@Nonnull Vector2i a, @Nonnull Vector2i b) {
        return new Vector2i(Math.max(a.x, b.x), Math.max(a.y, b.y));
    }

    @Nonnull
    public static Vector2i min(@Nonnull Vector2i a, @Nonnull Vector2i b) {
        return new Vector2i(Math.min(a.x, b.x), Math.min(a.y, b.y));
    }

    static {
        POS_Y = UP = new Vector2i(0, 1);
        NEG_Y = DOWN = new Vector2i(0, -1);
        POS_X = RIGHT = new Vector2i(1, 0);
        NEG_X = LEFT = new Vector2i(-1, 0);
        ALL_ONES = new Vector2i(1, 1);
        DIRECTIONS = new Vector2i[]{UP, DOWN, LEFT, RIGHT};
    }
}

