/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.builders;

import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderMotionBase;
import com.hypixel.hytale.server.npc.instructions.BodyMotion;
import javax.annotation.Nonnull;

public abstract class BuilderBodyMotionBase
extends BuilderMotionBase<BodyMotion> {
    @Override
    @Nonnull
    public final Class<BodyMotion> category() {
        return BodyMotion.class;
    }
}

