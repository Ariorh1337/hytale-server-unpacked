package org.joml.sampling;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import org.joml.Random;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class BestCandidateSampling {
   public static class Cube {
      private int numSamples;
      private int numCandidates = 60;
      private long seed;

      public BestCandidateSampling.Cube seed(long seed) {
         this.seed = seed;
         return this;
      }

      public BestCandidateSampling.Cube numSamples(int numSamples) {
         this.numSamples = numSamples;
         return this;
      }

      public BestCandidateSampling.Cube numCandidates(int numCandidates) {
         this.numCandidates = numCandidates;
         return this;
      }

      public BestCandidateSampling.Cube generate(final float[] xyzs) {
         final BestCandidateSampling.IntHolder i = new BestCandidateSampling.IntHolder();
         return this.generate(new Callback3d() {
            public void onNewSample(float x, float y, float z) {
               xyzs[3 * i.value + 0] = x;
               xyzs[3 * i.value + 1] = y;
               xyzs[3 * i.value + 2] = z;
               i.value++;
            }
         });
      }

      public BestCandidateSampling.Cube generate(final FloatBuffer xyzs) {
         final BestCandidateSampling.IntHolder i = new BestCandidateSampling.IntHolder();
         final int pos = xyzs.position();
         return this.generate(new Callback3d() {
            public void onNewSample(float x, float y, float z) {
               xyzs.put(pos + 3 * i.value + 0, x);
               xyzs.put(pos + 3 * i.value + 1, y);
               xyzs.put(pos + 3 * i.value + 2, z);
               i.value++;
            }
         });
      }

      public BestCandidateSampling.Cube generate(Callback3d callback) {
         BestCandidateSampling.Octree octree = new BestCandidateSampling.Octree(-1.0F, -1.0F, -1.0F, 2.0F);
         Random rnd = new Random(this.seed);

         for (int i = 0; i < this.numSamples; i++) {
            float bestX = 0.0F;
            float bestY = 0.0F;
            float bestZ = 0.0F;
            float bestDist = 0.0F;

            for (int c = 0; c < this.numCandidates; c++) {
               float x = rnd.nextFloat() * 2.0F - 1.0F;
               float y = rnd.nextFloat() * 2.0F - 1.0F;
               float z = rnd.nextFloat() * 2.0F - 1.0F;
               float minDist = octree.nearest(x, y, z, bestDist, Float.POSITIVE_INFINITY);
               if (minDist > bestDist) {
                  bestDist = minDist;
                  bestX = x;
                  bestY = y;
                  bestZ = z;
               }
            }

            callback.onNewSample(bestX, bestY, bestZ);
            octree.insert(new Vector3f(bestX, bestY, bestZ));
         }

         return this;
      }
   }

   public static class Disk {
      private int numSamples;
      private int numCandidates = 60;
      private long seed;

      public BestCandidateSampling.Disk seed(long seed) {
         this.seed = seed;
         return this;
      }

      public BestCandidateSampling.Disk numSamples(int numSamples) {
         this.numSamples = numSamples;
         return this;
      }

      public BestCandidateSampling.Disk numCandidates(int numCandidates) {
         this.numCandidates = numCandidates;
         return this;
      }

      public BestCandidateSampling.Disk generate(final float[] xys) {
         final BestCandidateSampling.IntHolder i = new BestCandidateSampling.IntHolder();
         return this.generate(new Callback2d() {
            public void onNewSample(float x, float y) {
               xys[2 * i.value + 0] = x;
               xys[2 * i.value + 1] = y;
               i.value++;
            }
         });
      }

      public BestCandidateSampling.Disk generate(final FloatBuffer xys) {
         final BestCandidateSampling.IntHolder i = new BestCandidateSampling.IntHolder();
         final int pos = xys.position();
         return this.generate(new Callback2d() {
            public void onNewSample(float x, float y) {
               xys.put(pos + 3 * i.value + 0, x);
               xys.put(pos + 3 * i.value + 1, y);
               i.value++;
            }
         });
      }

      public BestCandidateSampling.Disk generate(Callback2d callback) {
         BestCandidateSampling.QuadTree qtree = new BestCandidateSampling.QuadTree(-1.0F, -1.0F, 2.0F);
         Random rnd = new Random(this.seed);

         for (int i = 0; i < this.numSamples; i++) {
            float bestX = 0.0F;
            float bestY = 0.0F;
            float bestDist = 0.0F;

            for (int c = 0; c < this.numCandidates; c++) {
               float x;
               float y;
               do {
                  x = rnd.nextFloat() * 2.0F - 1.0F;
                  y = rnd.nextFloat() * 2.0F - 1.0F;
               } while (x * x + y * y > 1.0F);

               float minDist = qtree.nearest(x, y, bestDist, Float.POSITIVE_INFINITY);
               if (minDist > bestDist) {
                  bestDist = minDist;
                  bestX = x;
                  bestY = y;
               }
            }

            callback.onNewSample(bestX, bestY);
            qtree.insert(new Vector2f(bestX, bestY));
         }

         return this;
      }
   }

   private static final class IntHolder {
      int value;

      private IntHolder() {
      }
   }

   private static class Octree {
      private static final int MAX_OBJECTS_PER_NODE = 32;
      private static final int PXNYNZ = 0;
      private static final int NXNYNZ = 1;
      private static final int NXPYNZ = 2;
      private static final int PXPYNZ = 3;
      private static final int PXNYPZ = 4;
      private static final int NXNYPZ = 5;
      private static final int NXPYPZ = 6;
      private static final int PXPYPZ = 7;
      private float minX;
      private float minY;
      private float minZ;
      private float hs;
      private ArrayList objects;
      private BestCandidateSampling.Octree[] children;

      Octree(float minX, float minY, float minZ, float size) {
         this.minX = minX;
         this.minY = minY;
         this.minZ = minZ;
         this.hs = size * 0.5F;
      }

      private void split() {
         this.children = new BestCandidateSampling.Octree[8];
         this.children[1] = new BestCandidateSampling.Octree(this.minX, this.minY, this.minZ, this.hs);
         this.children[0] = new BestCandidateSampling.Octree(this.minX + this.hs, this.minY, this.minZ, this.hs);
         this.children[2] = new BestCandidateSampling.Octree(this.minX, this.minY + this.hs, this.minZ, this.hs);
         this.children[3] = new BestCandidateSampling.Octree(this.minX + this.hs, this.minY + this.hs, this.minZ, this.hs);
         this.children[5] = new BestCandidateSampling.Octree(this.minX, this.minY, this.minZ + this.hs, this.hs);
         this.children[4] = new BestCandidateSampling.Octree(this.minX + this.hs, this.minY, this.minZ + this.hs, this.hs);
         this.children[6] = new BestCandidateSampling.Octree(this.minX, this.minY + this.hs, this.minZ + this.hs, this.hs);
         this.children[7] = new BestCandidateSampling.Octree(this.minX + this.hs, this.minY + this.hs, this.minZ + this.hs, this.hs);
      }

      private void insertIntoChild(Vector3f o) {
         this.children[this.octant(o.x, o.y, o.z)].insert(o);
      }

      void insert(Vector3f object) {
         if (this.children != null) {
            this.insertIntoChild(object);
         } else {
            if (this.objects != null && this.objects.size() == 32) {
               this.split();

               for (int i = 0; i < this.objects.size(); i++) {
                  this.insertIntoChild((Vector3f)this.objects.get(i));
               }

               this.objects = null;
               this.insertIntoChild(object);
            } else {
               if (this.objects == null) {
                  this.objects = new ArrayList(32);
               }

               this.objects.add(object);
            }
         }
      }

      private int octant(float x, float y, float z) {
         if (x < this.minX + this.hs) {
            if (y < this.minY + this.hs) {
               return z < this.minZ + this.hs ? 1 : 5;
            } else {
               return z < this.minZ + this.hs ? 2 : 6;
            }
         } else if (y < this.minY + this.hs) {
            return z < this.minZ + this.hs ? 0 : 4;
         } else {
            return z < this.minZ + this.hs ? 3 : 7;
         }
      }

      float nearest(float x, float y, float z, float lowerBound, float upperBound) {
         float up = upperBound;
         if (!(x < this.minX - upperBound)
            && !(x > this.minX + this.hs * 2.0F + upperBound)
            && !(y < this.minY - upperBound)
            && !(y > this.minY + this.hs * 2.0F + upperBound)
            && !(z < this.minZ - upperBound)
            && !(z > this.minZ + this.hs * 2.0F + upperBound)) {
            if (this.children != null) {
               int i = this.octant(x, y, z);

               for (int c = 0; c < 8; c++) {
                  float n1 = this.children[i].nearest(x, y, z, lowerBound, up);
                  up = Math.min(n1, up);
                  if (up <= lowerBound) {
                     return lowerBound;
                  }

                  i = i + 1 & 7;
               }

               return up;
            } else {
               float up2 = up * up;
               float lb2 = lowerBound * lowerBound;

               for (int i = 0; this.objects != null && i < this.objects.size(); i++) {
                  Vector3f o = (Vector3f)this.objects.get(i);
                  float d = o.distanceSquared(x, y, z);
                  if (d <= lb2) {
                     return lowerBound;
                  }

                  if (d < up2) {
                     up2 = d;
                  }
               }

               return Math.sqrt(up2);
            }
         } else {
            return up;
         }
      }
   }

   public static class Quad {
      private int numSamples;
      private int numCandidates = 60;
      private long seed;

      public BestCandidateSampling.Quad seed(long seed) {
         this.seed = seed;
         return this;
      }

      public BestCandidateSampling.Quad numSamples(int numSamples) {
         this.numSamples = numSamples;
         return this;
      }

      public BestCandidateSampling.Quad numCandidates(int numCandidates) {
         this.numCandidates = numCandidates;
         return this;
      }

      public BestCandidateSampling.Quad generate(final float[] xyzs) {
         final BestCandidateSampling.IntHolder i = new BestCandidateSampling.IntHolder();
         return this.generate(new Callback2d() {
            public void onNewSample(float x, float y) {
               xyzs[2 * i.value + 0] = x;
               xyzs[2 * i.value + 1] = y;
               i.value++;
            }
         });
      }

      public BestCandidateSampling.Quad generate(final FloatBuffer xys) {
         final BestCandidateSampling.IntHolder i = new BestCandidateSampling.IntHolder();
         final int pos = xys.position();
         return this.generate(new Callback2d() {
            public void onNewSample(float x, float y) {
               xys.put(pos + 3 * i.value + 0, x);
               xys.put(pos + 3 * i.value + 1, y);
               i.value++;
            }
         });
      }

      public BestCandidateSampling.Quad generate(Callback2d callback) {
         BestCandidateSampling.QuadTree qtree = new BestCandidateSampling.QuadTree(-1.0F, -1.0F, 2.0F);
         Random rnd = new Random(this.seed);

         for (int i = 0; i < this.numSamples; i++) {
            float bestX = 0.0F;
            float bestY = 0.0F;
            float bestDist = 0.0F;

            for (int c = 0; c < this.numCandidates; c++) {
               float x = rnd.nextFloat() * 2.0F - 1.0F;
               float y = rnd.nextFloat() * 2.0F - 1.0F;
               float minDist = qtree.nearest(x, y, bestDist, Float.POSITIVE_INFINITY);
               if (minDist > bestDist) {
                  bestDist = minDist;
                  bestX = x;
                  bestY = y;
               }
            }

            callback.onNewSample(bestX, bestY);
            qtree.insert(new Vector2f(bestX, bestY));
         }

         return this;
      }
   }

   private static class QuadTree {
      private static final int MAX_OBJECTS_PER_NODE = 32;
      private static final int PXNY = 0;
      private static final int NXNY = 1;
      private static final int NXPY = 2;
      private static final int PXPY = 3;
      private float minX;
      private float minY;
      private float hs;
      private ArrayList objects;
      private BestCandidateSampling.QuadTree[] children;

      QuadTree(float minX, float minY, float size) {
         this.minX = minX;
         this.minY = minY;
         this.hs = size * 0.5F;
      }

      private void split() {
         this.children = new BestCandidateSampling.QuadTree[4];
         this.children[1] = new BestCandidateSampling.QuadTree(this.minX, this.minY, this.hs);
         this.children[0] = new BestCandidateSampling.QuadTree(this.minX + this.hs, this.minY, this.hs);
         this.children[2] = new BestCandidateSampling.QuadTree(this.minX, this.minY + this.hs, this.hs);
         this.children[3] = new BestCandidateSampling.QuadTree(this.minX + this.hs, this.minY + this.hs, this.hs);
      }

      private void insertIntoChild(Vector2f o) {
         this.children[this.quadrant(o.x, o.y)].insert(o);
      }

      void insert(Vector2f object) {
         if (this.children != null) {
            this.insertIntoChild(object);
         } else {
            if (this.objects != null && this.objects.size() == 32) {
               this.split();

               for (int i = 0; i < this.objects.size(); i++) {
                  this.insertIntoChild((Vector2f)this.objects.get(i));
               }

               this.objects = null;
               this.insertIntoChild(object);
            } else {
               if (this.objects == null) {
                  this.objects = new ArrayList(32);
               }

               this.objects.add(object);
            }
         }
      }

      private int quadrant(float x, float y) {
         if (x < this.minX + this.hs) {
            return y < this.minY + this.hs ? 1 : 2;
         } else {
            return y < this.minY + this.hs ? 0 : 3;
         }
      }

      float nearest(float x, float y, float lowerBound, float upperBound) {
         float ub = upperBound;
         if (!(x < this.minX - upperBound)
            && !(x > this.minX + this.hs * 2.0F + upperBound)
            && !(y < this.minY - upperBound)
            && !(y > this.minY + this.hs * 2.0F + upperBound)) {
            if (this.children != null) {
               int i = this.quadrant(x, y);

               for (int c = 0; c < 4; c++) {
                  float n1 = this.children[i].nearest(x, y, lowerBound, ub);
                  ub = Math.min(n1, ub);
                  if (ub <= lowerBound) {
                     return lowerBound;
                  }

                  i = i + 1 & 3;
               }

               return ub;
            } else {
               float ub2 = ub * ub;
               float lb2 = lowerBound * lowerBound;

               for (int i = 0; this.objects != null && i < this.objects.size(); i++) {
                  Vector2f o = (Vector2f)this.objects.get(i);
                  float d = o.distanceSquared(x, y);
                  if (d <= lb2) {
                     return lowerBound;
                  }

                  if (d < ub2) {
                     ub2 = d;
                  }
               }

               return Math.sqrt(ub2);
            }
         } else {
            return ub;
         }
      }
   }

   public static class Sphere {
      private boolean onHemisphere;
      private int numSamples;
      private int numCandidates = 60;
      private long seed;

      public BestCandidateSampling.Sphere generate(final float[] xyzs) {
         final BestCandidateSampling.IntHolder i = new BestCandidateSampling.IntHolder();
         return this.generate(new Callback3d() {
            public void onNewSample(float x, float y, float z) {
               xyzs[3 * i.value + 0] = x;
               xyzs[3 * i.value + 1] = y;
               xyzs[3 * i.value + 2] = z;
               i.value++;
            }
         });
      }

      public BestCandidateSampling.Sphere generate(final FloatBuffer xyzs) {
         final BestCandidateSampling.IntHolder i = new BestCandidateSampling.IntHolder();
         final int pos = xyzs.position();
         return this.generate(new Callback3d() {
            public void onNewSample(float x, float y, float z) {
               xyzs.put(pos + 3 * i.value + 0, x);
               xyzs.put(pos + 3 * i.value + 1, y);
               xyzs.put(pos + 3 * i.value + 2, z);
               i.value++;
            }
         });
      }

      public BestCandidateSampling.Sphere seed(long seed) {
         this.seed = seed;
         return this;
      }

      public BestCandidateSampling.Sphere numSamples(int numSamples) {
         this.numSamples = numSamples;
         return this;
      }

      public BestCandidateSampling.Sphere numCandidates(int numCandidates) {
         this.numCandidates = numCandidates;
         return this;
      }

      public BestCandidateSampling.Sphere onHemisphere(boolean onHemisphere) {
         this.onHemisphere = onHemisphere;
         return this;
      }

      public BestCandidateSampling.Sphere generate(Callback3d callback) {
         Random rnd = new Random(this.seed);
         BestCandidateSampling.Sphere.Node otree = new BestCandidateSampling.Sphere.Node();

         for (int i = 0; i < this.numSamples; i++) {
            float bestX = Float.NaN;
            float bestY = Float.NaN;
            float bestZ = Float.NaN;
            float bestDist = 0.0F;

            for (int c = 0; c < this.numCandidates; c++) {
               float x1;
               float x2;
               do {
                  x1 = rnd.nextFloat() * 2.0F - 1.0F;
                  x2 = rnd.nextFloat() * 2.0F - 1.0F;
               } while (x1 * x1 + x2 * x2 > 1.0F);

               float sqrt = (float)Math.sqrt(1.0 - x1 * x1 - x2 * x2);
               float x = 2.0F * x1 * sqrt;
               float y = 2.0F * x2 * sqrt;
               float z = 1.0F - 2.0F * (x1 * x1 + x2 * x2);
               if (this.onHemisphere) {
                  z = Math.abs(z);
               }

               float minDist = otree.nearest(x, y, z);
               if (minDist > bestDist) {
                  bestDist = minDist;
                  bestX = x;
                  bestY = y;
                  bestZ = z;
               }
            }

            callback.onNewSample(bestX, bestY, bestZ);
            otree.insert(new Vector3f(bestX, bestY, bestZ));
         }

         return this;
      }

      private static final class Node {
         private static final int MAX_OBJECTS_PER_NODE = 32;
         private float v0x;
         private float v0y;
         private float v0z;
         private float v1x;
         private float v1y;
         private float v1z;
         private float v2x;
         private float v2y;
         private float v2z;
         private float cx;
         private float cy;
         private float cz;
         private float arc;
         private ArrayList objects;
         private BestCandidateSampling.Sphere.Node[] children;

         Node() {
            this.children = new BestCandidateSampling.Sphere.Node[8];
            float s = 1.0F;
            this.arc = (float) (java.lang.Math.PI * 2);
            this.children[0] = new BestCandidateSampling.Sphere.Node(-s, 0.0F, 0.0F, 0.0F, 0.0F, s, 0.0F, s, 0.0F);
            this.children[1] = new BestCandidateSampling.Sphere.Node(0.0F, 0.0F, s, s, 0.0F, 0.0F, 0.0F, s, 0.0F);
            this.children[2] = new BestCandidateSampling.Sphere.Node(s, 0.0F, 0.0F, 0.0F, 0.0F, -s, 0.0F, s, 0.0F);
            this.children[3] = new BestCandidateSampling.Sphere.Node(0.0F, 0.0F, -s, -s, 0.0F, 0.0F, 0.0F, s, 0.0F);
            this.children[4] = new BestCandidateSampling.Sphere.Node(-s, 0.0F, 0.0F, 0.0F, -s, 0.0F, 0.0F, 0.0F, s);
            this.children[5] = new BestCandidateSampling.Sphere.Node(0.0F, 0.0F, s, 0.0F, -s, 0.0F, s, 0.0F, 0.0F);
            this.children[6] = new BestCandidateSampling.Sphere.Node(s, 0.0F, 0.0F, 0.0F, -s, 0.0F, 0.0F, 0.0F, -s);
            this.children[7] = new BestCandidateSampling.Sphere.Node(0.0F, 0.0F, -s, 0.0F, -s, 0.0F, -s, 0.0F, 0.0F);
         }

         private Node(float x0, float y0, float z0, float x1, float y1, float z1, float x2, float y2, float z2) {
            this.v0x = x0;
            this.v0y = y0;
            this.v0z = z0;
            this.v1x = x1;
            this.v1y = y1;
            this.v1z = z1;
            this.v2x = x2;
            this.v2y = y2;
            this.v2z = z2;
            this.cx = (this.v0x + this.v1x + this.v2x) / 3.0F;
            this.cy = (this.v0y + this.v1y + this.v2y) / 3.0F;
            this.cz = (this.v0z + this.v1z + this.v2z) / 3.0F;
            float invCLen = Math.invsqrt(this.cx * this.cx + this.cy * this.cy + this.cz * this.cz);
            this.cx *= invCLen;
            this.cy *= invCLen;
            this.cz *= invCLen;
            float arc1 = this.greatCircleDist(this.cx, this.cy, this.cz, this.v0x, this.v0y, this.v0z);
            float arc2 = this.greatCircleDist(this.cx, this.cy, this.cz, this.v1x, this.v1y, this.v1z);
            float arc3 = this.greatCircleDist(this.cx, this.cy, this.cz, this.v2x, this.v2y, this.v2z);
            float dist = Math.max(Math.max(arc1, arc2), arc3);
            dist *= 1.7F;
            this.arc = dist;
         }

         private void split() {
            float w0x = this.v1x + this.v2x;
            float w0y = this.v1y + this.v2y;
            float w0z = this.v1z + this.v2z;
            float len0 = Math.invsqrt(w0x * w0x + w0y * w0y + w0z * w0z);
            w0x *= len0;
            w0y *= len0;
            w0z *= len0;
            float w1x = this.v0x + this.v2x;
            float w1y = this.v0y + this.v2y;
            float w1z = this.v0z + this.v2z;
            float len1 = Math.invsqrt(w1x * w1x + w1y * w1y + w1z * w1z);
            w1x *= len1;
            w1y *= len1;
            w1z *= len1;
            float w2x = this.v0x + this.v1x;
            float w2y = this.v0y + this.v1y;
            float w2z = this.v0z + this.v1z;
            float len2 = Math.invsqrt(w2x * w2x + w2y * w2y + w2z * w2z);
            w2x *= len2;
            w2y *= len2;
            w2z *= len2;
            this.children = new BestCandidateSampling.Sphere.Node[4];
            this.children[0] = new BestCandidateSampling.Sphere.Node(this.v0x, this.v0y, this.v0z, w2x, w2y, w2z, w1x, w1y, w1z);
            this.children[1] = new BestCandidateSampling.Sphere.Node(this.v1x, this.v1y, this.v1z, w0x, w0y, w0z, w2x, w2y, w2z);
            this.children[2] = new BestCandidateSampling.Sphere.Node(this.v2x, this.v2y, this.v2z, w1x, w1y, w1z, w0x, w0y, w0z);
            this.children[3] = new BestCandidateSampling.Sphere.Node(w0x, w0y, w0z, w1x, w1y, w1z, w2x, w2y, w2z);
         }

         private void insertIntoChild(Vector3f o) {
            for (int i = 0; i < this.children.length; i++) {
               BestCandidateSampling.Sphere.Node c = this.children[i];
               if (isPointOnSphericalTriangle(o.x, o.y, o.z, c.v0x, c.v0y, c.v0z, c.v1x, c.v1y, c.v1z, c.v2x, c.v2y, c.v2z, 1.0E-6F)) {
                  c.insert(o);
                  return;
               }
            }
         }

         void insert(Vector3f object) {
            if (this.children != null) {
               this.insertIntoChild(object);
            } else {
               if (this.objects != null && this.objects.size() == 32) {
                  this.split();

                  for (int i = 0; i < 32; i++) {
                     this.insertIntoChild((Vector3f)this.objects.get(i));
                  }

                  this.objects = null;
                  this.insertIntoChild(object);
               } else {
                  if (this.objects == null) {
                     this.objects = new ArrayList(32);
                  }

                  this.objects.add(object);
               }
            }
         }

         private static boolean isPointOnSphericalTriangle(
            float x, float y, float z, float v0X, float v0Y, float v0Z, float v1X, float v1Y, float v1Z, float v2X, float v2Y, float v2Z, float epsilon
         ) {
            float edge1X = v1X - v0X;
            float edge1Y = v1Y - v0Y;
            float edge1Z = v1Z - v0Z;
            float edge2X = v2X - v0X;
            float edge2Y = v2Y - v0Y;
            float edge2Z = v2Z - v0Z;
            float pvecX = y * edge2Z - z * edge2Y;
            float pvecY = z * edge2X - x * edge2Z;
            float pvecZ = x * edge2Y - y * edge2X;
            float det = edge1X * pvecX + edge1Y * pvecY + edge1Z * pvecZ;
            if (det > -epsilon && det < epsilon) {
               return false;
            }

            float tvecX = -v0X;
            float tvecY = -v0Y;
            float tvecZ = -v0Z;
            float invDet = 1.0F / det;
            float u = (tvecX * pvecX + tvecY * pvecY + tvecZ * pvecZ) * invDet;
            if (!(u < 0.0F) && !(u > 1.0F)) {
               float qvecX = tvecY * edge1Z - tvecZ * edge1Y;
               float qvecY = tvecZ * edge1X - tvecX * edge1Z;
               float qvecZ = tvecX * edge1Y - tvecY * edge1X;
               float v = (x * qvecX + y * qvecY + z * qvecZ) * invDet;
               if (!(v < 0.0F) && !(u + v > 1.0F)) {
                  float t = (edge2X * qvecX + edge2Y * qvecY + edge2Z * qvecZ) * invDet;
                  return t >= epsilon;
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }

         private int child(float x, float y, float z) {
            for (int i = 0; i < this.children.length; i++) {
               BestCandidateSampling.Sphere.Node c = this.children[i];
               if (isPointOnSphericalTriangle(x, y, z, c.v0x, c.v0y, c.v0z, c.v1x, c.v1y, c.v1z, c.v2x, c.v2y, c.v2z, 1.0E-5F)) {
                  return i;
               }
            }

            return 0;
         }

         private float greatCircleDist(float x1, float y1, float z1, float x2, float y2, float z2) {
            float dot = x1 * x2 + y1 * y2 + z1 * z2;
            return (float)((-java.lang.Math.PI / 2) * dot + (java.lang.Math.PI / 2));
         }

         float nearest(float x, float y, float z) {
            return this.nearest(x, y, z, Float.POSITIVE_INFINITY);
         }

         float nearest(float x, float y, float z, float n) {
            float gcd = this.greatCircleDist(x, y, z, this.cx, this.cy, this.cz);
            if (gcd - this.arc > n) {
               return n;
            }

            float nr = n;
            if (this.children != null) {
               int num = this.children.length;
               int mod = num - 1;
               int i = this.child(x, y, z);

               for (int c = 0; c < num; c++) {
                  float n1 = this.children[i].nearest(x, y, z, nr);
                  nr = Math.min(n1, nr);
                  i = i + 1 & mod;
               }

               return nr;
            } else {
               for (int i = 0; this.objects != null && i < this.objects.size(); i++) {
                  Vector3f o = (Vector3f)this.objects.get(i);
                  float d = this.greatCircleDist(o.x, o.y, o.z, x, y, z);
                  if (d < nr) {
                     nr = d;
                  }
               }

               return nr;
            }
         }
      }
   }
}
