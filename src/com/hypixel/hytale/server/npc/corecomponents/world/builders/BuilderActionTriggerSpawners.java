package com.hypixel.hytale.server.npc.corecomponents.world.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.AssetHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.BooleanHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.DoubleHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.IntHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.StringHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.AssetValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleSingleValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.IntSingleValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringNullOrNotEmptyValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.asset.ManualSpawnMarkerExistsValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.corecomponents.world.ActionTriggerSpawners;
import com.hypixel.hytale.server.npc.instructions.Action;
import java.util.EnumSet;
import javax.annotation.Nonnull;

public class BuilderActionTriggerSpawners extends BuilderActionBase {
   protected final AssetHolder spawner = new AssetHolder();
   protected final DoubleHolder range = new DoubleHolder();
   protected final IntHolder count = new IntHolder();
   protected final StringHolder markedTargetSlot = new StringHolder();
   protected final BooleanHolder rebindTarget = new BooleanHolder();

   @Nonnull
   @Override
   public String getShortDescription() {
      return "Trigger all, or up to a certain number of manual spawn markers in a radius around the NPC";
   }

   @Nonnull
   @Override
   public String getLongDescription() {
      return this.getShortDescription();
   }

   @Nonnull
   public Action build(@Nonnull BuilderSupport builderSupport) {
      return new ActionTriggerSpawners(this, builderSupport);
   }

   @Nonnull
   public BuilderActionTriggerSpawners readConfig(@Nonnull JsonElement data) {
      this.getAsset(
         data,
         "SpawnMarker",
         this.spawner,
         null,
         ManualSpawnMarkerExistsValidator.withConfig(EnumSet.of(AssetValidator.Config.NULLABLE)),
         BuilderDescriptorState.Stable,
         "The spawn marker type to trigger",
         null
      );
      this.requireDouble(
         data, "Range", this.range, DoubleSingleValidator.greater0(), BuilderDescriptorState.Stable, "The range within which to trigger spawn markers", null
      );
      this.getInt(
         data,
         "Count",
         this.count,
         0,
         IntSingleValidator.greaterEqual0(),
         BuilderDescriptorState.Stable,
         "The number of markers to randomly trigger (0 will trigger all matching validators)",
         null
      );
      this.getString(
         data,
         "TargetSlot",
         this.markedTargetSlot,
         null,
         StringNullOrNotEmptyValidator.get(),
         BuilderDescriptorState.Stable,
         "A target slot to place the first spawned NPC in. If omitted, no slot will be used",
         null
      );
      this.getBoolean(
         data,
         "Rebind",
         this.rebindTarget,
         false,
         BuilderDescriptorState.Stable,
         "Whether the target slot rebinds to the spawned entity, so it survives short reloads or role changes.",
         null
      );
      return this;
   }

   @Nonnull
   @Override
   public BuilderDescriptorState getBuilderDescriptorState() {
      return BuilderDescriptorState.Stable;
   }

   public String getSpawner(@Nonnull BuilderSupport support) {
      return this.spawner.get(support.getExecutionContext());
   }

   public double getRange(@Nonnull BuilderSupport support) {
      return this.range.get(support.getExecutionContext());
   }

   public int getCount(@Nonnull BuilderSupport support) {
      return this.count.get(support.getExecutionContext());
   }

   public int getMarkedTargetSlot(@Nonnull BuilderSupport support) {
      String slot = this.markedTargetSlot.get(support.getExecutionContext());
      return slot == null ? Integer.MIN_VALUE : support.getTargetSlot(slot);
   }

   public boolean isRebindTarget(@Nonnull BuilderSupport support) {
      return this.rebindTarget.get(support.getExecutionContext());
   }
}
