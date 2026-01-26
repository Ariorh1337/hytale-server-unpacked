/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.climate;

import com.hypixel.hytale.server.worldgen.climate.ClimateColor;
import com.hypixel.hytale.server.worldgen.climate.ClimateGraph;
import com.hypixel.hytale.server.worldgen.climate.ClimatePoint;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ClimateType {
    public static final ClimateType[] EMPTY_ARRAY = new ClimateType[0];
    private static final int MAX_DEPTH = 10;
    public static final int IS_ISLAND = 0x20000000;
    public static final int IS_SHORE = 0x40000000;
    public static final int IS_OCEAN = Integer.MIN_VALUE;
    public static final int IS_MAINLAND = 0;
    public static final int IS_MAINLAND_SHORE = 0x40000000;
    public static final int IS_MAINLAND_SHALLOW_OCEAN = -1073741824;
    public static final int IS_ISLAND_SHORE = 0x60000000;
    public static final int IS_ISLAND_SHALLOW_OCEAN = -536870912;
    public static final int MASK = 0x1FFFFFFF;
    @Nonnull
    public final String name;
    @Nonnull
    public final ClimateColor color;
    @Nonnull
    public final ClimateColor island;
    @Nonnull
    public final ClimatePoint[] points;
    @Nonnull
    public final ClimateType[] children;

    public ClimateType(@Nonnull String name, @Nonnull ClimateColor color, @Nonnull ClimateColor island, @Nonnull ClimatePoint[] points, @Nonnull ClimateType[] children) {
        this.name = name;
        this.color = color;
        this.island = island;
        this.points = points;
        this.children = children;
    }

    public String toString() {
        return this.name;
    }

    public static String name(@Nullable ClimateType parent, @Nonnull ClimateType type) {
        return parent == null || parent == type ? type.name : parent.name + " / " + type.name;
    }

    public static void walk(ClimateType type, Consumer<ClimateType> visitor) {
        ClimateType.walkRecursive(type, visitor, 0);
    }

    public static void walk(ClimateType[] types, Consumer<ClimateType> visitor) {
        for (ClimateType type : types) {
            ClimateType.walkRecursive(type, visitor, 0);
        }
    }

    public static int color(int id, @Nonnull ClimateGraph climate) {
        int flags = id & 0xE0000000;
        ClimateType type = climate.getType(id & 0x1FFFFFFF);
        return switch (flags) {
            case Integer.MIN_VALUE -> type.color.ocean;
            case 0x40000000 -> type.color.shore;
            case -1073741824 -> type.color.shallowOcean;
            case 0x20000000 -> type.island.land;
            case 0x60000000 -> type.island.shore;
            case -536870912 -> type.island.shallowOcean;
            default -> type.color.land;
        };
    }

    private static void walkRecursive(ClimateType type, Consumer<ClimateType> visitor, int depth) {
        if (depth >= 10) {
            return;
        }
        visitor.accept(type);
        for (ClimateType child : type.children) {
            ClimateType.walkRecursive(child, visitor, depth + 1);
        }
    }
}

