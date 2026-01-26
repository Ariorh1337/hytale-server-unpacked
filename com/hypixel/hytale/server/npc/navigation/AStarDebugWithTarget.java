/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.navigation;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.npc.movement.controllers.MotionController;
import com.hypixel.hytale.server.npc.navigation.AStarBase;
import com.hypixel.hytale.server.npc.navigation.AStarDebugBase;
import com.hypixel.hytale.server.npc.navigation.AStarWithTarget;
import javax.annotation.Nonnull;

public class AStarDebugWithTarget
extends AStarDebugBase {
    protected final AStarWithTarget aStarWithTarget;

    public AStarDebugWithTarget(AStarWithTarget aStarWithTarget, @Nonnull HytaleLogger logger) {
        super(aStarWithTarget, logger);
        this.aStarWithTarget = aStarWithTarget;
    }

    @Override
    protected int getDumpMapRegionZ(int def) {
        return AStarBase.zFromIndex(this.aStarWithTarget.getTargetPositionIndex());
    }

    @Override
    protected int getDumpMapRegionX(int def) {
        return AStarBase.xFromIndex(this.aStarWithTarget.getTargetPositionIndex());
    }

    @Override
    protected void drawMapFinish(@Nonnull StringBuilder[] map, int minX, int minZ) {
        super.drawMapFinish(map, minX, minZ);
        this.plot(this.aStarWithTarget.getTargetPositionIndex(), '\u03a9', map, minX, minZ);
    }

    @Override
    @Nonnull
    protected String getExtraLogString(MotionController controller) {
        return String.format("end=%s dist=%s", AStarBase.positionIndexToString(this.aStarWithTarget.getTargetPositionIndex()), Float.valueOf(this.aStarWithTarget.getEvaluator().estimateToGoal(this.aStarWithTarget, this.aStarWithTarget.getStartPosition(), controller)));
    }
}

