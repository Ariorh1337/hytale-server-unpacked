package com.hypixel.hytale.server.core.io.transport;

import com.hypixel.hytale.lib.quiche.QuicheConfig;
import com.hypixel.hytale.lib.quiche.QuicheListener;
import com.hypixel.hytale.lib.quiche.QuicheServerCredentials;
import com.hypixel.hytale.protocol.io.ServerListener;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.HytaleServerConfig;
import com.hypixel.hytale.server.core.auth.ServerAuthManager;
import com.hypixel.hytale.server.core.config.RateLimitConfig;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.io.handlers.InitialPacketHandler;
import com.hypixel.hytale.server.core.io.stream.PendingStreamConnectionHandler;
import com.hypixel.hytale.sneakythrow.SneakyThrow;
import java.net.Inet6Address;
import java.net.InetSocketAddress;
import java.net.StandardProtocolFamily;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class QuicheTransport implements Transport {
   private final QuicheServerCredentials credentials = QuicheServerCredentials.generateSelfSigned();

   public QuicheTransport() {
      ServerAuthManager.getInstance().setServerCertificate(this.credentials.certificate());
   }

   @Override
   public TransportType getType() {
      return TransportType.QUICHE;
   }

   @Override
   public Future<ServerListener> bind(InetSocketAddress address) throws InterruptedException {
      return CompletableFuture.supplyAsync(
         SneakyThrow.sneakySupplier(
            () -> {
               StandardProtocolFamily family = address.getAddress() instanceof Inet6Address ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET;
               HytaleServerConfig serverConfig = HytaleServer.get().getConfig();
               HytaleServerConfig.TimeoutProfile timeouts = serverConfig.getConnectionTimeouts();
               RateLimitConfig rateLimitCfg = serverConfig.getRateLimitConfig();
               QuicheConfig.RateLimit rateLimit = rateLimitCfg.isEnabled()
                  ? new QuicheConfig.RateLimit(rateLimitCfg.getBurstCapacity(), rateLimitCfg.getPacketsPerSecond())
                  : QuicheConfig.RateLimit.DISABLED;
               QuicheConfig quicheConfig = new QuicheConfig(timeouts.getInitial(), timeouts.getInitial(), timeouts.getAuxStreamPending(), rateLimit);
               return new QuicheListener(
                  family,
                  address,
                  this.credentials,
                  quicheConfig,
                  InitialPacketHandler::new,
                  (primaryHandler, auxChannel) -> primaryHandler instanceof PacketHandler packetHandler
                     ? new PendingStreamConnectionHandler(packetHandler, auxChannel)
                     : null
               );
            }
         )
      );
   }

   @Override
   public void shutdown() {
   }
}
