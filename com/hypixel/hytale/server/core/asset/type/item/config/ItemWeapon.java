/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.item.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemWeapon
implements NetworkSerializable<com.hypixel.hytale.protocol.ItemWeapon> {
    public static final BuilderCodec<ItemWeapon> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ItemWeapon.class, ItemWeapon::new).append(new KeyedCodec("StatModifiers", new MapCodec<T[], HashMap>(new ArrayCodec<StaticModifier>(StaticModifier.CODEC, StaticModifier[]::new), HashMap::new)), (itemArmor, map) -> {
        itemArmor.rawStatModifiers = map;
    }, itemArmor -> itemArmor.rawStatModifiers).addValidator(EntityStatType.VALIDATOR_CACHE.getMapKeyValidator().late()).add()).append(new KeyedCodec<T[]>("EntityStatsToClear", Codec.STRING_ARRAY), (itemWeapon, strings) -> {
        itemWeapon.rawEntityStatsToClear = strings;
    }, itemWeapon -> itemWeapon.rawEntityStatsToClear).add()).append(new KeyedCodec<Boolean>("RenderDualWielded", Codec.BOOLEAN), (itemWeapon, value) -> {
        itemWeapon.renderDualWielded = value;
    }, itemWeapon -> itemWeapon.renderDualWielded).add()).afterDecode(item -> {
        item.statModifiers = EntityStatsModule.resolveEntityStats(item.rawStatModifiers);
        item.entityStatsToClear = EntityStatsModule.resolveEntityStats(item.rawEntityStatsToClear);
    })).build();
    @Nullable
    protected Map<String, StaticModifier[]> rawStatModifiers;
    @Nullable
    protected Int2ObjectMap<StaticModifier[]> statModifiers;
    protected String[] rawEntityStatsToClear;
    @Nullable
    protected int[] entityStatsToClear;
    protected boolean renderDualWielded;

    @Nullable
    public Int2ObjectMap<StaticModifier[]> getStatModifiers() {
        return this.statModifiers;
    }

    public int[] getEntityStatsToClear() {
        return this.entityStatsToClear;
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.ItemWeapon toPacket() {
        return new com.hypixel.hytale.protocol.ItemWeapon(this.entityStatsToClear, EntityStatMap.toPacket(this.statModifiers), this.renderDualWielded);
    }

    @Nonnull
    public String toString() {
        return "ItemWeapon{rawStatModifiers=" + String.valueOf(this.rawStatModifiers) + ", statModifiers=" + String.valueOf(this.statModifiers) + ", rawEntityStatsToClear=" + Arrays.toString(this.rawEntityStatsToClear) + ", entityStatsToClear=" + Arrays.toString(this.entityStatsToClear) + ", renderDualWielded=" + this.renderDualWielded + "}";
    }
}

