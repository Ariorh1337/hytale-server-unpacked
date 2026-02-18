/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import org.joml.AxisAngle4d;
import org.joml.AxisAngle4f;
import org.joml.Matrix3d;
import org.joml.Matrix3f;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4d;
import org.joml.Vector4dc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

public interface Quaterniondc {
    public double x();

    public double y();

    public double z();

    public double w();

    public Quaterniond normalize(Quaterniond var1);

    public Quaterniond add(double var1, double var3, double var5, double var7, Quaterniond var9);

    public Quaterniond add(Quaterniondc var1, Quaterniond var2);

    public Quaterniond sub(double var1, double var3, double var5, double var7, Quaterniond var9);

    public Quaterniond sub(Quaterniondc var1, Quaterniond var2);

    public double dot(Quaterniondc var1);

    public double angle();

    public Matrix3d get(Matrix3d var1);

    public Matrix3f get(Matrix3f var1);

    public Matrix4d get(Matrix4d var1);

    public Matrix4f get(Matrix4f var1);

    public AxisAngle4f get(AxisAngle4f var1);

    public AxisAngle4d get(AxisAngle4d var1);

    public Quaterniond get(Quaterniond var1);

    public Quaternionf get(Quaternionf var1);

    public Quaterniond mul(Quaterniondc var1, Quaterniond var2);

    public Quaterniond mul(double var1, double var3, double var5, double var7, Quaterniond var9);

    public Quaterniond mul(double var1, Quaterniond var3);

    public Quaterniond premul(Quaterniondc var1, Quaterniond var2);

    public Quaterniond premul(double var1, double var3, double var5, double var7, Quaterniond var9);

    public Vector3d transform(Vector3d var1);

    public Vector3d transformInverse(Vector3d var1);

    public Vector3d transformUnit(Vector3d var1);

    public Vector3d transformInverseUnit(Vector3d var1);

    public Vector3d transformPositiveX(Vector3d var1);

    public Vector4d transformPositiveX(Vector4d var1);

    public Vector3d transformUnitPositiveX(Vector3d var1);

    public Vector4d transformUnitPositiveX(Vector4d var1);

    public Vector3d transformPositiveY(Vector3d var1);

    public Vector4d transformPositiveY(Vector4d var1);

    public Vector3d transformUnitPositiveY(Vector3d var1);

    public Vector4d transformUnitPositiveY(Vector4d var1);

    public Vector3d transformPositiveZ(Vector3d var1);

    public Vector4d transformPositiveZ(Vector4d var1);

    public Vector3d transformUnitPositiveZ(Vector3d var1);

    public Vector4d transformUnitPositiveZ(Vector4d var1);

    public Vector4d transform(Vector4d var1);

    public Vector4d transformInverse(Vector4d var1);

    public Vector3d transform(Vector3dc var1, Vector3d var2);

    public Vector3d transformInverse(Vector3dc var1, Vector3d var2);

    public Vector3d transform(double var1, double var3, double var5, Vector3d var7);

    public Vector3d transformInverse(double var1, double var3, double var5, Vector3d var7);

    public Vector4d transform(Vector4dc var1, Vector4d var2);

    public Vector4d transformInverse(Vector4dc var1, Vector4d var2);

    public Vector4d transform(double var1, double var3, double var5, Vector4d var7);

    public Vector4d transformInverse(double var1, double var3, double var5, Vector4d var7);

    public Vector3f transform(Vector3f var1);

    public Vector3f transformInverse(Vector3f var1);

    public Vector4d transformUnit(Vector4d var1);

    public Vector4d transformInverseUnit(Vector4d var1);

    public Vector3d transformUnit(Vector3dc var1, Vector3d var2);

    public Vector3d transformInverseUnit(Vector3dc var1, Vector3d var2);

    public Vector3d transformUnit(double var1, double var3, double var5, Vector3d var7);

    public Vector3d transformInverseUnit(double var1, double var3, double var5, Vector3d var7);

    public Vector4d transformUnit(Vector4dc var1, Vector4d var2);

    public Vector4d transformInverseUnit(Vector4dc var1, Vector4d var2);

    public Vector4d transformUnit(double var1, double var3, double var5, Vector4d var7);

    public Vector4d transformInverseUnit(double var1, double var3, double var5, Vector4d var7);

    public Vector3f transformUnit(Vector3f var1);

    public Vector3f transformInverseUnit(Vector3f var1);

    public Vector3f transformPositiveX(Vector3f var1);

    public Vector4f transformPositiveX(Vector4f var1);

    public Vector3f transformUnitPositiveX(Vector3f var1);

    public Vector4f transformUnitPositiveX(Vector4f var1);

    public Vector3f transformPositiveY(Vector3f var1);

    public Vector4f transformPositiveY(Vector4f var1);

    public Vector3f transformUnitPositiveY(Vector3f var1);

    public Vector4f transformUnitPositiveY(Vector4f var1);

    public Vector3f transformPositiveZ(Vector3f var1);

    public Vector4f transformPositiveZ(Vector4f var1);

    public Vector3f transformUnitPositiveZ(Vector3f var1);

    public Vector4f transformUnitPositiveZ(Vector4f var1);

    public Vector4f transform(Vector4f var1);

    public Vector4f transformInverse(Vector4f var1);

    public Vector3f transform(Vector3fc var1, Vector3f var2);

    public Vector3f transformInverse(Vector3fc var1, Vector3f var2);

    public Vector3f transform(double var1, double var3, double var5, Vector3f var7);

    public Vector3f transformInverse(double var1, double var3, double var5, Vector3f var7);

    public Vector4f transform(Vector4fc var1, Vector4f var2);

    public Vector4f transformInverse(Vector4fc var1, Vector4f var2);

    public Vector4f transform(double var1, double var3, double var5, Vector4f var7);

    public Vector4f transformInverse(double var1, double var3, double var5, Vector4f var7);

    public Vector4f transformUnit(Vector4f var1);

    public Vector4f transformInverseUnit(Vector4f var1);

    public Vector3f transformUnit(Vector3fc var1, Vector3f var2);

    public Vector3f transformInverseUnit(Vector3fc var1, Vector3f var2);

    public Vector3f transformUnit(double var1, double var3, double var5, Vector3f var7);

    public Vector3f transformInverseUnit(double var1, double var3, double var5, Vector3f var7);

    public Vector4f transformUnit(Vector4fc var1, Vector4f var2);

    public Vector4f transformInverseUnit(Vector4fc var1, Vector4f var2);

    public Vector4f transformUnit(double var1, double var3, double var5, Vector4f var7);

    public Vector4f transformInverseUnit(double var1, double var3, double var5, Vector4f var7);

    public Quaterniond invert(Quaterniond var1);

    public Quaterniond div(Quaterniondc var1, Quaterniond var2);

    public Quaterniond div(double var1, Quaterniond var3);

    public Quaterniond conjugate(Quaterniond var1);

    public double lengthSquared();

    public Quaterniond slerp(Quaterniondc var1, double var2, Quaterniond var4);

    public Quaterniond scale(double var1, Quaterniond var3);

    public Quaterniond integrate(double var1, double var3, double var5, double var7, Quaterniond var9);

    public Quaterniond nlerp(Quaterniondc var1, double var2, Quaterniond var4);

    public Quaterniond nlerpIterative(Quaterniondc var1, double var2, double var4, Quaterniond var6);

    public Quaterniond lookAlong(Vector3dc var1, Vector3dc var2, Quaterniond var3);

    public Quaterniond lookAlong(double var1, double var3, double var5, double var7, double var9, double var11, Quaterniond var13);

    public Quaterniond difference(Quaterniondc var1, Quaterniond var2);

    public Quaterniond rotateTo(double var1, double var3, double var5, double var7, double var9, double var11, Quaterniond var13);

    public Quaterniond rotateTo(Vector3dc var1, Vector3dc var2, Quaterniond var3);

    public Quaterniond rotateX(double var1, Quaterniond var3);

    public Quaterniond rotateY(double var1, Quaterniond var3);

    public Quaterniond rotateZ(double var1, Quaterniond var3);

    public Quaterniond rotateLocalX(double var1, Quaterniond var3);

    public Quaterniond rotateLocalY(double var1, Quaterniond var3);

    public Quaterniond rotateLocalZ(double var1, Quaterniond var3);

    public Quaterniond rotateXYZ(double var1, double var3, double var5, Quaterniond var7);

    public Quaterniond rotateZYX(double var1, double var3, double var5, Quaterniond var7);

    public Quaterniond rotateYXZ(double var1, double var3, double var5, Quaterniond var7);

    public Vector3d getEulerAnglesXYZ(Vector3d var1);

    public Vector3d getEulerAnglesZYX(Vector3d var1);

    public Vector3d getEulerAnglesZXY(Vector3d var1);

    public Vector3d getEulerAnglesYXZ(Vector3d var1);

    public Quaterniond rotateAxis(double var1, double var3, double var5, double var7, Quaterniond var9);

    public Quaterniond rotateAxis(double var1, Vector3dc var3, Quaterniond var4);

    public Vector3d positiveX(Vector3d var1);

    public Vector3d normalizedPositiveX(Vector3d var1);

    public Vector3d positiveY(Vector3d var1);

    public Vector3d normalizedPositiveY(Vector3d var1);

    public Vector3d positiveZ(Vector3d var1);

    public Vector3d normalizedPositiveZ(Vector3d var1);

    public Quaterniond conjugateBy(Quaterniondc var1, Quaterniond var2);

    public boolean isFinite();

    public boolean equals(Quaterniondc var1, double var2);

    public boolean equals(double var1, double var3, double var5, double var7);
}

