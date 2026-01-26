/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component.system.tick;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.ISystem;
import javax.annotation.Nonnull;

public interface TickableSystem<ECS_TYPE>
extends ISystem<ECS_TYPE> {
    public void tick(float var1, int var2, @Nonnull Store<ECS_TYPE> var3);
}

