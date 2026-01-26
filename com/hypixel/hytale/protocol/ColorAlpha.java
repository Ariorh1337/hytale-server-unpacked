/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class ColorAlpha {
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 4;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 4;
    public static final int MAX_SIZE = 4;
    public byte alpha;
    public byte red;
    public byte green;
    public byte blue;

    public ColorAlpha() {
    }

    public ColorAlpha(byte alpha, byte red, byte green, byte blue) {
        this.alpha = alpha;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public ColorAlpha(@Nonnull ColorAlpha other) {
        this.alpha = other.alpha;
        this.red = other.red;
        this.green = other.green;
        this.blue = other.blue;
    }

    @Nonnull
    public static ColorAlpha deserialize(@Nonnull ByteBuf buf, int offset) {
        ColorAlpha obj = new ColorAlpha();
        obj.alpha = buf.getByte(offset + 0);
        obj.red = buf.getByte(offset + 1);
        obj.green = buf.getByte(offset + 2);
        obj.blue = buf.getByte(offset + 3);
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 4;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        buf.writeByte(this.alpha);
        buf.writeByte(this.red);
        buf.writeByte(this.green);
        buf.writeByte(this.blue);
    }

    public int computeSize() {
        return 4;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 4) {
            return ValidationResult.error("Buffer too small: expected at least 4 bytes");
        }
        return ValidationResult.OK;
    }

    public ColorAlpha clone() {
        ColorAlpha copy = new ColorAlpha();
        copy.alpha = this.alpha;
        copy.red = this.red;
        copy.green = this.green;
        copy.blue = this.blue;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ColorAlpha)) {
            return false;
        }
        ColorAlpha other = (ColorAlpha)obj;
        return this.alpha == other.alpha && this.red == other.red && this.green == other.green && this.blue == other.blue;
    }

    public int hashCode() {
        return Objects.hash(this.alpha, this.red, this.green, this.blue);
    }
}

