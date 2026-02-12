package org.joml.sampling;

import java.util.ArrayList;
import org.joml.Random;
import org.joml.Vector2f;

public class PoissonSampling {
   public static class Disk {
      private final Vector2f[] grid;
      private final float diskRadius;
      private final float diskRadiusSquared;
      private final float minDist;
      private final float minDistSquared;
      private final float cellSize;
      private final int numCells;
      private final Random rnd;
      private final ArrayList processList;

      public Disk(long seed, float diskRadius, float minDist, int k, Callback2d callback) {
         this.diskRadius = diskRadius;
         this.diskRadiusSquared = diskRadius * diskRadius;
         this.minDist = minDist;
         this.minDistSquared = minDist * minDist;
         this.rnd = new Random(seed);
         this.cellSize = minDist / (float)Math.sqrt(2.0);
         this.numCells = (int)(diskRadius * 2.0F / this.cellSize) + 1;
         this.grid = new Vector2f[this.numCells * this.numCells];
         this.processList = new ArrayList();
         this.compute(k, callback);
      }

      private void compute(int k, Callback2d callback) {
         float x;
         float y;
         do {
            x = this.rnd.nextFloat() * 2.0F - 1.0F;
            y = this.rnd.nextFloat() * 2.0F - 1.0F;
         } while (x * x + y * y > 1.0F);

         Vector2f initial = new Vector2f(x, y);
         this.processList.add(initial);
         callback.onNewSample(initial.x, initial.y);
         this.insert(initial);

         while (!this.processList.isEmpty()) {
            int i = this.rnd.nextInt(this.processList.size());
            Vector2f sample = (Vector2f)this.processList.get(i);
            boolean found = false;

            for (int s = 0; s < k; s++) {
               float angle = this.rnd.nextFloat() * (float) (java.lang.Math.PI * 2);
               float radius = this.minDist * (this.rnd.nextFloat() + 1.0F);
               x = (float)(radius * Math.sin_roquen_9(angle + (java.lang.Math.PI / 2)));
               y = (float)(radius * Math.sin_roquen_9(angle));
               x += sample.x;
               y += sample.y;
               if (!(x * x + y * y > this.diskRadiusSquared) && !this.searchNeighbors(x, y)) {
                  found = true;
                  callback.onNewSample(x, y);
                  Vector2f f = new Vector2f(x, y);
                  this.processList.add(f);
                  this.insert(f);
                  break;
               }
            }

            if (!found) {
               this.processList.remove(i);
            }
         }
      }

      private boolean searchNeighbors(float px, float py) {
         float invCellSize = 1.0F / this.cellSize;
         int row = (int)((py + this.diskRadius) * invCellSize);
         int col = (int)((px + this.diskRadius) * invCellSize);
         if (this.grid[row * this.numCells + col] != null) {
            return true;
         }

         int minX = Math.max(0, col - 1);
         int minY = Math.max(0, row - 1);
         int maxX = Math.min(col + 1, this.numCells - 1);
         int maxY = Math.min(row + 1, this.numCells - 1);

         for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
               Vector2f v = this.grid[y * this.numCells + x];
               if (v != null) {
                  float dx = v.x - px;
                  float dy = v.y - py;
                  if (dx * dx + dy * dy < this.minDistSquared) {
                     return true;
                  }
               }
            }
         }

         return false;
      }

      private void insert(Vector2f p) {
         float invCellSize = 1.0F / this.cellSize;
         int row = (int)((p.y + this.diskRadius) * invCellSize);
         int col = (int)((p.x + this.diskRadius) * invCellSize);
         this.grid[row * this.numCells + col] = p;
      }
   }
}
