/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.accesscontrol.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.modules.accesscontrol.ban.InfiniteBan;
import com.hypixel.hytale.server.core.modules.accesscontrol.provider.HytaleBanProvider;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.util.AuthUtil;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public class BanCommand
extends AbstractAsyncCommand {
    @Nonnull
    private final HytaleBanProvider banProvider;
    @Nonnull
    private final RequiredArg<String> usernameArg = this.withRequiredArg("username", "server.commands.ban.username.desc", ArgTypes.STRING);
    @Nonnull
    private final OptionalArg<String> reasonArg = this.withOptionalArg("reason", "server.commands.ban.reason.desc", ArgTypes.STRING);

    public BanCommand(@Nonnull HytaleBanProvider banProvider) {
        super("ban", "server.commands.ban.desc");
        this.setUnavailableInSingleplayer(true);
        this.banProvider = banProvider;
    }

    @Override
    @Nonnull
    protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
        String afterUsername;
        String username = (String)this.usernameArg.get(context);
        String rawInput = context.getInputString();
        int usernameIndex = rawInput.indexOf(username);
        String reason = usernameIndex != -1 && usernameIndex + username.length() < rawInput.length() ? ((afterUsername = rawInput.substring(usernameIndex + username.length()).trim()).isEmpty() ? "No reason." : afterUsername) : "No reason.";
        return AuthUtil.lookupUuid(username).thenCompose(uuid -> {
            if (this.banProvider.hasBan((UUID)uuid)) {
                context.sendMessage(Message.translation("server.modules.ban.alreadyBanned").param("name", username));
                return CompletableFuture.completedFuture(null);
            }
            InfiniteBan ban = new InfiniteBan((UUID)uuid, context.sender().getUuid(), Instant.now(), reason);
            this.banProvider.modify(banMap -> {
                banMap.put(uuid, ban);
                return true;
            });
            PlayerRef player = Universe.get().getPlayer((UUID)uuid);
            if (player != null) {
                CompletableFuture<Optional<String>> disconnectReason = ban.getDisconnectReason((UUID)uuid);
                return ((CompletableFuture)disconnectReason.whenComplete((string, disconnectEx) -> {
                    Optional<String> optional = string;
                    if (disconnectEx != null) {
                        context.sendMessage(Message.translation("server.modules.ban.failedDisconnectReason").param("name", username));
                        disconnectEx.printStackTrace();
                    }
                    if (optional == null || !optional.isPresent()) {
                        optional = Optional.of("Failed to get disconnect reason.");
                    }
                    player.getPacketHandler().disconnect(optional.get());
                    context.sendMessage(Message.translation("server.modules.ban.bannedWithReason").param("name", username).param("reason", reason));
                })).thenApply(v -> null);
            }
            context.sendMessage(Message.translation("server.modules.ban.bannedWithReason").param("name", username).param("reason", reason));
            return CompletableFuture.completedFuture(null);
        });
    }
}

