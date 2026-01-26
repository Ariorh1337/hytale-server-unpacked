/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.client;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.Interaction;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockFace;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockConditionInteraction
extends SimpleBlockInteraction {
    @Nonnull
    public static final BuilderCodec<BlockConditionInteraction> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(BlockConditionInteraction.class, BlockConditionInteraction::new, SimpleBlockInteraction.CODEC).documentation("Tests the target block and executes `Next` if it matches all the conditions, otherwise `Failed` is run.")).appendInherited(new KeyedCodec<T[]>("Matchers", new ArrayCodec<BlockMatcher>(BlockMatcher.CODEC, BlockMatcher[]::new)), (o, i) -> {
        o.matchers = i;
    }, o -> o.matchers, (o, p) -> {
        o.matchers = p.matchers;
    }).documentation("The matchers to test the block against.").add()).build();
    private BlockMatcher[] matchers;

    @Override
    protected void interactWithBlock(@Nonnull World world, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull Vector3i targetBlock, @Nonnull CooldownHandler cooldownHandler) {
        com.hypixel.hytale.protocol.BlockFace face = context.getClientState().blockFace;
        this.doInteraction(context, world, targetBlock, face);
    }

    @Override
    protected void simulateInteractWithBlock(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull World world, @Nonnull Vector3i targetBlock) {
        context.getState().blockFace = com.hypixel.hytale.protocol.BlockFace.Up;
        this.doInteraction(context, world, targetBlock, context.getState().blockFace);
    }

    private void doInteraction(@Nonnull InteractionContext context, @Nonnull World world, @Nonnull Vector3i targetBlock, @Nonnull com.hypixel.hytale.protocol.BlockFace face) {
        WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(targetBlock.x, targetBlock.z));
        if (chunk == null) {
            return;
        }
        BlockType blockType = chunk.getBlockType(targetBlock);
        RotationTuple blockRotation = chunk.getRotation(targetBlock.x, targetBlock.y, targetBlock.z);
        Item itemType = blockType.getItem();
        if (itemType == null) {
            context.getState().state = InteractionState.Failed;
            return;
        }
        boolean ok = false;
        for (BlockMatcher matcher : this.matchers) {
            if (matcher.face != com.hypixel.hytale.protocol.BlockFace.None) {
                com.hypixel.hytale.protocol.BlockFace transformedFace = matcher.face;
                if (!matcher.staticFace) {
                    Rotation yaw = blockRotation.yaw();
                    Rotation pitch = blockRotation.pitch();
                    BlockFace newFace = BlockFace.rotate(BlockFace.fromProtocolFace(transformedFace), yaw, pitch);
                    transformedFace = BlockFace.toProtocolFace(newFace);
                }
                if (!transformedFace.equals((Object)face)) continue;
            }
            if (matcher.block != null) {
                Int2ObjectMap<IntSet> tags;
                AssetExtraInfo.Data data;
                if (matcher.block.id != null && !matcher.block.id.equals(itemType.getId())) continue;
                if (matcher.block.state != null) {
                    String state = blockType.getStateForBlock(blockType);
                    if (state == null) {
                        state = "default";
                    }
                    if (!matcher.block.state.equals(state)) continue;
                }
                if (matcher.block.tag != null && ((data = blockType.getData()) == null || (tags = data.getTags()) == null || !tags.containsKey(matcher.block.tagIndex))) continue;
            }
            ok = true;
            break;
        }
        context.getState().state = ok ? InteractionState.Finished : InteractionState.Failed;
    }

    @Override
    @Nonnull
    protected Interaction generatePacket() {
        return new com.hypixel.hytale.protocol.BlockConditionInteraction();
    }

    @Override
    protected void configurePacket(Interaction packet) {
        super.configurePacket(packet);
        com.hypixel.hytale.protocol.BlockConditionInteraction p = (com.hypixel.hytale.protocol.BlockConditionInteraction)packet;
        if (this.matchers != null) {
            p.matchers = new com.hypixel.hytale.protocol.BlockMatcher[this.matchers.length];
            for (int i = 0; i < this.matchers.length; ++i) {
                p.matchers[i] = this.matchers[i].toPacket();
            }
        }
    }

    @Override
    @Nonnull
    public String toString() {
        return "BlockConditionInteraction{matchers=" + Arrays.toString(this.matchers) + "} " + super.toString();
    }

    public static class BlockMatcher
    implements NetworkSerializable<com.hypixel.hytale.protocol.BlockMatcher> {
        @Nonnull
        public static BuilderCodec<BlockMatcher> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(BlockMatcher.class, BlockMatcher::new).appendInherited(new KeyedCodec<BlockIdMatcher>("Block", BlockIdMatcher.CODEC), (blockMatcher, blockIdMatcher) -> {
            blockMatcher.block = blockIdMatcher;
        }, blockMatcher -> blockMatcher.block, (blockMatcher, parent) -> {
            blockMatcher.block = parent.block;
        }).documentation("Match against block values").add()).appendInherited(new KeyedCodec<BlockFace>("Face", BlockFace.CODEC), (blockMatcher, face) -> {
            blockMatcher.face = BlockFace.toProtocolFace(face);
        }, blockMatcher -> BlockFace.fromProtocolFace(blockMatcher.face), (blockMatcher, parent) -> {
            blockMatcher.face = parent.face;
        }).documentation("Match against a specific block face.").add()).appendInherited(new KeyedCodec<Boolean>("StaticFace", Codec.BOOLEAN), (blockMatcher, aBoolean) -> {
            blockMatcher.staticFace = aBoolean;
        }, blockMatcher -> blockMatcher.staticFace, (blockMatcher, parent) -> {
            blockMatcher.staticFace = parent.staticFace;
        }).documentation("Whether the face matching is unaffected by the block rotation or not.").add()).build();
        protected BlockIdMatcher block;
        protected com.hypixel.hytale.protocol.BlockFace face = com.hypixel.hytale.protocol.BlockFace.None;
        protected boolean staticFace;

        @Override
        @Nonnull
        public com.hypixel.hytale.protocol.BlockMatcher toPacket() {
            com.hypixel.hytale.protocol.BlockMatcher packet = new com.hypixel.hytale.protocol.BlockMatcher();
            if (this.block != null) {
                packet.block = this.block.toPacket();
            }
            if (this.face != null) {
                packet.face = this.face;
            }
            packet.staticFace = this.staticFace;
            return packet;
        }

        @Nonnull
        public String toString() {
            return "BlockMatcher{block=" + String.valueOf(this.block) + ", face=" + String.valueOf((Object)this.face) + ", staticFace=" + this.staticFace + "}";
        }
    }

    public static class BlockIdMatcher
    implements NetworkSerializable<com.hypixel.hytale.protocol.BlockIdMatcher> {
        @Nonnull
        public static BuilderCodec<BlockIdMatcher> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(BlockIdMatcher.class, BlockIdMatcher::new).appendInherited(new KeyedCodec<String>("Id", Codec.STRING), (blockIdMatcher, s) -> {
            blockIdMatcher.id = s;
        }, blockIdMatcher -> blockIdMatcher.id, (blockIdMatcher, parent) -> {
            blockIdMatcher.id = parent.id;
        }).addValidatorLate(() -> BlockType.VALIDATOR_CACHE.getValidator().late()).documentation("Match against a specific block id.").add()).appendInherited(new KeyedCodec<String>("State", Codec.STRING), (blockIdMatcher, s) -> {
            blockIdMatcher.state = s;
        }, blockIdMatcher -> blockIdMatcher.state, (blockIdMatcher, parent) -> {
            blockIdMatcher.state = parent.state;
        }).documentation("Match against specific block state.").add()).appendInherited(new KeyedCodec<String>("Tag", Codec.STRING), (blockIdMatcher, s) -> {
            blockIdMatcher.tag = s;
        }, blockIdMatcher -> blockIdMatcher.tag, (blockIdMatcher, parent) -> {
            blockIdMatcher.tag = parent.tag;
        }).documentation("Match against specific block tag.").add()).afterDecode(blockIdMatcher -> {
            if (blockIdMatcher.tag != null) {
                blockIdMatcher.tagIndex = AssetRegistry.getOrCreateTagIndex(blockIdMatcher.tag);
            }
        })).build();
        protected String id;
        protected String state;
        protected String tag;
        protected int tagIndex = Integer.MIN_VALUE;

        @Override
        @Nonnull
        public com.hypixel.hytale.protocol.BlockIdMatcher toPacket() {
            com.hypixel.hytale.protocol.BlockIdMatcher packet = new com.hypixel.hytale.protocol.BlockIdMatcher();
            if (this.id != null) {
                packet.id = this.id;
            }
            if (this.state != null) {
                packet.state = this.state;
            }
            packet.tagIndex = this.tagIndex;
            return packet;
        }

        @Nonnull
        public String toString() {
            return "BlockIdMatcher{id='" + this.id + "', state='" + this.state + "', tag='" + this.tag + "'}";
        }
    }
}

