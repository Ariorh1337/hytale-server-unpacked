package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.passes;

import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders.EmptyPositionProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders.PositionProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.passes.EmptyGraphPass;
import com.hypixel.hytale.builtin.hytalegenerator.graph.passes.GraphPass;
import com.hypixel.hytale.builtin.hytalegenerator.graph.passes.PositionsGraphPass;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class PositionsGraphPassAsset extends GraphPassAsset implements Cleanable {
   @Nonnull
   public static final BuilderCodec<PositionsGraphPassAsset> CODEC = BuilderCodec.builder(
         PositionsGraphPassAsset.class, PositionsGraphPassAsset::new, GraphPassAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("Positions", PositionProviderAsset.CODEC, true),
         (asset, value) -> asset.positionProviderAsset = value,
         asset -> asset.positionProviderAsset
      )
      .add()
      .append(new KeyedCodec<>("StatsLabel", Codec.STRING, true), (asset, value) -> asset.statsLabel = value, asset -> asset.statsLabel)
      .add()
      .build();
   @Nonnull
   private PositionProviderAsset positionProviderAsset = EmptyPositionProviderAsset.INSTANCE;
   @Nonnull
   private String statsLabel = "";

   @Nonnull
   @Override
   public GraphPass build(@Nonnull GraphGeneratorAsset.Argument argument) {
      return super.skip()
         ? EmptyGraphPass.INSTANCE
         : new PositionsGraphPass(this.positionProviderAsset.build(new PositionProviderAsset.Argument(argument)), this.statsLabel);
   }

   @Override
   public void cleanUp() {
      this.positionProviderAsset.cleanUp();
   }
}
