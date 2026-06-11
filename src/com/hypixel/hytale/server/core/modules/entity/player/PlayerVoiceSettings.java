package com.hypixel.hytale.server.core.modules.entity.player;

import com.hypixel.hytale.protocol.packets.voice.VoiceInputMode;
import javax.annotation.Nonnull;

public record PlayerVoiceSettings(boolean voiceChatEnabled, boolean voiceInputEnabled, VoiceInputMode voiceInputMode) {
   private static final PlayerVoiceSettings DEFAULTS = new PlayerVoiceSettings();

   public PlayerVoiceSettings() {
      this(false, false, VoiceInputMode.PushToTalk);
   }

   @Nonnull
   public static PlayerVoiceSettings defaults() {
      return DEFAULTS;
   }
}
