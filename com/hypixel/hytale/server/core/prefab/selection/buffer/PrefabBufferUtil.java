/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.prefab.selection.buffer;

import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.logger.sentry.SkipSentryException;
import com.hypixel.hytale.server.core.Options;
import com.hypixel.hytale.server.core.asset.AssetModule;
import com.hypixel.hytale.server.core.prefab.selection.buffer.BinaryPrefabBufferCodec;
import com.hypixel.hytale.server.core.prefab.selection.buffer.BsonPrefabBufferDeserializer;
import com.hypixel.hytale.server.core.prefab.selection.buffer.UpdateBinaryPrefabException;
import com.hypixel.hytale.server.core.prefab.selection.buffer.impl.IPrefabBuffer;
import com.hypixel.hytale.server.core.prefab.selection.buffer.impl.PrefabBuffer;
import com.hypixel.hytale.server.core.util.BsonUtil;
import com.hypixel.hytale.server.core.util.io.FileUtil;
import com.hypixel.hytale.sneakythrow.SneakyThrow;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.StampedLock;
import java.util.logging.Level;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PrefabBufferUtil {
    public static final Path CACHE_PATH = Options.getOrDefault(Options.PREFAB_CACHE_DIRECTORY, Options.getOptionSet(), Path.of(".cache/prefabs", new String[0]));
    public static final String LPF_FILE_SUFFIX = ".lpf";
    public static final String JSON_FILE_SUFFIX = ".prefab.json";
    public static final String JSON_LPF_FILE_SUFFIX = ".prefab.json.lpf";
    public static final String FILE_SUFFIX_REGEX = "((!\\.prefab\\.json)\\.lpf|\\.prefab\\.json)$";
    public static final Pattern FILE_SUFFIX_PATTERN = Pattern.compile("((!\\.prefab\\.json)\\.lpf|\\.prefab\\.json)$");
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final Map<Path, WeakReference<CachedEntry>> CACHE = new ConcurrentHashMap<Path, WeakReference<CachedEntry>>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    public static IPrefabBuffer getCached(@Nonnull Path path) {
        long stamp;
        CachedEntry cachedPrefab;
        WeakReference<CachedEntry> reference = CACHE.get(path);
        CachedEntry cachedEntry = cachedPrefab = reference != null ? (CachedEntry)reference.get() : null;
        if (cachedPrefab != null) {
            stamp = cachedPrefab.lock.readLock();
            try {
                if (cachedPrefab.buffer != null) {
                    PrefabBuffer.PrefabBufferAccessor prefabBufferAccessor = cachedPrefab.buffer.newAccess();
                    return prefabBufferAccessor;
                }
            }
            finally {
                cachedPrefab.lock.unlockRead(stamp);
            }
        }
        cachedPrefab = PrefabBufferUtil.getOrCreateCacheEntry(path);
        stamp = cachedPrefab.lock.writeLock();
        try {
            if (cachedPrefab.buffer != null) {
                PrefabBuffer.PrefabBufferAccessor prefabBufferAccessor = cachedPrefab.buffer.newAccess();
                return prefabBufferAccessor;
            }
            cachedPrefab.buffer = PrefabBufferUtil.loadBuffer(path);
            PrefabBuffer.PrefabBufferAccessor prefabBufferAccessor = cachedPrefab.buffer.newAccess();
            return prefabBufferAccessor;
        }
        finally {
            cachedPrefab.lock.unlockWrite(stamp);
        }
    }

    @Nonnull
    public static PrefabBuffer loadBuffer(@Nonnull Path path) {
        Path cachedLpfPath;
        AssetPack pack;
        String fileNameStr = path.getFileName().toString();
        String fileName = fileNameStr.replace(JSON_LPF_FILE_SUFFIX, "").replace(JSON_FILE_SUFFIX, "");
        Path lpfPath = path.resolveSibling(fileName + LPF_FILE_SUFFIX);
        if (Files.exists(lpfPath, new LinkOption[0])) {
            return PrefabBufferUtil.loadFromLPF(path, lpfPath);
        }
        if (AssetModule.get().isAssetPathImmutable(path)) {
            Path lpfConvertedPath = path.resolveSibling(fileName + JSON_LPF_FILE_SUFFIX);
            if (Files.exists(lpfConvertedPath, new LinkOption[0])) {
                return PrefabBufferUtil.loadFromLPF(path, lpfConvertedPath);
            }
            pack = AssetModule.get().findAssetPackForPath(path);
            if (pack != null) {
                String safePackName = FileUtil.INVALID_FILENAME_CHARACTERS.matcher(pack.getName()).replaceAll("_");
                cachedLpfPath = CACHE_PATH.resolve(safePackName).resolve(pack.getRoot().relativize(lpfConvertedPath).toString());
            } else {
                cachedLpfPath = lpfConvertedPath.getRoot() != null ? CACHE_PATH.resolve(lpfConvertedPath.subpath(1, lpfConvertedPath.getNameCount()).toString()) : CACHE_PATH.resolve(lpfConvertedPath.toString());
            }
        } else {
            cachedLpfPath = path.resolveSibling(fileName + JSON_LPF_FILE_SUFFIX);
            pack = null;
        }
        Path jsonPath = path.resolveSibling(fileName + JSON_FILE_SUFFIX);
        if (!Files.exists(jsonPath, new LinkOption[0])) {
            try {
                Files.deleteIfExists(cachedLpfPath);
            }
            catch (IOException safePackName) {
                // empty catch block
            }
            throw new Error("Error loading Prefab from " + String.valueOf(jsonPath.toAbsolutePath()) + " (.lpf and .prefab.json) File NOT found!");
        }
        try {
            return PrefabBufferUtil.loadFromJson(pack, path, cachedLpfPath, jsonPath);
        }
        catch (IOException e) {
            throw SneakyThrow.sneakyThrow(e);
        }
    }

    @Nonnull
    public static CompletableFuture<Void> writeToFileAsync(@Nonnull PrefabBuffer prefab, @Nonnull Path path) {
        return CompletableFuture.runAsync(SneakyThrow.sneakyRunnable(() -> {
            try (SeekableByteChannel channel = Files.newByteChannel(path, FileUtil.DEFAULT_WRITE_OPTIONS, new FileAttribute[0]);){
                channel.write(BinaryPrefabBufferCodec.INSTANCE.serialize(prefab).nioBuffer());
            }
        }));
    }

    public static PrefabBuffer readFromFile(@Nonnull Path path) {
        return PrefabBufferUtil.readFromFileAsync(path).join();
    }

    @Nonnull
    public static CompletableFuture<PrefabBuffer> readFromFileAsync(@Nonnull Path path) {
        return CompletableFuture.supplyAsync(SneakyThrow.sneakySupplier(() -> {
            try (SeekableByteChannel channel = Files.newByteChannel(path, new OpenOption[0]);){
                int size = (int)channel.size();
                ByteBuf buf = Unpooled.buffer(size);
                buf.writerIndex(size);
                if (channel.read(buf.internalNioBuffer(0, size)) != size) {
                    throw new IOException("Didn't read full file!");
                }
                PrefabBuffer prefabBuffer = BinaryPrefabBufferCodec.INSTANCE.deserialize(path, buf);
                return prefabBuffer;
            }
        }));
    }

    @Nonnull
    public static PrefabBuffer loadFromLPF(@Nonnull Path path, @Nonnull Path realPath) {
        try {
            return PrefabBufferUtil.readFromFile(realPath);
        }
        catch (Exception e) {
            throw new Error("Error while loading prefab " + String.valueOf(path.toAbsolutePath()) + " from " + String.valueOf(realPath.toAbsolutePath()), e);
        }
    }

    @Nonnull
    public static PrefabBuffer loadFromJson(@Nullable AssetPack pack, Path path, @Nonnull Path cachedLpfPath, @Nonnull Path jsonPath) throws IOException {
        FileTime targetModifiedTime;
        block11: {
            BasicFileAttributes cachedAttr = null;
            try {
                cachedAttr = Files.readAttributes(cachedLpfPath, BasicFileAttributes.class, new LinkOption[0]);
            }
            catch (IOException iOException) {
                // empty catch block
            }
            targetModifiedTime = pack == null || !pack.isImmutable() ? Files.readAttributes(jsonPath, BasicFileAttributes.class, new LinkOption[0]).lastModifiedTime() : Files.readAttributes(pack.getPackLocation(), BasicFileAttributes.class, new LinkOption[0]).lastModifiedTime();
            if (cachedAttr != null && targetModifiedTime.compareTo(cachedAttr.lastModifiedTime()) <= 0) {
                try {
                    return PrefabBufferUtil.readFromFile(cachedLpfPath);
                }
                catch (CompletionException e) {
                    if (Options.getOptionSet().has(Options.VALIDATE_PREFABS)) break block11;
                    if (e.getCause() instanceof UpdateBinaryPrefabException) {
                        LOGGER.at(Level.FINE).log("Ignoring LPF %s due to: %s", (Object)path, (Object)e.getMessage());
                    }
                    ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(new SkipSentryException(e))).log("Failed to load %s", cachedLpfPath);
                }
            }
        }
        try {
            PrefabBuffer buffer = BsonPrefabBufferDeserializer.INSTANCE.deserialize(jsonPath, BsonUtil.readDocument(jsonPath, false).join());
            if (!Options.getOptionSet().has(Options.DISABLE_CPB_BUILD)) {
                try {
                    Files.createDirectories(cachedLpfPath.getParent(), new FileAttribute[0]);
                    ((CompletableFuture)PrefabBufferUtil.writeToFileAsync(buffer, cachedLpfPath).thenRun(() -> {
                        try {
                            Files.setLastModifiedTime(cachedLpfPath, targetModifiedTime);
                        }
                        catch (IOException iOException) {
                            // empty catch block
                        }
                    })).exceptionally(throwable -> {
                        ((HytaleLogger.Api)HytaleLogger.getLogger().at(Level.FINE).withCause(new SkipSentryException((Throwable)throwable))).log("Failed to save prefab cache %s", cachedLpfPath);
                        return null;
                    });
                }
                catch (IOException e) {
                    LOGGER.at(Level.FINE).log("Cannot create cache directory for %s: %s", (Object)cachedLpfPath, (Object)e.getMessage());
                }
            }
            return buffer;
        }
        catch (Exception e) {
            throw new Error("Error while loading Prefab from " + String.valueOf(jsonPath.toAbsolutePath()), e);
        }
    }

    @Nonnull
    private static CachedEntry getOrCreateCacheEntry(Path path) {
        CachedEntry[] temp = new CachedEntry[1];
        CACHE.compute(path, (p, ref) -> {
            if (ref != null) {
                CachedEntry cached;
                temp[0] = cached = (CachedEntry)ref.get();
                if (cached != null) {
                    return ref;
                }
            }
            temp[0] = new CachedEntry();
            return new WeakReference<CachedEntry>(temp[0]);
        });
        return temp[0];
    }

    private static class CachedEntry {
        private final StampedLock lock = new StampedLock();
        private PrefabBuffer buffer;

        private CachedEntry() {
        }
    }
}

