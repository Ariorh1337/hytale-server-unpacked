package com.hypixel.hytale.server.worldgen.zone;

import com.hypixel.fastutil.ints.Int2ObjectConcurrentHashMap;

public class ZonePatternGeneratorCache {
   protected final Int2ObjectConcurrentHashMap.IntFunction<ZonePatternGenerator> compute;
   protected final Int2ObjectConcurrentHashMap<ZonePatternGenerator> cache = new Int2ObjectConcurrentHashMap<>();

   public ZonePatternGeneratorCache(ZonePatternProvider provider) {
      this.compute = provider::createGenerator;
   }

   public ZonePatternGenerator get(int seed) {
      try {
         return this.cache.computeIfAbsent(seed, this.compute);
      } catch (Exception e) {
         throw new Error("Failed to receive UniquePrefabEntry for " + seed, e);
      }
   }
}
