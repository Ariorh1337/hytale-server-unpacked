/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.framework;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.assets.framework.FrameworkAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders.ListPositionProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders.PositionProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.worldstructures.WorldStructureAsset;
import com.hypixel.hytale.builtin.hytalegenerator.referencebundle.ReferenceBundle;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import java.util.HashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class PositionsFrameworkAsset
extends FrameworkAsset {
    @Nonnull
    public static final String NAME = "Positions";
    @Nonnull
    public static final Class<Entries> CLASS = Entries.class;
    @Nonnull
    public static final BuilderCodec<PositionsFrameworkAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(PositionsFrameworkAsset.class, PositionsFrameworkAsset::new, FrameworkAsset.ABSTRACT_CODEC).append(new KeyedCodec<T[]>("Entries", new ArrayCodec(EntryAsset.CODEC, EntryAsset[]::new), true), (asset, value) -> {
        asset.entryAssets = value;
    }, asset -> asset.entryAssets).add()).build();
    private String id;
    private AssetExtraInfo.Data data;
    private EntryAsset[] entryAssets = new EntryAsset[0];

    private PositionsFrameworkAsset() {
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void build(@NonNullDecl WorldStructureAsset.Argument argument, @NonNullDecl ReferenceBundle referenceBundle) {
        Entries entries = new Entries();
        for (EntryAsset entryAsset : this.entryAssets) {
            entries.put(entryAsset.name, entryAsset.positionProviderAsset);
        }
        referenceBundle.put(NAME, entries, CLASS);
    }

    public static class EntryAsset
    implements JsonAssetWithMap<String, DefaultAssetMap<String, EntryAsset>> {
        @Nonnull
        public static final AssetBuilderCodec<String, EntryAsset> CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(EntryAsset.class, EntryAsset::new, Codec.STRING, (asset, id) -> {
            asset.id = id;
        }, config -> config.id, (config, data) -> {
            config.data = data;
        }, config -> config.data).append(new KeyedCodec<String>("Name", Codec.STRING, true), (asset, value) -> {
            asset.name = value;
        }, asset -> asset.name).add()).append(new KeyedCodec("Positions", PositionProviderAsset.CODEC, true), (asset, value) -> {
            asset.positionProviderAsset = value;
        }, asset -> asset.positionProviderAsset).add()).build();
        private String id;
        private AssetExtraInfo.Data data;
        private String name = "";
        private PositionProviderAsset positionProviderAsset = new ListPositionProviderAsset();

        @Override
        public String getId() {
            return this.id;
        }
    }

    public static class Entries
    extends HashMap<String, PositionProviderAsset> {
        @Nullable
        public static Entries get(@Nonnull ReferenceBundle referenceBundle) {
            return referenceBundle.get(PositionsFrameworkAsset.NAME, CLASS);
        }

        @Nullable
        public static PositionProviderAsset get(@Nonnull String name, @Nonnull ReferenceBundle referenceBundle) {
            Entries entries = Entries.get(referenceBundle);
            if (entries == null) {
                return null;
            }
            return (PositionProviderAsset)entries.get(name);
        }
    }
}

