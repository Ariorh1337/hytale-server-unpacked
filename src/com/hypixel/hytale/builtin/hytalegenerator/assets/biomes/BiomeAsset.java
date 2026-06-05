package com.hypixel.hytale.builtin.hytalegenerator.assets.biomes;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetKeyValidator;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.PropRuntime;
import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.environmentproviders.ConstantEnvironmentProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.environmentproviders.EnvironmentProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.ConstantMaterialProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.MaterialProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.propruntime.PropRuntimeAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.terrains.DensityTerrainAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.terrains.TerrainAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.tintproviders.ConstantTintProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.tintproviders.TintProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.biome.Biome;
import com.hypixel.hytale.builtin.hytalegenerator.biome.SimpleBiome;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.environmentproviders.EnvironmentProvider;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.material.MaterialCache;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.referencebundle.ReferenceBundle;
import com.hypixel.hytale.builtin.hytalegenerator.rng.SeedBox;
import com.hypixel.hytale.builtin.hytalegenerator.tintproviders.TintProvider;
import com.hypixel.hytale.builtin.hytalegenerator.workerindexer.WorkerIndexer;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.ValidatorCache;
import javax.annotation.Nonnull;

public class BiomeAsset implements JsonAssetWithMap<String, DefaultAssetMap<String, BiomeAsset>>, Cleanable {
   @Nonnull
   public static final ValidatorCache<String> VALIDATOR_CACHE = new ValidatorCache<>(new AssetKeyValidator<>(BiomeAsset::getAssetStore));
   private static AssetStore<String, BiomeAsset, DefaultAssetMap<String, BiomeAsset>> STORE;
   public static final String NAME_KEY = "Name";
   @Nonnull
   public static final AssetBuilderCodec<String, BiomeAsset> CODEC = AssetBuilderCodec.builder(
         BiomeAsset.class,
         BiomeAsset::new,
         Codec.STRING,
         (asset, value) -> asset.id = value,
         asset -> asset.id,
         (asset, value) -> asset.data = value,
         asset -> asset.data
      )
      .append(new KeyedCodec<>("Terrain", TerrainAsset.CODEC, true), (asset, value) -> asset.terrainAsset = value, asset -> asset.terrainAsset)
      .add()
      .append(
         new KeyedCodec<>("FloatingFunctionNodes", new ArrayCodec<>(DensityAsset.CODEC, DensityAsset[]::new), true),
         (asset, value) -> asset.floatingFunctionNodeAssets = value,
         asset -> asset.floatingFunctionNodeAssets
      )
      .add()
      .append(new KeyedCodec<>("Name", Codec.STRING, true), (asset, value) -> asset.biomeName = value, asset -> asset.biomeName)
      .add()
      .append(
         new KeyedCodec<>("MaterialProvider", MaterialProviderAsset.CODEC, true),
         (asset, value) -> asset.materialProviderAsset = value,
         asset -> asset.materialProviderAsset
      )
      .add()
      .append(
         new KeyedCodec<>("Props", new ArrayCodec<>(PropRuntimeAsset.CODEC, PropRuntimeAsset[]::new), true),
         (asset, value) -> asset.propRuntimeAssets = value,
         asset -> asset.propRuntimeAssets
      )
      .add()
      .append(
         new KeyedCodec<>("EnvironmentProvider", EnvironmentProviderAsset.CODEC, true),
         (asset, value) -> asset.environmentProviderAsset = value,
         asset -> asset.environmentProviderAsset
      )
      .add()
      .append(
         new KeyedCodec<>("TintProvider", TintProviderAsset.CODEC, true), (asset, value) -> asset.tintProviderAsset = value, asset -> asset.tintProviderAsset
      )
      .add()
      .build();
   private String id;
   private AssetExtraInfo.Data data;
   private TerrainAsset terrainAsset = new DensityTerrainAsset();
   private MaterialProviderAsset materialProviderAsset = new ConstantMaterialProviderAsset();
   private PropRuntimeAsset[] propRuntimeAssets = new PropRuntimeAsset[0];
   private EnvironmentProviderAsset environmentProviderAsset = new ConstantEnvironmentProviderAsset();
   private TintProviderAsset tintProviderAsset = new ConstantTintProviderAsset();
   private String biomeName = "DefaultName";
   private DensityAsset[] floatingFunctionNodeAssets = new DensityAsset[0];

   @Nonnull
   public static AssetStore<String, BiomeAsset, DefaultAssetMap<String, BiomeAsset>> getAssetStore() {
      if (STORE == null) {
         STORE = AssetRegistry.getAssetStore(BiomeAsset.class);
      }

      return STORE;
   }

   private BiomeAsset() {
   }

   public void setId(@Nonnull String id) {
      this.id = id;
   }

   @Override
   public void cleanUp() {
      this.terrainAsset.cleanUp();
      this.materialProviderAsset.cleanUp();

      for (PropRuntimeAsset propRuntimeAsset : this.propRuntimeAssets) {
         propRuntimeAsset.cleanUp();
      }

      this.environmentProviderAsset.cleanUp();
      this.tintProviderAsset.cleanUp();

      for (DensityAsset densityAsset : this.floatingFunctionNodeAssets) {
         densityAsset.cleanUp();
      }
   }

   @Nonnull
   public Biome build(
      @Nonnull MaterialCache materialCache, @Nonnull SeedBox parentSeed, @Nonnull ReferenceBundle referenceBundle, @Nonnull WorkerIndexer.Id workerId
   ) {
      MaterialProvider<Material> materialProvider = this.materialProviderAsset
         .build(new MaterialProviderAsset.Argument(parentSeed, materialCache, referenceBundle, workerId));
      Density density = this.terrainAsset.buildDensity(parentSeed, referenceBundle, workerId);
      EnvironmentProvider provider = EnvironmentProvider.noEnvironmentProvider();
      if (this.environmentProviderAsset != null) {
         provider = this.environmentProviderAsset.build(new EnvironmentProviderAsset.Argument(parentSeed, materialCache, referenceBundle, workerId));
      }

      TintProvider tints = TintProvider.noTintProvider();
      if (this.tintProviderAsset != null) {
         tints = this.tintProviderAsset.build(new TintProviderAsset.Argument(parentSeed, materialCache, referenceBundle, workerId));
      }

      SimpleBiome biome = new SimpleBiome(this.biomeName, density, materialProvider, provider, tints);

      for (PropRuntimeAsset fieldAsset : this.propRuntimeAssets) {
         if (!fieldAsset.isSkip()) {
            PropRuntime propRuntime = new PropRuntime(
               fieldAsset.getRuntime(), fieldAsset.buildPropDistribution(parentSeed, materialCache, fieldAsset.getRuntime(), referenceBundle, workerId)
            );
            biome.addPropFieldTo(propRuntime);
         }
      }

      return biome;
   }

   public String getBiomeName() {
      return this.biomeName;
   }

   public String getId() {
      return this.id;
   }
}
