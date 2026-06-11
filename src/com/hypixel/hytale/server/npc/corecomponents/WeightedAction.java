package com.hypixel.hytale.server.npc.corecomponents;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderWeightedAction;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.instructions.Action;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.movement.controllers.MotionController;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.hypixel.hytale.server.npc.util.ComponentInfo;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WeightedAction extends AnnotatedComponentBase implements Action {
   private final Action action;
   private final double weight;

   public WeightedAction(@Nonnull BuilderWeightedAction builder, @Nonnull BuilderSupport support, @Nonnull Action action) {
      this.action = action;
      this.weight = builder.getWeight(support);
   }

   public double getWeight() {
      return this.weight;
   }

   @Override
   public boolean canExecute(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull ExecutionSupport executionSupport,
      @Nullable InfoProvider sensorInfo,
      double dt,
      @Nonnull Store<EntityStore> store
   ) {
      return this.action.canExecute(ref, executionSupport, sensorInfo, dt, store);
   }

   @Override
   public boolean execute(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull ExecutionSupport executionSupport,
      @Nullable InfoProvider sensorInfo,
      double dt,
      @Nonnull Store<EntityStore> store
   ) {
      return this.action.execute(ref, executionSupport, sensorInfo, dt, store);
   }

   @Override
   public void activate(ExecutionSupport executionSupport, InfoProvider infoProvider) {
      this.action.activate(executionSupport, infoProvider);
   }

   @Override
   public void deactivate(ExecutionSupport executionSupport, InfoProvider infoProvider) {
      this.action.deactivate(executionSupport, infoProvider);
   }

   @Override
   public boolean isActivated() {
      return this.action.isActivated();
   }

   @Override
   public void getInfo(ExecutionSupport executionSupport, ComponentInfo holder) {
      this.action.getInfo(executionSupport, holder);
   }

   @Override
   public boolean processDelay(float dt) {
      return this.action.processDelay(dt);
   }

   @Override
   public void clearOnce() {
      this.action.clearOnce();
   }

   @Override
   public void setOnce() {
      this.action.setOnce();
   }

   @Override
   public boolean isTriggered() {
      return this.action.isTriggered();
   }

   @Override
   public void registerWithSupport(ExecutionSupport executionSupport) {
      this.action.registerWithSupport(executionSupport);
   }

   @Override
   public void motionControllerChanged(
      @Nullable Ref<EntityStore> ref,
      @Nonnull NPCEntity npcComponent,
      MotionController motionController,
      @Nullable ComponentAccessor<EntityStore> componentAccessor
   ) {
      this.action.motionControllerChanged(ref, npcComponent, motionController, componentAccessor);
   }

   @Override
   public void loaded(ExecutionSupport executionSupport) {
      this.action.loaded(executionSupport);
   }

   @Override
   public void spawned(ExecutionSupport executionSupport) {
      this.action.spawned(executionSupport);
   }

   @Override
   public void unloaded(ExecutionSupport executionSupport) {
      this.action.unloaded(executionSupport);
   }

   @Override
   public void removed(ExecutionSupport executionSupport) {
      this.action.removed(executionSupport);
   }

   @Override
   public void teleported(ExecutionSupport executionSupport, World from, World to) {
      this.action.teleported(executionSupport, from, to);
   }
}
