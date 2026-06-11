package com.hypixel.hytale.server.npc.role.support;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FlagsComponent implements Component<EntityStore> {
   @Nullable
   private boolean[] flags;

   @Nonnull
   public static ComponentType<EntityStore, FlagsComponent> getComponentType() {
      return NPCPlugin.get().getFlagsComponentType();
   }

   public void setFlags(@Nullable boolean[] flags) {
      this.flags = flags;
   }

   public void setFlag(int index, boolean value) {
      if (this.flags == null) {
         throw new NullPointerException("Trying to set a flag but flags are null");
      }

      if (index >= 0 && index < this.flags.length) {
         this.flags[index] = value;
      } else {
         throw new IllegalArgumentException(String.format("Flag index must be in [0, %s). Got %s", this.flags.length, index));
      }
   }

   public boolean isFlagSet(int index) {
      return this.flags != null && index >= 0 && index < this.flags.length ? this.flags[index] : false;
   }

   @Override
   public Component<EntityStore> clone() {
      return this;
   }
}
