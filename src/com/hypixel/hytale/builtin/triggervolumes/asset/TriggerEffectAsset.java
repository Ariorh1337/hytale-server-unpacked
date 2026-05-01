package com.hypixel.hytale.builtin.triggervolumes.asset;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.triggervolumes.EntityTargetType;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TriggerEffectAsset implements JsonAssetWithMap<String, DefaultAssetMap<String, TriggerEffectAsset>> {
   @Nonnull
   public static final AssetBuilderCodec<String, TriggerEffectAsset> CODEC = AssetBuilderCodec.builder(
         TriggerEffectAsset.class, TriggerEffectAsset::new, Codec.STRING, (a, id) -> a.id = id, a -> a.id, (a, data) -> a.data = data, a -> a.data
      )
      .append(new KeyedCodec<>("Effects", new ArrayCodec<>(TriggerEffect.CODEC, TriggerEffect[]::new)), (a, v) -> a.effects = v, a -> a.effects)
      .add()
      .append(
         new KeyedCodec<>("TargetTypes", new ArrayCodec<>(new EnumCodec<>(EntityTargetType.class), EntityTargetType[]::new), false),
         (a, v) -> a.targetTypeArray = v,
         a -> a.targetTypeArray
      )
      .add()
      .afterDecode(TriggerEffectAsset::resolveTargetTypes)
      .build();
   private String id;
   private AssetExtraInfo.Data data;
   private TriggerEffect[] effects;
   @Nullable
   private EntityTargetType[] targetTypeArray;
   private transient Set<EntityTargetType> targetTypes;

   private void resolveTargetTypes() {
      this.targetTypes = EnumSet.noneOf(EntityTargetType.class);
      if (this.targetTypeArray != null) {
         this.targetTypes.addAll(Arrays.asList(this.targetTypeArray));
      }

      if (this.targetTypes.isEmpty()) {
         this.targetTypes.add(EntityTargetType.PLAYER);
      }
   }

   @Nonnull
   public static TriggerEffectAsset create(@Nonnull String id, @Nonnull TriggerEffect[] effects) {
      TriggerEffectAsset asset = new TriggerEffectAsset();
      asset.id = id;
      asset.effects = effects;
      asset.resolveTargetTypes();
      return asset;
   }

   @Nonnull
   public String getId() {
      return this.id;
   }

   public void setId(@Nonnull String id) {
      this.id = id;
   }

   @Nonnull
   public TriggerEffect[] getEffects() {
      return this.effects;
   }

   @Nonnull
   public Set<EntityTargetType> getTargetTypes() {
      return this.targetTypes != null ? this.targetTypes : Set.of(EntityTargetType.PLAYER);
   }
}
