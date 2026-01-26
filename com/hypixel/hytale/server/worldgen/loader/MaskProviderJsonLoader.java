/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader;

import com.google.gson.JsonElement;
import com.hypixel.hytale.math.vector.Vector2i;
import com.hypixel.hytale.procedurallib.json.CoordinateRandomizerJsonLoader;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.server.worldgen.SeedStringResource;
import com.hypixel.hytale.server.worldgen.chunk.MaskProvider;
import com.hypixel.hytale.server.worldgen.zoom.FuzzyZoom;
import com.hypixel.hytale.server.worldgen.zoom.PixelProvider;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import javax.imageio.ImageIO;

public class MaskProviderJsonLoader
extends JsonLoader<SeedStringResource, MaskProvider> {
    protected final Path file;
    protected final Vector2i zoomSize;
    protected final Vector2i worldOffset;

    public MaskProviderJsonLoader(@Nonnull SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json, Path file, Vector2i zoomSize, Vector2i worldOffset) {
        super(seed.append(".MaskProvider"), dataFolder, json);
        this.file = file;
        this.zoomSize = zoomSize;
        this.worldOffset = worldOffset;
    }

    @Override
    @Nonnull
    public MaskProvider load() {
        try {
            BufferedImage mask = MaskProviderJsonLoader.loadImage(this.file);
            return new MaskProvider(this.loadFuzzyZoom(mask));
        }
        catch (Throwable e) {
            throw new Error("Error while loading MaskProvider in " + String.valueOf(this.file.toAbsolutePath()), e);
        }
    }

    public static BufferedImage loadImage(@Nonnull Path file) throws IOException {
        try {
            return ImageIO.read(Files.newInputStream(file, new OpenOption[0]));
        }
        catch (IOException e) {
            throw new IOException("Failed to load image " + String.valueOf(file), e);
        }
    }

    @Nonnull
    protected FuzzyZoom loadFuzzyZoom(@Nonnull BufferedImage mask) {
        return new FuzzyZoom(new CoordinateRandomizerJsonLoader(this.seed, this.dataFolder, this.json).load(), new PixelProvider(mask), (double)this.zoomSize.getX() / (double)mask.getWidth(), (double)this.zoomSize.getY() / (double)mask.getHeight(), this.worldOffset.x, this.worldOffset.y);
    }
}

