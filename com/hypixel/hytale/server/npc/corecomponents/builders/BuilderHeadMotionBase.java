/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.builders;

import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderMotionBase;
import com.hypixel.hytale.server.npc.instructions.HeadMotion;
import javax.annotation.Nonnull;

public abstract class BuilderHeadMotionBase
extends BuilderMotionBase<HeadMotion> {
    @Override
    @Nonnull
    public final Class<HeadMotion> category() {
        return HeadMotion.class;
    }
}

