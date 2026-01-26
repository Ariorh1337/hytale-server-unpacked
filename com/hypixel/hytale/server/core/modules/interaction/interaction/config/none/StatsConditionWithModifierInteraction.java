/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.none;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.ValueType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemArmor;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.none.StatsConditionBaseInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StatsConditionWithModifierInteraction
extends StatsConditionBaseInteraction {
    @Nonnull
    public static final BuilderCodec<StatsConditionWithModifierInteraction> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(StatsConditionWithModifierInteraction.class, StatsConditionWithModifierInteraction::new, StatsConditionBaseInteraction.CODEC).documentation("Interaction that is successful if the given stat conditions match.")).append(new KeyedCodec<ItemArmor.InteractionModifierId>("InteractionModifierId", new EnumCodec<ItemArmor.InteractionModifierId>(ItemArmor.InteractionModifierId.class)), (changeStatWithModifierInteraction, s) -> {
        changeStatWithModifierInteraction.interactionModifierId = s;
    }, changeStatWithModifierInteraction -> changeStatWithModifierInteraction.interactionModifierId).addValidator(Validators.nonNull()).add()).build();
    protected ItemArmor.InteractionModifierId interactionModifierId;

    @Override
    protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        assert (commandBuffer != null);
        Ref<EntityStore> ref = context.getEntity();
        if (!this.canAfford(ref, commandBuffer)) {
            context.getState().state = InteractionState.Failed;
        }
    }

    @Override
    protected boolean canAfford(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        EntityStatMap entityStatMapComponent = componentAccessor.getComponent(ref, EntityStatMap.getComponentType());
        if (entityStatMapComponent == null) {
            return false;
        }
        if (this.costs == null) {
            return false;
        }
        for (Int2FloatMap.Entry cost : this.costs.int2FloatEntrySet()) {
            EntityStatValue stat = entityStatMapComponent.get(cost.getIntKey());
            if (stat == null) {
                return false;
            }
            float statValue = this.valueType == ValueType.Absolute ? stat.get() : stat.asPercentage() * 100.0f;
            Inventory inventory = null;
            Entity entity = EntityUtils.getEntity(ref, componentAccessor);
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)entity;
                inventory = livingEntity.getInventory();
            }
            float modifiedCost = this.calculateDiscount(inventory, cost.getIntKey(), cost.getFloatValue());
            if (!(this.lessThan ? statValue >= modifiedCost : statValue < modifiedCost && !this.canOverdraw(statValue, stat.getMin()))) continue;
            return false;
        }
        return true;
    }

    private float calculateDiscount(@Nullable Inventory inventory, int statIndex, float baseCost) {
        float modifiedCost = baseCost;
        float flatModifier = 0.0f;
        float multiplierModifier = 0.0f;
        ItemContainer armorContainer = null;
        if (inventory != null) {
            armorContainer = inventory.getArmor();
        }
        if (armorContainer != null) {
            for (short i = 0; i < armorContainer.getCapacity(); i = (short)(i + 1)) {
                Int2ObjectMap<StaticModifier> statModifierMap;
                Item item;
                ItemStack itemStack = armorContainer.getItemStack(i);
                if (itemStack == null || itemStack.isEmpty() || (item = itemStack.getItem()) == null || item.getArmor() == null || (statModifierMap = item.getArmor().getInteractionModifier(this.interactionModifierId.toString())) == null) continue;
                StaticModifier statModifier = (StaticModifier)statModifierMap.get(statIndex);
                if (statModifier.getCalculationType() == StaticModifier.CalculationType.ADDITIVE) {
                    flatModifier += statModifier.getAmount();
                    continue;
                }
                multiplierModifier = statModifier.getAmount();
            }
        }
        modifiedCost += flatModifier;
        return modifiedCost *= Math.max(0.0f, 1.0f - multiplierModifier);
    }

    @Override
    @Nonnull
    public String toString() {
        return "StatsConditionWithModifierInteraction{interactionModifierId=" + String.valueOf((Object)this.interactionModifierId) + "}" + super.toString();
    }
}

