/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component.system;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.System;
import javax.annotation.Nonnull;

public abstract class StoreSystem<ECS_TYPE>
extends System<ECS_TYPE> {
    public abstract void onSystemAddedToStore(@Nonnull Store<ECS_TYPE> var1);

    public abstract void onSystemRemovedFromStore(@Nonnull Store<ECS_TYPE> var1);
}

