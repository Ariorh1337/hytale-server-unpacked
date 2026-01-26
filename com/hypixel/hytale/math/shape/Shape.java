/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.shape;

import com.hypixel.hytale.function.predicate.TriIntObjPredicate;
import com.hypixel.hytale.function.predicate.TriIntPredicate;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;

public interface Shape {
    default public Box getBox(@Nonnull Vector3d position) {
        return this.getBox(position.getX(), position.getY(), position.getZ());
    }

    public Box getBox(double var1, double var3, double var5);

    default public boolean containsPosition(@Nonnull Vector3d origin, @Nonnull Vector3d position) {
        return this.containsPosition(position.getX() - origin.getX(), position.getY() - origin.getY(), position.getZ() - origin.getZ());
    }

    default public boolean containsPosition(@Nonnull Vector3d position) {
        return this.containsPosition(position.getX(), position.getY(), position.getZ());
    }

    public boolean containsPosition(double var1, double var3, double var5);

    public void expand(double var1);

    default public boolean forEachBlock(@Nonnull Vector3d origin, TriIntPredicate consumer) {
        return this.forEachBlock(origin.getX(), origin.getY(), origin.getZ(), consumer);
    }

    default public boolean forEachBlock(@Nonnull Vector3d origin, double epsilon, TriIntPredicate consumer) {
        return this.forEachBlock(origin.getX(), origin.getY(), origin.getZ(), epsilon, consumer);
    }

    default public boolean forEachBlock(double x, double y, double z, TriIntPredicate consumer) {
        return this.forEachBlock(x, y, z, 0.0, consumer);
    }

    public boolean forEachBlock(double var1, double var3, double var5, double var7, TriIntPredicate var9);

    default public <T> boolean forEachBlock(@Nonnull Vector3d origin, T t, TriIntObjPredicate<T> consumer) {
        return this.forEachBlock(origin.getX(), origin.getY(), origin.getZ(), t, consumer);
    }

    default public <T> boolean forEachBlock(@Nonnull Vector3d origin, double epsilon, T t, TriIntObjPredicate<T> consumer) {
        return this.forEachBlock(origin.getX(), origin.getY(), origin.getZ(), epsilon, t, consumer);
    }

    default public <T> boolean forEachBlock(double x, double y, double z, T t, TriIntObjPredicate<T> consumer) {
        return this.forEachBlock(x, y, z, 0.0, t, consumer);
    }

    public <T> boolean forEachBlock(double var1, double var3, double var5, double var7, T var9, TriIntObjPredicate<T> var10);
}

