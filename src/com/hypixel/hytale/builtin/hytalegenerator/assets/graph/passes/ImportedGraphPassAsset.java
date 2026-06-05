package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.passes;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.passes.EmptyGraphPass;
import com.hypixel.hytale.builtin.hytalegenerator.graph.passes.GraphPass;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ImportedGraphPassAsset extends GraphPassAsset {
   @Nonnull
   public static final BuilderCodec<ImportedGraphPassAsset> CODEC = BuilderCodec.builder(
         ImportedGraphPassAsset.class, ImportedGraphPassAsset::new, GraphPassAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Name", Codec.STRING, true), (asset, value) -> asset.name = value, asset -> asset.name)
      .add()
      .build();
   @Nonnull
   private String name = "";

   @Override
   public GraphPass build(@NonNullDecl GraphGeneratorAsset.Argument argument) {
      if (super.skip()) {
         return EmptyGraphPass.INSTANCE;
      } else if (this.name != null && !this.name.isEmpty()) {
         GraphPassAsset exportedAsset = GraphPassAsset.getExportedAsset(this.name);
         return exportedAsset == null ? EmptyGraphPass.INSTANCE : exportedAsset.build(argument);
      } else {
         return EmptyGraphPass.INSTANCE;
      }
   }
}
