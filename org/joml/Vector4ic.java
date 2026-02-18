/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.joml.Vector4i;

public interface Vector4ic {
    public int x();

    public int y();

    public int z();

    public int w();

    public IntBuffer get(IntBuffer var1);

    public IntBuffer get(int var1, IntBuffer var2);

    public ByteBuffer get(ByteBuffer var1);

    public ByteBuffer get(int var1, ByteBuffer var2);

    public Vector4ic getToAddress(long var1);

    public Vector4i sub(Vector4ic var1, Vector4i var2);

    public Vector4i sub(int var1, int var2, int var3, int var4, Vector4i var5);

    public Vector4i add(Vector4ic var1, Vector4i var2);

    public Vector4i add(int var1, int var2, int var3, int var4, Vector4i var5);

    public Vector4i mul(Vector4ic var1, Vector4i var2);

    public Vector4i div(Vector4ic var1, Vector4i var2);

    public Vector4i mul(int var1, Vector4i var2);

    public Vector4i div(float var1, Vector4i var2);

    public Vector4i div(int var1, Vector4i var2);

    public long lengthSquared();

    public double length();

    public double distance(Vector4ic var1);

    public double distance(int var1, int var2, int var3, int var4);

    public long gridDistance(Vector4ic var1);

    public long gridDistance(int var1, int var2, int var3, int var4);

    public long distanceSquared(Vector4ic var1);

    public long distanceSquared(int var1, int var2, int var3, int var4);

    public long dot(Vector4ic var1);

    public Vector4i negate(Vector4i var1);

    public Vector4i min(Vector4ic var1, Vector4i var2);

    public Vector4i max(Vector4ic var1, Vector4i var2);

    public int get(int var1) throws IllegalArgumentException;

    public int maxComponent();

    public int minComponent();

    public Vector4i absolute(Vector4i var1);

    public boolean equals(int var1, int var2, int var3, int var4);
}

