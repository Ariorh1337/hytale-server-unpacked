/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.blocktick;

import com.hypixel.hytale.server.core.asset.type.blocktick.IBlockTickProvider;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;

@Deprecated(forRemoval=true)
public final class BlockTickManager {
    private static final AtomicReference<IBlockTickProvider> BLOCK_TICK_PROVIDER = new AtomicReference<IBlockTickProvider>(IBlockTickProvider.NONE);

    private BlockTickManager() {
    }

    public static void setBlockTickProvider(@Nonnull IBlockTickProvider provider) {
        BLOCK_TICK_PROVIDER.set(provider);
    }

    @Nonnull
    public static IBlockTickProvider getBlockTickProvider() {
        return BLOCK_TICK_PROVIDER.get();
    }

    public static boolean hasBlockTickProvider() {
        return BlockTickManager.getBlockTickProvider() != IBlockTickProvider.NONE;
    }
}

