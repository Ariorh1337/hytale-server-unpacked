/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.utility;

import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.utility.MotionSequence;
import com.hypixel.hytale.server.npc.corecomponents.utility.builders.BuilderBodyMotionSequence;
import com.hypixel.hytale.server.npc.corecomponents.utility.builders.BuilderMotionSequence;
import com.hypixel.hytale.server.npc.instructions.BodyMotion;
import com.hypixel.hytale.server.npc.instructions.Motion;
import com.hypixel.hytale.server.npc.util.IAnnotatedComponentCollection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BodyMotionSequence
extends MotionSequence<BodyMotion>
implements BodyMotion,
IAnnotatedComponentCollection {
    public BodyMotionSequence(@Nonnull BuilderBodyMotionSequence builder, @Nonnull BuilderSupport support) {
        super((BuilderMotionSequence)builder, (Motion[])builder.getSteps(support));
    }

    @Override
    @Nullable
    public BodyMotion getSteeringMotion() {
        return this.activeMotion == null ? null : ((BodyMotion)this.activeMotion).getSteeringMotion();
    }
}

