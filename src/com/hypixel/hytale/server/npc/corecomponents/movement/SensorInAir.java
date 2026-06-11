package com.hypixel.hytale.server.npc.corecomponents.movement;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.corecomponents.SensorBase;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import javax.annotation.Nonnull;

public class SensorInAir extends SensorBase {
   public SensorInAir(@Nonnull BuilderSensorBase builderSensorBase) {
      super(builderSensorBase);
   }

   @Override
   public boolean matches(@Nonnull Ref<EntityStore> ref, @Nonnull ExecutionSupport executionSupport, double dt, @Nonnull Store<EntityStore> store) {
      return super.matches(ref, executionSupport, dt, store) && executionSupport.getMotionContextSupport().getActiveMotionController().inAir();
   }

   @Override
   public InfoProvider getSensorInfo() {
      return null;
   }
}
