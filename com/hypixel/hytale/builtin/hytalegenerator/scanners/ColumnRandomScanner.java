/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.scanners;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.SpaceSize;
import com.hypixel.hytale.builtin.hytalegenerator.framework.interfaces.functions.BiDouble2DoubleFunction;
import com.hypixel.hytale.builtin.hytalegenerator.framework.math.SeedGenerator;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
import com.hypixel.hytale.builtin.hytalegenerator.scanners.Scanner;
import com.hypixel.hytale.math.util.FastRandom;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ColumnRandomScanner
extends Scanner {
    private final int minY;
    private final int maxY;
    private final boolean isRelativeToPosition;
    @Nullable
    private final BiDouble2DoubleFunction bedFunction;
    private final int resultsCap;
    @Nonnull
    private final SeedGenerator seedGenerator;
    @Nonnull
    private final Strategy strategy;
    @Nonnull
    private final SpaceSize scanSpaceSize;

    public ColumnRandomScanner(int minY, int maxY, int resultsCap, int seed, @Nonnull Strategy strategy, boolean isRelativeToPosition, @Nullable BiDouble2DoubleFunction bedFunction) {
        if (resultsCap < 0) {
            throw new IllegalArgumentException();
        }
        this.bedFunction = bedFunction;
        this.minY = minY;
        this.maxY = maxY;
        this.isRelativeToPosition = isRelativeToPosition;
        this.resultsCap = resultsCap;
        this.seedGenerator = new SeedGenerator(seed);
        this.strategy = strategy;
        this.scanSpaceSize = new SpaceSize(new Vector3i(0, 0, 0), new Vector3i(1, 0, 1));
    }

    @Override
    @Nonnull
    public List<Vector3i> scan(@Nonnull Scanner.Context context) {
        return switch (this.strategy.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> this.scanDartThrow(context);
            case 1 -> this.scanPickValid(context);
        };
    }

    @Nonnull
    private List<Vector3i> scanPickValid(@Nonnull Scanner.Context context) {
        int scanMaxY;
        int scanMinY;
        if (this.resultsCap == 0) {
            return Collections.emptyList();
        }
        if (this.isRelativeToPosition) {
            scanMinY = Math.max(context.position.y + this.minY, context.materialSpace.minY());
            scanMaxY = Math.min(context.position.y + this.maxY, context.materialSpace.maxY());
        } else if (this.bedFunction != null) {
            int bedY = (int)this.bedFunction.apply(context.position.x, context.position.z);
            scanMinY = Math.max(bedY + this.minY, context.materialSpace.minY());
            scanMaxY = Math.min(bedY + this.maxY, context.materialSpace.maxY());
        } else {
            scanMinY = Math.max(this.minY, context.materialSpace.minY());
            scanMaxY = Math.min(this.maxY, context.materialSpace.maxY());
        }
        int numberOfPossiblePositions = Math.max(0, scanMaxY - scanMinY);
        ArrayList<Vector3i> validPositions = new ArrayList<Vector3i>(numberOfPossiblePositions);
        Vector3i patternPosition = context.position.clone();
        Pattern.Context patternContext = new Pattern.Context(patternPosition, context.materialSpace, context.workerId);
        for (int y = scanMinY; y < scanMaxY; ++y) {
            patternPosition.y = y;
            if (!context.pattern.matches(patternContext)) continue;
            Vector3i position = context.position.clone();
            position.setY(y);
            validPositions.add(position);
        }
        if (validPositions.isEmpty()) {
            return validPositions;
        }
        if (validPositions.size() <= this.resultsCap) {
            return validPositions;
        }
        ArrayList<Integer> usedIndices = new ArrayList<Integer>(this.resultsCap);
        ArrayList<Vector3i> outPositions = new ArrayList<Vector3i>(this.resultsCap);
        FastRandom random = new FastRandom(this.seedGenerator.seedAt(context.position.x, context.position.y, context.position.z));
        for (int i = 0; i < this.resultsCap; ++i) {
            int pickedIndex = random.nextInt(validPositions.size());
            if (usedIndices.contains(pickedIndex)) continue;
            usedIndices.add(pickedIndex);
            outPositions.add(validPositions.get(pickedIndex));
        }
        return outPositions;
    }

    @Nonnull
    private List<Vector3i> scanDartThrow(@Nonnull Scanner.Context context) {
        int scanMinY;
        if (this.resultsCap == 0) {
            return Collections.emptyList();
        }
        int scanMaxY = this.isRelativeToPosition ? Math.min(context.position.y + this.maxY, context.materialSpace.maxY()) : Math.min(this.maxY, context.materialSpace.maxY());
        int range = scanMaxY - (scanMinY = this.isRelativeToPosition ? Math.max(context.position.y + this.minY, context.materialSpace.minY()) : Math.max(this.minY, context.materialSpace.minY()));
        if (range == 0) {
            return Collections.emptyList();
        }
        boolean TRY_MULTIPLIER = true;
        int numberOfTries = range * 1;
        ArrayList<Vector3i> validPositions = new ArrayList<Vector3i>(this.resultsCap);
        FastRandom random = new FastRandom(this.seedGenerator.seedAt(context.position.x, context.position.y, context.position.z));
        ArrayList<Integer> usedYs = new ArrayList<Integer>(this.resultsCap);
        Vector3i patternPosition = context.position.clone();
        Pattern.Context patternContext = new Pattern.Context(patternPosition, context.materialSpace, context.workerId);
        for (int i = 0; i < numberOfTries; ++i) {
            patternPosition.y = random.nextInt(range) + scanMinY;
            if (!context.pattern.matches(patternContext) || usedYs.contains(patternPosition.y)) continue;
            usedYs.add(patternPosition.y);
            Vector3i position = patternPosition.clone();
            validPositions.add(position);
            if (validPositions.size() == this.resultsCap) break;
        }
        return validPositions;
    }

    @Override
    @Nonnull
    public SpaceSize scanSpace() {
        return this.scanSpaceSize.clone();
    }

    public static enum Strategy {
        DART_THROW,
        PICK_VALID;

    }
}

