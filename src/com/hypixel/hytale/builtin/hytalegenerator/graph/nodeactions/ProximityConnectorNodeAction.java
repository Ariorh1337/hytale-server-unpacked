package com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.NodeSelector;
import com.hypixel.hytale.builtin.hytalegenerator.rng.RngField;
import com.hypixel.hytale.builtin.hytalegenerator.rng.RngUtil;
import com.hypixel.hytale.math.util.FastRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ProximityConnectorNodeAction extends NodeAction {
   public static final int NO_CAP = -1;
   @Nonnull
   private final NodeSelector nodeSelector;
   private final double range;
   private final int cap;
   @Nonnull
   private final RngField rngField;
   @Nonnull
   private final FastRandom random;
   @Nonnull
   private final List<GraphSpace.Node> rResultList;

   public ProximityConnectorNodeAction(double range, @Nonnull NodeSelector nodeSelector, int seed, int cap) {
      assert range >= 0.0 && cap >= -1;
      this.range = range;
      this.nodeSelector = nodeSelector;
      this.cap = cap;
      this.rngField = new RngField(seed);
      this.random = new FastRandom();
      this.rResultList = new ArrayList<>();
   }

   @Override
   public void run(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Node node, @NonNullDecl Bounds3d bounds) {
      this.rResultList.clear();
      if (this.cap < 0) {
         graphSpace.viewNodes(node.position(), this.range, this.rResultList);

         for (GraphSpace.Node neighbour : this.rResultList) {
            if (this.nodeSelector.isSelected(graphSpace, neighbour) && node != neighbour) {
               graphSpace.scheduleEdgeCreation(node, neighbour);
            }
         }

         this.rResultList.clear();
      } else {
         List<GraphSpace.Node> nodeList = new ArrayList<>();
         graphSpace.viewNodes(node.position(), this.range, this.rResultList);

         for (GraphSpace.Node neighbour : this.rResultList) {
            if (this.nodeSelector.isSelected(graphSpace, neighbour) && node != neighbour) {
               nodeList.add(neighbour);
            }
         }

         this.rResultList.clear();
         this.random.setSeed(this.rngField.get(node.position()));
         List<GraphSpace.Node> cappedList = new ArrayList<>(this.cap);
         nodeList.sort(Comparator.comparingInt(GraphSpace.Node::hashCode));
         RngUtil.pickElements(this.random, this.cap, nodeList, cappedList);

         for (GraphSpace.Node neighbour : cappedList) {
            graphSpace.scheduleEdgeCreation(node, neighbour);
         }
      }
   }

   @Override
   public double getReadRange(double longestConnection) {
      return this.range + this.nodeSelector.getReadRange(longestConnection);
   }

   @Override
   public double getCausalRangeIncrement() {
      return this.range;
   }

   @Override
   public void viewAllPossibleContent(@NonNullDecl Consumer<GraphSpace.Content> consumer) {
   }
}
