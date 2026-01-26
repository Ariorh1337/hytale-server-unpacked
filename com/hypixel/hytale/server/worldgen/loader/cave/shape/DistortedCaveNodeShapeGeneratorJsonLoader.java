/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.cave.shape;

import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.json.DoubleRangeJsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.procedurallib.logic.GeneralNoise;
import com.hypixel.hytale.procedurallib.supplier.IDoubleRange;
import com.hypixel.hytale.server.worldgen.SeedStringResource;
import com.hypixel.hytale.server.worldgen.cave.shape.CaveNodeShapeEnum;
import com.hypixel.hytale.server.worldgen.cave.shape.DistortedCaveNodeShape;
import com.hypixel.hytale.server.worldgen.cave.shape.distorted.DistortedShape;
import com.hypixel.hytale.server.worldgen.cave.shape.distorted.DistortedShapes;
import com.hypixel.hytale.server.worldgen.cave.shape.distorted.ShapeDistortion;
import com.hypixel.hytale.server.worldgen.loader.cave.shape.CaveNodeShapeGeneratorJsonLoader;
import com.hypixel.hytale.server.worldgen.loader.cave.shape.ShapeDistortionJsonLoader;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DistortedCaveNodeShapeGeneratorJsonLoader
extends CaveNodeShapeGeneratorJsonLoader {
    public DistortedCaveNodeShapeGeneratorJsonLoader(@Nonnull SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json) {
        super(seed.append(".DistortedCaveNodeShape"), dataFolder, json);
    }

    @Override
    @Nonnull
    public CaveNodeShapeEnum.CaveNodeShapeGenerator load() {
        return new DistortedCaveNodeShape.DistortedCaveNodeShapeGenerator(this.loadShape(), this.loadWidth(), this.loadHeight(), this.loadMidWidth(), this.loadMidHeight(), this.loadLength(), this.loadInheritParentRadius(), this.loadShapeDistortion(), this.loadInterpolation());
    }

    @Nonnull
    private DistortedShape.Factory loadShape() {
        DistortedShape.Factory shape;
        if (this.has("Shape") && (shape = DistortedShapes.getByName(this.get("Shape").getAsString())) != null) {
            return shape;
        }
        return Constants.DEFAULT_SHAPE;
    }

    @Nullable
    private IDoubleRange loadWidth() {
        if (this.has("RadiusX")) {
            return new DoubleRangeJsonLoader(this.seed, this.dataFolder, this.get("RadiusX"), 3.0).load();
        }
        return new DoubleRangeJsonLoader(this.seed, this.dataFolder, this.get("Width"), 3.0).load();
    }

    @Nullable
    private IDoubleRange loadHeight() {
        if (this.has("RadiusY")) {
            return new DoubleRangeJsonLoader(this.seed, this.dataFolder, this.get("RadiusY"), 3.0).load();
        }
        return new DoubleRangeJsonLoader(this.seed, this.dataFolder, this.get("Height"), 3.0).load();
    }

    @Nullable
    private IDoubleRange loadMidWidth() {
        IDoubleRange midWidth = null;
        if (this.has("MiddleWidth")) {
            midWidth = new DoubleRangeJsonLoader(this.seed, this.dataFolder, this.get("MiddleWidth"), 0.0).load();
        }
        return midWidth;
    }

    @Nullable
    private IDoubleRange loadMidHeight() {
        IDoubleRange midHeight = null;
        if (this.has("MiddleHeight")) {
            midHeight = new DoubleRangeJsonLoader(this.seed, this.dataFolder, this.get("MiddleHeight"), 0.0).load();
        }
        return midHeight;
    }

    @Nullable
    private IDoubleRange loadLength() {
        if (this.has("RadiusZ")) {
            return new DoubleRangeJsonLoader(this.seed, this.dataFolder, this.get("RadiusZ"), 3.0).load();
        }
        IDoubleRange length = null;
        if (this.has("Length")) {
            length = new DoubleRangeJsonLoader(this.seed, this.dataFolder, this.get("Length"), 5.0, 15.0).load();
        }
        return length;
    }

    private boolean loadInheritParentRadius() {
        boolean inherit = true;
        if (this.has("InheritParentRadius")) {
            inherit = this.get("InheritParentRadius").getAsBoolean();
        }
        return inherit;
    }

    @Nullable
    private ShapeDistortion loadShapeDistortion() {
        ShapeDistortion distortion = ShapeDistortion.DEFAULT;
        if (this.has("Distortion")) {
            distortion = new ShapeDistortionJsonLoader(this.seed, this.dataFolder, this.get("Distortion")).load();
        }
        return distortion;
    }

    private GeneralNoise.InterpolationFunction loadInterpolation() {
        GeneralNoise.InterpolationFunction interpolation = Constants.DEFAULT_INTERPOLATION;
        if (this.has("Interpolation")) {
            interpolation = GeneralNoise.InterpolationMode.valueOf((String)this.get((String)"Interpolation").getAsString()).function;
        }
        return interpolation;
    }

    public static interface Constants {
        public static final String KEY_SHAPE = "Shape";
        public static final String KEY_WIDTH = "Width";
        public static final String KEY_HEIGHT = "Height";
        public static final String KEY_MID_WIDTH = "MiddleWidth";
        public static final String KEY_MID_HEIGHT = "MiddleHeight";
        public static final String KEY_LENGTH = "Length";
        public static final String KEY_RADIUS_X = "RadiusX";
        public static final String KEY_RADIUS_Y = "RadiusY";
        public static final String KEY_RADIUS_Z = "RadiusZ";
        public static final String KEY_INHERIT_PARENT_RADIUS = "InheritParentRadius";
        public static final String KEY_DISTORTION = "Distortion";
        public static final String KEY_INTERPOLATION = "Interpolation";
        public static final DistortedShape.Factory DEFAULT_SHAPE = DistortedShapes.getDefault();
        public static final GeneralNoise.InterpolationFunction DEFAULT_INTERPOLATION = GeneralNoise.InterpolationMode.LINEAR.function;
    }
}

