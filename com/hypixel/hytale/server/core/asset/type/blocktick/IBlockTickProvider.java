/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.blocktick;

import com.hypixel.hytale.server.core.asset.type.blocktick.config.TickProcedure;
import javax.annotation.Nullable;

@FunctionalInterface
public interface IBlockTickProvider {
    public static final IBlockTickProvider NONE = blockId -> null;

    @Nullable
    public TickProcedure getTickProcedure(int var1);
}

