/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import org.joml.Vector3L;

public interface Vector3Lc {
    public long x();

    public long y();

    public long z();

    public LongBuffer get(LongBuffer var1);

    public LongBuffer get(int var1, LongBuffer var2);

    public ByteBuffer get(ByteBuffer var1);

    public ByteBuffer get(int var1, ByteBuffer var2);

    public Vector3Lc getToAddress(long var1);

    public Vector3L sub(Vector3Lc var1, Vector3L var2);

    public Vector3L sub(long var1, long var3, long var5, Vector3L var7);

    public Vector3L add(Vector3Lc var1, Vector3L var2);

    public Vector3L add(long var1, long var3, long var5, Vector3L var7);

    public Vector3L mul(long var1, Vector3L var3);

    public Vector3L mul(Vector3Lc var1, Vector3L var2);

    public Vector3L mul(long var1, long var3, long var5, Vector3L var7);

    public Vector3L div(float var1, Vector3L var2);

    public Vector3L div(long var1, Vector3L var3);

    public long lengthSquared();

    public double length();

    public double distance(Vector3Lc var1);

    public double distance(long var1, long var3, long var5);

    public long gridDistance(Vector3Lc var1);

    public long gridDistance(long var1, long var3, long var5);

    public long distanceSquared(Vector3Lc var1);

    public long distanceSquared(long var1, long var3, long var5);

    public Vector3L negate(Vector3L var1);

    public Vector3L min(Vector3Lc var1, Vector3L var2);

    public Vector3L max(Vector3Lc var1, Vector3L var2);

    public long get(int var1) throws IllegalArgumentException;

    public int maxComponent();

    public int minComponent();

    public Vector3L absolute(Vector3L var1);

    public boolean equals(long var1, long var3, long var5);
}

