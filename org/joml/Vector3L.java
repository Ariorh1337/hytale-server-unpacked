/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.text.NumberFormat;
import org.joml.Math;
import org.joml.MemUtil;
import org.joml.Options;
import org.joml.Runtime;
import org.joml.Vector2L;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2ic;
import org.joml.Vector3Lc;
import org.joml.Vector3dc;
import org.joml.Vector3fc;

public class Vector3L
implements Externalizable,
Cloneable,
Vector3Lc {
    private static final long serialVersionUID = 1L;
    public long x;
    public long y;
    public long z;

    public Vector3L() {
    }

    public Vector3L(int d) {
        this.x = d;
        this.y = d;
        this.z = d;
    }

    public Vector3L(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3L(Vector3Lc v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
    }

    public Vector3L(Vector2ic v, int z) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
    }

    public Vector3L(float x, float y, float z, int mode) {
        this.x = Math.roundLongUsing(x, mode);
        this.y = Math.roundLongUsing(y, mode);
        this.z = Math.roundLongUsing(z, mode);
    }

    public Vector3L(double x, double y, double z, int mode) {
        this.x = Math.roundLongUsing(x, mode);
        this.y = Math.roundLongUsing(y, mode);
        this.z = Math.roundLongUsing(z, mode);
    }

    public Vector3L(Vector2fc v, float z, int mode) {
        this.x = Math.roundLongUsing(v.x(), mode);
        this.y = Math.roundLongUsing(v.y(), mode);
        this.z = Math.roundLongUsing(z, mode);
    }

    public Vector3L(Vector3fc v, int mode) {
        this.x = Math.roundLongUsing(v.x(), mode);
        this.y = Math.roundLongUsing(v.y(), mode);
        this.z = Math.roundLongUsing(v.z(), mode);
    }

    public Vector3L(Vector2dc v, float z, int mode) {
        this.x = Math.roundLongUsing(v.x(), mode);
        this.y = Math.roundLongUsing(v.y(), mode);
        this.z = Math.roundLongUsing(z, mode);
    }

    public Vector3L(Vector3dc v, int mode) {
        this.x = Math.roundLongUsing(v.x(), mode);
        this.y = Math.roundLongUsing(v.y(), mode);
        this.z = Math.roundLongUsing(v.z(), mode);
    }

    public Vector3L(int[] xyz) {
        this.x = xyz[0];
        this.y = xyz[1];
        this.z = xyz[2];
    }

    public Vector3L(ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
    }

    public Vector3L(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
    }

    public Vector3L(LongBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
    }

    public Vector3L(int index, LongBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
    }

    public long x() {
        return this.x;
    }

    public long y() {
        return this.y;
    }

    public long z() {
        return this.z;
    }

    public Vector2f xy(Vector2f dest) {
        return dest.set(this.x, this.y);
    }

    public Vector2d xy(Vector2d dest) {
        return dest.set(this.x, this.y);
    }

    public Vector2L xy(Vector2L dest) {
        return dest.set(this.x, this.y);
    }

    public Vector3L set(Vector3Lc v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        return this;
    }

    public Vector3L set(Vector3dc v) {
        this.x = (int)v.x();
        this.y = (int)v.y();
        this.z = (int)v.z();
        return this;
    }

    public Vector3L set(Vector3dc v, int mode) {
        this.x = Math.roundLongUsing(v.x(), mode);
        this.y = Math.roundLongUsing(v.y(), mode);
        this.z = Math.roundLongUsing(v.z(), mode);
        return this;
    }

    public Vector3L set(Vector3fc v, int mode) {
        this.x = Math.roundLongUsing(v.x(), mode);
        this.y = Math.roundLongUsing(v.y(), mode);
        this.z = Math.roundLongUsing(v.z(), mode);
        return this;
    }

    public Vector3L set(Vector2ic v, long z) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
        return this;
    }

    public Vector3L set(long d) {
        this.x = d;
        this.y = d;
        this.z = d;
        return this;
    }

    public Vector3L set(long x, long y, long z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vector3L set(long[] xyz) {
        this.x = xyz[0];
        this.y = xyz[1];
        this.z = xyz[2];
        return this;
    }

    public Vector3L set(ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
        return this;
    }

    public Vector3L set(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
        return this;
    }

    public Vector3L set(LongBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
        return this;
    }

    public Vector3L set(int index, LongBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
        return this;
    }

    public Vector3L setFromAddress(long address) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        }
        MemUtil.MemUtilUnsafe.get(this, address);
        return this;
    }

    public long get(int component) throws IllegalArgumentException {
        switch (component) {
            case 0: {
                return this.x;
            }
            case 1: {
                return this.y;
            }
            case 2: {
                return this.z;
            }
        }
        throw new IllegalArgumentException();
    }

    public Vector3L setComponent(int component, int value) throws IllegalArgumentException {
        switch (component) {
            case 0: {
                this.x = value;
                break;
            }
            case 1: {
                this.y = value;
                break;
            }
            case 2: {
                this.z = value;
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
        return this;
    }

    public LongBuffer get(LongBuffer buffer) {
        MemUtil.INSTANCE.put(this, buffer.position(), buffer);
        return buffer;
    }

    public LongBuffer get(int index, LongBuffer buffer) {
        MemUtil.INSTANCE.put(this, index, buffer);
        return buffer;
    }

    public ByteBuffer get(ByteBuffer buffer) {
        MemUtil.INSTANCE.put(this, buffer.position(), buffer);
        return buffer;
    }

    public ByteBuffer get(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.put(this, index, buffer);
        return buffer;
    }

    public Vector3Lc getToAddress(long address) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        }
        MemUtil.MemUtilUnsafe.put(this, address);
        return this;
    }

    public Vector3L sub(Vector3Lc v) {
        return this.sub(v, this);
    }

    public Vector3L sub(Vector3Lc v, Vector3L dest) {
        dest.x = this.x - v.x();
        dest.y = this.y - v.y();
        dest.z = this.z - v.z();
        return dest;
    }

    public Vector3L sub(long x, long y, long z) {
        return this.sub(x, y, z, this);
    }

    public Vector3L sub(long x, long y, long z, Vector3L dest) {
        dest.x = this.x - x;
        dest.y = this.y - y;
        dest.z = this.z - z;
        return dest;
    }

    public Vector3L add(Vector3Lc v) {
        return this.add(v, this);
    }

    public Vector3L add(Vector3Lc v, Vector3L dest) {
        dest.x = this.x + v.x();
        dest.y = this.y + v.y();
        dest.z = this.z + v.z();
        return dest;
    }

    public Vector3L add(long x, long y, long z) {
        return this.add(x, y, z, this);
    }

    public Vector3L add(long x, long y, long z, Vector3L dest) {
        dest.x = this.x + x;
        dest.y = this.y + y;
        dest.z = this.z + z;
        return dest;
    }

    public Vector3L mul(long scalar) {
        return this.mul(scalar, this);
    }

    public Vector3L mul(long scalar, Vector3L dest) {
        dest.x = this.x * scalar;
        dest.y = this.y * scalar;
        dest.z = this.z * scalar;
        return dest;
    }

    public Vector3L mul(Vector3Lc v) {
        return this.mul(v, this);
    }

    public Vector3L mul(Vector3Lc v, Vector3L dest) {
        dest.x = this.x * v.x();
        dest.y = this.y * v.y();
        dest.z = this.z * v.z();
        return dest;
    }

    public Vector3L mul(long x, long y, long z) {
        return this.mul(x, y, z, this);
    }

    public Vector3L mul(long x, long y, long z, Vector3L dest) {
        dest.x = this.x * x;
        dest.y = this.y * y;
        dest.z = this.z * z;
        return dest;
    }

    public Vector3L div(float scalar) {
        return this.div(scalar, this);
    }

    public Vector3L div(float scalar, Vector3L dest) {
        float invscalar = 1.0f / scalar;
        dest.x = (int)((float)this.x * invscalar);
        dest.y = (int)((float)this.y * invscalar);
        dest.z = (int)((float)this.z * invscalar);
        return dest;
    }

    public Vector3L div(long scalar) {
        return this.div(scalar, this);
    }

    public Vector3L div(long scalar, Vector3L dest) {
        dest.x = this.x / scalar;
        dest.y = this.y / scalar;
        dest.z = this.z / scalar;
        return dest;
    }

    public long lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public static long lengthSquared(long x, long y, long z) {
        return x * x + y * y + z * z;
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public static double length(long x, long y, long z) {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double distance(Vector3Lc v) {
        long dx = this.x - v.x();
        long dy = this.y - v.y();
        long dz = this.z - v.z();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public double distance(long x, long y, long z) {
        long dx = this.x - x;
        long dy = this.y - y;
        long dz = this.z - z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public long gridDistance(Vector3Lc v) {
        return Math.abs(v.x() - this.x()) + Math.abs(v.y() - this.y()) + Math.abs(v.z() - this.z());
    }

    public long gridDistance(long x, long y, long z) {
        return Math.abs(x - this.x()) + Math.abs(y - this.y()) + Math.abs(z - this.z());
    }

    public long distanceSquared(Vector3Lc v) {
        long dx = this.x - v.x();
        long dy = this.y - v.y();
        long dz = this.z - v.z();
        return dx * dx + dy * dy + dz * dz;
    }

    public long distanceSquared(long x, long y, long z) {
        long dx = this.x - x;
        long dy = this.y - y;
        long dz = this.z - z;
        return dx * dx + dy * dy + dz * dz;
    }

    public static double distance(long x1, long y1, long z1, long x2, long y2, long z2) {
        return Math.sqrt(Vector3L.distanceSquared(x1, y1, z1, x2, y2, z2));
    }

    public static long distanceSquared(long x1, long y1, long z1, long x2, long y2, long z2) {
        long dx = x1 - x2;
        long dy = y1 - y2;
        long dz = z1 - z2;
        return dx * dx + dy * dy + dz * dz;
    }

    public Vector3L zero() {
        this.x = 0L;
        this.y = 0L;
        this.z = 0L;
        return this;
    }

    public String toString() {
        return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
    }

    public String toString(NumberFormat formatter) {
        return "(" + formatter.format(this.x) + " " + formatter.format(this.y) + " " + formatter.format(this.z) + ")";
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(this.x);
        out.writeLong(this.y);
        out.writeLong(this.z);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.x = in.readLong();
        this.y = in.readLong();
        this.z = in.readLong();
    }

    public Vector3L negate() {
        return this.negate(this);
    }

    public Vector3L negate(Vector3L dest) {
        dest.x = -this.x;
        dest.y = -this.y;
        dest.z = -this.z;
        return dest;
    }

    public Vector3L min(Vector3Lc v) {
        return this.min(v, this);
    }

    public Vector3L min(Vector3Lc v, Vector3L dest) {
        dest.x = this.x < v.x() ? this.x : v.x();
        dest.y = this.y < v.y() ? this.y : v.y();
        dest.z = this.z < v.z() ? this.z : v.z();
        return dest;
    }

    public Vector3L max(Vector3Lc v) {
        return this.max(v, this);
    }

    public Vector3L max(Vector3Lc v, Vector3L dest) {
        dest.x = this.x > v.x() ? this.x : v.x();
        dest.y = this.y > v.y() ? this.y : v.y();
        dest.z = this.z > v.z() ? this.z : v.z();
        return dest;
    }

    public int maxComponent() {
        float absX = Math.abs(this.x);
        float absY = Math.abs(this.y);
        float absZ = Math.abs(this.z);
        if (absX >= absY && absX >= absZ) {
            return 0;
        }
        if (absY >= absZ) {
            return 1;
        }
        return 2;
    }

    public int minComponent() {
        float absX = Math.abs(this.x);
        float absY = Math.abs(this.y);
        float absZ = Math.abs(this.z);
        if (absX < absY && absX < absZ) {
            return 0;
        }
        if (absY < absZ) {
            return 1;
        }
        return 2;
    }

    public Vector3L absolute() {
        return this.absolute(this);
    }

    public Vector3L absolute(Vector3L dest) {
        dest.x = Math.abs(this.x);
        dest.y = Math.abs(this.y);
        dest.z = Math.abs(this.z);
        return dest;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (int)(this.x ^ this.x >>> 32);
        result = 31 * result + (int)(this.y ^ this.y >>> 32);
        result = 31 * result + (int)(this.z ^ this.z >>> 32);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Vector3L other = (Vector3L)obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        return this.z == other.z;
    }

    public boolean equals(long x, long y, long z) {
        if (this.x != x) {
            return false;
        }
        if (this.y != y) {
            return false;
        }
        return this.z == z;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

