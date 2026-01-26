/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.unsafe;

import com.hypixel.hytale.sneakythrow.SneakyThrow;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import javax.annotation.Nullable;
import sun.misc.Unsafe;

public class UnsafeUtil {
    @Nullable
    public static final Unsafe UNSAFE;

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
    }
}

