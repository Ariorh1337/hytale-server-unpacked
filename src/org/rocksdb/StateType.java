package org.rocksdb;

public enum StateType {
   STATE_UNKNOWN((byte)0),
   STATE_MUTEX_WAIT((byte)1);

   private final byte value;

   StateType(final byte nullxx) {
      this.value = nullxx;
   }

   byte getValue() {
      return this.value;
   }

   static StateType fromValue(byte var0) throws IllegalArgumentException {
      for (StateType var4 : values()) {
         if (var4.value == var0) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Unknown value for StateType: " + var0);
   }
}
