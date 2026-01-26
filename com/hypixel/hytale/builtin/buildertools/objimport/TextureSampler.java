/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.objimport;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;

public final class TextureSampler {
    private static final Map<Path, BufferedImage> textureCache = new HashMap<Path, BufferedImage>();

    private TextureSampler() {
    }

    @Nullable
    public static BufferedImage loadTexture(@Nonnull Path path) {
        if (!Files.exists(path, new LinkOption[0])) {
            return null;
        }
        BufferedImage cached = textureCache.get(path);
        if (cached != null) {
            return cached;
        }
        try {
            BufferedImage image = ImageIO.read(path.toFile());
            if (image != null) {
                textureCache.put(path, image);
            }
            return image;
        }
        catch (IOException e) {
            return null;
        }
    }

    @Nonnull
    public static int[] sampleAt(@Nonnull BufferedImage texture, float u, float v) {
        u -= (float)Math.floor(u);
        v -= (float)Math.floor(v);
        v = 1.0f - v;
        int width = texture.getWidth();
        int height = texture.getHeight();
        int x = Math.min((int)(u * (float)width), width - 1);
        int y = Math.min((int)(v * (float)height), height - 1);
        int rgb = texture.getRGB(x, y);
        return new int[]{rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF};
    }

    public static int sampleAlphaAt(@Nonnull BufferedImage texture, float u, float v) {
        if (!texture.getColorModel().hasAlpha()) {
            return 255;
        }
        u -= (float)Math.floor(u);
        v -= (float)Math.floor(v);
        v = 1.0f - v;
        int width = texture.getWidth();
        int height = texture.getHeight();
        int x = Math.min((int)(u * (float)width), width - 1);
        int y = Math.min((int)(v * (float)height), height - 1);
        int rgba = texture.getRGB(x, y);
        return rgba >> 24 & 0xFF;
    }

    public static void clearCache() {
        textureCache.clear();
    }

    @Nullable
    public static int[] getAverageColor(@Nonnull Path path) {
        if (!Files.exists(path, new LinkOption[0])) {
            return null;
        }
        try {
            BufferedImage image = ImageIO.read(path.toFile());
            if (image == null) {
                return null;
            }
            long totalR = 0L;
            long totalG = 0L;
            long totalB = 0L;
            int count = 0;
            int width = image.getWidth();
            int height = image.getHeight();
            boolean hasAlpha = image.getColorModel().hasAlpha();
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    int alpha;
                    int rgba = image.getRGB(x, y);
                    if (hasAlpha && (alpha = rgba >> 24 & 0xFF) == 0) continue;
                    int r = rgba >> 16 & 0xFF;
                    int g = rgba >> 8 & 0xFF;
                    int b = rgba & 0xFF;
                    totalR += (long)r;
                    totalG += (long)g;
                    totalB += (long)b;
                    ++count;
                }
            }
            if (count == 0) {
                return null;
            }
            return new int[]{(int)(totalR / (long)count), (int)(totalG / (long)count), (int)(totalB / (long)count)};
        }
        catch (IOException e) {
            return null;
        }
    }
}

