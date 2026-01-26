/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.instances.command;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.AssetModule;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import com.hypixel.hytale.server.core.util.io.FileUtil;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class InstanceEditCopyCommand
extends AbstractAsyncCommand {
    private final RequiredArg<String> originNameArg = this.withRequiredArg("instanceToCopy", "server.commands.instances.editcopy.origin.name", ArgTypes.STRING);
    private final RequiredArg<String> destinationNameArg = this.withRequiredArg("newInstanceName", "server.commands.instances.editcopy.destination.name", ArgTypes.STRING);

    public InstanceEditCopyCommand() {
        super("copy", "server.commands.instances.edit.copy.desc");
    }

    @Override
    @Nonnull
    protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
        WorldConfig worldConfig;
        if (AssetModule.get().getBaseAssetPack().isImmutable()) {
            context.sendMessage(Message.translation("server.commands.instances.edit.assetsImmutable"));
            return CompletableFuture.completedFuture(null);
        }
        String instanceToCopy = (String)this.originNameArg.get(context);
        Path originPath = InstancesPlugin.getInstanceAssetPath(instanceToCopy);
        if (!Files.exists(originPath, new LinkOption[0])) {
            context.sendMessage(Message.translation("server.commands.instances.edit.copy.originNotFound").param("path", originPath.toAbsolutePath().toString()));
            return CompletableFuture.completedFuture(null);
        }
        String destinationName = (String)this.destinationNameArg.get(context);
        Path destinationPath = originPath.getParent().resolve(destinationName);
        if (Files.exists(destinationPath, new LinkOption[0])) {
            context.sendMessage(Message.translation("server.commands.instances.edit.copy.destinationExists").param("path", destinationPath.toAbsolutePath().toString()));
            return CompletableFuture.completedFuture(null);
        }
        try {
            worldConfig = WorldConfig.load(originPath.resolve("instance.bson")).join();
        }
        catch (Throwable t) {
            context.sendMessage(Message.translation("server.commands.instances.edit.copy.errorLoading"));
            InstancesPlugin.get().getLogger().at(Level.SEVERE).log("Error loading origin instance config for copy", t);
            return CompletableFuture.completedFuture(null);
        }
        worldConfig.setUuid(UUID.randomUUID());
        Path destinationConfigFile = destinationPath.resolve("instance.bson");
        try {
            FileUtil.copyDirectory(originPath, destinationPath);
            Files.deleteIfExists(destinationConfigFile);
        }
        catch (Throwable t) {
            context.sendMessage(Message.translation("server.commands.instances.edit.copy.errorCopying"));
            InstancesPlugin.get().getLogger().at(Level.SEVERE).log("Error copying instance folder for copy", t);
            return CompletableFuture.completedFuture(null);
        }
        return WorldConfig.save(destinationConfigFile, worldConfig).thenRun(() -> context.sendMessage(Message.translation("server.commands.instances.copiedInstanceAssetConfig").param("origin", instanceToCopy).param("destination", destinationName)));
    }
}

