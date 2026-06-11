package com.hypixel.hytale.server.npc.movement.controllers;

import com.hypixel.hytale.logger.HytaleLogger;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class RailPathSmoother {
   private static final int DEFAULT_INITIAL_CAPACITY = 8;
   private static final int GROWTH_INCREMENT = 4;
   private static final double VERTICAL_EPSILON = 1.0E-6;
   private static final double MIN_WAYPOINT_OFFSET_SQ = 1.0E-12;
   private static final double INITIAL_DIP_COLLAPSE_MAX_DISTANCE = 1.0;
   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
   private Vector3d[] waypoints;
   private RailPath.SegmentCommitType[] waypointSegmentCommitTypes;
   private int waypointCount;
   private double[] horizontalRunEndDistance;
   private double[] horizontalRunY;
   private RailPath.SegmentCommitType[] horizontalRunTransitionCommitTypes;
   private int horizontalRunCount;
   private boolean debug;
   private final Vector3d startPos = new Vector3d();
   private final Vector3d horizDir = new Vector3d();
   private final Vector3d waypointScratch = new Vector3d();
   private final Vector3d projectionScratch = new Vector3d();

   public RailPathSmoother() {
      this(8);
   }

   public RailPathSmoother(int initialCapacity) {
      if (initialCapacity < 1) {
         initialCapacity = 1;
      }

      this.waypoints = new Vector3d[initialCapacity];

      for (int i = 0; i < this.waypoints.length; i++) {
         this.waypoints[i] = new Vector3d();
      }

      this.waypointSegmentCommitTypes = new RailPath.SegmentCommitType[Math.max(1, initialCapacity - 1)];
      Arrays.fill(this.waypointSegmentCommitTypes, RailPath.SegmentCommitType.NONE);
      this.horizontalRunEndDistance = new double[initialCapacity];
      this.horizontalRunY = new double[initialCapacity];
      this.horizontalRunTransitionCommitTypes = new RailPath.SegmentCommitType[Math.max(1, initialCapacity - 1)];
      Arrays.fill(this.horizontalRunTransitionCommitTypes, RailPath.SegmentCommitType.NONE);
   }

   public void reset() {
      this.waypointCount = 0;
      this.horizontalRunCount = 0;
      Arrays.fill(this.waypointSegmentCommitTypes, RailPath.SegmentCommitType.NONE);
      Arrays.fill(this.horizontalRunTransitionCommitTypes, RailPath.SegmentCommitType.NONE);
   }

   public void setDebug(boolean debug) {
      this.debug = debug;
   }

   @Nonnull
   public Vector3d[] getWaypoints() {
      return this.waypoints;
   }

   public int getWaypointCount() {
      return this.waypointCount;
   }

   @Nonnull
   public RailPath.SegmentCommitType[] getWaypointSegmentCommitTypes() {
      return this.waypointSegmentCommitTypes;
   }

   public void smooth(
      @Nonnull ProbeMoveData data,
      @Nonnull MotionController motionController,
      @Nonnull RailPathSmoother.Config config,
      @Nullable RailPathSmoother.ContactContext contactContext
   ) {
      this.reset();
      ProbeMoveData.Segment[] segments = data.segments;
      int count = data.segmentCount;
      if (segments != null && count > 0) {
         if (count == 1) {
            this.emitWaypoint(segments[0].position, RailPath.SegmentCommitType.NONE);
            this.logDebugOutput(data, motionController, contactContext);
         } else if (!motionController.is2D()) {
            this.emitWaypoint(segments[0].position, RailPath.SegmentCommitType.NONE);
            this.emitWaypoint(segments[count - 1].position, RailPath.SegmentCommitType.NONE);
            this.logDebugOutput(data, motionController, contactContext);
         } else {
            double horizLength = computeProjectedDirection(data.initialPosition, data.targetPosition, motionController.getComponentSelector(), this.horizDir);
            if (horizLength > 1.0E-6) {
               this.horizDir.div(horizLength);
            } else {
               this.horizDir.zero();
            }

            this.startPos.set(segments[0].position);
            this.buildHorizontalRuns(segments, count);
            this.collapseInitialZeroDistanceDip();
            this.skipShortDipRuns(config, contactContext);
            this.emitWaypoint(segments[0].position, RailPath.SegmentCommitType.NONE);
            int i = 0;

            while (i < this.horizontalRunCount - 1) {
               double runStartDistance = i <= 0 ? 0.0 : this.horizontalRunEndDistance[i - 1];
               double runEndDistance = this.horizontalRunEndDistance[i];
               double currentRunY = this.horizontalRunY[i];
               double successorRunY = this.horizontalRunY[i + 1];
               boolean isDrop = successorRunY < currentRunY - 1.0E-6;
               boolean isClimb = successorRunY > currentRunY + 1.0E-6;
               boolean handledDropClimbPair = false;
               if (isDrop && i + 2 < this.horizontalRunCount && this.horizontalRunY[i + 2] > successorRunY + 1.0E-6) {
                  double dropEndDistance = this.computeDropEndDistance(i, config, contactContext);
                  double climbStartDistance = this.computeClimbStartDistance(i + 1, config, contactContext);
                  if (dropEndDistance > climbStartDistance + 1.0E-6 && config.dropSlope > 0.0 && config.climbSlope > 0.0) {
                     double intersectionDistance = this.computeDropClimbIntersectionDistance(i, config);
                     double midRunStart = runEndDistance;
                     double midRunEnd = this.horizontalRunEndDistance[i + 1];
                     if (intersectionDistance < midRunStart) {
                        intersectionDistance = midRunStart;
                     } else if (intersectionDistance > midRunEnd) {
                        intersectionDistance = midRunEnd;
                     }

                     if (isDistanceInsideRun(contactContext, midRunStart, midRunEnd)) {
                        intersectionDistance = contactContext.hitDistanceS;
                     }

                     this.emitWaypoint(runEndDistance, currentRunY, RailPath.SegmentCommitType.NONE);
                     this.emitWaypoint(intersectionDistance, successorRunY, RailPath.SegmentCommitType.SLOPE_DOWN);
                     this.emitWaypoint(this.horizontalRunEndDistance[i + 1], this.horizontalRunY[i + 2], RailPath.SegmentCommitType.SLOPE_UP);
                     i += 2;
                     handledDropClimbPair = true;
                  }
               }

               if (!handledDropClimbPair) {
                  if (isClimb && config.climbSlope > 0.0) {
                     double dy = successorRunY - currentRunY;
                     double maxBackExtension = dy * config.climbSlope;
                     double climbStartDistance = runEndDistance - maxBackExtension;
                     if (climbStartDistance < runStartDistance) {
                        climbStartDistance = runStartDistance;
                     }

                     if (isDistanceInsideRun(contactContext, runStartDistance, runEndDistance)) {
                        climbStartDistance = Math.max(climbStartDistance, contactContext.hitDistanceS);
                     }

                     this.emitWaypoint(climbStartDistance, currentRunY, this.horizontalRunTransitionCommitTypes[i]);
                     this.emitWaypoint(runEndDistance, successorRunY, RailPath.SegmentCommitType.SLOPE_UP);
                  } else if (isDrop && config.dropSlope > 0.0) {
                     double dropEndDistance = this.computeDropEndDistance(i, config, contactContext);
                     this.emitWaypoint(runEndDistance, currentRunY, this.horizontalRunTransitionCommitTypes[i]);
                     this.emitWaypoint(dropEndDistance, successorRunY, RailPath.SegmentCommitType.SLOPE_DOWN);
                  } else {
                     this.emitWaypoint(runEndDistance, currentRunY, this.horizontalRunTransitionCommitTypes[i]);
                     this.emitWaypoint(runEndDistance, successorRunY, RailPath.SegmentCommitType.NONE);
                  }

                  i++;
               }
            }

            RailPath.SegmentCommitType finalRunCommitType = this.horizontalRunCount == 1
               ? this.horizontalRunTransitionCommitTypes[0]
               : RailPath.SegmentCommitType.NONE;
            this.emitWaypoint(this.horizontalRunEndDistance[this.horizontalRunCount - 1], this.horizontalRunY[this.horizontalRunCount - 1], finalRunCommitType);
            this.emitFinalWaypoint(segments[count - 1], config);
            this.logDebugOutput(data, motionController, contactContext);
         }
      }
   }

   private void emitFinalWaypoint(@Nonnull ProbeMoveData.Segment finalSegment, @Nonnull RailPathSmoother.Config config) {
      if (finalSegment.type == ProbeMoveData.Segment.Type.BLOCKED_WALL && !(config.blockedWallEndOffset <= 0.0)) {
         this.waypointScratch.set(finalSegment.position).fma(config.blockedWallEndOffset, this.horizDir);
         this.emitWaypoint(this.waypointScratch, RailPath.SegmentCommitType.NONE);
      } else {
         this.emitWaypoint(finalSegment.position, RailPath.SegmentCommitType.NONE);
      }
   }

   private void buildHorizontalRuns(@Nonnull ProbeMoveData.Segment[] segments, int count) {
      this.ensureHorizontalRunCapacity(count);
      this.horizontalRunCount = 0;
      double currentRunY = segments[0].position.y;
      double currentRunStartDistance = segments[0].distance;

      for (int i = 1; i < count; i++) {
         Vector3d previous = segments[i - 1].position;
         Vector3d current = segments[i].position;
         if (!(Math.abs(current.y - previous.y) <= 1.0E-6)) {
            double currentRunEndDistance = segments[i - 1].distance;
            boolean isInitialZeroLengthRun = this.horizontalRunCount == 0 && currentRunEndDistance == currentRunStartDistance;
            if (currentRunEndDistance > currentRunStartDistance + 1.0E-6 || isInitialZeroLengthRun) {
               this.addHorizontalRun(currentRunEndDistance, currentRunY);
               currentRunStartDistance = currentRunEndDistance;
            }

            currentRunY = current.y;
         }
      }

      double finalDistance = segments[count - 1].distance;
      if (finalDistance > currentRunStartDistance + 1.0E-6 || this.horizontalRunCount == 0) {
         this.addHorizontalRun(finalDistance, currentRunY);
      }

      int transitionCount = Math.max(0, this.horizontalRunCount - 1);
      if (transitionCount > 0) {
         Arrays.fill(this.horizontalRunTransitionCommitTypes, 0, transitionCount, RailPath.SegmentCommitType.NONE);
      }
   }

   private void addHorizontalRun(double runEndDistance, double runY) {
      if (this.horizontalRunCount > 0) {
         double previousRunEndDistance = this.horizontalRunEndDistance[this.horizontalRunCount - 1];
         assert runEndDistance >= previousRunEndDistance : "Malformed ProbeMoveData: horizontal run end distances must be non-decreasing";
      }

      this.horizontalRunEndDistance[this.horizontalRunCount] = runEndDistance;
      this.horizontalRunY[this.horizontalRunCount] = runY;
      this.horizontalRunCount++;
   }

   private void ensureHorizontalRunCapacity(int needed) {
      if (this.horizontalRunEndDistance.length < needed) {
         this.horizontalRunEndDistance = Arrays.copyOf(this.horizontalRunEndDistance, needed);
         this.horizontalRunY = Arrays.copyOf(this.horizontalRunY, needed);
         int transitionNeeded = Math.max(1, needed - 1);
         int oldTransitionLength = this.horizontalRunTransitionCommitTypes.length;
         this.horizontalRunTransitionCommitTypes = Arrays.copyOf(this.horizontalRunTransitionCommitTypes, transitionNeeded);
         Arrays.fill(
            this.horizontalRunTransitionCommitTypes, oldTransitionLength, this.horizontalRunTransitionCommitTypes.length, RailPath.SegmentCommitType.NONE
         );
      }
   }

   private void collapseInitialZeroDistanceDip() {
      if (this.horizontalRunCount >= 3) {
         if (!(this.horizontalRunEndDistance[0] > 1.0E-6)) {
            double initialDipRunLength = this.horizontalRunEndDistance[1] - this.horizontalRunEndDistance[0];
            boolean isShortInitialDip = initialDipRunLength <= 1.000001;
            boolean isInitialDip = this.horizontalRunY[1] < this.horizontalRunY[0] - 1.0E-6 && this.horizontalRunY[2] > this.horizontalRunY[1] + 1.0E-6;
            boolean returnsToStartHeightOrHigher = this.horizontalRunY[2] >= this.horizontalRunY[0] - 1.0E-6;
            if (isInitialDip && isShortInitialDip && returnsToStartHeightOrHigher) {
               for (int i = 1; i < this.horizontalRunCount - 1; i++) {
                  this.horizontalRunEndDistance[i] = this.horizontalRunEndDistance[i + 1];
                  this.horizontalRunY[i] = this.horizontalRunY[i + 1];
               }

               if (this.horizontalRunCount > 2) {
                  this.horizontalRunTransitionCommitTypes[0] = mergeCommitTypes(
                     mergeCommitTypes(this.horizontalRunTransitionCommitTypes[0], this.horizontalRunTransitionCommitTypes[1]),
                     RailPath.SegmentCommitType.SKIP_DIP
                  );
                  int newTransitionCount = this.horizontalRunCount - 2;

                  for (int i = 1; i < newTransitionCount; i++) {
                     this.horizontalRunTransitionCommitTypes[i] = this.horizontalRunTransitionCommitTypes[i + 1];
                  }

                  this.horizontalRunTransitionCommitTypes[newTransitionCount] = RailPath.SegmentCommitType.NONE;
               }

               this.horizontalRunCount--;
            }
         }
      }
   }

   private void skipShortDipRuns(@Nonnull RailPathSmoother.Config config, @Nullable RailPathSmoother.ContactContext contactContext) {
      if (this.horizontalRunCount >= 3) {
         boolean hasValidHitDistance = contactContext != null && contactContext.valid && Double.isFinite(contactContext.hitDistanceS);
         int index = 1;

         while (index < this.horizontalRunCount - 1) {
            double predecessorY = this.horizontalRunY[index - 1];
            double runY = this.horizontalRunY[index];
            double successorY = this.horizontalRunY[index + 1];
            boolean isDip = Math.abs(predecessorY - successorY) <= 1.0E-6 && predecessorY > runY + 1.0E-6;
            if (!isDip) {
               index++;
            } else {
               double runStartDistance = this.horizontalRunEndDistance[index - 1];
               double runEndDistance = this.horizontalRunEndDistance[index];
               double runLength = runEndDistance - runStartDistance;
               boolean insideContactRange = hasValidHitDistance
                  && contactContext.hitDistanceS >= runStartDistance - 1.0E-6
                  && contactContext.hitDistanceS <= runEndDistance + 1.0E-6;
               if (!(runLength > config.horizontalSkipGapWidth + 1.0E-6) && !insideContactRange) {
                  this.horizontalRunTransitionCommitTypes[index - 1] = mergeCommitTypes(
                     mergeCommitTypes(this.horizontalRunTransitionCommitTypes[index - 1], this.horizontalRunTransitionCommitTypes[index]),
                     RailPath.SegmentCommitType.SKIP_DIP
                  );
                  this.horizontalRunEndDistance[index - 1] = this.horizontalRunEndDistance[index + 1];
                  int newCount = this.horizontalRunCount - 2;

                  for (int i = index; i < newCount; i++) {
                     this.horizontalRunEndDistance[i] = this.horizontalRunEndDistance[i + 2];
                     this.horizontalRunY[i] = this.horizontalRunY[i + 2];
                     this.horizontalRunTransitionCommitTypes[i] = this.horizontalRunTransitionCommitTypes[i + 2];
                  }

                  this.horizontalRunCount = newCount;
                  int transitionCount = Math.max(0, this.horizontalRunCount - 1);
                  if (transitionCount > 0 && transitionCount < this.horizontalRunTransitionCommitTypes.length) {
                     this.horizontalRunTransitionCommitTypes[transitionCount] = RailPath.SegmentCommitType.NONE;
                  }

                  if (index > 1) {
                     index--;
                  }
               } else {
                  index++;
               }
            }
         }
      }
   }

   private static boolean isDistanceInsideRun(@Nullable RailPathSmoother.ContactContext contactContext, double runStartDistance, double runEndDistance) {
      return contactContext != null
         && contactContext.valid
         && Double.isFinite(contactContext.hitDistanceS)
         && contactContext.hitDistanceS >= runStartDistance - 1.0E-6
         && contactContext.hitDistanceS <= runEndDistance + 1.0E-6;
   }

   private double computeClimbStartDistance(int runIndex, @Nonnull RailPathSmoother.Config config, @Nullable RailPathSmoother.ContactContext contactContext) {
      double runStartDistance = runIndex <= 0 ? 0.0 : this.horizontalRunEndDistance[runIndex - 1];
      double runEndDistance = this.horizontalRunEndDistance[runIndex];
      double currentRunY = this.horizontalRunY[runIndex];
      double successorRunY = this.horizontalRunY[runIndex + 1];
      if (config.climbSlope <= 0.0) {
         return runEndDistance;
      }

      double dy = successorRunY - currentRunY;
      double climbStartDistance = runEndDistance - dy * config.climbSlope;
      if (climbStartDistance < runStartDistance) {
         climbStartDistance = runStartDistance;
      }

      if (isDistanceInsideRun(contactContext, runStartDistance, runEndDistance)) {
         climbStartDistance = Math.max(climbStartDistance, contactContext.hitDistanceS);
      }

      return climbStartDistance;
   }

   private double computeDropEndDistance(int runIndex, @Nonnull RailPathSmoother.Config config, @Nullable RailPathSmoother.ContactContext contactContext) {
      double runEndDistance = this.horizontalRunEndDistance[runIndex];
      double successorRunEndDistance = this.horizontalRunEndDistance[runIndex + 1];
      double currentRunY = this.horizontalRunY[runIndex];
      double successorRunY = this.horizontalRunY[runIndex + 1];
      if (config.dropSlope <= 0.0) {
         return runEndDistance;
      }

      double dy = currentRunY - successorRunY;
      double dropEndDistance = runEndDistance + dy * config.dropSlope;
      if (dropEndDistance > successorRunEndDistance) {
         dropEndDistance = successorRunEndDistance;
      }

      if (isDistanceInsideRun(contactContext, runEndDistance, successorRunEndDistance)) {
         dropEndDistance = Math.min(dropEndDistance, contactContext.hitDistanceS);
      }

      return dropEndDistance;
   }

   private double computeDropClimbIntersectionDistance(int dropRunIndex, @Nonnull RailPathSmoother.Config config) {
      double dropEdgeDistance = this.horizontalRunEndDistance[dropRunIndex];
      double dropTopY = this.horizontalRunY[dropRunIndex];
      double climbEdgeDistance = this.horizontalRunEndDistance[dropRunIndex + 1];
      double climbTopY = this.horizontalRunY[dropRunIndex + 2];
      double denominator = 1.0 / config.dropSlope + 1.0 / config.climbSlope;
      return Math.abs(denominator) <= 1.0E-6
         ? dropEdgeDistance
         : (dropTopY - climbTopY + dropEdgeDistance / config.dropSlope + climbEdgeDistance / config.climbSlope) / denominator;
   }

   private void emitWaypoint(double distance, double y, @Nonnull RailPath.SegmentCommitType commitType) {
      this.waypointScratch.set(this.startPos).fma(distance, this.horizDir);
      this.waypointScratch.y = y;
      this.emitWaypoint(this.waypointScratch, commitType);
   }

   private void emitWaypoint(@Nonnull Vector3dc waypoint, @Nonnull RailPath.SegmentCommitType commitType) {
      if (this.waypointCount <= 0 || !(this.waypoints[this.waypointCount - 1].distanceSquared(waypoint) <= 1.0E-12)) {
         int segmentIndex = this.waypointCount - 1;
         this.nextWaypoint().set(waypoint);
         if (segmentIndex >= 0) {
            this.ensureWaypointSegmentCommitCapacity(this.waypointCount - 1);
            this.waypointSegmentCommitTypes[segmentIndex] = commitType;
         }
      }
   }

   @Nonnull
   private Vector3d nextWaypoint() {
      if (this.waypointCount == this.waypoints.length) {
         int oldLength = this.waypoints.length;
         this.waypoints = Arrays.copyOf(this.waypoints, oldLength + 4);

         for (int i = oldLength; i < this.waypoints.length; i++) {
            this.waypoints[i] = new Vector3d();
         }

         this.ensureWaypointSegmentCommitCapacity(this.waypoints.length - 1);
      }

      return this.waypoints[this.waypointCount++];
   }

   private void ensureWaypointSegmentCommitCapacity(int needed) {
      if (needed > this.waypointSegmentCommitTypes.length) {
         int oldLength = this.waypointSegmentCommitTypes.length;
         this.waypointSegmentCommitTypes = Arrays.copyOf(this.waypointSegmentCommitTypes, needed);
         Arrays.fill(this.waypointSegmentCommitTypes, oldLength, this.waypointSegmentCommitTypes.length, RailPath.SegmentCommitType.NONE);
      }
   }

   @Nonnull
   private static RailPath.SegmentCommitType mergeCommitTypes(@Nonnull RailPath.SegmentCommitType first, @Nonnull RailPath.SegmentCommitType second) {
      if (first == RailPath.SegmentCommitType.SKIP_DIP || second == RailPath.SegmentCommitType.SKIP_DIP) {
         return RailPath.SegmentCommitType.SKIP_DIP;
      } else {
         return first != RailPath.SegmentCommitType.NONE ? first : second;
      }
   }

   private void logDebugOutput(
      @Nonnull ProbeMoveData data, @Nonnull MotionController motionController, @Nullable RailPathSmoother.ContactContext contactContext
   ) {
      if (this.debug) {
         ProbeMoveData.Segment[] segments = data.segments;
         if (segments != null && data.segmentCount > 0) {
            boolean hasValidContact = contactContext != null && contactContext.valid && Double.isFinite(contactContext.hitDistanceS);
            double hitDistance = hasValidContact ? contactContext.hitDistanceS : Double.NaN;
            StringBuilder segmentLog = new StringBuilder();
            segmentLog.append("RailPathSmoother segments")
               .append('\n')
               .append(String.format(Locale.ROOT, "%-6s %-12s %-18s %-12s", "index", "distance", "type", "y"));

            for (int i = 0; i < data.segmentCount; i++) {
               ProbeMoveData.Segment segment = segments[i];
               double segmentStart = i <= 0 ? 0.0 : segments[i - 1].distance;
               double segmentEnd = segment.distance;
               boolean overlapsHitDistance = hasValidContact
                  && hitDistance >= Math.min(segmentStart, segmentEnd) - 1.0E-6
                  && hitDistance <= Math.max(segmentStart, segmentEnd) + 1.0E-6;
               segmentLog.append('\n').append(String.format(Locale.ROOT, "%-6d %-12.4f %-18s %-12.4f", i, segment.distance, segment.type, segment.position.y));
               if (overlapsHitDistance) {
                  segmentLog.append(String.format(Locale.ROOT, " hitDistance=%s hitSide=%s", hitDistance, contactContext.hitSide));
               }
            }

            LOGGER.at(Level.INFO).log("%s", segmentLog);
            StringBuilder runLog = new StringBuilder();
            runLog.append("RailPathSmoother horizontal runs").append('\n').append(String.format(Locale.ROOT, "%-6s %-12s %-12s", "index", "endDistance", "y"));

            for (int i = 0; i < this.horizontalRunCount; i++) {
               double runStartDistance = i <= 0 ? 0.0 : this.horizontalRunEndDistance[i - 1];
               double runEndDistance = this.horizontalRunEndDistance[i];
               assert runEndDistance >= runStartDistance : "Malformed ProbeMoveData: run end distance cannot be smaller than run start distance";
               boolean overlapsHitDistance = hasValidContact && hitDistance >= runStartDistance - 1.0E-6 && hitDistance <= runEndDistance + 1.0E-6;
               runLog.append('\n').append(String.format(Locale.ROOT, "%-6d %-12.4f %-12.4f", i, this.horizontalRunEndDistance[i], this.horizontalRunY[i]));
               if (overlapsHitDistance) {
                  runLog.append(String.format(Locale.ROOT, " hitDistance=%s hitSide=%s", hitDistance, contactContext.hitSide));
               }
            }

            LOGGER.at(Level.INFO).log("%s", runLog);
            double directionLength = computeProjectedDirection(
               segments[0].position, segments[data.segmentCount - 1].position, motionController.getComponentSelector(), this.projectionScratch
            );
            StringBuilder waypointLog = new StringBuilder();
            waypointLog.append("RailPathSmoother waypoints").append('\n').append(String.format(Locale.ROOT, "%-6s %-12s %-12s", "index", "distance", "y"));

            for (int i = 0; i < this.waypointCount; i++) {
               Vector3d waypoint = this.waypoints[i];
               double distance = directionLength <= 1.0E-6
                  ? 0.0
                  : computeProjectedDirection(segments[0].position, waypoint, motionController.getComponentSelector(), this.projectionScratch)
                     / directionLength
                     * segments[data.segmentCount - 1].distance;
               waypointLog.append('\n').append(String.format(Locale.ROOT, "%-6d %-12.4f %-12.4f", i, distance, waypoint.y));
            }

            LOGGER.at(Level.INFO).log("%s", waypointLog);
         }
      }
   }

   private static double computeProjectedDirection(
      @Nonnull Vector3dc from, @Nonnull Vector3dc to, @Nonnull Vector3dc componentSelector, @Nonnull Vector3d directionOut
   ) {
      directionOut.set((to.x() - from.x()) * componentSelector.x(), (to.y() - from.y()) * componentSelector.y(), (to.z() - from.z()) * componentSelector.z());
      return directionOut.length();
   }

   public static final class Config {
      public double climbSlope;
      public double dropSlope;
      public double horizontalSkipGapWidth;
      public double blockedWallEndOffset;

      public void reset() {
         this.climbSlope = 0.0;
         this.dropSlope = 0.0;
         this.horizontalSkipGapWidth = 0.0;
         this.blockedWallEndOffset = 0.0;
      }
   }

   public static final class ContactContext {
      public boolean valid;
      public double hitDistanceS;
      public double hitY;
      public int hitSegmentIndex;
      @Nonnull
      public RailPathSmoother.HitSide hitSide = RailPathSmoother.HitSide.UNKNOWN;
      public double contactWindowHalfWidth;

      public void reset() {
         this.valid = false;
         this.hitDistanceS = Double.NaN;
         this.hitY = Double.NaN;
         this.hitSegmentIndex = -1;
         this.hitSide = RailPathSmoother.HitSide.UNKNOWN;
         this.contactWindowHalfWidth = Double.NaN;
      }

      public void setFrom(@Nonnull RailPathSmoother.ContactContext other) {
         this.valid = other.valid;
         this.hitDistanceS = other.hitDistanceS;
         this.hitY = other.hitY;
         this.hitSegmentIndex = other.hitSegmentIndex;
         this.hitSide = other.hitSide;
         this.contactWindowHalfWidth = other.contactWindowHalfWidth;
      }
   }

   public enum HitSide {
      BEFORE_TARGET,
      AFTER_TARGET,
      UNKNOWN;
   }
}
