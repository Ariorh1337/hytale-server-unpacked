package com.hypixel.hytale.builtin.adventure.wilderness.resource;

import com.hypixel.hytale.builtin.adventure.wilderness.WildernessPlugin;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Resource;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.function.consumer.IntBiObjectConsumer;
import com.hypixel.hytale.function.consumer.IntTriObjectConsumer;
import com.hypixel.hytale.function.consumer.TriConsumer;
import com.hypixel.hytale.function.predicate.TriPredicate;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class WildernessVisitor<ECS_TYPE>
   implements AutoCloseable,
   BiConsumer<ArchetypeChunk<ECS_TYPE>, CommandBuffer<ECS_TYPE>>,
   BiPredicate<ArchetypeChunk<ECS_TYPE>, CommandBuffer<ECS_TYPE>>,
   IntBiObjectConsumer<ArchetypeChunk<ECS_TYPE>, CommandBuffer<ECS_TYPE>>,
   Resource<ECS_TYPE> {
   @Nullable
   protected WildernessTracker tracker;
   @Nullable
   protected TriConsumer<ArchetypeChunk<ECS_TYPE>, CommandBuffer<ECS_TYPE>, WildernessTracker> tableConsumer;
   @Nullable
   protected TriPredicate<ArchetypeChunk<ECS_TYPE>, CommandBuffer<ECS_TYPE>, WildernessTracker> tablePredicate;
   @Nullable
   protected IntTriObjectConsumer<ArchetypeChunk<ECS_TYPE>, CommandBuffer<ECS_TYPE>, WildernessTracker> entityConsumer;

   public WildernessVisitor<ECS_TYPE> setup(
      @Nonnull WildernessTracker tracker, @Nonnull TriPredicate<ArchetypeChunk<ECS_TYPE>, CommandBuffer<ECS_TYPE>, WildernessTracker> predicate
   ) {
      this.tracker = tracker;
      this.tablePredicate = predicate;
      return this;
   }

   public WildernessVisitor<ECS_TYPE> setup(
      @Nonnull WildernessTracker tracker, @Nonnull TriConsumer<ArchetypeChunk<ECS_TYPE>, CommandBuffer<ECS_TYPE>, WildernessTracker> consumer
   ) {
      this.tracker = tracker;
      this.tableConsumer = consumer;
      return this;
   }

   public WildernessVisitor<ECS_TYPE> setup(
      @Nonnull WildernessTracker tracker, @Nonnull IntTriObjectConsumer<ArchetypeChunk<ECS_TYPE>, CommandBuffer<ECS_TYPE>, WildernessTracker> consumer
   ) {
      this.tracker = tracker;
      this.entityConsumer = consumer;
      return this;
   }

   public boolean test(ArchetypeChunk<ECS_TYPE> table, CommandBuffer<ECS_TYPE> buffer) {
      assert this.tracker != null;
      assert this.tablePredicate != null;
      return this.tablePredicate.test(table, buffer, this.tracker);
   }

   public void accept(ArchetypeChunk<ECS_TYPE> table, CommandBuffer<ECS_TYPE> buffer) {
      assert this.tracker != null;
      assert this.tableConsumer != null;
      this.tableConsumer.accept(table, buffer, this.tracker);
   }

   public void accept(int i, ArchetypeChunk<ECS_TYPE> table, CommandBuffer<ECS_TYPE> buffer) {
      assert this.tracker != null;
      assert this.entityConsumer != null;
      this.entityConsumer.accept(i, table, buffer, this.tracker);
   }

   public BiConsumer<ArchetypeChunk<ECS_TYPE>, CommandBuffer<ECS_TYPE>> asConsumer() {
      return this;
   }

   public BiPredicate<ArchetypeChunk<ECS_TYPE>, CommandBuffer<ECS_TYPE>> asPredicate() {
      return this;
   }

   @Override
   public void close() {
      this.tracker = null;
      this.tableConsumer = null;
      this.tablePredicate = null;
      this.entityConsumer = null;
   }

   @Nonnull
   public abstract WildernessVisitor<ECS_TYPE> clone();

   public static ResourceType<ChunkStore, WildernessVisitor.ChunkVisitor> getChunkResourceType() {
      return WildernessPlugin.get().getChunkResourceType();
   }

   public static ResourceType<EntityStore, WildernessVisitor.EntityVisitor> getEntityResourceType() {
      return WildernessPlugin.get().getEntityResourceType();
   }

   public static class ChunkVisitor extends WildernessVisitor<ChunkStore> {
      @Nonnull
      public WildernessVisitor.ChunkVisitor clone() {
         return new WildernessVisitor.ChunkVisitor();
      }
   }

   public static class EntityVisitor extends WildernessVisitor<EntityStore> {
      @Nonnull
      public WildernessVisitor.EntityVisitor clone() {
         return new WildernessVisitor.EntityVisitor();
      }
   }
}
