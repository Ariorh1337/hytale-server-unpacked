package com.hypixel.hytale.server.npc.asset.builder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.core.asset.type.attitude.Attitude;
import com.hypixel.hytale.server.npc.asset.builder.holder.AssetArrayHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.AssetHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.EnumHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.AssetValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringNotEmptyValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.TagSetExistsValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.asset.AttitudeGroupExistsValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.asset.ItemAttitudeGroupExistsValidator;
import com.hypixel.hytale.server.npc.config.AttitudeGroup;
import com.hypixel.hytale.server.npc.config.ItemAttitudeGroup;
import com.hypixel.hytale.server.npc.role.RoleDebugFlags;
import com.hypixel.hytale.server.npc.role.support.WorldSupport;
import com.hypixel.hytale.server.npc.statetransition.StateTransitionController;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class SupportConfigBuilder<T> extends DependencyTrackingBuilder<T> {
   protected final EnumSet<RoleDebugFlags> parsedDebugFlags = EnumSet.noneOf(RoleDebugFlags.class);
   protected String debugFlags;
   protected String startState;
   protected String defaultSubState;
   protected int startStateIndex;
   protected int startSubStateIndex;
   protected Int2ObjectMap<IntSet> busyStates;
   protected final BuilderObjectReferenceHelper<StateTransitionController> stateTransitionController = new BuilderObjectReferenceHelper<>(
      StateTransitionController.class, this
   );
   protected final EnumHolder<Attitude> defaultPlayerAttitude = new EnumHolder<>();
   protected final EnumHolder<Attitude> defaultNPCAttitude = new EnumHolder<>();
   protected final AssetHolder attitudeGroup = new AssetHolder();
   protected final AssetHolder itemAttitudeGroup = new AssetHolder();
   protected boolean disableDamageFlock;
   protected final AssetArrayHolder disableDamageGroups = new AssetArrayHolder();

   @Nonnull
   public SupportConfigBuilder<T> readConfig(@Nonnull JsonElement data) {
      super.readConfig(data);
      this.getString(data, "Debug", e -> this.debugFlags = e, "", null, BuilderDescriptorState.WorkInProgress, "Debugging flags", null);
      this.getString(data, "StartState", s -> this.startState = s, "start", StringNotEmptyValidator.get(), BuilderDescriptorState.Stable, "Initial state", null);
      this.getDefaultSubState(
         data,
         "DefaultSubState",
         v -> this.defaultSubState = v,
         StringNotEmptyValidator.get(),
         BuilderDescriptorState.Stable,
         "The default sub state to reference when transitioning to a main state without a specified sub state",
         null
      );
      this.getBoolean(
         data,
         "DisableDamageFlock",
         b -> this.disableDamageFlock = b,
         true,
         BuilderDescriptorState.WorkInProgress,
         "If true disables combat damage from flock members",
         null
      );
      this.getAssetArray(
         data,
         "DisableDamageGroups",
         this.disableDamageGroups,
         null,
         0,
         Integer.MAX_VALUE,
         TagSetExistsValidator.withConfig(AssetValidator.ListCanBeEmpty),
         BuilderDescriptorState.WorkInProgress,
         "Members in this list of group won't cause damage",
         null
      );
      this.getExistentStateSet(
         data,
         "BusyStates",
         s -> this.busyStates = s,
         this.stateHelper,
         BuilderDescriptorState.Stable,
         "States during which this NPC is busy and can't be interacted with",
         null
      );
      this.getEnum(
         data,
         "DefaultPlayerAttitude",
         this.defaultPlayerAttitude,
         Attitude.class,
         Attitude.HOSTILE,
         BuilderDescriptorState.Stable,
         "The default attitude of this NPC towards players",
         null
      );
      this.getEnum(
         data,
         "DefaultNPCAttitude",
         this.defaultNPCAttitude,
         Attitude.class,
         Attitude.NEUTRAL,
         BuilderDescriptorState.Stable,
         "The default attitude of this NPC towards other NPCs",
         null
      );
      this.getAsset(
         data,
         "AttitudeGroup",
         this.attitudeGroup,
         null,
         AttitudeGroupExistsValidator.withConfig(EnumSet.of(AssetValidator.Config.NULLABLE)),
         BuilderDescriptorState.Stable,
         "The attitude group towards other NPCs this NPC belongs to (often species related)",
         null
      );
      this.getAsset(
         data,
         "ItemAttitudeGroup",
         this.itemAttitudeGroup,
         null,
         ItemAttitudeGroupExistsValidator.withConfig(EnumSet.of(AssetValidator.Config.NULLABLE)),
         BuilderDescriptorState.Stable,
         "This NPC's item attitudes",
         null
      );
      this.registerStateSetter(this.startState, this.defaultSubState, (m, s) -> {
         this.startStateIndex = m;
         this.startSubStateIndex = s;
      });
      if (this.debugFlags != null && !this.debugFlags.isEmpty()) {
         this.parsedDebugFlags.addAll(this.toDebugFlagSet("RoleDebugFlags", this.debugFlags));
      }

      return this;
   }

   public String getStartState() {
      return this.startState;
   }

   public int getStartStateIndex() {
      return this.startStateIndex;
   }

   public int getStartSubStateIndex() {
      return this.startSubStateIndex;
   }

   @Nonnull
   public EnumSet<RoleDebugFlags> getDebugFlags() {
      return this.parsedDebugFlags;
   }

   public boolean isDisableDamageFlock() {
      return this.disableDamageFlock;
   }

   @Nullable
   public int[] getDisableDamageGroups(@Nonnull BuilderSupport support) {
      return WorldSupport.createTagSetIndexArray(this.disableDamageGroups.get(support.getExecutionContext()));
   }

   public Int2ObjectMap<IntSet> getBusyStates() {
      return this.busyStates;
   }

   public Attitude getDefaultPlayerAttitude(@Nonnull BuilderSupport support) {
      return this.defaultPlayerAttitude.get(support.getExecutionContext());
   }

   public Attitude getDefaultNPCAttitude(@Nonnull BuilderSupport support) {
      return this.defaultNPCAttitude.get(support.getExecutionContext());
   }

   public int getAttitudeGroup(@Nonnull BuilderSupport support) {
      String groupName = this.attitudeGroup.get(support.getExecutionContext());
      return AttitudeGroup.getAssetMap().getIndex(groupName);
   }

   public int getItemAttitudeGroup(@Nonnull BuilderSupport support) {
      String groupName = this.itemAttitudeGroup.get(support.getExecutionContext());
      return ItemAttitudeGroup.getAssetMap().getIndex(groupName);
   }

   @Nullable
   public StateTransitionController getStateTransitionController(@Nonnull BuilderSupport support) {
      support.setCurrentInstructionContext(InstructionType.Default);
      return this.stateTransitionController.build(support);
   }
}
