/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.density.nodes;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.math.vector.Vector3d;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import java.util.ArrayList;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PositionsPinchDensity
extends Density {
    @Nullable
    private Density input;
    @Nullable
    private PositionProvider positions;
    private Double2DoubleFunction pinchCurve;
    private double maxDistance;
    private boolean distanceNormalized;

    public PositionsPinchDensity(@Nullable Density input, @Nullable PositionProvider positions, @Nonnull Double2DoubleFunction pinchCurve, double maxDistance, boolean distanceNormalized) {
        if (maxDistance < 0.0) {
            throw new IllegalArgumentException();
        }
        this.input = input;
        this.positions = positions;
        this.pinchCurve = pinchCurve;
        this.maxDistance = maxDistance;
        this.distanceNormalized = distanceNormalized;
    }

    @Override
    public double process(@Nonnull Density.Context context) {
        int i;
        if (this.input == null) {
            return 0.0;
        }
        if (this.positions == null) {
            return this.input.process(context);
        }
        Vector3d min = new Vector3d(context.position.x - this.maxDistance, context.position.y - this.maxDistance, context.position.z - this.maxDistance);
        Vector3d max = new Vector3d(context.position.x + this.maxDistance, context.position.y + this.maxDistance, context.position.z + this.maxDistance);
        Vector3d samplePoint = context.position.clone();
        ArrayList warpVectors = new ArrayList(10);
        ArrayList warpDistances = new ArrayList(10);
        Consumer<Vector3d> consumer = p -> {
            double radialDistance;
            double distance = p.distanceTo(samplePoint);
            if (distance > this.maxDistance) {
                return;
            }
            double normalizedDistance = distance / this.maxDistance;
            Vector3d warpVector = p.clone().addScaled(samplePoint, -1.0);
            if (this.distanceNormalized) {
                radialDistance = this.pinchCurve.applyAsDouble(normalizedDistance);
                radialDistance *= this.maxDistance;
            } else {
                radialDistance = this.pinchCurve.applyAsDouble(distance);
            }
            if (!(Math.abs(warpVector.length()) < 1.0E-9)) {
                warpVector.setLength(radialDistance);
            }
            warpVectors.add(warpVector);
            warpDistances.add(normalizedDistance);
        };
        PositionProvider.Context positionsContext = new PositionProvider.Context();
        positionsContext.minInclusive = min;
        positionsContext.maxExclusive = max;
        positionsContext.consumer = consumer;
        positionsContext.workerId = context.workerId;
        this.positions.positionsIn(positionsContext);
        if (warpVectors.isEmpty()) {
            return this.input.process(context);
        }
        if (warpVectors.size() == 1) {
            Vector3d warpVector = (Vector3d)warpVectors.getFirst();
            samplePoint.add(warpVector);
            Density.Context childContext = new Density.Context(context);
            context.position = samplePoint;
            return this.input.process(childContext);
        }
        int possiblePointsSize = warpVectors.size();
        ArrayList<Double> weights = new ArrayList<Double>(warpDistances.size());
        double totalWeight = 0.0;
        for (i = 0; i < possiblePointsSize; ++i) {
            double distance = (Double)warpDistances.get(i);
            double weight = 1.0 - distance;
            weights.add(weight);
            totalWeight += weight;
        }
        for (i = 0; i < possiblePointsSize; ++i) {
            double weight = (Double)weights.get(i) / totalWeight;
            Vector3d warpVector = (Vector3d)warpVectors.get(i);
            warpVector.scale(weight);
            samplePoint.add(warpVector);
        }
        return this.input.process(context);
    }

    @Override
    public void setInputs(@Nonnull Density[] inputs) {
        if (inputs.length == 0) {
            this.input = null;
        }
        this.input = inputs[0];
    }
}

