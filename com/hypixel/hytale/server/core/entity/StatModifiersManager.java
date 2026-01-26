/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.entity;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.asset.type.gameplay.BrokenPenalties;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemArmor;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StatModifiersManager {
    @Nonnull
    private final AtomicBoolean recalculate = new AtomicBoolean();
    @Nonnull
    private final IntSet statsToClear = new IntOpenHashSet();

    public void setRecalculate(boolean value) {
        this.recalculate.set(value);
    }

    public void queueEntityStatsToClear(@Nonnull int[] entityStatsToClear) {
        for (int i = 0; i < entityStatsToClear.length; ++i) {
            this.statsToClear.add(entityStatsToClear[i]);
        }
    }

    public void recalculateEntityStatModifiers(@Nonnull Ref<EntityStore> ref, @Nonnull EntityStatMap statMap, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        if (!this.recalculate.getAndSet(false)) {
            return;
        }
        if (!this.statsToClear.isEmpty()) {
            IntIterator iterator = this.statsToClear.iterator();
            while (iterator.hasNext()) {
                statMap.minimizeStatValue(EntityStatMap.Predictable.SELF, iterator.nextInt());
            }
            this.statsToClear.clear();
        }
        World world = componentAccessor.getExternalData().getWorld();
        Entity entity = EntityUtils.getEntity(ref, componentAccessor);
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        LivingEntity livingEntity = (LivingEntity)entity;
        Inventory inventory = livingEntity.getInventory();
        Int2ObjectOpenHashMap<Object2FloatMap<StaticModifier.CalculationType>> effectModifiers = StatModifiersManager.calculateEffectStatModifiers(ref, componentAccessor);
        StatModifiersManager.applyEffectModifiers(statMap, effectModifiers);
        BrokenPenalties brokenPenalties = world.getGameplayConfig().getItemDurabilityConfig().getBrokenPenalties();
        Int2ObjectMap<Object2FloatMap<StaticModifier.CalculationType>> statModifiers = StatModifiersManager.computeStatModifiers(brokenPenalties, inventory);
        StatModifiersManager.applyStatModifiers(statMap, statModifiers);
        ItemStack itemInHand = inventory.getItemInHand();
        StatModifiersManager.addItemStatModifiers(itemInHand, statMap, "*Weapon_", v -> v.getWeapon() != null ? v.getWeapon().getStatModifiers() : null);
        if (itemInHand == null || itemInHand.getItem().getUtility().isCompatible()) {
            StatModifiersManager.addItemStatModifiers(inventory.getUtilityItem(), statMap, "*Utility_", v -> v.getUtility().getStatModifiers());
        }
    }

    @Nonnull
    private static Int2ObjectOpenHashMap<Object2FloatMap<StaticModifier.CalculationType>> calculateEffectStatModifiers(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        Int2ObjectOpenHashMap<Object2FloatMap<StaticModifier.CalculationType>> statModifiers = new Int2ObjectOpenHashMap<Object2FloatMap<StaticModifier.CalculationType>>();
        EffectControllerComponent effectControllerComponent = componentAccessor.getComponent(ref, EffectControllerComponent.getComponentType());
        if (effectControllerComponent == null) {
            return statModifiers;
        }
        effectControllerComponent.getActiveEffects().forEach((k, v) -> {
            if (!v.isInfinite() && v.getRemainingDuration() <= 0.0f) {
                return;
            }
            int index = v.getEntityEffectIndex();
            EntityEffect effect = EntityEffect.getAssetMap().getAsset(index);
            if (effect == null || effect.getStatModifiers() == null) {
                return;
            }
            for (Int2ObjectMap.Entry entry : effect.getStatModifiers().int2ObjectEntrySet()) {
                int entityStatType = entry.getIntKey();
                for (StaticModifier modifier : (StaticModifier[])entry.getValue()) {
                    float value = modifier.getAmount();
                    Object2FloatMap statModifierToApply = statModifiers.computeIfAbsent(entityStatType, x -> new Object2FloatOpenHashMap());
                    statModifierToApply.mergeFloat(modifier.getCalculationType(), value, Float::sum);
                }
            }
        });
        return statModifiers;
    }

    /*
     * Could not resolve type clashes
     */
    private static void applyEffectModifiers(@Nonnull EntityStatMap statMap, @Nonnull Int2ObjectMap<Object2FloatMap<StaticModifier.CalculationType>> statModifiers) {
        for (int i = 0; i < statMap.size(); ++i) {
            Object2FloatMap statModifiersForEntityStat = (Object2FloatMap)statModifiers.get(i);
            if (statModifiersForEntityStat == null) {
                for (StaticModifier.CalculationType calculationType : StaticModifier.CalculationType.values()) {
                    statMap.removeModifier(i, calculationType.createKey("Effect"));
                }
                continue;
            }
            for (StaticModifier.CalculationType calculationType : StaticModifier.CalculationType.values()) {
                if (statModifiersForEntityStat.containsKey((Object)calculationType)) continue;
                statMap.removeModifier(i, calculationType.createKey("Effect"));
            }
            for (Object2FloatMap.Entry entry : statModifiersForEntityStat.object2FloatEntrySet()) {
                StaticModifier.CalculationType calculationType = (StaticModifier.CalculationType)((Object)entry.getKey());
                StaticModifier modifier = new StaticModifier(Modifier.ModifierTarget.MAX, calculationType, entry.getFloatValue());
                statMap.putModifier(i, calculationType.createKey("Effect"), modifier);
            }
        }
    }

    private static void computeStatModifiers(double brokenPenalty, @Nonnull Int2ObjectMap<Object2FloatMap<StaticModifier.CalculationType>> statModifiers, @Nonnull ItemStack itemInHand, @Nonnull Int2ObjectMap<StaticModifier[]> itemStatModifiers) {
        boolean broken = itemInHand.isBroken();
        for (Int2ObjectMap.Entry entry : itemStatModifiers.int2ObjectEntrySet()) {
            int entityStatType = entry.getIntKey();
            for (StaticModifier modifier : (StaticModifier[])entry.getValue()) {
                float value = modifier.getAmount();
                if (broken) {
                    value = (float)((double)value * brokenPenalty);
                }
                Object2FloatMap statModifierToApply = statModifiers.computeIfAbsent(entityStatType, x -> new Object2FloatOpenHashMap());
                statModifierToApply.mergeFloat(modifier.getCalculationType(), value, Float::sum);
            }
        }
    }

    @Nonnull
    private static Int2ObjectMap<Object2FloatMap<StaticModifier.CalculationType>> computeStatModifiers(@Nonnull BrokenPenalties brokenPenalties, @Nonnull Inventory inventory) {
        Int2ObjectOpenHashMap<Object2FloatMap<StaticModifier.CalculationType>> statModifiers = new Int2ObjectOpenHashMap<Object2FloatMap<StaticModifier.CalculationType>>();
        double armorBrokenPenalty = brokenPenalties.getArmor(0.0);
        ItemContainer armorContainer = inventory.getArmor();
        for (short i = 0; i < armorContainer.getCapacity(); i = (short)(i + 1)) {
            ItemStack armorItemStack = armorContainer.getItemStack(i);
            if (armorItemStack == null) continue;
            StatModifiersManager.addArmorStatModifiers(armorItemStack, armorBrokenPenalty, statModifiers);
        }
        return statModifiers;
    }

    private static void addArmorStatModifiers(@Nonnull ItemStack itemStack, double brokenPenalties, @Nonnull Int2ObjectOpenHashMap<Object2FloatMap<StaticModifier.CalculationType>> statModifiers) {
        if (ItemStack.isEmpty(itemStack)) {
            return;
        }
        ItemArmor armorItem = itemStack.getItem().getArmor();
        if (armorItem == null) {
            return;
        }
        Int2ObjectMap<StaticModifier[]> itemStatModifiers = armorItem.getStatModifiers();
        if (itemStatModifiers == null) {
            return;
        }
        StatModifiersManager.computeStatModifiers(brokenPenalties, statModifiers, itemStack, itemStatModifiers);
    }

    private static void addItemStatModifiers(@Nullable ItemStack itemStack, @Nonnull EntityStatMap entityStatMap, @Nonnull String prefix, @Nonnull Function<Item, Int2ObjectMap<StaticModifier[]>> toStatModifiers) {
        if (ItemStack.isEmpty(itemStack)) {
            StatModifiersManager.clearAllStatModifiers(EntityStatMap.Predictable.SELF, entityStatMap, prefix, null);
            return;
        }
        Int2ObjectMap<StaticModifier[]> itemStatModifiers = toStatModifiers.apply(itemStack.getItem());
        if (itemStatModifiers == null) {
            StatModifiersManager.clearAllStatModifiers(EntityStatMap.Predictable.SELF, entityStatMap, prefix, null);
            return;
        }
        for (Int2ObjectMap.Entry entry : itemStatModifiers.int2ObjectEntrySet()) {
            int offset = 0;
            int statIndex = entry.getIntKey();
            for (StaticModifier modifier : (StaticModifier[])entry.getValue()) {
                StaticModifier existingStatic;
                String key = prefix + offset;
                ++offset;
                Modifier existing = entityStatMap.getModifier(statIndex, key);
                if (existing instanceof StaticModifier && (existingStatic = (StaticModifier)existing).equals(modifier)) continue;
                entityStatMap.putModifier(EntityStatMap.Predictable.SELF, statIndex, key, modifier);
            }
            StatModifiersManager.clearStatModifiers(EntityStatMap.Predictable.SELF, entityStatMap, statIndex, prefix, offset);
        }
        StatModifiersManager.clearAllStatModifiers(EntityStatMap.Predictable.SELF, entityStatMap, prefix, itemStatModifiers);
    }

    private static void clearAllStatModifiers(@Nonnull EntityStatMap.Predictable predictable, @Nonnull EntityStatMap entityStatMap, @Nonnull String prefix, @Nullable Int2ObjectMap<StaticModifier[]> excluding) {
        for (int i = 0; i < entityStatMap.size(); ++i) {
            if (excluding != null && excluding.containsKey(i)) continue;
            StatModifiersManager.clearStatModifiers(predictable, entityStatMap, i, prefix, 0);
        }
    }

    private static void clearStatModifiers(@Nonnull EntityStatMap.Predictable predictable, @Nonnull EntityStatMap entityStatMap, int statIndex, @Nonnull String prefix, int offset) {
        String key;
        do {
            key = prefix + offset;
            ++offset;
        } while (entityStatMap.removeModifier(predictable, statIndex, key) != null);
    }

    /*
     * Could not resolve type clashes
     */
    private static void applyStatModifiers(@Nonnull EntityStatMap statMap, @Nonnull Int2ObjectMap<Object2FloatMap<StaticModifier.CalculationType>> statModifiers) {
        for (int i = 0; i < statMap.size(); ++i) {
            Object2FloatMap statModifiersForEntityStat = (Object2FloatMap)statModifiers.get(i);
            if (statModifiersForEntityStat == null) {
                for (StaticModifier.CalculationType calculationType : StaticModifier.CalculationType.values()) {
                    statMap.removeModifier(i, calculationType.createKey("Armor"));
                }
                continue;
            }
            for (StaticModifier.CalculationType calculationType : StaticModifier.CalculationType.values()) {
                if (statModifiersForEntityStat.containsKey((Object)calculationType)) continue;
                statMap.removeModifier(i, calculationType.createKey("Armor"));
            }
            for (Object2FloatMap.Entry entry : statModifiersForEntityStat.object2FloatEntrySet()) {
                StaticModifier.CalculationType calculationType = (StaticModifier.CalculationType)((Object)entry.getKey());
                StaticModifier modifier = new StaticModifier(Modifier.ModifierTarget.MAX, calculationType, entry.getFloatValue());
                statMap.putModifier(i, calculationType.createKey("Armor"), modifier);
            }
        }
    }
}

