/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.newsystem.views;

import com.hypixel.hytale.builtin.hytalegenerator.props.entity.EntityPlacementData;
import javax.annotation.Nonnull;

public interface EntityContainer {
    public void addEntity(@Nonnull EntityPlacementData var1);

    public boolean isInsideBuffer(int var1, int var2, int var3);
}

