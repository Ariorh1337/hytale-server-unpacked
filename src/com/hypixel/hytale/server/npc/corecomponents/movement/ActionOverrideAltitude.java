package com.hypixel.hytale.server.npc.corecomponents.movement;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.corecomponents.movement.builders.BuilderActionOverrideAltitude;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.movement.controllers.MotionControllerFly;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ActionOverrideAltitude extends ActionBase {
   private final double[] desiredRange;

   public ActionOverrideAltitude(@Nonnull BuilderActionOverrideAltitude builder, @Nonnull BuilderSupport support) {
      super(builder);
      this.desiredRange = builder.getDesiredAltitudeRange(support);
   }

   @Override
   public boolean canExecute(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull ExecutionSupport executionSupport,
      @Nullable InfoProvider sensorInfo,
      double dt,
      @Nonnull Store<EntityStore> store
   ) {
      return super.canExecute(ref, executionSupport, sensorInfo, dt, store)
         && "Fly".equals(executionSupport.getMotionContextSupport().getActiveMotionController().getType());
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
      ((MotionControllerFly)executionSupport.getMotionContextSupport().getActiveMotionController()).setDesiredAltitudeOverride(this.desiredRange);
      return true;
   }
}
