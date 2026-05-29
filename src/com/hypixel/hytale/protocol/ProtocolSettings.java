package com.hypixel.hytale.protocol;

public final class ProtocolSettings {
   public static final int PROTOCOL_CRC = -2125278700;
   public static final int PROTOCOL_VERSION = 3;
   public static final int PROTOCOL_BUILD_NUMBER = 101;
   public static final int PACKET_COUNT = 326;
   public static final int STRUCT_COUNT = 386;
   public static final int ENUM_COUNT = 155;
   public static final int MAX_PACKET_SIZE = 1677721600;

   private ProtocolSettings() {
   }

   public static boolean validateCrc(int crc) {
      return -2125278700 == crc;
   }
}
