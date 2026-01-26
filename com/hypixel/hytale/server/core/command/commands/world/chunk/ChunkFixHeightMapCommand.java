/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.commands.world.chunk;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector2i;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.arguments.types.RelativeChunkPosition;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractWorldCommand;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.lighting.ChunkLightingManager;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

public class ChunkFixHeightMapCommand
extends AbstractWorldCommand {
    @Nonnull
    private static final Message MESSAGE_COMMANDS_CHUNK_FIXHEIGHTMAP_STARTED = Message.translation("server.commands.chunk.fixHeightMap.started");
    @Nonnull
    private static final Message MESSAGE_COMMANDS_CHUNK_FIXHEIGHTMAP_DONE = Message.translation("server.commands.chunk.fixHeightMap.done");
    @Nonnull
    private static final Message MESSAGE_COMMANDS_CHUNK_FIXHEIGHTMAP_INVALIDATING_LIGHTING = Message.translation("server.commands.chunk.fixHeightMap.invalidatingLighting");
    @Nonnull
    private final RequiredArg<RelativeChunkPosition> chunkPosArg = this.withRequiredArg("x z", "server.commands.chunk.fixheight.position.desc", ArgTypes.RELATIVE_CHUNK_POSITION);

    public ChunkFixHeightMapCommand() {
        super("fixheight", "server.commands.chunk.fixheight.desc");
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull World world, @Nonnull Store<EntityStore> store) {
        RelativeChunkPosition chunkPosition = (RelativeChunkPosition)this.chunkPosArg.get(context);
        Vector2i position = chunkPosition.getChunkPosition(context, store);
        ChunkFixHeightMapCommand.fixHeightMap(context, world, position.x, position.y);
    }

    private static void fixHeightMap(@Nonnull CommandContext context, @Nonnull World world, int chunkX, int chunkZ) {
        ScheduledFuture[] scheduledFuture;
        ChunkLightingManager chunkLighting = world.getChunkLighting();
        ChunkStore chunkStore = world.getChunkStore();
        Store<ChunkStore> chunkStoreStore = chunkStore.getStore();
        long chunkIndex = ChunkUtil.indexChunk(chunkX, chunkZ);
        Ref<ChunkStore> chunkRef = chunkStore.getChunkReference(chunkIndex);
        if (chunkRef == null || !chunkRef.isValid()) {
            context.sendMessage(Message.translation("server.commands.errors.chunkNotLoaded").param("chunkX", chunkX).param("chunkZ", chunkZ).param("world", world.getName()));
            return;
        }
        WorldChunk worldChunkComponent = chunkStoreStore.getComponent(chunkRef, WorldChunk.getComponentType());
        assert (worldChunkComponent != null);
        BlockChunk blockChunkComponent = chunkStoreStore.getComponent(chunkRef, BlockChunk.getComponentType());
        assert (blockChunkComponent != null);
        context.sendMessage(MESSAGE_COMMANDS_CHUNK_FIXHEIGHTMAP_STARTED);
        blockChunkComponent.updateHeightmap();
        context.sendMessage(MESSAGE_COMMANDS_CHUNK_FIXHEIGHTMAP_DONE);
        context.sendMessage(MESSAGE_COMMANDS_CHUNK_FIXHEIGHTMAP_INVALIDATING_LIGHTING);
        for (int chunkSectionY = 0; chunkSectionY < 10; ++chunkSectionY) {
            blockChunkComponent.getSectionAtIndex(chunkSectionY).invalidateLocalLight();
        }
        chunkLighting.invalidateLightInChunk(worldChunkComponent);
        context.sendMessage(MESSAGE_COMMANDS_CHUNK_FIXHEIGHTMAP_DONE);
        context.sendMessage(Message.translation("server.commands.chunk.fixHeightMap.waitingForLighting").param("x", chunkX).param("z", chunkZ));
        int[] count = new int[]{0};
        scheduledFuture = new ScheduledFuture[]{HytaleServer.SCHEDULED_EXECUTOR.scheduleWithFixedDelay(() -> {
            if (chunkLighting.isQueued(chunkX, chunkZ)) {
                int n = count[0];
                count[0] = n + 1;
                if (n > 60) {
                    scheduledFuture[0].cancel(false);
                    context.sendMessage(Message.translation("server.commands.chunk.fixHeightMap.lightingError").param("x", chunkX).param("z", chunkZ));
                }
                return;
            }
            world.getNotificationHandler().updateChunk(chunkIndex);
            context.sendMessage(Message.translation("server.commands.chunk.fixHeightMap.lightingFinished").param("x", chunkX).param("z", chunkZ));
            scheduledFuture[0].cancel(false);
        }, 1L, 1L, TimeUnit.SECONDS)};
    }
}

