/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.text.NumberFormat;
import org.joml.Math;
import org.joml.Matrix4dc;
import org.joml.Matrix4fc;
import org.joml.Matrix4x3dc;
import org.joml.Matrix4x3fc;
import org.joml.MemUtil;
import org.joml.Options;
import org.joml.Quaterniondc;
import org.joml.Runtime;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2ic;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3ic;
import org.joml.Vector4dc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.joml.Vector4i;
import org.joml.Vector4ic;

public class Vector4d
implements Externalizable,
Cloneable,
Vector4dc {
    private static final long serialVersionUID = 1L;
    public double x;
    public double y;
    public double z;
    public double w;

    public Vector4d() {
        this.w = 1.0;
    }

    public Vector4d(Vector4dc v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = v.w();
    }

    public Vector4d(Vector4ic v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = v.w();
    }

    public Vector4d(Vector3dc v, double w) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = w;
    }

    public Vector4d(Vector3ic v, double w) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = w;
    }

    public Vector4d(Vector2dc v, double z, double w) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
        this.w = w;
    }

    public Vector4d(Vector2ic v, double z, double w) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
        this.w = w;
    }

    public Vector4d(double d) {
        this.x = d;
        this.y = d;
        this.z = d;
        this.w = d;
    }

    public Vector4d(Vector4fc v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = v.w();
    }

    public Vector4d(Vector3fc v, double w) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = w;
    }

    public Vector4d(Vector2fc v, double z, double w) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
        this.w = w;
    }

    public Vector4d(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4d(float[] xyzw) {
        this.x = xyzw[0];
        this.y = xyzw[1];
        this.z = xyzw[2];
        this.w = xyzw[3];
    }

    public Vector4d(double[] xyzw) {
        this.x = xyzw[0];
        this.y = xyzw[1];
        this.z = xyzw[2];
        this.w = xyzw[3];
    }

    public Vector4d(ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
    }

    public Vector4d(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
    }

    public Vector4d(DoubleBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
    }

    public Vector4d(int index, DoubleBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
    }

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }

    public double z() {
        return this.z;
    }

    public double w() {
        return this.w;
    }

    public Vector3f xyz(Vector3f dest) {
        return dest.set(this.x, this.y, this.z);
    }

    public Vector3d xyz(Vector3d dest) {
        return dest.set(this.x, this.y, this.z);
    }

    public Vector2f xy(Vector2f dest) {
        return dest.set(this.x, this.y);
    }

    public Vector2d xy(Vector2d dest) {
        return dest.set(this.x, this.y);
    }

    public Vector4d set(Vector4dc v) {
        if (v == this) {
            return this;
        }
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = v.w();
        return this;
    }

    public Vector4d set(Vector4fc v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = v.w();
        return this;
    }

    public Vector4d set(Vector4ic v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = v.w();
        return this;
    }

    public Vector4d set(Vector3dc v, double w) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = w;
        return this;
    }

    public Vector4d set(Vector3ic v, double w) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = w;
        return this;
    }

    public Vector4d set(Vector3fc v, double w) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = w;
        return this;
    }

    public Vector4d set(Vector2dc v, double z, double w) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
        this.w = w;
        return this;
    }

    public Vector4d set(Vector2ic v, double z, double w) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
        this.w = w;
        return this;
    }

    public Vector4d set(double d) {
        this.x = d;
        this.y = d;
        this.z = d;
        this.w = d;
        return this;
    }

    public Vector4d set(Vector2fc v, double z, double w) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
        this.w = w;
        return this;
    }

    public Vector4d set(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    public Vector4d set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vector4d set(double[] xyzw) {
        this.x = xyzw[0];
        this.y = xyzw[1];
        this.z = xyzw[2];
        this.w = xyzw[3];
        return this;
    }

    public Vector4d set(float[] xyzw) {
        this.x = xyzw[0];
        this.y = xyzw[1];
        this.z = xyzw[2];
        this.w = xyzw[3];
        return this;
    }

    public Vector4d set(ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
        return this;
    }

    public Vector4d set(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
        return this;
    }

    public Vector4d set(DoubleBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
        return this;
    }

    public Vector4d set(int index, DoubleBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
        return this;
    }

    public Vector4d setFromAddress(long address) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        }
        MemUtil.MemUtilUnsafe.get(this, address);
        return this;
    }

    public Vector4d setComponent(int component, double value) throws IllegalArgumentException {
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

    public ByteBuffer get(ByteBuffer buffer) {
        MemUtil.INSTANCE.put(this, buffer.position(), buffer);
        return buffer;
    }

    public ByteBuffer get(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.put(this, index, buffer);
        return buffer;
    }

    public DoubleBuffer get(DoubleBuffer buffer) {
        MemUtil.INSTANCE.put(this, buffer.position(), buffer);
        return buffer;
    }

    public DoubleBuffer get(int index, DoubleBuffer buffer) {
        MemUtil.INSTANCE.put(this, index, buffer);
        return buffer;
    }

    public ByteBuffer getf(ByteBuffer buffer) {
        MemUtil.INSTANCE.putf(this, buffer.position(), buffer);
        return buffer;
    }

    public ByteBuffer getf(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.putf(this, index, buffer);
        return buffer;
    }

    public FloatBuffer get(FloatBuffer buffer) {
        MemUtil.INSTANCE.put(this, buffer.position(), buffer);
        return buffer;
    }

    public FloatBuffer get(int index, FloatBuffer buffer) {
        MemUtil.INSTANCE.put(this, index, buffer);
        return buffer;
    }

    public Vector4dc getToAddress(long address) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        }
        MemUtil.MemUtilUnsafe.put(this, address);
        return this;
    }

    public Vector4d sub(Vector4dc v) {
        return this.sub(v, this);
    }

    public Vector4d sub(Vector4dc v, Vector4d dest) {
        dest.x = this.x - v.x();
        dest.y = this.y - v.y();
        dest.z = this.z - v.z();
        dest.w = this.w - v.w();
        return dest;
    }

    public Vector4d sub(Vector4fc v) {
        return this.sub(v, this);
    }

    public Vector4d sub(Vector4fc v, Vector4d dest) {
        dest.x = this.x - (double)v.x();
        dest.y = this.y - (double)v.y();
        dest.z = this.z - (double)v.z();
        dest.w = this.w - (double)v.w();
        return dest;
    }

    public Vector4d sub(double x, double y, double z, double w) {
        return this.sub(x, y, z, w, this);
    }

    public Vector4d sub(double x, double y, double z, double w, Vector4d dest) {
        dest.x = this.x - x;
        dest.y = this.y - y;
        dest.z = this.z - z;
        dest.w = this.w - w;
        return dest;
    }

    public Vector4d add(Vector4dc v) {
        return this.add(v, this);
    }

    public Vector4d add(Vector4dc v, Vector4d dest) {
        dest.x = this.x + v.x();
        dest.y = this.y + v.y();
        dest.z = this.z + v.z();
        dest.w = this.w + v.w();
        return dest;
    }

    public Vector4d add(Vector4fc v, Vector4d dest) {
        dest.x = this.x + (double)v.x();
        dest.y = this.y + (double)v.y();
        dest.z = this.z + (double)v.z();
        dest.w = this.w + (double)v.w();
        return dest;
    }

    public Vector4d add(double x, double y, double z, double w) {
        return this.add(x, y, z, w, this);
    }

    public Vector4d add(double x, double y, double z, double w, Vector4d dest) {
        dest.x = this.x + x;
        dest.y = this.y + y;
        dest.z = this.z + z;
        dest.w = this.w + w;
        return dest;
    }

    public Vector4d add(Vector4fc v) {
        return this.add(v, this);
    }

    public Vector4d fma(Vector4dc a, Vector4dc b) {
        return this.fma(a, b, this);
    }

    public Vector4d fma(double a, Vector4dc b) {
        return this.fma(a, b, this);
    }

    public Vector4d fma(Vector4dc a, Vector4dc b, Vector4d dest) {
        dest.x = Math.fma(a.x(), b.x(), this.x);
        dest.y = Math.fma(a.y(), b.y(), this.y);
        dest.z = Math.fma(a.z(), b.z(), this.z);
        dest.w = Math.fma(a.w(), b.w(), this.w);
        return dest;
    }

    public Vector4d fma(double a, Vector4dc b, Vector4d dest) {
        dest.x = Math.fma(a, b.x(), this.x);
        dest.y = Math.fma(a, b.y(), this.y);
        dest.z = Math.fma(a, b.z(), this.z);
        dest.w = Math.fma(a, b.w(), this.w);
        return dest;
    }

    public Vector4d mulAdd(Vector4dc a, Vector4dc b) {
        return this.mulAdd(a, b, this);
    }

    public Vector4d mulAdd(double a, Vector4dc b) {
        return this.mulAdd(a, b, this);
    }

    public Vector4d mulAdd(Vector4dc a, Vector4dc b, Vector4d dest) {
        dest.x = Math.fma(this.x, a.x(), b.x());
        dest.y = Math.fma(this.y, a.y(), b.y());
        dest.z = Math.fma(this.z, a.z(), b.z());
        return dest;
    }

    public Vector4d mulAdd(double a, Vector4dc b, Vector4d dest) {
        dest.x = Math.fma(this.x, a, b.x());
        dest.y = Math.fma(this.y, a, b.y());
        dest.z = Math.fma(this.z, a, b.z());
        return dest;
    }

    public Vector4d mul(Vector4dc v) {
        return this.mul(v, this);
    }

    public Vector4d mul(Vector4dc v, Vector4d dest) {
        dest.x = this.x * v.x();
        dest.y = this.y * v.y();
        dest.z = this.z * v.z();
        dest.w = this.w * v.w();
        return dest;
    }

    public Vector4d div(Vector4dc v) {
        return this.div(v, this);
    }

    public Vector4d div(Vector4dc v, Vector4d dest) {
        dest.x = this.x / v.x();
        dest.y = this.y / v.y();
        dest.z = this.z / v.z();
        dest.w = this.w / v.w();
        return dest;
    }

    public Vector4d mul(Vector4fc v) {
        return this.mul(v, this);
    }

    public Vector4d mul(Vector4fc v, Vector4d dest) {
        dest.x = this.x * (double)v.x();
        dest.y = this.y * (double)v.y();
        dest.z = this.z * (double)v.z();
        dest.w = this.w * (double)v.w();
        return dest;
    }

    public Vector4d mul(Matrix4dc mat) {
        return this.mul(mat, this);
    }

    public Vector4d mul(Matrix4dc mat, Vector4d dest) {
        int prop = mat.properties();
        if ((prop & 4) != 0) {
            return dest.set(this);
        }
        if ((prop & 8) != 0) {
            return this.mulTranslation(mat, dest);
        }
        if ((prop & 2) != 0) {
            return this.mulAffine(mat, dest);
        }
        return this.mulGeneric(mat, dest);
    }

    public Vector4d mulTranspose(Matrix4dc mat) {
        return this.mulTranspose(mat, this);
    }

    public Vector4d mulTranspose(Matrix4dc mat, Vector4d dest) {
        int prop = mat.properties();
        if ((prop & 4) != 0) {
            return dest.set(this);
        }
        if ((prop & 2) != 0) {
            return this.mulAffineTranspose(mat, dest);
        }
        return this.mulGenericTranspose(mat, dest);
    }

    public Vector4d mulTranslation(Matrix4dc mat, Vector4d dest) {
        double x = this.x;
        double y = this.y;
        double z = this.z;
        double w = this.w;
        dest.x = Math.fma(mat.m30(), w, x);
        dest.y = Math.fma(mat.m31(), w, y);
        dest.z = Math.fma(mat.m32(), w, z);
        dest.w = w;
        return dest;
    }

    public Vector4d mulTranslation(Matrix4fc mat, Vector4d dest) {
        double x = this.x;
        double y = this.y;
        double z = this.z;
        double w = this.w;
        dest.x = Math.fma((double)mat.m30(), w, x);
        dest.y = Math.fma((double)mat.m31(), w, y);
        dest.z = Math.fma((double)mat.m32(), w, z);
        dest.w = w;
        return dest;
    }

    public Vector4d mulAffine(Matrix4dc mat, Vector4d dest) {
        double rx = Math.fma(mat.m00(), this.x, Math.fma(mat.m10(), this.y, Math.fma(mat.m20(), this.z, mat.m30() * this.w)));
        double ry = Math.fma(mat.m01(), this.x, Math.fma(mat.m11(), this.y, Math.fma(mat.m21(), this.z, mat.m31() * this.w)));
        double rz = Math.fma(mat.m02(), this.x, Math.fma(mat.m12(), this.y, Math.fma(mat.m22(), this.z, mat.m32() * this.w)));
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        dest.w = this.w;
        return dest;
    }

    public Vector4d mulGeneric(Matrix4dc mat) {
        return this.mulGeneric(mat, this);
    }

    public Vector4d mulGeneric(Matrix4dc mat, Vector4d dest) {
        double rx = Math.fma(mat.m00(), this.x, Math.fma(mat.m10(), this.y, Math.fma(mat.m20(), this.z, mat.m30() * this.w)));
        double ry = Math.fma(mat.m01(), this.x, Math.fma(mat.m11(), this.y, Math.fma(mat.m21(), this.z, mat.m31() * this.w)));
        double rz = Math.fma(mat.m02(), this.x, Math.fma(mat.m12(), this.y, Math.fma(mat.m22(), this.z, mat.m32() * this.w)));
        double rw = Math.fma(mat.m03(), this.x, Math.fma(mat.m13(), this.y, Math.fma(mat.m23(), this.z, mat.m33() * this.w)));
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        dest.w = rw;
        return dest;
    }

    public Vector4d mulAffineTranspose(Matrix4dc mat, Vector4d dest) {
        double x = this.x;
        double y = this.y;
        double z = this.z;
        double w = this.w;
        dest.x = Math.fma(mat.m00(), x, Math.fma(mat.m01(), y, mat.m02() * z));
        dest.y = Math.fma(mat.m10(), x, Math.fma(mat.m11(), y, mat.m12() * z));
        dest.z = Math.fma(mat.m20(), x, Math.fma(mat.m21(), y, mat.m22() * z));
        dest.w = Math.fma(mat.m30(), x, Math.fma(mat.m31(), y, mat.m32() * z + w));
        return dest;
    }

    public Vector4d mulGenericTranspose(Matrix4dc mat, Vector4d dest) {
        double x = this.x;
        double y = this.y;
        double z = this.z;
        double w = this.w;
        dest.x = Math.fma(mat.m00(), x, Math.fma(mat.m01(), y, Math.fma(mat.m02(), z, mat.m03() * w)));
        dest.y = Math.fma(mat.m10(), x, Math.fma(mat.m11(), y, Math.fma(mat.m12(), z, mat.m13() * w)));
        dest.z = Math.fma(mat.m20(), x, Math.fma(mat.m21(), y, Math.fma(mat.m22(), z, mat.m23() * w)));
        dest.w = Math.fma(mat.m30(), x, Math.fma(mat.m31(), y, Math.fma(mat.m32(), z, mat.m33() * w)));
        return dest;
    }

    public Vector4d mul(Matrix4x3dc mat) {
        return this.mul(mat, this);
    }

    public Vector4d mul(Matrix4x3dc mat, Vector4d dest) {
        int prop = mat.properties();
        if ((prop & 4) != 0) {
            return dest.set(this);
        }
        if ((prop & 8) != 0) {
            return this.mulTranslation(mat, dest);
        }
        return this.mulGeneric(mat, dest);
    }

    public Vector4d mulGeneric(Matrix4x3dc mat, Vector4d dest) {
        double rx = Math.fma(mat.m00(), this.x, Math.fma(mat.m10(), this.y, Math.fma(mat.m20(), this.z, mat.m30() * this.w)));
        double ry = Math.fma(mat.m01(), this.x, Math.fma(mat.m11(), this.y, Math.fma(mat.m21(), this.z, mat.m31() * this.w)));
        double rz = Math.fma(mat.m02(), this.x, Math.fma(mat.m12(), this.y, Math.fma(mat.m22(), this.z, mat.m32() * this.w)));
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        dest.w = this.w;
        return dest;
    }

    public Vector4d mulTranslation(Matrix4x3dc mat, Vector4d dest) {
        dest.x = Math.fma(mat.m30(), this.w, this.x);
        dest.y = Math.fma(mat.m31(), this.w, this.y);
        dest.z = Math.fma(mat.m32(), this.w, this.z);
        dest.w = this.w;
        return dest;
    }

    public Vector4d mul(Matrix4x3fc mat) {
        return this.mul(mat, this);
    }

    public Vector4d mul(Matrix4x3fc mat, Vector4d dest) {
        int prop = mat.properties();
        if ((prop & 4) != 0) {
            return dest.set(this);
        }
        if ((prop & 8) != 0) {
            return this.mulTranslation(mat, dest);
        }
        return this.mulGeneric(mat, dest);
    }

    public Vector4d mulGeneric(Matrix4x3fc mat, Vector4d dest) {
        double rx = Math.fma((double)mat.m00(), this.x, Math.fma((double)mat.m10(), this.y, Math.fma((double)mat.m20(), this.z, (double)mat.m30() * this.w)));
        double ry = Math.fma((double)mat.m01(), this.x, Math.fma((double)mat.m11(), this.y, Math.fma((double)mat.m21(), this.z, (double)mat.m31() * this.w)));
        double rz = Math.fma((double)mat.m02(), this.x, Math.fma((double)mat.m12(), this.y, Math.fma((double)mat.m22(), this.z, (double)mat.m32() * this.w)));
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        dest.w = this.w;
        return dest;
    }

    public Vector4d mulTranslation(Matrix4x3fc mat, Vector4d dest) {
        dest.x = Math.fma((double)mat.m30(), this.w, this.x);
        dest.y = Math.fma((double)mat.m31(), this.w, this.y);
        dest.z = Math.fma((double)mat.m32(), this.w, this.z);
        dest.w = this.w;
        return dest;
    }

    public Vector4d mul(Matrix4fc mat) {
        return this.mul(mat, this);
    }

    public Vector4d mul(Matrix4fc mat, Vector4d dest) {
        int prop = mat.properties();
        if ((prop & 4) != 0) {
            return dest.set(this);
        }
        if ((prop & 8) != 0) {
            return this.mulTranslation(mat, dest);
        }
        if ((prop & 2) != 0) {
            return this.mulAffine(mat, dest);
        }
        return this.mulGeneric(mat, dest);
    }

    public Vector4d mulAffine(Matrix4fc mat) {
        return this.mulAffine(mat, this);
    }

    public Vector4d mulAffine(Matrix4fc mat, Vector4d dest) {
        double rx = Math.fma((double)mat.m00(), this.x, Math.fma((double)mat.m10(), this.y, Math.fma((double)mat.m20(), this.z, (double)mat.m30() * this.w)));
        double ry = Math.fma((double)mat.m01(), this.x, Math.fma((double)mat.m11(), this.y, Math.fma((double)mat.m21(), this.z, (double)mat.m31() * this.w)));
        double rz = Math.fma((double)mat.m02(), this.x, Math.fma((double)mat.m12(), this.y, Math.fma((double)mat.m22(), this.z, (double)mat.m32() * this.w)));
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        dest.w = this.w;
        return dest;
    }

    public Vector4d mulGeneric(Matrix4fc mat) {
        return this.mulGeneric(mat, this);
    }

    public Vector4d mulGeneric(Matrix4fc mat, Vector4d dest) {
        double rx = Math.fma((double)mat.m00(), this.x, Math.fma((double)mat.m10(), this.y, Math.fma((double)mat.m20(), this.z, (double)mat.m30() * this.w)));
        double ry = Math.fma((double)mat.m01(), this.x, Math.fma((double)mat.m11(), this.y, Math.fma((double)mat.m21(), this.z, (double)mat.m31() * this.w)));
        double rz = Math.fma((double)mat.m02(), this.x, Math.fma((double)mat.m12(), this.y, Math.fma((double)mat.m22(), this.z, (double)mat.m32() * this.w)));
        double rw = Math.fma((double)mat.m03(), this.x, Math.fma((double)mat.m13(), this.y, Math.fma((double)mat.m23(), this.z, (double)mat.m33() * this.w)));
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        dest.w = rw;
        return dest;
    }

    public Vector4d mulProjectGeneric(Matrix4dc mat) {
        return this.mulProjectGeneric(mat, this);
    }

    public Vector4d mulProjectGeneric(Matrix4dc mat, Vector4d dest) {
        double invW = 1.0 / Math.fma(mat.m03(), this.x, Math.fma(mat.m13(), this.y, Math.fma(mat.m23(), this.z, mat.m33() * this.w)));
        double rx = Math.fma(mat.m00(), this.x, Math.fma(mat.m10(), this.y, Math.fma(mat.m20(), this.z, mat.m30() * this.w))) * invW;
        double ry = Math.fma(mat.m01(), this.x, Math.fma(mat.m11(), this.y, Math.fma(mat.m21(), this.z, mat.m31() * this.w))) * invW;
        double rz = Math.fma(mat.m02(), this.x, Math.fma(mat.m12(), this.y, Math.fma(mat.m22(), this.z, mat.m32() * this.w))) * invW;
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        dest.w = 1.0;
        return dest;
    }

    public Vector4d mulProjectTranslation(Matrix4dc mat) {
        return this.mulProjectTranslation(mat, this);
    }

    public Vector4d mulProjectTranslation(Matrix4dc mat, Vector4d dest) {
        double invW = 1.0 / this.w;
        double rx = Math.fma(mat.m00(), this.x, mat.m30() * this.w) * invW;
        double ry = Math.fma(mat.m11(), this.y, mat.m31() * this.w) * invW;
        double rz = Math.fma(mat.m22(), this.z, mat.m32() * this.w) * invW;
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        dest.w = 1.0;
        return dest;
    }

    public Vector3d mulProjectTranslation(Matrix4dc mat, Vector3d dest) {
        double invW = 1.0 / this.w;
        double rx = Math.fma(mat.m00(), this.x, mat.m30() * this.w) * invW;
        double ry = Math.fma(mat.m11(), this.y, mat.m31() * this.w) * invW;
        double rz = Math.fma(mat.m22(), this.z, mat.m32() * this.w) * invW;
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    public Vector4d mulProjectAffine(Matrix4dc mat) {
        return this.mulProjectAffine(mat, this);
    }

    public Vector4d mulProjectAffine(Matrix4dc mat, Vector4d dest) {
        double invW = 1.0 / this.w;
        double rx = Math.fma(mat.m00(), this.x, Math.fma(mat.m10(), this.y, Math.fma(mat.m20(), this.z, mat.m30() * this.w))) * invW;
        double ry = Math.fma(mat.m01(), this.x, Math.fma(mat.m11(), this.y, Math.fma(mat.m21(), this.z, mat.m31() * this.w))) * invW;
        double rz = Math.fma(mat.m02(), this.x, Math.fma(mat.m12(), this.y, Math.fma(mat.m22(), this.z, mat.m32() * this.w))) * invW;
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        dest.w = 1.0;
        return dest;
    }

    public Vector3d mulProjectAffine(Matrix4dc mat, Vector3d dest) {
        double invW = 1.0 / this.w;
        double rx = Math.fma(mat.m00(), this.x, Math.fma(mat.m10(), this.y, Math.fma(mat.m20(), this.z, mat.m30() * this.w))) * invW;
        double ry = Math.fma(mat.m01(), this.x, Math.fma(mat.m11(), this.y, Math.fma(mat.m21(), this.z, mat.m31() * this.w))) * invW;
        double rz = Math.fma(mat.m02(), this.x, Math.fma(mat.m12(), this.y, Math.fma(mat.m22(), this.z, mat.m32() * this.w))) * invW;
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    public Vector4d mulProject(Matrix4dc mat) {
        return this.mulProject(mat, this);
    }

    public Vector4d mulProject(Matrix4dc mat, Vector4d dest) {
        int prop = mat.properties();
        if ((prop & 4) != 0) {
            return dest.set(this);
        }
        if ((prop & 8) != 0) {
            return this.mulProjectTranslation(mat, dest);
        }
        if ((prop & 2) != 0) {
            return this.mulProjectAffine(mat, dest);
        }
        return this.mulProjectGeneric(mat, dest);
    }

    public Vector3d mulProject(Matrix4dc mat, Vector3d dest) {
        int prop = mat.properties();
        if ((prop & 4) != 0) {
            return dest.set(this);
        }
        if ((prop & 8) != 0) {
            return this.mulProjectTranslation(mat, dest);
        }
        if ((prop & 2) != 0) {
            return this.mulProjectAffine(mat, dest);
        }
        return this.mulProjectGeneric(mat, dest);
    }

    public Vector3d mulProjectGeneric(Matrix4dc mat, Vector3d dest) {
        double invW = 1.0 / Math.fma(mat.m03(), this.x, Math.fma(mat.m13(), this.y, Math.fma(mat.m23(), this.z, mat.m33() * this.w)));
        double rx = Math.fma(mat.m00(), this.x, Math.fma(mat.m10(), this.y, Math.fma(mat.m20(), this.z, mat.m30() * this.w))) * invW;
        double ry = Math.fma(mat.m01(), this.x, Math.fma(mat.m11(), this.y, Math.fma(mat.m21(), this.z, mat.m31() * this.w))) * invW;
        double rz = Math.fma(mat.m02(), this.x, Math.fma(mat.m12(), this.y, Math.fma(mat.m22(), this.z, mat.m32() * this.w))) * invW;
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    public Vector4d mul(double scalar) {
        return this.mul(scalar, this);
    }

    public Vector4d mul(double scalar, Vector4d dest) {
        dest.x = this.x * scalar;
        dest.y = this.y * scalar;
        dest.z = this.z * scalar;
        dest.w = this.w * scalar;
        return dest;
    }

    public Vector4d div(double scalar) {
        return this.div(scalar, this);
    }

    public Vector4d div(double scalar, Vector4d dest) {
        double inv = 1.0 / scalar;
        dest.x = this.x * inv;
        dest.y = this.y * inv;
        dest.z = this.z * inv;
        dest.w = this.w * inv;
        return dest;
    }

    public Vector4d rotate(Quaterniondc quat) {
        quat.transform(this, this);
        return this;
    }

    public Vector4d rotate(Quaterniondc quat, Vector4d dest) {
        quat.transform(this, dest);
        return dest;
    }

    public Vector4d rotateAxis(double angle, double x, double y, double z) {
        return this.rotateAxis(angle, x, y, z, this);
    }

    public Vector4d rotateAxis(double angle, double aX, double aY, double aZ, Vector4d dest) {
        if (aY == 0.0 && aZ == 0.0 && Math.absEqualsOne(aX)) {
            return this.rotateX(aX * angle, dest);
        }
        if (aX == 0.0 && aZ == 0.0 && Math.absEqualsOne(aY)) {
            return this.rotateY(aY * angle, dest);
        }
        if (aX == 0.0 && aY == 0.0 && Math.absEqualsOne(aZ)) {
            return this.rotateZ(aZ * angle, dest);
        }
        return this.rotateAxisInternal(angle, aX, aY, aZ, dest);
    }

    private Vector4d rotateAxisInternal(double angle, double aX, double aY, double aZ, Vector4d dest) {
        double hangle = angle * 0.5;
        double sinAngle = Math.sin(hangle);
        double qx = aX * sinAngle;
        double qy = aY * sinAngle;
        double qz = aZ * sinAngle;
        double qw = Math.cosFromSin(sinAngle, hangle);
        double w2 = qw * qw;
        double x2 = qx * qx;
        double y2 = qy * qy;
        double z2 = qz * qz;
        double zw = qz * qw;
        double xy = qx * qy;
        double xz = qx * qz;
        double yw = qy * qw;
        double yz = qy * qz;
        double xw = qx * qw;
        double nx = (w2 + x2 - z2 - y2) * this.x + (-zw + xy - zw + xy) * this.y + (yw + xz + xz + yw) * this.z;
        double ny = (xy + zw + zw + xy) * this.x + (y2 - z2 + w2 - x2) * this.y + (yz + yz - xw - xw) * this.z;
        double nz = (xz - yw + xz - yw) * this.x + (yz + yz + xw + xw) * this.y + (z2 - y2 - x2 + w2) * this.z;
        dest.x = nx;
        dest.y = ny;
        dest.z = nz;
        return dest;
    }

    public Vector4d rotateX(double angle) {
        return this.rotateX(angle, this);
    }

    public Vector4d rotateX(double angle, Vector4d dest) {
        double sin = Math.sin(angle);
        double cos = Math.cosFromSin(sin, angle);
        double y = this.y * cos - this.z * sin;
        double z = this.y * sin + this.z * cos;
        dest.x = this.x;
        dest.y = y;
        dest.z = z;
        dest.w = this.w;
        return dest;
    }

    public Vector4d rotateY(double angle) {
        return this.rotateY(angle, this);
    }

    public Vector4d rotateY(double angle, Vector4d dest) {
        double sin = Math.sin(angle);
        double cos = Math.cosFromSin(sin, angle);
        double x = this.x * cos + this.z * sin;
        double z = -this.x * sin + this.z * cos;
        dest.x = x;
        dest.y = this.y;
        dest.z = z;
        dest.w = this.w;
        return dest;
    }

    public Vector4d rotateZ(double angle) {
        return this.rotateZ(angle, this);
    }

    public Vector4d rotateZ(double angle, Vector4d dest) {
        double sin = Math.sin(angle);
        double cos = Math.cosFromSin(sin, angle);
        double x = this.x * cos - this.y * sin;
        double y = this.x * sin + this.y * cos;
        dest.x = x;
        dest.y = y;
        dest.z = this.z;
        dest.w = this.w;
        return dest;
    }

    public double lengthSquared() {
        return Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
    }

    public static double lengthSquared(double x, double y, double z, double w) {
        return Math.fma(x, x, Math.fma(y, y, Math.fma(z, z, w * w)));
    }

    public double length() {
        return Math.sqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w))));
    }

    public static double length(double x, double y, double z, double w) {
        return Math.sqrt(Math.fma(x, x, Math.fma(y, y, Math.fma(z, z, w * w))));
    }

    public Vector4d normalize() {
        return this.normalize(this);
    }

    public Vector4d normalize(Vector4d dest) {
        double invLength = 1.0 / this.length();
        dest.x = this.x * invLength;
        dest.y = this.y * invLength;
        dest.z = this.z * invLength;
        dest.w = this.w * invLength;
        return dest;
    }

    public Vector4d normalize(double length) {
        return this.normalize(length, this);
    }

    public Vector4d normalize(double length, Vector4d dest) {
        double invLength = 1.0 / this.length() * length;
        dest.x = this.x * invLength;
        dest.y = this.y * invLength;
        dest.z = this.z * invLength;
        dest.w = this.w * invLength;
        return dest;
    }

    public Vector4d normalize3() {
        return this.normalize3(this);
    }

    public Vector4d normalize3(Vector4d dest) {
        double invLength = Math.invsqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z)));
        dest.x = this.x * invLength;
        dest.y = this.y * invLength;
        dest.z = this.z * invLength;
        dest.w = this.w * invLength;
        return dest;
    }

    public double distance(Vector4dc v) {
        double dx = this.x - v.x();
        double dy = this.y - v.y();
        double dz = this.z - v.z();
        double dw = this.w - v.w();
        return Math.sqrt(Math.fma(dx, dx, Math.fma(dy, dy, Math.fma(dz, dz, dw * dw))));
    }

    public double distance(double x, double y, double z, double w) {
        double dx = this.x - x;
        double dy = this.y - y;
        double dz = this.z - z;
        double dw = this.w - w;
        return Math.sqrt(Math.fma(dx, dx, Math.fma(dy, dy, Math.fma(dz, dz, dw * dw))));
    }

    public double distanceSquared(Vector4dc v) {
        double dx = this.x - v.x();
        double dy = this.y - v.y();
        double dz = this.z - v.z();
        double dw = this.w - v.w();
        return Math.fma(dx, dx, Math.fma(dy, dy, Math.fma(dz, dz, dw * dw)));
    }

    public double distanceSquared(double x, double y, double z, double w) {
        double dx = this.x - x;
        double dy = this.y - y;
        double dz = this.z - z;
        double dw = this.w - w;
        return Math.fma(dx, dx, Math.fma(dy, dy, Math.fma(dz, dz, dw * dw)));
    }

    public static double distance(double x1, double y1, double z1, double w1, double x2, double y2, double z2, double w2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double dz = z1 - z2;
        double dw = w1 - w2;
        return Math.sqrt(Math.fma(dx, dx, Math.fma(dy, dy, Math.fma(dz, dz, dw * dw))));
    }

    public static double distanceSquared(double x1, double y1, double z1, double w1, double x2, double y2, double z2, double w2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double dz = z1 - z2;
        double dw = w1 - w2;
        return Math.fma(dx, dx, Math.fma(dy, dy, Math.fma(dz, dz, dw * dw)));
    }

    public double dot(Vector4dc v) {
        return Math.fma(this.x, v.x(), Math.fma(this.y, v.y(), Math.fma(this.z, v.z(), this.w * v.w())));
    }

    public double dot(double x, double y, double z, double w) {
        return Math.fma(this.x, x, Math.fma(this.y, y, Math.fma(this.z, z, this.w * w)));
    }

    public double angleCos(Vector4dc v) {
        double length1Squared = Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
        double length2Squared = Math.fma(v.x(), v.x(), Math.fma(v.y(), v.y(), Math.fma(v.z(), v.z(), v.w() * v.w())));
        double dot = Math.fma(this.x, v.x(), Math.fma(this.y, v.y(), Math.fma(this.z, v.z(), this.w * v.w())));
        return dot / Math.sqrt(length1Squared * length2Squared);
    }

    public double angle(Vector4dc v) {
        double cos = this.angleCos(v);
        cos = cos < 1.0 ? cos : 1.0;
        cos = cos > -1.0 ? cos : -1.0;
        return Math.acos(cos);
    }

    public Vector4d zero() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
        this.w = 0.0;
        return this;
    }

    public Vector4d negate() {
        return this.negate(this);
    }

    public Vector4d negate(Vector4d dest) {
        dest.x = -this.x;
        dest.y = -this.y;
        dest.z = -this.z;
        dest.w = -this.w;
        return dest;
    }

    public Vector4d min(Vector4dc v) {
        return this.min(v, this);
    }

    public Vector4d min(Vector4dc v, Vector4d dest) {
        dest.x = this.x < v.x() ? this.x : v.x();
        dest.y = this.y < v.y() ? this.y : v.y();
        dest.z = this.z < v.z() ? this.z : v.z();
        dest.w = this.w < v.w() ? this.w : v.w();
        return dest;
    }

    public Vector4d max(Vector4dc v) {
        return this.max(v, this);
    }

    public Vector4d max(Vector4dc v, Vector4d dest) {
        dest.x = this.x > v.x() ? this.x : v.x();
        dest.y = this.y > v.y() ? this.y : v.y();
        dest.z = this.z > v.z() ? this.z : v.z();
        dest.w = this.w > v.w() ? this.w : v.w();
        return dest;
    }

    public String toString() {
        return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
    }

    public String toString(NumberFormat formatter) {
        return "(" + Runtime.format(this.x, formatter) + " " + Runtime.format(this.y, formatter) + " " + Runtime.format(this.z, formatter) + " " + Runtime.format(this.w, formatter) + ")";
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeDouble(this.x);
        out.writeDouble(this.y);
        out.writeDouble(this.z);
        out.writeDouble(this.w);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.x = in.readDouble();
        this.y = in.readDouble();
        this.z = in.readDouble();
        this.w = in.readDouble();
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        long temp = Double.doubleToLongBits(this.w);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.x);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.y);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.z);
        result = 31 * result + (int)(temp ^ temp >>> 32);
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
        Vector4d other = (Vector4d)obj;
        if (Double.doubleToLongBits(this.w) != Double.doubleToLongBits(other.w)) {
            return false;
        }
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
            return false;
        }
        if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
            return false;
        }
        return Double.doubleToLongBits(this.z) == Double.doubleToLongBits(other.z);
    }

    public boolean equals(Vector4dc v, double delta) {
        if (this == v) {
            return true;
        }
        if (v == null) {
            return false;
        }
        if (this.getClass() != v.getClass()) {
            return false;
        }
        if (!Runtime.equals(this.x, v.x(), delta)) {
            return false;
        }
        if (!Runtime.equals(this.y, v.y(), delta)) {
            return false;
        }
        if (!Runtime.equals(this.z, v.z(), delta)) {
            return false;
        }
        return Runtime.equals(this.w, v.w(), delta);
    }

    public boolean equals(double x, double y, double z, double w) {
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(x)) {
            return false;
        }
        if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(y)) {
            return false;
        }
        if (Double.doubleToLongBits(this.z) != Double.doubleToLongBits(z)) {
            return false;
        }
        return Double.doubleToLongBits(this.w) == Double.doubleToLongBits(w);
    }

    public Vector4d smoothStep(Vector4dc v, double t, Vector4d dest) {
        double t2 = t * t;
        double t3 = t2 * t;
        dest.x = (this.x + this.x - v.x() - v.x()) * t3 + (3.0 * v.x() - 3.0 * this.x) * t2 + this.x * t + this.x;
        dest.y = (this.y + this.y - v.y() - v.y()) * t3 + (3.0 * v.y() - 3.0 * this.y) * t2 + this.y * t + this.y;
        dest.z = (this.z + this.z - v.z() - v.z()) * t3 + (3.0 * v.z() - 3.0 * this.z) * t2 + this.z * t + this.z;
        dest.w = (this.w + this.w - v.w() - v.w()) * t3 + (3.0 * v.w() - 3.0 * this.w) * t2 + this.w * t + this.w;
        return dest;
    }

    public Vector4d hermite(Vector4dc t0, Vector4dc v1, Vector4dc t1, double t, Vector4d dest) {
        double t2 = t * t;
        double t3 = t2 * t;
        dest.x = (this.x + this.x - v1.x() - v1.x() + t1.x() + t0.x()) * t3 + (3.0 * v1.x() - 3.0 * this.x - t0.x() - t0.x() - t1.x()) * t2 + this.x * t + this.x;
        dest.y = (this.y + this.y - v1.y() - v1.y() + t1.y() + t0.y()) * t3 + (3.0 * v1.y() - 3.0 * this.y - t0.y() - t0.y() - t1.y()) * t2 + this.y * t + this.y;
        dest.z = (this.z + this.z - v1.z() - v1.z() + t1.z() + t0.z()) * t3 + (3.0 * v1.z() - 3.0 * this.z - t0.z() - t0.z() - t1.z()) * t2 + this.z * t + this.z;
        dest.w = (this.w + this.w - v1.w() - v1.w() + t1.w() + t0.w()) * t3 + (3.0 * v1.w() - 3.0 * this.w - t0.w() - t0.w() - t1.w()) * t2 + this.w * t + this.w;
        return dest;
    }

    public Vector4d lerp(Vector4dc other, double t) {
        return this.lerp(other, t, this);
    }

    public Vector4d lerp(Vector4dc other, double t, Vector4d dest) {
        dest.x = Math.fma(other.x() - this.x, t, this.x);
        dest.y = Math.fma(other.y() - this.y, t, this.y);
        dest.z = Math.fma(other.z() - this.z, t, this.z);
        dest.w = Math.fma(other.w() - this.w, t, this.w);
        return dest;
    }

    public double get(int component) throws IllegalArgumentException {
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

    public Vector4i get(int mode, Vector4i dest) {
        dest.x = Math.roundUsing(this.x(), mode);
        dest.y = Math.roundUsing(this.y(), mode);
        dest.z = Math.roundUsing(this.z(), mode);
        dest.w = Math.roundUsing(this.w(), mode);
        return dest;
    }

    public Vector4f get(Vector4f dest) {
        dest.x = (float)this.x();
        dest.y = (float)this.y();
        dest.z = (float)this.z();
        dest.w = (float)this.w();
        return dest;
    }

    public Vector4d get(Vector4d dest) {
        dest.x = this.x();
        dest.y = this.y();
        dest.z = this.z();
        dest.w = this.w();
        return dest;
    }

    public int maxComponent() {
        double absX = Math.abs(this.x);
        double absY = Math.abs(this.y);
        double absZ = Math.abs(this.z);
        double absW = Math.abs(this.w);
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
        double absX = Math.abs(this.x);
        double absY = Math.abs(this.y);
        double absZ = Math.abs(this.z);
        double absW = Math.abs(this.w);
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

    public Vector4d floor() {
        return this.floor(this);
    }

    public Vector4d floor(Vector4d dest) {
        dest.x = Math.floor(this.x);
        dest.y = Math.floor(this.y);
        dest.z = Math.floor(this.z);
        dest.w = Math.floor(this.w);
        return dest;
    }

    public Vector4d ceil() {
        return this.ceil(this);
    }

    public Vector4d ceil(Vector4d dest) {
        dest.x = Math.ceil(this.x);
        dest.y = Math.ceil(this.y);
        dest.z = Math.ceil(this.z);
        dest.w = Math.ceil(this.w);
        return dest;
    }

    public Vector4d round() {
        return this.round(this);
    }

    public Vector4d round(Vector4d dest) {
        dest.x = Math.round(this.x);
        dest.y = Math.round(this.y);
        dest.z = Math.round(this.z);
        dest.w = Math.round(this.w);
        return dest;
    }

    public boolean isFinite() {
        return Math.isFinite(this.x) && Math.isFinite(this.y) && Math.isFinite(this.z) && Math.isFinite(this.w);
    }

    public Vector4d absolute() {
        return this.absolute(this);
    }

    public Vector4d absolute(Vector4d dest) {
        dest.x = Math.abs(this.x);
        dest.y = Math.abs(this.y);
        dest.z = Math.abs(this.z);
        dest.w = Math.abs(this.w);
        return dest;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

