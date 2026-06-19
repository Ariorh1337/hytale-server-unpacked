package com.hypixel.hytale.protocol.packets.buildertools;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum ColorToolMode {
   Coloring(0),
   Gradient(1),
   Shading(2);

   public static final ColorToolMode[] VALUES = values();
   private final int value;

   ColorToolMode(int value) {
      this.value = value;
   }

   public int getValue() {
      return this.value;
   }

   public static ColorToolMode fromValue(int value) {
      if (value >= 0 && value < VALUES.length) {
         return VALUES[value];
      } else {
         throw ProtocolException.invalidEnumValue("ColorToolMode", value);
      }
   }
}
