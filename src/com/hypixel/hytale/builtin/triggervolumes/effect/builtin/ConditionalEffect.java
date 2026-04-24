package com.hypixel.hytale.builtin.triggervolumes.effect.builtin;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.builtin.triggervolumes.asset.TriggerEffectAsset;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerContext;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEventType;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ConditionalEffect extends TriggerEffect {
   @Nonnull
   public static final BuilderCodec<ConditionalEffect> CODEC = BuilderCodec.builder(ConditionalEffect.class, ConditionalEffect::new, BASE_CODEC)
      .append(new KeyedCodec<>("Permission", Codec.STRING, false), (e, v) -> e.permission = v, e -> e.permission)
      .add()
      .append(new KeyedCodec<>("Cooldown", Codec.FLOAT, false), (e, v) -> e.cooldown = v, e -> e.cooldown)
      .add()
      .append(new KeyedCodec<>("Effect", TriggerEffect.CODEC), (e, v) -> e.innerEffect = v, e -> e.innerEffect)
      .add()
      .append(new KeyedCodec<>("EffectAssetRef", Codec.STRING, false), (e, v) -> e.effectAssetRef = v, e -> e.effectAssetRef)
      .add()
      .build();
   @Nullable
   private String permission;
   private float cooldown = 0.0F;
   private TriggerEffect innerEffect;
   @Nullable
   private String effectAssetRef;
   private final transient Map<UUID, Long> cooldownTimestamps = new ConcurrentHashMap<>();
   private transient TriggerEffect resolvedEffect;

   @Nonnull
   public static ConditionalEffect create(@Nonnull TriggerEventType eventType, @Nullable String permission, float cooldown, @Nonnull TriggerEffect innerEffect) {
      ConditionalEffect effect = new ConditionalEffect();
      effect.setEventType(eventType);
      effect.permission = permission;
      effect.cooldown = cooldown;
      effect.innerEffect = innerEffect;
      return effect;
   }

   @Nullable
   public String getEffectAssetRef() {
      return this.effectAssetRef;
   }

   public void setEffectAssetRef(@Nullable String effectAssetRef) {
      this.effectAssetRef = effectAssetRef;
      this.resolvedEffect = null;
   }

   @Nullable
   private TriggerEffect resolveInnerEffect() {
      if (this.innerEffect != null) {
         return this.innerEffect;
      } else if (this.effectAssetRef == null) {
         return null;
      } else if (this.resolvedEffect != null) {
         return this.resolvedEffect;
      } else {
         AssetStore<String, TriggerEffectAsset, DefaultAssetMap<String, TriggerEffectAsset>> effectAssetStore = AssetRegistry.getAssetStore(
            TriggerEffectAsset.class
         );
         if (effectAssetStore == null) {
            return null;
         } else {
            TriggerEffectAsset asset = (TriggerEffectAsset)((DefaultAssetMap)effectAssetStore.getAssetMap()).getAsset(this.effectAssetRef);
            if (asset != null && asset.getEffects().length != 0) {
               this.resolvedEffect = asset.getEffects()[0];
               return this.resolvedEffect;
            } else {
               return null;
            }
         }
      }
   }

   @Override
   public void execute(@Nonnull TriggerContext context) {
      TriggerEffect effect = this.resolveInnerEffect();
      if (effect != null) {
         Ref<EntityStore> entityRef = context.getEntityRef();
         Store<EntityStore> store = context.getStore();
         if (this.permission != null) {
            PlayerRef playerRef = store.getComponent(entityRef, PlayerRef.getComponentType());
            if (playerRef == null || !playerRef.hasPermission(this.permission)) {
               return;
            }
         }

         if (this.cooldown > 0.0F) {
            UUIDComponent uuidComponent = store.getComponent(entityRef, UUIDComponent.getComponentType());
            if (uuidComponent != null) {
               UUID uuid = uuidComponent.getUuid();
               long now = System.nanoTime();
               Long lastFire = this.cooldownTimestamps.get(uuid);
               if (lastFire != null) {
                  double elapsedSeconds = (now - lastFire) / 1.0E9;
                  if (elapsedSeconds < this.cooldown) {
                     return;
                  }
               }

               this.cooldownTimestamps.put(uuid, now);
            }
         }

         effect.execute(context);
      }
   }

   @Override
   public void onEntityExit(@Nonnull UUID entityUuid) {
      this.cooldownTimestamps.remove(entityUuid);
      TriggerEffect effect = this.resolveInnerEffect();
      if (effect != null) {
         effect.onEntityExit(entityUuid);
      }
   }
}
