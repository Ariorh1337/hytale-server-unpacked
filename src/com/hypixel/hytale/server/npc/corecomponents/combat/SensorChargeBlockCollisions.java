package com.hypixel.hytale.server.npc.corecomponents.combat;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.modules.blockset.BlockSetModule;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.SensorBase;
import com.hypixel.hytale.server.npc.corecomponents.combat.builders.BuilderSensorChargeBlockCollisions;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.instructions.Instruction;
import com.hypixel.hytale.server.npc.movement.controllers.BlockHit;
import com.hypixel.hytale.server.npc.sensorinfo.BlockCollisionProvider;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SensorChargeBlockCollisions extends SensorBase {
   private final BlockCollisionProvider blockCollisionProvider = new BlockCollisionProvider();
   private final List<BlockHit> hits = new ObjectArrayList<>();
   private final int blockFilterSet;
   @Nullable
   private BodyMotionCharge matchingChargeBodyMotion;

   public SensorChargeBlockCollisions(@Nonnull BuilderSensorChargeBlockCollisions builder, @Nonnull BuilderSupport support) {
      super(builder);
      this.blockFilterSet = builder.getBlockFilterSet(support);
   }

   @Override
   public void loaded(ExecutionSupport executionSupport) {
      if (this.parent instanceof Instruction instruction) {
         this.matchingChargeBodyMotion = instruction.findNearestPrecedingBodyMotion(BodyMotionCharge.class);
      }
   }

   @Override
   public boolean matches(@Nonnull Ref<EntityStore> ref, @Nonnull ExecutionSupport executionSupport, double dt, @Nonnull Store<EntityStore> store) {
      this.hits.clear();
      this.blockCollisionProvider.clear();
      if (super.matches(ref, executionSupport, dt, store) && this.matchingChargeBodyMotion != null && this.matchingChargeBodyMotion.getBlockHitCount() != 0) {
         int hitCount = this.matchingChargeBodyMotion.getBlockHitCount();

         for (int i = 0; i < hitCount; i++) {
            BlockHit hit = this.matchingChargeBodyMotion.getBlockHit(i);
            if (this.blockFilterSet == Integer.MIN_VALUE || BlockSetModule.getInstance().blockInSet(this.blockFilterSet, hit.blockTypeId)) {
               this.hits.add(hit);
            }
         }

         return this.blockCollisionProvider.populate(this.hits, this.hits.size());
      } else {
         return false;
      }
   }

   @Override
   public InfoProvider getSensorInfo() {
      return this.blockCollisionProvider;
   }

   @Override
   public void done() {
      super.done();
      this.blockCollisionProvider.clear();
      this.hits.clear();
   }
}
