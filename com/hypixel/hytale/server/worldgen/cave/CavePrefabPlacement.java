/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.cave;

import com.hypixel.hytale.server.worldgen.cave.element.CaveNode;

public enum CavePrefabPlacement {
    CEILING((seed, x, z, caveNode) -> caveNode.getCeilingPosition(seed, x, z)),
    FLOOR((seed, x, z, caveNode) -> caveNode.getFloorPosition(seed, x, z)),
    DEFAULT((seed, x, z, caveNode) -> (int)caveNode.getBounds().fractionY(0.5));

    public static final int NO_HEIGHT = -1;
    private final PrefabPlacementFunction function;

    private CavePrefabPlacement(PrefabPlacementFunction function) {
        this.function = function;
    }

    public PrefabPlacementFunction getFunction() {
        return this.function;
    }

    @FunctionalInterface
    public static interface PrefabPlacementFunction {
        public int generate(int var1, double var2, double var4, CaveNode var6);
    }
}

