/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.navigation;

import com.hypixel.hytale.server.npc.navigation.AStarNode;

public interface AStarNodePool {
    public AStarNode allocate();

    public void deallocate(AStarNode var1);
}

