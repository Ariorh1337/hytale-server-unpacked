package org.rocksdb;

public enum InfoLogLevel {
   DEBUG_LEVEL((byte)0),
   INFO_LEVEL((byte)1),
   WARN_LEVEL((byte)2),
   ERROR_LEVEL((byte)3),
   FATAL_LEVEL((byte)4),
   HEADER_LEVEL((byte)5),
   NUM_INFO_LOG_LEVELS((byte)6);

   private final byte value_;

   InfoLogLevel(final byte nullxx) {
      this.value_ = nullxx;
   }

   public byte getValue() {
      return this.value_;
   }

   public static InfoLogLevel getInfoLogLevel(byte var0) {
      for (InfoLogLevel var4 : values()) {
         if (var4.getValue() == var0) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Illegal value provided for InfoLogLevel.");
   }
}
