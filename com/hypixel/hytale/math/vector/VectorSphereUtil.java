/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.vector;

import com.hypixel.fastutil.FastCollection;
import com.hypixel.hytale.function.consumer.IntBiObjectConsumer;
import com.hypixel.hytale.function.consumer.IntObjectConsumer;
import com.hypixel.hytale.function.consumer.IntTriObjectConsumer;
import com.hypixel.hytale.function.consumer.TriConsumer;
import com.hypixel.hytale.math.vector.Vector3d;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nonnull;

public class VectorSphereUtil {
    public static void forEachVector(Iterable<Vector3d> vectors, double originX, double originY, double originZ, double radius, Consumer<Vector3d> consumer) {
        VectorSphereUtil.forEachVector(vectors, originX, originY, originZ, radius, radius, radius, consumer);
    }

    public static void forEachVector(Iterable<Vector3d> vectors, double originX, double originY, double originZ, double radiusX, double radiusY, double radiusZ, Consumer<Vector3d> consumer) {
        VectorSphereUtil.forEachVector(vectors, Function.identity(), originX, originY, originZ, radiusX, radiusY, radiusZ, consumer);
    }

    public static <T> void forEachVector(Iterable<T> input, @Nonnull Function<T, Vector3d> func, double originX, double originY, double originZ, double radius, Consumer<T> consumer) {
        VectorSphereUtil.forEachVector(input, func, originX, originY, originZ, radius, radius, radius, consumer);
    }

    public static <T> void forEachVector(Iterable<T> input, @Nonnull Function<T, Vector3d> func, double originX, double originY, double originZ, double radiusX, double radiusY, double radiusZ, Consumer<T> consumer) {
        VectorSphereUtil.forEachVector(input, func, originX, originY, originZ, radiusX, radiusY, radiusZ, (T t, V1 c, V2 n0) -> c.accept(t), consumer, null);
    }

    public static <T, V> void forEachVector(Iterable<T> input, @Nonnull Function<T, Vector3d> func, double originX, double originY, double originZ, double radius, BiConsumer<T, V> consumer, V objV) {
        VectorSphereUtil.forEachVector(input, func, originX, originY, originZ, radius, radius, radius, consumer, objV);
    }

    public static <T, V> void forEachVector(Iterable<T> input, @Nonnull Function<T, Vector3d> func, double originX, double originY, double originZ, double radiusX, double radiusY, double radiusZ, BiConsumer<T, V> consumer, V objV) {
        VectorSphereUtil.forEachVector(input, func, originX, originY, originZ, radiusX, radiusY, radiusZ, (T t, V1 c, V2 objV2) -> c.accept(t, objV2), consumer, objV);
    }

    public static <T, V1, V2> void forEachVector(Iterable<T> input, @Nonnull Function<T, Vector3d> func, double originX, double originY, double originZ, double radius, @Nonnull TriConsumer<T, V1, V2> consumer, V1 objV1, V2 objV2) {
        VectorSphereUtil.forEachVector(input, func, originX, originY, originZ, radius, radius, radius, consumer, objV1, objV2);
    }

    public static <T, V1, V2> void forEachVector(Iterable<T> input, @Nonnull Function<T, Vector3d> func, double originX, double originY, double originZ, double radiusX, double radiusY, double radiusZ, @Nonnull TriConsumer<T, V1, V2> consumer, V1 objV1, V2 objV2) {
        if (input instanceof FastCollection) {
            FastCollection fastCollection = (FastCollection)input;
            fastCollection.forEach((obj, _func, _originX, _originY, _originZ, _radiusX, _radiusY, _radiusZ, _consumer, _objV1, _objV2) -> {
                Vector3d vector = (Vector3d)_func.apply(obj);
                if (VectorSphereUtil.isInside(_originX, _originY, _originZ, _radiusX, _radiusY, _radiusZ, vector)) {
                    _consumer.accept(obj, _objV1, _objV2);
                }
            }, func, originX, originY, originZ, radiusX, radiusY, radiusZ, consumer, objV1, objV2);
        } else {
            for (T obj2 : input) {
                Vector3d vector = func.apply(obj2);
                if (!VectorSphereUtil.isInside(originX, originY, originZ, radiusX, radiusY, radiusZ, vector)) continue;
                consumer.accept(obj2, objV1, objV2);
            }
        }
    }

    public static <T> void forEachVector(@Nonnull Int2ObjectMap<T> input, @Nonnull Function<T, Vector3d> func, double originX, double originY, double originZ, double radius, IntObjectConsumer<T> consumer) {
        VectorSphereUtil.forEachVector(input, func, originX, originY, originZ, radius, radius, radius, consumer);
    }

    public static <T> void forEachVector(@Nonnull Int2ObjectMap<T> input, @Nonnull Function<T, Vector3d> func, double originX, double originY, double originZ, double radiusX, double radiusY, double radiusZ, IntObjectConsumer<T> consumer) {
        VectorSphereUtil.forEachVector(input, func, originX, originY, originZ, radiusX, radiusY, radiusZ, (int i, T t, V1 c, V2 n0) -> c.accept(i, t), consumer, null);
    }

    public static <T, V> void forEachVector(@Nonnull Int2ObjectMap<T> input, @Nonnull Function<T, Vector3d> func, double originX, double originY, double originZ, double radius, IntBiObjectConsumer<T, V> consumer, V objV) {
        VectorSphereUtil.forEachVector(input, func, originX, originY, originZ, radius, radius, radius, consumer, objV);
    }

    public static <T, V> void forEachVector(@Nonnull Int2ObjectMap<T> input, @Nonnull Function<T, Vector3d> func, double originX, double originY, double originZ, double radiusX, double radiusY, double radiusZ, IntBiObjectConsumer<T, V> consumer, V objV) {
        VectorSphereUtil.forEachVector(input, func, originX, originY, originZ, radiusX, radiusY, radiusZ, (int i, T t, V1 objV1, V2 c) -> c.accept(i, t, objV1), objV, consumer);
    }

    public static <T, V1, V2> void forEachVector(@Nonnull Int2ObjectMap<T> input, @Nonnull Function<T, Vector3d> func, double originX, double originY, double originZ, double radius, @Nonnull IntTriObjectConsumer<T, V1, V2> consumer, V1 objV1, V2 objV2) {
        VectorSphereUtil.forEachVector(input, func, originX, originY, originZ, radius, radius, radius, consumer, objV1, objV2);
    }

    public static <T, V1, V2> void forEachVector(@Nonnull Int2ObjectMap<T> input, @Nonnull Function<T, Vector3d> func, double originX, double originY, double originZ, double radiusX, double radiusY, double radiusZ, @Nonnull IntTriObjectConsumer<T, V1, V2> consumer, V1 objV1, V2 objV2) {
        for (Int2ObjectMap.Entry entry : input.int2ObjectEntrySet()) {
            int key = entry.getIntKey();
            Object value = entry.getValue();
            Vector3d vector = func.apply(value);
            if (!VectorSphereUtil.isInside(originX, originY, originZ, radiusX, radiusY, radiusZ, vector)) continue;
            consumer.accept(key, value, objV1, objV2);
        }
    }

    public static boolean isInside(double originX, double originY, double originZ, double radius, @Nonnull Vector3d vector) {
        return VectorSphereUtil.isInside(originX, originY, originZ, radius, radius, radius, vector);
    }

    public static boolean isInside(double originX, double originY, double originZ, double radiusX, double radiusY, double radiusZ, @Nonnull Vector3d vector) {
        double z;
        double zRatio;
        double y;
        double yRatio;
        double x = vector.getX() - originX;
        double xRatio = x / radiusX;
        return xRatio * xRatio + (yRatio = (y = vector.getY() - originY) / radiusY) * yRatio + (zRatio = (z = vector.getZ() - originZ) / radiusZ) * zRatio <= 1.0;
    }
}

