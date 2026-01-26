/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.climate;

import com.hypixel.hytale.math.vector.Vector2i;
import com.hypixel.hytale.server.worldgen.climate.ClimateGraph;
import com.hypixel.hytale.server.worldgen.climate.ClimateNoise;
import com.hypixel.hytale.server.worldgen.climate.ClimateSearch;
import com.hypixel.hytale.server.worldgen.util.LogUtil;
import com.hypixel.hytale.server.worldgen.zone.Zone;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UniqueClimateGenerator {
    public static final UniqueClimateGenerator EMPTY = new UniqueClimateGenerator(Entry.EMPTY_ARRAY, Unique.EMPTY_ARRAY);
    private static final int[] EMPTY_PARENTS = new int[0];
    private static final int MAX_PARENT_DEPTH = 10;
    private static final Vector2i DEFAULT_ORIGIN = new Vector2i(0, 0);
    private static final Vector2i[] EMPTY_POSITIONS = new Vector2i[0];
    protected final Entry[] entries;
    protected final Unique[] zones;

    public UniqueClimateGenerator(@Nonnull Entry[] entries) {
        this(entries, Unique.EMPTY_ARRAY);
    }

    public UniqueClimateGenerator(@Nonnull Entry[] entries, @Nonnull Unique[] zones) {
        this.entries = entries;
        this.zones = zones;
    }

    public Entry[] entries() {
        return this.entries;
    }

    public Unique[] zones() {
        return this.zones;
    }

    public int generate(int x, int y) {
        for (int i = 0; i < this.zones.length; ++i) {
            if (!this.zones[i].contains(x, y)) continue;
            return this.zones[i].color;
        }
        return -1;
    }

    public Zone.UniqueCandidate[] getCandidates(Map<String, Zone> zoneLookup) {
        if (this.entries.length == 0) {
            return Zone.UniqueCandidate.EMPTY_ARRAY;
        }
        Zone.UniqueCandidate[] candidates = new Zone.UniqueCandidate[this.entries.length];
        for (int i = 0; i < this.entries.length; ++i) {
            Entry entry = this.entries[i];
            Zone zone = zoneLookup.get(entry.zone);
            if (zone == null) {
                throw new Error("Could not find zone: " + entry.zone);
            }
            candidates[i] = new Zone.UniqueCandidate(new Zone.UniqueEntry(zone, entry.color, EMPTY_PARENTS, entry.radius, 0), EMPTY_POSITIONS);
        }
        return candidates;
    }

    public UniqueClimateGenerator apply(int seed, @Nonnull Zone.UniqueCandidate[] candidates, @Nonnull ClimateNoise noise, @Nonnull ClimateGraph graph, @Nonnull List<Zone.Unique> collector) {
        if (candidates.length != this.entries.length) {
            LogUtil.getLogger().at(Level.WARNING).log("Mismatched unique climate generator candidates: expected %d, got %d", this.entries.length, candidates.length);
            return this;
        }
        Unique[] unique = new Unique[candidates.length];
        Object2ObjectOpenHashMap<String, Unique> lookup = new Object2ObjectOpenHashMap<String, Unique>();
        for (int it = 0; it < 10 && lookup.size() < unique.length; ++it) {
            for (int i = 0; i < this.entries.length; ++i) {
                if (unique[i] != null) continue;
                Entry entry = this.entries[i];
                Unique parent = (Unique)lookup.get(entry.parent);
                if (!entry.parent.isEmpty() && parent == null) continue;
                CompletableFuture<Vector2i> position = UniqueClimateGenerator.findZonePosition(seed, DEFAULT_ORIGIN, entry, parent, noise, graph);
                unique[i] = new Unique(entry.color, entry.radius, position);
                collector.add(new Zone.Unique(candidates[i].zone().zone(), position));
                lookup.put(entry.zone, unique[i]);
            }
        }
        return new UniqueClimateGenerator(this.entries, unique);
    }

    public UniqueClimateGenerator apply(int seed, @Nonnull ClimateNoise noise, @Nonnull ClimateGraph graph) {
        Unique[] unique = new Unique[this.entries.length];
        Object2ObjectOpenHashMap<String, Unique> lookup = new Object2ObjectOpenHashMap<String, Unique>();
        for (int it = 0; it < 10 && lookup.size() < unique.length; ++it) {
            for (int i = 0; i < this.entries.length; ++i) {
                if (unique[i] != null) continue;
                Entry entry = this.entries[i];
                Unique parent = (Unique)lookup.get(entry.parent);
                if (!entry.parent.isEmpty() && parent == null) continue;
                CompletableFuture<Vector2i> position = UniqueClimateGenerator.findZonePosition(seed, DEFAULT_ORIGIN, entry, parent, noise, graph);
                unique[i] = new Unique(entry.color, entry.radius, position);
                lookup.put(entry.zone, unique[i]);
            }
        }
        if (lookup.size() < unique.length) {
            LogUtil.getLogger().at(Level.WARNING).log("Could not resolve all unique climate zones, resolved %d out of %d", lookup.size(), unique.length);
            Entry[] newEntries = new Entry[lookup.size()];
            Unique[] newUnique = new Unique[lookup.size()];
            int index = 0;
            for (int i = 0; i < unique.length; ++i) {
                if (unique[i] == null) continue;
                newEntries[index] = this.entries[i];
                newUnique[index] = unique[i];
                ++index;
            }
            return new UniqueClimateGenerator(newEntries, newUnique);
        }
        return new UniqueClimateGenerator(this.entries, unique);
    }

    protected static CompletableFuture<Vector2i> findZonePosition(int seed, Vector2i origin, @Nonnull Entry entry, @Nullable Unique parent, @Nonnull ClimateNoise noise, @Nonnull ClimateGraph graph) {
        if (parent != null) {
            return parent.position.thenCompose(pos -> UniqueClimateGenerator.findZonePosition(seed, pos, entry, null, noise, graph));
        }
        return ClimateSearch.search(seed, origin.x + entry.origin.x, origin.y + entry.origin.y, entry.minDistance, entry.maxDistance, entry.rule, noise, graph).thenApply(result -> {
            LogUtil.getLogger().at(Level.INFO).log("Found location for unique zone '%s' -> %s", (Object)entry.zone, (Object)result.pretty());
            return result.position();
        });
    }

    public record Unique(int color, int radius, int radius2, @Nonnull CompletableFuture<Vector2i> position) {
        public static final Unique[] EMPTY_ARRAY = new Unique[0];

        public Unique(int color, int radius, @Nonnull CompletableFuture<Vector2i> position) {
            this(color, radius, radius * radius, position);
        }

        public boolean contains(int x, int y) {
            Vector2i pos = this.position.join();
            int dx = x - pos.x;
            int dy = y - pos.y;
            return dx >= -this.radius && dy >= -this.radius && dx <= this.radius && dy <= this.radius && dx * dx + dy * dy <= this.radius2;
        }
    }

    public record Entry(@Nonnull String zone, @Nonnull String parent, int color, int radius, @Nonnull Vector2i origin, int minDistance, int maxDistance, @Nonnull ClimateSearch.Rule rule) {
        public static final Entry[] EMPTY_ARRAY = new Entry[0];
        public static final String DEFAULT_PARENT = "";
    }
}

