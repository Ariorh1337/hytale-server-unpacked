package com.hypixel.hytale.protocol;

public final class ProtocolSettings {
   public static final int PROTOCOL_CRC = 1608127164;
   public static final int PROTOCOL_VERSION = 2;
   public static final int PROTOCOL_BUILD_NUMBER = 63;
   public static final int PACKET_COUNT = 289;
   public static final int STRUCT_COUNT = 369;
   public static final int ENUM_COUNT = 146;
   public static final int MAX_PACKET_SIZE = 1677721600;

   private ProtocolSettings() {
   }

   public static boolean validateCrc(int crc) {
      return 1608127164 == crc;
   }
}
