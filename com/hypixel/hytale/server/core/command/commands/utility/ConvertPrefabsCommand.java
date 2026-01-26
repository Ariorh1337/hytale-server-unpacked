/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.commands.utility;

import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.prefab.config.SelectionPrefabSerializer;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import com.hypixel.hytale.server.core.universe.world.storage.provider.EmptyChunkStorageProvider;
import com.hypixel.hytale.server.core.universe.world.storage.resources.EmptyResourceStorageProvider;
import com.hypixel.hytale.server.core.universe.world.worldgen.provider.DummyWorldGenProvider;
import com.hypixel.hytale.server.core.util.BsonUtil;
import com.hypixel.hytale.server.core.util.io.FileUtil;
import com.hypixel.hytale.server.core.util.message.MessageFormat;
import com.hypixel.hytale.sneakythrow.SneakyThrow;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonDocument;

public class ConvertPrefabsCommand
extends AbstractAsyncCommand {
    private static final String UNABLE_TO_LOAD_MODEL = "Unable to load entity with model ";
    private static final String FAILED_TO_FIND_BLOCK = "Failed to find block ";
    private static final int BATCH_SIZE = 10;
    private static final long DELAY_BETWEEN_BATCHES_MS = 50L;
    @Nonnull
    private static final Message MESSAGE_COMMANDS_CONVERT_PREFABS_FAILED = Message.translation("server.commands.convertprefabs.failed");
    @Nonnull
    private static final Message MESSAGE_COMMANDS_CONVERT_PREFABS_DEFAULT_WORLD_NULL = Message.translation("server.commands.convertprefabs.defaultWorldNull");
    @Nonnull
    private final FlagArg blocksFlag = this.withFlagArg("blocks", "server.commands.convertprefabs.blocks.desc");
    @Nonnull
    private final FlagArg fillerFlag = this.withFlagArg("filler", "server.commands.convertprefabs.filler.desc");
    @Nonnull
    private final FlagArg relativeFlag = this.withFlagArg("relative", "server.commands.convertprefabs.relative.desc");
    @Nonnull
    private final FlagArg entitiesFlag = this.withFlagArg("entities", "server.commands.convertprefabs.entities.desc");
    private final FlagArg destructiveFlag = this.withFlagArg("destructive", "server.commands.convertprefabs.destructive.desc");
    @Nonnull
    private final OptionalArg<String> pathArg = this.withOptionalArg("path", "server.commands.convertprefabs.path.desc", ArgTypes.STRING);
    @Nonnull
    private final DefaultArg<String> storeArg = this.withDefaultArg("store", "server.commands.convertprefabs.store.desc", ArgTypes.STRING, "asset", "server.commands.convertprefabs.store.defaultDesc");

    public ConvertPrefabsCommand() {
        super("convertprefabs", "server.commands.convertprefabs.desc");
    }

    @Override
    @Nonnull
    protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
        boolean blocks = (Boolean)this.blocksFlag.get(context);
        boolean filler = (Boolean)this.fillerFlag.get(context);
        boolean relative = (Boolean)this.relativeFlag.get(context);
        boolean entities = (Boolean)this.entitiesFlag.get(context);
        boolean destructive = (Boolean)this.destructiveFlag.get(context);
        World defaultWorld = Universe.get().getDefaultWorld();
        if (defaultWorld == null) {
            context.sendMessage(MESSAGE_COMMANDS_CONVERT_PREFABS_DEFAULT_WORLD_NULL);
            return CompletableFuture.completedFuture(null);
        }
        defaultWorld.getChunk(ChunkUtil.indexChunk(0, 0));
        ObjectArrayList<String> failed = new ObjectArrayList<String>();
        ObjectArrayList<String> skipped = new ObjectArrayList<String>();
        String storeOption = (String)this.storeArg.get(context);
        if (this.pathArg.provided(context)) {
            Path assetPath = Paths.get((String)this.pathArg.get(context), new String[0]);
            return this.convertPath(assetPath, blocks, filler, relative, entities, destructive, failed, skipped).thenApply(_v -> {
                this.sendCompletionMessages(context, assetPath, failed, skipped);
                return null;
            });
        }
        return switch (storeOption) {
            case "server" -> {
                Path assetPath = PrefabStore.get().getServerPrefabsPath();
                yield this.convertPath(assetPath, blocks, filler, relative, entities, destructive, failed, skipped).thenApply(_v -> {
                    this.sendCompletionMessages(context, assetPath, failed, skipped);
                    return null;
                });
            }
            case "asset" -> {
                Path assetPath = PrefabStore.get().getAssetPrefabsPath();
                yield this.convertPath(assetPath, blocks, filler, relative, entities, destructive, failed, skipped).thenApply(_v -> {
                    this.sendCompletionMessages(context, assetPath, failed, skipped);
                    return null;
                });
            }
            case "worldgen" -> {
                Path assetPath = PrefabStore.get().getWorldGenPrefabsPath();
                yield this.convertPath(assetPath, blocks, filler, relative, entities, destructive, failed, skipped).thenApply(_v -> {
                    this.sendCompletionMessages(context, assetPath, failed, skipped);
                    return null;
                });
            }
            case "all" -> {
                Path assetPath = Path.of("", new String[0]);
                yield ((CompletableFuture)((CompletableFuture)this.convertPath(PrefabStore.get().getWorldGenPrefabsPath(), blocks, filler, relative, entities, destructive, failed, skipped).thenCompose(_v -> this.convertPath(PrefabStore.get().getServerPrefabsPath(), blocks, filler, relative, entities, destructive, failed, skipped))).thenCompose(_v -> this.convertPath(PrefabStore.get().getAssetPrefabsPath(), blocks, filler, relative, entities, destructive, failed, skipped))).thenApply(_v -> {
                    this.sendCompletionMessages(context, assetPath, failed, skipped);
                    return null;
                });
            }
            default -> {
                context.sendMessage(Message.translation("server.commands.convertprefabs.invalidStore").param("store", storeOption));
                yield CompletableFuture.completedFuture(null);
            }
        };
    }

    private void sendCompletionMessages(@Nonnull CommandContext context, @Nonnull Path assetPath, @Nonnull List<String> failed, @Nonnull List<String> skipped) {
        if (!skipped.isEmpty()) {
            Message header = Message.translation("server.commands.convertprefabs.skipped");
            context.sendMessage(MessageFormat.list(header, skipped.stream().map(Message::raw).collect(Collectors.toSet())));
        }
        if (!failed.isEmpty()) {
            context.sendMessage(MessageFormat.list(MESSAGE_COMMANDS_CONVERT_PREFABS_FAILED, failed.stream().map(Message::raw).collect(Collectors.toSet())));
        }
        context.sendMessage(Message.translation("server.commands.prefabConvertionDone").param("path", assetPath.toString()));
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nonnull
    private CompletableFuture<Void> convertPath(@Nonnull Path assetPath, boolean blocks, boolean filler, boolean relative, boolean entities, boolean destructive, @Nonnull List<String> failed, @Nonnull List<String> skipped) {
        CompletableFuture<World> conversionWorldFuture;
        if (!Files.exists(assetPath, new LinkOption[0])) {
            return CompletableFuture.completedFuture(null);
        }
        if (entities || blocks) {
            Universe universe = Universe.get();
            WorldConfig config = new WorldConfig();
            config.setWorldGenProvider(new DummyWorldGenProvider());
            config.setChunkStorageProvider(EmptyChunkStorageProvider.INSTANCE);
            config.setResourceStorageProvider(EmptyResourceStorageProvider.INSTANCE);
            try {
                conversionWorldFuture = universe.makeWorld("ConvertPrefabs-" + String.valueOf(UUID.randomUUID()), Files.createTempDirectory("convertprefab", new FileAttribute[0]), config);
            }
            catch (IOException e) {
                throw SneakyThrow.sneakyThrow(e);
            }
        } else {
            conversionWorldFuture = null;
        }
        try (Stream<Path> stream = Files.walk(assetPath, FileUtil.DEFAULT_WALK_TREE_OPTIONS_ARRAY);){
            List<Path> prefabPaths = stream.filter(path -> Files.isRegularFile(path, new LinkOption[0]) && path.toString().endsWith(".prefab.json")).collect(Collectors.toList());
            if (prefabPaths.isEmpty()) {
                if (conversionWorldFuture != null) {
                    conversionWorldFuture.thenAccept(world -> Universe.get().removeWorld(world.getName()));
                }
                CompletableFuture<Object> completableFuture = CompletableFuture.completedFuture(null);
                return completableFuture;
            }
            CompletionStage completionStage = this.processPrefabsInBatches(prefabPaths, blocks, filler, relative, entities, destructive, conversionWorldFuture, failed, skipped).thenApply(_v -> {
                if (conversionWorldFuture != null) {
                    conversionWorldFuture.thenAccept(world -> Universe.get().removeWorld(world.getName()));
                }
                return null;
            });
            return completionStage;
        }
        catch (IOException e) {
            throw SneakyThrow.sneakyThrow(e);
        }
    }

    @Nonnull
    private CompletableFuture<Void> processPrefabsInBatches(@Nonnull List<Path> prefabPaths, boolean blocks, boolean filler, boolean relative, boolean entities, boolean destructive, @Nullable CompletableFuture<World> conversionWorldFuture, @Nonnull List<String> failed, @Nonnull List<String> skipped) {
        CompletionStage<Object> result = CompletableFuture.completedFuture(null);
        for (int i = 0; i < prefabPaths.size(); i += 10) {
            int batchEnd = Math.min(i + 10, prefabPaths.size());
            List<Path> batch = prefabPaths.subList(i, batchEnd);
            int batchIndex = i / 10;
            if (batchIndex > 0) {
                result = ((CompletableFuture)result).thenCompose(_v -> CompletableFuture.runAsync(() -> {}, CompletableFuture.delayedExecutor(50L, TimeUnit.MILLISECONDS)));
            }
            CompletableFuture[] batchFutures = (CompletableFuture[])batch.stream().map(path -> this.processPrefab((Path)path, blocks, filler, relative, entities, destructive, conversionWorldFuture, failed, skipped)).toArray(CompletableFuture[]::new);
            result = ((CompletableFuture)result).thenCompose(_v -> CompletableFuture.allOf(batchFutures));
        }
        return result;
    }

    @Nonnull
    private CompletableFuture<Void> processPrefab(@Nonnull Path path, boolean blocks, boolean filler, boolean relative, boolean entities, boolean destructive, @Nullable CompletableFuture<World> conversionWorldFuture, @Nonnull List<String> failed, @Nonnull List<String> skipped) {
        return ((CompletableFuture)((CompletableFuture)((CompletableFuture)((CompletableFuture)BsonUtil.readDocument(path, false).thenApply(document -> {
            BlockSelection prefab = SelectionPrefabSerializer.deserialize(document);
            if (filler) {
                prefab.tryFixFiller(destructive);
            }
            if (relative) {
                prefab = prefab.relativize();
            }
            return prefab;
        })).thenCompose(prefab -> {
            if (entities && conversionWorldFuture != null) {
                return ((CompletableFuture)conversionWorldFuture.thenCompose(world -> CompletableFuture.runAsync(() -> {
                    try {
                        prefab.reserializeEntities(world.getEntityStore().getStore(), destructive);
                    }
                    catch (IOException e) {
                        throw SneakyThrow.sneakyThrow(e);
                    }
                }, world))).thenApply(_v -> prefab);
            }
            return CompletableFuture.completedFuture(prefab);
        })).thenCompose(prefab -> {
            if (blocks && conversionWorldFuture != null) {
                return ((CompletableFuture)conversionWorldFuture.thenCompose(world -> CompletableFuture.runAsync(() -> prefab.reserializeBlockStates(world.getChunkStore(), destructive), world))).thenApply(_v -> prefab);
            }
            return CompletableFuture.completedFuture(prefab);
        })).thenCompose(prefab -> {
            BsonDocument newDocument = SelectionPrefabSerializer.serialize(prefab);
            return BsonUtil.writeDocument(path, newDocument, false);
        })).exceptionally(throwable -> {
            String message;
            String string = message = throwable.getCause() != null ? throwable.getCause().getMessage() : null;
            if (message != null) {
                if (message.contains(FAILED_TO_FIND_BLOCK)) {
                    if (message.substring(FAILED_TO_FIND_BLOCK.length()).contains("%")) {
                        skipped.add("Skipped prefab " + String.valueOf(path) + " because it contains block % chance.");
                        return null;
                    }
                    failed.add("Failed to update " + String.valueOf(path) + " because " + message);
                    return null;
                }
                if (message.contains(UNABLE_TO_LOAD_MODEL)) {
                    failed.add("Failed to update " + String.valueOf(path) + " because " + message);
                    return null;
                }
            }
            failed.add("Failed to update " + String.valueOf(path) + " because " + String.valueOf(message != null ? message : (throwable.getCause() != null ? throwable.getCause().getClass() : throwable.getClass())));
            if (throwable.getCause() != null) {
                new Exception("Failed to update " + String.valueOf(path), throwable.getCause()).printStackTrace();
            }
            return null;
        });
    }
}

