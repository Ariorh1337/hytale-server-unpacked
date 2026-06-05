package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeactions;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.EmptyNodeAction;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.NodeAction;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ImportedNodeActionAsset extends NodeActionAsset {
   @Nonnull
   public static final BuilderCodec<ImportedNodeActionAsset> CODEC = BuilderCodec.builder(
         ImportedNodeActionAsset.class, ImportedNodeActionAsset::new, NodeActionAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Name", Codec.STRING, true), (asset, value) -> asset.name = value, asset -> asset.name)
      .add()
      .build();
   @Nonnull
   private String name = "";

   @NonNullDecl
   @Override
   public NodeAction build(@NonNullDecl GraphGeneratorAsset.Argument argument) {
      if (super.skip()) {
         return EmptyNodeAction.INSTANCE;
      } else if (this.name != null && !this.name.isEmpty()) {
         NodeActionAsset exportedAsset = NodeActionAsset.getExportedAsset(this.name);
         return exportedAsset == null ? EmptyNodeAction.INSTANCE : exportedAsset.build(argument);
      } else {
         HytaleLogger.getLogger().atWarning().log("An exported NodeAction with the name does not exist: " + this.name);
         return EmptyNodeAction.INSTANCE;
      }
   }
}
