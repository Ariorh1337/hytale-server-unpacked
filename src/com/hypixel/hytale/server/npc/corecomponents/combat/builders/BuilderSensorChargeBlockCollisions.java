package com.hypixel.hytale.server.npc.corecomponents.combat.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.core.asset.type.blockset.config.BlockSet;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.Feature;
import com.hypixel.hytale.server.npc.asset.builder.holder.AssetHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.AssetValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.asset.BlockSetExistsValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.corecomponents.combat.BodyMotionCharge;
import com.hypixel.hytale.server.npc.corecomponents.combat.SensorChargeBlockCollisions;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import javax.annotation.Nonnull;

public class BuilderSensorChargeBlockCollisions extends BuilderSensorBase {
   protected final AssetHolder blockFilter = new AssetHolder();

   @Nonnull
   public Sensor build(@Nonnull BuilderSupport builderSupport) {
      return new SensorChargeBlockCollisions(this, builderSupport);
   }

   @Nonnull
   @Override
   public String getShortDescription() {
      return "Match when the preceding BodyMotion Charge reports at least one block collision this tick.";
   }

   @Nonnull
   @Override
   public String getLongDescription() {
      return "Match when the preceding BodyMotion Charge reports at least one block collision in the current rail-step tick. Provides a BlockCollisionProvider exposing the list of BlockHit entries to downstream actions.";
   }

   @Nonnull
   @Override
   public BuilderDescriptorState getBuilderDescriptorState() {
      return BuilderDescriptorState.Stable;
   }

   @Nonnull
   @Override
   public Builder<Sensor> readConfig(@Nonnull JsonElement data) {
      this.requirePreceding(
         BodyMotionCharge.class, BuilderDescriptorState.Stable, "Requires a preceding BodyMotionCharge in the same instruction list scope", null
      );
      this.getAsset(
         data,
         "BlockFilter",
         this.blockFilter,
         "",
         BlockSetExistsValidator.withConfig(AssetValidator.CanBeEmpty),
         BuilderDescriptorState.Stable,
         "Optional BlockSet filter for reported collisions. Empty means pass all blocks",
         null
      );
      this.provideFeature(Feature.BlockHits);
      return this;
   }

   public int getBlockFilterSet(@Nonnull BuilderSupport support) {
      String key = this.blockFilter.get(support.getExecutionContext());
      if (key != null && !key.isEmpty()) {
         int index = BlockSet.getAssetMap().getIndex(key);
         if (index == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Unknown BlockSet: " + key);
         } else {
            return index;
         }
      } else {
         return Integer.MIN_VALUE;
      }
   }
}
