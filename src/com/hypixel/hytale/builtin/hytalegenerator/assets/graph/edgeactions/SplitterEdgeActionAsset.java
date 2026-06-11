package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.edgeactions;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.contentsuppliers.ConstantContentSupplierAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.contentsuppliers.ContentSupplierAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeactions.EdgeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeactions.SplitterEdgeAction;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class SplitterEdgeActionAsset extends EdgeActionAsset {
   public static final SplitterEdgeActionAsset INSTANCE = new SplitterEdgeActionAsset();
   @Nonnull
   public static final BuilderCodec<SplitterEdgeActionAsset> CODEC = BuilderCodec.builder(
         SplitterEdgeActionAsset.class, SplitterEdgeActionAsset::new, EdgeActionAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("NodeCount", Codec.INTEGER, true), (asset, value) -> asset.nodeCount = value, asset -> asset.nodeCount)
      .addValidator(Validators.greaterThanOrEqual(0))
      .add()
      .append(
         new KeyedCodec<>("Content", ContentSupplierAsset.CODEC, true),
         (asset, value) -> asset.contentSupplierAsset = value,
         asset -> asset.contentSupplierAsset
      )
      .add()
      .build();
   private int nodeCount = 0;
   @Nonnull
   private ContentSupplierAsset contentSupplierAsset = ConstantContentSupplierAsset.INSTANCE;

   @Nonnull
   @Override
   public EdgeAction build(@Nonnull GraphGeneratorAsset.Argument argument) {
      assert this.nodeCount >= 0;
      return new SplitterEdgeAction(this.contentSupplierAsset.build(argument), this.nodeCount);
   }

   @Override
   public void cleanUp() {
      this.contentSupplierAsset.cleanUp();
   }
}
