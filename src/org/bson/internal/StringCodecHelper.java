package org.bson.internal;

import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonType;

public final class StringCodecHelper {
   private StringCodecHelper() {
   }

   public static char decodeChar(BsonReader reader) {
      BsonType currentBsonType = reader.getCurrentBsonType();
      if (currentBsonType != BsonType.STRING) {
         throw new BsonInvalidOperationException(String.format("Invalid string type, found: %s", currentBsonType));
      } else {
         String string = reader.readString();
         if (string.length() != 1) {
            throw new BsonInvalidOperationException(
               String.format("Attempting to decode the string '%s' to a character, but its length is not equal to one", string)
            );
         } else {
            return string.charAt(0);
         }
      }
   }
}
