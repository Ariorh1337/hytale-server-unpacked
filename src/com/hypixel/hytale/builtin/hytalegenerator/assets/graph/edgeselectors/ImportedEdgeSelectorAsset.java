package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.edgeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors.AllEdgeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors.EdgeSelector;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ImportedEdgeSelectorAsset extends EdgeSelectorAsset {
   @Nonnull
   public static final BuilderCodec<ImportedEdgeSelectorAsset> CODEC = BuilderCodec.builder(
         ImportedEdgeSelectorAsset.class, ImportedEdgeSelectorAsset::new, EdgeSelectorAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Name", Codec.STRING, true), (asset, value) -> asset.name = value, asset -> asset.name)
      .add()
      .build();
   @Nonnull
   private String name = "";

   @NonNullDecl
   @Override
   public EdgeSelector build(@NonNullDecl GraphGeneratorAsset.Argument argument) {
      if (super.skip()) {
         return AllEdgeSelector.INSTANCE;
      } else if (this.name != null && !this.name.isEmpty()) {
         EdgeSelectorAsset exportedAsset = EdgeSelectorAsset.getExportedAsset(this.name);
         return exportedAsset == null ? AllEdgeSelector.INSTANCE : exportedAsset.build(argument);
      } else {
         HytaleLogger.getLogger().atWarning().log("An exported EdgeSelector with the name does not exist: " + this.name);
         return AllEdgeSelector.INSTANCE;
      }
   }
}
