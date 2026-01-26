/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.objectives.config.worldlocationproviders;

import com.hypixel.hytale.builtin.adventure.objectives.config.worldlocationproviders.WorldLocationProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.util.TrigMathUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LocationRadiusProvider
extends WorldLocationProvider {
    public static final BuilderCodec<LocationRadiusProvider> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(LocationRadiusProvider.class, LocationRadiusProvider::new, BASE_CODEC).append(new KeyedCodec<Integer>("MinRadius", Codec.INTEGER), (locationRadiusCondition, integer) -> {
        locationRadiusCondition.minRadius = integer;
    }, locationRadiusCondition -> locationRadiusCondition.minRadius).addValidator(Validators.greaterThan(0)).add()).append(new KeyedCodec<Integer>("MaxRadius", Codec.INTEGER), (locationRadiusCondition, integer) -> {
        locationRadiusCondition.maxRadius = integer;
    }, locationRadiusCondition -> locationRadiusCondition.maxRadius).addValidator(Validators.greaterThanOrEqual(1)).add()).afterDecode(locationRadiusCondition -> {
        if (locationRadiusCondition.minRadius > locationRadiusCondition.maxRadius) {
            throw new IllegalArgumentException("LocationRadiusCondition.MinRadius (" + locationRadiusCondition.minRadius + ") needs to be greater than LocationRadiusCondition.MaxRadius (" + locationRadiusCondition.maxRadius + ")");
        }
    })).build();
    protected int minRadius = 10;
    protected int maxRadius = 50;

    @Override
    @Nonnull
    public Vector3i runCondition(@Nonnull World world, @Nonnull Vector3i position) {
        double angle = Math.random() * 6.2831854820251465;
        int radius = MathUtil.randomInt(this.minRadius, this.maxRadius);
        Vector3i newPosition = position.clone();
        newPosition.add((int)((float)radius * TrigMathUtil.cos(angle)), 0, (int)((float)radius * TrigMathUtil.sin(angle)));
        newPosition.y = ((WorldChunk)world.getChunk(ChunkUtil.indexChunkFromBlock(newPosition.x, newPosition.z))).getHeight(newPosition.x, newPosition.z);
        return newPosition;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LocationRadiusProvider that = (LocationRadiusProvider)o;
        if (this.minRadius != that.minRadius) {
            return false;
        }
        return this.maxRadius == that.maxRadius;
    }

    @Override
    public int hashCode() {
        int result = this.minRadius;
        result = 31 * result + this.maxRadius;
        return result;
    }

    @Override
    @Nonnull
    public String toString() {
        return "LocationRadiusProvider{minRadius=" + this.minRadius + ", maxRadius=" + this.maxRadius + "} " + super.toString();
    }
}

