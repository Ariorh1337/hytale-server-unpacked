/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.shape;

import com.hypixel.hytale.math.shape.Box2D;
import com.hypixel.hytale.math.vector.Vector2d;
import javax.annotation.Nonnull;

public interface Shape2D {
    default public Box2D getBox(@Nonnull Vector2d position) {
        return this.getBox(position.getX(), position.getY());
    }

    public Box2D getBox(double var1, double var3);

    public boolean containsPosition(Vector2d var1, Vector2d var2);

    public boolean containsPosition(Vector2d var1, double var2, double var4);
}

