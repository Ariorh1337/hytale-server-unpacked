/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.patterns;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.SpaceSize;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.annotation.Nonnull;

public class GapPattern
extends Pattern {
    private List<List<PositionedPattern>> axisPositionedPatterns;
    private List<PositionedPattern> depthPositionedPatterns;
    private double gapSize;
    private double anchorSize;
    private double anchorRoughness;
    private int depthDown;
    private int depthUp;
    private Pattern gapPattern;
    private Pattern anchorPattern;
    private SpaceSize readSpaceSize;

    public GapPattern(@Nonnull List<Float> angles, double gapSize, double anchorSize, double anchorRoughness, int depthDown, int depthUp, @Nonnull Pattern gapPattern, @Nonnull Pattern anchorPattern) {
        if (gapSize < 0.0 || anchorSize < 0.0 || anchorRoughness < 0.0 || depthDown < 0 || depthUp < 0) {
            throw new IllegalArgumentException("negative sizes");
        }
        this.gapSize = gapSize;
        this.anchorSize = anchorSize;
        this.gapPattern = gapPattern;
        this.anchorPattern = anchorPattern;
        this.anchorRoughness = anchorRoughness;
        this.depthDown = depthDown;
        this.depthUp = depthUp;
        this.depthPositionedPatterns = this.renderDepths();
        this.axisPositionedPatterns = new ArrayList<List<PositionedPattern>>(angles.size());
        for (float angle : angles) {
            List<PositionedPattern> positions = this.renderPositions(angle);
            this.axisPositionedPatterns.add(positions);
        }
        Vector3i min = null;
        Vector3i max = null;
        for (List<PositionedPattern> direction : this.axisPositionedPatterns) {
            for (PositionedPattern pos : direction) {
                if (min == null) {
                    min = pos.position.clone();
                    max = pos.position.clone();
                    continue;
                }
                min = Vector3i.min(min, pos.position);
                max = Vector3i.max(max, pos.position);
            }
        }
        if (max == null) {
            this.readSpaceSize = new SpaceSize(new Vector3i(), new Vector3i());
            return;
        }
        max.add(1, 1, 1);
        this.readSpaceSize = new SpaceSize(min, max);
    }

    @Nonnull
    private List<PositionedPattern> renderDepths() {
        ArrayList<PositionedPattern> positions = new ArrayList<PositionedPattern>();
        Vector3i pointer = new Vector3i();
        int stepsDown = this.depthDown - 1;
        for (int i = 0; i < this.depthDown; ++i) {
            pointer.add(0, -1, 0);
            positions.add(new PositionedPattern(this.gapPattern, pointer.clone()));
        }
        pointer = new Vector3i();
        int stepsUp = this.depthUp - 1;
        for (int i = 0; i < this.depthUp; ++i) {
            pointer.add(0, 1, 0);
            positions.add(new PositionedPattern(this.gapPattern, pointer.clone()));
        }
        return positions;
    }

    @Nonnull
    private List<PositionedPattern> renderPositions(float angle) {
        ArrayList<PositionedPattern> positions = new ArrayList<PositionedPattern>();
        positions.addAll(this.renderHalfPositions(angle));
        positions.addAll(this.renderHalfPositions((float)Math.PI + angle));
        ArrayList<PositionedPattern> uniquePositions = new ArrayList<PositionedPattern>(positions.size());
        HashSet<Vector3i> positionsSet = new HashSet<Vector3i>();
        for (PositionedPattern e : positions) {
            if (positionsSet.contains(e.position)) continue;
            uniquePositions.add(e);
            positionsSet.add(e.position);
        }
        return uniquePositions;
    }

    @Nonnull
    private List<PositionedPattern> renderHalfPositions(float angle) {
        ArrayList<PositionedPattern> positions = new ArrayList<PositionedPattern>();
        double halfGap = this.gapSize / 2.0 - 1.0 - this.anchorRoughness;
        halfGap = Math.max(0.0, halfGap);
        double halfWall = this.anchorSize / 2.0;
        Vector3d pointer = new Vector3d(0.5, 0.5, 0.5);
        Vector3d mov = new Vector3d(0.0, 0.0, -1.0);
        mov.rotateY(angle);
        double stepSize = 0.5;
        mov.setLength(stepSize);
        int steps = (int)(halfGap / stepSize);
        for (int s = 0; s < steps; ++s) {
            pointer.add(mov);
            positions.add(new PositionedPattern(this.gapPattern, pointer.toVector3i()));
        }
        positions.add(new PositionedPattern(this.gapPattern, new Vector3i()));
        pointer = mov.clone().setLength(halfGap).add(0.5, 0.5, 0.5);
        positions.add(new PositionedPattern(this.gapPattern, pointer.toVector3i()));
        Vector3d anchor = mov.clone().setLength(this.gapSize / 2.0);
        pointer = anchor.clone().add(0.5, 0.5, 0.5);
        positions.add(new PositionedPattern(this.anchorPattern, anchor.toVector3i()));
        mov.rotateY(1.5707964f);
        steps = (int)(halfWall / stepSize);
        for (int s = 0; s < steps; ++s) {
            pointer.add(mov);
            positions.add(new PositionedPattern(this.anchorPattern, pointer.toVector3i()));
        }
        Vector3d wallTip = anchor.clone().add(0.5, 0.5, 0.5);
        wallTip.add(mov.clone().setLength(halfWall));
        positions.add(new PositionedPattern(this.anchorPattern, wallTip.toVector3i()));
        mov.scale(-1.0);
        pointer = anchor.clone().add(0.5, 0.5, 0.5);
        for (int s = 0; s < steps; ++s) {
            pointer.add(mov);
            positions.add(new PositionedPattern(this.anchorPattern, pointer.toVector3i()));
        }
        wallTip = anchor.clone().add(0.5, 0.5, 0.5);
        wallTip.add(mov.clone().setLength(halfWall));
        positions.add(new PositionedPattern(this.anchorPattern, wallTip.toVector3i()));
        return positions;
    }

    @Override
    public boolean matches(@Nonnull Pattern.Context context) {
        Vector3i childPosition = new Vector3i();
        Pattern.Context childContext = new Pattern.Context(context);
        childContext.position = childPosition;
        for (PositionedPattern positionedPattern : this.depthPositionedPatterns) {
            childPosition.assign(positionedPattern.position).add(context.position);
            if (positionedPattern.pattern.matches(childContext)) continue;
            return false;
        }
        for (List list : this.axisPositionedPatterns) {
            boolean matchesDirection = true;
            for (PositionedPattern entry : list) {
                childPosition.assign(entry.position).add(context.position);
                if (entry.pattern.matches(context)) continue;
                matchesDirection = false;
                break;
            }
            if (!matchesDirection) continue;
            return true;
        }
        return false;
    }

    @Override
    @Nonnull
    public SpaceSize readSpace() {
        return this.readSpaceSize.clone();
    }

    public static class PositionedPattern {
        private Vector3i position;
        private Pattern pattern;

        public PositionedPattern(@Nonnull Pattern pattern, @Nonnull Vector3i position) {
            this.pattern = pattern;
            this.position = position.clone();
        }

        public int getX() {
            return this.position.x;
        }

        public int getY() {
            return this.position.y;
        }

        public int getZ() {
            return this.position.z;
        }

        public Pattern getPattern() {
            return this.pattern;
        }

        @Nonnull
        protected PositionedPattern clone() {
            return new PositionedPattern(this.pattern, this.position.clone());
        }
    }
}

