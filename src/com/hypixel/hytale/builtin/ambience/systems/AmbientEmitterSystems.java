package com.hypixel.hytale.builtin.ambience.systems;

import com.hypixel.hytale.builtin.ambience.AmbiencePlugin;
import com.hypixel.hytale.builtin.ambience.components.AmbientEmitterComponent;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.NonSerialized;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.modules.entity.component.AudioComponent;
import com.hypixel.hytale.server.core.modules.entity.component.Intangible;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.prefab.PrefabCopyableComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class AmbientEmitterSystems {
   public static class EntityAdded extends HolderSystem<EntityStore> {
      @Nonnull
      private final Query<EntityStore> query = Query.and(AmbientEmitterComponent.getComponentType(), TransformComponent.getComponentType());

      @Override
      public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
         Archetype<EntityStore> archetype = holder.getArchetype();
         ComponentType<EntityStore, NetworkId> networkIdComponentType = NetworkId.getComponentType();
         if (!archetype.contains(networkIdComponentType)) {
            holder.addComponent(networkIdComponentType, new NetworkId(store.getExternalData().takeNextNetworkId()));
         }

         holder.ensureComponent(Intangible.getComponentType());
         holder.ensureComponent(PrefabCopyableComponent.getComponentType());
      }

      @Override
      public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
      }

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return this.query;
      }
   }

   public static class EntityRefAdded extends RefSystem<EntityStore> {
      @Nonnull
      private final Query<EntityStore> query = Query.and(AmbientEmitterComponent.getComponentType(), TransformComponent.getComponentType());

      @Override
      public void onEntityAdded(
         @Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         AmbientEmitterComponent emitterComponent = store.getComponent(ref, AmbientEmitterComponent.getComponentType());
         assert emitterComponent != null;
         TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
         assert transformComponent != null;
         Holder<EntityStore> emitterHolder = EntityStore.REGISTRY.newHolder();
         emitterHolder.addComponent(TransformComponent.getComponentType(), transformComponent.clone());
         AudioComponent audioComponent = new AudioComponent();
         audioComponent.addSound(SoundEvent.getAssetMap().getIndex(emitterComponent.getSoundEventId()));
         emitterHolder.addComponent(AudioComponent.getComponentType(), audioComponent);
         emitterHolder.addComponent(NetworkId.getComponentType(), new NetworkId(store.getExternalData().takeNextNetworkId()));
         emitterHolder.ensureComponent(Intangible.getComponentType());
         emitterHolder.addComponent(EntityStore.REGISTRY.getNonSerializedComponentType(), NonSerialized.get());
         emitterComponent.setSpawnedEmitter(commandBuffer.addEntity(emitterHolder, AddReason.SPAWN));
      }

      @Override
      public void onEntityRemove(
         @Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         if (reason == RemoveReason.REMOVE) {
            AmbientEmitterComponent emitterComponent = store.getComponent(ref, AmbientEmitterComponent.getComponentType());
            assert emitterComponent != null;
            Ref<EntityStore> emitterRef = emitterComponent.getSpawnedEmitter();
            if (emitterRef != null) {
               commandBuffer.removeEntity(emitterRef, RemoveReason.REMOVE);
            }
         }
      }

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return this.query;
      }
   }

   public static class Ticking extends EntityTickingSystem<EntityStore> {
      @Nonnull
      private final Query<EntityStore> query = Query.and(AmbientEmitterComponent.getComponentType(), TransformComponent.getComponentType());

      @Override
      public void tick(
         float dt,
         int index,
         @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         AmbientEmitterComponent emitterComponent = archetypeChunk.getComponent(index, AmbientEmitterComponent.getComponentType());
         assert emitterComponent != null;
         TransformComponent transformComponent = archetypeChunk.getComponent(index, TransformComponent.getComponentType());
         assert transformComponent != null;
         Ref<EntityStore> spawnedEmitterRef = emitterComponent.getSpawnedEmitter();
         if (spawnedEmitterRef != null && spawnedEmitterRef.isValid()) {
            TransformComponent ownedEmitterTransform = commandBuffer.getComponent(spawnedEmitterRef, TransformComponent.getComponentType());
            if (ownedEmitterTransform != null) {
               if (transformComponent.getPosition().distanceSquaredTo(ownedEmitterTransform.getPosition()) > 1.0) {
                  ownedEmitterTransform.setPosition(transformComponent.getPosition());
               }
            }
         } else {
            Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
            AmbiencePlugin.get()
               .getLogger()
               .at(Level.WARNING)
               .log("Ambient emitter lost at %s: %d %s", transformComponent.getPosition(), ref.getIndex(), emitterComponent.getSoundEventId());
            commandBuffer.removeEntity(ref, RemoveReason.REMOVE);
         }
      }

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return this.query;
      }
   }
}
