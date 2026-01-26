/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.inventory.container;

import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemQuality;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import java.util.Comparator;
import java.util.function.Function;
import javax.annotation.Nonnull;

public enum SortType {
    NAME(i -> i.getItem().getTranslationKey(), false, false),
    TYPE(i -> Dummy.ItemType.getType(i.getItem()), false, true),
    RARITY(i -> {
        int qualityIndex = i.getItem().getQualityIndex();
        ItemQuality itemQuality = ItemQuality.getAssetMap().getAsset(qualityIndex);
        int itemQualityValue = (itemQuality != null ? itemQuality : ItemQuality.DEFAULT_ITEM_QUALITY).getQualityValue();
        return itemQualityValue;
    }, true, true);

    @Nonnull
    public static SortType[] VALUES;
    @Nonnull
    private final Comparator<ItemStack> comparator;

    private <U extends Comparable<U>> SortType(Function<ItemStack, U> key, boolean inverted, boolean thenName) {
        Comparator<ItemStack> comp = SortType.comparatorFor(key);
        if (inverted) {
            comp = comp.reversed();
        }
        if (thenName) {
            comp = comp.thenComparing(SortType.comparatorFor(i -> i.getItem().getTranslationKey()));
        }
        this.comparator = Comparator.nullsLast(comp);
    }

    @Nonnull
    public Comparator<ItemStack> getComparator() {
        return this.comparator;
    }

    @Nonnull
    public com.hypixel.hytale.protocol.SortType toPacket() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 1 -> com.hypixel.hytale.protocol.SortType.Type;
            case 2 -> com.hypixel.hytale.protocol.SortType.Rarity;
            case 0 -> com.hypixel.hytale.protocol.SortType.Name;
        };
    }

    @Nonnull
    public static SortType fromPacket(@Nonnull com.hypixel.hytale.protocol.SortType sortType_) {
        return switch (sortType_) {
            default -> throw new MatchException(null, null);
            case com.hypixel.hytale.protocol.SortType.Type -> TYPE;
            case com.hypixel.hytale.protocol.SortType.Rarity -> RARITY;
            case com.hypixel.hytale.protocol.SortType.Name -> NAME;
        };
    }

    @Nonnull
    private static <U extends Comparable<U>> Comparator<ItemStack> comparatorFor(@Nonnull Function<ItemStack, U> key) {
        return (a, b) -> {
            Comparable bkey;
            Comparable akey = (Comparable)key.apply((ItemStack)a);
            if (akey == (bkey = (Comparable)key.apply((ItemStack)b))) {
                return 0;
            }
            if (akey == null) {
                return 1;
            }
            if (bkey == null) {
                return -1;
            }
            return akey.compareTo(bkey);
        };
    }

    static {
        VALUES = SortType.values();
    }

    static class Dummy {
        Dummy() {
        }

        static enum ItemType {
            WEAPON,
            ARMOR,
            TOOL,
            ITEM,
            SPECIAL;


            @Nonnull
            private static ItemType getType(@Nonnull Item item) {
                if (item.getWeapon() != null) {
                    return WEAPON;
                }
                if (item.getArmor() != null) {
                    return ARMOR;
                }
                if (item.getTool() != null) {
                    return TOOL;
                }
                if (item.getBuilderToolData() != null) {
                    return SPECIAL;
                }
                return ITEM;
            }
        }
    }
}

