/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class BlockSelectorToolData {
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 4;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 4;
    public static final int MAX_SIZE = 4;
    public float durabilityLossOnUse;

    public BlockSelectorToolData() {
    }

    public BlockSelectorToolData(float durabilityLossOnUse) {
        this.durabilityLossOnUse = durabilityLossOnUse;
    }

    public BlockSelectorToolData(@Nonnull BlockSelectorToolData other) {
        this.durabilityLossOnUse = other.durabilityLossOnUse;
    }

    @Nonnull
    public static BlockSelectorToolData deserialize(@Nonnull ByteBuf buf, int offset) {
        BlockSelectorToolData obj = new BlockSelectorToolData();
        obj.durabilityLossOnUse = buf.getFloatLE(offset + 0);
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 4;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        buf.writeFloatLE(this.durabilityLossOnUse);
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

    public BlockSelectorToolData clone() {
        BlockSelectorToolData copy = new BlockSelectorToolData();
        copy.durabilityLossOnUse = this.durabilityLossOnUse;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BlockSelectorToolData)) {
            return false;
        }
        BlockSelectorToolData other = (BlockSelectorToolData)obj;
        return this.durabilityLossOnUse == other.durabilityLossOnUse;
    }

    public int hashCode() {
        return Objects.hash(Float.valueOf(this.durabilityLossOnUse));
    }
}

