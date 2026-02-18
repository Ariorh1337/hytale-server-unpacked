/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.commands.world;

import com.hypixel.fastutil.util.SneakyThrow;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncWorldCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.provider.RocksDbChunkStorageProvider;
import java.util.concurrent.CompletableFuture;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class WorldRocksDbCommand
extends AbstractCommandCollection {
    public WorldRocksDbCommand() {
        super("rocksdb", "server.commands.world.rocksdb");
        this.addSubCommand(new CompactCommand());
    }

    public static class CompactCommand
    extends AbstractAsyncWorldCommand {
        public CompactCommand() {
            super("compact", "server.commands.world.rocksdb.compact");
        }

        @Override
        @NonNullDecl
        protected CompletableFuture<Void> executeAsync(@NonNullDecl CommandContext context, @NonNullDecl World world) {
            Object storage = world.getChunkStore().getStorageData();
            if (storage instanceof RocksDbChunkStorageProvider.RocksDbResource) {
                RocksDbChunkStorageProvider.RocksDbResource rocksDbResource = (RocksDbChunkStorageProvider.RocksDbResource)storage;
                context.sendMessage(Message.translation("server.commands.world.rocksdb.compact.start"));
                return CompletableFuture.runAsync(() -> {
                    try {
                        rocksDbResource.db.compactRange(rocksDbResource.chunkColumn);
                    }
                    catch (Exception e) {
                        throw SneakyThrow.sneakyThrow(e);
                    }
                    context.sendMessage(Message.translation("server.commands.world.rocksdb.compact.end"));
                });
            }
            context.sendMessage(Message.translation("server.commands.world.rocksdb.compact.wrong"));
            return CompletableFuture.completedFuture(null);
        }
    }
}

