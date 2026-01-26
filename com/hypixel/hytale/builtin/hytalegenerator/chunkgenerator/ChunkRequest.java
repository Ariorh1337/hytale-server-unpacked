/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.chunkgenerator;

import com.hypixel.hytale.math.vector.Transform;
import java.util.Objects;
import java.util.function.LongPredicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record ChunkRequest(@Nonnull GeneratorProfile generatorProfile, @Nonnull Arguments arguments) {

    public static final class GeneratorProfile {
        @Nonnull
        private final String worldStructureName;
        @Nonnull
        private final Transform spawnPosition;
        private int seed;

        public GeneratorProfile(@Nonnull String worldStructureName, @Nonnull Transform spawnPosition, int seed) {
            this.worldStructureName = worldStructureName;
            this.spawnPosition = spawnPosition;
            this.seed = seed;
        }

        @Nonnull
        public String worldStructureName() {
            return this.worldStructureName;
        }

        @Nonnull
        public Transform spawnPosition() {
            return this.spawnPosition;
        }

        public int seed() {
            return this.seed;
        }

        public void setSeed(int seed) {
            this.seed = seed;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            GeneratorProfile that = (GeneratorProfile)obj;
            return Objects.equals(this.worldStructureName, that.worldStructureName) && Objects.equals(this.spawnPosition, that.spawnPosition) && this.seed == that.seed;
        }

        public int hashCode() {
            return Objects.hash(this.worldStructureName, this.spawnPosition, this.seed);
        }

        public String toString() {
            return "GeneratorProfile[worldStructureName=" + this.worldStructureName + ", spawnPosition=" + String.valueOf(this.spawnPosition) + ", seed=" + this.seed + "]";
        }
    }

    public record Arguments(int seed, long index, int x, int z, @Nullable LongPredicate stillNeeded) {
    }
}

