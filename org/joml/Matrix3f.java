/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.NumberFormat;
import org.joml.AxisAngle4d;
import org.joml.AxisAngle4f;
import org.joml.Math;
import org.joml.Matrix2fc;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Matrix4x3fc;
import org.joml.MemUtil;
import org.joml.Options;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Runtime;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Matrix3f
implements Externalizable,
Cloneable,
Matrix3fc {
    private static final long serialVersionUID = 1L;
    public float m00;
    public float m01;
    public float m02;
    public float m10;
    public float m11;
    public float m12;
    public float m20;
    public float m21;
    public float m22;

    public Matrix3f() {
        this.m00 = 1.0f;
        this.m11 = 1.0f;
        this.m22 = 1.0f;
    }

    public Matrix3f(Matrix2fc mat) {
        this.set(mat);
    }

    public Matrix3f(Matrix3fc mat) {
        this.set(mat);
    }

    public Matrix3f(Matrix4fc mat) {
        this.set(mat);
    }

    public Matrix3f(float m00, float m01, float m02, float m10, float m11, float m12, float m20, float m21, float m22) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
    }

    public Matrix3f(FloatBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
    }

    public Matrix3f(Vector3fc col0, Vector3fc col1, Vector3fc col2) {
        this.set(col0, col1, col2);
    }

    public float m00() {
        return this.m00;
    }

    public float m01() {
        return this.m01;
    }

    public float m02() {
        return this.m02;
    }

    public float m10() {
        return this.m10;
    }

    public float m11() {
        return this.m11;
    }

    public float m12() {
        return this.m12;
    }

    public float m20() {
        return this.m20;
    }

    public float m21() {
        return this.m21;
    }

    public float m22() {
        return this.m22;
    }

    public Matrix3f m00(float m00) {
        this.m00 = m00;
        return this;
    }

    public Matrix3f m01(float m01) {
        this.m01 = m01;
        return this;
    }

    public Matrix3f m02(float m02) {
        this.m02 = m02;
        return this;
    }

    public Matrix3f m10(float m10) {
        this.m10 = m10;
        return this;
    }

    public Matrix3f m11(float m11) {
        this.m11 = m11;
        return this;
    }

    public Matrix3f m12(float m12) {
        this.m12 = m12;
        return this;
    }

    public Matrix3f m20(float m20) {
        this.m20 = m20;
        return this;
    }

    public Matrix3f m21(float m21) {
        this.m21 = m21;
        return this;
    }

    public Matrix3f m22(float m22) {
        this.m22 = m22;
        return this;
    }

    Matrix3f _m00(float m00) {
        this.m00 = m00;
        return this;
    }

    Matrix3f _m01(float m01) {
        this.m01 = m01;
        return this;
    }

    Matrix3f _m02(float m02) {
        this.m02 = m02;
        return this;
    }

    Matrix3f _m10(float m10) {
        this.m10 = m10;
        return this;
    }

    Matrix3f _m11(float m11) {
        this.m11 = m11;
        return this;
    }

    Matrix3f _m12(float m12) {
        this.m12 = m12;
        return this;
    }

    Matrix3f _m20(float m20) {
        this.m20 = m20;
        return this;
    }

    Matrix3f _m21(float m21) {
        this.m21 = m21;
        return this;
    }

    Matrix3f _m22(float m22) {
        this.m22 = m22;
        return this;
    }

    public Matrix3f set(Matrix3fc m) {
        if (m == this) {
            return this;
        }
        return this._m00(m.m00())._m01(m.m01())._m02(m.m02())._m10(m.m10())._m11(m.m11())._m12(m.m12())._m20(m.m20())._m21(m.m21())._m22(m.m22());
    }

    public Matrix3f setTransposed(Matrix3fc m) {
        float nm10 = m.m01();
        float nm12 = m.m21();
        float nm20 = m.m02();
        float nm21 = m.m12();
        return this._m00(m.m00())._m01(m.m10())._m02(m.m20())._m10(nm10)._m11(m.m11())._m12(nm12)._m20(nm20)._m21(nm21)._m22(m.m22());
    }

    public Matrix3f set(Matrix4x3fc m) {
        this.m00 = m.m00();
        this.m01 = m.m01();
        this.m02 = m.m02();
        this.m10 = m.m10();
        this.m11 = m.m11();
        this.m12 = m.m12();
        this.m20 = m.m20();
        this.m21 = m.m21();
        this.m22 = m.m22();
        return this;
    }

    public Matrix3f set(Matrix4fc mat) {
        this.m00 = mat.m00();
        this.m01 = mat.m01();
        this.m02 = mat.m02();
        this.m10 = mat.m10();
        this.m11 = mat.m11();
        this.m12 = mat.m12();
        this.m20 = mat.m20();
        this.m21 = mat.m21();
        this.m22 = mat.m22();
        return this;
    }

    public Matrix3f set(Matrix2fc mat) {
        this.m00 = mat.m00();
        this.m01 = mat.m01();
        this.m02 = 0.0f;
        this.m10 = mat.m10();
        this.m11 = mat.m11();
        this.m12 = 0.0f;
        this.m20 = 0.0f;
        this.m21 = 0.0f;
        this.m22 = 1.0f;
        return this;
    }

    public Matrix3f set(AxisAngle4f axisAngle) {
        float x = axisAngle.x;
        float y = axisAngle.y;
        float z = axisAngle.z;
        float angle = axisAngle.angle;
        float invLength = Math.invsqrt(x * x + y * y + z * z);
        x *= invLength;
        y *= invLength;
        z *= invLength;
        float s = Math.sin(angle);
        float c = Math.cosFromSin(s, angle);
        float omc = 1.0f - c;
        this.m00 = c + x * x * omc;
        this.m11 = c + y * y * omc;
        this.m22 = c + z * z * omc;
        float tmp1 = x * y * omc;
        float tmp2 = z * s;
        this.m10 = tmp1 - tmp2;
        this.m01 = tmp1 + tmp2;
        tmp1 = x * z * omc;
        tmp2 = y * s;
        this.m20 = tmp1 + tmp2;
        this.m02 = tmp1 - tmp2;
        tmp1 = y * z * omc;
        tmp2 = x * s;
        this.m21 = tmp1 - tmp2;
        this.m12 = tmp1 + tmp2;
        return this;
    }

    public Matrix3f set(AxisAngle4d axisAngle) {
        double x = axisAngle.x;
        double y = axisAngle.y;
        double z = axisAngle.z;
        double angle = axisAngle.angle;
        double invLength = Math.invsqrt(x * x + y * y + z * z);
        x *= invLength;
        y *= invLength;
        z *= invLength;
        double s = Math.sin(angle);
        double c = Math.cosFromSin(s, angle);
        double omc = 1.0 - c;
        this.m00 = (float)(c + x * x * omc);
        this.m11 = (float)(c + y * y * omc);
        this.m22 = (float)(c + z * z * omc);
        double tmp1 = x * y * omc;
        double tmp2 = z * s;
        this.m10 = (float)(tmp1 - tmp2);
        this.m01 = (float)(tmp1 + tmp2);
        tmp1 = x * z * omc;
        tmp2 = y * s;
        this.m20 = (float)(tmp1 + tmp2);
        this.m02 = (float)(tmp1 - tmp2);
        tmp1 = y * z * omc;
        tmp2 = x * s;
        this.m21 = (float)(tmp1 - tmp2);
        this.m12 = (float)(tmp1 + tmp2);
        return this;
    }

    public Matrix3f set(Quaternionfc q) {
        return this.rotation(q);
    }

    public Matrix3f set(Quaterniondc q) {
        double w2 = q.w() * q.w();
        double x2 = q.x() * q.x();
        double y2 = q.y() * q.y();
        double z2 = q.z() * q.z();
        double zw = q.z() * q.w();
        double xy = q.x() * q.y();
        double xz = q.x() * q.z();
        double yw = q.y() * q.w();
        double yz = q.y() * q.z();
        double xw = q.x() * q.w();
        this.m00 = (float)(w2 + x2 - z2 - y2);
        this.m01 = (float)(xy + zw + zw + xy);
        this.m02 = (float)(xz - yw + xz - yw);
        this.m10 = (float)(-zw + xy - zw + xy);
        this.m11 = (float)(y2 - z2 + w2 - x2);
        this.m12 = (float)(yz + yz + xw + xw);
        this.m20 = (float)(yw + xz + xz + yw);
        this.m21 = (float)(yz + yz - xw - xw);
        this.m22 = (float)(z2 - y2 - x2 + w2);
        return this;
    }

    public Matrix3f mul(Matrix3fc right) {
        return this.mul(right, this);
    }

    public Matrix3f mul(Matrix3fc right, Matrix3f dest) {
        float nm00 = Math.fma(this.m00, right.m00(), Math.fma(this.m10, right.m01(), this.m20 * right.m02()));
        float nm01 = Math.fma(this.m01, right.m00(), Math.fma(this.m11, right.m01(), this.m21 * right.m02()));
        float nm02 = Math.fma(this.m02, right.m00(), Math.fma(this.m12, right.m01(), this.m22 * right.m02()));
        float nm10 = Math.fma(this.m00, right.m10(), Math.fma(this.m10, right.m11(), this.m20 * right.m12()));
        float nm11 = Math.fma(this.m01, right.m10(), Math.fma(this.m11, right.m11(), this.m21 * right.m12()));
        float nm12 = Math.fma(this.m02, right.m10(), Math.fma(this.m12, right.m11(), this.m22 * right.m12()));
        float nm20 = Math.fma(this.m00, right.m20(), Math.fma(this.m10, right.m21(), this.m20 * right.m22()));
        float nm21 = Math.fma(this.m01, right.m20(), Math.fma(this.m11, right.m21(), this.m21 * right.m22()));
        float nm22 = Math.fma(this.m02, right.m20(), Math.fma(this.m12, right.m21(), this.m22 * right.m22()));
        dest.m00 = nm00;
        dest.m01 = nm01;
        dest.m02 = nm02;
        dest.m10 = nm10;
        dest.m11 = nm11;
        dest.m12 = nm12;
        dest.m20 = nm20;
        dest.m21 = nm21;
        dest.m22 = nm22;
        return dest;
    }

    public Matrix3f mulLocal(Matrix3fc left) {
        return this.mulLocal(left, this);
    }

    public Matrix3f mulLocal(Matrix3fc left, Matrix3f dest) {
        float nm00 = left.m00() * this.m00 + left.m10() * this.m01 + left.m20() * this.m02;
        float nm01 = left.m01() * this.m00 + left.m11() * this.m01 + left.m21() * this.m02;
        float nm02 = left.m02() * this.m00 + left.m12() * this.m01 + left.m22() * this.m02;
        float nm10 = left.m00() * this.m10 + left.m10() * this.m11 + left.m20() * this.m12;
        float nm11 = left.m01() * this.m10 + left.m11() * this.m11 + left.m21() * this.m12;
        float nm12 = left.m02() * this.m10 + left.m12() * this.m11 + left.m22() * this.m12;
        float nm20 = left.m00() * this.m20 + left.m10() * this.m21 + left.m20() * this.m22;
        float nm21 = left.m01() * this.m20 + left.m11() * this.m21 + left.m21() * this.m22;
        float nm22 = left.m02() * this.m20 + left.m12() * this.m21 + left.m22() * this.m22;
        dest.m00 = nm00;
        dest.m01 = nm01;
        dest.m02 = nm02;
        dest.m10 = nm10;
        dest.m11 = nm11;
        dest.m12 = nm12;
        dest.m20 = nm20;
        dest.m21 = nm21;
        dest.m22 = nm22;
        return dest;
    }

    public Matrix3f set(float m00, float m01, float m02, float m10, float m11, float m12, float m20, float m21, float m22) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        return this;
    }

    public Matrix3f set(float[] m) {
        return this.set(m, 0);
    }

    public Matrix3f set(float[] m, int off) {
        MemUtil.INSTANCE.copy(m, off, this);
        return this;
    }

    public Matrix3f set(Vector3fc col0, Vector3fc col1, Vector3fc col2) {
        this.m00 = col0.x();
        this.m01 = col0.y();
        this.m02 = col0.z();
        this.m10 = col1.x();
        this.m11 = col1.y();
        this.m12 = col1.z();
        this.m20 = col2.x();
        this.m21 = col2.y();
        this.m22 = col2.z();
        return this;
    }

    public float determinant() {
        return (this.m00 * this.m11 - this.m01 * this.m10) * this.m22 + (this.m02 * this.m10 - this.m00 * this.m12) * this.m21 + (this.m01 * this.m12 - this.m02 * this.m11) * this.m20;
    }

    public Matrix3f invert() {
        return this.invert(this);
    }

    public Matrix3f invert(Matrix3f dest) {
        float a = Math.fma(this.m00, this.m11, -this.m01 * this.m10);
        float b = Math.fma(this.m02, this.m10, -this.m00 * this.m12);
        float c = Math.fma(this.m01, this.m12, -this.m02 * this.m11);
        float d = Math.fma(a, this.m22, Math.fma(b, this.m21, c * this.m20));
        float s = 1.0f / d;
        float nm00 = Math.fma(this.m11, this.m22, -this.m21 * this.m12) * s;
        float nm01 = Math.fma(this.m21, this.m02, -this.m01 * this.m22) * s;
        float nm02 = c * s;
        float nm10 = Math.fma(this.m20, this.m12, -this.m10 * this.m22) * s;
        float nm11 = Math.fma(this.m00, this.m22, -this.m20 * this.m02) * s;
        float nm12 = b * s;
        float nm20 = Math.fma(this.m10, this.m21, -this.m20 * this.m11) * s;
        float nm21 = Math.fma(this.m20, this.m01, -this.m00 * this.m21) * s;
        float nm22 = a * s;
        dest.m00 = nm00;
        dest.m01 = nm01;
        dest.m02 = nm02;
        dest.m10 = nm10;
        dest.m11 = nm11;
        dest.m12 = nm12;
        dest.m20 = nm20;
        dest.m21 = nm21;
        dest.m22 = nm22;
        return dest;
    }

    public Matrix3f transpose() {
        return this.transpose(this);
    }

    public Matrix3f transpose(Matrix3f dest) {
        return dest.set(this.m00, this.m10, this.m20, this.m01, this.m11, this.m21, this.m02, this.m12, this.m22);
    }

    public String toString() {
        String str = this.toString(Options.NUMBER_FORMAT);
        StringBuffer res = new StringBuffer();
        int eIndex = Integer.MIN_VALUE;
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            if (c == 'E') {
                eIndex = i;
            } else {
                if (c == ' ' && eIndex == i - 1) {
                    res.append('+');
                    continue;
                }
                if (Character.isDigit(c) && eIndex == i - 1) {
                    res.append('+');
                }
            }
            res.append(c);
        }
        return res.toString();
    }

    public String toString(NumberFormat formatter) {
        return Runtime.format(this.m00, formatter) + " " + Runtime.format(this.m10, formatter) + " " + Runtime.format(this.m20, formatter) + "\n" + Runtime.format(this.m01, formatter) + " " + Runtime.format(this.m11, formatter) + " " + Runtime.format(this.m21, formatter) + "\n" + Runtime.format(this.m02, formatter) + " " + Runtime.format(this.m12, formatter) + " " + Runtime.format(this.m22, formatter) + "\n";
    }

    public Matrix3f get(Matrix3f dest) {
        return dest.set(this);
    }

    public Matrix4f get(Matrix4f dest) {
        return dest.set(this);
    }

    public AxisAngle4f getRotation(AxisAngle4f dest) {
        return dest.set(this);
    }

    public Quaternionf getUnnormalizedRotation(Quaternionf dest) {
        return dest.setFromUnnormalized(this);
    }

    public Quaternionf getNormalizedRotation(Quaternionf dest) {
        return dest.setFromNormalized(this);
    }

    public Quaterniond getUnnormalizedRotation(Quaterniond dest) {
        return dest.setFromUnnormalized(this);
    }

    public Quaterniond getNormalizedRotation(Quaterniond dest) {
        return dest.setFromNormalized(this);
    }

    public FloatBuffer get(FloatBuffer buffer) {
        return this.get(buffer.position(), buffer);
    }

    public FloatBuffer get(int index, FloatBuffer buffer) {
        MemUtil.INSTANCE.put(this, index, buffer);
        return buffer;
    }

    public ByteBuffer get(ByteBuffer buffer) {
        return this.get(buffer.position(), buffer);
    }

    public ByteBuffer get(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.put(this, index, buffer);
        return buffer;
    }

    public FloatBuffer get3x4(FloatBuffer buffer) {
        return this.get3x4(buffer.position(), buffer);
    }

    public FloatBuffer get3x4(int index, FloatBuffer buffer) {
        MemUtil.INSTANCE.put3x4(this, index, buffer);
        return buffer;
    }

    public ByteBuffer get3x4(ByteBuffer buffer) {
        return this.get3x4(buffer.position(), buffer);
    }

    public ByteBuffer get3x4(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.put3x4(this, index, buffer);
        return buffer;
    }

    public FloatBuffer getTransposed(FloatBuffer buffer) {
        return this.getTransposed(buffer.position(), buffer);
    }

    public FloatBuffer getTransposed(int index, FloatBuffer buffer) {
        MemUtil.INSTANCE.putTransposed(this, index, buffer);
        return buffer;
    }

    public ByteBuffer getTransposed(ByteBuffer buffer) {
        return this.getTransposed(buffer.position(), buffer);
    }

    public ByteBuffer getTransposed(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.putTransposed(this, index, buffer);
        return buffer;
    }

    public Matrix3fc getToAddress(long address) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        }
        MemUtil.MemUtilUnsafe.put(this, address);
        return this;
    }

    public Matrix3fc getTransposedToAddress(long address) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        }
        MemUtil.MemUtilUnsafe.putTransposed(this, address);
        return this;
    }

    public float[] get(float[] arr, int offset) {
        MemUtil.INSTANCE.copy(this, arr, offset);
        return arr;
    }

    public float[] get(float[] arr) {
        return this.get(arr, 0);
    }

    public Matrix3f set(FloatBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
        return this;
    }

    public Matrix3f set(ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
        return this;
    }

    public Matrix3f set(int index, FloatBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
        return this;
    }

    public Matrix3f set(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
        return this;
    }

    public Matrix3f setFromAddress(long address) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        }
        MemUtil.MemUtilUnsafe.get(this, address);
        return this;
    }

    public Matrix3f setTransposedFromAddress(long address) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        }
        MemUtil.MemUtilUnsafe.getTransposed(this, address);
        return this;
    }

    public Matrix3f zero() {
        MemUtil.INSTANCE.zero(this);
        return this;
    }

    public Matrix3f identity() {
        MemUtil.INSTANCE.identity(this);
        return this;
    }

    public Matrix3f scale(Vector3fc xyz, Matrix3f dest) {
        return this.scale(xyz.x(), xyz.y(), xyz.z(), dest);
    }

    public Matrix3f scale(Vector3fc xyz) {
        return this.scale(xyz.x(), xyz.y(), xyz.z(), this);
    }

    public Matrix3f scale(float x, float y, float z, Matrix3f dest) {
        dest.m00 = this.m00 * x;
        dest.m01 = this.m01 * x;
        dest.m02 = this.m02 * x;
        dest.m10 = this.m10 * y;
        dest.m11 = this.m11 * y;
        dest.m12 = this.m12 * y;
        dest.m20 = this.m20 * z;
        dest.m21 = this.m21 * z;
        dest.m22 = this.m22 * z;
        return dest;
    }

    public Matrix3f scale(float x, float y, float z) {
        return this.scale(x, y, z, this);
    }

    public Matrix3f scale(float xyz, Matrix3f dest) {
        return this.scale(xyz, xyz, xyz, dest);
    }

    public Matrix3f scale(float xyz) {
        return this.scale(xyz, xyz, xyz);
    }

    public Matrix3f scaleLocal(float x, float y, float z, Matrix3f dest) {
        float nm00 = x * this.m00;
        float nm01 = y * this.m01;
        float nm02 = z * this.m02;
        float nm10 = x * this.m10;
        float nm11 = y * this.m11;
        float nm12 = z * this.m12;
        float nm20 = x * this.m20;
        float nm21 = y * this.m21;
        float nm22 = z * this.m22;
        dest.m00 = nm00;
        dest.m01 = nm01;
        dest.m02 = nm02;
        dest.m10 = nm10;
        dest.m11 = nm11;
        dest.m12 = nm12;
        dest.m20 = nm20;
        dest.m21 = nm21;
        dest.m22 = nm22;
        return dest;
    }

    public Matrix3f scaleLocal(float x, float y, float z) {
        return this.scaleLocal(x, y, z, this);
    }

    public Matrix3f scaling(float factor) {
        MemUtil.INSTANCE.zero(this);
        this.m00 = factor;
        this.m11 = factor;
        this.m22 = factor;
        return this;
    }

    public Matrix3f scaling(float x, float y, float z) {
        MemUtil.INSTANCE.zero(this);
        this.m00 = x;
        this.m11 = y;
        this.m22 = z;
        return this;
    }

    public Matrix3f scaling(Vector3fc xyz) {
        return this.scaling(xyz.x(), xyz.y(), xyz.z());
    }

    public Matrix3f rotation(float angle, Vector3fc axis) {
        return this.rotation(angle, axis.x(), axis.y(), axis.z());
    }

    public Matrix3f rotation(AxisAngle4f axisAngle) {
        return this.rotation(axisAngle.angle, axisAngle.x, axisAngle.y, axisAngle.z);
    }

    public Matrix3f rotation(float angle, float x, float y, float z) {
        float sin = Math.sin(angle);
        float cos = Math.cosFromSin(sin, angle);
        float C = 1.0f - cos;
        float xy = x * y;
        float xz = x * z;
        float yz = y * z;
        this.m00 = cos + x * x * C;
        this.m10 = xy * C - z * sin;
        this.m20 = xz * C + y * sin;
        this.m01 = xy * C + z * sin;
        this.m11 = cos + y * y * C;
        this.m21 = yz * C - x * sin;
        this.m02 = xz * C - y * sin;
        this.m12 = yz * C + x * sin;
        this.m22 = cos + z * z * C;
        return this;
    }

    public Matrix3f rotationX(float ang) {
        float sin = Math.sin(ang);
        float cos = Math.cosFromSin(sin, ang);
        this.m00 = 1.0f;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = cos;
        this.m12 = sin;
        this.m20 = 0.0f;
        this.m21 = -sin;
        this.m22 = cos;
        return this;
    }

    public Matrix3f rotationY(float ang) {
        float cos;
        float sin = Math.sin(ang);
        this.m00 = cos = Math.cosFromSin(sin, ang);
        this.m01 = 0.0f;
        this.m02 = -sin;
        this.m10 = 0.0f;
        this.m11 = 1.0f;
        this.m12 = 0.0f;
        this.m20 = sin;
        this.m21 = 0.0f;
        this.m22 = cos;
        return this;
    }

    public Matrix3f rotationZ(float ang) {
        float cos;
        float sin = Math.sin(ang);
        this.m00 = cos = Math.cosFromSin(sin, ang);
        this.m01 = sin;
        this.m02 = 0.0f;
        this.m10 = -sin;
        this.m11 = cos;
        this.m12 = 0.0f;
        this.m20 = 0.0f;
        this.m21 = 0.0f;
        this.m22 = 1.0f;
        return this;
    }

    public Matrix3f rotationXYZ(float angleX, float angleY, float angleZ) {
        float sinX = Math.sin(angleX);
        float cosX = Math.cosFromSin(sinX, angleX);
        float sinY = Math.sin(angleY);
        float cosY = Math.cosFromSin(sinY, angleY);
        float sinZ = Math.sin(angleZ);
        float cosZ = Math.cosFromSin(sinZ, angleZ);
        float m_sinX = -sinX;
        float m_sinY = -sinY;
        float m_sinZ = -sinZ;
        float nm11 = cosX;
        float nm12 = sinX;
        float nm21 = m_sinX;
        float nm22 = cosX;
        float nm00 = cosY;
        float nm01 = nm21 * m_sinY;
        float nm02 = nm22 * m_sinY;
        this.m20 = sinY;
        this.m21 = nm21 * cosY;
        this.m22 = nm22 * cosY;
        this.m00 = nm00 * cosZ;
        this.m01 = nm01 * cosZ + nm11 * sinZ;
        this.m02 = nm02 * cosZ + nm12 * sinZ;
        this.m10 = nm00 * m_sinZ;
        this.m11 = nm01 * m_sinZ + nm11 * cosZ;
        this.m12 = nm02 * m_sinZ + nm12 * cosZ;
        return this;
    }

    public Matrix3f rotationZYX(float angleZ, float angleY, float angleX) {
        float sinX = Math.sin(angleX);
        float cosX = Math.cosFromSin(sinX, angleX);
        float sinY = Math.sin(angleY);
        float cosY = Math.cosFromSin(sinY, angleY);
        float sinZ = Math.sin(angleZ);
        float cosZ = Math.cosFromSin(sinZ, angleZ);
        float m_sinZ = -sinZ;
        float m_sinY = -sinY;
        float m_sinX = -sinX;
        float nm00 = cosZ;
        float nm01 = sinZ;
        float nm10 = m_sinZ;
        float nm11 = cosZ;
        float nm20 = nm00 * sinY;
        float nm21 = nm01 * sinY;
        float nm22 = cosY;
        this.m00 = nm00 * cosY;
        this.m01 = nm01 * cosY;
        this.m02 = m_sinY;
        this.m10 = nm10 * cosX + nm20 * sinX;
        this.m11 = nm11 * cosX + nm21 * sinX;
        this.m12 = nm22 * sinX;
        this.m20 = nm10 * m_sinX + nm20 * cosX;
        this.m21 = nm11 * m_sinX + nm21 * cosX;
        this.m22 = nm22 * cosX;
        return this;
    }

    public Matrix3f rotationYXZ(float angleY, float angleX, float angleZ) {
        float sinX = Math.sin(angleX);
        float cosX = Math.cosFromSin(sinX, angleX);
        float sinY = Math.sin(angleY);
        float cosY = Math.cosFromSin(sinY, angleY);
        float sinZ = Math.sin(angleZ);
        float cosZ = Math.cosFromSin(sinZ, angleZ);
        float m_sinY = -sinY;
        float m_sinX = -sinX;
        float m_sinZ = -sinZ;
        float nm00 = cosY;
        float nm02 = m_sinY;
        float nm20 = sinY;
        float nm22 = cosY;
        float nm10 = nm20 * sinX;
        float nm11 = cosX;
        float nm12 = nm22 * sinX;
        this.m20 = nm20 * cosX;
        this.m21 = m_sinX;
        this.m22 = nm22 * cosX;
        this.m00 = nm00 * cosZ + nm10 * sinZ;
        this.m01 = nm11 * sinZ;
        this.m02 = nm02 * cosZ + nm12 * sinZ;
        this.m10 = nm00 * m_sinZ + nm10 * cosZ;
        this.m11 = nm11 * cosZ;
        this.m12 = nm02 * m_sinZ + nm12 * cosZ;
        return this;
    }

    public Matrix3f rotation(Quaternionfc quat) {
        float w2 = quat.w() * quat.w();
        float x2 = quat.x() * quat.x();
        float y2 = quat.y() * quat.y();
        float z2 = quat.z() * quat.z();
        float zw = quat.z() * quat.w();
        float dzw = zw + zw;
        float xy = quat.x() * quat.y();
        float dxy = xy + xy;
        float xz = quat.x() * quat.z();
        float dxz = xz + xz;
        float yw = quat.y() * quat.w();
        float dyw = yw + yw;
        float yz = quat.y() * quat.z();
        float dyz = yz + yz;
        float xw = quat.x() * quat.w();
        float dxw = xw + xw;
        this.m00 = w2 + x2 - z2 - y2;
        this.m01 = dxy + dzw;
        this.m02 = dxz - dyw;
        this.m10 = -dzw + dxy;
        this.m11 = y2 - z2 + w2 - x2;
        this.m12 = dyz + dxw;
        this.m20 = dyw + dxz;
        this.m21 = dyz - dxw;
        this.m22 = z2 - y2 - x2 + w2;
        return this;
    }

    public Vector3f transform(Vector3f v) {
        return v.mul(this);
    }

    public Vector3f transform(Vector3fc v, Vector3f dest) {
        return v.mul(this, dest);
    }

    public Vector3f transform(float x, float y, float z, Vector3f dest) {
        return dest.set(Math.fma(this.m00, x, Math.fma(this.m10, y, this.m20 * z)), Math.fma(this.m01, x, Math.fma(this.m11, y, this.m21 * z)), Math.fma(this.m02, x, Math.fma(this.m12, y, this.m22 * z)));
    }

    public Vector3f transformTranspose(Vector3f v) {
        return v.mulTranspose(this);
    }

    public Vector3f transformTranspose(Vector3fc v, Vector3f dest) {
        return v.mulTranspose(this, dest);
    }

    public Vector3f transformTranspose(float x, float y, float z, Vector3f dest) {
        return dest.set(Math.fma(this.m00, x, Math.fma(this.m01, y, this.m02 * z)), Math.fma(this.m10, x, Math.fma(this.m11, y, this.m12 * z)), Math.fma(this.m20, x, Math.fma(this.m21, y, this.m22 * z)));
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeFloat(this.m00);
        out.writeFloat(this.m01);
        out.writeFloat(this.m02);
        out.writeFloat(this.m10);
        out.writeFloat(this.m11);
        out.writeFloat(this.m12);
        out.writeFloat(this.m20);
        out.writeFloat(this.m21);
        out.writeFloat(this.m22);
    }

    public void readExternal(ObjectInput in) throws IOException {
        this.m00 = in.readFloat();
        this.m01 = in.readFloat();
        this.m02 = in.readFloat();
        this.m10 = in.readFloat();
        this.m11 = in.readFloat();
        this.m12 = in.readFloat();
        this.m20 = in.readFloat();
        this.m21 = in.readFloat();
        this.m22 = in.readFloat();
    }

    public Matrix3f rotateX(float ang, Matrix3f dest) {
        float cos;
        float sin = Math.sin(ang);
        float rm11 = cos = Math.cosFromSin(sin, ang);
        float rm21 = -sin;
        float rm12 = sin;
        float rm22 = cos;
        float nm10 = this.m10 * rm11 + this.m20 * rm12;
        float nm11 = this.m11 * rm11 + this.m21 * rm12;
        float nm12 = this.m12 * rm11 + this.m22 * rm12;
        dest.m20 = this.m10 * rm21 + this.m20 * rm22;
        dest.m21 = this.m11 * rm21 + this.m21 * rm22;
        dest.m22 = this.m12 * rm21 + this.m22 * rm22;
        dest.m10 = nm10;
        dest.m11 = nm11;
        dest.m12 = nm12;
        dest.m00 = this.m00;
        dest.m01 = this.m01;
        dest.m02 = this.m02;
        return dest;
    }

    public Matrix3f rotateX(float ang) {
        return this.rotateX(ang, this);
    }

    public Matrix3f rotateY(float ang, Matrix3f dest) {
        float cos;
        float sin = Math.sin(ang);
        float rm00 = cos = Math.cosFromSin(sin, ang);
        float rm20 = sin;
        float rm02 = -sin;
        float rm22 = cos;
        float nm00 = this.m00 * rm00 + this.m20 * rm02;
        float nm01 = this.m01 * rm00 + this.m21 * rm02;
        float nm02 = this.m02 * rm00 + this.m22 * rm02;
        dest.m20 = this.m00 * rm20 + this.m20 * rm22;
        dest.m21 = this.m01 * rm20 + this.m21 * rm22;
        dest.m22 = this.m02 * rm20 + this.m22 * rm22;
        dest.m00 = nm00;
        dest.m01 = nm01;
        dest.m02 = nm02;
        dest.m10 = this.m10;
        dest.m11 = this.m11;
        dest.m12 = this.m12;
        return dest;
    }

    public Matrix3f rotateY(float ang) {
        return this.rotateY(ang, this);
    }

    public Matrix3f rotateZ(float ang, Matrix3f dest) {
        float cos;
        float sin = Math.sin(ang);
        float rm00 = cos = Math.cosFromSin(sin, ang);
        float rm10 = -sin;
        float rm01 = sin;
        float rm11 = cos;
        float nm00 = this.m00 * rm00 + this.m10 * rm01;
        float nm01 = this.m01 * rm00 + this.m11 * rm01;
        float nm02 = this.m02 * rm00 + this.m12 * rm01;
        dest.m10 = this.m00 * rm10 + this.m10 * rm11;
        dest.m11 = this.m01 * rm10 + this.m11 * rm11;
        dest.m12 = this.m02 * rm10 + this.m12 * rm11;
        dest.m00 = nm00;
        dest.m01 = nm01;
        dest.m02 = nm02;
        dest.m20 = this.m20;
        dest.m21 = this.m21;
        dest.m22 = this.m22;
        return dest;
    }

    public Matrix3f rotateZ(float ang) {
        return this.rotateZ(ang, this);
    }

    public Matrix3f rotateXYZ(Vector3f angles) {
        return this.rotateXYZ(angles.x, angles.y, angles.z);
    }

    public Matrix3f rotateXYZ(float angleX, float angleY, float angleZ) {
        return this.rotateXYZ(angleX, angleY, angleZ, this);
    }

    public Matrix3f rotateXYZ(float angleX, float angleY, float angleZ, Matrix3f dest) {
        float sinX = Math.sin(angleX);
        float cosX = Math.cosFromSin(sinX, angleX);
        float sinY = Math.sin(angleY);
        float cosY = Math.cosFromSin(sinY, angleY);
        float sinZ = Math.sin(angleZ);
        float cosZ = Math.cosFromSin(sinZ, angleZ);
        float m_sinX = -sinX;
        float m_sinY = -sinY;
        float m_sinZ = -sinZ;
        float nm10 = this.m10 * cosX + this.m20 * sinX;
        float nm11 = this.m11 * cosX + this.m21 * sinX;
        float nm12 = this.m12 * cosX + this.m22 * sinX;
        float nm20 = this.m10 * m_sinX + this.m20 * cosX;
        float nm21 = this.m11 * m_sinX + this.m21 * cosX;
        float nm22 = this.m12 * m_sinX + this.m22 * cosX;
        float nm00 = this.m00 * cosY + nm20 * m_sinY;
        float nm01 = this.m01 * cosY + nm21 * m_sinY;
        float nm02 = this.m02 * cosY + nm22 * m_sinY;
        dest.m20 = this.m00 * sinY + nm20 * cosY;
        dest.m21 = this.m01 * sinY + nm21 * cosY;
        dest.m22 = this.m02 * sinY + nm22 * cosY;
        dest.m00 = nm00 * cosZ + nm10 * sinZ;
        dest.m01 = nm01 * cosZ + nm11 * sinZ;
        dest.m02 = nm02 * cosZ + nm12 * sinZ;
        dest.m10 = nm00 * m_sinZ + nm10 * cosZ;
        dest.m11 = nm01 * m_sinZ + nm11 * cosZ;
        dest.m12 = nm02 * m_sinZ + nm12 * cosZ;
        return dest;
    }

    public Matrix3f rotateZYX(Vector3f angles) {
        return this.rotateZYX(angles.z, angles.y, angles.x);
    }

    public Matrix3f rotateZYX(float angleZ, float angleY, float angleX) {
        return this.rotateZYX(angleZ, angleY, angleX, this);
    }

    public Matrix3f rotateZYX(float angleZ, float angleY, float angleX, Matrix3f dest) {
        float sinX = Math.sin(angleX);
        float cosX = Math.cosFromSin(sinX, angleX);
        float sinY = Math.sin(angleY);
        float cosY = Math.cosFromSin(sinY, angleY);
        float sinZ = Math.sin(angleZ);
        float cosZ = Math.cosFromSin(sinZ, angleZ);
        float m_sinZ = -sinZ;
        float m_sinY = -sinY;
        float m_sinX = -sinX;
        float nm00 = this.m00 * cosZ + this.m10 * sinZ;
        float nm01 = this.m01 * cosZ + this.m11 * sinZ;
        float nm02 = this.m02 * cosZ + this.m12 * sinZ;
        float nm10 = this.m00 * m_sinZ + this.m10 * cosZ;
        float nm11 = this.m01 * m_sinZ + this.m11 * cosZ;
        float nm12 = this.m02 * m_sinZ + this.m12 * cosZ;
        float nm20 = nm00 * sinY + this.m20 * cosY;
        float nm21 = nm01 * sinY + this.m21 * cosY;
        float nm22 = nm02 * sinY + this.m22 * cosY;
        dest.m00 = nm00 * cosY + this.m20 * m_sinY;
        dest.m01 = nm01 * cosY + this.m21 * m_sinY;
        dest.m02 = nm02 * cosY + this.m22 * m_sinY;
        dest.m10 = nm10 * cosX + nm20 * sinX;
        dest.m11 = nm11 * cosX + nm21 * sinX;
        dest.m12 = nm12 * cosX + nm22 * sinX;
        dest.m20 = nm10 * m_sinX + nm20 * cosX;
        dest.m21 = nm11 * m_sinX + nm21 * cosX;
        dest.m22 = nm12 * m_sinX + nm22 * cosX;
        return dest;
    }

    public Matrix3f rotateYXZ(Vector3f angles) {
        return this.rotateYXZ(angles.y, angles.x, angles.z);
    }

    public Matrix3f rotateYXZ(float angleY, float angleX, float angleZ) {
        return this.rotateYXZ(angleY, angleX, angleZ, this);
    }

    public Matrix3f rotateYXZ(float angleY, float angleX, float angleZ, Matrix3f dest) {
        float sinX = Math.sin(angleX);
        float cosX = Math.cosFromSin(sinX, angleX);
        float sinY = Math.sin(angleY);
        float cosY = Math.cosFromSin(sinY, angleY);
        float sinZ = Math.sin(angleZ);
        float cosZ = Math.cosFromSin(sinZ, angleZ);
        float m_sinY = -sinY;
        float m_sinX = -sinX;
        float m_sinZ = -sinZ;
        float nm20 = this.m00 * sinY + this.m20 * cosY;
        float nm21 = this.m01 * sinY + this.m21 * cosY;
        float nm22 = this.m02 * sinY + this.m22 * cosY;
        float nm00 = this.m00 * cosY + this.m20 * m_sinY;
        float nm01 = this.m01 * cosY + this.m21 * m_sinY;
        float nm02 = this.m02 * cosY + this.m22 * m_sinY;
        float nm10 = this.m10 * cosX + nm20 * sinX;
        float nm11 = this.m11 * cosX + nm21 * sinX;
        float nm12 = this.m12 * cosX + nm22 * sinX;
        dest.m20 = this.m10 * m_sinX + nm20 * cosX;
        dest.m21 = this.m11 * m_sinX + nm21 * cosX;
        dest.m22 = this.m12 * m_sinX + nm22 * cosX;
        dest.m00 = nm00 * cosZ + nm10 * sinZ;
        dest.m01 = nm01 * cosZ + nm11 * sinZ;
        dest.m02 = nm02 * cosZ + nm12 * sinZ;
        dest.m10 = nm00 * m_sinZ + nm10 * cosZ;
        dest.m11 = nm01 * m_sinZ + nm11 * cosZ;
        dest.m12 = nm02 * m_sinZ + nm12 * cosZ;
        return dest;
    }

    public Matrix3f rotate(float ang, float x, float y, float z) {
        return this.rotate(ang, x, y, z, this);
    }

    public Matrix3f rotate(float ang, float x, float y, float z, Matrix3f dest) {
        float s = Math.sin(ang);
        float c = Math.cosFromSin(s, ang);
        float C = 1.0f - c;
        float xx = x * x;
        float xy = x * y;
        float xz = x * z;
        float yy = y * y;
        float yz = y * z;
        float zz = z * z;
        float rm00 = xx * C + c;
        float rm01 = xy * C + z * s;
        float rm02 = xz * C - y * s;
        float rm10 = xy * C - z * s;
        float rm11 = yy * C + c;
        float rm12 = yz * C + x * s;
        float rm20 = xz * C + y * s;
        float rm21 = yz * C - x * s;
        float rm22 = zz * C + c;
        float nm00 = this.m00 * rm00 + this.m10 * rm01 + this.m20 * rm02;
        float nm01 = this.m01 * rm00 + this.m11 * rm01 + this.m21 * rm02;
        float nm02 = this.m02 * rm00 + this.m12 * rm01 + this.m22 * rm02;
        float nm10 = this.m00 * rm10 + this.m10 * rm11 + this.m20 * rm12;
        float nm11 = this.m01 * rm10 + this.m11 * rm11 + this.m21 * rm12;
        float nm12 = this.m02 * rm10 + this.m12 * rm11 + this.m22 * rm12;
        dest.m20 = this.m00 * rm20 + this.m10 * rm21 + this.m20 * rm22;
        dest.m21 = this.m01 * rm20 + this.m11 * rm21 + this.m21 * rm22;
        dest.m22 = this.m02 * rm20 + this.m12 * rm21 + this.m22 * rm22;
        dest.m00 = nm00;
        dest.m01 = nm01;
        dest.m02 = nm02;
        dest.m10 = nm10;
        dest.m11 = nm11;
        dest.m12 = nm12;
        return dest;
    }

    public Matrix3f rotateLocal(float ang, float x, float y, float z, Matrix3f dest) {
        float s = Math.sin(ang);
        float c = Math.cosFromSin(s, ang);
        float C = 1.0f - c;
        float xx = x * x;
        float xy = x * y;
        float xz = x * z;
        float yy = y * y;
        float yz = y * z;
        float zz = z * z;
        float lm00 = xx * C + c;
        float lm01 = xy * C + z * s;
        float lm02 = xz * C - y * s;
        float lm10 = xy * C - z * s;
        float lm11 = yy * C + c;
        float lm12 = yz * C + x * s;
        float lm20 = xz * C + y * s;
        float lm21 = yz * C - x * s;
        float lm22 = zz * C + c;
        float nm00 = lm00 * this.m00 + lm10 * this.m01 + lm20 * this.m02;
        float nm01 = lm01 * this.m00 + lm11 * this.m01 + lm21 * this.m02;
        float nm02 = lm02 * this.m00 + lm12 * this.m01 + lm22 * this.m02;
        float nm10 = lm00 * this.m10 + lm10 * this.m11 + lm20 * this.m12;
        float nm11 = lm01 * this.m10 + lm11 * this.m11 + lm21 * this.m12;
        float nm12 = lm02 * this.m10 + lm12 * this.m11 + lm22 * this.m12;
        float nm20 = lm00 * this.m20 + lm10 * this.m21 + lm20 * this.m22;
        float nm21 = lm01 * this.m20 + lm11 * this.m21 + lm21 * this.m22;
        float nm22 = lm02 * this.m20 + lm12 * this.m21 + lm22 * this.m22;
        dest.m00 = nm00;
        dest.m01 = nm01;
        dest.m02 = nm02;
        dest.m10 = nm10;
        dest.m11 = nm11;
        dest.m12 = nm12;
        dest.m20 = nm20;
        dest.m21 = nm21;
        dest.m22 = nm22;
        return dest;
    }

    public Matrix3f rotateLocal(float ang, float x, float y, float z) {
        return this.rotateLocal(ang, x, y, z, this);
    }

    public Matrix3f rotateLocalX(float ang, Matrix3f dest) {
        float sin = Math.sin(ang);
        float cos = Math.cosFromSin(sin, ang);
        float nm01 = cos * this.m01 - sin * this.m02;
        float nm02 = sin * this.m01 + cos * this.m02;
        float nm11 = cos * this.m11 - sin * this.m12;
        float nm12 = sin * this.m11 + cos * this.m12;
        float nm21 = cos * this.m21 - sin * this.m22;
        float nm22 = sin * this.m21 + cos * this.m22;
        dest.m00 = this.m00;
        dest.m01 = nm01;
        dest.m02 = nm02;
        dest.m10 = this.m10;
        dest.m11 = nm11;
        dest.m12 = nm12;
        dest.m20 = this.m20;
        dest.m21 = nm21;
        dest.m22 = nm22;
        return dest;
    }

    public Matrix3f rotateLocalX(float ang) {
        return this.rotateLocalX(ang, this);
    }

    public Matrix3f rotateLocalY(float ang, Matrix3f dest) {
        float sin = Math.sin(ang);
        float cos = Math.cosFromSin(sin, ang);
        float nm00 = cos * this.m00 + sin * this.m02;
        float nm02 = -sin * this.m00 + cos * this.m02;
        float nm10 = cos * this.m10 + sin * this.m12;
        float nm12 = -sin * this.m10 + cos * this.m12;
        float nm20 = cos * this.m20 + sin * this.m22;
        float nm22 = -sin * this.m20 + cos * this.m22;
        dest.m00 = nm00;
        dest.m01 = this.m01;
        dest.m02 = nm02;
        dest.m10 = nm10;
        dest.m11 = this.m11;
        dest.m12 = nm12;
        dest.m20 = nm20;
        dest.m21 = this.m21;
        dest.m22 = nm22;
        return dest;
    }

    public Matrix3f rotateLocalY(float ang) {
        return this.rotateLocalY(ang, this);
    }

    public Matrix3f rotateLocalZ(float ang, Matrix3f dest) {
        float sin = Math.sin(ang);
        float cos = Math.cosFromSin(sin, ang);
        float nm00 = cos * this.m00 - sin * this.m01;
        float nm01 = sin * this.m00 + cos * this.m01;
        float nm10 = cos * this.m10 - sin * this.m11;
        float nm11 = sin * this.m10 + cos * this.m11;
        float nm20 = cos * this.m20 - sin * this.m21;
        float nm21 = sin * this.m20 + cos * this.m21;
        dest.m00 = nm00;
        dest.m01 = nm01;
        dest.m02 = this.m02;
        dest.m10 = nm10;
        dest.m11 = nm11;
        dest.m12 = this.m12;
        dest.m20 = nm20;
        dest.m21 = nm21;
        dest.m22 = this.m22;
        return dest;
    }

    public Matrix3f rotateLocalZ(float ang) {
        return this.rotateLocalZ(ang, this);
    }

    public Matrix3f rotate(Quaternionfc quat) {
        return this.rotate(quat, this);
    }

    public Matrix3f rotate(Quaternionfc quat, Matrix3f dest) {
        float w2 = quat.w() * quat.w();
        float x2 = quat.x() * quat.x();
        float y2 = quat.y() * quat.y();
        float z2 = quat.z() * quat.z();
        float zw = quat.z() * quat.w();
        float dzw = zw + zw;
        float xy = quat.x() * quat.y();
        float dxy = xy + xy;
        float xz = quat.x() * quat.z();
        float dxz = xz + xz;
        float yw = quat.y() * quat.w();
        float dyw = yw + yw;
        float yz = quat.y() * quat.z();
        float dyz = yz + yz;
        float xw = quat.x() * quat.w();
        float dxw = xw + xw;
        float rm00 = w2 + x2 - z2 - y2;
        float rm01 = dxy + dzw;
        float rm02 = dxz - dyw;
        float rm10 = dxy - dzw;
        float rm11 = y2 - z2 + w2 - x2;
        float rm12 = dyz + dxw;
        float rm20 = dyw + dxz;
        float rm21 = dyz - dxw;
        float rm22 = z2 - y2 - x2 + w2;
        float nm00 = this.m00 * rm00 + this.m10 * rm01 + this.m20 * rm02;
        float nm01 = this.m01 * rm00 + this.m11 * rm01 + this.m21 * rm02;
        float nm02 = this.m02 * rm00 + this.m12 * rm01 + this.m22 * rm02;
        float nm10 = this.m00 * rm10 + this.m10 * rm11 + this.m20 * rm12;
        float nm11 = this.m01 * rm10 + this.m11 * rm11 + this.m21 * rm12;
        float nm12 = this.m02 * rm10 + this.m12 * rm11 + this.m22 * rm12;
        dest.m20 = this.m00 * rm20 + this.m10 * rm21 + this.m20 * rm22;
        dest.m21 = this.m01 * rm20 + this.m11 * rm21 + this.m21 * rm22;
        dest.m22 = this.m02 * rm20 + this.m12 * rm21 + this.m22 * rm22;
        dest.m00 = nm00;
        dest.m01 = nm01;
        dest.m02 = nm02;
        dest.m10 = nm10;
        dest.m11 = nm11;
        dest.m12 = nm12;
        return dest;
    }

    public Matrix3f rotateLocal(Quaternionfc quat, Matrix3f dest) {
        float w2 = quat.w() * quat.w();
        float x2 = quat.x() * quat.x();
        float y2 = quat.y() * quat.y();
        float z2 = quat.z() * quat.z();
        float zw = quat.z() * quat.w();
        float dzw = zw + zw;
        float xy = quat.x() * quat.y();
        float dxy = xy + xy;
        float xz = quat.x() * quat.z();
        float dxz = xz + xz;
        float yw = quat.y() * quat.w();
        float dyw = yw + yw;
        float yz = quat.y() * quat.z();
        float dyz = yz + yz;
        float xw = quat.x() * quat.w();
        float dxw = xw + xw;
        float lm00 = w2 + x2 - z2 - y2;
        float lm01 = dxy + dzw;
        float lm02 = dxz - dyw;
        float lm10 = dxy - dzw;
        float lm11 = y2 - z2 + w2 - x2;
        float lm12 = dyz + dxw;
        float lm20 = dyw + dxz;
        float lm21 = dyz - dxw;
        float lm22 = z2 - y2 - x2 + w2;
        float nm00 = lm00 * this.m00 + lm10 * this.m01 + lm20 * this.m02;
        float nm01 = lm01 * this.m00 + lm11 * this.m01 + lm21 * this.m02;
        float nm02 = lm02 * this.m00 + lm12 * this.m01 + lm22 * this.m02;
        float nm10 = lm00 * this.m10 + lm10 * this.m11 + lm20 * this.m12;
        float nm11 = lm01 * this.m10 + lm11 * this.m11 + lm21 * this.m12;
        float nm12 = lm02 * this.m10 + lm12 * this.m11 + lm22 * this.m12;
        float nm20 = lm00 * this.m20 + lm10 * this.m21 + lm20 * this.m22;
        float nm21 = lm01 * this.m20 + lm11 * this.m21 + lm21 * this.m22;
        float nm22 = lm02 * this.m20 + lm12 * this.m21 + lm22 * this.m22;
        dest.m00 = nm00;
        dest.m01 = nm01;
        dest.m02 = nm02;
        dest.m10 = nm10;
        dest.m11 = nm11;
        dest.m12 = nm12;
        dest.m20 = nm20;
        dest.m21 = nm21;
        dest.m22 = nm22;
        return dest;
    }

    public Matrix3f rotateLocal(Quaternionfc quat) {
        return this.rotateLocal(quat, this);
    }

    public Matrix3f rotate(AxisAngle4f axisAngle) {
        return this.rotate(axisAngle.angle, axisAngle.x, axisAngle.y, axisAngle.z);
    }

    public Matrix3f rotate(AxisAngle4f axisAngle, Matrix3f dest) {
        return this.rotate(axisAngle.angle, axisAngle.x, axisAngle.y, axisAngle.z, dest);
    }

    public Matrix3f rotate(float angle, Vector3fc axis) {
        return this.rotate(angle, axis.x(), axis.y(), axis.z());
    }

    public Matrix3f rotate(float angle, Vector3fc axis, Matrix3f dest) {
        return this.rotate(angle, axis.x(), axis.y(), axis.z(), dest);
    }

    public Matrix3f lookAlong(Vector3fc dir, Vector3fc up) {
        return this.lookAlong(dir.x(), dir.y(), dir.z(), up.x(), up.y(), up.z(), this);
    }

    public Matrix3f lookAlong(Vector3fc dir, Vector3fc up, Matrix3f dest) {
        return this.lookAlong(dir.x(), dir.y(), dir.z(), up.x(), up.y(), up.z(), dest);
    }

    public Matrix3f lookAlong(float dirX, float dirY, float dirZ, float upX, float upY, float upZ, Matrix3f dest) {
        float invDirLength = Math.invsqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        float leftX = upY * (dirZ *= -invDirLength) - upZ * (dirY *= -invDirLength);
        float leftY = upZ * (dirX *= -invDirLength) - upX * dirZ;
        float leftZ = upX * dirY - upY * dirX;
        float invLeftLength = Math.invsqrt(leftX * leftX + leftY * leftY + leftZ * leftZ);
        float upnX = dirY * (leftZ *= invLeftLength) - dirZ * (leftY *= invLeftLength);
        float upnY = dirZ * (leftX *= invLeftLength) - dirX * leftZ;
        float upnZ = dirX * leftY - dirY * leftX;
        float rm00 = leftX;
        float rm01 = upnX;
        float rm02 = dirX;
        float rm10 = leftY;
        float rm11 = upnY;
        float rm12 = dirY;
        float rm20 = leftZ;
        float rm21 = upnZ;
        float rm22 = dirZ;
        float nm00 = this.m00 * rm00 + this.m10 * rm01 + this.m20 * rm02;
        float nm01 = this.m01 * rm00 + this.m11 * rm01 + this.m21 * rm02;
        float nm02 = this.m02 * rm00 + this.m12 * rm01 + this.m22 * rm02;
        float nm10 = this.m00 * rm10 + this.m10 * rm11 + this.m20 * rm12;
        float nm11 = this.m01 * rm10 + this.m11 * rm11 + this.m21 * rm12;
        float nm12 = this.m02 * rm10 + this.m12 * rm11 + this.m22 * rm12;
        dest.m20 = this.m00 * rm20 + this.m10 * rm21 + this.m20 * rm22;
        dest.m21 = this.m01 * rm20 + this.m11 * rm21 + this.m21 * rm22;
        dest.m22 = this.m02 * rm20 + this.m12 * rm21 + this.m22 * rm22;
        dest.m00 = nm00;
        dest.m01 = nm01;
        dest.m02 = nm02;
        dest.m10 = nm10;
        dest.m11 = nm11;
        dest.m12 = nm12;
        return dest;
    }

    public Matrix3f lookAlong(float dirX, float dirY, float dirZ, float upX, float upY, float upZ) {
        return this.lookAlong(dirX, dirY, dirZ, upX, upY, upZ, this);
    }

    public Matrix3f setLookAlong(Vector3fc dir, Vector3fc up) {
        return this.setLookAlong(dir.x(), dir.y(), dir.z(), up.x(), up.y(), up.z());
    }

    public Matrix3f setLookAlong(float dirX, float dirY, float dirZ, float upX, float upY, float upZ) {
        float invDirLength = Math.invsqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        float leftX = upY * (dirZ *= -invDirLength) - upZ * (dirY *= -invDirLength);
        float leftY = upZ * (dirX *= -invDirLength) - upX * dirZ;
        float leftZ = upX * dirY - upY * dirX;
        float invLeftLength = Math.invsqrt(leftX * leftX + leftY * leftY + leftZ * leftZ);
        float upnX = dirY * (leftZ *= invLeftLength) - dirZ * (leftY *= invLeftLength);
        float upnY = dirZ * (leftX *= invLeftLength) - dirX * leftZ;
        float upnZ = dirX * leftY - dirY * leftX;
        this.m00 = leftX;
        this.m01 = upnX;
        this.m02 = dirX;
        this.m10 = leftY;
        this.m11 = upnY;
        this.m12 = dirY;
        this.m20 = leftZ;
        this.m21 = upnZ;
        this.m22 = dirZ;
        return this;
    }

    public Vector3f getRow(int row, Vector3f dest) throws IndexOutOfBoundsException {
        switch (row) {
            case 0: {
                return dest.set(this.m00, this.m10, this.m20);
            }
            case 1: {
                return dest.set(this.m01, this.m11, this.m21);
            }
            case 2: {
                return dest.set(this.m02, this.m12, this.m22);
            }
        }
        throw new IndexOutOfBoundsException();
    }

    public Matrix3f setRow(int row, Vector3fc src) throws IndexOutOfBoundsException {
        return this.setRow(row, src.x(), src.y(), src.z());
    }

    public Matrix3f setRow(int row, float x, float y, float z) throws IndexOutOfBoundsException {
        switch (row) {
            case 0: {
                this.m00 = x;
                this.m10 = y;
                this.m20 = z;
                break;
            }
            case 1: {
                this.m01 = x;
                this.m11 = y;
                this.m21 = z;
                break;
            }
            case 2: {
                this.m02 = x;
                this.m12 = y;
                this.m22 = z;
                break;
            }
            default: {
                throw new IndexOutOfBoundsException();
            }
        }
        return this;
    }

    public Vector3f getColumn(int column, Vector3f dest) throws IndexOutOfBoundsException {
        switch (column) {
            case 0: {
                return dest.set(this.m00, this.m01, this.m02);
            }
            case 1: {
                return dest.set(this.m10, this.m11, this.m12);
            }
            case 2: {
                return dest.set(this.m20, this.m21, this.m22);
            }
        }
        throw new IndexOutOfBoundsException();
    }

    public Matrix3f setColumn(int column, Vector3fc src) throws IndexOutOfBoundsException {
        return this.setColumn(column, src.x(), src.y(), src.z());
    }

    public Matrix3f setColumn(int column, float x, float y, float z) throws IndexOutOfBoundsException {
        switch (column) {
            case 0: {
                this.m00 = x;
                this.m01 = y;
                this.m02 = z;
                break;
            }
            case 1: {
                this.m10 = x;
                this.m11 = y;
                this.m12 = z;
                break;
            }
            case 2: {
                this.m20 = x;
                this.m21 = y;
                this.m22 = z;
                break;
            }
            default: {
                throw new IndexOutOfBoundsException();
            }
        }
        return this;
    }

    public float get(int column, int row) {
        return MemUtil.INSTANCE.get(this, column, row);
    }

    public Matrix3f set(int column, int row, float value) {
        return MemUtil.INSTANCE.set(this, column, row, value);
    }

    public float getRowColumn(int row, int column) {
        return MemUtil.INSTANCE.get(this, column, row);
    }

    public Matrix3f setRowColumn(int row, int column, float value) {
        return MemUtil.INSTANCE.set(this, column, row, value);
    }

    public Matrix3f normal() {
        return this.normal(this);
    }

    public Matrix3f normal(Matrix3f dest) {
        float m00m11 = this.m00 * this.m11;
        float m01m10 = this.m01 * this.m10;
        float m02m10 = this.m02 * this.m10;
        float m00m12 = this.m00 * this.m12;
        float m01m12 = this.m01 * this.m12;
        float m02m11 = this.m02 * this.m11;
        float det = (m00m11 - m01m10) * this.m22 + (m02m10 - m00m12) * this.m21 + (m01m12 - m02m11) * this.m20;
        float s = 1.0f / det;
        float nm00 = (this.m11 * this.m22 - this.m21 * this.m12) * s;
        float nm01 = (this.m20 * this.m12 - this.m10 * this.m22) * s;
        float nm02 = (this.m10 * this.m21 - this.m20 * this.m11) * s;
        float nm10 = (this.m21 * this.m02 - this.m01 * this.m22) * s;
        float nm11 = (this.m00 * this.m22 - this.m20 * this.m02) * s;
        float nm12 = (this.m20 * this.m01 - this.m00 * this.m21) * s;
        float nm20 = (m01m12 - m02m11) * s;
        float nm21 = (m02m10 - m00m12) * s;
        float nm22 = (m00m11 - m01m10) * s;
        dest.m00 = nm00;
        dest.m01 = nm01;
        dest.m02 = nm02;
        dest.m10 = nm10;
        dest.m11 = nm11;
        dest.m12 = nm12;
        dest.m20 = nm20;
        dest.m21 = nm21;
        dest.m22 = nm22;
        return dest;
    }

    public Matrix3f cofactor() {
        return this.cofactor(this);
    }

    public Matrix3f cofactor(Matrix3f dest) {
        float nm00 = this.m11 * this.m22 - this.m21 * this.m12;
        float nm01 = this.m20 * this.m12 - this.m10 * this.m22;
        float nm02 = this.m10 * this.m21 - this.m20 * this.m11;
        float nm10 = this.m21 * this.m02 - this.m01 * this.m22;
        float nm11 = this.m00 * this.m22 - this.m20 * this.m02;
        float nm12 = this.m20 * this.m01 - this.m00 * this.m21;
        float nm20 = this.m01 * this.m12 - this.m11 * this.m02;
        float nm21 = this.m02 * this.m10 - this.m12 * this.m00;
        float nm22 = this.m00 * this.m11 - this.m10 * this.m01;
        dest.m00 = nm00;
        dest.m01 = nm01;
        dest.m02 = nm02;
        dest.m10 = nm10;
        dest.m11 = nm11;
        dest.m12 = nm12;
        dest.m20 = nm20;
        dest.m21 = nm21;
        dest.m22 = nm22;
        return dest;
    }

    public Vector3f getScale(Vector3f dest) {
        return dest.set(Math.sqrt(this.m00 * this.m00 + this.m01 * this.m01 + this.m02 * this.m02), Math.sqrt(this.m10 * this.m10 + this.m11 * this.m11 + this.m12 * this.m12), Math.sqrt(this.m20 * this.m20 + this.m21 * this.m21 + this.m22 * this.m22));
    }

    public Vector3f positiveZ(Vector3f dir) {
        dir.x = this.m10 * this.m21 - this.m11 * this.m20;
        dir.y = this.m20 * this.m01 - this.m21 * this.m00;
        dir.z = this.m00 * this.m11 - this.m01 * this.m10;
        return dir.normalize(dir);
    }

    public Vector3f normalizedPositiveZ(Vector3f dir) {
        dir.x = this.m02;
        dir.y = this.m12;
        dir.z = this.m22;
        return dir;
    }

    public Vector3f positiveX(Vector3f dir) {
        dir.x = this.m11 * this.m22 - this.m12 * this.m21;
        dir.y = this.m02 * this.m21 - this.m01 * this.m22;
        dir.z = this.m01 * this.m12 - this.m02 * this.m11;
        return dir.normalize(dir);
    }

    public Vector3f normalizedPositiveX(Vector3f dir) {
        dir.x = this.m00;
        dir.y = this.m10;
        dir.z = this.m20;
        return dir;
    }

    public Vector3f positiveY(Vector3f dir) {
        dir.x = this.m12 * this.m20 - this.m10 * this.m22;
        dir.y = this.m00 * this.m22 - this.m02 * this.m20;
        dir.z = this.m02 * this.m10 - this.m00 * this.m12;
        return dir.normalize(dir);
    }

    public Vector3f normalizedPositiveY(Vector3f dir) {
        dir.x = this.m01;
        dir.y = this.m11;
        dir.z = this.m21;
        return dir;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + Float.floatToIntBits(this.m00);
        result = 31 * result + Float.floatToIntBits(this.m01);
        result = 31 * result + Float.floatToIntBits(this.m02);
        result = 31 * result + Float.floatToIntBits(this.m10);
        result = 31 * result + Float.floatToIntBits(this.m11);
        result = 31 * result + Float.floatToIntBits(this.m12);
        result = 31 * result + Float.floatToIntBits(this.m20);
        result = 31 * result + Float.floatToIntBits(this.m21);
        result = 31 * result + Float.floatToIntBits(this.m22);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Matrix3f)) {
            return false;
        }
        Matrix3f other = (Matrix3f)obj;
        if (Float.floatToIntBits(this.m00) != Float.floatToIntBits(other.m00)) {
            return false;
        }
        if (Float.floatToIntBits(this.m01) != Float.floatToIntBits(other.m01)) {
            return false;
        }
        if (Float.floatToIntBits(this.m02) != Float.floatToIntBits(other.m02)) {
            return false;
        }
        if (Float.floatToIntBits(this.m10) != Float.floatToIntBits(other.m10)) {
            return false;
        }
        if (Float.floatToIntBits(this.m11) != Float.floatToIntBits(other.m11)) {
            return false;
        }
        if (Float.floatToIntBits(this.m12) != Float.floatToIntBits(other.m12)) {
            return false;
        }
        if (Float.floatToIntBits(this.m20) != Float.floatToIntBits(other.m20)) {
            return false;
        }
        if (Float.floatToIntBits(this.m21) != Float.floatToIntBits(other.m21)) {
            return false;
        }
        return Float.floatToIntBits(this.m22) == Float.floatToIntBits(other.m22);
    }

    public boolean equals(Matrix3fc m, float delta) {
        if (this == m) {
            return true;
        }
        if (m == null) {
            return false;
        }
        if (!Runtime.equals(this.m00, m.m00(), delta)) {
            return false;
        }
        if (!Runtime.equals(this.m01, m.m01(), delta)) {
            return false;
        }
        if (!Runtime.equals(this.m02, m.m02(), delta)) {
            return false;
        }
        if (!Runtime.equals(this.m10, m.m10(), delta)) {
            return false;
        }
        if (!Runtime.equals(this.m11, m.m11(), delta)) {
            return false;
        }
        if (!Runtime.equals(this.m12, m.m12(), delta)) {
            return false;
        }
        if (!Runtime.equals(this.m20, m.m20(), delta)) {
            return false;
        }
        if (!Runtime.equals(this.m21, m.m21(), delta)) {
            return false;
        }
        return Runtime.equals(this.m22, m.m22(), delta);
    }

    public Matrix3f swap(Matrix3f other) {
        MemUtil.INSTANCE.swap(this, other);
        return this;
    }

    public Matrix3f add(Matrix3fc other) {
        return this.add(other, this);
    }

    public Matrix3f add(Matrix3fc other, Matrix3f dest) {
        dest.m00 = this.m00 + other.m00();
        dest.m01 = this.m01 + other.m01();
        dest.m02 = this.m02 + other.m02();
        dest.m10 = this.m10 + other.m10();
        dest.m11 = this.m11 + other.m11();
        dest.m12 = this.m12 + other.m12();
        dest.m20 = this.m20 + other.m20();
        dest.m21 = this.m21 + other.m21();
        dest.m22 = this.m22 + other.m22();
        return dest;
    }

    public Matrix3f sub(Matrix3fc subtrahend) {
        return this.sub(subtrahend, this);
    }

    public Matrix3f sub(Matrix3fc subtrahend, Matrix3f dest) {
        dest.m00 = this.m00 - subtrahend.m00();
        dest.m01 = this.m01 - subtrahend.m01();
        dest.m02 = this.m02 - subtrahend.m02();
        dest.m10 = this.m10 - subtrahend.m10();
        dest.m11 = this.m11 - subtrahend.m11();
        dest.m12 = this.m12 - subtrahend.m12();
        dest.m20 = this.m20 - subtrahend.m20();
        dest.m21 = this.m21 - subtrahend.m21();
        dest.m22 = this.m22 - subtrahend.m22();
        return dest;
    }

    public Matrix3f mulComponentWise(Matrix3fc other) {
        return this.mulComponentWise(other, this);
    }

    public Matrix3f mulComponentWise(Matrix3fc other, Matrix3f dest) {
        dest.m00 = this.m00 * other.m00();
        dest.m01 = this.m01 * other.m01();
        dest.m02 = this.m02 * other.m02();
        dest.m10 = this.m10 * other.m10();
        dest.m11 = this.m11 * other.m11();
        dest.m12 = this.m12 * other.m12();
        dest.m20 = this.m20 * other.m20();
        dest.m21 = this.m21 * other.m21();
        dest.m22 = this.m22 * other.m22();
        return dest;
    }

    public Matrix3f setSkewSymmetric(float a, float b, float c) {
        this.m22 = 0.0f;
        this.m11 = 0.0f;
        this.m00 = 0.0f;
        this.m01 = -a;
        this.m02 = b;
        this.m10 = a;
        this.m12 = -c;
        this.m20 = -b;
        this.m21 = c;
        return this;
    }

    public Matrix3f lerp(Matrix3fc other, float t) {
        return this.lerp(other, t, this);
    }

    public Matrix3f lerp(Matrix3fc other, float t, Matrix3f dest) {
        dest.m00 = Math.fma(other.m00() - this.m00, t, this.m00);
        dest.m01 = Math.fma(other.m01() - this.m01, t, this.m01);
        dest.m02 = Math.fma(other.m02() - this.m02, t, this.m02);
        dest.m10 = Math.fma(other.m10() - this.m10, t, this.m10);
        dest.m11 = Math.fma(other.m11() - this.m11, t, this.m11);
        dest.m12 = Math.fma(other.m12() - this.m12, t, this.m12);
        dest.m20 = Math.fma(other.m20() - this.m20, t, this.m20);
        dest.m21 = Math.fma(other.m21() - this.m21, t, this.m21);
        dest.m22 = Math.fma(other.m22() - this.m22, t, this.m22);
        return dest;
    }

    public Matrix3f rotateTowards(Vector3fc direction, Vector3fc up, Matrix3f dest) {
        return this.rotateTowards(direction.x(), direction.y(), direction.z(), up.x(), up.y(), up.z(), dest);
    }

    public Matrix3f rotateTowards(Vector3fc direction, Vector3fc up) {
        return this.rotateTowards(direction.x(), direction.y(), direction.z(), up.x(), up.y(), up.z(), this);
    }

    public Matrix3f rotateTowards(float dirX, float dirY, float dirZ, float upX, float upY, float upZ) {
        return this.rotateTowards(dirX, dirY, dirZ, upX, upY, upZ, this);
    }

    public Matrix3f rotateTowards(float dirX, float dirY, float dirZ, float upX, float upY, float upZ, Matrix3f dest) {
        float invDirLength = Math.invsqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        float ndirX = dirX * invDirLength;
        float ndirY = dirY * invDirLength;
        float ndirZ = dirZ * invDirLength;
        float leftX = upY * ndirZ - upZ * ndirY;
        float leftY = upZ * ndirX - upX * ndirZ;
        float leftZ = upX * ndirY - upY * ndirX;
        float invLeftLength = Math.invsqrt(leftX * leftX + leftY * leftY + leftZ * leftZ);
        float upnX = ndirY * (leftZ *= invLeftLength) - ndirZ * (leftY *= invLeftLength);
        float upnY = ndirZ * (leftX *= invLeftLength) - ndirX * leftZ;
        float upnZ = ndirX * leftY - ndirY * leftX;
        float rm00 = leftX;
        float rm01 = leftY;
        float rm02 = leftZ;
        float rm10 = upnX;
        float rm11 = upnY;
        float rm12 = upnZ;
        float rm20 = ndirX;
        float rm21 = ndirY;
        float rm22 = ndirZ;
        float nm00 = this.m00 * rm00 + this.m10 * rm01 + this.m20 * rm02;
        float nm01 = this.m01 * rm00 + this.m11 * rm01 + this.m21 * rm02;
        float nm02 = this.m02 * rm00 + this.m12 * rm01 + this.m22 * rm02;
        float nm10 = this.m00 * rm10 + this.m10 * rm11 + this.m20 * rm12;
        float nm11 = this.m01 * rm10 + this.m11 * rm11 + this.m21 * rm12;
        float nm12 = this.m02 * rm10 + this.m12 * rm11 + this.m22 * rm12;
        dest.m20 = this.m00 * rm20 + this.m10 * rm21 + this.m20 * rm22;
        dest.m21 = this.m01 * rm20 + this.m11 * rm21 + this.m21 * rm22;
        dest.m22 = this.m02 * rm20 + this.m12 * rm21 + this.m22 * rm22;
        dest.m00 = nm00;
        dest.m01 = nm01;
        dest.m02 = nm02;
        dest.m10 = nm10;
        dest.m11 = nm11;
        dest.m12 = nm12;
        return dest;
    }

    public Matrix3f rotationTowards(Vector3fc dir, Vector3fc up) {
        return this.rotationTowards(dir.x(), dir.y(), dir.z(), up.x(), up.y(), up.z());
    }

    public Matrix3f rotationTowards(float dirX, float dirY, float dirZ, float upX, float upY, float upZ) {
        float invDirLength = Math.invsqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        float ndirX = dirX * invDirLength;
        float ndirY = dirY * invDirLength;
        float ndirZ = dirZ * invDirLength;
        float leftX = upY * ndirZ - upZ * ndirY;
        float leftY = upZ * ndirX - upX * ndirZ;
        float leftZ = upX * ndirY - upY * ndirX;
        float invLeftLength = Math.invsqrt(leftX * leftX + leftY * leftY + leftZ * leftZ);
        float upnX = ndirY * (leftZ *= invLeftLength) - ndirZ * (leftY *= invLeftLength);
        float upnY = ndirZ * (leftX *= invLeftLength) - ndirX * leftZ;
        float upnZ = ndirX * leftY - ndirY * leftX;
        this.m00 = leftX;
        this.m01 = leftY;
        this.m02 = leftZ;
        this.m10 = upnX;
        this.m11 = upnY;
        this.m12 = upnZ;
        this.m20 = ndirX;
        this.m21 = ndirY;
        this.m22 = ndirZ;
        return this;
    }

    public Vector3f getEulerAnglesZYX(Vector3f dest) {
        dest.x = Math.atan2(this.m12, this.m22);
        dest.y = Math.atan2(-this.m02, Math.sqrt(1.0f - this.m02 * this.m02));
        dest.z = Math.atan2(this.m01, this.m00);
        return dest;
    }

    public Vector3f getEulerAnglesXYZ(Vector3f dest) {
        dest.x = Math.atan2(-this.m21, this.m22);
        dest.y = Math.atan2(this.m20, Math.sqrt(1.0f - this.m20 * this.m20));
        dest.z = Math.atan2(-this.m10, this.m00);
        return dest;
    }

    public Vector3f getEulerAnglesYXZ(Vector3f dest) {
        dest.x = Math.atan2(-this.m21, Math.sqrt(1.0f - this.m21 * this.m21));
        dest.y = Math.atan2(this.m20, this.m22);
        dest.z = Math.atan2(this.m01, this.m11);
        return dest;
    }

    public Matrix3f obliqueZ(float a, float b) {
        this.m20 = this.m00 * a + this.m10 * b + this.m20;
        this.m21 = this.m01 * a + this.m11 * b + this.m21;
        this.m22 = this.m02 * a + this.m12 * b + this.m22;
        return this;
    }

    public Matrix3f obliqueZ(float a, float b, Matrix3f dest) {
        dest.m00 = this.m00;
        dest.m01 = this.m01;
        dest.m02 = this.m02;
        dest.m10 = this.m10;
        dest.m11 = this.m11;
        dest.m12 = this.m12;
        dest.m20 = this.m00 * a + this.m10 * b + this.m20;
        dest.m21 = this.m01 * a + this.m11 * b + this.m21;
        dest.m22 = this.m02 * a + this.m12 * b + this.m22;
        return dest;
    }

    public Matrix3f reflect(float nx, float ny, float nz, Matrix3f dest) {
        float da = nx + nx;
        float db = ny + ny;
        float dc = nz + nz;
        float rm00 = 1.0f - da * nx;
        float rm01 = -da * ny;
        float rm02 = -da * nz;
        float rm10 = -db * nx;
        float rm11 = 1.0f - db * ny;
        float rm12 = -db * nz;
        float rm20 = -dc * nx;
        float rm21 = -dc * ny;
        float rm22 = 1.0f - dc * nz;
        float nm00 = this.m00 * rm00 + this.m10 * rm01 + this.m20 * rm02;
        float nm01 = this.m01 * rm00 + this.m11 * rm01 + this.m21 * rm02;
        float nm02 = this.m02 * rm00 + this.m12 * rm01 + this.m22 * rm02;
        float nm10 = this.m00 * rm10 + this.m10 * rm11 + this.m20 * rm12;
        float nm11 = this.m01 * rm10 + this.m11 * rm11 + this.m21 * rm12;
        float nm12 = this.m02 * rm10 + this.m12 * rm11 + this.m22 * rm12;
        return dest._m20(this.m00 * rm20 + this.m10 * rm21 + this.m20 * rm22)._m21(this.m01 * rm20 + this.m11 * rm21 + this.m21 * rm22)._m22(this.m02 * rm20 + this.m12 * rm21 + this.m22 * rm22)._m00(nm00)._m01(nm01)._m02(nm02)._m10(nm10)._m11(nm11)._m12(nm12);
    }

    public Matrix3f reflect(float nx, float ny, float nz) {
        return this.reflect(nx, ny, nz, this);
    }

    public Matrix3f reflect(Vector3fc normal) {
        return this.reflect(normal.x(), normal.y(), normal.z());
    }

    public Matrix3f reflect(Quaternionfc orientation) {
        return this.reflect(orientation, this);
    }

    public Matrix3f reflect(Quaternionfc orientation, Matrix3f dest) {
        float num1 = orientation.x() + orientation.x();
        float num2 = orientation.y() + orientation.y();
        float num3 = orientation.z() + orientation.z();
        float normalX = orientation.x() * num3 + orientation.w() * num2;
        float normalY = orientation.y() * num3 - orientation.w() * num1;
        float normalZ = 1.0f - (orientation.x() * num1 + orientation.y() * num2);
        return this.reflect(normalX, normalY, normalZ, dest);
    }

    public Matrix3f reflect(Vector3fc normal, Matrix3f dest) {
        return this.reflect(normal.x(), normal.y(), normal.z(), dest);
    }

    public Matrix3f reflection(float nx, float ny, float nz) {
        float da = nx + nx;
        float db = ny + ny;
        float dc = nz + nz;
        this._m00(1.0f - da * nx);
        this._m01(-da * ny);
        this._m02(-da * nz);
        this._m10(-db * nx);
        this._m11(1.0f - db * ny);
        this._m12(-db * nz);
        this._m20(-dc * nx);
        this._m21(-dc * ny);
        this._m22(1.0f - dc * nz);
        return this;
    }

    public Matrix3f reflection(Vector3fc normal) {
        return this.reflection(normal.x(), normal.y(), normal.z());
    }

    public Matrix3f reflection(Quaternionfc orientation) {
        float num1 = orientation.x() + orientation.x();
        float num2 = orientation.y() + orientation.y();
        float num3 = orientation.z() + orientation.z();
        float normalX = orientation.x() * num3 + orientation.w() * num2;
        float normalY = orientation.y() * num3 - orientation.w() * num1;
        float normalZ = 1.0f - (orientation.x() * num1 + orientation.y() * num2);
        return this.reflection(normalX, normalY, normalZ);
    }

    public boolean isFinite() {
        return Math.isFinite(this.m00) && Math.isFinite(this.m01) && Math.isFinite(this.m02) && Math.isFinite(this.m10) && Math.isFinite(this.m11) && Math.isFinite(this.m12) && Math.isFinite(this.m20) && Math.isFinite(this.m21) && Math.isFinite(this.m22);
    }

    public float quadraticFormProduct(float x, float y, float z) {
        float Axx = this.m00 * x + this.m10 * y + this.m20 * z;
        float Axy = this.m01 * x + this.m11 * y + this.m21 * z;
        float Axz = this.m02 * x + this.m12 * y + this.m22 * z;
        return x * Axx + y * Axy + z * Axz;
    }

    public float quadraticFormProduct(Vector3fc v) {
        return this.quadraticFormProduct(v.x(), v.y(), v.z());
    }

    public Matrix3f mapXZY() {
        return this.mapXZY(this);
    }

    public Matrix3f mapXZY(Matrix3f dest) {
        float m10 = this.m10;
        float m11 = this.m11;
        float m12 = this.m12;
        return dest._m00(this.m00)._m01(this.m01)._m02(this.m02)._m10(this.m20)._m11(this.m21)._m12(this.m22)._m20(m10)._m21(m11)._m22(m12);
    }

    public Matrix3f mapXZnY() {
        return this.mapXZnY(this);
    }

    public Matrix3f mapXZnY(Matrix3f dest) {
        float m10 = this.m10;
        float m11 = this.m11;
        float m12 = this.m12;
        return dest._m00(this.m00)._m01(this.m01)._m02(this.m02)._m10(this.m20)._m11(this.m21)._m12(this.m22)._m20(-m10)._m21(-m11)._m22(-m12);
    }

    public Matrix3f mapXnYnZ() {
        return this.mapXnYnZ(this);
    }

    public Matrix3f mapXnYnZ(Matrix3f dest) {
        return dest._m00(this.m00)._m01(this.m01)._m02(this.m02)._m10(-this.m10)._m11(-this.m11)._m12(-this.m12)._m20(-this.m20)._m21(-this.m21)._m22(-this.m22);
    }

    public Matrix3f mapXnZY() {
        return this.mapXnZY(this);
    }

    public Matrix3f mapXnZY(Matrix3f dest) {
        float m10 = this.m10;
        float m11 = this.m11;
        float m12 = this.m12;
        return dest._m00(this.m00)._m01(this.m01)._m02(this.m02)._m10(-this.m20)._m11(-this.m21)._m12(-this.m22)._m20(m10)._m21(m11)._m22(m12);
    }

    public Matrix3f mapXnZnY() {
        return this.mapXnZnY(this);
    }

    public Matrix3f mapXnZnY(Matrix3f dest) {
        float m10 = this.m10;
        float m11 = this.m11;
        float m12 = this.m12;
        return dest._m00(this.m00)._m01(this.m01)._m02(this.m02)._m10(-this.m20)._m11(-this.m21)._m12(-this.m22)._m20(-m10)._m21(-m11)._m22(-m12);
    }

    public Matrix3f mapYXZ() {
        return this.mapYXZ(this);
    }

    public Matrix3f mapYXZ(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        return dest._m00(this.m10)._m01(this.m11)._m02(this.m12)._m10(m00)._m11(m01)._m12(m02)._m20(this.m20)._m21(this.m21)._m22(this.m22);
    }

    public Matrix3f mapYXnZ() {
        return this.mapYXnZ(this);
    }

    public Matrix3f mapYXnZ(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        return dest._m00(this.m10)._m01(this.m11)._m02(this.m12)._m10(m00)._m11(m01)._m12(m02)._m20(-this.m20)._m21(-this.m21)._m22(-this.m22);
    }

    public Matrix3f mapYZX() {
        return this.mapYZX(this);
    }

    public Matrix3f mapYZX(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        return dest._m00(this.m10)._m01(this.m11)._m02(this.m12)._m10(this.m20)._m11(this.m21)._m12(this.m22)._m20(m00)._m21(m01)._m22(m02);
    }

    public Matrix3f mapYZnX() {
        return this.mapYZnX(this);
    }

    public Matrix3f mapYZnX(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        return dest._m00(this.m10)._m01(this.m11)._m02(this.m12)._m10(this.m20)._m11(this.m21)._m12(this.m22)._m20(-m00)._m21(-m01)._m22(-m02);
    }

    public Matrix3f mapYnXZ() {
        return this.mapYnXZ(this);
    }

    public Matrix3f mapYnXZ(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        return dest._m00(this.m10)._m01(this.m11)._m02(this.m12)._m10(-m00)._m11(-m01)._m12(-m02)._m20(this.m20)._m21(this.m21)._m22(this.m22);
    }

    public Matrix3f mapYnXnZ() {
        return this.mapYnXnZ(this);
    }

    public Matrix3f mapYnXnZ(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        return dest._m00(this.m10)._m01(this.m11)._m02(this.m12)._m10(-m00)._m11(-m01)._m12(-m02)._m20(-this.m20)._m21(-this.m21)._m22(-this.m22);
    }

    public Matrix3f mapYnZX() {
        return this.mapYnZX(this);
    }

    public Matrix3f mapYnZX(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        return dest._m00(this.m10)._m01(this.m11)._m02(this.m12)._m10(-this.m20)._m11(-this.m21)._m12(-this.m22)._m20(m00)._m21(m01)._m22(m02);
    }

    public Matrix3f mapYnZnX() {
        return this.mapYnZnX(this);
    }

    public Matrix3f mapYnZnX(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        return dest._m00(this.m10)._m01(this.m11)._m02(this.m12)._m10(-this.m20)._m11(-this.m21)._m12(-this.m22)._m20(-m00)._m21(-m01)._m22(-m02);
    }

    public Matrix3f mapZXY() {
        return this.mapZXY(this);
    }

    public Matrix3f mapZXY(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        float m10 = this.m10;
        float m11 = this.m11;
        float m12 = this.m12;
        return dest._m00(this.m20)._m01(this.m21)._m02(this.m22)._m10(m00)._m11(m01)._m12(m02)._m20(m10)._m21(m11)._m22(m12);
    }

    public Matrix3f mapZXnY() {
        return this.mapZXnY(this);
    }

    public Matrix3f mapZXnY(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        float m10 = this.m10;
        float m11 = this.m11;
        float m12 = this.m12;
        return dest._m00(this.m20)._m01(this.m21)._m02(this.m22)._m10(m00)._m11(m01)._m12(m02)._m20(-m10)._m21(-m11)._m22(-m12);
    }

    public Matrix3f mapZYX() {
        return this.mapZYX(this);
    }

    public Matrix3f mapZYX(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        return dest._m00(this.m20)._m01(this.m21)._m02(this.m22)._m10(this.m10)._m11(this.m11)._m12(this.m12)._m20(m00)._m21(m01)._m22(m02);
    }

    public Matrix3f mapZYnX() {
        return this.mapZYnX(this);
    }

    public Matrix3f mapZYnX(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        return dest._m00(this.m20)._m01(this.m21)._m02(this.m22)._m10(this.m10)._m11(this.m11)._m12(this.m12)._m20(-m00)._m21(-m01)._m22(-m02);
    }

    public Matrix3f mapZnXY() {
        return this.mapZnXY(this);
    }

    public Matrix3f mapZnXY(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        float m10 = this.m10;
        float m11 = this.m11;
        float m12 = this.m12;
        return dest._m00(this.m20)._m01(this.m21)._m02(this.m22)._m10(-m00)._m11(-m01)._m12(-m02)._m20(m10)._m21(m11)._m22(m12);
    }

    public Matrix3f mapZnXnY() {
        return this.mapZnXnY(this);
    }

    public Matrix3f mapZnXnY(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        float m10 = this.m10;
        float m11 = this.m11;
        float m12 = this.m12;
        return dest._m00(this.m20)._m01(this.m21)._m02(this.m22)._m10(-m00)._m11(-m01)._m12(-m02)._m20(-m10)._m21(-m11)._m22(-m12);
    }

    public Matrix3f mapZnYX() {
        return this.mapZnYX(this);
    }

    public Matrix3f mapZnYX(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        return dest._m00(this.m20)._m01(this.m21)._m02(this.m22)._m10(-this.m10)._m11(-this.m11)._m12(-this.m12)._m20(m00)._m21(m01)._m22(m02);
    }

    public Matrix3f mapZnYnX() {
        return this.mapZnYnX(this);
    }

    public Matrix3f mapZnYnX(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        return dest._m00(this.m20)._m01(this.m21)._m02(this.m22)._m10(-this.m10)._m11(-this.m11)._m12(-this.m12)._m20(-m00)._m21(-m01)._m22(-m02);
    }

    public Matrix3f mapnXYnZ() {
        return this.mapnXYnZ(this);
    }

    public Matrix3f mapnXYnZ(Matrix3f dest) {
        return dest._m00(-this.m00)._m01(-this.m01)._m02(-this.m02)._m10(this.m10)._m11(this.m11)._m12(this.m12)._m20(-this.m20)._m21(-this.m21)._m22(-this.m22);
    }

    public Matrix3f mapnXZY() {
        return this.mapnXZY(this);
    }

    public Matrix3f mapnXZY(Matrix3f dest) {
        float m10 = this.m10;
        float m11 = this.m11;
        float m12 = this.m12;
        return dest._m00(-this.m00)._m01(-this.m01)._m02(-this.m02)._m10(this.m20)._m11(this.m21)._m12(this.m22)._m20(m10)._m21(m11)._m22(m12);
    }

    public Matrix3f mapnXZnY() {
        return this.mapnXZnY(this);
    }

    public Matrix3f mapnXZnY(Matrix3f dest) {
        float m10 = this.m10;
        float m11 = this.m11;
        float m12 = this.m12;
        return dest._m00(-this.m00)._m01(-this.m01)._m02(-this.m02)._m10(this.m20)._m11(this.m21)._m12(this.m22)._m20(-m10)._m21(-m11)._m22(-m12);
    }

    public Matrix3f mapnXnYZ() {
        return this.mapnXnYZ(this);
    }

    public Matrix3f mapnXnYZ(Matrix3f dest) {
        return dest._m00(-this.m00)._m01(-this.m01)._m02(-this.m02)._m10(-this.m10)._m11(-this.m11)._m12(-this.m12)._m20(this.m20)._m21(this.m21)._m22(this.m22);
    }

    public Matrix3f mapnXnYnZ() {
        return this.mapnXnYnZ(this);
    }

    public Matrix3f mapnXnYnZ(Matrix3f dest) {
        return dest._m00(-this.m00)._m01(-this.m01)._m02(-this.m02)._m10(-this.m10)._m11(-this.m11)._m12(-this.m12)._m20(-this.m20)._m21(-this.m21)._m22(-this.m22);
    }

    public Matrix3f mapnXnZY() {
        return this.mapnXnZY(this);
    }

    public Matrix3f mapnXnZY(Matrix3f dest) {
        float m10 = this.m10;
        float m11 = this.m11;
        float m12 = this.m12;
        return dest._m00(-this.m00)._m01(-this.m01)._m02(-this.m02)._m10(-this.m20)._m11(-this.m21)._m12(-this.m22)._m20(m10)._m21(m11)._m22(m12);
    }

    public Matrix3f mapnXnZnY() {
        return this.mapnXnZnY(this);
    }

    public Matrix3f mapnXnZnY(Matrix3f dest) {
        float m10 = this.m10;
        float m11 = this.m11;
        float m12 = this.m12;
        return dest._m00(-this.m00)._m01(-this.m01)._m02(-this.m02)._m10(-this.m20)._m11(-this.m21)._m12(-this.m22)._m20(-m10)._m21(-m11)._m22(-m12);
    }

    public Matrix3f mapnYXZ() {
        return this.mapnYXZ(this);
    }

    public Matrix3f mapnYXZ(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        return dest._m00(-this.m10)._m01(-this.m11)._m02(-this.m12)._m10(m00)._m11(m01)._m12(m02)._m20(this.m20)._m21(this.m21)._m22(this.m22);
    }

    public Matrix3f mapnYXnZ() {
        return this.mapnYXnZ(this);
    }

    public Matrix3f mapnYXnZ(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        return dest._m00(-this.m10)._m01(-this.m11)._m02(-this.m12)._m10(m00)._m11(m01)._m12(m02)._m20(-this.m20)._m21(-this.m21)._m22(-this.m22);
    }

    public Matrix3f mapnYZX() {
        return this.mapnYZX(this);
    }

    public Matrix3f mapnYZX(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        return dest._m00(-this.m10)._m01(-this.m11)._m02(-this.m12)._m10(this.m20)._m11(this.m21)._m12(this.m22)._m20(m00)._m21(m01)._m22(m02);
    }

    public Matrix3f mapnYZnX() {
        return this.mapnYZnX(this);
    }

    public Matrix3f mapnYZnX(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        return dest._m00(-this.m10)._m01(-this.m11)._m02(-this.m12)._m10(this.m20)._m11(this.m21)._m12(this.m22)._m20(-m00)._m21(-m01)._m22(-m02);
    }

    public Matrix3f mapnYnXZ() {
        return this.mapnYnXZ(this);
    }

    public Matrix3f mapnYnXZ(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        return dest._m00(-this.m10)._m01(-this.m11)._m02(-this.m12)._m10(-m00)._m11(-m01)._m12(-m02)._m20(this.m20)._m21(this.m21)._m22(this.m22);
    }

    public Matrix3f mapnYnXnZ() {
        return this.mapnYnXnZ(this);
    }

    public Matrix3f mapnYnXnZ(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        return dest._m00(-this.m10)._m01(-this.m11)._m02(-this.m12)._m10(-m00)._m11(-m01)._m12(-m02)._m20(-this.m20)._m21(-this.m21)._m22(-this.m22);
    }

    public Matrix3f mapnYnZX() {
        return this.mapnYnZX(this);
    }

    public Matrix3f mapnYnZX(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        return dest._m00(-this.m10)._m01(-this.m11)._m02(-this.m12)._m10(-this.m20)._m11(-this.m21)._m12(-this.m22)._m20(m00)._m21(m01)._m22(m02);
    }

    public Matrix3f mapnYnZnX() {
        return this.mapnYnZnX(this);
    }

    public Matrix3f mapnYnZnX(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        return dest._m00(-this.m10)._m01(-this.m11)._m02(-this.m12)._m10(-this.m20)._m11(-this.m21)._m12(-this.m22)._m20(-m00)._m21(-m01)._m22(-m02);
    }

    public Matrix3f mapnZXY() {
        return this.mapnZXY(this);
    }

    public Matrix3f mapnZXY(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        float m10 = this.m10;
        float m11 = this.m11;
        float m12 = this.m12;
        return dest._m00(-this.m20)._m01(-this.m21)._m02(-this.m22)._m10(m00)._m11(m01)._m12(m02)._m20(m10)._m21(m11)._m22(m12);
    }

    public Matrix3f mapnZXnY() {
        return this.mapnZXnY(this);
    }

    public Matrix3f mapnZXnY(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        float m10 = this.m10;
        float m11 = this.m11;
        float m12 = this.m12;
        return dest._m00(-this.m20)._m01(-this.m21)._m02(-this.m22)._m10(m00)._m11(m01)._m12(m02)._m20(-m10)._m21(-m11)._m22(-m12);
    }

    public Matrix3f mapnZYX() {
        return this.mapnZYX(this);
    }

    public Matrix3f mapnZYX(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        return dest._m00(-this.m20)._m01(-this.m21)._m02(-this.m22)._m10(this.m10)._m11(this.m11)._m12(this.m12)._m20(m00)._m21(m01)._m22(m02);
    }

    public Matrix3f mapnZYnX() {
        return this.mapnZYnX(this);
    }

    public Matrix3f mapnZYnX(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        return dest._m00(-this.m20)._m01(-this.m21)._m02(-this.m22)._m10(this.m10)._m11(this.m11)._m12(this.m12)._m20(-m00)._m21(-m01)._m22(-m02);
    }

    public Matrix3f mapnZnXY() {
        return this.mapnZnXY(this);
    }

    public Matrix3f mapnZnXY(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        float m10 = this.m10;
        float m11 = this.m11;
        float m12 = this.m12;
        return dest._m00(-this.m20)._m01(-this.m21)._m02(-this.m22)._m10(-m00)._m11(-m01)._m12(-m02)._m20(m10)._m21(m11)._m22(m12);
    }

    public Matrix3f mapnZnXnY() {
        return this.mapnZnXnY(this);
    }

    public Matrix3f mapnZnXnY(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        float m10 = this.m10;
        float m11 = this.m11;
        float m12 = this.m12;
        return dest._m00(-this.m20)._m01(-this.m21)._m02(-this.m22)._m10(-m00)._m11(-m01)._m12(-m02)._m20(-m10)._m21(-m11)._m22(-m12);
    }

    public Matrix3f mapnZnYX() {
        return this.mapnZnYX(this);
    }

    public Matrix3f mapnZnYX(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        return dest._m00(-this.m20)._m01(-this.m21)._m02(-this.m22)._m10(-this.m10)._m11(-this.m11)._m12(-this.m12)._m20(m00)._m21(m01)._m22(m02);
    }

    public Matrix3f mapnZnYnX() {
        return this.mapnZnYnX(this);
    }

    public Matrix3f mapnZnYnX(Matrix3f dest) {
        float m00 = this.m00;
        float m01 = this.m01;
        float m02 = this.m02;
        return dest._m00(-this.m20)._m01(-this.m21)._m02(-this.m22)._m10(-this.m10)._m11(-this.m11)._m12(-this.m12)._m20(-m00)._m21(-m01)._m22(-m02);
    }

    public Matrix3f negateX() {
        return this._m00(-this.m00)._m01(-this.m01)._m02(-this.m02);
    }

    public Matrix3f negateX(Matrix3f dest) {
        return dest._m00(-this.m00)._m01(-this.m01)._m02(-this.m02)._m10(this.m10)._m11(this.m11)._m12(this.m12)._m20(this.m20)._m21(this.m21)._m22(this.m22);
    }

    public Matrix3f negateY() {
        return this._m10(-this.m10)._m11(-this.m11)._m12(-this.m12);
    }

    public Matrix3f negateY(Matrix3f dest) {
        return dest._m00(this.m00)._m01(this.m01)._m02(this.m02)._m10(-this.m10)._m11(-this.m11)._m12(-this.m12)._m20(this.m20)._m21(this.m21)._m22(this.m22);
    }

    public Matrix3f negateZ() {
        return this._m20(-this.m20)._m21(-this.m21)._m22(-this.m22);
    }

    public Matrix3f negateZ(Matrix3f dest) {
        return dest._m00(this.m00)._m01(this.m01)._m02(this.m02)._m10(this.m10)._m11(this.m11)._m12(this.m12)._m20(-this.m20)._m21(-this.m21)._m22(-this.m22);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

