/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.chunk;

public interface BlockPriorityModifier {
    public static final BlockPriorityModifier NONE = new BlockPriorityModifier(){

        @Override
        public byte modifyCurrent(byte current, byte target) {
            return current;
        }

        @Override
        public byte modifyTarget(byte original, byte target) {
            return target;
        }
    };

    public byte modifyCurrent(byte var1, byte var2);

    public byte modifyTarget(byte var1, byte var2);
}

