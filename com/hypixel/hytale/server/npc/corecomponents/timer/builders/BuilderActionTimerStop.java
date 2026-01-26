/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.timer.builders;

import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.timer.ActionTimer;
import com.hypixel.hytale.server.npc.corecomponents.timer.builders.BuilderActionTimer;
import com.hypixel.hytale.server.npc.util.Timer;
import javax.annotation.Nonnull;

public class BuilderActionTimerStop
extends BuilderActionTimer {
    @Override
    @Nonnull
    public ActionTimer build(@Nonnull BuilderSupport builderSupport) {
        return new ActionTimer(this, builderSupport);
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Stop a timer";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return "Stop a timer";
    }

    @Override
    @Nonnull
    public Timer.TimerAction getTimerAction() {
        return Timer.TimerAction.STOP;
    }
}

