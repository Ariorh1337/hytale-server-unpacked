package com.hypixel.hytale.server.npc.corecomponents.utility;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.corecomponents.utility.builders.BuilderActionSequence;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.instructions.ActionList;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.movement.controllers.MotionController;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.hypixel.hytale.server.npc.util.IAnnotatedComponent;
import com.hypixel.hytale.server.npc.util.IAnnotatedComponentCollection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ActionSequence extends ActionBase implements IAnnotatedComponentCollection {
   @Nonnull
   protected final ActionList actions;

   public ActionSequence(@Nonnull BuilderActionSequence builder, @Nonnull BuilderSupport support) {
      super(builder);
      this.actions = builder.getActionList(support);
   }

   @Override
   public boolean canExecute(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull ExecutionSupport executionSupport,
      @Nullable InfoProvider sensorInfo,
      double dt,
      @Nonnull Store<EntityStore> store
   ) {
      return super.canExecute(ref, executionSupport, sensorInfo, dt, store) && this.actions.canExecute(ref, executionSupport, sensorInfo, dt, store);
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
      return this.actions.execute(ref, executionSupport, sensorInfo, dt, store);
   }

   @Override
   public void registerWithSupport(ExecutionSupport executionSupport) {
      this.actions.registerWithSupport(executionSupport);
   }

   @Override
   public void motionControllerChanged(
      @Nullable Ref<EntityStore> ref,
      @Nonnull NPCEntity npcComponent,
      MotionController motionController,
      @Nullable ComponentAccessor<EntityStore> componentAccessor
   ) {
      this.actions.motionControllerChanged(ref, npcComponent, motionController, componentAccessor);
   }

   @Override
   public void loaded(ExecutionSupport executionSupport) {
      this.actions.loaded(executionSupport);
   }

   @Override
   public void spawned(ExecutionSupport executionSupport) {
      this.actions.spawned(executionSupport);
   }

   @Override
   public void unloaded(ExecutionSupport executionSupport) {
      this.actions.unloaded(executionSupport);
   }

   @Override
   public void removed(ExecutionSupport executionSupport) {
      this.actions.removed(executionSupport);
   }

   @Override
   public void teleported(ExecutionSupport executionSupport, World from, World to) {
      this.actions.teleported(executionSupport, from, to);
   }

   @Override
   public void clearOnce() {
      super.clearOnce();
      this.actions.clearOnce();
   }

   @Override
   public void setOnce() {
      super.setOnce();
      this.actions.setOnce();
   }

   @Override
   public int componentCount() {
      return this.actions.actionCount();
   }

   @Override
   public IAnnotatedComponent getComponent(int index) {
      return this.actions.getComponent(index);
   }

   @Override
   public void setContext(IAnnotatedComponent parent, int index) {
      super.setContext(parent, index);
      this.actions.setContext(parent);
   }
}
