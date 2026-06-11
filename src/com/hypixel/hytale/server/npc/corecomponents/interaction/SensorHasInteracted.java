package com.hypixel.hytale.server.npc.corecomponents.interaction;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.corecomponents.SensorBase;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import javax.annotation.Nonnull;

public class SensorHasInteracted extends SensorBase {
   public SensorHasInteracted(@Nonnull BuilderSensorBase builderSensorBase) {
      super(builderSensorBase);
   }

   @Override
   public boolean matches(@Nonnull Ref<EntityStore> ref, @Nonnull ExecutionSupport executionSupport, double dt, @Nonnull Store<EntityStore> store) {
      if (!super.matches(ref, executionSupport, dt, store)) {
         return false;
      }

      Ref<EntityStore> target = executionSupport.getStateSupport().getInteractionIterationTarget();
      if (target == null) {
         return false;
      }

      Archetype<EntityStore> targetArchetype = store.getArchetype(target);
      return targetArchetype.contains(DeathComponent.getComponentType()) ? false : executionSupport.getStateSupport().consumeInteraction(target);
   }

   @Override
   public InfoProvider getSensorInfo() {
      return null;
   }
}
