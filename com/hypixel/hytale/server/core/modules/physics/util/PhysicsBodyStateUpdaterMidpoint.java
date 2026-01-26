/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.physics.util;

import com.hypixel.hytale.server.core.modules.physics.util.ForceProvider;
import com.hypixel.hytale.server.core.modules.physics.util.PhysicsBodyState;
import com.hypixel.hytale.server.core.modules.physics.util.PhysicsBodyStateUpdater;
import javax.annotation.Nonnull;

public class PhysicsBodyStateUpdaterMidpoint
extends PhysicsBodyStateUpdater {
    @Override
    public void update(@Nonnull PhysicsBodyState before, @Nonnull PhysicsBodyState after, double mass, double dt, boolean onGround, @Nonnull ForceProvider[] forceProvider) {
        double halfTime = 0.5 * dt;
        this.computeAcceleration(before, onGround, forceProvider, mass, halfTime);
        this.updateVelocity(before, after, halfTime);
        PhysicsBodyStateUpdaterMidpoint.updatePositionBeforeVelocity(before, after, halfTime);
        this.computeAcceleration(after, onGround, forceProvider, mass, dt);
        this.updateAndClampVelocity(before, after, dt);
        PhysicsBodyStateUpdaterMidpoint.updatePositionAfterVelocity(before, after, dt);
    }
}

