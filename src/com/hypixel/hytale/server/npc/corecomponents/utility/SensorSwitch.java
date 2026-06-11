package com.hypixel.hytale.server.npc.corecomponents.utility;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.SensorBase;
import com.hypixel.hytale.server.npc.corecomponents.utility.builders.BuilderSensorSwitch;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import javax.annotation.Nonnull;

public class SensorSwitch extends SensorBase {
   protected final boolean flag;

   public SensorSwitch(@Nonnull BuilderSensorSwitch builderSensorSwitch, @Nonnull BuilderSupport builderSupport) {
      super(builderSensorSwitch);
      this.flag = builderSensorSwitch.getSwitch(builderSupport);
   }

   @Override
   public InfoProvider getSensorInfo() {
      return null;
   }

   @Override
   public boolean matches(@Nonnull Ref<EntityStore> ref, @Nonnull ExecutionSupport executionSupport, double dt, @Nonnull Store<EntityStore> store) {
      return super.matches(ref, executionSupport, dt, store) && this.flag;
   }
}
