package com.hypixel.hytale.builtin.encountermanager;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.component.system.tick.TickingSystem;
import com.hypixel.hytale.server.core.modules.entity.component.Intangible;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.system.ModelSystems;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.modules.entity.teleport.TeleportSystems;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.asset.builder.BuilderInfo;
import com.hypixel.hytale.server.npc.asset.builder.BuilderManager;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.instructions.Instruction;
import com.hypixel.hytale.server.npc.role.support.DebugSupport;
import com.hypixel.hytale.server.npc.role.support.PositionCache;
import com.hypixel.hytale.server.npc.role.support.StateSupport;
import com.hypixel.hytale.server.npc.systems.BlackboardSystems;
import com.hypixel.hytale.server.npc.systems.PositionCacheSystems;
import com.hypixel.hytale.server.npc.systems.RoleSystems;
import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import com.hypixel.hytale.server.spawning.SpawnLineage;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class EncounterManagerSystems {
   private static final ThreadLocal<List<Ref<EntityStore>>> ENTITY_LIST = ThreadLocal.withInitial(ReferenceArrayList::new);

   private EncounterManagerSystems() {
   }

   public static class ActivateSystem extends HolderSystem<EntityStore> {
      @Nonnull
      private final ComponentType<EntityStore, EncounterManager> encounterComponentType;
      @Nonnull
      private final Set<Dependency<EntityStore>> dependencies = Set.of(new SystemDependency<>(Order.AFTER, EncounterManagerSystems.BuilderSystem.class));

      public ActivateSystem(@Nonnull ComponentType<EntityStore, EncounterManager> encounterComponentType) {
         this.encounterComponentType = encounterComponentType;
      }

      @Nonnull
      @Override
      public Set<Dependency<EntityStore>> getDependencies() {
         return this.dependencies;
      }

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return Query.and(this.encounterComponentType, StateSupport.getComponentType(), DebugSupport.getComponentType(), PositionCache.getComponentType());
      }

      @Override
      public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
         EncounterManager encounter = holder.getComponent(this.encounterComponentType);
         assert encounter != null;
         if (encounter.isBuilt()) {
            ExecutionSupport es = ExecutionSupport.acquire();
            es.populateFromHolder(holder);

            try {
               es.getStateSupport().activate();
               DebugSupport debugSupport = es.getDebugSupport();
               debugSupport.setDebugFlags(debugSupport.getDebugFlags());
               es.getPositionCache().reset(true);
               Instruction rootInstruction = encounter.getRootInstruction();
               assert rootInstruction != null;
               PositionCacheSystems.registerInstructionsWithCache(es, rootInstruction, null, null, null);
            } finally {
               es.clearForReuse();
            }
         }
      }

      @Override
      public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
         PositionCache positionCache = holder.getComponent(PositionCache.getComponentType());
         assert positionCache != null;
         positionCache.reset(false);
      }
   }

   public static class BuilderSystem extends HolderSystem<EntityStore> {
      @Nonnull
      private final ComponentType<EntityStore, EncounterManager> encounterComponentType;
      @Nonnull
      private final Set<Dependency<EntityStore>> dependencies = Set.of(
         new SystemDependency<>(Order.BEFORE, ModelSystems.ModelSpawned.class),
         new SystemDependency<>(Order.BEFORE, BlackboardSystems.SubscriptionLifecycleSystem.class)
      );
      @Nonnull
      private final Query<EntityStore> query;

      public BuilderSystem(@Nonnull ComponentType<EntityStore, EncounterManager> encounterComponentType) {
         this.encounterComponentType = encounterComponentType;
         this.query = Archetype.of(encounterComponentType, TransformComponent.getComponentType());
      }

      @Nonnull
      @Override
      public Set<Dependency<EntityStore>> getDependencies() {
         return this.dependencies;
      }

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return this.query;
      }

      @Override
      public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
         EncounterManager encounter = holder.getComponent(this.encounterComponentType);
         assert encounter != null;
         if (!encounter.isBuilt()) {
            NPCPlugin npcPlugin = NPCPlugin.get();
            BuilderManager builderManager = npcPlugin.getBuilderManager();
            int index = encounter.getEncounterIndex();
            if (index == Integer.MIN_VALUE) {
               String id = encounter.getEncounterId();
               index = id != null ? npcPlugin.getIndex(id) : Integer.MIN_VALUE;
            }

            BuilderInfo builderInfo = index >= 0 ? builderManager.tryGetBuilderInfo(index) : null;
            if (builderInfo != null
               && builderInfo.getBuilder().category() == EncounterManager.class
               && builderInfo.getBuilder() instanceof EncounterBuilder encounterBuilder) {
               encounter.setEncounterIndex(index);
               BuilderSupport var14 = new BuilderSupport(builderManager, holder, new ExecutionContext(), builderInfo.getBuilder(), null);

               try {
                  encounterBuilder.createAndAttach(holder, var14);
               } catch (RuntimeException e) {
                  npcPlugin.getLogger().at(Level.SEVERE).withCause(e).log("Failed to build encounter manager '%s'", encounter.getEncounterId());
                  return;
               }

               if (!holder.getArchetype().contains(SpawnLineage.getComponentType())) {
                  holder.addComponent(SpawnLineage.getComponentType(), new SpawnLineage(UUID.randomUUID().toString()));
               }

               encounter.spawned(holder);
            } else {
               npcPlugin.getLogger().at(Level.SEVERE).log("Encounter manager references unknown or invalid asset '%s'", encounter.getEncounterId());
            }
         }
      }

      @Override
      public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
      }
   }

   public static class EnsureNetworkSendable extends HolderSystem<EntityStore> {
      @Nonnull
      private final Query<EntityStore> query;

      public EnsureNetworkSendable(@Nonnull ComponentType<EntityStore, EncounterManager> encounterComponentType) {
         this.query = encounterComponentType;
      }

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return this.query;
      }

      @Override
      public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
         Archetype<EntityStore> archetype = holder.getArchetype();
         assert archetype != null;
         ComponentType<EntityStore, NetworkId> networkIdComponentType = NetworkId.getComponentType();
         if (!archetype.contains(networkIdComponentType)) {
            holder.addComponent(networkIdComponentType, new NetworkId(store.getExternalData().takeNextNetworkId()));
         }

         holder.ensureComponent(Intangible.getComponentType());
      }

      @Override
      public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
      }
   }

   public static class LifecycleSystem extends RefSystem<EntityStore> {
      @Nonnull
      private final ComponentType<EntityStore, EncounterManager> encounterComponentType;
      @Nonnull
      private final Set<Dependency<EntityStore>> dependencies = Set.of(new SystemDependency<>(Order.AFTER, EncounterManagerSystems.BuilderSystem.class));

      public LifecycleSystem(@Nonnull ComponentType<EntityStore, EncounterManager> encounterComponentType) {
         this.encounterComponentType = encounterComponentType;
      }

      @Nonnull
      @Override
      public Set<Dependency<EntityStore>> getDependencies() {
         return this.dependencies;
      }

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return this.encounterComponentType;
      }

      @Override
      public void onEntityAdded(
         @Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
      }

      @Override
      public void onEntityRemove(
         @Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         EncounterManager encounter = store.getComponent(ref, this.encounterComponentType);
         assert encounter != null;
         if (encounter.isBuilt()) {
            switch (reason) {
               case REMOVE:
               case BUILDER_TOOLS_UNDO:
                  encounter.removed(ref, commandBuffer);
                  break;
               case UNLOAD:
                  encounter.unloaded(ref, commandBuffer);
            }
         }
      }
   }

   public static class TeleportSystem extends RefChangeSystem<EntityStore, Teleport> {
      @Nonnull
      private final ComponentType<EntityStore, EncounterManager> encounterComponentType;
      @Nonnull
      private final ComponentType<EntityStore, Teleport> teleportComponentType = Teleport.getComponentType();
      @Nonnull
      private final Set<Dependency<EntityStore>> dependencies = Set.of(new SystemDependency<>(Order.AFTER, TeleportSystems.MoveSystem.class));

      public TeleportSystem(@Nonnull ComponentType<EntityStore, EncounterManager> encounterComponentType) {
         this.encounterComponentType = encounterComponentType;
      }

      @Nonnull
      @Override
      public Set<Dependency<EntityStore>> getDependencies() {
         return this.dependencies;
      }

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return this.encounterComponentType;
      }

      @Nonnull
      @Override
      public ComponentType<EntityStore, Teleport> componentType() {
         return this.teleportComponentType;
      }

      public void onComponentAdded(
         @Nonnull Ref<EntityStore> ref, @Nonnull Teleport component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         EncounterManager encounter = commandBuffer.getComponent(ref, this.encounterComponentType);
         assert encounter != null;
         if (encounter.isBuilt()) {
            World world = store.getExternalData().getWorld();
            World worldTo = component.getWorld();
            encounter.teleported(ref, commandBuffer, world, worldTo == null ? world : worldTo);
         }
      }

      public void onComponentSet(
         @Nonnull Ref<EntityStore> ref,
         Teleport oldComponent,
         @Nonnull Teleport newComponent,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
      }

      public void onComponentRemoved(
         @Nonnull Ref<EntityStore> ref, @Nonnull Teleport component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
      }
   }

   public static class TickSystem extends TickingSystem<EntityStore> {
      @Nonnull
      private final ComponentType<EntityStore, EncounterManager> encounterComponentType;
      @Nonnull
      private final Set<Dependency<EntityStore>> dependencies = Set.of(
         new SystemDependency<>(Order.AFTER, PositionCacheSystems.UpdateSystem.class),
         new SystemDependency<>(Order.AFTER, RoleSystems.MarkedTargetRebindSystem.class),
         new SystemDependency<>(Order.BEFORE, RoleSystems.WorldSupportTickSystem.class),
         new SystemDependency<>(Order.BEFORE, RoleSystems.EntitySupportTickSystem.class),
         new SystemDependency<>(Order.BEFORE, RoleSystems.PositionCacheClearSystem.class),
         new SystemDependency<>(Order.BEFORE, RoleSystems.StateSupportUpdateSystem.class),
         new SystemDependency<>(Order.BEFORE, RoleSystems.MarkedEntitySupportPostSystem.class)
      );

      public TickSystem(@Nonnull ComponentType<EntityStore, EncounterManager> encounterComponentType) {
         this.encounterComponentType = encounterComponentType;
      }

      @Nonnull
      @Override
      public Set<Dependency<EntityStore>> getDependencies() {
         return this.dependencies;
      }

      @Override
      public void tick(float dt, int systemIndex, @Nonnull Store<EntityStore> store) {
         List<Ref<EntityStore>> entities = EncounterManagerSystems.ENTITY_LIST.get();
         store.forEachChunk(this.encounterComponentType, (archetypeChunk, var2x) -> {
            for (int index = 0; index < archetypeChunk.size(); index++) {
               entities.add(archetypeChunk.getReferenceTo(index));
            }
         });

         for (Ref<EntityStore> ref : entities) {
            if (ref.isValid()) {
               EncounterManager encounter = store.getComponent(ref, this.encounterComponentType);
               assert encounter != null;
               if (encounter.isBuilt()) {
                  try {
                     encounter.tick(ref, dt, store);
                  } catch (IllegalArgumentException | IllegalStateException e) {
                     NPCPlugin.get().getLogger().at(Level.SEVERE).withCause(e).log("Failed to tick encounter manager '%s'", encounter.getEncounterId());
                     store.removeEntity(ref, RemoveReason.REMOVE);
                  }
               }
            }
         }

         entities.clear();
      }
   }
}
