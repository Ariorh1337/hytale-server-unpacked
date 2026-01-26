/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.commands.player;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public class HideCommand
extends AbstractCommandCollection {
    public HideCommand() {
        super("hide", "server.commands.hide.desc");
        this.addUsageVariant(new HidePlayerCommand());
        this.addSubCommand(new ShowPlayerCommand());
        this.addSubCommand(new HideAllCommand());
        this.addSubCommand(new ShowAllCommand());
    }

    static class HidePlayerCommand
    extends AbstractAsyncCommand {
        @Nonnull
        private final RequiredArg<PlayerRef> playerArg = this.withRequiredArg("player", "server.commands.argtype.player.desc", ArgTypes.PLAYER_REF);
        @Nonnull
        private final OptionalArg<PlayerRef> targetArg = this.withOptionalArg("target", "server.commands.hide.target.desc", ArgTypes.PLAYER_REF);

        HidePlayerCommand() {
            super("server.commands.hide.player.desc");
        }

        @Override
        @Nonnull
        protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
            PlayerRef playerRef = (PlayerRef)this.playerArg.get(context);
            Ref<EntityStore> ref = playerRef.getReference();
            if (ref == null || !ref.isValid()) {
                context.sendMessage(Message.translation("server.commands.errors.playerNotInWorld"));
                return CompletableFuture.completedFuture(null);
            }
            Store<EntityStore> store = ref.getStore();
            World world = store.getExternalData().getWorld();
            return this.runAsync(context, () -> {
                Player playerComponent = store.getComponent(ref, Player.getComponentType());
                if (playerComponent == null) {
                    context.sendMessage(Message.translation("server.commands.errors.playerNotInWorld"));
                    return;
                }
                UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
                if (uuidComponent == null) {
                    context.sendMessage(Message.translation("server.commands.errors.playerNotInWorld"));
                    return;
                }
                UUID playerUuid = uuidComponent.getUuid();
                if (this.targetArg.provided(context)) {
                    PlayerRef targetPlayerRef = (PlayerRef)this.targetArg.get(context);
                    Ref<EntityStore> targetRef = targetPlayerRef.getReference();
                    if (targetRef == null || !targetRef.isValid()) {
                        context.sendMessage(Message.translation("server.commands.errors.playerNotInWorld"));
                        return;
                    }
                    if (targetRef.equals(ref)) {
                        context.sendMessage(Message.translation("server.commands.hide.cantHideFromSelf"));
                        return;
                    }
                    Store<EntityStore> targetStore = targetRef.getStore();
                    Player targetPlayerComponent = targetStore.getComponent(targetRef, Player.getComponentType());
                    if (targetPlayerComponent == null) {
                        context.sendMessage(Message.translation("server.commands.errors.playerNotInWorld"));
                        return;
                    }
                    targetPlayerRef.getHiddenPlayersManager().hidePlayer(playerUuid);
                    context.sendMessage(Message.translation("server.commands.hide.hiddenFrom").param("username", playerRef.getUsername()).param("targetUsername", targetPlayerRef.getUsername()));
                } else {
                    Universe.get().getWorlds().forEach((name, w) -> w.execute(() -> {
                        Collection<PlayerRef> playerRefs = w.getPlayerRefs();
                        for (PlayerRef targetPlayerRef : playerRefs) {
                            Store<EntityStore> targetStore;
                            Player targetPlayerComponent;
                            Ref<EntityStore> targetRef = targetPlayerRef.getReference();
                            if (targetRef == null || !targetRef.isValid() || targetRef.equals(ref) || (targetPlayerComponent = (targetStore = targetRef.getStore()).getComponent(targetRef, Player.getComponentType())) == null) continue;
                            targetPlayerRef.getHiddenPlayersManager().hidePlayer(playerUuid);
                        }
                    }));
                    context.sendMessage(Message.translation("server.commands.hide.hiddenFromAll").param("username", playerRef.getUsername()));
                }
            }, world);
        }
    }

    static class ShowPlayerCommand
    extends AbstractAsyncCommand {
        @Nonnull
        private final RequiredArg<PlayerRef> playerArg = this.withRequiredArg("player", "server.commands.argtype.player.desc", ArgTypes.PLAYER_REF);
        @Nonnull
        private final OptionalArg<PlayerRef> targetArg = this.withOptionalArg("target", "server.commands.hide.target.desc", ArgTypes.PLAYER_REF);

        ShowPlayerCommand() {
            super("show", "server.commands.hide.showPlayer.desc");
        }

        @Override
        @Nonnull
        protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
            PlayerRef playerRef = (PlayerRef)this.playerArg.get(context);
            Ref<EntityStore> ref = playerRef.getReference();
            if (ref == null || !ref.isValid()) {
                context.sendMessage(Message.translation("server.commands.errors.playerNotInWorld"));
                return CompletableFuture.completedFuture(null);
            }
            Store<EntityStore> store = ref.getStore();
            World world = store.getExternalData().getWorld();
            return this.runAsync(context, () -> {
                Player playerComponent = store.getComponent(ref, Player.getComponentType());
                if (playerComponent == null) {
                    context.sendMessage(Message.translation("server.commands.errors.playerNotInWorld"));
                    return;
                }
                UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
                if (uuidComponent == null) {
                    context.sendMessage(Message.translation("server.commands.errors.playerNotInWorld"));
                    return;
                }
                UUID playerUuid = uuidComponent.getUuid();
                if (this.targetArg.provided(context)) {
                    PlayerRef targetPlayerRef = (PlayerRef)this.targetArg.get(context);
                    Ref<EntityStore> targetRef = targetPlayerRef.getReference();
                    if (targetRef == null || !targetRef.isValid()) {
                        context.sendMessage(Message.translation("server.commands.errors.playerNotInWorld"));
                        return;
                    }
                    if (targetRef.equals(ref)) {
                        context.sendMessage(Message.translation("server.commands.hide.cantHideFromSelf"));
                        return;
                    }
                    Store<EntityStore> targetStore = targetRef.getStore();
                    Player targetPlayerComponent = targetStore.getComponent(targetRef, Player.getComponentType());
                    if (targetPlayerComponent == null) {
                        context.sendMessage(Message.translation("server.commands.errors.playerNotInWorld"));
                        return;
                    }
                    targetPlayerRef.getHiddenPlayersManager().showPlayer(playerUuid);
                    context.sendMessage(Message.translation("server.commands.hide.shownTo").param("username", playerRef.getUsername()).param("targetUsername", targetPlayerRef.getUsername()));
                } else {
                    Universe.get().getWorlds().forEach((name, w) -> w.execute(() -> {
                        Collection<PlayerRef> playerRefs = w.getPlayerRefs();
                        for (PlayerRef targetPlayerRef : playerRefs) {
                            Store<EntityStore> targetStore;
                            Player targetPlayerComponent;
                            Ref<EntityStore> targetRef = targetPlayerRef.getReference();
                            if (targetRef == null || !targetRef.isValid() || targetRef.equals(ref) || (targetPlayerComponent = (targetStore = targetRef.getStore()).getComponent(targetRef, Player.getComponentType())) == null) continue;
                            targetPlayerRef.getHiddenPlayersManager().showPlayer(playerUuid);
                        }
                    }));
                    context.sendMessage(Message.translation("server.commands.hide.shownToAll").param("username", playerRef.getUsername()));
                }
            }, world);
        }
    }

    static class HideAllCommand
    extends CommandBase {
        HideAllCommand() {
            super("all", "server.commands.hide.all.desc");
        }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            Universe.get().getWorlds().forEach((name, world) -> world.execute(() -> {
                Collection<PlayerRef> playerRefs = world.getPlayerRefs();
                for (PlayerRef playerRef1 : playerRefs) {
                    Store<EntityStore> store1;
                    Player playerComponent1;
                    Ref<EntityStore> ref1 = playerRef1.getReference();
                    if (ref1 == null || !ref1.isValid() || (playerComponent1 = (store1 = ref1.getStore()).getComponent(ref1, Player.getComponentType())) == null) continue;
                    for (PlayerRef playerRef2 : playerRefs) {
                        UUIDComponent uuidComponent;
                        Ref<EntityStore> ref2;
                        if (playerRef1.equals(playerRef2) || (ref2 = playerRef2.getReference()) == null || !ref2.isValid() || (uuidComponent = store1.getComponent(ref2, UUIDComponent.getComponentType())) == null) continue;
                        playerRef1.getHiddenPlayersManager().hidePlayer(uuidComponent.getUuid());
                    }
                }
            }));
            context.sendMessage(Message.translation("server.commands.hide.allHiddenFromAll"));
        }
    }

    static class ShowAllCommand
    extends CommandBase {
        ShowAllCommand() {
            super("showall", "server.commands.hide.showAll.desc");
        }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            Universe.get().getWorlds().forEach((name, world) -> world.execute(() -> {
                Collection<PlayerRef> playerRefs = world.getPlayerRefs();
                for (PlayerRef playerRef1 : playerRefs) {
                    Store<EntityStore> store1;
                    Player playerComponent1;
                    Ref<EntityStore> ref1 = playerRef1.getReference();
                    if (ref1 == null || !ref1.isValid() || (playerComponent1 = (store1 = ref1.getStore()).getComponent(ref1, Player.getComponentType())) == null) continue;
                    for (PlayerRef playerRef2 : playerRefs) {
                        UUIDComponent uuidComponent;
                        Ref<EntityStore> ref2;
                        if (playerRef1.equals(playerRef2) || (ref2 = playerRef2.getReference()) == null || !ref2.isValid() || (uuidComponent = store1.getComponent(ref2, UUIDComponent.getComponentType())) == null) continue;
                        playerRef1.getHiddenPlayersManager().showPlayer(uuidComponent.getUuid());
                    }
                }
            }));
            context.sendMessage(Message.translation("server.commands.hide.allShownToAll"));
        }
    }
}

