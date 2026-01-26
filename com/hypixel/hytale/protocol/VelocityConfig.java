/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.VelocityThresholdStyle;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class VelocityConfig {
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 21;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 21;
    public static final int MAX_SIZE = 21;
    public float groundResistance;
    public float groundResistanceMax;
    public float airResistance;
    public float airResistanceMax;
    public float threshold;
    @Nonnull
    public VelocityThresholdStyle style = VelocityThresholdStyle.Linear;

    public VelocityConfig() {
    }

    public VelocityConfig(float groundResistance, float groundResistanceMax, float airResistance, float airResistanceMax, float threshold, @Nonnull VelocityThresholdStyle style) {
        this.groundResistance = groundResistance;
        this.groundResistanceMax = groundResistanceMax;
        this.airResistance = airResistance;
        this.airResistanceMax = airResistanceMax;
        this.threshold = threshold;
        this.style = style;
    }

    public VelocityConfig(@Nonnull VelocityConfig other) {
        this.groundResistance = other.groundResistance;
        this.groundResistanceMax = other.groundResistanceMax;
        this.airResistance = other.airResistance;
        this.airResistanceMax = other.airResistanceMax;
        this.threshold = other.threshold;
        this.style = other.style;
    }

    @Nonnull
    public static VelocityConfig deserialize(@Nonnull ByteBuf buf, int offset) {
        VelocityConfig obj = new VelocityConfig();
        obj.groundResistance = buf.getFloatLE(offset + 0);
        obj.groundResistanceMax = buf.getFloatLE(offset + 4);
        obj.airResistance = buf.getFloatLE(offset + 8);
        obj.airResistanceMax = buf.getFloatLE(offset + 12);
        obj.threshold = buf.getFloatLE(offset + 16);
        obj.style = VelocityThresholdStyle.fromValue(buf.getByte(offset + 20));
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 21;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        buf.writeFloatLE(this.groundResistance);
        buf.writeFloatLE(this.groundResistanceMax);
        buf.writeFloatLE(this.airResistance);
        buf.writeFloatLE(this.airResistanceMax);
        buf.writeFloatLE(this.threshold);
        buf.writeByte(this.style.getValue());
    }

    public int computeSize() {
        return 21;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 21) {
            return ValidationResult.error("Buffer too small: expected at least 21 bytes");
        }
        return ValidationResult.OK;
    }

    public VelocityConfig clone() {
        VelocityConfig copy = new VelocityConfig();
        copy.groundResistance = this.groundResistance;
        copy.groundResistanceMax = this.groundResistanceMax;
        copy.airResistance = this.airResistance;
        copy.airResistanceMax = this.airResistanceMax;
        copy.threshold = this.threshold;
        copy.style = this.style;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof VelocityConfig)) {
            return false;
        }
        VelocityConfig other = (VelocityConfig)obj;
        return this.groundResistance == other.groundResistance && this.groundResistanceMax == other.groundResistanceMax && this.airResistance == other.airResistance && this.airResistanceMax == other.airResistanceMax && this.threshold == other.threshold && Objects.equals((Object)this.style, (Object)other.style);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{Float.valueOf(this.groundResistance), Float.valueOf(this.groundResistanceMax), Float.valueOf(this.airResistance), Float.valueOf(this.airResistanceMax), Float.valueOf(this.threshold), this.style});
    }
}

