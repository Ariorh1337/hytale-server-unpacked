package com.hypixel.hytale.builtin.hytalegenerator.graph;

import java.math.BigDecimal;
import javax.annotation.Nonnull;

public class PreciseTimeInstrument {
   @Nonnull
   private final PreciseTimeInstrument.Probe[] probes;
   private long sampleCount;

   public PreciseTimeInstrument(int probeCount) {
      this.probes = new PreciseTimeInstrument.Probe[probeCount];

      for (int i = 0; i < probeCount; i++) {
         this.probes[i] = new PreciseTimeInstrument.Probe();
         this.probes[i].label = Integer.toString(i);
      }

      this.sampleCount = 0L;
   }

   public void setProbeLabel(int probeId, @Nonnull String label) {
      assert this.isValid(probeId);
      this.probes[probeId].label = label;
   }

   public void start(int probeId) {
      assert this.isValid(probeId);
      assert !this.probes[probeId].hasStart;
      this.probes[probeId].start = System.nanoTime();
   }

   public void stop(int probeId) {
      assert this.isValid(probeId);
      assert this.probes[probeId].hasStart;
      long elapsed = System.nanoTime() - this.probes[probeId].start;
      this.probes[probeId].total += elapsed;
      this.probes[probeId].max = Math.max(this.probes[probeId].max, elapsed);
      this.probes[probeId].min = Math.min(this.probes[probeId].min, elapsed);
      this.probes[probeId].hasStart = false;
   }

   public void save() {
      assert this.isAllProbesStopped();
      this.sampleCount++;
   }

   public long getTotal_ns(int probeId) {
      assert this.isValid(probeId);
      assert this.isAllProbesStopped();
      return this.probes[probeId].total;
   }

   public long getMin_ns(int probeId) {
      assert this.isValid(probeId);
      assert this.isAllProbesStopped();
      return this.probes[probeId].min;
   }

   public long getMax_ns(int probeId) {
      assert this.isValid(probeId);
      assert this.isAllProbesStopped();
      return this.probes[probeId].max;
   }

   public long getAverage_ns(int probeId) {
      assert this.isValid(probeId);
      assert this.isAllProbesStopped();
      return this.probes[probeId].total / this.sampleCount;
   }

   public long getSampleCount() {
      return this.sampleCount;
   }

   public void clear() {
      assert this.isAllProbesStopped();
      this.sampleCount = 0L;

      for (PreciseTimeInstrument.Probe probe : this.probes) {
         probe.clear();
      }
   }

   public boolean isValid(int probeId) {
      return probeId >= 0 && probeId < this.probes.length;
   }

   public boolean isAllProbesStopped() {
      for (PreciseTimeInstrument.Probe probe : this.probes) {
         if (probe.hasStart) {
            return false;
         }
      }

      return true;
   }

   @Nonnull
   public String toString(@Nonnull String label) {
      String msg = label + " :";
      msg = msg + "\n\tSample Count : " + this.sampleCount;

      for (PreciseTimeInstrument.Probe probe : this.probes) {
         msg = msg + "\n\t\t" + probe.label + " :";
         msg = msg + "\n\t\t\tAverage : " + this.getAverage_ns(probe) + " ns / " + toString_ms(this.getAverage_ns(probe)) + " ms";
         msg = msg + "\n\t\t\tMax : " + probe.max + " ns / " + toString_ms(probe.max) + " ms";
         msg = msg + "\n\t\t\tMin : " + probe.min + " ns / " + toString_ms(probe.min) + " ms";
      }

      return msg;
   }

   private long getAverage_ns(@Nonnull PreciseTimeInstrument.Probe probe) {
      return probe.total / this.sampleCount;
   }

   private static String toString_ms(long ns) {
      return BigDecimal.valueOf(ns / 1000000.0).toString();
   }

   private static class Probe {
      @Nonnull
      String label = "";
      boolean hasStart = false;
      long start = 0L;
      long total = 0L;
      long max = Long.MIN_VALUE;
      long min = Long.MAX_VALUE;

      void clear() {
         this.total = 0L;
         this.max = Long.MIN_VALUE;
         this.min = Long.MAX_VALUE;
      }
   }
}
