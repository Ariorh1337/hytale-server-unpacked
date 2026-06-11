package com.hypixel.hytale.server.npc.corecomponents.combat;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.SensorBase;
import com.hypixel.hytale.server.npc.corecomponents.combat.builders.BuilderSensorChargeState;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.instructions.Instruction;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SensorChargeState extends SensorBase {
   private final EnumSet<BodyMotionCharge.ChargeState> states;
   @Nullable
   private BodyMotionCharge matchingChargeBodyMotion;

   public SensorChargeState(@Nonnull BuilderSensorChargeState builder, @Nonnull BuilderSupport support) {
      super(builder);
      this.states = builder.getStates(support);
   }

   @Override
   public void loaded(ExecutionSupport executionSupport) {
      if (this.parent instanceof Instruction instruction) {
         this.matchingChargeBodyMotion = instruction.findNearestPrecedingBodyMotion(BodyMotionCharge.class);
      }
   }

   @Override
   public boolean matches(@Nonnull Ref<EntityStore> ref, @Nonnull ExecutionSupport executionSupport, double dt, @Nonnull Store<EntityStore> store) {
      return !super.matches(ref, executionSupport, dt, store)
         ? false
         : this.matchingChargeBodyMotion != null && this.states.contains(this.matchingChargeBodyMotion.getState());
   }

   @Override
   public InfoProvider getSensorInfo() {
      return null;
   }
}
