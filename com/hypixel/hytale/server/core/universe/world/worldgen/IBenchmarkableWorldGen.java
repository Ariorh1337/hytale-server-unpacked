/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.worldgen;

import com.hypixel.hytale.server.core.universe.world.worldgen.IWorldGen;
import com.hypixel.hytale.server.core.universe.world.worldgen.IWorldGenBenchmark;

public interface IBenchmarkableWorldGen
extends IWorldGen {
    public IWorldGenBenchmark getBenchmark();
}

