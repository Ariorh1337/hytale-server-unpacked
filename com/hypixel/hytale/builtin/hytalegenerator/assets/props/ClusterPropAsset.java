/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.props;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
import com.hypixel.hytale.builtin.hytalegenerator.assets.curves.ConstantCurveAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.curves.CurveAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.patterns.ConstantPatternAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.patterns.PatternAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.props.PropAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.scanners.OriginScannerAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.scanners.ScannerAsset;
import com.hypixel.hytale.builtin.hytalegenerator.datastructures.WeightedMap;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
import com.hypixel.hytale.builtin.hytalegenerator.props.ClusterProp;
import com.hypixel.hytale.builtin.hytalegenerator.props.Prop;
import com.hypixel.hytale.builtin.hytalegenerator.scanners.OriginScanner;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class ClusterPropAsset
extends PropAsset {
    public static final BuilderCodec<ClusterPropAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ClusterPropAsset.class, ClusterPropAsset::new, PropAsset.ABSTRACT_CODEC).append(new KeyedCodec<Integer>("Range", Codec.INTEGER, false), (asset, v) -> {
        asset.range = v;
    }, asset -> asset.range).addValidator(Validators.greaterThanOrEqual(0)).add()).append(new KeyedCodec("DistanceCurve", CurveAsset.CODEC, true), (asset, v) -> {
        asset.distanceCurve = v;
    }, asset -> asset.distanceCurve).add()).append(new KeyedCodec<String>("Seed", Codec.STRING, false), (asset, v) -> {
        asset.seed = v;
    }, asset -> asset.seed).add()).append(new KeyedCodec<T[]>("WeightedProps", new ArrayCodec(WeightedPropAsset.CODEC, WeightedPropAsset[]::new), true), (asset, v) -> {
        asset.weightedPropAssets = v;
    }, asset -> asset.weightedPropAssets).add()).append(new KeyedCodec("Pattern", PatternAsset.CODEC, false), (asset, v) -> {
        asset.patternAsset = v;
    }, asset -> asset.patternAsset).add()).append(new KeyedCodec("Scanner", ScannerAsset.CODEC, false), (asset, v) -> {
        asset.scannerAsset = v;
    }, asset -> asset.scannerAsset).add()).build();
    private int range = 0;
    private CurveAsset distanceCurve = new ConstantCurveAsset();
    private String seed = "A";
    private WeightedPropAsset[] weightedPropAssets = new WeightedPropAsset[0];
    private PatternAsset patternAsset = new ConstantPatternAsset();
    private ScannerAsset scannerAsset = new OriginScannerAsset();

    @Override
    @Nonnull
    public Prop build(@Nonnull PropAsset.Argument argument) {
        if (super.skip()) {
            return Prop.noProp();
        }
        WeightedMap<Prop> weightedMap = new WeightedMap<Prop>();
        for (WeightedPropAsset entry : this.weightedPropAssets) {
            Prop columnProp = entry.propAsset.build(argument);
            Vector3i readSize = columnProp.getReadBounds_voxelGrid().getSize();
            Vector3i writeSize = columnProp.getWriteBounds_voxelGrid().getSize();
            if (readSize.x != 1 || readSize.z != 1) {
                LoggerUtil.getLogger().log(Level.WARNING, "Cluster Prop child has a read area larger than a column.");
                continue;
            }
            if (writeSize.x != 1 || writeSize.z != 1) {
                LoggerUtil.getLogger().log(Level.WARNING, "Cluster Prop child has a write area larger than a column.");
                continue;
            }
            weightedMap.add(entry.propAsset.build(argument), entry.weight);
        }
        Pattern pattern = this.patternAsset == null ? Pattern.yesPattern() : this.patternAsset.build(PatternAsset.argumentFrom(argument));
        OriginScanner scanner = this.scannerAsset == null ? OriginScanner.getInstance() : this.scannerAsset.build(ScannerAsset.argumentFrom(argument));
        int intSeed = argument.parentSeed.child(this.seed).createSupplier().get();
        return new ClusterProp(this.range, this.distanceCurve.build(), intSeed, weightedMap, pattern, scanner);
    }

    @Override
    public void cleanUp() {
        this.distanceCurve.cleanUp();
        for (WeightedPropAsset weightedPropAsset : this.weightedPropAssets) {
            weightedPropAsset.cleanUp();
        }
        this.patternAsset.cleanUp();
        this.scannerAsset.cleanUp();
    }

    public static class WeightedPropAsset
    implements Cleanable,
    JsonAssetWithMap<String, DefaultAssetMap<String, WeightedPropAsset>> {
        public static final AssetBuilderCodec<String, WeightedPropAsset> CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(WeightedPropAsset.class, WeightedPropAsset::new, Codec.STRING, (asset, id) -> {
            asset.id = id;
        }, config -> config.id, (config, data) -> {
            config.data = data;
        }, config -> config.data).append(new KeyedCodec<Double>("Weight", Codec.DOUBLE, true), (t, w) -> {
            t.weight = w;
        }, t -> t.weight).addValidator(Validators.greaterThan(0.0)).add()).append(new KeyedCodec("ColumnProp", PropAsset.CODEC, true), (t, out) -> {
            t.propAsset = out;
        }, t -> t.propAsset).add()).build();
        private String id;
        private AssetExtraInfo.Data data;
        private double weight = 1.0;
        private PropAsset propAsset;

        @Override
        public String getId() {
            return this.id;
        }

        @Override
        public void cleanUp() {
            this.propAsset.cleanUp();
        }
    }
}

