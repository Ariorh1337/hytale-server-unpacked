/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.connectedblocks.builtin;

import com.hypixel.hytale.server.core.universe.world.connectedblocks.builtin.StairConnectedBlockRuleSet;
import javax.annotation.Nullable;

public interface StairLikeConnectedBlockRuleSet {
    public StairConnectedBlockRuleSet.StairType getStairType(int var1);

    @Nullable
    public String getMaterialName();
}

