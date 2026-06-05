package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.edgeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors.AllEdgeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors.EdgeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors.RandomEdgeSelector;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class RandomEdgeSelectorAsset extends EdgeSelectorAsset {
   @Nonnull
   public static final BuilderCodec<RandomEdgeSelectorAsset> CODEC = BuilderCodec.builder(
         RandomEdgeSelectorAsset.class, RandomEdgeSelectorAsset::new, EdgeSelectorAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Chance", Codec.DOUBLE, true), (asset, value) -> asset.chance = value, asset -> asset.chance)
      .addValidator(Validators.greaterThanOrEqual(0.0))
      .add()
      .append(new KeyedCodec<>("Seed", Codec.STRING, true), (asset, value) -> asset.seed = value, asset -> asset.seed)
      .add()
      .build();
   private double chance = 0.0;
   @Nonnull
   private String seed = "";

   @Nonnull
   @Override
   public EdgeSelector build(@Nonnull GraphGeneratorAsset.Argument argument) {
      return super.skip() ? AllEdgeSelector.INSTANCE : new RandomEdgeSelector(this.chance, argument.parentSeed.child(this.seed).createSupplier().get());
   }
}
