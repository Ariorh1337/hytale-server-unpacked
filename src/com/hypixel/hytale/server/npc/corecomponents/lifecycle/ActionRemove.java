package com.hypixel.hytale.server.npc.corecomponents.lifecycle;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.corecomponents.lifecycle.builders.BuilderActionRemove;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ActionRemove extends ActionBase {
   protected final boolean useTarget;

   public ActionRemove(@Nonnull BuilderActionRemove builder, @Nonnull BuilderSupport builderSupport) {
      super(builder);
      this.useTarget = builder.getUseTarget(builderSupport);
   }

   @Override
   public boolean canExecute(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull ExecutionSupport executionSupport,
      @Nullable InfoProvider sensorInfo,
      double dt,
      @Nonnull Store<EntityStore> store
   ) {
      return super.canExecute(ref, executionSupport, sensorInfo, dt, store) && (!this.useTarget || sensorInfo != null && sensorInfo.hasPosition());
   }

   @Override
   public boolean execute(
      @Nonnull Ref<EntityStore> ref, @Nonnull ExecutionSupport executionSupport, @Nonnull InfoProvider sensorInfo, double dt, @Nonnull Store<EntityStore> store
   ) {
      super.execute(ref, executionSupport, sensorInfo, dt, store);
      Ref<EntityStore> targetRef = this.useTarget ? sensorInfo.getPositionProvider().getTarget() : ref;
      if (!store.getArchetype(targetRef).contains(Player.getComponentType())) {
         store.removeEntity(targetRef, RemoveReason.REMOVE);
      }

      return true;
   }
}
