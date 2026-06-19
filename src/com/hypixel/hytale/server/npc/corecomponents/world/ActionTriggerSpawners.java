package com.hypixel.hytale.server.npc.corecomponents.world;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.random.RandomExtra;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.corecomponents.world.builders.BuilderActionTriggerSpawners;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.hypixel.hytale.server.spawning.SpawnLineage;
import com.hypixel.hytale.server.spawning.spawnmarkers.SpawnMarkerEntity;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;

public class ActionTriggerSpawners extends ActionBase {
   protected static final ComponentType<EntityStore, SpawnMarkerEntity> SPAWN_MARKER_ENTITY_COMPONENT_TYPE = SpawnMarkerEntity.getComponentType();
   protected static final ComponentType<EntityStore, TransformComponent> TRANSFORM_COMPONENT_TYPE = TransformComponent.getComponentType();
   protected final String spawner;
   protected final double range;
   protected final double rangeSquared;
   protected final int count;
   protected final int markedTargetSlot;
   protected final boolean rebindTarget;
   @Nullable
   protected final List<Ref<EntityStore>> triggerList;
   protected final List<Ref<EntityStore>> spawnedList;
   protected Ref<EntityStore> parentRef;

   public ActionTriggerSpawners(@Nonnull BuilderActionTriggerSpawners builder, @Nonnull BuilderSupport support) {
      super(builder);
      this.spawner = builder.getSpawner(support);
      this.range = builder.getRange(support);
      this.rangeSquared = this.range * this.range;
      this.count = builder.getCount(support);
      this.triggerList = this.count > 0 ? new ReferenceArrayList<>(this.count) : null;
      this.markedTargetSlot = builder.getMarkedTargetSlot(support);
      this.rebindTarget = builder.isRebindTarget(support);
      this.spawnedList = new ReferenceArrayList<>();
   }

   @Override
   public void registerWithSupport(@Nonnull ExecutionSupport executionSupport) {
      executionSupport.getPositionCache().requireSpawnMarkerDistance(this.range);
   }

   @Override
   public boolean execute(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull ExecutionSupport executionSupport,
      @Nullable InfoProvider sensorInfo,
      double dt,
      @Nonnull Store<EntityStore> store
   ) {
      super.execute(ref, executionSupport, sensorInfo, dt, store);
      this.parentRef = ref;
      List<Ref<EntityStore>> spawners = executionSupport.getPositionCache().getSpawnMarkerList();
      if (this.count <= 0) {
         for (int i = 0; i < spawners.size(); i++) {
            Ref<EntityStore> spawnMarkerRef = this.filterMarker(spawners.get(i), store);
            if (spawnMarkerRef != null) {
               SpawnMarkerEntity spawnMarkerEntityComponent = store.getComponent(spawnMarkerRef, SPAWN_MARKER_ENTITY_COMPONENT_TYPE);
               assert spawnMarkerEntityComponent != null;
               spawnMarkerEntityComponent.trigger(spawnMarkerRef, this.spawnedList, store);
            }
         }

         this.stampLineageAndTrack(executionSupport, store);
         return true;
      } else {
         RandomExtra.reservoirSample(spawners, (reference, _this, _store) -> _this.filterMarker(reference, _store), this.count, this.triggerList, this, store);

         for (int i = 0; i < this.triggerList.size(); i++) {
            Ref<EntityStore> spawnMarkerRef = this.triggerList.get(i);
            SpawnMarkerEntity spawnMarkerEntityComponent = store.getComponent(spawnMarkerRef, SPAWN_MARKER_ENTITY_COMPONENT_TYPE);
            assert spawnMarkerEntityComponent != null;
            spawnMarkerEntityComponent.trigger(spawnMarkerRef, this.spawnedList, store);
         }

         this.stampLineageAndTrack(executionSupport, store);
         this.triggerList.clear();
         return true;
      }
   }

   private void stampLineageAndTrack(@Nonnull ExecutionSupport executionSupport, @Nonnull Store<EntityStore> store) {
      for (int i = 0; i < this.spawnedList.size(); i++) {
         SpawnLineage.inherit(this.parentRef, this.spawnedList.get(i), store);
      }

      if (this.markedTargetSlot >= 0 && !this.spawnedList.isEmpty()) {
         executionSupport.getMarkedEntitySupport().setMarkedEntity(this.markedTargetSlot, this.spawnedList.getFirst(), this.rebindTarget, store);
      }

      this.spawnedList.clear();
   }

   @Nullable
   protected Ref<EntityStore> filterMarker(@Nonnull Ref<EntityStore> targetRef, @Nonnull Store<EntityStore> store) {
      if (!targetRef.isValid()) {
         return null;
      }

      TransformComponent parentTransformComponent = store.getComponent(this.parentRef, TRANSFORM_COMPONENT_TYPE);
      assert parentTransformComponent != null;
      Vector3d parentPosition = parentTransformComponent.getPosition();
      TransformComponent targetTransformComponent = store.getComponent(targetRef, TRANSFORM_COMPONENT_TYPE);
      assert targetTransformComponent != null;
      Vector3d targetPosition = targetTransformComponent.getPosition();
      SpawnMarkerEntity targetMarkerEntityComponent = store.getComponent(targetRef, SPAWN_MARKER_ENTITY_COMPONENT_TYPE);
      return targetMarkerEntityComponent == null
            || !targetMarkerEntityComponent.isManualTrigger()
            || targetMarkerEntityComponent.getSpawnCount() > 0
            || !(parentPosition.distanceSquared(targetPosition) <= this.rangeSquared)
            || this.spawner != null && !this.spawner.equals(targetMarkerEntityComponent.getSpawnMarkerId())
         ? null
         : targetRef;
   }
}
