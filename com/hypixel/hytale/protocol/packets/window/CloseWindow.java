/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.window;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class CloseWindow
implements Packet {
    public static final int PACKET_ID = 202;
    public static final boolean IS_COMPRESSED = false;
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 4;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 4;
    public static final int MAX_SIZE = 4;
    public int id;

    @Override
    public int getId() {
        return 202;
    }

    public CloseWindow() {
    }

    public CloseWindow(int id) {
        this.id = id;
    }

    public CloseWindow(@Nonnull CloseWindow other) {
        this.id = other.id;
    }

    @Nonnull
    public static CloseWindow deserialize(@Nonnull ByteBuf buf, int offset) {
        CloseWindow obj = new CloseWindow();
        obj.id = buf.getIntLE(offset + 0);
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 4;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        buf.writeIntLE(this.id);
    }

    @Override
    public int computeSize() {
        return 4;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 4) {
            return ValidationResult.error("Buffer too small: expected at least 4 bytes");
        }
        return ValidationResult.OK;
    }

    public CloseWindow clone() {
        CloseWindow copy = new CloseWindow();
        copy.id = this.id;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CloseWindow)) {
            return false;
        }
        CloseWindow other = (CloseWindow)obj;
        return this.id == other.id;
    }

    public int hashCode() {
        return Objects.hash(this.id);
    }
}

