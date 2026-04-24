package com.hypixel.hytale.server.core.modules.entity.tracker;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.protocol.ModelUpdate;
import com.hypixel.hytale.protocol.PlayerSkinUpdate;
import com.hypixel.hytale.protocol.PropUpdate;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.AllLegacyLivingEntityTypesQuery;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.EntityScaleComponent;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.PropComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSettings;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSkinComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;

public class LegacyEntityTrackerSystems {
   @Deprecated
   public static boolean clear(@Nonnull Player player, @Nonnull Holder<EntityStore> holder) {
      World world = player.getWorld();
      if (world != null && world.isInThread()) {
         Ref<EntityStore> ref = player.getReference();
         return ref != null && ref.isValid() ? EntityTrackerSystems.clear(ref, world.getEntityStore().getStore()) : false;
      }

      EntityTrackerSystems.EntityViewer entityViewerComponent = holder.getComponent(EntityTrackerSystems.EntityViewer.getComponentType());
      if (entityViewerComponent == null) {
         return false;
      }

      entityViewerComponent.sent.clear();
      return true;
   }

   public static class LegacyEntityModel extends EntityTickingSystem<EntityStore> {
      private final ComponentType<EntityStore, EntityTrackerSystems.Visible> componentType;
      private final ComponentType<EntityStore, ModelComponent> modelComponentType;
      @Nonnull
      private final Query<EntityStore> query;

      public LegacyEntityModel(ComponentType<EntityStore, EntityTrackerSystems.Visible> componentType) {
         this.componentType = componentType;
         this.modelComponentType = ModelComponent.getComponentType();
         this.query = Query.and(componentType, this.modelComponentType);
      }

      @Nullable
      @Override
      public SystemGroup<EntityStore> getGroup() {
         return EntityTrackerSystems.QUEUE_UPDATE_GROUP;
      }

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return this.query;
      }

      @Override
      public boolean isParallel(int archetypeChunkSize, int taskCount) {
         return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
      }

      @Override
      public void tick(
         float dt,
         int index,
         @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         EntityTrackerSystems.Visible visibleComponent = archetypeChunk.getComponent(index, this.componentType);
         assert visibleComponent != null;
         ModelComponent modelComponent = archetypeChunk.getComponent(index, this.modelComponentType);
         assert modelComponent != null;
         float entityScale = 0.0F;
         boolean scaleOutdated = false;
         EntityScaleComponent entityScaleComponent = archetypeChunk.getComponent(index, EntityScaleComponent.getComponentType());
         if (entityScaleComponent != null) {
            entityScale = entityScaleComponent.getScale();
            scaleOutdated = entityScaleComponent.consumeNetworkOutdated();
         }

         boolean modelOutdated = modelComponent.consumeNetworkOutdated();
         Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
         boolean isProp = store.getComponent(ref, PropComponent.getComponentType()) != null;
         if (modelOutdated || scaleOutdated) {
            queueUpdatesFor(ref, modelComponent, entityScale, isProp, visibleComponent.visibleTo);
         } else if (!visibleComponent.newlyVisibleTo.isEmpty()) {
            queueUpdatesFor(ref, modelComponent, entityScale, isProp, visibleComponent.newlyVisibleTo);
         }
      }

      private static void queueUpdatesFor(
         Ref<EntityStore> ref,
         @Nullable ModelComponent model,
         float entityScale,
         boolean isProp,
         @Nonnull Map<Ref<EntityStore>, EntityTrackerSystems.EntityViewer> visibleTo
      ) {
         ModelUpdate update = new ModelUpdate(model != null ? model.getModel().toPacket() : null, entityScale);

         for (EntityTrackerSystems.EntityViewer viewer : visibleTo.values()) {
            viewer.queueUpdate(ref, update);
         }

         if (isProp) {
            PropUpdate propUpdate = new PropUpdate();

            for (EntityTrackerSystems.EntityViewer viewer : visibleTo.values()) {
               viewer.queueUpdate(ref, propUpdate);
            }
         }
      }
   }

   public static class LegacyEntitySkin extends EntityTickingSystem<EntityStore> {
      private final ComponentType<EntityStore, PlayerSkinComponent> playerSkinComponentComponentType;
      private final ComponentType<EntityStore, EntityTrackerSystems.Visible> visibleComponentType;
      @Nonnull
      private final Query<EntityStore> query;

      public LegacyEntitySkin(
         ComponentType<EntityStore, EntityTrackerSystems.Visible> visibleComponentType,
         ComponentType<EntityStore, PlayerSkinComponent> playerSkinComponentComponentType
      ) {
         this.visibleComponentType = visibleComponentType;
         this.playerSkinComponentComponentType = playerSkinComponentComponentType;
         this.query = Query.and(visibleComponentType, playerSkinComponentComponentType);
      }

      @Nullable
      @Override
      public SystemGroup<EntityStore> getGroup() {
         return EntityTrackerSystems.QUEUE_UPDATE_GROUP;
      }

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return this.query;
      }

      @Override
      public boolean isParallel(int archetypeChunkSize, int taskCount) {
         return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
      }

      @Override
      public void tick(
         float dt,
         int index,
         @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         EntityTrackerSystems.Visible visibleComponent = archetypeChunk.getComponent(index, this.visibleComponentType);
         assert visibleComponent != null;
         PlayerSkinComponent playerSkinComponent = archetypeChunk.getComponent(index, this.playerSkinComponentComponentType);
         assert playerSkinComponent != null;
         if (playerSkinComponent.consumeNetworkOutdated()) {
            queueUpdatesFor(archetypeChunk.getReferenceTo(index), playerSkinComponent, visibleComponent.visibleTo);
         } else if (!visibleComponent.newlyVisibleTo.isEmpty()) {
            queueUpdatesFor(archetypeChunk.getReferenceTo(index), playerSkinComponent, visibleComponent.newlyVisibleTo);
         }
      }

      private static void queueUpdatesFor(
         Ref<EntityStore> ref, @Nonnull PlayerSkinComponent component, @Nonnull Map<Ref<EntityStore>, EntityTrackerSystems.EntityViewer> visibleTo
      ) {
         PlayerSkinUpdate update = new PlayerSkinUpdate();
         update.skin = component.getPlayerSkin();

         for (EntityTrackerSystems.EntityViewer viewer : visibleTo.values()) {
            viewer.queueUpdate(ref, update);
         }
      }
   }

   public static class LegacyHideFromEntity extends EntityTickingSystem<EntityStore> {
      private final ComponentType<EntityStore, EntityTrackerSystems.EntityViewer> entityViewerComponentType;
      private final ComponentType<EntityStore, PlayerSettings> playerSettingsComponentType;
      @Nonnull
      private final Query<EntityStore> query;
      @Nonnull
      private final Set<Dependency<EntityStore>> dependencies;

      public LegacyHideFromEntity(ComponentType<EntityStore, EntityTrackerSystems.EntityViewer> entityViewerComponentType) {
         this.entityViewerComponentType = entityViewerComponentType;
         this.playerSettingsComponentType = EntityModule.get().getPlayerSettingsComponentType();
         this.query = Query.and(entityViewerComponentType, AllLegacyLivingEntityTypesQuery.INSTANCE);
         this.dependencies = Collections.singleton(new SystemDependency<>(Order.AFTER, EntityTrackerSystems.CollectVisible.class));
      }

      @Nullable
      @Override
      public SystemGroup<EntityStore> getGroup() {
         return EntityTrackerSystems.FIND_VISIBLE_ENTITIES_GROUP;
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
      public boolean isParallel(int archetypeChunkSize, int taskCount) {
         return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
      }

      @Override
      public void tick(
         float dt,
         int index,
         @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         Ref<EntityStore> viewerRef = archetypeChunk.getReferenceTo(index);
         PlayerSettings settings = archetypeChunk.getComponent(index, this.playerSettingsComponentType);
         if (settings == null) {
            settings = PlayerSettings.defaults();
         }

         EntityTrackerSystems.EntityViewer entityViewerComponent = archetypeChunk.getComponent(index, this.entityViewerComponentType);
         assert entityViewerComponent != null;
         Iterator<Ref<EntityStore>> iterator = entityViewerComponent.visible.iterator();

         while (iterator.hasNext()) {
            Ref<EntityStore> ref = iterator.next();
            Entity entity = EntityUtils.getEntity(ref, commandBuffer);
            if (entity != null && entity.isHiddenFromLivingEntity(ref, viewerRef, commandBuffer) && canHideEntities(entity, settings)) {
               entityViewerComponent.hiddenCount++;
               iterator.remove();
            }
         }
      }

      private static boolean canHideEntities(Entity entity, @Nonnull PlayerSettings settings) {
         return entity instanceof Player && !settings.showEntityMarkers();
      }
   }

   public static class LegacyLODCull extends EntityTickingSystem<EntityStore> {
      public static final double ENTITY_LOD_RATIO_DEFAULT = 3.5E-5;
      public static double ENTITY_LOD_RATIO = 3.5E-5;
      private final ComponentType<EntityStore, EntityTrackerSystems.EntityViewer> entityViewerComponentType;
      private final ComponentType<EntityStore, BoundingBox> boundingBoxComponentType;
      @Nonnull
      private final Query<EntityStore> query;
      @Nonnull
      private final Set<Dependency<EntityStore>> dependencies;

      public LegacyLODCull(ComponentType<EntityStore, EntityTrackerSystems.EntityViewer> entityViewerComponentType) {
         this.entityViewerComponentType = entityViewerComponentType;
         this.boundingBoxComponentType = BoundingBox.getComponentType();
         this.query = Query.and(entityViewerComponentType, TransformComponent.getComponentType());
         this.dependencies = Collections.singleton(new SystemDependency<>(Order.AFTER, EntityTrackerSystems.CollectVisible.class));
      }

      @Nullable
      @Override
      public SystemGroup<EntityStore> getGroup() {
         return EntityTrackerSystems.FIND_VISIBLE_ENTITIES_GROUP;
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
      public boolean isParallel(int archetypeChunkSize, int taskCount) {
         return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
      }

      @Override
      public void tick(
         float dt,
         int index,
         @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         EntityTrackerSystems.EntityViewer entityViewerComponent = archetypeChunk.getComponent(index, this.entityViewerComponentType);
         assert entityViewerComponent != null;
         TransformComponent transformComponent = archetypeChunk.getComponent(index, TransformComponent.getComponentType());
         assert transformComponent != null;
         Vector3d position = transformComponent.getPosition();
         Iterator<Ref<EntityStore>> iterator = entityViewerComponent.visible.iterator();

         while (iterator.hasNext()) {
            Ref<EntityStore> targetRef = iterator.next();
            BoundingBox targetBoundingBoxComponent = commandBuffer.getComponent(targetRef, this.boundingBoxComponentType);
            if (targetBoundingBoxComponent != null) {
               TransformComponent targetTransformComponent = commandBuffer.getComponent(targetRef, TransformComponent.getComponentType());
               if (targetTransformComponent != null) {
                  double distanceSq = targetTransformComponent.getPosition().distanceSquared(position);
                  double maximumThickness = targetBoundingBoxComponent.getBoundingBox().getMaximumThickness();
                  if (maximumThickness < ENTITY_LOD_RATIO * distanceSq) {
                     entityViewerComponent.lodExcludedCount++;
                     iterator.remove();
                  }
               }
            }
         }
      }
   }
}
