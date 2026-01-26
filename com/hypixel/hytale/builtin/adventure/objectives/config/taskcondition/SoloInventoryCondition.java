/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.objectives.config.taskcondition;

import com.hypixel.hytale.builtin.adventure.objectives.config.task.BlockTagOrItemIdField;
import com.hypixel.hytale.builtin.adventure.objectives.config.taskcondition.TaskConditionAsset;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SoloInventoryCondition
extends TaskConditionAsset {
    public static final BuilderCodec<SoloInventoryCondition> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(SoloInventoryCondition.class, SoloInventoryCondition::new).append(new KeyedCodec<BlockTagOrItemIdField>("BlockTagOrItemId", BlockTagOrItemIdField.CODEC), (soloInventoryCondition, blockTagOrItemIdField) -> {
        soloInventoryCondition.blockTypeOrTagTask = blockTagOrItemIdField;
    }, soloInventoryCondition -> soloInventoryCondition.blockTypeOrTagTask).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<Integer>("Quantity", Codec.INTEGER), (soloInventoryCondition, integer) -> {
        soloInventoryCondition.quantity = integer;
    }, soloInventoryCondition -> soloInventoryCondition.quantity).addValidator(Validators.greaterThan(0)).add()).append(new KeyedCodec<Boolean>("ConsumeOnCompletion", Codec.BOOLEAN), (soloInventoryCondition, aBoolean) -> {
        soloInventoryCondition.consumeOnCompletion = aBoolean;
    }, soloInventoryCondition -> soloInventoryCondition.consumeOnCompletion).add()).append(new KeyedCodec<Boolean>("HoldInHand", Codec.BOOLEAN), (soloInventoryCondition, aBoolean) -> {
        soloInventoryCondition.holdInHand = aBoolean;
    }, soloInventoryCondition -> soloInventoryCondition.holdInHand).add()).build();
    protected BlockTagOrItemIdField blockTypeOrTagTask;
    protected int quantity = 1;
    protected boolean consumeOnCompletion;
    protected boolean holdInHand;

    public BlockTagOrItemIdField getBlockTypeOrTagTask() {
        return this.blockTypeOrTagTask;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public boolean isConsumeOnCompletion() {
        return this.consumeOnCompletion;
    }

    public boolean isHoldInHand() {
        return this.holdInHand;
    }

    @Override
    public boolean isConditionFulfilled(@Nonnull ComponentAccessor<EntityStore> componentAccessor, @Nonnull Ref<EntityStore> ref, Set<UUID> objectivePlayers) {
        Player playerComponent = componentAccessor.getComponent(ref, Player.getComponentType());
        if (playerComponent == null) {
            return false;
        }
        Inventory inventory = playerComponent.getInventory();
        if (this.holdInHand) {
            ItemStack itemInHand = inventory.getItemInHand();
            if (!this.blockTypeOrTagTask.isBlockTypeIncluded(itemInHand.getItemId())) {
                return false;
            }
            return inventory.getItemInHand().getQuantity() >= this.quantity;
        }
        return inventory.getCombinedHotbarFirst().countItemStacks(itemStack -> this.blockTypeOrTagTask.isBlockTypeIncluded(itemStack.getItemId())) >= this.quantity;
    }

    @Override
    public void consumeCondition(@Nonnull ComponentAccessor<EntityStore> componentAccessor, @Nonnull Ref<EntityStore> ref, Set<UUID> objectivePlayers) {
        Player playerComponent = componentAccessor.getComponent(ref, Player.getComponentType());
        if (playerComponent == null) {
            return;
        }
        if (this.consumeOnCompletion) {
            this.blockTypeOrTagTask.consumeItemStacks(playerComponent.getInventory().getCombinedHotbarFirst(), this.quantity);
        }
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SoloInventoryCondition that = (SoloInventoryCondition)o;
        if (this.quantity != that.quantity) {
            return false;
        }
        if (this.consumeOnCompletion != that.consumeOnCompletion) {
            return false;
        }
        if (this.holdInHand != that.holdInHand) {
            return false;
        }
        return this.blockTypeOrTagTask != null ? this.blockTypeOrTagTask.equals(that.blockTypeOrTagTask) : that.blockTypeOrTagTask == null;
    }

    @Override
    public int hashCode() {
        int result = this.blockTypeOrTagTask != null ? this.blockTypeOrTagTask.hashCode() : 0;
        result = 31 * result + this.quantity;
        result = 31 * result + (this.consumeOnCompletion ? 1 : 0);
        result = 31 * result + (this.holdInHand ? 1 : 0);
        return result;
    }

    @Nonnull
    public String toString() {
        return "SoloInventoryCondition{blockTypeOrTagTask=" + String.valueOf(this.blockTypeOrTagTask) + ", quantity=" + this.quantity + ", consumeOnCompletion=" + this.consumeOnCompletion + ", holdInHand=" + this.holdInHand + "} " + super.toString();
    }
}

