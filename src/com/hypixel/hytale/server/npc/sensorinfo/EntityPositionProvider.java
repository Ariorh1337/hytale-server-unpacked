package com.hypixel.hytale.server.npc.sensorinfo;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.sensorinfo.parameterproviders.ParameterProvider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntityPositionProvider extends PositionProvider {
   @Nullable
   private Ref<EntityStore> target;
   private boolean includeDead;

   public EntityPositionProvider() {
   }

   public EntityPositionProvider(ParameterProvider parameterProvider) {
      super(parameterProvider);
   }

   public void setIncludeDead(boolean includeDead) {
      this.includeDead = includeDead;
   }

   @Override
   public void clear() {
      super.clear();
      this.target = null;
   }

   @Override
   public Ref<EntityStore> setTarget(@Nullable Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
      return this.target = super.setTarget(ref, componentAccessor, this.includeDead);
   }

   @Nullable
   @Override
   public Ref<EntityStore> getTarget() {
      if (this.target == null) {
         return null;
      }

      Store<EntityStore> store = this.target.getStore();
      if (!this.target.isValid() || !this.includeDead && store.getArchetype(this.target).contains(DeathComponent.getComponentType())) {
         this.clear();
      }

      return this.target;
   }

   @Override
   public boolean hasPosition() {
      return this.getTarget() != null;
   }
}
