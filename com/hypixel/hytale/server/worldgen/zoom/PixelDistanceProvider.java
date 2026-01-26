/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.zoom;

import com.hypixel.hytale.server.worldgen.zoom.PixelProvider;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import javax.annotation.Nonnull;

public class PixelDistanceProvider {
    private static final int TABLE_SIZE = 8;
    @Nonnull
    protected final PixelProvider image;
    protected final int width;
    protected final int height;
    protected final int cellsX;
    protected final int cellsY;
    @Nonnull
    protected final IPixelSet[] table;
    @Nonnull
    protected final IntSet pixels;

    public PixelDistanceProvider(@Nonnull PixelProvider image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.cellsX = image.getWidth() / 8;
        this.cellsY = image.getHeight() / 8;
        this.table = new IPixelSet[this.cellsX * this.cellsY];
        this.pixels = new IntOpenHashSet();
        this.prepareSegmentTable();
    }

    @Nonnull
    public IntSet getColors() {
        return this.pixels;
    }

    public double distanceSqToDifferentPixel(double ox, double oy, int px, int py) {
        px = this.clampX(px);
        py = this.clampY(py);
        int color = this.image.getPixel(px, py);
        int cellX = px / 8;
        int cellY = py / 8;
        double distance = Double.POSITIVE_INFINITY;
        int minX = Math.max(cellX - 1, 0);
        int maxX = Math.min(cellX + 2, this.cellsX);
        int minY = Math.max(cellY - 1, 0);
        int maxY = Math.min(cellY + 2, this.cellsY);
        for (int ix = minX; ix < maxX; ++ix) {
            for (int iy = minY; iy < maxY; ++iy) {
                double dist = this.distanceSqToDiffInSeq(ox, oy, color, ix, iy);
                if (!(dist < distance)) continue;
                distance = dist;
            }
        }
        return distance;
    }

    protected double distanceSqToDiffInSeq(double ox, double oy, int pixel, int cellX, int cellY) {
        double distSq = Double.POSITIVE_INFINITY;
        if (this.hasDifferentPixel(cellX, cellY, pixel)) {
            int offsetX = cellX * 8;
            int offsetY = cellY * 8;
            for (int ix = 0; ix < 8; ++ix) {
                int px = ix + offsetX;
                for (int iy = 0; iy < 8; ++iy) {
                    double dist;
                    int py = iy + offsetY;
                    if (pixel == this.image.getPixel(px, py) || !((dist = PixelDistanceProvider.distanceSqToPixel(ox, oy, px, py)) < distSq)) continue;
                    distSq = dist;
                }
            }
        }
        return distSq;
    }

    protected boolean hasDifferentPixel(int cellX, int cellY, int pixel) {
        IPixelSet pixelSet = this.table[this.cellIndex(cellX, cellY)];
        return !pixelSet.contains(pixel) || pixelSet.size() > 1;
    }

    private void prepareSegmentTable() {
        for (int cellX = 0; cellX < this.cellsX; ++cellX) {
            for (int cellY = 0; cellY < this.cellsY; ++cellY) {
                IntOpenHashSet colors = new IntOpenHashSet();
                int offsetX = cellX * 8;
                int offsetY = cellY * 8;
                for (int ix = 0; ix < 8; ++ix) {
                    for (int iy = 0; iy < 8; ++iy) {
                        int x = ix + offsetX;
                        int y = iy + offsetY;
                        if (x >= this.width || y >= this.height) continue;
                        colors.add(this.image.getPixel(x, y));
                    }
                }
                this.pixels.addAll(colors);
                this.table[this.cellIndex((int)cellX, (int)cellY)] = colors.size() == 1 ? new SinglePixelSet(colors.iterator().nextInt()) : new MultiplePixelSet(colors);
            }
        }
    }

    protected int clampX(int x) {
        if (x < 0) {
            return 0;
        }
        if (x >= this.width) {
            return this.width - 1;
        }
        return x;
    }

    protected int clampY(int y) {
        if (y < 0) {
            return 0;
        }
        if (y >= this.height) {
            return this.height - 1;
        }
        return y;
    }

    protected int cellIndex(int cellX, int cellY) {
        return cellX * this.cellsY + cellY;
    }

    private static double distanceSqToPixel(double ox, double oy, int px, int py) {
        double dx = Math.max(Math.max((double)px - ox, ox - (double)px - 1.0), 0.0);
        double dy = Math.max(Math.max((double)py - oy, oy - (double)py - 1.0), 0.0);
        return dx * dx + dy * dy;
    }

    private static interface IPixelSet {
        public boolean contains(int var1);

        public int size();
    }

    private static class SinglePixelSet
    implements IPixelSet {
        private final int pixel;

        SinglePixelSet(int pixel) {
            this.pixel = pixel;
        }

        @Override
        public boolean contains(int pixel) {
            return this.pixel == pixel;
        }

        @Override
        public int size() {
            return 1;
        }

        @Nonnull
        public String toString() {
            return "SinglePixelSet{pixel=" + this.pixel + "}";
        }
    }

    private static class MultiplePixelSet
    implements IPixelSet {
        private final IntSet pixels;

        MultiplePixelSet(IntSet pixels) {
            this.pixels = pixels;
        }

        @Override
        public boolean contains(int pixel) {
            return this.pixels.contains(pixel);
        }

        @Override
        public int size() {
            return this.pixels.size();
        }

        @Nonnull
        public String toString() {
            return "MultiplePixelSet{pixels=" + String.valueOf(this.pixels) + "}";
        }
    }
}

