/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.portals.ui;

import com.hypixel.hytale.builtin.portals.utils.posqueries.generators.SearchCircular;
import com.hypixel.hytale.builtin.portals.utils.posqueries.predicates.FitsAPortal;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockMaterial;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.portalworld.PortalSpawn;
import com.hypixel.hytale.server.core.modules.collision.WorldUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.ChunkColumn;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class PortalSpawnFinder {
    @Nullable
    public static Transform computeSpawnTransform(World world, PortalSpawn config) {
        Vector3d spawn = PortalSpawnFinder.findSpawnByThrowingDarts(world, config);
        if (spawn == null) {
            spawn = PortalSpawnFinder.findFallbackPositionOnGround(world, config);
            HytaleLogger.getLogger().at(Level.INFO).log("Had to use fallback spawn for portal spawn");
        }
        if (spawn == null) {
            HytaleLogger.getLogger().at(Level.INFO).log("Both dart and fallback spawn finder failed for portal spawn");
            return null;
        }
        Vector3f direction = Vector3f.lookAt(spawn).scale(-1.0f);
        direction.setPitch(0.0f);
        direction.setRoll(0.0f);
        return new Transform(spawn.clone().add(0.0, 0.5, 0.0), direction);
    }

    @Nullable
    private static Vector3d findSpawnByThrowingDarts(World world, PortalSpawn config) {
        Vector3d center = config.getCenter().toVector3d();
        center.setY(config.getCheckSpawnY());
        int halfwayThrows = config.getChunkDartThrows() / 2;
        for (int chunkDart = 0; chunkDart < config.getChunkDartThrows(); ++chunkDart) {
            boolean checkIfPortalFitsNice;
            Vector3d spawn;
            BlockMaterial firstBlockMat;
            Vector3d pointd = new SearchCircular(config.getMinRadius(), config.getMaxRadius(), 1).execute(world, center).orElse(null);
            if (pointd == null) continue;
            Vector3i point = pointd.toVector3i();
            Object chunk = world.getChunk(ChunkUtil.indexChunkFromBlock(point.x, point.z));
            BlockType firstBlock = chunk.getBlockType(point.x, point.y, point.z);
            if (firstBlock == null || (firstBlockMat = firstBlock.getMaterial()) == BlockMaterial.Solid || (spawn = PortalSpawnFinder.findGroundWithinChunk(chunk, config, checkIfPortalFitsNice = chunkDart < halfwayThrows)) == null) continue;
            HytaleLogger.getLogger().at(Level.INFO).log("Found fragment spawn at " + String.valueOf(spawn) + " after " + (chunkDart + 1) + " chunk scan(s)");
            return spawn;
        }
        return null;
    }

    @Nullable
    private static Vector3d findGroundWithinChunk(WorldChunk chunk, PortalSpawn config, boolean checkIfPortalFitsNice) {
        int chunkBlockX = ChunkUtil.minBlock(chunk.getX());
        int chunkBlockZ = ChunkUtil.minBlock(chunk.getZ());
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        for (int i = 0; i < config.getChecksPerChunk(); ++i) {
            int x = chunkBlockX + rand.nextInt(2, 14);
            int z = chunkBlockZ + rand.nextInt(2, 14);
            Vector3d point = PortalSpawnFinder.findWithGroundBelow(chunk, x, config.getCheckSpawnY(), z, config.getScanHeight(), false);
            if (point == null || checkIfPortalFitsNice && !FitsAPortal.check(chunk.getWorld(), point)) continue;
            return point;
        }
        return null;
    }

    @Nullable
    private static Vector3d findWithGroundBelow(WorldChunk chunk, int x, int y, int z, int scanHeight, boolean fluidsAreAcceptable) {
        World world = chunk.getWorld();
        ChunkStore chunkStore = world.getChunkStore();
        Ref<ChunkStore> chunkRef = chunk.getReference();
        Store<ChunkStore> chunkStoreAccessor = chunkStore.getStore();
        ChunkColumn chunkColumnComponent = chunkStoreAccessor.getComponent(chunkRef, ChunkColumn.getComponentType());
        BlockChunk blockChunkComponent = chunkStoreAccessor.getComponent(chunkRef, BlockChunk.getComponentType());
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
    private static Vector3d findFallbackPositionOnGround(World world, PortalSpawn config) {
        Vector3i center = config.getCenter();
        Object centerChunk = world.getChunk(ChunkUtil.indexChunkFromBlock(center.x, center.z));
        return PortalSpawnFinder.findWithGroundBelow(centerChunk, 0, 319, 0, 319, true);
    }

    private static enum Material {
        SOLID,
        FLUID,
        AIR,
        UNKNOWN;

    }
}

