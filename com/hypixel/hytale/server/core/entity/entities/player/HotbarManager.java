/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.entity.entities.player;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public class HotbarManager {
    public static final int HOTBARS_MAX = 10;
    @Nonnull
    public static final BuilderCodec<HotbarManager> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(HotbarManager.class, HotbarManager::new).append(new KeyedCodec<T[]>("SavedHotbars", new ArrayCodec<ItemContainer>(ItemContainer.CODEC, ItemContainer[]::new)), (player, savedHotbars) -> {
        player.savedHotbars = savedHotbars;
    }, player -> player.savedHotbars).documentation("An array of item containers that represent the saved hotbars.").add()).append(new KeyedCodec<Integer>("CurrentHotbar", Codec.INTEGER), (player, currentHotbar) -> {
        player.currentHotbar = currentHotbar;
    }, player -> player.currentHotbar).documentation("The current hotbar that the player has active.").add()).build();
    private static final Message MESSAGE_GENERAL_HOTBAR_INVALID_SLOT = Message.translation("server.general.hotbar.invalidSlot");
    private static final Message MESSAGE_GENERAL_HOTBAR_INVALID_GAME_MODE = Message.translation("server.general.hotbar.invalidGameMode");
    @Nonnull
    private ItemContainer[] savedHotbars = new ItemContainer[10];
    private int currentHotbar = 0;
    private boolean currentlyLoadingHotbar;

    public void saveHotbar(@Nonnull Ref<EntityStore> playerRef, short hotbarIndex, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        PlayerRef playerRefComponent = componentAccessor.getComponent(playerRef, PlayerRef.getComponentType());
        assert (playerRefComponent != null);
        if (hotbarIndex < 0 || hotbarIndex > 9) {
            playerRefComponent.sendMessage(MESSAGE_GENERAL_HOTBAR_INVALID_SLOT);
            return;
        }
        Player playerComponent = componentAccessor.getComponent(playerRef, Player.getComponentType());
        assert (playerComponent != null);
        if (!playerComponent.getGameMode().equals((Object)GameMode.Creative)) {
            playerRefComponent.sendMessage(MESSAGE_GENERAL_HOTBAR_INVALID_GAME_MODE);
            return;
        }
        this.currentlyLoadingHotbar = true;
        this.savedHotbars[hotbarIndex] = playerComponent.getInventory().getHotbar().clone();
        this.currentHotbar = hotbarIndex;
        this.currentlyLoadingHotbar = false;
    }

    public void loadHotbar(@Nonnull Ref<EntityStore> playerRef, short hotbarIndex, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        PlayerRef playerRefComponent = componentAccessor.getComponent(playerRef, PlayerRef.getComponentType());
        assert (playerRefComponent != null);
        if (hotbarIndex < 0 || hotbarIndex > 9) {
            playerRefComponent.sendMessage(MESSAGE_GENERAL_HOTBAR_INVALID_SLOT);
            return;
        }
        Player playerComponent = componentAccessor.getComponent(playerRef, Player.getComponentType());
        assert (playerComponent != null);
        if (!playerComponent.getGameMode().equals((Object)GameMode.Creative)) {
            playerRefComponent.sendMessage(MESSAGE_GENERAL_HOTBAR_INVALID_GAME_MODE);
            return;
        }
        this.currentlyLoadingHotbar = true;
        ItemContainer hotbar = playerComponent.getInventory().getHotbar();
        hotbar.removeAllItemStacks();
        if (this.savedHotbars[hotbarIndex] != null) {
            ItemContainer savedHotbar = this.savedHotbars[hotbarIndex].clone();
            savedHotbar.forEach(hotbar::setItemStackForSlot);
        }
        this.currentHotbar = hotbarIndex;
        this.currentlyLoadingHotbar = false;
        playerRefComponent.sendMessage(Message.translation("server.general.hotbar.loaded").param("id", hotbarIndex + 1));
    }

    public int getCurrentHotbarIndex() {
        return this.currentHotbar;
    }

    public boolean getIsCurrentlyLoadingHotbar() {
        return this.currentlyLoadingHotbar;
    }
}

