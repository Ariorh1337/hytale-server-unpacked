package com.hypixel.hytale.server.npc.corecomponents.utility;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.corecomponents.utility.builders.BuilderActionResetInstructions;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.instructions.IndexedInstructions;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ActionResetInstructions extends ActionBase {
   protected final int[] instructions;

   public ActionResetInstructions(@Nonnull BuilderActionResetInstructions builder, @Nonnull BuilderSupport support) {
      super(builder);
      this.instructions = builder.getInstructions(support);
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
      executionSupport.getEntitySupport().addDeferredAction((var1x, _exec, var3x, var5) -> this.resetInstructions(_exec));
      return true;
   }

   protected boolean resetInstructions(@Nonnull ExecutionSupport executionSupport) {
      IndexedInstructions indexedInstructions = executionSupport.getIndexedInstructions();
      if (indexedInstructions == null) {
         return true;
      }

      if (this.instructions.length == 0) {
         indexedInstructions.resetAll();
         return true;
      }

      for (int instruction : this.instructions) {
         indexedInstructions.reset(instruction);
      }

      return true;
   }
}
