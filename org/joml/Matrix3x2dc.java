/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import org.joml.Matrix3x2d;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector2fc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public interface Matrix3x2dc {
    public double m00();

    public double m01();

    public double m10();

    public double m11();

    public double m20();

    public double m21();

    public Matrix3x2d mul(Matrix3x2dc var1, Matrix3x2d var2);

    public Matrix3x2d mulLocal(Matrix3x2dc var1, Matrix3x2d var2);

    public double determinant();

    public Matrix3x2d invert(Matrix3x2d var1);

    public Matrix3x2d translate(double var1, double var3, Matrix3x2d var5);

    public Matrix3x2d translate(Vector2dc var1, Matrix3x2d var2);

    public Matrix3x2d translateLocal(Vector2dc var1, Matrix3x2d var2);

    public Matrix3x2d translateLocal(double var1, double var3, Matrix3x2d var5);

    public Matrix3x2d get(Matrix3x2d var1);

    public DoubleBuffer get(DoubleBuffer var1);

    public DoubleBuffer get(int var1, DoubleBuffer var2);

    public ByteBuffer get(ByteBuffer var1);

    public ByteBuffer get(int var1, ByteBuffer var2);

    public DoubleBuffer getTransposed(DoubleBuffer var1);

    public DoubleBuffer getTransposed(int var1, DoubleBuffer var2);

    public ByteBuffer getTransposed(ByteBuffer var1);

    public ByteBuffer getTransposed(int var1, ByteBuffer var2);

    public FloatBuffer getTransposed(FloatBuffer var1);

    public FloatBuffer getTransposed(int var1, FloatBuffer var2);

    public ByteBuffer getTransposedFloats(ByteBuffer var1);

    public ByteBuffer getTransposedFloats(int var1, ByteBuffer var2);

    public DoubleBuffer get3x3(DoubleBuffer var1);

    public DoubleBuffer get3x3(int var1, DoubleBuffer var2);

    public ByteBuffer get3x3(ByteBuffer var1);

    public ByteBuffer get3x3(int var1, ByteBuffer var2);

    public DoubleBuffer get4x4(DoubleBuffer var1);

    public DoubleBuffer get4x4(int var1, DoubleBuffer var2);

    public ByteBuffer get4x4(ByteBuffer var1);

    public ByteBuffer get4x4(int var1, ByteBuffer var2);

    public Matrix3x2dc getToAddress(long var1);

    public Matrix3x2dc getTransposedToAddress(long var1);

    public double[] get(double[] var1, int var2);

    public double[] get(double[] var1);

    public double[] get3x3(double[] var1, int var2);

    public double[] get3x3(double[] var1);

    public double[] get4x4(double[] var1, int var2);

    public double[] get4x4(double[] var1);

    public Matrix3x2d scale(double var1, double var3, Matrix3x2d var5);

    public Matrix3x2d scale(Vector2dc var1, Matrix3x2d var2);

    public Matrix3x2d scale(Vector2fc var1, Matrix3x2d var2);

    public Matrix3x2d scaleLocal(double var1, Matrix3x2d var3);

    public Matrix3x2d scaleLocal(double var1, double var3, Matrix3x2d var5);

    public Matrix3x2d scaleAroundLocal(double var1, double var3, double var5, double var7, Matrix3x2d var9);

    public Matrix3x2d scaleAroundLocal(double var1, double var3, double var5, Matrix3x2d var7);

    public Matrix3x2d scale(double var1, Matrix3x2d var3);

    public Matrix3x2d scaleAround(double var1, double var3, double var5, double var7, Matrix3x2d var9);

    public Matrix3x2d scaleAround(double var1, double var3, double var5, Matrix3x2d var7);

    public Vector3d transform(Vector3d var1);

    public Vector3d transform(Vector3dc var1, Vector3d var2);

    public Vector3d transform(double var1, double var3, double var5, Vector3d var7);

    public Vector2d transformPosition(Vector2d var1);

    public Vector2d transformPosition(Vector2dc var1, Vector2d var2);

    public Vector2d transformPosition(double var1, double var3, Vector2d var5);

    public Vector2d transformDirection(Vector2d var1);

    public Vector2d transformDirection(Vector2dc var1, Vector2d var2);

    public Vector2d transformDirection(double var1, double var3, Vector2d var5);

    public Matrix3x2d rotate(double var1, Matrix3x2d var3);

    public Matrix3x2d rotateLocal(double var1, Matrix3x2d var3);

    public Matrix3x2d rotateAbout(double var1, double var3, double var5, Matrix3x2d var7);

    public Matrix3x2d rotateTo(Vector2dc var1, Vector2dc var2, Matrix3x2d var3);

    public Matrix3x2d view(double var1, double var3, double var5, double var7, Matrix3x2d var9);

    public Vector2d origin(Vector2d var1);

    public double[] viewArea(double[] var1);

    public Vector2d positiveX(Vector2d var1);

    public Vector2d normalizedPositiveX(Vector2d var1);

    public Vector2d positiveY(Vector2d var1);

    public Vector2d normalizedPositiveY(Vector2d var1);

    public Vector2d unproject(double var1, double var3, int[] var5, Vector2d var6);

    public Vector2d unprojectInv(double var1, double var3, int[] var5, Vector2d var6);

    public boolean testPoint(double var1, double var3);

    public boolean testCircle(double var1, double var3, double var5);

    public boolean testAar(double var1, double var3, double var5, double var7);

    public boolean equals(Matrix3x2dc var1, double var2);

    public boolean isFinite();
}

