package org.rocksdb;

public enum CompactionPriority {
   ByCompensatedSize((byte)0),
   OldestLargestSeqFirst((byte)1),
   OldestSmallestSeqFirst((byte)2),
   MinOverlappingRatio((byte)3),
   RoundRobin((byte)4);

   private final byte value;

   CompactionPriority(byte var3) {
      this.value = var3;
   }

   public byte getValue() {
      return this.value;
   }

   public static CompactionPriority getCompactionPriority(byte var0) {
      for (CompactionPriority var4 : values()) {
         if (var4.getValue() == var0) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Illegal value provided for CompactionPriority.");
   }
}
