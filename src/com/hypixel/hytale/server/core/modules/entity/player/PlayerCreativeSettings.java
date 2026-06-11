package com.hypixel.hytale.server.core.modules.entity.player;

import javax.annotation.Nonnull;

public record PlayerCreativeSettings(
   boolean allowNPCDetection,
   boolean respondToHit,
   @Nonnull String placeMode,
   int creativeInteractionDistance,
   boolean showBuilderToolsNotifications,
   boolean noPhysics
) {
   public PlayerCreativeSettings() {
      this(false, false, "default", 10, true, false);
   }
}
