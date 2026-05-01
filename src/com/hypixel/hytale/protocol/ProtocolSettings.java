package com.hypixel.hytale.protocol;

public final class ProtocolSettings {
   public static final int PROTOCOL_CRC = 315434396;
   public static final int PROTOCOL_VERSION = 2;
   public static final int PROTOCOL_BUILD_NUMBER = 84;
   public static final int PACKET_COUNT = 314;
   public static final int STRUCT_COUNT = 380;
   public static final int ENUM_COUNT = 150;
   public static final int MAX_PACKET_SIZE = 1677721600;

   private ProtocolSettings() {
   }

   public static boolean validateCrc(int crc) {
      return 315434396 == crc;
   }
}
