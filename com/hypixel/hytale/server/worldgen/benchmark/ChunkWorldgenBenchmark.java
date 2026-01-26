/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.benchmark;

import com.hypixel.hytale.server.core.universe.world.worldgen.IWorldGenBenchmark;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;

public class ChunkWorldgenBenchmark
implements IWorldGenBenchmark {
    @Nonnull
    private final ConcurrentHashMap<String, AtomicInteger> prefabCounts = new ConcurrentHashMap();
    @Nonnull
    private final ConcurrentHashMap<String, AtomicInteger> caveNodeCounts = new ConcurrentHashMap();
    private boolean enabled = false;

    @Override
    public void start() {
        this.enabled = true;
    }

    @Override
    public void stop() {
        this.enabled = false;
        this.prefabCounts.clear();
        this.caveNodeCounts.clear();
    }

    @Override
    @Nonnull
    public CompletableFuture<String> buildReport() {
        TreeMap<String, Integer> map = new TreeMap<String, Integer>(String::compareToIgnoreCase);
        StringBuilder sb = new StringBuilder();
        sb.append("Generated prefab counts: \n");
        this.prefabCounts.forEach((key, value) -> map.put((String)key, value.get()));
        map.forEach((key, value) -> sb.append((String)key).append('\t').append(value).append('\n'));
        sb.append('\n');
        map.clear();
        sb.append("Generated cave node counts: \n");
        this.caveNodeCounts.forEach((key, value) -> map.put((String)key, value.get()));
        map.forEach((key, value) -> sb.append((String)key).append('\t').append(value).append('\n'));
        sb.append('\n');
        map.clear();
        return CompletableFuture.completedFuture(sb.toString());
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void registerPrefab(String name) {
        this.prefabCounts.computeIfAbsent(name, i -> new AtomicInteger(0));
        this.prefabCounts.get(name).incrementAndGet();
    }

    public void registerCaveNode(String name) {
        this.caveNodeCounts.computeIfAbsent(name, i -> new AtomicInteger(0));
        this.caveNodeCounts.get(name).incrementAndGet();
    }
}

