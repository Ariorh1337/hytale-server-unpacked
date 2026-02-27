package com.hypixel.hytale.server.core.modules.voice;

import com.hypixel.hytale.common.plugin.PluginManifest;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.stream.StreamType;
import com.hypixel.hytale.server.core.io.stream.StreamManager;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import io.netty.handler.codec.quic.QuicStreamPriority;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class VoiceModule extends JavaPlugin {
   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
   public static final PluginManifest MANIFEST = PluginManifest.corePlugin(VoiceModule.class).build();

   public VoiceModule(@Nonnull JavaPluginInit init) {
      super(init);
   }

   @Override
   protected void setup() {
      StreamManager.getInstance().registerHandler(StreamType.Voice, VoiceStreamHandler::new, new QuicStreamPriority(127, true));
      LOGGER.at(Level.INFO).log("Voice stream handler registered");
   }
}
