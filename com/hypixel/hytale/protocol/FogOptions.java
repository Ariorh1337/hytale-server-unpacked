/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class FogOptions {
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 18;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 18;
    public static final int MAX_SIZE = 18;
    public boolean ignoreFogLimits;
    public float effectiveViewDistanceMultiplier;
    public float fogFarViewDistance;
    public float fogHeightCameraOffset;
    public boolean fogHeightCameraOverriden;
    public float fogHeightCameraFixed;

    public FogOptions() {
    }

    public FogOptions(boolean ignoreFogLimits, float effectiveViewDistanceMultiplier, float fogFarViewDistance, float fogHeightCameraOffset, boolean fogHeightCameraOverriden, float fogHeightCameraFixed) {
        this.ignoreFogLimits = ignoreFogLimits;
        this.effectiveViewDistanceMultiplier = effectiveViewDistanceMultiplier;
        this.fogFarViewDistance = fogFarViewDistance;
        this.fogHeightCameraOffset = fogHeightCameraOffset;
        this.fogHeightCameraOverriden = fogHeightCameraOverriden;
        this.fogHeightCameraFixed = fogHeightCameraFixed;
    }

    public FogOptions(@Nonnull FogOptions other) {
        this.ignoreFogLimits = other.ignoreFogLimits;
        this.effectiveViewDistanceMultiplier = other.effectiveViewDistanceMultiplier;
        this.fogFarViewDistance = other.fogFarViewDistance;
        this.fogHeightCameraOffset = other.fogHeightCameraOffset;
        this.fogHeightCameraOverriden = other.fogHeightCameraOverriden;
        this.fogHeightCameraFixed = other.fogHeightCameraFixed;
    }

    @Nonnull
    public static FogOptions deserialize(@Nonnull ByteBuf buf, int offset) {
        FogOptions obj = new FogOptions();
        obj.ignoreFogLimits = buf.getByte(offset + 0) != 0;
        obj.effectiveViewDistanceMultiplier = buf.getFloatLE(offset + 1);
        obj.fogFarViewDistance = buf.getFloatLE(offset + 5);
        obj.fogHeightCameraOffset = buf.getFloatLE(offset + 9);
        obj.fogHeightCameraOverriden = buf.getByte(offset + 13) != 0;
        obj.fogHeightCameraFixed = buf.getFloatLE(offset + 14);
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 18;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        buf.writeByte(this.ignoreFogLimits ? 1 : 0);
        buf.writeFloatLE(this.effectiveViewDistanceMultiplier);
        buf.writeFloatLE(this.fogFarViewDistance);
        buf.writeFloatLE(this.fogHeightCameraOffset);
        buf.writeByte(this.fogHeightCameraOverriden ? 1 : 0);
        buf.writeFloatLE(this.fogHeightCameraFixed);
    }

    public int computeSize() {
        return 18;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 18) {
            return ValidationResult.error("Buffer too small: expected at least 18 bytes");
        }
        return ValidationResult.OK;
    }

    public FogOptions clone() {
        FogOptions copy = new FogOptions();
        copy.ignoreFogLimits = this.ignoreFogLimits;
        copy.effectiveViewDistanceMultiplier = this.effectiveViewDistanceMultiplier;
        copy.fogFarViewDistance = this.fogFarViewDistance;
        copy.fogHeightCameraOffset = this.fogHeightCameraOffset;
        copy.fogHeightCameraOverriden = this.fogHeightCameraOverriden;
        copy.fogHeightCameraFixed = this.fogHeightCameraFixed;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FogOptions)) {
            return false;
        }
        FogOptions other = (FogOptions)obj;
        return this.ignoreFogLimits == other.ignoreFogLimits && this.effectiveViewDistanceMultiplier == other.effectiveViewDistanceMultiplier && this.fogFarViewDistance == other.fogFarViewDistance && this.fogHeightCameraOffset == other.fogHeightCameraOffset && this.fogHeightCameraOverriden == other.fogHeightCameraOverriden && this.fogHeightCameraFixed == other.fogHeightCameraFixed;
    }

    public int hashCode() {
        return Objects.hash(this.ignoreFogLimits, Float.valueOf(this.effectiveViewDistanceMultiplier), Float.valueOf(this.fogFarViewDistance), Float.valueOf(this.fogHeightCameraOffset), this.fogHeightCameraOverriden, Float.valueOf(this.fogHeightCameraFixed));
    }
}

