/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.portals.utils;

import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;

public final class BlockTypeUtils {
    private BlockTypeUtils() {
    }

    public static BlockType getBlockForState(BlockType blockType, String state) {
        BlockType baseBlock;
        String baseKey = blockType.getDefaultStateKey();
        BlockType blockType2 = baseBlock = baseKey == null ? blockType : (BlockType)BlockType.getAssetMap().getAsset(baseKey);
        if ("default".equals(state)) {
            return baseBlock;
        }
        return baseBlock.getBlockForState(state);
    }
}

