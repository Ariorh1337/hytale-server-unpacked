package com.hypixel.hytale.builtin.hytalegenerator.chunkgenerator;

import java.util.Objects;
import java.util.function.LongPredicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record ChunkRequest(@Nonnull ChunkRequest.GeneratorProfile generatorProfile, @Nonnull ChunkRequest.Arguments arguments) {
   public record Arguments(int seed, long index, int x, int z, @Nullable LongPredicate stillNeeded) {
   }

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

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (obj != null && obj.getClass() == this.getClass()) {
            ChunkRequest.GeneratorProfile that = (ChunkRequest.GeneratorProfile)obj;
            return Objects.equals(this.worldStructureName, that.worldStructureName) && this.seed == that.seed;
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.worldStructureName, this.seed);
      }

      @Override
      public String toString() {
         return "GeneratorProfile[worldStructureName=" + this.worldStructureName + ", seed=" + this.seed + "]";
      }
   }
}
