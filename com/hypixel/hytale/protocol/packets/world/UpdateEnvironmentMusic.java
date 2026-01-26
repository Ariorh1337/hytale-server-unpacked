/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.world;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class UpdateEnvironmentMusic
implements Packet {
    public static final int PACKET_ID = 151;
    public static final boolean IS_COMPRESSED = false;
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 4;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 4;
    public static final int MAX_SIZE = 4;
    public int environmentIndex;

    @Override
    public int getId() {
        return 151;
    }

    public UpdateEnvironmentMusic() {
    }

    public UpdateEnvironmentMusic(int environmentIndex) {
        this.environmentIndex = environmentIndex;
    }

    public UpdateEnvironmentMusic(@Nonnull UpdateEnvironmentMusic other) {
        this.environmentIndex = other.environmentIndex;
    }

    @Nonnull
    public static UpdateEnvironmentMusic deserialize(@Nonnull ByteBuf buf, int offset) {
        UpdateEnvironmentMusic obj = new UpdateEnvironmentMusic();
        obj.environmentIndex = buf.getIntLE(offset + 0);
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 4;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        buf.writeIntLE(this.environmentIndex);
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

    public UpdateEnvironmentMusic clone() {
        UpdateEnvironmentMusic copy = new UpdateEnvironmentMusic();
        copy.environmentIndex = this.environmentIndex;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UpdateEnvironmentMusic)) {
            return false;
        }
        UpdateEnvironmentMusic other = (UpdateEnvironmentMusic)obj;
        return this.environmentIndex == other.environmentIndex;
    }

    public int hashCode() {
        return Objects.hash(this.environmentIndex);
    }
}

