/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.cave;

import com.hypixel.hytale.server.worldgen.chunk.BlockPriorityModifier;

public class CaveBlockPriorityModifier
implements BlockPriorityModifier {
    public static final BlockPriorityModifier INSTANCE = new CaveBlockPriorityModifier();

    @Override
    public byte modifyCurrent(byte current, byte target) {
        if (current == 8 && target == 6) {
            return 6;
        }
        if (current == 6 && target == 5) {
            return 5;
        }
        return current;
    }

    @Override
    public byte modifyTarget(byte current, byte target) {
        if (current == 8 && target == 6) {
            return 8;
        }
        return target;
    }
}

