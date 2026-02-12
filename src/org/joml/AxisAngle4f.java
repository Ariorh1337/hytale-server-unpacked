package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.NumberFormat;

public class AxisAngle4f implements Externalizable, Cloneable {
   private static final long serialVersionUID = 1L;
   public float angle;
   public float x;
   public float y;
   public float z;

   public AxisAngle4f() {
      this.z = 1.0F;
   }

   public AxisAngle4f(AxisAngle4f a) {
      this.x = a.x;
      this.y = a.y;
      this.z = a.z;
      this.angle = (a.angle < 0.0 ? (float) (java.lang.Math.PI * 2) + a.angle % (float) (java.lang.Math.PI * 2) : a.angle) % (float) (java.lang.Math.PI * 2);
   }

   public AxisAngle4f(Quaternionfc q) {
      float acos = Math.safeAcos(q.w());
      float invSqrt = Math.invsqrt(1.0F - q.w() * q.w());
      if (Float.isInfinite(invSqrt)) {
         this.x = 0.0F;
         this.y = 0.0F;
         this.z = 1.0F;
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
      this.angle = (angle < 0.0 ? (float) (java.lang.Math.PI * 2) + angle % (float) (java.lang.Math.PI * 2) : angle) % (float) (java.lang.Math.PI * 2);
   }

   public AxisAngle4f(float angle, Vector3fc v) {
      this(angle, v.x(), v.y(), v.z());
   }

   public AxisAngle4f set(AxisAngle4f a) {
      this.x = a.x;
      this.y = a.y;
      this.z = a.z;
      this.angle = a.angle;
      this.angle = (this.angle < 0.0 ? (float) (java.lang.Math.PI * 2) + this.angle % (float) (java.lang.Math.PI * 2) : this.angle)
         % (float) (java.lang.Math.PI * 2);
      return this;
   }

   public AxisAngle4f set(AxisAngle4d a) {
      this.x = (float)a.x;
      this.y = (float)a.y;
      this.z = (float)a.z;
      this.angle = (float)a.angle;
      this.angle = (this.angle < 0.0 ? (float) (java.lang.Math.PI * 2) + this.angle % (float) (java.lang.Math.PI * 2) : this.angle)
         % (float) (java.lang.Math.PI * 2);
      return this;
   }

   public AxisAngle4f set(float angle, float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.angle = (angle < 0.0 ? (float) (java.lang.Math.PI * 2) + angle % (float) (java.lang.Math.PI * 2) : angle) % (float) (java.lang.Math.PI * 2);
      return this;
   }

   public AxisAngle4f set(float angle, Vector3fc v) {
      return this.set(angle, v.x(), v.y(), v.z());
   }

   public AxisAngle4f set(Quaternionfc q) {
      float acos = Math.safeAcos(q.w());
      float invSqrt = Math.invsqrt(1.0F - q.w() * q.w());
      if (Float.isInfinite(invSqrt)) {
         this.x = 0.0F;
         this.y = 0.0F;
         this.z = 1.0F;
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
         this.x = 0.0F;
         this.y = 0.0F;
         this.z = 1.0F;
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
      float epsilon = 1.0E-4F;
      float epsilon2 = 0.001F;
      if (!(Math.abs(nm10 - nm01) < epsilon) || !(Math.abs(nm20 - nm02) < epsilon) || !(Math.abs(nm21 - nm12) < epsilon)) {
         float s = Math.invsqrt((nm12 - nm21) * (nm12 - nm21) + (nm20 - nm02) * (nm20 - nm02) + (nm01 - nm10) * (nm01 - nm10));
         this.angle = Math.safeAcos((nm00 + nm11 + nm22 - 1.0F) / 2.0F);
         this.x = (nm12 - nm21) * s;
         this.y = (nm20 - nm02) * s;
         this.z = (nm01 - nm10) * s;
         return this;
      }

      if (Math.abs(nm10 + nm01) < epsilon2
         && Math.abs(nm20 + nm02) < epsilon2
         && Math.abs(nm21 + nm12) < epsilon2
         && Math.abs(nm00 + nm11 + nm22 - 3.0F) < epsilon2) {
         this.x = 0.0F;
         this.y = 0.0F;
         this.z = 1.0F;
         this.angle = 0.0F;
         return this;
      }

      this.angle = (float) java.lang.Math.PI;
      float xx = (nm00 + 1.0F) / 2.0F;
      float yy = (nm11 + 1.0F) / 2.0F;
      float zz = (nm22 + 1.0F) / 2.0F;
      float xy = (nm10 + nm01) / 4.0F;
      float xz = (nm20 + nm02) / 4.0F;
      float yz = (nm21 + nm12) / 4.0F;
      if (xx > yy && xx > zz) {
         this.x = Math.sqrt(xx);
         float invX = 1.0F / this.x;
         this.y = xy * invX;
         this.z = xz * invX;
      } else if (yy > zz) {
         this.y = Math.sqrt(yy);
         float invZ = 1.0F / this.z;
         this.x = xy * invZ;
         this.z = yz * invZ;
      } else {
         this.z = Math.sqrt(zz);
         float invY = 1.0F / this.y;
         this.x = xz * invY;
         this.y = yz * invY;
      }

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
      if (!(Math.abs(nm10 - nm01) < epsilon) || !(Math.abs(nm20 - nm02) < epsilon) || !(Math.abs(nm21 - nm12) < epsilon)) {
         double s = Math.invsqrt((nm12 - nm21) * (nm12 - nm21) + (nm20 - nm02) * (nm20 - nm02) + (nm01 - nm10) * (nm01 - nm10));
         this.angle = (float)Math.safeAcos((nm00 + nm11 + nm22 - 1.0) / 2.0);
         this.x = (float)((nm12 - nm21) * s);
         this.y = (float)((nm20 - nm02) * s);
         this.z = (float)((nm01 - nm10) * s);
         return this;
      }

      if (Math.abs(nm10 + nm01) < epsilon2
         && Math.abs(nm20 + nm02) < epsilon2
         && Math.abs(nm21 + nm12) < epsilon2
         && Math.abs(nm00 + nm11 + nm22 - 3.0) < epsilon2) {
         this.x = 0.0F;
         this.y = 0.0F;
         this.z = 1.0F;
         this.angle = 0.0F;
         return this;
      }

      this.angle = (float) java.lang.Math.PI;
      double xx = (nm00 + 1.0) / 2.0;
      double yy = (nm11 + 1.0) / 2.0;
      double zz = (nm22 + 1.0) / 2.0;
      double xy = (nm10 + nm01) / 4.0;
      double xz = (nm20 + nm02) / 4.0;
      double yz = (nm21 + nm12) / 4.0;
      if (xx > yy && xx > zz) {
         this.x = (float)Math.sqrt(xx);
         float invX = 1.0F / this.x;
         this.y = (float)(xy * invX);
         this.z = (float)(xz * invX);
      } else if (yy > zz) {
         this.y = (float)Math.sqrt(yy);
         float invY = 1.0F / this.y;
         this.x = (float)(xy * invY);
         this.z = (float)(yz * invY);
      } else {
         this.z = (float)Math.sqrt(zz);
         float invZ = 1.0F / this.z;
         this.x = (float)(xz * invZ);
         this.y = (float)(yz * invZ);
      }

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
      float epsilon = 1.0E-4F;
      float epsilon2 = 0.001F;
      if (!(Math.abs(nm10 - nm01) < epsilon) || !(Math.abs(nm20 - nm02) < epsilon) || !(Math.abs(nm21 - nm12) < epsilon)) {
         float s = Math.invsqrt((nm12 - nm21) * (nm12 - nm21) + (nm20 - nm02) * (nm20 - nm02) + (nm01 - nm10) * (nm01 - nm10));
         this.angle = Math.safeAcos((nm00 + nm11 + nm22 - 1.0F) / 2.0F);
         this.x = (nm12 - nm21) * s;
         this.y = (nm20 - nm02) * s;
         this.z = (nm01 - nm10) * s;
         return this;
      }

      if (Math.abs(nm10 + nm01) < epsilon2
         && Math.abs(nm20 + nm02) < epsilon2
         && Math.abs(nm21 + nm12) < epsilon2
         && Math.abs(nm00 + nm11 + nm22 - 3.0F) < epsilon2) {
         this.x = 0.0F;
         this.y = 0.0F;
         this.z = 1.0F;
         this.angle = 0.0F;
         return this;
      }

      this.angle = (float) java.lang.Math.PI;
      float xx = (nm00 + 1.0F) / 2.0F;
      float yy = (nm11 + 1.0F) / 2.0F;
      float zz = (nm22 + 1.0F) / 2.0F;
      float xy = (nm10 + nm01) / 4.0F;
      float xz = (nm20 + nm02) / 4.0F;
      float yz = (nm21 + nm12) / 4.0F;
      if (xx > yy && xx > zz) {
         this.x = Math.sqrt(xx);
         float invX = 1.0F / this.x;
         this.y = xy * invX;
         this.z = xz * invX;
      } else if (yy > zz) {
         this.y = Math.sqrt(yy);
         float invZ = 1.0F / this.z;
         this.x = xy * invZ;
         this.z = yz * invZ;
      } else {
         this.z = Math.sqrt(zz);
         float invY = 1.0F / this.y;
         this.x = xz * invY;
         this.y = yz * invY;
      }

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
      float epsilon = 1.0E-4F;
      float epsilon2 = 0.001F;
      if (!(Math.abs(nm10 - nm01) < epsilon) || !(Math.abs(nm20 - nm02) < epsilon) || !(Math.abs(nm21 - nm12) < epsilon)) {
         float s = Math.invsqrt((nm12 - nm21) * (nm12 - nm21) + (nm20 - nm02) * (nm20 - nm02) + (nm01 - nm10) * (nm01 - nm10));
         this.angle = Math.safeAcos((nm00 + nm11 + nm22 - 1.0F) / 2.0F);
         this.x = (nm12 - nm21) * s;
         this.y = (nm20 - nm02) * s;
         this.z = (nm01 - nm10) * s;
         return this;
      }

      if (Math.abs(nm10 + nm01) < epsilon2
         && Math.abs(nm20 + nm02) < epsilon2
         && Math.abs(nm21 + nm12) < epsilon2
         && Math.abs(nm00 + nm11 + nm22 - 3.0F) < epsilon2) {
         this.x = 0.0F;
         this.y = 0.0F;
         this.z = 1.0F;
         this.angle = 0.0F;
         return this;
      }

      this.angle = (float) java.lang.Math.PI;
      float xx = (nm00 + 1.0F) / 2.0F;
      float yy = (nm11 + 1.0F) / 2.0F;
      float zz = (nm22 + 1.0F) / 2.0F;
      float xy = (nm10 + nm01) / 4.0F;
      float xz = (nm20 + nm02) / 4.0F;
      float yz = (nm21 + nm12) / 4.0F;
      if (xx > yy && xx > zz) {
         this.x = Math.sqrt(xx);
         float invX = 1.0F / this.x;
         this.y = xy * invX;
         this.z = xz * invX;
      } else if (yy > zz) {
         this.y = Math.sqrt(yy);
         float invZ = 1.0F / this.z;
         this.x = xy * invZ;
         this.z = yz * invZ;
      } else {
         this.z = Math.sqrt(zz);
         float invY = 1.0F / this.y;
         this.x = xz * invY;
         this.y = yz * invY;
      }

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
      if (!(Math.abs(nm10 - nm01) < epsilon) || !(Math.abs(nm20 - nm02) < epsilon) || !(Math.abs(nm21 - nm12) < epsilon)) {
         double s = Math.invsqrt((nm12 - nm21) * (nm12 - nm21) + (nm20 - nm02) * (nm20 - nm02) + (nm01 - nm10) * (nm01 - nm10));
         this.angle = (float)Math.safeAcos((nm00 + nm11 + nm22 - 1.0) / 2.0);
         this.x = (float)((nm12 - nm21) * s);
         this.y = (float)((nm20 - nm02) * s);
         this.z = (float)((nm01 - nm10) * s);
         return this;
      }

      if (Math.abs(nm10 + nm01) < epsilon2
         && Math.abs(nm20 + nm02) < epsilon2
         && Math.abs(nm21 + nm12) < epsilon2
         && Math.abs(nm00 + nm11 + nm22 - 3.0) < epsilon2) {
         this.x = 0.0F;
         this.y = 0.0F;
         this.z = 1.0F;
         this.angle = 0.0F;
         return this;
      }

      this.angle = (float) java.lang.Math.PI;
      double xx = (nm00 + 1.0) / 2.0;
      double yy = (nm11 + 1.0) / 2.0;
      double zz = (nm22 + 1.0) / 2.0;
      double xy = (nm10 + nm01) / 4.0;
      double xz = (nm20 + nm02) / 4.0;
      double yz = (nm21 + nm12) / 4.0;
      if (xx > yy && xx > zz) {
         this.x = (float)Math.sqrt(xx);
         float invX = 1.0F / this.x;
         this.y = (float)(xy * invX);
         this.z = (float)(xz * invX);
      } else if (yy > zz) {
         this.y = (float)Math.sqrt(yy);
         float invY = 1.0F / this.y;
         this.x = (float)(xy * invY);
         this.z = (float)(yz * invY);
      } else {
         this.z = (float)Math.sqrt(zz);
         float invZ = 1.0F / this.z;
         this.x = (float)(xz * invZ);
         this.y = (float)(yz * invZ);
      }

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
      this.angle = (this.angle < 0.0 ? (float) (java.lang.Math.PI * 2) + this.angle % (float) (java.lang.Math.PI * 2) : this.angle)
         % (float) (java.lang.Math.PI * 2);
      return this;
   }

   public Vector3f transform(Vector3f v) {
      return this.transform(v, v);
   }

   public Vector3f transform(Vector3fc v, Vector3f dest) {
      double sin = Math.sin(this.angle);
      double cos = Math.cosFromSin(sin, this.angle);
      float dot = this.x * v.x() + this.y * v.y() + this.z * v.z();
      dest.set(
         (float)(v.x() * cos + sin * (this.y * v.z() - this.z * v.y()) + (1.0 - cos) * dot * this.x),
         (float)(v.y() * cos + sin * (this.z * v.x() - this.x * v.z()) + (1.0 - cos) * dot * this.y),
         (float)(v.z() * cos + sin * (this.x * v.y() - this.y * v.x()) + (1.0 - cos) * dot * this.z)
      );
      return dest;
   }

   public Vector4f transform(Vector4f v) {
      return this.transform(v, v);
   }

   public Vector4f transform(Vector4fc v, Vector4f dest) {
      double sin = Math.sin(this.angle);
      double cos = Math.cosFromSin(sin, this.angle);
      float dot = this.x * v.x() + this.y * v.y() + this.z * v.z();
      dest.set(
         (float)(v.x() * cos + sin * (this.y * v.z() - this.z * v.y()) + (1.0 - cos) * dot * this.x),
         (float)(v.y() * cos + sin * (this.z * v.x() - this.x * v.z()) + (1.0 - cos) * dot * this.y),
         (float)(v.z() * cos + sin * (this.x * v.y() - this.y * v.x()) + (1.0 - cos) * dot * this.z),
         dest.w
      );
      return dest;
   }

   public String toString() {
      return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
   }

   public String toString(NumberFormat formatter) {
      return "("
         + Runtime.format(this.x, formatter)
         + " "
         + Runtime.format(this.y, formatter)
         + " "
         + Runtime.format(this.z, formatter)
         + " <| "
         + Runtime.format(this.angle, formatter)
         + ")";
   }

   public int hashCode() {
      int prime = 31;
      int result = 1;
      float nangle = (this.angle < 0.0 ? (float) (java.lang.Math.PI * 2) + this.angle % (float) (java.lang.Math.PI * 2) : this.angle)
         % (float) (java.lang.Math.PI * 2);
      result = 31 * result + Float.floatToIntBits(nangle);
      result = 31 * result + Float.floatToIntBits(this.x);
      result = 31 * result + Float.floatToIntBits(this.y);
      return 31 * result + Float.floatToIntBits(this.z);
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         AxisAngle4f other = (AxisAngle4f)obj;
         float nangle = (this.angle < 0.0 ? (float) (java.lang.Math.PI * 2) + this.angle % (float) (java.lang.Math.PI * 2) : this.angle)
            % (float) (java.lang.Math.PI * 2);
         float nangleOther = (other.angle < 0.0 ? (float) (java.lang.Math.PI * 2) + other.angle % (float) (java.lang.Math.PI * 2) : other.angle)
            % (float) (java.lang.Math.PI * 2);
         if (Float.floatToIntBits(nangle) != Float.floatToIntBits(nangleOther)) {
            return false;
         } else if (Float.floatToIntBits(this.x) != Float.floatToIntBits(other.x)) {
            return false;
         } else {
            return Float.floatToIntBits(this.y) != Float.floatToIntBits(other.y) ? false : Float.floatToIntBits(this.z) == Float.floatToIntBits(other.z);
         }
      }
   }

   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }
}
