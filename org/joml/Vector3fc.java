/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.joml.Matrix3dc;
import org.joml.Matrix3fc;
import org.joml.Matrix3x2dc;
import org.joml.Matrix3x2fc;
import org.joml.Matrix4dc;
import org.joml.Matrix4fc;
import org.joml.Matrix4x3dc;
import org.joml.Matrix4x3fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;

public interface Vector3fc {
    public float x();

    public float y();

    public float z();

    public FloatBuffer get(FloatBuffer var1);

    public FloatBuffer get(int var1, FloatBuffer var2);

    public ByteBuffer get(ByteBuffer var1);

    public ByteBuffer get(int var1, ByteBuffer var2);

    public Vector3fc getToAddress(long var1);

    public Vector3f sub(Vector3fc var1, Vector3f var2);

    public Vector3f sub(float var1, float var2, float var3, Vector3f var4);

    public Vector3f add(Vector3fc var1, Vector3f var2);

    public Vector3f add(float var1, float var2, float var3, Vector3f var4);

    public Vector3f fma(Vector3fc var1, Vector3fc var2, Vector3f var3);

    public Vector3f fma(float var1, Vector3fc var2, Vector3f var3);

    public Vector3f mulAdd(Vector3fc var1, Vector3fc var2, Vector3f var3);

    public Vector3f mulAdd(float var1, Vector3fc var2, Vector3f var3);

    public Vector3f mul(Vector3fc var1, Vector3f var2);

    public Vector3f div(Vector3fc var1, Vector3f var2);

    public Vector3f mulProject(Matrix4fc var1, Vector3f var2);

    public Vector3f mulProject(Matrix4fc var1, float var2, Vector3f var3);

    public Vector3f mulProjectTranslation(Matrix4fc var1, Vector3f var2);

    public Vector3f mulProjectTranslation(Matrix4dc var1, Vector3f var2);

    public Vector3f mulProjectAffine(Matrix4fc var1, Vector3f var2);

    public Vector3f mulProjectAffine(Matrix4dc var1, Vector3f var2);

    public Vector3f mulProjectGeneric(Matrix4fc var1, Vector3f var2);

    public Vector3f mulProjectGeneric(Matrix4dc var1, Vector3f var2);

    public Vector3f mul(Matrix3fc var1, Vector3f var2);

    public Vector3f mul(Matrix3dc var1, Vector3f var2);

    public Vector3f mul(Matrix3x2fc var1, Vector3f var2);

    public Vector3f mul(Matrix3x2dc var1, Vector3f var2);

    public Vector3f mulTranspose(Matrix3fc var1, Vector3f var2);

    public Vector3f mulPosition(Matrix4fc var1, Vector3f var2);

    public Vector3f mulPosition(Matrix4dc var1, Vector3f var2);

    public Vector3f mulPosition(Matrix4x3fc var1, Vector3f var2);

    public Vector3f mulPosition(Matrix4x3dc var1, Vector3f var2);

    public Vector3f mulPositionGeneric(Matrix4fc var1, Vector3f var2);

    public Vector3f mulPositionGeneric(Matrix4dc var1, Vector3f var2);

    public Vector3f mulPositionTranslation(Matrix4fc var1, Vector3f var2);

    public Vector3f mulPositionTranslation(Matrix4dc var1, Vector3f var2);

    public Vector3f mulPositionTranslation(Matrix4x3fc var1, Vector3f var2);

    public Vector3f mulPositionTranslation(Matrix4x3dc var1, Vector3f var2);

    public Vector3f mulTransposePosition(Matrix4fc var1, Vector3f var2);

    public Vector3f mulTransposePosition(Matrix4dc var1, Vector3f var2);

    public float mulPositionW(Matrix4fc var1, Vector3f var2);

    public float mulPositionW(Matrix4dc var1, Vector3f var2);

    public Vector3f mulDirection(Matrix4dc var1, Vector3f var2);

    public Vector3f mulDirection(Matrix4fc var1, Vector3f var2);

    public Vector3f mulDirection(Matrix4x3fc var1, Vector3f var2);

    public Vector3f mulDirection(Matrix4x3dc var1, Vector3f var2);

    public Vector3f mulTransposeDirection(Matrix4fc var1, Vector3f var2);

    public Vector3f mul(float var1, Vector3f var2);

    public Vector3f mul(float var1, float var2, float var3, Vector3f var4);

    public Vector3f div(float var1, Vector3f var2);

    public Vector3f div(float var1, float var2, float var3, Vector3f var4);

    public Vector3f rotate(Quaternionfc var1, Vector3f var2);

    public Quaternionf rotationTo(Vector3fc var1, Quaternionf var2);

    public Quaternionf rotationTo(float var1, float var2, float var3, Quaternionf var4);

    public Vector3f rotateAxis(float var1, float var2, float var3, float var4, Vector3f var5);

    public Vector3f rotateX(float var1, Vector3f var2);

    public Vector3f rotateY(float var1, Vector3f var2);

    public Vector3f rotateZ(float var1, Vector3f var2);

    public float lengthSquared();

    public float length();

    public Vector3f normalize(Vector3f var1);

    public Vector3f normalize(float var1, Vector3f var2);

    public Vector3f cross(Vector3fc var1, Vector3f var2);

    public Vector3f cross(float var1, float var2, float var3, Vector3f var4);

    public float distance(Vector3fc var1);

    public float distance(float var1, float var2, float var3);

    public float distanceSquared(Vector3fc var1);

    public float distanceSquared(float var1, float var2, float var3);

    public float dot(Vector3fc var1);

    public float dot(float var1, float var2, float var3);

    public float angleCos(Vector3fc var1);

    public float angle(Vector3fc var1);

    public float angleSigned(Vector3fc var1, Vector3fc var2);

    public float angleSigned(float var1, float var2, float var3, float var4, float var5, float var6);

    public Vector3f min(Vector3fc var1, Vector3f var2);

    public Vector3f max(Vector3fc var1, Vector3f var2);

    public Vector3f negate(Vector3f var1);

    public Vector3f absolute(Vector3f var1);

    public Vector3f reflect(Vector3fc var1, Vector3f var2);

    public Vector3f reflect(float var1, float var2, float var3, Vector3f var4);

    public Vector3f half(Vector3fc var1, Vector3f var2);

    public Vector3f half(float var1, float var2, float var3, Vector3f var4);

    public Vector3f smoothStep(Vector3fc var1, float var2, Vector3f var3);

    public Vector3f hermite(Vector3fc var1, Vector3fc var2, Vector3fc var3, float var4, Vector3f var5);

    public Vector3f lerp(Vector3fc var1, float var2, Vector3f var3);

    public float get(int var1) throws IllegalArgumentException;

    public Vector3i get(int var1, Vector3i var2);

    public Vector3f get(Vector3f var1);

    public Vector3d get(Vector3d var1);

    public int maxComponent();

    public int minComponent();

    public Vector3f orthogonalize(Vector3fc var1, Vector3f var2);

    public Vector3f orthogonalizeUnit(Vector3fc var1, Vector3f var2);

    public Vector3f floor(Vector3f var1);

    public Vector3f ceil(Vector3f var1);

    public Vector3f round(Vector3f var1);

    public boolean isFinite();

    public boolean equals(Vector3fc var1, float var2);

    public boolean equals(float var1, float var2, float var3);
}

