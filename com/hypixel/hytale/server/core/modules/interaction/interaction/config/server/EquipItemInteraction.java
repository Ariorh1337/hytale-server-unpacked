/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.server;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemArmor;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.MoveTransaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public class EquipItemInteraction
extends SimpleInstantInteraction {
    public static final BuilderCodec<EquipItemInteraction> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(EquipItemInteraction.class, EquipItemInteraction::new, SimpleInstantInteraction.CODEC).documentation("Equips the item being held.")).build();

    @Override
    @Nonnull
    public WaitForDataFrom getWaitForDataFrom() {
        return WaitForDataFrom.Server;
    }

    @Override
    protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        ItemContainer armorContainer;
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        assert (commandBuffer != null);
        Ref<EntityStore> ref = context.getEntity();
        Entity entity = EntityUtils.getEntity(ref, commandBuffer);
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        LivingEntity livingEntity = (LivingEntity)entity;
        Inventory inventory = livingEntity.getInventory();
        byte activeSlot = context.getHeldItemSlot();
        ItemStack itemInHand = context.getHeldItem();
        if (itemInHand == null) {
            return;
        }
        Item item = itemInHand.getItem();
        if (item == null) {
            return;
        }
        ItemArmor armor = item.getArmor();
        if (armor == null) {
            return;
        }
        short slotId = (short)armor.getArmorSlot().ordinal();
        if (slotId > (armorContainer = inventory.getArmor()).getCapacity()) {
            return;
        }
        MoveTransaction<ItemStackTransaction> stackTransaction = context.getHeldItemContainer().moveItemStackFromSlot((short)activeSlot, itemInHand.getQuantity(), armorContainer);
        if (!stackTransaction.succeeded()) {
            context.getState().state = InteractionState.Failed;
        }
    }

    @Override
    @Nonnull
    public String toString() {
        return "EquipItemInteraction{} " + super.toString();
    }
}

