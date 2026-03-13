package com.hypixel.hytale.protocol;

public final class ProtocolSettings {
   public static final int PROTOCOL_CRC = 1097867039;
   public static final int PROTOCOL_VERSION = 2;
   public static final int PROTOCOL_BUILD_NUMBER = 48;
   public static final int PACKET_COUNT = 279;
   public static final int STRUCT_COUNT = 342;
   public static final int ENUM_COUNT = 142;
   public static final int MAX_PACKET_SIZE = 1677721600;

   private ProtocolSettings() {
   }

   public static boolean validateCrc(int crc) {
      return 1097867039 == crc;
   }
}
