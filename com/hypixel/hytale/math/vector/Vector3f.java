/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.vector;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.schema.metadata.ui.UIDisplayMode;
import com.hypixel.hytale.math.Axis;
import com.hypixel.hytale.math.util.HashUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.util.TrigMathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Vector3f {
    @Nonnull
    public static final BuilderCodec<Vector3f> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(Vector3f.class, () -> new Vector3f(Float.NaN, Float.NaN, Float.NaN)).metadata(UIDisplayMode.COMPACT)).appendInherited(new KeyedCodec<Float>("X", Codec.FLOAT), (o, i) -> {
        o.x = i.floatValue();
    }, o -> Float.isNaN(o.x) ? null : Float.valueOf(o.x), (o, p) -> {
        o.x = p.x;
    }).add()).appendInherited(new KeyedCodec<Float>("Y", Codec.FLOAT), (o, i) -> {
        o.y = i.floatValue();
    }, o -> Float.isNaN(o.y) ? null : Float.valueOf(o.y), (o, p) -> {
        o.y = p.y;
    }).add()).appendInherited(new KeyedCodec<Float>("Z", Codec.FLOAT), (o, i) -> {
        o.z = i.floatValue();
    }, o -> Float.isNaN(o.z) ? null : Float.valueOf(o.z), (o, p) -> {
        o.z = p.z;
    }).add()).build();
    @Nonnull
    public static final BuilderCodec<Vector3f> ROTATION = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(Vector3f.class, () -> new Vector3f(Float.NaN, Float.NaN, Float.NaN)).metadata(UIDisplayMode.COMPACT)).appendInherited(new KeyedCodec<Float>("Pitch", Codec.FLOAT), (o, i) -> {
        o.x = i.floatValue();
    }, o -> Float.isNaN(o.x) ? null : Float.valueOf(o.x), (o, p) -> {
        o.x = p.x;
    }).add()).appendInherited(new KeyedCodec<Float>("Yaw", Codec.FLOAT), (o, i) -> {
        o.y = i.floatValue();
    }, o -> Float.isNaN(o.y) ? null : Float.valueOf(o.y), (o, p) -> {
        o.y = p.y;
    }).add()).appendInherited(new KeyedCodec<Float>("Roll", Codec.FLOAT), (o, i) -> {
        o.z = i.floatValue();
    }, o -> Float.isNaN(o.z) ? null : Float.valueOf(o.z), (o, p) -> {
        o.z = p.z;
    }).add()).build();
    public static final Vector3f ZERO = new Vector3f(0.0f, 0.0f, 0.0f);
    public static final Vector3f UP;
    public static final Vector3f POS_Y;
    public static final Vector3f DOWN;
    public static final Vector3f NEG_Y;
    public static final Vector3f FORWARD;
    public static final Vector3f NEG_Z;
    public static final Vector3f NORTH;
    public static final Vector3f BACKWARD;
    public static final Vector3f POS_Z;
    public static final Vector3f SOUTH;
    public static final Vector3f RIGHT;
    public static final Vector3f POS_X;
    public static final Vector3f EAST;
    public static final Vector3f LEFT;
    public static final Vector3f NEG_X;
    public static final Vector3f WEST;
    public static final Vector3f ALL_ONES;
    public static final Vector3f MIN;
    public static final Vector3f MAX;
    public static final Vector3f NaN;
    public static final Vector3f[] BLOCK_SIDES;
    public static final Vector3f[] BLOCK_EDGES;
    public static final Vector3f[] BLOCK_CORNERS;
    public static final Vector3f[][] BLOCK_PARTS;
    public static final Vector3f[] CARDINAL_DIRECTIONS;
    public float x;
    public float y;
    public float z;
    private transient int hash;

    public Vector3f() {
        this(0.0f, 0.0f, 0.0f);
    }

    public Vector3f(@Nonnull Vector3f v) {
        this(v.x, v.y, v.z);
    }

    public Vector3f(@Nonnull Vector3i v) {
        this(v.x, v.y, v.z);
    }

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.hash = 0;
    }

    public Vector3f(float yaw, float pitch) {
        this();
        this.assign(yaw, pitch);
    }

    public Vector3f(@Nonnull Random random, float length) {
        this(random.nextFloat() * ((float)Math.PI * 2), random.nextFloat() * ((float)Math.PI * 2));
        this.scale(length);
    }

    public float getX() {
        return this.x;
    }

    public float getPitch() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
        this.hash = 0;
    }

    public void setPitch(float pitch) {
        this.x = pitch;
        this.hash = 0;
    }

    public float getY() {
        return this.y;
    }

    public float getYaw() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
        this.hash = 0;
    }

    public void setYaw(float yaw) {
        this.y = yaw;
        this.hash = 0;
    }

    public float getZ() {
        return this.z;
    }

    public float getRoll() {
        return this.z;
    }

    public void setZ(float z) {
        this.z = z;
        this.hash = 0;
    }

    public void setRoll(float roll) {
        this.z = roll;
        this.hash = 0;
    }

    @Nonnull
    public Vector3f assign(@Nonnull Vector3f v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.hash = v.hash;
        return this;
    }

    @Nonnull
    public Vector3f assign(float v) {
        this.x = v;
        this.y = v;
        this.z = v;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3f assign(@Nonnull float[] v) {
        this.x = v[0];
        this.y = v[1];
        this.z = v[2];
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3f assign(float yaw, float pitch) {
        float len = TrigMathUtil.cos(pitch);
        float x = len * -TrigMathUtil.sin(yaw);
        float y = TrigMathUtil.sin(pitch);
        float z = len * -TrigMathUtil.cos(yaw);
        return this.assign(x, y, z);
    }

    @Nonnull
    public Vector3f assign(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3f add(@Nonnull Vector3f v) {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3f add(@Nonnull Vector3i v) {
        this.x += (float)v.x;
        this.y += (float)v.y;
        this.z += (float)v.z;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3f add(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.hash = 0;
        return this;
    }

    public void addPitch(float pitch) {
        this.x += pitch;
        this.hash = 0;
    }

    public void addYaw(float yaw) {
        this.y += yaw;
        this.hash = 0;
    }

    public void addRoll(float roll) {
        this.z += roll;
        this.hash = 0;
    }

    @Nonnull
    public Vector3f addScaled(@Nonnull Vector3f v, float s) {
        this.x += v.x * s;
        this.y += v.y * s;
        this.z += v.z * s;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3f subtract(@Nonnull Vector3f v) {
        this.x -= v.x;
        this.y -= v.y;
        this.z -= v.z;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3f subtract(@Nonnull Vector3i v) {
        this.x -= (float)v.x;
        this.y -= (float)v.y;
        this.z -= (float)v.z;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3f subtract(float x, float y, float z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        this.hash = 0;
        return this;
    }

    public void addRotationOnAxis(@Nonnull Axis axis, int angle) {
        float rad = (float)Math.PI / 180 * (float)angle;
        switch (axis) {
            case X: {
                this.setPitch(this.getPitch() + rad);
                break;
            }
            case Y: {
                this.setYaw(this.getYaw() + rad);
                break;
            }
            case Z: {
                this.setRoll(this.getRoll() + rad);
            }
        }
    }

    public void flipRotationOnAxis(@Nonnull Axis axis) {
        this.addRotationOnAxis(axis, 180);
    }

    @Nonnull
    public Vector3f negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3f scale(float s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3f scale(@Nonnull Vector3f p) {
        this.x *= p.x;
        this.y *= p.y;
        this.z *= p.z;
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3f cross(@Nonnull Vector3f v) {
        float x0 = this.y * v.z - this.z * v.y;
        float y0 = this.z * v.x - this.x * v.z;
        float z0 = this.x * v.y - this.y * v.x;
        return new Vector3f(x0, y0, z0);
    }

    @Nonnull
    public Vector3f cross(@Nonnull Vector3f v, @Nonnull Vector3f res) {
        res.assign(this.y * v.z - this.z * v.y, this.z * v.x - this.x * v.z, this.x * v.y - this.y * v.x);
        return this;
    }

    public float dot(@Nonnull Vector3f other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public float distanceTo(@Nonnull Vector3f v) {
        return (float)Math.sqrt(this.distanceSquaredTo(v));
    }

    public float distanceTo(@Nonnull Vector3i v) {
        return (float)Math.sqrt(this.distanceSquaredTo(v));
    }

    public float distanceTo(float x, float y, float z) {
        return (float)Math.sqrt(this.distanceSquaredTo(x, y, z));
    }

    public float distanceSquaredTo(@Nonnull Vector3f v) {
        float x0 = v.x - this.x;
        float y0 = v.y - this.y;
        float z0 = v.z - this.z;
        return x0 * x0 + y0 * y0 + z0 * z0;
    }

    public float distanceSquaredTo(@Nonnull Vector3i v) {
        float x0 = (float)v.x - this.x;
        float y0 = (float)v.y - this.y;
        float z0 = (float)v.z - this.z;
        return x0 * x0 + y0 * y0 + z0 * z0;
    }

    public float distanceSquaredTo(float x, float y, float z) {
        float dx = x - this.x;
        float dy = y - this.y;
        float dz = z - this.z;
        return dx * dx + dy * dy + dz * dz;
    }

    @Nonnull
    public Vector3f normalize() {
        return this.setLength(1.0f);
    }

    public float length() {
        return (float)Math.sqrt(this.squaredLength());
    }

    public float squaredLength() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    @Nonnull
    public Vector3f setLength(float newLen) {
        return this.scale(newLen / this.length());
    }

    @Nonnull
    public Vector3f clampLength(float maxLength) {
        float length = this.length();
        if (maxLength > length) {
            return this;
        }
        return this.scale(maxLength / length);
    }

    @Nonnull
    public Vector3f rotateX(float angle) {
        float cos = TrigMathUtil.cos(angle);
        float sin = TrigMathUtil.sin(angle);
        float cy = this.y * cos - this.z * sin;
        float cz = this.y * sin + this.z * cos;
        this.y = cy;
        this.z = cz;
        return this;
    }

    @Nonnull
    public Vector3f rotateY(float angle) {
        float cos = TrigMathUtil.cos(angle);
        float sin = TrigMathUtil.sin(angle);
        float cx = this.x * cos + this.z * sin;
        float cz = this.x * -sin + this.z * cos;
        this.x = cx;
        this.z = cz;
        return this;
    }

    @Nonnull
    public Vector3f rotateZ(float angle) {
        float cos = TrigMathUtil.cos(angle);
        float sin = TrigMathUtil.sin(angle);
        float cx = this.x * cos - this.y * sin;
        float cy = this.x * sin + this.y * cos;
        this.x = cx;
        this.y = cy;
        return this;
    }

    @Nonnull
    public Vector3f floor() {
        this.x = MathUtil.fastFloor(this.x);
        this.y = MathUtil.fastFloor(this.y);
        this.z = MathUtil.fastFloor(this.z);
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3f ceil() {
        this.x = MathUtil.fastCeil(this.x);
        this.y = MathUtil.fastCeil(this.y);
        this.z = MathUtil.fastCeil(this.z);
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3f clipToZero(float epsilon) {
        this.x = MathUtil.clipToZero(this.x, epsilon);
        this.y = MathUtil.clipToZero(this.y, epsilon);
        this.z = MathUtil.clipToZero(this.z, epsilon);
        this.hash = 0;
        return this;
    }

    public boolean closeToZero(float epsilon) {
        return MathUtil.closeToZero(this.x, epsilon) && MathUtil.closeToZero(this.y, epsilon) && MathUtil.closeToZero(this.z, epsilon);
    }

    public boolean isInside(int x, int y, int z) {
        float dx = this.x - (float)x;
        float dy = this.y - (float)y;
        float dz = this.z - (float)z;
        return dx >= 0.0f && dx < 1.0f && dy >= 0.0f && dy < 1.0f && dz >= 0.0f && dz < 1.0f;
    }

    public boolean isFinite() {
        return Float.isFinite(this.x) && Float.isFinite(this.y) && Float.isFinite(this.z);
    }

    @Nonnull
    public Vector3f dropHash() {
        this.hash = 0;
        return this;
    }

    @Nonnull
    public Vector3f clone() {
        return new Vector3f(this.x, this.y, this.z);
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Vector3f vector3f = (Vector3f)o;
        if (vector3f.x == this.x && vector3f.y == this.y && vector3f.z == this.z) {
            return true;
        }
        return Float.isNaN(vector3f.x) && Float.isNaN(this.x) && Float.isNaN(vector3f.y) && Float.isNaN(this.y) && Float.isNaN(vector3f.z) && Float.isNaN(this.z);
    }

    public boolean equals(@Nullable Vector3f o) {
        if (o == null) {
            return false;
        }
        if (o.x == this.x && o.y == this.y && o.z == this.z) {
            return true;
        }
        return Float.isNaN(o.x) && Float.isNaN(this.x) && Float.isNaN(o.y) && Float.isNaN(this.y) && Float.isNaN(o.z) && Float.isNaN(this.z);
    }

    public int hashCode() {
        if (this.hash == 0) {
            this.hash = (int)HashUtil.hash(Float.floatToIntBits(this.x), Float.floatToIntBits(this.y), Float.floatToIntBits(this.z));
        }
        return this.hash;
    }

    @Nonnull
    public String toString() {
        return "Vector3f{x=" + this.x + ", y=" + this.y + ", z=" + this.z + "}";
    }

    @Nonnull
    public Vector3d toVector3d() {
        return new Vector3d(this.x, this.y, this.z);
    }

    @Nonnull
    public static Vector3f max(@Nonnull Vector3f a, @Nonnull Vector3f b) {
        return new Vector3f(Math.max(a.x, b.x), Math.max(a.y, b.y), Math.max(a.z, b.z));
    }

    @Nonnull
    public static Vector3f min(@Nonnull Vector3f a, @Nonnull Vector3f b) {
        return new Vector3f(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.min(a.z, b.z));
    }

    @Nonnull
    public static Vector3f lerp(@Nonnull Vector3f a, @Nonnull Vector3f b, float t) {
        return Vector3f.lerpUnclamped(a, b, MathUtil.clamp(t, 0.0f, 1.0f));
    }

    @Nonnull
    public static Vector3f lerpUnclamped(@Nonnull Vector3f a, @Nonnull Vector3f b, float t) {
        return new Vector3f(a.x + t * (b.x - a.x), a.y + t * (b.y - a.y), a.z + t * (b.z - a.z));
    }

    @Nonnull
    public static Vector3f lerpAngle(@Nonnull Vector3f a, @Nonnull Vector3f b, float t) {
        return Vector3f.lerpAngle(a, b, t, new Vector3f());
    }

    @Nonnull
    public static Vector3f lerpAngle(@Nonnull Vector3f a, @Nonnull Vector3f b, float t, @Nonnull Vector3f target) {
        target.assign(MathUtil.lerpAngle(a.x, b.x, t), MathUtil.lerpAngle(a.y, b.y, t), MathUtil.lerpAngle(a.z, b.z, t));
        return target;
    }

    @Nonnull
    public static Vector3f directionTo(@Nonnull Vector3f from, @Nonnull Vector3f to) {
        return to.clone().subtract(from).normalize();
    }

    @Nonnull
    public static Vector3f add(@Nonnull Vector3f one, @Nonnull Vector3f two) {
        return new Vector3f().add(one).add(two);
    }

    @Nonnull
    public static Vector3f add(@Nonnull Vector3f one, @Nonnull Vector3f two, @Nonnull Vector3f three) {
        return new Vector3f().add(one).add(two).add(three);
    }

    @Nonnull
    public static Vector3f lookAt(@Nonnull Vector3d relative) {
        return Vector3f.lookAt(relative, new Vector3f());
    }

    @Nonnull
    public static Vector3f lookAt(@Nonnull Vector3d relative, @Nonnull Vector3f result) {
        double length;
        if (!MathUtil.closeToZero(relative.x) || !MathUtil.closeToZero(relative.z)) {
            float yaw = TrigMathUtil.atan2((float)(-relative.x), (float)(-relative.z));
            result.setY(MathUtil.wrapAngle(yaw));
        }
        if ((length = relative.squaredLength()) > 0.0) {
            float pitch = 1.5707964f - (float)Math.acos(relative.y / Math.sqrt(length));
            result.setX(MathUtil.clamp(pitch, -1.5707964f + MathUtil.PITCH_EDGE_PADDING, 1.5707964f - MathUtil.PITCH_EDGE_PADDING));
        }
        return result;
    }

    static {
        POS_Y = UP = new Vector3f(0.0f, 1.0f, 0.0f);
        NEG_Y = DOWN = new Vector3f(0.0f, -1.0f, 0.0f);
        NEG_Z = FORWARD = new Vector3f(0.0f, 0.0f, -1.0f);
        NORTH = FORWARD;
        POS_Z = BACKWARD = new Vector3f(0.0f, 0.0f, 1.0f);
        SOUTH = BACKWARD;
        POS_X = RIGHT = new Vector3f(1.0f, 0.0f, 0.0f);
        EAST = RIGHT;
        NEG_X = LEFT = new Vector3f(-1.0f, 0.0f, 0.0f);
        WEST = LEFT;
        ALL_ONES = new Vector3f(1.0f, 1.0f, 1.0f);
        MIN = new Vector3f(-3.4028235E38f, -3.4028235E38f, -3.4028235E38f);
        MAX = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
        NaN = new Vector3f(Float.NaN, Float.NaN, Float.NaN);
        BLOCK_SIDES = new Vector3f[]{UP, DOWN, FORWARD, BACKWARD, LEFT, RIGHT};
        BLOCK_EDGES = new Vector3f[]{Vector3f.add(UP, FORWARD), Vector3f.add(DOWN, FORWARD), Vector3f.add(UP, BACKWARD), Vector3f.add(DOWN, BACKWARD), Vector3f.add(UP, LEFT), Vector3f.add(DOWN, LEFT), Vector3f.add(UP, RIGHT), Vector3f.add(DOWN, RIGHT), Vector3f.add(FORWARD, LEFT), Vector3f.add(FORWARD, RIGHT), Vector3f.add(BACKWARD, LEFT), Vector3f.add(BACKWARD, RIGHT)};
        BLOCK_CORNERS = new Vector3f[]{Vector3f.add(UP, FORWARD, LEFT), Vector3f.add(UP, FORWARD, RIGHT), Vector3f.add(DOWN, FORWARD, LEFT), Vector3f.add(DOWN, FORWARD, RIGHT), Vector3f.add(UP, BACKWARD, LEFT), Vector3f.add(UP, BACKWARD, RIGHT), Vector3f.add(DOWN, BACKWARD, LEFT), Vector3f.add(DOWN, BACKWARD, RIGHT)};
        BLOCK_PARTS = new Vector3f[][]{BLOCK_SIDES, BLOCK_EDGES, BLOCK_CORNERS};
        CARDINAL_DIRECTIONS = new Vector3f[]{NORTH, SOUTH, EAST, WEST};
    }
}

