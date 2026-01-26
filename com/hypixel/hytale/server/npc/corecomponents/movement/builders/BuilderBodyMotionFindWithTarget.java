/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.movement.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.Feature;
import com.hypixel.hytale.server.npc.asset.builder.holder.BooleanHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.DoubleHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.FloatHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleRangeValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleSingleValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleValidator;
import com.hypixel.hytale.server.npc.corecomponents.movement.builders.BuilderBodyMotionFindBase;
import javax.annotation.Nonnull;

public abstract class BuilderBodyMotionFindWithTarget
extends BuilderBodyMotionFindBase {
    protected final DoubleHolder minMoveDistanceWait = new DoubleHolder();
    protected final DoubleHolder minMoveDistanceRecompute = new DoubleHolder();
    protected final FloatHolder recomputeConeAngle = new FloatHolder();
    protected final DoubleHolder minMoveDistanceReproject = new DoubleHolder();
    protected final BooleanHolder adjustRangeByHitboxSize = new BooleanHolder();

    public BuilderBodyMotionFindWithTarget() {
    }

    public BuilderBodyMotionFindWithTarget(boolean enableSteering) {
        super(enableSteering);
    }

    @Override
    @Nonnull
    public BuilderBodyMotionFindBase readConfig(@Nonnull JsonElement data) {
        super.readConfig(data);
        this.getDouble(data, "WaitDistance", this.minMoveDistanceWait, 1.0, (DoubleValidator)DoubleSingleValidator.greater0(), BuilderDescriptorState.Experimental, "Minimum distance target needs to move before recomputing path when no path can be found", null);
        this.getDouble(data, "RecomputeDistance", this.minMoveDistanceRecompute, 10.0, (DoubleValidator)DoubleSingleValidator.greaterEqual0(), BuilderDescriptorState.Experimental, "Maximum distance target can move before path is recomputed or 0 to supress recomputation", null);
        this.getDouble(data, "ReprojectDistance", this.minMoveDistanceReproject, 0.5, (DoubleValidator)DoubleSingleValidator.greaterEqual0(), BuilderDescriptorState.Experimental, "Maximum distance target can move before position is reprojected", null);
        this.getBoolean(data, "AdjustRangeByHitboxSize", this.adjustRangeByHitboxSize, false, BuilderDescriptorState.Stable, "Correct range by hitbox sizes of involved entities", null);
        this.getFloat(data, "RecomputeConeAngle", this.recomputeConeAngle, 0.0, (DoubleValidator)DoubleRangeValidator.between(0.0, 360.0), BuilderDescriptorState.Experimental, "Recompute path when target leaves cone from initial position to target", null);
        this.requireFeature(Feature.AnyPosition);
        return this;
    }

    public double getMinMoveDistanceWait(@Nonnull BuilderSupport support) {
        return this.minMoveDistanceWait.get(support.getExecutionContext());
    }

    public double getMinMoveDistanceRecompute(@Nonnull BuilderSupport support) {
        return this.minMoveDistanceRecompute.get(support.getExecutionContext());
    }

    public double getRecomputeConeAngle(@Nonnull BuilderSupport support) {
        return this.recomputeConeAngle.get(support.getExecutionContext()) * ((float)Math.PI / 180);
    }

    public boolean isAdjustRangeByHitboxSize(@Nonnull BuilderSupport support) {
        return this.adjustRangeByHitboxSize.get(support.getExecutionContext());
    }

    public double getMinMoveDistanceReproject(@Nonnull BuilderSupport support) {
        return this.minMoveDistanceReproject.get(support.getExecutionContext());
    }
}

