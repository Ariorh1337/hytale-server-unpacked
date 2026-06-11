package com.hypixel.hytale.server.npc.corecomponents.entity;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.corecomponents.entity.builders.BuilderActionReleaseTarget;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ActionReleaseTarget extends ActionBase {
   protected final int targetSlot;

   public ActionReleaseTarget(@Nonnull BuilderActionReleaseTarget builder, @Nonnull BuilderSupport support) {
      super(builder);
      this.targetSlot = builder.getTargetSlot(support);
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
      executionSupport.getMarkedEntitySupport().clearMarkedEntity(this.targetSlot);
      return true;
   }
}
