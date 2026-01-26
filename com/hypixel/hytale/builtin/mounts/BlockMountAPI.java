/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.mounts;

import com.hypixel.hytale.builtin.mounts.BlockMountComponent;
import com.hypixel.hytale.builtin.mounts.MountedComponent;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockMountType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.mountpoints.BlockMountPoint;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public final class BlockMountAPI {
    private BlockMountAPI() {
    }

    public static BlockMountResult mountOnBlock(Ref<EntityStore> entity, CommandBuffer<EntityStore> commandBuffer, Vector3i targetBlock, Vector3f interactPos) {
        MountedComponent existingMounted = commandBuffer.getComponent(entity, MountedComponent.getComponentType());
        if (existingMounted != null) {
            return DidNotMount.ALREADY_MOUNTED;
        }
        World world = entity.getStore().getExternalData().getWorld();
        WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(targetBlock.x, targetBlock.z));
        if (chunk == null) {
            return DidNotMount.CHUNK_NOT_FOUND;
        }
        Ref<ChunkStore> chunkRef = chunk.getReference();
        if (chunkRef == null) {
            return DidNotMount.CHUNK_REF_NOT_FOUND;
        }
        ChunkStore chunkStore = world.getChunkStore();
        BlockComponentChunk blockComponentChunk = chunkStore.getStore().getComponent(chunkRef, BlockComponentChunk.getComponentType());
        if (blockComponentChunk == null) {
            return DidNotMount.CHUNK_REF_NOT_FOUND;
        }
        BlockType blockType = world.getBlockType(targetBlock);
        if (blockType == null) {
            return DidNotMount.INVALID_BLOCK;
        }
        int rotationIndex = chunk.getRotationIndex(targetBlock.x, targetBlock.y, targetBlock.z);
        int blockIndex = ChunkUtil.indexBlockInColumn(targetBlock.x, targetBlock.y, targetBlock.z);
        Ref<ChunkStore> blockRef = blockComponentChunk.getEntityReference(blockIndex);
        if (blockRef == null || !blockRef.isValid()) {
            Holder<ChunkStore> blockHolder = ChunkStore.REGISTRY.newHolder();
            blockHolder.putComponent(BlockModule.BlockStateInfo.getComponentType(), new BlockModule.BlockStateInfo(blockIndex, chunkRef));
            blockRef = world.getChunkStore().getStore().addEntity(blockHolder, AddReason.SPAWN);
            if (blockRef == null || !blockRef.isValid()) {
                return DidNotMount.BLOCK_REF_NOT_FOUND;
            }
        }
        BlockMountType blockMountType = null;
        BlockMountPoint[] mountPointsForBlock = null;
        if (blockType.getSeats() != null) {
            blockMountType = BlockMountType.Seat;
            mountPointsForBlock = blockType.getSeats().getRotated(rotationIndex);
        } else if (blockType.getBeds() != null) {
            blockMountType = BlockMountType.Bed;
            mountPointsForBlock = blockType.getBeds().getRotated(rotationIndex);
        } else {
            return DidNotMount.UNKNOWN_BLOCKMOUNT_TYPE;
        }
        BlockMountComponent blockMountComponent = world.getChunkStore().getStore().getComponent(blockRef, BlockMountComponent.getComponentType());
        if (blockMountComponent == null) {
            blockMountComponent = new BlockMountComponent(blockMountType, targetBlock, blockType, rotationIndex);
            world.getChunkStore().getStore().addComponent(blockRef, BlockMountComponent.getComponentType(), blockMountComponent);
        }
        if (mountPointsForBlock == null || mountPointsForBlock.length == 0) {
            return DidNotMount.NO_MOUNT_POINT_FOUND;
        }
        BlockMountPoint pickedMountPoint = blockMountComponent.findAvailableSeat(targetBlock, mountPointsForBlock, interactPos);
        if (pickedMountPoint == null) {
            return DidNotMount.NO_MOUNT_POINT_FOUND;
        }
        TransformComponent transformComponent = commandBuffer.getComponent(entity, TransformComponent.getComponentType());
        if (transformComponent != null) {
            Vector3f position = pickedMountPoint.computeWorldSpacePosition(blockMountComponent.getBlockPos());
            Vector3f rotationEuler = pickedMountPoint.computeRotationEuler(blockMountComponent.getExpectedRotation());
            transformComponent.setPosition(position.toVector3d());
            transformComponent.setRotation(rotationEuler);
        }
        MountedComponent mountedComponent = new MountedComponent(blockRef, new Vector3f(0.0f, 0.0f, 0.0f), blockMountType);
        commandBuffer.addComponent(entity, MountedComponent.getComponentType(), mountedComponent);
        blockMountComponent.putSeatedEntity(pickedMountPoint, entity);
        return new Mounted(blockType, mountedComponent);
    }

    public static enum DidNotMount implements BlockMountResult
    {
        CHUNK_NOT_FOUND,
        CHUNK_REF_NOT_FOUND,
        BLOCK_REF_NOT_FOUND,
        INVALID_BLOCK,
        ALREADY_MOUNTED,
        UNKNOWN_BLOCKMOUNT_TYPE,
        NO_MOUNT_POINT_FOUND;

    }

    public record Mounted(BlockType blockType, MountedComponent component) implements BlockMountResult
    {
    }

    public static sealed interface BlockMountResult
    permits Mounted, DidNotMount {
    }
}

