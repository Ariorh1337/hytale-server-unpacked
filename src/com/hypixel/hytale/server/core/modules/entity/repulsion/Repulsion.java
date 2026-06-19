package com.hypixel.hytale.server.core.modules.entity.repulsion;

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

public class Repulsion implements Component<EntityStore> {
   public static final int VERSION = 1;
   @Nonnull
   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
   public static final BuilderCodec<Repulsion> CODEC = BuilderCodec.<Repulsion>builder(Repulsion.class, Repulsion::new)
      .codecVersion(1)
      .versioned()
      .<Integer>append(
         new KeyedCodec<>("RepulsionConfigIndex", Codec.INTEGER),
         (repulsion, integer) -> repulsion.indexToMigrate = integer,
         repulsion -> repulsion.indexToMigrate
      )
      .setVersionRange(0, 0)
      .add()
      .<String>append(
         new KeyedCodec<>("RepulsionConfigId", Codec.STRING), (repulsion, id) -> repulsion.repulsionConfigId = id, repulsion -> repulsion.repulsionConfigId
      )
      .setVersionRange(1, 1)
      .add()
      .afterDecode((repulsion, info) -> {
         if (info.getVersion() == 0) {
            repulsion.repulsionConfigId = legacyIndexToId(repulsion.indexToMigrate);
            repulsion.migrated = true;
            repulsion.indexToMigrate = -1;
         }

         String id = repulsion.repulsionConfigId;
         if (id == null) {
            repulsion.repulsionConfigIndex = -1;
         } else {
            int index = RepulsionConfig.getAssetMap().getIndexOrDefault(id, -1);
            if (index == -1) {
               LOGGER.at(Level.WARNING).log("RepulsionConfig '%s' does not exist; clearing the reference", id);
               repulsion.repulsionConfigId = null;
            }

            repulsion.repulsionConfigIndex = index;
         }
      })
      .build();
   private int indexToMigrate = -1;
   @Nullable
   private String repulsionConfigId;
   private int repulsionConfigIndex = -1;
   private boolean migrated;
   private boolean isNetworkOutdated = true;

   public static ComponentType<EntityStore, Repulsion> getComponentType() {
      return EntityModule.get().getRepulsionComponentType();
   }

   @Nullable
   private static String legacyIndexToId(int index) {
      return switch (index) {
         case -1 -> null;
         case 0 -> "DefaultRepulsion";
         case 1 -> "Minigames";
         default -> {
            LOGGER.at(Level.WARNING).log("Unknown legacy RepulsionConfigIndex %d; dropping the reference", (int)index);
            yield null;
         }
      };
   }

   public Repulsion(@Nonnull RepulsionConfig repulsionConfig) {
      this.repulsionConfigIndex = RepulsionConfig.getAssetMap().getIndexOrDefault(repulsionConfig.getId(), -1);
      if (this.repulsionConfigIndex != -1) {
         this.repulsionConfigId = repulsionConfig.getId();
      }
   }

   protected Repulsion() {
   }

   public int getRepulsionConfigIndex() {
      return this.repulsionConfigIndex;
   }

   @Nullable
   public String getRepulsionConfigId() {
      return this.repulsionConfigId;
   }

   public boolean isMigrated() {
      return this.migrated;
   }

   public void setRepulsionConfig(@Nonnull RepulsionConfig repulsionConfig) {
      int index = RepulsionConfig.getAssetMap().getIndexOrDefault(repulsionConfig.getId(), -1);
      if (index != this.repulsionConfigIndex) {
         this.repulsionConfigIndex = index;
         this.repulsionConfigId = index != -1 ? repulsionConfig.getId() : null;
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
      Repulsion component = new Repulsion();
      component.repulsionConfigId = this.repulsionConfigId;
      component.repulsionConfigIndex = this.repulsionConfigIndex;
      component.migrated = this.migrated;
      return component;
   }
}
