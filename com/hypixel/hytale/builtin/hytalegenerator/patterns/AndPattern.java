/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.patterns;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.SpaceSize;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
import java.util.List;
import javax.annotation.Nonnull;

public class AndPattern
extends Pattern {
    @Nonnull
    private final Pattern[] patterns;
    private final SpaceSize readSpaceSize;

    public AndPattern(@Nonnull List<Pattern> patterns) {
        if (patterns.isEmpty()) {
            this.patterns = new Pattern[0];
            this.readSpaceSize = SpaceSize.empty();
            return;
        }
        this.patterns = new Pattern[patterns.size()];
        SpaceSize spaceAcc = patterns.getFirst().readSpace();
        for (int i = 0; i < patterns.size(); ++i) {
            Pattern pattern;
            this.patterns[i] = pattern = patterns.get(i);
            spaceAcc = SpaceSize.merge(spaceAcc, pattern.readSpace());
        }
        this.readSpaceSize = spaceAcc;
    }

    @Override
    public boolean matches(@Nonnull Pattern.Context context) {
        for (Pattern pattern : this.patterns) {
            if (pattern.matches(context)) continue;
            return false;
        }
        return true;
    }

    @Override
    @Nonnull
    public SpaceSize readSpace() {
        return this.readSpaceSize.clone();
    }
}

