/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.server;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
import com.hypixel.hytale.server.core.entity.entities.player.pages.PageManager;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.plugin.PluginBase;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.universe.world.meta.BlockStateModule;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OpenCustomUIInteraction
extends SimpleInstantInteraction {
    public static final CodecMapCodec<CustomPageSupplier> PAGE_CODEC = new CodecMapCodec();
    public static final BuilderCodec<OpenCustomUIInteraction> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(OpenCustomUIInteraction.class, OpenCustomUIInteraction::new, SimpleInstantInteraction.CODEC).documentation("Opens a custom ui page.")).appendInherited(new KeyedCodec<CustomPageSupplier>("Page", PAGE_CODEC), (o, v) -> {
        o.customPageSupplier = v;
    }, o -> o.customPageSupplier, (o, p) -> {
        o.customPageSupplier = p.customPageSupplier;
    }).addValidator(Validators.nonNull()).add()).build();
    private CustomPageSupplier customPageSupplier;

    @Override
    protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        Ref<EntityStore> ref = context.getEntity();
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        Player playerComponent = commandBuffer.getComponent(ref, Player.getComponentType());
        if (playerComponent == null) {
            return;
        }
        PageManager pageManager = playerComponent.getPageManager();
        if (pageManager.getCustomPage() != null) {
            return;
        }
        PlayerRef playerRef = commandBuffer.getComponent(ref, PlayerRef.getComponentType());
        assert (playerRef != null);
        CustomUIPage page = this.customPageSupplier.tryCreate(ref, commandBuffer, playerRef, context);
        if (page != null) {
            Store<EntityStore> store = commandBuffer.getStore();
            pageManager.openCustomPage(ref, store, page);
        }
    }

    public static <S extends CustomPageSupplier> void registerCustomPageSupplier(@Nonnull PluginBase plugin, Class<?> tClass, String id, @Nonnull S supplier) {
        plugin.getCodecRegistry(PAGE_CODEC).register(id, supplier.getClass(), BuilderCodec.builder(tClass, () -> supplier).build());
    }

    public static void registerSimple(@Nonnull PluginBase plugin, Class<?> tClass, String id, @Nonnull Function<PlayerRef, CustomUIPage> supplier) {
        OpenCustomUIInteraction.registerCustomPageSupplier(plugin, tClass, id, (ref, componentAccessor, playerRef, context) -> (CustomUIPage)supplier.apply(playerRef));
    }

    @Deprecated
    public static <T extends BlockState> void registerBlockCustomPage(@Nonnull PluginBase plugin, Class<?> tClass, String id, @Nonnull Class<T> stateClass, @Nonnull BlockCustomPageSupplier<T> blockSupplier) {
        OpenCustomUIInteraction.registerBlockCustomPage(plugin, tClass, id, stateClass, blockSupplier, false);
    }

    @Deprecated
    public static <T extends BlockState> void registerBlockCustomPage(@Nonnull PluginBase plugin, Class<?> tClass, String id, @Nonnull Class<T> stateClass, @Nonnull BlockCustomPageSupplier<T> blockSupplier, boolean createState) {
        CustomPageSupplier supplier = (ref, componentAccessor, playerRef, context) -> {
            BlockPosition targetBlock = context.getTargetBlock();
            if (targetBlock == null) {
                return null;
            }
            Store store = ref.getStore();
            World world = ((EntityStore)store.getExternalData()).getWorld();
            BlockState state = world.getState(targetBlock.x, targetBlock.y, targetBlock.z, true);
            if (state == null) {
                if (createState) {
                    Object chunk = world.getChunk(ChunkUtil.indexChunkFromBlock(targetBlock.x, targetBlock.z));
                    state = BlockStateModule.get().createBlockState(stateClass, (WorldChunk)chunk, new Vector3i(targetBlock.x, targetBlock.y, targetBlock.z), chunk.getBlockType(targetBlock.x, targetBlock.y, targetBlock.z));
                    chunk.setState(targetBlock.x, targetBlock.y, targetBlock.z, state);
                }
                if (state == null) {
                    return null;
                }
            }
            if (stateClass.isInstance(state)) {
                return blockSupplier.tryCreate(playerRef, (BlockState)stateClass.cast(state));
            }
            return null;
        };
        OpenCustomUIInteraction.registerCustomPageSupplier(plugin, tClass, id, supplier);
    }

    public static void registerBlockEntityCustomPage(@Nonnull PluginBase plugin, Class<?> tClass, String id, @Nonnull BlockEntityCustomPageSupplier blockSupplier) {
        CustomPageSupplier supplier = (ref, componentAccessor, playerRef, context) -> {
            BlockPosition targetBlock = context.getTargetBlock();
            if (targetBlock == null) {
                return null;
            }
            Store store = ref.getStore();
            World world = ((EntityStore)store.getExternalData()).getWorld();
            WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(targetBlock.x, targetBlock.z));
            if (chunk == null) {
                return null;
            }
            BlockPosition targetBaseBlock = world.getBaseBlock(targetBlock);
            Ref<ChunkStore> blockEntityRef = chunk.getBlockComponentEntity(targetBaseBlock.x, targetBaseBlock.y, targetBaseBlock.z);
            if (blockEntityRef == null) {
                return null;
            }
            return blockSupplier.tryCreate(playerRef, blockEntityRef);
        };
        OpenCustomUIInteraction.registerCustomPageSupplier(plugin, tClass, id, supplier);
    }

    public static void registerBlockEntityCustomPage(@Nonnull PluginBase plugin, Class<?> tClass, String id, @Nonnull BlockEntityCustomPageSupplier blockSupplier, Supplier<Holder<ChunkStore>> creator) {
        CustomPageSupplier supplier = (ref, componentAccessor, playerRef, context) -> {
            int index;
            BlockPosition targetBlock = context.getTargetBlock();
            if (targetBlock == null) {
                return null;
            }
            Store store = ref.getStore();
            World world = ((EntityStore)store.getExternalData()).getWorld();
            WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(targetBlock.x, targetBlock.z));
            if (chunk == null) {
                return null;
            }
            BlockPosition targetBaseBlock = world.getBaseBlock(targetBlock);
            BlockComponentChunk blockComponentChunk = chunk.getBlockComponentChunk();
            Ref<ChunkStore> blockEntityRef = blockComponentChunk.getEntityReference(index = ChunkUtil.indexBlockInColumn(targetBaseBlock.x, targetBaseBlock.y, targetBaseBlock.z));
            if (blockEntityRef == null) {
                Holder holder = (Holder)creator.get();
                holder.putComponent(BlockModule.BlockStateInfo.getComponentType(), new BlockModule.BlockStateInfo(index, chunk.getReference()));
                blockEntityRef = world.getChunkStore().getStore().addEntity(holder, AddReason.SPAWN);
            }
            return blockSupplier.tryCreate(playerRef, blockEntityRef);
        };
        OpenCustomUIInteraction.registerCustomPageSupplier(plugin, tClass, id, supplier);
    }

    @FunctionalInterface
    public static interface CustomPageSupplier {
        @Nullable
        public CustomUIPage tryCreate(Ref<EntityStore> var1, ComponentAccessor<EntityStore> var2, PlayerRef var3, InteractionContext var4);
    }

    @FunctionalInterface
    public static interface BlockCustomPageSupplier<T extends BlockState> {
        public CustomUIPage tryCreate(PlayerRef var1, T var2);
    }

    @FunctionalInterface
    public static interface BlockEntityCustomPageSupplier {
        public CustomUIPage tryCreate(PlayerRef var1, Ref<ChunkStore> var2);
    }
}

