/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import org.joml.AxisAngle4d;
import org.joml.AxisAngle4f;
import org.joml.Matrix3d;
import org.joml.Matrix3x2dc;
import org.joml.Matrix3x2fc;
import org.joml.Matrix4d;
import org.joml.Matrix4fc;
import org.joml.Matrix4x3d;
import org.joml.Matrix4x3dc;
import org.joml.Matrix4x3fc;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector2dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4d;
import org.joml.Vector4dc;

public interface Matrix4dc {
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

    public double m00();

    public double m01();

    public double m02();

    public double m03();

    public double m10();

    public double m11();

    public double m12();

    public double m13();

    public double m20();

    public double m21();

    public double m22();

    public double m23();

    public double m30();

    public double m31();

    public double m32();

    public double m33();

    public Matrix4d mul(Matrix4dc var1, Matrix4d var2);

    public Matrix4d mul0(Matrix4dc var1, Matrix4d var2);

    public Matrix4d mul(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, double var19, double var21, double var23, double var25, double var27, double var29, double var31, Matrix4d var33);

    public Matrix4d mul3x3(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, Matrix4d var19);

    public Matrix4d mulLocal(Matrix4dc var1, Matrix4d var2);

    public Matrix4d mulLocalAffine(Matrix4dc var1, Matrix4d var2);

    public Matrix4d mul(Matrix3x2dc var1, Matrix4d var2);

    public Matrix4d mul(Matrix3x2fc var1, Matrix4d var2);

    public Matrix4d mul(Matrix4x3dc var1, Matrix4d var2);

    public Matrix4d mul(Matrix4x3fc var1, Matrix4d var2);

    public Matrix4d mul(Matrix4fc var1, Matrix4d var2);

    public Matrix4d mulPerspectiveAffine(Matrix4dc var1, Matrix4d var2);

    public Matrix4d mulPerspectiveAffine(Matrix4x3dc var1, Matrix4d var2);

    public Matrix4d mulAffineR(Matrix4dc var1, Matrix4d var2);

    public Matrix4d mulAffine(Matrix4dc var1, Matrix4d var2);

    public Matrix4d mulTranslationAffine(Matrix4dc var1, Matrix4d var2);

    public Matrix4d mulOrthoAffine(Matrix4dc var1, Matrix4d var2);

    public Matrix4d fma4x3(Matrix4dc var1, double var2, Matrix4d var4);

    public Matrix4d add(Matrix4dc var1, Matrix4d var2);

    public Matrix4d sub(Matrix4dc var1, Matrix4d var2);

    public Matrix4d mulComponentWise(Matrix4dc var1, Matrix4d var2);

    public Matrix4d add4x3(Matrix4dc var1, Matrix4d var2);

    public Matrix4d add4x3(Matrix4fc var1, Matrix4d var2);

    public Matrix4d sub4x3(Matrix4dc var1, Matrix4d var2);

    public Matrix4d mul4x3ComponentWise(Matrix4dc var1, Matrix4d var2);

    public double determinant();

    public double determinant3x3();

    public double determinantAffine();

    public Matrix4d invert(Matrix4d var1);

    public Matrix4d invertPerspective(Matrix4d var1);

    public Matrix4d invertFrustum(Matrix4d var1);

    public Matrix4d invertOrtho(Matrix4d var1);

    public Matrix4d invertPerspectiveView(Matrix4dc var1, Matrix4d var2);

    public Matrix4d invertPerspectiveView(Matrix4x3dc var1, Matrix4d var2);

    public Matrix4d invertAffine(Matrix4d var1);

    public Matrix4d transpose(Matrix4d var1);

    public Matrix4d transpose3x3(Matrix4d var1);

    public Matrix3d transpose3x3(Matrix3d var1);

    public Vector3d getTranslation(Vector3d var1);

    public Vector3d getScale(Vector3d var1);

    public Matrix4d get(Matrix4d var1);

    public Matrix4x3d get4x3(Matrix4x3d var1);

    public Matrix3d get3x3(Matrix3d var1);

    public Quaternionf getUnnormalizedRotation(Quaternionf var1);

    public Quaternionf getNormalizedRotation(Quaternionf var1);

    public Quaterniond getUnnormalizedRotation(Quaterniond var1);

    public Quaterniond getNormalizedRotation(Quaterniond var1);

    public DoubleBuffer get(DoubleBuffer var1);

    public DoubleBuffer get(int var1, DoubleBuffer var2);

    public FloatBuffer get(FloatBuffer var1);

    public FloatBuffer get(int var1, FloatBuffer var2);

    public ByteBuffer get(ByteBuffer var1);

    public ByteBuffer get(int var1, ByteBuffer var2);

    public Matrix4dc getToAddress(long var1);

    public Matrix4dc getTransposedToAddress(long var1);

    public ByteBuffer getFloats(ByteBuffer var1);

    public ByteBuffer getFloats(int var1, ByteBuffer var2);

    public double[] get(double[] var1, int var2);

    public double[] get(double[] var1);

    public float[] get(float[] var1, int var2);

    public float[] get(float[] var1);

    public DoubleBuffer getTransposed(DoubleBuffer var1);

    public DoubleBuffer getTransposed(int var1, DoubleBuffer var2);

    public ByteBuffer getTransposed(ByteBuffer var1);

    public ByteBuffer getTransposed(int var1, ByteBuffer var2);

    public FloatBuffer getTransposed(FloatBuffer var1);

    public FloatBuffer getTransposed(int var1, FloatBuffer var2);

    public ByteBuffer getTransposedFloats(ByteBuffer var1);

    public ByteBuffer getTransposedFloats(int var1, ByteBuffer var2);

    public DoubleBuffer get4x3Transposed(DoubleBuffer var1);

    public DoubleBuffer get4x3Transposed(int var1, DoubleBuffer var2);

    public ByteBuffer get4x3Transposed(ByteBuffer var1);

    public ByteBuffer get4x3Transposed(int var1, ByteBuffer var2);

    public Vector4d transform(Vector4d var1);

    public Vector4d transform(Vector4dc var1, Vector4d var2);

    public Vector4d transform(double var1, double var3, double var5, double var7, Vector4d var9);

    public Vector4d transformTranspose(Vector4d var1);

    public Vector4d transformTranspose(Vector4dc var1, Vector4d var2);

    public Vector4d transformTranspose(double var1, double var3, double var5, double var7, Vector4d var9);

    public Vector4d transformProject(Vector4d var1);

    public Vector4d transformProject(Vector4dc var1, Vector4d var2);

    public Vector3d transformProject(Vector4dc var1, Vector3d var2);

    public Vector4d transformProject(double var1, double var3, double var5, double var7, Vector4d var9);

    public Vector3d transformProject(Vector3d var1);

    public Vector3d transformProject(Vector3dc var1, Vector3d var2);

    public Vector3d transformProject(double var1, double var3, double var5, Vector3d var7);

    public Vector3d transformProject(double var1, double var3, double var5, double var7, Vector3d var9);

    public Vector3d transformPosition(Vector3d var1);

    public Vector3d transformPosition(Vector3dc var1, Vector3d var2);

    public Vector3d transformPosition(double var1, double var3, double var5, Vector3d var7);

    public Vector3d transformDirection(Vector3d var1);

    public Vector3d transformDirection(Vector3dc var1, Vector3d var2);

    public Vector3f transformDirection(Vector3f var1);

    public Vector3f transformDirection(Vector3fc var1, Vector3f var2);

    public Vector3d transformDirection(double var1, double var3, double var5, Vector3d var7);

    public Vector3f transformDirection(double var1, double var3, double var5, Vector3f var7);

    public Vector4d transformAffine(Vector4d var1);

    public Vector4d transformAffine(Vector4dc var1, Vector4d var2);

    public Vector4d transformAffine(double var1, double var3, double var5, double var7, Vector4d var9);

    public Matrix4d scale(Vector3dc var1, Matrix4d var2);

    public Matrix4d scale(double var1, double var3, double var5, Matrix4d var7);

    public Matrix4d scale(double var1, Matrix4d var3);

    public Matrix4d scaleXY(double var1, double var3, Matrix4d var5);

    public Matrix4d scaleAround(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13);

    public Matrix4d scaleAround(double var1, double var3, double var5, double var7, Matrix4d var9);

    public Matrix4d scaleLocal(double var1, Matrix4d var3);

    public Matrix4d scaleLocal(double var1, double var3, double var5, Matrix4d var7);

    public Matrix4d scaleAroundLocal(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13);

    public Matrix4d scaleAroundLocal(double var1, double var3, double var5, double var7, Matrix4d var9);

    public Matrix4d rotate(double var1, double var3, double var5, double var7, Matrix4d var9);

    public Matrix4d rotateTranslation(double var1, double var3, double var5, double var7, Matrix4d var9);

    public Matrix4d rotateAffine(double var1, double var3, double var5, double var7, Matrix4d var9);

    public Matrix4d rotateAroundAffine(Quaterniondc var1, double var2, double var4, double var6, Matrix4d var8);

    public Matrix4d rotateAround(Quaterniondc var1, double var2, double var4, double var6, Matrix4d var8);

    public Matrix4d rotateLocal(double var1, double var3, double var5, double var7, Matrix4d var9);

    public Matrix4d rotateLocalX(double var1, Matrix4d var3);

    public Matrix4d rotateLocalY(double var1, Matrix4d var3);

    public Matrix4d rotateLocalZ(double var1, Matrix4d var3);

    public Matrix4d rotateAroundLocal(Quaterniondc var1, double var2, double var4, double var6, Matrix4d var8);

    public Matrix4d translate(Vector3dc var1, Matrix4d var2);

    public Matrix4d translate(Vector3fc var1, Matrix4d var2);

    public Matrix4d translate(double var1, double var3, double var5, Matrix4d var7);

    public Matrix4d translateLocal(Vector3fc var1, Matrix4d var2);

    public Matrix4d translateLocal(Vector3dc var1, Matrix4d var2);

    public Matrix4d translateLocal(double var1, double var3, double var5, Matrix4d var7);

    public Matrix4d rotateX(double var1, Matrix4d var3);

    public Matrix4d rotateY(double var1, Matrix4d var3);

    public Matrix4d rotateZ(double var1, Matrix4d var3);

    public Matrix4d rotateTowardsXY(double var1, double var3, Matrix4d var5);

    public Matrix4d rotateXYZ(double var1, double var3, double var5, Matrix4d var7);

    public Matrix4d rotateAffineXYZ(double var1, double var3, double var5, Matrix4d var7);

    public Matrix4d rotateZYX(double var1, double var3, double var5, Matrix4d var7);

    public Matrix4d rotateAffineZYX(double var1, double var3, double var5, Matrix4d var7);

    public Matrix4d rotateYXZ(double var1, double var3, double var5, Matrix4d var7);

    public Matrix4d rotateAffineYXZ(double var1, double var3, double var5, Matrix4d var7);

    public Matrix4d rotate(Quaterniondc var1, Matrix4d var2);

    public Matrix4d rotate(Quaternionfc var1, Matrix4d var2);

    public Matrix4d rotateAffine(Quaterniondc var1, Matrix4d var2);

    public Matrix4d rotateTranslation(Quaterniondc var1, Matrix4d var2);

    public Matrix4d rotateTranslation(Quaternionfc var1, Matrix4d var2);

    public Matrix4d rotateLocal(Quaterniondc var1, Matrix4d var2);

    public Matrix4d rotateAffine(Quaternionfc var1, Matrix4d var2);

    public Matrix4d rotateLocal(Quaternionfc var1, Matrix4d var2);

    public Matrix4d rotate(AxisAngle4f var1, Matrix4d var2);

    public Matrix4d rotate(AxisAngle4d var1, Matrix4d var2);

    public Matrix4d rotate(double var1, Vector3dc var3, Matrix4d var4);

    public Matrix4d rotate(double var1, Vector3fc var3, Matrix4d var4);

    public Vector4d getRow(int var1, Vector4d var2) throws IndexOutOfBoundsException;

    public Vector3d getRow(int var1, Vector3d var2) throws IndexOutOfBoundsException;

    public Vector4d getColumn(int var1, Vector4d var2) throws IndexOutOfBoundsException;

    public Vector3d getColumn(int var1, Vector3d var2) throws IndexOutOfBoundsException;

    public double get(int var1, int var2);

    public double getRowColumn(int var1, int var2);

    public Matrix4d normal(Matrix4d var1);

    public Matrix3d normal(Matrix3d var1);

    public Matrix3d cofactor3x3(Matrix3d var1);

    public Matrix4d cofactor3x3(Matrix4d var1);

    public Matrix4d normalize3x3(Matrix4d var1);

    public Matrix3d normalize3x3(Matrix3d var1);

    public Vector4d unproject(double var1, double var3, double var5, int[] var7, Vector4d var8);

    public Vector3d unproject(double var1, double var3, double var5, int[] var7, Vector3d var8);

    public Vector4d unproject(Vector3dc var1, int[] var2, Vector4d var3);

    public Vector3d unproject(Vector3dc var1, int[] var2, Vector3d var3);

    public Matrix4d unprojectRay(double var1, double var3, int[] var5, Vector3d var6, Vector3d var7);

    public Matrix4d unprojectRay(Vector2dc var1, int[] var2, Vector3d var3, Vector3d var4);

    public Vector4d unprojectInv(Vector3dc var1, int[] var2, Vector4d var3);

    public Vector4d unprojectInv(double var1, double var3, double var5, int[] var7, Vector4d var8);

    public Vector3d unprojectInv(Vector3dc var1, int[] var2, Vector3d var3);

    public Vector3d unprojectInv(double var1, double var3, double var5, int[] var7, Vector3d var8);

    public Matrix4d unprojectInvRay(Vector2dc var1, int[] var2, Vector3d var3, Vector3d var4);

    public Matrix4d unprojectInvRay(double var1, double var3, int[] var5, Vector3d var6, Vector3d var7);

    public Vector4d project(double var1, double var3, double var5, int[] var7, Vector4d var8);

    public Vector3d project(double var1, double var3, double var5, int[] var7, Vector3d var8);

    public Vector4d project(Vector3dc var1, int[] var2, Vector4d var3);

    public Vector3d project(Vector3dc var1, int[] var2, Vector3d var3);

    public Matrix4d reflect(double var1, double var3, double var5, double var7, Matrix4d var9);

    public Matrix4d reflect(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13);

    public Matrix4d reflect(Quaterniondc var1, Vector3dc var2, Matrix4d var3);

    public Matrix4d reflect(Vector3dc var1, Vector3dc var2, Matrix4d var3);

    public Matrix4d ortho(double var1, double var3, double var5, double var7, double var9, double var11, boolean var13, Matrix4d var14);

    public Matrix4d ortho(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13);

    public Matrix4d orthoLH(double var1, double var3, double var5, double var7, double var9, double var11, boolean var13, Matrix4d var14);

    public Matrix4d orthoLH(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13);

    public Matrix4d orthoSymmetric(double var1, double var3, double var5, double var7, boolean var9, Matrix4d var10);

    public Matrix4d orthoSymmetric(double var1, double var3, double var5, double var7, Matrix4d var9);

    public Matrix4d orthoSymmetricLH(double var1, double var3, double var5, double var7, boolean var9, Matrix4d var10);

    public Matrix4d orthoSymmetricLH(double var1, double var3, double var5, double var7, Matrix4d var9);

    public Matrix4d ortho2D(double var1, double var3, double var5, double var7, Matrix4d var9);

    public Matrix4d ortho2DLH(double var1, double var3, double var5, double var7, Matrix4d var9);

    public Matrix4d lookAlong(Vector3dc var1, Vector3dc var2, Matrix4d var3);

    public Matrix4d lookAlong(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13);

    public Matrix4d lookAt(Vector3dc var1, Vector3dc var2, Vector3dc var3, Matrix4d var4);

    public Matrix4d lookAt(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, Matrix4d var19);

    public Matrix4d lookAtPerspective(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, Matrix4d var19);

    public Matrix4d lookAtLH(Vector3dc var1, Vector3dc var2, Vector3dc var3, Matrix4d var4);

    public Matrix4d lookAtLH(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, Matrix4d var19);

    public Matrix4d lookAtPerspectiveLH(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, Matrix4d var19);

    public Matrix4d tile(int var1, int var2, int var3, int var4, Matrix4d var5);

    public Matrix4d perspective(double var1, double var3, double var5, double var7, boolean var9, Matrix4d var10);

    public Matrix4d perspective(double var1, double var3, double var5, double var7, Matrix4d var9);

    public Matrix4d perspectiveRect(double var1, double var3, double var5, double var7, boolean var9, Matrix4d var10);

    public Matrix4d perspectiveRect(double var1, double var3, double var5, double var7, Matrix4d var9);

    public Matrix4d perspectiveOffCenter(double var1, double var3, double var5, double var7, double var9, double var11, boolean var13, Matrix4d var14);

    public Matrix4d perspectiveOffCenter(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13);

    public Matrix4d perspectiveOffCenterFov(double var1, double var3, double var5, double var7, double var9, double var11, boolean var13, Matrix4d var14);

    public Matrix4d perspectiveOffCenterFov(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13);

    public Matrix4d perspectiveOffCenterFovLH(double var1, double var3, double var5, double var7, double var9, double var11, boolean var13, Matrix4d var14);

    public Matrix4d perspectiveOffCenterFovLH(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13);

    public Matrix4d perspectiveLH(double var1, double var3, double var5, double var7, boolean var9, Matrix4d var10);

    public Matrix4d perspectiveLH(double var1, double var3, double var5, double var7, Matrix4d var9);

    public Matrix4d frustum(double var1, double var3, double var5, double var7, double var9, double var11, boolean var13, Matrix4d var14);

    public Matrix4d frustum(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13);

    public Matrix4d frustumLH(double var1, double var3, double var5, double var7, double var9, double var11, boolean var13, Matrix4d var14);

    public Matrix4d frustumLH(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13);

    public Vector4d frustumPlane(int var1, Vector4d var2);

    public Vector3d frustumCorner(int var1, Vector3d var2);

    public Vector3d perspectiveOrigin(Vector3d var1);

    public Vector3d perspectiveInvOrigin(Vector3d var1);

    public double perspectiveFov();

    public double perspectiveNear();

    public double perspectiveFar();

    public Vector3d frustumRayDir(double var1, double var3, Vector3d var5);

    public Vector3d positiveZ(Vector3d var1);

    public Vector3d normalizedPositiveZ(Vector3d var1);

    public Vector3d positiveX(Vector3d var1);

    public Vector3d normalizedPositiveX(Vector3d var1);

    public Vector3d positiveY(Vector3d var1);

    public Vector3d normalizedPositiveY(Vector3d var1);

    public Vector3d originAffine(Vector3d var1);

    public Vector3d origin(Vector3d var1);

    public Matrix4d shadow(Vector4dc var1, double var2, double var4, double var6, double var8, Matrix4d var10);

    public Matrix4d shadow(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, Matrix4d var17);

    public Matrix4d shadow(Vector4dc var1, Matrix4dc var2, Matrix4d var3);

    public Matrix4d shadow(double var1, double var3, double var5, double var7, Matrix4dc var9, Matrix4d var10);

    public Matrix4d pick(double var1, double var3, double var5, double var7, int[] var9, Matrix4d var10);

    public boolean isAffine();

    public Matrix4d arcball(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13);

    public Matrix4d arcball(double var1, Vector3dc var3, double var4, double var6, Matrix4d var8);

    public Matrix4d projectedGridRange(Matrix4dc var1, double var2, double var4, Matrix4d var6);

    public Matrix4d perspectiveFrustumSlice(double var1, double var3, Matrix4d var5);

    public Matrix4d orthoCrop(Matrix4dc var1, Matrix4d var2);

    public Matrix4d transformAab(double var1, double var3, double var5, double var7, double var9, double var11, Vector3d var13, Vector3d var14);

    public Matrix4d transformAab(Vector3dc var1, Vector3dc var2, Vector3d var3, Vector3d var4);

    public Matrix4d lerp(Matrix4dc var1, double var2, Matrix4d var4);

    public Matrix4d rotateTowards(Vector3dc var1, Vector3dc var2, Matrix4d var3);

    public Matrix4d rotateTowards(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4d var13);

    public Vector3d getEulerAnglesXYZ(Vector3d var1);

    public Vector3d getEulerAnglesZYX(Vector3d var1);

    public Vector3d getEulerAnglesYXZ(Vector3d var1);

    public boolean testPoint(double var1, double var3, double var5);

    public boolean testSphere(double var1, double var3, double var5, double var7);

    public boolean testAab(double var1, double var3, double var5, double var7, double var9, double var11);

    public Matrix4d obliqueZ(double var1, double var3, Matrix4d var5);

    public Matrix4d withLookAtUp(Vector3dc var1, Matrix4d var2);

    public Matrix4d withLookAtUp(double var1, double var3, double var5, Matrix4d var7);

    public Matrix4d mapXZY(Matrix4d var1);

    public Matrix4d mapXZnY(Matrix4d var1);

    public Matrix4d mapXnYnZ(Matrix4d var1);

    public Matrix4d mapXnZY(Matrix4d var1);

    public Matrix4d mapXnZnY(Matrix4d var1);

    public Matrix4d mapYXZ(Matrix4d var1);

    public Matrix4d mapYXnZ(Matrix4d var1);

    public Matrix4d mapYZX(Matrix4d var1);

    public Matrix4d mapYZnX(Matrix4d var1);

    public Matrix4d mapYnXZ(Matrix4d var1);

    public Matrix4d mapYnXnZ(Matrix4d var1);

    public Matrix4d mapYnZX(Matrix4d var1);

    public Matrix4d mapYnZnX(Matrix4d var1);

    public Matrix4d mapZXY(Matrix4d var1);

    public Matrix4d mapZXnY(Matrix4d var1);

    public Matrix4d mapZYX(Matrix4d var1);

    public Matrix4d mapZYnX(Matrix4d var1);

    public Matrix4d mapZnXY(Matrix4d var1);

    public Matrix4d mapZnXnY(Matrix4d var1);

    public Matrix4d mapZnYX(Matrix4d var1);

    public Matrix4d mapZnYnX(Matrix4d var1);

    public Matrix4d mapnXYnZ(Matrix4d var1);

    public Matrix4d mapnXZY(Matrix4d var1);

    public Matrix4d mapnXZnY(Matrix4d var1);

    public Matrix4d mapnXnYZ(Matrix4d var1);

    public Matrix4d mapnXnYnZ(Matrix4d var1);

    public Matrix4d mapnXnZY(Matrix4d var1);

    public Matrix4d mapnXnZnY(Matrix4d var1);

    public Matrix4d mapnYXZ(Matrix4d var1);

    public Matrix4d mapnYXnZ(Matrix4d var1);

    public Matrix4d mapnYZX(Matrix4d var1);

    public Matrix4d mapnYZnX(Matrix4d var1);

    public Matrix4d mapnYnXZ(Matrix4d var1);

    public Matrix4d mapnYnXnZ(Matrix4d var1);

    public Matrix4d mapnYnZX(Matrix4d var1);

    public Matrix4d mapnYnZnX(Matrix4d var1);

    public Matrix4d mapnZXY(Matrix4d var1);

    public Matrix4d mapnZXnY(Matrix4d var1);

    public Matrix4d mapnZYX(Matrix4d var1);

    public Matrix4d mapnZYnX(Matrix4d var1);

    public Matrix4d mapnZnXY(Matrix4d var1);

    public Matrix4d mapnZnXnY(Matrix4d var1);

    public Matrix4d mapnZnYX(Matrix4d var1);

    public Matrix4d mapnZnYnX(Matrix4d var1);

    public Matrix4d negateX(Matrix4d var1);

    public Matrix4d negateY(Matrix4d var1);

    public Matrix4d negateZ(Matrix4d var1);

    public boolean equals(Matrix4dc var1, double var2);

    public boolean isFinite();
}

