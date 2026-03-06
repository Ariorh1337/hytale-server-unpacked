package com.hypixel.hytale.builtin.hytalegenerator.propdistributions;

import com.hypixel.hytale.builtin.hytalegenerator.assignments.Assignments;
import com.hypixel.hytale.builtin.hytalegenerator.props.EmptyProp;
import com.hypixel.hytale.builtin.hytalegenerator.props.Prop;
import com.hypixel.hytale.builtin.hytalegenerator.workerindexer.WorkerIndexer;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class AssignedPropDistribution extends PropDistribution {
   @Nonnull
   private final PropDistribution propDistribution;
   @Nonnull
   private final Assignments assignments;
   private final boolean isOverrideAllProps;
   @Nonnull
   private final PropDistribution.Context rPropDistributionContext;

   public AssignedPropDistribution(@Nonnull PropDistribution propDistribution, @Nonnull Assignments assignments, boolean isOverrideAllProps) {
      this.propDistribution = propDistribution;
      this.assignments = assignments;
      this.isOverrideAllProps = isOverrideAllProps;
      this.rPropDistributionContext = new PropDistribution.Context();
   }

   @Override
   public void distribute(@NonNullDecl PropDistribution.Context context) {
      this.rPropDistributionContext.assign(context);
      this.rPropDistributionContext.pipe = (position, existingProp, control) -> {
         if (this.isOverrideAllProps || existingProp == EmptyProp.INSTANCE) {
            Prop newProp = this.assignments.propAt(position, WorkerIndexer.Id.MAIN, context.distanceToBiomeEdge);
            context.pipe.accept(position, newProp, control);
         }
      };
      this.propDistribution.distribute(this.rPropDistributionContext);
   }

   @Override
   public void forEachPossibleProp(@NonNullDecl Consumer<Prop> consumer) {
      this.assignments.getAllPossibleProps().forEach(consumer);
      this.propDistribution.forEachPossibleProp(consumer);
   }
}
