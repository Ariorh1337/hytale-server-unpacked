package com.hypixel.hytale.server.flock.corecomponents;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.flock.FlockPlugin;
import com.hypixel.hytale.server.flock.corecomponents.builders.BuilderActionFlockSetTarget;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ActionFlockSetTarget extends ActionBase {
   protected final boolean clear;
   protected final String targetSlot;

   public ActionFlockSetTarget(@Nonnull BuilderActionFlockSetTarget builderActionFlockSetTarget, @Nonnull BuilderSupport support) {
      super(builderActionFlockSetTarget);
      this.clear = builderActionFlockSetTarget.isClear();
      this.targetSlot = builderActionFlockSetTarget.getTargetSlot(support);
   }

   @Override
   public boolean canExecute(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull ExecutionSupport executionSupport,
      @Nullable InfoProvider sensorInfo,
      double dt,
      @Nonnull Store<EntityStore> store
   ) {
      if (!super.canExecute(ref, executionSupport, sensorInfo, dt, store) || !FlockPlugin.isFlockMember(ref, store)) {
         return false;
      }

      if (this.clear) {
         return true;
      }

      Ref<EntityStore> target = sensorInfo != null && sensorInfo.hasPosition() ? sensorInfo.getPositionProvider().getTarget() : null;
      return target != null;
   }

   @Override
   public boolean execute(
      @Nonnull Ref<EntityStore> ref, @Nonnull ExecutionSupport executionSupport, @Nonnull InfoProvider sensorInfo, double dt, @Nonnull Store<EntityStore> store
   ) {
      super.execute(ref, executionSupport, sensorInfo, dt, store);
      if (this.clear) {
         executionSupport.getMarkedEntitySupport().flockSetTarget(ref, this.targetSlot, null, store);
         return true;
      } else {
         Ref<EntityStore> targetRef = sensorInfo.getPositionProvider().getTarget();
         executionSupport.getMarkedEntitySupport().flockSetTarget(ref, this.targetSlot, targetRef, store);
         return true;
      }
   }
}
