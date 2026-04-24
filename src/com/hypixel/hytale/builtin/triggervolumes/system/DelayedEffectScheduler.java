package com.hypixel.hytale.builtin.triggervolumes.system;

import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerContext;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEventType;
import com.hypixel.hytale.builtin.triggervolumes.manager.VolumeEntry;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.List;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DelayedEffectScheduler {
   @Nonnull
   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
   private final PriorityQueue<DelayedEffectScheduler.ScheduledEffect> queue = new PriorityQueue<>();

   public void schedule(
      @Nonnull TriggerEffect effect,
      @Nonnull Ref<EntityStore> entityRef,
      @Nonnull UUID entityUuid,
      @Nonnull TriggerEventType eventType,
      @Nonnull VolumeEntry volume,
      long nowNanos,
      float delaySeconds
   ) {
      this.schedule(effect, entityRef, entityUuid, eventType, volume, nowNanos, delaySeconds, null);
   }

   public void schedule(
      @Nonnull TriggerEffect effect,
      @Nonnull Ref<EntityStore> entityRef,
      @Nonnull UUID entityUuid,
      @Nonnull TriggerEventType eventType,
      @Nonnull VolumeEntry volume,
      long nowNanos,
      float delaySeconds,
      @Nullable List<VolumeEntry> spatialVolumes
   ) {
      long executeAt = nowNanos + (long)(delaySeconds * 1.0E9F);
      this.queue.add(new DelayedEffectScheduler.ScheduledEffect(executeAt, effect, entityRef, entityUuid, eventType, volume, spatialVolumes));
   }

   public void tick(long nowNanos, @Nonnull Store<EntityStore> store) {
      while (!this.queue.isEmpty() && this.queue.peek().executeAtNanos <= nowNanos) {
         DelayedEffectScheduler.ScheduledEffect scheduled = this.queue.poll();
         if (scheduled.entityRef.isValid() && !scheduled.volume.isPendingDestroy()) {
            try {
               TriggerContext context = scheduled.spatialVolumes != null
                  ? new TriggerContext(scheduled.entityRef, store, scheduled.eventType, scheduled.volume, scheduled.spatialVolumes)
                  : new TriggerContext(scheduled.entityRef, store, scheduled.eventType, scheduled.volume);
               scheduled.effect.execute(context);
            } catch (Exception e) {
               LOGGER.at(Level.WARNING)
                  .withCause(e)
                  .log("Error executing delayed effect %s on volume '%s'", scheduled.effect.getClass().getSimpleName(), scheduled.volume.getId());
            }
         }
      }
   }

   public void cancelForEntity(@Nonnull UUID entityUuid) {
      this.queue.removeIf(s -> s.entityUuid.equals(entityUuid));
   }

   public void cancelNonExitForEntity(@Nonnull UUID entityUuid) {
      this.queue.removeIf(s -> s.entityUuid.equals(entityUuid) && s.eventType != TriggerEventType.EXIT);
   }

   public void cancelForVolume(@Nonnull VolumeEntry volume) {
      this.queue.removeIf(s -> s.volume == volume);
   }

   public boolean isEmpty() {
      return this.queue.isEmpty();
   }

   private record ScheduledEffect(
      long executeAtNanos,
      @Nonnull TriggerEffect effect,
      @Nonnull Ref<EntityStore> entityRef,
      @Nonnull UUID entityUuid,
      @Nonnull TriggerEventType eventType,
      @Nonnull VolumeEntry volume,
      @Nullable List<VolumeEntry> spatialVolumes
   ) implements Comparable<DelayedEffectScheduler.ScheduledEffect> {
      public int compareTo(@Nonnull DelayedEffectScheduler.ScheduledEffect other) {
         return Long.compare(this.executeAtNanos, other.executeAtNanos);
      }
   }
}
