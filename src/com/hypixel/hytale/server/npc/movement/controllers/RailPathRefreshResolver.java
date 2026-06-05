package com.hypixel.hytale.server.npc.movement.controllers;

import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public final class RailPathRefreshResolver {
   private RailPathRefreshResolver() {
   }

   @Nonnull
   public static RailPathRefreshResolver.RefreshDecision resolve(
      @Nonnull RailPath railPath,
      @Nonnull RailPath candidateRailPath,
      @Nonnull Vector3dc selfPosition,
      double remainingDistance,
      double pathTolerance,
      @Nonnull Vector3d anchorScratch,
      @Nonnull RailPathRefreshResolver.RemainingDistanceResolver remainingDistanceResolver,
      @Nonnull RailPathRefreshResolver.CandidateRailProbe candidateRailProbe,
      @Nonnull BooleanSupplier blockedProbeSupplier,
      @Nonnull BooleanSupplier replacementAllowedSupplier
   ) {
      RailPath.SegmentCommitType activeCommitType = railPath.getActiveSegmentCommitType();
      Vector3dc probeStartPosition = selfPosition;
      boolean committedAnchor = false;
      if (railPath.isOnCommittedSegment()) {
         int committedEndWaypoint = railPath.getCommittedSegmentEndWaypointIndex();
         if (committedEndWaypoint >= 0 && committedEndWaypoint < railPath.getWaypointCount()) {
            anchorScratch.set(railPath.getWaypoint(committedEndWaypoint));
            probeStartPosition = anchorScratch;
            committedAnchor = true;
            remainingDistance = remainingDistanceResolver.resolve(probeStartPosition);
         }
      }

      if (remainingDistance <= 1.0E-6) {
         return new RailPathRefreshResolver.RefreshDecision(false, false, false, committedAnchor, activeCommitType);
      }

      candidateRailProbe.probe(probeStartPosition, remainingDistance);
      boolean probeBlocked = blockedProbeSupplier.getAsBoolean();
      if (committedAnchor) {
         return new RailPathRefreshResolver.RefreshDecision(false, probeBlocked, false, committedAnchor, activeCommitType);
      }

      if (probeBlocked && candidateRailPath.getWaypointCount() <= 1) {
         return new RailPathRefreshResolver.RefreshDecision(true, true, false, false, activeCommitType);
      }

      if (!replacementAllowedSupplier.getAsBoolean()) {
         return new RailPathRefreshResolver.RefreshDecision(false, probeBlocked, false, false, activeCommitType);
      }

      boolean equivalent = railPath.remainingEquivalentTo(candidateRailPath, selfPosition, pathTolerance);
      boolean replaced = false;
      if (!equivalent) {
         railPath.capture(candidateRailPath);
         replaced = true;
      }

      return new RailPathRefreshResolver.RefreshDecision(false, probeBlocked, replaced, false, activeCommitType);
   }

   @FunctionalInterface
   public interface CandidateRailProbe {
      void probe(@Nonnull Vector3dc var1, double var2);
   }

   public record RefreshDecision(
      boolean blocked, boolean unreachable, boolean replaced, boolean committedAnchor, @Nonnull RailPath.SegmentCommitType commitType
   ) {
   }

   @FunctionalInterface
   public interface RemainingDistanceResolver {
      double resolve(@Nonnull Vector3dc var1);
   }
}
