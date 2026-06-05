package com.hypixel.hytale.server.core.universe.world.events;

import com.hypixel.hytale.server.core.universe.world.World;
import javax.annotation.Nonnull;

public class WorldGenChunksClearedEvent extends WorldEvent {
   public WorldGenChunksClearedEvent(@Nonnull World world) {
      super(world);
   }

   @Nonnull
   @Override
   public String toString() {
      return "WorldGenChunksClearedEvent{} " + super.toString();
   }
}
