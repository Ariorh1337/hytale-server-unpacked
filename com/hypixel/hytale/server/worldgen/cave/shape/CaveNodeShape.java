/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.cave.shape;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.worldgen.cave.Cave;
import com.hypixel.hytale.server.worldgen.cave.element.CaveNode;
import com.hypixel.hytale.server.worldgen.chunk.ChunkGeneratorExecution;
import com.hypixel.hytale.server.worldgen.util.bounds.IWorldBounds;
import java.util.Random;

public interface CaveNodeShape {
    public Vector3d getStart();

    public Vector3d getEnd();

    public Vector3d getAnchor(Vector3d var1, double var2, double var4, double var6);

    public IWorldBounds getBounds();

    public boolean shouldReplace(int var1, double var2, double var4, int var6);

    public double getFloorPosition(int var1, double var2, double var4);

    public double getCeilingPosition(int var1, double var2, double var4);

    public void populateChunk(int var1, ChunkGeneratorExecution var2, Cave var3, CaveNode var4, Random var5);

    default public boolean hasGeometry() {
        return true;
    }
}

