/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.entity.entities.player.windows;

import com.hypixel.hytale.server.core.entity.entities.player.windows.MaterialExtraResourcesSection;
import javax.annotation.Nonnull;

public interface MaterialContainerWindow {
    @Nonnull
    public MaterialExtraResourcesSection getExtraResourcesSection();

    public void invalidateExtraResources();

    public boolean isValid();
}

