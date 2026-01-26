/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.climate;

public class ClimateColor {
    public static final int UNSET = -1;
    public static final int ISLAND = 0x554433;
    public static final int ISLAND_SHORE = 0x775544;
    public static final int SHORE = 0xDDCC99;
    public static final int OCEAN = 33913;
    public static final int SHALLOW_OCEAN = 0x3388CC;
    public final int land;
    public final int shore;
    public final int ocean;
    public final int shallowOcean;

    public ClimateColor(int land, int shore, int ocean, int shallowOcean) {
        this.land = land;
        this.shore = shore;
        this.ocean = ocean;
        this.shallowOcean = shallowOcean;
    }
}

