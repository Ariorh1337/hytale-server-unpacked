package com.hypixel.hytale.protocol;

public final class ProtocolSettings {
   public static final int PROTOCOL_CRC = -773805964;
   public static final int PROTOCOL_VERSION = 2;
   public static final int PROTOCOL_BUILD_NUMBER = 97;
   public static final int PACKET_COUNT = 322;
   public static final int STRUCT_COUNT = 386;
   public static final int ENUM_COUNT = 154;
   public static final int MAX_PACKET_SIZE = 1677721600;

   private ProtocolSettings() {
   }

   public static boolean validateCrc(int crc) {
      return -773805964 == crc;
   }
}
