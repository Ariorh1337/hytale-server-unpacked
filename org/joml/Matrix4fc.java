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
import org.joml.Matrix3x2fc;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Matrix4x3f;
import org.joml.Matrix4x3fc;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

public interface Matrix4fc {
    public static final int PLANE_NX = 0;
    public static final int PLANE_PX = 1;
    public static final int PLANE_NY = 2;
    public static final int PLANE_PY = 3;
    public static final int PLANE_NZ = 4;
    public static final int PLANE_PZ = 5;
    public static final int CORNER_NXNYNZ = 0;
    public static final int CORNER_PXNYNZ = 1;
    public static final int CORNER_PXPYNZ = 2;
    public static final int CORNER_NXPYNZ = 3;
    public static final int CORNER_PXNYPZ = 4;
    public static final int CORNER_NXNYPZ = 5;
    public static final int CORNER_NXPYPZ = 6;
    public static final int CORNER_PXPYPZ = 7;
    public static final byte PROPERTY_UNKNOWN = 0;
    public static final byte PROPERTY_PERSPECTIVE = 1;
    public static final byte PROPERTY_AFFINE = 2;
    public static final byte PROPERTY_IDENTITY = 4;
    public static final byte PROPERTY_TRANSLATION = 8;
    public static final byte PROPERTY_ORTHONORMAL = 16;

    public int properties();

    public float m00();

    public float m01();

    public float m02();

    public float m03();

    public float m10();

    public float m11();

    public float m12();

    public float m13();

    public float m20();

    public float m21();

    public float m22();

    public float m23();

    public float m30();

    public float m31();

    public float m32();

    public float m33();

    public Matrix4f mul(Matrix4fc var1, Matrix4f var2);

    public Matrix4f mul0(Matrix4fc var1, Matrix4f var2);

    public Matrix4f mul(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14, float var15, float var16, Matrix4f var17);

    public Matrix4f mul3x3(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, Matrix4f var10);

    public Matrix4f mulLocal(Matrix4fc var1, Matrix4f var2);

    public Matrix4f mulLocalAffine(Matrix4fc var1, Matrix4f var2);

    public Matrix4f mul(Matrix3x2fc var1, Matrix4f var2);

    public Matrix4f mul(Matrix4x3fc var1, Matrix4f var2);

    public Matrix4f mulPerspectiveAffine(Matrix4fc var1, Matrix4f var2);

    public Matrix4f mulPerspectiveAffine(Matrix4x3fc var1, Matrix4f var2);

    public Matrix4f mulAffineR(Matrix4fc var1, Matrix4f var2);

    public Matrix4f mulAffine(Matrix4fc var1, Matrix4f var2);

    public Matrix4f mulTranslationAffine(Matrix4fc var1, Matrix4f var2);

    public Matrix4f mulOrthoAffine(Matrix4fc var1, Matrix4f var2);

    public Matrix4f fma4x3(Matrix4fc var1, float var2, Matrix4f var3);

    public Matrix4f add(Matrix4fc var1, Matrix4f var2);

    public Matrix4f sub(Matrix4fc var1, Matrix4f var2);

    public Matrix4f mulComponentWise(Matrix4fc var1, Matrix4f var2);

    public Matrix4f add4x3(Matrix4fc var1, Matrix4f var2);

    public Matrix4f sub4x3(Matrix4fc var1, Matrix4f var2);

    public Matrix4f mul4x3ComponentWise(Matrix4fc var1, Matrix4f var2);

    public float determinant();

    public float determinant3x3();

    public float determinantAffine();

    public Matrix4f invert(Matrix4f var1);

    public Matrix4f invertPerspective(Matrix4f var1);

    public Matrix4f invertFrustum(Matrix4f var1);

    public Matrix4f invertOrtho(Matrix4f var1);

    public Matrix4f invertPerspectiveView(Matrix4fc var1, Matrix4f var2);

    public Matrix4f invertPerspectiveView(Matrix4x3fc var1, Matrix4f var2);

    public Matrix4f invertAffine(Matrix4f var1);

    public Matrix4f transpose(Matrix4f var1);

    public Matrix4f transpose3x3(Matrix4f var1);

    public Matrix3f transpose3x3(Matrix3f var1);

    public Vector3f getTranslation(Vector3f var1);

    public Vector3f getScale(Vector3f var1);

    public Matrix4f get(Matrix4f var1);

    public Matrix4x3f get4x3(Matrix4x3f var1);

    public Matrix4d get(Matrix4d var1);

    public Matrix3f get3x3(Matrix3f var1);

    public Matrix3d get3x3(Matrix3d var1);

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

    public FloatBuffer get4x3(FloatBuffer var1);

    public FloatBuffer get4x3(int var1, FloatBuffer var2);

    public ByteBuffer get4x3(ByteBuffer var1);

    public ByteBuffer get4x3(int var1, ByteBuffer var2);

    public FloatBuffer get3x4(FloatBuffer var1);

    public FloatBuffer get3x4(int var1, FloatBuffer var2);

    public ByteBuffer get3x4(ByteBuffer var1);

    public ByteBuffer get3x4(int var1, ByteBuffer var2);

    public FloatBuffer getTransposed(FloatBuffer var1);

    public FloatBuffer getTransposed(int var1, FloatBuffer var2);

    public ByteBuffer getTransposed(ByteBuffer var1);

    public ByteBuffer getTransposed(int var1, ByteBuffer var2);

    public FloatBuffer get4x3Transposed(FloatBuffer var1);

    public FloatBuffer get4x3Transposed(int var1, FloatBuffer var2);

    public ByteBuffer get4x3Transposed(ByteBuffer var1);

    public ByteBuffer get4x3Transposed(int var1, ByteBuffer var2);

    public Matrix4fc getToAddress(long var1);

    public Matrix4fc getTransposedToAddress(long var1);

    public float[] get(float[] var1, int var2);

    public float[] get(float[] var1);

    public Vector4f transform(Vector4f var1);

    public Vector4f transform(Vector4fc var1, Vector4f var2);

    public Vector4f transform(float var1, float var2, float var3, float var4, Vector4f var5);

    public Vector4f transformTranspose(Vector4f var1);

    public Vector4f transformTranspose(Vector4fc var1, Vector4f var2);

    public Vector4f transformTranspose(float var1, float var2, float var3, float var4, Vector4f var5);

    public Vector4f transformProject(Vector4f var1);

    public Vector4f transformProject(Vector4fc var1, Vector4f var2);

    public Vector4f transformProject(float var1, float var2, float var3, float var4, Vector4f var5);

    public Vector3f transformProject(Vector3f var1);

    public Vector3f transformProject(Vector3fc var1, Vector3f var2);

    public Vector3f transformProject(Vector4fc var1, Vector3f var2);

    public Vector3f transformProject(float var1, float var2, float var3, Vector3f var4);

    public Vector3f transformProject(float var1, float var2, float var3, float var4, Vector3f var5);

    public Vector3f transformPosition(Vector3f var1);

    public Vector3f transformPosition(Vector3fc var1, Vector3f var2);

    public Vector3f transformPosition(float var1, float var2, float var3, Vector3f var4);

    public Vector3f transformDirection(Vector3f var1);

    public Vector3f transformDirection(Vector3fc var1, Vector3f var2);

    public Vector3f transformDirection(float var1, float var2, float var3, Vector3f var4);

    public Vector4f transformAffine(Vector4f var1);

    public Vector4f transformAffine(Vector4fc var1, Vector4f var2);

    public Vector4f transformAffine(float var1, float var2, float var3, float var4, Vector4f var5);

    public Matrix4f scale(Vector3fc var1, Matrix4f var2);

    public Matrix4f scale(float var1, Matrix4f var2);

    public Matrix4f scaleXY(float var1, float var2, Matrix4f var3);

    public Matrix4f scale(float var1, float var2, float var3, Matrix4f var4);

    public Matrix4f scaleAround(float var1, float var2, float var3, float var4, float var5, float var6, Matrix4f var7);

    public Matrix4f scaleAround(float var1, float var2, float var3, float var4, Matrix4f var5);

    public Matrix4f scaleLocal(float var1, Matrix4f var2);

    public Matrix4f scaleLocal(float var1, float var2, float var3, Matrix4f var4);

    public Matrix4f scaleAroundLocal(float var1, float var2, float var3, float var4, float var5, float var6, Matrix4f var7);

    public Matrix4f scaleAroundLocal(float var1, float var2, float var3, float var4, Matrix4f var5);

    public Matrix4f rotateX(float var1, Matrix4f var2);

    public Matrix4f rotateY(float var1, Matrix4f var2);

    public Matrix4f rotateZ(float var1, Matrix4f var2);

    public Matrix4f rotateTowardsXY(float var1, float var2, Matrix4f var3);

    public Matrix4f rotateXYZ(float var1, float var2, float var3, Matrix4f var4);

    public Matrix4f rotateAffineXYZ(float var1, float var2, float var3, Matrix4f var4);

    public Matrix4f rotateZYX(float var1, float var2, float var3, Matrix4f var4);

    public Matrix4f rotateAffineZYX(float var1, float var2, float var3, Matrix4f var4);

    public Matrix4f rotateYXZ(float var1, float var2, float var3, Matrix4f var4);

    public Matrix4f rotateAffineYXZ(float var1, float var2, float var3, Matrix4f var4);

    public Matrix4f rotate(float var1, float var2, float var3, float var4, Matrix4f var5);

    public Matrix4f rotateTranslation(float var1, float var2, float var3, float var4, Matrix4f var5);

    public Matrix4f rotateAffine(float var1, float var2, float var3, float var4, Matrix4f var5);

    public Matrix4f rotateLocal(float var1, float var2, float var3, float var4, Matrix4f var5);

    public Matrix4f rotateLocalX(float var1, Matrix4f var2);

    public Matrix4f rotateLocalY(float var1, Matrix4f var2);

    public Matrix4f rotateLocalZ(float var1, Matrix4f var2);

    public Matrix4f translate(Vector3fc var1, Matrix4f var2);

    public Matrix4f translate(float var1, float var2, float var3, Matrix4f var4);

    public Matrix4f translateLocal(Vector3fc var1, Matrix4f var2);

    public Matrix4f translateLocal(float var1, float var2, float var3, Matrix4f var4);

    public Matrix4f ortho(float var1, float var2, float var3, float var4, float var5, float var6, boolean var7, Matrix4f var8);

    public Matrix4f ortho(float var1, float var2, float var3, float var4, float var5, float var6, Matrix4f var7);

    public Matrix4f orthoLH(float var1, float var2, float var3, float var4, float var5, float var6, boolean var7, Matrix4f var8);

    public Matrix4f orthoLH(float var1, float var2, float var3, float var4, float var5, float var6, Matrix4f var7);

    public Matrix4f orthoSymmetric(float var1, float var2, float var3, float var4, boolean var5, Matrix4f var6);

    public Matrix4f orthoSymmetric(float var1, float var2, float var3, float var4, Matrix4f var5);

    public Matrix4f orthoSymmetricLH(float var1, float var2, float var3, float var4, boolean var5, Matrix4f var6);

    public Matrix4f orthoSymmetricLH(float var1, float var2, float var3, float var4, Matrix4f var5);

    public Matrix4f ortho2D(float var1, float var2, float var3, float var4, Matrix4f var5);

    public Matrix4f ortho2DLH(float var1, float var2, float var3, float var4, Matrix4f var5);

    public Matrix4f lookAlong(Vector3fc var1, Vector3fc var2, Matrix4f var3);

    public Matrix4f lookAlong(float var1, float var2, float var3, float var4, float var5, float var6, Matrix4f var7);

    public Matrix4f lookAt(Vector3fc var1, Vector3fc var2, Vector3fc var3, Matrix4f var4);

    public Matrix4f lookAt(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, Matrix4f var10);

    public Matrix4f lookAtPerspective(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, Matrix4f var10);

    public Matrix4f lookAtLH(Vector3fc var1, Vector3fc var2, Vector3fc var3, Matrix4f var4);

    public Matrix4f lookAtLH(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, Matrix4f var10);

    public Matrix4f lookAtPerspectiveLH(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, Matrix4f var10);

    public Matrix4f tile(int var1, int var2, int var3, int var4, Matrix4f var5);

    public Matrix4f perspective(float var1, float var2, float var3, float var4, boolean var5, Matrix4f var6);

    public Matrix4f perspective(float var1, float var2, float var3, float var4, Matrix4f var5);

    public Matrix4f perspectiveRect(float var1, float var2, float var3, float var4, boolean var5, Matrix4f var6);

    public Matrix4f perspectiveRect(float var1, float var2, float var3, float var4, Matrix4f var5);

    public Matrix4f perspectiveOffCenter(float var1, float var2, float var3, float var4, float var5, float var6, boolean var7, Matrix4f var8);

    public Matrix4f perspectiveOffCenter(float var1, float var2, float var3, float var4, float var5, float var6, Matrix4f var7);

    public Matrix4f perspectiveOffCenterFov(float var1, float var2, float var3, float var4, float var5, float var6, boolean var7, Matrix4f var8);

    public Matrix4f perspectiveOffCenterFov(float var1, float var2, float var3, float var4, float var5, float var6, Matrix4f var7);

    public Matrix4f perspectiveOffCenterFovLH(float var1, float var2, float var3, float var4, float var5, float var6, boolean var7, Matrix4f var8);

    public Matrix4f perspectiveOffCenterFovLH(float var1, float var2, float var3, float var4, float var5, float var6, Matrix4f var7);

    public Matrix4f perspectiveLH(float var1, float var2, float var3, float var4, boolean var5, Matrix4f var6);

    public Matrix4f perspectiveLH(float var1, float var2, float var3, float var4, Matrix4f var5);

    public Matrix4f frustum(float var1, float var2, float var3, float var4, float var5, float var6, boolean var7, Matrix4f var8);

    public Matrix4f frustum(float var1, float var2, float var3, float var4, float var5, float var6, Matrix4f var7);

    public Matrix4f frustumLH(float var1, float var2, float var3, float var4, float var5, float var6, boolean var7, Matrix4f var8);

    public Matrix4f frustumLH(float var1, float var2, float var3, float var4, float var5, float var6, Matrix4f var7);

    public Matrix4f rotate(Quaternionfc var1, Matrix4f var2);

    public Matrix4f rotateAffine(Quaternionfc var1, Matrix4f var2);

    public Matrix4f rotateTranslation(Quaternionfc var1, Matrix4f var2);

    public Matrix4f rotateAroundAffine(Quaternionfc var1, float var2, float var3, float var4, Matrix4f var5);

    public Matrix4f rotateAround(Quaternionfc var1, float var2, float var3, float var4, Matrix4f var5);

    public Matrix4f rotateLocal(Quaternionfc var1, Matrix4f var2);

    public Matrix4f rotateAroundLocal(Quaternionfc var1, float var2, float var3, float var4, Matrix4f var5);

    public Matrix4f rotate(AxisAngle4f var1, Matrix4f var2);

    public Matrix4f rotate(float var1, Vector3fc var2, Matrix4f var3);

    public Vector4f unproject(float var1, float var2, float var3, int[] var4, Vector4f var5);

    public Vector3f unproject(float var1, float var2, float var3, int[] var4, Vector3f var5);

    public Vector4f unproject(Vector3fc var1, int[] var2, Vector4f var3);

    public Vector3f unproject(Vector3fc var1, int[] var2, Vector3f var3);

    public Matrix4f unprojectRay(float var1, float var2, int[] var3, Vector3f var4, Vector3f var5);

    public Matrix4f unprojectRay(Vector2fc var1, int[] var2, Vector3f var3, Vector3f var4);

    public Vector4f unprojectInv(Vector3fc var1, int[] var2, Vector4f var3);

    public Vector4f unprojectInv(float var1, float var2, float var3, int[] var4, Vector4f var5);

    public Matrix4f unprojectInvRay(Vector2fc var1, int[] var2, Vector3f var3, Vector3f var4);

    public Matrix4f unprojectInvRay(float var1, float var2, int[] var3, Vector3f var4, Vector3f var5);

    public Vector3f unprojectInv(Vector3fc var1, int[] var2, Vector3f var3);

    public Vector3f unprojectInv(float var1, float var2, float var3, int[] var4, Vector3f var5);

    public Vector4f project(float var1, float var2, float var3, int[] var4, Vector4f var5);

    public Vector3f project(float var1, float var2, float var3, int[] var4, Vector3f var5);

    public Vector4f project(Vector3fc var1, int[] var2, Vector4f var3);

    public Vector3f project(Vector3fc var1, int[] var2, Vector3f var3);

    public Matrix4f reflect(float var1, float var2, float var3, float var4, Matrix4f var5);

    public Matrix4f reflect(float var1, float var2, float var3, float var4, float var5, float var6, Matrix4f var7);

    public Matrix4f reflect(Quaternionfc var1, Vector3fc var2, Matrix4f var3);

    public Matrix4f reflect(Vector3fc var1, Vector3fc var2, Matrix4f var3);

    public Vector4f getRow(int var1, Vector4f var2) throws IndexOutOfBoundsException;

    public Vector3f getRow(int var1, Vector3f var2) throws IndexOutOfBoundsException;

    public Vector4f getColumn(int var1, Vector4f var2) throws IndexOutOfBoundsException;

    public Vector3f getColumn(int var1, Vector3f var2) throws IndexOutOfBoundsException;

    public float get(int var1, int var2);

    public float getRowColumn(int var1, int var2);

    public Matrix4f normal(Matrix4f var1);

    public Matrix3f normal(Matrix3f var1);

    public Matrix3f cofactor3x3(Matrix3f var1);

    public Matrix4f cofactor3x3(Matrix4f var1);

    public Matrix4f normalize3x3(Matrix4f var1);

    public Matrix3f normalize3x3(Matrix3f var1);

    public Vector4f frustumPlane(int var1, Vector4f var2);

    public Vector3f frustumCorner(int var1, Vector3f var2);

    public Vector3f perspectiveOrigin(Vector3f var1);

    public Vector3f perspectiveInvOrigin(Vector3f var1);

    public float perspectiveFov();

    public float perspectiveNear();

    public float perspectiveFar();

    public Vector3f frustumRayDir(float var1, float var2, Vector3f var3);

    public Vector3f positiveZ(Vector3f var1);

    public Vector3f normalizedPositiveZ(Vector3f var1);

    public Vector3f positiveX(Vector3f var1);

    public Vector3f normalizedPositiveX(Vector3f var1);

    public Vector3f positiveY(Vector3f var1);

    public Vector3f normalizedPositiveY(Vector3f var1);

    public Vector3f originAffine(Vector3f var1);

    public Vector3f origin(Vector3f var1);

    public Matrix4f shadow(Vector4f var1, float var2, float var3, float var4, float var5, Matrix4f var6);

    public Matrix4f shadow(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, Matrix4f var9);

    public Matrix4f shadow(Vector4f var1, Matrix4fc var2, Matrix4f var3);

    public Matrix4f shadow(float var1, float var2, float var3, float var4, Matrix4fc var5, Matrix4f var6);

    public Matrix4f pick(float var1, float var2, float var3, float var4, int[] var5, Matrix4f var6);

    public boolean isAffine();

    public Matrix4f arcball(float var1, float var2, float var3, float var4, float var5, float var6, Matrix4f var7);

    public Matrix4f arcball(float var1, Vector3fc var2, float var3, float var4, Matrix4f var5);

    public Matrix4f frustumAabb(Vector3f var1, Vector3f var2);

    public Matrix4f projectedGridRange(Matrix4fc var1, float var2, float var3, Matrix4f var4);

    public Matrix4f perspectiveFrustumSlice(float var1, float var2, Matrix4f var3);

    public Matrix4f orthoCrop(Matrix4fc var1, Matrix4f var2);

    public Matrix4f transformAab(float var1, float var2, float var3, float var4, float var5, float var6, Vector3f var7, Vector3f var8);

    public Matrix4f transformAab(Vector3fc var1, Vector3fc var2, Vector3f var3, Vector3f var4);

    public Matrix4f lerp(Matrix4fc var1, float var2, Matrix4f var3);

    public Matrix4f rotateTowards(Vector3fc var1, Vector3fc var2, Matrix4f var3);

    public Matrix4f rotateTowards(float var1, float var2, float var3, float var4, float var5, float var6, Matrix4f var7);

    public Vector3f getEulerAnglesXYZ(Vector3f var1);

    public Vector3f getEulerAnglesZYX(Vector3f var1);

    public Vector3f getEulerAnglesYXZ(Vector3f var1);

    public boolean testPoint(float var1, float var2, float var3);

    public boolean testSphere(float var1, float var2, float var3, float var4);

    public boolean testAab(float var1, float var2, float var3, float var4, float var5, float var6);

    public Matrix4f obliqueZ(float var1, float var2, Matrix4f var3);

    public Matrix4f withLookAtUp(Vector3fc var1, Matrix4f var2);

    public Matrix4f withLookAtUp(float var1, float var2, float var3, Matrix4f var4);

    public Matrix4f mapXZY(Matrix4f var1);

    public Matrix4f mapXZnY(Matrix4f var1);

    public Matrix4f mapXnYnZ(Matrix4f var1);

    public Matrix4f mapXnZY(Matrix4f var1);

    public Matrix4f mapXnZnY(Matrix4f var1);

    public Matrix4f mapYXZ(Matrix4f var1);

    public Matrix4f mapYXnZ(Matrix4f var1);

    public Matrix4f mapYZX(Matrix4f var1);

    public Matrix4f mapYZnX(Matrix4f var1);

    public Matrix4f mapYnXZ(Matrix4f var1);

    public Matrix4f mapYnXnZ(Matrix4f var1);

    public Matrix4f mapYnZX(Matrix4f var1);

    public Matrix4f mapYnZnX(Matrix4f var1);

    public Matrix4f mapZXY(Matrix4f var1);

    public Matrix4f mapZXnY(Matrix4f var1);

    public Matrix4f mapZYX(Matrix4f var1);

    public Matrix4f mapZYnX(Matrix4f var1);

    public Matrix4f mapZnXY(Matrix4f var1);

    public Matrix4f mapZnXnY(Matrix4f var1);

    public Matrix4f mapZnYX(Matrix4f var1);

    public Matrix4f mapZnYnX(Matrix4f var1);

    public Matrix4f mapnXYnZ(Matrix4f var1);

    public Matrix4f mapnXZY(Matrix4f var1);

    public Matrix4f mapnXZnY(Matrix4f var1);

    public Matrix4f mapnXnYZ(Matrix4f var1);

    public Matrix4f mapnXnYnZ(Matrix4f var1);

    public Matrix4f mapnXnZY(Matrix4f var1);

    public Matrix4f mapnXnZnY(Matrix4f var1);

    public Matrix4f mapnYXZ(Matrix4f var1);

    public Matrix4f mapnYXnZ(Matrix4f var1);

    public Matrix4f mapnYZX(Matrix4f var1);

    public Matrix4f mapnYZnX(Matrix4f var1);

    public Matrix4f mapnYnXZ(Matrix4f var1);

    public Matrix4f mapnYnXnZ(Matrix4f var1);

    public Matrix4f mapnYnZX(Matrix4f var1);

    public Matrix4f mapnYnZnX(Matrix4f var1);

    public Matrix4f mapnZXY(Matrix4f var1);

    public Matrix4f mapnZXnY(Matrix4f var1);

    public Matrix4f mapnZYX(Matrix4f var1);

    public Matrix4f mapnZYnX(Matrix4f var1);

    public Matrix4f mapnZnXY(Matrix4f var1);

    public Matrix4f mapnZnXnY(Matrix4f var1);

    public Matrix4f mapnZnYX(Matrix4f var1);

    public Matrix4f mapnZnYnX(Matrix4f var1);

    public Matrix4f negateX(Matrix4f var1);

    public Matrix4f negateY(Matrix4f var1);

    public Matrix4f negateZ(Matrix4f var1);

    public boolean equals(Matrix4fc var1, float var2);

    public boolean isFinite();
}

