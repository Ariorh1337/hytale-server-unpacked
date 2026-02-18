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
import org.joml.Vector2Lc;
import org.joml.Vector2dc;
import org.joml.Vector2fc;
import org.joml.Vector2ic;

public class Vector2L
implements Externalizable,
Cloneable,
Vector2Lc {
    private static final long serialVersionUID = 1L;
    public long x;
    public long y;

    public Vector2L() {
    }

    public Vector2L(long s) {
        this.x = s;
        this.y = s;
    }

    public Vector2L(long x, long y) {
        this.x = x;
        this.y = y;
    }

    public Vector2L(float x, float y, int mode) {
        this.x = Math.roundLongUsing(x, mode);
        this.y = Math.roundLongUsing(y, mode);
    }

    public Vector2L(double x, double y, int mode) {
        this.x = Math.roundLongUsing(x, mode);
        this.y = Math.roundLongUsing(y, mode);
    }

    public Vector2L(Vector2Lc v) {
        this.x = v.x();
        this.y = v.y();
    }

    public Vector2L(Vector2ic v) {
        this.x = v.x();
        this.y = v.y();
    }

    public Vector2L(Vector2fc v, int mode) {
        this.x = Math.roundLongUsing(v.x(), mode);
        this.y = Math.roundLongUsing(v.y(), mode);
    }

    public Vector2L(Vector2dc v, int mode) {
        this.x = Math.roundLongUsing(v.x(), mode);
        this.y = Math.roundLongUsing(v.y(), mode);
    }

    public Vector2L(long[] xy) {
        this.x = xy[0];
        this.y = xy[1];
    }

    public Vector2L(ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
    }

    public Vector2L(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
    }

    public Vector2L(LongBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
    }

    public Vector2L(int index, LongBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
    }

    public long x() {
        return this.x;
    }

    public long y() {
        return this.y;
    }

    public Vector2L set(long s) {
        this.x = s;
        this.y = s;
        return this;
    }

    public Vector2L set(long x, long y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2L set(Vector2Lc v) {
        this.x = v.x();
        this.y = v.y();
        return this;
    }

    public Vector2L set(Vector2ic v) {
        this.x = v.x();
        this.y = v.y();
        return this;
    }

    public Vector2L set(Vector2dc v) {
        this.x = (long)v.x();
        this.y = (long)v.y();
        return this;
    }

    public Vector2L set(Vector2dc v, int mode) {
        this.x = Math.roundLongUsing(v.x(), mode);
        this.y = Math.roundLongUsing(v.y(), mode);
        return this;
    }

    public Vector2L set(Vector2fc v, int mode) {
        this.x = Math.roundLongUsing(v.x(), mode);
        this.y = Math.roundLongUsing(v.y(), mode);
        return this;
    }

    public Vector2L set(long[] xy) {
        this.x = xy[0];
        this.y = xy[1];
        return this;
    }

    public Vector2L set(ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
        return this;
    }

    public Vector2L set(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
        return this;
    }

    public Vector2L set(LongBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
        return this;
    }

    public Vector2L set(int index, LongBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
        return this;
    }

    public Vector2L setFromAddress(long address) {
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
        }
        throw new IllegalArgumentException();
    }

    public Vector2L setComponent(int component, long value) throws IllegalArgumentException {
        switch (component) {
            case 0: {
                this.x = value;
                break;
            }
            case 1: {
                this.y = value;
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
        return this;
    }

    public ByteBuffer get(ByteBuffer buffer) {
        MemUtil.INSTANCE.put(this, buffer.position(), buffer);
        return buffer;
    }

    public ByteBuffer get(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.put(this, index, buffer);
        return buffer;
    }

    public LongBuffer get(LongBuffer buffer) {
        MemUtil.INSTANCE.put(this, buffer.position(), buffer);
        return buffer;
    }

    public LongBuffer get(int index, LongBuffer buffer) {
        MemUtil.INSTANCE.put(this, index, buffer);
        return buffer;
    }

    public Vector2Lc getToAddress(long address) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        }
        MemUtil.MemUtilUnsafe.put(this, address);
        return this;
    }

    public Vector2L sub(Vector2Lc v) {
        return this.sub(v, this);
    }

    public Vector2L sub(Vector2Lc v, Vector2L dest) {
        dest.x = this.x - v.x();
        dest.y = this.y - v.y();
        return dest;
    }

    public Vector2L sub(Vector2ic v) {
        return this.sub(v, this);
    }

    public Vector2L sub(Vector2ic v, Vector2L dest) {
        dest.x = this.x - (long)v.x();
        dest.y = this.y - (long)v.y();
        return dest;
    }

    public Vector2L sub(long x, long y) {
        return this.sub(x, y, this);
    }

    public Vector2L sub(long x, long y, Vector2L dest) {
        dest.x = this.x - x;
        dest.y = this.y - y;
        return dest;
    }

    public long lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }

    public static long lengthSquared(long x, long y) {
        return x * x + y * y;
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public static double length(long x, long y) {
        return Math.sqrt(x * x + y * y);
    }

    public double distance(Vector2Lc v) {
        long dx = this.x - v.x();
        long dy = this.y - v.y();
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double distance(long x, long y) {
        long dx = this.x - x;
        long dy = this.y - y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public long distanceSquared(Vector2Lc v) {
        long dx = this.x - v.x();
        long dy = this.y - v.y();
        return dx * dx + dy * dy;
    }

    public long distanceSquared(long x, long y) {
        long dx = this.x - x;
        long dy = this.y - y;
        return dx * dx + dy * dy;
    }

    public long gridDistance(Vector2Lc v) {
        return Math.abs(v.x() - this.x()) + Math.abs(v.y() - this.y());
    }

    public long gridDistance(long x, long y) {
        return Math.abs(x - this.x()) + Math.abs(y - this.y());
    }

    public static double distance(long x1, long y1, long x2, long y2) {
        long dx = x1 - x2;
        long dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static long distanceSquared(long x1, long y1, long x2, long y2) {
        long dx = x1 - x2;
        long dy = y1 - y2;
        return dx * dx + dy * dy;
    }

    public Vector2L add(Vector2Lc v) {
        this.x += v.x();
        this.y += v.y();
        return this;
    }

    public Vector2L add(Vector2Lc v, Vector2L dest) {
        dest.x = this.x + v.x();
        dest.y = this.y + v.y();
        return dest;
    }

    public Vector2L add(Vector2ic v) {
        this.x += (long)v.x();
        this.y += (long)v.y();
        return this;
    }

    public Vector2L add(Vector2ic v, Vector2L dest) {
        dest.x = this.x + (long)v.x();
        dest.y = this.y + (long)v.y();
        return dest;
    }

    public Vector2L add(long x, long y) {
        return this.add(x, y, this);
    }

    public Vector2L add(long x, long y, Vector2L dest) {
        dest.x = this.x + x;
        dest.y = this.y + y;
        return dest;
    }

    public Vector2L mul(long scalar) {
        return this.mul(scalar, this);
    }

    public Vector2L mul(long scalar, Vector2L dest) {
        dest.x = this.x * scalar;
        dest.y = this.y * scalar;
        return dest;
    }

    public Vector2L mul(Vector2Lc v) {
        return this.mul(v, this);
    }

    public Vector2L mul(Vector2Lc v, Vector2L dest) {
        dest.x = this.x * v.x();
        dest.y = this.y * v.y();
        return dest;
    }

    public Vector2L mul(Vector2ic v) {
        return this.mul(v, this);
    }

    public Vector2L mul(Vector2ic v, Vector2L dest) {
        dest.x = this.x * (long)v.x();
        dest.y = this.y * (long)v.y();
        return dest;
    }

    public Vector2L mul(long x, long y) {
        return this.mul(x, y, this);
    }

    public Vector2L mul(long x, long y, Vector2L dest) {
        dest.x = this.x * x;
        dest.y = this.y * y;
        return dest;
    }

    public Vector2L div(float scalar) {
        return this.div(scalar, this);
    }

    public Vector2L div(float scalar, Vector2L dest) {
        float invscalar = 1.0f / scalar;
        dest.x = (int)((float)this.x * invscalar);
        dest.y = (int)((float)this.y * invscalar);
        return dest;
    }

    public Vector2L div(long scalar) {
        return this.div(scalar, this);
    }

    public Vector2L div(long scalar, Vector2L dest) {
        dest.x = this.x / scalar;
        dest.y = this.y / scalar;
        return dest;
    }

    public Vector2L zero() {
        this.x = 0L;
        this.y = 0L;
        return this;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(this.x);
        out.writeLong(this.y);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.x = in.readLong();
        this.y = in.readLong();
    }

    public Vector2L negate() {
        return this.negate(this);
    }

    public Vector2L negate(Vector2L dest) {
        dest.x = -this.x;
        dest.y = -this.y;
        return dest;
    }

    public Vector2L min(Vector2Lc v) {
        return this.min(v, this);
    }

    public Vector2L min(Vector2Lc v, Vector2L dest) {
        dest.x = this.x < v.x() ? this.x : v.x();
        dest.y = this.y < v.y() ? this.y : v.y();
        return dest;
    }

    public Vector2L max(Vector2Lc v) {
        return this.max(v, this);
    }

    public Vector2L max(Vector2Lc v, Vector2L dest) {
        dest.x = this.x > v.x() ? this.x : v.x();
        dest.y = this.y > v.y() ? this.y : v.y();
        return dest;
    }

    public long maxComponent() {
        long absY;
        long absX = Math.abs(this.x);
        if (absX >= (absY = Math.abs(this.y))) {
            return 0L;
        }
        return 1L;
    }

    public long minComponent() {
        long absY;
        long absX = Math.abs(this.x);
        if (absX < (absY = Math.abs(this.y))) {
            return 0L;
        }
        return 1L;
    }

    public Vector2L absolute() {
        return this.absolute(this);
    }

    public Vector2L absolute(Vector2L dest) {
        dest.x = Math.abs(this.x);
        dest.y = Math.abs(this.y);
        return dest;
    }

    public int hashCode() {
        long prime = 31L;
        long result = 1L;
        result = 31L * result + this.x;
        result = 31L * result + this.y;
        return (int)(result ^ result >> 32);
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
        Vector2L other = (Vector2L)obj;
        if (this.x != other.x) {
            return false;
        }
        return this.y == other.y;
    }

    public boolean equals(long x, long y) {
        if (this.x != x) {
            return false;
        }
        return this.y == y;
    }

    public String toString() {
        return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
    }

    public String toString(NumberFormat formatter) {
        return "(" + formatter.format(this.x) + " " + formatter.format(this.y) + ")";
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

