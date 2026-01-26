/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.accesscontrol.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.auth.ProfileServiceClient;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandUtil;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.modules.accesscontrol.ban.InfiniteBan;
import com.hypixel.hytale.server.core.modules.accesscontrol.provider.HytaleBanProvider;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
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
    private final RequiredArg<ProfileServiceClient.PublicGameProfile> playerArg = this.withRequiredArg("player", "server.commands.ban.player.desc", ArgTypes.GAME_PROFILE_LOOKUP);

    public BanCommand(@Nonnull HytaleBanProvider banProvider) {
        super("ban", "server.commands.ban.desc");
        this.setUnavailableInSingleplayer(true);
        this.banProvider = banProvider;
    }

    @Override
    @Nonnull
    protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
        String afterPlayer;
        ProfileServiceClient.PublicGameProfile profile = (ProfileServiceClient.PublicGameProfile)this.playerArg.get(context);
        if (profile == null) {
            return CompletableFuture.completedFuture(null);
        }
        UUID uuid = profile.getUuid();
        String rawArgs = CommandUtil.stripCommandName(context.getInputString());
        int firstSpaceIndex = rawArgs.indexOf(32);
        String reason = firstSpaceIndex != -1 ? ((afterPlayer = rawArgs.substring(firstSpaceIndex + 1).trim()).isEmpty() ? "No reason." : afterPlayer) : "No reason.";
        Message displayMessage = Message.raw(profile.getUsername()).bold(true);
        PlayerRef playerRef = Universe.get().getPlayer(uuid);
        if (this.banProvider.hasBan(uuid)) {
            context.sendMessage(Message.translation("server.modules.ban.alreadyBanned").param("name", displayMessage));
            return CompletableFuture.completedFuture(null);
        }
        InfiniteBan ban = new InfiniteBan(uuid, context.sender().getUuid(), Instant.now(), reason);
        this.banProvider.modify(banMap -> {
            banMap.put(uuid, ban);
            return Boolean.TRUE;
        });
        if (playerRef != null) {
            CompletableFuture<Optional<String>> disconnectReason = ban.getDisconnectReason(uuid);
            return ((CompletableFuture)disconnectReason.whenComplete((string, disconnectEx) -> {
                Optional<String> optional = string;
                if (disconnectEx != null) {
                    context.sendMessage(Message.translation("server.modules.ban.failedDisconnectReason").param("name", displayMessage));
                    disconnectEx.printStackTrace();
                }
                if (optional == null || !optional.isPresent()) {
                    optional = Optional.of("Failed to get disconnect reason.");
                }
                playerRef.getPacketHandler().disconnect(optional.get());
                context.sendMessage(Message.translation("server.modules.ban.bannedWithReason").param("name", displayMessage).param("reason", reason));
            })).thenApply(v -> null);
        }
        context.sendMessage(Message.translation("server.modules.ban.bannedWithReason").param("name", displayMessage).param("reason", reason));
        return CompletableFuture.completedFuture(null);
    }
}

