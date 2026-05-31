package com.hypixel.hytale.server.core.io.transport;

import com.hypixel.hytale.protocol.io.ServerListener;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

public interface Transport {
   TransportType getType();

   CompletableFuture<ServerListener> bind(InetSocketAddress var1);

   void shutdown();
}
