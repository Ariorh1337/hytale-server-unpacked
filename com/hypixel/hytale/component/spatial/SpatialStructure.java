/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component.spatial;

import com.hypixel.hytale.component.spatial.SpatialData;
import com.hypixel.hytale.math.vector.Vector3d;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface SpatialStructure<T> {
    public int size();

    public void rebuild(@Nonnull SpatialData<T> var1);

    @Nullable
    public T closest(@Nonnull Vector3d var1);

    public void collect(@Nonnull Vector3d var1, double var2, @Nonnull List<T> var4);

    public void collectCylinder(@Nonnull Vector3d var1, double var2, double var4, @Nonnull List<T> var6);

    public void collectBox(@Nonnull Vector3d var1, @Nonnull Vector3d var2, @Nonnull List<T> var3);

    public void ordered(@Nonnull Vector3d var1, double var2, @Nonnull List<T> var4);

    public void ordered3DAxis(@Nonnull Vector3d var1, double var2, double var4, double var6, @Nonnull List<T> var8);

    @Nonnull
    public String dump();
}

