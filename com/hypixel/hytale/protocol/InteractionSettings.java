/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class InteractionSettings {
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 1;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 1;
    public static final int MAX_SIZE = 1;
    public boolean allowSkipOnClick;

    public InteractionSettings() {
    }

    public InteractionSettings(boolean allowSkipOnClick) {
        this.allowSkipOnClick = allowSkipOnClick;
    }

    public InteractionSettings(@Nonnull InteractionSettings other) {
        this.allowSkipOnClick = other.allowSkipOnClick;
    }

    @Nonnull
    public static InteractionSettings deserialize(@Nonnull ByteBuf buf, int offset) {
        InteractionSettings obj = new InteractionSettings();
        obj.allowSkipOnClick = buf.getByte(offset + 0) != 0;
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 1;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        buf.writeByte(this.allowSkipOnClick ? 1 : 0);
    }

    public int computeSize() {
        return 1;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 1) {
            return ValidationResult.error("Buffer too small: expected at least 1 bytes");
        }
        return ValidationResult.OK;
    }

    public InteractionSettings clone() {
        InteractionSettings copy = new InteractionSettings();
        copy.allowSkipOnClick = this.allowSkipOnClick;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof InteractionSettings)) {
            return false;
        }
        InteractionSettings other = (InteractionSettings)obj;
        return this.allowSkipOnClick == other.allowSkipOnClick;
    }

    public int hashCode() {
        return Objects.hash(this.allowSkipOnClick);
    }
}

