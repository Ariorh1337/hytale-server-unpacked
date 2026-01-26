/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.zone;

import com.hypixel.hytale.math.vector.Vector2i;
import com.hypixel.hytale.server.worldgen.biome.BiomePatternGenerator;
import com.hypixel.hytale.server.worldgen.cave.CaveGenerator;
import com.hypixel.hytale.server.worldgen.container.UniquePrefabContainer;
import com.hypixel.hytale.server.worldgen.zone.ZoneDiscoveryConfig;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record Zone(int id, String name, @Nonnull ZoneDiscoveryConfig discoveryConfig, @Nullable CaveGenerator caveGenerator, @Nonnull BiomePatternGenerator biomePatternGenerator, @Nonnull UniquePrefabContainer uniquePrefabContainer) {
    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    @Nonnull
    public String toString() {
        return "Zone{id=" + this.id + ", name='" + this.name + "', discoveryConfig=" + String.valueOf(this.discoveryConfig) + ", caveGenerator=" + String.valueOf(this.caveGenerator) + ", biomePatternGenerator=" + String.valueOf(this.biomePatternGenerator) + ", uniquePrefabContainer=" + String.valueOf(this.uniquePrefabContainer) + "}";
    }

    public record UniqueCandidate(@Nonnull UniqueEntry zone, @Nonnull Vector2i[] positions) {
        public static final UniqueCandidate[] EMPTY_ARRAY = new UniqueCandidate[0];
    }

    public record UniqueEntry(@Nonnull Zone zone, int color, int[] parent, int radius, int padding) {
        @Nonnull
        public static final UniqueEntry[] EMPTY_ARRAY = new UniqueEntry[0];

        public boolean matchesParent(int color) {
            for (int p : this.parent) {
                if (p != color) continue;
                return true;
            }
            return false;
        }
    }

    public record Unique(@Nonnull Zone zone, @Nonnull CompletableFuture<Vector2i> position) {
        public Vector2i getPosition() {
            return this.position.join();
        }
    }
}

