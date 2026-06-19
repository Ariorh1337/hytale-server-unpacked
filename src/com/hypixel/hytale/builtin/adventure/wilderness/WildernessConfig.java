package com.hypixel.hytale.builtin.adventure.wilderness;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.server.core.universe.world.World;
import javax.annotation.Nonnull;

public class WildernessConfig {
   public static final String ID = "Wilderness";
   public static final int HOME_CHUNK_RADIUS_DISABLED = -1;
   public static final boolean DEFAULT_ENABLED = false;
   public static final int DEFAULT_HOME_RADIUS_CHUNKS_OWNED = 8;
   public static final int DEFAULT_HOME_RADIUS_Y_CHUNKS_OWNED = 2;
   public static final int DEFAULT_HOME_RADIUS_CHUNKS_UNOWNED = 2;
   public static final int DEFAULT_HOME_RADIUS_Y_CHUNKS_UNOWNED = 1;
   public static final int DEFAULT_PLAYER_RADIUS_CHUNKS = 2;
   public static final int DEFAULT_PLAYER_RADIUS_Y_CHUNKS = 1;
   public static final WildernessConfig DEFAULT = new WildernessConfig();
   public static final BuilderCodec<WildernessConfig> CODEC = BuilderCodec.builder(WildernessConfig.class, WildernessConfig::new)
      .documentation("Configuration for wilderness tracking.")
      .<Boolean>appendInherited(
         new KeyedCodec<>("Enabled", Codec.BOOLEAN),
         (wildernessConfig, v) -> wildernessConfig.enabled = v,
         wildernessConfig -> wildernessConfig.enabled,
         (wildernessConfig, parent) -> wildernessConfig.enabled = parent.enabled
      )
      .documentation("Enable wilderness tracking in a world.")
      .add()
      .<Integer>appendInherited(
         new KeyedCodec<>("OwnedHomeChunkRadius", Codec.INTEGER),
         (wildernessConfig, v) -> wildernessConfig.ownedHomeChunkRadius = v,
         wildernessConfig -> wildernessConfig.ownedHomeChunkRadius,
         (wildernessConfig, parent) -> wildernessConfig.ownedHomeChunkRadius = parent.ownedHomeChunkRadius
      )
      .documentation("The horizontal chunk radius around an owned player home marker. Set to -1 to disable the radius.")
      .addValidator(Validators.greaterThanOrEqual(-1))
      .add()
      .<Integer>appendInherited(
         new KeyedCodec<>("OwnedHomeChunkRadiusY", Codec.INTEGER),
         (wildernessConfig, v) -> wildernessConfig.ownedHomeChunkRadiusY = v,
         wildernessConfig -> wildernessConfig.ownedHomeChunkRadiusY,
         (wildernessConfig, parent) -> wildernessConfig.ownedHomeChunkRadiusY = parent.ownedHomeChunkRadiusY
      )
      .documentation("The vertical chunk radius around an owned player home marker. Set to -1 to disable the radius.")
      .addValidator(Validators.greaterThanOrEqual(-1))
      .add()
      .<Integer>appendInherited(
         new KeyedCodec<>("UnownedHomeChunkRadius", Codec.INTEGER),
         (wildernessConfig, v) -> wildernessConfig.unownedHomeChunkRadius = v,
         wildernessConfig -> wildernessConfig.unownedHomeChunkRadius,
         (wildernessConfig, parent) -> wildernessConfig.unownedHomeChunkRadius = parent.unownedHomeChunkRadius
      )
      .documentation("The horizontal chunk radius around an unowned player home marker. Set to -1 to disable the radius.")
      .addValidator(Validators.greaterThanOrEqual(-1))
      .add()
      .<Integer>appendInherited(
         new KeyedCodec<>("UnownedHomeChunkRadiusY", Codec.INTEGER),
         (wildernessConfig, v) -> wildernessConfig.unownedHomeChunkRadiusY = v,
         wildernessConfig -> wildernessConfig.unownedHomeChunkRadiusY,
         (wildernessConfig, parent) -> wildernessConfig.unownedHomeChunkRadiusY = parent.unownedHomeChunkRadiusY
      )
      .documentation("The vertical chunk radius around an unowned player home marker. Set to -1 to disable the radius.")
      .addValidator(Validators.greaterThanOrEqual(-1))
      .add()
      .<Integer>appendInherited(
         new KeyedCodec<>("PlayerTrackerChunkRadius", Codec.INTEGER),
         (wildernessConfig, v) -> wildernessConfig.playerTrackerChunkRadius = v,
         wildernessConfig -> wildernessConfig.playerTrackerChunkRadius,
         (wildernessConfig, parent) -> wildernessConfig.playerTrackerChunkRadius = parent.playerTrackerChunkRadius
      )
      .documentation("The horizontal chunk radius around a player to track wilderness in.")
      .addValidator(Validators.greaterThanOrEqual(0))
      .add()
      .<Integer>appendInherited(
         new KeyedCodec<>("PlayerTrackerChunkRadiusY", Codec.INTEGER),
         (wildernessConfig, v) -> wildernessConfig.playerTrackerChunkRadiusY = v,
         wildernessConfig -> wildernessConfig.playerTrackerChunkRadiusY,
         (wildernessConfig, parent) -> wildernessConfig.playerTrackerChunkRadiusY = parent.playerTrackerChunkRadiusY
      )
      .documentation("The horizontal chunk radius around a player to track wilderness in.")
      .addValidator(Validators.greaterThanOrEqual(0))
      .add()
      .build();
   protected boolean enabled = false;
   protected int ownedHomeChunkRadius = 8;
   protected int ownedHomeChunkRadiusY = 2;
   protected int unownedHomeChunkRadius = 2;
   protected int unownedHomeChunkRadiusY = 1;
   protected int playerTrackerChunkRadius = 2;
   protected int playerTrackerChunkRadiusY = 1;

   public boolean isEnabled() {
      return this.enabled;
   }

   public int getOwnedHomeChunkRadius() {
      return this.ownedHomeChunkRadius;
   }

   public int getOwnedHomeChunkRadiusY() {
      return this.ownedHomeChunkRadiusY;
   }

   public int getUnownedHomeChunkRadius() {
      return this.unownedHomeChunkRadius;
   }

   public int getUnownedHomeChunkRadiusY() {
      return this.unownedHomeChunkRadiusY;
   }

   public int getPlayerTrackerChunkRadius() {
      return this.playerTrackerChunkRadius;
   }

   public int getPlayerTrackerChunkRadiusY() {
      return this.playerTrackerChunkRadiusY;
   }

   @Nonnull
   public static WildernessConfig getOrDefault(@Nonnull World world) {
      WildernessConfig config = world.getGameplayConfig().getPluginConfig().get(WildernessConfig.class);
      return config == null ? DEFAULT : config;
   }
}
