package com.hypixel.hytale.server.npc.asset.builder;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public abstract class DependencyTrackingBuilder<T> extends BuilderBase<T> {
   private IntSet dynamicDependencies;

   @Override
   public boolean hasDynamicDependencies() {
      return this.dynamicDependencies != null;
   }

   @Override
   public void addDynamicDependency(int builderIndex) {
      if (this.dynamicDependencies == null) {
         this.dynamicDependencies = new IntOpenHashSet();
      }

      this.dynamicDependencies.add(builderIndex);
   }

   @Override
   public IntSet getDynamicDependencies() {
      return this.dynamicDependencies;
   }

   @Override
   public void clearDynamicDependencies() {
      if (this.dynamicDependencies != null) {
         this.dynamicDependencies.clear();
      }
   }
}
