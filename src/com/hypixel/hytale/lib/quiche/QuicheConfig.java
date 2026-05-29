package com.hypixel.hytale.lib.quiche;

import java.time.Duration;
import javax.annotation.Nonnull;

public record QuicheConfig(
   @Nonnull Duration idleTimeout, @Nonnull Duration initialPacketTimeout, @Nonnull Duration auxStreamPendingTimeout, @Nonnull QuicheConfig.RateLimit rateLimit
) {
   public record RateLimit(int burstCapacity, int packetsPerSecond) {
      public static final QuicheConfig.RateLimit DISABLED = new QuicheConfig.RateLimit(0, 0);

      public boolean isEnabled() {
         return this.packetsPerSecond > 0 && this.burstCapacity > 0;
      }
   }
}
