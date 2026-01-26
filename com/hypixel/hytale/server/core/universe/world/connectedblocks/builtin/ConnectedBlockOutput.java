/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.connectedblocks.builtin;

import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;

public class ConnectedBlockOutput {
    public static final BuilderCodec<ConnectedBlockOutput> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ConnectedBlockOutput.class, ConnectedBlockOutput::new).append(new KeyedCodec<String>("State", Codec.STRING), (output, state) -> {
        output.state = state;
    }, output -> output.state).documentation("An optional state definition to apply to the base block type").add()).append(new KeyedCodec<String>("Block", Codec.STRING), (output, blockTypeKey) -> {
        output.blockTypeKey = blockTypeKey;
    }, output -> output.blockTypeKey).documentation("An optional block ID to use instead of the base block type").add()).build();
    protected String state;
    protected String blockTypeKey;

    protected ConnectedBlockOutput() {
    }

    public int resolve(BlockType baseBlockType, BlockTypeAssetMap<String, BlockType> assetMap) {
        int index;
        BlockType blockType;
        String blockTypeKey = this.blockTypeKey;
        if (blockTypeKey == null) {
            blockTypeKey = baseBlockType.getId();
        }
        if ((blockType = (BlockType)assetMap.getAsset(blockTypeKey)) == null) {
            return -1;
        }
        if (this.state != null) {
            String baseKey = blockType.getDefaultStateKey();
            BlockType baseBlock = baseKey == null ? blockType : (BlockType)BlockType.getAssetMap().getAsset(baseKey);
            blockTypeKey = "default".equals(this.state) ? baseBlock.getId() : baseBlock.getBlockKeyForState(this.state);
            if (blockTypeKey == null) {
                return -1;
            }
        }
        if ((index = assetMap.getIndex(blockTypeKey)) == Integer.MIN_VALUE) {
            return -1;
        }
        this.blockTypeKey = blockTypeKey;
        return index;
    }
}

