package com.hypixel.hytale.server.npc.corecomponents.combat.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.core.asset.type.blockset.config.BlockSet;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.Feature;
import com.hypixel.hytale.server.npc.asset.builder.holder.AssetHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.BooleanHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.DoubleHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.IntHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.NumberArrayHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.AssetValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleRangeValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleSequenceValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.IntRangeValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.RelationalOperator;
import com.hypixel.hytale.server.npc.asset.builder.validators.asset.BlockSetExistsValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.asset.RootInteractionValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderBodyMotionBase;
import com.hypixel.hytale.server.npc.corecomponents.combat.BodyMotionCharge;
import com.hypixel.hytale.server.npc.instructions.BodyMotion;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderBodyMotionCharge extends BuilderBodyMotionBase {
   public static final double[] DEFAULT_LOCKED_ON_DURATION_RANGE = new double[]{5.0, 5.0};
   public static final double[] DEFAULT_WINDING_UP_DURATION_RANGE = new double[]{5.0, 5.0};
   public static final double[] DEFAULT_POST_CHARGE_DURATION_RANGE = new double[]{2.0, 2.0};
   protected final DoubleHolder relativeTurnSpeed = new DoubleHolder();
   protected final DoubleHolder windingUpRelativeTurnSpeed = new DoubleHolder();
   protected final DoubleHolder chargeRelativeSpeed = new DoubleHolder();
   protected final NumberArrayHolder chargeDistanceRange = new NumberArrayHolder();
   protected final NumberArrayHolder lockedOnDurationRange = new NumberArrayHolder();
   protected final NumberArrayHolder windingUpDurationRange = new NumberArrayHolder();
   protected final NumberArrayHolder postChargeDurationRange = new NumberArrayHolder();
   protected final BooleanHolder windingUpUninterruptable = new BooleanHolder();
   protected final BooleanHolder clearOnceOnStateChange = new BooleanHolder();
   protected final DoubleHolder chargeAbsoluteSpeed = new DoubleHolder();
   protected final DoubleHolder chargeAcceleration = new DoubleHolder();
   protected final BooleanHolder ignoredBlockSetTriggers = new BooleanHolder();
   protected final BooleanHolder entityStopsCharge = new BooleanHolder();
   protected final AssetHolder blockCollisionInteraction = new AssetHolder();
   protected final AssetHolder npcCollisionInteraction = new AssetHolder();
   protected final AssetHolder playerCollisionInteraction = new AssetHolder();
   protected final DoubleHolder repeatCollisionIgnoreDuration = new DoubleHolder();
   protected final DoubleHolder climbSlope = new DoubleHolder();
   protected final DoubleHolder dropSlope = new DoubleHolder();
   protected final DoubleHolder horizontalSkipGapWidth = new DoubleHolder();
   protected final DoubleHolder knockbackThreshold = new DoubleHolder();
   protected final DoubleHolder probeChargeRecomputeDistance = new DoubleHolder();
   protected final IntHolder probeMinFrequency = new IntHolder();
   protected final IntHolder probeMaxFrequency = new IntHolder();
   protected final DoubleHolder probeMinDirectionChangeDegrees = new DoubleHolder();
   protected final DoubleHolder lockedOnToleranceAngleDegrees = new DoubleHolder();
   protected final AssetHolder ignoredBlockSet = new AssetHolder();

   @Nonnull
   public BodyMotion build(@Nonnull BuilderSupport builderSupport) {
      return new BodyMotionCharge(this, builderSupport);
   }

   @Nonnull
   @Override
   public String getShortDescription() {
      return "Aim at a target and progress through locked-on, winding up, and charging motion states.";
   }

   @Nonnull
   @Override
   public String getLongDescription() {
      return this.getShortDescription();
   }

   @Nonnull
   @Override
   public BuilderDescriptorState getBuilderDescriptorState() {
      return BuilderDescriptorState.Stable;
   }

   @Nonnull
   public BuilderBodyMotionCharge readConfig(@Nonnull JsonElement data) {
      this.getDouble(
         data,
         "RelativeTurnSpeed",
         this.relativeTurnSpeed,
         1.0,
         DoubleRangeValidator.between(0.0, 2.0),
         BuilderDescriptorState.Stable,
         "Relative turn speed while aiming and locked on",
         null
      );
      this.getDouble(
         data,
         "LockedOnToleranceAngle",
         this.lockedOnToleranceAngleDegrees,
         15.0,
         DoubleRangeValidator.fromExclToIncl(0.0, 360.0),
         BuilderDescriptorState.Stable,
         "View cone angle where target is still considered locked on",
         null
      );
      this.getDoubleRange(
         data,
         "LockedOnDurationRange",
         this.lockedOnDurationRange,
         DEFAULT_LOCKED_ON_DURATION_RANGE,
         DoubleSequenceValidator.betweenWeaklyMonotonic(0.0, Double.MAX_VALUE),
         BuilderDescriptorState.Stable,
         "Random interval range in seconds specifying minimum time target must be locked on before winding up",
         null
      );
      this.getDoubleRange(
         data,
         "WindingUpDurationRange",
         this.windingUpDurationRange,
         DEFAULT_WINDING_UP_DURATION_RANGE,
         DoubleSequenceValidator.betweenWeaklyMonotonic(0.0, Double.MAX_VALUE),
         BuilderDescriptorState.Stable,
         "Random interval range in seconds specifying winding up time before charging",
         null
      );
      this.getDoubleRange(
         data,
         "PostChargeDurationRange",
         this.postChargeDurationRange,
         DEFAULT_POST_CHARGE_DURATION_RANGE,
         DoubleSequenceValidator.betweenWeaklyMonotonic(0.0, Double.MAX_VALUE),
         BuilderDescriptorState.Stable,
         "Random interval range in seconds spent resting after an obstructed or finished charge",
         null
      );
      this.getBoolean(
         data,
         "WindingUpUninterruptable",
         this.windingUpUninterruptable,
         true,
         BuilderDescriptorState.Stable,
         "When true, WindingUp is not interrupted by target loss or aim drift",
         null
      );
      this.getDouble(
         data,
         "WindingUpRelativeTurnSpeed",
         this.windingUpRelativeTurnSpeed,
         1.0,
         DoubleRangeValidator.between(0.0, 2.0),
         BuilderDescriptorState.Stable,
         "Relative turn speed during WindingUp. Can be 0 to disable rotation in that phase",
         null
      );
      this.getDouble(
         data,
         "ChargeRelativeSpeed",
         this.chargeRelativeSpeed,
         5.0,
         DoubleRangeValidator.between(0.0, 20.0),
         BuilderDescriptorState.Stable,
         "Relative forward movement speed toward the target during Charging",
         null
      );
      this.requireDoubleRange(
         data,
         "ChargeDistanceRange",
         this.chargeDistanceRange,
         DoubleSequenceValidator.betweenWeaklyMonotonic(1.0, 50.0),
         BuilderDescriptorState.Stable,
         "Range used to pick a random maximum charge distance when a new aiming cycle starts",
         null
      );
      this.getAsset(
         data,
         "IgnoredBlockSet",
         this.ignoredBlockSet,
         "",
         BlockSetExistsValidator.withConfig(AssetValidator.CanBeEmpty),
         BuilderDescriptorState.Stable,
         "Blocks in this set are ignored by the charge probe (treated as non-colliding)",
         null
      );
      this.getBoolean(
         data,
         "ClearOnceOnStateChange",
         this.clearOnceOnStateChange,
         true,
         BuilderDescriptorState.Stable,
         "When true, calls clearOnce() on the parent instruction whenever the charge state changes",
         null
      );
      this.getDouble(
         data,
         "ChargeAbsoluteSpeed",
         this.chargeAbsoluteSpeed,
         0.0,
         DoubleRangeValidator.between(0.0, 100.0),
         BuilderDescriptorState.Stable,
         "Maximum charge speed in blocks/s. 0 disables the cap and ChargeRelativeSpeed is used",
         null
      );
      this.validateExactlyOneZero(this.chargeRelativeSpeed, this.chargeAbsoluteSpeed);
      this.getDouble(
         data,
         "ChargeAcceleration",
         this.chargeAcceleration,
         1000.0,
         DoubleRangeValidator.between(0.0, 10000.0),
         BuilderDescriptorState.Stable,
         "Linear acceleration in blocks/s^2 applied each tick during charging. Very high values reproduce the pre-existing near-instant ramp-up",
         null
      );
      this.getBoolean(
         data,
         "IgnoredBlockSetTriggers",
         this.ignoredBlockSetTriggers,
         false,
         BuilderDescriptorState.Stable,
         "When true, blocks in IgnoredBlockSet still fire trigger/damage interactions during the rail step",
         null
      );
      this.getBoolean(
         data,
         "EntityStopsCharge",
         this.entityStopsCharge,
         false,
         BuilderDescriptorState.Stable,
         "When true, the first entity (player or NPC) hit during the rail step clamps the charge and transitions to Obstructed. When false, entity hits are reported without stopping the charge",
         null
      );
      this.getAsset(
         data,
         "BlockCollisionInteraction",
         this.blockCollisionInteraction,
         "",
         RootInteractionValidator.withConfig(AssetValidator.CanBeEmpty),
         BuilderDescriptorState.Stable,
         "Optional interaction to execute immediately for each block collision during charging",
         null
      );
      this.getAsset(
         data,
         "NPCCollisionInteraction",
         this.npcCollisionInteraction,
         "",
         RootInteractionValidator.withConfig(AssetValidator.CanBeEmpty),
         BuilderDescriptorState.Stable,
         "Optional interaction to execute immediately for each NPC collision during charging",
         null
      );
      this.getAsset(
         data,
         "PlayerCollisionInteraction",
         this.playerCollisionInteraction,
         "",
         RootInteractionValidator.withConfig(AssetValidator.CanBeEmpty),
         BuilderDescriptorState.Stable,
         "Optional interaction to execute immediately for each player collision during charging",
         null
      );
      this.getDouble(
         data,
         "RepeatCollisionIgnoreDuration",
         this.repeatCollisionIgnoreDuration,
         0.2,
         DoubleRangeValidator.between(0.0, Double.MAX_VALUE),
         BuilderDescriptorState.Stable,
         "Duration in seconds for which repeat collisions with the same entity are ignored by BodyMotionCharge collision interactions and sensors",
         null
      );
      this.getDouble(
         data,
         "ClimbSlope",
         this.climbSlope,
         2.0,
         DoubleRangeValidator.between(0.0, 10.0),
         BuilderDescriptorState.Stable,
         "Horizontal blocks per vertical block when smoothing CLIMB segments along the charge path. 0 disables climb smoothing; otherwise the climb starts as far back from the edge as a slope of this value allows (e.g. 2 = 2 horizontal blocks per 1 vertical block). The actual slope can become steeper but never shallower than this value",
         null
      );
      this.getDouble(
         data,
         "DropSlope",
         this.dropSlope,
         2.0,
         DoubleRangeValidator.between(0.0, 10.0),
         BuilderDescriptorState.Stable,
         "Horizontal blocks per vertical block when smoothing DROP segments along the charge path. 0 disables drop smoothing; otherwise the drop lands as far past the edge as a slope of this value allows. The actual slope can become steeper but never shallower than this value",
         null
      );
      this.getDouble(
         data,
         "HorizontalSkipGapWidth",
         this.horizontalSkipGapWidth,
         1.2,
         DoubleRangeValidator.between(0.0, 8.0),
         BuilderDescriptorState.Stable,
         "Maximum horizontal width (in blocks) the NPC may stride over while sloping a climb/drop or when collapsing a drop+climb dip whose endpoints are at the same height. 0 disables both shortcuts",
         null
      );
      this.getDouble(
         data,
         "KnockbackThreshold",
         this.knockbackThreshold,
         0.0,
         DoubleRangeValidator.between(0.0, Double.MAX_VALUE),
         BuilderDescriptorState.Stable,
         "Threshold for external knockback magnitude while Charging. If the combined external velocity is below this value the knockback is ignored and cleared; otherwise Charging transitions to Knockback",
         null
      );
      this.getDouble(
         data,
         "ProbeChargeRecomputeDistance",
         this.probeChargeRecomputeDistance,
         0.0,
         DoubleRangeValidator.between(-Double.MAX_VALUE, Double.MAX_VALUE),
         BuilderDescriptorState.Stable,
         "Interval in blocks during Charging for re-validating the walk path against world changes (2D motion controllers only). Must be > 0; values <= 0 disable mid-charge re-validation",
         null
      );
      this.getInt(
         data,
         "ProbeMinFrequency",
         this.probeMinFrequency,
         2,
         IntRangeValidator.between(0, 30),
         BuilderDescriptorState.Stable,
         "Minimum frequency for executing the reachability test during updates. 0 means maximum frequency (as often as possible)",
         null
      );
      this.getInt(
         data,
         "ProbeMaxFrequency",
         this.probeMaxFrequency,
         10,
         IntRangeValidator.between(0, 30),
         BuilderDescriptorState.Stable,
         "Maximum frequency for executing the reachability test during updates. 0 means maximum frequency (as often as possible)",
         null
      );
      this.validateIntRelation(this.probeMinFrequency, RelationalOperator.LessEqual, this.probeMaxFrequency);
      this.getDouble(
         data,
         "ProbeMinDirectionChange",
         this.probeMinDirectionChangeDegrees,
         5.0,
         DoubleRangeValidator.between(0.0, 180.0),
         BuilderDescriptorState.Stable,
         "Minimum angular change to target in degrees required before executing another reachability test",
         null
      );
      this.requireFeature(Feature.AnyPosition);
      this.providePreceding(BodyMotionCharge.class);
      return this;
   }

   public double getRelativeTurnSpeed(@Nonnull BuilderSupport support) {
      return this.relativeTurnSpeed.get(support.getExecutionContext());
   }

   public double getLockedOnHalfAngleRadians(@Nonnull BuilderSupport support) {
      return this.lockedOnToleranceAngleDegrees.get(support.getExecutionContext()) / 2.0 * (float) (Math.PI / 180.0);
   }

   @Nonnull
   public double[] getLockedOnDurationRange(@Nonnull BuilderSupport support) {
      return this.lockedOnDurationRange.get(support.getExecutionContext());
   }

   @Nonnull
   public double[] getWindingUpDurationRange(@Nonnull BuilderSupport support) {
      return this.windingUpDurationRange.get(support.getExecutionContext());
   }

   @Nonnull
   public double[] getPostChargeDurationRange(@Nonnull BuilderSupport support) {
      return this.postChargeDurationRange.get(support.getExecutionContext());
   }

   public boolean isWindingUpUninterruptable(@Nonnull BuilderSupport support) {
      return this.windingUpUninterruptable.get(support.getExecutionContext());
   }

   public boolean isClearOnceOnStateChange(@Nonnull BuilderSupport support) {
      return this.clearOnceOnStateChange.get(support.getExecutionContext());
   }

   public double getWindingUpRelativeTurnSpeed(@Nonnull BuilderSupport support) {
      return this.windingUpRelativeTurnSpeed.get(support.getExecutionContext());
   }

   public double getChargeRelativeSpeed(@Nonnull BuilderSupport support) {
      return this.chargeRelativeSpeed.get(support.getExecutionContext());
   }

   @Nonnull
   public double[] getChargeDistanceRange(@Nonnull BuilderSupport support) {
      return this.chargeDistanceRange.get(support.getExecutionContext());
   }

   public int getIgnoredBlockSet(@Nonnull BuilderSupport support) {
      String key = this.ignoredBlockSet.get(support.getExecutionContext());
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

   public double getChargeAbsoluteSpeed(@Nonnull BuilderSupport support) {
      return this.chargeAbsoluteSpeed.get(support.getExecutionContext());
   }

   public double getChargeAcceleration(@Nonnull BuilderSupport support) {
      return this.chargeAcceleration.get(support.getExecutionContext());
   }

   public boolean isIgnoredBlockSetTriggers(@Nonnull BuilderSupport support) {
      return this.ignoredBlockSetTriggers.get(support.getExecutionContext());
   }

   public boolean isEntityStopsCharge(@Nonnull BuilderSupport support) {
      return this.entityStopsCharge.get(support.getExecutionContext());
   }

   @Nullable
   public String getBlockCollisionInteraction(@Nonnull BuilderSupport support) {
      String value = this.blockCollisionInteraction.get(support.getExecutionContext());
      return value != null && !value.isEmpty() ? value : null;
   }

   @Nullable
   public String getNPCCollisionInteraction(@Nonnull BuilderSupport support) {
      String value = this.npcCollisionInteraction.get(support.getExecutionContext());
      return value != null && !value.isEmpty() ? value : null;
   }

   @Nullable
   public String getPlayerCollisionInteraction(@Nonnull BuilderSupport support) {
      String value = this.playerCollisionInteraction.get(support.getExecutionContext());
      return value != null && !value.isEmpty() ? value : null;
   }

   public double getClimbSlope(@Nonnull BuilderSupport support) {
      return this.climbSlope.get(support.getExecutionContext());
   }

   public double getDropSlope(@Nonnull BuilderSupport support) {
      return this.dropSlope.get(support.getExecutionContext());
   }

   public double getHorizontalSkipGapWidth(@Nonnull BuilderSupport support) {
      return this.horizontalSkipGapWidth.get(support.getExecutionContext());
   }

   public double getRepeatCollisionIgnoreDuration(@Nonnull BuilderSupport support) {
      return this.repeatCollisionIgnoreDuration.get(support.getExecutionContext());
   }

   public double getKnockbackThreshold(@Nonnull BuilderSupport support) {
      return this.knockbackThreshold.get(support.getExecutionContext());
   }

   public double getProbeChargeRecomputeDistance(@Nonnull BuilderSupport support) {
      return this.probeChargeRecomputeDistance.get(support.getExecutionContext());
   }

   public double getProbeMinInterval(@Nonnull BuilderSupport support) {
      return frequencyToDuration(this.probeMaxFrequency.get(support.getExecutionContext()));
   }

   public double getProbeMaxInterval(@Nonnull BuilderSupport support) {
      return frequencyToDuration(this.probeMinFrequency.get(support.getExecutionContext()));
   }

   public double getProbeMinDirectionChangeRadians(@Nonnull BuilderSupport support) {
      return this.probeMinDirectionChangeDegrees.get(support.getExecutionContext()) * (float) (Math.PI / 180.0);
   }

   private static double frequencyToDuration(int frequency) {
      if (frequency <= 0) {
         return 0.0;
      }

      int cappedFrequency = Math.min(frequency, 30);
      return 1.0 / cappedFrequency;
   }
}
