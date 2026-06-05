package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.edgeactions;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeactions.EdgeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeactions.EmptyEdgeAction;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ImportedEdgeActionAsset extends EdgeActionAsset {
   @Nonnull
   public static final BuilderCodec<ImportedEdgeActionAsset> CODEC = BuilderCodec.builder(
         ImportedEdgeActionAsset.class, ImportedEdgeActionAsset::new, EdgeActionAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Name", Codec.STRING, true), (asset, value) -> asset.name = value, asset -> asset.name)
      .add()
      .build();
   @Nonnull
   private String name = "";

   @NonNullDecl
   @Override
   public EdgeAction build(@NonNullDecl GraphGeneratorAsset.Argument argument) {
      if (super.skip()) {
         return EmptyEdgeAction.INSTANCE;
      } else if (this.name != null && !this.name.isEmpty()) {
         EdgeActionAsset exportedAsset = EdgeActionAsset.getExportedAsset(this.name);
         return exportedAsset == null ? EmptyEdgeAction.INSTANCE : exportedAsset.build(argument);
      } else {
         HytaleLogger.getLogger().atWarning().log("An exported EdgeAction with the name does not exist: " + this.name);
         return EmptyEdgeAction.INSTANCE;
      }
   }
}
