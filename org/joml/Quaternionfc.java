/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.joml.AxisAngle4d;
import org.joml.AxisAngle4f;
import org.joml.Matrix3d;
import org.joml.Matrix3f;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Matrix4x3d;
import org.joml.Matrix4x3f;
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

public interface Quaternionfc {
    public float x();

    public float y();

    public float z();

    public float w();

    public Quaternionf normalize(Quaternionf var1);

    public Quaternionf add(float var1, float var2, float var3, float var4, Quaternionf var5);

    public Quaternionf add(Quaternionfc var1, Quaternionf var2);

    public Quaternionf sub(float var1, float var2, float var3, float var4, Quaternionf var5);

    public Quaternionf sub(Quaternionfc var1, Quaternionf var2);

    public float angle();

    public Matrix3f get(Matrix3f var1);

    public Matrix3d get(Matrix3d var1);

    public Matrix4f get(Matrix4f var1);

    public Matrix4d get(Matrix4d var1);

    public Matrix4x3f get(Matrix4x3f var1);

    public Matrix4x3d get(Matrix4x3d var1);

    public AxisAngle4f get(AxisAngle4f var1);

    public AxisAngle4d get(AxisAngle4d var1);

    public Quaterniond get(Quaterniond var1);

    public Quaternionf get(Quaternionf var1);

    public ByteBuffer getAsMatrix3f(ByteBuffer var1);

    public FloatBuffer getAsMatrix3f(FloatBuffer var1);

    public ByteBuffer getAsMatrix4f(ByteBuffer var1);

    public FloatBuffer getAsMatrix4f(FloatBuffer var1);

    public ByteBuffer getAsMatrix4x3f(ByteBuffer var1);

    public FloatBuffer getAsMatrix4x3f(FloatBuffer var1);

    public Quaternionf mul(Quaternionfc var1, Quaternionf var2);

    public Quaternionf mul(float var1, float var2, float var3, float var4, Quaternionf var5);

    public Quaternionf mul(float var1, Quaternionf var2);

    public Quaternionf premul(Quaternionfc var1, Quaternionf var2);

    public Quaternionf premul(float var1, float var2, float var3, float var4, Quaternionf var5);

    public Vector3f transform(Vector3f var1);

    public Vector3f transformInverse(Vector3f var1);

    public Vector3f transformUnit(Vector3f var1);

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

    public Vector3f transform(float var1, float var2, float var3, Vector3f var4);

    public Vector3d transform(float var1, float var2, float var3, Vector3d var4);

    public Vector3f transformInverse(float var1, float var2, float var3, Vector3f var4);

    public Vector3d transformInverse(float var1, float var2, float var3, Vector3d var4);

    public Vector3f transformInverseUnit(Vector3f var1);

    public Vector3f transformUnit(Vector3fc var1, Vector3f var2);

    public Vector3f transformInverseUnit(Vector3fc var1, Vector3f var2);

    public Vector3f transformUnit(float var1, float var2, float var3, Vector3f var4);

    public Vector3d transformUnit(float var1, float var2, float var3, Vector3d var4);

    public Vector3f transformInverseUnit(float var1, float var2, float var3, Vector3f var4);

    public Vector3d transformInverseUnit(float var1, float var2, float var3, Vector3d var4);

    public Vector4f transform(Vector4fc var1, Vector4f var2);

    public Vector4f transformInverse(Vector4fc var1, Vector4f var2);

    public Vector4f transform(float var1, float var2, float var3, Vector4f var4);

    public Vector4f transformInverse(float var1, float var2, float var3, Vector4f var4);

    public Vector4f transformUnit(Vector4fc var1, Vector4f var2);

    public Vector4f transformUnit(Vector4f var1);

    public Vector4f transformInverseUnit(Vector4f var1);

    public Vector4f transformInverseUnit(Vector4fc var1, Vector4f var2);

    public Vector4f transformUnit(float var1, float var2, float var3, Vector4f var4);

    public Vector4f transformInverseUnit(float var1, float var2, float var3, Vector4f var4);

    public Vector3d transform(Vector3d var1);

    public Vector3d transformInverse(Vector3d var1);

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

    public Quaternionf invert(Quaternionf var1);

    public Quaternionf div(Quaternionfc var1, Quaternionf var2);

    public Quaternionf div(float var1, Quaternionf var2);

    public Quaternionf conjugate(Quaternionf var1);

    public Quaternionf rotateXYZ(float var1, float var2, float var3, Quaternionf var4);

    public Quaternionf rotateZYX(float var1, float var2, float var3, Quaternionf var4);

    public Quaternionf rotateYXZ(float var1, float var2, float var3, Quaternionf var4);

    public Vector3f getEulerAnglesXYZ(Vector3f var1);

    public Vector3f getEulerAnglesZYX(Vector3f var1);

    public Vector3f getEulerAnglesZXY(Vector3f var1);

    public Vector3f getEulerAnglesYXZ(Vector3f var1);

    public float lengthSquared();

    public Quaternionf slerp(Quaternionfc var1, float var2, Quaternionf var3);

    public Quaternionf scale(float var1, Quaternionf var2);

    public Quaternionf integrate(float var1, float var2, float var3, float var4, Quaternionf var5);

    public Quaternionf nlerp(Quaternionfc var1, float var2, Quaternionf var3);

    public Quaternionf nlerpIterative(Quaternionfc var1, float var2, float var3, Quaternionf var4);

    public Quaternionf lookAlong(Vector3fc var1, Vector3fc var2, Quaternionf var3);

    public Quaternionf lookAlong(float var1, float var2, float var3, float var4, float var5, float var6, Quaternionf var7);

    public Quaternionf rotateTo(float var1, float var2, float var3, float var4, float var5, float var6, Quaternionf var7);

    public Quaternionf rotateTo(Vector3fc var1, Vector3fc var2, Quaternionf var3);

    public Quaternionf rotateX(float var1, Quaternionf var2);

    public Quaternionf rotateY(float var1, Quaternionf var2);

    public Quaternionf rotateZ(float var1, Quaternionf var2);

    public Quaternionf rotateLocalX(float var1, Quaternionf var2);

    public Quaternionf rotateLocalY(float var1, Quaternionf var2);

    public Quaternionf rotateLocalZ(float var1, Quaternionf var2);

    public Quaternionf rotateAxis(float var1, float var2, float var3, float var4, Quaternionf var5);

    public Quaternionf rotateAxis(float var1, Vector3fc var2, Quaternionf var3);

    public Quaternionf difference(Quaternionfc var1, Quaternionf var2);

    public Vector3f positiveX(Vector3f var1);

    public Vector3f normalizedPositiveX(Vector3f var1);

    public Vector3f positiveY(Vector3f var1);

    public Vector3f normalizedPositiveY(Vector3f var1);

    public Vector3f positiveZ(Vector3f var1);

    public Vector3f normalizedPositiveZ(Vector3f var1);

    public Quaternionf conjugateBy(Quaternionfc var1, Quaternionf var2);

    public boolean isFinite();

    public boolean equals(Quaternionfc var1, float var2);

    public boolean equals(float var1, float var2, float var3, float var4);
}

