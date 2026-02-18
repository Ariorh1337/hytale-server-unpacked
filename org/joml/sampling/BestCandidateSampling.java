/*
 * Decompiled with CFR 0.152.
 */
package org.joml.sampling;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import org.joml.Random;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.sampling.Callback2d;
import org.joml.sampling.Callback3d;
import org.joml.sampling.Math;

public class BestCandidateSampling {

    public static class Cube {
        private int numSamples;
        private int numCandidates = 60;
        private long seed;

        public Cube seed(long seed) {
            this.seed = seed;
            return this;
        }

        public Cube numSamples(int numSamples) {
            this.numSamples = numSamples;
            return this;
        }

        public Cube numCandidates(int numCandidates) {
            this.numCandidates = numCandidates;
            return this;
        }

        public Cube generate(final float[] xyzs) {
            final IntHolder i = new IntHolder();
            return this.generate(new Callback3d(){

                public void onNewSample(float x, float y, float z) {
                    xyzs[3 * i.value + 0] = x;
                    xyzs[3 * i.value + 1] = y;
                    xyzs[3 * i.value + 2] = z;
                    ++i.value;
                }
            });
        }

        public Cube generate(final FloatBuffer xyzs) {
            final IntHolder i = new IntHolder();
            final int pos = xyzs.position();
            return this.generate(new Callback3d(){

                public void onNewSample(float x, float y, float z) {
                    xyzs.put(pos + 3 * i.value + 0, x);
                    xyzs.put(pos + 3 * i.value + 1, y);
                    xyzs.put(pos + 3 * i.value + 2, z);
                    ++i.value;
                }
            });
        }

        public Cube generate(Callback3d callback) {
            Octree octree = new Octree(-1.0f, -1.0f, -1.0f, 2.0f);
            Random rnd = new Random(this.seed);
            for (int i = 0; i < this.numSamples; ++i) {
                float bestX = 0.0f;
                float bestY = 0.0f;
                float bestZ = 0.0f;
                float bestDist = 0.0f;
                for (int c = 0; c < this.numCandidates; ++c) {
                    float z;
                    float y;
                    float x = rnd.nextFloat() * 2.0f - 1.0f;
                    float minDist = octree.nearest(x, y = rnd.nextFloat() * 2.0f - 1.0f, z = rnd.nextFloat() * 2.0f - 1.0f, bestDist, Float.POSITIVE_INFINITY);
                    if (!(minDist > bestDist)) continue;
                    bestDist = minDist;
                    bestX = x;
                    bestY = y;
                    bestZ = z;
                }
                callback.onNewSample(bestX, bestY, bestZ);
                octree.insert(new Vector3f(bestX, bestY, bestZ));
            }
            return this;
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
        private Octree[] children;

        Octree(float minX, float minY, float minZ, float size) {
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.hs = size * 0.5f;
        }

        private void split() {
            this.children = new Octree[8];
            this.children[1] = new Octree(this.minX, this.minY, this.minZ, this.hs);
            this.children[0] = new Octree(this.minX + this.hs, this.minY, this.minZ, this.hs);
            this.children[2] = new Octree(this.minX, this.minY + this.hs, this.minZ, this.hs);
            this.children[3] = new Octree(this.minX + this.hs, this.minY + this.hs, this.minZ, this.hs);
            this.children[5] = new Octree(this.minX, this.minY, this.minZ + this.hs, this.hs);
            this.children[4] = new Octree(this.minX + this.hs, this.minY, this.minZ + this.hs, this.hs);
            this.children[6] = new Octree(this.minX, this.minY + this.hs, this.minZ + this.hs, this.hs);
            this.children[7] = new Octree(this.minX + this.hs, this.minY + this.hs, this.minZ + this.hs, this.hs);
        }

        private void insertIntoChild(Vector3f o) {
            this.children[this.octant(o.x, o.y, o.z)].insert(o);
        }

        void insert(Vector3f object) {
            if (this.children != null) {
                this.insertIntoChild(object);
                return;
            }
            if (this.objects != null && this.objects.size() == 32) {
                this.split();
                for (int i = 0; i < this.objects.size(); ++i) {
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

        private int octant(float x, float y, float z) {
            if (x < this.minX + this.hs) {
                if (y < this.minY + this.hs) {
                    if (z < this.minZ + this.hs) {
                        return 1;
                    }
                    return 5;
                }
                if (z < this.minZ + this.hs) {
                    return 2;
                }
                return 6;
            }
            if (y < this.minY + this.hs) {
                if (z < this.minZ + this.hs) {
                    return 0;
                }
                return 4;
            }
            if (z < this.minZ + this.hs) {
                return 3;
            }
            return 7;
        }

        float nearest(float x, float y, float z, float lowerBound, float upperBound) {
            float up = upperBound;
            if (x < this.minX - upperBound || x > this.minX + this.hs * 2.0f + upperBound || y < this.minY - upperBound || y > this.minY + this.hs * 2.0f + upperBound || z < this.minZ - upperBound || z > this.minZ + this.hs * 2.0f + upperBound) {
                return up;
            }
            if (this.children != null) {
                int i = this.octant(x, y, z);
                for (int c = 0; c < 8; ++c) {
                    float n1 = this.children[i].nearest(x, y, z, lowerBound, up);
                    if ((up = Math.min(n1, up)) <= lowerBound) {
                        return lowerBound;
                    }
                    i = i + 1 & 7;
                }
                return up;
            }
            float up2 = up * up;
            float lb2 = lowerBound * lowerBound;
            for (int i = 0; this.objects != null && i < this.objects.size(); ++i) {
                Vector3f o = (Vector3f)this.objects.get(i);
                float d = o.distanceSquared(x, y, z);
                if (d <= lb2) {
                    return lowerBound;
                }
                if (!(d < up2)) continue;
                up2 = d;
            }
            return Math.sqrt(up2);
        }
    }

    public static class Quad {
        private int numSamples;
        private int numCandidates = 60;
        private long seed;

        public Quad seed(long seed) {
            this.seed = seed;
            return this;
        }

        public Quad numSamples(int numSamples) {
            this.numSamples = numSamples;
            return this;
        }

        public Quad numCandidates(int numCandidates) {
            this.numCandidates = numCandidates;
            return this;
        }

        public Quad generate(final float[] xyzs) {
            final IntHolder i = new IntHolder();
            return this.generate(new Callback2d(){

                public void onNewSample(float x, float y) {
                    xyzs[2 * i.value + 0] = x;
                    xyzs[2 * i.value + 1] = y;
                    ++i.value;
                }
            });
        }

        public Quad generate(final FloatBuffer xys) {
            final IntHolder i = new IntHolder();
            final int pos = xys.position();
            return this.generate(new Callback2d(){

                public void onNewSample(float x, float y) {
                    xys.put(pos + 3 * i.value + 0, x);
                    xys.put(pos + 3 * i.value + 1, y);
                    ++i.value;
                }
            });
        }

        public Quad generate(Callback2d callback) {
            QuadTree qtree = new QuadTree(-1.0f, -1.0f, 2.0f);
            Random rnd = new Random(this.seed);
            for (int i = 0; i < this.numSamples; ++i) {
                float bestX = 0.0f;
                float bestY = 0.0f;
                float bestDist = 0.0f;
                for (int c = 0; c < this.numCandidates; ++c) {
                    float y;
                    float x = rnd.nextFloat() * 2.0f - 1.0f;
                    float minDist = qtree.nearest(x, y = rnd.nextFloat() * 2.0f - 1.0f, bestDist, Float.POSITIVE_INFINITY);
                    if (!(minDist > bestDist)) continue;
                    bestDist = minDist;
                    bestX = x;
                    bestY = y;
                }
                callback.onNewSample(bestX, bestY);
                qtree.insert(new Vector2f(bestX, bestY));
            }
            return this;
        }
    }

    public static class Disk {
        private int numSamples;
        private int numCandidates = 60;
        private long seed;

        public Disk seed(long seed) {
            this.seed = seed;
            return this;
        }

        public Disk numSamples(int numSamples) {
            this.numSamples = numSamples;
            return this;
        }

        public Disk numCandidates(int numCandidates) {
            this.numCandidates = numCandidates;
            return this;
        }

        public Disk generate(final float[] xys) {
            final IntHolder i = new IntHolder();
            return this.generate(new Callback2d(){

                public void onNewSample(float x, float y) {
                    xys[2 * i.value + 0] = x;
                    xys[2 * i.value + 1] = y;
                    ++i.value;
                }
            });
        }

        public Disk generate(final FloatBuffer xys) {
            final IntHolder i = new IntHolder();
            final int pos = xys.position();
            return this.generate(new Callback2d(){

                public void onNewSample(float x, float y) {
                    xys.put(pos + 3 * i.value + 0, x);
                    xys.put(pos + 3 * i.value + 1, y);
                    ++i.value;
                }
            });
        }

        public Disk generate(Callback2d callback) {
            QuadTree qtree = new QuadTree(-1.0f, -1.0f, 2.0f);
            Random rnd = new Random(this.seed);
            for (int i = 0; i < this.numSamples; ++i) {
                float bestX = 0.0f;
                float bestY = 0.0f;
                float bestDist = 0.0f;
                for (int c = 0; c < this.numCandidates; ++c) {
                    float y;
                    float x;
                    while ((x = rnd.nextFloat() * 2.0f - 1.0f) * x + (y = rnd.nextFloat() * 2.0f - 1.0f) * y > 1.0f) {
                    }
                    float minDist = qtree.nearest(x, y, bestDist, Float.POSITIVE_INFINITY);
                    if (!(minDist > bestDist)) continue;
                    bestDist = minDist;
                    bestX = x;
                    bestY = y;
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
        private QuadTree[] children;

        QuadTree(float minX, float minY, float size) {
            this.minX = minX;
            this.minY = minY;
            this.hs = size * 0.5f;
        }

        private void split() {
            this.children = new QuadTree[4];
            this.children[1] = new QuadTree(this.minX, this.minY, this.hs);
            this.children[0] = new QuadTree(this.minX + this.hs, this.minY, this.hs);
            this.children[2] = new QuadTree(this.minX, this.minY + this.hs, this.hs);
            this.children[3] = new QuadTree(this.minX + this.hs, this.minY + this.hs, this.hs);
        }

        private void insertIntoChild(Vector2f o) {
            this.children[this.quadrant(o.x, o.y)].insert(o);
        }

        void insert(Vector2f object) {
            if (this.children != null) {
                this.insertIntoChild(object);
                return;
            }
            if (this.objects != null && this.objects.size() == 32) {
                this.split();
                for (int i = 0; i < this.objects.size(); ++i) {
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

        private int quadrant(float x, float y) {
            if (x < this.minX + this.hs) {
                if (y < this.minY + this.hs) {
                    return 1;
                }
                return 2;
            }
            if (y < this.minY + this.hs) {
                return 0;
            }
            return 3;
        }

        float nearest(float x, float y, float lowerBound, float upperBound) {
            float ub = upperBound;
            if (x < this.minX - upperBound || x > this.minX + this.hs * 2.0f + upperBound || y < this.minY - upperBound || y > this.minY + this.hs * 2.0f + upperBound) {
                return ub;
            }
            if (this.children != null) {
                int i = this.quadrant(x, y);
                for (int c = 0; c < 4; ++c) {
                    float n1 = this.children[i].nearest(x, y, lowerBound, ub);
                    if ((ub = Math.min(n1, ub)) <= lowerBound) {
                        return lowerBound;
                    }
                    i = i + 1 & 3;
                }
                return ub;
            }
            float ub2 = ub * ub;
            float lb2 = lowerBound * lowerBound;
            for (int i = 0; this.objects != null && i < this.objects.size(); ++i) {
                Vector2f o = (Vector2f)this.objects.get(i);
                float d = o.distanceSquared(x, y);
                if (d <= lb2) {
                    return lowerBound;
                }
                if (!(d < ub2)) continue;
                ub2 = d;
            }
            return Math.sqrt(ub2);
        }
    }

    public static class Sphere {
        private boolean onHemisphere;
        private int numSamples;
        private int numCandidates = 60;
        private long seed;

        public Sphere generate(final float[] xyzs) {
            final IntHolder i = new IntHolder();
            return this.generate(new Callback3d(){

                public void onNewSample(float x, float y, float z) {
                    xyzs[3 * i.value + 0] = x;
                    xyzs[3 * i.value + 1] = y;
                    xyzs[3 * i.value + 2] = z;
                    ++i.value;
                }
            });
        }

        public Sphere generate(final FloatBuffer xyzs) {
            final IntHolder i = new IntHolder();
            final int pos = xyzs.position();
            return this.generate(new Callback3d(){

                public void onNewSample(float x, float y, float z) {
                    xyzs.put(pos + 3 * i.value + 0, x);
                    xyzs.put(pos + 3 * i.value + 1, y);
                    xyzs.put(pos + 3 * i.value + 2, z);
                    ++i.value;
                }
            });
        }

        public Sphere seed(long seed) {
            this.seed = seed;
            return this;
        }

        public Sphere numSamples(int numSamples) {
            this.numSamples = numSamples;
            return this;
        }

        public Sphere numCandidates(int numCandidates) {
            this.numCandidates = numCandidates;
            return this;
        }

        public Sphere onHemisphere(boolean onHemisphere) {
            this.onHemisphere = onHemisphere;
            return this;
        }

        public Sphere generate(Callback3d callback) {
            Random rnd = new Random(this.seed);
            Node otree = new Node();
            for (int i = 0; i < this.numSamples; ++i) {
                float bestX = Float.NaN;
                float bestY = Float.NaN;
                float bestZ = Float.NaN;
                float bestDist = 0.0f;
                for (int c = 0; c < this.numCandidates; ++c) {
                    float minDist;
                    float x2;
                    float x1;
                    while ((x1 = rnd.nextFloat() * 2.0f - 1.0f) * x1 + (x2 = rnd.nextFloat() * 2.0f - 1.0f) * x2 > 1.0f) {
                    }
                    float sqrt = (float)Math.sqrt(1.0 - (double)(x1 * x1) - (double)(x2 * x2));
                    float x = 2.0f * x1 * sqrt;
                    float y = 2.0f * x2 * sqrt;
                    float z = 1.0f - 2.0f * (x1 * x1 + x2 * x2);
                    if (this.onHemisphere) {
                        z = Math.abs(z);
                    }
                    if (!((minDist = otree.nearest(x, y, z)) > bestDist)) continue;
                    bestDist = minDist;
                    bestX = x;
                    bestY = y;
                    bestZ = z;
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
            private Node[] children;

            Node() {
                this.children = new Node[8];
                float s = 1.0f;
                this.arc = (float)java.lang.Math.PI * 2;
                this.children[0] = new Node(-s, 0.0f, 0.0f, 0.0f, 0.0f, s, 0.0f, s, 0.0f);
                this.children[1] = new Node(0.0f, 0.0f, s, s, 0.0f, 0.0f, 0.0f, s, 0.0f);
                this.children[2] = new Node(s, 0.0f, 0.0f, 0.0f, 0.0f, -s, 0.0f, s, 0.0f);
                this.children[3] = new Node(0.0f, 0.0f, -s, -s, 0.0f, 0.0f, 0.0f, s, 0.0f);
                this.children[4] = new Node(-s, 0.0f, 0.0f, 0.0f, -s, 0.0f, 0.0f, 0.0f, s);
                this.children[5] = new Node(0.0f, 0.0f, s, 0.0f, -s, 0.0f, s, 0.0f, 0.0f);
                this.children[6] = new Node(s, 0.0f, 0.0f, 0.0f, -s, 0.0f, 0.0f, 0.0f, -s);
                this.children[7] = new Node(0.0f, 0.0f, -s, 0.0f, -s, 0.0f, -s, 0.0f, 0.0f);
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
                this.cx = (this.v0x + this.v1x + this.v2x) / 3.0f;
                this.cy = (this.v0y + this.v1y + this.v2y) / 3.0f;
                this.cz = (this.v0z + this.v1z + this.v2z) / 3.0f;
                float invCLen = Math.invsqrt(this.cx * this.cx + this.cy * this.cy + this.cz * this.cz);
                this.cx *= invCLen;
                this.cy *= invCLen;
                this.cz *= invCLen;
                float arc1 = this.greatCircleDist(this.cx, this.cy, this.cz, this.v0x, this.v0y, this.v0z);
                float arc2 = this.greatCircleDist(this.cx, this.cy, this.cz, this.v1x, this.v1y, this.v1z);
                float arc3 = this.greatCircleDist(this.cx, this.cy, this.cz, this.v2x, this.v2y, this.v2z);
                float dist = Math.max(Math.max(arc1, arc2), arc3);
                this.arc = dist *= 1.7f;
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
                this.children = new Node[4];
                this.children[0] = new Node(this.v0x, this.v0y, this.v0z, w2x *= len2, w2y *= len2, w2z *= len2, w1x, w1y, w1z);
                this.children[1] = new Node(this.v1x, this.v1y, this.v1z, w0x, w0y, w0z, w2x, w2y, w2z);
                this.children[2] = new Node(this.v2x, this.v2y, this.v2z, w1x, w1y, w1z, w0x, w0y, w0z);
                this.children[3] = new Node(w0x, w0y, w0z, w1x, w1y, w1z, w2x, w2y, w2z);
            }

            private void insertIntoChild(Vector3f o) {
                for (int i = 0; i < this.children.length; ++i) {
                    Node c = this.children[i];
                    if (!Node.isPointOnSphericalTriangle(o.x, o.y, o.z, c.v0x, c.v0y, c.v0z, c.v1x, c.v1y, c.v1z, c.v2x, c.v2y, c.v2z, 1.0E-6f)) continue;
                    c.insert(o);
                    return;
                }
            }

            void insert(Vector3f object) {
                if (this.children != null) {
                    this.insertIntoChild(object);
                    return;
                }
                if (this.objects != null && this.objects.size() == 32) {
                    this.split();
                    for (int i = 0; i < 32; ++i) {
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

            private static boolean isPointOnSphericalTriangle(float x, float y, float z, float v0X, float v0Y, float v0Z, float v1X, float v1Y, float v1Z, float v2X, float v2Y, float v2Z, float epsilon) {
                float edge1X = v1X - v0X;
                float edge2Z = v2Z - v0Z;
                float edge2Y = v2Y - v0Y;
                float pvecX = y * edge2Z - z * edge2Y;
                float edge1Y = v1Y - v0Y;
                float edge2X = v2X - v0X;
                float pvecY = z * edge2X - x * edge2Z;
                float edge1Z = v1Z - v0Z;
                float pvecZ = x * edge2Y - y * edge2X;
                float det = edge1X * pvecX + edge1Y * pvecY + edge1Z * pvecZ;
                if (det > -epsilon && det < epsilon) {
                    return false;
                }
                float tvecX = -v0X;
                float tvecY = -v0Y;
                float tvecZ = -v0Z;
                float invDet = 1.0f / det;
                float u = (tvecX * pvecX + tvecY * pvecY + tvecZ * pvecZ) * invDet;
                if (u < 0.0f || u > 1.0f) {
                    return false;
                }
                float qvecX = tvecY * edge1Z - tvecZ * edge1Y;
                float qvecY = tvecZ * edge1X - tvecX * edge1Z;
                float qvecZ = tvecX * edge1Y - tvecY * edge1X;
                float v = (x * qvecX + y * qvecY + z * qvecZ) * invDet;
                if (v < 0.0f || u + v > 1.0f) {
                    return false;
                }
                float t = (edge2X * qvecX + edge2Y * qvecY + edge2Z * qvecZ) * invDet;
                return t >= epsilon;
            }

            private int child(float x, float y, float z) {
                for (int i = 0; i < this.children.length; ++i) {
                    Node c = this.children[i];
                    if (!Node.isPointOnSphericalTriangle(x, y, z, c.v0x, c.v0y, c.v0z, c.v1x, c.v1y, c.v1z, c.v2x, c.v2y, c.v2z, 1.0E-5f)) continue;
                    return i;
                }
                return 0;
            }

            private float greatCircleDist(float x1, float y1, float z1, float x2, float y2, float z2) {
                float dot = x1 * x2 + y1 * y2 + z1 * z2;
                return (float)(-1.5707963267948966 * (double)dot + 1.5707963267948966);
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
                    for (int c = 0; c < num; ++c) {
                        float n1 = this.children[i].nearest(x, y, z, nr);
                        nr = Math.min(n1, nr);
                        i = i + 1 & mod;
                    }
                    return nr;
                }
                for (int i = 0; this.objects != null && i < this.objects.size(); ++i) {
                    Vector3f o = (Vector3f)this.objects.get(i);
                    float d = this.greatCircleDist(o.x, o.y, o.z, x, y, z);
                    if (!(d < nr)) continue;
                    nr = d;
                }
                return nr;
            }
        }
    }

    private static final class IntHolder {
        int value;

        private IntHolder() {
        }
    }
}

