package com.hypixel.hytale.server.npc.corecomponents.utility;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.corecomponents.MotionBase;
import com.hypixel.hytale.server.npc.corecomponents.utility.builders.BuilderMotionSequence;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.instructions.Motion;
import com.hypixel.hytale.server.npc.movement.Steering;
import com.hypixel.hytale.server.npc.movement.controllers.MotionController;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.hypixel.hytale.server.npc.util.IAnnotatedComponent;
import com.hypixel.hytale.server.npc.util.IAnnotatedComponentCollection;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class MotionSequence<T extends Motion> extends MotionBase implements IAnnotatedComponentCollection {
   protected final boolean looped;
   protected final boolean restartOnActivate;
   protected final T[] steps;
   protected boolean finished;
   protected int index;
   @Nullable
   protected T activeMotion;

   public MotionSequence(@Nonnull BuilderMotionSequence<T> builder, T[] steps) {
      this.restart();
      this.looped = builder.isLooped();
      this.restartOnActivate = builder.isRestartOnActivate();
      this.steps = steps;
   }

   @Override
   public void activate(@Nonnull Ref<EntityStore> ref, @Nonnull ExecutionSupport executionSupport, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
      if (this.restartOnActivate) {
         this.deactivate(ref, executionSupport, componentAccessor);
         this.restart();
      }

      if (!this.finished) {
         this.doActivate(ref, executionSupport, componentAccessor);
      }
   }

   @Override
   public void deactivate(@Nonnull Ref<EntityStore> ref, @Nonnull ExecutionSupport executionSupport, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
      if (this.activeMotion != null) {
         this.activeMotion.deactivate(ref, executionSupport, componentAccessor);
         this.activeMotion = null;
      }
   }

   @Override
   public boolean computeSteering(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull ExecutionSupport executionSupport,
      @Nullable InfoProvider sensorInfo,
      double dt,
      @Nonnull Steering desiredSteering,
      @Nonnull ComponentAccessor<EntityStore> componentAccessor
   ) {
      if (this.finished) {
         desiredSteering.clear();
         return false;
      }

      T currentActiveMotion = this.activeMotion;

      do {
         Objects.requireNonNull(this.activeMotion, "Active motion not set");
         if (this.activeMotion.computeSteering(ref, executionSupport, sensorInfo, dt, desiredSteering, componentAccessor)) {
            return true;
         }

         if (this.index + 1 < this.steps.length) {
            this.activateNext(ref, this.index + 1, executionSupport, componentAccessor);
         } else {
            if (!this.looped) {
               break;
            }

            this.activateNext(ref, 0, executionSupport, componentAccessor);
         }
      } while (this.activeMotion != currentActiveMotion);

      this.deactivate(ref, executionSupport, componentAccessor);
      this.finished = true;
      return false;
   }

   @Override
   public void registerWithSupport(ExecutionSupport executionSupport) {
      for (T step : this.steps) {
         step.registerWithSupport(executionSupport);
      }
   }

   @Override
   public void motionControllerChanged(
      @Nullable Ref<EntityStore> ref,
      @Nonnull NPCEntity npcComponent,
      MotionController motionController,
      @Nullable ComponentAccessor<EntityStore> componentAccessor
   ) {
      for (T step : this.steps) {
         step.motionControllerChanged(ref, npcComponent, motionController, componentAccessor);
      }
   }

   @Override
   public void loaded(ExecutionSupport executionSupport) {
      for (T step : this.steps) {
         step.loaded(executionSupport);
      }
   }

   @Override
   public void spawned(ExecutionSupport executionSupport) {
      for (T step : this.steps) {
         step.spawned(executionSupport);
      }
   }

   @Override
   public void unloaded(ExecutionSupport executionSupport) {
      for (T step : this.steps) {
         step.unloaded(executionSupport);
      }
   }

   @Override
   public void removed(ExecutionSupport executionSupport) {
      for (T step : this.steps) {
         step.removed(executionSupport);
      }
   }

   @Override
   public void teleported(ExecutionSupport executionSupport, World from, World to) {
      for (T step : this.steps) {
         step.teleported(executionSupport, from, to);
      }
   }

   @Override
   public int componentCount() {
      return this.steps.length;
   }

   @Override
   public IAnnotatedComponent getComponent(int index) {
      return this.steps[index];
   }

   @Override
   public void setContext(IAnnotatedComponent parent, int index) {
      for (int i = 0; i < this.steps.length; i++) {
         this.steps[i].setContext(parent, i);
      }
   }

   public void restart() {
      this.index = 0;
      this.finished = false;
   }

   protected void doActivate(
      @Nonnull Ref<EntityStore> ref, @Nonnull ExecutionSupport executionSupport, @Nonnull ComponentAccessor<EntityStore> componentAccessor
   ) {
      if (this.steps.length == 0) {
         throw new IllegalArgumentException("Motion sequence must have steps!");
      }

      if (this.index >= 0 && this.index < this.steps.length) {
         this.activeMotion = this.steps[this.index];
         Objects.requireNonNull(this.activeMotion, "Active motion must not be null");
         this.activeMotion.activate(ref, executionSupport, componentAccessor);
      } else {
         throw new IndexOutOfBoundsException(
            String.format("Motion sequence index out of range (%s) must be less than size (%s)", this.index, this.steps.length)
         );
      }
   }

   protected void activateNext(
      @Nonnull Ref<EntityStore> ref, int newIndex, @Nonnull ExecutionSupport executionSupport, @Nonnull ComponentAccessor<EntityStore> componentAccessor
   ) {
      this.activeMotion.deactivate(ref, executionSupport, componentAccessor);
      this.index = newIndex;
      this.doActivate(ref, executionSupport, componentAccessor);
   }
}
