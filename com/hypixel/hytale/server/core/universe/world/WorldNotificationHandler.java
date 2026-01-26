/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world;

import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.protocol.BlockParticleEvent;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.Position;
import com.hypixel.hytale.protocol.packets.world.SpawnBlockParticleSystem;
import com.hypixel.hytale.protocol.packets.world.UpdateBlockDamage;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.universe.world.meta.state.SendableBlockState;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.lang.invoke.LambdaMetafactory;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WorldNotificationHandler {
    @Nonnull
    private final World world;

    public WorldNotificationHandler(@Nonnull World world) {
        this.world = world;
    }

    public void updateState(int x, int y, int z, BlockState state, BlockState oldState) {
        this.updateState(x, y, z, state, oldState, null);
    }

    /*
     * Unable to fully structure code
     */
    public void updateState(int x, int y, int z, BlockState state, BlockState oldState, @Nullable Predicate<PlayerRef> skip) {
        if (y < 0 || y >= 320) {
            throw new IllegalArgumentException("Y value is outside the world! " + x + ", " + y + ", " + z);
        }
        if (!(oldState instanceof SendableBlockState)) ** GOTO lbl-1000
        sendableBlockState = (SendableBlockState)oldState;
        if (state != oldState) {
            removeOldState = (Consumer<List>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)V, unloadFrom(java.util.List<com.hypixel.hytale.protocol.Packet> ), (Ljava/util/List;)V)((SendableBlockState)sendableBlockState);
            canPlayerSeeOld = (Predicate<PlayerRef>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Z, canPlayerSee(com.hypixel.hytale.server.core.universe.PlayerRef ), (Lcom/hypixel/hytale/server/core/universe/PlayerRef;)Z)((SendableBlockState)sendableBlockState);
        } else lbl-1000:
        // 2 sources

        {
            removeOldState = null;
            canPlayerSeeOld = null;
        }
        if (state instanceof SendableBlockState) {
            sendableBlockState = (SendableBlockState)state;
            updateBlockState = (Consumer<List>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)V, sendTo(java.util.List<com.hypixel.hytale.protocol.Packet> ), (Ljava/util/List;)V)((SendableBlockState)sendableBlockState);
            canPlayerSee = (Predicate<PlayerRef>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Z, canPlayerSee(com.hypixel.hytale.server.core.universe.PlayerRef ), (Lcom/hypixel/hytale/server/core/universe/PlayerRef;)Z)((SendableBlockState)sendableBlockState);
        } else {
            updateBlockState = null;
            canPlayerSee = null;
        }
        if (removeOldState != null || updateBlockState != null) {
            indexChunk = ChunkUtil.indexChunkFromBlock(x, z);
            packets = new ObjectArrayList<K>();
            for (PlayerRef playerRef : this.world.getPlayerRefs()) {
                chunkTracker = playerRef.getChunkTracker();
                if (!chunkTracker.isLoaded(indexChunk) || skip != null && skip.test(playerRef)) continue;
                if (removeOldState != null && canPlayerSeeOld.test(playerRef)) {
                    removeOldState.accept(packets);
                }
                if (updateBlockState != null && canPlayerSee.test(playerRef)) {
                    updateBlockState.accept(packets);
                }
                for (Packet packet : packets) {
                    playerRef.getPacketHandler().write(packet);
                }
                packets.clear();
            }
        }
    }

    public void updateChunk(long indexChunk) {
        for (PlayerRef playerRef : this.world.getPlayerRefs()) {
            playerRef.getChunkTracker().removeForReload(indexChunk);
        }
    }

    public void sendBlockParticle(double x, double y, double z, int id, @Nonnull BlockParticleEvent particleType) {
        this.sendPacketIfChunkLoaded((Packet)this.getBlockParticlePacket(x, y, z, id, particleType), MathUtil.floor(x), MathUtil.floor(z));
    }

    public void sendBlockParticle(@Nonnull PlayerRef playerRef, double x, double y, double z, int id, @Nonnull BlockParticleEvent particleType) {
        this.sendPacketIfChunkLoaded(playerRef, this.getBlockParticlePacket(x, y, z, id, particleType), MathUtil.floor(x), MathUtil.floor(z));
    }

    public void updateBlockDamage(int x, int y, int z, float health, float healthDelta) {
        this.sendPacketIfChunkLoaded((Packet)this.getBlockDamagePacket(x, y, z, health, healthDelta), x, z);
    }

    public void updateBlockDamage(int x, int y, int z, float health, float healthDelta, @Nullable Predicate<PlayerRef> filter) {
        this.sendPacketIfChunkLoaded(this.getBlockDamagePacket(x, y, z, health, healthDelta), x, z, filter);
    }

    public void sendPacketIfChunkLoaded(@Nonnull Packet packet, int x, int z) {
        long indexChunk = ChunkUtil.indexChunkFromBlock(x, z);
        this.sendPacketIfChunkLoaded(packet, indexChunk);
    }

    public void sendPacketIfChunkLoaded(@Nonnull Packet packet, long indexChunk) {
        for (PlayerRef playerRef : this.world.getPlayerRefs()) {
            if (!playerRef.getChunkTracker().isLoaded(indexChunk)) continue;
            playerRef.getPacketHandler().write(packet);
        }
    }

    public void sendPacketIfChunkLoaded(@Nonnull Packet packet, int x, int z, @Nullable Predicate<PlayerRef> filter) {
        long indexChunk = ChunkUtil.indexChunkFromBlock(x, z);
        this.sendPacketIfChunkLoaded(packet, indexChunk, filter);
    }

    public void sendPacketIfChunkLoaded(@Nonnull Packet packet, long indexChunk, @Nullable Predicate<PlayerRef> filter) {
        for (PlayerRef playerRef : this.world.getPlayerRefs()) {
            if (filter != null && !filter.test(playerRef) || !playerRef.getChunkTracker().isLoaded(indexChunk)) continue;
            playerRef.getPacketHandler().write(packet);
        }
    }

    private void sendPacketIfChunkLoaded(@Nonnull PlayerRef player, @Nonnull Packet packet, int x, int z) {
        long indexChunk = ChunkUtil.indexChunkFromBlock(x, z);
        this.sendPacketIfChunkLoaded(player, packet, indexChunk);
    }

    private void sendPacketIfChunkLoaded(@Nonnull PlayerRef playerRef, @Nonnull Packet packet, long indexChunk) {
        if (playerRef.getChunkTracker().isLoaded(indexChunk)) {
            playerRef.getPacketHandler().write(packet);
        }
    }

    @Nonnull
    public SpawnBlockParticleSystem getBlockParticlePacket(double x, double y, double z, int id, @Nonnull BlockParticleEvent particleType) {
        if (y < 0.0 || y >= 320.0) {
            throw new IllegalArgumentException("Y value is outside the world! " + x + ", " + y + ", " + z);
        }
        return new SpawnBlockParticleSystem(id, particleType, new Position(x, y, z));
    }

    @Nonnull
    public UpdateBlockDamage getBlockDamagePacket(int x, int y, int z, float health, float healthDelta) {
        if (y < 0 || y >= 320) {
            throw new IllegalArgumentException("Y value is outside the world! " + x + ", " + y + ", " + z);
        }
        return new UpdateBlockDamage(new BlockPosition(x, y, z), health, healthDelta);
    }
}

