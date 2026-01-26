/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.chunkgenerator;

import java.util.Objects;
import java.util.function.LongPredicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record ChunkRequest(@Nonnull GeneratorProfile generatorProfile, @Nonnull Arguments arguments) {

    public static final class GeneratorProfile {
        @Nonnull
        private final String worldStructureName;
        private int seed;

        public GeneratorProfile(@Nonnull String worldStructureName, int seed) {
            this.worldStructureName = worldStructureName;
            this.seed = seed;
        }

        @Nonnull
        public String worldStructureName() {
            return this.worldStructureName;
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
            return Objects.equals(this.worldStructureName, that.worldStructureName) && this.seed == that.seed;
        }

        public int hashCode() {
            return Objects.hash(this.worldStructureName, this.seed);
        }

        public String toString() {
            return "GeneratorProfile[worldStructureName=" + this.worldStructureName + ", seed=" + this.seed + "]";
        }
    }

    public record Arguments(int seed, long index, int x, int z, @Nullable LongPredicate stillNeeded) {
    }
}

