/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.joml.AxisAngle4f;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public interface Matrix3fc {
    public float m00();

    public float m01();

    public float m02();

    public float m10();

    public float m11();

    public float m12();

    public float m20();

    public float m21();

    public float m22();

    public Matrix3f mul(Matrix3fc var1, Matrix3f var2);

    public Matrix3f mulLocal(Matrix3fc var1, Matrix3f var2);

    public float determinant();

    public Matrix3f invert(Matrix3f var1);

    public Matrix3f transpose(Matrix3f var1);

    public Matrix3f get(Matrix3f var1);

    public Matrix4f get(Matrix4f var1);

    public AxisAngle4f getRotation(AxisAngle4f var1);

    public Quaternionf getUnnormalizedRotation(Quaternionf var1);

    public Quaternionf getNormalizedRotation(Quaternionf var1);

    public Quaterniond getUnnormalizedRotation(Quaterniond var1);

    public Quaterniond getNormalizedRotation(Quaterniond var1);

    public FloatBuffer get(FloatBuffer var1);

    public FloatBuffer get(int var1, FloatBuffer var2);

    public ByteBuffer get(ByteBuffer var1);

    public ByteBuffer get(int var1, ByteBuffer var2);

    public FloatBuffer get3x4(FloatBuffer var1);

    public FloatBuffer get3x4(int var1, FloatBuffer var2);

    public ByteBuffer get3x4(ByteBuffer var1);

    public ByteBuffer get3x4(int var1, ByteBuffer var2);

    public FloatBuffer getTransposed(FloatBuffer var1);

    public FloatBuffer getTransposed(int var1, FloatBuffer var2);

    public ByteBuffer getTransposed(ByteBuffer var1);

    public ByteBuffer getTransposed(int var1, ByteBuffer var2);

    public Matrix3fc getToAddress(long var1);

    public Matrix3fc getTransposedToAddress(long var1);

    public float[] get(float[] var1, int var2);

    public float[] get(float[] var1);

    public Matrix3f scale(Vector3fc var1, Matrix3f var2);

    public Matrix3f scale(float var1, float var2, float var3, Matrix3f var4);

    public Matrix3f scale(float var1, Matrix3f var2);

    public Matrix3f scaleLocal(float var1, float var2, float var3, Matrix3f var4);

    public Vector3f transform(Vector3f var1);

    public Vector3f transform(Vector3fc var1, Vector3f var2);

    public Vector3f transform(float var1, float var2, float var3, Vector3f var4);

    public Vector3f transformTranspose(Vector3f var1);

    public Vector3f transformTranspose(Vector3fc var1, Vector3f var2);

    public Vector3f transformTranspose(float var1, float var2, float var3, Vector3f var4);

    public Matrix3f rotateX(float var1, Matrix3f var2);

    public Matrix3f rotateY(float var1, Matrix3f var2);

    public Matrix3f rotateZ(float var1, Matrix3f var2);

    public Matrix3f rotateXYZ(float var1, float var2, float var3, Matrix3f var4);

    public Matrix3f rotateZYX(float var1, float var2, float var3, Matrix3f var4);

    public Matrix3f rotateYXZ(float var1, float var2, float var3, Matrix3f var4);

    public Matrix3f rotate(float var1, float var2, float var3, float var4, Matrix3f var5);

    public Matrix3f rotateLocal(float var1, float var2, float var3, float var4, Matrix3f var5);

    public Matrix3f rotateLocalX(float var1, Matrix3f var2);

    public Matrix3f rotateLocalY(float var1, Matrix3f var2);

    public Matrix3f rotateLocalZ(float var1, Matrix3f var2);

    public Matrix3f rotate(Quaternionfc var1, Matrix3f var2);

    public Matrix3f rotateLocal(Quaternionfc var1, Matrix3f var2);

    public Matrix3f rotate(AxisAngle4f var1, Matrix3f var2);

    public Matrix3f rotate(float var1, Vector3fc var2, Matrix3f var3);

    public Matrix3f lookAlong(Vector3fc var1, Vector3fc var2, Matrix3f var3);

    public Matrix3f lookAlong(float var1, float var2, float var3, float var4, float var5, float var6, Matrix3f var7);

    public Vector3f getRow(int var1, Vector3f var2) throws IndexOutOfBoundsException;

    public Vector3f getColumn(int var1, Vector3f var2) throws IndexOutOfBoundsException;

    public float get(int var1, int var2);

    public float getRowColumn(int var1, int var2);

    public Matrix3f normal(Matrix3f var1);

    public Matrix3f cofactor(Matrix3f var1);

    public Vector3f getScale(Vector3f var1);

    public Vector3f positiveZ(Vector3f var1);

    public Vector3f normalizedPositiveZ(Vector3f var1);

    public Vector3f positiveX(Vector3f var1);

    public Vector3f normalizedPositiveX(Vector3f var1);

    public Vector3f positiveY(Vector3f var1);

    public Vector3f normalizedPositiveY(Vector3f var1);

    public Matrix3f add(Matrix3fc var1, Matrix3f var2);

    public Matrix3f sub(Matrix3fc var1, Matrix3f var2);

    public Matrix3f mulComponentWise(Matrix3fc var1, Matrix3f var2);

    public Matrix3f lerp(Matrix3fc var1, float var2, Matrix3f var3);

    public Matrix3f rotateTowards(Vector3fc var1, Vector3fc var2, Matrix3f var3);

    public Matrix3f rotateTowards(float var1, float var2, float var3, float var4, float var5, float var6, Matrix3f var7);

    public Vector3f getEulerAnglesXYZ(Vector3f var1);

    public Vector3f getEulerAnglesZYX(Vector3f var1);

    public Vector3f getEulerAnglesYXZ(Vector3f var1);

    public Matrix3f obliqueZ(float var1, float var2, Matrix3f var3);

    public boolean equals(Matrix3fc var1, float var2);

    public Matrix3f reflect(float var1, float var2, float var3, Matrix3f var4);

    public Matrix3f reflect(Quaternionfc var1, Matrix3f var2);

    public Matrix3f reflect(Vector3fc var1, Matrix3f var2);

    public boolean isFinite();

    public float quadraticFormProduct(float var1, float var2, float var3);

    public float quadraticFormProduct(Vector3fc var1);

    public Matrix3f mapXZY(Matrix3f var1);

    public Matrix3f mapXZnY(Matrix3f var1);

    public Matrix3f mapXnYnZ(Matrix3f var1);

    public Matrix3f mapXnZY(Matrix3f var1);

    public Matrix3f mapXnZnY(Matrix3f var1);

    public Matrix3f mapYXZ(Matrix3f var1);

    public Matrix3f mapYXnZ(Matrix3f var1);

    public Matrix3f mapYZX(Matrix3f var1);

    public Matrix3f mapYZnX(Matrix3f var1);

    public Matrix3f mapYnXZ(Matrix3f var1);

    public Matrix3f mapYnXnZ(Matrix3f var1);

    public Matrix3f mapYnZX(Matrix3f var1);

    public Matrix3f mapYnZnX(Matrix3f var1);

    public Matrix3f mapZXY(Matrix3f var1);

    public Matrix3f mapZXnY(Matrix3f var1);

    public Matrix3f mapZYX(Matrix3f var1);

    public Matrix3f mapZYnX(Matrix3f var1);

    public Matrix3f mapZnXY(Matrix3f var1);

    public Matrix3f mapZnXnY(Matrix3f var1);

    public Matrix3f mapZnYX(Matrix3f var1);

    public Matrix3f mapZnYnX(Matrix3f var1);

    public Matrix3f mapnXYnZ(Matrix3f var1);

    public Matrix3f mapnXZY(Matrix3f var1);

    public Matrix3f mapnXZnY(Matrix3f var1);

    public Matrix3f mapnXnYZ(Matrix3f var1);

    public Matrix3f mapnXnYnZ(Matrix3f var1);

    public Matrix3f mapnXnZY(Matrix3f var1);

    public Matrix3f mapnXnZnY(Matrix3f var1);

    public Matrix3f mapnYXZ(Matrix3f var1);

    public Matrix3f mapnYXnZ(Matrix3f var1);

    public Matrix3f mapnYZX(Matrix3f var1);

    public Matrix3f mapnYZnX(Matrix3f var1);

    public Matrix3f mapnYnXZ(Matrix3f var1);

    public Matrix3f mapnYnXnZ(Matrix3f var1);

    public Matrix3f mapnYnZX(Matrix3f var1);

    public Matrix3f mapnYnZnX(Matrix3f var1);

    public Matrix3f mapnZXY(Matrix3f var1);

    public Matrix3f mapnZXnY(Matrix3f var1);

    public Matrix3f mapnZYX(Matrix3f var1);

    public Matrix3f mapnZYnX(Matrix3f var1);

    public Matrix3f mapnZnXY(Matrix3f var1);

    public Matrix3f mapnZnXnY(Matrix3f var1);

    public Matrix3f mapnZnYX(Matrix3f var1);

    public Matrix3f mapnZnYnX(Matrix3f var1);

    public Matrix3f negateX(Matrix3f var1);

    public Matrix3f negateY(Matrix3f var1);

    public Matrix3f negateZ(Matrix3f var1);
}

