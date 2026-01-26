/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.noisegenerators;

import com.hypixel.hytale.builtin.hytalegenerator.assets.noisegenerators.NoiseAsset;
import com.hypixel.hytale.builtin.hytalegenerator.fields.FastNoiseLite;
import com.hypixel.hytale.builtin.hytalegenerator.fields.noise.CellNoiseField;
import com.hypixel.hytale.builtin.hytalegenerator.seed.SeedBox;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;

public class CellNoiseAsset
extends NoiseAsset {
    private static Set<String> validCellTypes = new HashSet<String>();
    public static final BuilderCodec<CellNoiseAsset> CODEC;
    private double warpScale = 1.0;
    private double warpAmount = 1.0;
    private double scale = 1.0;
    private double jitter = 0.5;
    private int octaves = 1;
    private String seedKey = "A";
    @Nonnull
    private FastNoiseLite.CellularReturnType cellType = FastNoiseLite.CellularReturnType.CellValue;

    @Override
    @Nonnull
    public CellNoiseField build(@Nonnull SeedBox parentSeed) {
        SeedBox childSeed = parentSeed.child(this.seedKey);
        return new CellNoiseField(childSeed.createSupplier().get(), this.scale, this.scale, this.scale, this.jitter, this.octaves, this.cellType, FastNoiseLite.DomainWarpType.OpenSimplex2, this.warpAmount, this.warpScale);
    }

    static {
        for (FastNoiseLite.CellularReturnType e : FastNoiseLite.CellularReturnType.values()) {
            validCellTypes.add(e.toString());
        }
        CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(CellNoiseAsset.class, CellNoiseAsset::new, NoiseAsset.ABSTRACT_CODEC).append(new KeyedCodec<Double>("WarpAmount", Codec.DOUBLE, true), (asset, warpAmount) -> {
            asset.warpAmount = warpAmount;
        }, asset -> asset.warpAmount).addValidator(Validators.greaterThan(0.0)).add()).append(new KeyedCodec<Double>("WarpScale", Codec.DOUBLE, true), (asset, warpScale) -> {
            asset.warpScale = warpScale;
        }, asset -> asset.warpScale).addValidator(Validators.greaterThan(0.0)).add()).append(new KeyedCodec<Double>("Scale", Codec.DOUBLE, true), (asset, scale) -> {
            asset.scale = scale;
        }, asset -> asset.scale).addValidator(Validators.greaterThan(0.0)).add()).append(new KeyedCodec<Integer>("Octaves", Codec.INTEGER, true), (asset, octaves) -> {
            asset.octaves = octaves;
        }, asset -> asset.octaves).addValidator(Validators.greaterThan(0)).add()).append(new KeyedCodec<String>("Seed", Codec.STRING, true), (asset, seed) -> {
            asset.seedKey = seed;
        }, asset -> asset.seedKey).add()).append(new KeyedCodec<String>("CellType", Codec.STRING, true), (asset, cellType) -> {
            asset.cellType = FastNoiseLite.CellularReturnType.valueOf(cellType);
        }, asset -> asset.cellType.name()).addValidator((v, r) -> {
            try {
                FastNoiseLite.CellularReturnType.valueOf(v);
            }
            catch (IllegalArgumentException e) {
                String msg = "Invalid CellType: " + v + ". Valid choices: ";
                for (String t : validCellTypes) {
                    msg = msg + " ";
                    msg = msg + t;
                }
                r.fail(msg);
            }
        }).add()).build();
    }
}

