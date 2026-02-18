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
    @Nonnull
    private final Vector3i rScanMin;
    @Nonnull
    private final Vector3i rScanMax;
    @Nonnull
    private final Vector3i rChildPosition;
    @Nonnull
    private final Pattern.Context rChildContext;

    public CuboidPattern(@Nonnull Pattern subPattern, @Nonnull Vector3i min, @Nonnull Vector3i max) {
        this.subPattern = subPattern;
        this.min = min;
        this.max = max;
        this.readSpaceSize = new SpaceSize(min, max.clone().add(1, 1, 1));
        this.rScanMin = new Vector3i();
        this.rScanMax = new Vector3i();
        this.rChildPosition = new Vector3i();
        this.rChildContext = new Pattern.Context();
    }

    @Override
    public boolean matches(@Nonnull Pattern.Context context) {
        this.rScanMin.assign(this.min).add(context.position);
        this.rScanMax.assign(this.max).add(context.position);
        this.rChildPosition.assign(context.position);
        this.rChildContext.assign(context);
        this.rChildContext.position = this.rChildPosition;
        this.rChildPosition.x = this.rScanMin.x;
        while (this.rChildPosition.x <= this.rScanMax.x) {
            this.rChildPosition.z = this.rScanMin.z;
            while (this.rChildPosition.z <= this.rScanMax.z) {
                this.rChildPosition.y = this.rScanMin.y;
                while (this.rChildPosition.y <= this.rScanMax.y) {
                    if (!context.materialSpace.isInsideSpace(this.rChildPosition)) {
                        return false;
                    }
                    if (!this.subPattern.matches(this.rChildContext)) {
                        return false;
                    }
                    ++this.rChildPosition.y;
                }
                ++this.rChildPosition.z;
            }
            ++this.rChildPosition.x;
        }
        return true;
    }

    @Override
    @Nonnull
    public SpaceSize readSpace() {
        return this.readSpaceSize.clone();
    }
}

