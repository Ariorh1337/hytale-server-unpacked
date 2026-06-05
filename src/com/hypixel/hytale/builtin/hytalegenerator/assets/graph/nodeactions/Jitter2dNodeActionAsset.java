package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeactions;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.EmptyNodeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.Jitter2DNodeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.NodeAction;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class Jitter2dNodeActionAsset extends NodeActionAsset {
   @Nonnull
   public static final BuilderCodec<Jitter2dNodeActionAsset> CODEC = BuilderCodec.builder(
         Jitter2dNodeActionAsset.class, Jitter2dNodeActionAsset::new, NodeActionAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Magnitude", Codec.DOUBLE, true), (asset, value) -> asset.magnitude = value, asset -> asset.magnitude)
      .addValidator(Validators.greaterThanOrEqual(0.0))
      .add()
      .append(new KeyedCodec<>("Seed", Codec.STRING, true), (asset, value) -> asset.seed = value, asset -> asset.seed)
      .add()
      .build();
   private double magnitude = 0.0;
   @Nonnull
   private String seed = "";

   @Nonnull
   @Override
   public NodeAction build(@Nonnull GraphGeneratorAsset.Argument argument) {
      return super.skip() ? EmptyNodeAction.INSTANCE : new Jitter2DNodeAction(this.magnitude, argument.parentSeed.child(this.seed).createSupplier().get());
   }
}
