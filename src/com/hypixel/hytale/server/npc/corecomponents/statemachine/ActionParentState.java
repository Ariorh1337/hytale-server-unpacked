package com.hypixel.hytale.server.npc.corecomponents.statemachine;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.StatePair;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.corecomponents.statemachine.builders.BuilderActionParentState;
import com.hypixel.hytale.server.npc.decisionmaker.stateevaluator.StateEvaluator;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.role.support.StateSupport;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ActionParentState extends ActionBase {
   protected final int state;
   protected final int subState;
   protected final boolean clearHeadMotion;
   protected final boolean clearBodyMotion;

   public ActionParentState(@Nonnull BuilderActionParentState builderActionState, @Nonnull BuilderSupport support) {
      super(builderActionState);
      StatePair statePair = builderActionState.getStatePair(support);
      this.state = statePair.getState();
      this.subState = statePair.getSubState();
      this.clearHeadMotion = builderActionState.isClearHeadMotion();
      this.clearBodyMotion = builderActionState.isClearBodyMotion();
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
      StateEvaluator stateEvaluatorComponent = store.getComponent(ref, StateEvaluator.getComponentType());
      if (stateEvaluatorComponent == null || !stateEvaluatorComponent.isActive()) {
         StateSupport stateSupport = executionSupport.getStateSupport();
         stateSupport.setClearHeadMotion(this.clearHeadMotion);
         stateSupport.setClearBodyMotion(this.clearBodyMotion);
         stateSupport.setState(this.state, this.subState, true, false);
      }

      return true;
   }
}
