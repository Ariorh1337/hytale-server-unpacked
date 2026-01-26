/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.commands.debug;

import com.hypixel.hytale.assetstore.AssetMap;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.commands.debug.AssetTagsCommand;
import com.hypixel.hytale.server.core.command.commands.debug.AssetsDuplicatesCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public class AssetsCommand
extends AbstractCommandCollection {
    public AssetsCommand() {
        super("assets", "server.commands.assets.desc");
        this.addSubCommand(new AssetTagsCommand());
        this.addSubCommand(new AssetsDuplicatesCommand());
        this.addSubCommand(new AssetLongestAssetNameCommand());
    }

    public static class AssetLongestAssetNameCommand
    extends AbstractAsyncCommand {
        public AssetLongestAssetNameCommand() {
            super("longest", "");
        }

        @Override
        @Nonnull
        protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
            return CompletableFuture.runAsync(() -> {
                for (Map.Entry<Class<JsonAssetWithMap>, AssetStore<?, ?, ?>> e : AssetRegistry.getStoreMap().entrySet()) {
                    String longestName = "";
                    for (Object asset : ((AssetMap)e.getValue().getAssetMap()).getAssetMap().keySet()) {
                        String name = e.getValue().transformKey(asset).toString();
                        if (name.length() <= longestName.length()) continue;
                        longestName = name;
                    }
                    context.sendMessage(Message.raw("Longest asset name for " + e.getKey().getSimpleName() + ": " + longestName + " (" + longestName.length() + " characters)"));
                }
            });
        }
    }
}

