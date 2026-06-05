package com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.graph.contentsuppliers.ContentSupplier;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.VectorProvider;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.joml.Vector3d;

public class SpawnerNodeAction extends NodeAction {
   @Nonnull
   private final VectorProvider offsetProvider;
   @Nonnull
   private final PositionProvider positionProvider;
   @Nonnull
   private final ContentSupplier contentSupplier;
   @Nonnull
   private final Bounds3d clusterBounds;
   private final boolean isCreateEdges;
   private final double maxOffsetExclusiveSqr;
   private final double causalRange;
   @Nonnull
   private final Bounds3d rLocalClusterBounds;
   @Nonnull
   private final Vector3d rClusterPosition;
   @Nonnull
   private final PositionProvider.Context rPositionsContext;

   public SpawnerNodeAction(
      @Nonnull VectorProvider offsetProvider,
      @Nonnull PositionProvider positionProvider,
      @Nonnull ContentSupplier contentSupplier,
      @Nonnull Bounds3d clusterBounds,
      double maxOffsetExclusive,
      boolean isCreateEdges
   ) {
      assert maxOffsetExclusive >= 0.0;
      this.offsetProvider = offsetProvider;
      this.positionProvider = positionProvider;
      this.contentSupplier = contentSupplier;
      this.clusterBounds = clusterBounds;
      this.maxOffsetExclusiveSqr = maxOffsetExclusive * maxOffsetExclusive;
      this.causalRange = maxOffsetExclusive + clusterBounds.maxRangeOrthogonal();
      this.isCreateEdges = isCreateEdges;
      this.rLocalClusterBounds = new Bounds3d();
      this.rClusterPosition = new Vector3d();
      this.rPositionsContext = new PositionProvider.Context();
   }

   @Override
   public void run(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Node node, @NonNullDecl Bounds3d bounds) {
      this.offsetProvider.process(new VectorProvider.Context(new Vector3d(node.position()), null, node), this.rClusterPosition);
      if (!(this.rClusterPosition.lengthSquared() >= this.maxOffsetExclusiveSqr)) {
         this.rClusterPosition.add(node.position());
         this.rLocalClusterBounds.assign(this.clusterBounds);
         this.rLocalClusterBounds.offset(this.rClusterPosition);
         this.rPositionsContext.bounds.assign(this.rLocalClusterBounds);
         this.rPositionsContext.graphNode = node;
         this.rPositionsContext.anchor = this.rClusterPosition;
         this.rPositionsContext.pipe = (position, control) -> {
            assert this.rLocalClusterBounds.contains(position);
            if (bounds.contains(position)) {
               Vector3d taskPosition = new Vector3d(position);
               graphSpace.schedule(() -> {
                  GraphSpace.Node newNode = graphSpace.createNode(taskPosition);
                  newNode.setContent(this.contentSupplier.get(newNode));
                  if (this.isCreateEdges) {
                     graphSpace.getOrCreateEdge(node, newNode);
                  }
               });
            }
         };
         this.positionProvider.generate(this.rPositionsContext);
      }
   }

   @Override
   public double getReadRange(double longestConnection) {
      return 0.0;
   }

   @Override
   public double getCausalRangeIncrement() {
      return this.causalRange;
   }

   @Override
   public void viewAllPossibleContent(@NonNullDecl Consumer<GraphSpace.Content> consumer) {
      this.contentSupplier.viewAllPossibleContent(consumer);
   }
}
