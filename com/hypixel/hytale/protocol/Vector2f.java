/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class Vector2f {
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 8;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 8;
    public static final int MAX_SIZE = 8;
    public float x;
    public float y;

    public Vector2f() {
    }

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f(@Nonnull Vector2f other) {
        this.x = other.x;
        this.y = other.y;
    }

    @Nonnull
    public static Vector2f deserialize(@Nonnull ByteBuf buf, int offset) {
        Vector2f obj = new Vector2f();
        obj.x = buf.getFloatLE(offset + 0);
        obj.y = buf.getFloatLE(offset + 4);
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 8;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        buf.writeFloatLE(this.x);
        buf.writeFloatLE(this.y);
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

    public Vector2f clone() {
        Vector2f copy = new Vector2f();
        copy.x = this.x;
        copy.y = this.y;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Vector2f)) {
            return false;
        }
        Vector2f other = (Vector2f)obj;
        return this.x == other.x && this.y == other.y;
    }

    public int hashCode() {
        return Objects.hash(Float.valueOf(this.x), Float.valueOf(this.y));
    }
}

