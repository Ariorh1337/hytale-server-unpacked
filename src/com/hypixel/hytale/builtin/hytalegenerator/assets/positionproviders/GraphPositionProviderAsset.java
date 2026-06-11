package com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.VectorAssetValidatorUtil;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphGenerator;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.EmptyPositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.GraphPositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class GraphPositionProviderAsset extends PositionProviderAsset {
   @Nonnull
   public static final BuilderCodec<GraphPositionProviderAsset> CODEC = BuilderCodec.builder(
         GraphPositionProviderAsset.class, GraphPositionProviderAsset::new, PositionProviderAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("GraphGenerator", GraphGeneratorAsset.CODEC, true),
         (asset, value) -> asset.graphGeneratorAsset = value,
         asset -> asset.graphGeneratorAsset
      )
      .add()
      .append(new KeyedCodec<>("ContentLayer", Codec.STRING, true), (asset, value) -> asset.contentLayerName = value, asset -> asset.contentLayerName)
      .add()
      .<Vector3d>append(
         new KeyedCodec<>("CacheCellSize", Vector3dUtil.CODEC, true), (asset, value) -> asset.cacheCellSize = value, asset -> asset.cacheCellSize
      )
      .addValidator(VectorAssetValidatorUtil.greaterThan(0.0))
      .add()
      .<Integer>append(new KeyedCodec<>("CacheCapacity", Codec.INTEGER, true), (asset, value) -> asset.cacheCapacity = value, asset -> asset.cacheCapacity)
      .addValidator(Validators.greaterThanOrEqual(0))
      .add()
      .build();
   @Nonnull
   private GraphGeneratorAsset graphGeneratorAsset = GraphGeneratorAsset.EMPTY;
   @Nonnull
   private String contentLayerName = "";
   @Nonnull
   private Vector3d cacheCellSize = new Vector3d(320.0, 320.0, 320.0);
   private int cacheCapacity = 50;

   @Nonnull
   @Override
   public PositionProvider build(@Nonnull PositionProviderAsset.Argument argument) {
      if (super.skip()) {
         return EmptyPositionProvider.INSTANCE;
      }

      GraphGenerator graphGenerator = this.graphGeneratorAsset.build(new GraphGeneratorAsset.Argument(argument, false, false, false, true));
      int contentLayerId = GraphSpace.Content.toIntId(this.contentLayerName);
      return new GraphPositionProvider(graphGenerator, contentLayerId, this.cacheCellSize, this.cacheCapacity);
   }

   @Override
   public void cleanUp() {
      this.graphGeneratorAsset.cleanUp();
   }
}
