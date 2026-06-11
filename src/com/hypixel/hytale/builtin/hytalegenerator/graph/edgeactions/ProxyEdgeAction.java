package com.hypixel.hytale.builtin.hytalegenerator.graph.edgeactions;

import com.hypixel.hytale.builtin.hytalegenerator.VectorUtil;
import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.graph.contentsuppliers.ContentSupplier;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.NodeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.rng.RngField;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.VectorProvider;
import com.hypixel.hytale.math.util.FastRandom;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.joml.Vector3d;

public class ProxyEdgeAction extends EdgeAction {
   @Nonnull
   private final NodeSelector hostSelector;
   @Nonnull
   private final VectorProvider normalVectorProvider;
   @Nonnull
   private final Density angleToNormalDensity;
   @Nonnull
   private final Density spinAngleDensity;
   @Nonnull
   private final Density distanceDensity;
   @Nonnull
   private final ContentSupplier proxyContent;
   private final double maxProxyDistance;
   @Nonnull
   private final FastRandom random;
   @Nonnull
   private final RngField rngField;

   public ProxyEdgeAction(
      @Nonnull NodeSelector hostSelector,
      @Nonnull VectorProvider normalVectorProvider,
      @Nonnull Density angleToNormalDensity,
      @Nonnull Density spinAngleDensity,
      @Nonnull Density proxyDistanceDensity,
      @Nonnull ContentSupplier proxyContent,
      double maxProxyDistance,
      int seed
   ) {
      assert maxProxyDistance >= 0.0;
      this.hostSelector = hostSelector;
      this.normalVectorProvider = normalVectorProvider;
      this.angleToNormalDensity = angleToNormalDensity;
      this.spinAngleDensity = spinAngleDensity;
      this.distanceDensity = proxyDistanceDensity;
      this.proxyContent = proxyContent;
      this.maxProxyDistance = maxProxyDistance;
      this.random = new FastRandom();
      this.rngField = new RngField(seed);
   }

   @Override
   public void run(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Edge edge, @NonNullDecl Bounds3d bounds) {
      GraphSpace.Node nodeA = edge.nodeA();
      GraphSpace.Node nodeB = edge.nodeB();
      boolean isHostA = this.hostSelector.isSelected(graphSpace, nodeA);
      boolean isHostB = this.hostSelector.isSelected(graphSpace, nodeB);
      if (isHostA && isHostB) {
         graphSpace.schedule(() -> {
            GraphSpace.Edge otherEdge = this.createProxy(edge, nodeA, nodeB, graphSpace);
            this.createProxy(otherEdge, nodeB, otherEdge.otherNode(nodeB), graphSpace);
         });
      } else if (isHostA) {
         graphSpace.schedule(() -> this.createProxy(edge, nodeA, nodeB, graphSpace));
      } else if (isHostB) {
         graphSpace.schedule(() -> this.createProxy(edge, nodeB, nodeA, graphSpace));
      }
   }

   @Nonnull
   private GraphSpace.Edge createProxy(
      @Nonnull GraphSpace.Edge initialEdge, @Nonnull GraphSpace.Node hostNode, @Nonnull GraphSpace.Node otherNode, @Nonnull GraphSpace graphSpace
   ) {
      Vector3d normalVector = new Vector3d();
      this.normalVectorProvider.process(new VectorProvider.Context(new Vector3d(hostNode.position()), null, hostNode), normalVector);
      if (normalVector.x == 0.0 && normalVector.y == 0.0 && normalVector.z == 0.0) {
         normalVector.set(0.0, 1.0, 0.0);
      }

      Density.Context densityContext = new Density.Context();
      densityContext.position.set(otherNode.position());
      densityContext.densityAnchor = new Vector3d(hostNode.position());
      densityContext.graphNode = hostNode;
      double angleToNormal = Math.toRadians(this.angleToNormalDensity.process(densityContext));
      double spinAngle = Math.toRadians(this.spinAngleDensity.process(densityContext));
      double distance = Math.min(this.distanceDensity.process(densityContext), this.maxProxyDistance);
      Vector3d vectorToNeighbour = new Vector3d(otherNode.position());
      vectorToNeighbour.sub(hostNode.position());
      Vector3d proxyPosition = new Vector3d(normalVector);
      Vector3d spinAxis = new Vector3d();
      vectorToNeighbour.cross(normalVector, spinAxis);
      if (VectorUtil.isZero(spinAxis)) {
         VectorUtil.orthogonalVector(normalVector, spinAxis);
         spinAxis.normalize();
         int localSeed = this.rngField
            .get(
               hostNode.position().x() * 0.5 + otherNode.position().x() * 0.5,
               hostNode.position().y() * 0.5 + otherNode.position().y() * 0.5,
               hostNode.position().z() * 0.5 + otherNode.position().z() * 0.5
            );
         this.random.setSeed(localSeed);
         double randomAngle = this.random.nextDouble() * Math.PI * 2.0;
         spinAxis.rotateAxis(randomAngle, normalVector.x, normalVector.y, normalVector.z);
      } else {
         spinAxis.normalize();
      }

      proxyPosition.rotateAxis(-angleToNormal, spinAxis.x, spinAxis.y, spinAxis.z);
      proxyPosition.rotateAxis(spinAngle, normalVector.x, normalVector.y, normalVector.z);
      proxyPosition.normalize(distance);
      proxyPosition.add(hostNode.position());
      graphSpace.deleteEdge(initialEdge);
      GraphSpace.Node proxyNode = graphSpace.createNode(proxyPosition);
      GraphSpace.Content content = this.proxyContent.get(proxyNode);
      proxyNode.setContent(content);
      graphSpace.getOrCreateEdge(hostNode, proxyNode);
      GraphSpace.Edge otherEdge = graphSpace.getOrCreateEdge(otherNode, proxyNode);
      assert otherEdge != null;
      return otherEdge;
   }

   @Override
   public double getReadRange(double longestConnection) {
      return longestConnection + this.hostSelector.getReadRange(longestConnection);
   }

   @Override
   public double getConnectionRangeIncrement() {
      return this.maxProxyDistance * 2.0;
   }

   @Override
   public void viewAllPossibleContent(@NonNullDecl Consumer<GraphSpace.Content> consumer) {
      this.proxyContent.viewAllPossibleContent(consumer);
   }
}
