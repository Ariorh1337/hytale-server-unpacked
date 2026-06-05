package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeactions;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.EmptyNodeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.Jitter3DNodeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.NodeAction;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class Jitter3dNodeActionAsset extends NodeActionAsset {
   @Nonnull
   public static final BuilderCodec<Jitter3dNodeActionAsset> CODEC = BuilderCodec.builder(
         Jitter3dNodeActionAsset.class, Jitter3dNodeActionAsset::new, NodeActionAsset.ABSTRACT_CODEC
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
      return super.skip() ? EmptyNodeAction.INSTANCE : new Jitter3DNodeAction(this.magnitude, argument.parentSeed.child(this.seed).createSupplier().get());
   }
}
