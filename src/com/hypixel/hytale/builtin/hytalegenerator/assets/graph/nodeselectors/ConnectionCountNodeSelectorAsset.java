package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.nodeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.AllNodeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.ConnectionCountNodeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.NodeSelector;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import java.util.List;
import javax.annotation.Nonnull;

public class ConnectionCountNodeSelectorAsset extends NodeSelectorAsset {
   @Nonnull
   public static final BuilderCodec<ConnectionCountNodeSelectorAsset> CODEC = BuilderCodec.builder(
         ConnectionCountNodeSelectorAsset.class, ConnectionCountNodeSelectorAsset::new, NodeSelectorAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Counts", new ArrayCodec<>(Codec.INTEGER, Integer[]::new), true), (asset, value) -> asset.counts = value, asset -> asset.counts)
      .add()
      .build();
   @Nonnull
   private Integer[] counts = new Integer[0];

   @Nonnull
   @Override
   public NodeSelector build(@Nonnull GraphGeneratorAsset.Argument argument) {
      return super.skip() ? AllNodeSelector.INSTANCE : new ConnectionCountNodeSelector(List.of(this.counts));
   }
}
