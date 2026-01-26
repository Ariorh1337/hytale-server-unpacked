/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.world;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;

public class ClearEditorTimeOverride
implements Packet {
    public static final int PACKET_ID = 148;
    public static final boolean IS_COMPRESSED = false;
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 0;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 0;
    public static final int MAX_SIZE = 0;

    @Override
    public int getId() {
        return 148;
    }

    @Nonnull
    public static ClearEditorTimeOverride deserialize(@Nonnull ByteBuf buf, int offset) {
        ClearEditorTimeOverride obj = new ClearEditorTimeOverride();
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 0;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
    }

    @Override
    public int computeSize() {
        return 0;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 0) {
            return ValidationResult.error("Buffer too small: expected at least 0 bytes");
        }
        return ValidationResult.OK;
    }

    public ClearEditorTimeOverride clone() {
        return new ClearEditorTimeOverride();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ClearEditorTimeOverride)) {
            return false;
        }
        ClearEditorTimeOverride other = (ClearEditorTimeOverride)obj;
        return true;
    }

    public int hashCode() {
        return 0;
    }
}

