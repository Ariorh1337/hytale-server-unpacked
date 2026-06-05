package com.hypixel.hytale.builtin.hytalegenerator.materialproviders;

import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphGenerator;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphGrid;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.math.util.ChunkUtil;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.joml.Vector3d;
import org.joml.Vector3i;

public class GraphMaterialProvider extends MaterialProvider<Material> {
   @Nonnull
   private final GraphGrid graphGrid;
   private final int contentLayerId;
   private final double contentRadius;
   @Nonnull
   private final List<GraphSpace.Node> rResultList;

   public GraphMaterialProvider(@Nonnull GraphGenerator graphGenerator, int contentLayerId) {
      int CACHE_CAPACITY = 1;
      this.contentLayerId = contentLayerId;
      this.graphGrid = new GraphGrid(graphGenerator, graphGenerator.getMaterialContentRadius(contentLayerId), 1);
      this.contentRadius = graphGenerator.getMaterialContentRadius(contentLayerId);
      this.rResultList = new ArrayList<>();
   }

   @NullableDecl
   public Material getVoxelTypeAt(@NonNullDecl MaterialProvider.Context context) {
      Vector3i cellPosition = new Vector3i(ChunkUtil.chunkCoordinate(context.position.x), 0, ChunkUtil.chunkCoordinate(context.position.z));
      GraphSpace graph = this.graphGrid.get(cellPosition);
      GraphSpace.Node[] nearestValidNode = new GraphSpace.Node[1];
      double[] smallestDistance = new double[]{Double.MAX_VALUE};
      double[] smallestPositionDiscriminator = new double[]{Double.MAX_VALUE};
      this.rResultList.clear();
      graph.viewNodes(new Vector3d(context.position), this.contentRadius, this.rResultList);

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
