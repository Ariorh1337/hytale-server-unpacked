package org.bson;

public enum UuidRepresentation {
   UNSPECIFIED,
   STANDARD,
   C_SHARP_LEGACY,
   JAVA_LEGACY,
   PYTHON_LEGACY;

   public BsonBinarySubType getSubtype() {
      switch (this) {
         case STANDARD:
            return BsonBinarySubType.UUID_STANDARD;
         case JAVA_LEGACY:
         case PYTHON_LEGACY:
         case C_SHARP_LEGACY:
            return BsonBinarySubType.UUID_LEGACY;
         default:
            throw new BSONException(String.format("No BsonBinarySubType for %s", this));
      }
   }
}
