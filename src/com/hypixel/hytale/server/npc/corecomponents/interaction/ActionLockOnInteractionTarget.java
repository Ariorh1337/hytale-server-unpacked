package com.hypixel.hytale.server.npc.corecomponents.interaction;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.corecomponents.interaction.builders.BuilderActionLockOnInteractionTarget;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ActionLockOnInteractionTarget extends ActionBase {
   protected final int targetSlot;

   public ActionLockOnInteractionTarget(@Nonnull BuilderActionLockOnInteractionTarget builderActionBase, @Nonnull BuilderSupport support) {
      super(builderActionBase);
      this.targetSlot = builderActionBase.getTargetSlot(support);
   }

   @Override
   public boolean canExecute(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull ExecutionSupport executionSupport,
      @Nullable InfoProvider sensorInfo,
      double dt,
      @Nonnull Store<EntityStore> store
   ) {
      return super.canExecute(ref, executionSupport, sensorInfo, dt, store) && executionSupport.getStateSupport().getInteractionIterationTarget() != null;
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
      Ref<EntityStore> target = executionSupport.getStateSupport().getInteractionIterationTarget();
      executionSupport.getMarkedEntitySupport().setMarkedEntity(this.targetSlot, target);
      return true;
   }
}
