/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import org.joml.Matrix3dc;
import org.joml.Matrix3fc;
import org.joml.Matrix3x2dc;
import org.joml.Matrix3x2fc;
import org.joml.Matrix4dc;
import org.joml.Matrix4fc;
import org.joml.Matrix4x3dc;
import org.joml.Matrix4x3fc;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;

public interface Vector3dc {
    public double x();

    public double y();

    public double z();

    public ByteBuffer get(ByteBuffer var1);

    public ByteBuffer get(int var1, ByteBuffer var2);

    public DoubleBuffer get(DoubleBuffer var1);

    public DoubleBuffer get(int var1, DoubleBuffer var2);

    public FloatBuffer get(FloatBuffer var1);

    public FloatBuffer get(int var1, FloatBuffer var2);

    public ByteBuffer getf(ByteBuffer var1);

    public ByteBuffer getf(int var1, ByteBuffer var2);

    public Vector3dc getToAddress(long var1);

    public Vector3d sub(Vector3dc var1, Vector3d var2);

    public Vector3d sub(Vector3fc var1, Vector3d var2);

    public Vector3d sub(double var1, double var3, double var5, Vector3d var7);

    public Vector3d add(Vector3dc var1, Vector3d var2);

    public Vector3d add(Vector3fc var1, Vector3d var2);

    public Vector3d add(double var1, double var3, double var5, Vector3d var7);

    public Vector3d fma(Vector3dc var1, Vector3dc var2, Vector3d var3);

    public Vector3d fma(double var1, Vector3dc var3, Vector3d var4);

    public Vector3d fma(Vector3dc var1, Vector3fc var2, Vector3d var3);

    public Vector3d fma(Vector3fc var1, Vector3fc var2, Vector3d var3);

    public Vector3d fma(double var1, Vector3fc var3, Vector3d var4);

    public Vector3d mulAdd(Vector3dc var1, Vector3dc var2, Vector3d var3);

    public Vector3d mulAdd(double var1, Vector3dc var3, Vector3d var4);

    public Vector3d mulAdd(Vector3fc var1, Vector3dc var2, Vector3d var3);

    public Vector3d mul(Vector3fc var1, Vector3d var2);

    public Vector3d mul(Vector3dc var1, Vector3d var2);

    public Vector3d div(Vector3fc var1, Vector3d var2);

    public Vector3d div(Vector3dc var1, Vector3d var2);

    public Vector3d mulProject(Matrix4dc var1, double var2, Vector3d var4);

    public Vector3d mulProject(Matrix4dc var1, Vector3d var2);

    public Vector3d mulProject(Matrix4fc var1, Vector3d var2);

    public Vector3d mulProjectTranslation(Matrix4fc var1, Vector3d var2);

    public Vector3d mulProjectTranslation(Matrix4dc var1, Vector3d var2);

    public Vector3d mulProjectAffine(Matrix4fc var1, Vector3d var2);

    public Vector3d mulProjectGeneric(Matrix4fc var1, Vector3d var2);

    public Vector3d mulProjectGeneric(Matrix4dc var1, Vector3d var2);

    public Vector3d mulProjectAffine(Matrix4dc var1, Vector3d var2);

    public Vector3d mul(Matrix3dc var1, Vector3d var2);

    public Vector3f mul(Matrix3dc var1, Vector3f var2);

    public Vector3d mul(Matrix3fc var1, Vector3d var2);

    public Vector3d mul(Matrix3x2dc var1, Vector3d var2);

    public Vector3d mul(Matrix3x2fc var1, Vector3d var2);

    public Vector3d mulTranspose(Matrix3dc var1, Vector3d var2);

    public Vector3d mulTranspose(Matrix3fc var1, Vector3d var2);

    public Vector3d mulPosition(Matrix4dc var1, Vector3d var2);

    public Vector3d mulPositionGeneric(Matrix4dc var1, Vector3d var2);

    public Vector3d mulPositionTranslation(Matrix4dc var1, Vector3d var2);

    public Vector3d mulPosition(Matrix4fc var1, Vector3d var2);

    public Vector3d mulPositionGeneric(Matrix4fc var1, Vector3d var2);

    public Vector3d mulPositionTranslation(Matrix4fc var1, Vector3d var2);

    public Vector3d mulPosition(Matrix4x3dc var1, Vector3d var2);

    public Vector3d mulPositionGeneric(Matrix4x3dc var1, Vector3d var2);

    public Vector3d mulPositionTranslation(Matrix4x3dc var1, Vector3d var2);

    public Vector3d mulPosition(Matrix4x3fc var1, Vector3d var2);

    public Vector3d mulPositionGeneric(Matrix4x3fc var1, Vector3d var2);

    public Vector3d mulPositionTranslation(Matrix4x3fc var1, Vector3d var2);

    public Vector3d mulTransposePosition(Matrix4dc var1, Vector3d var2);

    public Vector3d mulTransposePosition(Matrix4fc var1, Vector3d var2);

    public double mulPositionW(Matrix4fc var1, Vector3d var2);

    public double mulPositionW(Matrix4dc var1, Vector3d var2);

    public Vector3d mulDirection(Matrix4dc var1, Vector3d var2);

    public Vector3d mulDirection(Matrix4fc var1, Vector3d var2);

    public Vector3d mulDirection(Matrix4x3dc var1, Vector3d var2);

    public Vector3d mulDirection(Matrix4x3fc var1, Vector3d var2);

    public Vector3d mulTransposeDirection(Matrix4dc var1, Vector3d var2);

    public Vector3d mulTransposeDirection(Matrix4fc var1, Vector3d var2);

    public Vector3d mul(double var1, Vector3d var3);

    public Vector3d mul(double var1, double var3, double var5, Vector3d var7);

    public Vector3d rotate(Quaterniondc var1, Vector3d var2);

    public Quaterniond rotationTo(Vector3dc var1, Quaterniond var2);

    public Quaterniond rotationTo(double var1, double var3, double var5, Quaterniond var7);

    public Vector3d rotateAxis(double var1, double var3, double var5, double var7, Vector3d var9);

    public Vector3d rotateX(double var1, Vector3d var3);

    public Vector3d rotateY(double var1, Vector3d var3);

    public Vector3d rotateZ(double var1, Vector3d var3);

    public Vector3d div(double var1, Vector3d var3);

    public Vector3d div(double var1, double var3, double var5, Vector3d var7);

    public double lengthSquared();

    public double length();

    public Vector3d normalize(Vector3d var1);

    public Vector3d normalize(double var1, Vector3d var3);

    public Vector3d cross(Vector3dc var1, Vector3d var2);

    public Vector3d cross(double var1, double var3, double var5, Vector3d var7);

    public double distance(Vector3dc var1);

    public double distance(double var1, double var3, double var5);

    public double distanceSquared(Vector3dc var1);

    public double distanceSquared(double var1, double var3, double var5);

    public double dot(Vector3dc var1);

    public double dot(double var1, double var3, double var5);

    public double angleCos(Vector3dc var1);

    public double angle(Vector3dc var1);

    public double angleSigned(Vector3dc var1, Vector3dc var2);

    public double angleSigned(double var1, double var3, double var5, double var7, double var9, double var11);

    public Vector3d min(Vector3dc var1, Vector3d var2);

    public Vector3d max(Vector3dc var1, Vector3d var2);

    public Vector3d negate(Vector3d var1);

    public Vector3d absolute(Vector3d var1);

    public Vector3d reflect(Vector3dc var1, Vector3d var2);

    public Vector3d reflect(double var1, double var3, double var5, Vector3d var7);

    public Vector3d half(Vector3dc var1, Vector3d var2);

    public Vector3d half(double var1, double var3, double var5, Vector3d var7);

    public Vector3d smoothStep(Vector3dc var1, double var2, Vector3d var4);

    public Vector3d hermite(Vector3dc var1, Vector3dc var2, Vector3dc var3, double var4, Vector3d var6);

    public Vector3d lerp(Vector3dc var1, double var2, Vector3d var4);

    public double get(int var1) throws IllegalArgumentException;

    public Vector3i get(int var1, Vector3i var2);

    public Vector3f get(Vector3f var1);

    public Vector3d get(Vector3d var1);

    public int maxComponent();

    public int minComponent();

    public Vector3d orthogonalize(Vector3dc var1, Vector3d var2);

    public Vector3d orthogonalizeUnit(Vector3dc var1, Vector3d var2);

    public Vector3d floor(Vector3d var1);

    public Vector3d ceil(Vector3d var1);

    public Vector3d round(Vector3d var1);

    public boolean isFinite();

    public boolean equals(Vector3dc var1, double var2);

    public boolean equals(double var1, double var3, double var5);
}

