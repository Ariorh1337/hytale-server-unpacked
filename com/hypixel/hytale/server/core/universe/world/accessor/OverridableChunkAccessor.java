/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.accessor;

import com.hypixel.hytale.server.core.universe.world.accessor.BlockAccessor;
import com.hypixel.hytale.server.core.universe.world.accessor.ChunkAccessor;

public interface OverridableChunkAccessor<X extends BlockAccessor>
extends ChunkAccessor<X> {
    public void overwrite(X var1);
}

