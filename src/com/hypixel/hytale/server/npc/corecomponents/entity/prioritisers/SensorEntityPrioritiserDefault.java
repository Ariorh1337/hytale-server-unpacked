package com.hypixel.hytale.server.npc.corecomponents.entity.prioritisers;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.corecomponents.IEntityFilter;
import com.hypixel.hytale.server.npc.corecomponents.ISensorEntityPrioritiser;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.movement.controllers.MotionController;
import com.hypixel.hytale.server.npc.util.IEntityByPriorityFilter;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;

public class SensorEntityPrioritiserDefault implements ISensorEntityPrioritiser {
   private static final ComponentType<EntityStore, TransformComponent> TRANSFORM_COMPONENT_TYPE = TransformComponent.getComponentType();
   private final SensorEntityPrioritiserDefault.DefaultPrioritiser playerPrioritiser = new SensorEntityPrioritiserDefault.DefaultPrioritiser();
   private final SensorEntityPrioritiserDefault.DefaultPrioritiser npcPrioritiser = new SensorEntityPrioritiserDefault.DefaultPrioritiser();

   @Nonnull
   @Override
   public IEntityByPriorityFilter getNPCPrioritiser() {
      return this.npcPrioritiser;
   }

   @Nonnull
   @Override
   public IEntityByPriorityFilter getPlayerPrioritiser() {
      return this.playerPrioritiser;
   }

   @Nonnull
   @Override
   public Ref<EntityStore> pickTarget(
      Ref<EntityStore> ref,
      @Nonnull ExecutionSupport executionSupport,
      @Nonnull Vector3d position,
      @Nonnull Ref<EntityStore> playerRef,
      @Nonnull Ref<EntityStore> npcRef,
      boolean useProjectedDistance,
      @Nonnull Store<EntityStore> store
   ) {
      TransformComponent playerTransformComponent = store.getComponent(playerRef, TRANSFORM_COMPONENT_TYPE);
      assert playerTransformComponent != null;
      TransformComponent npcTransformComponent = store.getComponent(npcRef, TRANSFORM_COMPONENT_TYPE);
      assert npcTransformComponent != null;
      Vector3d playerPos = playerTransformComponent.getPosition();
      Vector3d npcPos = npcTransformComponent.getPosition();
      double playerDistance;
      double npcDistance;
      if (useProjectedDistance) {
         MotionController motionController = executionSupport.getMotionContextSupport().getActiveMotionController();
         playerDistance = motionController.getSquaredDistance(position, playerPos, true);
         npcDistance = motionController.getSquaredDistance(position, npcPos, true);
      } else {
         playerDistance = position.distanceSquared(playerPos);
         npcDistance = position.distanceSquared(npcPos);
      }

      return playerDistance <= npcDistance ? playerRef : npcRef;
   }

   @Override
   public boolean providesFilters() {
      return false;
   }

   @Override
   public void buildProvidedFilters(List<IEntityFilter> filters) {
   }

   public static class DefaultPrioritiser implements IEntityByPriorityFilter {
      @Nullable
      private Ref<EntityStore> target;

      private DefaultPrioritiser() {
      }

      @Override
      public void init(ExecutionSupport executionSupport) {
      }

      @Nullable
      @Override
      public Ref<EntityStore> getHighestPriorityTarget() {
         return this.target;
      }

      @Override
      public void cleanup() {
         this.target = null;
      }

      public boolean test(@Nonnull Ref<EntityStore> ref, @Nonnull Ref<EntityStore> targetRef, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
         if (this.target == null) {
            this.target = targetRef;
         }

         return true;
      }
   }
}
