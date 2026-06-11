package com.hypixel.hytale.server.npc.instructions;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.movement.controllers.MotionController;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface RoleStateChange {
   default void registerWithSupport(ExecutionSupport executionSupport) {
   }

   default void motionControllerChanged(
      @Nullable Ref<EntityStore> ref,
      @Nonnull NPCEntity npcComponent,
      MotionController motionController,
      @Nullable ComponentAccessor<EntityStore> componentAccessor
   ) {
   }

   default void loaded(ExecutionSupport executionSupport) {
   }

   default void spawned(ExecutionSupport executionSupport) {
   }

   default void unloaded(ExecutionSupport executionSupport) {
   }

   default void removed(ExecutionSupport executionSupport) {
   }

   default void teleported(ExecutionSupport executionSupport, World from, World to) {
   }
}
