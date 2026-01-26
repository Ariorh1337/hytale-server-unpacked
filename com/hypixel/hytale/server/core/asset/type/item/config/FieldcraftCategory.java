/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.item.config;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.protocol.ItemCategory;
import com.hypixel.hytale.server.core.asset.common.CommonAssetValidator;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import java.lang.ref.SoftReference;
import javax.annotation.Nonnull;

public class FieldcraftCategory
implements JsonAssetWithMap<String, DefaultAssetMap<String, FieldcraftCategory>>,
NetworkSerializable<ItemCategory> {
    public static final AssetBuilderCodec<String, FieldcraftCategory> CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(FieldcraftCategory.class, FieldcraftCategory::new, Codec.STRING, (itemCategory, k) -> {
        itemCategory.id = k;
    }, itemCategory -> itemCategory.id, (asset, data) -> {
        asset.data = data;
    }, asset -> asset.data).addField(new KeyedCodec<String>("Name", Codec.STRING), (itemCategory, s) -> {
        itemCategory.name = s;
    }, itemCategory -> itemCategory.name)).append(new KeyedCodec<String>("Icon", Codec.STRING), (itemCategory, s) -> {
        itemCategory.icon = s;
    }, itemCategory -> itemCategory.icon).addValidator(CommonAssetValidator.ICON_CRAFTING).add()).addField(new KeyedCodec<Integer>("Order", Codec.INTEGER), (itemCategory, s) -> {
        itemCategory.order = s;
    }, itemCategory -> itemCategory.order)).build();
    private static DefaultAssetMap<String, FieldcraftCategory> ASSET_MAP;
    protected AssetExtraInfo.Data data;
    protected String id;
    protected String name;
    protected String icon;
    protected int order;
    private SoftReference<ItemCategory> cachedPacket;

    public static DefaultAssetMap<String, FieldcraftCategory> getAssetMap() {
        if (ASSET_MAP == null) {
            ASSET_MAP = (DefaultAssetMap)AssetRegistry.getAssetStore(FieldcraftCategory.class).getAssetMap();
        }
        return ASSET_MAP;
    }

    protected FieldcraftCategory() {
    }

    @Override
    @Nonnull
    public ItemCategory toPacket() {
        ItemCategory cached;
        ItemCategory itemCategory = cached = this.cachedPacket == null ? null : this.cachedPacket.get();
        if (cached != null) {
            return cached;
        }
        ItemCategory packet = new ItemCategory();
        packet.id = this.id;
        packet.icon = this.icon;
        packet.name = this.name;
        packet.order = this.order;
        this.cachedPacket = new SoftReference<ItemCategory>(packet);
        return packet;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getIcon() {
        return this.icon;
    }

    @Nonnull
    public String toString() {
        return "FieldcraftCategory{id='" + this.id + "', name='" + this.name + "', icon='" + this.icon + "', order=" + this.order + "}";
    }
}

