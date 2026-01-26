/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.role.support;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Arrays;
import java.util.EnumMap;
import javax.annotation.Nullable;

public class RoleStats {
    protected final EnumMap<RangeType, IntSet> playerRanges = new EnumMap(RangeType.class);
    protected final EnumMap<RangeType, IntSet> npcRanges = new EnumMap(RangeType.class);
    @Nullable
    protected IntArrayList playerBucketRanges;
    @Nullable
    protected IntArrayList npcBucketRanges;

    public void clear() {
        this.playerRanges.clear();
        this.npcRanges.clear();
        this.playerBucketRanges = null;
        this.npcBucketRanges = null;
    }

    public void trackRange(boolean isPlayer, RangeType type, int value) {
        EnumMap<RangeType, IntSet> map = isPlayer ? this.playerRanges : this.npcRanges;
        map.computeIfAbsent(type, t -> new IntOpenHashSet()).add(value);
    }

    public IntSet getRanges(boolean isPlayer, RangeType type) {
        EnumMap<RangeType, IntSet> map = isPlayer ? this.playerRanges : this.npcRanges;
        return map.get((Object)type);
    }

    public int[] getRangesSorted(boolean isPlayer, RangeType type) {
        IntSet intSet = this.getRanges(isPlayer, type);
        if (intSet == null) {
            return null;
        }
        int[] intArray = intSet.toIntArray();
        Arrays.sort(intArray);
        return intArray;
    }

    public void trackBuckets(boolean isPlayer, IntArrayList bucketRanges) {
        if (isPlayer) {
            this.playerBucketRanges = bucketRanges;
        } else {
            this.npcBucketRanges = bucketRanges;
        }
    }

    @Nullable
    public IntArrayList getBuckets(boolean isPlayer) {
        return isPlayer ? this.playerBucketRanges : this.npcBucketRanges;
    }

    public static enum RangeType {
        SORTED,
        UNSORTED,
        AVOIDANCE;

    }
}

