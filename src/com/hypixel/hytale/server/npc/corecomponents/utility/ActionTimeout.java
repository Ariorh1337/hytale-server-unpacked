package com.hypixel.hytale.server.npc.corecomponents.utility;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.ActionWithDelay;
import com.hypixel.hytale.server.npc.corecomponents.utility.builders.BuilderActionTimeout;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.instructions.Action;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.movement.controllers.MotionController;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.hypixel.hytale.server.npc.util.IAnnotatedComponent;
import com.hypixel.hytale.server.npc.util.IAnnotatedComponentCollection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ActionTimeout extends ActionWithDelay implements IAnnotatedComponentCollection {
   protected final boolean delayAfter;
   @Nullable
   protected final Action action;

   public ActionTimeout(@Nonnull BuilderActionTimeout builderActionTimeout, @Nonnull BuilderSupport builderSupport) {
      super(builderActionTimeout, builderSupport);
      this.action = builderActionTimeout.getAction(builderSupport);
      this.delayAfter = builderActionTimeout.isDelayAfter();
   }

   @Override
   public boolean canExecute(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull ExecutionSupport executionSupport,
      @Nullable InfoProvider sensorInfo,
      double dt,
      @Nonnull Store<EntityStore> store
   ) {
      if (super.canExecute(ref, executionSupport, sensorInfo, dt, store)
         && (this.action == null || this.action.canExecute(ref, executionSupport, sensorInfo, dt, store))) {
         if (!this.isDelaying() && this.isDelayPrepared()) {
            this.startDelay(executionSupport.getEntitySupport());
         }

         return !this.isDelaying();
      } else {
         return false;
      }
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
      if (this.action != null) {
         this.action.execute(ref, executionSupport, sensorInfo, dt, store);
      }

      this.prepareDelay();
      return true;
   }

   @Override
   public void registerWithSupport(ExecutionSupport executionSupport) {
      if (this.action != null) {
         this.action.registerWithSupport(executionSupport);
      }

      if (this.delayAfter) {
         this.clearDelay();
      } else {
         this.prepareDelay();
      }
   }

   @Override
   public void motionControllerChanged(
      @Nullable Ref<EntityStore> ref,
      @Nonnull NPCEntity npcComponent,
      MotionController motionController,
      @Nullable ComponentAccessor<EntityStore> componentAccessor
   ) {
      if (this.action != null) {
         this.action.motionControllerChanged(ref, npcComponent, motionController, componentAccessor);
      }
   }

   @Override
   public void loaded(ExecutionSupport executionSupport) {
      if (this.action != null) {
         this.action.loaded(executionSupport);
      }
   }

   @Override
   public void spawned(ExecutionSupport executionSupport) {
      if (this.action != null) {
         this.action.spawned(executionSupport);
      }
   }

   @Override
   public void unloaded(ExecutionSupport executionSupport) {
      if (this.action != null) {
         this.action.unloaded(executionSupport);
      }
   }

   @Override
   public void removed(ExecutionSupport executionSupport) {
      if (this.action != null) {
         this.action.removed(executionSupport);
      }
   }

   @Override
   public void teleported(ExecutionSupport executionSupport, World from, World to) {
      if (this.action != null) {
         this.action.teleported(executionSupport, from, to);
      }
   }

   @Override
   public void clearOnce() {
      super.clearOnce();
      if (this.delayAfter) {
         this.clearDelay();
      } else {
         this.prepareDelay();
      }
   }

   @Override
   public int componentCount() {
      return this.action != null ? 1 : 0;
   }

   @Nullable
   @Override
   public IAnnotatedComponent getComponent(int index) {
      if (index >= this.componentCount()) {
         throw new IndexOutOfBoundsException();
      } else {
         return this.action;
      }
   }
}
