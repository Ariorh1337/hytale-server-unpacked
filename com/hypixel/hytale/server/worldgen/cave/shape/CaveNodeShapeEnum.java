/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.cave.shape;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.worldgen.cave.CaveNodeType;
import com.hypixel.hytale.server.worldgen.cave.CaveType;
import com.hypixel.hytale.server.worldgen.cave.element.CaveNode;
import com.hypixel.hytale.server.worldgen.cave.shape.CaveNodeShape;
import java.util.Random;

public enum CaveNodeShapeEnum {
    PIPE,
    CYLINDER,
    PREFAB,
    EMPTY_LINE,
    ELLIPSOID,
    DISTORTED;


    public static interface CaveNodeShapeGenerator {
        public CaveNodeShape generateCaveNodeShape(Random var1, CaveType var2, CaveNode var3, CaveNodeType.CaveNodeChildEntry var4, Vector3d var5, float var6, float var7);
    }
}

