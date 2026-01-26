/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.storage;

import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public interface IChunkSaver
extends Closeable {
    @Nonnull
    public CompletableFuture<Void> saveHolder(int var1, int var2, @Nonnull Holder<ChunkStore> var3);

    @Nonnull
    public CompletableFuture<Void> removeHolder(int var1, int var2);

    @Nonnull
    public LongSet getIndexes() throws IOException;

    public void flush() throws IOException;
}

