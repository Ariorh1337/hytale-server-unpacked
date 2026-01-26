/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.utility.builders;

import com.hypixel.hytale.server.npc.asset.builder.BuilderObjectListHelper;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.utility.HeadMotionSequence;
import com.hypixel.hytale.server.npc.corecomponents.utility.builders.BuilderMotionSequence;
import com.hypixel.hytale.server.npc.instructions.HeadMotion;
import javax.annotation.Nonnull;

public class BuilderHeadMotionSequence
extends BuilderMotionSequence<HeadMotion> {
    public static final HeadMotion[] EMPTY_SEQUENCE = new HeadMotion[0];

    public BuilderHeadMotionSequence() {
        this.steps = new BuilderObjectListHelper(HeadMotion.class, this);
    }

    @Override
    @Nonnull
    public HeadMotionSequence build(@Nonnull BuilderSupport builderSupport) {
        return new HeadMotionSequence(this, builderSupport);
    }

    @Override
    @Nonnull
    public final Class<HeadMotion> category() {
        return HeadMotion.class;
    }

    @Nonnull
    public HeadMotion[] getSteps(@Nonnull BuilderSupport builderSupport) {
        Object motions = this.steps.build(builderSupport);
        return motions == null ? EMPTY_SEQUENCE : (HeadMotion[])motions.toArray(HeadMotion[]::new);
    }
}

