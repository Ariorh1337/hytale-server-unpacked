package com.hypixel.hytale.lib.quiche;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.io.ChannelConnection;
import com.hypixel.hytale.protocol.io.ConnectionHandler;
import com.hypixel.hytale.protocol.io.ServerListener;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.ValueLayout;
import java.net.InetSocketAddress;
import java.net.ProtocolFamily;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class QuicheListener implements ServerListener {
   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
   private static final int QUICHE_PROTOCOL_VERSION = 1;
   static final int MAX_DATAGRAM_SIZE = 64000;
   private static final int LOCAL_CONN_ID_LEN = 16;
   private static final int UDP_BUFFER_SIZE = 65535;
   private static final int BUFFER_POOL_SIZE = 256;
   private final Arena arena = Arena.ofShared();
   private final ArrayBlockingQueue<QuicheListener.DatagramBuffer> buffers = new ArrayBlockingQueue<>(256);
   private final AtomicBoolean closed = new AtomicBoolean(false);
   private final Thread readerThread;
   private final InetSocketAddress address;
   final DatagramChannel channel;
   private final MemorySegment config;
   final QuicheConfig quicheConfig;
   final Function<ChannelConnection, ConnectionHandler> handler;
   @Nullable
   final BiFunction<ConnectionHandler, ChannelConnection, ConnectionHandler> auxStreamHandler;
   private final X509Certificate serverCertificate;
   final MemorySegment localAddr;
   final int localAddrLen;

   public QuicheListener(
      ProtocolFamily family,
      InetSocketAddress address,
      @Nonnull QuicheServerCredentials credentials,
      @Nonnull QuicheConfig quicheConfig,
      Function<ChannelConnection, ConnectionHandler> handler
   ) throws IOException {
      this(family, address, credentials, quicheConfig, handler, null);
   }

   public QuicheListener(
      ProtocolFamily family,
      InetSocketAddress address,
      @Nonnull QuicheServerCredentials credentials,
      @Nonnull QuicheConfig quicheConfig,
      Function<ChannelConnection, ConnectionHandler> handler,
      @Nullable BiFunction<ConnectionHandler, ChannelConnection, ConnectionHandler> auxStreamHandler
   ) throws IOException {
      this.address = address;
      this.quicheConfig = quicheConfig;
      this.handler = handler;
      this.auxStreamHandler = auxStreamHandler;
      this.serverCertificate = credentials.certificate();

      for (int i = 0; i < this.buffers.remainingCapacity(); i++) {
         MemorySegment memory = this.arena.allocate(65535L);
         this.buffers.add(new QuicheListener.DatagramBuffer(memory, memory.asByteBuffer()));
      }

      this.channel = DatagramChannel.open(family);
      this.channel.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
      this.channel.bind(address);
      this.localAddr = this.arena.allocate(QuicheNative.SOCKADDR_STORAGE_LAYOUT);
      this.localAddrLen = QuicheUtil.toSocketAddrStorage(this.localAddr, (InetSocketAddress)this.channel.getLocalAddress());
      this.config = QuicheNative.configNew(1);

      try {
         initConfig(this.config, credentials, quicheConfig);
      } catch (Exception e) {
         QuicheNative.configFree(this.config);
         throw e;
      }

      this.readerThread = Thread.ofVirtual().name("quiche-listener-" + address).start(this::reader);
   }

   public X509Certificate getServerCertificate() {
      return this.serverCertificate;
   }

   private static void initConfig(MemorySegment config, QuicheServerCredentials credentials, QuicheConfig quicheConfig) {
      try (Arena arena = Arena.ofConfined()) {
         String currentAlpn = "hytale/3";
         String prevAlpn = "hytale/2";
         MemorySegment alpnBytes = buildAlpnWireFormat(arena, currentAlpn, prevAlpn);
         int result = QuicheNative.configSetApplicationProtos(config, alpnBytes, alpnBytes.byteSize());
         if (result < 0) {
            throw new RuntimeException("Failed to set ALPN protocols: " + QuicheNative.QuicheError.fromCode(result));
         }

         MemorySegment certDer = arena.allocateFrom(ValueLayout.JAVA_BYTE, credentials.certificateDer());
         MemorySegment keyDer = arena.allocateFrom(ValueLayout.JAVA_BYTE, credentials.privateKeyDer());
         result = QuicheNative.configLoadCert(config, certDer, certDer.byteSize());
         if (result < 0) {
            throw new RuntimeException("Failed to load certificate: " + QuicheNative.QuicheError.fromCode(result));
         }

         result = QuicheNative.configLoadPrivKey(config, keyDer, keyDer.byteSize());
         if (result < 0) {
            throw new RuntimeException("Failed to load private key: " + QuicheNative.QuicheError.fromCode(result));
         }

         QuicheNative.configVerifyPeerOptional(config);
         QuicheNative.configSetMaxIdleTimeout(config, quicheConfig.idleTimeout().toMillis());
         QuicheNative.configSetMaxRecvUdpPayloadSize(config, 64000L);
         QuicheNative.configSetMaxSendUdpPayloadSize(config, 64000L);
         QuicheNative.configSetInitialMaxData(config, 524288L);
         QuicheNative.configSetInitialMaxStreamDataBidiLocal(config, 131072L);
         QuicheNative.configSetInitialMaxStreamDataBidiRemote(config, 131072L);
         QuicheNative.configSetInitialMaxStreamDataUni(config, 131072L);
         QuicheNative.configSetInitialMaxStreamsBidi(config, 8L);
         MemorySegment ccName = arena.allocateFrom("bbr2_gcongestion", StandardCharsets.US_ASCII);
         int ccResult = QuicheNative.configSetCcAlgorithmName(config, ccName);
         if (ccResult < 0) {
            throw new RuntimeException("Failed to select congestion control: " + QuicheNative.QuicheError.fromCode(ccResult));
         }

         QuicheNative.configDiscoverPmtu(config, true);
      }
   }

   private static MemorySegment buildAlpnWireFormat(SegmentAllocator allocator, String currentAlpn, String prevAlpn) {
      byte[] current = currentAlpn.getBytes(StandardCharsets.UTF_8);
      byte[] prev = prevAlpn.getBytes(StandardCharsets.UTF_8);
      MemorySegment mem = allocator.allocate(1 + current.length + 1 + prev.length);
      mem.set(ValueLayout.JAVA_BYTE, 0L, (byte)current.length);
      MemorySegment.copy(current, 0, mem, ValueLayout.JAVA_BYTE, 1L, current.length);
      mem.set(ValueLayout.JAVA_BYTE, 1 + current.length, (byte)prev.length);
      MemorySegment.copy(prev, 0, mem, ValueLayout.JAVA_BYTE, 1 + current.length + 1, prev.length);
      return mem;
   }

   private void reader() {
      Object2ObjectOpenCustomHashMap<Object, QuicheConnection> connectionMap = new Object2ObjectOpenCustomHashMap<>(new QuicheListener.DcidHash());
      ObjectArrayList<byte[]> connections = new ObjectArrayList<>();
      Thread thread = Thread.currentThread();

      try {
         try (Arena arena = Arena.ofConfined()) {
            MemorySegment type = arena.allocate(QuicheNative.C_BYTE);
            MemorySegment version = arena.allocate(QuicheNative.C_INT);
            MemorySegment scid = arena.allocate(20L);
            MemorySegment scidLen = arena.allocate(QuicheNative.C_SIZE_T);
            MemorySegment dcid = arena.allocate(20L);
            MemorySegment dcidLen = arena.allocate(QuicheNative.C_SIZE_T);
            MemorySegment token = MemorySegment.NULL;
            MemorySegment tokenLen = arena.allocate(QuicheNative.C_SIZE_T);
            tokenLen.fill((byte)0);
            MemorySegment peerAddr = arena.allocate(QuicheNative.SOCKADDR_STORAGE_LAYOUT);

            while (!thread.isInterrupted()) {
               for (int i = 0; i < connections.size(); i++) {
                  byte[] connKey = connections.get(i);
                  QuicheConnection conn = connectionMap.get(connKey);
                  if (!conn.isActive()) {
                     connectionMap.remove(connKey);
                     connections.remove(i);
                     i--;
                  }
               }

               QuicheListener.DatagramBuffer buf;
               try {
                  buf = this.buffers.take();
               } catch (InterruptedException e) {
                  this.readerThread.interrupt();
                  return;
               }

               buf.buffer.position(0);
               buf.buffer.limit(buf.buffer.capacity());
               SocketAddress source = this.channel.receive(buf.buffer);
               buf.buffer.flip();
               QuicheNative.setSizeT(scidLen, 0L, 20L);
               QuicheNative.setSizeT(dcidLen, 0L, 20L);
               QuicheNative.setSizeT(tokenLen, 0L, 0L);
               dcid.fill((byte)0);
               int result = QuicheNative.headerInfo(buf.memory, buf.buffer.limit(), 16L, version, type, scid, scidLen, dcid, dcidLen, token, tokenLen);
               if (result < 0) {
                  this.buffers.add(buf);
               } else {
                  QuicheConnection conn = connectionMap.get(dcid);
                  if (conn == null) {
                     if (type.get(ValueLayout.JAVA_BYTE, 0L) != 1) {
                        this.buffers.add(buf);
                        continue;
                     }

                     byte[] dcidKey = dcid.toArray(ValueLayout.JAVA_BYTE);
                     LOGGER.atInfo().log("New connection: %s", Arrays.toString(dcidKey));
                     int peerAddrLen = QuicheUtil.toSocketAddrStorage(peerAddr, (InetSocketAddress)source);
                     MemorySegment connHandle = QuicheNative.accept(
                        dcid, 20L, MemorySegment.NULL, 0L, this.localAddr, this.localAddrLen, peerAddr, peerAddrLen, this.config
                     );
                     if (connHandle.address() == 0L) {
                        this.buffers.add(buf);
                        continue;
                     }

                     conn = new QuicheConnection(this, dcidKey, connHandle);
                     connectionMap.put(dcidKey, conn);
                     connections.add(dcidKey);
                  }

                  conn.submit(source, buf);
               }
            }
         } catch (ClosedChannelException e) {
            LOGGER.atInfo().log("Reader channel closed, shutting down");
         } catch (IOException e) {
            if (thread.isInterrupted()) {
               LOGGER.atInfo().log("Reader thread interrupted during I/O");
            } else {
               LOGGER.atSevere().log("Fatal I/O error in reader thread: %s", e.getMessage());
            }

            return;
         }
      } finally {
         for (byte[] connKey : connections) {
            QuicheConnection conn = connectionMap.get(connKey);
            if (conn != null && conn.isActive()) {
               conn.processingThread.interrupt();
            }
         }

         for (byte[] connKey : connections) {
            QuicheConnection conn = connectionMap.get(connKey);
            if (conn != null) {
               try {
                  conn.processingThread.join();
               } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
                  break;
               }
            }
         }
      }
   }

   public CompletableFuture<Void> close() {
      return !this.closed.compareAndSet(false, true) ? CompletableFuture.completedFuture(null) : CompletableFuture.runAsync(() -> {
         this.readerThread.interrupt();

         try {
            this.readerThread.join();
         } catch (InterruptedException e) {
            throw new RuntimeException(e);
         }
      }).whenComplete((var1, var2) -> {
         try {
            this.channel.close();
         } catch (IOException e) {
            LOGGER.atWarning().log("Failed to close datagram channel: %s", e.getMessage());
         }

         QuicheNative.configFree(this.config);
         this.arena.close();
      });
   }

   @Override
   public SocketAddress localAddress() {
      return this.address;
   }

   @Override
   public String toString() {
      return "QuicheListener{address=" + this.address + ", channel=" + this.channel + "}";
   }

   void returnBuffer(QuicheListener.DatagramBuffer buffer) {
      this.buffers.add(buffer);
   }

   record DatagramBuffer(MemorySegment memory, ByteBuffer buffer) {
   }

   private static class DcidHash implements Hash.Strategy<Object> {
      @Override
      public int hashCode(Object o) {
         return switch (o) {
            case null -> 0;
            case byte[] b -> {
               int hash = 0;

               for (byte v : b) {
                  hash = 31 * hash + v;
               }

               yield hash;
            }
            case MemorySegment mem -> {
               int hash = 0;

               for (int i = 0; i < mem.byteSize(); i++) {
                  hash = 31 * hash + mem.get(ValueLayout.JAVA_BYTE, i);
               }

               yield hash;
            }
            default -> throw new UnsupportedOperationException();
         };
      }

      @Override
      public boolean equals(Object a, Object b) {
         if (a instanceof byte[] ab) {
            if (b instanceof byte[] bb) {
               return Arrays.equals(ab, bb);
            } else if (b instanceof MemorySegment bm) {
               if (ab.length != bm.byteSize()) {
                  return false;
               }

               for (int i = 0; i < ab.length; i++) {
                  if (ab[i] != bm.get(ValueLayout.JAVA_BYTE, i)) {
                     return false;
                  }
               }

               return true;
            } else {
               return false;
            }
         } else if (!(a instanceof MemorySegment am)) {
            return a == b;
         } else if (b instanceof byte[] bb) {
            if (am.byteSize() != bb.length) {
               return false;
            }

            for (int i = 0; i < bb.length; i++) {
               if (am.get(ValueLayout.JAVA_BYTE, i) != bb[i]) {
                  return false;
               }
            }

            return true;
         } else {
            return !(b instanceof MemorySegment bm) ? false : am.byteSize() == bm.byteSize() && am.mismatch(bm) == -1L;
         }
      }
   }
}
