/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class IntersectionHighlight {
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 8;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 8;
    public static final int MAX_SIZE = 8;
    public float highlightThreshold;
    @Nullable
    public Color highlightColor;

    public IntersectionHighlight() {
    }

    public IntersectionHighlight(float highlightThreshold, @Nullable Color highlightColor) {
        this.highlightThreshold = highlightThreshold;
        this.highlightColor = highlightColor;
    }

    public IntersectionHighlight(@Nonnull IntersectionHighlight other) {
        this.highlightThreshold = other.highlightThreshold;
        this.highlightColor = other.highlightColor;
    }

    @Nonnull
    public static IntersectionHighlight deserialize(@Nonnull ByteBuf buf, int offset) {
        IntersectionHighlight obj = new IntersectionHighlight();
        byte nullBits = buf.getByte(offset);
        obj.highlightThreshold = buf.getFloatLE(offset + 1);
        if ((nullBits & 1) != 0) {
            obj.highlightColor = Color.deserialize(buf, offset + 5);
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 8;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        int nullBits = 0;
        if (this.highlightColor != null) {
            nullBits = (byte)(nullBits | 1);
        }
        buf.writeByte(nullBits);
        buf.writeFloatLE(this.highlightThreshold);
        if (this.highlightColor != null) {
            this.highlightColor.serialize(buf);
        } else {
            buf.writeZero(3);
        }
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

    public IntersectionHighlight clone() {
        IntersectionHighlight copy = new IntersectionHighlight();
        copy.highlightThreshold = this.highlightThreshold;
        copy.highlightColor = this.highlightColor != null ? this.highlightColor.clone() : null;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof IntersectionHighlight)) {
            return false;
        }
        IntersectionHighlight other = (IntersectionHighlight)obj;
        return this.highlightThreshold == other.highlightThreshold && Objects.equals(this.highlightColor, other.highlightColor);
    }

    public int hashCode() {
        return Objects.hash(Float.valueOf(this.highlightThreshold), this.highlightColor);
    }
}

