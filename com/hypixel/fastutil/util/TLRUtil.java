/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.fastutil.util;

import com.hypixel.fastutil.util.SneakyThrow;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.concurrent.ThreadLocalRandom;
import sun.misc.Unsafe;

public class TLRUtil {
    private static final Unsafe UNSAFE;
    private static final long PROBE;

    public static void localInit() {
        ThreadLocalRandom.current();
    }

    public static int getProbe() {
        return UNSAFE.getInt(Thread.currentThread(), PROBE);
    }

    public static int advanceProbe(int probe) {
        probe ^= probe << 13;
        probe ^= probe >>> 17;
        probe ^= probe << 5;
        UNSAFE.putInt(Thread.currentThread(), PROBE, probe);
        return probe;
    }

    static {
        Unsafe instance;
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            instance = (Unsafe)field.get(null);
        }
        catch (Exception e1) {
            try {
                Constructor c = Unsafe.class.getDeclaredConstructor(new Class[0]);
                c.setAccessible(true);
                instance = (Unsafe)c.newInstance(new Object[0]);
            }
            catch (Exception e) {
                throw SneakyThrow.sneakyThrow(e);
            }
        }
        UNSAFE = instance;
        try {
            PROBE = UNSAFE.objectFieldOffset(Thread.class.getDeclaredField("threadLocalRandomProbe"));
        }
        catch (NoSuchFieldException t) {
            throw SneakyThrow.sneakyThrow(t);
        }
    }
}

