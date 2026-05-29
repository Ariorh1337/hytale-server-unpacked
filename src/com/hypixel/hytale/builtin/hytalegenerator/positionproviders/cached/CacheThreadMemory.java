package com.hypixel.hytale.builtin.hytalegenerator.positionproviders.cached;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import org.joml.Vector3d;

public class CacheThreadMemory {
   Long2ObjectMap<Vector3d[]> sections;
   LongArrayList expirationList;
   int size;

   public CacheThreadMemory(int size) {
      if (size < 0) {
         throw new IllegalArgumentException();
      }

      this.sections = new Long2ObjectOpenHashMap<>(size);
      this.expirationList = new LongArrayList();
      this.size = size;
   }
}
