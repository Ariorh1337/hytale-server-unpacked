/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.NumberFormat;
import org.joml.AxisAngle4f;
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
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4d;
import org.joml.Vector4dc;

public class AxisAngle4d
implements Externalizable,
Cloneable {
    private static final long serialVersionUID = 1L;
    public double angle;
    public double x;
    public double y;
    public double z;

    public AxisAngle4d() {
        this.z = 1.0;
    }

    public AxisAngle4d(AxisAngle4d a) {
        this.x = a.x;
        this.y = a.y;
        this.z = a.z;
        this.angle = (a.angle < 0.0 ? java.lang.Math.PI * 2 + a.angle % (java.lang.Math.PI * 2) : a.angle) % (java.lang.Math.PI * 2);
    }

    public AxisAngle4d(AxisAngle4f a) {
        this.x = a.x;
        this.y = a.y;
        this.z = a.z;
        this.angle = ((double)a.angle < 0.0 ? java.lang.Math.PI * 2 + (double)a.angle % (java.lang.Math.PI * 2) : (double)a.angle) % (java.lang.Math.PI * 2);
    }

    public AxisAngle4d(Quaternionfc q) {
        float acos = Math.safeAcos(q.w());
        float invSqrt = Math.invsqrt(1.0f - q.w() * q.w());
        if (Float.isInfinite(invSqrt)) {
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
        } else {
            this.x = q.x() * invSqrt;
            this.y = q.y() * invSqrt;
            this.z = q.z() * invSqrt;
        }
        this.angle = acos + acos;
    }

    public AxisAngle4d(Quaterniondc q) {
        double acos = Math.safeAcos(q.w());
        double invSqrt = Math.invsqrt(1.0 - q.w() * q.w());
        if (Double.isInfinite(invSqrt)) {
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
        } else {
            this.x = q.x() * invSqrt;
            this.y = q.y() * invSqrt;
            this.z = q.z() * invSqrt;
        }
        this.angle = acos + acos;
    }

    public AxisAngle4d(double angle, double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.angle = (angle < 0.0 ? java.lang.Math.PI * 2 + angle % (java.lang.Math.PI * 2) : angle) % (java.lang.Math.PI * 2);
    }

    public AxisAngle4d(double angle, Vector3dc v) {
        this(angle, v.x(), v.y(), v.z());
    }

    public AxisAngle4d(double angle, Vector3f v) {
        this(angle, v.x, v.y, v.z);
    }

    public AxisAngle4d set(AxisAngle4d a) {
        this.x = a.x;
        this.y = a.y;
        this.z = a.z;
        this.angle = (a.angle < 0.0 ? java.lang.Math.PI * 2 + a.angle % (java.lang.Math.PI * 2) : a.angle) % (java.lang.Math.PI * 2);
        return this;
    }

    public AxisAngle4d set(AxisAngle4f a) {
        this.x = a.x;
        this.y = a.y;
        this.z = a.z;
        this.angle = ((double)a.angle < 0.0 ? java.lang.Math.PI * 2 + (double)a.angle % (java.lang.Math.PI * 2) : (double)a.angle) % (java.lang.Math.PI * 2);
        return this;
    }

    public AxisAngle4d set(double angle, double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.angle = (angle < 0.0 ? java.lang.Math.PI * 2 + angle % (java.lang.Math.PI * 2) : angle) % (java.lang.Math.PI * 2);
        return this;
    }

    public AxisAngle4d set(double angle, Vector3dc v) {
        return this.set(angle, v.x(), v.y(), v.z());
    }

    public AxisAngle4d set(double angle, Vector3f v) {
        return this.set(angle, v.x, v.y, v.z);
    }

    public AxisAngle4d set(Quaternionfc q) {
        float acos = Math.safeAcos(q.w());
        float invSqrt = Math.invsqrt(1.0f - q.w() * q.w());
        if (Float.isInfinite(invSqrt)) {
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
        } else {
            this.x = q.x() * invSqrt;
            this.y = q.y() * invSqrt;
            this.z = q.z() * invSqrt;
        }
        this.angle = acos + acos;
        return this;
    }

    public AxisAngle4d set(Quaterniondc q) {
        double acos = Math.safeAcos(q.w());
        double invSqrt = Math.invsqrt(1.0 - q.w() * q.w());
        if (Double.isInfinite(invSqrt)) {
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
        } else {
            this.x = q.x() * invSqrt;
            this.y = q.y() * invSqrt;
            this.z = q.z() * invSqrt;
        }
        this.angle = acos + acos;
        return this;
    }

    public AxisAngle4d set(Matrix3fc m) {
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
                this.x = 0.0;
                this.y = 0.0;
                this.z = 1.0;
                this.angle = 0.0;
                return this;
            }
            this.angle = java.lang.Math.PI;
            double xx = (nm00 + 1.0) / 2.0;
            double yy = (nm11 + 1.0) / 2.0;
            double zz = (nm22 + 1.0) / 2.0;
            double xy = (nm10 + nm01) / 4.0;
            double xz = (nm20 + nm02) / 4.0;
            double yz = (nm21 + nm12) / 4.0;
            if (xx > yy && xx > zz) {
                this.x = Math.sqrt(xx);
                double invX = 1.0 / this.x;
                this.y = xy * invX;
                this.z = xz * invX;
            } else if (yy > zz) {
                this.y = Math.sqrt(yy);
                double invY = 1.0 / this.y;
                this.x = xy * invY;
                this.z = yz * invY;
            } else {
                this.z = Math.sqrt(zz);
                double invZ = 1.0 / this.z;
                this.x = xz * invZ;
                this.y = yz * invZ;
            }
            return this;
        }
        double s = Math.invsqrt((nm12 - nm21) * (nm12 - nm21) + (nm20 - nm02) * (nm20 - nm02) + (nm01 - nm10) * (nm01 - nm10));
        this.angle = Math.safeAcos((nm00 + nm11 + nm22 - 1.0) / 2.0);
        this.x = (nm12 - nm21) * s;
        this.y = (nm20 - nm02) * s;
        this.z = (nm01 - nm10) * s;
        return this;
    }

    public AxisAngle4d set(Matrix3dc m) {
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
                this.x = 0.0;
                this.y = 0.0;
                this.z = 1.0;
                this.angle = 0.0;
                return this;
            }
            this.angle = java.lang.Math.PI;
            double xx = (nm00 + 1.0) / 2.0;
            double yy = (nm11 + 1.0) / 2.0;
            double zz = (nm22 + 1.0) / 2.0;
            double xy = (nm10 + nm01) / 4.0;
            double xz = (nm20 + nm02) / 4.0;
            double yz = (nm21 + nm12) / 4.0;
            if (xx > yy && xx > zz) {
                this.x = Math.sqrt(xx);
                double invX = 1.0 / this.x;
                this.y = xy * invX;
                this.z = xz * invX;
            } else if (yy > zz) {
                this.y = Math.sqrt(yy);
                double invY = 1.0 / this.y;
                this.x = xy * invY;
                this.z = yz * invY;
            } else {
                this.z = Math.sqrt(zz);
                double invZ = 1.0 / this.z;
                this.x = xz * invZ;
                this.y = yz * invZ;
            }
            return this;
        }
        double s = Math.invsqrt((nm12 - nm21) * (nm12 - nm21) + (nm20 - nm02) * (nm20 - nm02) + (nm01 - nm10) * (nm01 - nm10));
        this.angle = Math.safeAcos((nm00 + nm11 + nm22 - 1.0) / 2.0);
        this.x = (nm12 - nm21) * s;
        this.y = (nm20 - nm02) * s;
        this.z = (nm01 - nm10) * s;
        return this;
    }

    public AxisAngle4d set(Matrix4fc m) {
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
                this.x = 0.0;
                this.y = 0.0;
                this.z = 1.0;
                this.angle = 0.0;
                return this;
            }
            this.angle = java.lang.Math.PI;
            double xx = (nm00 + 1.0) / 2.0;
            double yy = (nm11 + 1.0) / 2.0;
            double zz = (nm22 + 1.0) / 2.0;
            double xy = (nm10 + nm01) / 4.0;
            double xz = (nm20 + nm02) / 4.0;
            double yz = (nm21 + nm12) / 4.0;
            if (xx > yy && xx > zz) {
                this.x = Math.sqrt(xx);
                double invX = 1.0 / this.x;
                this.y = xy * invX;
                this.z = xz * invX;
            } else if (yy > zz) {
                this.y = Math.sqrt(yy);
                double invY = 1.0 / this.y;
                this.x = xy * invY;
                this.z = yz * invY;
            } else {
                this.z = Math.sqrt(zz);
                double invZ = 1.0 / this.z;
                this.x = xz * invZ;
                this.y = yz * invZ;
            }
            return this;
        }
        double s = Math.invsqrt((nm12 - nm21) * (nm12 - nm21) + (nm20 - nm02) * (nm20 - nm02) + (nm01 - nm10) * (nm01 - nm10));
        this.angle = Math.safeAcos((nm00 + nm11 + nm22 - 1.0) / 2.0);
        this.x = (nm12 - nm21) * s;
        this.y = (nm20 - nm02) * s;
        this.z = (nm01 - nm10) * s;
        return this;
    }

    public AxisAngle4d set(Matrix4x3fc m) {
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
                this.x = 0.0;
                this.y = 0.0;
                this.z = 1.0;
                this.angle = 0.0;
                return this;
            }
            this.angle = java.lang.Math.PI;
            double xx = (nm00 + 1.0) / 2.0;
            double yy = (nm11 + 1.0) / 2.0;
            double zz = (nm22 + 1.0) / 2.0;
            double xy = (nm10 + nm01) / 4.0;
            double xz = (nm20 + nm02) / 4.0;
            double yz = (nm21 + nm12) / 4.0;
            if (xx > yy && xx > zz) {
                this.x = Math.sqrt(xx);
                double invX = 1.0 / this.x;
                this.y = xy * invX;
                this.z = xz * invX;
            } else if (yy > zz) {
                this.y = Math.sqrt(yy);
                double invY = 1.0 / this.y;
                this.x = xy * invY;
                this.z = yz * invY;
            } else {
                this.z = Math.sqrt(zz);
                double invZ = 1.0 / this.z;
                this.x = xz * invZ;
                this.y = yz * invZ;
            }
            return this;
        }
        double s = Math.invsqrt((nm12 - nm21) * (nm12 - nm21) + (nm20 - nm02) * (nm20 - nm02) + (nm01 - nm10) * (nm01 - nm10));
        this.angle = Math.safeAcos((nm00 + nm11 + nm22 - 1.0) / 2.0);
        this.x = (nm12 - nm21) * s;
        this.y = (nm20 - nm02) * s;
        this.z = (nm01 - nm10) * s;
        return this;
    }

    public AxisAngle4d set(Matrix4dc m) {
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
                this.x = 0.0;
                this.y = 0.0;
                this.z = 1.0;
                this.angle = 0.0;
                return this;
            }
            this.angle = java.lang.Math.PI;
            double xx = (nm00 + 1.0) / 2.0;
            double yy = (nm11 + 1.0) / 2.0;
            double zz = (nm22 + 1.0) / 2.0;
            double xy = (nm10 + nm01) / 4.0;
            double xz = (nm20 + nm02) / 4.0;
            double yz = (nm21 + nm12) / 4.0;
            if (xx > yy && xx > zz) {
                this.x = Math.sqrt(xx);
                double invX = 1.0 / this.x;
                this.y = xy * invX;
                this.z = xz * invX;
            } else if (yy > zz) {
                this.y = Math.sqrt(yy);
                double invY = 1.0 / this.y;
                this.x = xy * invY;
                this.z = yz * invY;
            } else {
                this.z = Math.sqrt(zz);
                double invZ = 1.0 / this.z;
                this.x = xz * invZ;
                this.y = yz * invZ;
            }
            return this;
        }
        double s = Math.invsqrt((nm12 - nm21) * (nm12 - nm21) + (nm20 - nm02) * (nm20 - nm02) + (nm01 - nm10) * (nm01 - nm10));
        this.angle = Math.safeAcos((nm00 + nm11 + nm22 - 1.0) / 2.0);
        this.x = (nm12 - nm21) * s;
        this.y = (nm20 - nm02) * s;
        this.z = (nm01 - nm10) * s;
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
        out.writeDouble(this.angle);
        out.writeDouble(this.x);
        out.writeDouble(this.y);
        out.writeDouble(this.z);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.angle = in.readDouble();
        this.x = in.readDouble();
        this.y = in.readDouble();
        this.z = in.readDouble();
    }

    public AxisAngle4d normalize() {
        double invLength = Math.invsqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        this.x *= invLength;
        this.y *= invLength;
        this.z *= invLength;
        return this;
    }

    public AxisAngle4d rotate(double ang) {
        this.angle += ang;
        this.angle = (this.angle < 0.0 ? java.lang.Math.PI * 2 + this.angle % (java.lang.Math.PI * 2) : this.angle) % (java.lang.Math.PI * 2);
        return this;
    }

    public Vector3d transform(Vector3d v) {
        return this.transform(v, v);
    }

    public Vector3d transform(Vector3dc v, Vector3d dest) {
        double sin = Math.sin(this.angle);
        double cos = Math.cosFromSin(sin, this.angle);
        double dot = this.x * v.x() + this.y * v.y() + this.z * v.z();
        dest.set(v.x() * cos + sin * (this.y * v.z() - this.z * v.y()) + (1.0 - cos) * dot * this.x, v.y() * cos + sin * (this.z * v.x() - this.x * v.z()) + (1.0 - cos) * dot * this.y, v.z() * cos + sin * (this.x * v.y() - this.y * v.x()) + (1.0 - cos) * dot * this.z);
        return dest;
    }

    public Vector3f transform(Vector3f v) {
        return this.transform(v, v);
    }

    public Vector3f transform(Vector3fc v, Vector3f dest) {
        double sin = Math.sin(this.angle);
        double cos = Math.cosFromSin(sin, this.angle);
        double dot = this.x * (double)v.x() + this.y * (double)v.y() + this.z * (double)v.z();
        dest.set((float)((double)v.x() * cos + sin * (this.y * (double)v.z() - this.z * (double)v.y()) + (1.0 - cos) * dot * this.x), (float)((double)v.y() * cos + sin * (this.z * (double)v.x() - this.x * (double)v.z()) + (1.0 - cos) * dot * this.y), (float)((double)v.z() * cos + sin * (this.x * (double)v.y() - this.y * (double)v.x()) + (1.0 - cos) * dot * this.z));
        return dest;
    }

    public Vector4d transform(Vector4d v) {
        return this.transform(v, v);
    }

    public Vector4d transform(Vector4dc v, Vector4d dest) {
        double sin = Math.sin(this.angle);
        double cos = Math.cosFromSin(sin, this.angle);
        double dot = this.x * v.x() + this.y * v.y() + this.z * v.z();
        dest.set(v.x() * cos + sin * (this.y * v.z() - this.z * v.y()) + (1.0 - cos) * dot * this.x, v.y() * cos + sin * (this.z * v.x() - this.x * v.z()) + (1.0 - cos) * dot * this.y, v.z() * cos + sin * (this.x * v.y() - this.y * v.x()) + (1.0 - cos) * dot * this.z, dest.w);
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
        long temp = Double.doubleToLongBits((this.angle < 0.0 ? java.lang.Math.PI * 2 + this.angle % (java.lang.Math.PI * 2) : this.angle) % (java.lang.Math.PI * 2));
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
        AxisAngle4d other = (AxisAngle4d)obj;
        if (Double.doubleToLongBits((this.angle < 0.0 ? java.lang.Math.PI * 2 + this.angle % (java.lang.Math.PI * 2) : this.angle) % (java.lang.Math.PI * 2)) != Double.doubleToLongBits((other.angle < 0.0 ? java.lang.Math.PI * 2 + other.angle % (java.lang.Math.PI * 2) : other.angle) % (java.lang.Math.PI * 2))) {
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

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

