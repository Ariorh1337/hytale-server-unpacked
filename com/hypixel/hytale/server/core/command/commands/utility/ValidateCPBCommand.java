/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.commands.utility;

import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.common.util.PathUtil;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.AssetModule;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.prefab.selection.buffer.BsonPrefabBufferDeserializer;
import com.hypixel.hytale.server.core.util.BsonUtil;
import com.hypixel.hytale.server.core.util.io.FileUtil;
import com.hypixel.hytale.sneakythrow.SneakyThrow;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.bson.BsonDocument;

public class ValidateCPBCommand
extends AbstractAsyncCommand {
    private static final String UNABLE_TO_LOAD_MODEL = "Unable to load entity with model ";
    private static final String FAILED_TO_FIND_BLOCK = "Failed to find block ";
    @Nonnull
    private final OptionalArg<String> pathArg = this.withOptionalArg("path", "server.commands.validatecpb.path.desc", ArgTypes.STRING);

    public ValidateCPBCommand() {
        super("validatecpb", "server.commands.validatecpb.desc");
    }

    @Override
    @Nonnull
    protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
        if (this.pathArg.provided(context)) {
            String path = (String)this.pathArg.get(context);
            return CompletableFuture.runAsync(() -> ValidateCPBCommand.convertPrefabs(context, PathUtil.get(path)));
        }
        return CompletableFuture.runAsync(() -> {
            for (AssetPack pack : AssetModule.get().getAssetPacks()) {
                ValidateCPBCommand.convertPrefabs(context, pack.getRoot());
            }
        });
    }

    private static void convertPrefabs(@Nonnull CommandContext context, @Nonnull Path assetPath) {
        ObjectArrayList failed = new ObjectArrayList();
        try (Stream<Path> stream = Files.walk(assetPath, FileUtil.DEFAULT_WALK_TREE_OPTIONS_ARRAY);){
            CompletableFuture[] futures = (CompletableFuture[])stream.filter(path -> Files.isRegularFile(path, new LinkOption[0]) && path.toString().endsWith(".prefab.json")).map(path -> ((CompletableFuture)BsonUtil.readDocument(path, false).thenAccept(document -> {
                BsonPrefabBufferDeserializer.INSTANCE.deserialize((Path)path, (BsonDocument)document);
                context.sendMessage(Message.translation("server.general.loadedPrefab").param("name", path.toString()));
            })).exceptionally(throwable -> {
                String message = throwable.getCause().getMessage();
                if (message != null) {
                    if (message.contains(FAILED_TO_FIND_BLOCK)) {
                        failed.add("Failed to load " + String.valueOf(path) + " because " + message);
                        return null;
                    }
                    if (message.contains(UNABLE_TO_LOAD_MODEL)) {
                        failed.add("Failed to load " + String.valueOf(path) + " because " + message);
                        return null;
                    }
                }
                failed.add("Failed to load " + String.valueOf(path) + " because " + String.valueOf(message != null ? message : throwable.getCause().getClass()));
                new Exception("Failed to load " + String.valueOf(path), throwable.getCause()).printStackTrace();
                return null;
            })).toArray(CompletableFuture[]::new);
            CompletableFuture.allOf(futures).join();
        }
        catch (IOException e) {
            throw SneakyThrow.sneakyThrow(e);
        }
        if (!failed.isEmpty()) {
            context.sendMessage(Message.translation("server.commands.validatecpb.failed").param("failed", ((Object)failed).toString()));
        }
        context.sendMessage(Message.translation("server.commands.prefabConvertionDone").param("path", assetPath.toString()));
    }
}

