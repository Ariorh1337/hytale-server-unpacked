/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.joml.Vector2i;

public interface Vector2ic {
    public int x();

    public int y();

    public ByteBuffer get(ByteBuffer var1);

    public ByteBuffer get(int var1, ByteBuffer var2);

    public IntBuffer get(IntBuffer var1);

    public IntBuffer get(int var1, IntBuffer var2);

    public Vector2ic getToAddress(long var1);

    public Vector2i sub(Vector2ic var1, Vector2i var2);

    public Vector2i sub(int var1, int var2, Vector2i var3);

    public long lengthSquared();

    public double length();

    public double distance(Vector2ic var1);

    public double distance(int var1, int var2);

    public long distanceSquared(Vector2ic var1);

    public long distanceSquared(int var1, int var2);

    public long gridDistance(Vector2ic var1);

    public long gridDistance(int var1, int var2);

    public Vector2i add(Vector2ic var1, Vector2i var2);

    public Vector2i add(int var1, int var2, Vector2i var3);

    public Vector2i mul(int var1, Vector2i var2);

    public Vector2i mul(Vector2ic var1, Vector2i var2);

    public Vector2i mul(int var1, int var2, Vector2i var3);

    public Vector2i div(float var1, Vector2i var2);

    public Vector2i div(int var1, Vector2i var2);

    public Vector2i negate(Vector2i var1);

    public Vector2i min(Vector2ic var1, Vector2i var2);

    public Vector2i max(Vector2ic var1, Vector2i var2);

    public int maxComponent();

    public int minComponent();

    public Vector2i absolute(Vector2i var1);

    public int get(int var1) throws IllegalArgumentException;

    public boolean equals(int var1, int var2);
}

