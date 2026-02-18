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
import org.joml.Matrix4d;
import org.joml.Matrix4x3d;
import org.joml.Matrix4x3fc;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3fc;
import org.joml.Vector4d;
import org.joml.Vector4dc;

public interface Matrix4x3dc {
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

    public double m00();

    public double m01();

    public double m02();

    public double m10();

    public double m11();

    public double m12();

    public double m20();

    public double m21();

    public double m22();

    public double m30();

    public double m31();

    public double m32();

    public Matrix4d get(Matrix4d var1);

    public Matrix4x3d mul(Matrix4x3dc var1, Matrix4x3d var2);

    public Matrix4x3d mul(Matrix4x3fc var1, Matrix4x3d var2);

    public Matrix4x3d mulTranslation(Matrix4x3dc var1, Matrix4x3d var2);

    public Matrix4x3d mulTranslation(Matrix4x3fc var1, Matrix4x3d var2);

    public Matrix4x3d mulOrtho(Matrix4x3dc var1, Matrix4x3d var2);

    public Matrix4x3d mul3x3(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, Matrix4x3d var19);

    public Matrix4x3d fma(Matrix4x3dc var1, double var2, Matrix4x3d var4);

    public Matrix4x3d fma(Matrix4x3fc var1, double var2, Matrix4x3d var4);

    public Matrix4x3d add(Matrix4x3dc var1, Matrix4x3d var2);

    public Matrix4x3d add(Matrix4x3fc var1, Matrix4x3d var2);

    public Matrix4x3d sub(Matrix4x3dc var1, Matrix4x3d var2);

    public Matrix4x3d sub(Matrix4x3fc var1, Matrix4x3d var2);

    public Matrix4x3d mulComponentWise(Matrix4x3dc var1, Matrix4x3d var2);

    public double determinant();

    public Matrix4x3d invert(Matrix4x3d var1);

    public Matrix4x3d invertOrtho(Matrix4x3d var1);

    public Matrix4x3d transpose3x3(Matrix4x3d var1);

    public Matrix3d transpose3x3(Matrix3d var1);

    public Vector3d getTranslation(Vector3d var1);

    public Vector3d getScale(Vector3d var1);

    public Matrix4x3d get(Matrix4x3d var1);

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

    public ByteBuffer getFloats(ByteBuffer var1);

    public ByteBuffer getFloats(int var1, ByteBuffer var2);

    public Matrix4x3dc getToAddress(long var1);

    public Matrix4x3dc getTransposedToAddress(long var1);

    public double[] get(double[] var1, int var2);

    public double[] get(double[] var1);

    public float[] get(float[] var1, int var2);

    public float[] get(float[] var1);

    public double[] get4x4(double[] var1, int var2);

    public double[] get4x4(double[] var1);

    public float[] get4x4(float[] var1, int var2);

    public float[] get4x4(float[] var1);

    public DoubleBuffer get4x4(DoubleBuffer var1);

    public DoubleBuffer get4x4(int var1, DoubleBuffer var2);

    public ByteBuffer get4x4(ByteBuffer var1);

    public ByteBuffer get4x4(int var1, ByteBuffer var2);

    public DoubleBuffer getTransposed(DoubleBuffer var1);

    public DoubleBuffer getTransposed(int var1, DoubleBuffer var2);

    public ByteBuffer getTransposed(ByteBuffer var1);

    public ByteBuffer getTransposed(int var1, ByteBuffer var2);

    public FloatBuffer getTransposed(FloatBuffer var1);

    public FloatBuffer getTransposed(int var1, FloatBuffer var2);

    public ByteBuffer getTransposedFloats(ByteBuffer var1);

    public ByteBuffer getTransposedFloats(int var1, ByteBuffer var2);

    public double[] getTransposed(double[] var1, int var2);

    public double[] getTransposed(double[] var1);

    public Vector4d transform(Vector4d var1);

    public Vector4d transform(Vector4dc var1, Vector4d var2);

    public Vector3d transformPosition(Vector3d var1);

    public Vector3d transformPosition(Vector3dc var1, Vector3d var2);

    public Vector3d transformDirection(Vector3d var1);

    public Vector3d transformDirection(Vector3dc var1, Vector3d var2);

    public Matrix4x3d scale(Vector3dc var1, Matrix4x3d var2);

    public Matrix4x3d scale(double var1, double var3, double var5, Matrix4x3d var7);

    public Matrix4x3d scale(double var1, Matrix4x3d var3);

    public Matrix4x3d scaleXY(double var1, double var3, Matrix4x3d var5);

    public Matrix4x3d scaleAround(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4x3d var13);

    public Matrix4x3d scaleAround(double var1, double var3, double var5, double var7, Matrix4x3d var9);

    public Matrix4x3d scaleLocal(double var1, double var3, double var5, Matrix4x3d var7);

    public Matrix4x3d rotate(double var1, double var3, double var5, double var7, Matrix4x3d var9);

    public Matrix4x3d rotateTranslation(double var1, double var3, double var5, double var7, Matrix4x3d var9);

    public Matrix4x3d rotateAround(Quaterniondc var1, double var2, double var4, double var6, Matrix4x3d var8);

    public Matrix4x3d rotateLocal(double var1, double var3, double var5, double var7, Matrix4x3d var9);

    public Matrix4x3d translate(Vector3dc var1, Matrix4x3d var2);

    public Matrix4x3d translate(Vector3fc var1, Matrix4x3d var2);

    public Matrix4x3d translate(double var1, double var3, double var5, Matrix4x3d var7);

    public Matrix4x3d translateLocal(Vector3fc var1, Matrix4x3d var2);

    public Matrix4x3d translateLocal(Vector3dc var1, Matrix4x3d var2);

    public Matrix4x3d translateLocal(double var1, double var3, double var5, Matrix4x3d var7);

    public Matrix4x3d rotateX(double var1, Matrix4x3d var3);

    public Matrix4x3d rotateY(double var1, Matrix4x3d var3);

    public Matrix4x3d rotateZ(double var1, Matrix4x3d var3);

    public Matrix4x3d rotateXYZ(double var1, double var3, double var5, Matrix4x3d var7);

    public Matrix4x3d rotateZYX(double var1, double var3, double var5, Matrix4x3d var7);

    public Matrix4x3d rotateYXZ(double var1, double var3, double var5, Matrix4x3d var7);

    public Matrix4x3d rotate(Quaterniondc var1, Matrix4x3d var2);

    public Matrix4x3d rotate(Quaternionfc var1, Matrix4x3d var2);

    public Matrix4x3d rotateTranslation(Quaterniondc var1, Matrix4x3d var2);

    public Matrix4x3d rotateTranslation(Quaternionfc var1, Matrix4x3d var2);

    public Matrix4x3d rotateLocal(Quaterniondc var1, Matrix4x3d var2);

    public Matrix4x3d rotateLocal(Quaternionfc var1, Matrix4x3d var2);

    public Matrix4x3d rotate(AxisAngle4f var1, Matrix4x3d var2);

    public Matrix4x3d rotate(AxisAngle4d var1, Matrix4x3d var2);

    public Matrix4x3d rotate(double var1, Vector3dc var3, Matrix4x3d var4);

    public Matrix4x3d rotate(double var1, Vector3fc var3, Matrix4x3d var4);

    public Vector4d getRow(int var1, Vector4d var2) throws IndexOutOfBoundsException;

    public Vector3d getColumn(int var1, Vector3d var2) throws IndexOutOfBoundsException;

    public Matrix4x3d normal(Matrix4x3d var1);

    public Matrix3d normal(Matrix3d var1);

    public Matrix3d cofactor3x3(Matrix3d var1);

    public Matrix4x3d cofactor3x3(Matrix4x3d var1);

    public Matrix4x3d normalize3x3(Matrix4x3d var1);

    public Matrix3d normalize3x3(Matrix3d var1);

    public Matrix4x3d reflect(double var1, double var3, double var5, double var7, Matrix4x3d var9);

    public Matrix4x3d reflect(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4x3d var13);

    public Matrix4x3d reflect(Quaterniondc var1, Vector3dc var2, Matrix4x3d var3);

    public Matrix4x3d reflect(Vector3dc var1, Vector3dc var2, Matrix4x3d var3);

    public Matrix4x3d ortho(double var1, double var3, double var5, double var7, double var9, double var11, boolean var13, Matrix4x3d var14);

    public Matrix4x3d ortho(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4x3d var13);

    public Matrix4x3d orthoLH(double var1, double var3, double var5, double var7, double var9, double var11, boolean var13, Matrix4x3d var14);

    public Matrix4x3d orthoLH(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4x3d var13);

    public Matrix4x3d orthoSymmetric(double var1, double var3, double var5, double var7, boolean var9, Matrix4x3d var10);

    public Matrix4x3d orthoSymmetric(double var1, double var3, double var5, double var7, Matrix4x3d var9);

    public Matrix4x3d orthoSymmetricLH(double var1, double var3, double var5, double var7, boolean var9, Matrix4x3d var10);

    public Matrix4x3d orthoSymmetricLH(double var1, double var3, double var5, double var7, Matrix4x3d var9);

    public Matrix4x3d ortho2D(double var1, double var3, double var5, double var7, Matrix4x3d var9);

    public Matrix4x3d ortho2DLH(double var1, double var3, double var5, double var7, Matrix4x3d var9);

    public Matrix4x3d lookAlong(Vector3dc var1, Vector3dc var2, Matrix4x3d var3);

    public Matrix4x3d lookAlong(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4x3d var13);

    public Matrix4x3d lookAt(Vector3dc var1, Vector3dc var2, Vector3dc var3, Matrix4x3d var4);

    public Matrix4x3d lookAt(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, Matrix4x3d var19);

    public Matrix4x3d lookAtLH(Vector3dc var1, Vector3dc var2, Vector3dc var3, Matrix4x3d var4);

    public Matrix4x3d lookAtLH(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, Matrix4x3d var19);

    public Vector4d frustumPlane(int var1, Vector4d var2);

    public Vector3d positiveZ(Vector3d var1);

    public Vector3d normalizedPositiveZ(Vector3d var1);

    public Vector3d positiveX(Vector3d var1);

    public Vector3d normalizedPositiveX(Vector3d var1);

    public Vector3d positiveY(Vector3d var1);

    public Vector3d normalizedPositiveY(Vector3d var1);

    public Vector3d origin(Vector3d var1);

    public Matrix4x3d shadow(Vector4dc var1, double var2, double var4, double var6, double var8, Matrix4x3d var10);

    public Matrix4x3d shadow(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, Matrix4x3d var17);

    public Matrix4x3d shadow(Vector4dc var1, Matrix4x3dc var2, Matrix4x3d var3);

    public Matrix4x3d shadow(double var1, double var3, double var5, double var7, Matrix4x3dc var9, Matrix4x3d var10);

    public Matrix4x3d pick(double var1, double var3, double var5, double var7, int[] var9, Matrix4x3d var10);

    public Matrix4x3d arcball(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4x3d var13);

    public Matrix4x3d arcball(double var1, Vector3dc var3, double var4, double var6, Matrix4x3d var8);

    public Matrix4x3d transformAab(double var1, double var3, double var5, double var7, double var9, double var11, Vector3d var13, Vector3d var14);

    public Matrix4x3d transformAab(Vector3dc var1, Vector3dc var2, Vector3d var3, Vector3d var4);

    public Matrix4x3d lerp(Matrix4x3dc var1, double var2, Matrix4x3d var4);

    public Matrix4x3d rotateTowards(Vector3dc var1, Vector3dc var2, Matrix4x3d var3);

    public Matrix4x3d rotateTowards(double var1, double var3, double var5, double var7, double var9, double var11, Matrix4x3d var13);

    public Vector3d getEulerAnglesXYZ(Vector3d var1);

    public Vector3d getEulerAnglesZYX(Vector3d var1);

    public Vector3d getEulerAnglesYXZ(Vector3d var1);

    public Matrix4x3d obliqueZ(double var1, double var3, Matrix4x3d var5);

    public Matrix4x3d mapXZY(Matrix4x3d var1);

    public Matrix4x3d mapXZnY(Matrix4x3d var1);

    public Matrix4x3d mapXnYnZ(Matrix4x3d var1);

    public Matrix4x3d mapXnZY(Matrix4x3d var1);

    public Matrix4x3d mapXnZnY(Matrix4x3d var1);

    public Matrix4x3d mapYXZ(Matrix4x3d var1);

    public Matrix4x3d mapYXnZ(Matrix4x3d var1);

    public Matrix4x3d mapYZX(Matrix4x3d var1);

    public Matrix4x3d mapYZnX(Matrix4x3d var1);

    public Matrix4x3d mapYnXZ(Matrix4x3d var1);

    public Matrix4x3d mapYnXnZ(Matrix4x3d var1);

    public Matrix4x3d mapYnZX(Matrix4x3d var1);

    public Matrix4x3d mapYnZnX(Matrix4x3d var1);

    public Matrix4x3d mapZXY(Matrix4x3d var1);

    public Matrix4x3d mapZXnY(Matrix4x3d var1);

    public Matrix4x3d mapZYX(Matrix4x3d var1);

    public Matrix4x3d mapZYnX(Matrix4x3d var1);

    public Matrix4x3d mapZnXY(Matrix4x3d var1);

    public Matrix4x3d mapZnXnY(Matrix4x3d var1);

    public Matrix4x3d mapZnYX(Matrix4x3d var1);

    public Matrix4x3d mapZnYnX(Matrix4x3d var1);

    public Matrix4x3d mapnXYnZ(Matrix4x3d var1);

    public Matrix4x3d mapnXZY(Matrix4x3d var1);

    public Matrix4x3d mapnXZnY(Matrix4x3d var1);

    public Matrix4x3d mapnXnYZ(Matrix4x3d var1);

    public Matrix4x3d mapnXnYnZ(Matrix4x3d var1);

    public Matrix4x3d mapnXnZY(Matrix4x3d var1);

    public Matrix4x3d mapnXnZnY(Matrix4x3d var1);

    public Matrix4x3d mapnYXZ(Matrix4x3d var1);

    public Matrix4x3d mapnYXnZ(Matrix4x3d var1);

    public Matrix4x3d mapnYZX(Matrix4x3d var1);

    public Matrix4x3d mapnYZnX(Matrix4x3d var1);

    public Matrix4x3d mapnYnXZ(Matrix4x3d var1);

    public Matrix4x3d mapnYnXnZ(Matrix4x3d var1);

    public Matrix4x3d mapnYnZX(Matrix4x3d var1);

    public Matrix4x3d mapnYnZnX(Matrix4x3d var1);

    public Matrix4x3d mapnZXY(Matrix4x3d var1);

    public Matrix4x3d mapnZXnY(Matrix4x3d var1);

    public Matrix4x3d mapnZYX(Matrix4x3d var1);

    public Matrix4x3d mapnZYnX(Matrix4x3d var1);

    public Matrix4x3d mapnZnXY(Matrix4x3d var1);

    public Matrix4x3d mapnZnXnY(Matrix4x3d var1);

    public Matrix4x3d mapnZnYX(Matrix4x3d var1);

    public Matrix4x3d mapnZnYnX(Matrix4x3d var1);

    public Matrix4x3d negateX(Matrix4x3d var1);

    public Matrix4x3d negateY(Matrix4x3d var1);

    public Matrix4x3d negateZ(Matrix4x3d var1);

    public boolean equals(Matrix4x3dc var1, double var2);

    public boolean isFinite();
}

