/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.joml.Matrix3x2f;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public interface Matrix3x2fc {
    public float m00();

    public float m01();

    public float m10();

    public float m11();

    public float m20();

    public float m21();

    public Matrix3x2f mul(Matrix3x2fc var1, Matrix3x2f var2);

    public Matrix3x2f mulLocal(Matrix3x2fc var1, Matrix3x2f var2);

    public float determinant();

    public Matrix3x2f invert(Matrix3x2f var1);

    public Matrix3x2f translate(float var1, float var2, Matrix3x2f var3);

    public Matrix3x2f translate(Vector2fc var1, Matrix3x2f var2);

    public Matrix3x2f translateLocal(Vector2fc var1, Matrix3x2f var2);

    public Matrix3x2f translateLocal(float var1, float var2, Matrix3x2f var3);

    public Matrix3x2f get(Matrix3x2f var1);

    public FloatBuffer get(FloatBuffer var1);

    public FloatBuffer get(int var1, FloatBuffer var2);

    public ByteBuffer get(ByteBuffer var1);

    public ByteBuffer get(int var1, ByteBuffer var2);

    public FloatBuffer get3x3(FloatBuffer var1);

    public FloatBuffer get3x3(int var1, FloatBuffer var2);

    public ByteBuffer get3x3(ByteBuffer var1);

    public ByteBuffer get3x3(int var1, ByteBuffer var2);

    public FloatBuffer get4x4(FloatBuffer var1);

    public FloatBuffer get4x4(int var1, FloatBuffer var2);

    public ByteBuffer get4x4(ByteBuffer var1);

    public ByteBuffer get4x4(int var1, ByteBuffer var2);

    public Matrix3x2fc getToAddress(long var1);

    public Matrix3x2fc getTransposedToAddress(long var1);

    public float[] get(float[] var1, int var2);

    public float[] get(float[] var1);

    public float[] get3x3(float[] var1, int var2);

    public float[] get3x3(float[] var1);

    public float[] get4x4(float[] var1, int var2);

    public float[] get4x4(float[] var1);

    public Matrix3x2f scale(float var1, float var2, Matrix3x2f var3);

    public Matrix3x2f scale(Vector2fc var1, Matrix3x2f var2);

    public Matrix3x2f scaleAroundLocal(float var1, float var2, float var3, float var4, Matrix3x2f var5);

    public Matrix3x2f scaleAroundLocal(float var1, float var2, float var3, Matrix3x2f var4);

    public Matrix3x2f scale(float var1, Matrix3x2f var2);

    public Matrix3x2f scaleLocal(float var1, Matrix3x2f var2);

    public Matrix3x2f scaleLocal(float var1, float var2, Matrix3x2f var3);

    public Matrix3x2f scaleAround(float var1, float var2, float var3, float var4, Matrix3x2f var5);

    public Matrix3x2f scaleAround(float var1, float var2, float var3, Matrix3x2f var4);

    public Vector3f transform(Vector3f var1);

    public Vector3f transform(Vector3fc var1, Vector3f var2);

    public Vector3f transform(float var1, float var2, float var3, Vector3f var4);

    public Vector2f transformPosition(Vector2f var1);

    public Vector2f transformPosition(Vector2fc var1, Vector2f var2);

    public Vector2f transformPosition(float var1, float var2, Vector2f var3);

    public Vector2f transformDirection(Vector2f var1);

    public Vector2f transformDirection(Vector2fc var1, Vector2f var2);

    public Vector2f transformDirection(float var1, float var2, Vector2f var3);

    public Matrix3x2f rotate(float var1, Matrix3x2f var2);

    public Matrix3x2f rotateLocal(float var1, Matrix3x2f var2);

    public Matrix3x2f rotateAbout(float var1, float var2, float var3, Matrix3x2f var4);

    public Matrix3x2f rotateTo(Vector2fc var1, Vector2fc var2, Matrix3x2f var3);

    public Matrix3x2f view(float var1, float var2, float var3, float var4, Matrix3x2f var5);

    public Vector2f origin(Vector2f var1);

    public float[] viewArea(float[] var1);

    public Vector2f positiveX(Vector2f var1);

    public Vector2f normalizedPositiveX(Vector2f var1);

    public Vector2f positiveY(Vector2f var1);

    public Vector2f normalizedPositiveY(Vector2f var1);

    public Vector2f unproject(float var1, float var2, int[] var3, Vector2f var4);

    public Vector2f unprojectInv(float var1, float var2, int[] var3, Vector2f var4);

    public boolean testPoint(float var1, float var2);

    public boolean testCircle(float var1, float var2, float var3);

    public boolean testAar(float var1, float var2, float var3, float var4);

    public boolean equals(Matrix3x2fc var1, float var2);

    public boolean isFinite();
}

