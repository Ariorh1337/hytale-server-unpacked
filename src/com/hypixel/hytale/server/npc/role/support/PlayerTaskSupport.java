package com.hypixel.hytale.server.npc.role.support;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlayerTaskSupport implements Component<EntityStore> {
   @Nullable
   private List<String> targetPlayerActiveTasks;

   @Nonnull
   public static ComponentType<EntityStore, PlayerTaskSupport> getComponentType() {
      return NPCPlugin.get().getPlayerTaskSupportComponentType();
   }

   public void addTargetPlayerActiveTask(@Nonnull String task) {
      if (this.targetPlayerActiveTasks == null) {
         this.targetPlayerActiveTasks = new ObjectArrayList<>();
      }

      this.targetPlayerActiveTasks.add(task);
   }

   public void clearTargetPlayerActiveTasks() {
      if (this.targetPlayerActiveTasks != null) {
         this.targetPlayerActiveTasks.clear();
      }
   }

   @Nullable
   public List<String> getTargetPlayerActiveTasks() {
      return this.targetPlayerActiveTasks;
   }

   @Override
   public Component<EntityStore> clone() {
      return this;
   }
}
