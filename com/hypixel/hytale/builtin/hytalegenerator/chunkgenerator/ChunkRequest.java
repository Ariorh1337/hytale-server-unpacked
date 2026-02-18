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
        private int worldCounter;

        public GeneratorProfile(@Nonnull String worldStructureName, int seed, int worldCounter) {
            this.worldStructureName = worldStructureName;
            this.seed = seed;
            this.worldCounter = worldCounter;
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

        public boolean equals(Object o) {
            if (!(o instanceof GeneratorProfile)) {
                return false;
            }
            GeneratorProfile that = (GeneratorProfile)o;
            return this.seed == that.seed && this.worldCounter == that.worldCounter && Objects.equals(this.worldStructureName, that.worldStructureName);
        }

        public int hashCode() {
            return Objects.hash(this.worldStructureName, this.seed, this.worldCounter);
        }

        public GeneratorProfile clone() {
            return new GeneratorProfile(this.worldStructureName, this.seed, this.worldCounter);
        }

        @Nonnull
        public String toString() {
            return "GeneratorProfile{worldStructureName='" + this.worldStructureName + "', seed=" + this.seed + ", worldCounter=" + this.worldCounter + "}";
        }
    }

    public record Arguments(int seed, long index, int x, int z, @Nullable LongPredicate stillNeeded) {
    }
}

