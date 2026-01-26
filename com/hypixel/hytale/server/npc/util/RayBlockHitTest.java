/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.util;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.iterator.BlockIterator;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.modules.blockset.BlockSetModule;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RayBlockHitTest
implements BlockIterator.BlockIteratorProcedure {
    public static final ThreadLocal<RayBlockHitTest> THREAD_LOCAL = ThreadLocal.withInitial(RayBlockHitTest::new);
    @Nullable
    private World world;
    @Nullable
    private WorldChunk chunk;
    private final Vector3d origin = new Vector3d();
    private final Vector3d direction = new Vector3d();
    private int blockSet;
    private final Vector3d hitPosition = new Vector3d(Vector3d.MIN);
    private short lastBlockRevision;

    @Override
    public boolean accept(int x, int y, int z, double px, double py, double pz, double qx, double qy, double qz) {
        int blockId;
        if (!ChunkUtil.isInsideChunk(this.chunk.getX(), this.chunk.getZ(), x, z)) {
            this.chunk = this.world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(x, z));
            if (this.chunk == null) {
                this.hitPosition.assign(Vector3d.MIN);
                return false;
            }
        }
        if ((blockId = this.chunk.getBlock(x, y, z)) == 0) {
            return true;
        }
        if (blockId == 1) {
            return false;
        }
        this.lastBlockRevision = this.chunk.getBlockChunk().getSectionAtBlockY(y).getLocalChangeCounter();
        if (BlockSetModule.getInstance().blockInSet(this.blockSet, blockId)) {
            this.hitPosition.assign(x, y, z);
        }
        return false;
    }

    @Nonnull
    public Vector3d getHitPosition() {
        return this.hitPosition;
    }

    public short getLastBlockRevision() {
        return this.lastBlockRevision;
    }

    public boolean init(@Nonnull Ref<EntityStore> ref, int blockSet, float pitch, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        TransformComponent transformComponent = componentAccessor.getComponent(ref, TransformComponent.getComponentType());
        assert (transformComponent != null);
        ModelComponent modelComponent = componentAccessor.getComponent(ref, ModelComponent.getComponentType());
        assert (modelComponent != null);
        HeadRotation headRotationComponent = componentAccessor.getComponent(ref, HeadRotation.getComponentType());
        assert (headRotationComponent != null);
        World world = componentAccessor.getExternalData().getWorld();
        this.blockSet = blockSet;
        this.origin.assign(transformComponent.getPosition());
        this.origin.y += (double)modelComponent.getModel().getEyeHeight();
        this.world = world;
        this.chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(this.origin.x, this.origin.z));
        if (this.chunk == null) {
            return false;
        }
        float yaw = headRotationComponent.getRotation().getYaw();
        this.direction.assign(yaw, pitch);
        return true;
    }

    public boolean run(double range) {
        BlockIterator.iterate(this.origin, this.direction, range, this);
        return !this.hitPosition.equals(Vector3d.MIN);
    }

    public void clear() {
        this.world = null;
        this.chunk = null;
        this.hitPosition.assign(Vector3d.MIN);
    }
}

