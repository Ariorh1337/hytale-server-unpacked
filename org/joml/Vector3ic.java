/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.joml.Vector3i;

public interface Vector3ic {
    public int x();

    public int y();

    public int z();

    public IntBuffer get(IntBuffer var1);

    public IntBuffer get(int var1, IntBuffer var2);

    public ByteBuffer get(ByteBuffer var1);

    public ByteBuffer get(int var1, ByteBuffer var2);

    public Vector3ic getToAddress(long var1);

    public Vector3i sub(Vector3ic var1, Vector3i var2);

    public Vector3i sub(int var1, int var2, int var3, Vector3i var4);

    public Vector3i add(Vector3ic var1, Vector3i var2);

    public Vector3i add(int var1, int var2, int var3, Vector3i var4);

    public Vector3i mul(int var1, Vector3i var2);

    public Vector3i mul(Vector3ic var1, Vector3i var2);

    public Vector3i mul(int var1, int var2, int var3, Vector3i var4);

    public Vector3i div(float var1, Vector3i var2);

    public Vector3i div(int var1, Vector3i var2);

    public long lengthSquared();

    public double length();

    public double distance(Vector3ic var1);

    public double distance(int var1, int var2, int var3);

    public long gridDistance(Vector3ic var1);

    public long gridDistance(int var1, int var2, int var3);

    public long distanceSquared(Vector3ic var1);

    public long distanceSquared(int var1, int var2, int var3);

    public Vector3i negate(Vector3i var1);

    public Vector3i min(Vector3ic var1, Vector3i var2);

    public Vector3i max(Vector3ic var1, Vector3i var2);

    public int get(int var1) throws IllegalArgumentException;

    public int maxComponent();

    public int minComponent();

    public Vector3i absolute(Vector3i var1);

    public boolean equals(int var1, int var2, int var3);
}

