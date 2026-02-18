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
import org.joml.Vector2dc;
import org.joml.Vector2fc;
import org.joml.Vector2ic;

public class Vector2i
implements Externalizable,
Cloneable,
Vector2ic {
    private static final long serialVersionUID = 1L;
    public int x;
    public int y;

    public Vector2i() {
    }

    public Vector2i(int s) {
        this.x = s;
        this.y = s;
    }

    public Vector2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2i(float x, float y, int mode) {
        this.x = Math.roundUsing(x, mode);
        this.y = Math.roundUsing(y, mode);
    }

    public Vector2i(double x, double y, int mode) {
        this.x = Math.roundUsing(x, mode);
        this.y = Math.roundUsing(y, mode);
    }

    public Vector2i(Vector2ic v) {
        this.x = v.x();
        this.y = v.y();
    }

    public Vector2i(Vector2fc v, int mode) {
        this.x = Math.roundUsing(v.x(), mode);
        this.y = Math.roundUsing(v.y(), mode);
    }

    public Vector2i(Vector2dc v, int mode) {
        this.x = Math.roundUsing(v.x(), mode);
        this.y = Math.roundUsing(v.y(), mode);
    }

    public Vector2i(int[] xy) {
        this.x = xy[0];
        this.y = xy[1];
    }

    public Vector2i(ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
    }

    public Vector2i(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
    }

    public Vector2i(IntBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
    }

    public Vector2i(int index, IntBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
    }

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }

    public Vector2i set(int s) {
        this.x = s;
        this.y = s;
        return this;
    }

    public Vector2i set(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2i set(Vector2ic v) {
        this.x = v.x();
        this.y = v.y();
        return this;
    }

    public Vector2i set(Vector2dc v) {
        this.x = (int)v.x();
        this.y = (int)v.y();
        return this;
    }

    public Vector2i set(Vector2dc v, int mode) {
        this.x = Math.roundUsing(v.x(), mode);
        this.y = Math.roundUsing(v.y(), mode);
        return this;
    }

    public Vector2i set(Vector2fc v, int mode) {
        this.x = Math.roundUsing(v.x(), mode);
        this.y = Math.roundUsing(v.y(), mode);
        return this;
    }

    public Vector2i set(int[] xy) {
        this.x = xy[0];
        this.y = xy[1];
        return this;
    }

    public Vector2i set(ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
        return this;
    }

    public Vector2i set(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
        return this;
    }

    public Vector2i set(IntBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
        return this;
    }

    public Vector2i set(int index, IntBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
        return this;
    }

    public Vector2i setFromAddress(long address) {
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
        }
        throw new IllegalArgumentException();
    }

    public Vector2i setComponent(int component, int value) throws IllegalArgumentException {
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

    public IntBuffer get(IntBuffer buffer) {
        MemUtil.INSTANCE.put(this, buffer.position(), buffer);
        return buffer;
    }

    public IntBuffer get(int index, IntBuffer buffer) {
        MemUtil.INSTANCE.put(this, index, buffer);
        return buffer;
    }

    public Vector2ic getToAddress(long address) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        }
        MemUtil.MemUtilUnsafe.put(this, address);
        return this;
    }

    public Vector2i sub(Vector2ic v) {
        return this.sub(v, this);
    }

    public Vector2i sub(Vector2ic v, Vector2i dest) {
        dest.x = this.x - v.x();
        dest.y = this.y - v.y();
        return dest;
    }

    public Vector2i sub(int x, int y) {
        return this.sub(x, y, this);
    }

    public Vector2i sub(int x, int y, Vector2i dest) {
        dest.x = this.x - x;
        dest.y = this.y - y;
        return dest;
    }

    public long lengthSquared() {
        return (long)this.x * (long)this.x + (long)this.y * (long)this.y;
    }

    public static long lengthSquared(int x, int y) {
        return (long)x * (long)x + (long)y * (long)y;
    }

    public double length() {
        return Math.sqrt((long)this.x * (long)this.x + (long)this.y * (long)this.y);
    }

    public static double length(int x, int y) {
        return Math.sqrt((long)x * (long)x + (long)y * (long)y);
    }

    public double distance(Vector2ic v) {
        int dx = this.x - v.x();
        int dy = this.y - v.y();
        return Math.sqrt((long)dx * (long)dx + (long)dy * (long)dy);
    }

    public double distance(int x, int y) {
        int dx = this.x - x;
        int dy = this.y - y;
        return Math.sqrt((long)dx * (long)dx + (long)dy * (long)dy);
    }

    public long distanceSquared(Vector2ic v) {
        int dx = this.x - v.x();
        int dy = this.y - v.y();
        return (long)dx * (long)dx + (long)dy * (long)dy;
    }

    public long distanceSquared(int x, int y) {
        int dx = this.x - x;
        int dy = this.y - y;
        return (long)dx * (long)dx + (long)dy * (long)dy;
    }

    public long gridDistance(Vector2ic v) {
        return Math.abs(v.x() - this.x()) + Math.abs(v.y() - this.y());
    }

    public long gridDistance(int x, int y) {
        return Math.abs(x - this.x()) + Math.abs(y - this.y());
    }

    public static double distance(int x1, int y1, int x2, int y2) {
        int dx = x1 - x2;
        int dy = y1 - y2;
        return Math.sqrt((long)dx * (long)dx + (long)dy * (long)dy);
    }

    public static long distanceSquared(int x1, int y1, int x2, int y2) {
        int dx = x1 - x2;
        int dy = y1 - y2;
        return (long)dx * (long)dx + (long)dy * (long)dy;
    }

    public Vector2i add(Vector2ic v) {
        this.x += v.x();
        this.y += v.y();
        return this;
    }

    public Vector2i add(Vector2ic v, Vector2i dest) {
        dest.x = this.x + v.x();
        dest.y = this.y + v.y();
        return dest;
    }

    public Vector2i add(int x, int y) {
        return this.add(x, y, this);
    }

    public Vector2i add(int x, int y, Vector2i dest) {
        dest.x = this.x + x;
        dest.y = this.y + y;
        return dest;
    }

    public Vector2i mul(int scalar) {
        return this.mul(scalar, this);
    }

    public Vector2i mul(int scalar, Vector2i dest) {
        dest.x = this.x * scalar;
        dest.y = this.y * scalar;
        return dest;
    }

    public Vector2i mul(Vector2ic v) {
        return this.mul(v, this);
    }

    public Vector2i mul(Vector2ic v, Vector2i dest) {
        dest.x = this.x * v.x();
        dest.y = this.y * v.y();
        return dest;
    }

    public Vector2i mul(int x, int y) {
        return this.mul(x, y, this);
    }

    public Vector2i mul(int x, int y, Vector2i dest) {
        dest.x = this.x * x;
        dest.y = this.y * y;
        return dest;
    }

    public Vector2i div(float scalar) {
        return this.div(scalar, this);
    }

    public Vector2i div(float scalar, Vector2i dest) {
        float invscalar = 1.0f / scalar;
        dest.x = (int)((float)this.x * invscalar);
        dest.y = (int)((float)this.y * invscalar);
        return dest;
    }

    public Vector2i div(int scalar) {
        return this.div(scalar, this);
    }

    public Vector2i div(int scalar, Vector2i dest) {
        dest.x = this.x / scalar;
        dest.y = this.y / scalar;
        return dest;
    }

    public Vector2i zero() {
        this.x = 0;
        this.y = 0;
        return this;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(this.x);
        out.writeInt(this.y);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.x = in.readInt();
        this.y = in.readInt();
    }

    public Vector2i negate() {
        return this.negate(this);
    }

    public Vector2i negate(Vector2i dest) {
        dest.x = -this.x;
        dest.y = -this.y;
        return dest;
    }

    public Vector2i min(Vector2ic v) {
        return this.min(v, this);
    }

    public Vector2i min(Vector2ic v, Vector2i dest) {
        dest.x = this.x < v.x() ? this.x : v.x();
        dest.y = this.y < v.y() ? this.y : v.y();
        return dest;
    }

    public Vector2i max(Vector2ic v) {
        return this.max(v, this);
    }

    public Vector2i max(Vector2ic v, Vector2i dest) {
        dest.x = this.x > v.x() ? this.x : v.x();
        dest.y = this.y > v.y() ? this.y : v.y();
        return dest;
    }

    public int maxComponent() {
        int absY;
        int absX = Math.abs(this.x);
        if (absX >= (absY = Math.abs(this.y))) {
            return 0;
        }
        return 1;
    }

    public int minComponent() {
        int absY;
        int absX = Math.abs(this.x);
        if (absX < (absY = Math.abs(this.y))) {
            return 0;
        }
        return 1;
    }

    public Vector2i absolute() {
        return this.absolute(this);
    }

    public Vector2i absolute(Vector2i dest) {
        dest.x = Math.abs(this.x);
        dest.y = Math.abs(this.y);
        return dest;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.x;
        result = 31 * result + this.y;
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
        Vector2i other = (Vector2i)obj;
        if (this.x != other.x) {
            return false;
        }
        return this.y == other.y;
    }

    public boolean equals(int x, int y) {
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

