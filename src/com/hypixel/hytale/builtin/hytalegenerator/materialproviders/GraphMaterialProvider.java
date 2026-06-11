package com.hypixel.hytale.builtin.hytalegenerator.materialproviders;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphGenerator;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GridGraphCache;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;

public class GraphMaterialProvider extends MaterialProvider<Material> {
   @Nonnull
   private final GridGraphCache gridGraphCache;
   @Nonnull
   private final GraphGenerator graphGenerator;
   private final int contentLayerId;
   private final double contentRadius;
   @Nonnull
   private final List<GraphSpace.Node> rResultList;

   public GraphMaterialProvider(@Nonnull GraphGenerator graphGenerator, int contentLayerId, @Nonnull Vector3dc cacheCellSize, int cacheCapacity) {
      assert GridGraphCache.isValidCellSize(cacheCellSize);
      assert cacheCapacity >= 0;
      int CACHE_CAPACITY = 1;
      this.contentLayerId = contentLayerId;
      this.gridGraphCache = new GridGraphCache(cacheCellSize, cacheCapacity);
      this.graphGenerator = graphGenerator;
      this.contentRadius = graphGenerator.getMaterialContentRadius(contentLayerId);
      this.rResultList = new ArrayList<>();
   }

   @NullableDecl
   public Material getVoxelTypeAt(@NonNullDecl MaterialProvider.Context context) {
      Vector3i cellIndex = new Vector3i();
      Vector3d positionDouble = new Vector3d(context.position);
      this.gridGraphCache.toCellIndex(positionDouble, cellIndex);
      GridGraphCache.Result result = new GridGraphCache.Result();
      this.gridGraphCache.getCell(cellIndex, result);
      Bounds3d localCellBounds_voxelGrid = new Bounds3d();
      this.gridGraphCache.toCellBounds(cellIndex, localCellBounds_voxelGrid);
      localCellBounds_voxelGrid.expand(this.contentRadius);
      if (result.isNew) {
         this.graphGenerator.generate(result.graph, localCellBounds_voxelGrid);
      }

      GraphSpace.Node[] nearestValidNode = new GraphSpace.Node[1];
      double[] smallestDistance = new double[]{Double.MAX_VALUE};
      double[] smallestPositionDiscriminator = new double[]{Double.MAX_VALUE};
      this.rResultList.clear();
      result.graph.viewNodes(new Vector3d(context.position), this.contentRadius, this.rResultList);

      for (GraphSpace.Node node : this.rResultList) {
         GraphSpace.Content content = node.content();
         GraphSpace.MaterialContent materialContent = content.getMaterialContent().get(this.contentLayerId);
         if (materialContent != null) {
            Vector3d positionFloat = new Vector3d(context.position);
            double distance = node.position().distance(positionFloat);
            if (!(distance >= materialContent.range) && !(distance >= smallestDistance[0])) {
               double positionDiscriminator = node.position().x() + node.position().y() + node.position().z();
               if (distance != smallestDistance[0] || !(positionDiscriminator >= smallestPositionDiscriminator[0])) {
                  nearestValidNode[0] = node;
                  smallestDistance[0] = distance;
                  smallestPositionDiscriminator[0] = positionDiscriminator;
               }
            }
         }
      }

      this.rResultList.clear();
      if (nearestValidNode[0] == null) {
         return null;
      }

      MaterialProvider.Context childContext = new MaterialProvider.Context(context);
      childContext.graphNode = nearestValidNode[0];
      childContext.anchor = new Vector3d(nearestValidNode[0].position());
      GraphSpace.MaterialContent materialContent = nearestValidNode[0].content().getMaterialContent().get(this.contentLayerId);
      assert materialContent != null;
      return materialContent.materialProvider.getVoxelTypeAt(childContext);
   }
}
