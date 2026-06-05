package com.hypixel.hytale.server.npc.movement.controllers;

import com.hypixel.hytale.math.util.MathUtil;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public final class RailPath {
   private static final double MIN_WAYPOINT_OFFSET_SQ = 1.0E-12;
   private static final double CARRY_OVER_FACTOR = 2.0;
   private static final double COLLINEARITY_EPSILON = 1.0E-9;
   private static final double SNAP_SLACK = 1.0E-9;
   private static final int DEFAULT_INITIAL_CAPACITY = 6;
   private static final int GROWTH_INCREMENT = 4;
   private Vector3d[] waypoints;
   private RailPath.SegmentCommitType[] segmentCommitTypes;
   private int waypointCount;
   private int cursor;
   private double segmentProgress;
   private double carryOverDistance;
   private final Vector3d segDirScratch = new Vector3d();
   private final Vector3d referenceDirScratch = new Vector3d();
   private final RailPath.Projection projectionScratch = new RailPath.Projection();

   public RailPath() {
      this(6);
   }

   public RailPath(int initialCapacity) {
      if (initialCapacity < 1) {
         initialCapacity = 1;
      }

      this.waypoints = new Vector3d[initialCapacity];
      this.segmentCommitTypes = new RailPath.SegmentCommitType[Math.max(1, initialCapacity - 1)];

      for (int i = 0; i < this.waypoints.length; i++) {
         this.waypoints[i] = new Vector3d();
      }

      Arrays.fill(this.segmentCommitTypes, RailPath.SegmentCommitType.NONE);
   }

   public void reset() {
      this.waypointCount = 0;
      this.cursor = 0;
      this.segmentProgress = 0.0;
      this.carryOverDistance = 0.0;
      Arrays.fill(this.segmentCommitTypes, RailPath.SegmentCommitType.NONE);
   }

   public void capture(@Nonnull Vector3d[] waypoints, int count) {
      this.capture(waypoints, null, count);
   }

   public void capture(@Nonnull Vector3d[] waypoints, @Nullable RailPath.SegmentCommitType[] segmentCommitTypes, int count) {
      this.reset();
      if (count > 0) {
         this.nextWaypoint().set(waypoints[0]);

         for (int i = 1; i < count; i++) {
            RailPath.SegmentCommitType commitType = segmentCommitTypes != null && i - 1 < segmentCommitTypes.length
               ? segmentCommitTypes[i - 1]
               : RailPath.SegmentCommitType.NONE;
            this.appendIfDistinct(waypoints[i], commitType);
         }
      }
   }

   public void capture(@Nonnull RailPath source) {
      this.capture(source.waypoints, source.segmentCommitTypes, source.waypointCount);
   }

   public void captureSuffixFromProjection(@Nonnull RailPath source, @Nonnull RailPath.Projection projection) {
      this.reset();
      if (source.waypointCount > 0) {
         this.nextWaypoint().set(projection.position);

         for (int i = projection.segmentIndex + 1; i < source.waypointCount; i++) {
            RailPath.SegmentCommitType commitType = i - 1 < source.segmentCommitTypes.length
               ? source.segmentCommitTypes[i - 1]
               : RailPath.SegmentCommitType.NONE;
            this.appendIfDistinct(source.waypoints[i], commitType);
         }
      }
   }

   private void appendIfDistinct(@Nonnull Vector3dc candidate, @Nonnull RailPath.SegmentCommitType commitType) {
      Vector3d last = this.waypoints[this.waypointCount - 1];
      if (!(last.distanceSquared(candidate) <= 1.0E-12)) {
         int segmentIndex = this.waypointCount - 1;
         this.nextWaypoint().set(candidate);
         this.ensureSegmentCommitCapacity(this.waypointCount - 1);
         this.segmentCommitTypes[segmentIndex] = commitType;
      }
   }

   public boolean isEmpty() {
      return this.waypointCount <= 1;
   }

   public boolean isFinished() {
      return this.isEmpty() || this.cursor >= this.waypointCount - 1;
   }

   public int getWaypointCount() {
      return this.waypointCount;
   }

   @Nonnull
   public Vector3dc getWaypoint(int index) {
      if (index >= 0 && index < this.waypointCount) {
         return this.waypoints[index];
      } else {
         throw new IndexOutOfBoundsException(index);
      }
   }

   public int getCursor() {
      return this.cursor;
   }

   public double getSegmentProgress() {
      return this.segmentProgress;
   }

   public boolean isOnCommittedSegment() {
      int segmentIndex = this.getActiveSegmentIndex();
      return segmentIndex >= 0 && segmentIndex < this.waypointCount - 1 ? this.segmentCommitTypes[segmentIndex].isCommitted() : false;
   }

   public int getCommittedSegmentEndWaypointIndex() {
      return !this.isOnCommittedSegment() ? -1 : this.getActiveSegmentIndex() + 1;
   }

   @Nonnull
   public RailPath.SegmentCommitType getActiveSegmentCommitType() {
      int segmentIndex = this.getActiveSegmentIndex();
      return segmentIndex >= 0 && segmentIndex < this.waypointCount - 1 ? this.segmentCommitTypes[segmentIndex] : RailPath.SegmentCommitType.NONE;
   }

   public double getRemainingDistance() {
      if (this.isFinished()) {
         return 0.0;
      }

      double remaining = 0.0;
      Vector3d curr = this.waypoints[this.cursor];
      Vector3d next = this.waypoints[this.cursor + 1];
      remaining += curr.distance(next) * (1.0 - this.segmentProgress);

      for (int i = this.cursor + 1; i < this.waypointCount - 1; i++) {
         remaining += this.waypoints[i].distance(this.waypoints[i + 1]);
      }

      return remaining;
   }

   public boolean projectRemaining(@Nonnull Vector3dc position, @Nonnull RailPath.Projection result) {
      return this.projectFrom(position, this.cursor, this.segmentProgress, result);
   }

   public boolean project(@Nonnull Vector3dc position, @Nonnull RailPath.Projection result) {
      return this.projectFrom(position, 0, 0.0, result);
   }

   private boolean projectFrom(@Nonnull Vector3dc position, int startSegmentIndex, double startSegmentProgress, @Nonnull RailPath.Projection result) {
      result.reset();
      if (this.waypointCount <= 0) {
         return false;
      }

      if (this.waypointCount != 1 && startSegmentIndex < this.waypointCount - 1) {
         double bestDistanceSquared = Double.POSITIVE_INFINITY;
         int bestSegmentIndex = startSegmentIndex;
         double bestSegmentProgress = startSegmentProgress;

         for (int i = startSegmentIndex; i < this.waypointCount - 1; i++) {
            Vector3d start = this.waypoints[i];
            Vector3d end = this.waypoints[i + 1];
            this.segDirScratch.set(end).sub(start);
            double segmentLengthSquared = this.segDirScratch.lengthSquared();
            if (!(segmentLengthSquared <= 1.0E-12)) {
               double t = (
                     (position.x() - start.x) * this.segDirScratch.x
                        + (position.y() - start.y) * this.segDirScratch.y
                        + (position.z() - start.z) * this.segDirScratch.z
                  )
                  / segmentLengthSquared;
               if (i == startSegmentIndex) {
                  t = Math.max(t, startSegmentProgress);
               }

               t = MathUtil.clamp(t, 0.0, 1.0);
               this.referenceDirScratch.set(start).fma(t, this.segDirScratch);
               double distanceSquared = this.referenceDirScratch.distanceSquared(position);
               if (distanceSquared < bestDistanceSquared) {
                  bestDistanceSquared = distanceSquared;
                  bestSegmentIndex = i;
                  bestSegmentProgress = t;
                  result.position.set(this.referenceDirScratch);
               }
            }
         }

         if (!Double.isFinite(bestDistanceSquared)) {
            return false;
         }

         result.segmentIndex = bestSegmentIndex;
         result.segmentProgress = bestSegmentProgress;
         result.distanceSquared = bestDistanceSquared;
         return true;
      } else {
         result.position.set(this.waypoints[this.waypointCount - 1]);
         result.segmentIndex = Math.max(0, this.waypointCount - 1);
         result.segmentProgress = 1.0;
         result.distanceSquared = result.position.distanceSquared(position);
         return true;
      }
   }

   public boolean remainingEquivalentTo(@Nonnull RailPath candidate, @Nonnull Vector3dc currentPosition, double tolerance) {
      double toleranceSquared = tolerance * tolerance;
      if (candidate.waypointCount <= 1) {
         return this.isFinished();
      }

      if (this.isFinished()) {
         return candidate.isFinished();
      }

      if (candidate.waypoints[0].distanceSquared(currentPosition) > toleranceSquared) {
         return false;
      }

      int activeNextIndex = this.cursor + 1;
      if (this.segmentProgress >= 0.999999 && activeNextIndex < this.waypointCount - 1) {
         activeNextIndex++;
      }

      RailPath.Projection projection = this.projectionScratch;

      for (int activeIndex = activeNextIndex; activeIndex < this.waypointCount; activeIndex++) {
         if (!candidate.project(this.waypoints[activeIndex], projection) || projection.distanceSquared > toleranceSquared) {
            return false;
         }
      }

      for (int candidateIndex = 1; candidateIndex < candidate.waypointCount; candidateIndex++) {
         if (!this.projectFrom(candidate.waypoints[candidateIndex], this.cursor, this.segmentProgress, projection)
            || projection.distanceSquared > toleranceSquared) {
            return false;
         }
      }

      return true;
   }

   public void advance(@Nonnull Vector3dc currentPosition, double stepDistance, @Nonnull Vector3d out) {
      out.zero();
      if (this.waypointCount > 1 && !(stepDistance <= 0.0)) {
         double budget = stepDistance + this.carryOverDistance;
         this.carryOverDistance = 0.0;
         this.skipDegenerateSegments();
         if (this.cursor < this.waypointCount - 1) {
            this.referenceDirScratch.set(this.waypoints[this.cursor + 1]).sub(this.waypoints[this.cursor]);
            double referenceDirLen = this.referenceDirScratch.length();
            boolean anchored = false;

            do {
               Vector3d curr = this.waypoints[this.cursor];
               Vector3d next = this.waypoints[this.cursor + 1];
               double segLen = curr.distance(next);
               double leftOnSegment = segLen * (1.0 - this.segmentProgress);
               if (budget + 1.0E-9 < leftOnSegment) {
                  double fraction = budget / segLen;
                  this.segDirScratch.set(next).sub(curr);
                  out.fma(fraction, this.segDirScratch);
                  this.segmentProgress += fraction;
                  return;
               }

               if (!anchored) {
                  out.set(next).sub(currentPosition);
                  anchored = true;
               } else {
                  this.segDirScratch.set(next).sub(curr);
                  out.fma(1.0 - this.segmentProgress, this.segDirScratch);
               }

               this.cursor++;
               this.segmentProgress = 0.0;
               budget -= leftOnSegment;
               this.skipDegenerateSegments();
               if (this.cursor >= this.waypointCount - 1 || budget <= 1.0E-9) {
                  this.carryOverDistance = MathUtil.clamp(budget, 0.0, stepDistance * 2.0);
                  return;
               }
            } while (this.collinearWithReference(this.waypoints[this.cursor], this.waypoints[this.cursor + 1], referenceDirLen));

            this.carryOverDistance = Math.min(budget, stepDistance * 2.0);
         }
      }
   }

   private void skipDegenerateSegments() {
      while (this.cursor < this.waypointCount - 1 && this.waypoints[this.cursor].distanceSquared(this.waypoints[this.cursor + 1]) <= 1.0E-12) {
         this.cursor++;
         this.segmentProgress = 0.0;
      }
   }

   private boolean collinearWithReference(@Nonnull Vector3dc candStart, @Nonnull Vector3dc candEnd, double referenceDirLen) {
      this.segDirScratch.set(candEnd).sub(candStart);
      double candLen = this.segDirScratch.length();
      if (candLen <= 1.0E-6) {
         return false;
      }

      double cosTheta = this.referenceDirScratch.dot(this.segDirScratch) / (referenceDirLen * candLen);
      return cosTheta >= 0.999999999;
   }

   public void snapY(@Nonnull Vector3d position) {
      if (this.cursor < this.waypointCount - 1) {
         Vector3d curr = this.waypoints[this.cursor];
         Vector3d next = this.waypoints[this.cursor + 1];
         double expectedY = curr.y + (next.y - curr.y) * this.segmentProgress;
         if (Math.abs(position.y - expectedY) > 1.0E-6) {
            position.y = expectedY;
         }
      }
   }

   private Vector3d nextWaypoint() {
      if (this.waypointCount == this.waypoints.length) {
         int oldLen = this.waypoints.length;
         this.waypoints = Arrays.copyOf(this.waypoints, oldLen + 4);

         for (int i = oldLen; i < this.waypoints.length; i++) {
            this.waypoints[i] = new Vector3d();
         }

         this.ensureSegmentCommitCapacity(this.waypoints.length - 1);
      }

      return this.waypoints[this.waypointCount++];
   }

   private void ensureSegmentCommitCapacity(int needed) {
      if (needed > this.segmentCommitTypes.length) {
         int oldLen = this.segmentCommitTypes.length;
         this.segmentCommitTypes = Arrays.copyOf(this.segmentCommitTypes, needed);
         Arrays.fill(this.segmentCommitTypes, oldLen, this.segmentCommitTypes.length, RailPath.SegmentCommitType.NONE);
      }
   }

   private int getActiveSegmentIndex() {
      if (this.waypointCount > 1 && this.cursor < this.waypointCount - 1) {
         int segmentIndex = this.cursor;
         if (this.segmentProgress >= 0.999999 && segmentIndex < this.waypointCount - 2) {
            segmentIndex++;
         }

         return segmentIndex;
      } else {
         return -1;
      }
   }

   public static final class Projection {
      private final Vector3d position = new Vector3d();
      private int segmentIndex;
      private double segmentProgress;
      private double distanceSquared;

      private void reset() {
         this.position.zero();
         this.segmentIndex = 0;
         this.segmentProgress = 0.0;
         this.distanceSquared = Double.POSITIVE_INFINITY;
      }

      @Nonnull
      public Vector3dc getPosition() {
         return this.position;
      }

      public int getSegmentIndex() {
         return this.segmentIndex;
      }

      public double getSegmentProgress() {
         return this.segmentProgress;
      }

      public double getDistanceSquared() {
         return this.distanceSquared;
      }
   }

   public enum SegmentCommitType {
      NONE(false),
      SLOPE_UP(true),
      SLOPE_DOWN(true),
      SKIP_DIP(true);

      private final boolean committed;

      SegmentCommitType(boolean committed) {
         this.committed = committed;
      }

      public boolean isCommitted() {
         return this.committed;
      }
   }
}
