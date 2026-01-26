/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.farming.interactions;

import com.hypixel.hytale.builtin.adventure.farming.states.CoopBlock;
import com.hypixel.hytale.builtin.tagset.TagSetPlugin;
import com.hypixel.hytale.builtin.tagset.config.NPCGroup;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockFace;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.metadata.CapturedNPCMetadata;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class UseCaptureCrateInteraction
extends SimpleBlockInteraction {
    public static final BuilderCodec<UseCaptureCrateInteraction> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(UseCaptureCrateInteraction.class, UseCaptureCrateInteraction::new, SimpleInteraction.CODEC).appendInherited(new KeyedCodec<String[]>("AcceptedNpcGroups", NPCGroup.CHILD_ASSET_CODEC_ARRAY), (o, v) -> {
        o.acceptedNpcGroupIds = v;
    }, o -> o.acceptedNpcGroupIds, (o, p) -> {
        o.acceptedNpcGroupIds = p.acceptedNpcGroupIds;
    }).addValidator(NPCGroup.VALIDATOR_CACHE.getArrayValidator()).add()).appendInherited(new KeyedCodec<String>("FullIcon", Codec.STRING), (o, v) -> {
        o.fullIcon = v;
    }, o -> o.fullIcon, (o, p) -> {
        o.fullIcon = p.fullIcon;
    }).add()).afterDecode(captureData -> {
        if (captureData.acceptedNpcGroupIds != null) {
            captureData.acceptedNpcGroupIndexes = new int[captureData.acceptedNpcGroupIds.length];
            for (int i = 0; i < captureData.acceptedNpcGroupIds.length; ++i) {
                int assetIdx;
                captureData.acceptedNpcGroupIndexes[i] = assetIdx = NPCGroup.getAssetMap().getIndex(captureData.acceptedNpcGroupIds[i]);
            }
        }
    })).build();
    protected String[] acceptedNpcGroupIds;
    protected int[] acceptedNpcGroupIndexes;
    protected String fullIcon;

    @Override
    protected void tick0(boolean firstRun, float time, @NonNullDecl InteractionType type, @NonNullDecl InteractionContext context, @NonNullDecl CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        if (commandBuffer == null) {
            context.getState().state = InteractionState.Failed;
            return;
        }
        ItemStack item = context.getHeldItem();
        if (item == null) {
            context.getState().state = InteractionState.Failed;
            return;
        }
        Ref<EntityStore> playerRef = context.getEntity();
        LivingEntity playerEntity = (LivingEntity)EntityUtils.getEntity(playerRef, commandBuffer);
        Inventory playerInventory = playerEntity.getInventory();
        byte activeHotbarSlot = playerInventory.getActiveHotbarSlot();
        ItemStack inHandItemStack = playerInventory.getActiveHotbarItem();
        CapturedNPCMetadata existingMeta = item.getFromMetadataOrNull("CapturedEntity", CapturedNPCMetadata.CODEC);
        if (existingMeta == null) {
            Ref<EntityStore> targetEntity = context.getTargetEntity();
            if (targetEntity == null) {
                context.getState().state = InteractionState.Failed;
                return;
            }
            NPCEntity npc = commandBuffer.getComponent(targetEntity, NPCEntity.getComponentType());
            if (npc == null) {
                context.getState().state = InteractionState.Failed;
                return;
            }
            TagSetPlugin.TagSetLookup tagSetPlugin = TagSetPlugin.get(NPCGroup.class);
            boolean tagFound = false;
            for (int group : this.acceptedNpcGroupIndexes) {
                if (!tagSetPlugin.tagInSet(group, npc.getRoleIndex())) continue;
                tagFound = true;
                break;
            }
            if (!tagFound) {
                context.getState().state = InteractionState.Failed;
                return;
            }
            PersistentModel persistentModel = commandBuffer.getComponent(targetEntity, PersistentModel.getComponentType());
            if (persistentModel == null) {
                context.getState().state = InteractionState.Failed;
                return;
            }
            ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset(persistentModel.getModelReference().getModelAssetId());
            CapturedNPCMetadata meta = inHandItemStack.getFromMetadataOrDefault("CapturedEntity", CapturedNPCMetadata.CODEC);
            if (modelAsset != null) {
                meta.setIconPath(modelAsset.getIcon());
            }
            meta.setRoleIndex(npc.getRoleIndex());
            String npcName = NPCPlugin.get().getName(npc.getRoleIndex());
            if (npcName != null) {
                meta.setNpcNameKey(npcName);
            }
            if (this.fullIcon != null) {
                meta.setFullItemIcon(this.fullIcon);
            }
            ItemStack itemWithNPC = inHandItemStack.withMetadata(CapturedNPCMetadata.KEYED_CODEC, meta);
            playerInventory.getHotbar().replaceItemStackInSlot(activeHotbarSlot, item, itemWithNPC);
            commandBuffer.removeEntity(targetEntity, RemoveReason.REMOVE);
            return;
        }
        super.tick0(firstRun, time, type, context, cooldownHandler);
    }

    @Override
    protected void interactWithBlock(@NonNullDecl World world, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl InteractionType type, @NonNullDecl InteractionContext context, @NullableDecl ItemStack itemInHand, @NonNullDecl Vector3i targetBlock, @NonNullDecl CooldownHandler cooldownHandler) {
        BlockFace blockFace;
        Store<ChunkStore> chunkStore;
        CoopBlock coopBlockState;
        ItemStack item = context.getHeldItem();
        if (item == null) {
            context.getState().state = InteractionState.Failed;
            return;
        }
        Ref<EntityStore> playerRef = context.getEntity();
        LivingEntity playerEntity = (LivingEntity)EntityUtils.getEntity(playerRef, commandBuffer);
        Inventory playerInventory = playerEntity.getInventory();
        byte activeHotbarSlot = playerInventory.getActiveHotbarSlot();
        CapturedNPCMetadata existingMeta = item.getFromMetadataOrNull("CapturedEntity", CapturedNPCMetadata.CODEC);
        if (existingMeta == null) {
            context.getState().state = InteractionState.Failed;
            return;
        }
        BlockPosition pos = context.getTargetBlock();
        if (pos == null) {
            context.getState().state = InteractionState.Failed;
            return;
        }
        Object worldChunk = world.getChunk(ChunkUtil.indexChunkFromBlock(pos.x, pos.z));
        Ref<ChunkStore> blockRef = ((WorldChunk)worldChunk).getBlockComponentEntity(pos.x, pos.y, pos.z);
        if (blockRef == null) {
            blockRef = BlockModule.ensureBlockEntity(worldChunk, pos.x, pos.y, pos.z);
        }
        ItemStack noMetaItemStack = item.withMetadata(null);
        if (blockRef != null && (coopBlockState = (chunkStore = world.getChunkStore().getStore()).getComponent(blockRef, CoopBlock.getComponentType())) != null) {
            WorldTimeResource worldTimeResource = commandBuffer.getResource(WorldTimeResource.getResourceType());
            if (coopBlockState.tryPutResident(existingMeta, worldTimeResource)) {
                world.execute(() -> coopBlockState.ensureSpawnResidentsInWorld(world, world.getEntityStore().getStore(), new Vector3d(pos.x, pos.y, pos.z), new Vector3d().assign(Vector3d.FORWARD)));
                playerInventory.getHotbar().replaceItemStackInSlot(activeHotbarSlot, item, noMetaItemStack);
            } else {
                context.getState().state = InteractionState.Failed;
            }
            return;
        }
        Vector3d spawnPos = new Vector3d((float)pos.x + 0.5f, pos.y, (float)pos.z + 0.5f);
        if (context.getClientState() != null && (blockFace = BlockFace.fromProtocolFace(context.getClientState().blockFace)) != null) {
            spawnPos.add(blockFace.getDirection());
        }
        NPCPlugin npcModule = NPCPlugin.get();
        Store<EntityStore> store = context.getCommandBuffer().getStore();
        int roleIndex = existingMeta.getRoleIndex();
        commandBuffer.run(_store -> npcModule.spawnEntity(store, roleIndex, spawnPos, Vector3f.ZERO, null, null));
        playerInventory.getHotbar().replaceItemStackInSlot(activeHotbarSlot, item, noMetaItemStack);
    }

    @Override
    protected void simulateInteractWithBlock(@NonNullDecl InteractionType type, @NonNullDecl InteractionContext context, @NullableDecl ItemStack itemInHand, @NonNullDecl World world, @NonNullDecl Vector3i targetBlock) {
    }
}

