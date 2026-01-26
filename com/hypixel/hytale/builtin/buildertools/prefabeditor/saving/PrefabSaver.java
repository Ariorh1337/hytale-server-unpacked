/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.prefabeditor.saving;

import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.saving.PrefabSaverSettings;
import com.hypixel.hytale.component.ComponentRegistry;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.math.vector.VectorBoxUtil;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.blocktype.component.BlockPhysics;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.BlockEntity;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.prefab.PrefabCopyableComponent;
import com.hypixel.hytale.server.core.prefab.PrefabSaveException;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.ChunkColumn;
import com.hypixel.hytale.server.core.universe.world.chunk.EntityChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.FluidSection;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PrefabSaver {
    protected static final String EDITOR_BLOCK = "Editor_Block";
    protected static final String EDITOR_BLOCK_PREFAB_AIR = "Editor_Empty";
    protected static final String EDITOR_BLOCK_PREFAB_ANCHOR = "Editor_Anchor";

    @Nonnull
    public static CompletableFuture<Boolean> savePrefab(@Nonnull CommandSender sender, @Nonnull World world, @Nonnull Path pathToSave, @Nonnull Vector3i anchorPoint, @Nonnull Vector3i minPoint, @Nonnull Vector3i maxPoint, @Nonnull Vector3i pastePosition, @Nonnull Vector3i originalFileAnchor, @Nonnull PrefabSaverSettings settings) {
        return PrefabSaver.copyBlocksAsync(sender, world, anchorPoint, minPoint, maxPoint, pastePosition, originalFileAnchor, settings).thenApplyAsync(blockSelection -> {
            if (blockSelection == null) {
                return false;
            }
            return PrefabSaver.save(sender, blockSelection, pathToSave, settings);
        }, (Executor)world);
    }

    @Nonnull
    private static CompletableFuture<BlockSelection> copyBlocksAsync(@Nonnull CommandSender sender, @Nonnull World world, @Nonnull Vector3i anchorPoint, @Nonnull Vector3i minPoint, @Nonnull Vector3i maxPoint, @Nonnull Vector3i pastePosition, @Nonnull Vector3i originalFileAnchor, @Nonnull PrefabSaverSettings settings) {
        ChunkStore chunkStore = world.getChunkStore();
        BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
        int editorBlock = assetMap.getIndex(EDITOR_BLOCK);
        if (editorBlock == Integer.MIN_VALUE) {
            sender.sendMessage(Message.translation("server.commands.editprefab.save.error.unknownBlockIdKey").param("key", EDITOR_BLOCK.toString()));
            return CompletableFuture.completedFuture(null);
        }
        int editorBlockPrefabAir = assetMap.getIndex(EDITOR_BLOCK_PREFAB_AIR);
        if (editorBlockPrefabAir == Integer.MIN_VALUE) {
            sender.sendMessage(Message.translation("server.commands.editprefab.save.error.unknownBlockIdKey").param("key", EDITOR_BLOCK_PREFAB_AIR.toString()));
            return CompletableFuture.completedFuture(null);
        }
        int editorBlockPrefabAnchor = assetMap.getIndex(EDITOR_BLOCK_PREFAB_ANCHOR);
        if (editorBlockPrefabAnchor == Integer.MIN_VALUE) {
            sender.sendMessage(Message.translation("server.commands.editprefab.save.error.unknownBlockIdKey").param("key", EDITOR_BLOCK_PREFAB_ANCHOR.toString()));
            return CompletableFuture.completedFuture(null);
        }
        return PrefabSaver.preloadChunksInSelectionAsync(chunkStore, minPoint, maxPoint).thenApplyAsync(loadedChunks -> PrefabSaver.copyBlocksWithLoadedChunks(sender, world, anchorPoint, minPoint, maxPoint, pastePosition, originalFileAnchor, settings, loadedChunks, editorBlock, editorBlockPrefabAir, editorBlockPrefabAnchor), (Executor)world);
    }

    @Nullable
    private static BlockSelection copyBlocksWithLoadedChunks(@Nonnull CommandSender sender, @Nonnull World world, @Nonnull Vector3i anchorPoint, @Nonnull Vector3i minPoint, @Nonnull Vector3i maxPoint, @Nonnull Vector3i pastePosition, @Nonnull Vector3i originalFileAnchor, @Nonnull PrefabSaverSettings settings, @Nonnull Long2ObjectMap<Ref<ChunkStore>> loadedChunks, int editorBlock, int editorBlockPrefabAir, int editorBlockPrefabAnchor) {
        ChunkStore chunkStore = world.getChunkStore();
        long start = System.nanoTime();
        int width = maxPoint.x - minPoint.x;
        int height = maxPoint.y - minPoint.y;
        int depth = maxPoint.z - minPoint.z;
        int newAnchorX = anchorPoint.x - pastePosition.x;
        int newAnchorY = anchorPoint.y - pastePosition.y;
        int newAnchorZ = anchorPoint.z - pastePosition.z;
        BlockSelection selection = new BlockSelection();
        selection.setPosition(pastePosition.x - originalFileAnchor.x, pastePosition.y - originalFileAnchor.y, pastePosition.z - originalFileAnchor.z);
        selection.setSelectionArea(minPoint, maxPoint);
        selection.setAnchor(newAnchorX, newAnchorY, newAnchorZ);
        int blockCount = 0;
        int fluidCount = 0;
        int top = Math.max(minPoint.y, maxPoint.y);
        int bottom = Math.min(minPoint.y, maxPoint.y);
        for (int x = minPoint.x; x <= maxPoint.x; ++x) {
            for (int z = minPoint.z; z <= maxPoint.z; ++z) {
                long chunkIndex = ChunkUtil.indexChunkFromBlock(x, z);
                Ref chunkRef = (Ref)loadedChunks.get(chunkIndex);
                if (chunkRef == null || !chunkRef.isValid()) continue;
                WorldChunk worldChunkComponent = chunkStore.getStore().getComponent(chunkRef, WorldChunk.getComponentType());
                assert (worldChunkComponent != null);
                ChunkColumn chunkColumnComponent = chunkStore.getStore().getComponent(chunkRef, ChunkColumn.getComponentType());
                assert (chunkColumnComponent != null);
                for (int y = top; y >= bottom; --y) {
                    int sectionIndex = ChunkUtil.indexSection(y);
                    Ref<ChunkStore> sectionRef = chunkColumnComponent.getSection(sectionIndex);
                    if (sectionRef == null || !sectionRef.isValid()) continue;
                    BlockSection sectionComponent = chunkStore.getStore().getComponent(sectionRef, BlockSection.getComponentType());
                    assert (sectionComponent != null);
                    BlockPhysics blockPhysicsComponent = chunkStore.getStore().getComponent(sectionRef, BlockPhysics.getComponentType());
                    int block = sectionComponent.get(x, y, z);
                    int filler = sectionComponent.getFiller(x, y, z);
                    if (settings.isBlocks() && (block != 0 || settings.isEmpty()) && block != editorBlock) {
                        BlockState blockState;
                        Object holder;
                        if (block == editorBlockPrefabAir) {
                            block = 0;
                        }
                        if ((holder = worldChunkComponent.getBlockComponentHolder(x, y, z)) != null && (blockState = BlockState.getBlockState((Holder<ChunkStore>)(holder = ((Holder)holder).clone()))) != null) {
                            blockState.clearPositionForSerialization();
                        }
                        selection.addBlockAtWorldPos(x, y, z, block, sectionComponent.getRotationIndex(x, y, z), filler, blockPhysicsComponent != null ? blockPhysicsComponent.get(x, y, z) : 0, (Holder<ChunkStore>)holder);
                        ++blockCount;
                    }
                    FluidSection fluidSectionComponent = chunkStore.getStore().getComponent(sectionRef, FluidSection.getComponentType());
                    assert (fluidSectionComponent != null);
                    int fluid = fluidSectionComponent.getFluidId(x, y, z);
                    if (!settings.isBlocks() || fluid == 0 && !settings.isEmpty()) continue;
                    byte fluidLevel = fluidSectionComponent.getFluidLevel(x, y, z);
                    selection.addFluidAtWorldPos(x, y, z, fluid, fluidLevel);
                    ++fluidCount;
                }
            }
        }
        if (settings.isEntities()) {
            Store<EntityStore> store = world.getEntityStore().getStore();
            ComponentType<EntityStore, BlockEntity> blockEntityType = BlockEntity.getComponentType();
            HashSet<UUID> addedEntityUuids = new HashSet<UUID>();
            ComponentRegistry.Data<EntityStore> data = EntityStore.REGISTRY.getData();
            ComponentType<EntityStore, PrefabCopyableComponent> prefabCopyableType = PrefabCopyableComponent.getComponentType();
            ComponentType<EntityStore, TransformComponent> transformType = TransformComponent.getComponentType();
            BuilderToolsPlugin.forEachCopyableInSelection(world, minPoint.x, minPoint.y, minPoint.z, width, height, depth, e -> {
                TransformComponent transform;
                String key;
                BlockEntity blockEntity = (BlockEntity)store.getComponent((Ref<EntityStore>)e, blockEntityType);
                if (blockEntity != null && (key = blockEntity.getBlockTypeKey()) != null && (key.equals(EDITOR_BLOCK) || key.equals(EDITOR_BLOCK_PREFAB_AIR) || key.equals(EDITOR_BLOCK_PREFAB_ANCHOR))) {
                    return;
                }
                Holder<EntityStore> holder = store.copyEntity((Ref<EntityStore>)e);
                UUIDComponent uuidComp = holder.getComponent(UUIDComponent.getComponentType());
                if (uuidComp != null) {
                    addedEntityUuids.add(uuidComp.getUuid());
                }
                if ((transform = (TransformComponent)holder.getComponent(transformType)) != null && transform.getPosition() != null) {
                    transform.getPosition().subtract(selection.getX(), selection.getY(), selection.getZ());
                }
                selection.addEntityHolderRaw(holder);
            });
            for (Ref chunkRef : loadedChunks.values()) {
                EntityChunk entityChunk = chunkStore.getStore().getComponent(chunkRef, EntityChunk.getComponentType());
                if (entityChunk == null) continue;
                List<Holder<EntityStore>> holders = entityChunk.getEntityHolders();
                for (Holder<EntityStore> holder : holders) {
                    Object clonedHolder;
                    TransformComponent clonedTransform;
                    String key;
                    BlockEntity blockEntity;
                    UUIDComponent uuidComp = holder.getComponent(UUIDComponent.getComponentType());
                    TransformComponent transform = holder.getComponent(transformType);
                    Vector3d position = transform != null ? transform.getPosition() : null;
                    boolean hasPrefabCopyable = holder.getArchetype().contains(prefabCopyableType);
                    boolean hasSerializable = holder.hasSerializableComponents(data);
                    if (!hasPrefabCopyable || !hasSerializable || (blockEntity = holder.getComponent(blockEntityType)) != null && (key = blockEntity.getBlockTypeKey()) != null && (key.equals(EDITOR_BLOCK) || key.equals(EDITOR_BLOCK_PREFAB_AIR) || key.equals(EDITOR_BLOCK_PREFAB_ANCHOR)) || transform == null || position == null || !VectorBoxUtil.isInside(minPoint.x, minPoint.y, minPoint.z, 0.0, 0.0, 0.0, width + 1, height + 1, depth + 1, position) || uuidComp != null && addedEntityUuids.contains(uuidComp.getUuid())) continue;
                    if (uuidComp != null) {
                        addedEntityUuids.add(uuidComp.getUuid());
                    }
                    if ((clonedTransform = ((Holder)(clonedHolder = holder.clone())).getComponent(transformType)) != null && clonedTransform.getPosition() != null) {
                        clonedTransform.getPosition().subtract(selection.getX(), selection.getY(), selection.getZ());
                    }
                    selection.addEntityHolderRaw((Holder<EntityStore>)clonedHolder);
                }
            }
            selection.sortEntitiesByPosition();
        }
        long end = System.nanoTime();
        long diff = end - start;
        BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute copy of %d blocks, %d fluids", diff, TimeUnit.NANOSECONDS.toMillis(diff), blockCount, fluidCount);
        return selection;
    }

    @Nonnull
    private static CompletableFuture<Long2ObjectMap<Ref<ChunkStore>>> preloadChunksInSelectionAsync(@Nonnull ChunkStore chunkStore, @Nonnull Vector3i minPoint, @Nonnull Vector3i maxPoint) {
        LongOpenHashSet chunkIndices = new LongOpenHashSet();
        int minChunkX = minPoint.x >> 5;
        int maxChunkX = maxPoint.x >> 5;
        int minChunkZ = minPoint.z >> 5;
        int maxChunkZ = maxPoint.z >> 5;
        for (int cx = minChunkX; cx <= maxChunkX; ++cx) {
            for (int cz = minChunkZ; cz <= maxChunkZ; ++cz) {
                chunkIndices.add(ChunkUtil.indexChunk(cx, cz));
            }
        }
        Long2ObjectOpenHashMap loadedChunks = new Long2ObjectOpenHashMap(chunkIndices.size());
        ArrayList<CompletionStage> chunkFutures = new ArrayList<CompletionStage>(chunkIndices.size());
        LongIterator longIterator = chunkIndices.iterator();
        while (longIterator.hasNext()) {
            long chunkIndex = (Long)longIterator.next();
            CompletionStage future = chunkStore.getChunkReferenceAsync(chunkIndex).thenAccept(reference -> {
                if (reference != null && reference.isValid()) {
                    Long2ObjectMap long2ObjectMap = loadedChunks;
                    synchronized (long2ObjectMap) {
                        loadedChunks.put(chunkIndex, reference);
                    }
                }
            });
            chunkFutures.add(future);
        }
        return CompletableFuture.allOf((CompletableFuture[])chunkFutures.toArray(CompletableFuture[]::new)).thenApply(v -> loadedChunks);
    }

    private static boolean save(@Nonnull CommandSender sender, @Nonnull BlockSelection copiedSelection, @Nonnull Path saveFilePath, @Nonnull PrefabSaverSettings settings) {
        if (saveFilePath.getFileSystem() != FileSystems.getDefault()) {
            sender.sendMessage(Message.translation("server.builderTools.cannotSaveToReadOnlyPath").param("path", saveFilePath.toString()));
            return false;
        }
        try {
            long start = System.nanoTime();
            BlockSelection postClone = settings.isRelativize() ? copiedSelection.relativize() : copiedSelection.cloneSelection();
            PrefabStore.get().savePrefab(saveFilePath, postClone, settings.isOverwriteExisting());
            long diff = System.nanoTime() - start;
            BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute save of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), copiedSelection.getBlockCount());
            return true;
        }
        catch (PrefabSaveException e) {
            switch (e.getType()) {
                case ERROR: {
                    ((HytaleLogger.Api)BuilderToolsPlugin.get().getLogger().at(Level.WARNING).withCause(e)).log("Exception saving prefab %s", saveFilePath);
                    sender.sendMessage(Message.translation("server.builderTools.errorSavingPrefab").param("name", saveFilePath.toString()).param("message", e.getCause().getMessage()));
                    break;
                }
                case ALREADY_EXISTS: {
                    BuilderToolsPlugin.get().getLogger().at(Level.WARNING).log("Prefab already exists %s", saveFilePath.toString());
                    sender.sendMessage(Message.translation("server.builderTools.prefabAlreadyExists"));
                }
            }
            return false;
        }
    }
}

