/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class Rangef {
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 8;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 8;
    public static final int MAX_SIZE = 8;
    public float min;
    public float max;

    public Rangef() {
    }

    public Rangef(float min, float max) {
        this.min = min;
        this.max = max;
    }

    public Rangef(@Nonnull Rangef other) {
        this.min = other.min;
        this.max = other.max;
    }

    @Nonnull
    public static Rangef deserialize(@Nonnull ByteBuf buf, int offset) {
        Rangef obj = new Rangef();
        obj.min = buf.getFloatLE(offset + 0);
        obj.max = buf.getFloatLE(offset + 4);
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 8;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        buf.writeFloatLE(this.min);
        buf.writeFloatLE(this.max);
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

    public Rangef clone() {
        Rangef copy = new Rangef();
        copy.min = this.min;
        copy.max = this.max;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Rangef)) {
            return false;
        }
        Rangef other = (Rangef)obj;
        return this.min == other.min && this.max == other.max;
    }

    public int hashCode() {
        return Objects.hash(Float.valueOf(this.min), Float.valueOf(this.max));
    }
}

