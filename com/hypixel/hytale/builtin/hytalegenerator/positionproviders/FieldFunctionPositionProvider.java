/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.math.vector.Vector3d;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public class FieldFunctionPositionProvider
extends PositionProvider {
    @Nonnull
    private final Density field;
    @Nonnull
    private final List<Delimiter> delimiters;
    @Nonnull
    private final PositionProvider positionProvider;

    public FieldFunctionPositionProvider(@Nonnull Density field, @Nonnull PositionProvider positionProvider) {
        this.field = field;
        this.positionProvider = positionProvider;
        this.delimiters = new ArrayList<Delimiter>();
    }

    public void addDelimiter(double min, double max) {
        Delimiter d = new Delimiter();
        d.min = min;
        d.max = max;
        this.delimiters.add(d);
    }

    @Override
    public void positionsIn(@Nonnull PositionProvider.Context context) {
        PositionProvider.Context childContext = new PositionProvider.Context(context);
        childContext.consumer = p -> {
            Density.Context densityContext = new Density.Context();
            densityContext.position = p;
            densityContext.positionsAnchor = context.anchor;
            densityContext.workerId = context.workerId;
            double value = this.field.process(densityContext);
            for (Delimiter d : this.delimiters) {
                if (!d.isInside(value)) continue;
                context.consumer.accept((Vector3d)p);
            }
        };
        this.positionProvider.positionsIn(childContext);
    }

    private static class Delimiter {
        double min;
        double max;

        private Delimiter() {
        }

        boolean isInside(double v) {
            return v >= this.min && v < this.max;
        }
    }
}

