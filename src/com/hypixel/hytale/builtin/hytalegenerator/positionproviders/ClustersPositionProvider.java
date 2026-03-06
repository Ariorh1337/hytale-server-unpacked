package com.hypixel.hytale.builtin.hytalegenerator.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ClustersPositionProvider extends PositionProvider {
   @Nonnull
   private final PositionProvider clusterPositions;
   @Nonnull
   private final PositionProvider distributorPositions;
   @Nonnull
   private final Bounds3d clusterBounds;
   private final Bounds3d clusterBoundsFlipped;
   @Nonnull
   private final Bounds3d rDistributionBounds;
   @Nonnull
   private final PositionProvider.Context rDistributionContext;
   @Nonnull
   private final Bounds3d rClusterBounds;
   @Nonnull
   private final PositionProvider.Context rClusterContext;

   public ClustersPositionProvider(@Nonnull PositionProvider clusterPositions, @Nonnull PositionProvider distributorPositions, @Nonnull Bounds3d clusterBounds) {
      this.clusterPositions = clusterPositions;
      this.distributorPositions = distributorPositions;
      this.clusterBounds = clusterBounds.clone();
      this.clusterBoundsFlipped = this.clusterBounds.clone();
      this.clusterBoundsFlipped.flipOnOriginPoint();
      this.rDistributionBounds = new Bounds3d();
      this.rDistributionContext = new PositionProvider.Context();
      this.rClusterBounds = new Bounds3d();
      this.rClusterContext = new PositionProvider.Context();
   }

   @Override
   public void generate(@NonNullDecl PositionProvider.Context context) {
      this.rDistributionBounds.assign(context.bounds);
      this.rDistributionBounds.stack(this.clusterBoundsFlipped);
      this.rDistributionContext.assign(context);
      this.rDistributionContext.bounds = this.rDistributionBounds;
      this.rDistributionContext.pipe = (clusterAnchor, distributionControl) -> {
         this.rClusterBounds.assign(this.clusterBounds);
         this.rClusterBounds.offset(clusterAnchor);
         this.rClusterBounds.intersect(context.bounds);
         this.rClusterContext.assign(context);
         this.rClusterContext.bounds = this.rClusterBounds;
         this.rClusterContext.anchor = clusterAnchor;
         this.rClusterContext.pipe = (position, control) -> {
            if (control.stop) {
               distributionControl.stop = true;
            } else {
               context.pipe.accept(position, control);
            }
         };
         this.clusterPositions.generate(this.rClusterContext);
      };
      this.distributorPositions.generate(this.rDistributionContext);
   }
}
