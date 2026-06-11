package com.hypixel.hytale.protocol.packets.voice;

import com.hypixel.hytale.protocol.io.ProtocolException;

public enum VoiceInputMode {
   VoiceActivity(0),
   PushToTalk(1),
   PushToTalkToggle(2);

   public static final VoiceInputMode[] VALUES = values();
   private final int value;

   VoiceInputMode(int value) {
      this.value = value;
   }

   public int getValue() {
      return this.value;
   }

   public static VoiceInputMode fromValue(int value) {
      if (value >= 0 && value < VALUES.length) {
         return VALUES[value];
      } else {
         throw ProtocolException.invalidEnumValue("VoiceInputMode", value);
      }
   }
}
