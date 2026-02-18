/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.teleporter.page;

import com.hypixel.hytale.builtin.adventure.teleporter.component.Teleporter;
import com.hypixel.hytale.builtin.adventure.teleporter.page.TeleporterSettingsPage;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.OpenCustomUIInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TeleporterSettingsPageSupplier
implements OpenCustomUIInteraction.CustomPageSupplier {
    @Nonnull
    public static final BuilderCodec<TeleporterSettingsPageSupplier> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(TeleporterSettingsPageSupplier.class, TeleporterSettingsPageSupplier::new).appendInherited(new KeyedCodec<Boolean>("Create", Codec.BOOLEAN), (supplier, b) -> {
        supplier.create = b;
    }, supplier -> supplier.create, (supplier, parent) -> {
        supplier.create = parent.create;
    }).add()).appendInherited(new KeyedCodec<TeleporterSettingsPage.Mode>("Mode", TeleporterSettingsPage.Mode.CODEC), (supplier, o) -> {
        supplier.mode = o;
    }, supplier -> supplier.mode, (supplier, parent) -> {
        supplier.mode = parent.mode;
    }).add()).build();
    private boolean create = true;
    private TeleporterSettingsPage.Mode mode = TeleporterSettingsPage.Mode.FULL;

    @Override
    @Nullable
    public CustomUIPage tryCreate(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor, @Nonnull PlayerRef playerRef, @Nonnull InteractionContext context) {
        BlockComponentChunk blockComponentChunk;
        BlockPosition targetBlock = context.getTargetBlock();
        if (targetBlock == null) {
            return null;
        }
        Store<EntityStore> store = ref.getStore();
        World world = store.getExternalData().getWorld();
        ChunkStore chunkStore = world.getChunkStore();
        Store<ChunkStore> chunkComponentStore = chunkStore.getStore();
        Ref<ChunkStore> chunkRef = chunkStore.getChunkReference(ChunkUtil.indexChunkFromBlock(targetBlock.x, targetBlock.z));
        BlockComponentChunk blockComponentChunk2 = blockComponentChunk = chunkRef == null || !chunkRef.isValid() ? null : chunkComponentStore.getComponent(chunkRef, BlockComponentChunk.getComponentType());
        if (blockComponentChunk == null) {
            return null;
        }
        int blockIndex = ChunkUtil.indexBlockInColumn(targetBlock.x, targetBlock.y, targetBlock.z);
        Ref<ChunkStore> blockRef = blockComponentChunk.getEntityReference(blockIndex);
        if (blockRef == null || !blockRef.isValid()) {
            if (!this.create) {
                return null;
            }
            Holder<ChunkStore> holder = ChunkStore.REGISTRY.newHolder();
            holder.putComponent(BlockModule.BlockStateInfo.getComponentType(), new BlockModule.BlockStateInfo(blockIndex, chunkRef));
            holder.ensureComponent(Teleporter.getComponentType());
            blockRef = chunkComponentStore.addEntity(holder, AddReason.SPAWN);
        }
        if (blockRef == null || !blockRef.isValid()) {
            return null;
        }
        return new TeleporterSettingsPage(playerRef, blockRef, this.mode);
    }
}

