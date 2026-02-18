/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import org.joml.Vector2L;

public interface Vector2Lc {
    public long x();

    public long y();

    public ByteBuffer get(ByteBuffer var1);

    public ByteBuffer get(int var1, ByteBuffer var2);

    public LongBuffer get(LongBuffer var1);

    public LongBuffer get(int var1, LongBuffer var2);

    public Vector2Lc getToAddress(long var1);

    public Vector2L sub(Vector2Lc var1, Vector2L var2);

    public Vector2L sub(long var1, long var3, Vector2L var5);

    public long lengthSquared();

    public double length();

    public double distance(Vector2Lc var1);

    public double distance(long var1, long var3);

    public long distanceSquared(Vector2Lc var1);

    public long distanceSquared(long var1, long var3);

    public long gridDistance(Vector2Lc var1);

    public long gridDistance(long var1, long var3);

    public Vector2L add(Vector2Lc var1, Vector2L var2);

    public Vector2L add(long var1, long var3, Vector2L var5);

    public Vector2L mul(long var1, Vector2L var3);

    public Vector2L mul(Vector2Lc var1, Vector2L var2);

    public Vector2L mul(long var1, long var3, Vector2L var5);

    public Vector2L div(float var1, Vector2L var2);

    public Vector2L div(long var1, Vector2L var3);

    public Vector2L negate(Vector2L var1);

    public Vector2L min(Vector2Lc var1, Vector2L var2);

    public Vector2L max(Vector2Lc var1, Vector2L var2);

    public long maxComponent();

    public long minComponent();

    public Vector2L absolute(Vector2L var1);

    public long get(int var1) throws IllegalArgumentException;

    public boolean equals(long var1, long var3);
}

