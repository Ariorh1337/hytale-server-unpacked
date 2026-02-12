package org.rocksdb;

public enum LoggerType {
   JAVA_IMPLEMENTATION((byte)1),
   STDERR_IMPLEMENTATION((byte)2);

   private final byte value;

   LoggerType(byte var3) {
      this.value = var3;
   }

   byte getValue() {
      return this.value;
   }

   static LoggerType getLoggerType(byte var0) {
      for (LoggerType var4 : values()) {
         if (var4.getValue() == var0) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Illegal value provided for LoggerType.");
   }
}
