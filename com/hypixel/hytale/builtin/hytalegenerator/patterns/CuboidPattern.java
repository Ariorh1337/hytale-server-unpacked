/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.patterns;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.SpaceSize;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
import com.hypixel.hytale.math.vector.Vector3i;
import javax.annotation.Nonnull;

public class CuboidPattern
extends Pattern {
    @Nonnull
    private final Pattern subPattern;
    @Nonnull
    private final Vector3i min;
    @Nonnull
    private final Vector3i max;
    @Nonnull
    private final SpaceSize readSpaceSize;

    public CuboidPattern(@Nonnull Pattern subPattern, @Nonnull Vector3i min, @Nonnull Vector3i max) {
        this.subPattern = subPattern;
        this.min = min;
        this.max = max;
        this.readSpaceSize = new SpaceSize(min, max.clone().add(1, 1, 1));
    }

    @Override
    public boolean matches(@Nonnull Pattern.Context context) {
        Vector3i scanMin = this.min.clone().add(context.position);
        Vector3i scanMax = this.max.clone().add(context.position);
        Vector3i childPosition = context.position.clone();
        Pattern.Context childContext = new Pattern.Context(context);
        childContext.position = childPosition;
        childPosition.x = scanMin.x;
        while (childPosition.x <= scanMax.x) {
            childPosition.z = scanMin.z;
            while (childPosition.z <= scanMax.z) {
                childPosition.y = scanMin.y;
                while (childPosition.y <= scanMax.y) {
                    if (!context.materialSpace.isInsideSpace(childPosition)) {
                        return false;
                    }
                    if (!this.subPattern.matches(childContext)) {
                        return false;
                    }
                    ++childPosition.y;
                }
                ++childPosition.z;
            }
            ++childPosition.x;
        }
        return true;
    }

    @Override
    @Nonnull
    public SpaceSize readSpace() {
        return this.readSpaceSize.clone();
    }
}

