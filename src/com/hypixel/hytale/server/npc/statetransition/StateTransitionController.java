package com.hypixel.hytale.server.npc.statetransition;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderFactory;
import com.hypixel.hytale.server.npc.asset.builder.BuilderManager;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.StateMappingHelper;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.instructions.ActionList;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.instructions.RoleStateChange;
import com.hypixel.hytale.server.npc.movement.controllers.MotionController;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.hypixel.hytale.server.npc.statetransition.builders.BuilderStateTransition;
import com.hypixel.hytale.server.npc.statetransition.builders.BuilderStateTransitionController;
import com.hypixel.hytale.server.npc.statetransition.builders.BuilderStateTransitionEdges;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StateTransitionController {
   private final Int2ObjectOpenHashMap<StateTransitionController.IActionListHolder> stateTransitionActions = new Int2ObjectOpenHashMap<>();
   @Nullable
   private StateTransitionController.IActionListHolder runningActions;

   public StateTransitionController(@Nonnull BuilderStateTransitionController builder, @Nonnull BuilderSupport support) {
      StateMappingHelper stateHelper = support.getStateHelper();

      for (BuilderStateTransition.StateTransition stateTransitionEntry : builder.getStateTransitionEntries(support)) {
         ActionList actions = stateTransitionEntry.getActions();

         for (BuilderStateTransitionEdges.StateTransitionEdges stateTransition : stateTransitionEntry.getStateTransitionEdges()) {
            int priority = stateTransition.getPriority();
            int[] fromStateIndices = stateTransition.getFromStateIndices() != null ? stateTransition.getFromStateIndices() : stateHelper.getAllMainStates();
            int[] toStateIndices = stateTransition.getToStateIndices() != null ? stateTransition.getToStateIndices() : stateHelper.getAllMainStates();

            for (int fromIndex : fromStateIndices) {
               for (int toIndex : toStateIndices) {
                  if (toIndex != fromIndex) {
                     int combinedValue = indexStateTransitionEdge(fromIndex, toIndex);
                     StateTransitionController.IActionListHolder currentList = this.stateTransitionActions.get(combinedValue);
                     if (currentList == null) {
                        this.stateTransitionActions.put(combinedValue, new StateTransitionController.PrioritisedActionList(priority, actions));
                     } else {
                        StateTransitionController.CompositeActionList compositeActionList;
                        if (currentList instanceof StateTransitionController.CompositeActionList) {
                           compositeActionList = (StateTransitionController.CompositeActionList)currentList;
                        } else {
                           compositeActionList = new StateTransitionController.CompositeActionList((StateTransitionController.PrioritisedActionList)currentList);
                           this.stateTransitionActions.put(combinedValue, compositeActionList);
                        }

                        compositeActionList.addActionList(priority, actions);
                     }
                  }
               }
            }
         }
      }

      this.stateTransitionActions.trim();
   }

   public void registerWithSupport(ExecutionSupport executionSupport) {
      for (StateTransitionController.IActionListHolder actions : this.stateTransitionActions.values()) {
         actions.registerWithSupport(executionSupport);
      }
   }

   public void motionControllerChanged(
      @Nullable Ref<EntityStore> ref,
      @Nonnull NPCEntity npcComponent,
      @Nullable MotionController motionController,
      @Nullable ComponentAccessor<EntityStore> componentAccessor
   ) {
      for (StateTransitionController.IActionListHolder actions : this.stateTransitionActions.values()) {
         actions.motionControllerChanged(ref, npcComponent, motionController, componentAccessor);
      }
   }

   public void loaded(ExecutionSupport executionSupport) {
      for (StateTransitionController.IActionListHolder actions : this.stateTransitionActions.values()) {
         actions.loaded(executionSupport);
      }
   }

   public void spawned(ExecutionSupport executionSupport) {
      for (StateTransitionController.IActionListHolder actions : this.stateTransitionActions.values()) {
         actions.spawned(executionSupport);
      }
   }

   public void unloaded(ExecutionSupport executionSupport) {
      for (StateTransitionController.IActionListHolder actions : this.stateTransitionActions.values()) {
         actions.unloaded(executionSupport);
      }
   }

   public void removed(ExecutionSupport executionSupport) {
      for (StateTransitionController.IActionListHolder actions : this.stateTransitionActions.values()) {
         actions.removed(executionSupport);
      }
   }

   public void teleported(ExecutionSupport executionSupport, World from, World to) {
      for (StateTransitionController.IActionListHolder actions : this.stateTransitionActions.values()) {
         actions.teleported(executionSupport, from, to);
      }
   }

   public void clearOnce() {
      for (StateTransitionController.IActionListHolder actions : this.stateTransitionActions.values()) {
         actions.clearOnce();
      }
   }

   public void initiateStateTransition(int fromState, int toState) {
      this.runningActions = this.stateTransitionActions.get(indexStateTransitionEdge(fromState, toState));
   }

   public boolean isRunningTransitionActions() {
      return this.runningActions != null;
   }

   public boolean runTransitionActions(Ref<EntityStore> ref, ExecutionSupport executionSupport, double dt, Store<EntityStore> store) {
      if (this.runningActions == null) {
         return false;
      } else if (this.runningActions.canExecute(ref, executionSupport, null, dt, store)
         && this.runningActions.execute(ref, executionSupport, null, dt, store)
         && this.runningActions.hasCompletedRun()) {
         this.runningActions.clearOnce();
         this.runningActions = null;
         return false;
      } else {
         return true;
      }
   }

   public static void registerFactories(@Nonnull BuilderManager builderManager) {
      BuilderFactory<StateTransitionController> transitionControllerFactory = new BuilderFactory<>(
         StateTransitionController.class, "Type", BuilderStateTransitionController::new
      );
      builderManager.registerFactory(transitionControllerFactory);
      BuilderFactory<BuilderStateTransition.StateTransition> transitionEntryFactory = new BuilderFactory<>(
         BuilderStateTransition.StateTransition.class, "Type", BuilderStateTransition::new
      );
      builderManager.registerFactory(transitionEntryFactory);
      BuilderFactory<BuilderStateTransitionEdges.StateTransitionEdges> transitionFactory = new BuilderFactory<>(
         BuilderStateTransitionEdges.StateTransitionEdges.class, "Type", BuilderStateTransitionEdges::new
      );
      builderManager.registerFactory(transitionFactory);
   }

   public static int indexStateTransitionEdge(int from, int to) {
      return (from << 16) + to;
   }

   private static class CompositeActionList implements StateTransitionController.IActionListHolder {
      private final List<StateTransitionController.PrioritisedActionList> actionLists = new ObjectArrayList<>();
      private int currentIndex;

      private CompositeActionList(StateTransitionController.PrioritisedActionList initialActionList) {
         this.actionLists.add(initialActionList);
      }

      private void addActionList(int priority, ActionList actionList) {
         for (int i = 0; i < this.actionLists.size(); i++) {
            if (priority > this.actionLists.get(i).priority) {
               this.actionLists.add(i, new StateTransitionController.PrioritisedActionList(priority, actionList));
               return;
            }
         }

         this.actionLists.add(new StateTransitionController.PrioritisedActionList(priority, actionList));
      }

      @Override
      public boolean canExecute(
         @Nonnull Ref<EntityStore> ref,
         @Nonnull ExecutionSupport executionSupport,
         @Nullable InfoProvider sensorInfo,
         double dt,
         @Nonnull Store<EntityStore> store
      ) {
         if (this.currentIndex >= this.actionLists.size()) {
            this.currentIndex = 0;
         }

         return this.actionLists.get(this.currentIndex).actionList.canExecute(ref, executionSupport, sensorInfo, dt, store);
      }

      @Override
      public boolean execute(
         @Nonnull Ref<EntityStore> ref,
         @Nonnull ExecutionSupport executionSupport,
         @Nullable InfoProvider sensorInfo,
         double dt,
         @Nonnull Store<EntityStore> store
      ) {
         StateTransitionController.PrioritisedActionList actionList = this.actionLists.get(this.currentIndex);
         if (!actionList.actionList.canExecute(ref, executionSupport, sensorInfo, dt, store)) {
            return false;
         } else if (actionList.actionList.execute(ref, executionSupport, sensorInfo, dt, store) && actionList.actionList.hasCompletedRun()) {
            this.currentIndex++;
            return true;
         } else {
            return false;
         }
      }

      @Override
      public boolean hasCompletedRun() {
         if (this.currentIndex >= this.actionLists.size()) {
            this.currentIndex = 0;
            return true;
         } else {
            return false;
         }
      }

      @Override
      public void registerWithSupport(ExecutionSupport executionSupport) {
         for (StateTransitionController.PrioritisedActionList actionList : this.actionLists) {
            actionList.actionList.registerWithSupport(executionSupport);
         }
      }

      @Override
      public void motionControllerChanged(
         @Nullable Ref<EntityStore> ref,
         @Nonnull NPCEntity npcComponent,
         MotionController motionController,
         @Nullable ComponentAccessor<EntityStore> componentAccessor
      ) {
         for (StateTransitionController.PrioritisedActionList actionList : this.actionLists) {
            actionList.actionList.motionControllerChanged(ref, npcComponent, motionController, componentAccessor);
         }
      }

      @Override
      public void loaded(ExecutionSupport executionSupport) {
         for (StateTransitionController.PrioritisedActionList actionList : this.actionLists) {
            actionList.actionList.loaded(executionSupport);
         }
      }

      @Override
      public void spawned(ExecutionSupport executionSupport) {
         for (StateTransitionController.PrioritisedActionList actionList : this.actionLists) {
            actionList.actionList.spawned(executionSupport);
         }
      }

      @Override
      public void unloaded(ExecutionSupport executionSupport) {
         for (StateTransitionController.PrioritisedActionList actionList : this.actionLists) {
            actionList.actionList.unloaded(executionSupport);
         }
      }

      @Override
      public void removed(ExecutionSupport executionSupport) {
         for (StateTransitionController.PrioritisedActionList actionList : this.actionLists) {
            actionList.actionList.removed(executionSupport);
         }
      }

      @Override
      public void teleported(ExecutionSupport executionSupport, World from, World to) {
         for (StateTransitionController.PrioritisedActionList actionList : this.actionLists) {
            actionList.actionList.teleported(executionSupport, from, to);
         }
      }

      @Override
      public void clearOnce() {
         for (StateTransitionController.PrioritisedActionList actionList : this.actionLists) {
            actionList.actionList.clearOnce();
         }
      }
   }

   private interface IActionListHolder extends RoleStateChange {
      boolean canExecute(Ref<EntityStore> var1, ExecutionSupport var2, InfoProvider var3, double var4, Store<EntityStore> var6);

      boolean execute(Ref<EntityStore> var1, ExecutionSupport var2, InfoProvider var3, double var4, Store<EntityStore> var6);

      boolean hasCompletedRun();

      void clearOnce();
   }

   private record PrioritisedActionList(int priority, ActionList actionList) implements StateTransitionController.IActionListHolder {
      @Override
      public boolean canExecute(
         @Nonnull Ref<EntityStore> ref,
         @Nonnull ExecutionSupport executionSupport,
         @Nullable InfoProvider sensorInfo,
         double dt,
         @Nonnull Store<EntityStore> store
      ) {
         return this.actionList.canExecute(ref, executionSupport, sensorInfo, dt, store);
      }

      @Override
      public boolean execute(
         @Nonnull Ref<EntityStore> ref,
         @Nonnull ExecutionSupport executionSupport,
         @Nullable InfoProvider sensorInfo,
         double dt,
         @Nonnull Store<EntityStore> store
      ) {
         return this.actionList.execute(ref, executionSupport, sensorInfo, dt, store);
      }

      @Override
      public boolean hasCompletedRun() {
         return this.actionList.hasCompletedRun();
      }

      @Override
      public void registerWithSupport(ExecutionSupport executionSupport) {
         this.actionList.registerWithSupport(executionSupport);
      }

      @Override
      public void motionControllerChanged(
         @Nullable Ref<EntityStore> ref,
         @Nonnull NPCEntity npcComponent,
         MotionController motionController,
         @Nullable ComponentAccessor<EntityStore> componentAccessor
      ) {
         this.actionList.motionControllerChanged(ref, npcComponent, motionController, componentAccessor);
      }

      @Override
      public void loaded(ExecutionSupport executionSupport) {
         this.actionList.loaded(executionSupport);
      }

      @Override
      public void spawned(ExecutionSupport executionSupport) {
         this.actionList.spawned(executionSupport);
      }

      @Override
      public void unloaded(ExecutionSupport executionSupport) {
         this.actionList.unloaded(executionSupport);
      }

      @Override
      public void removed(ExecutionSupport executionSupport) {
         this.actionList.removed(executionSupport);
      }

      @Override
      public void teleported(ExecutionSupport executionSupport, World from, World to) {
         this.actionList.teleported(executionSupport, from, to);
      }

      @Override
      public void clearOnce() {
         this.actionList.clearOnce();
      }
   }
}
