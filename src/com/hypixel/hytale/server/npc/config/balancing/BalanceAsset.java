package com.hypixel.hytale.server.npc.config.balancing;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetKeyValidator;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetCodecMapCodec;
import com.hypixel.hytale.assetstore.codec.ContainedAssetCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.lookup.Priority;
import com.hypixel.hytale.codec.validation.ValidatorCache;
import com.hypixel.hytale.common.util.ArrayUtil;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class BalanceAsset implements JsonAssetWithMap<String, DefaultAssetMap<String, BalanceAsset>> {
   public static final BuilderCodec<BalanceAsset> ABSTRACT_CODEC = BuilderCodec.abstractBuilder(BalanceAsset.class)
      .documentation("Defines various parameters for NPCs relating to combat balancing.")
      .build();
   public static final BuilderCodec<BalanceAsset> BASE_CODEC = BuilderCodec.builder(BalanceAsset.class, BalanceAsset::new, ABSTRACT_CODEC)
      .appendInherited(
         new KeyedCodec<>("EntityEffects", EntityEffect.CHILD_ASSET_CODEC_ARRAY),
         (e, s) -> e.entityEffects = s,
         e -> e.entityEffects,
         (e, p) -> e.entityEffects = p.entityEffects
      )
      .addValidator(EntityEffect.VALIDATOR_CACHE.getArrayValidator())
      .documentation("A list of entity effects to apply to the NPC at spawn time.")
      .add()
      .build();
   public static final AssetCodecMapCodec<String, BalanceAsset> CODEC = new AssetCodecMapCodec<String, BalanceAsset>(
         Codec.STRING, (t, k) -> t.id = k, t -> t.id, (t, data) -> t.data = data, t -> t.data, true
      )
      .register(Priority.DEFAULT, "Default", BalanceAsset.class, BASE_CODEC);
   public static final Codec<String> CHILD_ASSET_CODEC = new ContainedAssetCodec<>(BalanceAsset.class, CODEC);
   public static final ValidatorCache<String> VALIDATOR_CACHE = new ValidatorCache<>(new AssetKeyValidator<>(BalanceAsset::getAssetStore));
   private static AssetStore<String, BalanceAsset, DefaultAssetMap<String, BalanceAsset>> ASSET_STORE;
   private AssetExtraInfo.Data data;
   protected String id;
   protected String[] entityEffects = ArrayUtil.EMPTY_STRING_ARRAY;

   public static AssetStore<String, BalanceAsset, DefaultAssetMap<String, BalanceAsset>> getAssetStore() {
      if (ASSET_STORE == null) {
         ASSET_STORE = AssetRegistry.getAssetStore(BalanceAsset.class);
      }

      return ASSET_STORE;
   }

   public static DefaultAssetMap<String, BalanceAsset> getAssetMap() {
      return (DefaultAssetMap<String, BalanceAsset>)getAssetStore().getAssetMap();
   }

   protected BalanceAsset() {
   }

   public String getId() {
      return this.id;
   }

   public String[] getEntityEffects() {
      return this.entityEffects;
   }

   @Nonnull
   @Override
   public String toString() {
      return "BalanceAsset{data=" + this.data + ", id='" + this.id + "', entityEffects='" + Arrays.toString(this.entityEffects) + "'}";
   }
}
