/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component.spatial;

import com.hypixel.hytale.component.spatial.SpatialData;
import com.hypixel.hytale.component.spatial.SpatialStructure;
import com.hypixel.hytale.math.vector.Vector3d;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class KDTree<T>
implements SpatialStructure<T> {
    @Nonnull
    private final List<Node<T>> nodePool = new ObjectArrayList<Node<T>>();
    private int nodePoolIndex = 0;
    @Nonnull
    private final List<List<T>> dataListPool = new ObjectArrayList<List<T>>();
    private int dataListPoolIndex = 0;
    private int size;
    @Nonnull
    private final Predicate<T> collectionFilter;
    @Nullable
    private Node<T> root;

    public KDTree(@Nonnull Predicate<T> collectionFilter) {
        this.collectionFilter = collectionFilter;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void rebuild(@Nonnull SpatialData<T> spatialData) {
        int rightSortedIndex;
        Vector3d rightVector;
        int right;
        int leftSortedIndex;
        Vector3d leftVector;
        int left;
        this.root = null;
        this.size = 0;
        int spatialDataSize = spatialData.size();
        if (spatialDataSize == 0) {
            return;
        }
        for (int i = 0; i < this.dataListPoolIndex; ++i) {
            this.dataListPool.get(i).clear();
        }
        this.nodePoolIndex = 0;
        this.dataListPoolIndex = 0;
        spatialData.sortMorton();
        int mid = spatialDataSize / 2;
        int sortedIndex = spatialData.getSortedIndex(mid);
        Vector3d vector = spatialData.getVector(sortedIndex);
        T data = spatialData.getData(sortedIndex);
        List<T> list = this.getPooledDataList();
        list.add(data);
        for (left = mid - 1; left >= 0 && (leftVector = spatialData.getVector(leftSortedIndex = spatialData.getSortedIndex(left))).equals(vector); --left) {
            T leftData = spatialData.getData(leftSortedIndex);
            list.add(leftData);
        }
        for (right = mid + 1; right < spatialDataSize && (rightVector = spatialData.getVector(rightSortedIndex = spatialData.getSortedIndex(right))).equals(vector); ++right) {
            T rightData = spatialData.getData(rightSortedIndex);
            list.add(rightData);
        }
        this.root = this.getPooledNode(vector, list);
        if (0 < left + 1) {
            this.build0(spatialData, 0, left + 1);
        }
        if (right < spatialDataSize) {
            this.build0(spatialData, right, spatialDataSize);
        }
        this.size = spatialDataSize;
    }

    @Override
    @Nullable
    public T closest(@Nonnull Vector3d point) {
        ClosestState closestState = new ClosestState(null, Double.MAX_VALUE);
        this.closest0(closestState, this.root, point, 0);
        if (closestState.node == null) {
            return null;
        }
        return closestState.node.data.getFirst();
    }

    @Override
    public void collect(@Nonnull Vector3d center, double radius, @Nonnull List<T> results) {
        double distanceSq = radius * radius;
        this.collect0(results, this.root, center, distanceSq, 0);
    }

    @Override
    public void collectCylinder(@Nonnull Vector3d center, double radius, double height, @Nonnull List<T> results) {
        double radiusSq = radius * radius;
        double halfHeight = height / 2.0;
        this.collectCylinder0(results, this.root, center, radiusSq, halfHeight, radius, 0);
    }

    @Override
    public void collectBox(@Nonnull Vector3d min, @Nonnull Vector3d max, @Nonnull List<T> results) {
        this.collectBox0(results, this.root, min, max, 0);
    }

    @Override
    public void ordered(@Nonnull Vector3d center, double radius, @Nonnull List<T> results) {
        double distanceSq = radius * radius;
        ObjectArrayList<OrderedEntry<T>> entryResults = new ObjectArrayList<OrderedEntry<T>>();
        this.ordered0(entryResults, this.root, center, distanceSq, 0);
        entryResults.sort((Comparator<OrderedEntry<T>>)Comparator.comparingDouble(o -> o.distanceSq));
        for (OrderedEntry orderedEntry : entryResults) {
            int bound = orderedEntry.values.size();
            for (int i = 0; i < bound; ++i) {
                Object data = orderedEntry.values.get(i);
                if (!this.collectionFilter.test(data)) continue;
                results.add(data);
            }
        }
    }

    @Override
    public void ordered3DAxis(@Nonnull Vector3d center, double xSearchRadius, double YSearchRadius, double zSearchRadius, @Nonnull List<T> results) {
        ObjectArrayList<OrderedEntry<T>> entryResults = new ObjectArrayList<OrderedEntry<T>>();
        this._internal_ordered3DAxis(entryResults, this.root, center, xSearchRadius, YSearchRadius, zSearchRadius, 0);
        entryResults.sort((Comparator<OrderedEntry<T>>)Comparator.comparingDouble(o -> o.distanceSq));
        for (OrderedEntry orderedEntry : entryResults) {
            int bound = orderedEntry.values.size();
            for (int i = 0; i < bound; ++i) {
                Object data = orderedEntry.values.get(i);
                if (!this.collectionFilter.test(data)) continue;
                results.add(data);
            }
        }
    }

    @Override
    @Nonnull
    public String dump() {
        return "KDTree(size=" + this.size + ")\n" + (this.root == null ? null : this.root.dump(0));
    }

    @Nonnull
    private Node<T> getPooledNode(Vector3d vector, List<T> data) {
        if (this.nodePoolIndex < this.nodePool.size()) {
            Node<T> node = this.nodePool.get(this.nodePoolIndex++);
            node.reset(vector, data);
            return node;
        }
        Node<T> node = new Node<T>(vector, data);
        this.nodePool.add(node);
        ++this.nodePoolIndex;
        return node;
    }

    private List<T> getPooledDataList() {
        if (this.dataListPoolIndex < this.dataListPool.size()) {
            return this.dataListPool.get(this.dataListPoolIndex++);
        }
        ObjectArrayList set = new ObjectArrayList(1);
        this.dataListPool.add(set);
        ++this.dataListPoolIndex;
        return set;
    }

    private void build0(@Nonnull SpatialData<T> spatialData, int start, int end) {
        int rightSortedIndex;
        Vector3d rightVector;
        int right;
        int leftSortedIndex;
        Vector3d leftVector;
        int left;
        int mid = (start + end) / 2;
        int sortedIndex = spatialData.getSortedIndex(mid);
        Vector3d vector = spatialData.getVector(sortedIndex);
        T data = spatialData.getData(sortedIndex);
        List<T> list = this.getPooledDataList();
        list.add(data);
        for (left = mid - 1; left >= start && (leftVector = spatialData.getVector(leftSortedIndex = spatialData.getSortedIndex(left))).equals(vector); --left) {
            T leftData = spatialData.getData(leftSortedIndex);
            list.add(leftData);
        }
        for (right = mid + 1; right < end && (rightVector = spatialData.getVector(rightSortedIndex = spatialData.getSortedIndex(right))).equals(vector); ++right) {
            T rightData = spatialData.getData(rightSortedIndex);
            list.add(rightData);
        }
        this.put0(this.root, vector, list, 0);
        if (start < left + 1) {
            this.build0(spatialData, start, left + 1);
        }
        if (right < end) {
            this.build0(spatialData, right, end);
        }
    }

    private void put0(@Nonnull Node<T> node, @Nonnull Vector3d vector, @Nonnull List<T> list, int axis) {
        if (KDTree.compare(node.vector, vector, axis) < 0) {
            if (node.one == null) {
                node.one = this.getPooledNode(vector, list);
            } else {
                this.put0(node.one, vector, list, (axis + 1) % 3);
            }
        } else if (node.two == null) {
            node.two = this.getPooledNode(vector, list);
        } else {
            this.put0(node.two, vector, list, (axis + 1) % 3);
        }
    }

    private void closest0(@Nonnull ClosestState<T> closestState, @Nullable Node<T> node, @Nonnull Vector3d vector, int depth) {
        if (node == null) {
            return;
        }
        if (vector.equals(node.vector)) {
            closestState.distanceSq = 0.0;
            closestState.node = node;
            return;
        }
        int axis = depth % 3;
        int compare = KDTree.compare(node.vector, vector, axis);
        double distanceSq = node.vector.distanceSquaredTo(vector);
        if (distanceSq < closestState.distanceSq) {
            closestState.node = node;
            closestState.distanceSq = distanceSq;
        }
        int newDepth = depth + 1;
        if (compare < 0) {
            this.closest0(closestState, node.one, vector, newDepth);
        } else {
            this.closest0(closestState, node.two, vector, newDepth);
        }
        double plane = KDTree.get(node.vector, axis);
        double component = KDTree.get(closestState.node.vector, axis);
        double planeDistance = Math.abs(component - plane);
        if (planeDistance * planeDistance < closestState.distanceSq) {
            if (compare < 0) {
                this.closest0(closestState, node.two, vector, newDepth);
            } else {
                this.closest0(closestState, node.one, vector, newDepth);
            }
        }
    }

    private void collect0(@Nonnull List<T> results, @Nullable Node<T> node, @Nonnull Vector3d vector, double distanceSq, int depth) {
        if (node == null) {
            return;
        }
        int axis = depth % 3;
        int compare = KDTree.compare(node.vector, vector, axis);
        double nodeDistanceSq = node.vector.distanceSquaredTo(vector);
        if (nodeDistanceSq < distanceSq) {
            int bound = node.data.size();
            for (int i = 0; i < bound; ++i) {
                Object data = node.data.get(i);
                if (!this.collectionFilter.test(data)) continue;
                results.add(data);
            }
        }
        int newDepth = depth + 1;
        if (compare < 0) {
            this.collect0(results, node.one, vector, distanceSq, newDepth);
        } else {
            this.collect0(results, node.two, vector, distanceSq, newDepth);
        }
        double plane = KDTree.get(node.vector, axis);
        double component = KDTree.get(vector, axis);
        double planeDistance = Math.abs(component - plane);
        if (planeDistance * planeDistance < distanceSq) {
            if (compare < 0) {
                this.collect0(results, node.two, vector, distanceSq, newDepth);
            } else {
                this.collect0(results, node.one, vector, distanceSq, newDepth);
            }
        }
    }

    private void collectCylinder0(@Nonnull List<T> results, @Nullable Node<T> node, @Nonnull Vector3d center, double radiusSq, double halfHeight, double radius, int depth) {
        double axisRadius;
        double dz;
        double dx;
        double xzDistanceSq;
        if (node == null) {
            return;
        }
        int axis = depth % 3;
        int compare = KDTree.compare(node.vector, center, axis);
        double dy = node.vector.y - center.y;
        if (Math.abs(dy) <= halfHeight && (xzDistanceSq = (dx = node.vector.x - center.x) * dx + (dz = node.vector.z - center.z) * dz) <= radiusSq) {
            int bound = node.data.size();
            for (int i = 0; i < bound; ++i) {
                Object data = node.data.get(i);
                if (!this.collectionFilter.test(data)) continue;
                results.add(data);
            }
        }
        int newDepth = depth + 1;
        if (compare < 0) {
            this.collectCylinder0(results, node.one, center, radiusSq, halfHeight, radius, newDepth);
        } else {
            this.collectCylinder0(results, node.two, center, radiusSq, halfHeight, radius, newDepth);
        }
        double plane = KDTree.get(node.vector, axis);
        double component = KDTree.get(center, axis);
        double d = axisRadius = axis == 2 ? halfHeight : radius;
        if (Math.abs(component - plane) <= axisRadius) {
            if (compare < 0) {
                this.collectCylinder0(results, node.two, center, radiusSq, halfHeight, radius, newDepth);
            } else {
                this.collectCylinder0(results, node.one, center, radiusSq, halfHeight, radius, newDepth);
            }
        }
    }

    private void collectBox0(@Nonnull List<T> results, @Nullable Node<T> node, @Nonnull Vector3d min, @Nonnull Vector3d max, int depth) {
        if (node == null) {
            return;
        }
        int axis = depth % 3;
        if (node.vector.x >= min.x && node.vector.x <= max.x && node.vector.y >= min.y && node.vector.y <= max.y && node.vector.z >= min.z && node.vector.z <= max.z) {
            int bound = node.data.size();
            for (int i = 0; i < bound; ++i) {
                Object data = node.data.get(i);
                if (!this.collectionFilter.test(data)) continue;
                results.add(data);
            }
        }
        int newDepth = depth + 1;
        double plane = KDTree.get(node.vector, axis);
        double minComponent = KDTree.get(min, axis);
        double maxComponent = KDTree.get(max, axis);
        if (maxComponent >= plane) {
            this.collectBox0(results, node.one, min, max, newDepth);
        }
        if (minComponent <= plane) {
            this.collectBox0(results, node.two, min, max, newDepth);
        }
    }

    private void ordered0(@Nonnull List<OrderedEntry<T>> results, @Nullable Node<T> node, @Nonnull Vector3d vector, double distanceSq, int depth) {
        if (node == null) {
            return;
        }
        int axis = depth % 3;
        int compare = KDTree.compare(node.vector, vector, axis);
        double nodeDistanceSq = node.vector.distanceSquaredTo(vector);
        if (nodeDistanceSq < distanceSq) {
            results.add(new OrderedEntry(nodeDistanceSq, node.data));
        }
        int newDepth = depth + 1;
        if (compare < 0) {
            this.ordered0(results, node.one, vector, distanceSq, newDepth);
        } else {
            this.ordered0(results, node.two, vector, distanceSq, newDepth);
        }
        double plane = KDTree.get(node.vector, axis);
        double component = KDTree.get(vector, axis);
        double planeDistance = Math.abs(component - plane);
        if (planeDistance * planeDistance < distanceSq) {
            if (compare < 0) {
                this.ordered0(results, node.two, vector, distanceSq, newDepth);
            } else {
                this.ordered0(results, node.one, vector, distanceSq, newDepth);
            }
        }
    }

    private void _internal_ordered3DAxis(@Nonnull List<OrderedEntry<T>> results, @Nullable Node<T> node, @Nonnull Vector3d center, double xSearchRadius, double ySearchRadius, double zSearchRadius, int depth) {
        double radius;
        boolean inCuboid;
        if (node == null) {
            return;
        }
        int axis = depth % 3;
        boolean bl = inCuboid = node.vector.x >= center.x - xSearchRadius && node.vector.x <= center.x + xSearchRadius && node.vector.y >= center.y - ySearchRadius && node.vector.y <= center.y + ySearchRadius && node.vector.z >= center.z - zSearchRadius && node.vector.z <= center.z + zSearchRadius;
        if (inCuboid) {
            double nodeDistanceSq = node.vector.distanceSquaredTo(center);
            results.add(new OrderedEntry(nodeDistanceSq, node.data));
        }
        int newDepth = depth + 1;
        int compare = KDTree.compare(node.vector, center, axis);
        Node primary = compare < 0 ? node.one : node.two;
        Node secondary = compare < 0 ? node.two : node.one;
        this._internal_ordered3DAxis(results, primary, center, xSearchRadius, ySearchRadius, zSearchRadius, newDepth);
        double plane = KDTree.get(node.vector, axis);
        double component = KDTree.get(center, axis);
        double d = axis == 0 ? xSearchRadius : (radius = axis == 1 ? zSearchRadius : ySearchRadius);
        if (Math.abs(component - plane) <= radius) {
            this._internal_ordered3DAxis(results, secondary, center, xSearchRadius, ySearchRadius, zSearchRadius, newDepth);
        }
    }

    private static int compare(@Nonnull Vector3d v1, @Nonnull Vector3d v2, int axis) {
        return switch (axis) {
            case 0 -> Double.compare(v1.x, v2.x);
            case 1 -> Double.compare(v1.z, v2.z);
            case 2 -> Double.compare(v1.y, v2.y);
            default -> throw new IllegalArgumentException("Invalid axis: " + axis);
        };
    }

    private static double get(@Nonnull Vector3d v, int axis) {
        return switch (axis) {
            case 0 -> v.x;
            case 1 -> v.z;
            case 2 -> v.y;
            default -> throw new IllegalArgumentException("Invalid axis: " + axis);
        };
    }

    private static class Node<T> {
        private Vector3d vector;
        private List<T> data;
        @Nullable
        private Node<T> one;
        @Nullable
        private Node<T> two;

        public Node(Vector3d vector, List<T> data) {
            this.vector = vector;
            this.data = data;
        }

        public void reset(Vector3d vector, List<T> data) {
            this.vector = vector;
            this.data = data;
            this.one = null;
            this.two = null;
        }

        @Nonnull
        public String dump(int depth) {
            int nextDepth = depth + 1;
            return "vector=" + String.valueOf(this.vector) + ", data=" + String.valueOf(this.data) + ",\n" + " ".repeat(depth) + "one=" + (this.one == null ? null : this.one.dump(nextDepth)) + ",\n" + " ".repeat(depth) + "two=" + (this.two == null ? null : this.two.dump(nextDepth));
        }
    }

    private static class ClosestState<T> {
        private Node<T> node;
        private double distanceSq;

        public ClosestState(Node<T> node, double distanceSq) {
            this.node = node;
            this.distanceSq = distanceSq;
        }
    }

    private static class OrderedEntry<T> {
        private final double distanceSq;
        private final List<T> values;

        public OrderedEntry(double distanceSq, List<T> values) {
            this.distanceSq = distanceSq;
            this.values = values;
        }
    }
}

