package com.hypixel.hytale.server.npc.corecomponents.combat;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.corecomponents.SensorBase;
import com.hypixel.hytale.server.npc.corecomponents.combat.builders.BuilderSensorIsBackingAway;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import javax.annotation.Nonnull;

public class SensorIsBackingAway extends SensorBase {
   public SensorIsBackingAway(@Nonnull BuilderSensorIsBackingAway builder) {
      super(builder);
   }

   @Override
   public boolean matches(@Nonnull Ref<EntityStore> ref, @Nonnull ExecutionSupport executionSupport, double dt, @Nonnull Store<EntityStore> store) {
      return !super.matches(ref, executionSupport, dt, store) ? false : executionSupport.getRole().isBackingAway();
   }

   @Override
   public InfoProvider getSensorInfo() {
      return null;
   }
}
