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
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Runtime;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2ic;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.joml.Vector4dc;
import org.joml.Vector4fc;
import org.joml.Vector4ic;

public class Vector3f
implements Externalizable,
Cloneable,
Vector3fc {
    private static final long serialVersionUID = 1L;
    public float x;
    public float y;
    public float z;

    public Vector3f() {
    }

    public Vector3f(float d) {
        this.x = d;
        this.y = d;
        this.z = d;
    }

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f(Vector3fc v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
    }

    public Vector3f(Vector3dc v) {
        this.x = (float)v.x();
        this.y = (float)v.y();
        this.z = (float)v.z();
    }

    public Vector3f(Vector3ic v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
    }

    public Vector3f(Vector2fc v, float z) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
    }

    public Vector3f(Vector2ic v, float z) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
    }

    public Vector3f(float[] xyz) {
        this.x = xyz[0];
        this.y = xyz[1];
        this.z = xyz[2];
    }

    public Vector3f(ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
    }

    public Vector3f(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
    }

    public Vector3f(FloatBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
    }

    public Vector3f(int index, FloatBuffer buffer) {
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

    public Vector2f xy(Vector2f dest) {
        return dest.set(this.x, this.y);
    }

    public Vector2d xy(Vector2d dest) {
        return dest.set(this.x, this.y);
    }

    public Vector3f set(Vector4dc v) {
        this.x = (float)v.x();
        this.y = (float)v.y();
        this.z = (float)v.z();
        return this;
    }

    public Vector3f set(Vector4fc v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        return this;
    }

    public Vector3f set(Vector4ic v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        return this;
    }

    public Vector3f set(Vector3fc v) {
        if (v == this) {
            return this;
        }
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        return this;
    }

    public Vector3f set(Vector3dc v) {
        this.x = (float)v.x();
        this.y = (float)v.y();
        this.z = (float)v.z();
        return this;
    }

    public Vector3f set(Vector3ic v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
        return this;
    }

    public Vector3f set(Vector2fc v, float z) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
        return this;
    }

    public Vector3f set(Vector2dc v, float z) {
        this.x = (float)v.x();
        this.y = (float)v.y();
        this.z = z;
        return this;
    }

    public Vector3f set(Vector2ic v, float z) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
        return this;
    }

    public Vector3f set(float d) {
        this.x = d;
        this.y = d;
        this.z = d;
        return this;
    }

    public Vector3f set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vector3f set(double d) {
        this.x = (float)d;
        this.y = (float)d;
        this.z = (float)d;
        return this;
    }

    public Vector3f set(double x, double y, double z) {
        this.x = (float)x;
        this.y = (float)y;
        this.z = (float)z;
        return this;
    }

    public Vector3f set(float[] xyz) {
        this.x = xyz[0];
        this.y = xyz[1];
        this.z = xyz[2];
        return this;
    }

    public Vector3f set(ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
        return this;
    }

    public Vector3f set(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
        return this;
    }

    public Vector3f set(FloatBuffer buffer) {
        MemUtil.INSTANCE.get(this, buffer.position(), buffer);
        return this;
    }

    public Vector3f set(int index, FloatBuffer buffer) {
        MemUtil.INSTANCE.get(this, index, buffer);
        return this;
    }

    public Vector3f setFromAddress(long address) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        }
        MemUtil.MemUtilUnsafe.get(this, address);
        return this;
    }

    public Vector3f setComponent(int component, float value) throws IllegalArgumentException {
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

    public Vector3fc getToAddress(long address) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        }
        MemUtil.MemUtilUnsafe.put(this, address);
        return this;
    }

    public Vector3f sub(Vector3fc v) {
        return this.sub(v, this);
    }

    public Vector3f sub(Vector3fc v, Vector3f dest) {
        dest.x = this.x - v.x();
        dest.y = this.y - v.y();
        dest.z = this.z - v.z();
        return dest;
    }

    public Vector3f sub(float x, float y, float z) {
        return this.sub(x, y, z, this);
    }

    public Vector3f sub(float x, float y, float z, Vector3f dest) {
        dest.x = this.x - x;
        dest.y = this.y - y;
        dest.z = this.z - z;
        return dest;
    }

    public Vector3f add(Vector3fc v) {
        return this.add(v, this);
    }

    public Vector3f add(Vector3fc v, Vector3f dest) {
        dest.x = this.x + v.x();
        dest.y = this.y + v.y();
        dest.z = this.z + v.z();
        return dest;
    }

    public Vector3f add(float x, float y, float z) {
        return this.add(x, y, z, this);
    }

    public Vector3f add(float x, float y, float z, Vector3f dest) {
        dest.x = this.x + x;
        dest.y = this.y + y;
        dest.z = this.z + z;
        return dest;
    }

    public Vector3f fma(Vector3fc a, Vector3fc b) {
        return this.fma(a, b, this);
    }

    public Vector3f fma(float a, Vector3fc b) {
        return this.fma(a, b, this);
    }

    public Vector3f fma(Vector3fc a, Vector3fc b, Vector3f dest) {
        dest.x = Math.fma(a.x(), b.x(), this.x);
        dest.y = Math.fma(a.y(), b.y(), this.y);
        dest.z = Math.fma(a.z(), b.z(), this.z);
        return dest;
    }

    public Vector3f fma(float a, Vector3fc b, Vector3f dest) {
        dest.x = Math.fma(a, b.x(), this.x);
        dest.y = Math.fma(a, b.y(), this.y);
        dest.z = Math.fma(a, b.z(), this.z);
        return dest;
    }

    public Vector3f mulAdd(Vector3fc a, Vector3fc b) {
        return this.mulAdd(a, b, this);
    }

    public Vector3f mulAdd(float a, Vector3fc b) {
        return this.mulAdd(a, b, this);
    }

    public Vector3f mulAdd(Vector3fc a, Vector3fc b, Vector3f dest) {
        dest.x = Math.fma(this.x, a.x(), b.x());
        dest.y = Math.fma(this.y, a.y(), b.y());
        dest.z = Math.fma(this.z, a.z(), b.z());
        return dest;
    }

    public Vector3f mulAdd(float a, Vector3fc b, Vector3f dest) {
        dest.x = Math.fma(this.x, a, b.x());
        dest.y = Math.fma(this.y, a, b.y());
        dest.z = Math.fma(this.z, a, b.z());
        return dest;
    }

    public Vector3f mul(Vector3fc v) {
        return this.mul(v, this);
    }

    public Vector3f mul(Vector3fc v, Vector3f dest) {
        dest.x = this.x * v.x();
        dest.y = this.y * v.y();
        dest.z = this.z * v.z();
        return dest;
    }

    public Vector3f div(Vector3fc v) {
        return this.div(v, this);
    }

    public Vector3f div(Vector3fc v, Vector3f dest) {
        dest.x = this.x / v.x();
        dest.y = this.y / v.y();
        dest.z = this.z / v.z();
        return dest;
    }

    public Vector3f mulProject(Matrix4fc mat, float w, Vector3f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        float invW = 1.0f / Math.fma(mat.m03(), x, Math.fma(mat.m13(), y, Math.fma(mat.m23(), z, mat.m33() * w)));
        dest.x = Math.fma(mat.m00(), x, Math.fma(mat.m10(), y, Math.fma(mat.m20(), z, mat.m30() * w))) * invW;
        dest.y = Math.fma(mat.m01(), x, Math.fma(mat.m11(), y, Math.fma(mat.m21(), z, mat.m31() * w))) * invW;
        dest.z = Math.fma(mat.m02(), x, Math.fma(mat.m12(), y, Math.fma(mat.m22(), z, mat.m32() * w))) * invW;
        return dest;
    }

    public Vector3f mulProject(Matrix4fc mat) {
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

    public Vector3f mulProjectTranslation(Matrix4fc mat) {
        return this.mulPositionTranslation(mat, this);
    }

    public Vector3f mulProjectTranslation(Matrix4fc mat, Vector3f dest) {
        return this.mulPositionTranslation(mat, dest);
    }

    public Vector3f mulProjectTranslation(Matrix4dc mat) {
        return this.mulPositionTranslation(mat, this);
    }

    public Vector3f mulProjectTranslation(Matrix4dc mat, Vector3f dest) {
        return this.mulPositionTranslation(mat, dest);
    }

    public Vector3f mulProjectAffine(Matrix4fc mat) {
        return this.mulProjectAffine(mat, this);
    }

    public Vector3f mulProjectAffine(Matrix4fc mat, Vector3f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        dest.x = Math.fma(mat.m00(), x, Math.fma(mat.m10(), y, mat.m20() * z)) + mat.m30();
        dest.y = Math.fma(mat.m01(), x, Math.fma(mat.m11(), y, mat.m21() * z)) + mat.m31();
        dest.z = Math.fma(mat.m02(), x, Math.fma(mat.m12(), y, mat.m22() * z)) + mat.m32();
        return dest;
    }

    public Vector3f mulProjectAffine(Matrix4dc mat) {
        return this.mulProjectAffine(mat, this);
    }

    public Vector3f mulProjectAffine(Matrix4dc mat, Vector3f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        dest.x = (float)(Math.fma(mat.m00(), (double)x, Math.fma(mat.m10(), (double)y, mat.m20() * (double)z)) + mat.m30());
        dest.y = (float)(Math.fma(mat.m01(), (double)x, Math.fma(mat.m11(), (double)y, mat.m21() * (double)z)) + mat.m31());
        dest.z = (float)(Math.fma(mat.m02(), (double)x, Math.fma(mat.m12(), (double)y, mat.m22() * (double)z)) + mat.m32());
        return dest;
    }

    public Vector3f mulProjectGeneric(Matrix4fc mat) {
        return this.mulProjectGeneric(mat, this);
    }

    public Vector3f mulProjectGeneric(Matrix4fc mat, Vector3f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        float invW = 1.0f / Math.fma(mat.m03(), x, Math.fma(mat.m13(), y, Math.fma(mat.m23(), z, mat.m33())));
        dest.x = Math.fma(mat.m00(), x, Math.fma(mat.m10(), y, Math.fma(mat.m20(), z, mat.m30()))) * invW;
        dest.y = Math.fma(mat.m01(), x, Math.fma(mat.m11(), y, Math.fma(mat.m21(), z, mat.m31()))) * invW;
        dest.z = Math.fma(mat.m02(), x, Math.fma(mat.m12(), y, Math.fma(mat.m22(), z, mat.m32()))) * invW;
        return dest;
    }

    public Vector3f mulProjectGeneric(Matrix4dc mat) {
        return this.mulProjectGeneric(mat, this);
    }

    public Vector3f mulProjectGeneric(Matrix4dc mat, Vector3f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        double invW = 1.0 / Math.fma(mat.m03(), (double)x, Math.fma(mat.m13(), (double)y, Math.fma(mat.m23(), (double)z, mat.m33())));
        dest.x = (float)(Math.fma(mat.m00(), (double)x, Math.fma(mat.m10(), (double)y, Math.fma(mat.m20(), (double)z, mat.m30()))) * invW);
        dest.y = (float)(Math.fma(mat.m01(), (double)x, Math.fma(mat.m11(), (double)y, Math.fma(mat.m21(), (double)z, mat.m31()))) * invW);
        dest.z = (float)(Math.fma(mat.m02(), (double)x, Math.fma(mat.m12(), (double)y, Math.fma(mat.m22(), (double)z, mat.m32()))) * invW);
        return dest;
    }

    public Vector3f mul(Matrix3fc mat) {
        return this.mul(mat, this);
    }

    public Vector3f mul(Matrix3fc mat, Vector3f dest) {
        float lx = this.x;
        float ly = this.y;
        float lz = this.z;
        dest.x = Math.fma(mat.m00(), lx, Math.fma(mat.m10(), ly, mat.m20() * lz));
        dest.y = Math.fma(mat.m01(), lx, Math.fma(mat.m11(), ly, mat.m21() * lz));
        dest.z = Math.fma(mat.m02(), lx, Math.fma(mat.m12(), ly, mat.m22() * lz));
        return dest;
    }

    public Vector3f mul(Matrix3dc mat) {
        return this.mul(mat, this);
    }

    public Vector3f mul(Matrix3dc mat, Vector3f dest) {
        float lx = this.x;
        float ly = this.y;
        float lz = this.z;
        dest.x = (float)Math.fma(mat.m00(), (double)lx, Math.fma(mat.m10(), (double)ly, mat.m20() * (double)lz));
        dest.y = (float)Math.fma(mat.m01(), (double)lx, Math.fma(mat.m11(), (double)ly, mat.m21() * (double)lz));
        dest.z = (float)Math.fma(mat.m02(), (double)lx, Math.fma(mat.m12(), (double)ly, mat.m22() * (double)lz));
        return dest;
    }

    public Vector3f mul(Matrix3x2fc mat) {
        return this.mul(mat, this);
    }

    public Vector3f mul(Matrix3x2fc mat, Vector3f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        dest.x = Math.fma(mat.m00(), x, Math.fma(mat.m10(), y, mat.m20() * z));
        dest.y = Math.fma(mat.m01(), x, Math.fma(mat.m11(), y, mat.m21() * z));
        dest.z = z;
        return dest;
    }

    public Vector3f mul(Matrix3x2dc mat) {
        return this.mul(mat, this);
    }

    public Vector3f mul(Matrix3x2dc mat, Vector3f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        dest.x = (float)Math.fma(mat.m00(), (double)x, Math.fma(mat.m10(), (double)y, mat.m20() * (double)z));
        dest.y = (float)Math.fma(mat.m01(), (double)x, Math.fma(mat.m11(), (double)y, mat.m21() * (double)z));
        dest.z = z;
        return dest;
    }

    public Vector3f mulTranspose(Matrix3fc mat) {
        return this.mulTranspose(mat, this);
    }

    public Vector3f mulTranspose(Matrix3fc mat, Vector3f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        dest.x = Math.fma(mat.m00(), x, Math.fma(mat.m01(), y, mat.m02() * z));
        dest.y = Math.fma(mat.m10(), x, Math.fma(mat.m11(), y, mat.m12() * z));
        dest.z = Math.fma(mat.m20(), x, Math.fma(mat.m21(), y, mat.m22() * z));
        return dest;
    }

    public Vector3f mulPosition(Matrix4fc mat) {
        int prop = mat.properties();
        if ((prop & 4) != 0) {
            return this;
        }
        if ((prop & 8) != 0) {
            return this.mulPositionTranslation(mat, this);
        }
        return this.mulPositionGeneric(mat, this);
    }

    public Vector3f mulPosition(Matrix4dc mat) {
        int prop = mat.properties();
        if ((prop & 4) != 0) {
            return this;
        }
        if ((prop & 8) != 0) {
            return this.mulPositionTranslation(mat, this);
        }
        return this.mulPositionGeneric(mat, this);
    }

    public Vector3f mulPosition(Matrix4fc mat, Vector3f dest) {
        int prop = mat.properties();
        if ((prop & 4) != 0) {
            return dest.set(this);
        }
        if ((prop & 8) != 0) {
            return this.mulPositionTranslation(mat, dest);
        }
        return this.mulPositionGeneric(mat, dest);
    }

    public Vector3f mulPosition(Matrix4dc mat, Vector3f dest) {
        int prop = mat.properties();
        if ((prop & 4) != 0) {
            return dest.set(this);
        }
        if ((prop & 8) != 0) {
            return this.mulPositionTranslation(mat, dest);
        }
        return this.mulPositionGeneric(mat, dest);
    }

    public Vector3f mulPositionTranslation(Matrix4x3fc mat) {
        return this.mulPositionTranslation(mat, this);
    }

    public Vector3f mulPositionTranslation(Matrix4x3fc mat, Vector3f dest) {
        float rx = Math.fma(mat.m00(), this.x, Math.fma(mat.m10(), this.y, Math.fma(mat.m20(), this.z, mat.m30())));
        float ry = Math.fma(mat.m01(), this.x, Math.fma(mat.m11(), this.y, Math.fma(mat.m21(), this.z, mat.m31())));
        float rz = Math.fma(mat.m02(), this.x, Math.fma(mat.m12(), this.y, Math.fma(mat.m22(), this.z, mat.m32())));
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    public Vector3f mulPositionTranslation(Matrix4x3dc mat) {
        return this.mulPositionTranslation(mat, this);
    }

    public Vector3f mulPositionTranslation(Matrix4x3dc mat, Vector3f dest) {
        float rx = (float)Math.fma(mat.m00(), (double)this.x, Math.fma(mat.m10(), (double)this.y, Math.fma(mat.m20(), (double)this.z, mat.m30())));
        float ry = (float)Math.fma(mat.m01(), (double)this.x, Math.fma(mat.m11(), (double)this.y, Math.fma(mat.m21(), (double)this.z, mat.m31())));
        float rz = (float)Math.fma(mat.m02(), (double)this.x, Math.fma(mat.m12(), (double)this.y, Math.fma(mat.m22(), (double)this.z, mat.m32())));
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    public Vector3f mulPositionTranslation(Matrix4fc mat) {
        return this.mulPositionTranslation(mat, this);
    }

    public Vector3f mulPositionTranslation(Matrix4fc mat, Vector3f dest) {
        dest.x = this.x + mat.m30();
        dest.y = this.y + mat.m31();
        dest.z = this.z + mat.m32();
        return dest;
    }

    public Vector3f mulPositionTranslation(Matrix4dc mat) {
        return this.mulPositionTranslation(mat, this);
    }

    public Vector3f mulPositionTranslation(Matrix4dc mat, Vector3f dest) {
        dest.x = (float)((double)this.x + mat.m30());
        dest.y = (float)((double)this.y + mat.m31());
        dest.z = (float)((double)this.z + mat.m32());
        return dest;
    }

    public Vector3f mulPositionGeneric(Matrix4fc mat) {
        return this.mulPositionGeneric(mat, this);
    }

    public Vector3f mulPositionGeneric(Matrix4fc mat, Vector3f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        dest.x = Math.fma(mat.m00(), x, Math.fma(mat.m10(), y, Math.fma(mat.m20(), z, mat.m30())));
        dest.y = Math.fma(mat.m01(), x, Math.fma(mat.m11(), y, Math.fma(mat.m21(), z, mat.m31())));
        dest.z = Math.fma(mat.m02(), x, Math.fma(mat.m12(), y, Math.fma(mat.m22(), z, mat.m32())));
        return dest;
    }

    public Vector3f mulPositionGeneric(Matrix4dc mat) {
        return this.mulPositionGeneric(mat, this);
    }

    public Vector3f mulPositionGeneric(Matrix4dc mat, Vector3f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        dest.x = (float)Math.fma(mat.m00(), (double)x, Math.fma(mat.m10(), (double)y, Math.fma(mat.m20(), (double)z, mat.m30())));
        dest.y = (float)Math.fma(mat.m01(), (double)x, Math.fma(mat.m11(), (double)y, Math.fma(mat.m21(), (double)z, mat.m31())));
        dest.z = (float)Math.fma(mat.m02(), (double)x, Math.fma(mat.m12(), (double)y, Math.fma(mat.m22(), (double)z, mat.m32())));
        return dest;
    }

    public Vector3f mulPosition(Matrix4x3fc mat) {
        int prop = mat.properties();
        if ((prop & 4) != 0) {
            return this;
        }
        if ((prop & 8) != 0) {
            return this.mulPositionTranslation(mat, this);
        }
        return this.mulPositionGeneric(mat);
    }

    public Vector3f mulPosition(Matrix4x3dc mat) {
        int prop = mat.properties();
        if ((prop & 4) != 0) {
            return this;
        }
        if ((prop & 8) != 0) {
            return this.mulPositionTranslation(mat, this);
        }
        return this.mulPositionGeneric(mat);
    }

    public Vector3f mulPosition(Matrix4x3fc mat, Vector3f dest) {
        int prop = mat.properties();
        if ((prop & 4) != 0) {
            return dest.set(this);
        }
        if ((prop & 8) != 0) {
            return this.mulPositionTranslation(mat, dest);
        }
        return this.mulPositionGeneric(mat, dest);
    }

    public Vector3f mulPosition(Matrix4x3dc mat, Vector3f dest) {
        int prop = mat.properties();
        if ((prop & 4) != 0) {
            return dest.set(this);
        }
        if ((prop & 8) != 0) {
            return this.mulPositionTranslation(mat, dest);
        }
        return this.mulPositionGeneric(mat, dest);
    }

    public Vector3f mulPositionGeneric(Matrix4x3fc mat) {
        return this.mulPositionGeneric(mat, this);
    }

    public Vector3f mulPositionGeneric(Matrix4x3dc mat) {
        return this.mulPositionGeneric(mat, this);
    }

    public Vector3f mulPositionGeneric(Matrix4x3fc mat, Vector3f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        dest.x = Math.fma(mat.m00(), x, Math.fma(mat.m10(), y, Math.fma(mat.m20(), z, mat.m30())));
        dest.y = Math.fma(mat.m01(), x, Math.fma(mat.m11(), y, Math.fma(mat.m21(), z, mat.m31())));
        dest.z = Math.fma(mat.m02(), x, Math.fma(mat.m12(), y, Math.fma(mat.m22(), z, mat.m32())));
        return dest;
    }

    public Vector3f mulPositionGeneric(Matrix4x3dc mat, Vector3f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        dest.x = (float)Math.fma(mat.m00(), (double)x, Math.fma(mat.m10(), (double)y, Math.fma(mat.m20(), (double)z, mat.m30())));
        dest.y = (float)Math.fma(mat.m01(), (double)x, Math.fma(mat.m11(), (double)y, Math.fma(mat.m21(), (double)z, mat.m31())));
        dest.z = (float)Math.fma(mat.m02(), (double)x, Math.fma(mat.m12(), (double)y, Math.fma(mat.m22(), (double)z, mat.m32())));
        return dest;
    }

    public Vector3f mulTransposePosition(Matrix4fc mat) {
        return this.mulTransposePosition(mat, this);
    }

    public Vector3f mulTransposePosition(Matrix4fc mat, Vector3f dest) {
        int prop = mat.properties();
        if ((prop & 4) != 0) {
            return dest.set(this);
        }
        return this.mulTransposePositionGeneric(mat, dest);
    }

    public Vector3f mulTransposePositionGeneric(Matrix4fc mat, Vector3f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        dest.x = Math.fma(mat.m00(), x, Math.fma(mat.m01(), y, Math.fma(mat.m02(), z, mat.m03())));
        dest.y = Math.fma(mat.m10(), x, Math.fma(mat.m11(), y, Math.fma(mat.m12(), z, mat.m13())));
        dest.z = Math.fma(mat.m20(), x, Math.fma(mat.m21(), y, Math.fma(mat.m22(), z, mat.m23())));
        return dest;
    }

    public Vector3f mulTransposePosition(Matrix4dc mat) {
        return this.mulTransposePosition(mat, this);
    }

    public Vector3f mulTransposePosition(Matrix4dc mat, Vector3f dest) {
        int properties = mat.properties();
        if ((properties & 4) != 0) {
            return dest.set(this);
        }
        return this.mulTransposePositionGeneric(mat, dest);
    }

    public Vector3f mulTransposePositionGeneric(Matrix4dc mat) {
        return this.mulTransposePositionGeneric(mat, this);
    }

    public Vector3f mulTransposePositionGeneric(Matrix4dc mat, Vector3f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        dest.x = (float)Math.fma(mat.m00(), (double)x, Math.fma(mat.m01(), (double)y, Math.fma(mat.m02(), (double)z, mat.m03())));
        dest.y = (float)Math.fma(mat.m10(), (double)x, Math.fma(mat.m11(), (double)y, Math.fma(mat.m12(), (double)z, mat.m13())));
        dest.z = (float)Math.fma(mat.m20(), (double)x, Math.fma(mat.m21(), (double)y, Math.fma(mat.m22(), (double)z, mat.m23())));
        return dest;
    }

    public float mulPositionW(Matrix4fc mat) {
        return this.mulPositionW(mat, this);
    }

    public float mulPositionW(Matrix4fc mat, Vector3f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        float w = Math.fma(mat.m03(), x, Math.fma(mat.m13(), y, Math.fma(mat.m23(), z, mat.m33())));
        dest.x = Math.fma(mat.m00(), x, Math.fma(mat.m10(), y, Math.fma(mat.m20(), z, mat.m30())));
        dest.y = Math.fma(mat.m01(), x, Math.fma(mat.m11(), y, Math.fma(mat.m21(), z, mat.m31())));
        dest.z = Math.fma(mat.m02(), x, Math.fma(mat.m12(), y, Math.fma(mat.m22(), z, mat.m32())));
        return w;
    }

    public float mulPositionW(Matrix4dc mat) {
        return this.mulPositionW(mat, this);
    }

    public float mulPositionW(Matrix4dc mat, Vector3f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        float w = (float)Math.fma(mat.m03(), (double)x, Math.fma(mat.m13(), (double)y, Math.fma(mat.m23(), (double)z, mat.m33())));
        dest.x = (float)Math.fma(mat.m00(), (double)x, Math.fma(mat.m10(), (double)y, Math.fma(mat.m20(), (double)z, mat.m30())));
        dest.y = (float)Math.fma(mat.m01(), (double)x, Math.fma(mat.m11(), (double)y, Math.fma(mat.m21(), (double)z, mat.m31())));
        dest.z = (float)Math.fma(mat.m02(), (double)x, Math.fma(mat.m12(), (double)y, Math.fma(mat.m22(), (double)z, mat.m32())));
        return w;
    }

    public Vector3f mulDirection(Matrix4dc mat) {
        return this.mulDirection(mat, this);
    }

    public Vector3f mulDirection(Matrix4fc mat) {
        return this.mulDirection(mat, this);
    }

    public Vector3f mulDirection(Matrix4x3fc mat) {
        return this.mulDirection(mat, this);
    }

    public Vector3f mulDirection(Matrix4x3dc mat) {
        return this.mulDirection(mat, this);
    }

    public Vector3f mulDirection(Matrix4dc mat, Vector3f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        dest.x = (float)Math.fma(mat.m00(), (double)x, Math.fma(mat.m10(), (double)y, mat.m20() * (double)z));
        dest.y = (float)Math.fma(mat.m01(), (double)x, Math.fma(mat.m11(), (double)y, mat.m21() * (double)z));
        dest.z = (float)Math.fma(mat.m02(), (double)x, Math.fma(mat.m12(), (double)y, mat.m22() * (double)z));
        return dest;
    }

    public Vector3f mulDirection(Matrix4fc mat, Vector3f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        dest.x = Math.fma(mat.m00(), x, Math.fma(mat.m10(), y, mat.m20() * z));
        dest.y = Math.fma(mat.m01(), x, Math.fma(mat.m11(), y, mat.m21() * z));
        dest.z = Math.fma(mat.m02(), x, Math.fma(mat.m12(), y, mat.m22() * z));
        return dest;
    }

    public Vector3f mulDirection(Matrix4x3fc mat, Vector3f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        dest.x = Math.fma(mat.m00(), x, Math.fma(mat.m10(), y, mat.m20() * z));
        dest.y = Math.fma(mat.m01(), x, Math.fma(mat.m11(), y, mat.m21() * z));
        dest.z = Math.fma(mat.m02(), x, Math.fma(mat.m12(), y, mat.m22() * z));
        return dest;
    }

    public Vector3f mulDirection(Matrix4x3dc mat, Vector3f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        dest.x = (float)Math.fma(mat.m00(), (double)x, Math.fma(mat.m10(), (double)y, mat.m20() * (double)z));
        dest.y = (float)Math.fma(mat.m01(), (double)x, Math.fma(mat.m11(), (double)y, mat.m21() * (double)z));
        dest.z = (float)Math.fma(mat.m02(), (double)x, Math.fma(mat.m12(), (double)y, mat.m22() * (double)z));
        return dest;
    }

    public Vector3f mulTransposeDirection(Matrix4fc mat) {
        return this.mulTransposeDirection(mat, this);
    }

    public Vector3f mulTransposeDirection(Matrix4fc mat, Vector3f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        dest.x = Math.fma(mat.m00(), x, Math.fma(mat.m01(), y, mat.m02() * z));
        dest.y = Math.fma(mat.m10(), x, Math.fma(mat.m11(), y, mat.m12() * z));
        dest.z = Math.fma(mat.m20(), x, Math.fma(mat.m21(), y, mat.m22() * z));
        return dest;
    }

    public Vector3f mul(float scalar) {
        return this.mul(scalar, this);
    }

    public Vector3f mul(float scalar, Vector3f dest) {
        dest.x = this.x * scalar;
        dest.y = this.y * scalar;
        dest.z = this.z * scalar;
        return dest;
    }

    public Vector3f mul(float x, float y, float z) {
        return this.mul(x, y, z, this);
    }

    public Vector3f mul(float x, float y, float z, Vector3f dest) {
        dest.x = this.x * x;
        dest.y = this.y * y;
        dest.z = this.z * z;
        return dest;
    }

    public Vector3f div(float scalar) {
        return this.div(scalar, this);
    }

    public Vector3f div(float scalar, Vector3f dest) {
        float inv = 1.0f / scalar;
        dest.x = this.x * inv;
        dest.y = this.y * inv;
        dest.z = this.z * inv;
        return dest;
    }

    public Vector3f div(float x, float y, float z) {
        return this.div(x, y, z, this);
    }

    public Vector3f div(float x, float y, float z, Vector3f dest) {
        dest.x = this.x / x;
        dest.y = this.y / y;
        dest.z = this.z / z;
        return dest;
    }

    public Vector3f rotate(Quaternionfc quat) {
        return quat.transform(this, this);
    }

    public Vector3f rotate(Quaternionfc quat, Vector3f dest) {
        return quat.transform(this, dest);
    }

    public Quaternionf rotationTo(Vector3fc toDir, Quaternionf dest) {
        return dest.rotationTo(this, toDir);
    }

    public Quaternionf rotationTo(float toDirX, float toDirY, float toDirZ, Quaternionf dest) {
        return dest.rotationTo(this.x, this.y, this.z, toDirX, toDirY, toDirZ);
    }

    public Vector3f rotateAxis(float angle, float x, float y, float z) {
        return this.rotateAxis(angle, x, y, z, this);
    }

    public Vector3f rotateAxis(float angle, float aX, float aY, float aZ, Vector3f dest) {
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

    private Vector3f rotateAxisInternal(float angle, float aX, float aY, float aZ, Vector3f dest) {
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

    public Vector3f rotateX(float angle) {
        return this.rotateX(angle, this);
    }

    public Vector3f rotateX(float angle, Vector3f dest) {
        float sin = Math.sin(angle);
        float cos = Math.cosFromSin(sin, angle);
        float y = this.y * cos - this.z * sin;
        float z = this.y * sin + this.z * cos;
        dest.x = this.x;
        dest.y = y;
        dest.z = z;
        return dest;
    }

    public Vector3f rotateY(float angle) {
        return this.rotateY(angle, this);
    }

    public Vector3f rotateY(float angle, Vector3f dest) {
        float sin = Math.sin(angle);
        float cos = Math.cosFromSin(sin, angle);
        float x = this.x * cos + this.z * sin;
        float z = -this.x * sin + this.z * cos;
        dest.x = x;
        dest.y = this.y;
        dest.z = z;
        return dest;
    }

    public Vector3f rotateZ(float angle) {
        return this.rotateZ(angle, this);
    }

    public Vector3f rotateZ(float angle, Vector3f dest) {
        float sin = Math.sin(angle);
        float cos = Math.cosFromSin(sin, angle);
        float x = this.x * cos - this.y * sin;
        float y = this.x * sin + this.y * cos;
        dest.x = x;
        dest.y = y;
        dest.z = this.z;
        return dest;
    }

    public float lengthSquared() {
        return Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z));
    }

    public static float lengthSquared(float x, float y, float z) {
        return Math.fma(x, x, Math.fma(y, y, z * z));
    }

    public float length() {
        return Math.sqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z)));
    }

    public static float length(float x, float y, float z) {
        return Math.sqrt(Math.fma(x, x, Math.fma(y, y, z * z)));
    }

    public Vector3f normalize() {
        return this.normalize(this);
    }

    public Vector3f normalize(Vector3f dest) {
        float scalar = Math.invsqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z)));
        dest.x = this.x * scalar;
        dest.y = this.y * scalar;
        dest.z = this.z * scalar;
        return dest;
    }

    public Vector3f normalize(float length) {
        return this.normalize(length, this);
    }

    public Vector3f normalize(float length, Vector3f dest) {
        float scalar = Math.invsqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z))) * length;
        dest.x = this.x * scalar;
        dest.y = this.y * scalar;
        dest.z = this.z * scalar;
        return dest;
    }

    public Vector3f cross(Vector3fc v) {
        return this.cross(v, this);
    }

    public Vector3f cross(float x, float y, float z) {
        return this.cross(x, y, z, this);
    }

    public Vector3f cross(Vector3fc v, Vector3f dest) {
        float rx = Math.fma(this.y, v.z(), -this.z * v.y());
        float ry = Math.fma(this.z, v.x(), -this.x * v.z());
        float rz = Math.fma(this.x, v.y(), -this.y * v.x());
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    public Vector3f cross(float x, float y, float z, Vector3f dest) {
        float rx = Math.fma(this.y, z, -this.z * y);
        float ry = Math.fma(this.z, x, -this.x * z);
        float rz = Math.fma(this.x, y, -this.y * x);
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    public float distance(Vector3fc v) {
        float dx = this.x - v.x();
        float dy = this.y - v.y();
        float dz = this.z - v.z();
        return Math.sqrt(Math.fma(dx, dx, Math.fma(dy, dy, dz * dz)));
    }

    public float distance(float x, float y, float z) {
        float dx = this.x - x;
        float dy = this.y - y;
        float dz = this.z - z;
        return Math.sqrt(Math.fma(dx, dx, Math.fma(dy, dy, dz * dz)));
    }

    public float distanceSquared(Vector3fc v) {
        float dx = this.x - v.x();
        float dy = this.y - v.y();
        float dz = this.z - v.z();
        return Math.fma(dx, dx, Math.fma(dy, dy, dz * dz));
    }

    public float distanceSquared(float x, float y, float z) {
        float dx = this.x - x;
        float dy = this.y - y;
        float dz = this.z - z;
        return Math.fma(dx, dx, Math.fma(dy, dy, dz * dz));
    }

    public static float distance(float x1, float y1, float z1, float x2, float y2, float z2) {
        return Math.sqrt(Vector3f.distanceSquared(x1, y1, z1, x2, y2, z2));
    }

    public static float distanceSquared(float x1, float y1, float z1, float x2, float y2, float z2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        float dz = z1 - z2;
        return Math.fma(dx, dx, Math.fma(dy, dy, dz * dz));
    }

    public float dot(Vector3fc v) {
        return Math.fma(this.x, v.x(), Math.fma(this.y, v.y(), this.z * v.z()));
    }

    public float dot(float x, float y, float z) {
        return Math.fma(this.x, x, Math.fma(this.y, y, this.z * z));
    }

    public float angleCos(Vector3fc v) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        float length1Squared = Math.fma(x, x, Math.fma(y, y, z * z));
        float length2Squared = Math.fma(v.x(), v.x(), Math.fma(v.y(), v.y(), v.z() * v.z()));
        float dot = Math.fma(x, v.x(), Math.fma(y, v.y(), z * v.z()));
        return dot / Math.sqrt(length1Squared * length2Squared);
    }

    public float angle(Vector3fc v) {
        float cos = this.angleCos(v);
        cos = cos < 1.0f ? cos : 1.0f;
        cos = cos > -1.0f ? cos : -1.0f;
        return Math.acos(cos);
    }

    public float angleSigned(Vector3fc v, Vector3fc n) {
        return this.angleSigned(v.x(), v.y(), v.z(), n.x(), n.y(), n.z());
    }

    public float angleSigned(float x, float y, float z, float nx, float ny, float nz) {
        float tx = this.x;
        float ty = this.y;
        float tz = this.z;
        return Math.atan2((ty * z - tz * y) * nx + (tz * x - tx * z) * ny + (tx * y - ty * x) * nz, tx * x + ty * y + tz * z);
    }

    public Vector3f min(Vector3fc v) {
        return this.min(v, this);
    }

    public Vector3f min(Vector3fc v, Vector3f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        dest.x = x < v.x() ? x : v.x();
        dest.y = y < v.y() ? y : v.y();
        dest.z = z < v.z() ? z : v.z();
        return dest;
    }

    public Vector3f max(Vector3fc v) {
        return this.max(v, this);
    }

    public Vector3f max(Vector3fc v, Vector3f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        dest.x = x > v.x() ? x : v.x();
        dest.y = y > v.y() ? y : v.y();
        dest.z = z > v.z() ? z : v.z();
        return dest;
    }

    public Vector3f zero() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        return this;
    }

    public String toString() {
        return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
    }

    public String toString(NumberFormat formatter) {
        return "(" + Runtime.format(this.x, formatter) + " " + Runtime.format(this.y, formatter) + " " + Runtime.format(this.z, formatter) + ")";
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeFloat(this.x);
        out.writeFloat(this.y);
        out.writeFloat(this.z);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.set(in.readFloat(), in.readFloat(), in.readFloat());
    }

    public Vector3f negate() {
        return this.negate(this);
    }

    public Vector3f negate(Vector3f dest) {
        dest.x = -this.x;
        dest.y = -this.y;
        dest.z = -this.z;
        return dest;
    }

    public Vector3f absolute() {
        return this.absolute(this);
    }

    public Vector3f absolute(Vector3f dest) {
        dest.x = Math.abs(this.x);
        dest.y = Math.abs(this.y);
        dest.z = Math.abs(this.z);
        return dest;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
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
        Vector3f other = (Vector3f)obj;
        if (Float.floatToIntBits(this.x) != Float.floatToIntBits(other.x)) {
            return false;
        }
        if (Float.floatToIntBits(this.y) != Float.floatToIntBits(other.y)) {
            return false;
        }
        return Float.floatToIntBits(this.z) == Float.floatToIntBits(other.z);
    }

    public boolean equals(Vector3fc v, float delta) {
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

    public boolean equals(float x, float y, float z) {
        if (Float.floatToIntBits(this.x) != Float.floatToIntBits(x)) {
            return false;
        }
        if (Float.floatToIntBits(this.y) != Float.floatToIntBits(y)) {
            return false;
        }
        return Float.floatToIntBits(this.z) == Float.floatToIntBits(z);
    }

    public Vector3f reflect(Vector3fc normal) {
        float x = normal.x();
        float y = normal.y();
        float z = normal.z();
        float dot = Math.fma(this.x, x, Math.fma(this.y, y, this.z * z));
        this.x -= (dot + dot) * x;
        this.y -= (dot + dot) * y;
        this.z -= (dot + dot) * z;
        return this;
    }

    public Vector3f reflect(float x, float y, float z) {
        float dot = Math.fma(this.x, x, Math.fma(this.y, y, this.z * z));
        this.x -= (dot + dot) * x;
        this.y -= (dot + dot) * y;
        this.z -= (dot + dot) * z;
        return this;
    }

    public Vector3f reflect(Vector3fc normal, Vector3f dest) {
        return this.reflect(normal.x(), normal.y(), normal.z(), dest);
    }

    public Vector3f reflect(float x, float y, float z, Vector3f dest) {
        float dot = this.dot(x, y, z);
        dest.x = this.x - (dot + dot) * x;
        dest.y = this.y - (dot + dot) * y;
        dest.z = this.z - (dot + dot) * z;
        return dest;
    }

    public Vector3f half(Vector3fc other) {
        return this.set(this).add(other.x(), other.y(), other.z()).normalize();
    }

    public Vector3f half(float x, float y, float z) {
        return this.half(x, y, z, this);
    }

    public Vector3f half(Vector3fc other, Vector3f dest) {
        return this.half(other.x(), other.y(), other.z(), dest);
    }

    public Vector3f half(float x, float y, float z, Vector3f dest) {
        return dest.set(this).add(x, y, z).normalize();
    }

    public Vector3f smoothStep(Vector3fc v, float t, Vector3f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        float t2 = t * t;
        float t3 = t2 * t;
        dest.x = (x + x - v.x() - v.x()) * t3 + (3.0f * v.x() - 3.0f * x) * t2 + x * t + x;
        dest.y = (y + y - v.y() - v.y()) * t3 + (3.0f * v.y() - 3.0f * y) * t2 + y * t + y;
        dest.z = (z + z - v.z() - v.z()) * t3 + (3.0f * v.z() - 3.0f * z) * t2 + z * t + z;
        return dest;
    }

    public Vector3f hermite(Vector3fc t0, Vector3fc v1, Vector3fc t1, float t, Vector3f dest) {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        float t2 = t * t;
        float t3 = t2 * t;
        dest.x = (x + x - v1.x() - v1.x() + t1.x() + t0.x()) * t3 + (3.0f * v1.x() - 3.0f * x - t0.x() - t0.x() - t1.x()) * t2 + x * t + x;
        dest.y = (y + y - v1.y() - v1.y() + t1.y() + t0.y()) * t3 + (3.0f * v1.y() - 3.0f * y - t0.y() - t0.y() - t1.y()) * t2 + y * t + y;
        dest.z = (z + z - v1.z() - v1.z() + t1.z() + t0.z()) * t3 + (3.0f * v1.z() - 3.0f * z - t0.z() - t0.z() - t1.z()) * t2 + z * t + z;
        return dest;
    }

    public Vector3f lerp(Vector3fc other, float t) {
        return this.lerp(other, t, this);
    }

    public Vector3f lerp(Vector3fc other, float t, Vector3f dest) {
        dest.x = Math.fma(other.x() - this.x, t, this.x);
        dest.y = Math.fma(other.y() - this.y, t, this.y);
        dest.z = Math.fma(other.z() - this.z, t, this.z);
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
        dest.x = this.x();
        dest.y = this.y();
        dest.z = this.z();
        return dest;
    }

    public Vector3d get(Vector3d dest) {
        dest.x = this.x();
        dest.y = this.y();
        dest.z = this.z();
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

    public Vector3f orthogonalize(Vector3fc v, Vector3f dest) {
        float rz;
        float ry;
        float rx;
        if (Math.abs(v.x()) > Math.abs(v.z())) {
            rx = -v.y();
            ry = v.x();
            rz = 0.0f;
        } else {
            rx = 0.0f;
            ry = -v.z();
            rz = v.y();
        }
        float invLen = Math.invsqrt(rx * rx + ry * ry + rz * rz);
        dest.x = rx * invLen;
        dest.y = ry * invLen;
        dest.z = rz * invLen;
        return dest;
    }

    public Vector3f orthogonalize(Vector3fc v) {
        return this.orthogonalize(v, this);
    }

    public Vector3f orthogonalizeUnit(Vector3fc v, Vector3f dest) {
        return this.orthogonalize(v, dest);
    }

    public Vector3f orthogonalizeUnit(Vector3fc v) {
        return this.orthogonalizeUnit(v, this);
    }

    public Vector3f floor() {
        return this.floor(this);
    }

    public Vector3f floor(Vector3f dest) {
        dest.x = Math.floor(this.x);
        dest.y = Math.floor(this.y);
        dest.z = Math.floor(this.z);
        return dest;
    }

    public Vector3f ceil() {
        return this.ceil(this);
    }

    public Vector3f ceil(Vector3f dest) {
        dest.x = Math.ceil(this.x);
        dest.y = Math.ceil(this.y);
        dest.z = Math.ceil(this.z);
        return dest;
    }

    public Vector3f round() {
        return this.round(this);
    }

    public Vector3f round(Vector3f dest) {
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

