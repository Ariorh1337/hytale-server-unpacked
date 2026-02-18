/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.joml.Matrix2dc;
import org.joml.Matrix2fc;
import org.joml.Matrix3x2fc;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;

public interface Vector2fc {
    public float x();

    public float y();

    public ByteBuffer get(ByteBuffer var1);

    public ByteBuffer get(int var1, ByteBuffer var2);

    public FloatBuffer get(FloatBuffer var1);

    public FloatBuffer get(int var1, FloatBuffer var2);

    public Vector2fc getToAddress(long var1);

    public Vector2f sub(Vector2fc var1, Vector2f var2);

    public Vector2f sub(float var1, float var2, Vector2f var3);

    public float dot(Vector2fc var1);

    public float angle(Vector2fc var1);

    public float lengthSquared();

    public float length();

    public float distance(Vector2fc var1);

    public float distanceSquared(Vector2fc var1);

    public float distance(float var1, float var2);

    public float distanceSquared(float var1, float var2);

    public Vector2f normalize(Vector2f var1);

    public Vector2f normalize(float var1, Vector2f var2);

    public Vector2f add(Vector2fc var1, Vector2f var2);

    public Vector2f add(float var1, float var2, Vector2f var3);

    public Vector2f negate(Vector2f var1);

    public Vector2f mul(float var1, Vector2f var2);

    public Vector2f mul(float var1, float var2, Vector2f var3);

    public Vector2f mul(Vector2fc var1, Vector2f var2);

    public Vector2f div(float var1, Vector2f var2);

    public Vector2f div(Vector2fc var1, Vector2f var2);

    public Vector2f div(float var1, float var2, Vector2f var3);

    public Vector2f mul(Matrix2fc var1, Vector2f var2);

    public Vector2f mul(Matrix2dc var1, Vector2f var2);

    public Vector2f mulTranspose(Matrix2fc var1, Vector2f var2);

    public Vector2f mulPosition(Matrix3x2fc var1, Vector2f var2);

    public Vector2f mulDirection(Matrix3x2fc var1, Vector2f var2);

    public Vector2f lerp(Vector2fc var1, float var2, Vector2f var3);

    public Vector2f fma(Vector2fc var1, Vector2fc var2, Vector2f var3);

    public Vector2f fma(float var1, Vector2fc var2, Vector2f var3);

    public Vector2f min(Vector2fc var1, Vector2f var2);

    public Vector2f max(Vector2fc var1, Vector2f var2);

    public int maxComponent();

    public int minComponent();

    public float get(int var1) throws IllegalArgumentException;

    public Vector2i get(int var1, Vector2i var2);

    public Vector2f get(Vector2f var1);

    public Vector2d get(Vector2d var1);

    public Vector2f floor(Vector2f var1);

    public Vector2f ceil(Vector2f var1);

    public Vector2f round(Vector2f var1);

    public boolean isFinite();

    public Vector2f absolute(Vector2f var1);

    public boolean equals(Vector2fc var1, float var2);

    public boolean equals(float var1, float var2);
}

