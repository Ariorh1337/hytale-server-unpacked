/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.instances.removal;

import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import javax.annotation.Nonnull;

public interface RemovalCondition {
    @Nonnull
    public static final CodecMapCodec<RemovalCondition> CODEC = new CodecMapCodec("Type");
    public static final RemovalCondition[] EMPTY = new RemovalCondition[0];

    public boolean shouldRemoveWorld(@Nonnull Store<ChunkStore> var1);
}

