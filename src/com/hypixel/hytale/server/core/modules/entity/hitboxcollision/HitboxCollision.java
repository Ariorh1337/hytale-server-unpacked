package com.hypixel.hytale.server.core.modules.entity.hitboxcollision;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HitboxCollision implements Component<EntityStore> {
   public static final int VERSION = 1;
   @Nonnull
   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
   public static final BuilderCodec<HitboxCollision> CODEC = BuilderCodec.<HitboxCollision>builder(HitboxCollision.class, HitboxCollision::new)
      .codecVersion(1)
      .versioned()
      .<Integer>append(
         new KeyedCodec<>("HitboxCollisionConfigIndex", Codec.INTEGER),
         (hitboxCollision, integer) -> hitboxCollision.indexToMigrate = integer,
         hitboxCollision -> hitboxCollision.indexToMigrate
      )
      .setVersionRange(0, 0)
      .add()
      .<String>append(
         new KeyedCodec<>("HitboxCollisionConfigId", Codec.STRING),
         (hitboxCollision, id) -> hitboxCollision.hitboxCollisionConfigId = id,
         hitboxCollision -> hitboxCollision.hitboxCollisionConfigId
      )
      .setVersionRange(1, 1)
      .add()
      .afterDecode((hitboxCollision, info) -> {
         if (info.getVersion() == 0) {
            hitboxCollision.hitboxCollisionConfigId = legacyIndexToId(hitboxCollision.indexToMigrate);
            hitboxCollision.migrated = true;
            hitboxCollision.indexToMigrate = -1;
         }

         String id = hitboxCollision.hitboxCollisionConfigId;
         if (id == null) {
            hitboxCollision.hitboxCollisionConfigIndex = -1;
         } else {
            int index = HitboxCollisionConfig.getAssetMap().getIndexOrDefault(id, -1);
            if (index == -1) {
               LOGGER.at(Level.WARNING).log("HitboxCollisionConfig '%s' does not exist; clearing the reference", id);
               hitboxCollision.hitboxCollisionConfigId = null;
            }

            hitboxCollision.hitboxCollisionConfigIndex = index;
         }
      })
      .build();
   private int indexToMigrate = -1;
   @Nullable
   private String hitboxCollisionConfigId;
   private int hitboxCollisionConfigIndex = -1;
   private boolean migrated;
   private boolean isNetworkOutdated = true;

   public static ComponentType<EntityStore, HitboxCollision> getComponentType() {
      return EntityModule.get().getHitboxCollisionComponentType();
   }

   @Nullable
   private static String legacyIndexToId(int index) {
      return switch (index) {
         case -1 -> null;
         case 0 -> "HardCollision";
         case 1 -> "SoftCollision";
         default -> {
            LOGGER.at(Level.WARNING).log("Unknown legacy HitboxCollisionConfigIndex %d; dropping the reference", (int)index);
            yield null;
         }
      };
   }

   public HitboxCollision(@Nonnull HitboxCollisionConfig hitboxCollisionConfig) {
      this.hitboxCollisionConfigIndex = HitboxCollisionConfig.getAssetMap().getIndexOrDefault(hitboxCollisionConfig.getId(), -1);
      if (this.hitboxCollisionConfigIndex != -1) {
         this.hitboxCollisionConfigId = hitboxCollisionConfig.getId();
      }
   }

   protected HitboxCollision() {
   }

   public int getHitboxCollisionConfigIndex() {
      return this.hitboxCollisionConfigIndex;
   }

   @Nullable
   public String getHitboxCollisionConfigId() {
      return this.hitboxCollisionConfigId;
   }

   public boolean isMigrated() {
      return this.migrated;
   }

   public void setHitboxCollisionConfig(@Nonnull HitboxCollisionConfig hitboxCollisionConfig) {
      int index = HitboxCollisionConfig.getAssetMap().getIndexOrDefault(hitboxCollisionConfig.getId(), -1);
      if (index != this.hitboxCollisionConfigIndex) {
         this.hitboxCollisionConfigIndex = index;
         this.hitboxCollisionConfigId = index != -1 ? hitboxCollisionConfig.getId() : null;
         this.isNetworkOutdated = true;
      }
   }

   public boolean consumeNetworkOutdated() {
      boolean temp = this.isNetworkOutdated;
      this.isNetworkOutdated = false;
      return temp;
   }

   @Nonnull
   @Override
   public Component<EntityStore> clone() {
      HitboxCollision component = new HitboxCollision();
      component.hitboxCollisionConfigId = this.hitboxCollisionConfigId;
      component.hitboxCollisionConfigIndex = this.hitboxCollisionConfigIndex;
      component.migrated = this.migrated;
      return component;
   }
}
