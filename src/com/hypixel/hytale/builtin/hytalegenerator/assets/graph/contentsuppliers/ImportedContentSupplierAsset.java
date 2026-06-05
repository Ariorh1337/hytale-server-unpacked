package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.contentsuppliers;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.contentsuppliers.ConstantContentSupplier;
import com.hypixel.hytale.builtin.hytalegenerator.graph.contentsuppliers.ContentSupplier;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ImportedContentSupplierAsset extends ContentSupplierAsset {
   @Nonnull
   public static final BuilderCodec<ImportedContentSupplierAsset> CODEC = BuilderCodec.builder(
         ImportedContentSupplierAsset.class, ImportedContentSupplierAsset::new, ContentSupplierAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Name", Codec.STRING, true), (asset, value) -> asset.name = value, asset -> asset.name)
      .add()
      .build();
   @Nonnull
   private String name = "";

   @NonNullDecl
   @Override
   public ContentSupplier build(@NonNullDecl GraphGeneratorAsset.Argument argument) {
      if (super.skip()) {
         return ConstantContentSupplier.INSTANCE;
      } else if (this.name != null && !this.name.isEmpty()) {
         ContentSupplierAsset exportedAsset = ContentSupplierAsset.getExportedAsset(this.name);
         return exportedAsset == null ? ConstantContentSupplier.INSTANCE : exportedAsset.build(argument);
      } else {
         HytaleLogger.getLogger().atWarning().log("An exported ContentSupplier with the name does not exist: " + this.name);
         return ConstantContentSupplier.INSTANCE;
      }
   }
}
