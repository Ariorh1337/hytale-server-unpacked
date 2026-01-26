/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.server;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.Interaction;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TempAssetIdUtil;
import javax.annotation.Nonnull;

public class ModifyInventoryInteraction
extends SimpleInstantInteraction {
    public static final BuilderCodec<ModifyInventoryInteraction> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ModifyInventoryInteraction.class, ModifyInventoryInteraction::new, SimpleInstantInteraction.CODEC).documentation("Modifies an item in the inventory.")).appendInherited(new KeyedCodec<GameMode>("RequiredGameMode", ProtocolCodecs.GAMEMODE), (interaction, s) -> {
        interaction.requiredGameMode = s;
    }, interaction -> interaction.requiredGameMode, (interaction, parent) -> {
        interaction.requiredGameMode = parent.requiredGameMode;
    }).add()).appendInherited(new KeyedCodec<ItemStack>("ItemToRemove", ItemStack.CODEC), (interaction, s) -> {
        interaction.itemToRemove = s;
    }, interaction -> interaction.itemToRemove, (interaction, parent) -> {
        interaction.itemToRemove = parent.itemToRemove;
    }).add()).appendInherited(new KeyedCodec<Integer>("AdjustHeldItemQuantity", Codec.INTEGER), (interaction, s) -> {
        interaction.adjustHeldItemQuantity = s;
    }, interaction -> interaction.adjustHeldItemQuantity, (interaction, parent) -> {
        interaction.adjustHeldItemQuantity = parent.adjustHeldItemQuantity;
    }).add()).appendInherited(new KeyedCodec<ItemStack>("ItemToAdd", ItemStack.CODEC), (interaction, s) -> {
        interaction.itemToAdd = s;
    }, interaction -> interaction.itemToAdd, (interaction, parent) -> {
        interaction.itemToAdd = parent.itemToAdd;
    }).add()).appendInherited(new KeyedCodec<Double>("AdjustHeldItemDurability", Codec.DOUBLE), (interaction, s) -> {
        interaction.adjustHeldItemDurability = s;
    }, interaction -> interaction.adjustHeldItemDurability, (interaction, parent) -> {
        interaction.adjustHeldItemDurability = parent.adjustHeldItemDurability;
    }).add()).appendInherited(new KeyedCodec<String>("BrokenItem", Codec.STRING), (interaction, s) -> {
        interaction.brokenItem = s;
    }, interaction -> interaction.brokenItem, (interaction, parent) -> {
        interaction.brokenItem = parent.brokenItem;
    }).add()).appendInherited(new KeyedCodec<Boolean>("NotifyOnBreak", Codec.BOOLEAN), (interaction, s) -> {
        interaction.notifyOnBreak = s;
    }, interaction -> interaction.notifyOnBreak, (interaction, parent) -> {
        interaction.notifyOnBreak = parent.notifyOnBreak;
    }).documentation("If true, shows the 'item broken' message and plays the break sound when durability reaches 0. Defaults to true for tools (no BrokenItem or same item), false for transformations (different BrokenItem).").add()).appendInherited(new KeyedCodec<String>("NotifyOnBreakMessage", Codec.STRING), (interaction, s) -> {
        interaction.notifyOnBreakMessage = s;
    }, interaction -> interaction.notifyOnBreakMessage, (interaction, parent) -> {
        interaction.notifyOnBreakMessage = parent.notifyOnBreakMessage;
    }).documentation("Custom translation key for the break notification message. Supports {itemName} parameter. Defaults to 'server.general.repair.itemBroken' if not specified.").add()).build();
    private GameMode requiredGameMode;
    private ItemStack itemToRemove;
    private int adjustHeldItemQuantity;
    private ItemStack itemToAdd;
    private double adjustHeldItemDurability;
    private String brokenItem;
    private Boolean notifyOnBreak;
    private String notifyOnBreakMessage;

    @Override
    @Nonnull
    public WaitForDataFrom getWaitForDataFrom() {
        return WaitForDataFrom.Server;
    }

    @Override
    protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        ItemStackSlotTransaction slotTransaction;
        boolean shouldNotify;
        boolean isTransformation;
        boolean justBroke;
        ItemStackTransaction removeItemStack;
        boolean hasRequiredGameMode;
        Ref<EntityStore> ref = context.getEntity();
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        Player playerComponent = commandBuffer.getComponent(ref, Player.getComponentType());
        if (playerComponent == null) {
            context.getState().state = InteractionState.Failed;
            return;
        }
        boolean bl = hasRequiredGameMode = this.requiredGameMode == null || playerComponent.getGameMode() == this.requiredGameMode;
        if (!hasRequiredGameMode) {
            return;
        }
        CombinedItemContainer combinedHotbarFirst = playerComponent.getInventory().getCombinedHotbarFirst();
        if (this.itemToRemove != null && !(removeItemStack = combinedHotbarFirst.removeItemStack(this.itemToRemove, true, true)).succeeded()) {
            context.getState().state = InteractionState.Failed;
            return;
        }
        ItemStack heldItem = context.getHeldItem();
        if (heldItem != null && this.adjustHeldItemQuantity != 0) {
            if (this.adjustHeldItemQuantity < 0) {
                ItemStackSlotTransaction slotTransaction2 = context.getHeldItemContainer().removeItemStackFromSlot(context.getHeldItemSlot(), heldItem, -this.adjustHeldItemQuantity);
                if (!slotTransaction2.succeeded()) {
                    context.getState().state = InteractionState.Failed;
                    return;
                }
                context.setHeldItem(slotTransaction2.getSlotAfter());
            } else {
                SimpleItemContainer.addOrDropItemStack(commandBuffer, ref, combinedHotbarFirst, heldItem.withQuantity(this.adjustHeldItemQuantity));
            }
        }
        if (this.itemToAdd != null) {
            SimpleItemContainer.addOrDropItemStack(commandBuffer, ref, combinedHotbarFirst, this.itemToAdd);
        }
        if (this.adjustHeldItemDurability == 0.0) {
            return;
        }
        ItemStack item = context.getHeldItem();
        if (item == null) {
            return;
        }
        ItemStack newItem = item.withIncreasedDurability(this.adjustHeldItemDurability);
        boolean bl2 = justBroke = newItem.isBroken() && !item.isBroken();
        if (newItem.isBroken() && this.brokenItem != null) {
            if (this.brokenItem.equals("Empty")) {
                newItem = null;
            } else if (!this.brokenItem.equals(item.getItemId())) {
                newItem = new ItemStack(this.brokenItem, 1);
            }
        }
        boolean bl3 = isTransformation = this.brokenItem != null && !this.brokenItem.equals(item.getItemId());
        boolean bl4 = this.notifyOnBreak != null ? this.notifyOnBreak : (shouldNotify = !isTransformation);
        if (justBroke && shouldNotify) {
            Message itemNameMessage = Message.translation(item.getItem().getTranslationKey());
            String messageKey = this.notifyOnBreakMessage != null ? this.notifyOnBreakMessage : "server.general.repair.itemBroken";
            playerComponent.sendMessage(Message.translation(messageKey).param("itemName", itemNameMessage).color("#ff5555"));
            PlayerRef playerRefComponent = commandBuffer.getComponent(ref, PlayerRef.getComponentType());
            if (playerRefComponent != null) {
                int soundEventIndex = TempAssetIdUtil.getSoundEventIndex("SFX_Item_Break");
                SoundUtil.playSoundEvent2dToPlayer(playerRefComponent, soundEventIndex, SoundCategory.SFX);
            }
        }
        if (!(slotTransaction = context.getHeldItemContainer().setItemStackForSlot(context.getHeldItemSlot(), newItem)).succeeded()) {
            context.getState().state = InteractionState.Failed;
            return;
        }
        context.setHeldItem(newItem);
    }

    @Override
    @Nonnull
    protected Interaction generatePacket() {
        return new com.hypixel.hytale.protocol.ModifyInventoryInteraction();
    }

    @Override
    protected void configurePacket(Interaction packet) {
        super.configurePacket(packet);
        com.hypixel.hytale.protocol.ModifyInventoryInteraction p = (com.hypixel.hytale.protocol.ModifyInventoryInteraction)packet;
        if (this.itemToRemove != null) {
            p.itemToRemove = this.itemToRemove.toPacket();
        }
        p.adjustHeldItemQuantity = this.adjustHeldItemQuantity;
        if (this.itemToAdd != null) {
            p.itemToAdd = this.itemToAdd.toPacket();
        }
        if (this.brokenItem != null) {
            p.brokenItem = this.brokenItem.toString();
        }
        p.adjustHeldItemDurability = this.adjustHeldItemDurability;
    }

    @Override
    @Nonnull
    public String toString() {
        return "ModifyInventoryInteraction{requiredGameMode=" + String.valueOf((Object)this.requiredGameMode) + ", itemToRemove=" + String.valueOf(this.itemToRemove) + ", adjustHeldItemQuantity=" + this.adjustHeldItemQuantity + ", itemToAdd=" + String.valueOf(this.itemToAdd) + ", adjustHeldItemDurability=" + this.adjustHeldItemDurability + ", brokenItem=" + this.brokenItem + ", notifyOnBreak=" + this.notifyOnBreak + ", notifyOnBreakMessage='" + this.notifyOnBreakMessage + "'} " + super.toString();
    }
}

