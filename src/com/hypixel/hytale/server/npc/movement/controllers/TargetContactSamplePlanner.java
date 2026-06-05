package com.hypixel.hytale.server.npc.movement.controllers;

import com.hypixel.hytale.math.util.MathUtil;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class TargetContactSamplePlanner {
   public static final int MAX_SAMPLES = 6;

   private TargetContactSamplePlanner() {
   }

   public static int planSamples(
      @Nonnull ProbeMoveData probeMoveData,
      double targetDistance,
      double wallOffset,
      double maxProbeDistance,
      double sampleDistanceEpsilon,
      @Nonnull TargetContactSamplePlanner.Scratch scratch,
      @Nonnull double[] sampleDistances,
      @Nonnull RailPathSmoother.HitSide[] sampleSides
   ) {
      if (sampleDistances.length < 6 || sampleSides.length < 6) {
         throw new IllegalArgumentException("sample buffers must have at least MAX_SAMPLES entries");
      }

      if (!computeTargetRunBounds(probeMoveData, targetDistance, scratch)) {
         return 0;
      }

      double minDistance = 0.0;
      double runStart = MathUtil.clamp(scratch.runBounds.start, minDistance, maxProbeDistance);
      double runEnd = MathUtil.clamp(scratch.runBounds.end, minDistance, maxProbeDistance);
      if (runStart > runEnd) {
         double t = runStart;
         runStart = runEnd;
         runEnd = t;
      }

      int sampleCount = 0;
      double beforeMin = runStart + sampleDistanceEpsilon;
      double beforeMax = Math.min(runEnd - sampleDistanceEpsilon, targetDistance - sampleDistanceEpsilon);
      if (beforeMin <= beforeMax) {
         sampleDistances[sampleCount] = MathUtil.clamp(targetDistance - wallOffset + 1.0E-6, beforeMin, beforeMax);
         sampleSides[sampleCount] = RailPathSmoother.HitSide.BEFORE_TARGET;
         sampleCount++;
      }

      double afterMin = Math.max(runStart + sampleDistanceEpsilon, targetDistance + sampleDistanceEpsilon);
      double afterMax = runEnd - sampleDistanceEpsilon;
      if (afterMin <= afterMax) {
         sampleDistances[sampleCount] = MathUtil.clamp(targetDistance + wallOffset - 1.0E-6, afterMin, afterMax);
         sampleSides[sampleCount] = RailPathSmoother.HitSide.AFTER_TARGET;
         sampleCount++;
      }

      sampleCount = addFallbackSample(
         runStart + sampleDistanceEpsilon, minDistance, maxProbeDistance, targetDistance, sampleCount, sampleDistances, sampleSides
      );
      sampleCount = addFallbackSample(runEnd - sampleDistanceEpsilon, minDistance, maxProbeDistance, targetDistance, sampleCount, sampleDistances, sampleSides);
      sampleCount = addFallbackSample(
         runStart - sampleDistanceEpsilon, minDistance, maxProbeDistance, targetDistance, sampleCount, sampleDistances, sampleSides
      );
      return addFallbackSample(runEnd + sampleDistanceEpsilon, minDistance, maxProbeDistance, targetDistance, sampleCount, sampleDistances, sampleSides);
   }

   private static int addFallbackSample(
      double sampleDistance,
      double minDistance,
      double maxProbeDistance,
      double targetDistance,
      int sampleCount,
      @Nonnull double[] sampleDistances,
      @Nonnull RailPathSmoother.HitSide[] sampleSides
   ) {
      if (Double.isFinite(sampleDistance) && !(sampleDistance < minDistance - 1.0E-6) && !(sampleDistance > maxProbeDistance + 1.0E-6)) {
         sampleDistances[sampleCount] = sampleDistance;
         sampleSides[sampleCount] = sampleDistance <= targetDistance ? RailPathSmoother.HitSide.BEFORE_TARGET : RailPathSmoother.HitSide.AFTER_TARGET;
         return sampleCount + 1;
      } else {
         return sampleCount;
      }
   }

   private static boolean computeTargetRunBounds(
      @Nonnull ProbeMoveData probeMoveData, double targetDistance, @Nonnull TargetContactSamplePlanner.Scratch scratch
   ) {
      if (!Double.isFinite(targetDistance) || probeMoveData.segments == null || probeMoveData.segmentCount <= 0) {
         return false;
      }

      if (!probeMoveData.locateSegmentAtDistance(targetDistance, scratch.segmentLocation)) {
         return false;
      }

      int segmentIndex = scratch.segmentLocation.segmentIndex;
      if (segmentIndex >= 0 && segmentIndex < probeMoveData.segmentCount) {
         ProbeMoveData.Segment[] segments = probeMoveData.segments;
         ProbeMoveData.Segment targetSegment = segments[segmentIndex];
         if (targetSegment == null) {
            return false;
         }

         double targetY = targetSegment.position.y;
         double start = segmentStartDistance(probeMoveData, segmentIndex);
         double end = segments[segmentIndex].distance;
         int left = segmentIndex;

         while (left > 0 && isSameRunY(segments[left - 1], targetY)) {
            start = segmentStartDistance(probeMoveData, --left);
         }

         int right = segmentIndex;

         while (right + 1 < probeMoveData.segmentCount && isSameRunY(segments[right + 1], targetY)) {
            end = segments[++right].distance;
         }

         scratch.runBounds.start = start;
         scratch.runBounds.end = end;
         return true;
      } else {
         return false;
      }
   }

   private static double segmentStartDistance(@Nonnull ProbeMoveData probeMoveData, int segmentIndex) {
      return segmentIndex > 0 && probeMoveData.segments != null ? probeMoveData.segments[segmentIndex - 1].distance : 0.0;
   }

   private static boolean isSameRunY(@Nullable ProbeMoveData.Segment segment, double referenceY) {
      return segment != null && Math.abs(segment.position.y - referenceY) <= 1.0E-6;
   }

   public static final class RunBounds {
      public double start;
      public double end;
   }

   public static final class Scratch {
      public final ProbeMoveData.SegmentLocation segmentLocation = new ProbeMoveData.SegmentLocation();
      public final TargetContactSamplePlanner.RunBounds runBounds = new TargetContactSamplePlanner.RunBounds();
   }
}
