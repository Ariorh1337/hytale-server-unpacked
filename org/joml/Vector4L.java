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
import org.joml.Vector2Lc;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2ic;
import org.joml.Vector3L;
import org.joml.Vector3Lc;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3ic;
import org.joml.Vector4Lc;
import org.joml.Vector4dc;
import org.joml.Vector4fc;
import org.joml.Vector4ic;

public class Vector4L
implements Externalizable,
Cloneable,
Vector4Lc {
    private static final long serialVersionUID = 1L;
    public long x;
    public long y;
    public long z;
    public long w;

    public Vector4L() {
        this.w = 1L;
    }

    public Vector4L(Vector4Lc v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = v.w();
    }

    public Vector4L(Vector4ic v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = v.w();
    }

    public Vector4L(Vector3Lc v, long w) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = w;
    }

    public Vector4L(Vector3ic v, long w) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = w;
    }

    public Vector4L(Vector2Lc v, long z, long w) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
        this.w = w;
    }

    public Vector4L(Vector2ic v, long z, long w) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
        this.w = w;
    }

    public Vector4L(Vector3fc v, float w, int mode) {
        this.x = Math.roundLongUsing(v.x(), mode);
        this.y = Math.roundLongUsing(v.y(), mode);
        this.z = Math.roundLongUsing(v.z(), mode);
        this.w = Math.roundLongUsing(w, mode);
    }

    public Vector4L(Vector4fc v, int mode) {
        this.x = Math.roundLongUsing(v.x(), mode);
        this.y = Math.roundLongUsing(v.y(), mode);
        this.z = Math.roundLongUsing(v.z(), mode);
        this.w = Math.roundLongUsing(v.w(), mode);
    }

    public Vector4L(Vector4dc v, int mode) {
        this.x = Math.roundLongUsing(v.x(), mode);
        this.y = Math.roundLongUsing(v.y(), mode);
        this.z = Math.roundLongUsing(v.z(), mode);
        this.w = Math.roundLongUsing(v.w(), mode);
    }

    public Vector4L(long s) {
        this.x = s;
        this.y = s;
        this.z = s;
        this.w = s;
    }

    public Vector4L(long x, long y, long z, long w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4L(int[] xyzw) {
        this.x = xyzw[0];
        this.y = xyzw[1];
        this.z = xyzw[2];
        this.w = xyzw[3];
    }

    public Vector4L(ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
    }

    public Vector4L(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
    }

    public Vector4L(LongBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
    }

    public Vector4L(int index, LongBuffer buffer) {
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

    public long w() {
        return this.w;
    }

    public Vector3f xyz(Vector3f dest) {
        return dest.set(this.x, this.y, this.z);
    }

    public Vector3d xyz(Vector3d dest) {
        return dest.set(this.x, this.y, this.z);
    }

    public Vector3L xyz(Vector3L dest) {
        return dest.set(this.x, this.y, this.z);
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

    public Vector4L set(Vector4Lc v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = v.w();
        return this;
    }

    public Vector4L set(Vector4ic v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = v.w();
        return this;
    }

    public Vector4L set(Vector4dc v) {
        this.x = (int)v.x();
        this.y = (int)v.y();
        this.z = (int)v.z();
        this.w = (int)v.w();
        return this;
    }

    public Vector4L set(Vector4dc v, int mode) {
        this.x = Math.roundLongUsing(v.x(), mode);
        this.y = Math.roundLongUsing(v.y(), mode);
        this.z = Math.roundLongUsing(v.z(), mode);
        this.w = Math.roundLongUsing(v.w(), mode);
        return this;
    }

    public Vector4L set(Vector4fc v, int mode) {
        this.x = Math.roundLongUsing(v.x(), mode);
        this.y = Math.roundLongUsing(v.y(), mode);
        this.z = Math.roundLongUsing(v.z(), mode);
        this.w = Math.roundLongUsing(v.w(), mode);
        return this;
    }

    public Vector4L set(Vector3ic v, long w) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = w;
        return this;
    }

    public Vector4L set(Vector2ic v, long z, long w) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
        this.w = w;
        return this;
    }

    public Vector4L set(long s) {
        this.x = s;
        this.y = s;
        this.z = s;
        this.w = s;
        return this;
    }

    public Vector4L set(long x, long y, long z, long w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    public Vector4L set(int[] xyzw) {
        this.x = xyzw[0];
        this.y = xyzw[1];
        this.z = xyzw[2];
        this.w = xyzw[3];
        return this;
    }

    public Vector4L set(ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
        return this;
    }

    public Vector4L set(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
        return this;
    }

    public Vector4L set(LongBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
        return this;
    }

    public Vector4L set(int index, LongBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
        return this;
    }

    public Vector4L setFromAddress(long address) {
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
            case 3: {
                return this.w;
            }
        }
        throw new IllegalArgumentException();
    }

    public int maxComponent() {
        long absX = Math.abs(this.x);
        long absY = Math.abs(this.y);
        long absZ = Math.abs(this.z);
        long absW = Math.abs(this.w);
        if (absX >= absY && absX >= absZ && absX >= absW) {
            return 0;
        }
        if (absY >= absZ && absY >= absW) {
            return 1;
        }
        if (absZ >= absW) {
            return 2;
        }
        return 3;
    }

    public int minComponent() {
        long absX = Math.abs(this.x);
        long absY = Math.abs(this.y);
        long absZ = Math.abs(this.z);
        long absW = Math.abs(this.w);
        if (absX < absY && absX < absZ && absX < absW) {
            return 0;
        }
        if (absY < absZ && absY < absW) {
            return 1;
        }
        if (absZ < absW) {
            return 2;
        }
        return 3;
    }

    public Vector4L setComponent(int component, long value) throws IllegalArgumentException {
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
            case 3: {
                this.w = value;
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

    public Vector4Lc getToAddress(long address) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        }
        MemUtil.MemUtilUnsafe.put(this, address);
        return this;
    }

    public Vector4L sub(Vector4Lc v) {
        return this.sub(v, this);
    }

    public Vector4L sub(Vector4Lc v, Vector4L dest) {
        dest.x = this.x - v.x();
        dest.y = this.y - v.y();
        dest.z = this.z - v.z();
        dest.w = this.w - v.w();
        return dest;
    }

    public Vector4L sub(Vector4ic v) {
        return this.sub(v, this);
    }

    public Vector4L sub(Vector4ic v, Vector4L dest) {
        dest.x = this.x - (long)v.x();
        dest.y = this.y - (long)v.y();
        dest.z = this.z - (long)v.z();
        dest.w = this.w - (long)v.w();
        return dest;
    }

    public Vector4L sub(long x, long y, long z, long w) {
        return this.sub(x, y, z, w, this);
    }

    public Vector4L sub(long x, long y, long z, long w, Vector4L dest) {
        dest.x = this.x - x;
        dest.y = this.y - y;
        dest.z = this.z - z;
        dest.w = this.w - w;
        return dest;
    }

    public Vector4L add(Vector4Lc v) {
        return this.add(v, this);
    }

    public Vector4L add(Vector4Lc v, Vector4L dest) {
        dest.x = this.x + v.x();
        dest.y = this.y + v.y();
        dest.z = this.z + v.z();
        dest.w = this.w + v.w();
        return dest;
    }

    public Vector4L add(Vector4ic v) {
        return this.add(v, this);
    }

    public Vector4L add(Vector4ic v, Vector4L dest) {
        dest.x = this.x + (long)v.x();
        dest.y = this.y + (long)v.y();
        dest.z = this.z + (long)v.z();
        dest.w = this.w + (long)v.w();
        return dest;
    }

    public Vector4L add(long x, long y, long z, long w) {
        return this.add(x, y, z, w, this);
    }

    public Vector4L add(long x, long y, long z, long w, Vector4L dest) {
        dest.x = this.x + x;
        dest.y = this.y + y;
        dest.z = this.z + z;
        dest.w = this.w + w;
        return dest;
    }

    public Vector4L mul(Vector4Lc v) {
        return this.mul(v, this);
    }

    public Vector4L mul(Vector4Lc v, Vector4L dest) {
        dest.x = this.x * v.x();
        dest.y = this.y * v.y();
        dest.z = this.z * v.z();
        dest.w = this.w * v.w();
        return dest;
    }

    public Vector4L mul(Vector4ic v) {
        return this.mul(v, this);
    }

    public Vector4L mul(Vector4ic v, Vector4L dest) {
        dest.x = this.x * (long)v.x();
        dest.y = this.y * (long)v.y();
        dest.z = this.z * (long)v.z();
        dest.w = this.w * (long)v.w();
        return dest;
    }

    public Vector4L div(Vector4Lc v) {
        return this.div(v, this);
    }

    public Vector4L div(Vector4Lc v, Vector4L dest) {
        dest.x = this.x / v.x();
        dest.y = this.y / v.y();
        dest.z = this.z / v.z();
        dest.w = this.w / v.w();
        return dest;
    }

    public Vector4L div(Vector4ic v) {
        return this.div(v, this);
    }

    public Vector4L div(Vector4ic v, Vector4L dest) {
        dest.x = this.x / (long)v.x();
        dest.y = this.y / (long)v.y();
        dest.z = this.z / (long)v.z();
        dest.w = this.w / (long)v.w();
        return dest;
    }

    public Vector4L mul(long scalar) {
        return this.mul(scalar, this);
    }

    public Vector4L mul(long scalar, Vector4L dest) {
        dest.x = this.x * scalar;
        dest.y = this.y * scalar;
        dest.z = this.z * scalar;
        dest.w = this.w * scalar;
        return dest;
    }

    public Vector4L div(float scalar) {
        return this.div(scalar, this);
    }

    public Vector4L div(float scalar, Vector4L dest) {
        float invscalar = 1.0f / scalar;
        dest.x = (int)((float)this.x * invscalar);
        dest.y = (int)((float)this.y * invscalar);
        dest.z = (int)((float)this.z * invscalar);
        dest.w = (int)((float)this.w * invscalar);
        return dest;
    }

    public Vector4L div(long scalar) {
        return this.div(scalar, this);
    }

    public Vector4L div(long scalar, Vector4L dest) {
        dest.x = this.x / scalar;
        dest.y = this.y / scalar;
        dest.z = this.z / scalar;
        dest.w = this.w / scalar;
        return dest;
    }

    public long lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
    }

    public static long lengthSquared(long x, long y, long z, long w) {
        return x * x + y * y + z * z + w * w;
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
    }

    public static double length(long x, long y, long z, long w) {
        return Math.sqrt(x * x + y * y + z * z + w * w);
    }

    public double distance(Vector4Lc v) {
        return this.distance(v.x(), v.y(), v.z(), v.w());
    }

    public double distance(Vector4ic v) {
        return this.distance(v.x(), v.y(), v.z(), v.w());
    }

    public double distance(long x, long y, long z, long w) {
        long dx = this.x - x;
        long dy = this.y - y;
        long dz = this.z - z;
        long dw = this.w - w;
        return Math.sqrt(Math.fma(dx, dx, Math.fma(dy, dy, Math.fma(dz, dz, dw * dw))));
    }

    public long gridDistance(Vector4Lc v) {
        return Math.abs(v.x() - this.x()) + Math.abs(v.y() - this.y()) + Math.abs(v.z() - this.z()) + Math.abs(v.w() - this.w());
    }

    public long gridDistance(Vector4ic v) {
        return Math.abs((long)v.x() - this.x()) + Math.abs((long)v.y() - this.y()) + Math.abs((long)v.z() - this.z()) + Math.abs((long)v.w() - this.w());
    }

    public long gridDistance(long x, long y, long z, long w) {
        return Math.abs(x - this.x()) + Math.abs(y - this.y()) + Math.abs(z - this.z()) + Math.abs(w - this.w());
    }

    public long distanceSquared(Vector4Lc v) {
        return this.distanceSquared(v.x(), v.y(), v.z(), v.w());
    }

    public long distanceSquared(Vector4ic v) {
        return this.distanceSquared(v.x(), v.y(), v.z(), v.w());
    }

    public long distanceSquared(long x, long y, long z, long w) {
        long dx = this.x - x;
        long dy = this.y - y;
        long dz = this.z - z;
        long dw = this.w - w;
        return dx * dx + dy * dy + dz * dz + dw * dw;
    }

    public static double distance(long x1, long y1, long z1, long w1, long x2, long y2, long z2, long w2) {
        long dx = x1 - x2;
        long dy = y1 - y2;
        long dz = z1 - z2;
        long dw = w1 - w2;
        return Math.sqrt(dx * dx + dy * dy + dz * dz + dw * dw);
    }

    public static long distanceSquared(long x1, long y1, long z1, long w1, long x2, long y2, long z2, long w2) {
        long dx = x1 - x2;
        long dy = y1 - y2;
        long dz = z1 - z2;
        long dw = w1 - w2;
        return dx * dx + dy * dy + dz * dz + dw * dw;
    }

    public long dot(Vector4Lc v) {
        return this.x * v.x() + this.y * v.y() + this.z * v.z() + this.w * v.w();
    }

    public long dot(Vector4ic v) {
        return this.x * (long)v.x() + this.y * (long)v.y() + this.z * (long)v.z() + this.w * (long)v.w();
    }

    public Vector4L zero() {
        this.x = 0L;
        this.y = 0L;
        this.z = 0L;
        this.w = 0L;
        return this;
    }

    public Vector4L negate() {
        return this.negate(this);
    }

    public Vector4L negate(Vector4L dest) {
        dest.x = -this.x;
        dest.y = -this.y;
        dest.z = -this.z;
        dest.w = -this.w;
        return dest;
    }

    public String toString() {
        return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
    }

    public String toString(NumberFormat formatter) {
        return "(" + formatter.format(this.x) + " " + formatter.format(this.y) + " " + formatter.format(this.z) + " " + formatter.format(this.w) + ")";
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(this.x);
        out.writeLong(this.y);
        out.writeLong(this.z);
        out.writeLong(this.w);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.x = in.readLong();
        this.y = in.readLong();
        this.z = in.readLong();
        this.w = in.readLong();
    }

    public Vector4L min(Vector4Lc v) {
        return this.min(v, this);
    }

    public Vector4L min(Vector4Lc v, Vector4L dest) {
        dest.x = this.x < v.x() ? this.x : v.x();
        dest.y = this.y < v.y() ? this.y : v.y();
        dest.z = this.z < v.z() ? this.z : v.z();
        dest.w = this.w < v.w() ? this.w : v.w();
        return dest;
    }

    public Vector4L max(Vector4Lc v) {
        return this.max(v, this);
    }

    public Vector4L max(Vector4Lc v, Vector4L dest) {
        dest.x = this.x > v.x() ? this.x : v.x();
        dest.y = this.y > v.y() ? this.y : v.y();
        dest.z = this.z > v.z() ? this.z : v.z();
        dest.w = this.w > v.w() ? this.w : v.w();
        return dest;
    }

    public Vector4L absolute() {
        return this.absolute(this);
    }

    public Vector4L absolute(Vector4L dest) {
        dest.x = Math.abs(this.x);
        dest.y = Math.abs(this.y);
        dest.z = Math.abs(this.z);
        dest.w = Math.abs(this.w);
        return dest;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (int)(this.x ^ this.x >>> 32);
        result = 31 * result + (int)(this.y ^ this.y >>> 32);
        result = 31 * result + (int)(this.z ^ this.z >>> 32);
        result = 31 * result + (int)(this.w ^ this.w >>> 32);
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
        Vector4L other = (Vector4L)obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        if (this.z != other.z) {
            return false;
        }
        return this.w == other.w;
    }

    public boolean equals(long x, long y, long z, long w) {
        if (this.x != x) {
            return false;
        }
        if (this.y != y) {
            return false;
        }
        if (this.z != z) {
            return false;
        }
        return this.w == w;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

