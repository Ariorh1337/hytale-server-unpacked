/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.framework.math.SeedGenerator;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.math.util.FastRandom;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;

public class FieldFunctionOccurrencePositionProvider
extends PositionProvider {
    public static final double FP_RESOLUTION = 100.0;
    @Nonnull
    private final Density field;
    @Nonnull
    private final PositionProvider positionProvider;
    @Nonnull
    private final SeedGenerator seedGenerator;

    public FieldFunctionOccurrencePositionProvider(@Nonnull Density field, @Nonnull PositionProvider positionProvider, int seed) {
        this.field = field;
        this.positionProvider = positionProvider;
        this.seedGenerator = new SeedGenerator(seed);
    }

    @Override
    public void positionsIn(@Nonnull PositionProvider.Context context) {
        PositionProvider.Context childContext = new PositionProvider.Context(context);
        childContext.consumer = position -> {
            FastRandom random;
            Density.Context densityContext = new Density.Context();
            densityContext.position = position;
            densityContext.positionsAnchor = context.anchor;
            densityContext.workerId = context.workerId;
            double discardChance = 1.0 - this.field.process(densityContext);
            if (discardChance > (random = new FastRandom(this.seedGenerator.seedAt(position.x, position.y, position.z, 100.0))).nextDouble()) {
                return;
            }
            context.consumer.accept((Vector3d)position);
        };
        this.positionProvider.positionsIn(childContext);
    }
}

