package com.hypixel.hytale.protocol;

public final class ProtocolSettings {
   public static final int PROTOCOL_CRC = 1230336123;
   public static final int PROTOCOL_VERSION = 2;
   public static final int PROTOCOL_BUILD_NUMBER = 53;
   public static final int PACKET_COUNT = 282;
   public static final int STRUCT_COUNT = 364;
   public static final int ENUM_COUNT = 142;
   public static final int MAX_PACKET_SIZE = 1677721600;

   private ProtocolSettings() {
   }

   public static boolean validateCrc(int crc) {
      return 1230336123 == crc;
   }
}
