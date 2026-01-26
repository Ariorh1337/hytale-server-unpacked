/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder;

import com.hypixel.hytale.server.npc.asset.builder.BuilderModifier;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;

public class EmptyBuilderModifier
extends BuilderModifier {
    public static final EmptyBuilderModifier INSTANCE = new EmptyBuilderModifier();

    private EmptyBuilderModifier() {
        super(Object2ObjectMaps.EMPTY_MAP, null, null, null, null);
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public int exportedStateCount() {
        return 0;
    }

    @Override
    public void applyComponentStateMap(BuilderSupport support) {
        throw new UnsupportedOperationException("applyComponentStateMap is not valid for EmptyBuilderModifier");
    }

    @Override
    public void popComponentStateMap(BuilderSupport support) {
        throw new UnsupportedOperationException("popComponentStateMap is not valid for EmptyBuilderModifier");
    }
}

