/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.common.util;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RandomUtil {
    public static final ThreadLocal<SecureRandom> SECURE_RANDOM = ThreadLocal.withInitial(SecureRandom::new);

    public static <T> T roll(int roll, T[] data, @Nonnull int[] chances) {
        ++roll;
        int lower = 0;
        int upper = 0;
        for (int i = 0; i < chances.length; ++i) {
            int thisOne = chances[i];
            if (roll > lower && roll <= (upper += thisOne)) {
                return data[i];
            }
            lower += thisOne;
        }
        throw new AssertionError((Object)("Reached end of roll(" + roll + ", " + Arrays.toString(data) + ", " + Arrays.toString(chances) + ")!"));
    }

    public static int rollInt(int roll, int[] data, @Nonnull int[] chances) {
        ++roll;
        int lower = 0;
        int upper = 0;
        for (int i = 0; i < chances.length; ++i) {
            int thisOne = chances[i];
            if (roll > lower && roll <= (upper += thisOne)) {
                return data[i];
            }
            lower += thisOne;
        }
        throw new AssertionError((Object)("Reached end of roll(" + roll + ", " + Arrays.toString(data) + ", " + Arrays.toString(chances) + ")!"));
    }

    public static SecureRandom getSecureRandom() {
        return SECURE_RANDOM.get();
    }

    public static <T> T selectRandom(@Nonnull T[] arr, @Nonnull Random random) {
        return arr[random.nextInt(arr.length)];
    }

    @Nullable
    public static <T> T selectRandomOrNull(@Nonnull T[] arr, @Nonnull Random random) {
        int index = random.nextInt(arr.length + 1);
        if (index == arr.length) {
            return null;
        }
        return arr[index];
    }

    public static <T> T selectRandom(@Nonnull List<? extends T> list) {
        return RandomUtil.selectRandom(list, (Random)ThreadLocalRandom.current());
    }

    public static <T> T selectRandom(@Nonnull List<? extends T> list, @Nonnull Random random) {
        return list.get(random.nextInt(list.size()));
    }
}

