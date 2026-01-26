/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.server;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionType;
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
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.ChangeStatBaseInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import javax.annotation.Nonnull;

public class ChangeStatWithModifierInteraction
extends ChangeStatBaseInteraction {
    public static final BuilderCodec<ChangeStatWithModifierInteraction> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ChangeStatWithModifierInteraction.class, ChangeStatWithModifierInteraction::new, ChangeStatBaseInteraction.CODEC).documentation("Changes the given stats.")).append(new KeyedCodec<ItemArmor.InteractionModifierId>("InteractionModifierId", new EnumCodec<ItemArmor.InteractionModifierId>(ItemArmor.InteractionModifierId.class)), (changeStatWithModifierInteraction, s) -> {
        changeStatWithModifierInteraction.interactionModifierId = s;
    }, changeStatWithModifierInteraction -> changeStatWithModifierInteraction.interactionModifierId).addValidator(Validators.nonNull()).add()).build();
    protected ItemArmor.InteractionModifierId interactionModifierId;

    @Override
    protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        Ref<EntityStore> ref = context.getEntity();
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        assert (commandBuffer != null);
        EntityStatMap entityStatMapComponent = commandBuffer.getComponent(ref, EntityStatMap.getComponentType());
        assert (entityStatMapComponent != null);
        Int2FloatOpenHashMap adjustedEntityStats = new Int2FloatOpenHashMap(this.entityStats);
        Inventory inventory = null;
        Entity entity = EntityUtils.getEntity(ref, commandBuffer);
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            inventory = livingEntity.getInventory();
        }
        IntIterator intIterator = adjustedEntityStats.keySet().iterator();
        while (intIterator.hasNext()) {
            ItemContainer armorContainer;
            int index = (Integer)intIterator.next();
            if (inventory == null || (armorContainer = inventory.getArmor()) == null) continue;
            float flatModifier = 0.0f;
            float multiplierModifier = 0.0f;
            for (short i = 0; i < armorContainer.getCapacity(); i = (short)(i + 1)) {
                StaticModifier statModifier;
                Int2ObjectMap<StaticModifier> statModifierMap;
                Item item;
                ItemStack itemStack = armorContainer.getItemStack(i);
                if (itemStack == null || itemStack.isEmpty() || (item = itemStack.getItem()) == null || item.getArmor() == null || (statModifierMap = item.getArmor().getInteractionModifier(this.interactionModifierId.toString())) == null || (statModifier = (StaticModifier)statModifierMap.get(index)) == null) continue;
                if (statModifier.getCalculationType() == StaticModifier.CalculationType.ADDITIVE) {
                    flatModifier += statModifier.getAmount();
                    continue;
                }
                multiplierModifier = statModifier.getAmount();
            }
            float cost = this.entityStats.get(index);
            cost += flatModifier;
            adjustedEntityStats.replace(index, cost *= Math.max(0.0f, 1.0f - multiplierModifier));
        }
        entityStatMapComponent.processStatChanges(EntityStatMap.Predictable.NONE, adjustedEntityStats, this.valueType, this.changeStatBehaviour);
    }

    @Override
    @Nonnull
    public String toString() {
        return "ChangeStatWithModifierInteraction{interactionModifierId=" + String.valueOf((Object)this.interactionModifierId) + "}" + super.toString();
    }
}

