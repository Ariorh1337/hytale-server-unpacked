package com.hypixel.hytale.protocol;

public final class ProtocolSettings {
   public static final int PROTOCOL_CRC = -1149927892;
   public static final int PROTOCOL_VERSION = 2;
   public static final int PROTOCOL_BUILD_NUMBER = 92;
   public static final int PACKET_COUNT = 319;
   public static final int STRUCT_COUNT = 386;
   public static final int ENUM_COUNT = 153;
   public static final int MAX_PACKET_SIZE = 1677721600;

   private ProtocolSettings() {
   }

   public static boolean validateCrc(int crc) {
      return -1149927892 == crc;
   }
}
