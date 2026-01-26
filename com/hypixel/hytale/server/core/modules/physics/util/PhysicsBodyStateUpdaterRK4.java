/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.physics.util;

import com.hypixel.hytale.server.core.modules.physics.util.ForceProvider;
import com.hypixel.hytale.server.core.modules.physics.util.PhysicsBodyState;
import com.hypixel.hytale.server.core.modules.physics.util.PhysicsBodyStateUpdater;
import javax.annotation.Nonnull;

public class PhysicsBodyStateUpdaterRK4
extends PhysicsBodyStateUpdater {
    private final PhysicsBodyState state = new PhysicsBodyState();

    @Override
    public void update(@Nonnull PhysicsBodyState before, @Nonnull PhysicsBodyState after, double mass, double dt, boolean onGround, @Nonnull ForceProvider[] forceProvider) {
        double halfTime = dt * 0.5;
        this.computeAcceleration(before, onGround, forceProvider, mass, halfTime);
        this.assignAcceleration(after);
        this.updateVelocity(before, this.state, halfTime);
        PhysicsBodyStateUpdaterRK4.updatePositionBeforeVelocity(before, this.state, halfTime);
        this.computeAcceleration(this.state, onGround, forceProvider, mass, halfTime);
        this.addAcceleration(after, 2.0);
        this.updateVelocity(before, this.state, halfTime);
        PhysicsBodyStateUpdaterRK4.updatePositionAfterVelocity(before, this.state, halfTime);
        this.computeAcceleration(this.state, onGround, forceProvider, mass, halfTime);
        this.addAcceleration(after, 2.0);
        this.updateVelocity(before, this.state, dt);
        PhysicsBodyStateUpdaterRK4.updatePositionAfterVelocity(before, this.state, dt);
        this.computeAcceleration(this.state, onGround, forceProvider, mass, dt);
        this.addAcceleration(after);
        this.convertAccelerationToVelocity(before, after, dt / 6.0);
        PhysicsBodyStateUpdaterRK4.updatePositionAfterVelocity(before, after, dt);
    }
}

