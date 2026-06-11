package com.hypixel.hytale.server.npc.corecomponents.combat;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.function.consumer.QuadConsumer;
import com.hypixel.hytale.function.predicate.QuadPredicate;
import com.hypixel.hytale.math.random.RandomExtra;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.util.TrigMathUtil;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionChain;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.InteractionManager;
import com.hypixel.hytale.server.core.modules.blockset.BlockSetModule;
import com.hypixel.hytale.server.core.modules.collision.CollisionConfig;
import com.hypixel.hytale.server.core.modules.debug.DebugUtils;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.modules.physics.util.PhysicsMath;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.BodyMotionBase;
import com.hypixel.hytale.server.npc.corecomponents.combat.builders.BuilderBodyMotionCharge;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.instructions.Instruction;
import com.hypixel.hytale.server.npc.movement.Steering;
import com.hypixel.hytale.server.npc.movement.controllers.BlockHit;
import com.hypixel.hytale.server.npc.movement.controllers.EntityHit;
import com.hypixel.hytale.server.npc.movement.controllers.MotionController;
import com.hypixel.hytale.server.npc.movement.controllers.ProbeMoveData;
import com.hypixel.hytale.server.npc.movement.controllers.RailPath;
import com.hypixel.hytale.server.npc.movement.controllers.RailPathRefreshResolver;
import com.hypixel.hytale.server.npc.movement.controllers.RailPathSmoother;
import com.hypixel.hytale.server.npc.movement.controllers.RailStepConfig;
import com.hypixel.hytale.server.npc.movement.controllers.RailStepResult;
import com.hypixel.hytale.server.npc.movement.controllers.TargetContactSamplePlanner;
import com.hypixel.hytale.server.npc.role.RoleDebugFlags;
import com.hypixel.hytale.server.npc.role.support.DebugSupport;
import com.hypixel.hytale.server.npc.role.support.PositionCache;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.hypixel.hytale.server.npc.util.AimingData;
import com.hypixel.hytale.server.npc.util.NPCPhysicsMath;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Reference2DoubleMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleOpenHashMap;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;

public class BodyMotionCharge extends BodyMotionBase implements DebugSupport.DebugFlagsChangeListener {
   private static final InteractionType INTERACTION_TYPE = InteractionType.Collision;
   private static final float CHARGE_DEBUG_SHAPE_DURATION_SECONDS = 5.0F;
   private static final double CHARGE_STATE_DEBUG_MARKER_RADIUS = 1.0;
   private static final float CHARGE_PROBE_DEBUG_SHAPE_DURATION_SECONDS = 0.05F;
   private static final double CHARGE_PROBE_END_MARKER_RADIUS = 1.0;
   private static final float CHARGE_PROBE_FRESH_OPACITY = 1.0F;
   private static final float CHARGE_PROBE_STALE_OPACITY = 0.45F;
   private static final double CHARGE_DEBUG_SPHERE_RADIUS = 0.2;
   private static final double CHARGE_DEBUG_CUBE_SIZE = 0.2;
   private static final int DEFAULT_COLLISION_BUFFER_CAPACITY = 8;
   private static final double MIN_CHARGE_DISTANCE = 1.0;
   private static final double TARGET_CONTACT_WINDOW_MIN = 1.0E-6;
   private static final double TARGET_CONTACT_WINDOW_MAX = 1.5;
   private static final double RAIL_REFRESH_PATH_TOLERANCE = 0.05;
   private static final double CHARGE_END_PLANE_EPSILON = 1.0E-6;
   private static final double CONTACT_CONTEXT_HYSTERESIS_TARGET_DISTANCE = 1.5;
   private static final double CONTACT_CONTEXT_HYSTERESIS_TARGET_DISTANCE_DELTA = 1.0;
   private static final double CONTACT_CONTEXT_HYSTERESIS_PROBE_START_DELTA = 0.5;
   private static final double CONTACT_SAMPLE_DISTANCE_EPSILON = 0.001;
   private static final double MAX_RELATIVE_SPEED_CHARGE_SEARCH_RADIUS = 64.0;
   private static final double CHARGE_CANDIDATE_APPROACH_PADDING = 2.0;
   private static final double CHARGE_SEARCH_RADIUS = 14.8F;
   private static final QuadPredicate<Ref<EntityStore>, Ref<EntityStore>, Object, ComponentAccessor<EntityStore>> NOT_SELF_PREDICATE = (ref, self, ignored, var3) -> !ref.equals(
      self
   );
   private static final QuadConsumer<Ref<EntityStore>, Ref<EntityStore>, List<Ref<EntityStore>>, ComponentAccessor<EntityStore>> COLLECT_ENTITY_CONSUMER = (ref, self, buffer, var3) -> {
      if (ref != null && ref.isValid() && ref != self) {
         buffer.add(ref);
      }
   };
   protected static final ComponentType<EntityStore, TransformComponent> TRANSFORM_COMPONENT_TYPE = TransformComponent.getComponentType();
   protected final double relativeTurnSpeed;
   protected final double lockedOnHalfAngleRad;
   protected final double[] lockedOnDurationRange;
   protected final double[] windingUpDurationRange;
   protected final double[] postChargeDurationRange;
   protected final boolean skipLockedOnState;
   protected final boolean skipWindingUpState;
   protected final double[] chargeDistanceRange;
   protected final boolean windingUpUninterruptable;
   protected final double windingUpRelativeTurnSpeed;
   protected final double chargeRelativeSpeed;
   protected final int ignoredBlockSet;
   @Nullable
   private final Predicate<CollisionConfig> ignoredBlockFilter;
   protected final boolean clearOnceOnStateChange;
   protected final double chargeAbsoluteSpeed;
   protected final double chargeAcceleration;
   protected final boolean ignoredBlockSetTriggers;
   protected final boolean entityStopsCharge;
   protected final double climbSlope;
   protected final double dropSlope;
   protected final double horizontalSkipGapWidth;
   @Nullable
   private final String blockCollisionInteractionId;
   @Nullable
   private final String npcCollisionInteractionId;
   @Nullable
   private final String playerCollisionInteractionId;
   protected final double repeatCollisionIgnoreDuration;
   protected final double probeMinInterval;
   protected final double probeMaxInterval;
   protected final double probeMinDirectionChange;
   protected final double probeChargeRecomputeDistance;
   protected final double knockbackThreshold;
   @Nullable
   private Instruction parentInstruction;
   protected final AimingData aimingData = new AimingData();
   protected final Vector3d targetPosition = new Vector3d();
   protected final Rotation3f rotation = new Rotation3f();
   protected final ProbeMoveData probeMoveData = new ProbeMoveData();
   protected final Vector3d chargeStartPosition = new Vector3d();
   protected final Vector3d chargeEndPosition = new Vector3d();
   protected final Vector3d chargeDirection = new Vector3d();
   protected final Vector3d chargeProbeEndPosition = new Vector3d();
   protected final Vector3d chargeProbeTargetDirection = new Vector3d();
   protected final Vector3d chargeProbeEndDirection = new Vector3d();
   protected final Vector3d chargeProbeArrowOrigin = new Vector3d();
   private final Vector3d launchChargeStartReference = new Vector3d();
   private final Vector3d launchChargeEndReference = new Vector3d();
   private final Vector3d launchChargePlaneNormal = new Vector3d();
   private final RailPath railPath = new RailPath();
   private final RailPath candidateRailPath = new RailPath();
   private final RailPathSmoother railPathSmoother = new RailPathSmoother();
   private final RailPathSmoother.Config smootherConfig = new RailPathSmoother.Config();
   private final RailStepConfig railConfig = new RailStepConfig();
   private final RailStepResult railResult = new RailStepResult();
   private double chargeSpeed;
   private final Vector3d railDelta = new Vector3d();
   private final Vector3d railStepTargetPosition = new Vector3d();
   private final Vector3d railReplanAnchorPosition = new Vector3d();
   private final Vector3d railRefreshProbeStartPosition = new Vector3d();
   private final List<Ref<EntityStore>> candidateEntitiesBuffer = new ObjectArrayList<>(8);
   private final List<EntityHit> filteredEntityHits = new ObjectArrayList<>(8);
   private final BitSet acceptedEntityHitIndexes = new BitSet(8);
   private final Reference2DoubleMap<Ref<EntityStore>> lastEntityCollisionHitTimes = new Reference2DoubleOpenHashMap<>(8);
   private BodyMotionCharge.ChargeState state = BodyMotionCharge.ChargeState.LostTarget;
   private BodyMotionCharge.ChargeState sensorVisibleState = BodyMotionCharge.ChargeState.LostTarget;
   private boolean debugChargeState;
   private boolean debugChargePath;
   private boolean visChargePath;
   private boolean visChargeCollisions;
   private boolean visChargeEntityHits;
   private boolean visChargeProbe;
   private double timeSinceHaveTarget;
   private double phaseEndTime;
   private double lastProbeTime;
   private final Vector3d lastProbeDirection = new Vector3d();
   private final float cosProbeMinDirectionChange;
   private double lastProbeDistanceSquared;
   private boolean lastProbeBoundingBoxesOverlapped;
   private double chargeDistanceSinceLastRailRefresh;
   private double probeSizeCompensation;
   private final Box targetWorldBoundingBox = new Box();
   private final Box npcWorldBoundingBox = new Box();
   private final Vector3d expectedHitPosition = new Vector3d();
   private final ProbeMoveData.SegmentLocation hitSegmentLocation = new ProbeMoveData.SegmentLocation();
   private final TargetContactSamplePlanner.Scratch targetContactSampleScratch = new TargetContactSamplePlanner.Scratch();
   private final double[] targetContactSampleDistances = new double[6];
   private final RailPathSmoother.HitSide[] targetContactSampleSides = new RailPathSmoother.HitSide[6];
   private final RailPathSmoother.ContactContext railContactContext = new RailPathSmoother.ContactContext();
   private double lastRefreshTargetDistance = Double.NaN;
   private boolean lastRefreshContactDroppedNearTarget;
   private final RailPathSmoother.ContactContext retainedContactContext = new RailPathSmoother.ContactContext();
   private final Vector3d retainedContactProbeStartPosition = new Vector3d();
   private boolean hasRetainedContactContext;
   private double retainedContactTargetDistance = Double.NaN;
   private double activeChargeDistance;
   private boolean hasProbeEndPosition;
   private boolean hasLaunchChargeReference;

   public BodyMotionCharge(@Nonnull BuilderBodyMotionCharge builder, @Nonnull BuilderSupport support) {
      super(builder);
      this.relativeTurnSpeed = builder.getRelativeTurnSpeed(support);
      this.lockedOnHalfAngleRad = builder.getLockedOnHalfAngleRadians(support);
      this.lockedOnDurationRange = builder.getLockedOnDurationRange(support);
      this.windingUpDurationRange = builder.getWindingUpDurationRange(support);
      this.postChargeDurationRange = builder.getPostChargeDurationRange(support);
      this.skipLockedOnState = isZeroDurationRange(this.lockedOnDurationRange);
      this.skipWindingUpState = isZeroDurationRange(this.windingUpDurationRange);
      this.chargeDistanceRange = builder.getChargeDistanceRange(support);
      this.windingUpUninterruptable = builder.isWindingUpUninterruptable(support);
      this.windingUpRelativeTurnSpeed = builder.getWindingUpRelativeTurnSpeed(support);
      this.chargeRelativeSpeed = builder.getChargeRelativeSpeed(support);
      this.activeChargeDistance = Math.max(1.0, RandomExtra.randomRange(this.chargeDistanceRange));
      this.clearOnceOnStateChange = builder.isClearOnceOnStateChange(support);
      this.chargeAbsoluteSpeed = builder.getChargeAbsoluteSpeed(support);
      this.chargeAcceleration = builder.getChargeAcceleration(support);
      this.ignoredBlockSetTriggers = builder.isIgnoredBlockSetTriggers(support);
      this.entityStopsCharge = builder.isEntityStopsCharge(support);
      this.climbSlope = builder.getClimbSlope(support);
      this.dropSlope = builder.getDropSlope(support);
      this.horizontalSkipGapWidth = builder.getHorizontalSkipGapWidth(support);
      this.blockCollisionInteractionId = builder.getBlockCollisionInteraction(support);
      this.npcCollisionInteractionId = builder.getNPCCollisionInteraction(support);
      this.playerCollisionInteractionId = builder.getPlayerCollisionInteraction(support);
      this.repeatCollisionIgnoreDuration = builder.getRepeatCollisionIgnoreDuration(support);
      this.knockbackThreshold = builder.getKnockbackThreshold(support);
      this.probeMinInterval = builder.getProbeMinInterval(support);
      this.probeMaxInterval = builder.getProbeMaxInterval(support);
      this.probeMinDirectionChange = builder.getProbeMinDirectionChangeRadians(support);
      double configuredProbeChargeRecomputeDistance = builder.getProbeChargeRecomputeDistance(support);
      if (!(configuredProbeChargeRecomputeDistance <= 0.0) && Double.isFinite(configuredProbeChargeRecomputeDistance)) {
         this.probeChargeRecomputeDistance = configuredProbeChargeRecomputeDistance;
      } else {
         this.probeChargeRecomputeDistance = Double.POSITIVE_INFINITY;
      }

      this.cosProbeMinDirectionChange = TrigMathUtil.cos(this.probeMinDirectionChange);
      this.ignoredBlockSet = builder.getIgnoredBlockSet(support);
      if (this.ignoredBlockSet == Integer.MIN_VALUE) {
         this.ignoredBlockFilter = null;
      } else {
         int setIndex = this.ignoredBlockSet;
         this.ignoredBlockFilter = cfg -> cfg.blockType == null || !BlockSetModule.getInstance().blockInSet(setIndex, cfg.blockType);
         this.probeMoveData.setBlockCollisionFilter(this.ignoredBlockFilter);
      }

      this.aimingData.requireCloseCombat();
   }

   @Override
   public void activate(@Nonnull Ref<EntityStore> ref, @Nonnull ExecutionSupport executionSupport, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
      if (this.debugChargeState) {
         Integer entityId = null;
         NetworkId networkId = componentAccessor.getComponent(ref, NetworkId.getComponentType());
         if (networkId != null) {
            entityId = networkId.getId();
         }

         NPCPlugin.get().getLogger().at(Level.INFO).log("BodyMotionCharge activating role=%s entityId=%s", executionSupport.getRole().getRoleName(), entityId);
      }

      super.activate(ref, executionSupport, componentAccessor);
      this.state = BodyMotionCharge.ChargeState.LostTarget;
      this.sensorVisibleState = BodyMotionCharge.ChargeState.LostTarget;
      MotionController motionController = executionSupport.getMotionContextSupport().getActiveMotionController();
      this.updateSizeCompensation(motionController);
      this.resetRailState();
   }

   @Override
   public void deactivate(@Nonnull Ref<EntityStore> ref, @Nonnull ExecutionSupport executionSupport, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
      super.deactivate(ref, executionSupport, componentAccessor);
      this.resetRailState();
      this.sensorVisibleState = BodyMotionCharge.ChargeState.LostTarget;
      if (this.debugChargeState) {
         Integer entityId = null;
         NetworkId networkId = componentAccessor.getComponent(ref, NetworkId.getComponentType());
         if (networkId != null) {
            entityId = networkId.getId();
         }

         NPCPlugin.get()
            .getLogger()
            .at(Level.INFO)
            .log("BodyMotionCharge deactivating role=%s entityId=%s", executionSupport.getRole().getRoleName(), entityId);
      }
   }

   @Override
   public void motionControllerChanged(
      @Nullable Ref<EntityStore> ref,
      @Nonnull NPCEntity npcComponent,
      MotionController motionController,
      @Nullable ComponentAccessor<EntityStore> componentAccessor
   ) {
      super.motionControllerChanged(ref, npcComponent, motionController, componentAccessor);
      this.updateSizeCompensation(motionController);
   }

   private void updateSizeCompensation(@Nonnull MotionController motionController) {
      double maxExtent = NPCPhysicsMath.getMaxBoundingBoxExtent(motionController.getCollisionBoundingBox(), motionController.getComponentSelector());
      this.probeSizeCompensation = maxExtent / 2.0;
   }

   private void resetRailState() {
      this.chargeSpeed = 0.0;
      this.railPath.reset();
      this.candidateRailPath.reset();
      this.railPathSmoother.reset();
      this.smootherConfig.reset();
      this.railResult.reset();
      this.railConfig.candidateEntities = null;
      this.candidateEntitiesBuffer.clear();
      this.filteredEntityHits.clear();
      this.acceptedEntityHitIndexes.clear();
      this.lastEntityCollisionHitTimes.clear();
      this.hasProbeEndPosition = false;
      this.chargeDistanceSinceLastRailRefresh = 0.0;
      this.hasLaunchChargeReference = false;
      this.railContactContext.reset();
      this.lastRefreshTargetDistance = Double.NaN;
      this.lastRefreshContactDroppedNearTarget = false;
      this.retainedContactContext.reset();
      this.retainedContactProbeStartPosition.zero();
      this.hasRetainedContactContext = false;
      this.retainedContactTargetDistance = Double.NaN;
   }

   @Override
   public void loaded(ExecutionSupport executionSupport) {
      super.loaded(executionSupport);
      if (this.clearOnceOnStateChange && this.parent instanceof Instruction instruction && instruction.getParent() instanceof Instruction instructionParent) {
         this.parentInstruction = instructionParent;
      }
   }

   @Override
   public void registerWithSupport(ExecutionSupport executionSupport) {
      super.registerWithSupport(executionSupport);
      DebugSupport debugSupport = executionSupport.getDebugSupport();
      debugSupport.registerDebugFlagsListener(this);
      this.onDebugFlagsChanged(debugSupport.getDebugFlags());
      executionSupport.getPositionCache().requirePlayerDistanceUnsorted(14.8F);
      executionSupport.getPositionCache().requireEntityDistanceUnsorted(14.8F);
   }

   @Override
   public void preComputeSteering(
      @Nonnull Ref<EntityStore> ref, @Nonnull ExecutionSupport executionSupport, @Nullable InfoProvider sensorInfo, @Nonnull Store<EntityStore> store
   ) {
      if (sensorInfo != null) {
         sensorInfo.passExtraInfo(this.aimingData);
      }
   }

   @Override
   public boolean computeSteering(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull ExecutionSupport executionSupport,
      @Nullable InfoProvider sensorInfo,
      double dt,
      @Nonnull Steering desiredSteering,
      @Nonnull ComponentAccessor<EntityStore> componentAccessor
   ) {
      if (sensorInfo != null && sensorInfo.getPositionProvider() != null) {
         this.timeSinceHaveTarget += dt;
         Ref<EntityStore> target = sensorInfo.getPositionProvider().getTarget();
         boolean targetAvailable = target != null && sensorInfo.getPositionProvider().providePosition(this.targetPosition);
         if (this.state == BodyMotionCharge.ChargeState.LostTarget) {
            this.sensorVisibleState = BodyMotionCharge.ChargeState.LostTarget;
            desiredSteering.clear();
            this.clearEntityCollisionHits();
            if (!targetAvailable) {
               return true;
            }

            this.activeChargeDistance = Math.max(1.0, RandomExtra.randomRange(this.chargeDistanceRange));
            this.aimingData.setChargeDistance(this.activeChargeDistance);
            this.transitionChargeState(BodyMotionCharge.ChargeState.Aiming, ref, executionSupport, componentAccessor, "have target");
            this.lastProbeTime = -Double.MAX_VALUE;
            this.timeSinceHaveTarget = 0.0;
         }

         TransformComponent transformComponent = componentAccessor.getComponent(ref, TRANSFORM_COMPONENT_TYPE);
         assert transformComponent != null;
         Vector3d selfPosition = transformComponent.getPosition();
         Rotation3f selfRotation = transformComponent.getRotation();
         MotionController motionController = executionSupport.getMotionContextSupport().getActiveMotionController();
         double targetDistanceSquared = targetAvailable ? motionController.waypointDistanceSquared(selfPosition, this.targetPosition) : Double.MAX_VALUE;
         boolean haveSolution;
         if (targetDistanceSquared <= this.activeChargeDistance * this.activeChargeDistance) {
            haveSolution = this.aimingData.computeSolution(selfPosition, this.targetPosition, null);
         } else {
            haveSolution = false;
         }

         if (this.state == BodyMotionCharge.ChargeState.Aiming) {
            this.sensorVisibleState = BodyMotionCharge.ChargeState.Aiming;
            if (!targetAvailable) {
               this.transitionChargeState(BodyMotionCharge.ChargeState.LostTarget, ref, executionSupport, componentAccessor, "no target");
               return true;
            }

            if (haveSolution) {
               turnTo(desiredSteering, this.aimingData, this.relativeTurnSpeed);
               if (this.aimingData.isOnTarget(selfRotation.yaw(), selfRotation.pitch(), this.lockedOnHalfAngleRad)
                  && this.isReachable(ref, componentAccessor, selfPosition, selfRotation, motionController, target, this.targetPosition, targetDistanceSquared)
                  )
                {
                  this.transitionChargeState(BodyMotionCharge.ChargeState.LockedOn, ref, executionSupport, componentAccessor, "on target");
               }

               return true;
            } else {
               NPCPhysicsMath.rotationFromDirection(selfPosition, this.targetPosition, selfRotation, this.rotation);
               turnTo(desiredSteering, this.rotation, this.relativeTurnSpeed);
               return true;
            }
         } else if (this.state == BodyMotionCharge.ChargeState.LockedOn) {
            this.sensorVisibleState = BodyMotionCharge.ChargeState.LockedOn;
            if (targetAvailable && haveSolution) {
               turnTo(desiredSteering, this.aimingData, this.relativeTurnSpeed);
               if (this.aimingData.isOnTarget(selfRotation.yaw(), selfRotation.pitch(), this.lockedOnHalfAngleRad)
                  && this.isReachable(ref, componentAccessor, selfPosition, selfRotation, motionController, target, this.targetPosition, targetDistanceSquared)
                  )
                {
                  if (this.isPhaseOver()) {
                     this.transitionChargeState(BodyMotionCharge.ChargeState.WindingUp, ref, executionSupport, componentAccessor, null);
                  }

                  return true;
               } else {
                  this.transitionChargeState(BodyMotionCharge.ChargeState.Aiming, ref, executionSupport, componentAccessor, "not reachable/not on target");
                  return true;
               }
            } else {
               this.transitionChargeState(BodyMotionCharge.ChargeState.LostTarget, ref, executionSupport, componentAccessor, "no solution");
               return true;
            }
         } else if (this.state == BodyMotionCharge.ChargeState.WindingUp) {
            this.sensorVisibleState = BodyMotionCharge.ChargeState.WindingUp;
            if (this.windingUpUninterruptable) {
               if (targetAvailable) {
                  NPCPhysicsMath.rotationFromDirection(selfPosition, this.targetPosition, selfRotation, this.rotation);
                  turnTo(desiredSteering, this.rotation, this.windingUpRelativeTurnSpeed);
               } else {
                  turnTo(desiredSteering, selfRotation, this.windingUpRelativeTurnSpeed);
               }

               if (this.isPhaseOver()) {
                  this.transitionChargeState(BodyMotionCharge.ChargeState.Launch, ref, executionSupport, componentAccessor, null);
               }

               return true;
            } else if (!haveSolution) {
               this.transitionChargeState(
                  BodyMotionCharge.ChargeState.LostTarget, ref, executionSupport, componentAccessor, "lost target/no solution (interruptable)"
               );
               return true;
            } else {
               turnTo(desiredSteering, this.aimingData, this.windingUpRelativeTurnSpeed);
               if (!this.isPhaseOver()) {
                  return true;
               } else if (this.aimingData.isOnTarget(selfRotation.yaw(), selfRotation.pitch(), this.lockedOnHalfAngleRad)
                  && this.isReachable(ref, componentAccessor, selfPosition, selfRotation, motionController, target, this.targetPosition, targetDistanceSquared)
                  )
                {
                  this.transitionChargeState(BodyMotionCharge.ChargeState.Launch, ref, executionSupport, componentAccessor, null);
                  return true;
               } else {
                  this.transitionChargeState(
                     this.skipLockedOnState ? BodyMotionCharge.ChargeState.Aiming : BodyMotionCharge.ChargeState.LockedOn,
                     ref,
                     executionSupport,
                     componentAccessor,
                     "off target/unreachable"
                  );
                  return true;
               }
            }
         } else {
            if (this.state == BodyMotionCharge.ChargeState.Launch) {
               this.sensorVisibleState = BodyMotionCharge.ChargeState.Launch;
               clearAccumulatedFallDistance(ref, componentAccessor);
               this.computeSmoothedChargeRail(
                  ref,
                  componentAccessor,
                  selfRotation,
                  selfPosition,
                  motionController,
                  this.activeChargeDistance,
                  target,
                  targetAvailable,
                  this.railPath,
                  this.visChargeProbe
               );
               this.updateLaunchChargeReference(selfPosition);
               this.chargeSpeed = 0.0;
               this.chargeDistanceSinceLastRailRefresh = 0.0;
               this.transitionChargeState(BodyMotionCharge.ChargeState.Charging, ref, executionSupport, componentAccessor, null);
               return true;
            }

            if (this.state == BodyMotionCharge.ChargeState.Charging) {
               this.sensorVisibleState = BodyMotionCharge.ChargeState.Charging;
               desiredSteering.clear();
               clearAccumulatedFallDistance(ref, componentAccessor);
               if (motionController.isForcePushed()) {
                  double knockbackAmount = motionController.getCombinedExternalVelocityLength();
                  boolean knockbackIgnored = knockbackAmount < this.knockbackThreshold;
                  if (this.debugChargeState) {
                     int entityId = -1;
                     NetworkId networkId = componentAccessor.getComponent(ref, NetworkId.getComponentType());
                     if (networkId != null) {
                        entityId = networkId.getId();
                     }

                     NPCPlugin.get()
                        .getLogger()
                        .at(Level.INFO)
                        .log(
                           "BodyMotionCharge knockback check entityId=%s role=%s knockbackAmount=%.5f threshold=%.5f ignored=%s",
                           entityId,
                           executionSupport.getName(),
                           knockbackAmount,
                           this.knockbackThreshold,
                           knockbackIgnored
                        );
                  }

                  if (!knockbackIgnored) {
                     this.transitionChargeState(BodyMotionCharge.ChargeState.Knockback, ref, executionSupport, componentAccessor, "force pushed");
                     return true;
                  }

                  motionController.clearExternalForces();
               }

               if (this.railPath.isFinished()) {
                  this.transitionChargeState(BodyMotionCharge.ChargeState.Finished, ref, executionSupport, componentAccessor, null);
                  return true;
               }

               double maxSpeed = this.getEffectiveMaximumChargeSpeed(motionController);
               this.chargeSpeed = Math.min(this.chargeSpeed + this.chargeAcceleration * dt, maxSpeed);
               double step = this.chargeSpeed * dt;
               this.railPath.snapY(selfPosition);
               int oldCursor = this.railPath.getCursor();
               double oldSegmentProgress = this.railPath.getSegmentProgress();
               this.railPath.advance(selfPosition, step, this.railDelta);
               boolean startedNewSegment = this.railPath.getCursor() != oldCursor
                  || oldSegmentProgress <= 1.0E-6 && this.railPath.getSegmentProgress() > 1.0E-6;
               this.renderChargePathDebug(componentAccessor, selfPosition, startedNewSegment);
               this.populateCandidateEntities(ref, executionSupport, componentAccessor);
               this.railConfig.ignoredBlockFilter = this.ignoredBlockFilter;
               this.railConfig.ignoredBlocksFireTriggers = this.ignoredBlockSetTriggers;
               this.railConfig.stopOnEntityHit = this.entityStopsCharge;
               this.railConfig.candidateEntities = this.candidateEntitiesBuffer.isEmpty() ? null : this.candidateEntitiesBuffer;

               try {
                  motionController.applyRailStep(ref, executionSupport.getRole(), this.railDelta, this.railConfig, this.railResult, componentAccessor);
                  clearAccumulatedFallDistance(ref, componentAccessor);
                  this.refreshEntityCollisionHits();
                  this.chargeDistanceSinceLastRailRefresh = this.chargeDistanceSinceLastRailRefresh + this.railDelta.length() * this.railResult.appliedFraction;
               } finally {
                  this.railConfig.candidateEntities = null;
                  this.candidateEntitiesBuffer.clear();
               }

               this.renderChargeCollisionDebug(componentAccessor);
               this.executeBlockCollisionInteraction(ref, componentAccessor);
               this.executeEntityCollisionInteraction(ref, componentAccessor);
               if (this.railResult.obstructed) {
                  this.transitionChargeState(BodyMotionCharge.ChargeState.Obstructed, ref, executionSupport, componentAccessor, null);
                  return true;
               } else if (this.railResult.hitEntity) {
                  this.transitionChargeState(BodyMotionCharge.ChargeState.EntityHit, ref, executionSupport, componentAccessor, null);
                  return true;
               } else if (this.railPath.isFinished()) {
                  this.transitionChargeState(BodyMotionCharge.ChargeState.Finished, ref, executionSupport, componentAccessor, null);
                  return true;
               } else if (this.hasPassedLaunchChargeEndPlane(selfPosition)) {
                  this.transitionChargeState(BodyMotionCharge.ChargeState.Finished, ref, executionSupport, componentAccessor, null);
                  return true;
               } else {
                  this.refreshChargeRailIfNeeded(
                     ref,
                     componentAccessor,
                     selfPosition,
                     selfRotation,
                     motionController,
                     target,
                     targetAvailable,
                     this.getRemainingDistanceToLaunchReference(selfPosition)
                  );
                  return true;
               }
            } else {
               if (this.state != BodyMotionCharge.ChargeState.Knockback
                  && this.state != BodyMotionCharge.ChargeState.EntityHit
                  && this.state != BodyMotionCharge.ChargeState.Obstructed
                  && this.state != BodyMotionCharge.ChargeState.Finished) {
                  this.sensorVisibleState = this.state;
                  return true;
               }

               this.sensorVisibleState = this.state;
               desiredSteering.clear();
               this.clearEntityCollisionHits();
               if (this.isPhaseOver()) {
                  this.transitionChargeState(BodyMotionCharge.ChargeState.LostTarget, ref, executionSupport, componentAccessor, null);
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   private static void clearAccumulatedFallDistance(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
      ComponentType<EntityStore, NPCEntity> componentType = NPCEntity.getComponentType();
      assert componentType != null;
      NPCEntity npcComponent = componentAccessor.getComponent(ref, componentType);
      if (npcComponent != null && npcComponent.getCurrentFallDistance() > 0.0) {
         npcComponent.setCurrentFallDistance(0.0);
      }
   }

   private boolean shouldProbe(Vector3d selfPosition, MotionController motionController) {
      if (this.timeSinceHaveTarget >= this.lastProbeTime + this.probeMaxInterval) {
         return true;
      } else {
         return this.timeSinceHaveTarget < this.lastProbeTime + this.probeMinInterval
            ? false
            : !NPCPhysicsMath.isInViewCone(
               selfPosition, this.lastProbeDirection, this.cosProbeMinDirectionChange, this.targetPosition, motionController.getComponentSelector()
            );
      }
   }

   private boolean isReachable(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull ComponentAccessor<EntityStore> componentAccessor,
      @Nonnull Vector3d selfPosition,
      @Nonnull Rotation3f selfRotation,
      @Nonnull MotionController motionController,
      @Nullable Ref<EntityStore> target,
      @Nonnull Vector3d targetPosition,
      double targetDistanceSquared
   ) {
      boolean computedProbeThisTick = false;
      boolean reachable = this.lastProbeBoundingBoxesOverlapped && this.lastProbeDistanceSquared >= targetDistanceSquared;
      if (this.shouldProbe(selfPosition, motionController)) {
         computedProbeThisTick = true;
         this.lastProbeTime = this.timeSinceHaveTarget;
         double probeDistance = this.probeCharge(ref, componentAccessor, selfRotation, selfPosition, motionController) + this.probeSizeCompensation;
         this.lastProbeDistanceSquared = probeDistance * probeDistance;
         this.lastProbeBoundingBoxesOverlapped = false;
         this.railContactContext.reset();
         reachable = this.lastProbeDistanceSquared >= targetDistanceSquared;
         if (reachable && target != null) {
            this.lastProbeBoundingBoxesOverlapped = this.updateTargetContactContext(
               componentAccessor, motionController, target, targetPosition, targetDistanceSquared, selfPosition
            );
            reachable = this.lastProbeBoundingBoxesOverlapped;
            if (!reachable) {
               this.lastProbeDistanceSquared = Double.MAX_VALUE;
            }
         }
      }

      if (this.visChargeProbe) {
         boolean targetOutsideProbeViewCone = this.isTargetOutsideProbeViewCone(selfPosition, motionController);
         this.renderChargeProbeDebug(componentAccessor, selfPosition, this.targetPosition, reachable, targetOutsideProbeViewCone, computedProbeThisTick);
      }

      return reachable;
   }

   private void refreshChargeRailIfNeeded(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull ComponentAccessor<EntityStore> componentAccessor,
      @Nonnull Vector3d selfPosition,
      @Nonnull Rotation3f selfRotation,
      @Nonnull MotionController motionController,
      @Nullable Ref<EntityStore> target,
      boolean targetAvailable,
      double remainingChargeDistance
   ) {
      if (motionController.is2D()) {
         if (!(this.chargeDistanceSinceLastRailRefresh < this.probeChargeRecomputeDistance)) {
            if (!(remainingChargeDistance <= 1.0E-6)) {
               this.chargeDistanceSinceLastRailRefresh = this.chargeDistanceSinceLastRailRefresh - this.probeChargeRecomputeDistance;
               RailPathRefreshResolver.RefreshDecision refreshDecision = RailPathRefreshResolver.resolve(
                  this.railPath,
                  this.candidateRailPath,
                  selfPosition,
                  remainingChargeDistance,
                  0.05,
                  this.railReplanAnchorPosition,
                  anchorPosition -> this.hasLaunchChargeReference
                     ? computeRemainingDistanceToLaunchEndPlane(anchorPosition, this.launchChargeEndReference, this.launchChargePlaneNormal)
                     : this.railPath.getRemainingDistance(),
                  (probeStartPosition, refreshDistance) -> this.computeSmoothedChargeRailForRefresh(
                     ref,
                     componentAccessor,
                     selfRotation,
                     probeStartPosition,
                     motionController,
                     refreshDistance,
                     target,
                     targetAvailable,
                     this.candidateRailPath,
                     this.visChargeProbe
                  ),
                  this::isLastProbeBlocked,
                  this::isRailReplacementAllowed
               );
               if (this.debugChargePath) {
                  NPCPlugin.get()
                     .getLogger()
                     .at(Level.INFO)
                     .log(
                        "[NPC] BodyMotionCharge rail refresh entityId=%s replaced=%s committedTraversal=%s commitType=%s blocked=%s unreachable=%s",
                        getNetworkId(ref, componentAccessor),
                        refreshDecision.replaced(),
                        refreshDecision.committedAnchor(),
                        refreshDecision.commitType(),
                        refreshDecision.blocked(),
                        refreshDecision.unreachable()
                     );
               }
            }
         }
      }
   }

   private void updateLaunchChargeReference(@Nonnull Vector3dc selfPosition) {
      this.hasLaunchChargeReference = false;
      int waypointCount = this.railPath.getWaypointCount();
      if (waypointCount > 0) {
         this.launchChargeStartReference.set(selfPosition);
         this.launchChargeEndReference.set(this.railPath.getWaypoint(waypointCount - 1));
         this.launchChargePlaneNormal.set(this.launchChargeEndReference).sub(this.launchChargeStartReference);
         if (!(this.launchChargePlaneNormal.lengthSquared() <= 1.0E-12)) {
            this.launchChargePlaneNormal.normalize();
            this.hasLaunchChargeReference = true;
         }
      }
   }

   private double getRemainingDistanceToLaunchReference(@Nonnull Vector3dc selfPosition) {
      return !this.hasLaunchChargeReference
         ? this.railPath.getRemainingDistance()
         : computeRemainingDistanceToLaunchEndPlane(selfPosition, this.launchChargeEndReference, this.launchChargePlaneNormal);
   }

   private boolean hasPassedLaunchChargeEndPlane(@Nonnull Vector3dc selfPosition) {
      if (!this.hasLaunchChargeReference) {
         return false;
      }

      this.chargeProbeEndDirection.set(selfPosition).sub(this.launchChargeEndReference);
      return this.chargeProbeEndDirection.dot(this.launchChargePlaneNormal) >= -1.0E-6;
   }

   static double computeRemainingDistanceToLaunchEndPlane(@Nonnull Vector3dc self, @Nonnull Vector3dc launchEnd, @Nonnull Vector3dc planeNormal) {
      double dx = self.x() - launchEnd.x();
      double dy = self.y() - launchEnd.y();
      double dz = self.z() - launchEnd.z();
      double signedDistanceToPlane = dx * planeNormal.x() + dy * planeNormal.y() + dz * planeNormal.z();
      return signedDistanceToPlane >= 0.0 ? 0.0 : -signedDistanceToPlane;
   }

   private double computeSmoothedChargeRailForRefresh(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull ComponentAccessor<EntityStore> componentAccessor,
      @Nonnull Rotation3f probeRotation,
      @Nonnull Vector3dc probeStartPosition,
      @Nonnull MotionController motionController,
      double chargeDistance,
      @Nullable Ref<EntityStore> target,
      boolean targetAvailable,
      @Nonnull RailPath destination,
      boolean renderProbe
   ) {
      this.railRefreshProbeStartPosition.set(probeStartPosition);
      return this.computeSmoothedChargeRail(
         ref,
         componentAccessor,
         probeRotation,
         this.railRefreshProbeStartPosition,
         motionController,
         chargeDistance,
         target,
         targetAvailable,
         destination,
         renderProbe
      );
   }

   private double computeSmoothedChargeRail(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull ComponentAccessor<EntityStore> componentAccessor,
      @Nonnull Rotation3f probeRotation,
      @Nonnull Vector3d probeStartPosition,
      @Nonnull MotionController motionController,
      double chargeDistance,
      @Nullable Ref<EntityStore> target,
      boolean targetAvailable,
      @Nonnull RailPath destination,
      boolean renderProbe
   ) {
      double probeDistance = this.probeCharge(ref, componentAccessor, probeRotation, probeStartPosition, motionController, chargeDistance);
      double targetDistanceSquared = targetAvailable
         ? motionController.waypointDistanceSquared(probeStartPosition, this.targetPosition)
         : Double.POSITIVE_INFINITY;
      double probeDistanceWithCompensation = probeDistance + this.probeSizeCompensation;
      this.lastRefreshTargetDistance = targetAvailable ? Math.sqrt(targetDistanceSquared) : Double.NaN;
      boolean hadRetainedContactBeforeProbe = this.hasRetainedContactContext;
      this.lastRefreshContactDroppedNearTarget = false;
      if (targetAvailable && probeDistanceWithCompensation * probeDistanceWithCompensation >= targetDistanceSquared) {
         this.updateTargetContactContext(componentAccessor, motionController, target, this.targetPosition, targetDistanceSquared, probeStartPosition);
      } else {
         this.railContactContext.reset();
      }

      if (this.railContactContext.valid) {
         this.retainedContactContext.setFrom(this.railContactContext);
         this.retainedContactProbeStartPosition.set(probeStartPosition);
         this.hasRetainedContactContext = true;
         this.retainedContactTargetDistance = this.lastRefreshTargetDistance;
      } else if (this.canReuseRetainedContactContext(probeStartPosition)) {
         this.railContactContext.setFrom(this.retainedContactContext);
      } else {
         this.retainedContactContext.reset();
         this.hasRetainedContactContext = false;
         this.retainedContactTargetDistance = Double.NaN;
      }

      if (!this.railContactContext.valid
         && hadRetainedContactBeforeProbe
         && Double.isFinite(this.lastRefreshTargetDistance)
         && this.lastRefreshTargetDistance <= 1.5) {
         this.lastRefreshContactDroppedNearTarget = true;
      }

      if (renderProbe) {
         this.renderChargeProbeDebug(
            componentAccessor,
            probeStartPosition,
            this.targetPosition,
            probeDistance * probeDistance >= targetDistanceSquared,
            this.isTargetOutsideProbeViewCone(probeStartPosition, motionController),
            true
         );
      }

      this.smootherConfig.climbSlope = this.climbSlope;
      this.smootherConfig.dropSlope = this.dropSlope;
      this.smootherConfig.horizontalSkipGapWidth = this.horizontalSkipGapWidth;
      this.smootherConfig.blockedWallEndOffset = this.computeBlockedWallEndOffset(chargeDistance, probeDistance);
      this.railPathSmoother.smooth(this.probeMoveData, motionController, this.smootherConfig, this.railContactContext);
      destination.capture(this.railPathSmoother.getWaypoints(), this.railPathSmoother.getWaypointSegmentCommitTypes(), this.railPathSmoother.getWaypointCount());
      return probeDistance;
   }

   private boolean isLastProbeBlocked() {
      if (this.probeMoveData.segmentCount > 0 && this.probeMoveData.segments != null) {
         ProbeMoveData.Segment lastSegment = this.probeMoveData.segments[this.probeMoveData.segmentCount - 1];
         return lastSegment == null || lastSegment.type == null || !lastSegment.type.isBlocked()
            ? false
            : !this.railContactContext.valid
               || !Double.isFinite(this.railContactContext.hitDistanceS)
               || !(lastSegment.distance > this.railContactContext.hitDistanceS + 1.0E-6);
      } else {
         return false;
      }
   }

   private boolean isRailReplacementAllowed() {
      return !this.lastRefreshContactDroppedNearTarget;
   }

   private boolean canReuseRetainedContactContext(@Nonnull Vector3dc probeStartPosition) {
      if (!this.hasRetainedContactContext || !this.retainedContactContext.valid) {
         return false;
      } else if (!Double.isFinite(this.lastRefreshTargetDistance) || !Double.isFinite(this.retainedContactTargetDistance)) {
         return false;
      } else if (this.lastRefreshTargetDistance > 1.5) {
         return false;
      } else {
         return Math.abs(this.lastRefreshTargetDistance - this.retainedContactTargetDistance) > 1.0
            ? false
            : this.retainedContactProbeStartPosition.distance(probeStartPosition) <= 0.5;
      }
   }

   private double computeBlockedWallEndOffset(double chargeDistance, double rawProbeDistance) {
      if (this.probeSizeCompensation <= 0.0) {
         return 0.0;
      }

      double remainingChargeDistance = chargeDistance - rawProbeDistance;
      return remainingChargeDistance <= 0.0 ? 0.0 : Math.min(this.probeSizeCompensation, remainingChargeDistance);
   }

   @Nullable
   private static Integer getNetworkId(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
      NetworkId networkId = componentAccessor.getComponent(ref, NetworkId.getComponentType());
      return networkId != null ? networkId.getId() : null;
   }

   private double probeCharge(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull ComponentAccessor<EntityStore> componentAccessor,
      Rotation3f selfRotation,
      Vector3d selfPosition,
      MotionController motionController
   ) {
      return this.probeCharge(ref, componentAccessor, selfRotation, selfPosition, motionController, this.activeChargeDistance);
   }

   private double probeCharge(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull ComponentAccessor<EntityStore> componentAccessor,
      Rotation3f selfRotation,
      Vector3d selfPosition,
      MotionController motionController,
      double chargeDistance
   ) {
      PhysicsMath.vectorFromAngles(selfRotation.yaw(), selfRotation.pitch(), this.chargeDirection);
      this.chargeDirection.normalize(chargeDistance);
      this.chargeStartPosition.set(selfPosition);
      this.chargeEndPosition.set(this.chargeStartPosition).add(this.chargeDirection);
      this.lastProbeDirection.set(this.chargeDirection).mul(motionController.getComponentSelector()).normalize();
      this.probeMoveData.setSaveSegments(true);
      double probeDistance = motionController.probeMove(ref, this.chargeStartPosition, this.chargeDirection, this.probeMoveData, componentAccessor);
      this.chargeProbeEndPosition.set(this.probeMoveData.probePosition);
      this.hasProbeEndPosition = true;
      this.probeMoveData.setSaveSegments(false);
      return probeDistance;
   }

   private void renderChargeProbeDebug(
      @Nonnull ComponentAccessor<EntityStore> componentAccessor,
      @Nonnull Vector3dc selfPosition,
      @Nonnull Vector3dc currentTargetPosition,
      boolean reachable,
      boolean targetOutsideProbeViewCone,
      boolean computedProbeThisTick
   ) {
      World world = componentAccessor.getExternalData().getWorld();
      Vector3f color = reachable && !targetOutsideProbeViewCone ? DebugUtils.COLOR_LIME : DebugUtils.COLOR_RED;
      float opacity = computedProbeThisTick ? 1.0F : 0.45F;
      this.chargeProbeArrowOrigin.set(selfPosition);
      this.chargeProbeTargetDirection.set(currentTargetPosition).sub(selfPosition);
      if (this.chargeProbeTargetDirection.lengthSquared() > 1.0E-12) {
         DebugUtils.addArrow(world, this.chargeProbeArrowOrigin, this.chargeProbeTargetDirection, color, opacity, 0.05F, 0);
      }

      if (this.hasProbeEndPosition) {
         this.chargeProbeEndDirection.set(this.chargeProbeEndPosition).sub(selfPosition);
         if (this.chargeProbeEndDirection.lengthSquared() > 1.0E-12) {
            DebugUtils.addArrow(world, this.chargeProbeArrowOrigin, this.chargeProbeEndDirection, color, opacity, 0.05F, 0);
         }

         DebugUtils.addSphere(world, this.chargeProbeEndPosition.x, this.chargeProbeEndPosition.y, this.chargeProbeEndPosition.z, color, opacity, 1.0, 0.05F);
      }
   }

   private boolean isTargetOutsideProbeViewCone(@Nonnull Vector3d selfPosition, @Nonnull MotionController motionController) {
      return !NPCPhysicsMath.isInViewCone(
         selfPosition, this.lastProbeDirection, this.cosProbeMinDirectionChange, this.targetPosition, motionController.getComponentSelector()
      );
   }

   private boolean updateTargetContactContext(
      @Nonnull ComponentAccessor<EntityStore> componentAccessor,
      @Nonnull MotionController motionController,
      @Nullable Ref<EntityStore> target,
      @Nonnull Vector3d targetPosition,
      double targetDistanceSquared,
      @Nonnull Vector3dc probeStartPosition
   ) {
      this.railContactContext.reset();
      if (target == null) {
         return false;
      }

      BoundingBox targetBoundingBoxComponent = componentAccessor.getComponent(target, BoundingBox.getComponentType());
      if (targetBoundingBoxComponent == null) {
         return false;
      }

      this.npcWorldBoundingBox.assign(motionController.getCollisionBoundingBox());
      this.targetWorldBoundingBox.assign(targetBoundingBoxComponent.getBoundingBox()).offset(targetPosition);
      double wallOffset = NPCPhysicsMath.getMinBoundingBoxDistance(
         this.npcWorldBoundingBox, this.targetWorldBoundingBox, motionController.getComponentSelector()
      );
      if (wallOffset <= 0.0) {
         return false;
      }

      this.railContactContext.contactWindowHalfWidth = MathUtil.clamp(wallOffset, 1.0E-6, 1.5);
      double targetDistance = Math.sqrt(targetDistanceSquared);
      double maxProbeDistance = this.probeMoveData.segments[this.probeMoveData.segmentCount - 1].distance;
      int sampleCount = TargetContactSamplePlanner.planSamples(
         this.probeMoveData,
         targetDistance,
         wallOffset,
         maxProbeDistance,
         0.001,
         this.targetContactSampleScratch,
         this.targetContactSampleDistances,
         this.targetContactSampleSides
      );

      for (int i = 0; i < sampleCount; i++) {
         if (this.tryContactSample(
            this.targetContactSampleDistances[i], this.targetContactSampleSides[i], motionController, probeStartPosition, maxProbeDistance
         )) {
            return true;
         }
      }

      return false;
   }

   private boolean tryContactSample(
      double sampleDistance,
      @Nonnull RailPathSmoother.HitSide side,
      @Nonnull MotionController motionController,
      @Nonnull Vector3dc probeStartPosition,
      double maxProbeDistance
   ) {
      double clampedDistance = MathUtil.clamp(sampleDistance, 0.0, maxProbeDistance);
      if (!this.probeMoveData.computePosition(clampedDistance, this.expectedHitPosition)) {
         return false;
      }

      this.npcWorldBoundingBox.assign(motionController.getCollisionBoundingBox()).offset(this.expectedHitPosition);
      boolean intersects = this.npcWorldBoundingBox.isIntersecting(this.targetWorldBoundingBox);
      if (!intersects) {
         return false;
      }

      this.updateContactContext(clampedDistance, this.expectedHitPosition.y, side, probeStartPosition);
      return true;
   }

   private void updateContactContext(double hitDistance, double hitY, @Nonnull RailPathSmoother.HitSide hitSide, @Nonnull Vector3dc probeStartPosition) {
      assert this.probeMoveData.initialPosition.distanceSquared(probeStartPosition) <= 1.0E-12;
      this.railContactContext.valid = true;
      this.railContactContext.hitDistanceS = hitDistance;
      this.railContactContext.hitY = hitY;
      this.railContactContext.hitSide = hitSide;
      this.railContactContext.hitSegmentIndex = -1;
      if (this.probeMoveData.locateSegmentAtDistance(hitDistance, this.hitSegmentLocation)) {
         this.railContactContext.hitSegmentIndex = this.hitSegmentLocation.segmentIndex;
      }
   }

   private void transitionChargeState(
      @Nonnull BodyMotionCharge.ChargeState newState,
      @Nonnull Ref<EntityStore> ref,
      @Nonnull ExecutionSupport executionSupport,
      @Nonnull ComponentAccessor<EntityStore> componentAccessor,
      @Nullable String reason
   ) {
      if (newState != this.state) {
         BodyMotionCharge.ChargeState previousState = this.state;
         if (newState == BodyMotionCharge.ChargeState.LockedOn && this.skipLockedOnState) {
            newState = BodyMotionCharge.ChargeState.WindingUp;
         }

         if (newState == BodyMotionCharge.ChargeState.WindingUp && this.skipWindingUpState) {
            newState = BodyMotionCharge.ChargeState.Launch;
         }

         switch (newState) {
            case LockedOn:
               this.initPhaseDuration(this.lockedOnDurationRange);
            case LostTarget:
            case Launch:
            case Charging:
            default:
               break;
            case WindingUp:
               this.initPhaseDuration(this.windingUpDurationRange);
               break;
            case Obstructed:
            case EntityHit:
            case Knockback:
            case Finished:
               this.initPhaseDuration(this.postChargeDurationRange);
         }

         if (this.parentInstruction != null) {
            this.parentInstruction.clearOnce();
         }

         if (this.debugChargeState) {
            renderChargeOutcomeDebugMarkerIfNeeded(previousState, newState, ref, componentAccessor);
         }

         if (this.debugChargeState) {
            Integer entityId = null;
            NetworkId networkId = componentAccessor.getComponent(ref, NetworkId.getComponentType());
            if (networkId != null) {
               entityId = networkId.getId();
            }

            NPCPlugin.get()
               .getLogger()
               .at(Level.INFO)
               .log(
                  "BodyMotionCharge state %s -> %s role=%s entityId=%s reason=%s",
                  this.state,
                  newState,
                  executionSupport.getRole().getRoleName(),
                  entityId,
                  reason != null ? reason : "<no reason>"
               );
         }

         this.state = newState;
      }
   }

   private static void renderChargeOutcomeDebugMarkerIfNeeded(
      @Nonnull BodyMotionCharge.ChargeState previousState,
      @Nonnull BodyMotionCharge.ChargeState newState,
      @Nonnull Ref<EntityStore> ref,
      @Nonnull ComponentAccessor<EntityStore> componentAccessor
   ) {
      if (previousState == BodyMotionCharge.ChargeState.Charging) {
         Vector3f color = switch (newState) {
            case Obstructed -> DebugUtils.COLOR_RED;
            case EntityHit -> DebugUtils.COLOR_GREEN;
            case Knockback -> DebugUtils.COLOR_CYAN;
            case Finished -> DebugUtils.COLOR_WHITE;
            default -> null;
         };
         if (color != null) {
            TransformComponent transformComponent = componentAccessor.getComponent(ref, TRANSFORM_COMPONENT_TYPE);
            if (transformComponent != null) {
               Vector3d position = transformComponent.getPosition();
               World world = componentAccessor.getExternalData().getWorld();
               DebugUtils.addSphere(world, position.x, position.y, position.z, color, 1.0, 5.0F);
            }
         }
      }
   }

   private void renderChargePathDebug(@Nonnull ComponentAccessor<EntityStore> componentAccessor, @Nonnull Vector3dc selfPosition, boolean startedNewSegment) {
      if (this.visChargePath) {
         if (!(this.railDelta.lengthSquared() <= 1.0E-12)) {
            this.railStepTargetPosition.set(selfPosition).add(this.railDelta);
            Vector3f color = startedNewSegment ? DebugUtils.COLOR_BLUE : DebugUtils.COLOR_WHITE;
            World world = componentAccessor.getExternalData().getWorld();
            DebugUtils.addSphere(world, this.railStepTargetPosition.x, this.railStepTargetPosition.y, this.railStepTargetPosition.z, color, 0.2, 5.0F);
         }
      }
   }

   private void renderChargeCollisionDebug(@Nonnull ComponentAccessor<EntityStore> componentAccessor) {
      if (this.visChargeCollisions || this.visChargeEntityHits) {
         World world = componentAccessor.getExternalData().getWorld();
         if (this.visChargeCollisions) {
            int blockHitCount = this.railResult.getPassThroughCount();

            for (int i = 0; i < blockHitCount; i++) {
               BlockHit hit = this.railResult.getPassThrough(i);
               DebugUtils.addCube(world, hit.blockX + 0.5, hit.blockY + 0.5, hit.blockZ + 0.5, DebugUtils.COLOR_RED, 0.2, 5.0F);
            }
         }

         int entityHitCount = this.railResult.getEntityHitCount();
         Vector3f color = this.visChargeEntityHits ? DebugUtils.COLOR_YELLOW : DebugUtils.COLOR_GREEN;

         for (int i = 0; i < entityHitCount; i++) {
            EntityHit hit = this.railResult.getEntityHit(i);
            if (!this.visChargeEntityHits || this.acceptedEntityHitIndexes.get(i)) {
               DebugUtils.addCube(world, hit.targetPosition.x, hit.targetPosition.y, hit.targetPosition.z, color, 0.2, 5.0F);
            }
         }
      }
   }

   private void populateCandidateEntities(
      @Nonnull Ref<EntityStore> ref, @Nonnull ExecutionSupport executionSupport, @Nonnull ComponentAccessor<EntityStore> componentAccessor
   ) {
      this.candidateEntitiesBuffer.clear();
      PositionCache positionCache = executionSupport.getPositionCache();
      positionCache.getPlayers()
         .forEachEntityUnordered(
            14.8F, NOT_SELF_PREDICATE, COLLECT_ENTITY_CONSUMER, ref, null, this.candidateEntitiesBuffer, componentAccessor, componentAccessor
         );
      positionCache.getNpcs()
         .forEachEntityUnordered(
            14.8F, NOT_SELF_PREDICATE, COLLECT_ENTITY_CONSUMER, ref, null, this.candidateEntitiesBuffer, componentAccessor, componentAccessor
         );
   }

   private boolean isPhaseOver() {
      return this.timeSinceHaveTarget >= this.phaseEndTime;
   }

   private void initPhaseDuration(@Nonnull double[] durationRange) {
      double activePhaseDuration = RandomExtra.randomRange(durationRange);
      this.phaseEndTime = this.timeSinceHaveTarget + activePhaseDuration;
   }

   private static boolean isZeroDurationRange(@Nonnull double[] durationRange) {
      return durationRange[0] == 0.0 && durationRange[1] == 0.0;
   }

   @Nonnull
   public BodyMotionCharge.ChargeState getState() {
      return this.sensorVisibleState;
   }

   public int getBlockHitCount() {
      return this.railResult.getPassThroughCount();
   }

   @Nonnull
   public BlockHit getBlockHit(int i) {
      return this.railResult.getPassThrough(i);
   }

   public int getEntityHitCount() {
      return this.filteredEntityHits.size();
   }

   @Nonnull
   public EntityHit getEntityHit(int i) {
      return this.filteredEntityHits.get(i);
   }

   private void executeBlockCollisionInteraction(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
      if (this.blockCollisionInteractionId != null) {
         InteractionManager interactionManagerComponent = componentAccessor.getComponent(ref, InteractionModule.get().getInteractionManagerComponent());
         if (interactionManagerComponent != null) {
            RootInteraction rootInteraction = RootInteraction.getRootInteractionOrUnknown(this.blockCollisionInteractionId);
            World world = componentAccessor.getExternalData().getWorld();
            int hitCount = this.railResult.getPassThroughCount();

            for (int i = 0; i < hitCount; i++) {
               BlockHit hit = this.railResult.getPassThrough(i);
               BlockPosition pos = new BlockPosition(hit.blockX, hit.blockY, hit.blockZ);
               InteractionContext context = InteractionContext.forInteraction(interactionManagerComponent, ref, INTERACTION_TYPE, componentAccessor);
               context.getMetaStore().putMetaObject(Interaction.TARGET_BLOCK_RAW, pos);
               context.getMetaStore().putMetaObject(Interaction.TARGET_BLOCK, world.getBaseBlock(pos));
               InteractionChain chain = interactionManagerComponent.initChain(INTERACTION_TYPE, context, rootInteraction, -1, pos, false);
               interactionManagerComponent.queueExecuteChain(chain);
            }
         }
      }
   }

   private void executeEntityCollisionInteraction(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
      if (this.npcCollisionInteractionId != null || this.playerCollisionInteractionId != null) {
         InteractionManager interactionManagerComponent = componentAccessor.getComponent(ref, InteractionModule.get().getInteractionManagerComponent());
         if (interactionManagerComponent != null) {
            RootInteraction npcRootInteraction = this.npcCollisionInteractionId != null
               ? RootInteraction.getRootInteractionOrUnknown(this.npcCollisionInteractionId)
               : null;
            RootInteraction playerRootInteraction = this.playerCollisionInteractionId != null
               ? RootInteraction.getRootInteractionOrUnknown(this.playerCollisionInteractionId)
               : null;
            if (npcRootInteraction != null || playerRootInteraction != null) {
               int hitCount = this.filteredEntityHits.size();

               for (int i = 0; i < hitCount; i++) {
                  EntityHit hit = this.filteredEntityHits.get(i);
                  RootInteraction rootInteraction = hit.isPlayer ? playerRootInteraction : npcRootInteraction;
                  if (rootInteraction != null) {
                     Ref<EntityStore> targetRef = hit.entity;
                     if (targetRef != null && targetRef.isValid()) {
                        NetworkId networkIdComponent = componentAccessor.getComponent(targetRef, NetworkId.getComponentType());
                        int networkId = networkIdComponent != null ? networkIdComponent.getId() : -1;
                        InteractionContext context = InteractionContext.forInteraction(interactionManagerComponent, ref, INTERACTION_TYPE, componentAccessor);
                        context.getMetaStore().putMetaObject(Interaction.TARGET_ENTITY, targetRef);
                        InteractionChain chain = interactionManagerComponent.initChain(INTERACTION_TYPE, context, rootInteraction, networkId, null, false);
                        interactionManagerComponent.queueExecuteChain(chain);
                     }
                  }
               }
            }
         }
      }
   }

   private void refreshEntityCollisionHits() {
      this.filteredEntityHits.clear();
      this.acceptedEntityHitIndexes.clear();
      int hitCount = this.railResult.getEntityHitCount();
      if (hitCount != 0) {
         if (this.repeatCollisionIgnoreDuration <= 0.0) {
            this.lastEntityCollisionHitTimes.clear();

            for (int i = 0; i < hitCount; i++) {
               this.filteredEntityHits.add(this.railResult.getEntityHit(i));
               this.acceptedEntityHitIndexes.set(i);
            }
         } else {
            double expirationTime = this.timeSinceHaveTarget - this.repeatCollisionIgnoreDuration;
            ObjectIterator<Reference2DoubleMap.Entry<Ref<EntityStore>>> iterator = this.lastEntityCollisionHitTimes.reference2DoubleEntrySet().iterator();

            while (iterator.hasNext()) {
               Reference2DoubleMap.Entry<Ref<EntityStore>> entry = iterator.next();
               Ref<EntityStore> ref = entry.getKey();
               if (entry.getDoubleValue() <= expirationTime || ref == null || !ref.isValid()) {
                  iterator.remove();
               }
            }

            for (int i = 0; i < hitCount; i++) {
               EntityHit hit = this.railResult.getEntityHit(i);
               Ref<EntityStore> targetRef = hit.entity;
               if (targetRef != null && targetRef.isValid()) {
                  double lastHitTime = this.lastEntityCollisionHitTimes.getOrDefault(targetRef, Double.NEGATIVE_INFINITY);
                  if (!(this.timeSinceHaveTarget - lastHitTime < this.repeatCollisionIgnoreDuration)) {
                     this.filteredEntityHits.add(hit);
                     this.acceptedEntityHitIndexes.set(i);
                     this.lastEntityCollisionHitTimes.put(targetRef, this.timeSinceHaveTarget);
                  }
               }
            }
         }
      }
   }

   private void clearEntityCollisionHits() {
      this.filteredEntityHits.clear();
      this.acceptedEntityHitIndexes.clear();
      this.lastEntityCollisionHitTimes.clear();
   }

   private double getEffectiveMaximumChargeSpeed(@Nullable MotionController motionController) {
      if (this.chargeAbsoluteSpeed > 0.0) {
         return this.chargeAbsoluteSpeed;
      }

      double maximumSpeed = motionController != null ? motionController.getMaximumSpeed() : 0.0;
      return this.chargeRelativeSpeed * maximumSpeed;
   }

   @Override
   public void onDebugFlagsChanged(EnumSet<RoleDebugFlags> newFlags) {
      this.debugChargeState = newFlags.contains(RoleDebugFlags.ChargeState);
      this.debugChargePath = newFlags.contains(RoleDebugFlags.ChargePath);
      this.railPathSmoother.setDebug(this.debugChargePath);
      this.visChargePath = newFlags.contains(RoleDebugFlags.VisChargePath);
      this.visChargeCollisions = newFlags.contains(RoleDebugFlags.VisChargeCollisions);
      this.visChargeEntityHits = newFlags.contains(RoleDebugFlags.VisChargeEntityHits);
      this.visChargeProbe = newFlags.contains(RoleDebugFlags.VisChargeProbe);
   }

   private static void turnTo(@Nonnull Steering desiredSteering, @Nonnull Rotation3f rotation, double turnSpeed) {
      desiredSteering.setYaw(rotation.yaw());
      desiredSteering.setPitch(rotation.pitch());
      desiredSteering.setRelativeTurnSpeed(turnSpeed);
   }

   private static void turnTo(@Nonnull Steering desiredSteering, @Nonnull AimingData aimingData, double turnSpeed) {
      desiredSteering.setYaw(aimingData.getYaw());
      desiredSteering.setPitch(aimingData.getPitch());
      desiredSteering.setRelativeTurnSpeed(turnSpeed);
   }

   public enum ChargeState implements Supplier<String> {
      Aiming("Have a target"),
      LockedOn("Target in range and within view cone"),
      LostTarget("Target lost or out of range"),
      WindingUp("Preparing to charge"),
      Launch("Starting the charge"),
      Charging("Charging"),
      Obstructed("Charge obstructed by something"),
      EntityHit("Charge stopped at an entity"),
      Knockback("Charge obstructed by knockback or other ext force"),
      Finished("Finished charge");

      private final String description;

      ChargeState(String description) {
         this.description = description;
      }

      public String get() {
         return this.description;
      }
   }
}
