package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.AllNodeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.NodeSelector;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ImportedNodeSelectorAsset extends NodeSelectorAsset {
   @Nonnull
   public static final BuilderCodec<ImportedNodeSelectorAsset> CODEC = BuilderCodec.builder(
         ImportedNodeSelectorAsset.class, ImportedNodeSelectorAsset::new, NodeSelectorAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Name", Codec.STRING, true), (asset, value) -> asset.name = value, asset -> asset.name)
      .add()
      .build();
   @Nonnull
   private String name = "";

   @NonNullDecl
   @Override
   public NodeSelector build(@NonNullDecl GraphGeneratorAsset.Argument argument) {
      if (super.skip()) {
         return AllNodeSelector.INSTANCE;
      } else if (this.name != null && !this.name.isEmpty()) {
         NodeSelectorAsset exportedAsset = NodeSelectorAsset.getExportedAsset(this.name);
         return exportedAsset == null ? AllNodeSelector.INSTANCE : exportedAsset.build(argument);
      } else {
         HytaleLogger.getLogger().atWarning().log("An exported NodeSelector with the name does not exist: " + this.name);
         return AllNodeSelector.INSTANCE;
      }
   }
}
