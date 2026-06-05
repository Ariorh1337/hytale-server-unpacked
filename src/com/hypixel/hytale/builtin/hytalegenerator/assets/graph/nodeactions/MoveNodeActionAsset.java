package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeactions;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.vectorproviders.ConstantVectorProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.vectorproviders.VectorProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.EmptyNodeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.MoveNodeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.NodeAction;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class MoveNodeActionAsset extends NodeActionAsset {
   @Nonnull
   public static final BuilderCodec<MoveNodeActionAsset> CODEC = BuilderCodec.builder(
         MoveNodeActionAsset.class, MoveNodeActionAsset::new, NodeActionAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("Vector", VectorProviderAsset.CODEC, true), (asset, value) -> asset.vectorProviderAsset = value, asset -> asset.vectorProviderAsset
      )
      .add()
      .<Double>append(
         new KeyedCodec<>("MaxDistance", Codec.DOUBLE, true), (asset, value) -> asset.maxDistanceExclusive = value, asset -> asset.maxDistanceExclusive
      )
      .addValidator(Validators.greaterThanOrEqual(0.0))
      .add()
      .build();
   @Nonnull
   private VectorProviderAsset vectorProviderAsset = ConstantVectorProviderAsset.INSTANCE;
   private double maxDistanceExclusive = 0.0;

   @Nonnull
   @Override
   public NodeAction build(@Nonnull GraphGeneratorAsset.Argument argument) {
      return super.skip()
         ? EmptyNodeAction.INSTANCE
         : new MoveNodeAction(this.vectorProviderAsset.build(new VectorProviderAsset.Argument(argument)), this.maxDistanceExclusive);
   }

   @Override
   public void cleanUp() {
      this.vectorProviderAsset.cleanUp();
   }
}
