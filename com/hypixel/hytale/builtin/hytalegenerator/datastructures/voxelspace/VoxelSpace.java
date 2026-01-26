/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.datastructures.voxelspace;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
import com.hypixel.hytale.builtin.hytalegenerator.datastructures.voxelspace.VoxelConsumer;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface VoxelSpace<T> {
    public boolean set(T var1, int var2, int var3, int var4);

    public boolean set(T var1, @Nonnull Vector3i var2);

    public void set(T var1);

    public void setOrigin(int var1, int var2, int var3);

    @Nullable
    public T getContent(int var1, int var2, int var3);

    @Nullable
    public T getContent(@Nonnull Vector3i var1);

    public boolean replace(T var1, int var2, int var3, int var4, @Nonnull Predicate<T> var5);

    public void pasteFrom(@Nonnull VoxelSpace<T> var1);

    public int getOriginX();

    public int getOriginY();

    public int getOriginZ();

    public String getName();

    public boolean isInsideSpace(int var1, int var2, int var3);

    public boolean isInsideSpace(@Nonnull Vector3i var1);

    public void forEach(VoxelConsumer<? super T> var1);

    @Nonnull
    default public Bounds3i getBounds() {
        return new Bounds3i(new Vector3i(this.minX(), this.minY(), this.minZ()), new Vector3i(this.maxX(), this.maxY(), this.maxZ()));
    }

    public int minX();

    public int maxX();

    public int minY();

    public int maxY();

    public int minZ();

    public int maxZ();

    public int sizeX();

    public int sizeY();

    public int sizeZ();
}

