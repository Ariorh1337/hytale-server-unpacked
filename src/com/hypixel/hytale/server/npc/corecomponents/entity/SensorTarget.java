package com.hypixel.hytale.server.npc.corecomponents.entity;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.ComponentContext;
import com.hypixel.hytale.server.npc.corecomponents.SensorWithEntityFilters;
import com.hypixel.hytale.server.npc.corecomponents.entity.builders.BuilderSensorTarget;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.sensorinfo.EntityPositionProvider;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class SensorTarget extends SensorWithEntityFilters {
   protected static final ComponentType<EntityStore, TransformComponent> TRANSFORM_COMPONENT_TYPE = TransformComponent.getComponentType();
   protected final int targetSlot;
   protected final boolean autoUnlockTarget;
   protected final double range;
   protected final EntityPositionProvider positionProvider = new EntityPositionProvider();

   public SensorTarget(@Nonnull BuilderSensorTarget builderSensorTarget, @Nonnull BuilderSupport support) {
      super(builderSensorTarget, builderSensorTarget.getFilters(support, null, ComponentContext.SensorTarget));
      this.targetSlot = builderSensorTarget.getTargetSlot(support);
      this.range = builderSensorTarget.getRange(support);
      this.autoUnlockTarget = builderSensorTarget.getAutoUnlockTarget(support);
      this.positionProvider.setIncludeDead(this.hasDeathFilter());
   }

   @Override
   public boolean matches(@Nonnull Ref<EntityStore> ref, @Nonnull ExecutionSupport executionSupport, double dt, @Nonnull Store<EntityStore> store) {
      if (!super.matches(ref, executionSupport, dt, store)) {
         this.positionProvider.clear();
         return false;
      }

      Ref<EntityStore> target = executionSupport.getMarkedEntitySupport().getMarkedEntityRef(this.targetSlot);
      if (target == null) {
         return false;
      }

      if (!this.fulfilsRequirements(ref, executionSupport, target, store)) {
         if (this.autoUnlockTarget) {
            this.positionProvider.clear();
            executionSupport.getMarkedEntitySupport().clearMarkedEntity(this.targetSlot);
         }

         return false;
      } else {
         return this.positionProvider.setTarget(target, store) != null;
      }
   }

   @Override
   public InfoProvider getSensorInfo() {
      return this.positionProvider;
   }

   protected boolean fulfilsRequirements(
      @Nonnull Ref<EntityStore> ref, @Nonnull ExecutionSupport executionSupport, @Nonnull Ref<EntityStore> target, @Nonnull Store<EntityStore> store
   ) {
      TransformComponent transformComponent = store.getComponent(ref, TRANSFORM_COMPONENT_TYPE);
      if (transformComponent == null) {
         return false;
      }

      Vector3d position = transformComponent.getPosition();
      if (this.range != Double.MAX_VALUE) {
         TransformComponent targetTransformComponent = store.getComponent(target, TRANSFORM_COMPONENT_TYPE);
         if (targetTransformComponent == null) {
            return false;
         }

         double squaredDistance = position.distanceSquared(targetTransformComponent.getPosition());
         if (squaredDistance > this.range * this.range) {
            return false;
         }
      }

      return this.matchesFilters(ref, target, executionSupport, store);
   }
}
