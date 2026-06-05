package com.hypixel.hytale.builtin.hytalegenerator.graph.edgeactions;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.joml.Vector3d;

public class SplitterEdgeAction extends EdgeAction {
   private final int nodeCount;
   private final int segmentCount;
   @Nonnull
   private final GraphSpace.Content content;
   @Nonnull
   private final Vector3d rEdgeVector;
   @Nonnull
   private final Vector3d rNodePosition;

   public SplitterEdgeAction(@Nonnull GraphSpace.Content content, int nodeCount) {
      assert nodeCount >= 0;
      this.nodeCount = nodeCount;
      this.segmentCount = nodeCount + 1;
      this.content = content;
      this.rEdgeVector = new Vector3d();
      this.rNodePosition = new Vector3d();
   }

   @Override
   public void run(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Edge edge, @NonNullDecl Bounds3d bounds) {
      if (this.nodeCount != 0) {
         this.rEdgeVector.set(edge.nodeB().position()).sub(edge.nodeA().position());
         double edgeLength = this.rEdgeVector.length();
         double segmentLength = edgeLength / this.segmentCount;
         this.rEdgeVector.normalize(segmentLength);
         this.rNodePosition.set(edge.nodeA().position());
         GraphSpace.Node previousNode = edge.nodeA();

         for (int i = 0; i < this.nodeCount; i++) {
            this.rNodePosition.add(this.rEdgeVector);
            Vector3d taskPosition = new Vector3d(this.rNodePosition);
            boolean isLastNode = i == this.nodeCount - 1;
            graphSpace.schedule(() -> {
               GraphSpace.Node newNode = graphSpace.createNode(taskPosition);
               newNode.setContent(this.content);
               graphSpace.getOrCreateEdge(previousNode, newNode);
               if (isLastNode) {
                  graphSpace.getOrCreateEdge(newNode, edge.nodeB());
               }
            });
         }

         graphSpace.scheduleEdgeDeletion(edge);
      }
   }

   @Override
   public double getReadRange(double longestConnection) {
      return 0.0;
   }

   @Override
   public double getConnectionRangeIncrement() {
      return 0.0;
   }

   @Override
   public void viewAllPossibleContent(@NonNullDecl Consumer<GraphSpace.Content> consumer) {
      consumer.accept(this.content);
   }
}
