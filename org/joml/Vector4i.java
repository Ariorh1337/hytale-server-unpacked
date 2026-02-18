/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.NumberFormat;
import org.joml.Math;
import org.joml.MemUtil;
import org.joml.Options;
import org.joml.Runtime;
import org.joml.Vector2L;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector3L;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.joml.Vector4dc;
import org.joml.Vector4fc;
import org.joml.Vector4ic;

public class Vector4i
implements Externalizable,
Cloneable,
Vector4ic {
    private static final long serialVersionUID = 1L;
    public int x;
    public int y;
    public int z;
    public int w;

    public Vector4i() {
        this.w = 1;
    }

    public Vector4i(Vector4ic v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = v.w();
    }

    public Vector4i(Vector3ic v, int w) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = w;
    }

    public Vector4i(Vector2ic v, int z, int w) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
        this.w = w;
    }

    public Vector4i(Vector3fc v, float w, int mode) {
        this.x = Math.roundUsing(v.x(), mode);
        this.y = Math.roundUsing(v.y(), mode);
        this.z = Math.roundUsing(v.z(), mode);
        this.w = Math.roundUsing(w, mode);
    }

    public Vector4i(Vector4fc v, int mode) {
        this.x = Math.roundUsing(v.x(), mode);
        this.y = Math.roundUsing(v.y(), mode);
        this.z = Math.roundUsing(v.z(), mode);
        this.w = Math.roundUsing(v.w(), mode);
    }

    public Vector4i(Vector4dc v, int mode) {
        this.x = Math.roundUsing(v.x(), mode);
        this.y = Math.roundUsing(v.y(), mode);
        this.z = Math.roundUsing(v.z(), mode);
        this.w = Math.roundUsing(v.w(), mode);
    }

    public Vector4i(int s) {
        this.x = s;
        this.y = s;
        this.z = s;
        this.w = s;
    }

    public Vector4i(int x, int y, int z, int w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4i(int[] xyzw) {
        this.x = xyzw[0];
        this.y = xyzw[1];
        this.z = xyzw[2];
        this.w = xyzw[3];
    }

    public Vector4i(ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
    }

    public Vector4i(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
    }

    public Vector4i(IntBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
    }

    public Vector4i(int index, IntBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
    }

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }

    public int z() {
        return this.z;
    }

    public int w() {
        return this.w;
    }

    public Vector3f xyz(Vector3f dest) {
        return dest.set(this.x, this.y, this.z);
    }

    public Vector3d xyz(Vector3d dest) {
        return dest.set(this.x, this.y, this.z);
    }

    public Vector3i xyz(Vector3i dest) {
        return dest.set(this.x, this.y, this.z);
    }

    public Vector3L xyz(Vector3L dest) {
        return dest.set(this.x, this.y, this.z);
    }

    public Vector2f xy(Vector2f dest) {
        return dest.set((float)this.x, this.y);
    }

    public Vector2d xy(Vector2d dest) {
        return dest.set((double)this.x, this.y);
    }

    public Vector2i xy(Vector2i dest) {
        return dest.set(this.x, this.y);
    }

    public Vector2L xy(Vector2L dest) {
        return dest.set((long)this.x, (long)this.y);
    }

    public Vector4i set(Vector4ic v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = v.w();
        return this;
    }

    public Vector4i set(Vector4dc v) {
        this.x = (int)v.x();
        this.y = (int)v.y();
        this.z = (int)v.z();
        this.w = (int)v.w();
        return this;
    }

    public Vector4i set(Vector4dc v, int mode) {
        this.x = Math.roundUsing(v.x(), mode);
        this.y = Math.roundUsing(v.y(), mode);
        this.z = Math.roundUsing(v.z(), mode);
        this.w = Math.roundUsing(v.w(), mode);
        return this;
    }

    public Vector4i set(Vector4fc v, int mode) {
        this.x = Math.roundUsing(v.x(), mode);
        this.y = Math.roundUsing(v.y(), mode);
        this.z = Math.roundUsing(v.z(), mode);
        this.w = Math.roundUsing(v.w(), mode);
        return this;
    }

    public Vector4i set(Vector3ic v, int w) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = w;
        return this;
    }

    public Vector4i set(Vector2ic v, int z, int w) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
        this.w = w;
        return this;
    }

    public Vector4i set(int s) {
        this.x = s;
        this.y = s;
        this.z = s;
        this.w = s;
        return this;
    }

    public Vector4i set(int x, int y, int z, int w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    public Vector4i set(int[] xyzw) {
        this.x = xyzw[0];
        this.y = xyzw[1];
        this.z = xyzw[2];
        this.w = xyzw[3];
        return this;
    }

    public Vector4i set(ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
        return this;
    }

    public Vector4i set(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
        return this;
    }

    public Vector4i set(IntBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
        return this;
    }

    public Vector4i set(int index, IntBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
        return this;
    }

    public Vector4i setFromAddress(long address) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        }
        MemUtil.MemUtilUnsafe.get(this, address);
        return this;
    }

    public int get(int component) throws IllegalArgumentException {
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
        int absX = Math.abs(this.x);
        int absY = Math.abs(this.y);
        int absZ = Math.abs(this.z);
        int absW = Math.abs(this.w);
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
        int absX = Math.abs(this.x);
        int absY = Math.abs(this.y);
        int absZ = Math.abs(this.z);
        int absW = Math.abs(this.w);
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

    public Vector4i setComponent(int component, int value) throws IllegalArgumentException {
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

    public IntBuffer get(IntBuffer buffer) {
        MemUtil.INSTANCE.put(this, buffer.position(), buffer);
        return buffer;
    }

    public IntBuffer get(int index, IntBuffer buffer) {
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

    public Vector4ic getToAddress(long address) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        }
        MemUtil.MemUtilUnsafe.put(this, address);
        return this;
    }

    public Vector4i sub(Vector4ic v) {
        return this.sub(v, this);
    }

    public Vector4i sub(int x, int y, int z, int w) {
        return this.sub(x, y, z, w, this);
    }

    public Vector4i sub(Vector4ic v, Vector4i dest) {
        dest.x = this.x - v.x();
        dest.y = this.y - v.y();
        dest.z = this.z - v.z();
        dest.w = this.w - v.w();
        return dest;
    }

    public Vector4i sub(int x, int y, int z, int w, Vector4i dest) {
        dest.x = this.x - x;
        dest.y = this.y - y;
        dest.z = this.z - z;
        dest.w = this.w - w;
        return dest;
    }

    public Vector4i add(Vector4ic v) {
        return this.add(v, this);
    }

    public Vector4i add(Vector4ic v, Vector4i dest) {
        dest.x = this.x + v.x();
        dest.y = this.y + v.y();
        dest.z = this.z + v.z();
        dest.w = this.w + v.w();
        return dest;
    }

    public Vector4i add(int x, int y, int z, int w) {
        return this.add(x, y, z, w, this);
    }

    public Vector4i add(int x, int y, int z, int w, Vector4i dest) {
        dest.x = this.x + x;
        dest.y = this.y + y;
        dest.z = this.z + z;
        dest.w = this.w + w;
        return dest;
    }

    public Vector4i mul(Vector4ic v) {
        return this.mul(v, this);
    }

    public Vector4i mul(Vector4ic v, Vector4i dest) {
        dest.x = this.x * v.x();
        dest.y = this.y * v.y();
        dest.z = this.z * v.z();
        dest.w = this.w * v.w();
        return dest;
    }

    public Vector4i div(Vector4ic v) {
        return this.div(v, this);
    }

    public Vector4i div(Vector4ic v, Vector4i dest) {
        dest.x = this.x / v.x();
        dest.y = this.y / v.y();
        dest.z = this.z / v.z();
        dest.w = this.w / v.w();
        return dest;
    }

    public Vector4i mul(int scalar) {
        return this.mul(scalar, this);
    }

    public Vector4i mul(int scalar, Vector4i dest) {
        dest.x = this.x * scalar;
        dest.y = this.y * scalar;
        dest.z = this.z * scalar;
        dest.w = this.w * scalar;
        return dest;
    }

    public Vector4i div(float scalar) {
        return this.div(scalar, this);
    }

    public Vector4i div(float scalar, Vector4i dest) {
        float invscalar = 1.0f / scalar;
        dest.x = (int)((float)this.x * invscalar);
        dest.y = (int)((float)this.y * invscalar);
        dest.z = (int)((float)this.z * invscalar);
        dest.w = (int)((float)this.w * invscalar);
        return dest;
    }

    public Vector4i div(int scalar) {
        return this.div(scalar, this);
    }

    public Vector4i div(int scalar, Vector4i dest) {
        dest.x = this.x / scalar;
        dest.y = this.y / scalar;
        dest.z = this.z / scalar;
        dest.w = this.w / scalar;
        return dest;
    }

    public long lengthSquared() {
        return (long)this.x * (long)this.x + (long)this.y * (long)this.y + (long)this.z * (long)this.z + (long)this.w * (long)this.w;
    }

    public static long lengthSquared(int x, int y, int z, int w) {
        return (long)x * (long)x + (long)y * (long)y + (long)z * (long)z + (long)w * (long)w;
    }

    public double length() {
        return Math.sqrt((long)this.x * (long)this.x + (long)this.y * (long)this.y + (long)this.z * (long)this.z + (long)this.w * (long)this.w);
    }

    public static double length(int x, int y, int z, int w) {
        return Math.sqrt((long)x * (long)x + (long)y * (long)y + (long)z * (long)z + (long)w * (long)w);
    }

    public double distance(Vector4ic v) {
        return this.distance(v.x(), v.y(), v.z(), v.w());
    }

    public double distance(int x, int y, int z, int w) {
        int dx = this.x - x;
        int dy = this.y - y;
        int dz = this.z - z;
        int dw = this.w - w;
        return Math.sqrt(Math.fma(dx, dx, Math.fma(dy, dy, Math.fma(dz, dz, dw * dw))));
    }

    public long gridDistance(Vector4ic v) {
        return Math.abs(v.x() - this.x()) + Math.abs(v.y() - this.y()) + Math.abs(v.z() - this.z()) + Math.abs(v.w() - this.w());
    }

    public long gridDistance(int x, int y, int z, int w) {
        return Math.abs(x - this.x()) + Math.abs(y - this.y()) + Math.abs(z - this.z()) + Math.abs(w - this.w());
    }

    public long distanceSquared(Vector4ic v) {
        return this.distanceSquared(v.x(), v.y(), v.z(), v.w());
    }

    public long distanceSquared(int x, int y, int z, int w) {
        int dx = this.x - x;
        int dy = this.y - y;
        int dz = this.z - z;
        int dw = this.w - w;
        return (long)dx * (long)dx + (long)dy * (long)dy + (long)dz * (long)dz + (long)dw * (long)dw;
    }

    public static double distance(int x1, int y1, int z1, int w1, int x2, int y2, int z2, int w2) {
        int dx = x1 - x2;
        int dy = y1 - y2;
        int dz = z1 - z2;
        int dw = w1 - w2;
        return Math.sqrt((long)dx * (long)dx + (long)dy * (long)dy + (long)dz * (long)dz + (long)dw * (long)dw);
    }

    public static long distanceSquared(int x1, int y1, int z1, int w1, int x2, int y2, int z2, int w2) {
        int dx = x1 - x2;
        int dy = y1 - y2;
        int dz = z1 - z2;
        int dw = w1 - w2;
        return (long)dx * (long)dx + (long)dy * (long)dy + (long)dz * (long)dz + (long)dw * (long)dw;
    }

    public long dot(Vector4ic v) {
        return (long)this.x * (long)v.x() + (long)this.y * (long)v.y() + (long)this.z * (long)v.z() + (long)this.w * (long)v.w();
    }

    public Vector4i zero() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 0;
        return this;
    }

    public Vector4i negate() {
        return this.negate(this);
    }

    public Vector4i negate(Vector4i dest) {
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
        out.writeInt(this.x);
        out.writeInt(this.y);
        out.writeInt(this.z);
        out.writeInt(this.w);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.x = in.readInt();
        this.y = in.readInt();
        this.z = in.readInt();
        this.w = in.readInt();
    }

    public Vector4i min(Vector4ic v) {
        return this.min(v, this);
    }

    public Vector4i min(Vector4ic v, Vector4i dest) {
        dest.x = this.x < v.x() ? this.x : v.x();
        dest.y = this.y < v.y() ? this.y : v.y();
        dest.z = this.z < v.z() ? this.z : v.z();
        dest.w = this.w < v.w() ? this.w : v.w();
        return dest;
    }

    public Vector4i max(Vector4ic v) {
        return this.max(v, this);
    }

    public Vector4i max(Vector4ic v, Vector4i dest) {
        dest.x = this.x > v.x() ? this.x : v.x();
        dest.y = this.y > v.y() ? this.y : v.y();
        dest.z = this.z > v.z() ? this.z : v.z();
        dest.w = this.w > v.w() ? this.w : v.w();
        return dest;
    }

    public Vector4i absolute() {
        return this.absolute(this);
    }

    public Vector4i absolute(Vector4i dest) {
        dest.x = Math.abs(this.x);
        dest.y = Math.abs(this.y);
        dest.z = Math.abs(this.z);
        dest.w = Math.abs(this.w);
        return dest;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.x;
        result = 31 * result + this.y;
        result = 31 * result + this.z;
        result = 31 * result + this.w;
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
        Vector4i other = (Vector4i)obj;
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

    public boolean equals(int x, int y, int z, int w) {
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

