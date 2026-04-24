package org.bson;

public enum BsonBinarySubType {
   BINARY((byte)0),
   FUNCTION((byte)1),
   OLD_BINARY((byte)2),
   UUID_LEGACY((byte)3),
   UUID_STANDARD((byte)4),
   MD5((byte)5),
   ENCRYPTED((byte)6),
   COLUMN((byte)7),
   SENSITIVE((byte)8),
   VECTOR((byte)9),
   USER_DEFINED((byte)-128);

   private final byte value;

   public static boolean isUuid(byte value) {
      return value == UUID_LEGACY.getValue() || value == UUID_STANDARD.getValue();
   }

   BsonBinarySubType(byte value) {
      this.value = value;
   }

   public byte getValue() {
      return this.value;
   }
}
