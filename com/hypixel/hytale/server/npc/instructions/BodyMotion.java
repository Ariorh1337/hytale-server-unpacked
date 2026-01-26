/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.instructions;

import com.hypixel.hytale.server.npc.instructions.Motion;
import javax.annotation.Nullable;

public interface BodyMotion
extends Motion {
    @Nullable
    default public BodyMotion getSteeringMotion() {
        return this;
    }
}

