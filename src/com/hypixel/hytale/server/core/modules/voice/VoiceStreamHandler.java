package com.hypixel.hytale.server.core.modules.voice;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.packets.stream.StreamType;
import com.hypixel.hytale.protocol.packets.voice.VoiceData;
import com.hypixel.hytale.server.core.io.PacketHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class VoiceStreamHandler extends SimpleChannelInboundHandler<Packet> {
   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
   private final PacketHandler packetHandler;

   public VoiceStreamHandler(@Nonnull PacketHandler packetHandler) {
      this.packetHandler = packetHandler;
   }

   @Override
   public void handlerAdded(@Nonnull ChannelHandlerContext ctx) throws Exception {
      this.packetHandler.setChannel(StreamType.Voice, ctx.channel());
      LOGGER.at(Level.INFO).log("Voice stream opened for %s", this.packetHandler.getIdentifier());
      super.handlerAdded(ctx);
   }

   protected void channelRead0(@Nonnull ChannelHandlerContext ctx, @Nonnull Packet packet) {
      if (packet instanceof VoiceData voiceData) {
         LOGGER.at(Level.FINE)
            .log(
               "Voice data from %s (seq=%d, size=%d)",
               this.packetHandler.getIdentifier(),
               voiceData.sequenceNumber,
               voiceData.opusData != null ? voiceData.opusData.length : 0
            );
      } else {
         LOGGER.at(Level.WARNING).log("Unexpected packet %s on voice stream from %s", packet.getClass().getSimpleName(), this.packetHandler.getIdentifier());
      }
   }

   @Override
   public void channelInactive(@Nonnull ChannelHandlerContext ctx) throws Exception {
      this.packetHandler.setChannel(StreamType.Voice, null);
      LOGGER.at(Level.INFO).log("Voice stream closed for %s", this.packetHandler.getIdentifier());
      super.channelInactive(ctx);
   }

   @Override
   public void exceptionCaught(@Nonnull ChannelHandlerContext ctx, @Nonnull Throwable cause) {
      LOGGER.at(Level.WARNING).withCause(cause).log("Exception in voice stream for %s", this.packetHandler.getIdentifier());
      ctx.close();
   }
}
