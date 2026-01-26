/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.commands.player.inventory;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class GiveArmorCommand
extends AbstractAsyncCommand {
    private static final String PREFIX = "Armor_";
    @Nonnull
    private static final Message MESSAGE_COMMANDS_GIVEARMOR_SUCCESS = Message.translation("server.commands.givearmor.success");
    @Nonnull
    private final OptionalArg<String> playerArg = this.withOptionalArg("player", "server.commands.givearmor.player.desc", ArgTypes.STRING);
    @Nonnull
    private final RequiredArg<String> searchStringArg = this.withRequiredArg("search", "server.commands.givearmor.search.desc", ArgTypes.STRING);
    @Nonnull
    private final FlagArg setFlag = this.withFlagArg("set", "server.commands.givearmor.set.desc");

    public GiveArmorCommand() {
        super("armor", "server.commands.givearmor.desc");
    }

    @Override
    @Nonnull
    protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
        List<Ref<EntityStore>> targets;
        if (this.playerArg.provided(context)) {
            String playerInput = (String)this.playerArg.get(context);
            if ("*".equals(playerInput)) {
                targets = new ObjectArrayList<Ref<EntityStore>>();
                for (PlayerRef player : Universe.get().getPlayers()) {
                    targets.add(player.getReference());
                }
            } else {
                PlayerRef player = Universe.get().getPlayer(playerInput, NameMatching.DEFAULT);
                if (player == null) {
                    context.sendMessage(Message.translation("server.commands.errors.noSuchPlayer").param("username", playerInput));
                    return CompletableFuture.completedFuture(null);
                }
                targets = Collections.singletonList(player.getReference());
            }
        } else {
            if (!context.isPlayer()) {
                context.sendMessage(Message.translation("server.commands.errors.playerOrArg").param("option", "player"));
                return CompletableFuture.completedFuture(null);
            }
            targets = Collections.singletonList(context.senderAsPlayerRef());
        }
        if (targets.isEmpty()) {
            context.sendMessage(Message.translation("server.commands.errors.noSuchPlayer").param("username", "*"));
            return CompletableFuture.completedFuture(null);
        }
        String searchString = (String)this.searchStringArg.get(context);
        List armor = Item.getAssetMap().getAssetMap().keySet().stream().filter(blockTypeKey -> blockTypeKey.startsWith(PREFIX) && blockTypeKey.indexOf(searchString, PREFIX.length()) == PREFIX.length()).map(ItemStack::new).collect(Collectors.toList());
        if (armor.isEmpty()) {
            context.sendMessage(Message.translation("server.commands.givearmor.typeNotFound").param("type", searchString).color(Color.RED));
            return CompletableFuture.completedFuture(null);
        }
        Object2ObjectOpenHashMap<World, List> playersByWorld = new Object2ObjectOpenHashMap<World, List>();
        for (Ref ref : targets) {
            if (ref == null || !ref.isValid()) continue;
            Store store = ref.getStore();
            World world = ((EntityStore)store.getExternalData()).getWorld();
            playersByWorld.computeIfAbsent(world, k -> new ObjectArrayList()).add(ref);
        }
        ObjectArrayList<CompletableFuture<Void>> futures = new ObjectArrayList<CompletableFuture<Void>>();
        boolean bl = this.setFlag.provided(context);
        for (Map.Entry entry : playersByWorld.entrySet()) {
            World world = (World)entry.getKey();
            List worldPlayers = (List)entry.getValue();
            CompletableFuture<Void> future = this.runAsync(context, () -> {
                for (Ref playerRef : worldPlayers) {
                    Store<EntityStore> store;
                    Player targetPlayerComponent;
                    if (playerRef == null || !playerRef.isValid() || (targetPlayerComponent = (store = playerRef.getStore()).getComponent(playerRef, Player.getComponentType())) == null) continue;
                    ItemContainer armorInventory = targetPlayerComponent.getInventory().getArmor();
                    if (shouldClear) {
                        armorInventory.clear();
                    }
                    armorInventory.addItemStacks(armor);
                }
            }, world);
            futures.add(future);
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> context.sendMessage(MESSAGE_COMMANDS_GIVEARMOR_SUCCESS));
    }
}

