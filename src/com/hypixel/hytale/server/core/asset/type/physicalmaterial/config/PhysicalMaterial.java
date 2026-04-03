package com.hypixel.hytale.server.core.asset.type.physicalmaterial.config;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetKeyValidator;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.validation.ValidatorCache;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import java.lang.ref.SoftReference;
import javax.annotation.Nonnull;

public class PhysicalMaterial
   implements JsonAssetWithMap<String, IndexedLookupTableAssetMap<String, PhysicalMaterial>>,
   NetworkSerializable<com.hypixel.hytale.protocol.PhysicalMaterial> {
   public static final int EMPTY_ID = 0;
   public static final String EMPTY = "EMPTY";
   public static final PhysicalMaterial EMPTY_PHYSICAL_MATERIAL = new PhysicalMaterial("EMPTY");
   public static final AssetBuilderCodec<String, PhysicalMaterial> CODEC = AssetBuilderCodec.builder(
         PhysicalMaterial.class,
         PhysicalMaterial::new,
         Codec.STRING,
         (mat, s) -> mat.id = s,
         mat -> mat.id,
         (asset, data) -> asset.data = data,
         asset -> asset.data
      )
      .appendInherited(
         new KeyedCodec<>("ReflectionCoeff", Codec.FLOAT),
         (mat, v) -> mat.reflectionCoeff = v,
         mat -> mat.reflectionCoeff,
         (mat, parent) -> mat.reflectionCoeff = parent.reflectionCoeff
      )
      .addValidator(Validators.range(0.0F, 1.0F))
      .add()
      .<Float>appendInherited(
         new KeyedCodec<>("GainPerBlock", Codec.FLOAT),
         (mat, v) -> mat.gainPerBlock = v,
         mat -> mat.gainPerBlock,
         (mat, parent) -> mat.gainPerBlock = parent.gainPerBlock
      )
      .addValidator(Validators.min(0.0F))
      .add()
      .<Float>appendInherited(
         new KeyedCodec<>("HFGainPerBlock", Codec.FLOAT),
         (mat, v) -> mat.hfGainPerBlock = v,
         mat -> mat.hfGainPerBlock,
         (mat, parent) -> mat.hfGainPerBlock = parent.hfGainPerBlock
      )
      .addValidator(Validators.min(0.0F))
      .add()
      .<Float>appendInherited(
         new KeyedCodec<>("ShelterOpacity", Codec.FLOAT),
         (mat, v) -> mat.shelterOpacity = v,
         mat -> mat.shelterOpacity,
         (mat, parent) -> mat.shelterOpacity = parent.shelterOpacity
      )
      .addValidator(Validators.range(0.0F, 1.0F))
      .add()
      .afterDecode(PhysicalMaterial::processConfig)
      .build();
   public static final ValidatorCache<String> VALIDATOR_CACHE = new ValidatorCache<>(new AssetKeyValidator<>(PhysicalMaterial::getAssetStore));
   private static AssetStore<String, PhysicalMaterial, IndexedLookupTableAssetMap<String, PhysicalMaterial>> ASSET_STORE;
   protected AssetExtraInfo.Data data;
   protected String id;
   protected float reflectionCoeff;
   protected float gainPerBlock;
   protected float hfGainPerBlock;
   protected float shelterOpacity;
   private SoftReference<com.hypixel.hytale.protocol.PhysicalMaterial> cachedPacket;

   public static AssetStore<String, PhysicalMaterial, IndexedLookupTableAssetMap<String, PhysicalMaterial>> getAssetStore() {
      if (ASSET_STORE == null) {
         ASSET_STORE = AssetRegistry.getAssetStore(PhysicalMaterial.class);
      }

      return ASSET_STORE;
   }

   public static IndexedLookupTableAssetMap<String, PhysicalMaterial> getAssetMap() {
      return (IndexedLookupTableAssetMap<String, PhysicalMaterial>)getAssetStore().getAssetMap();
   }

   public PhysicalMaterial(String id) {
      this.id = id;
   }

   protected PhysicalMaterial() {
   }

   @Nonnull
   public com.hypixel.hytale.protocol.PhysicalMaterial toPacket() {
      com.hypixel.hytale.protocol.PhysicalMaterial cached = this.cachedPacket == null ? null : this.cachedPacket.get();
      if (cached != null) {
         return cached;
      }

      com.hypixel.hytale.protocol.PhysicalMaterial packet = new com.hypixel.hytale.protocol.PhysicalMaterial();
      packet.id = this.id;
      packet.reflectionCoeff = this.reflectionCoeff;
      packet.gainPerBlock = this.gainPerBlock;
      packet.hFGainPerBlock = this.hfGainPerBlock;
      packet.shelterOpacity = this.shelterOpacity;
      this.cachedPacket = new SoftReference<>(packet);
      return packet;
   }

   public String getId() {
      return this.id;
   }

   public float getReflectionCoeff() {
      return this.reflectionCoeff;
   }

   public float getGainPerBlock() {
      return this.gainPerBlock;
   }

   public float getHfGainPerBlock() {
      return this.hfGainPerBlock;
   }

   public float getShelterOpacity() {
      return this.shelterOpacity;
   }

   protected void processConfig() {
   }

   @Nonnull
   @Override
   public String toString() {
      return "PhysicalMaterial{id='"
         + this.id
         + "', reflectionCoeff="
         + this.reflectionCoeff
         + ", gainPerBlock="
         + this.gainPerBlock
         + ", hfGainPerBlock="
         + this.hfGainPerBlock
         + ", shelterOpacity="
         + this.shelterOpacity
         + "}";
   }
}
