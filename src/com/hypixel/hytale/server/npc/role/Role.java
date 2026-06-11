package com.hypixel.hytale.server.npc.role;

import com.hypixel.hytale.common.util.ArrayUtil;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.random.RandomExtra;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.util.TrigMathUtil;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import com.hypixel.hytale.protocol.BlockMaterial;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.debug.DebugUtils;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.item.ItemModule;
import com.hypixel.hytale.server.core.modules.splitvelocity.VelocityConfig;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.instructions.BodyMotion;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.instructions.IndexedInstructions;
import com.hypixel.hytale.server.npc.instructions.Instruction;
import com.hypixel.hytale.server.npc.movement.GroupSteeringAccumulator;
import com.hypixel.hytale.server.npc.movement.Steering;
import com.hypixel.hytale.server.npc.movement.constraints.RelaxedConstraint;
import com.hypixel.hytale.server.npc.movement.controllers.MotionController;
import com.hypixel.hytale.server.npc.movement.steeringforces.SteeringForceAvoidCollision;
import com.hypixel.hytale.server.npc.role.builders.BuilderRole;
import com.hypixel.hytale.server.npc.role.support.CombatSupport;
import com.hypixel.hytale.server.npc.role.support.DebugSupport;
import com.hypixel.hytale.server.npc.role.support.DisplayNameSupport;
import com.hypixel.hytale.server.npc.role.support.EntitySupport;
import com.hypixel.hytale.server.npc.role.support.FlagsComponent;
import com.hypixel.hytale.server.npc.role.support.MarkedEntitySupport;
import com.hypixel.hytale.server.npc.role.support.MotionContextSupport;
import com.hypixel.hytale.server.npc.role.support.PlayerTaskSupport;
import com.hypixel.hytale.server.npc.role.support.PositionCache;
import com.hypixel.hytale.server.npc.role.support.StateSupport;
import com.hypixel.hytale.server.npc.role.support.WorldSupport;
import com.hypixel.hytale.server.npc.statetransition.StateTransitionController;
import com.hypixel.hytale.server.npc.util.ComponentInfo;
import com.hypixel.hytale.server.npc.util.IAnnotatedComponent;
import com.hypixel.hytale.server.npc.util.IAnnotatedComponentCollection;
import com.hypixel.hytale.server.npc.util.InventoryHelper;
import com.hypixel.hytale.server.npc.util.NPCPhysicsMath;
import com.hypixel.hytale.server.npc.util.VisHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;

public class Role implements IAnnotatedComponentCollection {
   public static final boolean DEBUG_APPLIED_FORCES = false;
   public static final double INTERACTION_PLAYER_DISTANCE = 10.0;
   public static final double RANDOMIZE_OFFSET_MAX_DISTANCE = 0.001;
   public static final double RANDOMIZE_OFFSET_SQUARED_MAX_DISTANCE = 1.0E-6;
   private static final double MIN_SEPARATION_SUMMED_SQUARED = 0.010000000000000002;
   protected final int initialMaxHealth;
   protected final double collisionProbeDistance;
   protected final double collisionRadius;
   protected final double collisionForceFalloff;
   protected final float collisionViewAngle;
   protected final float collisionViewHalfAngleCosine;
   protected final Steering bodySteering = new Steering();
   protected final Steering headSteering = new Steering();
   protected final SteeringForceAvoidCollision steeringForceAvoidCollision = new SteeringForceAvoidCollision();
   protected final GroupSteeringAccumulator groupSteeringAccumulator = new GroupSteeringAccumulator();
   protected final Vector3d separationTempDistanceVector = new Vector3d();
   protected final Vector3d separationTempSteeringVector = new Vector3d();
   protected final Set<Ref<EntityStore>> ignoredEntitiesForAvoidance = new ReferenceOpenHashSet<>();
   protected final double entityAvoidanceStrength;
   protected final Role.AvoidanceMode avoidanceMode;
   protected final boolean isAvoidingEntities;
   protected final Role.SeparationMode separationMode;
   protected final boolean useOrientationHint;
   protected final boolean alwaysApplySeparation;
   protected final boolean normalizeDistances;
   protected final double separationDistance;
   protected final double separationWeight;
   protected final double separationDistanceTarget;
   protected final double separationNearRadiusTarget;
   protected final double separationFarRadiusTarget;
   protected final boolean applySeparation;
   protected final double separationSafeDistanceMultiplier;
   protected final double separationLegacySteeringStrength;
   protected final double separationPushSteeringStrength;
   protected final double separationPushDistanceWeightDefault;
   protected final double separationPushDistanceWeightTarget;
   protected final double separationPushDistanceWeightAttacker;
   protected final double separationPushSpeedScale;
   protected final Vector3d lastSeparationSteering = new Vector3d();
   protected int separationSummedCount;
   protected final Vector3d separationSummedDistances = new Vector3d();
   @Nullable
   protected final float[] headPitchAngleRange;
   protected final boolean stayInEnvironment;
   protected final String allowedEnvironments;
   @Nullable
   protected final String[] flockSpawnTypes;
   protected final boolean flockSpawnTypesRandom;
   @Nonnull
   protected final String[] flockAllowedRoles;
   protected final boolean canLeadFlock;
   protected final double flockWeightAlignment;
   protected final double flockWeightSeparation;
   protected final double flockWeightCohesion;
   protected final double flockInfluenceRange;
   protected final boolean corpseStaysInFlock;
   protected final double inertia;
   protected final double knockbackScale;
   protected final boolean breathesInAir;
   protected final boolean breathesInWater;
   protected final boolean pickupDropOnDeath;
   @Nullable
   protected final String[] hotbarItems;
   @Nullable
   protected final String[] offHandItems;
   protected final double deathAnimationTime;
   protected final String deathParticles;
   protected final boolean dropDeathItemsInstantly;
   protected final float despawnAnimationTime;
   protected final String dropListId;
   @Nullable
   protected final String deathInteraction;
   protected final boolean invulnerable;
   protected final int inventorySlots;
   protected final String inventoryContentsDropList;
   protected final int hotbarSlots;
   protected final int offHandSlots;
   protected final byte defaultOffHandSlot;
   @Nullable
   protected final String balanceAsset;
   @Nullable
   protected final Map<String, String> interactionVars;
   protected int roleIndex;
   protected String roleName;
   protected String appearance;
   @Nonnull
   protected Map<String, MotionController> motionControllers = new HashMap<>();
   @Nullable
   protected String initialMotionControllerName;
   protected MotionController activeMotionController;
   protected int[] flockSpawnTypeIndices;
   protected boolean requiresLeashPosition;
   protected boolean hasReachedTerminalAction;
   @Nullable
   protected String[] armor;
   protected Instruction rootInstruction;
   @Nullable
   protected Instruction lastBodyMotionStep;
   @Nullable
   protected Instruction lastHeadMotionStep;
   protected IndexedInstructions indexedInstructions;
   @Nullable
   protected Instruction interactionInstruction;
   @Nullable
   protected Instruction deathInstruction;
   protected boolean roleChangeRequested;
   protected final boolean isMemory;
   protected final String memoriesNameOverride;
   protected final boolean isMemoriesNameOverriden;
   protected final float spawnLockTime;
   protected final String nameTranslationKey;
   protected boolean backingAway;
   protected boolean deathItemsDropped;

   @Nonnull
   public static Role createAndAttach(@Nonnull Holder<EntityStore> holder, @Nonnull BuilderRole builder, @Nonnull BuilderSupport builderSupport) {
      CombatSupport combatSupport = new CombatSupport(builder, builderSupport);
      StateSupport stateSupport = new StateSupport(builder, builderSupport);
      MarkedEntitySupport markedEntitySupport = new MarkedEntitySupport();
      WorldSupport worldSupport = new WorldSupport(builder, builderSupport);
      EntitySupport entitySupport = new EntitySupport();
      PositionCache positionCache = new PositionCache(builderSupport.getRoleStats());
      positionCache.setOpaqueBlockSet(builder.getOpaqueBlockSet());
      DebugSupport debugSupport = new DebugSupport(builder);
      FlagsComponent flagsComponent = new FlagsComponent();
      DisplayNameSupport displayNameSupport = new DisplayNameSupport();
      displayNameSupport.setDisplayNames(builder.getDisplayNames());
      MotionContextSupport motionContextSupport = new MotionContextSupport();
      PlayerTaskSupport playerTaskSupport = new PlayerTaskSupport();
      holder.putComponent(CombatSupport.getComponentType(), combatSupport);
      holder.putComponent(StateSupport.getComponentType(), stateSupport);
      holder.putComponent(MarkedEntitySupport.getComponentType(), markedEntitySupport);
      holder.putComponent(WorldSupport.getComponentType(), worldSupport);
      holder.putComponent(EntitySupport.getComponentType(), entitySupport);
      holder.putComponent(PositionCache.getComponentType(), positionCache);
      holder.putComponent(DebugSupport.getComponentType(), debugSupport);
      holder.putComponent(FlagsComponent.getComponentType(), flagsComponent);
      holder.putComponent(DisplayNameSupport.getComponentType(), displayNameSupport);
      holder.putComponent(MotionContextSupport.getComponentType(), motionContextSupport);
      holder.putComponent(PlayerTaskSupport.getComponentType(), playerTaskSupport);
      Role role = new Role(builder, builderSupport);
      role.postRoleBuilt(holder, builderSupport);
      role.preInitMotionControllers(debugSupport, builder.getMotionControllerMap(builderSupport), builder.getInitialMotionController(builderSupport));
      return role;
   }

   private Role(@Nonnull BuilderRole builder, @Nonnull BuilderSupport builderSupport) {
      this.initialMaxHealth = builder.getMaxHealth(builderSupport);
      this.nameTranslationKey = builder.getNameTranslationKey(builderSupport);
      this.appearance = builder.getAppearance(builderSupport);
      this.hotbarItems = builder.getHotbarItems(builderSupport);
      this.offHandItems = builder.getOffHandItems(builderSupport);
      this.defaultOffHandSlot = builder.getDefaultOffHandSlot(builderSupport);
      this.inventoryContentsDropList = builder.getInventoryItemsDropList(builderSupport);
      this.armor = builder.getArmor();
      this.inertia = builder.getInertia();

      for (MotionController motionController : this.motionControllers.values()) {
         motionController.setInertia(this.inertia);
      }

      this.knockbackScale = builder.getKnockbackScale(builderSupport);

      for (MotionController motionController : this.motionControllers.values()) {
         motionController.setKnockbackScale(this.knockbackScale);
      }

      this.dropListId = builder.getDropListId(builderSupport);
      this.isAvoidingEntities = builder.isAvoidingEntities();
      this.avoidanceMode = builder.getAvoidanceMode(builderSupport);
      this.collisionProbeDistance = builder.getCollisionDistance();
      this.collisionForceFalloff = builder.getCollisionForceFalloff();
      this.collisionRadius = builder.getCollisionRadius();
      this.collisionViewAngle = builder.getCollisionViewAngle();
      this.collisionViewHalfAngleCosine = TrigMathUtil.cos(this.collisionViewAngle / 2.0F);
      this.separationMode = builder.getSeparationMode(builderSupport);
      this.separationDistance = builder.getSeparationDistance(builderSupport);
      this.separationWeight = builder.getSeparationWeight(builderSupport);
      this.separationDistanceTarget = builder.getSeparationDistanceTarget(builderSupport);
      this.separationNearRadiusTarget = builder.getSeparationNearRadiusTarget(builderSupport);
      this.separationFarRadiusTarget = builder.getSeparationFarRadiusTarget(builderSupport);
      this.applySeparation = builder.isApplySeparation(builderSupport);
      this.separationSafeDistanceMultiplier = builder.getSeparationSafeDistanceMultiplier(builderSupport);
      this.separationLegacySteeringStrength = builder.getSeparationLegacySteeringStrength(builderSupport);
      this.separationPushSteeringStrength = builder.getSeparationPushSteeringStrength(builderSupport);
      this.separationPushDistanceWeightDefault = builder.getSeparationPushDistanceWeightDefault(builderSupport);
      this.separationPushDistanceWeightTarget = builder.getSeparationPushDistanceWeightTarget(builderSupport);
      this.separationPushDistanceWeightAttacker = builder.getSeparationPushDistanceWeightAttacker(builderSupport);
      this.separationPushSpeedScale = builder.getSeparationPushSpeedScale(builderSupport);
      this.useOrientationHint = builder.getOverrideUseOrientationHint(builderSupport).evaluate(this.separationMode != Role.SeparationMode.Legacy);
      this.alwaysApplySeparation = builder.getOverrideAlwaysSeparate(builderSupport).evaluate(this.separationMode != Role.SeparationMode.Legacy);
      this.normalizeDistances = builder.getOverrideNormalizeDistances(builderSupport).evaluate(this.separationMode != Role.SeparationMode.Legacy);
      if (builder.isOverridingHeadPitchAngle(builderSupport)) {
         this.headPitchAngleRange = builder.getHeadPitchAngleRange(builderSupport);
      } else {
         this.headPitchAngleRange = null;
      }

      this.stayInEnvironment = builder.isStayingInEnvironment();
      this.allowedEnvironments = builder.getAllowedEnvironments();
      this.entityAvoidanceStrength = builder.getEntityAvoidanceStrength();
      this.flockSpawnTypes = builder.getFlockSpawnTypes(builderSupport);
      this.flockSpawnTypesRandom = builder.isFlockSpawnTypeRandom(builderSupport);
      this.flockAllowedRoles = builder.getFlockAllowedRoles(builderSupport);
      this.canLeadFlock = builder.isCanLeadFlock(builderSupport);
      this.flockWeightAlignment = builder.getFlockWeightAlignment();
      this.flockWeightSeparation = builder.getFlockWeightSeparation();
      this.flockWeightCohesion = builder.getFlockWeightCohesion();
      this.flockInfluenceRange = builder.getFlockInfluenceRange();
      this.invulnerable = builder.isInvulnerable(builderSupport);
      this.breathesInAir = builder.breathesInAir(builderSupport.getExecutionContext(), null);
      this.breathesInWater = builder.breathesInWater(builderSupport.getExecutionContext(), null);
      this.pickupDropOnDeath = builder.isPickupDropOnDeath();
      this.deathAnimationTime = builder.getDeathAnimationTime(builderSupport);
      this.deathParticles = builder.getDeathParticles(builderSupport);
      this.dropDeathItemsInstantly = builder.isDropDeathItemsInstantly(builderSupport);
      this.deathInteraction = builder.getDeathInteraction(builderSupport);
      this.despawnAnimationTime = builder.getDespawnAnimationTime();
      this.inventorySlots = builder.getInventorySlots();
      this.hotbarSlots = builder.getHotbarSlots();
      this.offHandSlots = builder.getOffHandSlots();
      this.corpseStaysInFlock = builder.isCorpseStaysInFlock();
      this.balanceAsset = builder.getBalanceAsset(builderSupport);
      this.interactionVars = builder.getInteractionVars(builderSupport);
      this.isMemory = builder.isMemory(builderSupport.getExecutionContext());
      this.memoriesNameOverride = builder.getMemoriesNameOverride(builderSupport.getExecutionContext());
      this.isMemoriesNameOverriden = this.memoriesNameOverride != null && !this.memoriesNameOverride.isEmpty();
      this.spawnLockTime = builder.getSpawnLockTime(builderSupport);
      DisplayNameSupport.setRandomDisplayName(builderSupport.getHolder(), builder.getDisplayNames(), false);
      List<Instruction> instructionList = builder.getInstructionList(builderSupport);
      if (instructionList == null) {
         instructionList = new ObjectArrayList<>();
      }

      Instruction[] instructions = instructionList.toArray(Instruction[]::new);
      this.rootInstruction = Instruction.createRootInstruction(instructions, builderSupport);
      this.interactionInstruction = builder.getInteractionInstruction(builderSupport);
      this.deathInstruction = builder.getDeathInstruction(builderSupport);
      builder.registerStateEvaluator(builderSupport);
      if (this.interactionInstruction != null) {
         builderSupport.trackInteractions();
      }
   }

   public int getInitialMaxHealth() {
      return this.initialMaxHealth;
   }

   public boolean isAvoidingEntities() {
      return this.isAvoidingEntities;
   }

   public double getCollisionProbeDistance() {
      return this.collisionProbeDistance;
   }

   public boolean isApplySeparation() {
      return this.applySeparation;
   }

   public double getSeparationDistance() {
      return this.separationDistance;
   }

   public Instruction getRootInstruction() {
      return this.rootInstruction;
   }

   @Nullable
   public Instruction getInteractionInstruction() {
      return this.interactionInstruction;
   }

   @Nullable
   public Instruction getDeathInstruction() {
      return this.deathInstruction;
   }

   @Nonnull
   public Steering getBodySteering() {
      return this.bodySteering;
   }

   @Nonnull
   public Steering getHeadSteering() {
      return this.headSteering;
   }

   @Nonnull
   public Set<Ref<EntityStore>> getIgnoredEntitiesForAvoidance() {
      return this.ignoredEntitiesForAvoidance;
   }

   public String getDropListId() {
      return this.dropListId;
   }

   @Nullable
   public String getBalanceAsset() {
      return this.balanceAsset;
   }

   @Nullable
   public Map<String, String> getInteractionVars() {
      return this.interactionVars;
   }

   public boolean isMemory() {
      return this.isMemory;
   }

   public String getMemoriesNameOverride() {
      return this.memoriesNameOverride;
   }

   public String getNameTranslationKey() {
      return this.nameTranslationKey;
   }

   public boolean isMemoriesNameOverriden() {
      return this.isMemoriesNameOverriden;
   }

   public float getSpawnLockTime() {
      return this.spawnLockTime;
   }

   public void postRoleBuilt(@Nonnull Holder<EntityStore> holder, @Nonnull BuilderSupport builderSupport) {
      this.requiresLeashPosition = builderSupport.requiresLeashPosition();
      holder.getComponent(FlagsComponent.getComponentType()).setFlags(builderSupport.allocateFlags());
      this.indexedInstructions = new IndexedInstructions(builderSupport.getInstructionSlotMappings());
      holder.getComponent(StateSupport.getComponentType()).postRoleBuilt(builderSupport);
      holder.getComponent(WorldSupport.getComponentType()).postRoleBuilt(builderSupport);
      holder.getComponent(EntitySupport.getComponentType()).postRoleBuilt(builderSupport);
      holder.getComponent(MarkedEntitySupport.getComponentType()).postRoleBuilder(builderSupport);
      if (builderSupport.requiresBlockTypeBlackboard()) {
         NPCEntity npcEntity = holder.getComponent(NPCEntity.getComponentType());
         npcEntity.addBlackboardBlockTypeSets(builderSupport.getBlockTypeBlackboardBlockSets());
      }

      this.rootInstruction.setContext(this, 0);
   }

   @Nonnull
   public ExecutionSupport acquireExecutionSupport(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> accessor) {
      ExecutionSupport es = ExecutionSupport.acquire();
      es.populateFromEntity(ref, accessor);
      es.setRoleIndex(this.roleIndex);
      es.setName(this.roleName);
      es.setIndexedInstructions(this.indexedInstructions);
      return es;
   }

   public void loaded(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> accessor) {
      ExecutionSupport executionSupport = this.acquireExecutionSupport(ref, accessor);

      try {
         this.rootInstruction.loaded(executionSupport);
         if (this.interactionInstruction != null) {
            this.interactionInstruction.loaded(executionSupport);
         }

         if (this.deathInstruction != null) {
            this.deathInstruction.loaded(executionSupport);
         }

         StateTransitionController stateTransitions = executionSupport.getStateSupport().getStateTransitionController();
         if (stateTransitions != null) {
            stateTransitions.loaded(executionSupport);
         }
      } finally {
         executionSupport.clearForReuse();
      }
   }

   public void spawned(@Nonnull Holder<EntityStore> holder, @Nonnull NPCEntity npcComponent, @Nonnull Store<EntityStore> store) {
      MotionController activeMotionController = this.activeMotionController;
      if (activeMotionController != null) {
         activeMotionController.spawned();
      }

      holder.getComponent(DisplayNameSupport.getComponentType()).pickRandomDisplayName(holder, true);
      ExecutionSupport executionSupport = ExecutionSupport.acquire();
      executionSupport.populateFromHolder(holder);
      executionSupport.setRoleIndex(this.roleIndex);
      executionSupport.setName(this.roleName);
      executionSupport.setIndexedInstructions(this.indexedInstructions);

      try {
         this.rootInstruction.spawned(executionSupport);
         if (this.interactionInstruction != null) {
            this.interactionInstruction.spawned(executionSupport);
         }

         if (this.deathInstruction != null) {
            this.deathInstruction.spawned(executionSupport);
         }

         StateTransitionController stateTransitions = executionSupport.getStateSupport().getStateTransitionController();
         if (stateTransitions != null) {
            stateTransitions.spawned(executionSupport);
         }
      } finally {
         executionSupport.clearForReuse();
      }

      this.initialiseInventories(npcComponent, holder, store);
   }

   public void unloaded(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> accessor) {
      ExecutionSupport executionSupport = this.acquireExecutionSupport(ref, accessor);

      try {
         executionSupport.getEntitySupport().unloaded();
         executionSupport.getWorldSupport().unloaded(ref);
         executionSupport.getMarkedEntitySupport().unloaded();
         this.rootInstruction.unloaded(executionSupport);
         if (this.interactionInstruction != null) {
            this.interactionInstruction.unloaded(executionSupport);
         }

         if (this.deathInstruction != null) {
            this.deathInstruction.unloaded(executionSupport);
         }

         StateTransitionController stateTransitions = executionSupport.getStateSupport().getStateTransitionController();
         if (stateTransitions != null) {
            stateTransitions.unloaded(executionSupport);
         }
      } finally {
         executionSupport.clearForReuse();
      }
   }

   public void removed(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> accessor) {
      ExecutionSupport executionSupport = this.acquireExecutionSupport(ref, accessor);

      try {
         executionSupport.getWorldSupport().resetAllBlockSensors(ref);
         this.rootInstruction.removed(executionSupport);
         if (this.interactionInstruction != null) {
            this.interactionInstruction.removed(executionSupport);
         }

         if (this.deathInstruction != null) {
            this.deathInstruction.removed(executionSupport);
         }

         StateTransitionController stateTransitions = executionSupport.getStateSupport().getStateTransitionController();
         if (stateTransitions != null) {
            stateTransitions.removed(executionSupport);
         }
      } finally {
         executionSupport.clearForReuse();
      }
   }

   public void teleported(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> accessor, @Nonnull World from, @Nonnull World to) {
      ExecutionSupport executionSupport = this.acquireExecutionSupport(ref, accessor);

      try {
         this.rootInstruction.teleported(executionSupport, from, to);
         if (this.interactionInstruction != null) {
            this.interactionInstruction.teleported(executionSupport, from, to);
         }

         if (this.deathInstruction != null) {
            this.deathInstruction.teleported(executionSupport, from, to);
         }

         StateTransitionController stateTransitions = executionSupport.getStateSupport().getStateTransitionController();
         if (stateTransitions != null) {
            stateTransitions.teleported(executionSupport, from, to);
         }
      } finally {
         executionSupport.clearForReuse();
      }
   }

   public String getAppearanceName() {
      return this.appearance;
   }

   public MotionController getActiveMotionController() {
      return this.activeMotionController;
   }

   public boolean isRoleChangeRequested() {
      return this.roleChangeRequested;
   }

   public void setRoleChangeRequested() {
      this.roleChangeRequested = true;
   }

   public boolean setActiveMotionController(
      @Nonnull Ref<EntityStore> ref, @Nonnull NPCEntity npcComponent, @Nonnull String name, @Nonnull ComponentAccessor<EntityStore> componentAccessor
   ) {
      MotionController motionController = this.motionControllers.get(name);
      if (motionController == null) {
         NPCPlugin.get()
            .getLogger()
            .at(Level.SEVERE)
            .log("Failed to set MotionController for NPC of type '%s': MotionController '%s' not found! ", this.roleName, name);
         return false;
      } else {
         this.setActiveMotionController(ref, npcComponent, motionController, componentAccessor);
         return true;
      }
   }

   public void setActiveMotionController(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull NPCEntity npcComponent,
      @Nonnull MotionController motionController,
      @Nonnull ComponentAccessor<EntityStore> componentAccessor
   ) {
      if (this.activeMotionController != motionController) {
         if (this.activeMotionController != null) {
            this.activeMotionController.deactivate();
         }

         this.activeMotionController = motionController;
         MotionContextSupport mcs = componentAccessor.getComponent(ref, MotionContextSupport.getComponentType());
         assert mcs != null;
         mcs.setActiveMotionController(motionController);
         this.activeMotionController.activate();
         this.motionControllerChanged(ref, npcComponent, this.activeMotionController, componentAccessor);
      }
   }

   protected void motionControllerChanged(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull NPCEntity npcComponent,
      @Nullable MotionController motionController,
      @Nonnull ComponentAccessor<EntityStore> componentAccessor
   ) {
      npcComponent.setActiveMotionControllerName(motionController != null ? motionController.getType() : null);
      this.rootInstruction.motionControllerChanged(ref, npcComponent, motionController, componentAccessor);
      if (this.deathInstruction != null) {
         this.deathInstruction.motionControllerChanged(ref, npcComponent, motionController, componentAccessor);
      }

      if (this.interactionInstruction != null) {
         this.interactionInstruction.motionControllerChanged(ref, npcComponent, motionController, componentAccessor);
      }

      StateTransitionController stateTransitions = StateSupport.get(ref, componentAccessor).getStateTransitionController();
      if (stateTransitions != null) {
         stateTransitions.motionControllerChanged(ref, npcComponent, motionController, componentAccessor);
      }
   }

   public void preInitMotionControllers(
      @Nonnull DebugSupport debugSupport, @Nonnull Map<String, MotionController> motionControllers, @Nullable String initialMotionController
   ) {
      this.motionControllers = motionControllers;
      this.initialMotionControllerName = initialMotionController;

      for (Entry<String, MotionController> entry : this.motionControllers.entrySet()) {
         debugSupport.registerDebugFlagsListener(entry.getValue());
      }

      this.updateMotionControllers(null, null, null, null);
   }

   public void activateInitialMotionController(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> accessor, @Nonnull NPCEntity npcComponent) {
      if (this.activeMotionController == null) {
         if (!this.motionControllers.isEmpty()) {
            if (this.initialMotionControllerName == null || !this.setActiveMotionController(ref, npcComponent, this.initialMotionControllerName, accessor)) {
               this.setActiveMotionController(ref, npcComponent, RandomExtra.randomElement(new ObjectArrayList<>(this.motionControllers.values())), accessor);
            }
         }
      }
   }

   public void updateMotionControllers(
      @Nullable Ref<EntityStore> ref, @Nullable Model model, @Nullable Box boundingBox, @Nullable ComponentAccessor<EntityStore> componentAccessor
   ) {
      for (MotionController motionController : this.motionControllers.values()) {
         motionController.setRole(this);
         motionController.setInertia(this.inertia);
         motionController.setKnockbackScale(this.knockbackScale);
         motionController.setHeadPitchAngleRange(this.headPitchAngleRange);
         if (boundingBox != null && model != null) {
            motionController.updateModelParameters(ref, model, boundingBox, componentAccessor);
            motionController.updatePhysicsValues(model.getPhysicsValues());
         }
      }
   }

   public void updateMovementState(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull MovementStates movementStates,
      @Nonnull Vector3d velocity,
      @Nonnull ComponentAccessor<EntityStore> componentAccessor
   ) {
      if (this.activeMotionController != null) {
         this.activeMotionController.updateMovementState(ref, movementStates, this.bodySteering, velocity, componentAccessor);
      }
   }

   public void tick(@Nonnull Ref<EntityStore> ref, float tickTime, @Nonnull Store<EntityStore> store) {
      ExecutionSupport executionSupport = this.acquireExecutionSupport(ref, store);

      try {
         executionSupport.getEntitySupport().tickDeferredActions(ref, executionSupport, tickTime, store);
         this.computeActionsAndSteering(ref, executionSupport, tickTime, this.bodySteering, this.headSteering, store);
      } finally {
         executionSupport.clearForReuse();
      }
   }

   protected void computeActionsAndSteering(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull ExecutionSupport executionSupport,
      double tickTime,
      @Nonnull Steering bodySteering,
      @Nonnull Steering headSteering,
      @Nonnull Store<EntityStore> store
   ) {
      DebugSupport debugSupport = executionSupport.getDebugSupport();
      if (debugSupport.isVisSensorRanges()) {
         debugSupport.beginSensorVisualization();
      }

      boolean isDead = store.getArchetype(ref).contains(DeathComponent.getComponentType());
      if (isDead) {
         if (this.deathInstruction != null && this.deathInstruction.matches(ref, executionSupport, tickTime, store)) {
            this.deathInstruction.execute(ref, executionSupport, tickTime, store);
         }
      } else {
         StateSupport stateSupport = executionSupport.getStateSupport();
         if (this.interactionInstruction != null) {
            executionSupport.getPositionCache().forEachPlayer((d, _playerRef, _es, _selfRef, _store) -> {
               _es.getStateSupport().setInteractionIterationTarget(_playerRef);
               assert this.interactionInstruction != null;
               if (this.interactionInstruction.matches(_selfRef, _es, d, _store)) {
                  this.interactionInstruction.execute(_selfRef, _es, d, _store);
               }
            }, executionSupport, ref, store, tickTime, store);
            stateSupport.setInteractionIterationTarget(null);
            executionSupport.getPlayerTaskSupport().clearTargetPlayerActiveTasks();
         }

         this.activeMotionController.beforeInstructionSensorsAndActions(tickTime);
         MotionContextSupport motionContextSupport = executionSupport.getMotionContextSupport();
         if (!stateSupport.runTransitionActions(ref, executionSupport, tickTime, store)) {
            motionContextSupport.clearNextBodyMotionStep();
            motionContextSupport.clearNextHeadMotionStep();
            this.rootInstruction.execute(ref, executionSupport, tickTime, store);
         } else {
            if (stateSupport.isClearHeadMotion()) {
               motionContextSupport.clearNextHeadMotionStep();
            }

            if (stateSupport.isClearBodyMotion()) {
               motionContextSupport.clearNextBodyMotionStep();
            }
         }

         NPCEntity npcComponent = store.getComponent(ref, NPCEntity.getComponentType());
         assert npcComponent != null;
         if (!npcComponent.isPlayingDespawnAnim()) {
            this.activeMotionController.beforeInstructionMotion(tickTime);
            Instruction nextBodyMotionStep = motionContextSupport.getNextBodyMotionStep();
            if (nextBodyMotionStep != this.lastBodyMotionStep) {
               if (this.lastBodyMotionStep != null) {
                  this.lastBodyMotionStep.getBodyMotion().deactivate(ref, executionSupport, store);
                  this.lastBodyMotionStep.onEndMotion();
               }

               if (nextBodyMotionStep != null) {
                  nextBodyMotionStep.getBodyMotion().activate(ref, executionSupport, store);
               }
            }

            this.lastBodyMotionStep = nextBodyMotionStep;
            Instruction nextHeadMotionStep = motionContextSupport.getNextHeadMotionStep();
            if (nextHeadMotionStep != this.lastHeadMotionStep) {
               if (this.lastHeadMotionStep != null) {
                  this.lastHeadMotionStep.getHeadMotion().deactivate(ref, executionSupport, store);
                  this.lastHeadMotionStep.onEndMotion();
               }

               if (nextHeadMotionStep != null) {
                  nextHeadMotionStep.getHeadMotion().activate(ref, executionSupport, store);
               }
            }

            this.lastHeadMotionStep = nextHeadMotionStep;
            if (nextBodyMotionStep != null) {
               nextBodyMotionStep.getBodyMotion()
                  .computeSteering(ref, executionSupport, nextBodyMotionStep.getSensor().getSensorInfo(), tickTime, bodySteering, store);
            }

            if (nextHeadMotionStep != null) {
               nextHeadMotionStep.getHeadMotion()
                  .computeSteering(ref, executionSupport, nextHeadMotionStep.getSensor().getSensorInfo(), tickTime, headSteering, store);
            }
         }
      }
   }

   public void blendSeparation(
      @Nonnull Ref<EntityStore> selfRef,
      @Nonnull Vector3d position,
      @Nonnull Rotation3f rotation,
      @Nonnull Steering steering,
      @Nonnull ComponentType<EntityStore, TransformComponent> transformComponentType,
      @Nonnull CommandBuffer<EntityStore> commandBuffer
   ) {
      this.lastSeparationSteering.zero();
      Ref<EntityStore> targetRef = MarkedEntitySupport.get(selfRef, commandBuffer).getTargetReferenceToIgnoreForAvoidance();
      Ref<EntityStore> ignoredTargetRef = targetRef != null && targetRef.isValid() ? targetRef : null;
      this.separationSummedDistances.zero();
      this.separationSummedCount = 0;
      switch (this.separationMode) {
         case Legacy:
            this.computeSummedDistanceLegacy(selfRef, position, transformComponentType, commandBuffer, ignoredTargetRef, this.separationMode);
            break;
         case Push:
            this.computeSummedDistancePush(selfRef, position, transformComponentType, commandBuffer, ignoredTargetRef);
      }

      if (DebugSupport.get(selfRef, commandBuffer).isDebugFlagSet(RoleDebugFlags.VisSeparationSummed)) {
         World world = commandBuffer.getExternalData().getWorld();
         Vector3d direction = new Vector3d(this.separationSummedDistances);
         VisHelper.renderDebugVector(position, direction, DebugUtils.COLOR_BLACK, world);
      }

      if (this.separationSummedCount != 0) {
         if (!(this.separationSummedDistances.lengthSquared() < 0.010000000000000002)) {
            switch (this.separationMode) {
               case Legacy:
                  this.scaleSummedDistanceLegacy(rotation, steering);
                  break;
               case Push:
                  this.scaleSummedDistancesPush(position, rotation, steering, commandBuffer);
            }

            if (this.useOrientationHint) {
               steering.setDirectionHint(rotation);
            }
         }
      }
   }

   private void computeSummedDistanceLegacy(
      @Nonnull Ref<EntityStore> selfRef,
      @Nonnull Vector3d position,
      @Nonnull ComponentType<EntityStore, TransformComponent> transformComponentType,
      @Nonnull CommandBuffer<EntityStore> commandBuffer,
      @Nullable Ref<EntityStore> ignoredTargetRef,
      Role.SeparationMode separationMode
   ) {
      double maxRange = this.separationDistance;
      if (ignoredTargetRef != null && ignoredTargetRef.isValid()) {
         TransformComponent targetTransformComponent = commandBuffer.getComponent(ignoredTargetRef, transformComponentType);
         assert targetTransformComponent != null;
         double distance = targetTransformComponent.getPosition().distanceSquared(position);
         if (distance <= this.separationNearRadiusTarget * this.separationNearRadiusTarget) {
            maxRange = this.separationDistanceTarget;
         } else if (distance < this.separationFarRadiusTarget * this.separationFarRadiusTarget) {
            double s = (Math.sqrt(distance) - this.separationNearRadiusTarget) / (this.separationFarRadiusTarget - this.separationNearRadiusTarget);
            maxRange = NPCPhysicsMath.lerp(this.separationDistanceTarget, this.separationDistance, s);
         }
      }

      this.groupSteeringAccumulator.setComponentSelector(this.activeMotionController.getComponentSelector());
      this.groupSteeringAccumulator.setMaxRange(maxRange);
      this.groupSteeringAccumulator.setViewConeHalfAngleCosine(this.collisionViewHalfAngleCosine);
      this.groupSteeringAccumulator.setNormalizeDistances(this.normalizeDistances);
      this.groupSteeringAccumulator.begin(selfRef, commandBuffer);
      PositionCache.get(selfRef, commandBuffer)
         .forEachEntityInAvoidanceRange(
            this.ignoredEntitiesForAvoidance,
            (ref, _groupSteeringAccumulator, _role, _buffer) -> _groupSteeringAccumulator.processEntity(ref, this.separationWeight, 1.0, 1.0, _buffer),
            this.groupSteeringAccumulator,
            this,
            commandBuffer
         );
      this.groupSteeringAccumulator.end();
      this.separationSummedDistances.set(this.groupSteeringAccumulator.getSumOfDistances());
      this.separationSummedCount = this.groupSteeringAccumulator.getCount();
   }

   private void scaleSummedDistanceLegacy(@Nonnull Rotation3f rotation, @Nonnull Steering steering) {
      double speed = steering.getSpeed();
      this.separationTempDistanceVector.set(this.separationSummedDistances).normalize(-this.separationLegacySteeringStrength);
      if (speed > 0.0) {
         this.separationTempDistanceVector.add(steering.getTranslation());
         this.separationTempDistanceVector.normalize(speed);
      } else if (this.alwaysApplySeparation) {
         this.separationTempDistanceVector.add(steering.getTranslation());
      }

      this.lastSeparationSteering.set(this.separationTempDistanceVector).sub(steering.getTranslation());
      steering.setTranslation(this.separationTempDistanceVector);
   }

   private void computeSummedDistancePush(
      @Nonnull Ref<EntityStore> selfRef,
      @Nonnull Vector3d position,
      @Nonnull ComponentType<EntityStore, TransformComponent> transformComponentType,
      @Nonnull CommandBuffer<EntityStore> commandBuffer,
      @Nullable Ref<EntityStore> ignoredTargetRef
   ) {
      double x = position.x();
      double y = position.y();
      double z = position.z();
      BodyMotion bodyMotion = this.getLastBodySteeringMotion();
      Ref<EntityStore> desiredTargetEntity = bodyMotion != null ? bodyMotion.getDesiredTargetEntity() : null;
      Ref<EntityStore> motionTarget = desiredTargetEntity != null && desiredTargetEntity.isValid() ? desiredTargetEntity : null;
      double targetDistance = motionTarget != null ? bodyMotion.getDesiredTargetDistance() : Double.MAX_VALUE;
      double safeTargetDistance = targetDistance * this.separationSafeDistanceMultiplier;
      boolean needSwitchDistance = safeTargetDistance < this.separationDistance;
      double separationDistanceSquared = this.separationDistance * this.separationDistance;
      Vector3d motionTargetPosition;
      if (motionTarget != null) {
         TransformComponent transformComponent = commandBuffer.getComponent(motionTarget, transformComponentType);
         assert transformComponent != null;
         motionTargetPosition = transformComponent.getPosition();
      } else {
         motionTargetPosition = null;
      }

      PositionCache.get(selfRef, commandBuffer)
         .forEachEntityInAvoidanceRange(
            this.ignoredEntitiesForAvoidance,
            (ref, componentSelector, _role, componentAccessor) -> {
               if (selfRef != ref) {
                  if (ignoredTargetRef != ref) {
                     TransformComponent transformComponentx = componentAccessor.getComponent(ref, transformComponentType);
                     assert transformComponentx != null;
                     Vector3d otherPosition = transformComponentx.getPosition();
                     double maxRange = this.separationDistance;
                     double distanceWeight = this.separationPushDistanceWeightDefault;
                     if (needSwitchDistance) {
                        if (ref == motionTarget) {
                           double distanceSquared = NPCPhysicsMath.distanceSquaredWithSelector(motionTargetPosition, position, componentSelector);
                           if (distanceSquared <= separationDistanceSquared) {
                              maxRange = Math.max(Math.sqrt(distanceSquared) * this.separationSafeDistanceMultiplier, safeTargetDistance);
                              distanceWeight = this.separationPushDistanceWeightTarget;
                           }
                        } else if (motionTargetPosition != null
                           && NPCPhysicsMath.distanceSquaredWithSelector(otherPosition, motionTargetPosition, componentSelector) <= separationDistanceSquared) {
                           maxRange = safeTargetDistance;
                           distanceWeight = this.separationPushDistanceWeightAttacker;
                        }
                     }

                     double dx = (otherPosition.x() - x) * componentSelector.x;
                     double dy = (otherPosition.y() - y) * componentSelector.y;
                     double dz = (otherPosition.z() - z) * componentSelector.z;
                     double d = NPCPhysicsMath.dotProduct(dx, dy, dz);
                     if (!(d > maxRange * maxRange)) {
                        double distance;
                        if (d < 1.0E-6) {
                           while (true) {
                              dx = RandomExtra.randomRange(-1.0, 1.0) * componentSelector.x;
                              dy = RandomExtra.randomRange(-1.0, 1.0) * componentSelector.y;
                              dz = RandomExtra.randomRange(-1.0, 1.0) * componentSelector.z;
                              d = NPCPhysicsMath.dotProduct(dx, dy, dz);
                              if (!(d < 1.0E-6)) {
                                 double norm = 0.001 / Math.sqrt(d);
                                 dx *= norm;
                                 dy *= norm;
                                 dz *= norm;
                                 distance = 0.001;
                                 break;
                              }
                           }
                        } else {
                           distance = Math.sqrt(d);
                        }

                        d = distance / maxRange;
                        d = 1.0 - Math.pow(d, distanceWeight);
                        d /= distance;
                        dx *= d;
                        dy *= d;
                        dz *= d;
                        if (DebugSupport.get(selfRef, commandBuffer).isDebugFlagSet(RoleDebugFlags.VisSeparationTargets)) {
                           World world = commandBuffer.getExternalData().getWorld();
                           VisHelper.renderDebugSphere(
                              otherPosition, maxRange, maxRange == this.separationDistance ? DebugUtils.COLOR_WHITE : DebugUtils.COLOR_CYAN, world
                           );
                           Vector3d direction = new Vector3d(dx, dy, dz);
                           VisHelper.renderDebugVector(position, direction, DebugUtils.COLOR_YELLOW, world);
                        }

                        this.separationSummedDistances.add(dx, dy, dz);
                        this.separationSummedCount++;
                     }
                  }
               }
            },
            this.activeMotionController.getComponentSelector(),
            this,
            commandBuffer
         );
   }

   private void scaleSummedDistancesPush(
      @Nonnull Vector3d position, @Nonnull Rotation3f rotation, @Nonnull Steering steering, @Nonnull CommandBuffer<EntityStore> commandBuffer
   ) {
      this.separationTempDistanceVector.set(this.separationSummedDistances).mul(this.activeMotionController.getComponentSelector());
      double separationSquaredLength = this.separationTempDistanceVector.lengthSquared();
      if (separationSquaredLength > 1.0) {
         this.separationTempDistanceVector.normalize();
      }

      this.separationTempDistanceVector.mul(-this.separationPushSteeringStrength);
      this.separationTempSteeringVector.set(steering.getTranslation()).mul(this.activeMotionController.getComponentSelector());
      double speedSquared = this.separationTempSteeringVector.lengthSquared();
      if (speedSquared < 1.0000000000000002E-10) {
         if (!this.alwaysApplySeparation) {
            return;
         }

         steering.setTranslation(this.separationTempDistanceVector);
         this.lastSeparationSteering.set(this.separationTempDistanceVector);
      } else {
         double speed = Math.pow(speedSquared, this.separationPushSpeedScale * 0.5);
         this.separationTempDistanceVector.add(this.separationTempSteeringVector).normalize(speed).sub(this.separationTempSteeringVector);
         this.separationTempSteeringVector.set(steering.getTranslation()).add(this.separationTempDistanceVector);
         if (this.separationTempSteeringVector.lengthSquared() > 1.0) {
            this.separationTempSteeringVector.normalize();
         }

         steering.setTranslation(this.separationTempSteeringVector);
         this.lastSeparationSteering.set(this.separationTempDistanceVector);
      }
   }

   @Nonnull
   public Vector3d getLastSeparationSteering() {
      return this.lastSeparationSteering;
   }

   public void blendAvoidance(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull Vector3d position,
      @Nonnull Rotation3f rotation,
      @Nonnull Steering steering,
      @Nonnull CommandBuffer<EntityStore> commandBuffer
   ) {
      this.steeringForceAvoidCollision.setDebug(DebugSupport.get(ref, commandBuffer).isDebugRoleSteering());
      this.steeringForceAvoidCollision.setAvoidanceMode(this.avoidanceMode);
      this.steeringForceAvoidCollision.setSelf(ref, position, commandBuffer);
      if (!this.activeMotionController.estimateVelocity(steering, this.steeringForceAvoidCollision.getSelfVelocity())) {
         this.steeringForceAvoidCollision.setVelocityFromEntity(ref, commandBuffer);
      }

      if (this.collisionRadius >= 0.0) {
         this.steeringForceAvoidCollision.setSelfRadius(this.collisionRadius);
      }

      this.steeringForceAvoidCollision.setMaxDistance(this.collisionProbeDistance);
      this.steeringForceAvoidCollision.setFalloff(this.collisionForceFalloff);
      this.steeringForceAvoidCollision.setComponentSelector(this.activeMotionController.getComponentSelector());
      this.steeringForceAvoidCollision.reset();
      PositionCache.get(ref, commandBuffer)
         .forEachEntityInAvoidanceRange(
            this.ignoredEntitiesForAvoidance,
            (_ref, _steeringForceAvoidCollision, _buffer) -> _steeringForceAvoidCollision.add(_ref, _buffer),
            this.steeringForceAvoidCollision,
            commandBuffer
         );
      this.steeringForceAvoidCollision.compute(steering);
   }

   @Nonnull
   public Vector3d getLastAvoidanceSteering() {
      return this.steeringForceAvoidCollision.getLastSteeringDirection();
   }

   public String getRoleName() {
      return this.roleName;
   }

   public int getRoleIndex() {
      return this.roleIndex;
   }

   public void setRoleIndex(@Nonnull Holder<EntityStore> holder, int roleIndex, @Nonnull String roleName) {
      this.roleIndex = roleIndex;
      this.roleName = roleName;
      holder.getComponent(PositionCache.getComponentType()).setRoleIndex(roleIndex);
      holder.getComponent(CombatSupport.getComponentType()).setRoleIndex(roleIndex);
   }

   public boolean isInvulnerable() {
      return this.invulnerable;
   }

   public boolean isBreathesInAir() {
      return this.breathesInAir;
   }

   public boolean isBreathesInWater() {
      return this.breathesInWater;
   }

   public double getInertia() {
      return this.inertia;
   }

   public double getKnockbackScale() {
      return this.knockbackScale;
   }

   public boolean canBreathe(@Nonnull BlockMaterial breathingMaterial, int fluidId) {
      return this.invulnerable ? true : this.couldBreathe(breathingMaterial, fluidId);
   }

   public boolean couldBreathe(@Nonnull BlockMaterial breathingMaterial, int fluidId) {
      if (fluidId != 0) {
         return this.breathesInWater;
      } else {
         return breathingMaterial == BlockMaterial.Empty ? this.breathesInAir : false;
      }
   }

   public boolean couldBreatheCached(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> accessor) {
      return PositionCache.get(ref, accessor).couldBreatheCached();
   }

   public void addVelocity(@Nonnull Vector3d velocity, @Nullable VelocityConfig velocityConfig) {
      if (this.activeMotionController != null) {
         this.activeMotionController.addVelocity(velocity, velocityConfig);
      }
   }

   public void setVelocity(@Nonnull Vector3d velocity, @Nullable VelocityConfig velocityConfig, boolean ignoreDamping) {
      if (this.activeMotionController != null) {
         this.activeMotionController.setVelocity(velocity, velocityConfig, ignoreDamping);
      }
   }

   public void processAddVelocityInstruction(@Nonnull Vector3d velocity, @Nullable VelocityConfig velocityConfig) {
      if (this.activeMotionController != null) {
         this.activeMotionController.addVelocity(velocity, velocityConfig);
      }
   }

   public void processSetVelocityInstruction(@Nonnull Vector3d velocity, @Nullable VelocityConfig velocityConfig) {
      if (this.activeMotionController != null) {
         this.activeMotionController.setVelocity(Vector3dUtil.ZERO, null, false);
         this.activeMotionController.addVelocity(velocity, velocityConfig);
      }
   }

   public boolean isOnGround() {
      return this.activeMotionController != null && this.activeMotionController.onGround();
   }

   public void setArmor(@Nonnull NPCEntity npcComponent, @Nullable String[] armor) {
      this.armor = armor;
   }

   public boolean isPickupDropOnDeath() {
      return this.pickupDropOnDeath;
   }

   public boolean requiresLeashPosition() {
      return this.requiresLeashPosition;
   }

   public void clearOnce(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> accessor) {
      this.rootInstruction.clearOnce();
      if (this.interactionInstruction != null) {
         this.interactionInstruction.clearOnce();
      }

      if (this.deathInstruction != null) {
         this.deathInstruction.clearOnce();
      }

      StateSupport.get(ref, accessor).pollNeedClearOnce();
   }

   public void clearOnceIfNeeded(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> accessor) {
      StateSupport stateSupport = StateSupport.get(ref, accessor);
      if (stateSupport.pollNeedClearOnce()) {
         this.clearOnce(ref, accessor);
         stateSupport.resetLocalStateMachines();
      }
   }

   public void setMarkedTarget(
      @Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> accessor, @Nonnull String targetSlot, @Nonnull Ref<EntityStore> target
   ) {
      MarkedEntitySupport.get(ref, accessor).setMarkedEntity(targetSlot, target);
   }

   public boolean isIgnoredForAvoidance(@Nonnull Ref<EntityStore> entityReference) {
      return this.ignoredEntitiesForAvoidance.contains(entityReference);
   }

   public Role.AvoidanceMode getAvoidanceMode() {
      return this.avoidanceMode;
   }

   public double getCollisionRadius() {
      return this.collisionRadius;
   }

   public int[] getFlockSpawnTypes() {
      if (this.flockSpawnTypeIndices != null) {
         return this.flockSpawnTypeIndices;
      }

      int length = this.flockSpawnTypes == null ? 0 : this.flockSpawnTypes.length;
      this.flockSpawnTypeIndices = new int[length];

      for (int i = 0; i < length; i++) {
         String flockSpawnType = this.flockSpawnTypes[i];
         int index = NPCPlugin.get().getIndex(flockSpawnType);
         if (index == Integer.MIN_VALUE) {
            throw new IllegalStateException(String.format("Role %s contains unknown FlockSpawnTypes NPC %s", this.roleName, flockSpawnType));
         }

         this.flockSpawnTypeIndices[i] = index;
      }

      return this.flockSpawnTypeIndices;
   }

   @Nonnull
   public String[] getFlockAllowedRoles() {
      return this.flockAllowedRoles != null ? Arrays.copyOf(this.flockAllowedRoles, this.flockAllowedRoles.length) : ArrayUtil.EMPTY_STRING_ARRAY;
   }

   public boolean isFlockSpawnTypesRandom() {
      return this.flockSpawnTypesRandom;
   }

   public boolean isCanLeadFlock() {
      return this.canLeadFlock;
   }

   public double getFlockInfluenceRange() {
      return this.flockInfluenceRange;
   }

   public double getDeathAnimationTime() {
      return this.deathAnimationTime;
   }

   @Nullable
   public String getDeathParticles() {
      return this.deathParticles;
   }

   public boolean isDropDeathItemsInstantly() {
      return this.dropDeathItemsInstantly;
   }

   public boolean hasDroppedDeathItems() {
      return this.deathItemsDropped;
   }

   public void setDeathItemsDropped() {
      this.deathItemsDropped = true;
   }

   @Nullable
   public String getDeathInteraction() {
      return this.deathInteraction;
   }

   public float getDespawnAnimationTime() {
      return this.despawnAnimationTime;
   }

   public void setReachedTerminalAction(boolean hasReached) {
      this.hasReachedTerminalAction = hasReached;
   }

   public boolean hasReachedTerminalAction() {
      return this.hasReachedTerminalAction;
   }

   public boolean isBackingAway() {
      return this.backingAway;
   }

   public void setBackingAway(boolean backingAway) {
      this.backingAway = backingAway;
   }

   @Nullable
   public String getSteeringMotionName() {
      BodyMotion motion = this.getLastBodySteeringMotion();
      return motion == null ? null : motion.getClass().getSimpleName();
   }

   @Nullable
   public BodyMotion getLastBodySteeringMotion() {
      if (this.lastBodyMotionStep == null) {
         return null;
      }

      BodyMotion motion = this.lastBodyMotionStep.getBodyMotion();
      return motion == null ? null : motion.getSteeringMotion();
   }

   @Nullable
   public EnumSet<RelaxedConstraint> getSteeringRelaxedConstraints() {
      BodyMotion motion = this.getLastBodySteeringMotion();
      return motion == null ? null : motion.getRelaxedConstraints();
   }

   @Override
   public int componentCount() {
      return 1;
   }

   @Override
   public IAnnotatedComponent getComponent(int index) {
      return this.rootInstruction;
   }

   @Override
   public void getInfo(ExecutionSupport executionSupport, ComponentInfo holder) {
   }

   @Override
   public int getIndex() {
      throw new UnsupportedOperationException("Roles do not have component indexes!");
   }

   @Override
   public void setContext(IAnnotatedComponent parent, int index) {
      throw new UnsupportedOperationException("Roles do not have parent contexts!");
   }

   @Nullable
   @Override
   public IAnnotatedComponent getParent() {
      return null;
   }

   @Override
   public String getLabel() {
      return this.roleName;
   }

   private void initialiseInventories(@Nonnull NPCEntity npcComponent, @Nonnull Holder<EntityStore> holder, @Nonnull Store<EntityStore> store) {
      List<ItemStack> inventoryItems = null;
      if (this.inventoryContentsDropList != null) {
         ItemModule itemModule = ItemModule.get();
         if (itemModule.isEnabled()) {
            inventoryItems = itemModule.getRandomItemDrops(this.inventoryContentsDropList);
         }
      }

      int inventorySlots = inventoryItems != null && inventoryItems.size() > this.inventorySlots ? inventoryItems.size() : this.inventorySlots;
      if (inventorySlots > 0 || this.hotbarSlots > 3 || this.offHandSlots > 0) {
         ObjectArrayList<ItemStack> remainder = new ObjectArrayList<>();
         InventoryComponent.Hotbar hotbarComponent = holder.getComponent(InventoryComponent.Hotbar.getComponentType());
         if (hotbarComponent != null) {
            hotbarComponent.ensureCapacity((short)this.hotbarSlots, remainder);
         }

         InventoryComponent.Utility utilityComponent = holder.getComponent(InventoryComponent.Utility.getComponentType());
         if (utilityComponent != null) {
            utilityComponent.ensureCapacity((short)this.offHandSlots, remainder);
         }

         InventoryComponent.Storage storageComponent = holder.getComponent(InventoryComponent.Storage.getComponentType());
         if (storageComponent != null) {
            storageComponent.ensureCapacity((short)inventorySlots, remainder);
         }
      }

      if (inventoryItems != null) {
         InventoryComponent.Storage storageComponent = holder.getComponent(InventoryComponent.Storage.getComponentType());
         if (storageComponent != null) {
            for (ItemStack item : inventoryItems) {
               storageComponent.getInventory().addItemStack(item);
            }
         }
      }

      this.initialiseItemsAndArmor(holder, npcComponent, store);
      if (this.defaultOffHandSlot >= 0) {
         InventoryComponent.Utility utilityComponent = holder.getComponent(InventoryComponent.Utility.getComponentType());
         if (utilityComponent != null
            && this.defaultOffHandSlot != utilityComponent.getActiveSlot()
            && this.defaultOffHandSlot < utilityComponent.getInventory().getCapacity()) {
            utilityComponent.setActiveSlot(this.defaultOffHandSlot, holder, store);
         }
      }
   }

   private void initialiseInventories(@Nonnull NPCEntity npcComponent, @Nonnull ComponentAccessor<EntityStore> accessor, @Nonnull Ref<EntityStore> ref) {
      List<ItemStack> inventoryItems = null;
      if (this.inventoryContentsDropList != null) {
         ItemModule itemModule = ItemModule.get();
         if (itemModule.isEnabled()) {
            inventoryItems = itemModule.getRandomItemDrops(this.inventoryContentsDropList);
         }
      }

      int inventorySlots = inventoryItems != null && inventoryItems.size() > this.inventorySlots ? inventoryItems.size() : this.inventorySlots;
      if (inventorySlots > 0 || this.hotbarSlots > 3 || this.offHandSlots > 0) {
         ObjectArrayList<ItemStack> remainder = new ObjectArrayList<>();
         InventoryComponent.Hotbar hotbarComponent = accessor.getComponent(ref, InventoryComponent.Hotbar.getComponentType());
         if (hotbarComponent != null) {
            hotbarComponent.ensureCapacity((short)this.hotbarSlots, remainder);
         }

         InventoryComponent.Utility utilityComponent = accessor.getComponent(ref, InventoryComponent.Utility.getComponentType());
         if (utilityComponent != null) {
            utilityComponent.ensureCapacity((short)this.offHandSlots, remainder);
         }

         InventoryComponent.Storage storageComponent = accessor.getComponent(ref, InventoryComponent.Storage.getComponentType());
         if (storageComponent != null) {
            storageComponent.ensureCapacity((short)inventorySlots, remainder);
         }
      }

      if (inventoryItems != null) {
         InventoryComponent.Storage storageComponent = accessor.getComponent(ref, InventoryComponent.Storage.getComponentType());
         if (storageComponent != null) {
            for (ItemStack item : inventoryItems) {
               storageComponent.getInventory().addItemStack(item);
            }
         }
      }

      this.initialiseInventories(ref, npcComponent, accessor);
   }

   private void initialiseItemsAndArmor(@Nonnull Holder<EntityStore> holder, @Nonnull NPCEntity npcComponent, @Nonnull Store<EntityStore> store) {
      if (this.hotbarItems != null && this.hotbarItems.length > 0) {
         InventoryComponent.Hotbar hotbarComponent = holder.getComponent(InventoryComponent.Hotbar.getComponentType());
         if (hotbarComponent != null && hotbarComponent.getInventory().isEmpty()) {
            ItemContainer hotbarContainer = hotbarComponent.getInventory();

            for (byte i = 0; i < this.hotbarItems.length; i++) {
               String hotbarItem = this.hotbarItems[i];
               if (hotbarItem != null) {
                  if (hotbarItem.startsWith("Droplist:")) {
                     if (i >= hotbarContainer.getCapacity()) {
                        NPCPlugin.get().getLogger().at(Level.WARNING).log("Invalid hotbar slot %s. Max is %s", i, (int)(hotbarContainer.getCapacity() - 1));
                     } else {
                        List<ItemStack> items = ItemModule.get().getRandomItemDrops(hotbarItem.substring("Droplist:".length()));
                        hotbarContainer.setItemStackForSlot(i, items.get(RandomExtra.randomRange(items.size())));
                     }
                  } else if (InventoryHelper.itemKeyExists(hotbarItem)
                     && i < hotbarContainer.getCapacity()
                     && !InventoryHelper.matchesItem(hotbarItem, hotbarContainer.getItemStack(i))) {
                     ItemStack itemStack = InventoryHelper.createItem(hotbarItem);
                     hotbarContainer.setItemStackForSlot(i, itemStack);
                  }
               }
            }
         }
      }

      if (this.offHandItems != null && this.offHandItems.length > 0) {
         InventoryComponent.Utility utilityComponent = holder.getComponent(InventoryComponent.Utility.getComponentType());
         if (utilityComponent != null) {
            ItemContainer utilityContainer = utilityComponent.getInventory();

            for (byte i = 0; i < this.offHandItems.length; i++) {
               String offHandItem = this.offHandItems[i];
               if (InventoryHelper.itemKeyExists(offHandItem)
                  && i < utilityContainer.getCapacity()
                  && !InventoryHelper.matchesItem(offHandItem, utilityContainer.getItemStack(i))) {
                  utilityContainer.setItemStackForSlot(i, InventoryHelper.createItem(offHandItem));
               }
            }
         }
      }

      if (this.armor != null) {
         InventoryComponent.Armor armorComponent = holder.getComponent(InventoryComponent.Armor.getComponentType());
         if (armorComponent != null) {
            for (String s : this.armor) {
               if (!InventoryHelper.useArmor(armorComponent.getInventory(), s)) {
                  NPCPlugin.get().getLogger().at(Level.WARNING).log("NPC of type '%s': Failed to use armor '%s'", npcComponent.getRoleName(), s);
               }
            }
         }
      }
   }

   private void initialiseItemsAndArmor(
      @Nonnull Ref<EntityStore> ref, @Nonnull NPCEntity npcComponent, @Nonnull ComponentAccessor<EntityStore> componentAccessor
   ) {
      if (this.hotbarItems != null && this.hotbarItems.length > 0) {
         InventoryComponent.Hotbar hotbarComponent = componentAccessor.getComponent(ref, InventoryComponent.Hotbar.getComponentType());
         if (hotbarComponent != null && hotbarComponent.getInventory().isEmpty()) {
            for (byte i = 0; i < this.hotbarItems.length; i++) {
               String hotbarItem = this.hotbarItems[i];
               if (hotbarItem != null) {
                  if (hotbarItem.startsWith("Droplist:")) {
                     if (InventoryHelper.checkHotbarSlot(ref, i, componentAccessor)) {
                        List<ItemStack> items = ItemModule.get().getRandomItemDrops(hotbarItem.substring("Droplist:".length()));
                        hotbarComponent.getInventory().setItemStackForSlot(i, items.get(RandomExtra.randomRange(items.size())));
                     }
                  } else {
                     InventoryHelper.setHotbarItem(ref, hotbarItem, i, componentAccessor);
                  }
               }
            }
         }
      }

      if (this.offHandItems != null && this.offHandItems.length > 0) {
         for (byte i = 0; i < this.offHandItems.length; i++) {
            InventoryHelper.setOffHandItem(ref, this.offHandItems[i], i, componentAccessor);
         }
      }

      if (this.armor != null) {
         InventoryComponent.Armor armorComponent = componentAccessor.getComponent(ref, InventoryComponent.Armor.getComponentType());
         if (armorComponent != null) {
            for (String s : this.armor) {
               if (!InventoryHelper.useArmor(armorComponent.getInventory(), s)) {
                  NPCPlugin.get().getLogger().at(Level.WARNING).log("NPC of type '%s': Failed to use armor '%s'", npcComponent.getRoleName(), s);
               }
            }
         }
      }
   }

   private void initialiseInventories(@Nonnull Ref<EntityStore> ref, @Nonnull NPCEntity npcComponent, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
      this.initialiseItemsAndArmor(ref, npcComponent, componentAccessor);
      if (this.defaultOffHandSlot >= 0) {
         InventoryHelper.setOffHandSlot(ref, this.defaultOffHandSlot, componentAccessor);
      }
   }

   public boolean isCorpseStaysInFlock() {
      return this.corpseStaysInFlock;
   }

   public void onLoadFromWorldGenOrPrefab(
      @Nonnull Ref<EntityStore> ref, @Nonnull NPCEntity npcComponent, @Nonnull ComponentAccessor<EntityStore> componentAccessor
   ) {
      componentAccessor.getComponent(ref, DisplayNameSupport.getComponentType()).pickRandomDisplayName(ref, true, componentAccessor);
      this.initialiseInventories(npcComponent, componentAccessor, ref);
   }

   public enum AvoidanceMode implements Supplier<String> {
      Slowdown("Only slow down NPC"),
      Evade("Only evade"),
      Any("Any avoidance allowed");

      @Nonnull
      private final String description;

      AvoidanceMode(@Nonnull final String description) {
         this.description = description;
      }

      @Nonnull
      public String get() {
         return this.description;
      }
   }

   public enum SeparationMode implements Supplier<String> {
      Legacy("Flock like separation force"),
      Push("Push away from all neighbours and also applied when no other steering happens");

      private final String description;

      SeparationMode(String description) {
         this.description = description;
      }

      public String get() {
         return this.description;
      }
   }
}
