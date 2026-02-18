/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import org.joml.Matrix2d;
import org.joml.Matrix2fc;
import org.joml.Matrix3d;
import org.joml.Matrix3x2d;
import org.joml.Vector2d;
import org.joml.Vector2dc;

public interface Matrix2dc {
    public double m00();

    public double m01();

    public double m10();

    public double m11();

    public Matrix2d mul(Matrix2dc var1, Matrix2d var2);

    public Matrix2d mul(Matrix2fc var1, Matrix2d var2);

    public Matrix2d mulLocal(Matrix2dc var1, Matrix2d var2);

    public double determinant();

    public Matrix2d invert(Matrix2d var1);

    public Matrix2d transpose(Matrix2d var1);

    public Matrix2d get(Matrix2d var1);

    public Matrix3x2d get(Matrix3x2d var1);

    public Matrix3d get(Matrix3d var1);

    public double getRotation();

    public DoubleBuffer get(DoubleBuffer var1);

    public DoubleBuffer get(int var1, DoubleBuffer var2);

    public ByteBuffer get(ByteBuffer var1);

    public ByteBuffer get(int var1, ByteBuffer var2);

    public ByteBuffer getFloats(ByteBuffer var1);

    public ByteBuffer getFloats(int var1, ByteBuffer var2);

    public DoubleBuffer getTransposed(DoubleBuffer var1);

    public DoubleBuffer getTransposed(int var1, DoubleBuffer var2);

    public ByteBuffer getTransposed(ByteBuffer var1);

    public ByteBuffer getTransposed(int var1, ByteBuffer var2);

    public FloatBuffer getTransposed(FloatBuffer var1);

    public FloatBuffer getTransposed(int var1, FloatBuffer var2);

    public ByteBuffer getTransposedFloats(ByteBuffer var1);

    public ByteBuffer getTransposedFloats(int var1, ByteBuffer var2);

    public Matrix2dc getToAddress(long var1);

    public Matrix2dc getTransposedToAddress(long var1);

    public double[] get(double[] var1, int var2);

    public double[] get(double[] var1);

    public Matrix2d scale(Vector2dc var1, Matrix2d var2);

    public Matrix2d scale(double var1, double var3, Matrix2d var5);

    public Matrix2d scale(double var1, Matrix2d var3);

    public Matrix2d scaleLocal(double var1, double var3, Matrix2d var5);

    public Vector2d transform(Vector2d var1);

    public Vector2d transform(Vector2dc var1, Vector2d var2);

    public Vector2d transform(double var1, double var3, Vector2d var5);

    public Vector2d transformTranspose(Vector2d var1);

    public Vector2d transformTranspose(Vector2dc var1, Vector2d var2);

    public Vector2d transformTranspose(double var1, double var3, Vector2d var5);

    public Matrix2d rotate(double var1, Matrix2d var3);

    public Matrix2d rotateLocal(double var1, Matrix2d var3);

    public Vector2d getRow(int var1, Vector2d var2) throws IndexOutOfBoundsException;

    public Vector2d getColumn(int var1, Vector2d var2) throws IndexOutOfBoundsException;

    public double get(int var1, int var2);

    public Matrix2d normal(Matrix2d var1);

    public Vector2d getScale(Vector2d var1);

    public Vector2d positiveX(Vector2d var1);

    public Vector2d normalizedPositiveX(Vector2d var1);

    public Vector2d positiveY(Vector2d var1);

    public Vector2d normalizedPositiveY(Vector2d var1);

    public Matrix2d add(Matrix2dc var1, Matrix2d var2);

    public Matrix2d sub(Matrix2dc var1, Matrix2d var2);

    public Matrix2d mulComponentWise(Matrix2dc var1, Matrix2d var2);

    public Matrix2d lerp(Matrix2dc var1, double var2, Matrix2d var4);

    public boolean equals(Matrix2dc var1, double var2);

    public boolean isFinite();
}

