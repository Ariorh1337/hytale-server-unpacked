/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.NumberFormat;
import org.joml.AxisAngle4d;
import org.joml.Math;
import org.joml.Matrix3d;
import org.joml.Matrix3dc;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4d;
import org.joml.Matrix4dc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Matrix4x3fc;
import org.joml.Options;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Runtime;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

public class AxisAngle4f
implements Externalizable,
Cloneable {
    private static final long serialVersionUID = 1L;
    public float angle;
    public float x;
    public float y;
    public float z;

    public AxisAngle4f() {
        this.z = 1.0f;
    }

    public AxisAngle4f(AxisAngle4f a) {
        this.x = a.x;
        this.y = a.y;
        this.z = a.z;
        this.angle = ((double)a.angle < 0.0 ? (float)java.lang.Math.PI * 2 + a.angle % ((float)java.lang.Math.PI * 2) : a.angle) % ((float)java.lang.Math.PI * 2);
    }

    public AxisAngle4f(Quaternionfc q) {
        float acos = Math.safeAcos(q.w());
        float invSqrt = Math.invsqrt(1.0f - q.w() * q.w());
        if (Float.isInfinite(invSqrt)) {
            this.x = 0.0f;
            this.y = 0.0f;
            this.z = 1.0f;
        } else {
            this.x = q.x() * invSqrt;
            this.y = q.y() * invSqrt;
            this.z = q.z() * invSqrt;
        }
        this.angle = acos + acos;
    }

    public AxisAngle4f(float angle, float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.angle = ((double)angle < 0.0 ? (float)java.lang.Math.PI * 2 + angle % ((float)java.lang.Math.PI * 2) : angle) % ((float)java.lang.Math.PI * 2);
    }

    public AxisAngle4f(float angle, Vector3fc v) {
        this(angle, v.x(), v.y(), v.z());
    }

    public AxisAngle4f set(AxisAngle4f a) {
        this.x = a.x;
        this.y = a.y;
        this.z = a.z;
        this.angle = a.angle;
        this.angle = ((double)this.angle < 0.0 ? (float)java.lang.Math.PI * 2 + this.angle % ((float)java.lang.Math.PI * 2) : this.angle) % ((float)java.lang.Math.PI * 2);
        return this;
    }

    public AxisAngle4f set(AxisAngle4d a) {
        this.x = (float)a.x;
        this.y = (float)a.y;
        this.z = (float)a.z;
        this.angle = (float)a.angle;
        this.angle = ((double)this.angle < 0.0 ? (float)java.lang.Math.PI * 2 + this.angle % ((float)java.lang.Math.PI * 2) : this.angle) % ((float)java.lang.Math.PI * 2);
        return this;
    }

    public AxisAngle4f set(float angle, float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.angle = ((double)angle < 0.0 ? (float)java.lang.Math.PI * 2 + angle % ((float)java.lang.Math.PI * 2) : angle) % ((float)java.lang.Math.PI * 2);
        return this;
    }

    public AxisAngle4f set(float angle, Vector3fc v) {
        return this.set(angle, v.x(), v.y(), v.z());
    }

    public AxisAngle4f set(Quaternionfc q) {
        float acos = Math.safeAcos(q.w());
        float invSqrt = Math.invsqrt(1.0f - q.w() * q.w());
        if (Float.isInfinite(invSqrt)) {
            this.x = 0.0f;
            this.y = 0.0f;
            this.z = 1.0f;
        } else {
            this.x = q.x() * invSqrt;
            this.y = q.y() * invSqrt;
            this.z = q.z() * invSqrt;
        }
        this.angle = acos + acos;
        return this;
    }

    public AxisAngle4f set(Quaterniondc q) {
        double acos = Math.safeAcos(q.w());
        double invSqrt = Math.invsqrt(1.0 - q.w() * q.w());
        if (Double.isInfinite(invSqrt)) {
            this.x = 0.0f;
            this.y = 0.0f;
            this.z = 1.0f;
        } else {
            this.x = (float)(q.x() * invSqrt);
            this.y = (float)(q.y() * invSqrt);
            this.z = (float)(q.z() * invSqrt);
        }
        this.angle = (float)(acos + acos);
        return this;
    }

    public AxisAngle4f set(Matrix3fc m) {
        float nm00 = m.m00();
        float nm01 = m.m01();
        float nm02 = m.m02();
        float nm10 = m.m10();
        float nm11 = m.m11();
        float nm12 = m.m12();
        float nm20 = m.m20();
        float nm21 = m.m21();
        float nm22 = m.m22();
        float lenX = Math.invsqrt(m.m00() * m.m00() + m.m01() * m.m01() + m.m02() * m.m02());
        float lenY = Math.invsqrt(m.m10() * m.m10() + m.m11() * m.m11() + m.m12() * m.m12());
        float lenZ = Math.invsqrt(m.m20() * m.m20() + m.m21() * m.m21() + m.m22() * m.m22());
        nm00 *= lenX;
        nm01 *= lenX;
        nm02 *= lenX;
        nm10 *= lenY;
        nm11 *= lenY;
        nm12 *= lenY;
        nm20 *= lenZ;
        nm21 *= lenZ;
        nm22 *= lenZ;
        float epsilon = 1.0E-4f;
        float epsilon2 = 0.001f;
        if (Math.abs(nm10 - nm01) < epsilon && Math.abs(nm20 - nm02) < epsilon && Math.abs(nm21 - nm12) < epsilon) {
            if (Math.abs(nm10 + nm01) < epsilon2 && Math.abs(nm20 + nm02) < epsilon2 && Math.abs(nm21 + nm12) < epsilon2 && Math.abs(nm00 + nm11 + nm22 - 3.0f) < epsilon2) {
                this.x = 0.0f;
                this.y = 0.0f;
                this.z = 1.0f;
                this.angle = 0.0f;
                return this;
            }
            this.angle = (float)java.lang.Math.PI;
            float xx = (nm00 + 1.0f) / 2.0f;
            float yy = (nm11 + 1.0f) / 2.0f;
            float zz = (nm22 + 1.0f) / 2.0f;
            float xy = (nm10 + nm01) / 4.0f;
            float xz = (nm20 + nm02) / 4.0f;
            float yz = (nm21 + nm12) / 4.0f;
            if (xx > yy && xx > zz) {
                this.x = Math.sqrt(xx);
                float invX = 1.0f / this.x;
                this.y = xy * invX;
                this.z = xz * invX;
            } else if (yy > zz) {
                this.y = Math.sqrt(yy);
                float invZ = 1.0f / this.z;
                this.x = xy * invZ;
                this.z = yz * invZ;
            } else {
                this.z = Math.sqrt(zz);
                float invY = 1.0f / this.y;
                this.x = xz * invY;
                this.y = yz * invY;
            }
            return this;
        }
        float s = Math.invsqrt((nm12 - nm21) * (nm12 - nm21) + (nm20 - nm02) * (nm20 - nm02) + (nm01 - nm10) * (nm01 - nm10));
        this.angle = Math.safeAcos((nm00 + nm11 + nm22 - 1.0f) / 2.0f);
        this.x = (nm12 - nm21) * s;
        this.y = (nm20 - nm02) * s;
        this.z = (nm01 - nm10) * s;
        return this;
    }

    public AxisAngle4f set(Matrix3dc m) {
        double nm00 = m.m00();
        double nm01 = m.m01();
        double nm02 = m.m02();
        double nm10 = m.m10();
        double nm11 = m.m11();
        double nm12 = m.m12();
        double nm20 = m.m20();
        double nm21 = m.m21();
        double nm22 = m.m22();
        double lenX = Math.invsqrt(m.m00() * m.m00() + m.m01() * m.m01() + m.m02() * m.m02());
        double lenY = Math.invsqrt(m.m10() * m.m10() + m.m11() * m.m11() + m.m12() * m.m12());
        double lenZ = Math.invsqrt(m.m20() * m.m20() + m.m21() * m.m21() + m.m22() * m.m22());
        nm00 *= lenX;
        nm01 *= lenX;
        nm02 *= lenX;
        nm10 *= lenY;
        nm11 *= lenY;
        nm12 *= lenY;
        nm20 *= lenZ;
        nm21 *= lenZ;
        nm22 *= lenZ;
        double epsilon = 1.0E-4;
        double epsilon2 = 0.001;
        if (Math.abs(nm10 - nm01) < epsilon && Math.abs(nm20 - nm02) < epsilon && Math.abs(nm21 - nm12) < epsilon) {
            if (Math.abs(nm10 + nm01) < epsilon2 && Math.abs(nm20 + nm02) < epsilon2 && Math.abs(nm21 + nm12) < epsilon2 && Math.abs(nm00 + nm11 + nm22 - 3.0) < epsilon2) {
                this.x = 0.0f;
                this.y = 0.0f;
                this.z = 1.0f;
                this.angle = 0.0f;
                return this;
            }
            this.angle = (float)java.lang.Math.PI;
            double xx = (nm00 + 1.0) / 2.0;
            double yy = (nm11 + 1.0) / 2.0;
            double zz = (nm22 + 1.0) / 2.0;
            double xy = (nm10 + nm01) / 4.0;
            double xz = (nm20 + nm02) / 4.0;
            double yz = (nm21 + nm12) / 4.0;
            if (xx > yy && xx > zz) {
                this.x = (float)Math.sqrt(xx);
                float invX = 1.0f / this.x;
                this.y = (float)(xy * (double)invX);
                this.z = (float)(xz * (double)invX);
            } else if (yy > zz) {
                this.y = (float)Math.sqrt(yy);
                float invY = 1.0f / this.y;
                this.x = (float)(xy * (double)invY);
                this.z = (float)(yz * (double)invY);
            } else {
                this.z = (float)Math.sqrt(zz);
                float invZ = 1.0f / this.z;
                this.x = (float)(xz * (double)invZ);
                this.y = (float)(yz * (double)invZ);
            }
            return this;
        }
        double s = Math.invsqrt((nm12 - nm21) * (nm12 - nm21) + (nm20 - nm02) * (nm20 - nm02) + (nm01 - nm10) * (nm01 - nm10));
        this.angle = (float)Math.safeAcos((nm00 + nm11 + nm22 - 1.0) / 2.0);
        this.x = (float)((nm12 - nm21) * s);
        this.y = (float)((nm20 - nm02) * s);
        this.z = (float)((nm01 - nm10) * s);
        return this;
    }

    public AxisAngle4f set(Matrix4fc m) {
        float nm00 = m.m00();
        float nm01 = m.m01();
        float nm02 = m.m02();
        float nm10 = m.m10();
        float nm11 = m.m11();
        float nm12 = m.m12();
        float nm20 = m.m20();
        float nm21 = m.m21();
        float nm22 = m.m22();
        float lenX = Math.invsqrt(m.m00() * m.m00() + m.m01() * m.m01() + m.m02() * m.m02());
        float lenY = Math.invsqrt(m.m10() * m.m10() + m.m11() * m.m11() + m.m12() * m.m12());
        float lenZ = Math.invsqrt(m.m20() * m.m20() + m.m21() * m.m21() + m.m22() * m.m22());
        nm00 *= lenX;
        nm01 *= lenX;
        nm02 *= lenX;
        nm10 *= lenY;
        nm11 *= lenY;
        nm12 *= lenY;
        nm20 *= lenZ;
        nm21 *= lenZ;
        nm22 *= lenZ;
        float epsilon = 1.0E-4f;
        float epsilon2 = 0.001f;
        if (Math.abs(nm10 - nm01) < epsilon && Math.abs(nm20 - nm02) < epsilon && Math.abs(nm21 - nm12) < epsilon) {
            if (Math.abs(nm10 + nm01) < epsilon2 && Math.abs(nm20 + nm02) < epsilon2 && Math.abs(nm21 + nm12) < epsilon2 && Math.abs(nm00 + nm11 + nm22 - 3.0f) < epsilon2) {
                this.x = 0.0f;
                this.y = 0.0f;
                this.z = 1.0f;
                this.angle = 0.0f;
                return this;
            }
            this.angle = (float)java.lang.Math.PI;
            float xx = (nm00 + 1.0f) / 2.0f;
            float yy = (nm11 + 1.0f) / 2.0f;
            float zz = (nm22 + 1.0f) / 2.0f;
            float xy = (nm10 + nm01) / 4.0f;
            float xz = (nm20 + nm02) / 4.0f;
            float yz = (nm21 + nm12) / 4.0f;
            if (xx > yy && xx > zz) {
                this.x = Math.sqrt(xx);
                float invX = 1.0f / this.x;
                this.y = xy * invX;
                this.z = xz * invX;
            } else if (yy > zz) {
                this.y = Math.sqrt(yy);
                float invZ = 1.0f / this.z;
                this.x = xy * invZ;
                this.z = yz * invZ;
            } else {
                this.z = Math.sqrt(zz);
                float invY = 1.0f / this.y;
                this.x = xz * invY;
                this.y = yz * invY;
            }
            return this;
        }
        float s = Math.invsqrt((nm12 - nm21) * (nm12 - nm21) + (nm20 - nm02) * (nm20 - nm02) + (nm01 - nm10) * (nm01 - nm10));
        this.angle = Math.safeAcos((nm00 + nm11 + nm22 - 1.0f) / 2.0f);
        this.x = (nm12 - nm21) * s;
        this.y = (nm20 - nm02) * s;
        this.z = (nm01 - nm10) * s;
        return this;
    }

    public AxisAngle4f set(Matrix4x3fc m) {
        float nm00 = m.m00();
        float nm01 = m.m01();
        float nm02 = m.m02();
        float nm10 = m.m10();
        float nm11 = m.m11();
        float nm12 = m.m12();
        float nm20 = m.m20();
        float nm21 = m.m21();
        float nm22 = m.m22();
        float lenX = Math.invsqrt(m.m00() * m.m00() + m.m01() * m.m01() + m.m02() * m.m02());
        float lenY = Math.invsqrt(m.m10() * m.m10() + m.m11() * m.m11() + m.m12() * m.m12());
        float lenZ = Math.invsqrt(m.m20() * m.m20() + m.m21() * m.m21() + m.m22() * m.m22());
        nm00 *= lenX;
        nm01 *= lenX;
        nm02 *= lenX;
        nm10 *= lenY;
        nm11 *= lenY;
        nm12 *= lenY;
        nm20 *= lenZ;
        nm21 *= lenZ;
        nm22 *= lenZ;
        float epsilon = 1.0E-4f;
        float epsilon2 = 0.001f;
        if (Math.abs(nm10 - nm01) < epsilon && Math.abs(nm20 - nm02) < epsilon && Math.abs(nm21 - nm12) < epsilon) {
            if (Math.abs(nm10 + nm01) < epsilon2 && Math.abs(nm20 + nm02) < epsilon2 && Math.abs(nm21 + nm12) < epsilon2 && Math.abs(nm00 + nm11 + nm22 - 3.0f) < epsilon2) {
                this.x = 0.0f;
                this.y = 0.0f;
                this.z = 1.0f;
                this.angle = 0.0f;
                return this;
            }
            this.angle = (float)java.lang.Math.PI;
            float xx = (nm00 + 1.0f) / 2.0f;
            float yy = (nm11 + 1.0f) / 2.0f;
            float zz = (nm22 + 1.0f) / 2.0f;
            float xy = (nm10 + nm01) / 4.0f;
            float xz = (nm20 + nm02) / 4.0f;
            float yz = (nm21 + nm12) / 4.0f;
            if (xx > yy && xx > zz) {
                this.x = Math.sqrt(xx);
                float invX = 1.0f / this.x;
                this.y = xy * invX;
                this.z = xz * invX;
            } else if (yy > zz) {
                this.y = Math.sqrt(yy);
                float invZ = 1.0f / this.z;
                this.x = xy * invZ;
                this.z = yz * invZ;
            } else {
                this.z = Math.sqrt(zz);
                float invY = 1.0f / this.y;
                this.x = xz * invY;
                this.y = yz * invY;
            }
            return this;
        }
        float s = Math.invsqrt((nm12 - nm21) * (nm12 - nm21) + (nm20 - nm02) * (nm20 - nm02) + (nm01 - nm10) * (nm01 - nm10));
        this.angle = Math.safeAcos((nm00 + nm11 + nm22 - 1.0f) / 2.0f);
        this.x = (nm12 - nm21) * s;
        this.y = (nm20 - nm02) * s;
        this.z = (nm01 - nm10) * s;
        return this;
    }

    public AxisAngle4f set(Matrix4dc m) {
        double nm00 = m.m00();
        double nm01 = m.m01();
        double nm02 = m.m02();
        double nm10 = m.m10();
        double nm11 = m.m11();
        double nm12 = m.m12();
        double nm20 = m.m20();
        double nm21 = m.m21();
        double nm22 = m.m22();
        double lenX = Math.invsqrt(m.m00() * m.m00() + m.m01() * m.m01() + m.m02() * m.m02());
        double lenY = Math.invsqrt(m.m10() * m.m10() + m.m11() * m.m11() + m.m12() * m.m12());
        double lenZ = Math.invsqrt(m.m20() * m.m20() + m.m21() * m.m21() + m.m22() * m.m22());
        nm00 *= lenX;
        nm01 *= lenX;
        nm02 *= lenX;
        nm10 *= lenY;
        nm11 *= lenY;
        nm12 *= lenY;
        nm20 *= lenZ;
        nm21 *= lenZ;
        nm22 *= lenZ;
        double epsilon = 1.0E-4;
        double epsilon2 = 0.001;
        if (Math.abs(nm10 - nm01) < epsilon && Math.abs(nm20 - nm02) < epsilon && Math.abs(nm21 - nm12) < epsilon) {
            if (Math.abs(nm10 + nm01) < epsilon2 && Math.abs(nm20 + nm02) < epsilon2 && Math.abs(nm21 + nm12) < epsilon2 && Math.abs(nm00 + nm11 + nm22 - 3.0) < epsilon2) {
                this.x = 0.0f;
                this.y = 0.0f;
                this.z = 1.0f;
                this.angle = 0.0f;
                return this;
            }
            this.angle = (float)java.lang.Math.PI;
            double xx = (nm00 + 1.0) / 2.0;
            double yy = (nm11 + 1.0) / 2.0;
            double zz = (nm22 + 1.0) / 2.0;
            double xy = (nm10 + nm01) / 4.0;
            double xz = (nm20 + nm02) / 4.0;
            double yz = (nm21 + nm12) / 4.0;
            if (xx > yy && xx > zz) {
                this.x = (float)Math.sqrt(xx);
                float invX = 1.0f / this.x;
                this.y = (float)(xy * (double)invX);
                this.z = (float)(xz * (double)invX);
            } else if (yy > zz) {
                this.y = (float)Math.sqrt(yy);
                float invY = 1.0f / this.y;
                this.x = (float)(xy * (double)invY);
                this.z = (float)(yz * (double)invY);
            } else {
                this.z = (float)Math.sqrt(zz);
                float invZ = 1.0f / this.z;
                this.x = (float)(xz * (double)invZ);
                this.y = (float)(yz * (double)invZ);
            }
            return this;
        }
        double s = Math.invsqrt((nm12 - nm21) * (nm12 - nm21) + (nm20 - nm02) * (nm20 - nm02) + (nm01 - nm10) * (nm01 - nm10));
        this.angle = (float)Math.safeAcos((nm00 + nm11 + nm22 - 1.0) / 2.0);
        this.x = (float)((nm12 - nm21) * s);
        this.y = (float)((nm20 - nm02) * s);
        this.z = (float)((nm01 - nm10) * s);
        return this;
    }

    public Quaternionf get(Quaternionf q) {
        return q.set(this);
    }

    public Quaterniond get(Quaterniond q) {
        return q.set(this);
    }

    public Matrix4f get(Matrix4f m) {
        return m.set(this);
    }

    public Matrix3f get(Matrix3f m) {
        return m.set(this);
    }

    public Matrix4d get(Matrix4d m) {
        return m.set(this);
    }

    public Matrix3d get(Matrix3d m) {
        return m.set(this);
    }

    public AxisAngle4d get(AxisAngle4d dest) {
        return dest.set(this);
    }

    public AxisAngle4f get(AxisAngle4f dest) {
        return dest.set(this);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeFloat(this.angle);
        out.writeFloat(this.x);
        out.writeFloat(this.y);
        out.writeFloat(this.z);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.angle = in.readFloat();
        this.x = in.readFloat();
        this.y = in.readFloat();
        this.z = in.readFloat();
    }

    public AxisAngle4f normalize() {
        float invLength = Math.invsqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        this.x *= invLength;
        this.y *= invLength;
        this.z *= invLength;
        return this;
    }

    public AxisAngle4f rotate(float ang) {
        this.angle += ang;
        this.angle = ((double)this.angle < 0.0 ? (float)java.lang.Math.PI * 2 + this.angle % ((float)java.lang.Math.PI * 2) : this.angle) % ((float)java.lang.Math.PI * 2);
        return this;
    }

    public Vector3f transform(Vector3f v) {
        return this.transform(v, v);
    }

    public Vector3f transform(Vector3fc v, Vector3f dest) {
        double sin = Math.sin(this.angle);
        double cos = Math.cosFromSin(sin, (double)this.angle);
        float dot = this.x * v.x() + this.y * v.y() + this.z * v.z();
        dest.set((float)((double)v.x() * cos + sin * (double)(this.y * v.z() - this.z * v.y()) + (1.0 - cos) * (double)dot * (double)this.x), (float)((double)v.y() * cos + sin * (double)(this.z * v.x() - this.x * v.z()) + (1.0 - cos) * (double)dot * (double)this.y), (float)((double)v.z() * cos + sin * (double)(this.x * v.y() - this.y * v.x()) + (1.0 - cos) * (double)dot * (double)this.z));
        return dest;
    }

    public Vector4f transform(Vector4f v) {
        return this.transform(v, v);
    }

    public Vector4f transform(Vector4fc v, Vector4f dest) {
        double sin = Math.sin(this.angle);
        double cos = Math.cosFromSin(sin, (double)this.angle);
        float dot = this.x * v.x() + this.y * v.y() + this.z * v.z();
        dest.set((float)((double)v.x() * cos + sin * (double)(this.y * v.z() - this.z * v.y()) + (1.0 - cos) * (double)dot * (double)this.x), (float)((double)v.y() * cos + sin * (double)(this.z * v.x() - this.x * v.z()) + (1.0 - cos) * (double)dot * (double)this.y), (float)((double)v.z() * cos + sin * (double)(this.x * v.y() - this.y * v.x()) + (1.0 - cos) * (double)dot * (double)this.z), dest.w);
        return dest;
    }

    public String toString() {
        return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
    }

    public String toString(NumberFormat formatter) {
        return "(" + Runtime.format(this.x, formatter) + " " + Runtime.format(this.y, formatter) + " " + Runtime.format(this.z, formatter) + " <| " + Runtime.format(this.angle, formatter) + ")";
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        float nangle = ((double)this.angle < 0.0 ? (float)java.lang.Math.PI * 2 + this.angle % ((float)java.lang.Math.PI * 2) : this.angle) % ((float)java.lang.Math.PI * 2);
        result = 31 * result + Float.floatToIntBits(nangle);
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
        AxisAngle4f other = (AxisAngle4f)obj;
        float nangle = ((double)this.angle < 0.0 ? (float)java.lang.Math.PI * 2 + this.angle % ((float)java.lang.Math.PI * 2) : this.angle) % ((float)java.lang.Math.PI * 2);
        float nangleOther = ((double)other.angle < 0.0 ? (float)java.lang.Math.PI * 2 + other.angle % ((float)java.lang.Math.PI * 2) : other.angle) % ((float)java.lang.Math.PI * 2);
        if (Float.floatToIntBits(nangle) != Float.floatToIntBits(nangleOther)) {
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

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

