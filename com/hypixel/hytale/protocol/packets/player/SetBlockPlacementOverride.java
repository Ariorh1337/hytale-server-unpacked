/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.player;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class SetBlockPlacementOverride
implements Packet {
    public static final int PACKET_ID = 103;
    public static final boolean IS_COMPRESSED = false;
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 1;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 1;
    public static final int MAX_SIZE = 1;
    public boolean enabled;

    @Override
    public int getId() {
        return 103;
    }

    public SetBlockPlacementOverride() {
    }

    public SetBlockPlacementOverride(boolean enabled) {
        this.enabled = enabled;
    }

    public SetBlockPlacementOverride(@Nonnull SetBlockPlacementOverride other) {
        this.enabled = other.enabled;
    }

    @Nonnull
    public static SetBlockPlacementOverride deserialize(@Nonnull ByteBuf buf, int offset) {
        SetBlockPlacementOverride obj = new SetBlockPlacementOverride();
        obj.enabled = buf.getByte(offset + 0) != 0;
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 1;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        buf.writeByte(this.enabled ? 1 : 0);
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

    public SetBlockPlacementOverride clone() {
        SetBlockPlacementOverride copy = new SetBlockPlacementOverride();
        copy.enabled = this.enabled;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SetBlockPlacementOverride)) {
            return false;
        }
        SetBlockPlacementOverride other = (SetBlockPlacementOverride)obj;
        return this.enabled == other.enabled;
    }

    public int hashCode() {
        return Objects.hash(this.enabled);
    }
}

