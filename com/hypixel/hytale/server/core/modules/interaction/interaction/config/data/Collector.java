/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.data;

import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.CollectorTag;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Collector {
    public void start();

    public void into(@Nonnull InteractionContext var1, @Nullable Interaction var2);

    public boolean collect(@Nonnull CollectorTag var1, @Nonnull InteractionContext var2, @Nonnull Interaction var3);

    public void outof();

    public void finished();
}

