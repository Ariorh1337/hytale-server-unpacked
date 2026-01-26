/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.fields.points;

import com.hypixel.hytale.builtin.hytalegenerator.fields.points.PointProvider;
import com.hypixel.hytale.math.vector.Vector2d;
import com.hypixel.hytale.math.vector.Vector2i;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public abstract class PointField
implements PointProvider {
    protected double scaleX = 1.0;
    protected double scaleY = 1.0;
    protected double scaleZ = 1.0;
    protected double scaleW = 1.0;

    @Override
    @Nonnull
    public List<Vector3i> points3i(@Nonnull Vector3i min, @Nonnull Vector3i max) {
        ArrayList<Vector3i> list = new ArrayList<Vector3i>();
        this.points3i(min, max, list::add);
        return list;
    }

    @Override
    @Nonnull
    public List<Vector2i> points2i(@Nonnull Vector2i min, @Nonnull Vector2i max) {
        ArrayList<Vector2i> list = new ArrayList<Vector2i>();
        this.points2i(min, max, list::add);
        return list;
    }

    @Override
    @Nonnull
    public List<Integer> points1i(int min, int max) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        this.points1i(min, max, list::add);
        return list;
    }

    @Override
    @Nonnull
    public List<Vector3d> points3d(@Nonnull Vector3d min, @Nonnull Vector3d max) {
        ArrayList<Vector3d> list = new ArrayList<Vector3d>();
        this.points3d(min, max, list::add);
        return list;
    }

    @Override
    @Nonnull
    public List<Vector2d> points2d(@Nonnull Vector2d min, @Nonnull Vector2d max) {
        ArrayList<Vector2d> list = new ArrayList<Vector2d>();
        this.points2d(min, max, list::add);
        return list;
    }

    @Override
    @Nonnull
    public List<Double> points1d(double min, double max) {
        ArrayList<Double> list = new ArrayList<Double>();
        this.points1d(min, max, list::add);
        return list;
    }

    public PointField setScale(double scaleX, double scaleY, double scaleZ, double scaleW) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
        this.scaleW = scaleW;
        return this;
    }

    @Nonnull
    public PointField setScale(double scale) {
        this.setScale(scale, scale, scale, scale);
        return this;
    }
}

