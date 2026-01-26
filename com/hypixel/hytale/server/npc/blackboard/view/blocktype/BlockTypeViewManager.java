/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.blackboard.view.blocktype;

import com.hypixel.hytale.server.npc.blackboard.Blackboard;
import com.hypixel.hytale.server.npc.blackboard.view.BlockRegionViewManager;
import com.hypixel.hytale.server.npc.blackboard.view.blocktype.BlockPositionEntryGenerator;
import com.hypixel.hytale.server.npc.blackboard.view.blocktype.BlockTypeView;
import javax.annotation.Nonnull;

public class BlockTypeViewManager
extends BlockRegionViewManager<BlockTypeView> {
    private final BlockPositionEntryGenerator generator = new BlockPositionEntryGenerator();

    @Override
    @Nonnull
    protected BlockTypeView createView(long index, Blackboard blackboard) {
        return new BlockTypeView(index, blackboard, this.generator);
    }

    @Override
    protected boolean shouldCleanup(@Nonnull BlockTypeView view) {
        return view.getEntities().isEmpty();
    }
}

