package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.AllNodeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.NodeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.RandomNodeSelector;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class RandomNodeSelectorAsset extends NodeSelectorAsset {
   @Nonnull
   public static final BuilderCodec<RandomNodeSelectorAsset> CODEC = BuilderCodec.builder(
         RandomNodeSelectorAsset.class, RandomNodeSelectorAsset::new, NodeSelectorAsset.ABSTRACT_CODEC
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
   public NodeSelector build(@Nonnull GraphGeneratorAsset.Argument argument) {
      return super.skip() ? AllNodeSelector.INSTANCE : new RandomNodeSelector(this.chance, argument.parentSeed.child(this.seed).createSupplier().get());
   }
}
