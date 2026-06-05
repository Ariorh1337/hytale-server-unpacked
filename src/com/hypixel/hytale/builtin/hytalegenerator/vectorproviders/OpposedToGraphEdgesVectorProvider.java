package com.hypixel.hytale.builtin.hytalegenerator.vectorproviders;

import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class OpposedToGraphEdgesVectorProvider extends VectorProvider {
   @Nonnull
   private static final Vector3d DEFAULT_VECTOR = new Vector3d(0.0, 1.0, 0.0);
   private static final double EPSILON_SQUARED = 1.0E-12;
   @Nonnull
   private final List<Vector3d> rDirections = new ArrayList<>();
   @Nonnull
   private final Vector3d rCandidate = new Vector3d();
   @Nonnull
   private final Vector3d rBest = new Vector3d();
   private int rDirectionCount;
   private double rBestCost;

   @Override
   public void process(@Nonnull VectorProvider.Context context, @Nonnull Vector3d vector_out) {
      GraphSpace.Node graphNode = context.graphNode;
      if (graphNode == null) {
         vector_out.set(DEFAULT_VECTOR);
      } else {
         this.rDirectionCount = 0;
         double nodeX = graphNode.position().x();
         double nodeY = graphNode.position().y();
         double nodeZ = graphNode.position().z();

         for (GraphSpace.Edge edge : graphNode.edges()) {
            GraphSpace.Node otherNode = edge.otherNode(graphNode);
            double dx = otherNode.position().x() - nodeX;
            double dy = otherNode.position().y() - nodeY;
            double dz = otherNode.position().z() - nodeZ;
            double lengthSquared = dx * dx + dy * dy + dz * dz;
            if (!(lengthSquared < 1.0E-12)) {
               double inv = 1.0 / Math.sqrt(lengthSquared);
               this.slot(this.rDirectionCount).set(dx * inv, dy * inv, dz * inv);
               this.rDirectionCount++;
            }
         }

         if (this.rDirectionCount == 0) {
            vector_out.set(DEFAULT_VECTOR);
         } else {
            this.rBestCost = Double.POSITIVE_INFINITY;
            this.rBest.set(DEFAULT_VECTOR);

            for (int i = 0; i < this.rDirectionCount; i++) {
               Vector3d di = this.rDirections.get(i);
               this.tryCandidate(-di.x, -di.y, -di.z);

               for (int j = i + 1; j < this.rDirectionCount; j++) {
                  Vector3d dj = this.rDirections.get(j);
                  double sx = di.x + dj.x;
                  double sy = di.y + dj.y;
                  double sz = di.z + dj.z;
                  if (sx * sx + sy * sy + sz * sz < 1.0E-12) {
                     this.tryPerpendiculars(di);
                  } else {
                     this.tryCandidate(-sx, -sy, -sz);
                  }

                  for (int k = j + 1; k < this.rDirectionCount; k++) {
                     Vector3d dk = this.rDirections.get(k);
                     double ax = di.x - dj.x;
                     double ay = di.y - dj.y;
                     double az = di.z - dj.z;
                     double bx = di.x - dk.x;
                     double by = di.y - dk.y;
                     double bz = di.z - dk.z;
                     double cx = ay * bz - az * by;
                     double cy = az * bx - ax * bz;
                     double cz = ax * by - ay * bx;
                     this.tryCandidate(cx, cy, cz);
                     this.tryCandidate(-cx, -cy, -cz);
                  }
               }
            }

            vector_out.set(this.rBest);
         }
      }
   }

   private void tryCandidate(double x, double y, double z) {
      double lengthSquared = x * x + y * y + z * z;
      if (!(lengthSquared < 1.0E-12)) {
         double inv = 1.0 / Math.sqrt(lengthSquared);
         this.rCandidate.set(x * inv, y * inv, z * inv);
         double worst = Double.NEGATIVE_INFINITY;

         for (int i = 0; i < this.rDirectionCount; i++) {
            Vector3d direction = this.rDirections.get(i);
            double dot = this.rCandidate.x * direction.x + this.rCandidate.y * direction.y + this.rCandidate.z * direction.z;
            if (dot > worst) {
               worst = dot;
            }
         }

         if (worst < this.rBestCost) {
            this.rBestCost = worst;
            this.rBest.set(this.rCandidate);
         }
      }
   }

   private void tryPerpendiculars(@Nonnull Vector3d d) {
      double ax = Math.abs(d.x);
      double ay = Math.abs(d.y);
      double az = Math.abs(d.z);
      double hx;
      double hy;
      double hz;
      if (ax <= ay && ax <= az) {
         hx = 1.0;
         hy = 0.0;
         hz = 0.0;
      } else if (ay <= az) {
         hx = 0.0;
         hy = 1.0;
         hz = 0.0;
      } else {
         hx = 0.0;
         hy = 0.0;
         hz = 1.0;
      }

      double p1x = d.y * hz - d.z * hy;
      double p1y = d.z * hx - d.x * hz;
      double p1z = d.x * hy - d.y * hx;
      this.tryCandidate(p1x, p1y, p1z);
      this.tryCandidate(d.y * p1z - d.z * p1y, d.z * p1x - d.x * p1z, d.x * p1y - d.y * p1x);
   }

   @Nonnull
   private Vector3d slot(int index) {
      if (index < this.rDirections.size()) {
         return this.rDirections.get(index);
      }

      Vector3d vector = new Vector3d();
      this.rDirections.add(vector);
      return vector;
   }
}
