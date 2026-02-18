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
import org.joml.Math;
import org.joml.Matrix4fc;
import org.joml.Matrix4x3fc;
import org.joml.MemUtil;
import org.joml.Options;
import org.joml.Quaternionfc;
import org.joml.Runtime;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2ic;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3ic;
import org.joml.Vector4d;
import org.joml.Vector4dc;
import org.joml.Vector4fc;
import org.joml.Vector4i;
import org.joml.Vector4ic;

public class Vector4f
implements Externalizable,
Cloneable,
Vector4fc {
    private static final long serialVersionUID = 1L;
    public float x;
    public float y;
    public float z;
    public float w;

    public Vector4f() {
        this.w = 1.0f;
    }

    public Vector4f(Vector4fc v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = v.w();
    }

    public Vector4f(Vector4dc v) {
        this.x = (float)v.x();
        this.y = (float)v.y();
        this.z = (float)v.z();
        this.w = (float)v.w();
    }

    public Vector4f(Vector4ic v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = v.w();
    }

    public Vector4f(Vector3fc v, float w) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = w;
    }

    public Vector4f(Vector3ic v, float w) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = w;
    }

    public Vector4f(Vector2fc v, float z, float w) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
        this.w = w;
    }

    public Vector4f(Vector2ic v, float z, float w) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
        this.w = w;
    }

    public Vector4f(float d) {
        this.x = d;
        this.y = d;
        this.z = d;
        this.w = d;
    }

    public Vector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4f(float[] xyzw) {
        this.x = xyzw[0];
        this.y = xyzw[1];
        this.z = xyzw[2];
        this.w = xyzw[3];
    }

    public Vector4f(ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
    }

    public Vector4f(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
    }

    public Vector4f(FloatBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
    }

    public Vector4f(int index, FloatBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
    }

    public float x() {
        return this.x;
    }

    public float y() {
        return this.y;
    }

    public float z() {
        return this.z;
    }

    public float w() {
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

    public Vector4f set(Vector4fc v) {
        if (v == this) {
            return this;
        }
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = v.w();
        return this;
    }

    public Vector4f set(Vector4ic v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = v.w();
        return this;
    }

    public Vector4f set(Vector4dc v) {
        this.x = (float)v.x();
        this.y = (float)v.y();
        this.z = (float)v.z();
        this.w = (float)v.w();
        return this;
    }

    public Vector4f set(Vector3fc v, float w) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = w;
        return this;
    }

    public Vector4f set(Vector3dc v) {
        this.x = (float)v.x();
        this.y = (float)v.y();
        this.z = (float)v.z();
        return this;
    }

    public Vector4f set(Vector3ic v, float w) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        this.w = w;
        return this;
    }

    public Vector4f set(Vector2fc v, float z, float w) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
        this.w = w;
        return this;
    }

    public Vector4f set(Vector2ic v, float z, float w) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
        this.w = w;
        return this;
    }

    public Vector4f set(float d) {
        this.x = d;
        this.y = d;
        this.z = d;
        this.w = d;
        return this;
    }

    public Vector4f set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    public Vector4f set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vector4f set(double d) {
        this.x = (float)d;
        this.y = (float)d;
        this.z = (float)d;
        this.w = (float)d;
        return this;
    }

    public Vector4f set(double x, double y, double z, double w) {
        this.x = (float)x;
        this.y = (float)y;
        this.z = (float)z;
        this.w = (float)w;
        return this;
    }

    public Vector4f set(float[] xyzw) {
        this.x = xyzw[0];
        this.y = xyzw[1];
        this.z = xyzw[2];
        this.w = xyzw[3];
        return this;
    }

    public Vector4f set(ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
        return this;
    }

    public Vector4f set(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
        return this;
    }

    public Vector4f set(FloatBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
        return this;
    }

    public Vector4f set(int index, FloatBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
        return this;
    }

    public Vector4f setFromAddress(long address) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        }
        MemUtil.MemUtilUnsafe.get(this, address);
        return this;
    }

    public Vector4f setComponent(int component, float value) throws IllegalArgumentException {
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

    public FloatBuffer get(FloatBuffer buffer) {
        MemUtil.INSTANCE.put(this, buffer.position(), buffer);
        return buffer;
    }

    public FloatBuffer get(int index, FloatBuffer buffer) {
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

    public Vector4fc getToAddress(long address) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        }
        MemUtil.MemUtilUnsafe.put(this, address);
        return this;
    }

    public Vector4f sub(Vector4fc v) {
        return this.sub(v, this);
    }

    public Vector4f sub(float x, float y, float z, float w) {
        return this.sub(x, y, z, w, this);
    }

    public Vector4f sub(Vector4fc v, Vector4f dest) {
        dest.x = this.x - v.x();
        dest.y = this.y - v.y();
        dest.z = this.z - v.z();
        dest.w = this.w - v.w();
        return dest;
    }

    public Vector4f sub(float x, float y, float z, float w, Vector4f dest) {
        dest.x = this.x - x;
        dest.y = this.y - y;
        dest.z = this.z - z;
        dest.w = this.w - w;
        return dest;
    }

    public Vector4f add(Vector4fc v) {
        return this.add(v, this);
    }

    public Vector4f add(Vector4fc v, Vector4f dest) {
        dest.x = this.x + v.x();
        dest.y = this.y + v.y();
        dest.z = this.z + v.z();
        dest.w = this.w + v.w();
        return dest;
    }

    public Vector4f add(float x, float y, float z, float w) {
        return this.add(x, y, z, w, this);
    }

    public Vector4f add(float x, float y, float z, float w, Vector4f dest) {
        dest.x = this.x + x;
        dest.y = this.y + y;
        dest.z = this.z + z;
        dest.w = this.w + w;
        return dest;
    }

    public Vector4f fma(Vector4fc a, Vector4fc b) {
        return this.fma(a, b, this);
    }

    public Vector4f fma(float a, Vector4fc b) {
        return this.fma(a, b, this);
    }

    public Vector4f fma(Vector4fc a, Vector4fc b, Vector4f dest) {
        dest.x = Math.fma(a.x(), b.x(), this.x);
        dest.y = Math.fma(a.y(), b.y(), this.y);
        dest.z = Math.fma(a.z(), b.z(), this.z);
        dest.w = Math.fma(a.w(), b.w(), this.w);
        return dest;
    }

    public Vector4f fma(float a, Vector4fc b, Vector4f dest) {
        dest.x = Math.fma(a, b.x(), this.x);
        dest.y = Math.fma(a, b.y(), this.y);
        dest.z = Math.fma(a, b.z(), this.z);
        dest.w = Math.fma(a, b.w(), this.w);
        return dest;
    }

    public Vector4f mulAdd(Vector4fc a, Vector4fc b) {
        return this.mulAdd(a, b, this);
    }

    public Vector4f mulAdd(float a, Vector4fc b) {
        return this.mulAdd(a, b, this);
    }

    public Vector4f mulAdd(Vector4fc a, Vector4fc b, Vector4f dest) {
        dest.x = Math.fma(this.x, a.x(), b.x());
        dest.y = Math.fma(this.y, a.y(), b.y());
        dest.z = Math.fma(this.z, a.z(), b.z());
        return dest;
    }

    public Vector4f mulAdd(float a, Vector4fc b, Vector4f dest) {
        dest.x = Math.fma(this.x, a, b.x());
        dest.y = Math.fma(this.y, a, b.y());
        dest.z = Math.fma(this.z, a, b.z());
        return dest;
    }

    public Vector4f mul(Vector4fc v) {
        return this.mul(v, this);
    }

    public Vector4f mul(Vector4fc v, Vector4f dest) {
        dest.x = this.x * v.x();
        dest.y = this.y * v.y();
        dest.z = this.z * v.z();
        dest.w = this.w * v.w();
        return dest;
    }

    public Vector4f div(Vector4fc v) {
        return this.div(v, this);
    }

    public Vector4f div(Vector4fc v, Vector4f dest) {
        dest.x = this.x / v.x();
        dest.y = this.y / v.y();
        dest.z = this.z / v.z();
        dest.w = this.w / v.w();
        return dest;
    }

    public Vector4f mul(Matrix4fc mat) {
        return this.mul(mat, this);
    }

    public Vector4f mul(Matrix4fc mat, Vector4f dest) {
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

    public Vector4f mulAffine(Matrix4fc mat) {
        return this.mulAffine(mat, this);
    }

    public Vector4f mulAffine(Matrix4fc mat, Vector4f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        float w = this.w;
        dest.x = Math.fma(mat.m00(), x, Math.fma(mat.m10(), y, Math.fma(mat.m20(), z, mat.m30() * w)));
        dest.y = Math.fma(mat.m01(), x, Math.fma(mat.m11(), y, Math.fma(mat.m21(), z, mat.m31() * w)));
        dest.z = Math.fma(mat.m02(), x, Math.fma(mat.m12(), y, Math.fma(mat.m22(), z, mat.m32() * w)));
        dest.w = w;
        return dest;
    }

    public Vector4f mulTranspose(Matrix4fc mat) {
        return this.mulTranspose(mat, this);
    }

    public Vector4f mulTranspose(Matrix4fc mat, Vector4f dest) {
        int prop = mat.properties();
        if ((prop & 4) != 0) {
            return dest.set(this);
        }
        if ((prop & 2) != 0) {
            return this.mulAffineTranspose(mat, dest);
        }
        return this.mulGenericTranspose(mat, dest);
    }

    public Vector4f mulAffineTranspose(Matrix4fc mat) {
        return this.mulAffineTranspose(mat, this);
    }

    public Vector4f mulAffineTranspose(Matrix4fc mat, Vector4f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        float w = this.w;
        dest.x = Math.fma(mat.m00(), x, Math.fma(mat.m01(), y, mat.m02() * z));
        dest.y = Math.fma(mat.m10(), x, Math.fma(mat.m11(), y, mat.m12() * z));
        dest.z = Math.fma(mat.m20(), x, Math.fma(mat.m21(), y, mat.m22() * z));
        dest.w = Math.fma(mat.m30(), x, Math.fma(mat.m31(), y, mat.m32() * z + w));
        return dest;
    }

    public Vector4f mulGenericTranspose(Matrix4fc mat) {
        return this.mulGenericTranspose(mat, this);
    }

    public Vector4f mulGenericTranspose(Matrix4fc mat, Vector4f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        float w = this.w;
        dest.x = Math.fma(mat.m00(), x, Math.fma(mat.m01(), y, Math.fma(mat.m02(), z, mat.m03() * w)));
        dest.y = Math.fma(mat.m10(), x, Math.fma(mat.m11(), y, Math.fma(mat.m12(), z, mat.m13() * w)));
        dest.z = Math.fma(mat.m20(), x, Math.fma(mat.m21(), y, Math.fma(mat.m22(), z, mat.m23() * w)));
        dest.w = Math.fma(mat.m30(), x, Math.fma(mat.m31(), y, Math.fma(mat.m32(), z, mat.m33() * w)));
        return dest;
    }

    public Vector4f mulTranslation(Matrix4fc mat) {
        return this.mulTranslation(mat, this);
    }

    public Vector4f mulTranslation(Matrix4fc mat, Vector4f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        float w = this.w;
        dest.x = Math.fma(mat.m30(), w, x);
        dest.y = Math.fma(mat.m31(), w, y);
        dest.z = Math.fma(mat.m32(), w, z);
        dest.w = w;
        return dest;
    }

    public Vector4f mulGeneric(Matrix4fc mat) {
        return this.mulGeneric(mat, this);
    }

    public Vector4f mulGeneric(Matrix4fc mat, Vector4f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        float w = this.w;
        dest.x = Math.fma(mat.m00(), x, Math.fma(mat.m10(), y, Math.fma(mat.m20(), z, mat.m30() * w)));
        dest.y = Math.fma(mat.m01(), x, Math.fma(mat.m11(), y, Math.fma(mat.m21(), z, mat.m31() * w)));
        dest.z = Math.fma(mat.m02(), x, Math.fma(mat.m12(), y, Math.fma(mat.m22(), z, mat.m32() * w)));
        dest.w = Math.fma(mat.m03(), x, Math.fma(mat.m13(), y, Math.fma(mat.m23(), z, mat.m33() * w)));
        return dest;
    }

    public Vector4f mul(Matrix4x3fc mat) {
        return this.mul(mat, this);
    }

    public Vector4f mul(Matrix4x3fc mat, Vector4f dest) {
        int prop = mat.properties();
        if ((prop & 4) != 0) {
            return dest.set(this);
        }
        if ((prop & 8) != 0) {
            return this.mulTranslation(mat, dest);
        }
        return this.mulGeneric(mat, dest);
    }

    public Vector4f mulGeneric(Matrix4x3fc mat) {
        return this.mulGeneric(mat, this);
    }

    public Vector4f mulGeneric(Matrix4x3fc mat, Vector4f dest) {
        float rx = Math.fma(mat.m00(), this.x, Math.fma(mat.m10(), this.y, Math.fma(mat.m20(), this.z, mat.m30() * this.w)));
        float ry = Math.fma(mat.m01(), this.x, Math.fma(mat.m11(), this.y, Math.fma(mat.m21(), this.z, mat.m31() * this.w)));
        float rz = Math.fma(mat.m02(), this.x, Math.fma(mat.m12(), this.y, Math.fma(mat.m22(), this.z, mat.m32() * this.w)));
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        dest.w = this.w;
        return dest;
    }

    public Vector4f mulTranslation(Matrix4x3fc mat) {
        return this.mulTranslation(mat, this);
    }

    public Vector4f mulTranslation(Matrix4x3fc mat, Vector4f dest) {
        dest.x = Math.fma(mat.m30(), this.w, this.x);
        dest.y = Math.fma(mat.m31(), this.w, this.y);
        dest.z = Math.fma(mat.m32(), this.w, this.z);
        dest.w = this.w;
        return dest;
    }

    public Vector4f mulProject(Matrix4fc mat) {
        return this.mulProject(mat, this);
    }

    public Vector4f mulProject(Matrix4fc mat, Vector4f dest) {
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

    public Vector3f mulProject(Matrix4fc mat, Vector3f dest) {
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

    public Vector4f mulProjectGeneric(Matrix4fc mat) {
        return this.mulProjectGeneric(mat, this);
    }

    public Vector4f mulProjectGeneric(Matrix4fc mat, Vector4f dest) {
        float invW = 1.0f / Math.fma(mat.m03(), this.x, Math.fma(mat.m13(), this.y, Math.fma(mat.m23(), this.z, mat.m33() * this.w)));
        float rx = Math.fma(mat.m00(), this.x, Math.fma(mat.m10(), this.y, Math.fma(mat.m20(), this.z, mat.m30() * this.w))) * invW;
        float ry = Math.fma(mat.m01(), this.x, Math.fma(mat.m11(), this.y, Math.fma(mat.m21(), this.z, mat.m31() * this.w))) * invW;
        float rz = Math.fma(mat.m02(), this.x, Math.fma(mat.m12(), this.y, Math.fma(mat.m22(), this.z, mat.m32() * this.w))) * invW;
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        dest.w = 1.0f;
        return dest;
    }

    public Vector3f mulProjectGeneric(Matrix4fc mat, Vector3f dest) {
        float invW = 1.0f / Math.fma(mat.m03(), this.x, Math.fma(mat.m13(), this.y, Math.fma(mat.m23(), this.z, mat.m33() * this.w)));
        float rx = Math.fma(mat.m00(), this.x, Math.fma(mat.m10(), this.y, Math.fma(mat.m20(), this.z, mat.m30() * this.w))) * invW;
        float ry = Math.fma(mat.m01(), this.x, Math.fma(mat.m11(), this.y, Math.fma(mat.m21(), this.z, mat.m31() * this.w))) * invW;
        float rz = Math.fma(mat.m02(), this.x, Math.fma(mat.m12(), this.y, Math.fma(mat.m22(), this.z, mat.m32() * this.w))) * invW;
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    public Vector4f mulProjectTranslation(Matrix4fc mat) {
        return this.mulProjectTranslation(mat, this);
    }

    public Vector4f mulProjectTranslation(Matrix4fc mat, Vector4f dest) {
        float invW = 1.0f / this.w;
        float rx = Math.fma(mat.m00(), this.x, mat.m30() * this.w) * invW;
        float ry = Math.fma(mat.m11(), this.y, mat.m31() * this.w) * invW;
        float rz = Math.fma(mat.m22(), this.z, mat.m32() * this.w) * invW;
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        dest.w = 1.0f;
        return dest;
    }

    public Vector3f mulProjectTranslation(Matrix4fc mat, Vector3f dest) {
        float invW = 1.0f / this.w;
        float rx = Math.fma(mat.m00(), this.x, mat.m30() * this.w) * invW;
        float ry = Math.fma(mat.m11(), this.y, mat.m31() * this.w) * invW;
        float rz = Math.fma(mat.m22(), this.z, mat.m32() * this.w) * invW;
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    public Vector4f mulProjectAffine(Matrix4fc mat) {
        return this.mulProjectAffine(mat, this);
    }

    public Vector4f mulProjectAffine(Matrix4fc mat, Vector4f dest) {
        float invW = 1.0f / this.w;
        float rx = Math.fma(mat.m00(), this.x, Math.fma(mat.m10(), this.y, Math.fma(mat.m20(), this.z, mat.m30() * this.w))) * invW;
        float ry = Math.fma(mat.m01(), this.x, Math.fma(mat.m11(), this.y, Math.fma(mat.m21(), this.z, mat.m31() * this.w))) * invW;
        float rz = Math.fma(mat.m02(), this.x, Math.fma(mat.m12(), this.y, Math.fma(mat.m22(), this.z, mat.m32() * this.w))) * invW;
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        dest.w = 1.0f;
        return dest;
    }

    public Vector3f mulProjectAffine(Matrix4fc mat, Vector3f dest) {
        float invW = 1.0f / this.w;
        float rx = Math.fma(mat.m00(), this.x, Math.fma(mat.m10(), this.y, Math.fma(mat.m20(), this.z, mat.m30() * this.w))) * invW;
        float ry = Math.fma(mat.m01(), this.x, Math.fma(mat.m11(), this.y, Math.fma(mat.m21(), this.z, mat.m31() * this.w))) * invW;
        float rz = Math.fma(mat.m02(), this.x, Math.fma(mat.m12(), this.y, Math.fma(mat.m22(), this.z, mat.m32() * this.w))) * invW;
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    public Vector4f mul(float scalar) {
        return this.mul(scalar, this);
    }

    public Vector4f mul(float scalar, Vector4f dest) {
        dest.x = this.x * scalar;
        dest.y = this.y * scalar;
        dest.z = this.z * scalar;
        dest.w = this.w * scalar;
        return dest;
    }

    public Vector4f mul(float x, float y, float z, float w) {
        return this.mul(x, y, z, w, this);
    }

    public Vector4f mul(float x, float y, float z, float w, Vector4f dest) {
        dest.x = this.x * x;
        dest.y = this.y * y;
        dest.z = this.z * z;
        dest.w = this.w * w;
        return dest;
    }

    public Vector4f div(float scalar) {
        return this.div(scalar, this);
    }

    public Vector4f div(float scalar, Vector4f dest) {
        float inv = 1.0f / scalar;
        dest.x = this.x * inv;
        dest.y = this.y * inv;
        dest.z = this.z * inv;
        dest.w = this.w * inv;
        return dest;
    }

    public Vector4f div(float x, float y, float z, float w) {
        return this.div(x, y, z, w, this);
    }

    public Vector4f div(float x, float y, float z, float w, Vector4f dest) {
        dest.x = this.x / x;
        dest.y = this.y / y;
        dest.z = this.z / z;
        dest.w = this.w / w;
        return dest;
    }

    public Vector4f rotate(Quaternionfc quat) {
        return quat.transform(this, this);
    }

    public Vector4f rotate(Quaternionfc quat, Vector4f dest) {
        return quat.transform(this, dest);
    }

    public Vector4f rotateAxis(float angle, float x, float y, float z) {
        return this.rotateAxis(angle, x, y, z, this);
    }

    public Vector4f rotateAxis(float angle, float aX, float aY, float aZ, Vector4f dest) {
        if (aY == 0.0f && aZ == 0.0f && Math.absEqualsOne(aX)) {
            return this.rotateX(aX * angle, dest);
        }
        if (aX == 0.0f && aZ == 0.0f && Math.absEqualsOne(aY)) {
            return this.rotateY(aY * angle, dest);
        }
        if (aX == 0.0f && aY == 0.0f && Math.absEqualsOne(aZ)) {
            return this.rotateZ(aZ * angle, dest);
        }
        return this.rotateAxisInternal(angle, aX, aY, aZ, dest);
    }

    private Vector4f rotateAxisInternal(float angle, float aX, float aY, float aZ, Vector4f dest) {
        float hangle = angle * 0.5f;
        float sinAngle = Math.sin(hangle);
        float qx = aX * sinAngle;
        float qy = aY * sinAngle;
        float qz = aZ * sinAngle;
        float qw = Math.cosFromSin(sinAngle, hangle);
        float w2 = qw * qw;
        float x2 = qx * qx;
        float y2 = qy * qy;
        float z2 = qz * qz;
        float zw = qz * qw;
        float xy = qx * qy;
        float xz = qx * qz;
        float yw = qy * qw;
        float yz = qy * qz;
        float xw = qx * qw;
        float x = this.x;
        float y = this.y;
        float z = this.z;
        dest.x = (w2 + x2 - z2 - y2) * x + (-zw + xy - zw + xy) * y + (yw + xz + xz + yw) * z;
        dest.y = (xy + zw + zw + xy) * x + (y2 - z2 + w2 - x2) * y + (yz + yz - xw - xw) * z;
        dest.z = (xz - yw + xz - yw) * x + (yz + yz + xw + xw) * y + (z2 - y2 - x2 + w2) * z;
        return dest;
    }

    public Vector4f rotateX(float angle) {
        return this.rotateX(angle, this);
    }

    public Vector4f rotateX(float angle, Vector4f dest) {
        float sin = Math.sin(angle);
        float cos = Math.cosFromSin(sin, angle);
        float y = this.y * cos - this.z * sin;
        float z = this.y * sin + this.z * cos;
        dest.x = this.x;
        dest.y = y;
        dest.z = z;
        dest.w = this.w;
        return dest;
    }

    public Vector4f rotateY(float angle) {
        return this.rotateY(angle, this);
    }

    public Vector4f rotateY(float angle, Vector4f dest) {
        float sin = Math.sin(angle);
        float cos = Math.cosFromSin(sin, angle);
        float x = this.x * cos + this.z * sin;
        float z = -this.x * sin + this.z * cos;
        dest.x = x;
        dest.y = this.y;
        dest.z = z;
        dest.w = this.w;
        return dest;
    }

    public Vector4f rotateZ(float angle) {
        return this.rotateZ(angle, this);
    }

    public Vector4f rotateZ(float angle, Vector4f dest) {
        float sin = Math.sin(angle);
        float cos = Math.cosFromSin(sin, angle);
        float x = this.x * cos - this.y * sin;
        float y = this.x * sin + this.y * cos;
        dest.x = x;
        dest.y = y;
        dest.z = this.z;
        dest.w = this.w;
        return dest;
    }

    public float lengthSquared() {
        return Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
    }

    public static float lengthSquared(float x, float y, float z, float w) {
        return Math.fma(x, x, Math.fma(y, y, Math.fma(z, z, w * w)));
    }

    public static float lengthSquared(int x, int y, int z, int w) {
        return Math.fma(x, x, Math.fma(y, y, Math.fma(z, z, w * w)));
    }

    public float length() {
        return Math.sqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w))));
    }

    public static float length(float x, float y, float z, float w) {
        return Math.sqrt(Math.fma(x, x, Math.fma(y, y, Math.fma(z, z, w * w))));
    }

    public Vector4f normalize() {
        return this.normalize(this);
    }

    public Vector4f normalize(Vector4f dest) {
        float invLength = 1.0f / this.length();
        dest.x = this.x * invLength;
        dest.y = this.y * invLength;
        dest.z = this.z * invLength;
        dest.w = this.w * invLength;
        return dest;
    }

    public Vector4f normalize(float length) {
        return this.normalize(length, this);
    }

    public Vector4f normalize(float length, Vector4f dest) {
        float invLength = 1.0f / this.length() * length;
        dest.x = this.x * invLength;
        dest.y = this.y * invLength;
        dest.z = this.z * invLength;
        dest.w = this.w * invLength;
        return dest;
    }

    public Vector4f normalize3() {
        return this.normalize3(this);
    }

    public Vector4f normalize3(Vector4f dest) {
        float invLength = Math.invsqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z)));
        dest.x = this.x * invLength;
        dest.y = this.y * invLength;
        dest.z = this.z * invLength;
        dest.w = this.w * invLength;
        return dest;
    }

    public float distance(Vector4fc v) {
        return this.distance(v.x(), v.y(), v.z(), v.w());
    }

    public float distance(float x, float y, float z, float w) {
        float dx = this.x - x;
        float dy = this.y - y;
        float dz = this.z - z;
        float dw = this.w - w;
        return Math.sqrt(Math.fma(dx, dx, Math.fma(dy, dy, Math.fma(dz, dz, dw * dw))));
    }

    public float distanceSquared(Vector4fc v) {
        return this.distanceSquared(v.x(), v.y(), v.z(), v.w());
    }

    public float distanceSquared(float x, float y, float z, float w) {
        float dx = this.x - x;
        float dy = this.y - y;
        float dz = this.z - z;
        float dw = this.w - w;
        return Math.fma(dx, dx, Math.fma(dy, dy, Math.fma(dz, dz, dw * dw)));
    }

    public static float distance(float x1, float y1, float z1, float w1, float x2, float y2, float z2, float w2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        float dz = z1 - z2;
        float dw = w1 - w2;
        return Math.sqrt(Math.fma(dx, dx, Math.fma(dy, dy, Math.fma(dz, dz, dw * dw))));
    }

    public static float distanceSquared(float x1, float y1, float z1, float w1, float x2, float y2, float z2, float w2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        float dz = z1 - z2;
        float dw = w1 - w2;
        return Math.fma(dx, dx, Math.fma(dy, dy, Math.fma(dz, dz, dw * dw)));
    }

    public float dot(Vector4fc v) {
        return Math.fma(this.x, v.x(), Math.fma(this.y, v.y(), Math.fma(this.z, v.z(), this.w * v.w())));
    }

    public float dot(float x, float y, float z, float w) {
        return Math.fma(this.x, x, Math.fma(this.y, y, Math.fma(this.z, z, this.w * w)));
    }

    public float angleCos(Vector4fc v) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        float w = this.w;
        float length1Squared = Math.fma(x, x, Math.fma(y, y, Math.fma(z, z, w * w)));
        float length2Squared = Math.fma(v.x(), v.x(), Math.fma(v.y(), v.y(), Math.fma(v.z(), v.z(), v.w() * v.w())));
        float dot = Math.fma(x, v.x(), Math.fma(y, v.y(), Math.fma(z, v.z(), w * v.w())));
        return dot / Math.sqrt(length1Squared * length2Squared);
    }

    public float angle(Vector4fc v) {
        float cos = this.angleCos(v);
        cos = cos < 1.0f ? cos : 1.0f;
        cos = cos > -1.0f ? cos : -1.0f;
        return Math.acos(cos);
    }

    public Vector4f zero() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        this.w = 0.0f;
        return this;
    }

    public Vector4f negate() {
        return this.negate(this);
    }

    public Vector4f negate(Vector4f dest) {
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
        return "(" + Runtime.format(this.x, formatter) + " " + Runtime.format(this.y, formatter) + " " + Runtime.format(this.z, formatter) + " " + Runtime.format(this.w, formatter) + ")";
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeFloat(this.x);
        out.writeFloat(this.y);
        out.writeFloat(this.z);
        out.writeFloat(this.w);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.set(in.readFloat(), in.readFloat(), in.readFloat(), in.readFloat());
    }

    public Vector4f min(Vector4fc v) {
        return this.min(v, this);
    }

    public Vector4f min(Vector4fc v, Vector4f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        float w = this.w;
        dest.x = x < v.x() ? x : v.x();
        dest.y = y < v.y() ? y : v.y();
        dest.z = z < v.z() ? z : v.z();
        dest.w = w < v.w() ? w : v.w();
        return dest;
    }

    public Vector4f max(Vector4fc v) {
        return this.max(v, this);
    }

    public Vector4f max(Vector4fc v, Vector4f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        float w = this.w;
        dest.x = x > v.x() ? x : v.x();
        dest.y = y > v.y() ? y : v.y();
        dest.z = z > v.z() ? z : v.z();
        dest.w = w > v.w() ? w : v.w();
        return dest;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + Float.floatToIntBits(this.w);
        result = 31 * result + Float.floatToIntBits(this.x);
        result = 31 * result + Float.floatToIntBits(this.y);
        result = 31 * result + Float.floatToIntBits(this.z);
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
        Vector4f other = (Vector4f)obj;
        if (Float.floatToIntBits(this.w) != Float.floatToIntBits(other.w)) {
            return false;
        }
        if (Float.floatToIntBits(this.x) != Float.floatToIntBits(other.x)) {
            return false;
        }
        if (Float.floatToIntBits(this.y) != Float.floatToIntBits(other.y)) {
            return false;
        }
        return Float.floatToIntBits(this.z) == Float.floatToIntBits(other.z);
    }

    public boolean equals(Vector4fc v, float delta) {
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

    public boolean equals(float x, float y, float z, float w) {
        if (Float.floatToIntBits(this.x) != Float.floatToIntBits(x)) {
            return false;
        }
        if (Float.floatToIntBits(this.y) != Float.floatToIntBits(y)) {
            return false;
        }
        if (Float.floatToIntBits(this.z) != Float.floatToIntBits(z)) {
            return false;
        }
        return Float.floatToIntBits(this.w) == Float.floatToIntBits(w);
    }

    public Vector4f smoothStep(Vector4fc v, float t, Vector4f dest) {
        float t2 = t * t;
        float t3 = t2 * t;
        float x = this.x;
        float y = this.y;
        float z = this.z;
        float w = this.w;
        dest.x = (x + x - v.x() - v.x()) * t3 + (3.0f * v.x() - 3.0f * x) * t2 + x * t + x;
        dest.y = (y + y - v.y() - v.y()) * t3 + (3.0f * v.y() - 3.0f * y) * t2 + y * t + y;
        dest.z = (z + z - v.z() - v.z()) * t3 + (3.0f * v.z() - 3.0f * z) * t2 + z * t + z;
        dest.w = (w + w - v.w() - v.w()) * t3 + (3.0f * v.w() - 3.0f * w) * t2 + w * t + w;
        return dest;
    }

    public Vector4f hermite(Vector4fc t0, Vector4fc v1, Vector4fc t1, float t, Vector4f dest) {
        float t2 = t * t;
        float t3 = t2 * t;
        float x = this.x;
        float y = this.y;
        float z = this.z;
        float w = this.w;
        dest.x = (x + x - v1.x() - v1.x() + t1.x() + t0.x()) * t3 + (3.0f * v1.x() - 3.0f * x - t0.x() - t0.x() - t1.x()) * t2 + x * t + x;
        dest.y = (y + y - v1.y() - v1.y() + t1.y() + t0.y()) * t3 + (3.0f * v1.y() - 3.0f * y - t0.y() - t0.y() - t1.y()) * t2 + y * t + y;
        dest.z = (z + z - v1.z() - v1.z() + t1.z() + t0.z()) * t3 + (3.0f * v1.z() - 3.0f * z - t0.z() - t0.z() - t1.z()) * t2 + z * t + z;
        dest.w = (w + w - v1.w() - v1.w() + t1.w() + t0.w()) * t3 + (3.0f * v1.w() - 3.0f * w - t0.w() - t0.w() - t1.w()) * t2 + w * t + w;
        return dest;
    }

    public Vector4f lerp(Vector4fc other, float t) {
        return this.lerp(other, t, this);
    }

    public Vector4f lerp(Vector4fc other, float t, Vector4f dest) {
        dest.x = Math.fma(other.x() - this.x, t, this.x);
        dest.y = Math.fma(other.y() - this.y, t, this.y);
        dest.z = Math.fma(other.z() - this.z, t, this.z);
        dest.w = Math.fma(other.w() - this.w, t, this.w);
        return dest;
    }

    public float get(int component) throws IllegalArgumentException {
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
        dest.x = this.x();
        dest.y = this.y();
        dest.z = this.z();
        dest.w = this.w();
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
        float absX = Math.abs(this.x);
        float absY = Math.abs(this.y);
        float absZ = Math.abs(this.z);
        float absW = Math.abs(this.w);
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
        float absX = Math.abs(this.x);
        float absY = Math.abs(this.y);
        float absZ = Math.abs(this.z);
        float absW = Math.abs(this.w);
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

    public Vector4f floor() {
        return this.floor(this);
    }

    public Vector4f floor(Vector4f dest) {
        dest.x = Math.floor(this.x);
        dest.y = Math.floor(this.y);
        dest.z = Math.floor(this.z);
        dest.w = Math.floor(this.w);
        return dest;
    }

    public Vector4f ceil() {
        return this.ceil(this);
    }

    public Vector4f ceil(Vector4f dest) {
        dest.x = Math.ceil(this.x);
        dest.y = Math.ceil(this.y);
        dest.z = Math.ceil(this.z);
        dest.w = Math.ceil(this.w);
        return dest;
    }

    public Vector4f round() {
        return this.round(this);
    }

    public Vector4f round(Vector4f dest) {
        dest.x = Math.round(this.x);
        dest.y = Math.round(this.y);
        dest.z = Math.round(this.z);
        dest.w = Math.round(this.w);
        return dest;
    }

    public boolean isFinite() {
        return Math.isFinite(this.x) && Math.isFinite(this.y) && Math.isFinite(this.z) && Math.isFinite(this.w);
    }

    public Vector4f absolute() {
        return this.absolute(this);
    }

    public Vector4f absolute(Vector4f dest) {
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

