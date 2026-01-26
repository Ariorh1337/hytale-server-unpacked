/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.asseteditor;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class AssetEditorUpdateSecondsPerGameDay
implements Packet {
    public static final int PACKET_ID = 353;
    public static final boolean IS_COMPRESSED = false;
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 8;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 8;
    public static final int MAX_SIZE = 8;
    public int daytimeDurationSeconds;
    public int nighttimeDurationSeconds;

    @Override
    public int getId() {
        return 353;
    }

    public AssetEditorUpdateSecondsPerGameDay() {
    }

    public AssetEditorUpdateSecondsPerGameDay(int daytimeDurationSeconds, int nighttimeDurationSeconds) {
        this.daytimeDurationSeconds = daytimeDurationSeconds;
        this.nighttimeDurationSeconds = nighttimeDurationSeconds;
    }

    public AssetEditorUpdateSecondsPerGameDay(@Nonnull AssetEditorUpdateSecondsPerGameDay other) {
        this.daytimeDurationSeconds = other.daytimeDurationSeconds;
        this.nighttimeDurationSeconds = other.nighttimeDurationSeconds;
    }

    @Nonnull
    public static AssetEditorUpdateSecondsPerGameDay deserialize(@Nonnull ByteBuf buf, int offset) {
        AssetEditorUpdateSecondsPerGameDay obj = new AssetEditorUpdateSecondsPerGameDay();
        obj.daytimeDurationSeconds = buf.getIntLE(offset + 0);
        obj.nighttimeDurationSeconds = buf.getIntLE(offset + 4);
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 8;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        buf.writeIntLE(this.daytimeDurationSeconds);
        buf.writeIntLE(this.nighttimeDurationSeconds);
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

    public AssetEditorUpdateSecondsPerGameDay clone() {
        AssetEditorUpdateSecondsPerGameDay copy = new AssetEditorUpdateSecondsPerGameDay();
        copy.daytimeDurationSeconds = this.daytimeDurationSeconds;
        copy.nighttimeDurationSeconds = this.nighttimeDurationSeconds;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AssetEditorUpdateSecondsPerGameDay)) {
            return false;
        }
        AssetEditorUpdateSecondsPerGameDay other = (AssetEditorUpdateSecondsPerGameDay)obj;
        return this.daytimeDurationSeconds == other.daytimeDurationSeconds && this.nighttimeDurationSeconds == other.nighttimeDurationSeconds;
    }

    public int hashCode() {
        return Objects.hash(this.daytimeDurationSeconds, this.nighttimeDurationSeconds);
    }
}

