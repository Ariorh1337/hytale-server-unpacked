package com.hypixel.hytale.lib.quiche;

final class TokenBucket {
   private final int maxTokens;
   private final int tokensPerSecond;
   private int tokens;
   private long lastRefillNs;

   TokenBucket(int maxTokens, int tokensPerSecond, long nowNs) {
      this.maxTokens = maxTokens;
      this.tokensPerSecond = tokensPerSecond;
      this.tokens = maxTokens;
      this.lastRefillNs = nowNs;
   }

   boolean tryConsume(long nowNs) {
      long elapsedNs = nowNs - this.lastRefillNs;
      if (elapsedNs > 0L) {
         long toAdd = elapsedNs * this.tokensPerSecond / 1000000000L;
         if (toAdd > 0L) {
            this.tokens = (int)Math.min(this.maxTokens, this.tokens + toAdd);
            this.lastRefillNs = this.lastRefillNs + toAdd * 1000000000L / this.tokensPerSecond;
         }
      }

      if (this.tokens <= 0) {
         return false;
      }

      this.tokens--;
      return true;
   }
}
