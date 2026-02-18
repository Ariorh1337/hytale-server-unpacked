/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.portals.ui;

import com.hypixel.hytale.builtin.portals.utils.posqueries.predicates.FitsAPortal;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.BlockMaterial;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.modules.collision.WorldUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.ChunkColumn;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class PortalSpawnFinder {
    private static final int MAX_ATTEMPTS_PER_WORLD = 10;
    private static final int QUALITY_ATTEMPTS = 2;
    private static final int CHECKS_PER_CHUNK = 8;
    private static final Vector3d FALLBACK_POSITION = Vector3d.ZERO;

    @Nullable
    public static Transform computeSpawnTransform(@Nonnull World world, @Nonnull List<Vector3d> hintedSpawns) {
        Vector3d spawn = PortalSpawnFinder.guesstimateFromHints(world, hintedSpawns);
        if (spawn == null) {
            spawn = PortalSpawnFinder.findFallbackPositionOnGround(world);
            ((HytaleLogger.Api)HytaleLogger.getLogger().atWarning()).log("Had to use fallback spawn for portal spawn (10 attempts)");
        }
        if (spawn == null) {
            ((HytaleLogger.Api)HytaleLogger.getLogger().atWarning()).log("Both dart and fallback spawn finder failed for portal spawn");
            return null;
        }
        Vector3f direction = Vector3f.lookAt(spawn).scale(-1.0f);
        direction.setPitch(0.0f);
        direction.setRoll(0.0f);
        return new Transform(spawn.clone().add(0.0, 0.5, 0.0), direction);
    }

    @Nullable
    private static Vector3d guesstimateFromHints(World world, List<Vector3d> hintedSpawns) {
        for (int i = 0; i < Math.min(hintedSpawns.size(), 10); ++i) {
            boolean quality;
            int scanHeight;
            Vector3d spawn;
            Vector3d hintedSpawn = hintedSpawns.get(i);
            Object chunk = world.getChunk(ChunkUtil.indexChunkFromBlock(hintedSpawn.x, hintedSpawn.z));
            if (chunk == null || (spawn = PortalSpawnFinder.findGroundWithinChunk(chunk, scanHeight = (quality = i < 2) ? (int)hintedSpawn.y : 319, quality)) == null) continue;
            ((HytaleLogger.Api)HytaleLogger.getLogger().atInfo()).log("Found portal spawn " + String.valueOf(spawn) + " on attempt #" + (i + 1) + " quality=" + quality);
            return spawn;
        }
        return null;
    }

    @Nullable
    private static Vector3d findGroundWithinChunk(@Nonnull WorldChunk chunk, int scanHeight, boolean checkIfPortalFitsNice) {
        int chunkBlockX = ChunkUtil.minBlock(chunk.getX());
        int chunkBlockZ = ChunkUtil.minBlock(chunk.getZ());
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 8; ++i) {
            int z;
            int x = chunkBlockX + random.nextInt(2, 14);
            Vector3d point = PortalSpawnFinder.findWithGroundBelow(chunk, x, scanHeight, z = chunkBlockZ + random.nextInt(2, 14), scanHeight, false);
            if (point == null || checkIfPortalFitsNice && !FitsAPortal.check(chunk.getWorld(), point)) continue;
            return point;
        }
        return null;
    }

    @Nullable
    private static Vector3d findWithGroundBelow(@Nonnull WorldChunk chunk, int x, int y, int z, int scanHeight, boolean fluidsAreAcceptable) {
        World world = chunk.getWorld();
        ChunkStore chunkStore = world.getChunkStore();
        Ref<ChunkStore> chunkRef = chunk.getReference();
        if (chunkRef == null || !chunkRef.isValid()) {
            return null;
        }
        Store<ChunkStore> chunkStoreAccessor = chunkStore.getStore();
        ChunkColumn chunkColumnComponent = chunkStoreAccessor.getComponent(chunkRef, ChunkColumn.getComponentType());
        if (chunkColumnComponent == null) {
            return null;
        }
        BlockChunk blockChunkComponent = chunkStoreAccessor.getComponent(chunkRef, BlockChunk.getComponentType());
        if (blockChunkComponent == null) {
            return null;
        }
        for (int dy = 0; dy < scanHeight; ++dy) {
            boolean selfValid;
            Material selfMat = PortalSpawnFinder.getMaterial(chunkStoreAccessor, chunkColumnComponent, blockChunkComponent, x, y - dy, z);
            Material belowMat = PortalSpawnFinder.getMaterial(chunkStoreAccessor, chunkColumnComponent, blockChunkComponent, x, y - dy - 1, z);
            boolean bl = selfValid = selfMat == Material.AIR || fluidsAreAcceptable && selfMat == Material.FLUID;
            if (!selfValid) break;
            if (belowMat != Material.SOLID) continue;
            return new Vector3d(x, y - dy, z);
        }
        return null;
    }

    @Nonnull
    private static Material getMaterial(@Nonnull ComponentAccessor<ChunkStore> chunkStore, @Nonnull ChunkColumn chunkColumnComponent, @Nonnull BlockChunk blockChunkComponent, double x, double y, double z) {
        int blockX = (int)x;
        int blockY = (int)y;
        int blockZ = (int)z;
        int fluidId = WorldUtil.getFluidIdAtPosition(chunkStore, chunkColumnComponent, blockX, blockY, blockZ);
        if (fluidId != 0) {
            return Material.FLUID;
        }
        BlockSection blockSection = blockChunkComponent.getSectionAtBlockY(blockY);
        int blockId = blockSection.get(blockX, blockY, blockZ);
        BlockType blockType = BlockType.getAssetMap().getAsset(blockId);
        if (blockType == null) {
            return Material.UNKNOWN;
        }
        return switch (blockType.getMaterial()) {
            default -> throw new MatchException(null, null);
            case BlockMaterial.Solid -> Material.SOLID;
            case BlockMaterial.Empty -> Material.AIR;
        };
    }

    @Nullable
    private static Vector3d findFallbackPositionOnGround(@Nonnull World world) {
        Vector3d center = FALLBACK_POSITION.clone();
        long chunkIndex = ChunkUtil.indexChunkFromBlock(center.x, center.z);
        Object centerChunk = world.getChunk(chunkIndex);
        if (centerChunk == null) {
            return null;
        }
        return PortalSpawnFinder.findWithGroundBelow(centerChunk, 0, 319, 0, 319, true);
    }

    private static enum Material {
        SOLID,
        FLUID,
        AIR,
        UNKNOWN;

    }
}

