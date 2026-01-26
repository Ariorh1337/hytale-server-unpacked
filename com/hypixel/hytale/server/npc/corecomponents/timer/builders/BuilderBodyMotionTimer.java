/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.timer.builders;

import com.hypixel.hytale.server.npc.asset.builder.BuilderObjectReferenceHelper;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.timer.BodyMotionTimer;
import com.hypixel.hytale.server.npc.corecomponents.timer.builders.BuilderMotionTimer;
import com.hypixel.hytale.server.npc.instructions.BodyMotion;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderBodyMotionTimer
extends BuilderMotionTimer<BodyMotion> {
    public BuilderBodyMotionTimer() {
        this.motion = new BuilderObjectReferenceHelper(BodyMotion.class, this);
    }

    @Override
    @Nullable
    public BodyMotionTimer build(@Nonnull BuilderSupport builderSupport) {
        BodyMotion motion = (BodyMotion)this.getMotion(builderSupport);
        return motion == null ? null : new BodyMotionTimer(this, builderSupport, motion);
    }

    @Override
    @Nonnull
    public final Class<BodyMotion> category() {
        return BodyMotion.class;
    }
}

