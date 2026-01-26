/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.zone;

import com.hypixel.hytale.server.worldgen.zone.Zone;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import javax.annotation.Nonnull;

public class ZoneColorMapping {
    @Nonnull
    protected final Int2ObjectMap<Zone[]> mapping = new Int2ObjectOpenHashMap<Zone[]>();

    public void add(int rgb, Zone zone) {
        this.add(rgb, new Zone[]{zone});
    }

    public void add(int rgb, Zone[] zones) {
        this.mapping.put(rgb, zones);
    }

    public Zone[] get(int rgb) {
        return (Zone[])this.mapping.get(rgb);
    }

    @Nonnull
    public String toString() {
        return "ZoneColorMapping{mapping=" + String.valueOf(this.mapping) + "}";
    }
}

