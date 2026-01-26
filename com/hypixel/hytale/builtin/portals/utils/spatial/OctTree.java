/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.portals.utils.spatial;

import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.vector.Vector3d;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;

public class OctTree<T> {
    private static final int SIZE = 8;
    private static final int DEFAULT_NODE_CAPACITY = 4;
    private final Node root;
    private final int nodeCapacity;

    public OctTree(double inradius) {
        this(new Box(-inradius, -inradius, -inradius, inradius, inradius, inradius), 4);
    }

    public OctTree(Box boundary) {
        this(boundary, 4);
    }

    public OctTree(Box boundary, int nodeCapacity) {
        this.root = new Node(boundary);
        this.nodeCapacity = nodeCapacity;
    }

    public void add(Vector3d pos, T value) {
        this.add(this.root, pos, value);
    }

    private boolean add(Node node, Vector3d pos, T value) {
        if (node == null || !node.boundary.containsPosition(pos)) {
            return false;
        }
        if (node.size() < this.nodeCapacity) {
            node.addPoint(pos, value);
            return true;
        }
        if (node.dirs.isEmpty()) {
            this.subdivide(node);
        }
        for (int i = 0; i < 8; ++i) {
            if (!this.add(node.dirs.get(i), pos, value)) continue;
            return true;
        }
        return false;
    }

    private void subdivide(Node node) {
        Vector3d min = node.boundary.min;
        double side = node.boundary.width() / 2.0;
        for (int i = 0; i < 8; ++i) {
            Vector3d subMin = new Vector3d(min.x + ((i & 1) > 0 ? side : 0.0), min.y + ((i & 2) > 0 ? side : 0.0), min.z + ((i & 4) > 0 ? side : 0.0));
            Box sub = Box.cube(subMin, side);
            node.dirs.add(new Node(sub));
        }
    }

    public Map<T, Vector3d> getAllPoints() {
        return this.queryRange(this.root.boundary);
    }

    public Map<T, Vector3d> queryRange(Vector3d position, double inradius) {
        Box range = Box.centeredCube(position, inradius);
        return this.queryRange(range);
    }

    public Map<T, Vector3d> queryRange(Box range) {
        Object2ObjectOpenHashMap out = new Object2ObjectOpenHashMap();
        this.queryRange(this.root, range, out);
        return out;
    }

    private void queryRange(Node node, Box range, Map<T, Vector3d> out) {
        if (node == null || !node.boundary.isIntersecting(range)) {
            return;
        }
        for (int i = 0; i < node.size(); ++i) {
            Vector3d point = node.points[i];
            if (!range.containsPosition(point)) continue;
            Object value = node.values.get(i);
            out.put(value, point);
        }
        for (Node dir : node.dirs) {
            this.queryRange(dir, range, out);
        }
    }

    private class Node {
        private final Box boundary;
        private final List<Node> dirs = new ObjectArrayList<Node>(8);
        private final Vector3d[] points;
        private final List<T> values;
        private int count;

        public Node(Box boundary) {
            this.points = new Vector3d[OctTree.this.nodeCapacity];
            this.values = new ObjectArrayList(OctTree.this.nodeCapacity);
            this.boundary = boundary;
        }

        public int size() {
            return this.count;
        }

        public void addPoint(Vector3d pos, T value) {
            this.points[this.count++] = pos;
            this.values.add(value);
        }
    }
}

