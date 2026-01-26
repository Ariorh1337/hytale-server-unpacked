/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.climate.util;

import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.server.worldgen.climate.util.DoubleMap;
import com.hypixel.hytale.server.worldgen.climate.util.IntMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import javax.annotation.Nonnull;

public class DistanceTransform {
    private static final IntArrayList EMPTY_LIST = new IntArrayList();
    private static final int[] DX = new int[]{-1, 1, 0, 0, -1, -1, 1, 1};
    private static final int[] DY = new int[]{0, 0, -1, 1, -1, 1, -1, 1};
    private static final double[] COST = new double[]{1.0, 1.0, 1.0, 1.0, Math.sqrt(2.0), Math.sqrt(2.0), Math.sqrt(2.0), Math.sqrt(2.0)};

    public static void apply(@Nonnull IntMap source, @Nonnull DoubleMap dest, double radius) {
        if (radius <= 0.0) {
            throw new IllegalArgumentException("radius must be > 0");
        }
        int width = source.width;
        int height = source.height;
        int size = width * height;
        Int2ObjectOpenHashMap<IntArrayList> regions = new Int2ObjectOpenHashMap<IntArrayList>();
        Int2ObjectOpenHashMap<IntArrayList> boundaries = new Int2ObjectOpenHashMap<IntArrayList>();
        for (int y = 0; y < height; ++y) {
            block1: for (int x = 0; x < width; ++x) {
                int index = source.index(x, y);
                int value = source.at(index);
                regions.computeIfAbsent(value, k -> new IntArrayList()).add(index);
                for (int i = 0; i < 4; ++i) {
                    int neighborIndex;
                    int nx = x + DX[i];
                    int ny = y + DY[i];
                    if (nx < 0 || nx >= width || ny < 0 || ny >= height || source.at(neighborIndex = source.index(nx, ny)) == value) continue;
                    boundaries.computeIfAbsent(value, k -> new IntArrayList()).add(index);
                    continue block1;
                }
            }
        }
        double[] dist = new double[size];
        PriorityQueue<Node> queue = new PriorityQueue<Node>(Node::sort);
        for (Int2ObjectMap.Entry entry : regions.int2ObjectEntrySet()) {
            int index;
            int i;
            int id = entry.getIntKey();
            IntArrayList region = (IntArrayList)entry.getValue();
            IntArrayList boundary = boundaries.getOrDefault(id, EMPTY_LIST);
            if (boundary.isEmpty()) {
                for (i = 0; i < region.size(); ++i) {
                    dest.set(region.getInt(i), 1.0);
                }
                continue;
            }
            Arrays.fill(dist, radius);
            for (i = 0; i < boundary.size(); ++i) {
                index = boundary.getInt(i);
                dist[index] = 0.0;
                queue.offer(new Node(index, 0.0));
            }
            while (!queue.isEmpty()) {
                Node node = (Node)queue.poll();
                index = node.index;
                if (node.distance > dist[index]) continue;
                int cx = index % width;
                int cy = index / width;
                for (int i2 = 0; i2 < DX.length; ++i2) {
                    double distance;
                    int neighborIndex;
                    int neighborId;
                    int nx = cx + DX[i2];
                    int ny = cy + DY[i2];
                    if (nx < 0 || nx >= width || ny < 0 || ny >= height || (neighborId = source.at(neighborIndex = source.index(nx, ny))) != id || !((distance = node.distance + COST[i2]) < dist[neighborIndex])) continue;
                    dist[neighborIndex] = distance;
                    queue.offer(new Node(neighborIndex, distance));
                }
            }
            for (i = 0; i < region.size(); ++i) {
                index = region.getInt(i);
                double value = MathUtil.clamp(dist[index], 0.0, radius);
                dest.set(index, value / radius);
            }
        }
    }

    private record Node(int index, double distance) {
        public static int sort(Node a, Node b) {
            return Double.compare(a.distance, b.distance);
        }
    }
}

