package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.passes;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphContentAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.graph.passes.EmptyGraphPass;
import com.hypixel.hytale.builtin.hytalegenerator.graph.passes.GraphPass;
import com.hypixel.hytale.builtin.hytalegenerator.graph.passes.ManualGraphPass;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class ManualGraphPassAsset extends GraphPassAsset implements Cleanable {
   @Nonnull
   public static final BuilderCodec<ManualGraphPassAsset> CODEC = BuilderCodec.builder(
         ManualGraphPassAsset.class, ManualGraphPassAsset::new, GraphPassAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("Nodes", new ArrayCodec<>(ManualGraphPassAsset.NodeAsset.CODEC, ManualGraphPassAsset.NodeAsset[]::new), true),
         (asset, value) -> asset.nodeAssets = value,
         asset -> asset.nodeAssets
      )
      .add()
      .append(new KeyedCodec<>("StatsLabel", Codec.STRING, true), (asset, value) -> asset.statsLabel = value, asset -> asset.statsLabel)
      .add()
      .build();
   @Nonnull
   private ManualGraphPassAsset.NodeAsset[] nodeAssets = new ManualGraphPassAsset.NodeAsset[0];
   @Nonnull
   private String statsLabel = "";

   @Nonnull
   @Override
   public GraphPass build(@Nonnull GraphGeneratorAsset.Argument argument) {
      if (super.skip()) {
         return EmptyGraphPass.INSTANCE;
      }

      List<ManualGraphPass.NodeEntry> nodeEntries = new ArrayList<>(this.nodeAssets.length);

      for (ManualGraphPassAsset.NodeAsset nodeAsset : this.nodeAssets) {
         GraphSpace.Content content = nodeAsset.graphContentAsset.build(argument);
         nodeEntries.add(new ManualGraphPass.NodeEntry(nodeAsset.position, content, nodeAsset.name, nodeAsset.connections));
      }

      return new ManualGraphPass(nodeEntries, this.statsLabel);
   }

   @Override
   public void cleanUp() {
      for (ManualGraphPassAsset.NodeAsset nodeAsset : this.nodeAssets) {
         nodeAsset.graphContentAsset.cleanUp();
      }
   }

   public static class NodeAsset implements JsonAssetWithMap<String, DefaultAssetMap<String, ManualGraphPassAsset.NodeAsset>> {
      @Nonnull
      public static final AssetBuilderCodec<String, ManualGraphPassAsset.NodeAsset> CODEC = AssetBuilderCodec.builder(
            ManualGraphPassAsset.NodeAsset.class,
            ManualGraphPassAsset.NodeAsset::new,
            Codec.STRING,
            (asset, value) -> asset.id = value,
            asset -> asset.id,
            (asset, value) -> asset.data = value,
            asset -> asset.data
         )
         .append(new KeyedCodec<>("Position", Vector3dUtil.CODEC, true), (asset, value) -> asset.position = value, asset -> asset.position)
         .add()
         .append(
            new KeyedCodec<>("Content", GraphContentAsset.CODEC, true), (asset, value) -> asset.graphContentAsset = value, asset -> asset.graphContentAsset
         )
         .add()
         .append(new KeyedCodec<>("Name", Codec.STRING, true), (asset, value) -> asset.name = value, asset -> asset.name)
         .add()
         .append(
            new KeyedCodec<>("Connections", new ArrayCodec<>(Codec.STRING, String[]::new), false),
            (asset, value) -> asset.connections = value,
            asset -> asset.connections
         )
         .add()
         .build();
      private String id;
      private AssetExtraInfo.Data data;
      @Nonnull
      private Vector3d position = new Vector3d();
      @Nonnull
      private GraphContentAsset graphContentAsset = GraphContentAsset.INSTANCE;
      @Nonnull
      private String[] connections = new String[0];
      @Nonnull
      private String name = "";

      @Nonnull
      public String getId() {
         return "";
      }
   }
}
