/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.commands.server;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.DisplayNameComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.message.MessageFormat;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public class WhoCommand
extends AbstractAsyncCommand {
    public WhoCommand() {
        super("who", "server.commands.who.desc");
        this.setPermissionGroup(GameMode.Adventure);
    }

    @Override
    @Nonnull
    protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
        Map<String, World> worlds = Universe.get().getWorlds();
        ObjectArrayList<CompletableFuture<Void>> futures = new ObjectArrayList<CompletableFuture<Void>>();
        for (Map.Entry<String, World> entry : worlds.entrySet()) {
            World world = entry.getValue();
            CompletableFuture<Void> future = this.runAsync(context, () -> {
                Store<EntityStore> store = world.getEntityStore().getStore();
                Collection<PlayerRef> playerRefs = world.getPlayerRefs();
                ObjectArrayList<Message> playerMessages = new ObjectArrayList<Message>();
                for (PlayerRef playerRef : playerRefs) {
                    Player playerComponent;
                    Ref<EntityStore> ref = playerRef.getReference();
                    if (ref == null || !ref.isValid() || (playerComponent = store.getComponent(ref, Player.getComponentType())) == null) continue;
                    DisplayNameComponent displayNameComponent = store.getComponent(ref, DisplayNameComponent.getComponentType());
                    assert (displayNameComponent != null);
                    Message displayName = displayNameComponent.getDisplayName();
                    if (displayName != null) {
                        playerMessages.add(Message.translation("server.commands.who.playerWithDisplayName").param("displayName", displayName).param("username", playerRef.getUsername()));
                        continue;
                    }
                    playerMessages.add(Message.raw(playerRef.getUsername()));
                }
                context.sendMessage(MessageFormat.list(Message.raw(world.getName()), playerMessages));
            }, world);
            futures.add(future);
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
}

