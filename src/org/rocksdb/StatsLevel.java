package org.rocksdb;

public enum StatsLevel {
   EXCEPT_DETAILED_TIMERS((byte)0),
   EXCEPT_TIME_FOR_MUTEX((byte)1),
   ALL((byte)2);

   private final byte value;

   StatsLevel(byte var3) {
      this.value = var3;
   }

   public byte getValue() {
      return this.value;
   }

   public static StatsLevel getStatsLevel(byte var0) {
      for (StatsLevel var4 : values()) {
         if (var4.getValue() == var0) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Illegal value provided for StatsLevel.");
   }
}
