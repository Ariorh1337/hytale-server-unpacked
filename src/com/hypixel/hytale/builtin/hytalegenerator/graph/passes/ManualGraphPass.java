package com.hypixel.hytale.builtin.hytalegenerator.graph.passes;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.joml.Vector3d;

public class ManualGraphPass extends GraphPass {
   @Nonnull
   private final List<ManualGraphPass.NodeEntry> nodeEntries;

   public ManualGraphPass(@Nonnull List<ManualGraphPass.NodeEntry> nodeEntries, @Nonnull String label) {
      super(label);
      this.nodeEntries = new ArrayList<>(nodeEntries);
   }

   @Override
   public void run(@NonNullDecl GraphSpace graphSpace, @NonNullDecl Bounds3d runBounds) {
      Map<String, GraphSpace.Node> nameNodeMap = new HashMap<>();
      List<ManualGraphPass.ConnectionEntry> connectionEntries = new ArrayList<>();

      for (ManualGraphPass.NodeEntry nodeEntry : this.nodeEntries) {
         if (runBounds.contains(nodeEntry.position)) {
            GraphSpace.Node node = graphSpace.createNode(nodeEntry.position);
            nameNodeMap.put(nodeEntry.name, node);

            for (String otherNodeName : nodeEntry.connections) {
               connectionEntries.add(new ManualGraphPass.ConnectionEntry(nodeEntry.name, otherNodeName));
            }

            node.setContent(nodeEntry.content);
         }
      }

      for (ManualGraphPass.ConnectionEntry connection : connectionEntries) {
         GraphSpace.Node node0 = nameNodeMap.get(connection.node0);
         GraphSpace.Node node1 = nameNodeMap.get(connection.node1);
         if (node1 != null) {
            graphSpace.scheduleEdgeCreation(node0, node1);
         }
      }
   }

   @NonNullDecl
   @Override
   public Bounds3d getReadBounds(double longestConnection) {
      return Bounds3d.ZERO;
   }

   @Override
   public double getConnectionRangeIncrement() {
      return 100.0;
   }

   @Override
   public void viewAllPossibleContent(@NonNullDecl Consumer<GraphSpace.Content> consumer) {
      for (ManualGraphPass.NodeEntry nodeEntry : this.nodeEntries) {
         consumer.accept(nodeEntry.content);
      }
   }

   public static class ConnectionEntry {
      private final String node0;
      private final String node1;

      public ConnectionEntry(@Nonnull String node0, @Nonnull String node1) {
         this.node0 = node0;
         this.node1 = node1;
      }
   }

   public static class NodeEntry {
      private final Vector3d position;
      private final GraphSpace.Content content;
      private final String name;
      private final String[] connections;

      public NodeEntry(@Nonnull Vector3d position, @Nonnull GraphSpace.Content content, @Nonnull String name, @Nonnull String[] connections) {
         this.position = position;
         this.content = content;
         this.name = name;
         this.connections = connections;
      }
   }
}
