package com.hypixel.hytale.server.npc.role.support;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.instructions.Instruction;
import com.hypixel.hytale.server.npc.movement.controllers.MotionController;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MotionContextSupport implements Component<EntityStore> {
   @Nullable
   private MotionController activeMotionController;
   @Nullable
   private Instruction nextBodyMotionStep;
   @Nullable
   private Instruction nextHeadMotionStep;

   @Nonnull
   public static ComponentType<EntityStore, MotionContextSupport> getComponentType() {
      return NPCPlugin.get().getMotionContextSupportComponentType();
   }

   public void setActiveMotionController(@Nullable MotionController activeMotionController) {
      this.activeMotionController = activeMotionController;
   }

   @Nullable
   public MotionController getActiveMotionController() {
      return this.activeMotionController;
   }

   @Nullable
   public Instruction getNextBodyMotionStep() {
      return this.nextBodyMotionStep;
   }

   public boolean setNextBodyMotionStep(@Nonnull Instruction step) {
      if (this.nextBodyMotionStep != null) {
         return false;
      }

      this.nextBodyMotionStep = step;
      return true;
   }

   public void clearNextBodyMotionStep() {
      this.nextBodyMotionStep = null;
   }

   @Nullable
   public Instruction getNextHeadMotionStep() {
      return this.nextHeadMotionStep;
   }

   public boolean setNextHeadMotionStep(@Nonnull Instruction step) {
      if (this.nextHeadMotionStep != null) {
         return false;
      }

      this.nextHeadMotionStep = step;
      return true;
   }

   public void clearNextHeadMotionStep() {
      this.nextHeadMotionStep = null;
   }

   @Override
   public Component<EntityStore> clone() {
      return this;
   }
}
