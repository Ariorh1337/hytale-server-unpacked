/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.common.map;

import com.hypixel.hytale.function.function.BiDoubleToDoubleFunction;
import com.hypixel.hytale.function.function.BiIntToDoubleFunction;
import com.hypixel.hytale.function.function.BiLongToDoubleFunction;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ObjDoubleConsumer;
import javax.annotation.Nullable;

public interface IWeightedMap<T> {
    @Nullable
    public T get(double var1);

    @Nullable
    public T get(DoubleSupplier var1);

    @Nullable
    public T get(Random var1);

    @Nullable
    public T get(int var1, int var2, BiIntToDoubleFunction var3);

    @Nullable
    public T get(long var1, long var3, BiLongToDoubleFunction var5);

    @Nullable
    public T get(double var1, double var3, BiDoubleToDoubleFunction var5);

    @Nullable
    public <K> T get(int var1, int var2, int var3, SeedCoordinateFunction<K> var4, K var5);

    public int size();

    public boolean contains(T var1);

    public void forEach(Consumer<T> var1);

    public void forEachEntry(ObjDoubleConsumer<T> var1);

    public T[] internalKeys();

    public T[] toArray();

    public <K> IWeightedMap<K> resolveKeys(Function<T, K> var1, IntFunction<K[]> var2);

    @FunctionalInterface
    public static interface SeedCoordinateFunction<T> {
        public double apply(int var1, int var2, int var3, T var4);
    }
}

