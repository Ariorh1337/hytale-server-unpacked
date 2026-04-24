package org.bson.json;

import java.util.BitSet;

final class UuidStringValidator {
   private static final BitSet HEX_CHARS = new BitSet(103);

   private static void validateFourHexChars(String str, int startPos) {
      if (!HEX_CHARS.get(str.charAt(startPos))
         || !HEX_CHARS.get(str.charAt(startPos + 1))
         || !HEX_CHARS.get(str.charAt(startPos + 2))
         || !HEX_CHARS.get(str.charAt(startPos + 3))) {
         throw new IllegalArgumentException(String.format("Expected four hexadecimal characters in UUID string \"%s\" starting at position %d", str, startPos));
      }
   }

   private static void validateDash(String str, int pos) {
      if (str.charAt(pos) != '-') {
         throw new IllegalArgumentException(String.format("Expected dash in UUID string \"%s\" at position %d", str, pos));
      }
   }

   static void validate(String uuidString) {
      if (uuidString.length() != 36) {
         throw new IllegalArgumentException(String.format("UUID string \"%s\" must be 36 characters", uuidString));
      }

      validateFourHexChars(uuidString, 0);
      validateFourHexChars(uuidString, 4);
      validateDash(uuidString, 8);
      validateFourHexChars(uuidString, 9);
      validateDash(uuidString, 13);
      validateFourHexChars(uuidString, 14);
      validateDash(uuidString, 18);
      validateFourHexChars(uuidString, 19);
      validateDash(uuidString, 23);
      validateFourHexChars(uuidString, 24);
      validateFourHexChars(uuidString, 28);
      validateFourHexChars(uuidString, 32);
   }

   private UuidStringValidator() {
   }

   static {
      HEX_CHARS.set(48, 58);
      HEX_CHARS.set(65, 71);
      HEX_CHARS.set(97, 103);
   }
}
