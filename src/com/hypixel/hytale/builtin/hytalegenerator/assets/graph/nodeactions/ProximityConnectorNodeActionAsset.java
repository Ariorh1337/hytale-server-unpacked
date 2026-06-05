package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeactions;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeselectors.AllNodeSelectorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeselectors.NodeSelectorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.EmptyNodeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.NodeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.ProximityConnectorNodeAction;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class ProximityConnectorNodeActionAsset extends NodeActionAsset {
   @Nonnull
   public static final BuilderCodec<ProximityConnectorNodeActionAsset> CODEC = BuilderCodec.builder(
         ProximityConnectorNodeActionAsset.class, ProximityConnectorNodeActionAsset::new, NodeActionAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Range", Codec.DOUBLE, true), (asset, value) -> asset.range = value, asset -> asset.range)
      .addValidator(Validators.greaterThanOrEqual(0.0))
      .add()
      .append(
         new KeyedCodec<>("NodeSelector", NodeSelectorAsset.CODEC, true), (asset, value) -> asset.nodeSelectorAsset = value, asset -> asset.nodeSelectorAsset
      )
      .add()
      .<Integer>append(new KeyedCodec<>("Cap", Codec.INTEGER, false), (asset, value) -> asset.cap = value, asset -> asset.cap)
      .addValidator(Validators.greaterThanOrEqual(-1))
      .add()
      .append(new KeyedCodec<>("Seed", Codec.STRING, false), (asset, value) -> asset.seed = value, asset -> asset.seed)
      .add()
      .build();
   private double range = 0.0;
   @Nonnull
   private NodeSelectorAsset nodeSelectorAsset = new AllNodeSelectorAsset();
   private int cap = -1;
   @Nonnull
   private String seed = "";

   @Nonnull
   @Override
   public NodeAction build(@Nonnull GraphGeneratorAsset.Argument argument) {
      if (super.skip()) {
         return EmptyNodeAction.INSTANCE;
      }

      int seedInt = argument.parentSeed.child(this.seed).createSupplier().get();
      return new ProximityConnectorNodeAction(this.range, this.nodeSelectorAsset.build(argument), seedInt, this.cap);
   }

   @Override
   public void cleanUp() {
      this.nodeSelectorAsset.cleanUp();
   }
}
