/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.navigation;

import com.hypixel.hytale.component.Resource;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.navigation.AStarNodePool;
import com.hypixel.hytale.server.npc.navigation.AStarNodePoolProvider;
import com.hypixel.hytale.server.npc.navigation.AStarNodePoolSimple;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import javax.annotation.Nonnull;

public class AStarNodePoolProviderSimple
implements AStarNodePoolProvider,
Resource<EntityStore> {
    @Nonnull
    protected Int2ObjectMap<AStarNodePoolSimple> nodePools = new Int2ObjectOpenHashMap<AStarNodePoolSimple>();

    public static ResourceType<EntityStore, AStarNodePoolProviderSimple> getResourceType() {
        return NPCPlugin.get().getAStarNodePoolProviderSimpleResourceType();
    }

    @Override
    @Nonnull
    public AStarNodePool getPool(int childNodeCount) {
        return this.nodePools.computeIfAbsent(childNodeCount, AStarNodePoolSimple::new);
    }

    @Override
    @Nonnull
    public Resource<EntityStore> clone() {
        return new AStarNodePoolProviderSimple();
    }
}

