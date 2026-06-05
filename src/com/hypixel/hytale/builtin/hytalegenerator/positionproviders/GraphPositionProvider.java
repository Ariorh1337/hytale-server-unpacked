package com.hypixel.hytale.builtin.hytalegenerator.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphGenerator;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphGrid;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.pipe.Control;
import com.hypixel.hytale.builtin.hytalegenerator.pipe.Pipe;
import com.hypixel.hytale.math.util.ChunkUtil;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.joml.Vector3d;
import org.joml.Vector3i;

public class GraphPositionProvider extends PositionProvider {
   @Nonnull
   private final GraphGrid graphGrid;
   private final int contentLayerId;
   private final double contentRange;
   @Nonnull
   private final List<GraphSpace.Node> rResultList;

   public GraphPositionProvider(@Nonnull GraphGenerator graphGenerator, int contentLayerId) {
      int CACHE_CAPACITY = 50;
      int CELL_PADDING = 0;
      this.graphGrid = new GraphGrid(graphGenerator, 0.0, 50);
      this.contentLayerId = contentLayerId;
      this.contentRange = graphGenerator.getPositionsRadius(contentLayerId);
      this.rResultList = new ArrayList<>();
   }

   @Nonnull
   private static Bounds3d getCellBounds(@Nonnull Vector3i cellPosition, double minY, double maxY) {
      Bounds3d bounds = new Bounds3d();
      bounds.min.x = ChunkUtil.worldCoordFromLocalCoord(cellPosition.x, 0);
      bounds.min.z = ChunkUtil.worldCoordFromLocalCoord(cellPosition.z, 0);
      bounds.max.x = bounds.min.x + 32.0;
      bounds.max.z = bounds.min.z + 32.0;
      bounds.min.y = minY;
      bounds.max.y = maxY;
      return bounds;
   }

   private void forEachNode(@Nonnull Pipe.One<GraphSpace.Node> pipe, @Nonnull Bounds3d bounds) {
      Vector3i minCell = new Vector3i(ChunkUtil.chunkCoordinate(bounds.min.x), 0, ChunkUtil.chunkCoordinate(bounds.min.z));
      Vector3i maxCell = new Vector3i(ChunkUtil.chunkCoordinate(bounds.max.x), 1, ChunkUtil.chunkCoordinate(bounds.max.z));
      boolean inclusiveMaxX = ChunkUtil.worldCoordFromLocalCoord(maxCell.x, 0) != bounds.max.x;
      boolean inclusiveMaxZ = ChunkUtil.worldCoordFromLocalCoord(maxCell.z, 0) != bounds.max.z;
      if (inclusiveMaxX) {
         maxCell.x++;
      }

      if (inclusiveMaxZ) {
         maxCell.z++;
      }

      Control control = new Control();

      for (Vector3i cellPosition = new Vector3i(minCell); cellPosition.x < maxCell.x; cellPosition.x++) {
         for (cellPosition.z = minCell.z; cellPosition.z < maxCell.z; cellPosition.z++) {
            GraphSpace graph = this.graphGrid.get(cellPosition);
            Bounds3d intersectingCellBounds = getCellBounds(cellPosition, bounds.min.y, bounds.max.y).intersect(bounds);
            this.rResultList.clear();
            graph.viewNodes(intersectingCellBounds, this.rResultList);

            for (GraphSpace.Node node : this.rResultList) {
               if (control.stop) {
                  this.rResultList.clear();
                  return;
               }

               pipe.accept(node, control);
            }

            this.rResultList.clear();
         }
      }
   }

   @Override
   public void generate(@NonNullDecl PositionProvider.Context context) {
      Bounds3d graphBounds = context.bounds.clone().expand(this.contentRange);
      this.forEachNode((node, control) -> {
         GraphSpace.Content content = node.content();
         GraphSpace.PositionsContent positionsContent = content.getPositionsContent().get(this.contentLayerId);
         if (positionsContent != null) {
            PositionProvider.Context childContext = new PositionProvider.Context(context);
            childContext.anchor = new Vector3d(node.position());
            childContext.graphNode = node;
            childContext.pipe = (position, control1) -> {
               double distanceSqrToNode = position.distanceSquared(node.position());
               if (!(distanceSqrToNode >= positionsContent.rangeSquared)) {
                  context.pipe.accept(position, control1);
               }
            };
            positionsContent.positions.generate(childContext);
         }
      }, graphBounds);
   }
}
