/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import org.joml.Matrix4dc;
import org.joml.Matrix4fc;
import org.joml.Matrix4x3dc;
import org.joml.Matrix4x3fc;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.joml.Vector4i;

public interface Vector4dc {
    public double x();

    public double y();

    public double z();

    public double w();

    public ByteBuffer get(ByteBuffer var1);

    public ByteBuffer get(int var1, ByteBuffer var2);

    public DoubleBuffer get(DoubleBuffer var1);

    public DoubleBuffer get(int var1, DoubleBuffer var2);

    public FloatBuffer get(FloatBuffer var1);

    public FloatBuffer get(int var1, FloatBuffer var2);

    public ByteBuffer getf(ByteBuffer var1);

    public ByteBuffer getf(int var1, ByteBuffer var2);

    public Vector4dc getToAddress(long var1);

    public Vector4d sub(Vector4dc var1, Vector4d var2);

    public Vector4d sub(Vector4fc var1, Vector4d var2);

    public Vector4d sub(double var1, double var3, double var5, double var7, Vector4d var9);

    public Vector4d add(Vector4dc var1, Vector4d var2);

    public Vector4d add(Vector4fc var1, Vector4d var2);

    public Vector4d add(double var1, double var3, double var5, double var7, Vector4d var9);

    public Vector4d fma(Vector4dc var1, Vector4dc var2, Vector4d var3);

    public Vector4d fma(double var1, Vector4dc var3, Vector4d var4);

    public Vector4d mul(Vector4dc var1, Vector4d var2);

    public Vector4d mul(Vector4fc var1, Vector4d var2);

    public Vector4d div(Vector4dc var1, Vector4d var2);

    public Vector4d mul(Matrix4dc var1, Vector4d var2);

    public Vector4d mul(Matrix4x3dc var1, Vector4d var2);

    public Vector4d mulGeneric(Matrix4x3dc var1, Vector4d var2);

    public Vector4d mulTranslation(Matrix4x3dc var1, Vector4d var2);

    public Vector4d mul(Matrix4x3fc var1, Vector4d var2);

    public Vector4d mulTranslation(Matrix4x3fc var1, Vector4d var2);

    public Vector4d mulGeneric(Matrix4x3fc var1, Vector4d var2);

    public Vector4d mul(Matrix4fc var1, Vector4d var2);

    public Vector4d mulGeneric(Matrix4dc var1, Vector4d var2);

    public Vector4d mulGeneric(Matrix4fc var1, Vector4d var2);

    public Vector4d mulTranspose(Matrix4dc var1, Vector4d var2);

    public Vector4d mulTranslation(Matrix4dc var1, Vector4d var2);

    public Vector4d mulTranslation(Matrix4fc var1, Vector4d var2);

    public Vector4d mulAffine(Matrix4dc var1, Vector4d var2);

    public Vector4d mulAffine(Matrix4fc var1, Vector4d var2);

    public Vector4d mulAffineTranspose(Matrix4dc var1, Vector4d var2);

    public Vector4d mulGenericTranspose(Matrix4dc var1, Vector4d var2);

    public Vector4d mulProject(Matrix4dc var1, Vector4d var2);

    public Vector3d mulProject(Matrix4dc var1, Vector3d var2);

    public Vector3d mulProjectGeneric(Matrix4dc var1, Vector3d var2);

    public Vector4d mulProjectGeneric(Matrix4dc var1, Vector4d var2);

    public Vector3d mulProjectTranslation(Matrix4dc var1, Vector3d var2);

    public Vector4d mulProjectTranslation(Matrix4dc var1, Vector4d var2);

    public Vector3d mulProjectAffine(Matrix4dc var1, Vector3d var2);

    public Vector4d mulProjectAffine(Matrix4dc var1, Vector4d var2);

    public Vector4d mulAdd(Vector4dc var1, Vector4dc var2, Vector4d var3);

    public Vector4d mulAdd(double var1, Vector4dc var3, Vector4d var4);

    public Vector4d mul(double var1, Vector4d var3);

    public Vector4d div(double var1, Vector4d var3);

    public Vector4d rotate(Quaterniondc var1, Vector4d var2);

    public Vector4d rotateAxis(double var1, double var3, double var5, double var7, Vector4d var9);

    public Vector4d rotateX(double var1, Vector4d var3);

    public Vector4d rotateY(double var1, Vector4d var3);

    public Vector4d rotateZ(double var1, Vector4d var3);

    public double lengthSquared();

    public double length();

    public Vector4d normalize(Vector4d var1);

    public Vector4d normalize(double var1, Vector4d var3);

    public Vector4d normalize3(Vector4d var1);

    public double distance(Vector4dc var1);

    public double distance(double var1, double var3, double var5, double var7);

    public double distanceSquared(Vector4dc var1);

    public double distanceSquared(double var1, double var3, double var5, double var7);

    public double dot(Vector4dc var1);

    public double dot(double var1, double var3, double var5, double var7);

    public double angleCos(Vector4dc var1);

    public double angle(Vector4dc var1);

    public Vector4d negate(Vector4d var1);

    public Vector4d min(Vector4dc var1, Vector4d var2);

    public Vector4d max(Vector4dc var1, Vector4d var2);

    public Vector4d smoothStep(Vector4dc var1, double var2, Vector4d var4);

    public Vector4d hermite(Vector4dc var1, Vector4dc var2, Vector4dc var3, double var4, Vector4d var6);

    public Vector4d lerp(Vector4dc var1, double var2, Vector4d var4);

    public double get(int var1) throws IllegalArgumentException;

    public Vector4i get(int var1, Vector4i var2);

    public Vector4f get(Vector4f var1);

    public Vector4d get(Vector4d var1);

    public int maxComponent();

    public int minComponent();

    public Vector4d floor(Vector4d var1);

    public Vector4d ceil(Vector4d var1);

    public Vector4d round(Vector4d var1);

    public boolean isFinite();

    public Vector4d absolute(Vector4d var1);

    public boolean equals(Vector4dc var1, double var2);

    public boolean equals(double var1, double var3, double var5, double var7);
}

