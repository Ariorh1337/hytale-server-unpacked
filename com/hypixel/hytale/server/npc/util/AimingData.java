/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.util;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.TrigMathUtil;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.physics.util.PhysicsMath;
import com.hypixel.hytale.server.core.modules.projectile.config.BallisticData;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.sensorinfo.ExtraInfoProvider;
import com.hypixel.hytale.server.npc.util.AimingHelper;
import com.hypixel.hytale.server.npc.util.NPCPhysicsMath;
import com.hypixel.hytale.server.npc.util.RootSolver;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AimingData
implements ExtraInfoProvider {
    public static final double MIN_MOVE_SPEED_STATIC = 0.01;
    public static final double MIN_MOVE_SPEED_STATIC_2 = 1.0E-4;
    public static final double MIN_AIMING_DISTANCE = 0.01;
    public static final double MIN_AIMING_DISTANCE_2 = 1.0E-4;
    public static final double MIN_AIR_TIME = 0.01;
    public static final double ANGLE_EPSILON = 0.1;
    @Nullable
    private BallisticData ballisticData;
    private boolean useFlatTrajectory = true;
    private double depthOffset;
    private boolean pitchAdjustOffset;
    private boolean haveSolution;
    private boolean haveOrientation;
    private boolean haveAttacked;
    private double chargeDistance;
    private double desiredHitAngle;
    private final float[] pitch = new float[2];
    private final float[] yaw = new float[2];
    @Nullable
    private Ref<EntityStore> target;
    private int owner = Integer.MIN_VALUE;

    public boolean isHaveAttacked() {
        return this.haveAttacked;
    }

    public void setHaveAttacked(boolean haveAttacked) {
        this.haveAttacked = haveAttacked;
    }

    public void requireBallistic(@Nonnull BallisticData ballisticData) {
        this.ballisticData = ballisticData;
        this.haveSolution = false;
        this.haveOrientation = false;
    }

    public void requireCloseCombat() {
        this.ballisticData = null;
        this.haveSolution = false;
        this.haveOrientation = false;
    }

    public float getPitch() {
        return this.getPitch(this.useFlatTrajectory);
    }

    public float getPitch(boolean flatTrajectory) {
        return this.pitch[flatTrajectory ? 0 : 1];
    }

    public float getYaw() {
        return this.getYaw(this.useFlatTrajectory);
    }

    public float getYaw(boolean flatTrajectory) {
        return this.yaw[flatTrajectory ? 0 : 1];
    }

    public boolean isBallistic() {
        return this.ballisticData != null;
    }

    @Nullable
    public BallisticData getBallisticData() {
        return this.ballisticData;
    }

    public void setUseFlatTrajectory(boolean useFlatTrajectory) {
        this.useFlatTrajectory = useFlatTrajectory;
    }

    public void setChargeDistance(double chargeDistance) {
        this.chargeDistance = chargeDistance;
    }

    public double getChargeDistance() {
        return this.chargeDistance;
    }

    public void setDesiredHitAngle(double desiredHitAngle) {
        this.desiredHitAngle = desiredHitAngle;
    }

    public double getDesiredHitAngle() {
        return this.desiredHitAngle;
    }

    @Nonnull
    public Class<AimingData> getType() {
        return AimingData.class;
    }

    public void setDepthOffset(double depthOffset, boolean pitchAdjustOffset) {
        this.depthOffset = depthOffset;
        this.pitchAdjustOffset = depthOffset != 0.0 && pitchAdjustOffset;
    }

    @Nullable
    public Ref<EntityStore> getTarget() {
        if (this.target == null) {
            return null;
        }
        if (!this.target.isValid() || this.target.getStore().getArchetype(this.target).contains(DeathComponent.getComponentType())) {
            this.target = null;
            return null;
        }
        return this.target;
    }

    public void setTarget(Ref<EntityStore> ref) {
        this.target = ref;
    }

    public boolean haveOrientation() {
        return this.haveOrientation || this.haveSolution;
    }

    public void setOrientation(float yaw, float pitch) {
        this.yaw[0] = this.yaw[1] = yaw;
        this.pitch[0] = this.pitch[1] = pitch;
        this.haveOrientation = true;
    }

    public void clearSolution() {
        this.haveOrientation = false;
        this.haveSolution = false;
        this.target = null;
    }

    public boolean computeSolution(double x, double y, double z, double vx, double vy, double vz) {
        double[] solutions;
        double c1;
        double muzzleVelocity;
        double c2;
        double c3;
        double v2;
        double xxzz = x * x + z * z;
        double d2 = xxzz + y * y;
        if (d2 < 0.01) {
            this.haveSolution = false;
            return false;
        }
        if (!this.isBallistic()) {
            this.yaw[0] = this.yaw[1] = PhysicsMath.normalizeTurnAngle(PhysicsMath.headingFromDirection(x, z));
            this.pitch[0] = this.pitch[1] = PhysicsMath.pitchFromDirection(x, y, z);
            this.haveSolution = true;
            return true;
        }
        if (!this.pitchAdjustOffset && xxzz > this.depthOffset * this.depthOffset) {
            double len = Math.sqrt(xxzz);
            double newLen = len - this.depthOffset;
            double scale = newLen / len;
            x *= scale;
            z *= scale;
            xxzz = newLen * newLen;
            d2 = xxzz + y * y;
        }
        if ((v2 = NPCPhysicsMath.dotProduct(vx, vy, vz)) < 1.0E-4) {
            this.haveSolution = this.computeStaticSolution(Math.sqrt(xxzz), y);
            if (this.haveSolution) {
                this.yaw[0] = this.yaw[1] = PhysicsMath.normalizeTurnAngle(PhysicsMath.headingFromDirection(x, z));
            }
            return this.haveSolution;
        }
        double gravity = this.ballisticData.getGravity();
        double c4 = gravity * gravity / 4.0;
        int numSolutions = RootSolver.solveQuartic(c4, c3 = vy * gravity, c2 = v2 + y * gravity - (muzzleVelocity = this.ballisticData.getMuzzleVelocity()) * muzzleVelocity, c1 = 2.0 * (x * vx + y * vy + z * vz), d2, solutions = new double[4]);
        if (numSolutions == 0) {
            this.haveSolution = false;
            return false;
        }
        int numResults = 0;
        double lastT = Double.MAX_VALUE;
        for (int i = 0; i < numSolutions; ++i) {
            double sine;
            double tz;
            double tx;
            double t = solutions[i];
            if (t <= 0.01 || (xxzz = (tx = x + t * vx) * tx + (tz = z + t * vz) * tz) < 0.01 || (sine = (y / t + 0.5 * gravity * t) / muzzleVelocity) < -1.0 || sine > 1.0) continue;
            float p = TrigMathUtil.asin(sine);
            float h = PhysicsMath.headingFromDirection(tx, tz);
            if (numResults >= 2) continue;
            if (numResults == 0 || t > lastT) {
                lastT = t;
                this.pitch[numResults] = p;
                this.yaw[numResults] = h;
            } else {
                this.pitch[numResults] = this.pitch[numResults - 1];
                this.yaw[numResults] = this.yaw[numResults - 1];
                this.pitch[numResults - 1] = p;
                this.yaw[numResults - 1] = h;
            }
            ++numResults;
        }
        if (numResults == 0) {
            this.haveSolution = false;
            return false;
        }
        if (numResults == 1) {
            this.pitch[1] = this.pitch[0];
            this.yaw[1] = this.yaw[0];
        }
        this.haveSolution = true;
        return true;
    }

    public boolean isOnTarget(float yaw, float pitch, double hitAngle) {
        if (!this.haveOrientation()) {
            return false;
        }
        double differenceYaw = NPCPhysicsMath.turnAngle(yaw, this.getYaw());
        if (!this.isBallistic()) {
            return -hitAngle <= differenceYaw && differenceYaw <= hitAngle;
        }
        double differencePitch = NPCPhysicsMath.turnAngle(pitch, this.getPitch());
        return differencePitch >= -0.1 && differencePitch <= 0.1 && differenceYaw >= -0.1 && differenceYaw <= 0.1;
    }

    public void tryClaim(int id) {
        if (this.owner != Integer.MIN_VALUE) {
            return;
        }
        this.owner = id;
        this.clear();
    }

    public boolean isClaimedBy(int id) {
        return this.owner == id;
    }

    public void release() {
        this.owner = Integer.MIN_VALUE;
    }

    public void clear() {
        this.clearSolution();
        this.ballisticData = null;
        this.useFlatTrajectory = true;
        this.depthOffset = 0.0;
        this.pitchAdjustOffset = false;
        this.haveAttacked = false;
    }

    protected boolean computeStaticSolution(double dx, double dy) {
        this.haveSolution = AimingHelper.computePitch(dx, dy, this.ballisticData.getMuzzleVelocity(), this.ballisticData.getGravity(), this.pitch);
        return this.haveSolution;
    }
}

