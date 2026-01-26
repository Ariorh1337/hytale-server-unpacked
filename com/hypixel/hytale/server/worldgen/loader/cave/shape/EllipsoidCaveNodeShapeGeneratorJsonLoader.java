/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.cave.shape;

import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.json.DoubleRangeJsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.procedurallib.supplier.IDoubleRange;
import com.hypixel.hytale.server.worldgen.SeedStringResource;
import com.hypixel.hytale.server.worldgen.cave.shape.EllipsoidCaveNodeShape;
import com.hypixel.hytale.server.worldgen.loader.cave.shape.CaveNodeShapeGeneratorJsonLoader;
import java.nio.file.Path;
import java.util.Objects;
import javax.annotation.Nonnull;

public class EllipsoidCaveNodeShapeGeneratorJsonLoader
extends CaveNodeShapeGeneratorJsonLoader {
    public EllipsoidCaveNodeShapeGeneratorJsonLoader(@Nonnull SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json) {
        super(seed.append(".EllipsoidCaveNodeShapeGenerator"), dataFolder, json);
    }

    @Override
    @Nonnull
    public EllipsoidCaveNodeShape.EllipsoidCaveNodeShapeGenerator load() {
        IDoubleRange radiusX = null;
        IDoubleRange radiusY = null;
        IDoubleRange radiusZ = null;
        if (this.has("Radius")) {
            radiusY = radiusZ = new DoubleRangeJsonLoader(this.seed, this.dataFolder, this.get("Radius"), 5.0).load();
            radiusX = radiusZ;
        }
        if (this.has("RadiusX")) {
            radiusX = new DoubleRangeJsonLoader(this.seed, this.dataFolder, this.get("RadiusX"), 5.0).load();
        }
        if (this.has("RadiusY")) {
            radiusY = new DoubleRangeJsonLoader(this.seed, this.dataFolder, this.get("RadiusY"), 5.0).load();
        }
        if (this.has("RadiusZ")) {
            radiusZ = new DoubleRangeJsonLoader(this.seed, this.dataFolder, this.get("RadiusZ"), 5.0).load();
        }
        Objects.requireNonNull(radiusX, "RadiusX");
        Objects.requireNonNull(radiusY, "RadiusY");
        Objects.requireNonNull(radiusZ, "RadiusZ");
        return new EllipsoidCaveNodeShape.EllipsoidCaveNodeShapeGenerator(radiusX, radiusY, radiusZ);
    }

    public static interface Constants {
        public static final String KEY_RADIUS = "Radius";
        public static final String KEY_RADIUS_X = "RadiusX";
        public static final String KEY_RADIUS_Y = "RadiusY";
        public static final String KEY_RADIUS_Z = "RadiusZ";
        public static final String ERROR_RADIUS_NOT_SET = "%s was not set for Ellipsoid!";
    }
}

