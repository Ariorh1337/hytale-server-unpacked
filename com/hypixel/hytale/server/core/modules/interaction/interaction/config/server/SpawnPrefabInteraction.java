/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.server;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.FastRandom;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.prefab.selection.buffer.PrefabBufferUtil;
import com.hypixel.hytale.server.core.prefab.selection.buffer.impl.IPrefabBuffer;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.PrefabUtil;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public class SpawnPrefabInteraction
extends SimpleInstantInteraction {
    public static final BuilderCodec<SpawnPrefabInteraction> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(SpawnPrefabInteraction.class, SpawnPrefabInteraction::new, SimpleInstantInteraction.CODEC).documentation("Spawns a prefab at the current location.")).appendInherited(new KeyedCodec<String>("PrefabPath", Codec.STRING), (o, i) -> {
        o.prefabPath = i;
    }, o -> o.prefabPath, (o, p) -> {
        o.prefabPath = p.prefabPath;
    }).add()).appendInherited(new KeyedCodec<Vector3i>("Offset", Vector3i.CODEC), (o, i) -> {
        o.offset = i;
    }, o -> o.offset, (o, p) -> {
        o.offset = p.offset;
    }).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<Rotation>("RotationYaw", Rotation.CODEC), (o, i) -> {
        o.rotationYaw = i;
    }, o -> o.rotationYaw, (o, p) -> {
        o.rotationYaw = p.rotationYaw;
    }).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<OriginSource>("OriginSource", new EnumCodec<OriginSource>(OriginSource.class)), (o, i) -> {
        o.originSource = i;
    }, o -> o.originSource, (o, p) -> {
        o.originSource = p.originSource;
    }).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<Boolean>("Force", Codec.BOOLEAN), (o, i) -> {
        o.force = i;
    }, o -> o.force, (o, p) -> {
        o.force = p.force;
    }).add()).build();
    private String prefabPath;
    private Vector3i offset = Vector3i.ZERO;
    private Rotation rotationYaw = Rotation.None;
    @Nonnull
    private OriginSource originSource = OriginSource.ENTITY;
    private boolean force;

    @Override
    protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        Vector3i target;
        Path prefabRoot = PrefabStore.get().getAssetPrefabsPath();
        IPrefabBuffer prefab = PrefabBufferUtil.getCached(prefabRoot.resolve(this.prefabPath));
        if (prefab == null) {
            return;
        }
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        World world = commandBuffer.getExternalData().getWorld();
        Ref<EntityStore> ref = context.getEntity();
        TransformComponent transformComponent = commandBuffer.getComponent(ref, TransformComponent.getComponentType());
        assert (transformComponent != null);
        Vector3d entityPosition = transformComponent.getPosition();
        Rotation yaw = this.rotationYaw;
        switch (this.originSource.ordinal()) {
            case 0: {
                target = entityPosition.toVector3i();
                target.add(this.offset);
                break;
            }
            case 1: {
                BlockPosition targetBlock = context.getTargetBlock();
                if (targetBlock == null) {
                    return;
                }
                WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(targetBlock.x, targetBlock.z));
                if (chunk == null) {
                    return;
                }
                Rotation blockYaw = chunk.getRotation(targetBlock.x, targetBlock.y, targetBlock.z).yaw();
                target = new Vector3i();
                blockYaw.rotateYaw(this.offset, target);
                yaw = yaw.add(blockYaw);
                target.add(targetBlock.x, targetBlock.y, targetBlock.z);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unhandled origin source");
            }
        }
        PrefabUtil.paste(prefab, world, target, yaw, this.force, new FastRandom(), commandBuffer);
    }

    private static enum OriginSource {
        ENTITY,
        BLOCK;

    }
}

