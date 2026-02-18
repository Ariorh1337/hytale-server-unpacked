/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.joml.Matrix2f;
import org.joml.Matrix3f;
import org.joml.Matrix3x2f;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public interface Matrix2fc {
    public float m00();

    public float m01();

    public float m10();

    public float m11();

    public Matrix2f mul(Matrix2fc var1, Matrix2f var2);

    public Matrix2f mulLocal(Matrix2fc var1, Matrix2f var2);

    public float determinant();

    public Matrix2f invert(Matrix2f var1);

    public Matrix2f transpose(Matrix2f var1);

    public Matrix2f get(Matrix2f var1);

    public Matrix3x2f get(Matrix3x2f var1);

    public Matrix3f get(Matrix3f var1);

    public float getRotation();

    public FloatBuffer get(FloatBuffer var1);

    public FloatBuffer get(int var1, FloatBuffer var2);

    public ByteBuffer get(ByteBuffer var1);

    public ByteBuffer get(int var1, ByteBuffer var2);

    public FloatBuffer getTransposed(FloatBuffer var1);

    public FloatBuffer getTransposed(int var1, FloatBuffer var2);

    public ByteBuffer getTransposed(ByteBuffer var1);

    public ByteBuffer getTransposed(int var1, ByteBuffer var2);

    public Matrix2fc getToAddress(long var1);

    public Matrix2fc getTransposedToAddress(long var1);

    public float[] get(float[] var1, int var2);

    public float[] get(float[] var1);

    public Matrix2f scale(Vector2fc var1, Matrix2f var2);

    public Matrix2f scale(float var1, float var2, Matrix2f var3);

    public Matrix2f scale(float var1, Matrix2f var2);

    public Matrix2f scaleLocal(float var1, float var2, Matrix2f var3);

    public Vector2f transform(Vector2f var1);

    public Vector2f transform(Vector2fc var1, Vector2f var2);

    public Vector2f transform(float var1, float var2, Vector2f var3);

    public Vector2f transformTranspose(Vector2f var1);

    public Vector2f transformTranspose(Vector2fc var1, Vector2f var2);

    public Vector2f transformTranspose(float var1, float var2, Vector2f var3);

    public Matrix2f rotate(float var1, Matrix2f var2);

    public Matrix2f rotateLocal(float var1, Matrix2f var2);

    public Vector2f getRow(int var1, Vector2f var2) throws IndexOutOfBoundsException;

    public Vector2f getColumn(int var1, Vector2f var2) throws IndexOutOfBoundsException;

    public float get(int var1, int var2);

    public Matrix2f normal(Matrix2f var1);

    public Vector2f getScale(Vector2f var1);

    public Vector2f positiveX(Vector2f var1);

    public Vector2f normalizedPositiveX(Vector2f var1);

    public Vector2f positiveY(Vector2f var1);

    public Vector2f normalizedPositiveY(Vector2f var1);

    public Matrix2f add(Matrix2fc var1, Matrix2f var2);

    public Matrix2f sub(Matrix2fc var1, Matrix2f var2);

    public Matrix2f mulComponentWise(Matrix2fc var1, Matrix2f var2);

    public Matrix2f lerp(Matrix2fc var1, float var2, Matrix2f var3);

    public boolean equals(Matrix2fc var1, float var2);

    public boolean isFinite();
}

