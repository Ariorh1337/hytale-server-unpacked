/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import org.joml.Matrix2dc;
import org.joml.Matrix2fc;
import org.joml.Matrix3x2dc;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2i;

public interface Vector2dc {
    public double x();

    public double y();

    public ByteBuffer get(ByteBuffer var1);

    public ByteBuffer get(int var1, ByteBuffer var2);

    public DoubleBuffer get(DoubleBuffer var1);

    public DoubleBuffer get(int var1, DoubleBuffer var2);

    public Vector2dc getToAddress(long var1);

    public Vector2d sub(double var1, double var3, Vector2d var5);

    public Vector2d sub(Vector2dc var1, Vector2d var2);

    public Vector2d sub(Vector2fc var1, Vector2d var2);

    public Vector2d mul(double var1, Vector2d var3);

    public Vector2d mul(double var1, double var3, Vector2d var5);

    public Vector2d mul(Vector2dc var1, Vector2d var2);

    public Vector2d div(double var1, Vector2d var3);

    public Vector2d div(double var1, double var3, Vector2d var5);

    public Vector2d div(Vector2fc var1, Vector2d var2);

    public Vector2d div(Vector2dc var1, Vector2d var2);

    public Vector2d mul(Matrix2dc var1, Vector2d var2);

    public Vector2d mul(Matrix2fc var1, Vector2d var2);

    public Vector2d mulTranspose(Matrix2dc var1, Vector2d var2);

    public Vector2d mulTranspose(Matrix2fc var1, Vector2d var2);

    public Vector2d mulPosition(Matrix3x2dc var1, Vector2d var2);

    public Vector2d mulDirection(Matrix3x2dc var1, Vector2d var2);

    public double dot(Vector2dc var1);

    public double angle(Vector2dc var1);

    public double lengthSquared();

    public double length();

    public double distance(Vector2dc var1);

    public double distanceSquared(Vector2dc var1);

    public double distance(Vector2fc var1);

    public double distanceSquared(Vector2fc var1);

    public double distance(double var1, double var3);

    public double distanceSquared(double var1, double var3);

    public Vector2d normalize(Vector2d var1);

    public Vector2d normalize(double var1, Vector2d var3);

    public Vector2d add(double var1, double var3, Vector2d var5);

    public Vector2d add(Vector2dc var1, Vector2d var2);

    public Vector2d add(Vector2fc var1, Vector2d var2);

    public Vector2d negate(Vector2d var1);

    public Vector2d lerp(Vector2dc var1, double var2, Vector2d var4);

    public Vector2d fma(Vector2dc var1, Vector2dc var2, Vector2d var3);

    public Vector2d fma(double var1, Vector2dc var3, Vector2d var4);

    public Vector2d min(Vector2dc var1, Vector2d var2);

    public Vector2d max(Vector2dc var1, Vector2d var2);

    public int maxComponent();

    public int minComponent();

    public double get(int var1) throws IllegalArgumentException;

    public Vector2i get(int var1, Vector2i var2);

    public Vector2f get(Vector2f var1);

    public Vector2d get(Vector2d var1);

    public Vector2d floor(Vector2d var1);

    public Vector2d ceil(Vector2d var1);

    public Vector2d round(Vector2d var1);

    public boolean isFinite();

    public Vector2d absolute(Vector2d var1);

    public boolean equals(Vector2dc var1, double var2);

    public boolean equals(double var1, double var3);
}

