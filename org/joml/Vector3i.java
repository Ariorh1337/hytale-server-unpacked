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
import org.joml.Vector2dc;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector3dc;
import org.joml.Vector3fc;
import org.joml.Vector3ic;

public class Vector3i
implements Externalizable,
Cloneable,
Vector3ic {
    private static final long serialVersionUID = 1L;
    public int x;
    public int y;
    public int z;

    public Vector3i() {
    }

    public Vector3i(int d) {
        this.x = d;
        this.y = d;
        this.z = d;
    }

    public Vector3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3i(Vector3ic v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
    }

    public Vector3i(Vector2ic v, int z) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
    }

    public Vector3i(float x, float y, float z, int mode) {
        this.x = Math.roundUsing(x, mode);
        this.y = Math.roundUsing(y, mode);
        this.z = Math.roundUsing(z, mode);
    }

    public Vector3i(double x, double y, double z, int mode) {
        this.x = Math.roundUsing(x, mode);
        this.y = Math.roundUsing(y, mode);
        this.z = Math.roundUsing(z, mode);
    }

    public Vector3i(Vector2fc v, float z, int mode) {
        this.x = Math.roundUsing(v.x(), mode);
        this.y = Math.roundUsing(v.y(), mode);
        this.z = Math.roundUsing(z, mode);
    }

    public Vector3i(Vector3fc v, int mode) {
        this.x = Math.roundUsing(v.x(), mode);
        this.y = Math.roundUsing(v.y(), mode);
        this.z = Math.roundUsing(v.z(), mode);
    }

    public Vector3i(Vector2dc v, float z, int mode) {
        this.x = Math.roundUsing(v.x(), mode);
        this.y = Math.roundUsing(v.y(), mode);
        this.z = Math.roundUsing(z, mode);
    }

    public Vector3i(Vector3dc v, int mode) {
        this.x = Math.roundUsing(v.x(), mode);
        this.y = Math.roundUsing(v.y(), mode);
        this.z = Math.roundUsing(v.z(), mode);
    }

    public Vector3i(int[] xyz) {
        this.x = xyz[0];
        this.y = xyz[1];
        this.z = xyz[2];
    }

    public Vector3i(ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
    }

    public Vector3i(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
    }

    public Vector3i(IntBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
    }

    public Vector3i(int index, IntBuffer buffer) {
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

    public Vector3i set(Vector3ic v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        return this;
    }

    public Vector3i set(Vector3dc v) {
        this.x = (int)v.x();
        this.y = (int)v.y();
        this.z = (int)v.z();
        return this;
    }

    public Vector3i set(Vector3dc v, int mode) {
        this.x = Math.roundUsing(v.x(), mode);
        this.y = Math.roundUsing(v.y(), mode);
        this.z = Math.roundUsing(v.z(), mode);
        return this;
    }

    public Vector3i set(Vector3fc v, int mode) {
        this.x = Math.roundUsing(v.x(), mode);
        this.y = Math.roundUsing(v.y(), mode);
        this.z = Math.roundUsing(v.z(), mode);
        return this;
    }

    public Vector3i set(Vector2ic v, int z) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
        return this;
    }

    public Vector3i set(int d) {
        this.x = d;
        this.y = d;
        this.z = d;
        return this;
    }

    public Vector3i set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vector3i set(int[] xyz) {
        this.x = xyz[0];
        this.y = xyz[1];
        this.z = xyz[2];
        return this;
    }

    public Vector3i set(ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
        return this;
    }

    public Vector3i set(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
        return this;
    }

    public Vector3i set(IntBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
        return this;
    }

    public Vector3i set(int index, IntBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
        return this;
    }

    public Vector3i setFromAddress(long address) {
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
        }
        throw new IllegalArgumentException();
    }

    public Vector3i setComponent(int component, int value) throws IllegalArgumentException {
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

    public Vector3ic getToAddress(long address) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        }
        MemUtil.MemUtilUnsafe.put(this, address);
        return this;
    }

    public Vector3i sub(Vector3ic v) {
        return this.sub(v, this);
    }

    public Vector3i sub(Vector3ic v, Vector3i dest) {
        dest.x = this.x - v.x();
        dest.y = this.y - v.y();
        dest.z = this.z - v.z();
        return dest;
    }

    public Vector3i sub(int x, int y, int z) {
        return this.sub(x, y, z, this);
    }

    public Vector3i sub(int x, int y, int z, Vector3i dest) {
        dest.x = this.x - x;
        dest.y = this.y - y;
        dest.z = this.z - z;
        return dest;
    }

    public Vector3i add(Vector3ic v) {
        return this.add(v, this);
    }

    public Vector3i add(Vector3ic v, Vector3i dest) {
        dest.x = this.x + v.x();
        dest.y = this.y + v.y();
        dest.z = this.z + v.z();
        return dest;
    }

    public Vector3i add(int x, int y, int z) {
        return this.add(x, y, z, this);
    }

    public Vector3i add(int x, int y, int z, Vector3i dest) {
        dest.x = this.x + x;
        dest.y = this.y + y;
        dest.z = this.z + z;
        return dest;
    }

    public Vector3i mul(int scalar) {
        return this.mul(scalar, this);
    }

    public Vector3i mul(int scalar, Vector3i dest) {
        dest.x = this.x * scalar;
        dest.y = this.y * scalar;
        dest.z = this.z * scalar;
        return dest;
    }

    public Vector3i mul(Vector3ic v) {
        return this.mul(v, this);
    }

    public Vector3i mul(Vector3ic v, Vector3i dest) {
        dest.x = this.x * v.x();
        dest.y = this.y * v.y();
        dest.z = this.z * v.z();
        return dest;
    }

    public Vector3i mul(int x, int y, int z) {
        return this.mul(x, y, z, this);
    }

    public Vector3i mul(int x, int y, int z, Vector3i dest) {
        dest.x = this.x * x;
        dest.y = this.y * y;
        dest.z = this.z * z;
        return dest;
    }

    public Vector3i div(float scalar) {
        return this.div(scalar, this);
    }

    public Vector3i div(float scalar, Vector3i dest) {
        float invscalar = 1.0f / scalar;
        dest.x = (int)((float)this.x * invscalar);
        dest.y = (int)((float)this.y * invscalar);
        dest.z = (int)((float)this.z * invscalar);
        return dest;
    }

    public Vector3i div(int scalar) {
        return this.div(scalar, this);
    }

    public Vector3i div(int scalar, Vector3i dest) {
        dest.x = this.x / scalar;
        dest.y = this.y / scalar;
        dest.z = this.z / scalar;
        return dest;
    }

    public long lengthSquared() {
        return (long)this.x * (long)this.x + (long)this.y * (long)this.y + (long)this.z * (long)this.z;
    }

    public static long lengthSquared(int x, int y, int z) {
        return (long)x * (long)x + (long)y * (long)y + (long)z * (long)z;
    }

    public double length() {
        return Math.sqrt((long)this.x * (long)this.x + (long)this.y * (long)this.y + (long)this.z * (long)this.z);
    }

    public static double length(int x, int y, int z) {
        return Math.sqrt((long)x * (long)x + (long)y * (long)y + (long)z * (long)z);
    }

    public double distance(Vector3ic v) {
        int dx = this.x - v.x();
        int dy = this.y - v.y();
        int dz = this.z - v.z();
        return Math.sqrt((long)dx * (long)dx + (long)dy * (long)dy + (long)dz * (long)dz);
    }

    public double distance(int x, int y, int z) {
        int dx = this.x - x;
        int dy = this.y - y;
        int dz = this.z - z;
        return Math.sqrt((long)dx * (long)dx + (long)dy * (long)dy + (long)dz * (long)dz);
    }

    public long gridDistance(Vector3ic v) {
        return Math.abs(v.x() - this.x()) + Math.abs(v.y() - this.y()) + Math.abs(v.z() - this.z());
    }

    public long gridDistance(int x, int y, int z) {
        return Math.abs(x - this.x()) + Math.abs(y - this.y()) + Math.abs(z - this.z());
    }

    public long distanceSquared(Vector3ic v) {
        int dx = this.x - v.x();
        int dy = this.y - v.y();
        int dz = this.z - v.z();
        return (long)dx * (long)dx + (long)dy * (long)dy + (long)dz * (long)dz;
    }

    public long distanceSquared(int x, int y, int z) {
        int dx = this.x - x;
        int dy = this.y - y;
        int dz = this.z - z;
        return (long)dx * (long)dx + (long)dy * (long)dy + (long)dz * (long)dz;
    }

    public static double distance(int x1, int y1, int z1, int x2, int y2, int z2) {
        return Math.sqrt(Vector3i.distanceSquared(x1, y1, z1, x2, y2, z2));
    }

    public static long distanceSquared(int x1, int y1, int z1, int x2, int y2, int z2) {
        long dx = x1 - x2;
        long dy = y1 - y2;
        long dz = z1 - z2;
        return dx * dx + dy * dy + dz * dz;
    }

    public Vector3i zero() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        return this;
    }

    public String toString() {
        return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
    }

    public String toString(NumberFormat formatter) {
        return "(" + formatter.format(this.x) + " " + formatter.format(this.y) + " " + formatter.format(this.z) + ")";
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(this.x);
        out.writeInt(this.y);
        out.writeInt(this.z);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.x = in.readInt();
        this.y = in.readInt();
        this.z = in.readInt();
    }

    public Vector3i negate() {
        return this.negate(this);
    }

    public Vector3i negate(Vector3i dest) {
        dest.x = -this.x;
        dest.y = -this.y;
        dest.z = -this.z;
        return dest;
    }

    public Vector3i min(Vector3ic v) {
        return this.min(v, this);
    }

    public Vector3i min(Vector3ic v, Vector3i dest) {
        dest.x = this.x < v.x() ? this.x : v.x();
        dest.y = this.y < v.y() ? this.y : v.y();
        dest.z = this.z < v.z() ? this.z : v.z();
        return dest;
    }

    public Vector3i max(Vector3ic v) {
        return this.max(v, this);
    }

    public Vector3i max(Vector3ic v, Vector3i dest) {
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

    public Vector3i absolute() {
        return this.absolute(this);
    }

    public Vector3i absolute(Vector3i dest) {
        dest.x = Math.abs(this.x);
        dest.y = Math.abs(this.y);
        dest.z = Math.abs(this.z);
        return dest;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.x;
        result = 31 * result + this.y;
        result = 31 * result + this.z;
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
        Vector3i other = (Vector3i)obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        return this.z == other.z;
    }

    public boolean equals(int x, int y, int z) {
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

