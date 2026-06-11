package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.VectorAssetValidatorUtil;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.GraphDensity;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphGenerator;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class GraphDensityAsset extends DensityAsset {
   @Nonnull
   public static final BuilderCodec<GraphDensityAsset> CODEC = BuilderCodec.builder(
         GraphDensityAsset.class, GraphDensityAsset::new, DensityAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("GraphGenerator", GraphGeneratorAsset.CODEC, true),
         (asset, value) -> asset.graphGeneratorAsset = value,
         asset -> asset.graphGeneratorAsset
      )
      .add()
      .append(new KeyedCodec<>("ContentLayer", Codec.STRING, true), (asset, value) -> asset.contentLayerName = value, asset -> asset.contentLayerName)
      .add()
      .append(new KeyedCodec<>("BackgroundValue", Codec.DOUBLE, true), (asset, value) -> asset.backgroundValue = value, asset -> asset.backgroundValue)
      .add()
      .append(
         new KeyedCodec<>("TransitionSteepness", Codec.DOUBLE, true), (asset, value) -> asset.transitionSteepness = value, asset -> asset.transitionSteepness
      )
      .add()
      .append(new KeyedCodec<>("TransitionPoint", Codec.DOUBLE, true), (asset, value) -> asset.transitionPoint = value, asset -> asset.transitionPoint)
      .add()
      .append(new KeyedCodec<>("PrintStats", Codec.BOOLEAN, true), (asset, value) -> asset.printStats = value, asset -> asset.printStats)
      .add()
      .<Integer>append(
         new KeyedCodec<>("StatsPrintInterval", Codec.INTEGER, true), (asset, value) -> asset.statsPrintInterval = value, asset -> asset.statsPrintInterval
      )
      .addValidator(Validators.greaterThanOrEqual(0))
      .add()
      .append(new KeyedCodec<>("StatsLabel", Codec.STRING, true), (asset, value) -> asset.statsLabel = value, asset -> asset.statsLabel)
      .add()
      .<Vector3d>append(
         new KeyedCodec<>("CacheCellSize", Vector3dUtil.CODEC, true), (asset, value) -> asset.cacheCellSize = value, asset -> asset.cacheCellSize
      )
      .addValidator(VectorAssetValidatorUtil.greaterThan(0.0))
      .add()
      .<Integer>append(new KeyedCodec<>("CacheCapacity", Codec.INTEGER, true), (asset, value) -> asset.cacheCapacity = value, asset -> asset.cacheCapacity)
      .addValidator(Validators.greaterThanOrEqual(0))
      .add()
      .build();
   @Nonnull
   private GraphGeneratorAsset graphGeneratorAsset = new GraphGeneratorAsset();
   @Nonnull
   private String contentLayerName = "";
   private double backgroundValue = 0.0;
   private double transitionSteepness = 1.0;
   private double transitionPoint = 0.5;
   private boolean printStats = false;
   private int statsPrintInterval = 1000000;
   @Nonnull
   private String statsLabel = "UNNAMED";
   @Nonnull
   private Vector3d cacheCellSize = new Vector3d(320.0, 320.0, 320.0);
   private int cacheCapacity = 10;

   @Nonnull
   @Override
   public Density build(@Nonnull DensityAsset.Argument argument) {
      if (this.isSkipped()) {
         return new ConstantValueDensity(0.0);
      }

      GraphGenerator graphGenerator = this.graphGeneratorAsset.build(new GraphGeneratorAsset.Argument(argument, true, false, false, false));
      return new GraphDensity(
         graphGenerator,
         GraphSpace.Content.toIntId(this.contentLayerName),
         this.backgroundValue,
         this.transitionSteepness,
         this.transitionPoint,
         this.printStats,
         this.statsLabel,
         this.statsPrintInterval,
         this.cacheCellSize,
         this.cacheCapacity
      );
   }

   @Override
   public void cleanUp() {
      this.cleanUpInputs();
      this.graphGeneratorAsset.cleanUp();
   }
}
