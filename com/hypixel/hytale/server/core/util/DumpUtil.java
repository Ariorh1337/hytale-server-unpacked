/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.util;

import com.hypixel.fastutil.longs.Long2ObjectConcurrentHashMap;
import com.hypixel.hytale.common.plugin.PluginManifest;
import com.hypixel.hytale.common.util.FormatUtil;
import com.hypixel.hytale.common.util.StringUtil;
import com.hypixel.hytale.component.ComponentRegistry;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.metric.ArchetypeChunkData;
import com.hypixel.hytale.component.system.ISystem;
import com.hypixel.hytale.logger.backend.HytaleFileHandler;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.metrics.InitStackThread;
import com.hypixel.hytale.metrics.MetricsRegistry;
import com.hypixel.hytale.metrics.metric.HistoricMetric;
import com.hypixel.hytale.plugin.early.ClassTransformer;
import com.hypixel.hytale.plugin.early.EarlyPluginLoader;
import com.hypixel.hytale.protocol.io.PacketStatsRecorder;
import com.hypixel.hytale.protocol.packets.connection.PongType;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.HytaleServerConfig;
import com.hypixel.hytale.server.core.ShutdownReason;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.CameraManager;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.PluginBase;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.universe.world.storage.provider.IndexedStorageChunkStorageProvider;
import com.hypixel.hytale.server.core.universe.world.worldgen.WorldGenTimingsCollector;
import com.hypixel.hytale.server.core.util.BsonUtil;
import com.hypixel.hytale.storage.IndexedStorageFile;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bouncycastle.util.io.TeeOutputStream;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonValue;

public class DumpUtil {
    @Nonnull
    public static Path dumpToJson() throws IOException {
        Map<UUID, BsonDocument> playerComponents = DumpUtil.collectPlayerComponentMetrics();
        BsonDocument bson = HytaleServer.METRICS_REGISTRY.dumpToBson(HytaleServer.get()).asDocument();
        BsonDocument universeBson = Universe.METRICS_REGISTRY.dumpToBson(Universe.get()).asDocument();
        BsonArray playersArray = new BsonArray();
        for (PlayerRef playerRef : Universe.get().getPlayers()) {
            BsonDocument playerBson = PlayerRef.METRICS_REGISTRY.dumpToBson(playerRef).asDocument();
            BsonDocument componentData = playerComponents.get(playerRef.getUuid());
            if (componentData != null) {
                playerBson.putAll(componentData);
            }
            playersArray.add(playerBson);
        }
        universeBson.put("Players", playersArray);
        bson.put("Universe", universeBson);
        BsonArray earlyPluginsArray = new BsonArray();
        for (ClassTransformer transformer : EarlyPluginLoader.getTransformers()) {
            earlyPluginsArray.add(new BsonString(transformer.getClass().getName()));
        }
        bson.put("EarlyPlugins", earlyPluginsArray);
        Path path = MetricsRegistry.createDumpPath(".dump.json");
        Files.writeString(path, (CharSequence)BsonUtil.toJson(bson), new OpenOption[0]);
        return path;
    }

    @Nonnull
    private static Map<UUID, BsonDocument> collectPlayerComponentMetrics() {
        ConcurrentHashMap<UUID, BsonDocument> result = new ConcurrentHashMap<UUID, BsonDocument>();
        Collection<World> worlds = Universe.get().getWorlds().values();
        CompletableFuture[] futures = (CompletableFuture[])worlds.stream().map(world -> CompletableFuture.runAsync(() -> {
            for (PlayerRef playerRef : world.getPlayerRefs()) {
                BsonValue bson = PlayerRef.COMPONENT_METRICS_REGISTRY.dumpToBson(playerRef);
                result.put(playerRef.getUuid(), bson.asDocument());
            }
        }, world).orTimeout(30L, TimeUnit.SECONDS)).toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futures).join();
        return result;
    }

    @Nonnull
    public static Map<UUID, PlayerTextData> collectPlayerTextData() {
        ConcurrentHashMap<UUID, PlayerTextData> result = new ConcurrentHashMap<UUID, PlayerTextData>();
        Collection<World> worlds = Universe.get().getWorlds().values();
        CompletableFuture[] futures = (CompletableFuture[])worlds.stream().map(world -> CompletableFuture.runAsync(() -> {
            for (PlayerRef playerRef : world.getPlayerRefs()) {
                Ref<EntityStore> entityRef = playerRef.getReference();
                if (entityRef == null) continue;
                Store<EntityStore> store = entityRef.getStore();
                MovementStatesComponent ms = store.getComponent(entityRef, MovementStatesComponent.getComponentType());
                MovementManager mm = store.getComponent(entityRef, MovementManager.getComponentType());
                CameraManager cm = store.getComponent(entityRef, CameraManager.getComponentType());
                result.put(playerRef.getUuid(), new PlayerTextData(playerRef.getUuid(), ms != null ? ms.getMovementStates().toString() : null, mm != null ? mm.toString() : null, cm != null ? cm.toString() : null));
            }
        }, world).orTimeout(30L, TimeUnit.SECONDS)).toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futures).join();
        return result;
    }

    @Nonnull
    public static String hexDump(@Nonnull ByteBuf buf) {
        int readerIndex = buf.readerIndex();
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        buf.readerIndex(readerIndex);
        return DumpUtil.hexDump(data);
    }

    @Nonnull
    public static String hexDump(@Nonnull byte[] data) {
        if (data.length == 0) {
            return "[EMPTY ARRAY]";
        }
        return ByteBufUtil.hexDump(data);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    public static Path dump(boolean crash, boolean printToConsole) {
        OutputStream outputStream;
        Path filePath = DumpUtil.createDumpPath(crash, "dump.txt");
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(filePath.toFile());
            outputStream = printToConsole ? new TeeOutputStream(fileOutputStream, System.err) : fileOutputStream;
        }
        catch (IOException e) {
            e.printStackTrace();
            System.err.println();
            System.err.println("FAILED TO GET OUTPUT STREAM FOR " + String.valueOf(filePath));
            System.err.println("FAILED TO GET OUTPUT STREAM FOR " + String.valueOf(filePath));
            System.err.println();
            outputStream = System.err;
        }
        try {
            DumpUtil.write(new PrintWriter((Writer)new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)), true));
        }
        finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                }
                catch (IOException iOException) {}
            }
        }
        return filePath;
    }

    @Nonnull
    public static Path createDumpPath(boolean crash, String ext) {
        Path path = Paths.get("dumps", new String[0]);
        try {
            if (!Files.exists(path, new LinkOption[0])) {
                Files.createDirectories(path, new FileAttribute[0]);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        String name = (crash ? "crash-" : "") + HytaleFileHandler.LOG_FILE_DATE_FORMAT.format(LocalDateTime.now());
        Path filePath = path.resolve(name + "." + ext);
        int i = 0;
        while (Files.exists(filePath, new LinkOption[0])) {
            filePath = path.resolve(name + "_" + i++ + "." + ext);
        }
        return filePath;
    }

    private static void write(@Nonnull PrintWriter writer) {
        int width = 200;
        int height = 20;
        long startNanos = System.nanoTime();
        DumpUtil.section("Summary", () -> {
            Universe universe = Universe.get();
            writer.println("World Count: " + universe.getWorlds().size());
            for (World world : universe.getWorlds().values()) {
                writer.println("- " + world.getName());
                HistoricMetric metrics = world.getBufferedTickLengthMetricSet();
                long[] periodsNanos = metrics.getPeriodsNanos();
                int periodIndex = periodsNanos.length - 1;
                long lastTime = periodsNanos[periodIndex];
                double average = metrics.getAverage(periodIndex);
                long max = metrics.calculateMax(periodIndex);
                long min = metrics.calculateMin(periodIndex);
                String length = FormatUtil.timeUnitToString(lastTime, TimeUnit.NANOSECONDS, true);
                String value = FormatUtil.simpleTimeUnitFormat(min, average, max, TimeUnit.NANOSECONDS, TimeUnit.MILLISECONDS, 3);
                String limit = FormatUtil.simpleTimeUnitFormat(world.getTickStepNanos(), TimeUnit.NANOSECONDS, 3);
                writer.printf("\tTick (%s): %s (Limit: %s)\n", length, value, limit);
                writer.printf("\tPlayer count: %d\n", world.getPlayerCount());
            }
            writer.println("Player count: " + universe.getPlayerCount());
            for (PlayerRef ref : universe.getPlayers()) {
                writer.printf("- %s (%s)\n", ref.getUsername(), ref.getUuid());
                PacketHandler.PingInfo pingInfo = ref.getPacketHandler().getPingInfo(PongType.Raw);
                HistoricMetric pingMetricSet = pingInfo.getPingMetricSet();
                long min = pingMetricSet.calculateMin(1);
                long average = (long)pingMetricSet.getAverage(1);
                long max = pingMetricSet.calculateMax(1);
                writer.println("\tPing(raw) Min: " + FormatUtil.timeUnitToString(min, PacketHandler.PingInfo.TIME_UNIT) + ", Avg: " + FormatUtil.timeUnitToString(average, PacketHandler.PingInfo.TIME_UNIT) + ", Max: " + FormatUtil.timeUnitToString(max, PacketHandler.PingInfo.TIME_UNIT));
            }
        }, writer);
        DumpUtil.section("Server Lifecycle", () -> {
            HytaleServer server = HytaleServer.get();
            writer.println("Boot Timestamp: " + String.valueOf(server.getBoot()));
            writer.println("Boot Start (nanos): " + server.getBootStart());
            writer.println("Booting: " + server.isBooting());
            writer.println("Booted: " + server.isBooted());
            writer.println("Shutting Down: " + server.isShuttingDown());
            ShutdownReason shutdownReason = server.getShutdownReason();
            if (shutdownReason != null) {
                writer.println("Shutdown Reason: " + String.valueOf(shutdownReason));
            }
        }, writer);
        DumpUtil.section("Early Plugins", () -> {
            List<ClassTransformer> transformers = EarlyPluginLoader.getTransformers();
            writer.println("Class Transformer Count: " + transformers.size());
            for (ClassTransformer transformer : transformers) {
                writer.println("- " + transformer.getClass().getName() + " (priority=" + transformer.priority() + ")");
            }
        }, writer);
        DumpUtil.section("Plugins", () -> {
            List<PluginBase> plugins = HytaleServer.get().getPluginManager().getPlugins();
            writer.println("Plugin Count: " + plugins.size());
            for (PluginBase plugin : plugins) {
                JavaPlugin javaPlugin;
                boolean isBuiltin = plugin instanceof JavaPlugin && (javaPlugin = (JavaPlugin)plugin).getClassLoader().isInServerClassPath();
                writer.println("- " + String.valueOf(plugin.getIdentifier()) + (isBuiltin ? " [Builtin]" : " [External]"));
                writer.println("\tType: " + plugin.getType().getDisplayName());
                writer.println("\tState: " + String.valueOf((Object)plugin.getState()));
                writer.println("\tManifest:");
                BsonDocument manifestBson = PluginManifest.CODEC.encode(plugin.getManifest()).asDocument();
                DumpUtil.printIndented(writer, BsonUtil.toJson(manifestBson), "\t\t");
            }
        }, writer);
        DumpUtil.section("Server Config", () -> {
            HytaleServerConfig config = HytaleServer.get().getConfig();
            BsonDocument bson = HytaleServerConfig.CODEC.encode(config).asDocument();
            DumpUtil.printIndented(writer, BsonUtil.toJson(bson), "\t");
        }, writer);
        Map<UUID, PlayerTextData> playerTextData = DumpUtil.collectPlayerTextData();
        DumpUtil.section("Server Info", () -> {
            writer.println("HytaleServer:");
            writer.println("\t- " + String.valueOf(HytaleServer.get()));
            writer.println("\tBooted: " + HytaleServer.get().isBooting());
            writer.println("\tShutting Down: " + HytaleServer.get().isShuttingDown());
            writer.println();
            writer.println("Worlds: ");
            Map<String, World> worlds = Universe.get().getWorlds();
            worlds.forEach((worldName, world) -> {
                writer.println("- " + worldName + ":");
                writer.println("\t" + String.valueOf(world));
                HistoricMetric bufferedDeltaMetricSet = world.getBufferedTickLengthMetricSet();
                long[] periods = bufferedDeltaMetricSet.getPeriodsNanos();
                for (int i = 0; i < periods.length; ++i) {
                    long period = periods[i];
                    String string = FormatUtil.timeUnitToString(period, TimeUnit.NANOSECONDS, true);
                    double d = bufferedDeltaMetricSet.getAverage(i);
                    long l = bufferedDeltaMetricSet.calculateMin(i);
                    long max = bufferedDeltaMetricSet.calculateMax(i);
                    writer.println("\tTick (" + string + "): Min: " + FormatUtil.simpleTimeUnitFormat(l, TimeUnit.NANOSECONDS, 3) + ", Avg: " + FormatUtil.simpleTimeUnitFormat(Math.round(d), TimeUnit.NANOSECONDS, 3) + "ns, Max: " + FormatUtil.simpleTimeUnitFormat(max, TimeUnit.NANOSECONDS, 3));
                    long[] historyTimestamps = bufferedDeltaMetricSet.getTimestamps(i);
                    long[] historyValues = bufferedDeltaMetricSet.getValues(i);
                    StringBuilder sb = new StringBuilder();
                    sb.append("\tTick Graph ").append(string).append(":\n");
                    StringUtil.generateGraph(sb, width, height, startNanos - period, startNanos, 0.0, Math.max(max, (long)world.getTickStepNanos()), value -> FormatUtil.simpleTimeUnitFormat(MathUtil.fastCeil(value), TimeUnit.NANOSECONDS, 3), historyTimestamps.length, ii -> historyTimestamps[ii], ii -> historyValues[ii]);
                    writer.println(sb);
                }
                writer.println("\tPlayers: ");
                for (Player player : world.getPlayers()) {
                    writer.println("\t- " + String.valueOf(player));
                    PlayerTextData playerData = (PlayerTextData)playerTextData.get(player.getUuid());
                    writer.println("\t\tMovement States: " + (playerData != null ? playerData.movementStates() : "N/A"));
                    writer.println("\t\tMovement Manager: " + (playerData != null ? playerData.movementManager() : "N/A"));
                    writer.println("\t\tPage Manager: " + String.valueOf(player.getPageManager()));
                    writer.println("\t\tHud Manager: " + String.valueOf(player.getHudManager()));
                    writer.println("\t\tCamera Manager: " + (playerData != null ? playerData.cameraManager() : "N/A"));
                    writer.println("\t\tChunk Tracker:");
                    for (String string : player.getPlayerRef().getChunkTracker().getLoadedChunksDebug().split("\n")) {
                        writer.println("\t\t\t" + string);
                    }
                    writer.println("\t\tQueued Packets Count: " + player.getPlayerConnection().getQueuedPacketsCount());
                    writer.println("\t\tPing:");
                    for (PongType pongType : PongType.values()) {
                        PacketHandler.PingInfo pingInfo = player.getPlayerConnection().getPingInfo(pongType);
                        writer.println("\t\t- " + pongType.name() + ":");
                        HistoricMetric pingMetricSet = pingInfo.getPingMetricSet();
                        long average = (long)pingMetricSet.getAverage(1);
                        long min = pingMetricSet.calculateMin(1);
                        long max = pingMetricSet.calculateMax(1);
                        writer.println("\t\t\tPing: Min: " + min + ", Avg: " + average + ", Max: " + max);
                        writer.println("\t\t\t      Min: " + FormatUtil.timeUnitToString(min, PacketHandler.PingInfo.TIME_UNIT) + ", Avg: " + FormatUtil.timeUnitToString(average, PacketHandler.PingInfo.TIME_UNIT) + ", Max: " + FormatUtil.timeUnitToString(max, PacketHandler.PingInfo.TIME_UNIT));
                        long[] pingPeriods = pingMetricSet.getPeriodsNanos();
                        for (int i = 0; i < pingPeriods.length; ++i) {
                            long period = pingPeriods[i];
                            long min2 = pingMetricSet.calculateMin(1);
                            long max2 = pingMetricSet.calculateMax(1);
                            long[] historyTimestamps = pingMetricSet.getTimestamps(i);
                            long[] historyValues = pingMetricSet.getValues(i);
                            String historyLengthFormatted = FormatUtil.timeUnitToString(period, TimeUnit.NANOSECONDS, true);
                            StringBuilder sb = new StringBuilder();
                            sb.append("\t\t\tPing Graph ").append(historyLengthFormatted).append(":\n");
                            StringUtil.generateGraph(sb, width, height, startNanos - period, startNanos, min2, max2, value -> FormatUtil.timeUnitToString(MathUtil.fastCeil(value), PacketHandler.PingInfo.TIME_UNIT), historyTimestamps.length, ii -> historyTimestamps[ii], ii -> historyValues[ii]);
                            writer.println(sb);
                        }
                        writer.println("\t\t\tPacket Queue: Min: " + pingInfo.getPacketQueueMetric().getMin() + ", Avg: " + (long)pingInfo.getPacketQueueMetric().getAverage() + ", Max: " + pingInfo.getPacketQueueMetric().getMax());
                    }
                    writer.println();
                    PacketStatsRecorder packetStatsRecorder = player.getPlayerConnection().getPacketStatsRecorder();
                    if (packetStatsRecorder == null) continue;
                    int n = 30;
                    long totalSentCount = 0L;
                    long totalSentUncompressed = 0L;
                    long totalSentWire = 0L;
                    int recentSentCount = 0;
                    long recentSentUncompressed = 0L;
                    long recentSentWire = 0L;
                    writer.println("\t\tPackets Sent:");
                    for (int i = 0; i < 512; ++i) {
                        PacketStatsRecorder.PacketStatsEntry entry = packetStatsRecorder.getEntry(i);
                        if (entry.getSentCount() <= 0) continue;
                        totalSentCount += (long)entry.getSentCount();
                        totalSentUncompressed += entry.getSentUncompressedTotal();
                        totalSentWire += entry.getSentCompressedTotal() > 0L ? entry.getSentCompressedTotal() : entry.getSentUncompressedTotal();
                        writer.println("\t\t\t" + entry.getName() + " (" + i + "):");
                        DumpUtil.printPacketStats(writer, "\t\t\t\t", "Total", entry.getSentCount(), entry.getSentUncompressedTotal(), entry.getSentCompressedTotal(), entry.getSentUncompressedMin(), entry.getSentUncompressedMax(), entry.getSentCompressedMin(), entry.getSentCompressedMax(), entry.getSentUncompressedAvg(), entry.getSentCompressedAvg(), 0);
                        PacketStatsRecorder.RecentStats recent = entry.getSentRecently();
                        if (recent.count() <= 0) continue;
                        recentSentCount += recent.count();
                        recentSentUncompressed += recent.uncompressedTotal();
                        recentSentWire += recent.compressedTotal() > 0L ? recent.compressedTotal() : recent.uncompressedTotal();
                        DumpUtil.printPacketStats(writer, "\t\t\t\t", "Recent", recent.count(), recent.uncompressedTotal(), recent.compressedTotal(), recent.uncompressedMin(), recent.uncompressedMax(), recent.compressedMin(), recent.compressedMax(), (double)recent.uncompressedTotal() / (double)recent.count(), recent.compressedTotal() > 0L ? (double)recent.compressedTotal() / (double)recent.count() : 0.0, n);
                    }
                    writer.println("\t\t\t--- Summary ---");
                    writer.println("\t\t\t\tTotal: " + totalSentCount + " packets, " + FormatUtil.bytesToString(totalSentUncompressed) + " serialized, " + FormatUtil.bytesToString(totalSentWire) + " wire");
                    if (recentSentCount > 0) {
                        writer.println(String.format("\t\t\t\tRecent: %d packets (%.1f/sec), %s serialized, %s wire", recentSentCount, (double)recentSentCount / (double)n, FormatUtil.bytesToString(recentSentUncompressed), FormatUtil.bytesToString(recentSentWire)));
                    }
                    writer.println();
                    long totalRecvCount = 0L;
                    long totalRecvUncompressed = 0L;
                    long totalRecvWire = 0L;
                    int recentRecvCount = 0;
                    long recentRecvUncompressed = 0L;
                    long recentRecvWire = 0L;
                    writer.println("\t\tPackets Received:");
                    for (int i = 0; i < 512; ++i) {
                        PacketStatsRecorder.PacketStatsEntry entry = packetStatsRecorder.getEntry(i);
                        if (entry.getReceivedCount() <= 0) continue;
                        totalRecvCount += (long)entry.getReceivedCount();
                        totalRecvUncompressed += entry.getReceivedUncompressedTotal();
                        totalRecvWire += entry.getReceivedCompressedTotal() > 0L ? entry.getReceivedCompressedTotal() : entry.getReceivedUncompressedTotal();
                        writer.println("\t\t\t" + entry.getName() + " (" + i + "):");
                        DumpUtil.printPacketStats(writer, "\t\t\t\t", "Total", entry.getReceivedCount(), entry.getReceivedUncompressedTotal(), entry.getReceivedCompressedTotal(), entry.getReceivedUncompressedMin(), entry.getReceivedUncompressedMax(), entry.getReceivedCompressedMin(), entry.getReceivedCompressedMax(), entry.getReceivedUncompressedAvg(), entry.getReceivedCompressedAvg(), 0);
                        PacketStatsRecorder.RecentStats recent = entry.getReceivedRecently();
                        if (recent.count() <= 0) continue;
                        recentRecvCount += recent.count();
                        recentRecvUncompressed += recent.uncompressedTotal();
                        recentRecvWire += recent.compressedTotal() > 0L ? recent.compressedTotal() : recent.uncompressedTotal();
                        DumpUtil.printPacketStats(writer, "\t\t\t\t", "Recent", recent.count(), recent.uncompressedTotal(), recent.compressedTotal(), recent.uncompressedMin(), recent.uncompressedMax(), recent.compressedMin(), recent.compressedMax(), (double)recent.uncompressedTotal() / (double)recent.count(), recent.compressedTotal() > 0L ? (double)recent.compressedTotal() / (double)recent.count() : 0.0, n);
                    }
                    writer.println("\t\t\t--- Summary ---");
                    writer.println("\t\t\t\tTotal: " + totalRecvCount + " packets, " + FormatUtil.bytesToString(totalRecvUncompressed) + " serialized, " + FormatUtil.bytesToString(totalRecvWire) + " wire");
                    if (recentRecvCount > 0) {
                        writer.println(String.format("\t\t\t\tRecent: %d packets (%.1f/sec), %s serialized, %s wire", recentRecvCount, (double)recentRecvCount / (double)n, FormatUtil.bytesToString(recentRecvUncompressed), FormatUtil.bytesToString(recentRecvWire)));
                    }
                    writer.println();
                }
                writer.println("\tComponent Stores:");
                try {
                    CompletableFuture.runAsync(() -> {
                        DumpUtil.printComponentStore(writer, width, height, "Chunks", startNanos, world.getChunkStore().getStore());
                        DumpUtil.printComponentStore(writer, width, height, "Entities", startNanos, world.getEntityStore().getStore());
                    }, world).orTimeout(30L, TimeUnit.SECONDS).join();
                }
                catch (CompletionException e) {
                    if (!(e.getCause() instanceof TimeoutException)) {
                        e.printStackTrace();
                        writer.println("\t\tFAILED TO DUMP COMPONENT STORES! EXCEPTION!");
                    }
                    writer.println("\t\tFAILED TO DUMP COMPONENT STORES! TIMEOUT!");
                }
                writer.println();
                writer.println();
                WorldGenTimingsCollector timings = world.getChunkStore().getGenerator().getTimings();
                writer.println("\tWorld Gen Timings: ");
                if (timings != null) {
                    writer.println("\t\tChunk Count: " + timings.getChunkCounter());
                    writer.println("\t\tChunk Time: " + timings.getChunkTime());
                    writer.println("\t\tZone Biome Result Time: " + timings.zoneBiomeResult());
                    writer.println("\t\tPrepare Time: " + timings.prepare());
                    writer.println("\t\tBlock Generation Time: " + timings.blocksGeneration());
                    writer.println("\t\tCave Generation Time: " + timings.caveGeneration());
                    writer.println("\t\tPrefab Generation: " + timings.prefabGeneration());
                    writer.println("\t\tQueue Length: " + timings.getQueueLength());
                    writer.println("\t\tGenerating Count: " + timings.getGeneratingCount());
                } else {
                    writer.println("\t\tNo Timings Data Collected!");
                }
                IndexedStorageChunkStorageProvider.IndexedStorageCache storageCache = world.getChunkStore().getStore().getResource(IndexedStorageChunkStorageProvider.IndexedStorageCache.getResourceType());
                if (storageCache != null) {
                    Long2ObjectConcurrentHashMap<IndexedStorageFile> cache = storageCache.getCache();
                    writer.println();
                    writer.println("\tIndexed Storage Cache:");
                    for (Long2ObjectMap.Entry entry : cache.long2ObjectEntrySet()) {
                        long key = entry.getLongKey();
                        writer.println("\t\t" + ChunkUtil.xOfChunkIndex(key) + ", " + ChunkUtil.zOfChunkIndex(key));
                        IndexedStorageFile storageFile = (IndexedStorageFile)entry.getValue();
                        try {
                            writer.println("\t\t- Size: " + FormatUtil.bytesToString(storageFile.size()));
                        }
                        catch (IOException e) {
                            writer.println("\t\t- Size: ERROR: " + e.getMessage());
                        }
                        writer.println("\t\t- Blob Count: " + storageFile.keys().size());
                        int segmentSize = storageFile.segmentSize();
                        int segmentCount = storageFile.segmentCount();
                        writer.println("\t\t- Segment Size: " + segmentSize);
                        writer.println("\t\t- Segment Count: " + segmentCount);
                        writer.println("\t\t- Segment Used %: " + (double)(segmentCount * 100) / (double)segmentSize + "%");
                        writer.println("\t\t- " + String.valueOf(storageFile));
                    }
                }
            });
            List<PlayerRef> playersNotInWorld = Universe.get().getPlayers().stream().filter(ref -> ref.getReference() == null).toList();
            if (!playersNotInWorld.isEmpty()) {
                writer.println();
                writer.println("Players not in world (" + playersNotInWorld.size() + "):");
                for (PlayerRef ref2 : playersNotInWorld) {
                    writer.println("- " + ref2.getUsername() + " (" + String.valueOf(ref2.getUuid()) + ")");
                    writer.println("\tQueued Packets: " + ref2.getPacketHandler().getQueuedPacketsCount());
                    PacketHandler.PingInfo pingInfo = ref2.getPacketHandler().getPingInfo(PongType.Raw);
                    HistoricMetric pingMetricSet = pingInfo.getPingMetricSet();
                    long min = pingMetricSet.calculateMin(1);
                    long avg = (long)pingMetricSet.getAverage(1);
                    long max = pingMetricSet.calculateMax(1);
                    writer.println("\tPing(raw): Min: " + FormatUtil.timeUnitToString(min, PacketHandler.PingInfo.TIME_UNIT) + ", Avg: " + FormatUtil.timeUnitToString(avg, PacketHandler.PingInfo.TIME_UNIT) + ", Max: " + FormatUtil.timeUnitToString(max, PacketHandler.PingInfo.TIME_UNIT));
                }
            }
        }, writer);
        DumpUtil.section("System info", () -> {
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
            long currentTimeMillis = System.currentTimeMillis();
            writer.println("Start Time: " + String.valueOf(new Date(runtimeMXBean.getStartTime())) + " (" + runtimeMXBean.getStartTime() + "ms)");
            writer.println("Current Time: " + String.valueOf(new Date(currentTimeMillis)) + " (" + currentTimeMillis + "ms)");
            writer.println("Process Uptime: " + FormatUtil.timeUnitToString(runtimeMXBean.getUptime(), TimeUnit.MILLISECONDS) + " (" + runtimeMXBean.getUptime() + "ms)");
            writer.println("Available processors (cores): " + Runtime.getRuntime().availableProcessors() + " - " + operatingSystemMXBean.getAvailableProcessors());
            writer.println("System Load Average: " + operatingSystemMXBean.getSystemLoadAverage());
            writer.println();
            if (operatingSystemMXBean instanceof com.sun.management.OperatingSystemMXBean) {
                com.sun.management.OperatingSystemMXBean sunOSBean = (com.sun.management.OperatingSystemMXBean)operatingSystemMXBean;
                writer.println("Total Physical Memory: " + FormatUtil.bytesToString(sunOSBean.getTotalPhysicalMemorySize()) + " (" + sunOSBean.getTotalPhysicalMemorySize() + " Bytes)");
                writer.println("Free Physical Memory: " + FormatUtil.bytesToString(sunOSBean.getFreePhysicalMemorySize()) + " (" + sunOSBean.getFreePhysicalMemorySize() + " Bytes)");
                writer.println("Total Swap Memory: " + FormatUtil.bytesToString(sunOSBean.getTotalSwapSpaceSize()) + " (" + sunOSBean.getTotalSwapSpaceSize() + " Bytes)");
                writer.println("Free Swap Memory: " + FormatUtil.bytesToString(sunOSBean.getFreeSwapSpaceSize()) + " (" + sunOSBean.getFreeSwapSpaceSize() + " Bytes)");
                writer.println("System CPU Load: " + sunOSBean.getSystemCpuLoad());
                writer.println("Process CPU Load: " + sunOSBean.getProcessCpuLoad());
                writer.println();
            }
            writer.println("Processor Identifier: " + System.getenv("PROCESSOR_IDENTIFIER"));
            writer.println("Processor Architecture: " + System.getenv("PROCESSOR_ARCHITECTURE"));
            writer.println("Processor Architecture W64/32: " + System.getenv("PROCESSOR_ARCHITEW6432"));
            writer.println("Number of Processors: " + System.getenv("NUMBER_OF_PROCESSORS"));
            writer.println();
            writer.println("Runtime Name: " + runtimeMXBean.getName());
            writer.println();
            writer.println("OS Name: " + operatingSystemMXBean.getName());
            writer.println("OS Arch: " + operatingSystemMXBean.getArch());
            writer.println("OS Version: " + operatingSystemMXBean.getVersion());
            writer.println();
            writer.println("Spec Name: " + runtimeMXBean.getSpecName());
            writer.println("Spec Vendor: " + runtimeMXBean.getSpecVendor());
            writer.println("Spec Version: " + runtimeMXBean.getSpecVersion());
            writer.println();
            writer.println("VM Name: " + runtimeMXBean.getVmName());
            writer.println("VM Vendor: " + runtimeMXBean.getVmVendor());
            writer.println("VM Version: " + runtimeMXBean.getVmVersion());
            writer.println();
            writer.println("Management Spec Version: " + runtimeMXBean.getManagementSpecVersion());
            writer.println();
            writer.println("Library Path: " + runtimeMXBean.getLibraryPath());
            try {
                writer.println("Boot ClassPath: " + runtimeMXBean.getBootClassPath());
            }
            catch (UnsupportedOperationException unsupportedOperationException) {
                // empty catch block
            }
            writer.println("ClassPath: " + runtimeMXBean.getClassPath());
            writer.println();
            writer.println("Input Arguments: " + String.valueOf(runtimeMXBean.getInputArguments()));
            writer.println("System Properties: " + String.valueOf(runtimeMXBean.getSystemProperties()));
        }, writer);
        DumpUtil.section("Current process info", () -> {
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            DumpUtil.writeMemoryUsage(writer, "Heap Memory Usage: ", memoryMXBean.getHeapMemoryUsage());
            DumpUtil.writeMemoryUsage(writer, "Non-Heap Memory Usage: ", memoryMXBean.getNonHeapMemoryUsage());
            writer.println("Objects Pending Finalization Count: " + memoryMXBean.getObjectPendingFinalizationCount());
        }, writer);
        DumpUtil.section("Garbage collector", () -> {
            for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
                writer.println("Name: " + garbageCollectorMXBean.getName());
                writer.println("\tMemory Pool Names: " + Arrays.toString(garbageCollectorMXBean.getMemoryPoolNames()));
                writer.println("\tCollection Count: " + garbageCollectorMXBean.getCollectionCount());
                writer.println("\tCollection Time: " + garbageCollectorMXBean.getCollectionTime());
                writer.println();
            }
        }, writer);
        DumpUtil.section("Memory pools", () -> {
            for (MemoryPoolMXBean memoryPoolMXBean : ManagementFactory.getMemoryPoolMXBeans()) {
                writer.println("Name: " + memoryPoolMXBean.getName());
                writer.println("\tType: " + String.valueOf((Object)memoryPoolMXBean.getType()));
                writer.println("\tPeak Usage: " + String.valueOf(memoryPoolMXBean.getPeakUsage()));
                writer.println("\tUsage: " + String.valueOf(memoryPoolMXBean.getUsage()));
                writer.println("\tUsage Threshold Supported: " + memoryPoolMXBean.isUsageThresholdSupported());
                if (memoryPoolMXBean.isUsageThresholdSupported()) {
                    writer.println("\tUsage Threshold: " + memoryPoolMXBean.getUsageThreshold());
                    writer.println("\tUsage Threshold Count: " + memoryPoolMXBean.getUsageThresholdCount());
                    writer.println("\tUsage Threshold Exceeded: " + memoryPoolMXBean.isUsageThresholdExceeded());
                }
                writer.println("\tCollection Usage: " + String.valueOf(memoryPoolMXBean.getCollectionUsage()));
                writer.println("\tCollection Usage Threshold Supported: " + memoryPoolMXBean.isCollectionUsageThresholdSupported());
                if (memoryPoolMXBean.isCollectionUsageThresholdSupported()) {
                    writer.println("\tCollection Usage Threshold: " + memoryPoolMXBean.getCollectionUsageThreshold());
                    writer.println("\tCollection Usage Threshold Count: " + memoryPoolMXBean.getCollectionUsageThresholdCount());
                    writer.println("\tCollection Usage Threshold Exceeded: " + memoryPoolMXBean.isCollectionUsageThresholdExceeded());
                }
                writer.println();
            }
        }, writer);
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(true, true);
        DumpUtil.section("Threads (Count: " + threadInfos.length + ")", () -> {
            Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
            Long2ObjectOpenHashMap<Thread> threadIdMap = new Long2ObjectOpenHashMap<Thread>();
            for (Thread thread : allStackTraces.keySet()) {
                threadIdMap.put(thread.getId(), thread);
            }
            for (ThreadInfo threadInfo : threadInfos) {
                Thread thread = (Thread)threadIdMap.get(threadInfo.getThreadId());
                if (thread != null) {
                    StackTraceElement[] trace;
                    writer.println("Name: " + thread.getName());
                    writer.println("State: " + String.valueOf((Object)threadInfo.getThreadState()));
                    writer.println("Thread Class: " + String.valueOf(thread.getClass()));
                    writer.println("Thread Group: " + String.valueOf(thread.getThreadGroup()));
                    writer.println("Priority: " + thread.getPriority());
                    writer.println("CPU Time: " + threadMXBean.getThreadCpuTime(threadInfo.getThreadId()));
                    writer.println("Waited Time: " + threadInfo.getWaitedTime());
                    writer.println("Waited Count: " + threadInfo.getWaitedCount());
                    writer.println("Blocked Time: " + threadInfo.getBlockedTime());
                    writer.println("Blocked Count: " + threadInfo.getBlockedCount());
                    writer.println("Lock Name: " + threadInfo.getLockName());
                    writer.println("Lock Owner Id: " + threadInfo.getLockOwnerId());
                    writer.println("Lock Owner Name: " + threadInfo.getLockOwnerName());
                    writer.println("Daemon: " + thread.isDaemon());
                    writer.println("Interrupted: " + thread.isInterrupted());
                    writer.println("Uncaught Exception Handler: " + String.valueOf(thread.getUncaughtExceptionHandler().getClass()));
                    if (thread instanceof InitStackThread) {
                        writer.println("Init Stack: ");
                        for (StackTraceElement traceElement : trace = ((InitStackThread)((Object)thread)).getInitStack()) {
                            writer.println("\tat " + String.valueOf(traceElement));
                        }
                    }
                    writer.println("Current Stack: ");
                    for (StackTraceElement traceElement : trace = allStackTraces.get(thread)) {
                        writer.println("\tat " + String.valueOf(traceElement));
                    }
                } else {
                    writer.println("Failed to find thread!!!");
                }
                writer.println(threadInfo);
            }
        }, writer);
        DumpUtil.section("Security Manager", () -> {
            SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                writer.println("Class: " + securityManager.getClass().getName());
            } else {
                writer.println("No Security Manager found!");
            }
        }, writer);
        DumpUtil.section("Classes", () -> {
            ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
            writer.println("Loaded Class Count: " + classLoadingMXBean.getLoadedClassCount());
            writer.println("Unloaded Class Count: " + classLoadingMXBean.getUnloadedClassCount());
            writer.println("Total Loaded Class Count: " + classLoadingMXBean.getTotalLoadedClassCount());
        }, writer);
        DumpUtil.section("System Classloader", () -> {
            ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
            DumpUtil.writeClassLoader(writer, systemClassLoader);
        }, writer);
        DumpUtil.section("DumpUtil Classloader", () -> {
            ClassLoader systemClassLoader = DumpUtil.class.getClassLoader();
            DumpUtil.writeClassLoader(writer, systemClassLoader);
        }, writer);
    }

    private static void printPacketStats(@Nonnull PrintWriter writer, @Nonnull String indent, @Nonnull String label, int count, long uncompressedTotal, long compressedTotal, long uncompressedMin, long uncompressedMax, long compressedMin, long compressedMax, double uncompressedAvg, double compressedAvg, int recentSeconds) {
        StringBuilder sb = new StringBuilder();
        sb.append(label).append(": ").append(count).append(" packet").append(count != 1 ? "s" : "");
        if (recentSeconds > 0) {
            sb.append(String.format(" (%.1f/sec)", (double)count / (double)recentSeconds));
        }
        sb.append("\n").append(indent).append("  Size: ").append(FormatUtil.bytesToString(uncompressedTotal));
        if (compressedTotal > 0L) {
            sb.append(" -> ").append(FormatUtil.bytesToString(compressedTotal)).append(" wire");
            double ratio = 100.0 * (double)compressedTotal / (double)uncompressedTotal;
            sb.append(String.format(" (%.1f%%)", ratio));
        }
        sb.append("\n").append(indent).append("  Avg: ").append(FormatUtil.bytesToString((long)uncompressedAvg));
        if (compressedAvg > 0.0) {
            sb.append(" -> ").append(FormatUtil.bytesToString((long)compressedAvg)).append(" wire");
        }
        sb.append("\n").append(indent).append("  Range: ").append(FormatUtil.bytesToString(uncompressedMin)).append(" - ").append(FormatUtil.bytesToString(uncompressedMax));
        if (compressedMax > 0L) {
            sb.append(" (wire: ").append(FormatUtil.bytesToString(compressedMin)).append(" - ").append(FormatUtil.bytesToString(compressedMax)).append(")");
        }
        writer.println(indent + String.valueOf(sb));
    }

    private static void printComponentStore(@Nonnull PrintWriter writer, int width, int height, String name, long startNanos, @Nonnull Store<?> componentStore) {
        writer.println("\t- " + name + ":");
        writer.println("\t Archetype Chunk Count: " + componentStore.getArchetypeChunkCount());
        writer.println("\t Entity Count: " + componentStore.getEntityCount());
        ComponentRegistry.Data<?> data = componentStore.getRegistry().getData();
        HistoricMetric[] systemMetrics = componentStore.getSystemMetrics();
        for (int systemIndex = 0; systemIndex < data.getSystemSize(); ++systemIndex) {
            ISystem<?> system = data.getSystem(systemIndex);
            HistoricMetric systemMetric = systemMetrics[systemIndex];
            writer.println("\t\t " + system.getClass().getName());
            writer.println("\t\t " + String.valueOf(system));
            writer.println("\t\t Archetype Chunk Count: " + componentStore.getArchetypeChunkCountFor(systemIndex));
            writer.println("\t\t Entity Count: " + componentStore.getEntityCountFor(systemIndex));
            if (systemMetric == null) continue;
            long[] periods = systemMetric.getPeriodsNanos();
            for (int i = 0; i < periods.length; ++i) {
                long period = periods[i];
                String historyLengthFormatted = FormatUtil.timeUnitToString(period, TimeUnit.NANOSECONDS, true);
                double average = systemMetric.getAverage(i);
                long min = systemMetric.calculateMin(i);
                long max = systemMetric.calculateMax(i);
                writer.println("\t\t\t(" + historyLengthFormatted + "): Min: " + FormatUtil.timeUnitToString(min, TimeUnit.NANOSECONDS) + ", Avg: " + FormatUtil.timeUnitToString((long)average, TimeUnit.NANOSECONDS) + ", Max: " + FormatUtil.timeUnitToString(max, TimeUnit.NANOSECONDS));
                long[] historyTimestamps = systemMetric.getTimestamps(i);
                long[] historyValues = systemMetric.getValues(i);
                StringBuilder sb = new StringBuilder();
                StringUtil.generateGraph(sb, width, height, startNanos - period, startNanos, min, max, value -> FormatUtil.timeUnitToString(MathUtil.fastCeil(value), TimeUnit.NANOSECONDS), historyTimestamps.length, ii -> historyTimestamps[ii], ii -> historyValues[ii]);
                writer.println(sb);
            }
        }
        writer.println("\t\t Archetype Chunks:");
        for (ArchetypeChunkData chunkData : componentStore.collectArchetypeChunkData()) {
            writer.println("\t\t\t- Entities: " + chunkData.getEntityCount() + ", Components: " + Arrays.toString(chunkData.getComponentTypes()));
        }
    }

    private static void section(String name, @Nonnull Runnable runnable, @Nonnull PrintWriter writer) {
        writer.println("**** " + name + " ****");
        try {
            runnable.run();
        }
        catch (Throwable t) {
            new RuntimeException("Failed to get data for section: " + name, t).printStackTrace(writer);
        }
        writer.println();
        writer.println();
    }

    private static void printIndented(@Nonnull PrintWriter writer, @Nonnull String text, @Nonnull String indent) {
        for (String line : text.split("\n")) {
            writer.println(indent + line);
        }
    }

    private static void writeMemoryUsage(@Nonnull PrintWriter writer, String title, @Nonnull MemoryUsage memoryUsage) {
        writer.println(title);
        writer.println("\tInit: " + FormatUtil.bytesToString(memoryUsage.getInit()) + " (" + memoryUsage.getInit() + " Bytes)");
        writer.println("\tUsed: " + FormatUtil.bytesToString(memoryUsage.getUsed()) + " (" + memoryUsage.getUsed() + " Bytes)");
        writer.println("\tCommitted: " + FormatUtil.bytesToString(memoryUsage.getCommitted()) + " (" + memoryUsage.getCommitted() + " Bytes)");
        long max = memoryUsage.getMax();
        if (max > 0L) {
            writer.println("\tMax: " + FormatUtil.bytesToString(max) + " (" + max + " Bytes)");
            long free = max - memoryUsage.getCommitted();
            writer.println("\tFree: " + FormatUtil.bytesToString(free) + " (" + free + " Bytes)");
        }
    }

    private static void writeClassLoader(@Nonnull PrintWriter writer, @Nullable ClassLoader systemClassLoader) {
        if (systemClassLoader != null) {
            writer.println("Class: " + systemClassLoader.getClass().getName());
            while (systemClassLoader.getParent() != null) {
                systemClassLoader = systemClassLoader.getParent();
                writer.println(" - Parent: " + systemClassLoader.getClass().getName());
            }
        } else {
            writer.println("No class loader found!");
        }
    }

    public record PlayerTextData(@Nonnull UUID uuid, @Nullable String movementStates, @Nullable String movementManager, @Nullable String cameraManager) {
    }
}

