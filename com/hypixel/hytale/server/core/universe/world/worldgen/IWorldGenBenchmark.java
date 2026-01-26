/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.worldgen;

import java.util.concurrent.CompletableFuture;

public interface IWorldGenBenchmark {
    public void start();

    public void stop();

    public CompletableFuture<String> buildReport();
}

