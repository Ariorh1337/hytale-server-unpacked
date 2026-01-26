/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class NearFar {
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 8;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 8;
    public static final int MAX_SIZE = 8;
    public float near;
    public float far;

    public NearFar() {
    }

    public NearFar(float near, float far) {
        this.near = near;
        this.far = far;
    }

    public NearFar(@Nonnull NearFar other) {
        this.near = other.near;
        this.far = other.far;
    }

    @Nonnull
    public static NearFar deserialize(@Nonnull ByteBuf buf, int offset) {
        NearFar obj = new NearFar();
        obj.near = buf.getFloatLE(offset + 0);
        obj.far = buf.getFloatLE(offset + 4);
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 8;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        buf.writeFloatLE(this.near);
        buf.writeFloatLE(this.far);
    }

    public int computeSize() {
        return 8;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 8) {
            return ValidationResult.error("Buffer too small: expected at least 8 bytes");
        }
        return ValidationResult.OK;
    }

    public NearFar clone() {
        NearFar copy = new NearFar();
        copy.near = this.near;
        copy.far = this.far;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof NearFar)) {
            return false;
        }
        NearFar other = (NearFar)obj;
        return this.near == other.near && this.far == other.far;
    }

    public int hashCode() {
        return Objects.hash(Float.valueOf(this.near), Float.valueOf(this.far));
    }
}

