package com.hypixel.hytale.builtin.buildertools.snapshot;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public class EntityTransformSnapshot implements EntitySnapshot<EntityTransformSnapshot> {
   @Nonnull
   private Ref<EntityStore> ref;
   @Nonnull
   private final Transform transform;
   @Nonnull
   private final Rotation3f headRotation;

   public EntityTransformSnapshot(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
      this.ref = ref;
      TransformComponent transformComponent = componentAccessor.getComponent(ref, TransformComponent.getComponentType());
      assert transformComponent != null;
      HeadRotation headRotationComponent = componentAccessor.getComponent(ref, HeadRotation.getComponentType());
      assert headRotationComponent != null;
      this.transform = new Transform(transformComponent.getTransform());
      this.headRotation = new Rotation3f(headRotationComponent.getRotation());
   }

   @Override
   public void updateEntityRef(@Nonnull Ref<EntityStore> oldRef, @Nonnull Ref<EntityStore> newRef) {
      if (this.ref == oldRef) {
         this.ref = newRef;
      }
   }

   public void applyTransform(@Nonnull ComponentAccessor<EntityStore> componentAccessor) {
      if (this.ref.isValid()) {
         TransformComponent transformComponent = componentAccessor.getComponent(this.ref, TransformComponent.getComponentType());
         if (transformComponent != null) {
            transformComponent.setPosition(this.transform.getPosition());
            transformComponent.setRotation(this.transform.getRotation());
            HeadRotation headRotationComponent = componentAccessor.getComponent(this.ref, HeadRotation.getComponentType());
            if (headRotationComponent != null) {
               headRotationComponent.setRotation(this.headRotation);
            }
         }
      }
   }

   public EntityTransformSnapshot restoreEntity(@Nonnull PlayerRef playerRef, @Nonnull World world, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
      if (!this.ref.isValid()) {
         return null;
      }

      TransformComponent transformComponent = componentAccessor.getComponent(this.ref, TransformComponent.getComponentType());
      assert transformComponent != null;
      transformComponent.setPosition(this.transform.getPosition());
      transformComponent.setRotation(this.transform.getRotation());
      HeadRotation headRotationComponent = componentAccessor.getComponent(this.ref, HeadRotation.getComponentType());
      if (headRotationComponent != null) {
         headRotationComponent.setRotation(this.headRotation);
      }

      return new EntityTransformSnapshot(this.ref, componentAccessor);
   }
}
