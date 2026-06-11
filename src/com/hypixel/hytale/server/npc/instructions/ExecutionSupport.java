package com.hypixel.hytale.server.npc.instructions;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.Role;
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
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ExecutionSupport {
   private static final ThreadLocal<List<ExecutionSupport>> POOL = ThreadLocal.withInitial(ArrayList::new);
   @Nullable
   private StateSupport stateSupport;
   @Nullable
   private MarkedEntitySupport markedEntitySupport;
   @Nullable
   private WorldSupport worldSupport;
   @Nullable
   private EntitySupport entitySupport;
   @Nullable
   private PositionCache positionCache;
   @Nullable
   private DebugSupport debugSupport;
   @Nullable
   private FlagsComponent flagsComponent;
   @Nullable
   private CombatSupport combatSupport;
   @Nullable
   private MotionContextSupport motionContextSupport;
   @Nullable
   private DisplayNameSupport displayNameSupport;
   @Nullable
   private PlayerTaskSupport playerTaskSupport;
   private boolean inUse;
   @Nullable
   private Instruction currentTreeModeStep;
   private int roleIndex = -1;
   @Nullable
   private String name;
   @Nullable
   private IndexedInstructions indexedInstructions;
   @Nullable
   private Ref<EntityStore> currentRef;
   @Nullable
   private ComponentAccessor<EntityStore> currentAccessor;

   @Nonnull
   public static ExecutionSupport acquire() {
      List<ExecutionSupport> pool = POOL.get();
      ExecutionSupport es = pool.isEmpty() ? new ExecutionSupport() : pool.removeLast();
      assert !es.inUse : "ExecutionSupport pool invariant violated: pooled instance was already in use";
      es.inUse = true;
      return es;
   }

   @Nonnull
   public StateSupport getStateSupport() {
      if (this.stateSupport == null && this.currentRef != null && this.currentAccessor != null) {
         this.stateSupport = this.currentAccessor.getComponent(this.currentRef, StateSupport.getComponentType());
      }

      assert this.stateSupport != null : "Missing StateSupport on entity " + this.currentRef;
      return this.stateSupport;
   }

   @Nonnull
   public MarkedEntitySupport getMarkedEntitySupport() {
      if (this.markedEntitySupport == null && this.currentRef != null && this.currentAccessor != null) {
         this.markedEntitySupport = this.currentAccessor.getComponent(this.currentRef, MarkedEntitySupport.getComponentType());
      }

      assert this.markedEntitySupport != null : "Missing MarkedEntitySupport on entity " + this.currentRef;
      return this.markedEntitySupport;
   }

   @Nonnull
   public WorldSupport getWorldSupport() {
      if (this.worldSupport == null && this.currentRef != null && this.currentAccessor != null) {
         this.worldSupport = this.currentAccessor.getComponent(this.currentRef, WorldSupport.getComponentType());
      }

      assert this.worldSupport != null : "Missing WorldSupport on entity " + this.currentRef;
      return this.worldSupport;
   }

   @Nonnull
   public EntitySupport getEntitySupport() {
      if (this.entitySupport == null && this.currentRef != null && this.currentAccessor != null) {
         this.entitySupport = this.currentAccessor.getComponent(this.currentRef, EntitySupport.getComponentType());
      }

      assert this.entitySupport != null : "Missing EntitySupport on entity " + this.currentRef;
      return this.entitySupport;
   }

   @Nonnull
   public PositionCache getPositionCache() {
      if (this.positionCache == null && this.currentRef != null && this.currentAccessor != null) {
         this.positionCache = this.currentAccessor.getComponent(this.currentRef, PositionCache.getComponentType());
      }

      assert this.positionCache != null : "Missing PositionCache on entity " + this.currentRef;
      return this.positionCache;
   }

   @Nonnull
   public DebugSupport getDebugSupport() {
      if (this.debugSupport == null && this.currentRef != null && this.currentAccessor != null) {
         this.debugSupport = this.currentAccessor.getComponent(this.currentRef, DebugSupport.getComponentType());
      }

      assert this.debugSupport != null : "Missing DebugSupport on entity " + this.currentRef;
      return this.debugSupport;
   }

   @Nonnull
   public FlagsComponent getFlagsComponent() {
      if (this.flagsComponent == null && this.currentRef != null && this.currentAccessor != null) {
         this.flagsComponent = this.currentAccessor.getComponent(this.currentRef, FlagsComponent.getComponentType());
      }

      assert this.flagsComponent != null : "Missing FlagsComponent on entity " + this.currentRef;
      return this.flagsComponent;
   }

   @Nonnull
   public CombatSupport getCombatSupport() {
      if (this.combatSupport == null && this.currentRef != null && this.currentAccessor != null) {
         this.combatSupport = this.currentAccessor.getComponent(this.currentRef, CombatSupport.getComponentType());
      }

      assert this.combatSupport != null : "Missing CombatSupport on entity " + this.currentRef;
      return this.combatSupport;
   }

   @Nonnull
   public MotionContextSupport getMotionContextSupport() {
      if (this.motionContextSupport == null && this.currentRef != null && this.currentAccessor != null) {
         this.motionContextSupport = this.currentAccessor.getComponent(this.currentRef, MotionContextSupport.getComponentType());
      }

      assert this.motionContextSupport != null : "Missing MotionContextSupport on entity " + this.currentRef;
      return this.motionContextSupport;
   }

   @Nonnull
   public DisplayNameSupport getDisplayNameSupport() {
      if (this.displayNameSupport == null && this.currentRef != null && this.currentAccessor != null) {
         this.displayNameSupport = this.currentAccessor.getComponent(this.currentRef, DisplayNameSupport.getComponentType());
      }

      assert this.displayNameSupport != null : "Missing DisplayNameSupport on entity " + this.currentRef;
      return this.displayNameSupport;
   }

   @Nonnull
   public PlayerTaskSupport getPlayerTaskSupport() {
      if (this.playerTaskSupport == null && this.currentRef != null && this.currentAccessor != null) {
         this.playerTaskSupport = this.currentAccessor.getComponent(this.currentRef, PlayerTaskSupport.getComponentType());
      }

      assert this.playerTaskSupport != null : "Missing PlayerTaskSupport on entity " + this.currentRef;
      return this.playerTaskSupport;
   }

   public int getRoleIndex() {
      return this.roleIndex;
   }

   @Nullable
   public String getName() {
      return this.name;
   }

   @Nullable
   public Role getRole() {
      NPCEntity npc = this.getNpcEntity();
      return npc != null ? npc.getRole() : null;
   }

   @Nullable
   public NPCEntity getNpcEntity() {
      return this.currentRef != null && this.currentAccessor != null ? this.currentAccessor.getComponent(this.currentRef, NPCEntity.getComponentType()) : null;
   }

   @Nullable
   public Instruction swapTreeModeSteps(@Nullable Instruction newStep) {
      Instruction old = this.currentTreeModeStep;
      this.currentTreeModeStep = newStep;
      return old;
   }

   public void notifySensorMatch() {
      if (this.currentTreeModeStep != null) {
         this.currentTreeModeStep.notifyChildSensorMatch();
      }
   }

   public void populateFromEntity(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> accessor) {
      this.currentRef = ref;
      this.currentAccessor = accessor;
      this.combatSupport = null;
      this.stateSupport = null;
      this.markedEntitySupport = null;
      this.worldSupport = null;
      this.entitySupport = null;
      this.motionContextSupport = null;
      this.displayNameSupport = null;
      this.playerTaskSupport = null;
      this.positionCache = null;
      this.debugSupport = null;
      this.flagsComponent = null;
   }

   public void populateFromHolder(@Nonnull Holder<EntityStore> holder) {
      this.currentRef = null;
      this.currentAccessor = null;
      this.combatSupport = holder.getComponent(CombatSupport.getComponentType());
      this.stateSupport = holder.getComponent(StateSupport.getComponentType());
      this.markedEntitySupport = holder.getComponent(MarkedEntitySupport.getComponentType());
      this.worldSupport = holder.getComponent(WorldSupport.getComponentType());
      this.entitySupport = holder.getComponent(EntitySupport.getComponentType());
      this.motionContextSupport = holder.getComponent(MotionContextSupport.getComponentType());
      this.displayNameSupport = holder.getComponent(DisplayNameSupport.getComponentType());
      this.playerTaskSupport = holder.getComponent(PlayerTaskSupport.getComponentType());
      this.positionCache = holder.getComponent(PositionCache.getComponentType());
      this.debugSupport = holder.getComponent(DebugSupport.getComponentType());
      this.flagsComponent = holder.getComponent(FlagsComponent.getComponentType());
      assert this.stateSupport != null : "Missing StateSupport on holder";
      assert this.markedEntitySupport != null : "Missing MarkedEntitySupport on holder";
      assert this.worldSupport != null : "Missing WorldSupport on holder";
      assert this.entitySupport != null : "Missing EntitySupport on holder";
      assert this.positionCache != null : "Missing PositionCache on holder";
      assert this.debugSupport != null : "Missing DebugSupport on holder";
      assert this.flagsComponent != null : "Missing FlagsComponent on holder";
   }

   public void setRoleIndex(int roleIndex) {
      this.roleIndex = roleIndex;
   }

   public void setName(@Nullable String name) {
      this.name = name;
   }

   @Nullable
   public IndexedInstructions getIndexedInstructions() {
      return this.indexedInstructions;
   }

   public void setIndexedInstructions(@Nullable IndexedInstructions indexedInstructions) {
      this.indexedInstructions = indexedInstructions;
   }

   public void clearForReuse() {
      this.currentRef = null;
      this.currentAccessor = null;
      this.combatSupport = null;
      this.stateSupport = null;
      this.markedEntitySupport = null;
      this.worldSupport = null;
      this.entitySupport = null;
      this.motionContextSupport = null;
      this.displayNameSupport = null;
      this.playerTaskSupport = null;
      this.positionCache = null;
      this.debugSupport = null;
      this.flagsComponent = null;
      this.currentTreeModeStep = null;
      this.roleIndex = -1;
      this.name = null;
      this.indexedInstructions = null;
      if (this.inUse) {
         this.inUse = false;
         POOL.get().add(this);
      }
   }
}
