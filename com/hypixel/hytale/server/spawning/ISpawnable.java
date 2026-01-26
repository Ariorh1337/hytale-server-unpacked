/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.spawning;

import com.hypixel.hytale.server.spawning.SpawnTestResult;
import com.hypixel.hytale.server.spawning.SpawningContext;
import javax.annotation.Nonnull;

public interface ISpawnable {
    @Nonnull
    public String getIdentifier();

    @Nonnull
    public SpawnTestResult canSpawn(@Nonnull SpawningContext var1);
}

