package com.hypixel.hytale.builtin.encountermanager;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.common.thread.ticking.Tickable;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.blackboard.BlackboardSubscription;
import com.hypixel.hytale.server.npc.components.Timers;
import com.hypixel.hytale.server.npc.components.messaging.BeaconSupport;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.instructions.IndexedInstructions;
import com.hypixel.hytale.server.npc.instructions.Instruction;
import com.hypixel.hytale.server.npc.role.support.DebugSupport;
import com.hypixel.hytale.server.npc.role.support.EntitySupport;
import com.hypixel.hytale.server.npc.role.support.FlagsComponent;
import com.hypixel.hytale.server.npc.role.support.MarkedEntitySupport;
import com.hypixel.hytale.server.npc.role.support.PositionCache;
import com.hypixel.hytale.server.npc.role.support.StateSupport;
import com.hypixel.hytale.server.npc.role.support.WorldSupport;
import com.hypixel.hytale.server.npc.statetransition.StateTransitionController;
import com.hypixel.hytale.server.npc.util.ComponentInfo;
import com.hypixel.hytale.server.npc.util.IAnnotatedComponent;
import com.hypixel.hytale.server.npc.valuestore.ValueStore;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EncounterManager implements Component<EntityStore>, IAnnotatedComponent {
   @Nonnull
   public static final BuilderCodec<EncounterManager> CODEC = BuilderCodec.builder(EncounterManager.class, EncounterManager::new)
      .append(new KeyedCodec<>("EncounterId", Codec.STRING), (encounter, s) -> {
         encounter.encounterId = s;
         encounter.encounterIndex = NPCPlugin.get().getIndex(s);
      }, encounter -> encounter.encounterId)
      .add()
      .build();
   private String encounterId;
   private int encounterIndex = Integer.MIN_VALUE;
   @Nullable
   private Instruction rootInstruction;
   @Nullable
   private IndexedInstructions indexedInstructions;

   @Nonnull
   public static ComponentType<EntityStore, EncounterManager> getComponentType() {
      return EncounterManagerPlugin.get().getEncounterComponentType();
   }

   public EncounterManager() {
   }

   public EncounterManager(@Nonnull String encounterId, int encounterIndex) {
      this.encounterId = encounterId;
      this.encounterIndex = encounterIndex;
   }

   @Nonnull
   public static EncounterManager createAndAttach(
      @Nonnull Holder<EntityStore> holder, @Nonnull BuilderEncounterManager builder, @Nonnull BuilderSupport builderSupport
   ) {
      StateSupport stateSupport = new StateSupport(builder, builderSupport);
      WorldSupport worldSupport = new WorldSupport(builder, builderSupport);
      EntitySupport entitySupport = new EntitySupport();
      PositionCache positionCache = new PositionCache(builderSupport.getRoleStats());
      positionCache.setOpaqueBlockSet(-1);
      DebugSupport debugSupport = new DebugSupport(builder);
      FlagsComponent flagsComponent = new FlagsComponent();
      holder.putComponent(StateSupport.getComponentType(), stateSupport);
      if (holder.getComponent(MarkedEntitySupport.getComponentType()) == null) {
         holder.putComponent(MarkedEntitySupport.getComponentType(), new MarkedEntitySupport());
      }

      holder.putComponent(WorldSupport.getComponentType(), worldSupport);
      holder.putComponent(EntitySupport.getComponentType(), entitySupport);
      holder.putComponent(PositionCache.getComponentType(), positionCache);
      holder.putComponent(DebugSupport.getComponentType(), debugSupport);
      holder.putComponent(FlagsComponent.getComponentType(), flagsComponent);
      EncounterManager encounter = holder.getComponent(getComponentType());
      if (encounter == null) {
         encounter = new EncounterManager();
         holder.putComponent(getComponentType(), encounter);
      }

      encounter.buildInstructionTree(builder, builderSupport);
      encounter.postBuilt(holder, builderSupport);
      return encounter;
   }

   private void buildInstructionTree(@Nonnull BuilderEncounterManager builder, @Nonnull BuilderSupport builderSupport) {
      if (this.encounterIndex == Integer.MIN_VALUE && this.encounterId != null) {
         this.encounterIndex = NPCPlugin.get().getIndex(this.encounterId);
      }

      List<Instruction> instructionList = builder.getInstructionList(builderSupport);
      if (instructionList == null) {
         instructionList = List.of();
      }

      Instruction[] instructions = instructionList.toArray(Instruction[]::new);
      this.rootInstruction = Instruction.createRootInstruction(instructions, builderSupport);
   }

   private void postBuilt(@Nonnull Holder<EntityStore> holder, @Nonnull BuilderSupport builderSupport) {
      FlagsComponent flagsComponent = holder.getComponent(FlagsComponent.getComponentType());
      assert flagsComponent != null;
      flagsComponent.setFlags(builderSupport.allocateFlags());
      this.indexedInstructions = new IndexedInstructions(builderSupport.getInstructionSlotMappings());
      StateSupport stateSupport = holder.getComponent(StateSupport.getComponentType());
      assert stateSupport != null;
      stateSupport.postRoleBuilt(builderSupport);
      WorldSupport worldSupport = holder.getComponent(WorldSupport.getComponentType());
      assert worldSupport != null;
      worldSupport.postRoleBuilt(builderSupport);
      EntitySupport entitySupport = holder.getComponent(EntitySupport.getComponentType());
      assert entitySupport != null;
      entitySupport.postRoleBuilt(builderSupport);
      MarkedEntitySupport markedEntitySupport = holder.getComponent(MarkedEntitySupport.getComponentType());
      assert markedEntitySupport != null;
      markedEntitySupport.postRoleBuilder(builderSupport);
      PositionCache positionCache = holder.getComponent(PositionCache.getComponentType());
      assert positionCache != null;
      positionCache.setRoleIndex(this.encounterIndex);
      Tickable[] timers = builderSupport.allocateTimers();
      if (timers != null) {
         holder.putComponent(Timers.getComponentType(), new Timers(timers));
      }

      ValueStore.Builder valueStoreBuilder = builderSupport.getValueStoreBuilder();
      if (valueStoreBuilder != null) {
         holder.putComponent(ValueStore.getComponentType(), valueStoreBuilder.build());
      }

      Object2IntMap<String> beaconSlotMappings = builderSupport.getBeaconSlotMappings();
      if (beaconSlotMappings != null) {
         BeaconSupport beaconSupport = new BeaconSupport();
         beaconSupport.initialise(beaconSlotMappings);
         holder.putComponent(BeaconSupport.getComponentType(), beaconSupport);
      }

      BlackboardSubscription.buildAndAttach(holder, builderSupport);
      assert this.rootInstruction != null;
      this.rootInstruction.setContext(this, 0);
   }

   @Nonnull
   public ExecutionSupport acquireExecutionSupport(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> accessor) {
      ExecutionSupport es = ExecutionSupport.acquire();
      es.populateFromEntity(ref, accessor);
      es.setRoleIndex(this.encounterIndex);
      es.setName(this.encounterId);
      es.setIndexedInstructions(this.indexedInstructions);
      return es;
   }

   public void tick(@Nonnull Ref<EntityStore> ref, float dt, @Nonnull Store<EntityStore> store) {
      ExecutionSupport es = this.acquireExecutionSupport(ref, store);

      try {
         es.getEntitySupport().tickDeferredActions(ref, es, dt, store);
         DebugSupport debugSupport = es.getDebugSupport();
         if (debugSupport.isVisSensorRanges()) {
            debugSupport.beginSensorVisualization();
         }

         StateSupport stateSupport = es.getStateSupport();
         if (!stateSupport.runTransitionActions(ref, es, dt, store)) {
            assert this.rootInstruction != null;
            this.rootInstruction.execute(ref, es, dt, store);
         }
      } finally {
         es.clearForReuse();
      }
   }

   public void spawned(@Nonnull Holder<EntityStore> holder) {
      ExecutionSupport es = ExecutionSupport.acquire();
      es.populateFromHolder(holder);
      es.setRoleIndex(this.encounterIndex);
      es.setName(this.encounterId);
      es.setIndexedInstructions(this.indexedInstructions);

      try {
         assert this.rootInstruction != null;
         this.rootInstruction.spawned(es);
         StateTransitionController stateTransitions = es.getStateSupport().getStateTransitionController();
         if (stateTransitions != null) {
            stateTransitions.spawned(es);
         }
      } finally {
         es.clearForReuse();
      }
   }

   public void unloaded(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> accessor) {
      if (this.rootInstruction != null) {
         ExecutionSupport es = this.acquireExecutionSupport(ref, accessor);

         try {
            es.getEntitySupport().unloaded();
            es.getWorldSupport().unloaded(ref);
            es.getMarkedEntitySupport().unloaded();
            this.rootInstruction.unloaded(es);
            StateTransitionController stateTransitions = es.getStateSupport().getStateTransitionController();
            if (stateTransitions != null) {
               stateTransitions.unloaded(es);
            }
         } finally {
            es.clearForReuse();
         }
      }
   }

   public void removed(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> accessor) {
      if (this.rootInstruction != null) {
         ExecutionSupport es = this.acquireExecutionSupport(ref, accessor);

         try {
            es.getWorldSupport().resetAllBlockSensors(ref);
            this.rootInstruction.removed(es);
            StateTransitionController stateTransitions = es.getStateSupport().getStateTransitionController();
            if (stateTransitions != null) {
               stateTransitions.removed(es);
            }
         } finally {
            es.clearForReuse();
         }
      }
   }

   public void teleported(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> accessor, @Nonnull World from, @Nonnull World to) {
      if (this.rootInstruction != null) {
         ExecutionSupport es = this.acquireExecutionSupport(ref, accessor);

         try {
            this.rootInstruction.teleported(es, from, to);
            StateTransitionController stateTransitions = es.getStateSupport().getStateTransitionController();
            if (stateTransitions != null) {
               stateTransitions.teleported(es, from, to);
            }
         } finally {
            es.clearForReuse();
         }
      }
   }

   @Nullable
   public Instruction getRootInstruction() {
      return this.rootInstruction;
   }

   @Nullable
   public String getEncounterId() {
      return this.encounterId;
   }

   public void setEncounterId(@Nonnull String encounterId) {
      this.encounterId = encounterId;
   }

   public int getEncounterIndex() {
      return this.encounterIndex;
   }

   public void setEncounterIndex(int encounterIndex) {
      this.encounterIndex = encounterIndex;
   }

   public boolean isBuilt() {
      return this.rootInstruction != null;
   }

   public void resetRuntime() {
      this.rootInstruction = null;
      this.indexedInstructions = null;
   }

   @Nonnull
   @Override
   public Component<EntityStore> clone() {
      EncounterManager clone = new EncounterManager();
      clone.encounterId = this.encounterId;
      clone.encounterIndex = this.encounterIndex;
      return clone;
   }

   @Nullable
   @Override
   public Component<EntityStore> cloneSerializable() {
      EncounterManager clone = new EncounterManager();
      clone.encounterId = this.encounterId;
      return clone;
   }

   @Override
   public void getInfo(ExecutionSupport executionSupport, ComponentInfo holder) {
   }

   @Override
   public void setContext(IAnnotatedComponent parent, int index) {
   }

   @Nullable
   @Override
   public IAnnotatedComponent getParent() {
      return null;
   }

   @Override
   public int getIndex() {
      return -1;
   }
}
