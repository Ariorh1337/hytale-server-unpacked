/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.framework.cartas;

import com.hypixel.hytale.builtin.hytalegenerator.framework.interfaces.functions.TriCarta;
import com.hypixel.hytale.builtin.hytalegenerator.framework.interfaces.functions.TriDoubleFunction;
import com.hypixel.hytale.builtin.hytalegenerator.framework.math.Calculator;
import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ImageCarta<R>
extends TriCarta<R> {
    private int[] rgbArray;
    private int width;
    private int height;
    private TriDoubleFunction<Double> functionX;
    private TriDoubleFunction<Double> functionY;
    private Map<Integer, R> rgbToTerrainMap;
    private List<R> allPossibleValues;

    private ImageCarta() {
    }

    @Override
    @Nullable
    public R apply(int x, int y, int z, @Nonnull WorkerIndexer.Id tHreadId) {
        Objects.requireNonNull(x);
        Objects.requireNonNull(y);
        Objects.requireNonNull(z);
        int sampleX = Calculator.toNearestInt(this.functionX.apply(x, y, z) * (double)this.width);
        sampleX = sampleX < 0 ? 0 : Math.min(sampleX, this.width - 1);
        int sampleY = Calculator.toNearestInt(this.functionY.apply(x, y, z) * (double)this.height);
        sampleY = sampleY < 0 ? 0 : Math.min(sampleY, this.height - 1);
        int rgb = this.rgbArray[sampleX + sampleY * this.width];
        if (!this.rgbToTerrainMap.containsKey(rgb)) {
            return null;
        }
        return this.rgbToTerrainMap.get(rgb);
    }

    @Override
    public List<R> allPossibleValues() {
        return this.allPossibleValues;
    }

    public static int greenFromRgb(int rgb) {
        return (rgb & 0xFF00) >> 8;
    }

    public static int redFromRgb(int rgb) {
        return (rgb & 0xFF0000) >> 16;
    }

    public static int blueFromRgb(int rgb) {
        return rgb & 0xFF;
    }

    public static int coloursToRgb(int red, int green, int blue) {
        int rgb = red << 16;
        rgb += green << 8;
        return rgb += blue;
    }

    @Nonnull
    public String toString() {
        return "ImageCarta{rgbArray=" + Arrays.toString(this.rgbArray) + ", width=" + this.width + ", height=" + this.height + ", functionX=" + String.valueOf(this.functionX) + ", functionY=" + String.valueOf(this.functionY) + ", rgbToTerrainMap=" + String.valueOf(this.rgbToTerrainMap) + ", allPossibleValues=" + String.valueOf(this.allPossibleValues) + "}";
    }

    public static class Builder<R> {
        @Nonnull
        private final Map<Integer, R> rgbToTerrainMap = new HashMap<Integer, R>();
        private BufferedImage bufferedImage;
        private boolean bufferedImageCheck;
        private TriDoubleFunction<Double> noiseX;
        private TriDoubleFunction<Double> noiseY;
        private boolean noiseCheck;

        @Nonnull
        public ImageCarta<R> build() {
            if (!this.bufferedImageCheck || !this.noiseCheck) {
                throw new IllegalStateException("incomplete builder");
            }
            ImageCarta instance = new ImageCarta();
            instance.rgbToTerrainMap = this.rgbToTerrainMap;
            instance.functionX = this.noiseX;
            instance.functionY = this.noiseY;
            instance.allPossibleValues = new ArrayList(1);
            instance.allPossibleValues.addAll(this.rgbToTerrainMap.values());
            instance.width = this.bufferedImage.getWidth();
            instance.height = this.bufferedImage.getHeight();
            instance.rgbArray = new int[instance.width * instance.height];
            for (int x = 0; x < instance.width; ++x) {
                for (int y = 0; y < instance.height; ++y) {
                    instance.rgbArray[x + y * instance.width] = 0xFFFFFF & this.bufferedImage.getRGB(x, y);
                }
            }
            return instance;
        }

        @Nonnull
        public Builder<R> withImage(BufferedImage image) {
            Objects.requireNonNull(image);
            this.bufferedImage = image;
            this.bufferedImageCheck = true;
            return this;
        }

        @Nonnull
        public Builder<R> withNoiseFunctions(TriDoubleFunction<Double> noiseX, TriDoubleFunction<Double> noiseY) {
            Objects.requireNonNull(noiseX);
            Objects.requireNonNull(noiseY);
            this.noiseX = noiseX;
            this.noiseY = noiseY;
            this.noiseCheck = true;
            return this;
        }

        @Nonnull
        public Builder<R> addTerrainRgb(int rgb, @Nonnull R terrain) {
            this.rgbToTerrainMap.put(rgb, terrain);
            return this;
        }
    }
}

