package org.rocksdb;

public enum PerfLevel {
   UNINITIALIZED((byte)0),
   DISABLE((byte)1),
   ENABLE_COUNT((byte)2),
   ENABLE_TIME_EXCEPT_FOR_MUTEX((byte)3),
   ENABLE_TIME_AND_CPU_TIME_EXCEPT_FOR_MUTEX((byte)4),
   ENABLE_TIME((byte)5),
   @Deprecated
   OUT_OF_BOUNDS((byte)6);

   private final byte _value;

   PerfLevel(byte nullxx) {
      this._value = nullxx;
   }

   public byte getValue() {
      return this._value;
   }

   public static PerfLevel getPerfLevel(byte var0) {
      for (PerfLevel var4 : values()) {
         if (var4.getValue() == var0) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Uknknown PerfLevel constant : " + var0);
   }
}
