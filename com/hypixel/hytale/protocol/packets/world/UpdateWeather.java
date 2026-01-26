/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.world;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class UpdateWeather
implements Packet {
    public static final int PACKET_ID = 149;
    public static final boolean IS_COMPRESSED = false;
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 8;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 8;
    public static final int MAX_SIZE = 8;
    public int weatherIndex;
    public float transitionSeconds;

    @Override
    public int getId() {
        return 149;
    }

    public UpdateWeather() {
    }

    public UpdateWeather(int weatherIndex, float transitionSeconds) {
        this.weatherIndex = weatherIndex;
        this.transitionSeconds = transitionSeconds;
    }

    public UpdateWeather(@Nonnull UpdateWeather other) {
        this.weatherIndex = other.weatherIndex;
        this.transitionSeconds = other.transitionSeconds;
    }

    @Nonnull
    public static UpdateWeather deserialize(@Nonnull ByteBuf buf, int offset) {
        UpdateWeather obj = new UpdateWeather();
        obj.weatherIndex = buf.getIntLE(offset + 0);
        obj.transitionSeconds = buf.getFloatLE(offset + 4);
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 8;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        buf.writeIntLE(this.weatherIndex);
        buf.writeFloatLE(this.transitionSeconds);
    }

    @Override
    public int computeSize() {
        return 8;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 8) {
            return ValidationResult.error("Buffer too small: expected at least 8 bytes");
        }
        return ValidationResult.OK;
    }

    public UpdateWeather clone() {
        UpdateWeather copy = new UpdateWeather();
        copy.weatherIndex = this.weatherIndex;
        copy.transitionSeconds = this.transitionSeconds;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UpdateWeather)) {
            return false;
        }
        UpdateWeather other = (UpdateWeather)obj;
        return this.weatherIndex == other.weatherIndex && this.transitionSeconds == other.transitionSeconds;
    }

    public int hashCode() {
        return Objects.hash(this.weatherIndex, Float.valueOf(this.transitionSeconds));
    }
}

