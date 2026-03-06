package com.hypixel.hytale.protocol;

public final class ProtocolSettings {
   public static final int PROTOCOL_CRC = -1273927538;
   public static final int PROTOCOL_VERSION = 2;
   public static final int PROTOCOL_BUILD_NUMBER = 45;
   public static final int PACKET_COUNT = 279;
   public static final int STRUCT_COUNT = 343;
   public static final int ENUM_COUNT = 143;
   public static final int MAX_PACKET_SIZE = 1677721600;

   private ProtocolSettings() {
   }

   public static boolean validateCrc(int crc) {
      return -1273927538 == crc;
   }
}
