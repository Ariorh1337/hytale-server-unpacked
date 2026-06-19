package com.hypixel.hytale.protocol;

public final class ProtocolSettings {
   public static final int PROTOCOL_CRC = -1007251642;
   public static final int PROTOCOL_VERSION = 3;
   public static final int PROTOCOL_BUILD_NUMBER = 117;
   public static final int PACKET_COUNT = 328;
   public static final int STRUCT_COUNT = 390;
   public static final int ENUM_COUNT = 158;
   public static final int MAX_PACKET_SIZE = 1677721600;

   private ProtocolSettings() {
   }

   public static boolean validateCrc(int crc) {
      return -1007251642 == crc;
   }
}
