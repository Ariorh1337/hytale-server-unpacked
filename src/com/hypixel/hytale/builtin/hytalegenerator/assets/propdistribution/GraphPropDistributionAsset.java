package com.hypixel.hytale.builtin.hytalegenerator.assets.propdistribution;

import com.hypixel.hytale.builtin.hytalegenerator.VectorAssetValidatorUtil;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphGenerator;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.propdistributions.GraphPropDistribution;
import com.hypixel.hytale.builtin.hytalegenerator.propdistributions.NoPropDistribution;
import com.hypixel.hytale.builtin.hytalegenerator.propdistributions.PropDistribution;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class GraphPropDistributionAsset extends PropDistributionAsset {
   @Nonnull
   public static final BuilderCodec<GraphPropDistributionAsset> CODEC = BuilderCodec.builder(
         GraphPropDistributionAsset.class, GraphPropDistributionAsset::new, PropDistributionAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("GraphGenerator", GraphGeneratorAsset.CODEC, true),
         (asset, value) -> asset.graphGeneratorAsset = value,
         asset -> asset.graphGeneratorAsset
      )
      .add()
      .append(new KeyedCodec<>("ContentLayer", Codec.STRING, true), (asset, value) -> asset.contentLayerName = value, asset -> asset.contentLayerName)
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
   private GraphGeneratorAsset graphGeneratorAsset = GraphGeneratorAsset.EMPTY;
   @Nonnull
   private String contentLayerName = "";
   @Nonnull
   private Vector3d cacheCellSize = new Vector3d(320.0, 320.0, 320.0);
   private int cacheCapacity = 50;

   @Nonnull
   @Override
   public PropDistribution build(@Nonnull PropDistributionAsset.Argument argument) {
      if (super.isSkipped()) {
         return NoPropDistribution.INSTANCE;
      }

      GraphGenerator graphGenerator = this.graphGeneratorAsset.build(new GraphGeneratorAsset.Argument(argument, false, false, true, false));
      int contentId = GraphSpace.Content.toIntId(this.contentLayerName);
      return new GraphPropDistribution(graphGenerator, contentId, this.cacheCellSize, this.cacheCapacity);
   }

   @Override
   public void cleanUp() {
      this.graphGeneratorAsset.cleanUp();
   }
}
