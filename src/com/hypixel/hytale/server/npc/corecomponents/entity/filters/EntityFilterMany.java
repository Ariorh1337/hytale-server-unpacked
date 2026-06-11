package com.hypixel.hytale.server.npc.corecomponents.entity.filters;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.corecomponents.EntityFilterBase;
import com.hypixel.hytale.server.npc.corecomponents.IEntityFilter;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.movement.controllers.MotionController;
import com.hypixel.hytale.server.npc.util.IAnnotatedComponent;
import com.hypixel.hytale.server.npc.util.IAnnotatedComponentCollection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class EntityFilterMany extends EntityFilterBase implements IAnnotatedComponentCollection {
   @Nonnull
   protected final IEntityFilter[] filters;
   protected final int cost;

   public EntityFilterMany(@Nonnull List<IEntityFilter> filters) {
      if (filters == null) {
         throw new IllegalArgumentException("Filter list can't be null");
      }

      this.filters = filters.toArray(IEntityFilter[]::new);

      for (IEntityFilter filter : this.filters) {
         if (filter == null) {
            throw new IllegalArgumentException("Filter cannot be null in filter list");
         }
      }

      IEntityFilter.prioritiseFilters(this.filters);
      int cost = 0;

      for (int i = 0; i < this.filters.length; i++) {
         cost = (int)(cost + this.filters[i].cost() * (1.0 / Math.pow(2.0, i)));
      }

      this.cost = cost;
   }

   @Override
   public int cost() {
      return this.cost;
   }

   @Override
   public void registerWithSupport(ExecutionSupport executionSupport) {
      for (IEntityFilter filter : this.filters) {
         filter.registerWithSupport(executionSupport);
      }
   }

   @Override
   public void motionControllerChanged(
      @Nullable Ref<EntityStore> ref,
      @Nonnull NPCEntity npcComponent,
      MotionController motionController,
      @Nullable ComponentAccessor<EntityStore> componentAccessor
   ) {
      for (IEntityFilter filter : this.filters) {
         filter.motionControllerChanged(ref, npcComponent, motionController, componentAccessor);
      }
   }

   @Override
   public void loaded(ExecutionSupport executionSupport) {
      for (IEntityFilter filter : this.filters) {
         filter.loaded(executionSupport);
      }
   }

   @Override
   public void spawned(ExecutionSupport executionSupport) {
      for (IEntityFilter filter : this.filters) {
         filter.spawned(executionSupport);
      }
   }

   @Override
   public void unloaded(ExecutionSupport executionSupport) {
      for (IEntityFilter filter : this.filters) {
         filter.unloaded(executionSupport);
      }
   }

   @Override
   public void removed(ExecutionSupport executionSupport) {
      for (IEntityFilter filter : this.filters) {
         filter.removed(executionSupport);
      }
   }

   @Override
   public void teleported(ExecutionSupport executionSupport, World from, World to) {
      for (IEntityFilter filter : this.filters) {
         filter.teleported(executionSupport, from, to);
      }
   }

   @Override
   public int componentCount() {
      return this.filters.length;
   }

   @Override
   public IAnnotatedComponent getComponent(int index) {
      return this.filters[index];
   }

   @Override
   public void setContext(IAnnotatedComponent parent, int index) {
      super.setContext(parent, index);

      for (int i = 0; i < this.filters.length; i++) {
         this.filters[i].setContext(this, i);
      }
   }
}
