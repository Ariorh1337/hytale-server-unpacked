package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeactions;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.edgeactions.EdgeActionAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.edgeactions.EmptyEdgeActionAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeactions.EdgeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.ConnectedEdgesNodeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.EmptyNodeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.NodeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.SomeConnectedEdgesNodeAction;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class ConnectedEdgesNodeActionAsset extends NodeActionAsset {
   @Nonnull
   public static final BuilderCodec<ConnectedEdgesNodeActionAsset> CODEC = BuilderCodec.builder(
         ConnectedEdgesNodeActionAsset.class, ConnectedEdgesNodeActionAsset::new, NodeActionAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("EdgeAction", EdgeActionAsset.CODEC, true), (asset, value) -> asset.edgeActionAsset = value, asset -> asset.edgeActionAsset)
      .add()
      .<Double>append(new KeyedCodec<>("Ratio", Codec.DOUBLE, false), (asset, value) -> asset.ratio = value, asset -> asset.ratio)
      .addValidator(Validators.greaterThanOrEqual(0.0))
      .add()
      .append(new KeyedCodec<>("Seed", Codec.STRING, false), (asset, value) -> asset.seed = value, asset -> asset.seed)
      .add()
      .build();
   @Nonnull
   private EdgeActionAsset edgeActionAsset = EmptyEdgeActionAsset.INSTANCE;
   private double ratio = 1.0;
   @Nonnull
   private String seed = "";

   @Nonnull
   @Override
   public NodeAction build(@Nonnull GraphGeneratorAsset.Argument argument) {
      if (super.skip()) {
         return EmptyNodeAction.INSTANCE;
      }

      EdgeAction edgeAction = this.edgeActionAsset.build(argument);
      if (this.ratio >= 1.0) {
         return new ConnectedEdgesNodeAction(edgeAction);
      }

      int seedInt = argument.parentSeed.child(this.seed).createSupplier().get();
      return new SomeConnectedEdgesNodeAction(edgeAction, this.ratio, seedInt);
   }

   @Override
   public void cleanUp() {
      this.edgeActionAsset.cleanUp();
   }
}
