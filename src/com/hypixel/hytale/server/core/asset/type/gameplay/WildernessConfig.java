package com.hypixel.hytale.server.core.asset.type.gameplay;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;

public class WildernessConfig {
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
         new KeyedCodec<>("VerticalDistanceBlocks", Codec.INTEGER),
         (wildernessConfig, v) -> wildernessConfig.verticalDistanceBlocks = v,
         wildernessConfig -> wildernessConfig.verticalDistanceBlocks,
         (wildernessConfig, parent) -> wildernessConfig.verticalDistanceBlocks = parent.verticalDistanceBlocks
      )
      .documentation("The vertical distance, in blocks, used when checking wilderness tracking range.")
      .addValidator(Validators.greaterThanOrEqual(0))
      .add()
      .<Integer>appendInherited(
         new KeyedCodec<>("HorizontalDistanceBlocks", Codec.INTEGER),
         (wildernessConfig, v) -> wildernessConfig.horizontalDistanceBlocks = v,
         wildernessConfig -> wildernessConfig.horizontalDistanceBlocks,
         (wildernessConfig, parent) -> wildernessConfig.horizontalDistanceBlocks = parent.horizontalDistanceBlocks
      )
      .documentation("The horizontal distance, in blocks, used when checking wilderness tracking range.")
      .addValidator(Validators.greaterThanOrEqual(0))
      .add()
      .build();
   public static final boolean DEFAULT_ENABLED = false;
   public static final int DEFAULT_VERTICAL_DISTANCE_BLOCKS = 48;
   public static final int DEFAULT_HORIZONTAL_DISTANCE_BLOCKS = 128;
   public static final WildernessConfig DEFAULT = new WildernessConfig();
   protected boolean enabled = false;
   protected int verticalDistanceBlocks = 48;
   protected int horizontalDistanceBlocks = 128;

   public boolean isEnabled() {
      return this.enabled;
   }

   public int getVerticalDistanceBlocks() {
      return this.verticalDistanceBlocks;
   }

   public int getHorizontalDistanceBlocks() {
      return this.horizontalDistanceBlocks;
   }
}
