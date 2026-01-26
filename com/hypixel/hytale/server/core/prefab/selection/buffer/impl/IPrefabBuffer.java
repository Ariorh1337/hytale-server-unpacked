/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.prefab.selection.buffer.impl;

import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.server.core.prefab.PrefabRotation;
import com.hypixel.hytale.server.core.prefab.PrefabWeights;
import com.hypixel.hytale.server.core.prefab.selection.buffer.PrefabBufferCall;
import com.hypixel.hytale.server.core.prefab.selection.buffer.impl.PrefabBuffer;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IPrefabBuffer {
    public static final ColumnPredicate<?> ALL_COLUMNS = (x, z, blocks, o) -> true;

    public int getAnchorX();

    public int getAnchorY();

    public int getAnchorZ();

    public int getMinX(@Nonnull PrefabRotation var1);

    public int getMinY();

    public int getMinZ(@Nonnull PrefabRotation var1);

    public int getMaxX(@Nonnull PrefabRotation var1);

    public int getMaxY();

    public int getMaxZ(@Nonnull PrefabRotation var1);

    default public int getMinX() {
        return this.getMinX(PrefabRotation.ROTATION_0);
    }

    default public int getMinZ() {
        return this.getMinZ(PrefabRotation.ROTATION_0);
    }

    default public int getMaxX() {
        return this.getMaxX(PrefabRotation.ROTATION_0);
    }

    default public int getMaxZ() {
        return this.getMaxZ(PrefabRotation.ROTATION_0);
    }

    public int getMinYAt(@Nonnull PrefabRotation var1, int var2, int var3);

    public int getMaxYAt(@Nonnull PrefabRotation var1, int var2, int var3);

    public int getColumnCount();

    @Nonnull
    public PrefabBuffer.ChildPrefab[] getChildPrefabs();

    default public int getMaximumExtend() {
        int max = 0;
        for (PrefabRotation rotation : PrefabRotation.VALUES) {
            int z;
            int x = this.getMaxX(rotation) - this.getMinX(rotation);
            if (x > max) {
                max = x;
            }
            if ((z = this.getMaxZ(rotation) - this.getMinZ(rotation)) <= max) continue;
            max = z;
        }
        return max;
    }

    public <T extends PrefabBufferCall> void forEach(@Nonnull ColumnPredicate<T> var1, @Nonnull BlockConsumer<T> var2, @Nullable EntityConsumer<T> var3, @Nullable ChildConsumer<T> var4, @Nonnull T var5);

    public <T> void forEachRaw(@Nonnull ColumnPredicate<T> var1, @Nonnull RawBlockConsumer<T> var2, @Nonnull FluidConsumer<T> var3, @Nullable EntityConsumer<T> var4, @Nullable T var5);

    public <T> boolean forEachRaw(@Nonnull ColumnPredicate<T> var1, @Nonnull RawBlockPredicate<T> var2, @Nonnull FluidPredicate<T> var3, @Nullable EntityPredicate<T> var4, @Nullable T var5);

    public void release();

    default public <T extends PrefabBufferCall> boolean compare(@Nonnull BlockComparingPredicate<T> blockComparingPredicate, @Nonnull T t) {
        return this.forEachRaw(IPrefabBuffer.iterateAllColumns(), (int x, int y, int z, int blockId, float chance, Holder<ChunkStore> holder, int support, int rotation, int filler, T o) -> blockComparingPredicate.test(x, y, z, blockId, rotation, holder, o), (int x, int y, int z, int fluidId, byte level, T o) -> true, (int x, int z, Holder<EntityStore>[] entityWrappers, T o) -> true, t);
    }

    default public <T extends PrefabBufferCall> boolean compare(@Nonnull BlockComparingPrefabPredicate<T> blockComparingIterator, @Nonnull T t, @Nonnull IPrefabBuffer secondPrefab) {
        throw new UnsupportedOperationException("Not implemented! Please implement some inefficient default here!");
    }

    public int getBlockId(int var1, int var2, int var3);

    public int getFiller(int var1, int var2, int var3);

    public int getRotationIndex(int var1, int var2, int var3);

    @Nonnull
    public static <T> ColumnPredicate<T> iterateAllColumns() {
        return ALL_COLUMNS;
    }

    @FunctionalInterface
    public static interface ColumnPredicate<T> {
        public boolean test(int var1, int var2, int var3, T var4);
    }

    @FunctionalInterface
    public static interface BlockComparingPredicate<T> {
        public boolean test(int var1, int var2, int var3, int var4, int var5, Holder<ChunkStore> var6, T var7);
    }

    @FunctionalInterface
    public static interface RawBlockPredicate<T> {
        public boolean test(int var1, int var2, int var3, int var4, float var5, Holder<ChunkStore> var6, int var7, int var8, int var9, T var10);
    }

    @FunctionalInterface
    public static interface FluidPredicate<T> {
        public boolean test(int var1, int var2, int var3, int var4, byte var5, T var6);
    }

    @FunctionalInterface
    public static interface EntityPredicate<T> {
        public boolean test(int var1, int var2, @Nonnull Holder<EntityStore>[] var3, T var4);
    }

    @FunctionalInterface
    public static interface BlockComparingPrefabPredicate<T> {
        public boolean test(int var1, int var2, int var3, int var4, Holder<ChunkStore> var5, float var6, int var7, int var8, int var9, Holder<ChunkStore> var10, float var11, int var12, int var13, T var14);
    }

    @FunctionalInterface
    public static interface RawBlockConsumer<T> {
        public void accept(int var1, int var2, int var3, int var4, int var5, float var6, Holder<ChunkStore> var7, int var8, int var9, int var10, T var11);
    }

    @FunctionalInterface
    public static interface EntityConsumer<T> {
        public void accept(int var1, int var2, @Nullable Holder<EntityStore>[] var3, T var4);
    }

    @FunctionalInterface
    public static interface ChildConsumer<T> {
        public void accept(int var1, int var2, int var3, String var4, boolean var5, boolean var6, boolean var7, PrefabWeights var8, PrefabRotation var9, T var10);
    }

    @FunctionalInterface
    public static interface FluidConsumer<T> {
        public void accept(int var1, int var2, int var3, int var4, byte var5, T var6);
    }

    @FunctionalInterface
    public static interface BlockConsumer<T> {
        public void accept(int var1, int var2, int var3, int var4, @Nullable Holder<ChunkStore> var5, int var6, int var7, int var8, T var9, int var10, int var11);
    }
}

