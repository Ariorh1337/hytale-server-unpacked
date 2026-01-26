/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.climate;

import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.server.worldgen.climate.ClimatePoint;
import com.hypixel.hytale.server.worldgen.climate.ClimateType;
import com.hypixel.hytale.server.worldgen.climate.util.DistanceTransform;
import com.hypixel.hytale.server.worldgen.climate.util.DoubleMap;
import com.hypixel.hytale.server.worldgen.climate.util.IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;

public class ClimateGraph {
    public static final int RESOLUTION = 512;
    private static final double ONE_MINUS_EPS = 1.0 - MathUtil.EPSILON_DOUBLE;
    private final double width;
    private final double height;
    private final double fadeRadius;
    private final double fadeDistance;
    private final FadeMode fadeMode;
    @Nonnull
    private final ClimateType[] parents;
    @Nonnull
    private final ClimateType[] children;
    @Nonnull
    private final ClimateType[] id2TypeLookup;
    @Nonnull
    private final IntMap table;
    @Nonnull
    private final DoubleMap fade;
    @Nonnull
    private final Object2IntMap<ClimateType> type2IdLookup;

    public ClimateGraph(int resolution, @Nonnull ClimateType[] parents, @Nonnull FadeMode fadeMode, double fadeRadius, double fadeDistance) {
        this.table = new IntMap(resolution, resolution);
        this.fade = new DoubleMap(resolution, resolution);
        this.width = (double)resolution * ONE_MINUS_EPS;
        this.height = (double)resolution * ONE_MINUS_EPS;
        AtomicInteger typeCount = new AtomicInteger();
        ClimateType.walk(parents, climate -> typeCount.getAndIncrement());
        this.fadeMode = fadeMode;
        this.fadeRadius = fadeRadius;
        this.fadeDistance = fadeDistance;
        this.parents = parents;
        this.children = new ClimateType[typeCount.intValue() - parents.length];
        this.id2TypeLookup = new ClimateType[typeCount.intValue()];
        this.type2IdLookup = new Object2IntOpenHashMap<ClimateType>();
        AtomicInteger typeIndex = new AtomicInteger();
        AtomicInteger childIndex = new AtomicInteger();
        ClimateType[] climateTypeArray = parents;
        int n = climateTypeArray.length;
        for (int i = 0; i < n; ++i) {
            ClimateType parent;
            this.id2TypeLookup[typeIndex.get()] = parent = climateTypeArray[i];
            this.type2IdLookup.put(parent, typeIndex.get());
            typeIndex.getAndIncrement();
            ClimateType.walk(parent.children, climate -> {
                this.children[childIndex.get()] = climate;
                this.id2TypeLookup[typeIndex.get()] = climate;
                this.type2IdLookup.put((ClimateType)climate, typeIndex.get());
                typeIndex.getAndIncrement();
                childIndex.getAndIncrement();
            });
        }
        this.populateTable(this.table, this.fade);
    }

    public double fadeRadius() {
        return this.fadeRadius;
    }

    public double fadeDistance() {
        return this.fadeDistance;
    }

    public FadeMode fadeMode() {
        return this.fadeMode;
    }

    public void refresh() {
        this.populateTable(this.table, this.fade);
    }

    public ClimateType[] getParents() {
        return this.parents;
    }

    public ClimateType[] getChildren() {
        return this.children;
    }

    public IntMap getTable() {
        return this.table;
    }

    public DoubleMap getFade() {
        return this.fade;
    }

    public int indexOf(double x, double y) {
        int cx = MathUtil.floor(x * this.width);
        int cy = MathUtil.floor(y * this.height);
        return this.table.index(cx, cy);
    }

    public int getId(int index) {
        return this.table.at(index);
    }

    public double getFade(int index) {
        return this.fade.at(index) * this.fadeDistance;
    }

    public double getFadeRaw(int index) {
        return this.fade.at(index);
    }

    public int getId(double x, double y) {
        int cx = MathUtil.floor(x * this.width);
        int cy = MathUtil.floor(y * this.height);
        return this.table.at(cx, cy);
    }

    public double getFade(double x, double y) {
        return this.getFadeRaw(x, y) * this.fadeDistance;
    }

    public double getFadeRaw(double x, double y) {
        int cx = MathUtil.floor(x * this.width);
        int cy = MathUtil.floor(y * this.height);
        return this.fade.at(cx, cy);
    }

    public ClimateType getType(double x, double y) {
        int cx = MathUtil.floor(x * this.width);
        int cy = MathUtil.floor(y * this.height);
        int id = this.table.at(cx, cy);
        return this.id2TypeLookup[id];
    }

    public boolean validate(int id) {
        return (id & 0x1FFFFFFF) < this.id2TypeLookup.length;
    }

    public ClimateType getType(int id) {
        return this.id2TypeLookup[id & 0x1FFFFFFF];
    }

    private void populateTable(IntMap table, DoubleMap fade) {
        int y;
        int x;
        if (this.fadeMode == FadeMode.PARENTS) {
            for (x = 0; x < table.width; ++x) {
                for (y = 0; y < table.height; ++y) {
                    this.populatePixel(x, y, table, false);
                }
            }
            DistanceTransform.apply(table, fade, this.fadeRadius);
        }
        for (x = 0; x < table.width; ++x) {
            for (y = 0; y < table.height; ++y) {
                this.populatePixel(x, y, table, true);
            }
        }
        if (this.fadeMode == FadeMode.CHILDREN) {
            DistanceTransform.apply(table, fade, this.fadeRadius);
        }
    }

    private void populatePixel(int x, int y, IntMap table, boolean recursive) {
        ClimateType selected = null;
        ClimateType[] climates = this.parents;
        while (climates.length > 0) {
            ClimateType nearest = null;
            double minDist1 = Double.MAX_VALUE;
            double minDist2 = Double.MAX_VALUE;
            for (ClimateType climate : climates) {
                for (ClimatePoint point : climate.points) {
                    double dx = (double)x - point.temperature * this.width;
                    double dy = (double)y - point.intensity * this.height;
                    double dist2 = MathUtil.lengthSquared(dx, dy) / point.modifier;
                    if (dist2 < minDist1) {
                        minDist2 = minDist1;
                        minDist1 = dist2;
                        nearest = climate;
                        continue;
                    }
                    if (!(dist2 < minDist2)) continue;
                    minDist2 = dist2;
                }
            }
            if (nearest == null) break;
            selected = nearest;
            climates = nearest.children;
            if (recursive) continue;
            break;
        }
        if (selected == null) {
            table.set(x, y, -1);
            return;
        }
        table.set(x, y, this.type2IdLookup.getOrDefault((Object)selected, -1));
    }

    public static enum FadeMode {
        PARENTS,
        CHILDREN;

    }
}

