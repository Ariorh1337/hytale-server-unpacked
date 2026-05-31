package com.hypixel.hytale.server.core.io;

import com.hypixel.hytale.common.plugin.PluginManifest;
import com.hypixel.hytale.common.util.CompletableFutureUtil;
import com.hypixel.hytale.common.util.FormatUtil;
import com.hypixel.hytale.common.util.NetworkUtil;
import com.hypixel.hytale.logger.sentry.SkipSentryException;
import com.hypixel.hytale.protocol.io.ServerListener;
import com.hypixel.hytale.server.core.Constants;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Options;
import com.hypixel.hytale.server.core.event.events.ShutdownEvent;
import com.hypixel.hytale.server.core.io.commands.BindingsCommand;
import com.hypixel.hytale.server.core.io.handlers.IPacketHandler;
import com.hypixel.hytale.server.core.io.handlers.SubPacketHandler;
import com.hypixel.hytale.server.core.io.handlers.game.GamePacketHandler;
import com.hypixel.hytale.server.core.io.handlers.game.InventoryPacketHandler;
import com.hypixel.hytale.server.core.io.transport.QUICTransport;
import com.hypixel.hytale.server.core.io.transport.QuicheTransport;
import com.hypixel.hytale.server.core.io.transport.Transport;
import com.hypixel.hytale.server.core.io.transport.TransportType;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.sneakythrow.SneakyThrow;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ServerManager extends JavaPlugin {
   public static final PluginManifest MANIFEST = PluginManifest.corePlugin(ServerManager.class).build();
   @Nonnull
   private static final NetworkUtil.AddressType[] NON_PUBLIC_ADDRESS_TYPES = new NetworkUtil.AddressType[]{
      NetworkUtil.AddressType.ANY_LOCAL,
      NetworkUtil.AddressType.LOOPBACK,
      NetworkUtil.AddressType.SITE_LOCAL,
      NetworkUtil.AddressType.LINK_LOCAL,
      NetworkUtil.AddressType.MULTICAST
   };
   private static ServerManager instance;
   @Nonnull
   private final List<ServerListener> listeners = new CopyOnWriteArrayList<>();
   @Nonnull
   private final List<Function<IPacketHandler, SubPacketHandler>> subPacketHandlers = new ObjectArrayList<>();
   @Nullable
   private Transport transport;
   @Nullable
   private CompletableFuture<Void> registerFuture;
   @Nullable
   private CompletableFuture<Void> bootFuture;

   public static ServerManager get() {
      return instance;
   }

   public ServerManager(@Nonnull JavaPluginInit init) {
      super(init);
      instance = this;
      if (!Options.isBare()) {
         this.init();
      }
   }

   public void init() {
      this.registerFuture = CompletableFutureUtil._catch(CompletableFuture.runAsync(SneakyThrow.sneakyRunnable(() -> {
         long start = System.nanoTime();

         this.transport = switch ((TransportType)Options.getOptionSet().valuesOf(Options.TRANSPORT).getFirst()) {
            case QUIC -> new QUICTransport();
            case QUICHE -> new QuicheTransport();
         };
         this.getLogger().at(Level.INFO).log("Took %s to setup transport!", FormatUtil.nanosToString(System.nanoTime() - start));
         this.registerFuture = null;
      })));
   }

   @Override
   protected void setup() {
      this.getEventRegistry().register((short)-40, ShutdownEvent.class, event -> this.unbindAllListeners());
      get().registerSubPacketHandlers(InventoryPacketHandler::new);
      this.getCommandRegistry().registerCommand(new BindingsCommand());
   }

   @Override
   protected void start() {
      this.bootFuture = CompletableFuture.<CompletableFuture[]>supplyAsync(() -> {
         CompletableFuture<Void> registerFuture = this.registerFuture;
         if (registerFuture != null) {
            registerFuture.getNow(null);
         }

         if (HytaleServer.get().isShuttingDown()) {
            return null;
         }

         ReferenceArrayList<CompletableFuture<?>> futures = new ReferenceArrayList<>();
         if (Options.getOptionSet().has(Options.MIGRATIONS) || Options.isBare()) {
            return futures.toArray(CompletableFuture[]::new);
         }

         if (Constants.SINGLEPLAYER) {
            try {
               InetAddress[] localhosts = InetAddress.getAllByName("localhost");

               for (InetAddress localhost : localhosts) {
                  futures.add(this.bind(new InetSocketAddress(localhost, Options.getOptionSet().valueOf(Options.BIND).getPort())));
               }

               return futures.toArray(CompletableFuture[]::new);
            } catch (UnknownHostException e) {
               throw SneakyThrow.sneakyThrow(e);
            }
         } else {
            for (InetSocketAddress address : Options.getOptionSet().valuesOf(Options.BIND)) {
               futures.add(this.bind(address));
            }

            return futures.toArray(CompletableFuture[]::new);
         }
      }).thenCompose(v -> v == null ? CompletableFuture.completedFuture(null) : CompletableFuture.allOf(v)).thenRun(() -> {
         if (this.listeners.isEmpty()) {
            throw new IllegalArgumentException("Listeners is empty after starting ServerManager!!");
         }

         this.bootFuture = null;
      });
   }

   @Override
   protected void shutdown() {
      Universe.get().disconnectAllPLayers();
      this.unbindAllListeners();
      this.transport.shutdown();
      this.transport = null;
      this.getLogger().at(Level.INFO).log("Finished shutting down ServerManager...");
   }

   public void unbindAllListeners() {
      for (ServerListener channel : this.listeners) {
         this.unbind0(channel);
      }

      this.listeners.clear();
   }

   @Nonnull
   public List<ServerListener> getListeners() {
      return Collections.unmodifiableList(this.listeners);
   }

   public CompletableFuture<Boolean> bind(@Nonnull InetSocketAddress address) {
      if (address.getAddress().isAnyLocalAddress()) {
         CompletableFuture<Boolean> channelIpv6 = this.bind0(new InetSocketAddress(NetworkUtil.ANY_IPV6_ADDRESS, address.getPort())).thenApply(c -> {
            if (c != null) {
               this.listeners.add(c);
            }

            return c != null;
         });
         CompletableFuture<Boolean> channelIpv4 = this.bind0(new InetSocketAddress(NetworkUtil.ANY_IPV4_ADDRESS, address.getPort())).thenApply(c -> {
            if (c != null) {
               this.listeners.add(c);
            }

            return c != null;
         });
         CompletableFuture<Boolean> channelIpv6Localhost = this.bind0(new InetSocketAddress(NetworkUtil.LOOPBACK_IPV6_ADDRESS, address.getPort()))
            .thenApply(c -> {
               if (c != null) {
                  this.listeners.add(c);
               }

               return c != null;
            });
         return CompletableFuture.allOf(channelIpv6Localhost, channelIpv4, channelIpv6).thenApply(var2x -> channelIpv4.join() || channelIpv6.join());
      } else {
         return this.bind0(address).thenApply(c -> {
            if (c != null) {
               this.listeners.add(c);
            }

            return c != null;
         });
      }
   }

   public boolean unbind(@Nonnull ServerListener channel) {
      boolean success = this.unbind0(channel);
      if (success) {
         this.listeners.remove(channel);
      }

      return success;
   }

   @Nullable
   public InetSocketAddress getLocalOrPublicAddress() throws SocketException {
      for (ServerListener channel : this.listeners) {
         if (channel.localAddress() instanceof InetSocketAddress inetSocketAddress) {
            InetAddress address = inetSocketAddress.getAddress();
            if (address.isLoopbackAddress()) {
               return inetSocketAddress;
            }

            if (address.isAnyLocalAddress()) {
               InetAddress anyNonLoopbackAddress = NetworkUtil.getFirstNonLoopbackAddress();
               if (anyNonLoopbackAddress == null) {
                  return null;
               }

               return new InetSocketAddress(anyNonLoopbackAddress, inetSocketAddress.getPort());
            }

            return inetSocketAddress;
         }
      }

      return null;
   }

   @Nullable
   public InetSocketAddress getNonLoopbackAddress() throws SocketException {
      for (ServerListener channel : this.listeners) {
         if (channel.localAddress() instanceof InetSocketAddress inetSocketAddress) {
            InetAddress address = inetSocketAddress.getAddress();
            if (!address.isLoopbackAddress()) {
               if (address.isAnyLocalAddress()) {
                  InetAddress anyNonLoopbackAddress = NetworkUtil.getFirstNonLoopbackAddress();
                  if (anyNonLoopbackAddress == null) {
                     return null;
                  }

                  return new InetSocketAddress(anyNonLoopbackAddress, inetSocketAddress.getPort());
               }

               return inetSocketAddress;
            }
         }
      }

      return null;
   }

   @Nullable
   public InetSocketAddress getPublicAddress() throws SocketException {
      for (ServerListener channel : this.listeners) {
         if (channel.localAddress() instanceof InetSocketAddress inetSocketAddress) {
            InetAddress address = inetSocketAddress.getAddress();
            if (!address.isLoopbackAddress() && !address.isSiteLocalAddress()) {
               if (address.isAnyLocalAddress()) {
                  InetAddress anyPublicAddress = NetworkUtil.getFirstAddressWithout(NON_PUBLIC_ADDRESS_TYPES);
                  if (anyPublicAddress == null) {
                     return null;
                  }

                  return new InetSocketAddress(anyPublicAddress, inetSocketAddress.getPort());
               }

               return inetSocketAddress;
            }
         }
      }

      return null;
   }

   public void waitForBindComplete() {
      CompletableFuture<Void> future = this.bootFuture;
      if (future != null) {
         future.getNow(null);
      }
   }

   public void registerSubPacketHandlers(@Nonnull Function<IPacketHandler, SubPacketHandler> supplier) {
      this.subPacketHandlers.add(supplier);
   }

   public void populateSubPacketHandlers(@Nonnull GamePacketHandler packetHandler) {
      for (Function<IPacketHandler, SubPacketHandler> subPacketHandler : this.subPacketHandlers) {
         packetHandler.registerSubPacketHandler(subPacketHandler.apply(packetHandler));
      }
   }

   private CompletableFuture<ServerListener> bind0(@Nonnull InetSocketAddress address) {
      long start = System.nanoTime();
      this.getLogger().at(Level.FINE).log("Binding to %s (%s)", address, this.transport.getType());
      return this.transport.bind(address).thenApply(channel -> {
         this.getLogger().at(Level.INFO).log("Listening on %s and took %s", channel.localAddress(), FormatUtil.nanosToString(System.nanoTime() - start));
         return (ServerListener)channel;
      }).exceptionally(t -> {
         this.getLogger().at(Level.SEVERE).withCause(new SkipSentryException(t)).log("Failed to bind to %s", address);
         return null;
      });
   }

   private boolean unbind0(@Nonnull ServerListener channel) {
      long start = System.nanoTime();
      this.getLogger().at(Level.FINE).log("Closing listener %s", channel);

      try {
         channel.close().get(1L, TimeUnit.SECONDS);
         this.getLogger().at(Level.INFO).log("Closed listener %s and took %s", channel, FormatUtil.nanosToString(System.nanoTime() - start));
         return true;
      } catch (InterruptedException e) {
         this.getLogger().at(Level.SEVERE).withCause(e).log("Failed to await for listener to close!");
         Thread.currentThread().interrupt();
         return false;
      } catch (ExecutionException | TimeoutException e) {
         this.getLogger().at(Level.SEVERE).withCause(e).log("Failed to await for listener to close!");
         return false;
      }
   }
}
