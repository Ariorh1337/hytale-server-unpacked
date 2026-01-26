/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.world;

import com.hypixel.hytale.protocol.InstantData;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UpdateTime
implements Packet {
    public static final int PACKET_ID = 146;
    public static final boolean IS_COMPRESSED = false;
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 13;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 13;
    public static final int MAX_SIZE = 13;
    @Nullable
    public InstantData gameTime;

    @Override
    public int getId() {
        return 146;
    }

    public UpdateTime() {
    }

    public UpdateTime(@Nullable InstantData gameTime) {
        this.gameTime = gameTime;
    }

    public UpdateTime(@Nonnull UpdateTime other) {
        this.gameTime = other.gameTime;
    }

    @Nonnull
    public static UpdateTime deserialize(@Nonnull ByteBuf buf, int offset) {
        UpdateTime obj = new UpdateTime();
        byte nullBits = buf.getByte(offset);
        if ((nullBits & 1) != 0) {
            obj.gameTime = InstantData.deserialize(buf, offset + 1);
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 13;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        int nullBits = 0;
        if (this.gameTime != null) {
            nullBits = (byte)(nullBits | 1);
        }
        buf.writeByte(nullBits);
        if (this.gameTime != null) {
            this.gameTime.serialize(buf);
        } else {
            buf.writeZero(12);
        }
    }

    @Override
    public int computeSize() {
        return 13;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 13) {
            return ValidationResult.error("Buffer too small: expected at least 13 bytes");
        }
        return ValidationResult.OK;
    }

    public UpdateTime clone() {
        UpdateTime copy = new UpdateTime();
        copy.gameTime = this.gameTime != null ? this.gameTime.clone() : null;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UpdateTime)) {
            return false;
        }
        UpdateTime other = (UpdateTime)obj;
        return Objects.equals(this.gameTime, other.gameTime);
    }

    public int hashCode() {
        return Objects.hash(this.gameTime);
    }
}

