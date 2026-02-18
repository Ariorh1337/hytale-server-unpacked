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
import org.joml.Matrix3dc;
import org.joml.Matrix3fc;
import org.joml.Matrix3x2dc;
import org.joml.Matrix3x2fc;
import org.joml.Matrix4dc;
import org.joml.Matrix4fc;
import org.joml.Matrix4x3dc;
import org.joml.Matrix4x3fc;
import org.joml.MemUtil;
import org.joml.Options;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Runtime;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2ic;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.joml.Vector4dc;
import org.joml.Vector4fc;
import org.joml.Vector4ic;

public class Vector3d
implements Externalizable,
Cloneable,
Vector3dc {
    private static final long serialVersionUID = 1L;
    public double x;
    public double y;
    public double z;

    public Vector3d() {
    }

    public Vector3d(double d) {
        this.x = d;
        this.y = d;
        this.z = d;
    }

    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3d(Vector3fc v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
    }

    public Vector3d(Vector3ic v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
    }

    public Vector3d(Vector2fc v, double z) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
    }

    public Vector3d(Vector2ic v, double z) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
    }

    public Vector3d(Vector3dc v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
    }

    public Vector3d(Vector2dc v, double z) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
    }

    public Vector3d(double[] xyz) {
        this.x = xyz[0];
        this.y = xyz[1];
        this.z = xyz[2];
    }

    public Vector3d(float[] xyz) {
        this.x = xyz[0];
        this.y = xyz[1];
        this.z = xyz[2];
    }

    public Vector3d(ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
    }

    public Vector3d(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
    }

    public Vector3d(DoubleBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
    }

    public Vector3d(int index, DoubleBuffer buffer) {
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

    public Vector2f xy(Vector2f dest) {
        return dest.set(this.x, this.y);
    }

    public Vector2d xy(Vector2d dest) {
        return dest.set(this.x, this.y);
    }

    public Vector3d set(Vector4dc v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        return this;
    }

    public Vector3d set(Vector4fc v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        return this;
    }

    public Vector3d set(Vector4ic v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        return this;
    }

    public Vector3d set(Vector3dc v) {
        if (v == this) {
            return this;
        }
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        return this;
    }

    public Vector3d set(Vector3ic v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        return this;
    }

    public Vector3d set(Vector2dc v, double z) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
        return this;
    }

    public Vector3d set(Vector2ic v, double z) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
        return this;
    }

    public Vector3d set(Vector3fc v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        return this;
    }

    public Vector3d set(Vector2fc v, double z) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
        return this;
    }

    public Vector3d set(double d) {
        this.x = d;
        this.y = d;
        this.z = d;
        return this;
    }

    public Vector3d set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vector3d set(double[] xyz) {
        this.x = xyz[0];
        this.y = xyz[1];
        this.z = xyz[2];
        return this;
    }

    public Vector3d set(float[] xyz) {
        this.x = xyz[0];
        this.y = xyz[1];
        this.z = xyz[2];
        return this;
    }

    public Vector3d set(ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
        return this;
    }

    public Vector3d set(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
        return this;
    }

    public Vector3d set(DoubleBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
        return this;
    }

    public Vector3d set(int index, DoubleBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
        return this;
    }

    public Vector3d setFromAddress(long address) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        }
        MemUtil.MemUtilUnsafe.get(this, address);
        return this;
    }

    public Vector3d setComponent(int component, double value) throws IllegalArgumentException {
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

    public Vector3dc getToAddress(long address) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        }
        MemUtil.MemUtilUnsafe.put(this, address);
        return this;
    }

    public Vector3d sub(Vector3dc v) {
        return this.sub(v, this);
    }

    public Vector3d sub(Vector3dc v, Vector3d dest) {
        dest.x = this.x - v.x();
        dest.y = this.y - v.y();
        dest.z = this.z - v.z();
        return dest;
    }

    public Vector3d sub(Vector3fc v) {
        return this.sub(v, this);
    }

    public Vector3d sub(Vector3fc v, Vector3d dest) {
        dest.x = this.x - (double)v.x();
        dest.y = this.y - (double)v.y();
        dest.z = this.z - (double)v.z();
        return dest;
    }

    public Vector3d sub(double x, double y, double z) {
        return this.sub(x, y, z, this);
    }

    public Vector3d sub(double x, double y, double z, Vector3d dest) {
        dest.x = this.x - x;
        dest.y = this.y - y;
        dest.z = this.z - z;
        return dest;
    }

    public Vector3d add(Vector3dc v) {
        return this.add(v, this);
    }

    public Vector3d add(Vector3dc v, Vector3d dest) {
        dest.x = this.x + v.x();
        dest.y = this.y + v.y();
        dest.z = this.z + v.z();
        return dest;
    }

    public Vector3d add(Vector3fc v) {
        return this.add(v, this);
    }

    public Vector3d add(Vector3fc v, Vector3d dest) {
        dest.x = this.x + (double)v.x();
        dest.y = this.y + (double)v.y();
        dest.z = this.z + (double)v.z();
        return dest;
    }

    public Vector3d add(double x, double y, double z) {
        return this.add(x, y, z, this);
    }

    public Vector3d add(double x, double y, double z, Vector3d dest) {
        dest.x = this.x + x;
        dest.y = this.y + y;
        dest.z = this.z + z;
        return dest;
    }

    public Vector3d fma(Vector3dc a, Vector3dc b) {
        return this.fma(a, b, this);
    }

    public Vector3d fma(double a, Vector3dc b) {
        return this.fma(a, b, this);
    }

    public Vector3d fma(Vector3fc a, Vector3fc b) {
        return this.fma(a, b, this);
    }

    public Vector3d fma(Vector3fc a, Vector3fc b, Vector3d dest) {
        dest.x = Math.fma((double)a.x(), (double)b.x(), this.x);
        dest.y = Math.fma((double)a.y(), (double)b.y(), this.y);
        dest.z = Math.fma((double)a.z(), (double)b.z(), this.z);
        return dest;
    }

    public Vector3d fma(double a, Vector3fc b) {
        return this.fma(a, b, this);
    }

    public Vector3d fma(Vector3dc a, Vector3dc b, Vector3d dest) {
        dest.x = Math.fma(a.x(), b.x(), this.x);
        dest.y = Math.fma(a.y(), b.y(), this.y);
        dest.z = Math.fma(a.z(), b.z(), this.z);
        return dest;
    }

    public Vector3d fma(double a, Vector3dc b, Vector3d dest) {
        dest.x = Math.fma(a, b.x(), this.x);
        dest.y = Math.fma(a, b.y(), this.y);
        dest.z = Math.fma(a, b.z(), this.z);
        return dest;
    }

    public Vector3d fma(Vector3dc a, Vector3fc b, Vector3d dest) {
        dest.x = Math.fma(a.x(), (double)b.x(), this.x);
        dest.y = Math.fma(a.y(), (double)b.y(), this.y);
        dest.z = Math.fma(a.z(), (double)b.z(), this.z);
        return dest;
    }

    public Vector3d fma(double a, Vector3fc b, Vector3d dest) {
        dest.x = Math.fma(a, (double)b.x(), this.x);
        dest.y = Math.fma(a, (double)b.y(), this.y);
        dest.z = Math.fma(a, (double)b.z(), this.z);
        return dest;
    }

    public Vector3d mulAdd(Vector3dc a, Vector3dc b) {
        return this.mulAdd(a, b, this);
    }

    public Vector3d mulAdd(double a, Vector3dc b) {
        return this.mulAdd(a, b, this);
    }

    public Vector3d mulAdd(Vector3dc a, Vector3dc b, Vector3d dest) {
        dest.x = Math.fma(this.x, a.x(), b.x());
        dest.y = Math.fma(this.y, a.y(), b.y());
        dest.z = Math.fma(this.z, a.z(), b.z());
        return dest;
    }

    public Vector3d mulAdd(double a, Vector3dc b, Vector3d dest) {
        dest.x = Math.fma(this.x, a, b.x());
        dest.y = Math.fma(this.y, a, b.y());
        dest.z = Math.fma(this.z, a, b.z());
        return dest;
    }

    public Vector3d mulAdd(Vector3fc a, Vector3dc b, Vector3d dest) {
        dest.x = Math.fma(this.x, (double)a.x(), b.x());
        dest.y = Math.fma(this.y, (double)a.y(), b.y());
        dest.z = Math.fma(this.z, (double)a.z(), b.z());
        return dest;
    }

    public Vector3d mul(Vector3dc v) {
        return this.mul(v, this);
    }

    public Vector3d mul(Vector3fc v) {
        return this.mul(v, this);
    }

    public Vector3d mul(Vector3fc v, Vector3d dest) {
        dest.x = this.x * (double)v.x();
        dest.y = this.y * (double)v.y();
        dest.z = this.z * (double)v.z();
        return dest;
    }

    public Vector3d mul(Vector3dc v, Vector3d dest) {
        dest.x = this.x * v.x();
        dest.y = this.y * v.y();
        dest.z = this.z * v.z();
        return dest;
    }

    public Vector3d div(Vector3dc v) {
        return this.div(v, this);
    }

    public Vector3d div(Vector3fc v) {
        return this.div(v, this);
    }

    public Vector3d div(Vector3fc v, Vector3d dest) {
        dest.x = this.x / (double)v.x();
        dest.y = this.y / (double)v.y();
        dest.z = this.z / (double)v.z();
        return dest;
    }

    public Vector3d div(Vector3dc v, Vector3d dest) {
        dest.x = this.x / v.x();
        dest.y = this.y / v.y();
        dest.z = this.z / v.z();
        return dest;
    }

    public Vector3d mulProject(Matrix4dc mat, double w, Vector3d dest) {
        double invW = 1.0 / Math.fma(mat.m03(), this.x, Math.fma(mat.m13(), this.y, Math.fma(mat.m23(), this.z, mat.m33() * w)));
        double rx = Math.fma(mat.m00(), this.x, Math.fma(mat.m10(), this.y, Math.fma(mat.m20(), this.z, mat.m30() * w))) * invW;
        double ry = Math.fma(mat.m01(), this.x, Math.fma(mat.m11(), this.y, Math.fma(mat.m21(), this.z, mat.m31() * w))) * invW;
        double rz = Math.fma(mat.m02(), this.x, Math.fma(mat.m12(), this.y, Math.fma(mat.m22(), this.z, mat.m32() * w))) * invW;
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    public Vector3d mulProject(Matrix4dc mat) {
        int prop = mat.properties();
        if ((prop & 4) != 0) {
            return this;
        }
        if ((prop & 8) != 0) {
            return this.mulProjectTranslation(mat, this);
        }
        if ((prop & 2) != 0) {
            return this.mulProjectAffine(mat, this);
        }
        return this.mulProjectGeneric(mat, this);
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

    public Vector3d mulProject(Matrix4fc mat) {
        int prop = mat.properties();
        if ((prop & 4) != 0) {
            return this;
        }
        if ((prop & 8) != 0) {
            return this.mulProjectTranslation(mat, this);
        }
        if ((prop & 2) != 0) {
            return this.mulProjectAffine(mat, this);
        }
        return this.mulProjectGeneric(mat, this);
    }

    public Vector3d mulProject(Matrix4fc mat, Vector3d dest) {
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

    public Vector3d mulProjectTranslation(Matrix4dc mat) {
        return this.mulPositionTranslation(mat, this);
    }

    public Vector3d mulProjectTranslation(Matrix4dc mat, Vector3d dest) {
        return this.mulPositionTranslation(mat, dest);
    }

    public Vector3d mulProjectTranslation(Matrix4fc mat) {
        return this.mulPositionTranslation(mat, this);
    }

    public Vector3d mulProjectTranslation(Matrix4fc mat, Vector3d dest) {
        return this.mulPositionTranslation(mat, dest);
    }

    public Vector3d mulProjectAffine(Matrix4dc mat) {
        return this.mulProjectAffine(mat, this);
    }

    public Vector3d mulProjectAffine(Matrix4dc mat, Vector3d dest) {
        double x1 = this.x;
        double y1 = this.y;
        double z1 = this.z;
        dest.x = Math.fma(mat.m00(), x1, Math.fma(mat.m10(), y1, mat.m20() * z1)) + mat.m30();
        dest.y = Math.fma(mat.m01(), x1, Math.fma(mat.m11(), y1, mat.m21() * z1)) + mat.m31();
        dest.z = Math.fma(mat.m02(), x1, Math.fma(mat.m12(), y1, mat.m22() * z1)) + mat.m32();
        return dest;
    }

    public Vector3d mulProjectAffine(Matrix4fc mat) {
        return this.mulProjectAffine(mat, this);
    }

    public Vector3d mulProjectAffine(Matrix4fc mat, Vector3d dest) {
        double x = this.x;
        double y = this.y;
        double z = this.z;
        dest.x = Math.fma((double)mat.m00(), x, Math.fma((double)mat.m10(), y, (double)mat.m20() * z)) + (double)mat.m30();
        dest.y = Math.fma((double)mat.m01(), x, Math.fma((double)mat.m11(), y, (double)mat.m21() * z)) + (double)mat.m31();
        dest.z = Math.fma((double)mat.m02(), x, Math.fma((double)mat.m12(), y, (double)mat.m22() * z)) + (double)mat.m32();
        return dest;
    }

    public Vector3d mulProjectGeneric(Matrix4dc mat) {
        return this.mulProjectGeneric(mat, this);
    }

    public Vector3d mulProjectGeneric(Matrix4dc mat, Vector3d dest) {
        double x = this.x;
        double y = this.y;
        double z = this.z;
        double invW = 1.0 / Math.fma(mat.m03(), x, Math.fma(mat.m13(), y, Math.fma(mat.m23(), z, mat.m33())));
        dest.x = Math.fma(mat.m00(), x, Math.fma(mat.m10(), y, Math.fma(mat.m20(), z, mat.m30()))) * invW;
        dest.y = Math.fma(mat.m01(), x, Math.fma(mat.m11(), y, Math.fma(mat.m21(), z, mat.m31()))) * invW;
        dest.z = Math.fma(mat.m02(), x, Math.fma(mat.m12(), y, Math.fma(mat.m22(), z, mat.m32()))) * invW;
        return dest;
    }

    public Vector3d mulProjectGeneric(Matrix4fc mat) {
        return this.mulProjectGeneric(mat, this);
    }

    public Vector3d mulProjectGeneric(Matrix4fc mat, Vector3d dest) {
        double x = this.x;
        double y = this.y;
        double z = this.z;
        double invW = 1.0 / Math.fma((double)mat.m03(), x, Math.fma((double)mat.m13(), y, Math.fma((double)mat.m23(), z, (double)mat.m33())));
        dest.x = Math.fma((double)mat.m00(), x, Math.fma((double)mat.m10(), y, Math.fma((double)mat.m20(), z, (double)mat.m30()))) * invW;
        dest.y = Math.fma((double)mat.m01(), x, Math.fma((double)mat.m11(), y, Math.fma((double)mat.m21(), z, (double)mat.m31()))) * invW;
        dest.z = Math.fma((double)mat.m02(), x, Math.fma((double)mat.m12(), y, Math.fma((double)mat.m22(), z, (double)mat.m32()))) * invW;
        return dest;
    }

    public Vector3d mul(Matrix3fc mat) {
        return this.mul(mat, this);
    }

    public Vector3d mul(Matrix3dc mat) {
        return this.mul(mat, this);
    }

    public Vector3d mul(Matrix3dc mat, Vector3d dest) {
        double rx = Math.fma(mat.m00(), this.x, Math.fma(mat.m10(), this.y, mat.m20() * this.z));
        double ry = Math.fma(mat.m01(), this.x, Math.fma(mat.m11(), this.y, mat.m21() * this.z));
        double rz = Math.fma(mat.m02(), this.x, Math.fma(mat.m12(), this.y, mat.m22() * this.z));
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    public Vector3f mul(Matrix3dc mat, Vector3f dest) {
        double rx = Math.fma(mat.m00(), this.x, Math.fma(mat.m10(), this.y, mat.m20() * this.z));
        double ry = Math.fma(mat.m01(), this.x, Math.fma(mat.m11(), this.y, mat.m21() * this.z));
        double rz = Math.fma(mat.m02(), this.x, Math.fma(mat.m12(), this.y, mat.m22() * this.z));
        dest.x = (float)rx;
        dest.y = (float)ry;
        dest.z = (float)rz;
        return dest;
    }

    public Vector3d mul(Matrix3fc mat, Vector3d dest) {
        double rx = Math.fma((double)mat.m00(), this.x, Math.fma((double)mat.m10(), this.y, (double)mat.m20() * this.z));
        double ry = Math.fma((double)mat.m01(), this.x, Math.fma((double)mat.m11(), this.y, (double)mat.m21() * this.z));
        double rz = Math.fma((double)mat.m02(), this.x, Math.fma((double)mat.m12(), this.y, (double)mat.m22() * this.z));
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    public Vector3d mul(Matrix3x2dc mat) {
        return this.mul(mat, this);
    }

    public Vector3d mul(Matrix3x2dc mat, Vector3d dest) {
        double rx = Math.fma(mat.m00(), this.x, Math.fma(mat.m10(), this.y, mat.m20() * this.z));
        double ry = Math.fma(mat.m01(), this.x, Math.fma(mat.m11(), this.y, mat.m21() * this.z));
        dest.x = rx;
        dest.y = ry;
        dest.z = this.z;
        return dest;
    }

    public Vector3d mul(Matrix3x2fc mat) {
        return this.mul(mat, this);
    }

    public Vector3d mul(Matrix3x2fc mat, Vector3d dest) {
        double rx = Math.fma((double)mat.m00(), this.x, Math.fma((double)mat.m10(), this.y, (double)mat.m20() * this.z));
        double ry = Math.fma((double)mat.m01(), this.x, Math.fma((double)mat.m11(), this.y, (double)mat.m21() * this.z));
        dest.x = rx;
        dest.y = ry;
        dest.z = this.z;
        return dest;
    }

    public Vector3d mulTranspose(Matrix3dc mat) {
        return this.mulTranspose(mat, this);
    }

    public Vector3d mulTranspose(Matrix3dc mat, Vector3d dest) {
        double rx = Math.fma(mat.m00(), this.x, Math.fma(mat.m01(), this.y, mat.m02() * this.z));
        double ry = Math.fma(mat.m10(), this.x, Math.fma(mat.m11(), this.y, mat.m12() * this.z));
        double rz = Math.fma(mat.m20(), this.x, Math.fma(mat.m21(), this.y, mat.m22() * this.z));
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    public Vector3d mulTranspose(Matrix3fc mat) {
        return this.mulTranspose(mat, this);
    }

    public Vector3d mulTranspose(Matrix3fc mat, Vector3d dest) {
        double rx = Math.fma((double)mat.m00(), this.x, Math.fma((double)mat.m01(), this.y, (double)mat.m02() * this.z));
        double ry = Math.fma((double)mat.m10(), this.x, Math.fma((double)mat.m11(), this.y, (double)mat.m12() * this.z));
        double rz = Math.fma((double)mat.m20(), this.x, Math.fma((double)mat.m21(), this.y, (double)mat.m22() * this.z));
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    public Vector3d mulPosition(Matrix4dc mat) {
        int prop = mat.properties();
        if ((prop & 4) != 0) {
            return this;
        }
        if ((prop & 8) != 0) {
            return this.mulPositionTranslation(mat, this);
        }
        return this.mulPositionGeneric(mat, this);
    }

    public Vector3d mulPosition(Matrix4dc mat, Vector3d dest) {
        int prop = mat.properties();
        if ((prop & 4) != 0) {
            return dest.set(this);
        }
        if ((prop & 8) != 0) {
            return this.mulPositionTranslation(mat, dest);
        }
        return this.mulPositionGeneric(mat, dest);
    }

    public Vector3d mulPosition(Matrix4fc mat, Vector3d dest) {
        int prop = mat.properties();
        if ((prop & 4) != 0) {
            return dest.set(this);
        }
        if ((prop & 8) != 0) {
            return this.mulPositionTranslation(mat, dest);
        }
        return this.mulPositionGeneric(mat, dest);
    }

    public Vector3d mulPosition(Matrix4fc mat) {
        int prop = mat.properties();
        if ((prop & 4) != 0) {
            return this;
        }
        if ((prop & 8) != 0) {
            return this.mulPositionTranslation(mat, this);
        }
        return this.mulPositionGeneric(mat, this);
    }

    public Vector3d mulPositionTranslation(Matrix4dc mat) {
        return this.mulPositionTranslation(mat, this);
    }

    public Vector3d mulPositionTranslation(Matrix4dc mat, Vector3d dest) {
        dest.x = this.x + mat.m30();
        dest.y = this.y + mat.m31();
        dest.z = this.z + mat.m32();
        return dest;
    }

    public Vector3d mulPositionTranslation(Matrix4fc mat) {
        return this.mulPositionTranslation(mat, this);
    }

    public Vector3d mulPositionTranslation(Matrix4fc mat, Vector3d dest) {
        dest.x = this.x + (double)mat.m30();
        dest.y = this.y + (double)mat.m31();
        dest.z = this.z + (double)mat.m32();
        return dest;
    }

    public Vector3d mulPositionGeneric(Matrix4dc mat) {
        return this.mulPositionGeneric(mat, this);
    }

    public Vector3d mulPositionGeneric(Matrix4dc mat, Vector3d dest) {
        double x = this.x;
        double y = this.y;
        double z = this.z;
        dest.x = Math.fma(mat.m00(), x, Math.fma(mat.m10(), y, Math.fma(mat.m20(), z, mat.m30())));
        dest.y = Math.fma(mat.m01(), x, Math.fma(mat.m11(), y, Math.fma(mat.m21(), z, mat.m31())));
        dest.z = Math.fma(mat.m02(), x, Math.fma(mat.m12(), y, Math.fma(mat.m22(), z, mat.m32())));
        return dest;
    }

    public Vector3d mulPositionGeneric(Matrix4fc mat) {
        return this.mulPositionGeneric(mat, this);
    }

    public Vector3d mulPositionGeneric(Matrix4fc mat, Vector3d dest) {
        double x = this.x;
        double y = this.y;
        double z = this.z;
        dest.x = Math.fma((double)mat.m00(), x, Math.fma((double)mat.m10(), y, Math.fma((double)mat.m20(), z, (double)mat.m30())));
        dest.y = Math.fma((double)mat.m01(), x, Math.fma((double)mat.m11(), y, Math.fma((double)mat.m21(), z, (double)mat.m31())));
        dest.z = Math.fma((double)mat.m02(), x, Math.fma((double)mat.m12(), y, Math.fma((double)mat.m22(), z, (double)mat.m32())));
        return dest;
    }

    public Vector3d mulPosition(Matrix4x3dc mat) {
        int prop = mat.properties();
        if ((prop & 4) != 0) {
            return this;
        }
        if ((prop & 8) != 0) {
            return this.mulPositionTranslation(mat, this);
        }
        return this.mulPositionGeneric(mat);
    }

    public Vector3d mulPositionTranslation(Matrix4x3dc mat, Vector3d dest) {
        double rx = Math.fma(mat.m00(), this.x, Math.fma(mat.m10(), this.y, Math.fma(mat.m20(), this.z, mat.m30())));
        double ry = Math.fma(mat.m01(), this.x, Math.fma(mat.m11(), this.y, Math.fma(mat.m21(), this.z, mat.m31())));
        double rz = Math.fma(mat.m02(), this.x, Math.fma(mat.m12(), this.y, Math.fma(mat.m22(), this.z, mat.m32())));
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    public Vector3d mulPositionTranslation(Matrix4x3dc mat) {
        return this.mulPositionTranslation(mat, this);
    }

    public Vector3d mulPositionGeneric(Matrix4x3dc mat, Vector3d dest) {
        double rx = Math.fma(mat.m00(), this.x, Math.fma(mat.m10(), this.y, Math.fma(mat.m20(), this.z, mat.m30())));
        double ry = Math.fma(mat.m01(), this.x, Math.fma(mat.m11(), this.y, Math.fma(mat.m21(), this.z, mat.m31())));
        double rz = Math.fma(mat.m02(), this.x, Math.fma(mat.m12(), this.y, Math.fma(mat.m22(), this.z, mat.m32())));
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    public Vector3d mulPositionGeneric(Matrix4x3dc mat) {
        return this.mulPositionGeneric(mat, this);
    }

    public Vector3d mulPosition(Matrix4x3fc mat) {
        int prop = mat.properties();
        if ((prop & 4) != 0) {
            return this;
        }
        if ((prop & 8) != 0) {
            return this.mulPositionTranslation(mat, this);
        }
        return this.mulPositionGeneric(mat, this);
    }

    public Vector3d mulPositionTranslation(Matrix4x3fc mat) {
        return this.mulPositionTranslation(mat, this);
    }

    public Vector3d mulPositionTranslation(Matrix4x3fc mat, Vector3d dest) {
        double rx = Math.fma((double)mat.m00(), this.x, Math.fma((double)mat.m10(), this.y, Math.fma((double)mat.m20(), this.z, (double)mat.m30())));
        double ry = Math.fma((double)mat.m01(), this.x, Math.fma((double)mat.m11(), this.y, Math.fma((double)mat.m21(), this.z, (double)mat.m31())));
        double rz = Math.fma((double)mat.m02(), this.x, Math.fma((double)mat.m12(), this.y, Math.fma((double)mat.m22(), this.z, (double)mat.m32())));
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    public Vector3d mulPositionGeneric(Matrix4x3fc mat) {
        return this.mulPositionGeneric(mat, this);
    }

    public Vector3d mulPositionGeneric(Matrix4x3fc mat, Vector3d dest) {
        double rx = Math.fma((double)mat.m00(), this.x, Math.fma((double)mat.m10(), this.y, Math.fma((double)mat.m20(), this.z, (double)mat.m30())));
        double ry = Math.fma((double)mat.m01(), this.x, Math.fma((double)mat.m11(), this.y, Math.fma((double)mat.m21(), this.z, (double)mat.m31())));
        double rz = Math.fma((double)mat.m02(), this.x, Math.fma((double)mat.m12(), this.y, Math.fma((double)mat.m22(), this.z, (double)mat.m32())));
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    public Vector3d mulPosition(Matrix4x3dc mat, Vector3d dest) {
        int prop = mat.properties();
        if ((prop & 4) != 0) {
            return dest.set(this);
        }
        if ((prop & 8) != 0) {
            return this.mulPositionTranslation(mat, dest);
        }
        return this.mulPositionGeneric(mat, dest);
    }

    public Vector3d mulPosition(Matrix4x3fc mat, Vector3d dest) {
        double rx = Math.fma((double)mat.m00(), this.x, Math.fma((double)mat.m10(), this.y, Math.fma((double)mat.m20(), this.z, (double)mat.m30())));
        double ry = Math.fma((double)mat.m01(), this.x, Math.fma((double)mat.m11(), this.y, Math.fma((double)mat.m21(), this.z, (double)mat.m31())));
        double rz = Math.fma((double)mat.m02(), this.x, Math.fma((double)mat.m12(), this.y, Math.fma((double)mat.m22(), this.z, (double)mat.m32())));
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    public Vector3d mulTransposePosition(Matrix4dc mat) {
        return this.mulTransposePosition(mat, this);
    }

    public Vector3d mulTransposePosition(Matrix4dc mat, Vector3d dest) {
        double rx = Math.fma(mat.m00(), this.x, Math.fma(mat.m01(), this.y, Math.fma(mat.m02(), this.z, mat.m03())));
        double ry = Math.fma(mat.m10(), this.x, Math.fma(mat.m11(), this.y, Math.fma(mat.m12(), this.z, mat.m13())));
        double rz = Math.fma(mat.m20(), this.x, Math.fma(mat.m21(), this.y, Math.fma(mat.m22(), this.z, mat.m23())));
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    public Vector3d mulTransposePosition(Matrix4fc mat) {
        return this.mulTransposePosition(mat, this);
    }

    public Vector3d mulTransposePosition(Matrix4fc mat, Vector3d dest) {
        double rx = Math.fma((double)mat.m00(), this.x, Math.fma((double)mat.m01(), this.y, Math.fma((double)mat.m02(), this.z, (double)mat.m03())));
        double ry = Math.fma((double)mat.m10(), this.x, Math.fma((double)mat.m11(), this.y, Math.fma((double)mat.m12(), this.z, (double)mat.m13())));
        double rz = Math.fma((double)mat.m20(), this.x, Math.fma((double)mat.m21(), this.y, Math.fma((double)mat.m22(), this.z, (double)mat.m23())));
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    public double mulPositionW(Matrix4fc mat) {
        return this.mulPositionW(mat, this);
    }

    public double mulPositionW(Matrix4fc mat, Vector3d dest) {
        double w = Math.fma((double)mat.m03(), this.x, Math.fma((double)mat.m13(), this.y, Math.fma((double)mat.m23(), this.z, (double)mat.m33())));
        double rx = Math.fma((double)mat.m00(), this.x, Math.fma((double)mat.m10(), this.y, Math.fma((double)mat.m20(), this.z, (double)mat.m30())));
        double ry = Math.fma((double)mat.m01(), this.x, Math.fma((double)mat.m11(), this.y, Math.fma((double)mat.m21(), this.z, (double)mat.m31())));
        double rz = Math.fma((double)mat.m02(), this.x, Math.fma((double)mat.m12(), this.y, Math.fma((double)mat.m22(), this.z, (double)mat.m32())));
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return w;
    }

    public double mulPositionW(Matrix4dc mat) {
        return this.mulPositionW(mat, this);
    }

    public double mulPositionW(Matrix4dc mat, Vector3d dest) {
        double w = Math.fma(mat.m03(), this.x, Math.fma(mat.m13(), this.y, Math.fma(mat.m23(), this.z, mat.m33())));
        double rx = Math.fma(mat.m00(), this.x, Math.fma(mat.m10(), this.y, Math.fma(mat.m20(), this.z, mat.m30())));
        double ry = Math.fma(mat.m01(), this.x, Math.fma(mat.m11(), this.y, Math.fma(mat.m21(), this.z, mat.m31())));
        double rz = Math.fma(mat.m02(), this.x, Math.fma(mat.m12(), this.y, Math.fma(mat.m22(), this.z, mat.m32())));
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return w;
    }

    public Vector3d mulDirection(Matrix4fc mat) {
        return this.mulDirection(mat, this);
    }

    public Vector3d mulDirection(Matrix4dc mat) {
        return this.mulDirection(mat, this);
    }

    public Vector3d mulDirection(Matrix4x3dc mat) {
        return this.mulDirection(mat, this);
    }

    public Vector3d mulDirection(Matrix4x3fc mat) {
        return this.mulDirection(mat, this);
    }

    public Vector3d mulDirection(Matrix4dc mat, Vector3d dest) {
        double rx = Math.fma(mat.m00(), this.x, Math.fma(mat.m10(), this.y, mat.m20() * this.z));
        double ry = Math.fma(mat.m01(), this.x, Math.fma(mat.m11(), this.y, mat.m21() * this.z));
        double rz = Math.fma(mat.m02(), this.x, Math.fma(mat.m12(), this.y, mat.m22() * this.z));
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    public Vector3d mulDirection(Matrix4fc mat, Vector3d dest) {
        double rx = Math.fma((double)mat.m00(), this.x, Math.fma((double)mat.m10(), this.y, (double)mat.m20() * this.z));
        double ry = Math.fma((double)mat.m01(), this.x, Math.fma((double)mat.m11(), this.y, (double)mat.m21() * this.z));
        double rz = Math.fma((double)mat.m02(), this.x, Math.fma((double)mat.m12(), this.y, (double)mat.m22() * this.z));
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    public Vector3d mulDirection(Matrix4x3dc mat, Vector3d dest) {
        double rx = Math.fma(mat.m00(), this.x, Math.fma(mat.m10(), this.y, mat.m20() * this.z));
        double ry = Math.fma(mat.m01(), this.x, Math.fma(mat.m11(), this.y, mat.m21() * this.z));
        double rz = Math.fma(mat.m02(), this.x, Math.fma(mat.m12(), this.y, mat.m22() * this.z));
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    public Vector3d mulDirection(Matrix4x3fc mat, Vector3d dest) {
        double rx = Math.fma((double)mat.m00(), this.x, Math.fma((double)mat.m10(), this.y, (double)mat.m20() * this.z));
        double ry = Math.fma((double)mat.m01(), this.x, Math.fma((double)mat.m11(), this.y, (double)mat.m21() * this.z));
        double rz = Math.fma((double)mat.m02(), this.x, Math.fma((double)mat.m12(), this.y, (double)mat.m22() * this.z));
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    public Vector3d mulTransposeDirection(Matrix4dc mat) {
        return this.mulTransposeDirection(mat, this);
    }

    public Vector3d mulTransposeDirection(Matrix4dc mat, Vector3d dest) {
        double rx = Math.fma(mat.m00(), this.x, Math.fma(mat.m01(), this.y, mat.m02() * this.z));
        double ry = Math.fma(mat.m10(), this.x, Math.fma(mat.m11(), this.y, mat.m12() * this.z));
        double rz = Math.fma(mat.m20(), this.x, Math.fma(mat.m21(), this.y, mat.m22() * this.z));
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    public Vector3d mulTransposeDirection(Matrix4fc mat) {
        return this.mulTransposeDirection(mat, this);
    }

    public Vector3d mulTransposeDirection(Matrix4fc mat, Vector3d dest) {
        double rx = Math.fma((double)mat.m00(), this.x, Math.fma((double)mat.m01(), this.y, (double)mat.m02() * this.z));
        double ry = Math.fma((double)mat.m10(), this.x, Math.fma((double)mat.m11(), this.y, (double)mat.m12() * this.z));
        double rz = Math.fma((double)mat.m20(), this.x, Math.fma((double)mat.m21(), this.y, (double)mat.m22() * this.z));
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    public Vector3d mul(double scalar) {
        return this.mul(scalar, this);
    }

    public Vector3d mul(double scalar, Vector3d dest) {
        dest.x = this.x * scalar;
        dest.y = this.y * scalar;
        dest.z = this.z * scalar;
        return dest;
    }

    public Vector3d mul(double x, double y, double z) {
        return this.mul(x, y, z, this);
    }

    public Vector3d mul(double x, double y, double z, Vector3d dest) {
        dest.x = this.x * x;
        dest.y = this.y * y;
        dest.z = this.z * z;
        return dest;
    }

    public Vector3d rotate(Quaterniondc quat) {
        return quat.transform(this, this);
    }

    public Vector3d rotate(Quaterniondc quat, Vector3d dest) {
        return quat.transform(this, dest);
    }

    public Quaterniond rotationTo(Vector3dc toDir, Quaterniond dest) {
        return dest.rotationTo(this, toDir);
    }

    public Quaterniond rotationTo(double toDirX, double toDirY, double toDirZ, Quaterniond dest) {
        return dest.rotationTo(this.x, this.y, this.z, toDirX, toDirY, toDirZ);
    }

    public Vector3d rotateAxis(double angle, double x, double y, double z) {
        return this.rotateAxis(angle, x, y, z, this);
    }

    public Vector3d rotateAxis(double angle, double aX, double aY, double aZ, Vector3d dest) {
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

    private Vector3d rotateAxisInternal(double angle, double aX, double aY, double aZ, Vector3d dest) {
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

    public Vector3d rotateX(double angle) {
        return this.rotateX(angle, this);
    }

    public Vector3d rotateX(double angle, Vector3d dest) {
        double sin = Math.sin(angle);
        double cos = Math.cosFromSin(sin, angle);
        double y = this.y * cos - this.z * sin;
        double z = this.y * sin + this.z * cos;
        dest.x = this.x;
        dest.y = y;
        dest.z = z;
        return dest;
    }

    public Vector3d rotateY(double angle) {
        return this.rotateY(angle, this);
    }

    public Vector3d rotateY(double angle, Vector3d dest) {
        double sin = Math.sin(angle);
        double cos = Math.cosFromSin(sin, angle);
        double x = this.x * cos + this.z * sin;
        double z = -this.x * sin + this.z * cos;
        dest.x = x;
        dest.y = this.y;
        dest.z = z;
        return dest;
    }

    public Vector3d rotateZ(double angle) {
        return this.rotateZ(angle, this);
    }

    public Vector3d rotateZ(double angle, Vector3d dest) {
        double sin = Math.sin(angle);
        double cos = Math.cosFromSin(sin, angle);
        double x = this.x * cos - this.y * sin;
        double y = this.x * sin + this.y * cos;
        dest.x = x;
        dest.y = y;
        dest.z = this.z;
        return dest;
    }

    public Vector3d div(double scalar) {
        return this.div(scalar, this);
    }

    public Vector3d div(double scalar, Vector3d dest) {
        double inv = 1.0 / scalar;
        dest.x = this.x * inv;
        dest.y = this.y * inv;
        dest.z = this.z * inv;
        return dest;
    }

    public Vector3d div(double x, double y, double z) {
        return this.div(x, y, z, this);
    }

    public Vector3d div(double x, double y, double z, Vector3d dest) {
        dest.x = this.x / x;
        dest.y = this.y / y;
        dest.z = this.z / z;
        return dest;
    }

    public double lengthSquared() {
        return Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z));
    }

    public static double lengthSquared(double x, double y, double z) {
        return Math.fma(x, x, Math.fma(y, y, z * z));
    }

    public double length() {
        return Math.sqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z)));
    }

    public static double length(double x, double y, double z) {
        return Math.sqrt(Math.fma(x, x, Math.fma(y, y, z * z)));
    }

    public Vector3d normalize() {
        return this.normalize(this);
    }

    public Vector3d normalize(Vector3d dest) {
        double invLength = Math.invsqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z)));
        dest.x = this.x * invLength;
        dest.y = this.y * invLength;
        dest.z = this.z * invLength;
        return dest;
    }

    public Vector3d normalize(double length) {
        return this.normalize(length, this);
    }

    public Vector3d normalize(double length, Vector3d dest) {
        double invLength = Math.invsqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z))) * length;
        dest.x = this.x * invLength;
        dest.y = this.y * invLength;
        dest.z = this.z * invLength;
        return dest;
    }

    public Vector3d cross(Vector3dc v) {
        return this.cross(v, this);
    }

    public Vector3d cross(double x, double y, double z) {
        return this.cross(x, y, z, this);
    }

    public Vector3d cross(Vector3dc v, Vector3d dest) {
        double rx = Math.fma(this.y, v.z(), -this.z * v.y());
        double ry = Math.fma(this.z, v.x(), -this.x * v.z());
        double rz = Math.fma(this.x, v.y(), -this.y * v.x());
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    public Vector3d cross(double x, double y, double z, Vector3d dest) {
        double rx = Math.fma(this.y, z, -this.z * y);
        double ry = Math.fma(this.z, x, -this.x * z);
        double rz = Math.fma(this.x, y, -this.y * x);
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    public double distance(Vector3dc v) {
        double dx = this.x - v.x();
        double dy = this.y - v.y();
        double dz = this.z - v.z();
        return Math.sqrt(Math.fma(dx, dx, Math.fma(dy, dy, dz * dz)));
    }

    public double distance(double x, double y, double z) {
        double dx = this.x - x;
        double dy = this.y - y;
        double dz = this.z - z;
        return Math.sqrt(Math.fma(dx, dx, Math.fma(dy, dy, dz * dz)));
    }

    public double distanceSquared(Vector3dc v) {
        double dx = this.x - v.x();
        double dy = this.y - v.y();
        double dz = this.z - v.z();
        return Math.fma(dx, dx, Math.fma(dy, dy, dz * dz));
    }

    public double distanceSquared(double x, double y, double z) {
        double dx = this.x - x;
        double dy = this.y - y;
        double dz = this.z - z;
        return Math.fma(dx, dx, Math.fma(dy, dy, dz * dz));
    }

    public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Math.sqrt(Vector3d.distanceSquared(x1, y1, z1, x2, y2, z2));
    }

    public static double distanceSquared(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double dz = z1 - z2;
        return Math.fma(dx, dx, Math.fma(dy, dy, dz * dz));
    }

    public double dot(Vector3dc v) {
        return Math.fma(this.x, v.x(), Math.fma(this.y, v.y(), this.z * v.z()));
    }

    public double dot(double x, double y, double z) {
        return Math.fma(this.x, x, Math.fma(this.y, y, this.z * z));
    }

    public double angleCos(Vector3dc v) {
        double length1Squared = Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z));
        double length2Squared = Math.fma(v.x(), v.x(), Math.fma(v.y(), v.y(), v.z() * v.z()));
        double dot = Math.fma(this.x, v.x(), Math.fma(this.y, v.y(), this.z * v.z()));
        return dot / Math.sqrt(length1Squared * length2Squared);
    }

    public double angle(Vector3dc v) {
        double cos = this.angleCos(v);
        cos = cos < 1.0 ? cos : 1.0;
        cos = cos > -1.0 ? cos : -1.0;
        return Math.acos(cos);
    }

    public double angleSigned(Vector3dc v, Vector3dc n) {
        double x = v.x();
        double y = v.y();
        double z = v.z();
        return Math.atan2((this.y * z - this.z * y) * n.x() + (this.z * x - this.x * z) * n.y() + (this.x * y - this.y * x) * n.z(), this.x * x + this.y * y + this.z * z);
    }

    public double angleSigned(double x, double y, double z, double nx, double ny, double nz) {
        return Math.atan2((this.y * z - this.z * y) * nx + (this.z * x - this.x * z) * ny + (this.x * y - this.y * x) * nz, this.x * x + this.y * y + this.z * z);
    }

    public Vector3d min(Vector3dc v) {
        return this.min(v, this);
    }

    public Vector3d min(Vector3dc v, Vector3d dest) {
        dest.x = this.x < v.x() ? this.x : v.x();
        dest.y = this.y < v.y() ? this.y : v.y();
        dest.z = this.z < v.z() ? this.z : v.z();
        return dest;
    }

    public Vector3d max(Vector3dc v) {
        return this.max(v, this);
    }

    public Vector3d max(Vector3dc v, Vector3d dest) {
        dest.x = this.x > v.x() ? this.x : v.x();
        dest.y = this.y > v.y() ? this.y : v.y();
        dest.z = this.z > v.z() ? this.z : v.z();
        return dest;
    }

    public Vector3d zero() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
        return this;
    }

    public String toString() {
        return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
    }

    public String toString(NumberFormat formatter) {
        return "(" + Runtime.format(this.x, formatter) + " " + Runtime.format(this.y, formatter) + " " + Runtime.format(this.z, formatter) + ")";
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeDouble(this.x);
        out.writeDouble(this.y);
        out.writeDouble(this.z);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.x = in.readDouble();
        this.y = in.readDouble();
        this.z = in.readDouble();
    }

    public Vector3d negate() {
        return this.negate(this);
    }

    public Vector3d negate(Vector3d dest) {
        dest.x = -this.x;
        dest.y = -this.y;
        dest.z = -this.z;
        return dest;
    }

    public Vector3d absolute() {
        return this.absolute(this);
    }

    public Vector3d absolute(Vector3d dest) {
        dest.x = Math.abs(this.x);
        dest.y = Math.abs(this.y);
        dest.z = Math.abs(this.z);
        return dest;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        long temp = Double.doubleToLongBits(this.x);
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
        Vector3d other = (Vector3d)obj;
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
            return false;
        }
        if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
            return false;
        }
        return Double.doubleToLongBits(this.z) == Double.doubleToLongBits(other.z);
    }

    public boolean equals(Vector3dc v, double delta) {
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
        return Runtime.equals(this.z, v.z(), delta);
    }

    public boolean equals(double x, double y, double z) {
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(x)) {
            return false;
        }
        if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(y)) {
            return false;
        }
        return Double.doubleToLongBits(this.z) == Double.doubleToLongBits(z);
    }

    public Vector3d reflect(Vector3dc normal) {
        double x = normal.x();
        double y = normal.y();
        double z = normal.z();
        double dot = Math.fma(this.x, x, Math.fma(this.y, y, this.z * z));
        this.x -= (dot + dot) * x;
        this.y -= (dot + dot) * y;
        this.z -= (dot + dot) * z;
        return this;
    }

    public Vector3d reflect(double x, double y, double z) {
        double dot = Math.fma(this.x, x, Math.fma(this.y, y, this.z * z));
        this.x -= (dot + dot) * x;
        this.y -= (dot + dot) * y;
        this.z -= (dot + dot) * z;
        return this;
    }

    public Vector3d reflect(Vector3dc normal, Vector3d dest) {
        double x = normal.x();
        double y = normal.y();
        double z = normal.z();
        double dot = Math.fma(this.x, x, Math.fma(this.y, y, this.z * z));
        dest.x = this.x - (dot + dot) * x;
        dest.y = this.y - (dot + dot) * y;
        dest.z = this.z - (dot + dot) * z;
        return dest;
    }

    public Vector3d reflect(double x, double y, double z, Vector3d dest) {
        double dot = Math.fma(this.x, x, Math.fma(this.y, y, this.z * z));
        dest.x = this.x - (dot + dot) * x;
        dest.y = this.y - (dot + dot) * y;
        dest.z = this.z - (dot + dot) * z;
        return dest;
    }

    public Vector3d half(Vector3dc other) {
        return this.set(this).add(other.x(), other.y(), other.z()).normalize();
    }

    public Vector3d half(double x, double y, double z) {
        return this.set(this).add(x, y, z).normalize();
    }

    public Vector3d half(Vector3dc other, Vector3d dest) {
        return dest.set(this).add(other.x(), other.y(), other.z()).normalize();
    }

    public Vector3d half(double x, double y, double z, Vector3d dest) {
        return dest.set(this).add(x, y, z).normalize();
    }

    public Vector3d smoothStep(Vector3dc v, double t, Vector3d dest) {
        double t2 = t * t;
        double t3 = t2 * t;
        dest.x = (this.x + this.x - v.x() - v.x()) * t3 + (3.0 * v.x() - 3.0 * this.x) * t2 + this.x * t + this.x;
        dest.y = (this.y + this.y - v.y() - v.y()) * t3 + (3.0 * v.y() - 3.0 * this.y) * t2 + this.y * t + this.y;
        dest.z = (this.z + this.z - v.z() - v.z()) * t3 + (3.0 * v.z() - 3.0 * this.z) * t2 + this.z * t + this.z;
        return dest;
    }

    public Vector3d hermite(Vector3dc t0, Vector3dc v1, Vector3dc t1, double t, Vector3d dest) {
        double t2 = t * t;
        double t3 = t2 * t;
        dest.x = (this.x + this.x - v1.x() - v1.x() + t1.x() + t0.x()) * t3 + (3.0 * v1.x() - 3.0 * this.x - t0.x() - t0.x() - t1.x()) * t2 + this.x * t + this.x;
        dest.y = (this.y + this.y - v1.y() - v1.y() + t1.y() + t0.y()) * t3 + (3.0 * v1.y() - 3.0 * this.y - t0.y() - t0.y() - t1.y()) * t2 + this.y * t + this.y;
        dest.z = (this.z + this.z - v1.z() - v1.z() + t1.z() + t0.z()) * t3 + (3.0 * v1.z() - 3.0 * this.z - t0.z() - t0.z() - t1.z()) * t2 + this.z * t + this.z;
        return dest;
    }

    public Vector3d lerp(Vector3dc other, double t) {
        this.x = Math.fma(other.x() - this.x, t, this.x);
        this.y = Math.fma(other.y() - this.y, t, this.y);
        this.z = Math.fma(other.z() - this.z, t, this.z);
        return this;
    }

    public Vector3d lerp(Vector3dc other, double t, Vector3d dest) {
        dest.x = Math.fma(other.x() - this.x, t, this.x);
        dest.y = Math.fma(other.y() - this.y, t, this.y);
        dest.z = Math.fma(other.z() - this.z, t, this.z);
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
        }
        throw new IllegalArgumentException();
    }

    public Vector3i get(int mode, Vector3i dest) {
        dest.x = Math.roundUsing(this.x(), mode);
        dest.y = Math.roundUsing(this.y(), mode);
        dest.z = Math.roundUsing(this.z(), mode);
        return dest;
    }

    public Vector3f get(Vector3f dest) {
        dest.x = (float)this.x();
        dest.y = (float)this.y();
        dest.z = (float)this.z();
        return dest;
    }

    public Vector3d get(Vector3d dest) {
        dest.x = this.x();
        dest.y = this.y();
        dest.z = this.z();
        return dest;
    }

    public int maxComponent() {
        double absX = Math.abs(this.x);
        double absY = Math.abs(this.y);
        double absZ = Math.abs(this.z);
        if (absX >= absY && absX >= absZ) {
            return 0;
        }
        if (absY >= absZ) {
            return 1;
        }
        return 2;
    }

    public int minComponent() {
        double absX = Math.abs(this.x);
        double absY = Math.abs(this.y);
        double absZ = Math.abs(this.z);
        if (absX < absY && absX < absZ) {
            return 0;
        }
        if (absY < absZ) {
            return 1;
        }
        return 2;
    }

    public Vector3d orthogonalize(Vector3dc v, Vector3d dest) {
        double rz;
        double ry;
        double rx;
        if (Math.abs(v.x()) > Math.abs(v.z())) {
            rx = -v.y();
            ry = v.x();
            rz = 0.0;
        } else {
            rx = 0.0;
            ry = -v.z();
            rz = v.y();
        }
        double invLen = Math.invsqrt(rx * rx + ry * ry + rz * rz);
        dest.x = rx * invLen;
        dest.y = ry * invLen;
        dest.z = rz * invLen;
        return dest;
    }

    public Vector3d orthogonalize(Vector3dc v) {
        return this.orthogonalize(v, this);
    }

    public Vector3d orthogonalizeUnit(Vector3dc v, Vector3d dest) {
        return this.orthogonalize(v, dest);
    }

    public Vector3d orthogonalizeUnit(Vector3dc v) {
        return this.orthogonalizeUnit(v, this);
    }

    public Vector3d floor() {
        this.x = Math.floor(this.x);
        this.y = Math.floor(this.y);
        this.z = Math.floor(this.z);
        return this;
    }

    public Vector3d floor(Vector3d dest) {
        dest.x = Math.floor(this.x);
        dest.y = Math.floor(this.y);
        dest.z = Math.floor(this.z);
        return dest;
    }

    public Vector3d ceil() {
        this.x = Math.ceil(this.x);
        this.y = Math.ceil(this.y);
        this.z = Math.ceil(this.z);
        return this;
    }

    public Vector3d ceil(Vector3d dest) {
        dest.x = Math.ceil(this.x);
        dest.y = Math.ceil(this.y);
        dest.z = Math.ceil(this.z);
        return dest;
    }

    public Vector3d round() {
        this.x = Math.round(this.x);
        this.y = Math.round(this.y);
        this.z = Math.round(this.z);
        return this;
    }

    public Vector3d round(Vector3d dest) {
        dest.x = Math.round(this.x);
        dest.y = Math.round(this.y);
        dest.z = Math.round(this.z);
        return dest;
    }

    public boolean isFinite() {
        return Math.isFinite(this.x) && Math.isFinite(this.y) && Math.isFinite(this.z);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

