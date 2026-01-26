/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.movement.controllers;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.physics.component.PhysicsValues;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.modules.splitvelocity.VelocityConfig;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.movement.MovementState;
import com.hypixel.hytale.server.npc.movement.NavState;
import com.hypixel.hytale.server.npc.movement.Steering;
import com.hypixel.hytale.server.npc.movement.controllers.ProbeMoveData;
import com.hypixel.hytale.server.npc.role.Role;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface MotionController {
    public String getType();

    public Role getRole();

    public void setRole(Role var1);

    public void setInertia(double var1);

    public void setKnockbackScale(double var1);

    public double getGravity();

    public void setHeadPitchAngleRange(@Nullable float[] var1);

    public void spawned();

    public void activate();

    public void deactivate();

    public void updateModelParameters(@Nullable Ref<EntityStore> var1, Model var2, Box var3, @Nullable ComponentAccessor<EntityStore> var4);

    public double steer(@Nonnull Ref<EntityStore> var1, @Nonnull Role var2, @Nonnull Steering var3, @Nonnull Steering var4, double var5, @Nonnull ComponentAccessor<EntityStore> var7);

    public double probeMove(@Nonnull Ref<EntityStore> var1, Vector3d var2, Vector3d var3, ProbeMoveData var4, @Nonnull ComponentAccessor<EntityStore> var5);

    public double probeMove(@Nonnull Ref<EntityStore> var1, ProbeMoveData var2, @Nonnull ComponentAccessor<EntityStore> var3);

    public void constrainRotations(Role var1, TransformComponent var2);

    public double getCurrentMaxBodyRotationSpeed();

    public void updateMovementState(@Nonnull Ref<EntityStore> var1, @Nonnull MovementStates var2, @Nonnull Steering var3, @Nonnull Vector3d var4, @Nonnull ComponentAccessor<EntityStore> var5);

    public boolean isValidPosition(Vector3d var1, ComponentAccessor<EntityStore> var2);

    public boolean canAct(@Nonnull Ref<EntityStore> var1, @Nonnull ComponentAccessor<EntityStore> var2);

    public boolean isInProgress();

    public boolean isObstructed();

    public boolean inAir();

    public boolean inWater();

    public boolean onGround();

    public boolean standingOnBlockOfType(int var1);

    public double getMaximumSpeed();

    public double getCurrentSpeed();

    public boolean estimateVelocity(Steering var1, Vector3d var2);

    public double getCurrentTurnRadius();

    public double waypointDistance(Vector3d var1, Vector3d var2);

    public double waypointDistanceSquared(Vector3d var1, Vector3d var2);

    public double waypointDistance(@Nonnull Ref<EntityStore> var1, Vector3d var2, @Nonnull ComponentAccessor<EntityStore> var3);

    public double waypointDistanceSquared(@Nonnull Ref<EntityStore> var1, Vector3d var2, @Nonnull ComponentAccessor<EntityStore> var3);

    public float getMaxClimbAngle();

    public float getMaxSinkAngle();

    public boolean translateToAccessiblePosition(Vector3d var1, Box var2, double var3, double var5, ComponentAccessor<EntityStore> var7);

    public Vector3d getComponentSelector();

    public Vector3d getPlanarComponentSelector();

    public void setComponentSelector(Vector3d var1);

    public boolean is2D();

    public Vector3d getWorldNormal();

    public Vector3d getWorldAntiNormal();

    public void addForce(@Nonnull Vector3d var1, @Nullable VelocityConfig var2);

    public Vector3d getForce();

    public void forceVelocity(@Nonnull Vector3d var1, @Nullable VelocityConfig var2, boolean var3);

    public VerticalRange getDesiredVerticalRange(@Nonnull Ref<EntityStore> var1, @Nonnull ComponentAccessor<EntityStore> var2);

    public double getWanderVerticalMovementRatio();

    public void setAvoidingBlockDamage(boolean var1);

    public boolean isAvoidingBlockDamage();

    public boolean willReceiveBlockDamage();

    public void requirePreciseMovement(Vector3d var1);

    public void requireDepthProbing();

    public void enableHeadingBlending(double var1, Vector3d var3, double var4);

    public void enableHeadingBlending();

    public void setRelaxedMoveConstraints(boolean var1);

    public boolean isRelaxedMoveConstraints();

    public NavState getNavState();

    public double getThrottleDuration();

    public double getTargetDeltaSquared();

    public void setNavState(NavState var1, double var2, double var4);

    public void setForceRecomputePath(boolean var1);

    public boolean isForceRecomputePath();

    public boolean canRestAtPlace();

    public void beforeInstructionSensorsAndActions(double var1);

    public void beforeInstructionMotion(double var1);

    default public boolean matchesType(@Nonnull Class<? extends MotionController> clazz) {
        return clazz.isInstance(this);
    }

    public double getDesiredAltitudeWeight();

    public double getHeightOverGround();

    default public void clearOverrides() {
    }

    default public double getSquaredDistance(@Nonnull Vector3d p1, @Nonnull Vector3d p2, boolean useProjectedDistance) {
        return useProjectedDistance ? this.waypointDistanceSquared(p1, p2) : p1.distanceSquaredTo(p2);
    }

    public void updatePhysicsValues(PhysicsValues var1);

    public static boolean isInMovementState(@Nonnull Ref<EntityStore> ref, @Nonnull MovementState state, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        MovementStatesComponent movementStatesComponent = componentAccessor.getComponent(ref, MovementStatesComponent.getComponentType());
        if (!1.$assertionsDisabled && movementStatesComponent == null) {
            throw new AssertionError();
        }
        Velocity velocityComponent = componentAccessor.getComponent(ref, Velocity.getComponentType());
        if (!1.$assertionsDisabled && velocityComponent == null) {
            throw new AssertionError();
        }
        MovementStates states = movementStatesComponent.getMovementStates();
        return switch (state) {
            default -> throw new MatchException(null, null);
            case MovementState.CLIMBING -> states.climbing;
            case MovementState.FALLING -> states.falling;
            case MovementState.CROUCHING -> states.crouching;
            case MovementState.FLYING -> states.flying;
            case MovementState.JUMPING -> states.jumping;
            case MovementState.SPRINTING -> states.sprinting;
            case MovementState.RUNNING -> states.running;
            case MovementState.IDLE -> velocityComponent.getVelocity().closeToZero(0.001);
            case MovementState.WALKING -> {
                if (!(velocityComponent.getVelocity().closeToZero(0.001) || states.falling || states.climbing || states.flying || states.running || states.sprinting || states.jumping || states.crouching)) {
                    yield true;
                }
                yield false;
            }
            case MovementState.ANY -> true;
        };
    }

    static {
        if (1.$assertionsDisabled) {
            // empty if block
        }
    }

    public static class VerticalRange {
        public double current;
        public double min;
        public double max;

        public void assign(double current, double min, double max) {
            this.current = current;
            this.min = min;
            this.max = max;
        }

        public boolean isWithinRange() {
            return this.current >= this.min && this.current <= this.max;
        }
    }
}

