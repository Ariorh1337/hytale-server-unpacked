/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.worldstructures.basic;

import com.hypixel.hytale.assetstore.codec.ContainedAssetCodec;
import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.builtin.hytalegenerator.assets.biomes.BiomeAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.ConstantDensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.worldstructures.WorldStructureAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.worldstructures.basic.BiomeRangeAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.worldstructures.mapcontentfield.BaseHeightContentFieldAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.worldstructures.mapcontentfield.ContentFieldAsset;
import com.hypixel.hytale.builtin.hytalegenerator.biome.BiomeType;
import com.hypixel.hytale.builtin.hytalegenerator.biomemap.BiomeMap;
import com.hypixel.hytale.builtin.hytalegenerator.biomemap.SimpleBiomeMap;
import com.hypixel.hytale.builtin.hytalegenerator.cartas.SimpleNoiseCarta;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.rangemaps.DoubleRange;
import com.hypixel.hytale.builtin.hytalegenerator.referencebundle.BaseHeightReference;
import com.hypixel.hytale.builtin.hytalegenerator.referencebundle.Reference;
import com.hypixel.hytale.builtin.hytalegenerator.referencebundle.ReferenceBundle;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import java.util.HashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BasicWorldStructureAsset
extends WorldStructureAsset {
    public static final BuilderCodec<BasicWorldStructureAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(BasicWorldStructureAsset.class, BasicWorldStructureAsset::new, WorldStructureAsset.ABSTRACT_CODEC).append(new KeyedCodec<T[]>("Biomes", new ArrayCodec(BiomeRangeAsset.CODEC, BiomeRangeAsset[]::new), true), (t, k) -> {
        t.biomeRangeAssets = k;
    }, t -> t.biomeRangeAssets).add()).append(new KeyedCodec("Density", DensityAsset.CODEC, true), (t, k) -> {
        t.densityAsset = k;
    }, t -> t.densityAsset).add()).append(new KeyedCodec("DefaultBiome", new ContainedAssetCodec(BiomeAsset.class, BiomeAsset.CODEC), true), (t, k) -> {
        t.defaultBiomeId = k;
    }, t -> t.defaultBiomeId).addValidatorLate(() -> BiomeAsset.VALIDATOR_CACHE.getValidator().late()).add()).append(new KeyedCodec<Integer>("DefaultTransitionDistance", Codec.INTEGER, true), (t, k) -> {
        t.biomeTransitionDistance = k;
    }, t -> t.biomeTransitionDistance).addValidator(Validators.greaterThan(0)).add()).append(new KeyedCodec<Integer>("MaxBiomeEdgeDistance", Codec.INTEGER, true), (t, k) -> {
        t.maxBiomeEdgeDistance = k;
    }, t -> t.maxBiomeEdgeDistance).addValidator(Validators.greaterThanOrEqual(0)).add()).append(new KeyedCodec<T[]>("ContentFields", new ArrayCodec(ContentFieldAsset.CODEC, ContentFieldAsset[]::new), false), (t, k) -> {
        t.contentFieldAssets = k;
    }, t -> t.contentFieldAssets).add()).build();
    private BiomeRangeAsset[] biomeRangeAssets = new BiomeRangeAsset[0];
    private int biomeTransitionDistance = 32;
    private int maxBiomeEdgeDistance = 0;
    private DensityAsset densityAsset = new ConstantDensityAsset();
    private String defaultBiomeId = "";
    private ContentFieldAsset[] contentFieldAssets = new ContentFieldAsset[0];

    @Nullable
    public BiomeMap buildBiomeMap(@Nonnull WorldStructureAsset.Argument argument) {
        ReferenceBundle referenceBundle = new ReferenceBundle();
        for (int i = this.contentFieldAssets.length - 1; i >= 0; --i) {
            ContentFieldAsset contentFieldAsset = this.contentFieldAssets[i];
            if (!(contentFieldAsset instanceof BaseHeightContentFieldAsset)) continue;
            BaseHeightContentFieldAsset bedAsset = (BaseHeightContentFieldAsset)contentFieldAsset;
            String name = bedAsset.getName();
            double y = bedAsset.getY();
            BiomeRangeAsset[] bedLayer = new BaseHeightReference((x, z) -> y);
            referenceBundle.put(name, (Reference)bedLayer, bedLayer.getClass());
        }
        HashMap<BiomeAsset, BiomeType> biomeAssetToBiomeType = new HashMap<BiomeAsset, BiomeType>();
        BiomeAsset defaultBiomeAsset = BiomeAsset.getAssetStore().getAssetMap().getAsset(this.defaultBiomeId);
        if (defaultBiomeAsset == null) {
            LoggerUtil.getLogger().warning("Couldn't find Biome asset with id: " + this.defaultBiomeId);
            return null;
        }
        BiomeType defaultBiome = defaultBiomeAsset.build(argument.materialCache, argument.parentSeed, referenceBundle, argument.workerIndexer);
        biomeAssetToBiomeType.put(defaultBiomeAsset, defaultBiome);
        Density noise = this.densityAsset.build(DensityAsset.from(argument, referenceBundle));
        SimpleNoiseCarta<BiomeType> carta = new SimpleNoiseCarta<BiomeType>(noise, defaultBiome);
        for (BiomeRangeAsset asset : this.biomeRangeAssets) {
            BiomeType biome;
            DoubleRange range = asset.getRange();
            BiomeAsset biomeAsset = asset.getBiomeAsset();
            if (biomeAsset == null) {
                LoggerUtil.getLogger().warning("Couldn't find biome asset with name " + asset.getBiomeAssetId());
                continue;
            }
            if (biomeAssetToBiomeType.containsKey(biomeAsset)) {
                biome = (BiomeType)biomeAssetToBiomeType.get(biomeAsset);
            } else {
                biome = biomeAsset.build(argument.materialCache, argument.parentSeed, referenceBundle, argument.workerIndexer);
                biomeAssetToBiomeType.put(biomeAsset, biome);
            }
            carta.put(range, biome);
        }
        SimpleBiomeMap biomeMap = new SimpleBiomeMap(carta);
        int defaultRadius = Math.max(1, this.biomeTransitionDistance / 2);
        biomeMap.setDefaultRadius(defaultRadius);
        return biomeMap;
    }

    @Override
    public int getBiomeTransitionDistance() {
        return this.biomeTransitionDistance;
    }

    @Override
    public int getMaxBiomeEdgeDistance() {
        return this.maxBiomeEdgeDistance;
    }

    @Override
    public void cleanUp() {
        this.densityAsset.cleanUp();
        for (ContentFieldAsset contentFieldAsset : this.contentFieldAssets) {
            contentFieldAsset.cleanUp();
        }
        BiomeAsset defaultBiomeAsset = BiomeAsset.getAssetStore().getAssetMap().getAsset(this.defaultBiomeId);
        if (defaultBiomeAsset != null) {
            defaultBiomeAsset.cleanUp();
        }
        for (BiomeRangeAsset asset : this.biomeRangeAssets) {
            BiomeAsset biomeAsset = asset.getBiomeAsset();
            if (biomeAsset == null) continue;
            biomeAsset.cleanUp();
        }
    }
}

