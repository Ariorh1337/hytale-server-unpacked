package com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders;

import com.hypixel.hytale.builtin.hytalegenerator.VectorAssetValidatorUtil;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphGenerator;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.GraphMaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class GraphMaterialProviderAsset extends MaterialProviderAsset {
   @Nonnull
   public static final BuilderCodec<GraphMaterialProviderAsset> CODEC = BuilderCodec.builder(
         GraphMaterialProviderAsset.class, GraphMaterialProviderAsset::new, MaterialProviderAsset.ABSTRACT_CODEC
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
   private GraphGeneratorAsset graphGeneratorAsset = new GraphGeneratorAsset();
   @Nonnull
   private String contentLayerName = "";
   @Nonnull
   private Vector3d cacheCellSize = new Vector3d(320.0, 320.0, 320.0);
   private int cacheCapacity = 10;

   @Nonnull
   @Override
   public MaterialProvider<Material> build(@Nonnull MaterialProviderAsset.Argument argument) {
      if (super.skip()) {
         return MaterialProvider.noMaterialProvider();
      }

      GraphGenerator graphGenerator = this.graphGeneratorAsset.build(new GraphGeneratorAsset.Argument(argument, false, true, false, false));
      return new GraphMaterialProvider(graphGenerator, GraphSpace.Content.toIntId(this.contentLayerName), this.cacheCellSize, this.cacheCapacity);
   }

   @Override
   public void cleanUp() {
      this.graphGeneratorAsset.cleanUp();
   }
}
