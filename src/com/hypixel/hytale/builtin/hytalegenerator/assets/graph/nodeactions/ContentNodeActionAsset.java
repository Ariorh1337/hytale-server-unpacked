package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeactions;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.contentsuppliers.ConstantContentSupplierAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.contentsuppliers.ContentSupplierAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.ContentNodeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.EmptyNodeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.NodeAction;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class ContentNodeActionAsset extends NodeActionAsset {
   @Nonnull
   public static final BuilderCodec<ContentNodeActionAsset> CODEC = BuilderCodec.builder(
         ContentNodeActionAsset.class, ContentNodeActionAsset::new, NodeActionAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("Content", ContentSupplierAsset.CODEC, true),
         (asset, value) -> asset.contentSupplierAsset = value,
         asset -> asset.contentSupplierAsset
      )
      .add()
      .build();
   @Nonnull
   private ContentSupplierAsset contentSupplierAsset = ConstantContentSupplierAsset.INSTANCE;

   @Nonnull
   @Override
   public NodeAction build(@Nonnull GraphGeneratorAsset.Argument argument) {
      return super.skip() ? EmptyNodeAction.INSTANCE : new ContentNodeAction(this.contentSupplierAsset.build(argument));
   }

   @Override
   public void cleanUp() {
      this.contentSupplierAsset.cleanUp();
   }
}
