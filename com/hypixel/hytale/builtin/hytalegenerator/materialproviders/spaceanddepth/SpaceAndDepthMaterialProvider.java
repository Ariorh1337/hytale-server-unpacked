/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.materialproviders.spaceanddepth;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SpaceAndDepthMaterialProvider<V>
extends MaterialProvider<V> {
    @Nonnull
    private final LayerContextType layerContextType;
    @Nonnull
    private final Layer<V>[] layers;
    @Nonnull
    private final Condition condition;
    private final int maxDistance;

    public SpaceAndDepthMaterialProvider(@Nonnull LayerContextType layerContextType, @Nonnull List<Layer<V>> layers, @Nonnull Condition condition, int maxDistance) {
        this.layerContextType = layerContextType;
        this.maxDistance = maxDistance;
        this.layers = new Layer[layers.size()];
        for (int i = 0; i < layers.size(); ++i) {
            Layer<V> l = layers.get(i);
            if (l == null) {
                LoggerUtil.getLogger().warning("Couldn't retrieve layer with index " + i);
                continue;
            }
            this.layers[i] = l;
        }
        this.condition = condition;
    }

    @Override
    @Nullable
    public V getVoxelTypeAt(@Nonnull MaterialProvider.Context context) {
        int distance;
        switch (this.layerContextType.ordinal()) {
            default: {
                throw new MatchException(null, null);
            }
            case 0: {
                int n = context.depthIntoFloor;
                break;
            }
            case 1: {
                int n = distance = context.depthIntoCeiling;
            }
        }
        if (distance > this.maxDistance) {
            return null;
        }
        if (!this.condition.qualifies(context.position.x, context.position.y, context.position.z, context.depthIntoFloor, context.depthIntoCeiling, context.spaceAboveFloor, context.spaceBelowCeiling)) {
            return null;
        }
        MaterialProvider<V> material = null;
        int depthAccumulator = 0;
        for (Layer<V> l : this.layers) {
            int layerDepth = l.getThicknessAt(context.position.x, context.position.y, context.position.z, context.depthIntoFloor, context.depthIntoCeiling, context.spaceAboveFloor, context.spaceBelowCeiling, context.distanceToBiomeEdge);
            int nextDepthAccumulator = depthAccumulator + layerDepth;
            if (distance > depthAccumulator && distance <= nextDepthAccumulator) {
                material = l.getMaterialProvider();
                break;
            }
            depthAccumulator = nextDepthAccumulator;
        }
        if (material == null) {
            return null;
        }
        return material.getVoxelTypeAt(context);
    }

    public static enum LayerContextType {
        DEPTH_INTO_FLOOR,
        DEPTH_INTO_CEILING;

        public static final Codec<LayerContextType> CODEC;

        static {
            CODEC = new EnumCodec<LayerContextType>(LayerContextType.class, EnumCodec.EnumStyle.LEGACY);
        }
    }

    public static abstract class Layer<V> {
        public abstract int getThicknessAt(int var1, int var2, int var3, int var4, int var5, int var6, int var7, double var8);

        @Nullable
        public abstract MaterialProvider<V> getMaterialProvider();
    }

    public static interface Condition {
        public boolean qualifies(int var1, int var2, int var3, int var4, int var5, int var6, int var7);
    }
}

