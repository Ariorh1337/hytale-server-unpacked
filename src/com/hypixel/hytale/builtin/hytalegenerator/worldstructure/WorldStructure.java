package com.hypixel.hytale.builtin.hytalegenerator.worldstructure;

import com.hypixel.hytale.builtin.hytalegenerator.Registry;
import com.hypixel.hytale.builtin.hytalegenerator.biome.Biome;
import com.hypixel.hytale.builtin.hytalegenerator.framework.interfaces.functions.BiCarta;
import javax.annotation.Nonnull;

public class WorldStructure {
   private final BiCarta<Integer> biomeMap;
   private final Registry<Biome> biomeRegistry;
   private final int biomeTransitionDistance;
   private final int maxBiomeEdgeDistance;

   public WorldStructure(@Nonnull BiCarta<Integer> biomeMap, @Nonnull Registry<Biome> biomeRegistry, int biomeTransitionDistance, int maxBiomeEdgeDistance) {
      this.biomeMap = biomeMap;
      this.biomeRegistry = biomeRegistry;
      this.biomeTransitionDistance = biomeTransitionDistance;
      this.maxBiomeEdgeDistance = maxBiomeEdgeDistance;
   }

   @Nonnull
   public BiCarta<Integer> getBiomeMap() {
      return this.biomeMap;
   }

   @Nonnull
   public Registry<Biome> getBiomeRegistry() {
      return this.biomeRegistry;
   }

   public int getBiomeTransitionDistance() {
      return this.biomeTransitionDistance;
   }

   public int getMaxBiomeEdgeDistance() {
      return this.maxBiomeEdgeDistance;
   }
}
