/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.item.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.codecs.map.EnumMapCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.Cosmetic;
import com.hypixel.hytale.protocol.ItemArmorSlot;
import com.hypixel.hytale.protocol.Modifier;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.modules.entitystats.RegeneratingValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat.DamageClass;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemArmor
implements NetworkSerializable<com.hypixel.hytale.protocol.ItemArmor> {
    public static final BuilderCodec<ItemArmor> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ItemArmor.class, ItemArmor::new).append(new KeyedCodec<ItemArmorSlot>("ArmorSlot", new EnumCodec<ItemArmorSlot>(ItemArmorSlot.class)), (itemArmor, itemArmorSlot) -> {
        itemArmor.armorSlot = itemArmorSlot;
    }, itemArmor -> itemArmor.armorSlot).addValidator(Validators.nonNull()).add()).append(new KeyedCodec("DamageResistance", new MapCodec<T[], HashMap>(new ArrayCodec<StaticModifier>(StaticModifier.CODEC, StaticModifier[]::new), HashMap::new)), (itemArmor, map) -> {
        itemArmor.damageResistanceValuesRaw = map;
    }, itemArmor -> itemArmor.damageResistanceValuesRaw).addValidator(DamageCause.VALIDATOR_CACHE.getMapKeyValidator()).add()).append(new KeyedCodec("DamageEnhancement", new MapCodec<T[], HashMap>(new ArrayCodec<StaticModifier>(StaticModifier.CODEC, StaticModifier[]::new), HashMap::new)), (itemArmor, map) -> {
        itemArmor.damageEnhancementValuesRaw = map;
    }, itemArmor -> itemArmor.damageEnhancementValuesRaw).addValidator(DamageCause.VALIDATOR_CACHE.getMapKeyValidator()).add()).appendInherited(new KeyedCodec("DamageClassEnhancement", new EnumMapCodec<DamageClass, T[]>(DamageClass.class, new ArrayCodec<StaticModifier>(StaticModifier.CODEC, StaticModifier[]::new))), (o, v) -> {
        o.damageClassEnhancement = v;
    }, o -> o.damageClassEnhancement, (o, p) -> {
        o.damageClassEnhancement = p.damageClassEnhancement;
    }).addValidator(Validators.nonNull()).add()).append(new KeyedCodec("KnockbackResistances", new MapCodec<Float, HashMap>(Codec.FLOAT, HashMap::new)), (itemArmor, map) -> {
        itemArmor.knockbackResistancesRaw = map;
    }, itemArmor -> itemArmor.knockbackResistancesRaw).addValidator(DamageCause.VALIDATOR_CACHE.getMapKeyValidator()).add()).append(new KeyedCodec("KnockbackEnhancements", new MapCodec<Float, HashMap>(Codec.FLOAT, HashMap::new)), (itemArmor, map) -> {
        itemArmor.knockbackEnhancementsRaw = map;
    }, itemArmor -> itemArmor.knockbackEnhancementsRaw).addValidator(DamageCause.VALIDATOR_CACHE.getMapKeyValidator()).add()).append(new KeyedCodec("Regenerating", new MapCodec<T[], HashMap>(new ArrayCodec<EntityStatType.Regenerating>(EntityStatType.Regenerating.CODEC, EntityStatType.Regenerating[]::new), HashMap::new)), (itemArmor, map) -> {
        itemArmor.regenerating = map;
    }, itemArmor -> itemArmor.regenerating).addValidator(EntityStatType.VALIDATOR_CACHE.getMapKeyValidator()).add()).append(new KeyedCodec<Double>("BaseDamageResistance", Codec.DOUBLE), (itemArmor, d) -> {
        itemArmor.baseDamageResistance = d;
    }, itemArmor -> itemArmor.baseDamageResistance).add()).append(new KeyedCodec("StatModifiers", new MapCodec<T[], HashMap>(new ArrayCodec<StaticModifier>(StaticModifier.CODEC, StaticModifier[]::new), HashMap::new)), (itemArmor, map) -> {
        itemArmor.rawStatModifiers = map;
    }, itemArmor -> itemArmor.rawStatModifiers).add()).append(new KeyedCodec("InteractionModifiers", new MapCodec(new MapCodec<StaticModifier, HashMap>(StaticModifier.CODEC, HashMap::new), HashMap::new)), (itemArmor, map) -> {
        itemArmor.interactionModifiersRaw = map;
    }, itemArmor -> itemArmor.interactionModifiersRaw).add()).append(new KeyedCodec<T[]>("CosmeticsToHide", new ArrayCodec<Cosmetic>(new EnumCodec<Cosmetic>(Cosmetic.class), Cosmetic[]::new)), (item, s) -> {
        item.cosmeticsToHide = s;
    }, item -> item.cosmeticsToHide).add()).afterDecode(item -> ItemArmor.processConfig(item))).build();
    @Nonnull
    protected ItemArmorSlot armorSlot = ItemArmorSlot.Head;
    @Nullable
    protected Map<String, StaticModifier[]> damageResistanceValuesRaw;
    @Nullable
    protected Map<DamageCause, StaticModifier[]> damageResistanceValues;
    @Nullable
    protected Map<String, StaticModifier[]> damageEnhancementValuesRaw;
    @Nullable
    protected Map<DamageCause, StaticModifier[]> damageEnhancementValues;
    protected double baseDamageResistance;
    @Nullable
    protected Map<String, StaticModifier[]> rawStatModifiers;
    @Nullable
    protected Int2ObjectMap<StaticModifier[]> statModifiers;
    protected Cosmetic[] cosmeticsToHide;
    @Nullable
    protected Map<String, EntityStatType.Regenerating[]> regenerating;
    @Nullable
    protected Int2ObjectMap<List<RegeneratingValue>> regeneratingValues;
    @Nullable
    protected Map<String, Float> knockbackResistancesRaw;
    @Nullable
    protected Map<DamageCause, Float> knockbackResistances;
    @Nullable
    protected Map<String, Float> knockbackEnhancementsRaw;
    @Nullable
    protected Map<DamageCause, Float> knockbackEnhancements;
    @Nullable
    protected Map<String, Map<String, StaticModifier>> interactionModifiersRaw;
    @Nullable
    protected Map<String, Int2ObjectMap<StaticModifier>> interactionModifiers;
    @Nonnull
    protected Map<DamageClass, StaticModifier[]> damageClassEnhancement = Collections.emptyMap();

    public ItemArmor(ItemArmorSlot armorSlot, double baseDamageResistance, @Nullable Int2ObjectMap<StaticModifier[]> statModifiers, Cosmetic[] cosmeticsToHide) {
        this.armorSlot = armorSlot;
        this.baseDamageResistance = baseDamageResistance;
        this.statModifiers = statModifiers;
        this.cosmeticsToHide = cosmeticsToHide;
    }

    protected ItemArmor() {
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.ItemArmor toPacket() {
        int i;
        Modifier[] modifiers;
        com.hypixel.hytale.protocol.ItemArmor packet = new com.hypixel.hytale.protocol.ItemArmor();
        packet.armorSlot = this.armorSlot;
        packet.cosmeticsToHide = this.cosmeticsToHide;
        packet.statModifiers = EntityStatMap.toPacket(this.statModifiers);
        packet.baseDamageResistance = this.baseDamageResistance;
        if (this.damageResistanceValues != null && !this.damageResistanceValues.isEmpty()) {
            Object2ObjectOpenHashMap<String, Modifier[]> damageResistanceMap = new Object2ObjectOpenHashMap<String, Modifier[]>();
            for (Map.Entry<Object, StaticModifier[]> entry : this.damageResistanceValues.entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) continue;
                modifiers = new Modifier[entry.getValue().length];
                for (i = 0; i < entry.getValue().length; ++i) {
                    modifiers[i] = entry.getValue()[i].toPacket();
                }
                damageResistanceMap.put(((DamageCause)entry.getKey()).getId(), modifiers);
            }
            Map<String, Modifier[]> map = packet.damageResistance = damageResistanceMap.isEmpty() ? null : damageResistanceMap;
        }
        if (this.damageClassEnhancement != null && !this.damageClassEnhancement.isEmpty()) {
            Object2ObjectOpenHashMap<String, Modifier[]> damageClassEnhancementMap = new Object2ObjectOpenHashMap<String, Modifier[]>();
            for (Map.Entry<Object, StaticModifier[]> entry : this.damageClassEnhancement.entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) continue;
                modifiers = new Modifier[entry.getValue().length];
                for (i = 0; i < entry.getValue().length; ++i) {
                    modifiers[i] = entry.getValue()[i].toPacket();
                }
                damageClassEnhancementMap.put(((DamageClass)((Object)entry.getKey())).name().toLowerCase(), modifiers);
            }
            Map<String, Modifier[]> map = packet.damageClassEnhancement = damageClassEnhancementMap.isEmpty() ? null : damageClassEnhancementMap;
        }
        if (this.damageEnhancementValues != null && !this.damageEnhancementValues.isEmpty()) {
            Object2ObjectOpenHashMap<String, Modifier[]> damageEnhancementMap = new Object2ObjectOpenHashMap<String, Modifier[]>();
            for (Map.Entry<Object, StaticModifier[]> entry : this.damageEnhancementValues.entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) continue;
                modifiers = new Modifier[entry.getValue().length];
                for (i = 0; i < entry.getValue().length; ++i) {
                    modifiers[i] = entry.getValue()[i].toPacket();
                }
                damageEnhancementMap.put(((DamageCause)entry.getKey()).getId(), modifiers);
            }
            packet.damageEnhancement = damageEnhancementMap.isEmpty() ? null : damageEnhancementMap;
        }
        return packet;
    }

    public ItemArmorSlot getArmorSlot() {
        return this.armorSlot;
    }

    public double getBaseDamageResistance() {
        return this.baseDamageResistance;
    }

    @Nullable
    public Int2ObjectMap<List<RegeneratingValue>> getRegeneratingValues() {
        return this.regeneratingValues;
    }

    @Nullable
    public Int2ObjectMap<StaticModifier[]> getStatModifiers() {
        return this.statModifiers;
    }

    @Nullable
    public Map<DamageCause, StaticModifier[]> getDamageResistanceValues() {
        return this.damageResistanceValues;
    }

    @Nullable
    public Map<DamageCause, StaticModifier[]> getDamageEnhancementValues() {
        return this.damageEnhancementValues;
    }

    @Nonnull
    public Map<DamageClass, StaticModifier[]> getDamageClassEnhancement() {
        return this.damageClassEnhancement;
    }

    @Nullable
    public Map<DamageCause, Float> getKnockbackEnhancements() {
        return this.knockbackEnhancements;
    }

    @Nullable
    public Map<DamageCause, Float> getKnockbackResistances() {
        return this.knockbackResistances;
    }

    @Nullable
    public Int2ObjectMap<StaticModifier> getInteractionModifier(String Key2) {
        if (this.interactionModifiers == null) {
            return null;
        }
        return this.interactionModifiers.get(Key2);
    }

    private static void processConfig(@Nonnull ItemArmor item) {
        ItemArmor.processStatModifiers(item);
        ItemArmor.processRegenModifiers(item);
        ItemArmor.processInteractionModifiers(item);
        item.damageResistanceValues = ItemArmor.convertStringKeyToDamageCause(item.damageResistanceValuesRaw);
        item.damageEnhancementValues = ItemArmor.convertStringKeyToDamageCause(item.damageEnhancementValuesRaw);
        item.knockbackResistances = ItemArmor.convertStringKeyToDamageCause(item.knockbackResistancesRaw);
        item.knockbackEnhancements = ItemArmor.convertStringKeyToDamageCause(item.knockbackEnhancementsRaw);
    }

    private static void processStatModifiers(@Nonnull ItemArmor item) {
        item.statModifiers = EntityStatsModule.resolveEntityStats(item.rawStatModifiers);
    }

    private static void processRegenModifiers(@Nonnull ItemArmor item) {
        if (item.regenerating == null) {
            return;
        }
        Int2ObjectOpenHashMap<List<RegeneratingValue>> values = new Int2ObjectOpenHashMap<List<RegeneratingValue>>();
        for (Map.Entry<String, EntityStatType.Regenerating[]> entry : item.regenerating.entrySet()) {
            int index = EntityStatType.getAssetMap().getIndex(entry.getKey());
            if (index == Integer.MIN_VALUE) continue;
            EntityStatType.Regenerating[] entryValue = entry.getValue();
            List operatingEntry = values.computeIfAbsent(index, ArrayList::new);
            for (EntityStatType.Regenerating regen : entryValue) {
                operatingEntry.add(new RegeneratingValue(regen));
            }
        }
        item.regeneratingValues = values;
    }

    private static void processInteractionModifiers(@Nonnull ItemArmor item) {
        if (item.interactionModifiersRaw == null) {
            return;
        }
        Object2ObjectOpenHashMap<String, Int2ObjectMap<StaticModifier>> values = new Object2ObjectOpenHashMap<String, Int2ObjectMap<StaticModifier>>();
        for (Map.Entry<String, Map<String, StaticModifier>> entry : item.interactionModifiersRaw.entrySet()) {
            String key = entry.getKey();
            for (Map.Entry<String, StaticModifier> stat : entry.getValue().entrySet()) {
                int index = EntityStatType.getAssetMap().getIndex(stat.getKey());
                if (index == Integer.MIN_VALUE) continue;
                StaticModifier statValue = stat.getValue();
                Int2ObjectMap statModMap = values.computeIfAbsent(key, k -> new Int2ObjectOpenHashMap());
                if (statModMap.get(index) == null) {
                    statModMap.put(index, new StaticModifier(statValue.getTarget(), statValue.getCalculationType(), statValue.getAmount()));
                    continue;
                }
                HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
                LOGGER.at(Level.SEVERE).log("ItemArmor::processInteractionModifiers - Interaction Mod %s / %s has multiple entries on the same object %s", key, stat.getKey(), item.armorSlot.name());
            }
        }
        item.interactionModifiers = values;
    }

    public static <T> Map<DamageCause, T> convertStringKeyToDamageCause(@Nullable Map<String, T> rawData) {
        Object2ObjectOpenHashMap<DamageCause, T> values = new Object2ObjectOpenHashMap<DamageCause, T>();
        if (rawData == null) {
            return null;
        }
        for (Map.Entry<String, T> entry : rawData.entrySet()) {
            DamageCause cause = (DamageCause)DamageCause.getAssetMap().getAsset(entry.getKey());
            if (cause == null) continue;
            values.put(cause, entry.getValue());
        }
        return values;
    }

    @Nonnull
    public String toString() {
        return "ItemArmor{armorSlot=" + String.valueOf((Object)this.armorSlot) + ", damageResistanceValues=" + String.valueOf(this.damageResistanceValues) + ", damageEnhancementValues=" + String.valueOf(this.damageEnhancementValues) + ", baseDamageResistance=" + this.baseDamageResistance + ", rawStatModifiers=" + String.valueOf(this.rawStatModifiers) + ", statModifiers=" + String.valueOf(this.statModifiers) + ", cosmeticsToHide=" + Arrays.toString((Object[])this.cosmeticsToHide) + ", regenerating=" + String.valueOf(this.regenerating) + ", regeneratingValues=" + String.valueOf(this.regeneratingValues) + ", knockbackResistances=" + String.valueOf(this.knockbackResistances) + ", knockbackEnhancements=" + String.valueOf(this.knockbackEnhancements) + ", interactionModifiersRaw=" + String.valueOf(this.interactionModifiersRaw) + ", interactionModifiers=" + String.valueOf(this.interactionModifiers) + "}";
    }

    public static enum InteractionModifierId {
        Dodge;

    }
}

