/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import org.joml.Vector4L;
import org.joml.Vector4ic;

public interface Vector4Lc {
    public long x();

    public long y();

    public long z();

    public long w();

    public LongBuffer get(LongBuffer var1);

    public LongBuffer get(int var1, LongBuffer var2);

    public ByteBuffer get(ByteBuffer var1);

    public ByteBuffer get(int var1, ByteBuffer var2);

    public Vector4Lc getToAddress(long var1);

    public Vector4L sub(Vector4Lc var1, Vector4L var2);

    public Vector4L sub(Vector4ic var1, Vector4L var2);

    public Vector4L sub(long var1, long var3, long var5, long var7, Vector4L var9);

    public Vector4L add(Vector4Lc var1, Vector4L var2);

    public Vector4L add(Vector4ic var1, Vector4L var2);

    public Vector4L add(long var1, long var3, long var5, long var7, Vector4L var9);

    public Vector4L mul(Vector4Lc var1, Vector4L var2);

    public Vector4L mul(Vector4ic var1, Vector4L var2);

    public Vector4L div(Vector4Lc var1, Vector4L var2);

    public Vector4L div(Vector4ic var1, Vector4L var2);

    public Vector4L mul(long var1, Vector4L var3);

    public Vector4L div(float var1, Vector4L var2);

    public Vector4L div(long var1, Vector4L var3);

    public long lengthSquared();

    public double length();

    public double distance(Vector4Lc var1);

    public double distance(Vector4ic var1);

    public double distance(long var1, long var3, long var5, long var7);

    public long gridDistance(Vector4Lc var1);

    public long gridDistance(Vector4ic var1);

    public long gridDistance(long var1, long var3, long var5, long var7);

    public long distanceSquared(Vector4Lc var1);

    public long distanceSquared(Vector4ic var1);

    public long distanceSquared(long var1, long var3, long var5, long var7);

    public long dot(Vector4Lc var1);

    public long dot(Vector4ic var1);

    public Vector4L negate(Vector4L var1);

    public Vector4L min(Vector4Lc var1, Vector4L var2);

    public Vector4L max(Vector4Lc var1, Vector4L var2);

    public long get(int var1) throws IllegalArgumentException;

    public int maxComponent();

    public int minComponent();

    public Vector4L absolute(Vector4L var1);

    public boolean equals(long var1, long var3, long var5, long var7);
}

