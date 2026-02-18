/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.storage.provider;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.universe.world.storage.BufferChunkLoader;
import com.hypixel.hytale.server.core.universe.world.storage.BufferChunkSaver;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.IChunkLoader;
import com.hypixel.hytale.server.core.universe.world.storage.IChunkSaver;
import com.hypixel.hytale.server.core.universe.world.storage.provider.IChunkStorageProvider;
import com.hypixel.hytale.sneakythrow.SneakyThrow;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.FlushOptions;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;

public class RocksDbChunkStorageProvider
implements IChunkStorageProvider<RocksDbResource> {
    public static final String ID = "RocksDb";
    public static final BuilderCodec<RocksDbChunkStorageProvider> CODEC = BuilderCodec.builder(RocksDbChunkStorageProvider.class, RocksDbChunkStorageProvider::new).build();

    /*
     * Exception decompiling
     */
    @Override
    public RocksDbResource initialize(@NonNullDecl Store<ChunkStore> store) throws IOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 3 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    @Override
    public void close(@NonNullDecl RocksDbResource resource, @NonNullDecl Store<ChunkStore> store) throws IOException {
        try {
            resource.db.syncWal();
        }
        catch (RocksDBException e) {
            throw SneakyThrow.sneakyThrow(e);
        }
        resource.chunkColumn.close();
        resource.db.close();
        resource.db = null;
    }

    @Override
    @Nonnull
    public IChunkLoader getLoader(@Nonnull RocksDbResource resource, @Nonnull Store<ChunkStore> store) throws IOException {
        return new Loader(store, resource);
    }

    @Override
    @Nonnull
    public IChunkSaver getSaver(@Nonnull RocksDbResource resource, @Nonnull Store<ChunkStore> store) throws IOException {
        return new Saver(store, resource);
    }

    private static byte[] toKey(int x, int z) {
        byte[] key = new byte[]{(byte)(x >>> 24), (byte)(x >>> 16), (byte)(x >>> 8), (byte)x, (byte)(z >>> 24), (byte)(z >>> 16), (byte)(z >>> 8), (byte)z};
        return key;
    }

    private static int keyToX(byte[] key) {
        return (key[0] & 0xFF) << 24 | (key[1] & 0xFF) << 16 | (key[2] & 0xFF) << 8 | key[3] & 0xFF;
    }

    private static int keyToZ(byte[] key) {
        return (key[4] & 0xFF) << 24 | (key[5] & 0xFF) << 16 | (key[6] & 0xFF) << 8 | key[7] & 0xFF;
    }

    public static class RocksDbResource {
        public RocksDB db;
        public ColumnFamilyHandle chunkColumn;
    }

    public static class Loader
    extends BufferChunkLoader
    implements IChunkLoader {
        private final RocksDbResource db;

        public Loader(Store<ChunkStore> store, RocksDbResource db) {
            super(store);
            this.db = db;
        }

        @Override
        public CompletableFuture<ByteBuffer> loadBuffer(int x, int z) {
            return CompletableFuture.supplyAsync(SneakyThrow.sneakySupplier(() -> {
                byte[] key = RocksDbChunkStorageProvider.toKey(x, z);
                byte[] data = this.db.db.get(this.db.chunkColumn, key);
                if (data == null) {
                    return null;
                }
                return ByteBuffer.wrap(data);
            }));
        }

        @Override
        @Nonnull
        public LongSet getIndexes() throws IOException {
            LongOpenHashSet set = new LongOpenHashSet();
            try (RocksIterator iter = this.db.db.newIterator(this.db.chunkColumn);){
                iter.seekToFirst();
                while (iter.isValid()) {
                    byte[] key = iter.key();
                    set.add(ChunkUtil.indexChunk(RocksDbChunkStorageProvider.keyToX(key), RocksDbChunkStorageProvider.keyToZ(key)));
                    iter.next();
                }
            }
            return set;
        }

        @Override
        public void close() throws IOException {
        }
    }

    public static class Saver
    extends BufferChunkSaver
    implements IChunkSaver {
        private final RocksDbResource db;

        public Saver(Store<ChunkStore> store, RocksDbResource db) {
            super(store);
            this.db = db;
        }

        @Override
        @Nonnull
        public CompletableFuture<Void> saveBuffer(int x, int z, @Nonnull ByteBuffer buffer) {
            return CompletableFuture.runAsync(SneakyThrow.sneakyRunnable(() -> {
                if (buffer.hasArray()) {
                    this.db.db.put(this.db.chunkColumn, RocksDbChunkStorageProvider.toKey(x, z), buffer.array());
                } else {
                    byte[] buf = new byte[buffer.remaining()];
                    buffer.get(buf);
                    this.db.db.put(this.db.chunkColumn, RocksDbChunkStorageProvider.toKey(x, z), buf);
                }
            }));
        }

        @Override
        @Nonnull
        public CompletableFuture<Void> removeBuffer(int x, int z) {
            return CompletableFuture.runAsync(SneakyThrow.sneakyRunnable(() -> this.db.db.delete(this.db.chunkColumn, RocksDbChunkStorageProvider.toKey(x, z))));
        }

        @Override
        @Nonnull
        public LongSet getIndexes() throws IOException {
            LongOpenHashSet set = new LongOpenHashSet();
            try (RocksIterator iter = this.db.db.newIterator(this.db.chunkColumn);){
                iter.seekToFirst();
                while (iter.isValid()) {
                    byte[] key = iter.key();
                    set.add(ChunkUtil.indexChunk(RocksDbChunkStorageProvider.keyToX(key), RocksDbChunkStorageProvider.keyToZ(key)));
                    iter.next();
                }
            }
            return set;
        }

        @Override
        public void flush() throws IOException {
            try (FlushOptions opts = new FlushOptions().setWaitForFlush(true);){
                this.db.db.flush(opts);
            }
            catch (RocksDBException e) {
                throw SneakyThrow.sneakyThrow(e);
            }
        }

        @Override
        public void close() throws IOException {
        }
    }
}

