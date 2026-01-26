/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.config;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.codecs.map.EnumMapCodec;
import com.hypixel.hytale.server.core.asset.type.attitude.Attitude;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public class ItemAttitudeGroup
implements JsonAssetWithMap<String, IndexedLookupTableAssetMap<String, ItemAttitudeGroup>> {
    public static final AssetBuilderCodec<String, ItemAttitudeGroup> CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(ItemAttitudeGroup.class, ItemAttitudeGroup::new, Codec.STRING, (t, k) -> {
        t.id = k;
    }, t -> t.id, (asset, data) -> {
        asset.data = data;
    }, asset -> asset.data).documentation("Defines attitudes towards specific items by tag.")).append(new KeyedCodec("Attitudes", new EnumMapCodec<Sentiment, T[]>(Sentiment.class, Codec.STRING_ARRAY)), (group, map) -> {
        group.sentiments = map;
    }, group -> group.sentiments).documentation("A map of attitudes to item tags.").add()).afterDecode(itemAttitudeGroup -> {
        if (!itemAttitudeGroup.sentiments.isEmpty()) {
            itemAttitudeGroup.attitudes = new EnumMap<Attitude, String[]>(Attitude.class);
            for (Map.Entry<Sentiment, String[]> entry : itemAttitudeGroup.sentiments.entrySet()) {
                itemAttitudeGroup.attitudes.put(entry.getKey().attitude, entry.getValue());
            }
        }
    })).build();
    private static IndexedLookupTableAssetMap<String, ItemAttitudeGroup> ASSET_MAP;
    protected AssetExtraInfo.Data data;
    protected String id;
    protected Map<Sentiment, String[]> sentiments = Collections.emptyMap();
    @Nonnull
    protected Map<Attitude, String[]> attitudes = Collections.emptyMap();

    public static IndexedLookupTableAssetMap<String, ItemAttitudeGroup> getAssetMap() {
        if (ASSET_MAP == null) {
            ASSET_MAP = (IndexedLookupTableAssetMap)AssetRegistry.getAssetStore(ItemAttitudeGroup.class).getAssetMap();
        }
        return ASSET_MAP;
    }

    public ItemAttitudeGroup(String id) {
        this.id = id;
    }

    protected ItemAttitudeGroup() {
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Nonnull
    public Map<Attitude, String[]> getAttitudes() {
        return this.attitudes;
    }

    public static enum Sentiment implements Supplier<String>
    {
        Ignore(Attitude.IGNORE),
        Dislike(Attitude.HOSTILE),
        Neutral(Attitude.NEUTRAL),
        Like(Attitude.FRIENDLY),
        Love(Attitude.REVERED);

        private final Attitude attitude;

        private Sentiment(Attitude attitude) {
            this.attitude = attitude;
        }

        @Override
        @Nonnull
        public String get() {
            return this.name();
        }

        public Attitude getAttitude() {
            return this.attitude;
        }
    }
}

