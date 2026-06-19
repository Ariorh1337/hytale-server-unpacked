package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum BreakShapeOrientation {
   View(0),
   Surface(1);

   public static final BreakShapeOrientation[] VALUES = values();
   private final int value;

   BreakShapeOrientation(int value) {
      this.value = value;
   }

   public int getValue() {
      return this.value;
   }

   public static BreakShapeOrientation fromValue(int value) {
      if (value >= 0 && value < VALUES.length) {
         return VALUES[value];
      } else {
         throw ProtocolException.invalidEnumValue("BreakShapeOrientation", value);
      }
   }
}
