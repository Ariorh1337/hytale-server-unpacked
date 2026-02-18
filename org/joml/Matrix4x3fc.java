/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.joml.AxisAngle4d;
import org.joml.AxisAngle4f;
import org.joml.Matrix3f;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Matrix4x3d;
import org.joml.Matrix4x3f;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

public interface Matrix4x3fc {
    public static final int PLANE_NX = 0;
    public static final int PLANE_PX = 1;
    public static final int PLANE_NY = 2;
    public static final int PLANE_PY = 3;
    public static final int PLANE_NZ = 4;
    public static final int PLANE_PZ = 5;
    public static final byte PROPERTY_UNKNOWN = 0;
    public static final byte PROPERTY_IDENTITY = 4;
    public static final byte PROPERTY_TRANSLATION = 8;
    public static final byte PROPERTY_ORTHONORMAL = 16;

    public int properties();

    public float m00();

    public float m01();

    public float m02();

    public float m10();

    public float m11();

    public float m12();

    public float m20();

    public float m21();

    public float m22();

    public float m30();

    public float m31();

    public float m32();

    public Matrix4f get(Matrix4f var1);

    public Matrix4d get(Matrix4d var1);

    public Matrix4x3f mul(Matrix4x3fc var1, Matrix4x3f var2);

    public Matrix4x3f mulTranslation(Matrix4x3fc var1, Matrix4x3f var2);

    public Matrix4x3f mulOrtho(Matrix4x3fc var1, Matrix4x3f var2);

    public Matrix4x3f mul3x3(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, Matrix4x3f var10);

    public Matrix4x3f fma(Matrix4x3fc var1, float var2, Matrix4x3f var3);

    public Matrix4x3f add(Matrix4x3fc var1, Matrix4x3f var2);

    public Matrix4x3f sub(Matrix4x3fc var1, Matrix4x3f var2);

    public Matrix4x3f mulComponentWise(Matrix4x3fc var1, Matrix4x3f var2);

    public float determinant();

    public Matrix4x3f invert(Matrix4x3f var1);

    public Matrix4f invert(Matrix4f var1);

    public Matrix4x3f invertOrtho(Matrix4x3f var1);

    public Matrix4x3f transpose3x3(Matrix4x3f var1);

    public Matrix3f transpose3x3(Matrix3f var1);

    public Vector3f getTranslation(Vector3f var1);

    public Vector3f getScale(Vector3f var1);

    public Matrix4x3f get(Matrix4x3f var1);

    public Matrix4x3d get(Matrix4x3d var1);

    public AxisAngle4f getRotation(AxisAngle4f var1);

    public AxisAngle4d getRotation(AxisAngle4d var1);

    public Quaternionf getUnnormalizedRotation(Quaternionf var1);

    public Quaternionf getNormalizedRotation(Quaternionf var1);

    public Quaterniond getUnnormalizedRotation(Quaterniond var1);

    public Quaterniond getNormalizedRotation(Quaterniond var1);

    public FloatBuffer get(FloatBuffer var1);

    public FloatBuffer get(int var1, FloatBuffer var2);

    public ByteBuffer get(ByteBuffer var1);

    public ByteBuffer get(int var1, ByteBuffer var2);

    public Matrix4x3fc getToAddress(long var1);

    public Matrix4x3fc getTransposedToAddress(long var1);

    public float[] get(float[] var1, int var2);

    public float[] get(float[] var1);

    public float[] get4x4(float[] var1, int var2);

    public float[] get4x4(float[] var1);

    public FloatBuffer get4x4(FloatBuffer var1);

    public FloatBuffer get4x4(int var1, FloatBuffer var2);

    public ByteBuffer get4x4(ByteBuffer var1);

    public ByteBuffer get4x4(int var1, ByteBuffer var2);

    public FloatBuffer get3x4(FloatBuffer var1);

    public FloatBuffer get3x4(int var1, FloatBuffer var2);

    public ByteBuffer get3x4(ByteBuffer var1);

    public ByteBuffer get3x4(int var1, ByteBuffer var2);

    public FloatBuffer getTransposed(FloatBuffer var1);

    public FloatBuffer getTransposed(int var1, FloatBuffer var2);

    public ByteBuffer getTransposed(ByteBuffer var1);

    public ByteBuffer getTransposed(int var1, ByteBuffer var2);

    public float[] getTransposed(float[] var1, int var2);

    public float[] getTransposed(float[] var1);

    public Vector4f transform(Vector4f var1);

    public Vector4f transform(Vector4fc var1, Vector4f var2);

    public Vector3f transformPosition(Vector3f var1);

    public Vector3f transformPosition(Vector3fc var1, Vector3f var2);

    public Vector3f transformDirection(Vector3f var1);

    public Vector3f transformDirection(Vector3fc var1, Vector3f var2);

    public Matrix4x3f scale(Vector3fc var1, Matrix4x3f var2);

    public Matrix4x3f scale(float var1, Matrix4x3f var2);

    public Matrix4x3f scaleXY(float var1, float var2, Matrix4x3f var3);

    public Matrix4x3f scaleAround(float var1, float var2, float var3, float var4, float var5, float var6, Matrix4x3f var7);

    public Matrix4x3f scaleAround(float var1, float var2, float var3, float var4, Matrix4x3f var5);

    public Matrix4x3f scale(float var1, float var2, float var3, Matrix4x3f var4);

    public Matrix4x3f scaleLocal(float var1, float var2, float var3, Matrix4x3f var4);

    public Matrix4x3f rotateX(float var1, Matrix4x3f var2);

    public Matrix4x3f rotateY(float var1, Matrix4x3f var2);

    public Matrix4x3f rotateZ(float var1, Matrix4x3f var2);

    public Matrix4x3f rotateXYZ(float var1, float var2, float var3, Matrix4x3f var4);

    public Matrix4x3f rotateZYX(float var1, float var2, float var3, Matrix4x3f var4);

    public Matrix4x3f rotateYXZ(float var1, float var2, float var3, Matrix4x3f var4);

    public Matrix4x3f rotate(float var1, float var2, float var3, float var4, Matrix4x3f var5);

    public Matrix4x3f rotateTranslation(float var1, float var2, float var3, float var4, Matrix4x3f var5);

    public Matrix4x3f rotateAround(Quaternionfc var1, float var2, float var3, float var4, Matrix4x3f var5);

    public Matrix4x3f rotateLocal(float var1, float var2, float var3, float var4, Matrix4x3f var5);

    public Matrix4x3f translate(Vector3fc var1, Matrix4x3f var2);

    public Matrix4x3f translate(float var1, float var2, float var3, Matrix4x3f var4);

    public Matrix4x3f translateLocal(Vector3fc var1, Matrix4x3f var2);

    public Matrix4x3f translateLocal(float var1, float var2, float var3, Matrix4x3f var4);

    public Matrix4x3f ortho(float var1, float var2, float var3, float var4, float var5, float var6, boolean var7, Matrix4x3f var8);

    public Matrix4x3f ortho(float var1, float var2, float var3, float var4, float var5, float var6, Matrix4x3f var7);

    public Matrix4x3f orthoLH(float var1, float var2, float var3, float var4, float var5, float var6, boolean var7, Matrix4x3f var8);

    public Matrix4x3f orthoLH(float var1, float var2, float var3, float var4, float var5, float var6, Matrix4x3f var7);

    public Matrix4x3f orthoSymmetric(float var1, float var2, float var3, float var4, boolean var5, Matrix4x3f var6);

    public Matrix4x3f orthoSymmetric(float var1, float var2, float var3, float var4, Matrix4x3f var5);

    public Matrix4x3f orthoSymmetricLH(float var1, float var2, float var3, float var4, boolean var5, Matrix4x3f var6);

    public Matrix4x3f orthoSymmetricLH(float var1, float var2, float var3, float var4, Matrix4x3f var5);

    public Matrix4x3f ortho2D(float var1, float var2, float var3, float var4, Matrix4x3f var5);

    public Matrix4x3f ortho2DLH(float var1, float var2, float var3, float var4, Matrix4x3f var5);

    public Matrix4x3f lookAlong(Vector3fc var1, Vector3fc var2, Matrix4x3f var3);

    public Matrix4x3f lookAlong(float var1, float var2, float var3, float var4, float var5, float var6, Matrix4x3f var7);

    public Matrix4x3f lookAt(Vector3fc var1, Vector3fc var2, Vector3fc var3, Matrix4x3f var4);

    public Matrix4x3f lookAt(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, Matrix4x3f var10);

    public Matrix4x3f lookAtLH(Vector3fc var1, Vector3fc var2, Vector3fc var3, Matrix4x3f var4);

    public Matrix4x3f lookAtLH(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, Matrix4x3f var10);

    public Matrix4x3f rotate(Quaternionfc var1, Matrix4x3f var2);

    public Matrix4x3f rotateTranslation(Quaternionfc var1, Matrix4x3f var2);

    public Matrix4x3f rotateLocal(Quaternionfc var1, Matrix4x3f var2);

    public Matrix4x3f rotate(AxisAngle4f var1, Matrix4x3f var2);

    public Matrix4x3f rotate(float var1, Vector3fc var2, Matrix4x3f var3);

    public Matrix4x3f reflect(float var1, float var2, float var3, float var4, Matrix4x3f var5);

    public Matrix4x3f reflect(float var1, float var2, float var3, float var4, float var5, float var6, Matrix4x3f var7);

    public Matrix4x3f reflect(Quaternionfc var1, Vector3fc var2, Matrix4x3f var3);

    public Matrix4x3f reflect(Vector3fc var1, Vector3fc var2, Matrix4x3f var3);

    public Vector4f getRow(int var1, Vector4f var2) throws IndexOutOfBoundsException;

    public Vector3f getColumn(int var1, Vector3f var2) throws IndexOutOfBoundsException;

    public Matrix4x3f normal(Matrix4x3f var1);

    public Matrix3f normal(Matrix3f var1);

    public Matrix3f cofactor3x3(Matrix3f var1);

    public Matrix4x3f cofactor3x3(Matrix4x3f var1);

    public Matrix4x3f normalize3x3(Matrix4x3f var1);

    public Matrix3f normalize3x3(Matrix3f var1);

    public Vector4f frustumPlane(int var1, Vector4f var2);

    public Vector3f positiveZ(Vector3f var1);

    public Vector3f normalizedPositiveZ(Vector3f var1);

    public Vector3f positiveX(Vector3f var1);

    public Vector3f normalizedPositiveX(Vector3f var1);

    public Vector3f positiveY(Vector3f var1);

    public Vector3f normalizedPositiveY(Vector3f var1);

    public Vector3f origin(Vector3f var1);

    public Matrix4x3f shadow(Vector4fc var1, float var2, float var3, float var4, float var5, Matrix4x3f var6);

    public Matrix4x3f shadow(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, Matrix4x3f var9);

    public Matrix4x3f shadow(Vector4fc var1, Matrix4x3fc var2, Matrix4x3f var3);

    public Matrix4x3f shadow(float var1, float var2, float var3, float var4, Matrix4x3fc var5, Matrix4x3f var6);

    public Matrix4x3f pick(float var1, float var2, float var3, float var4, int[] var5, Matrix4x3f var6);

    public Matrix4x3f arcball(float var1, float var2, float var3, float var4, float var5, float var6, Matrix4x3f var7);

    public Matrix4x3f arcball(float var1, Vector3fc var2, float var3, float var4, Matrix4x3f var5);

    public Matrix4x3f transformAab(float var1, float var2, float var3, float var4, float var5, float var6, Vector3f var7, Vector3f var8);

    public Matrix4x3f transformAab(Vector3fc var1, Vector3fc var2, Vector3f var3, Vector3f var4);

    public Matrix4x3f lerp(Matrix4x3fc var1, float var2, Matrix4x3f var3);

    public Matrix4x3f rotateTowards(Vector3fc var1, Vector3fc var2, Matrix4x3f var3);

    public Matrix4x3f rotateTowards(float var1, float var2, float var3, float var4, float var5, float var6, Matrix4x3f var7);

    public Vector3f getEulerAnglesXYZ(Vector3f var1);

    public Vector3f getEulerAnglesZYX(Vector3f var1);

    public Vector3f getEulerAnglesYXZ(Vector3f var1);

    public Matrix4x3f obliqueZ(float var1, float var2, Matrix4x3f var3);

    public Matrix4x3f withLookAtUp(Vector3fc var1, Matrix4x3f var2);

    public Matrix4x3f withLookAtUp(float var1, float var2, float var3, Matrix4x3f var4);

    public Matrix4x3f mapXZY(Matrix4x3f var1);

    public Matrix4x3f mapXZnY(Matrix4x3f var1);

    public Matrix4x3f mapXnYnZ(Matrix4x3f var1);

    public Matrix4x3f mapXnZY(Matrix4x3f var1);

    public Matrix4x3f mapXnZnY(Matrix4x3f var1);

    public Matrix4x3f mapYXZ(Matrix4x3f var1);

    public Matrix4x3f mapYXnZ(Matrix4x3f var1);

    public Matrix4x3f mapYZX(Matrix4x3f var1);

    public Matrix4x3f mapYZnX(Matrix4x3f var1);

    public Matrix4x3f mapYnXZ(Matrix4x3f var1);

    public Matrix4x3f mapYnXnZ(Matrix4x3f var1);

    public Matrix4x3f mapYnZX(Matrix4x3f var1);

    public Matrix4x3f mapYnZnX(Matrix4x3f var1);

    public Matrix4x3f mapZXY(Matrix4x3f var1);

    public Matrix4x3f mapZXnY(Matrix4x3f var1);

    public Matrix4x3f mapZYX(Matrix4x3f var1);

    public Matrix4x3f mapZYnX(Matrix4x3f var1);

    public Matrix4x3f mapZnXY(Matrix4x3f var1);

    public Matrix4x3f mapZnXnY(Matrix4x3f var1);

    public Matrix4x3f mapZnYX(Matrix4x3f var1);

    public Matrix4x3f mapZnYnX(Matrix4x3f var1);

    public Matrix4x3f mapnXYnZ(Matrix4x3f var1);

    public Matrix4x3f mapnXZY(Matrix4x3f var1);

    public Matrix4x3f mapnXZnY(Matrix4x3f var1);

    public Matrix4x3f mapnXnYZ(Matrix4x3f var1);

    public Matrix4x3f mapnXnYnZ(Matrix4x3f var1);

    public Matrix4x3f mapnXnZY(Matrix4x3f var1);

    public Matrix4x3f mapnXnZnY(Matrix4x3f var1);

    public Matrix4x3f mapnYXZ(Matrix4x3f var1);

    public Matrix4x3f mapnYXnZ(Matrix4x3f var1);

    public Matrix4x3f mapnYZX(Matrix4x3f var1);

    public Matrix4x3f mapnYZnX(Matrix4x3f var1);

    public Matrix4x3f mapnYnXZ(Matrix4x3f var1);

    public Matrix4x3f mapnYnXnZ(Matrix4x3f var1);

    public Matrix4x3f mapnYnZX(Matrix4x3f var1);

    public Matrix4x3f mapnYnZnX(Matrix4x3f var1);

    public Matrix4x3f mapnZXY(Matrix4x3f var1);

    public Matrix4x3f mapnZXnY(Matrix4x3f var1);

    public Matrix4x3f mapnZYX(Matrix4x3f var1);

    public Matrix4x3f mapnZYnX(Matrix4x3f var1);

    public Matrix4x3f mapnZnXY(Matrix4x3f var1);

    public Matrix4x3f mapnZnXnY(Matrix4x3f var1);

    public Matrix4x3f mapnZnYX(Matrix4x3f var1);

    public Matrix4x3f mapnZnYnX(Matrix4x3f var1);

    public Matrix4x3f negateX(Matrix4x3f var1);

    public Matrix4x3f negateY(Matrix4x3f var1);

    public Matrix4x3f negateZ(Matrix4x3f var1);

    public boolean equals(Matrix4x3fc var1, float var2);

    public boolean isFinite();
}

