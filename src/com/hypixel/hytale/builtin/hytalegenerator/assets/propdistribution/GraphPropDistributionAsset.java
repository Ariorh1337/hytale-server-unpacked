package com.hypixel.hytale.builtin.hytalegenerator.assets.propdistribution;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphGenerator;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.propdistributions.GraphPropDistribution;
import com.hypixel.hytale.builtin.hytalegenerator.propdistributions.NoPropDistribution;
import com.hypixel.hytale.builtin.hytalegenerator.propdistributions.PropDistribution;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

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
      .build();
   @Nonnull
   private GraphGeneratorAsset graphGeneratorAsset = GraphGeneratorAsset.EMPTY;
   @Nonnull
   private String contentLayerName = "";

   @Nonnull
   @Override
   public PropDistribution build(@Nonnull PropDistributionAsset.Argument argument) {
      if (super.isSkipped()) {
         return NoPropDistribution.INSTANCE;
      }

      GraphGenerator graphGenerator = this.graphGeneratorAsset.build(new GraphGeneratorAsset.Argument(argument, false, false, true, false));
      int contentId = GraphSpace.Content.toIntId(this.contentLayerName);
      return new GraphPropDistribution(graphGenerator, contentId);
   }

   @Override
   public void cleanUp() {
      this.graphGeneratorAsset.cleanUp();
   }
}
