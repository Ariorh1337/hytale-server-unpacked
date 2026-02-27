package com.hypixel.hytale.protocol;

public final class ProtocolSettings {
   public static final int PROTOCOL_CRC = 1367909235;
   public static final int PROTOCOL_VERSION = 2;
   public static final int PROTOCOL_BUILD_NUMBER = 35;
   public static final int PACKET_COUNT = 277;
   public static final int STRUCT_COUNT = 341;
   public static final int ENUM_COUNT = 144;
   public static final int MAX_PACKET_SIZE = 1677721600;

   private ProtocolSettings() {
   }

   public static boolean validateCrc(int crc) {
      return 1367909235 == crc;
   }
}
