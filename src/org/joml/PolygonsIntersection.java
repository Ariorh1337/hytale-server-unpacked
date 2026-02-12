package org.joml;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PolygonsIntersection {
   private static final PolygonsIntersection.ByStartComparator byStartComparator = new PolygonsIntersection.ByStartComparator();
   private static final PolygonsIntersection.ByEndComparator byEndComparator = new PolygonsIntersection.ByEndComparator();
   protected final float[] verticesXY;
   private float minX;
   private float minY;
   private float maxX;
   private float maxY;
   private float centerX;
   private float centerY;
   private float radiusSquared;
   private PolygonsIntersection.IntervalTreeNode tree;

   public PolygonsIntersection(float[] verticesXY, int[] polygons, int count) {
      this.verticesXY = verticesXY;
      this.preprocess(count, polygons);
   }

   private PolygonsIntersection.IntervalTreeNode buildNode(List intervals, float center) {
      List left = null;
      List right = null;
      List byStart = null;
      List byEnd = null;
      float leftMin = 1.0E38F;
      float leftMax = -1.0E38F;
      float rightMin = 1.0E38F;
      float rightMax = -1.0E38F;
      float thisMin = 1.0E38F;
      float thisMax = -1.0E38F;

      for (int i = 0; i < intervals.size(); i++) {
         PolygonsIntersection.Interval ival = (PolygonsIntersection.Interval)intervals.get(i);
         if (ival.start < center && ival.end < center) {
            if (left == null) {
               left = new ArrayList();
            }

            left.add(ival);
            leftMin = leftMin < ival.start ? leftMin : ival.start;
            leftMax = leftMax > ival.end ? leftMax : ival.end;
         } else if (ival.start > center && ival.end > center) {
            if (right == null) {
               right = new ArrayList();
            }

            right.add(ival);
            rightMin = rightMin < ival.start ? rightMin : ival.start;
            rightMax = rightMax > ival.end ? rightMax : ival.end;
         } else {
            if (byStart == null || byEnd == null) {
               byStart = new ArrayList();
               byEnd = new ArrayList();
            }

            thisMin = ival.start < thisMin ? ival.start : thisMin;
            thisMax = ival.end > thisMax ? ival.end : thisMax;
            byStart.add(ival);
            byEnd.add(ival);
         }
      }

      if (byStart != null) {
         Collections.sort(byStart, byStartComparator);
         Collections.sort(byEnd, byEndComparator);
      }

      PolygonsIntersection.IntervalTreeNode tree = new PolygonsIntersection.IntervalTreeNode();
      tree.byBeginning = byStart;
      tree.byEnding = byEnd;
      tree.center = center;
      if (left != null) {
         tree.left = this.buildNode(left, (leftMin + leftMax) / 2.0F);
         tree.left.childrenMinMax = leftMax;
      }

      if (right != null) {
         tree.right = this.buildNode(right, (rightMin + rightMax) / 2.0F);
         tree.right.childrenMinMax = rightMin;
      }

      return tree;
   }

   private void preprocess(int count, int[] polygons) {
      int j = 0;
      this.minX = this.minY = 1.0E38F;
      this.maxX = this.maxY = -1.0E38F;
      List intervals = new ArrayList(count);
      int first = 0;
      int currPoly = 0;

      int i;
      for (i = 1; i < count; j = i++) {
         if (polygons != null && polygons.length > currPoly && polygons[currPoly] == i) {
            float prevy = this.verticesXY[2 * (i - 1) + 1];
            float firsty = this.verticesXY[2 * first + 1];
            PolygonsIntersection.Interval ival = new PolygonsIntersection.Interval();
            ival.start = prevy < firsty ? prevy : firsty;
            ival.end = firsty > prevy ? firsty : prevy;
            ival.i = i - 1;
            ival.j = first;
            ival.polyIndex = currPoly;
            intervals.add(ival);
            first = i;
            currPoly++;
            i++;
            j = i - 1;
         }

         float yi = this.verticesXY[2 * i + 1];
         float xi = this.verticesXY[2 * i + 0];
         float yj = this.verticesXY[2 * j + 1];
         this.minX = xi < this.minX ? xi : this.minX;
         this.minY = yi < this.minY ? yi : this.minY;
         this.maxX = xi > this.maxX ? xi : this.maxX;
         this.maxY = yi > this.maxY ? yi : this.maxY;
         PolygonsIntersection.Interval ival = new PolygonsIntersection.Interval();
         ival.start = yi < yj ? yi : yj;
         ival.end = yj > yi ? yj : yi;
         ival.i = i;
         ival.j = j;
         ival.polyIndex = currPoly;
         intervals.add(ival);
      }

      float yi = this.verticesXY[2 * (i - 1) + 1];
      float xi = this.verticesXY[2 * (i - 1) + 0];
      float yj = this.verticesXY[2 * first + 1];
      this.minX = xi < this.minX ? xi : this.minX;
      this.minY = yi < this.minY ? yi : this.minY;
      this.maxX = xi > this.maxX ? xi : this.maxX;
      this.maxY = yi > this.maxY ? yi : this.maxY;
      PolygonsIntersection.Interval ival = new PolygonsIntersection.Interval();
      ival.start = yi < yj ? yi : yj;
      ival.end = yj > yi ? yj : yi;
      ival.i = i - 1;
      ival.j = first;
      ival.polyIndex = currPoly;
      intervals.add(ival);
      this.centerX = (this.maxX + this.minX) * 0.5F;
      this.centerY = (this.maxY + this.minY) * 0.5F;
      float dx = this.maxX - this.centerX;
      float dy = this.maxY - this.centerY;
      this.radiusSquared = dx * dx + dy * dy;
      this.tree = this.buildNode(intervals, this.centerY);
   }

   public boolean testPoint(float x, float y) {
      return this.testPoint(x, y, null);
   }

   public boolean testPoint(float x, float y, BitSet inPolys) {
      float dx = x - this.centerX;
      float dy = y - this.centerY;
      if (inPolys != null) {
         inPolys.clear();
      }

      if (dx * dx + dy * dy > this.radiusSquared) {
         return false;
      } else {
         return !(this.maxX < x) && !(this.maxY < y) && !(this.minX > x) && !(this.minY > y)
            ? this.tree.traverse(this.verticesXY, x, y, false, inPolys)
            : false;
      }
   }

   static class ByEndComparator implements Comparator {
      public int compare(Object o1, Object o2) {
         PolygonsIntersection.Interval i1 = (PolygonsIntersection.Interval)o1;
         PolygonsIntersection.Interval i2 = (PolygonsIntersection.Interval)o2;
         return Float.compare(i2.end, i1.end);
      }
   }

   static class ByStartComparator implements Comparator {
      public int compare(Object o1, Object o2) {
         PolygonsIntersection.Interval i1 = (PolygonsIntersection.Interval)o1;
         PolygonsIntersection.Interval i2 = (PolygonsIntersection.Interval)o2;
         return Float.compare(i1.start, i2.start);
      }
   }

   static class Interval {
      float start;
      float end;
      int i;
      int j;
      int polyIndex;
   }

   static class IntervalTreeNode {
      float center;
      float childrenMinMax;
      PolygonsIntersection.IntervalTreeNode left;
      PolygonsIntersection.IntervalTreeNode right;
      List byBeginning;
      List byEnding;

      static boolean computeEvenOdd(float[] verticesXY, PolygonsIntersection.Interval ival, float x, float y, boolean evenOdd, BitSet inPolys) {
         boolean newEvenOdd = evenOdd;
         int i = ival.i;
         int j = ival.j;
         float yi = verticesXY[2 * i + 1];
         float yj = verticesXY[2 * j + 1];
         float xi = verticesXY[2 * i + 0];
         float xj = verticesXY[2 * j + 0];
         if ((yi < y && yj >= y || yj < y && yi >= y) && (xi <= x || xj <= x)) {
            float xDist = xi + (y - yi) / (yj - yi) * (xj - xi) - x;
            newEvenOdd ^= xDist < 0.0F;
            if (newEvenOdd != evenOdd && inPolys != null) {
               inPolys.flip(ival.polyIndex);
            }
         }

         return newEvenOdd;
      }

      boolean traverse(float[] verticesXY, float x, float y, boolean evenOdd, BitSet inPolys) {
         boolean newEvenOdd = evenOdd;
         if (y == this.center && this.byBeginning != null) {
            int size = this.byBeginning.size();

            for (int b = 0; b < size; b++) {
               PolygonsIntersection.Interval ival = (PolygonsIntersection.Interval)this.byBeginning.get(b);
               newEvenOdd = computeEvenOdd(verticesXY, ival, x, y, newEvenOdd, inPolys);
            }
         } else if (y < this.center) {
            if (this.left != null && this.left.childrenMinMax >= y) {
               newEvenOdd = this.left.traverse(verticesXY, x, y, newEvenOdd, inPolys);
            }

            if (this.byBeginning != null) {
               int size = this.byBeginning.size();

               for (int b = 0; b < size; b++) {
                  PolygonsIntersection.Interval ival = (PolygonsIntersection.Interval)this.byBeginning.get(b);
                  if (ival.start > y) {
                     break;
                  }

                  newEvenOdd = computeEvenOdd(verticesXY, ival, x, y, newEvenOdd, inPolys);
               }
            }
         } else if (y > this.center) {
            if (this.right != null && this.right.childrenMinMax <= y) {
               newEvenOdd = this.right.traverse(verticesXY, x, y, newEvenOdd, inPolys);
            }

            if (this.byEnding != null) {
               int size = this.byEnding.size();

               for (int b = 0; b < size; b++) {
                  PolygonsIntersection.Interval ival = (PolygonsIntersection.Interval)this.byEnding.get(b);
                  if (ival.end < y) {
                     break;
                  }

                  newEvenOdd = computeEvenOdd(verticesXY, ival, x, y, newEvenOdd, inPolys);
               }
            }
         }

         return newEvenOdd;
      }
   }
}
