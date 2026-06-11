package com.hypixel.hytale.builtin.hytalegenerator.graph;

import com.hypixel.hytale.builtin.hytalegenerator.VectorUtil;
import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.pipe.Control;
import com.hypixel.hytale.builtin.hytalegenerator.pipe.Pipe;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.propdistributions.PropDistribution;
import com.hypixel.hytale.component.spatial.KDTree;
import com.hypixel.hytale.component.spatial.SpatialData;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class GraphSpace {
   private int nextId = 0;
   @Nonnull
   private final List<GraphSpace.Node> nodes = new ArrayList<>();
   @Nonnull
   private final Map<Integer, GraphSpace.Edge> edges = new HashMap<>();
   @Nonnull
   private final List<Runnable> taskQueue = new ArrayList<>();
   @Nonnull
   private final KDTree<GraphSpace.Node> kdTree = new KDTree<>(n -> true);
   @Nonnull
   private final SpatialData<GraphSpace.Node> spacialData = new SpatialData<>();
   private volatile boolean dirty = true;

   public void viewNodes(@Nonnull Bounds3d bounds, @Nonnull List<GraphSpace.Node> results) {
      this.rebuildIfDirty();
      Vector3d inclusiveMax = new Vector3d(bounds.max);
      VectorUtil.nextDown(inclusiveMax);
      this.kdTree.collectBox(bounds.min, inclusiveMax, results);
   }

   public void viewNodes(@Nonnull Vector3dc center, double radius, @Nonnull List<GraphSpace.Node> results) {
      this.rebuildIfDirty();
      Vector3d centerMutable = new Vector3d(center);
      this.kdTree.collect(centerMutable, Math.nextDown(radius), results);
   }

   public void viewNodes(@Nonnull List<GraphSpace.Node> results) {
      this.viewNodes(Bounds3d.ALL, results);
   }

   public void viewEdges(@Nonnull Pipe.One<GraphSpace.Edge> pipe) {
      Control control = new Control();

      for (GraphSpace.Edge edge : new ArrayList<>(this.edges.values())) {
         if (control.stop) {
            return;
         }

         pipe.accept(edge, control);
      }
   }

   public void schedule(@Nonnull Runnable task) {
      this.taskQueue.add(task);
   }

   public void scheduleNodeCreation(@Nonnull Vector3d position) {
      this.taskQueue.add(() -> this.createNode(position));
   }

   public void scheduleEdgeCreation(@Nonnull GraphSpace.Node nodeA, @Nonnull GraphSpace.Node nodeB) {
      this.taskQueue.add(() -> this.getOrCreateEdge(nodeA, nodeB));
   }

   public void scheduleMoveNode(@Nonnull GraphSpace.Node node, @Nonnull Vector3dc position) {
      this.taskQueue.add(() -> this.moveNode(node, position));
   }

   public void scheduleNodeDeletion(@Nonnull GraphSpace.Node node) {
      assert node.graphSpace == this;
      this.taskQueue.add(() -> this.deleteNode(node));
   }

   public void scheduleEdgeDeletion(@Nonnull GraphSpace.Node nodeA, @Nonnull GraphSpace.Node nodeB) {
      int edgeId = GraphSpace.Edge.createId(nodeA, nodeB);
      GraphSpace.Edge edge = this.edges.get(edgeId);
      if (edge != null) {
         this.scheduleEdgeDeletion(edge);
      }
   }

   public void scheduleEdgeDeletion(@Nonnull GraphSpace.Edge edge) {
      this.taskQueue.add(() -> this.deleteEdge(edge));
   }

   @Nonnull
   public GraphSpace.Node createNode(@Nonnull Vector3d position) {
      GraphSpace.Node node = new GraphSpace.Node(this, this.nextId++);
      node.move(position);
      this.nodes.add(node);
      this.dirty = true;
      return node;
   }

   @Nullable
   public GraphSpace.Edge getOrCreateEdge(@Nonnull GraphSpace.Node nodeA, @Nonnull GraphSpace.Node nodeB) {
      if (!nodeA.isDiscarded() && !nodeB.isDiscarded()) {
         assert nodeA.graphSpace == this && nodeB.graphSpace == this;
         assert nodeA.id != nodeB.id;
         GraphSpace.Edge edge = this.edges.get(GraphSpace.Edge.createId(nodeA, nodeB));
         if (edge != null) {
            return edge;
         }

         edge = new GraphSpace.Edge(nodeA, nodeB);
         this.edges.put(edge.id, edge);
         nodeA.edges.add(edge);
         nodeB.edges.add(edge);
         return edge;
      } else {
         return null;
      }
   }

   public void moveNode(@Nonnull GraphSpace.Node node, @Nonnull Vector3dc position) {
      node.move(position);
      this.dirty = true;
   }

   @Nullable
   public GraphSpace.Edge getEdge(@Nonnull GraphSpace.Node nodeA, @Nonnull GraphSpace.Node nodeB) {
      assert nodeA.graphSpace == this && nodeB.graphSpace == this;
      assert nodeA.id != nodeB.id;
      return this.edges.get(GraphSpace.Edge.createId(nodeA, nodeB));
   }

   public void deleteNode(@Nonnull GraphSpace.Node node) {
      assert node.isParentSpace(this);
      this.nodes.remove(node);
      node.discard();
      this.dirty = true;
   }

   public void deleteEdge(@Nonnull GraphSpace.Node nodeA, @Nonnull GraphSpace.Node nodeB) {
      int edgeId = GraphSpace.Edge.createId(nodeA, nodeB);
      GraphSpace.Edge edge = this.edges.get(edgeId);
      if (edge != null) {
         edge.nodeA.edges.remove(edge);
         edge.nodeB.edges.remove(edge);
         this.edges.remove(edge.id);
      }
   }

   public void deleteEdge(@Nonnull GraphSpace.Edge edge) {
      edge = this.edges.get(edge.id);
      if (edge != null) {
         edge.nodeA.edges.remove(edge);
         edge.nodeB.edges.remove(edge);
         this.edges.remove(edge.id);
      }
   }

   public void processTaskQueue() {
      for (Runnable task : this.taskQueue) {
         task.run();
      }

      this.taskQueue.clear();
      this.dirty = true;
   }

   private void rebuildIfDirty() {
      if (this.dirty) {
         this.dirty = false;
         this.spacialData.clear();
         this.spacialData.addCapacity(this.nodes.size());

         for (GraphSpace.Node node : this.nodes) {
            this.spacialData.append(node.position, node);
         }

         this.kdTree.rebuild(this.spacialData);
      }
   }

   public static class Content {
      public static final int DEFAULT_TAG = toIntId("");
      public static final GraphSpace.Content DEFAULT = new GraphSpace.Content(List.of(), List.of(), List.of(), List.of(), new int[]{DEFAULT_TAG});
      @Nonnull
      public final IntSet tagSet;
      @Nonnull
      private final GraphSpace.IndexedContent<GraphSpace.DensityContent> densityContent;
      @Nonnull
      private final GraphSpace.IndexedContent<GraphSpace.MaterialContent> materialContent;
      @Nonnull
      private final GraphSpace.IndexedContent<GraphSpace.PropDistributionContent> propDistributionContent;
      @Nonnull
      private final GraphSpace.IndexedContent<GraphSpace.PositionsContent> positionsContent;

      public Content(
         @Nonnull List<GraphSpace.ContentEntry<GraphSpace.DensityContent>> densityContent,
         @Nonnull List<GraphSpace.ContentEntry<GraphSpace.MaterialContent>> materialContent,
         @Nonnull List<GraphSpace.ContentEntry<GraphSpace.PropDistributionContent>> propDistributionContent,
         @Nonnull List<GraphSpace.ContentEntry<GraphSpace.PositionsContent>> positionsContent,
         @Nonnull int[] tags
      ) {
         this.densityContent = new GraphSpace.IndexedContent<>(densityContent);
         this.materialContent = new GraphSpace.IndexedContent<>(materialContent);
         this.propDistributionContent = new GraphSpace.IndexedContent<>(propDistributionContent);
         this.positionsContent = new GraphSpace.IndexedContent<>(positionsContent);
         this.tagSet = new IntOpenHashSet(tags);
      }

      @Nonnull
      public GraphSpace.IndexedContent<GraphSpace.DensityContent> getDensityContent() {
         return this.densityContent;
      }

      @Nonnull
      public GraphSpace.IndexedContent<GraphSpace.MaterialContent> getMaterialContent() {
         return this.materialContent;
      }

      @Nonnull
      public GraphSpace.IndexedContent<GraphSpace.PropDistributionContent> getPropDistributionContent() {
         return this.propDistributionContent;
      }

      @Nonnull
      public GraphSpace.IndexedContent<GraphSpace.PositionsContent> getPositionsContent() {
         return this.positionsContent;
      }

      public static int toIntId(@Nonnull String name) {
         return name.hashCode();
      }
   }

   public record ContentEntry<C>(int id, @Nonnull C content) {
   }

   public static class DensityContent {
      @Nonnull
      public final Density density;
      public final double range;
      public final double rangeSquared;

      public DensityContent(@Nonnull Density density, double range) {
         assert range >= 0.0;
         this.density = density;
         this.range = range;
         this.rangeSquared = range * range;
      }
   }

   public static class Edge {
      private final int id;
      @Nonnull
      private final GraphSpace.Node nodeA;
      @Nonnull
      private final GraphSpace.Node nodeB;

      private Edge(@Nonnull GraphSpace.Node nodeA, @Nonnull GraphSpace.Node nodeB) {
         assert nodeA.graphSpace == nodeB.graphSpace;
         assert nodeA.id != nodeB.id;
         GraphSpace.OrderedNodePair nodePair = new GraphSpace.OrderedNodePair(nodeA, nodeB);
         this.nodeA = nodePair.lowNode;
         this.nodeB = nodePair.highNode;
         this.id = createId(nodeA, nodeB);
      }

      public int id() {
         return this.id;
      }

      @Nonnull
      public GraphSpace.Node nodeA() {
         return this.nodeA;
      }

      @Nonnull
      public GraphSpace.Node nodeB() {
         return this.nodeB;
      }

      @Nonnull
      public GraphSpace.Node otherNode(@Nonnull GraphSpace.Node node) {
         assert this.nodeA == node || this.nodeB == node;
         return this.nodeA == node ? this.nodeB : this.nodeA;
      }

      @Override
      public boolean equals(Object o) {
         return o instanceof GraphSpace.Edge other ? this.id == other.id : false;
      }

      @Override
      public int hashCode() {
         return this.id;
      }

      public static int createId(@Nonnull GraphSpace.Node nodeA, @Nonnull GraphSpace.Node nodeB) {
         int high = Math.max(nodeA.id, nodeB.id);
         int low = Math.min(nodeA.id, nodeB.id);
         int id = low;
         return id | high << 16;
      }
   }

   public static class IndexedContent<C> {
      @Nonnull
      private final List<GraphSpace.ContentEntry<C>> content;
      private int hotContentId;
      @Nullable
      private C hotContent;

      private IndexedContent(@Nonnull List<GraphSpace.ContentEntry<C>> content) {
         this.content = new ArrayList<>(content);
      }

      @Nullable
      public C get(int contentId) {
         if (this.hotContent != null && contentId == this.hotContentId) {
            return this.hotContent;
         }

         for (GraphSpace.ContentEntry<C> entry : this.content) {
            if (entry.id == contentId) {
               this.hotContentId = contentId;
               this.hotContent = entry.content;
               return entry.content;
            }
         }

         return null;
      }

      @Nonnull
      public List<GraphSpace.ContentEntry<C>> getAll() {
         return this.content;
      }
   }

   public static class MaterialContent {
      @Nonnull
      public final MaterialProvider<Material> materialProvider;
      public final double range;
      public final double rangeSquared;

      public MaterialContent(@Nonnull MaterialProvider<Material> materialProvider, double range) {
         assert range >= 9.0;
         this.materialProvider = materialProvider;
         this.range = range;
         this.rangeSquared = range * range;
      }
   }

   public static class Node {
      public static final int VOID_ID = -1;
      private final int id;
      private int hashCode;
      private boolean isHashCodeDirty;
      @Nonnull
      private final Vector3d position;
      @Nullable
      private GraphSpace graphSpace;
      @Nonnull
      private final List<GraphSpace.Edge> edges;
      @Nonnull
      private GraphSpace.Content content;

      private Node(@Nonnull GraphSpace graphSpace, int id) {
         this.id = id;
         this.hashCode = 0;
         this.isHashCodeDirty = true;
         this.graphSpace = graphSpace;
         this.position = new Vector3d();
         this.edges = new ArrayList<>(2);
         this.content = GraphSpace.Content.DEFAULT;
      }

      public int id() {
         return this.id;
      }

      @Override
      public int hashCode() {
         if (this.isHashCodeDirty) {
            this.hashCode = this.position.hashCode();
            this.isHashCodeDirty = false;
         }

         return this.hashCode;
      }

      @Nonnull
      public GraphSpace.Content content() {
         return this.content;
      }

      @Nonnull
      public Vector3dc position() {
         return this.position;
      }

      @Nonnull
      public List<GraphSpace.Edge> edges() {
         return this.edges;
      }

      public void viewConnections(@Nonnull Pipe.One<GraphSpace.Edge> pipe) {
         Control control = new Control();

         for (GraphSpace.Edge edge : this.edges) {
            if (control.stop) {
               return;
            }

            pipe.accept(edge, control);
         }
      }

      public boolean isConnected(@Nonnull GraphSpace.Node other) {
         if (other.graphSpace != this.graphSpace) {
            return false;
         }

         for (GraphSpace.Edge edge : this.edges) {
            if (edge.otherNode(this).id == other.id) {
               return true;
            }
         }

         return false;
      }

      public boolean isDiscarded() {
         return this.graphSpace == null;
      }

      public boolean isParentSpace(@Nonnull GraphSpace graphSpace) {
         return this.graphSpace == graphSpace;
      }

      public int getEdgesCount() {
         return this.edges.size();
      }

      public void setContent(@Nonnull GraphSpace.Content content) {
         this.content = content;
         this.isHashCodeDirty = true;
      }

      public void move(@Nonnull Vector3dc position) {
         this.position.set(position);
         this.isHashCodeDirty = true;
      }

      public void disconnectAll() {
         assert this.graphSpace != null;

         for (GraphSpace.Edge edge : this.edges) {
            this.graphSpace.edges.remove(edge.id);
            edge.otherNode(this).edges.remove(edge);
         }

         this.edges.clear();
      }

      private void discard() {
         this.disconnectAll();
         this.graphSpace = null;
      }
   }

   public static class OrderedNodePair {
      @Nonnull
      public GraphSpace.Node highNode;
      @Nonnull
      public GraphSpace.Node lowNode;

      public OrderedNodePair(@Nonnull GraphSpace.Node nodeA, @Nonnull GraphSpace.Node nodeB) {
         assert nodeA != nodeB && nodeA.graphSpace == nodeB.graphSpace;
         if (nodeA.id > nodeB.id) {
            this.highNode = nodeA;
            this.lowNode = nodeB;
         } else {
            this.highNode = nodeB;
            this.lowNode = nodeA;
         }
      }
   }

   public static class PositionsContent {
      @Nonnull
      public final PositionProvider positions;
      public final double range;
      public final double rangeSquared;

      public PositionsContent(@Nonnull PositionProvider positions, double range) {
         assert range >= 9.0;
         this.positions = positions;
         this.range = range;
         this.rangeSquared = range * range;
      }
   }

   public static class PropDistributionContent {
      @Nonnull
      public final PropDistribution propDistribution;
      public final double range;
      public final double rangeSquared;

      public PropDistributionContent(@Nonnull PropDistribution propDistribution, double range) {
         assert range >= 9.0;
         this.propDistribution = propDistribution;
         this.range = range;
         this.rangeSquared = range * range;
      }
   }
}
