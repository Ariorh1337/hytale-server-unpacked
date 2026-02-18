/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.joml.Matrix4fc;
import org.joml.Matrix4x3fc;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.joml.Vector4f;
import org.joml.Vector4i;

public interface Vector4fc {
    public float x();

    public float y();

    public float z();

    public float w();

    public FloatBuffer get(FloatBuffer var1);

    public FloatBuffer get(int var1, FloatBuffer var2);

    public ByteBuffer get(ByteBuffer var1);

    public ByteBuffer get(int var1, ByteBuffer var2);

    public Vector4fc getToAddress(long var1);

    public Vector4f sub(Vector4fc var1, Vector4f var2);

    public Vector4f sub(float var1, float var2, float var3, float var4, Vector4f var5);

    public Vector4f add(Vector4fc var1, Vector4f var2);

    public Vector4f add(float var1, float var2, float var3, float var4, Vector4f var5);

    public Vector4f fma(Vector4fc var1, Vector4fc var2, Vector4f var3);

    public Vector4f fma(float var1, Vector4fc var2, Vector4f var3);

    public Vector4f mulAdd(Vector4fc var1, Vector4fc var2, Vector4f var3);

    public Vector4f mulAdd(float var1, Vector4fc var2, Vector4f var3);

    public Vector4f mul(Vector4fc var1, Vector4f var2);

    public Vector4f div(Vector4fc var1, Vector4f var2);

    public Vector4f mul(Matrix4fc var1, Vector4f var2);

    public Vector4f mulGeneric(Matrix4fc var1, Vector4f var2);

    public Vector4f mulTranspose(Matrix4fc var1, Vector4f var2);

    public Vector4f mulTranslation(Matrix4fc var1, Vector4f var2);

    public Vector4f mulAffine(Matrix4fc var1, Vector4f var2);

    public Vector4f mulAffineTranspose(Matrix4fc var1, Vector4f var2);

    public Vector4f mulGenericTranspose(Matrix4fc var1, Vector4f var2);

    public Vector4f mul(Matrix4x3fc var1, Vector4f var2);

    public Vector4f mulTranslation(Matrix4x3fc var1, Vector4f var2);

    public Vector4f mulGeneric(Matrix4x3fc var1, Vector4f var2);

    public Vector4f mulProject(Matrix4fc var1, Vector4f var2);

    public Vector3f mulProject(Matrix4fc var1, Vector3f var2);

    public Vector3f mulProjectGeneric(Matrix4fc var1, Vector3f var2);

    public Vector4f mulProjectGeneric(Matrix4fc var1, Vector4f var2);

    public Vector3f mulProjectTranslation(Matrix4fc var1, Vector3f var2);

    public Vector4f mulProjectTranslation(Matrix4fc var1, Vector4f var2);

    public Vector3f mulProjectAffine(Matrix4fc var1, Vector3f var2);

    public Vector4f mulProjectAffine(Matrix4fc var1, Vector4f var2);

    public Vector4f mul(float var1, Vector4f var2);

    public Vector4f mul(float var1, float var2, float var3, float var4, Vector4f var5);

    public Vector4f div(float var1, Vector4f var2);

    public Vector4f div(float var1, float var2, float var3, float var4, Vector4f var5);

    public Vector4f rotate(Quaternionfc var1, Vector4f var2);

    public Vector4f rotateAxis(float var1, float var2, float var3, float var4, Vector4f var5);

    public Vector4f rotateX(float var1, Vector4f var2);

    public Vector4f rotateY(float var1, Vector4f var2);

    public Vector4f rotateZ(float var1, Vector4f var2);

    public float lengthSquared();

    public float length();

    public Vector4f normalize(Vector4f var1);

    public Vector4f normalize(float var1, Vector4f var2);

    public Vector4f normalize3(Vector4f var1);

    public float distance(Vector4fc var1);

    public float distance(float var1, float var2, float var3, float var4);

    public float distanceSquared(Vector4fc var1);

    public float distanceSquared(float var1, float var2, float var3, float var4);

    public float dot(Vector4fc var1);

    public float dot(float var1, float var2, float var3, float var4);

    public float angleCos(Vector4fc var1);

    public float angle(Vector4fc var1);

    public Vector4f negate(Vector4f var1);

    public Vector4f min(Vector4fc var1, Vector4f var2);

    public Vector4f max(Vector4fc var1, Vector4f var2);

    public Vector4f lerp(Vector4fc var1, float var2, Vector4f var3);

    public Vector4f smoothStep(Vector4fc var1, float var2, Vector4f var3);

    public Vector4f hermite(Vector4fc var1, Vector4fc var2, Vector4fc var3, float var4, Vector4f var5);

    public float get(int var1) throws IllegalArgumentException;

    public Vector4i get(int var1, Vector4i var2);

    public Vector4f get(Vector4f var1);

    public Vector4d get(Vector4d var1);

    public int maxComponent();

    public int minComponent();

    public Vector4f floor(Vector4f var1);

    public Vector4f ceil(Vector4f var1);

    public Vector4f round(Vector4f var1);

    public boolean isFinite();

    public Vector4f absolute(Vector4f var1);

    public boolean equals(Vector4fc var1, float var2);

    public boolean equals(float var1, float var2, float var3, float var4);
}

