package com.hypixel.hytale.server.npc.instructions;

import javax.annotation.Nonnull;

public final class IndexedInstructions {
   @Nonnull
   private final Instruction[] instructions;

   public IndexedInstructions(@Nonnull Instruction[] instructions) {
      this.instructions = instructions;
   }

   public void reset(int slot) {
      this.instructions[slot].reset();
   }

   public void resetAll() {
      for (Instruction instruction : this.instructions) {
         instruction.reset();
      }
   }
}
