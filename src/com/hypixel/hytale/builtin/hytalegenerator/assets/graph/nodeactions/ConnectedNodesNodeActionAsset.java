package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeactions;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.ConnectedNodesNodeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.EmptyNodeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.NodeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.SomeConnectedNodesNodeAction;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class ConnectedNodesNodeActionAsset extends NodeActionAsset {
   @Nonnull
   public static final BuilderCodec<ConnectedNodesNodeActionAsset> CODEC = BuilderCodec.builder(
         ConnectedNodesNodeActionAsset.class, ConnectedNodesNodeActionAsset::new, NodeActionAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("NodeAction", NodeActionAsset.CODEC, true), (asset, value) -> asset.nodeActionAsset = value, asset -> asset.nodeActionAsset)
      .add()
      .<Double>append(new KeyedCodec<>("Ratio", Codec.DOUBLE, false), (asset, value) -> asset.ratio = value, asset -> asset.ratio)
      .addValidator(Validators.greaterThanOrEqual(0.0))
      .add()
      .append(new KeyedCodec<>("Seed", Codec.STRING, false), (asset, value) -> asset.seed = value, asset -> asset.seed)
      .add()
      .build();
   @Nonnull
   private NodeActionAsset nodeActionAsset = EmptyNodeActionAsset.INSTANCE;
   private double ratio = 1.0;
   @Nonnull
   private String seed = "";

   @Nonnull
   @Override
   public NodeAction build(@Nonnull GraphGeneratorAsset.Argument argument) {
      if (super.skip()) {
         return EmptyNodeAction.INSTANCE;
      }

      NodeAction nodeAction = this.nodeActionAsset.build(argument);
      if (this.ratio >= 1.0) {
         return new ConnectedNodesNodeAction(nodeAction);
      }

      int seedInt = argument.parentSeed.child(this.seed).createSupplier().get();
      return new SomeConnectedNodesNodeAction(this.nodeActionAsset.build(argument), this.ratio, seedInt);
   }

   @Override
   public void cleanUp() {
      this.nodeActionAsset.cleanUp();
   }
}
