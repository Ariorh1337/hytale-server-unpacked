/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.codec;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.shape.Cylinder;
import com.hypixel.hytale.math.shape.Ellipsoid;
import com.hypixel.hytale.math.shape.OriginShape;
import com.hypixel.hytale.math.shape.Shape;
import com.hypixel.hytale.math.vector.Vector3d;

public class ShapeCodecs {
    public static final CodecMapCodec<Shape> SHAPE = new CodecMapCodec();
    public static final BuilderCodec<Box> BOX = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(Box.class, Box::new).addField(new KeyedCodec<Vector3d>("Min", Vector3d.CODEC), (shape, min) -> shape.min.assign((Vector3d)min), shape -> shape.min)).addField(new KeyedCodec<Vector3d>("Max", Vector3d.CODEC), (shape, max) -> shape.max.assign((Vector3d)max), shape -> shape.max)).build();
    public static final BuilderCodec<Ellipsoid> ELLIPSOID = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(Ellipsoid.class, Ellipsoid::new).addField(new KeyedCodec<Double>("RadiusX", Codec.DOUBLE), (shape, radius) -> {
        shape.radiusX = radius;
    }, shape -> shape.radiusX)).addField(new KeyedCodec<Double>("RadiusY", Codec.DOUBLE), (shape, radius) -> {
        shape.radiusY = radius;
    }, shape -> shape.radiusY)).addField(new KeyedCodec<Double>("RadiusZ", Codec.DOUBLE), (shape, radius) -> {
        shape.radiusZ = radius;
    }, shape -> shape.radiusZ)).addField(new KeyedCodec<Double>("Radius", Codec.DOUBLE), Ellipsoid::assign, shape -> null)).build();
    public static final BuilderCodec<Cylinder> CYLINDER = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(Cylinder.class, Cylinder::new).addField(new KeyedCodec<Double>("Height", Codec.DOUBLE), (shape, height) -> {
        shape.height = height;
    }, shape -> shape.height)).addField(new KeyedCodec<Double>("RadiusX", Codec.DOUBLE), (shape, radiusX) -> {
        shape.radiusX = radiusX;
    }, shape -> shape.radiusX)).addField(new KeyedCodec<Double>("RadiusZ", Codec.DOUBLE), (shape, radiusZ) -> {
        shape.radiusZ = radiusZ;
    }, shape -> shape.radiusZ)).addField(new KeyedCodec<Double>("Radius", Codec.DOUBLE), Cylinder::assign, shape -> null)).build();
    public static final BuilderCodec<OriginShape<Shape>> ORIGIN_SHAPE = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(OriginShape.class, OriginShape::new).addField(new KeyedCodec<Vector3d>("Origin", Vector3d.CODEC), (shape, origin) -> shape.origin.assign((Vector3d)origin), shape -> shape.origin)).addField(new KeyedCodec<Shape>("Shape", SHAPE), (shape, childShape) -> {
        shape.shape = childShape;
    }, shape -> shape.shape)).build();

    static {
        SHAPE.register("Box", (Class<Shape>)Box.class, (Codec<Shape>)BOX);
        SHAPE.register("Ellipsoid", (Class<Shape>)Ellipsoid.class, (Codec<Shape>)ELLIPSOID);
        SHAPE.register("Cylinder", (Class<Shape>)Cylinder.class, (Codec<Shape>)CYLINDER);
        SHAPE.register("OriginShape", (Class<Shape>)OriginShape.class, (Codec<Shape>)ORIGIN_SHAPE);
    }
}

