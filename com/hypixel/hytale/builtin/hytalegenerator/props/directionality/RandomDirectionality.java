/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.props.directionality;

import com.hypixel.hytale.builtin.hytalegenerator.framework.math.SeedGenerator;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
import com.hypixel.hytale.builtin.hytalegenerator.props.directionality.Directionality;
import com.hypixel.hytale.builtin.hytalegenerator.scanners.Scanner;
import com.hypixel.hytale.math.util.FastRandom;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.prefab.PrefabRotation;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

public class RandomDirectionality
extends Directionality {
    @Nonnull
    private final List<PrefabRotation> rotations;
    @Nonnull
    private final Pattern pattern;
    @Nonnull
    private final SeedGenerator seedGenerator;

    public RandomDirectionality(@Nonnull Pattern pattern, int seed) {
        this.pattern = pattern;
        this.seedGenerator = new SeedGenerator(seed);
        this.rotations = Collections.unmodifiableList(List.of(PrefabRotation.ROTATION_0, PrefabRotation.ROTATION_90, PrefabRotation.ROTATION_180, PrefabRotation.ROTATION_270));
    }

    @Override
    @Nonnull
    public Pattern getGeneralPattern() {
        return this.pattern;
    }

    @Override
    @Nonnull
    public Vector3i getReadRangeWith(@Nonnull Scanner scanner) {
        return scanner.readSpaceWith(this.pattern).getRange();
    }

    @Override
    @Nonnull
    public List<PrefabRotation> getPossibleRotations() {
        return this.rotations;
    }

    @Override
    public PrefabRotation getRotationAt(@Nonnull Pattern.Context context) {
        FastRandom random = new FastRandom(this.seedGenerator.seedAt(context.position.x, context.position.y, context.position.z));
        return this.rotations.get(random.nextInt(this.rotations.size()));
    }
}

