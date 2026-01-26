/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.patterns;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.SpaceSize;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public class FieldFunctionPattern
extends Pattern {
    @Nonnull
    private final Density field;
    @Nonnull
    private final SpaceSize readSpaceSize;
    @Nonnull
    private final List<Delimiter> delimiters;

    public FieldFunctionPattern(@Nonnull Density field) {
        this.field = field;
        this.readSpaceSize = SpaceSize.empty();
        this.delimiters = new ArrayList<Delimiter>(1);
    }

    @Override
    public boolean matches(@Nonnull Pattern.Context context) {
        Density.Context densityContext = new Density.Context(context);
        double density = this.field.process(densityContext);
        for (Delimiter d : this.delimiters) {
            if (!d.isInside(density)) continue;
            return true;
        }
        return false;
    }

    @Override
    @Nonnull
    public SpaceSize readSpace() {
        return this.readSpaceSize.clone();
    }

    public void addDelimiter(double min, double max) {
        Delimiter d = new Delimiter();
        d.min = min;
        d.max = max;
        this.delimiters.add(d);
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

