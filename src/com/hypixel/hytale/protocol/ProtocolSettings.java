package com.hypixel.hytale.protocol;

public final class ProtocolSettings {
   public static final int PROTOCOL_CRC = -1022205618;
   public static final int PROTOCOL_VERSION = 2;
   public static final int PROTOCOL_BUILD_NUMBER = 77;
   public static final int PACKET_COUNT = 313;
   public static final int STRUCT_COUNT = 378;
   public static final int ENUM_COUNT = 150;
   public static final int MAX_PACKET_SIZE = 1677721600;

   private ProtocolSettings() {
   }

   public static boolean validateCrc(int crc) {
      return -1022205618 == crc;
   }
}
