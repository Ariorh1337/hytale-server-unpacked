package com.hypixel.hytale.builtin.hytalegenerator.propdistributions;

import com.hypixel.hytale.builtin.hytalegenerator.VectorUtil;
import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphGenerator;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GridGraphCache;
import com.hypixel.hytale.builtin.hytalegenerator.pipe.Control;
import com.hypixel.hytale.builtin.hytalegenerator.pipe.Pipe;
import com.hypixel.hytale.builtin.hytalegenerator.props.Prop;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;

public class GraphPropDistribution extends PropDistribution {
   @Nonnull
   private static final Comparator<GraphSpace.Node> nodeComparator = Comparator.comparingInt(GraphSpace.Node::hashCode);
   @Nonnull
   private final GridGraphCache gridGraphCache;
   @Nonnull
   private final GraphGenerator graphGenerator;
   private final int contentLayerId;
   private final double contentRange;
   @Nonnull
   private final List<GraphSpace.Node> rResultList;
   @Nonnull
   private final Bounds3d rGraphBounds;
   @Nonnull
   private final Bounds3d rIntersectingCellBounds;
   @Nonnull
   private final Bounds3d rContentBounds;
   @Nonnull
   private final Vector3d rBoundsMaxInclusive;
   @Nonnull
   private final Vector3i rMinCellIndex;
   @Nonnull
   private final Vector3i rMaxCellIndex;
   @Nonnull
   private final Vector3i rCellIndex;
   @Nonnull
   private final GridGraphCache.Result rResult;
   @Nonnull
   private final Control rControl;
   @Nonnull
   private final PropDistribution.Context rChildContext;
   @Nonnull
   private final Vector3d rAnchor;
   private double rContentRangeSquared;
   @Nullable
   private Vector3dc rNodePosition;
   @Nullable
   private Pipe.Two<Vector3d, Prop> rContextPipe;
   @Nonnull
   private final Pipe.Two<Vector3d, Prop> rChildPipe;

   public GraphPropDistribution(@Nonnull GraphGenerator graphGenerator, int contentLayerId, @Nonnull Vector3dc cacheCellSize, int cacheCapacity) {
      assert GridGraphCache.isValidCellSize(cacheCellSize);
      assert cacheCapacity >= 0;
      this.gridGraphCache = new GridGraphCache(cacheCellSize, cacheCapacity);
      this.graphGenerator = graphGenerator;
      this.contentLayerId = contentLayerId;
      this.contentRange = graphGenerator.getPropDistributionRadius(contentLayerId);
      this.rResultList = new ArrayList<>();
      this.rGraphBounds = new Bounds3d();
      this.rIntersectingCellBounds = new Bounds3d();
      this.rContentBounds = new Bounds3d();
      this.rBoundsMaxInclusive = new Vector3d();
      this.rAnchor = new Vector3d();
      this.rMinCellIndex = new Vector3i();
      this.rMaxCellIndex = new Vector3i();
      this.rCellIndex = new Vector3i();
      this.rResult = new GridGraphCache.Result();
      this.rControl = new Control();
      this.rChildContext = new PropDistribution.Context();
      this.rChildPipe = this::pipe;
   }

   @Override
   public void distribute(@NonNullDecl PropDistribution.Context context) {
      this.rGraphBounds.assign(context.bounds).expand(this.contentRange);
      this.rBoundsMaxInclusive.set(this.rGraphBounds.max);
      VectorUtil.nextDown(this.rBoundsMaxInclusive);
      this.gridGraphCache.toCellIndex(this.rGraphBounds.min, this.rMinCellIndex);
      this.gridGraphCache.toCellIndex(this.rBoundsMaxInclusive, this.rMaxCellIndex);
      this.rMaxCellIndex.x++;
      this.rMaxCellIndex.y++;
      this.rMaxCellIndex.z++;
      this.rControl.reset();
      this.rCellIndex.set(this.rMinCellIndex);

      while (this.rCellIndex.x < this.rMaxCellIndex.x) {
         for (this.rCellIndex.y = this.rMinCellIndex.y; this.rCellIndex.y < this.rMaxCellIndex.y; this.rCellIndex.y++) {
            for (this.rCellIndex.z = this.rMinCellIndex.z; this.rCellIndex.z < this.rMaxCellIndex.z; this.rCellIndex.z++) {
               this.gridGraphCache.getCell(this.rCellIndex, this.rResult);
               assert this.rResult.graph != null;
               this.gridGraphCache.toCellBounds(this.rCellIndex, this.rIntersectingCellBounds);
               if (this.rResult.isNew) {
                  this.graphGenerator.generate(this.rResult.graph, this.rIntersectingCellBounds);
               }

               this.rIntersectingCellBounds.intersect(this.rGraphBounds);
               this.rResultList.clear();
               this.rResult.graph.viewNodes(this.rIntersectingCellBounds, this.rResultList);
               this.rResultList.sort(nodeComparator);

               for (GraphSpace.Node node : this.rResultList) {
                  if (this.rControl.stop) {
                     this.rResultList.clear();
                     return;
                  }

                  this.runOnNode(node, this.rControl, context);
               }

               this.rResultList.clear();
            }
         }

         this.rCellIndex.x++;
      }

      this.rResult.graph = null;
   }

   private void runOnNode(@Nonnull GraphSpace.Node node, @Nonnull Control control, @Nonnull PropDistribution.Context context) {
      GraphSpace.Content content = node.content();
      GraphSpace.PropDistributionContent propContent = content.getPropDistributionContent().get(this.contentLayerId);
      if (propContent != null) {
         this.rContentBounds.min.set(-propContent.range);
         this.rContentBounds.max.set(propContent.range);
         this.rContentBounds.offset(node.position());
         if (this.rContentBounds.intersects(context.bounds)) {
            this.rChildContext.assign(context);
            this.rChildContext.bounds = this.rContentBounds;
            this.rAnchor.set(node.position());
            this.rChildContext.anchor = this.rAnchor;
            this.rChildContext.graphNode = node;
            this.rContentRangeSquared = propContent.rangeSquared;
            this.rNodePosition = node.position();
            this.rContextPipe = context.pipe;
            this.rChildContext.pipe = this.rChildPipe;
            propContent.propDistribution.distribute(this.rChildContext);
            this.rNodePosition = null;
            this.rContextPipe = null;
         }
      }
   }

   private void pipe(@Nonnull Vector3d position, @Nonnull Prop prop, @Nonnull Control control) {
      double distanceSqrToNode = position.distanceSquared(this.rNodePosition);
      if (!(distanceSqrToNode >= this.rContentRangeSquared)) {
         this.rContextPipe.accept(position, prop, control);
      }
   }

   @Override
   public void forEachPossibleProp(@NonNullDecl Consumer<Prop> consumer) {
      this.graphGenerator.viewAllPossibleContent(content -> {
         for (GraphSpace.ContentEntry<GraphSpace.PropDistributionContent> entry : content.getPropDistributionContent().getAll()) {
            entry.content().propDistribution.forEachPossibleProp(consumer);
         }
      });
   }
}
