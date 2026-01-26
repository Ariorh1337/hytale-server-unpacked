/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.entitystats.asset.condition;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.BlockMaterial;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.modules.collision.WorldUtil;
import com.hypixel.hytale.server.core.modules.entitystats.asset.condition.Condition;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import java.time.Instant;
import javax.annotation.Nonnull;

public class SuffocatingCondition
extends Condition {
    @Nonnull
    public static final BuilderCodec<SuffocatingCondition> CODEC = BuilderCodec.builder(SuffocatingCondition.class, SuffocatingCondition::new, Condition.BASE_CODEC).build();

    protected SuffocatingCondition() {
    }

    public SuffocatingCondition(boolean inverse) {
        super(inverse);
    }

    @Override
    public boolean eval0(@Nonnull ComponentAccessor<EntityStore> componentAccessor, @Nonnull Ref<EntityStore> ref, @Nonnull Instant currentTime) {
        int fluidId;
        Entity entity = EntityUtils.getEntity(ref, componentAccessor);
        if (!(entity instanceof LivingEntity)) {
            return false;
        }
        LivingEntity livingEntity = (LivingEntity)entity;
        World world = componentAccessor.getExternalData().getWorld();
        Transform lookVec = TargetUtil.getLook(ref, componentAccessor);
        Vector3d position = lookVec.getPosition();
        ChunkStore chunkStore = world.getChunkStore();
        long chunkIndex = ChunkUtil.indexChunkFromBlock(position.x, position.z);
        Ref<ChunkStore> chunkRef = chunkStore.getChunkReference(chunkIndex);
        if (chunkRef == null || !chunkRef.isValid()) {
            return false;
        }
        long packed = WorldUtil.getPackedMaterialAndFluidAtPosition(chunkRef, chunkStore.getStore(), position.x, position.y, position.z);
        BlockMaterial material = BlockMaterial.VALUES[MathUtil.unpackLeft(packed)];
        return !livingEntity.canBreathe(ref, material, fluidId = MathUtil.unpackRight(packed), componentAccessor);
    }

    @Override
    @Nonnull
    public String toString() {
        return "SuffocatingCondition{} " + super.toString();
    }
}

