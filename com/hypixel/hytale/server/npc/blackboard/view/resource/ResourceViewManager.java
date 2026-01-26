/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.blackboard.view.resource;

import com.hypixel.hytale.server.npc.blackboard.Blackboard;
import com.hypixel.hytale.server.npc.blackboard.view.BlockRegionViewManager;
import com.hypixel.hytale.server.npc.blackboard.view.resource.ResourceView;
import javax.annotation.Nonnull;

public class ResourceViewManager
extends BlockRegionViewManager<ResourceView> {
    @Override
    @Nonnull
    protected ResourceView createView(long index, Blackboard blackboard) {
        return new ResourceView(index);
    }

    @Override
    protected boolean shouldCleanup(@Nonnull ResourceView view) {
        return view.getReservationsByEntity().isEmpty();
    }
}

