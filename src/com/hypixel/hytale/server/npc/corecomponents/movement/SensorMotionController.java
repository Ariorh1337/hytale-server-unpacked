package com.hypixel.hytale.server.npc.corecomponents.movement;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.corecomponents.SensorBase;
import com.hypixel.hytale.server.npc.corecomponents.movement.builders.BuilderSensorMotionController;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import javax.annotation.Nonnull;

public class SensorMotionController extends SensorBase {
   protected final String motionControllerName;

   public SensorMotionController(@Nonnull BuilderSensorMotionController builderSensorMotionController) {
      super(builderSensorMotionController);
      this.motionControllerName = builderSensorMotionController.getMotionControllerName();
   }

   @Override
   public boolean matches(@Nonnull Ref<EntityStore> ref, @Nonnull ExecutionSupport executionSupport, double dt, @Nonnull Store<EntityStore> store) {
      return super.matches(ref, executionSupport, dt, store)
         && this.motionControllerName.equalsIgnoreCase(executionSupport.getMotionContextSupport().getActiveMotionController().getType());
   }

   @Override
   public InfoProvider getSensorInfo() {
      return null;
   }
}
