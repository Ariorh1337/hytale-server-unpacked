/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.item.config;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.codec.AssetCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.common.util.ArrayUtil;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Deprecated
public class BlockGroup
implements JsonAssetWithMap<String, DefaultAssetMap<String, BlockGroup>>,
NetworkSerializable<com.hypixel.hytale.protocol.BlockGroup> {
    private static final String[] DEFAULT_BLOCK_LIST = new String[0];
    public static final AssetCodec<String, BlockGroup> CODEC = ((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(BlockGroup.class, BlockGroup::new, Codec.STRING, (t, k) -> {
        t.id = k;
    }, t -> t.id, (asset, data) -> {
        asset.data = data;
    }, asset -> asset.data).addField(new KeyedCodec<T[]>("Blocks", Codec.STRING_ARRAY), (blockSet, strings) -> {
        blockSet.blocks = strings;
    }, blockSet -> blockSet.blocks)).build();
    private String id;
    private AssetExtraInfo.Data data;
    private String[] blocks = DEFAULT_BLOCK_LIST;

    @Nullable
    public static BlockGroup findItemGroup(@Nonnull Item item) {
        String blockId = item.getBlockId();
        if (blockId == null) {
            return null;
        }
        Collection values = ((DefaultAssetMap)AssetRegistry.getAssetStore(BlockGroup.class).getAssetMap()).getAssetMap().values();
        for (BlockGroup group : values) {
            if (!ArrayUtil.contains(group.blocks, blockId)) continue;
            return group;
        }
        return null;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public String get(int index) {
        return this.blocks[index];
    }

    public int size() {
        return this.blocks.length;
    }

    public int getIndex(@Nonnull Item item) {
        String id = item.getBlockId();
        return ArrayUtil.indexOf(this.blocks, id);
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.BlockGroup toPacket() {
        com.hypixel.hytale.protocol.BlockGroup packet = new com.hypixel.hytale.protocol.BlockGroup();
        packet.names = this.blocks;
        return packet;
    }
}

