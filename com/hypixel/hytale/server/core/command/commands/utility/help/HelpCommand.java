/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.commands.utility.help;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.command.system.pages.CommandListPage;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HelpCommand
extends AbstractAsyncCommand {
    @Nonnull
    private static final Message MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD = Message.translation("server.commands.errors.playerNotInWorld");

    public HelpCommand() {
        super("help", "server.commands.help.desc");
        this.addAliases("?");
        this.setPermissionGroup(GameMode.Adventure);
        this.addUsageVariant(new HelpCommandVariant());
    }

    @Override
    @Nonnull
    protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
        return HelpCommand.openHelpUI(context, null);
    }

    @Nonnull
    static CompletableFuture<Void> openHelpUI(@Nonnull CommandContext context, @Nullable String initialCommand) {
        if (context.isPlayer()) {
            Ref<EntityStore> playerRef = context.senderAsPlayerRef();
            if (playerRef == null || !playerRef.isValid()) {
                context.sendMessage(MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD);
                return CompletableFuture.completedFuture(null);
            }
            Store<EntityStore> store = playerRef.getStore();
            World world = store.getExternalData().getWorld();
            String resolvedCommand = HelpCommand.resolveCommandName(initialCommand);
            return CompletableFuture.runAsync(() -> {
                Player playerComponent = store.getComponent(playerRef, Player.getComponentType());
                PlayerRef playerRefComponent = store.getComponent(playerRef, PlayerRef.getComponentType());
                if (playerComponent != null && playerRefComponent != null) {
                    playerComponent.getPageManager().openCustomPage(playerRef, store, new CommandListPage(playerRefComponent, resolvedCommand));
                }
            }, world);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Nullable
    private static String resolveCommandName(@Nullable String commandNameOrAlias) {
        if (commandNameOrAlias == null) {
            return null;
        }
        String lowerName = commandNameOrAlias.toLowerCase();
        Map<String, AbstractCommand> commands = CommandManager.get().getCommandRegistration();
        if (commands.containsKey(lowerName)) {
            return lowerName;
        }
        for (Map.Entry<String, AbstractCommand> entry : commands.entrySet()) {
            Set<String> aliases = entry.getValue().getAliases();
            if (aliases == null || !aliases.contains(lowerName)) continue;
            return entry.getKey();
        }
        return lowerName;
    }

    private static class HelpCommandVariant
    extends AbstractAsyncCommand {
        @Nonnull
        private final RequiredArg<String> commandArg = this.withRequiredArg("command", "server.commands.help.command.name.desc", ArgTypes.STRING);

        HelpCommandVariant() {
            super("server.commands.help.command.desc");
        }

        @Override
        @Nonnull
        protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
            String commandName = (String)this.commandArg.get(context);
            return HelpCommand.openHelpUI(context, commandName);
        }
    }
}

