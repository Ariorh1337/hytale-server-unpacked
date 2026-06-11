package com.hypixel.hytale.server.npc.corecomponents;

import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderBodyMotionBase;
import com.hypixel.hytale.server.npc.instructions.BodyMotion;
import com.hypixel.hytale.server.npc.instructions.ExecutionSupport;
import com.hypixel.hytale.server.npc.movement.constraints.RelaxedConstraint;
import com.hypixel.hytale.server.npc.movement.controllers.ProbeMoveData;
import java.util.EnumSet;
import javax.annotation.Nonnull;

public abstract class BodyMotionBase extends MotionBase implements BodyMotion {
   public BodyMotionBase(@Nonnull BuilderBodyMotionBase builderMotionBase) {
   }

   @Nonnull
   protected static EnumSet<RelaxedConstraint> computeEffectiveRelaxedConstraints(
      boolean usesLegacyConstraintMode,
      @Nonnull EnumSet<RelaxedConstraint> relaxedConstraints,
      boolean isLegacyRelaxedMoveConstraints,
      boolean isLegacyAvoidingBlockDamage
   ) {
      if (!usesLegacyConstraintMode) {
         return relaxedConstraints;
      }

      EnumSet<RelaxedConstraint> effectiveConstraints = isLegacyRelaxedMoveConstraints
         ? EnumSet.copyOf(RelaxedConstraint.DEFAULT_WHEN_RELAXED)
         : EnumSet.noneOf(RelaxedConstraint.class);
      if (!isLegacyAvoidingBlockDamage) {
         effectiveConstraints.add(RelaxedConstraint.DAMAGE);
      }

      return effectiveConstraints;
   }

   protected static boolean applyEscapeConstraints(@Nonnull ExecutionSupport executionSupport, @Nonnull ProbeMoveData probeMoveData) {
      if (!executionSupport.getPositionCache().couldBreatheCached()) {
         probeMoveData.getRelaxedConstraints().add(RelaxedConstraint.BREATHE);
         probeMoveData.getRelaxedConstraints().add(RelaxedConstraint.WADE);
         return true;
      } else {
         return false;
      }
   }
}
