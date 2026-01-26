/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.util;

import java.util.UUID;
import javax.annotation.Nonnull;

public class HashUtil {
    public static long hash(long v) {
        v = (v >>> 30 ^ v) * -4658895280553007687L;
        v = (v >>> 27 ^ v) * -7723592293110705685L;
        v = v >>> 31 ^ v;
        return v;
    }

    public static long hash(long l1, long l2) {
        l1 = (HashUtil.hash(l1) >>> 30 ^ l1) * -4658895280553007687L;
        l1 = HashUtil.hash(l2) >>> 31 ^ l1;
        return l1;
    }

    public static long hash(long l1, long l2, long l3) {
        l1 = (HashUtil.hash(l1) >>> 30 ^ l1) * -4658895280553007687L;
        l1 = (HashUtil.hash(l2) >>> 27 ^ l1) * -7723592293110705685L;
        l1 = HashUtil.hash(l3) >>> 31 ^ l1;
        return l1;
    }

    public static long hash(long l1, long l2, long l3, long l4) {
        l1 = (HashUtil.hash(l1) >>> 30 ^ l1) * -4658895280553007687L;
        l1 = (HashUtil.hash(l2) >>> 27 ^ l1) * -7723592293110705685L;
        l1 = (HashUtil.hash(l3) >>> 30 ^ l1) * -6389720478792763523L;
        l1 = HashUtil.hash(l4) >>> 31 ^ l1;
        return l1;
    }

    public static long rehash(long l1) {
        return HashUtil.hash(HashUtil.hash(l1));
    }

    public static long rehash(long l1, long l2) {
        return HashUtil.hash(HashUtil.hash(l1, l2));
    }

    public static long rehash(long l1, long l2, long l3) {
        return HashUtil.hash(HashUtil.hash(l1, l2, l3));
    }

    public static long rehash(long l1, long l2, long l3, long l4) {
        return HashUtil.hash(HashUtil.hash(l1, l2, l3, l4));
    }

    public static double random(long l1) {
        return HashUtil.hashToRandomDouble(HashUtil.rehash(l1));
    }

    public static double random(long l1, long l2) {
        return HashUtil.hashToRandomDouble(HashUtil.rehash(l1, l2));
    }

    public static double random(long l1, long l2, long l3) {
        return HashUtil.hashToRandomDouble(HashUtil.rehash(l1, l2, l3));
    }

    public static double random(long l1, long l2, long l3, long l4) {
        return HashUtil.hashToRandomDouble(HashUtil.rehash(l1, l2, l3, l4));
    }

    public static int randomInt(long l1, long l2, long l3, int bound) {
        long hash = HashUtil.rehash(l1, l2, l3);
        return (int)((hash &= Long.MAX_VALUE) % (long)bound);
    }

    private static double hashToRandomDouble(long hash) {
        return (double)(hash &= 0xFFFFFFFFL) / 4.294967295E9;
    }

    public static long hashUuid(@Nonnull UUID uuid) {
        return HashUtil.hash(uuid.getLeastSignificantBits(), uuid.getMostSignificantBits());
    }

    private HashUtil() {
    }
}

