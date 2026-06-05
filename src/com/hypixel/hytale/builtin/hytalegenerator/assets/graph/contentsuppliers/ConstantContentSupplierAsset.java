package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.contentsuppliers;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphContentAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.contentsuppliers.ConstantContentSupplier;
import com.hypixel.hytale.builtin.hytalegenerator.graph.contentsuppliers.ContentSupplier;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class ConstantContentSupplierAsset extends ContentSupplierAsset {
   public static final ConstantContentSupplierAsset INSTANCE = new ConstantContentSupplierAsset();
   @Nonnull
   public static final BuilderCodec<ConstantContentSupplierAsset> CODEC = BuilderCodec.builder(
         ConstantContentSupplierAsset.class, ConstantContentSupplierAsset::new, ContentSupplierAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Content", GraphContentAsset.CODEC, true), (asset, value) -> asset.contentAsset = value, asset -> asset.contentAsset)
      .add()
      .build();
   @Nonnull
   private GraphContentAsset contentAsset = GraphContentAsset.INSTANCE;

   @Nonnull
   @Override
   public ContentSupplier build(@Nonnull GraphGeneratorAsset.Argument argument) {
      return super.skip() ? ConstantContentSupplier.INSTANCE : new ConstantContentSupplier(this.contentAsset.build(argument));
   }

   @Override
   public void cleanUp() {
      this.contentAsset.cleanUp();
   }
}
