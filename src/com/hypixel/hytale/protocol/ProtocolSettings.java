package com.hypixel.hytale.protocol;

public final class ProtocolSettings {
   public static final int PROTOCOL_CRC = 701662755;
   public static final int PROTOCOL_VERSION = 2;
   public static final int PROTOCOL_BUILD_NUMBER = 5;
   public static final int PACKET_COUNT = 270;
   public static final int STRUCT_COUNT = 318;
   public static final int ENUM_COUNT = 136;
   public static final int MAX_PACKET_SIZE = 1677721600;

   private ProtocolSettings() {
   }

   public static boolean validateCrc(int crc) {
      return 701662755 == crc;
   }
}
