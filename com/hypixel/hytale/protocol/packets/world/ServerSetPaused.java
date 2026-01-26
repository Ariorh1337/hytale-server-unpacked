/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.world;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class ServerSetPaused
implements Packet {
    public static final int PACKET_ID = 159;
    public static final boolean IS_COMPRESSED = false;
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 1;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 1;
    public static final int MAX_SIZE = 1;
    public boolean paused;

    @Override
    public int getId() {
        return 159;
    }

    public ServerSetPaused() {
    }

    public ServerSetPaused(boolean paused) {
        this.paused = paused;
    }

    public ServerSetPaused(@Nonnull ServerSetPaused other) {
        this.paused = other.paused;
    }

    @Nonnull
    public static ServerSetPaused deserialize(@Nonnull ByteBuf buf, int offset) {
        ServerSetPaused obj = new ServerSetPaused();
        obj.paused = buf.getByte(offset + 0) != 0;
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 1;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        buf.writeByte(this.paused ? 1 : 0);
    }

    @Override
    public int computeSize() {
        return 1;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 1) {
            return ValidationResult.error("Buffer too small: expected at least 1 bytes");
        }
        return ValidationResult.OK;
    }

    public ServerSetPaused clone() {
        ServerSetPaused copy = new ServerSetPaused();
        copy.paused = this.paused;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ServerSetPaused)) {
            return false;
        }
        ServerSetPaused other = (ServerSetPaused)obj;
        return this.paused == other.paused;
    }

    public int hashCode() {
        return Objects.hash(this.paused);
    }
}

