/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.portals.utils;

import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class BlockTypeUtils {
    @Nullable
    public static BlockType getBlockForState(@Nonnull BlockType blockType, @Nonnull String state) {
        BlockType baseBlock;
        String baseKey = blockType.getDefaultStateKey();
        BlockType blockType2 = baseBlock = baseKey == null ? blockType : (BlockType)BlockType.getAssetMap().getAsset(baseKey);
        if ("default".equals(state)) {
            return baseBlock;
        }
        return baseBlock != null ? baseBlock.getBlockForState(state) : null;
    }
}

