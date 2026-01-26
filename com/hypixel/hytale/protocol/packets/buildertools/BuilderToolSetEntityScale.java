/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.buildertools;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class BuilderToolSetEntityScale
implements Packet {
    public static final int PACKET_ID = 420;
    public static final boolean IS_COMPRESSED = false;
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 8;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 8;
    public static final int MAX_SIZE = 8;
    public int entityId;
    public float scale;

    @Override
    public int getId() {
        return 420;
    }

    public BuilderToolSetEntityScale() {
    }

    public BuilderToolSetEntityScale(int entityId, float scale) {
        this.entityId = entityId;
        this.scale = scale;
    }

    public BuilderToolSetEntityScale(@Nonnull BuilderToolSetEntityScale other) {
        this.entityId = other.entityId;
        this.scale = other.scale;
    }

    @Nonnull
    public static BuilderToolSetEntityScale deserialize(@Nonnull ByteBuf buf, int offset) {
        BuilderToolSetEntityScale obj = new BuilderToolSetEntityScale();
        obj.entityId = buf.getIntLE(offset + 0);
        obj.scale = buf.getFloatLE(offset + 4);
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 8;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        buf.writeIntLE(this.entityId);
        buf.writeFloatLE(this.scale);
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

    public BuilderToolSetEntityScale clone() {
        BuilderToolSetEntityScale copy = new BuilderToolSetEntityScale();
        copy.entityId = this.entityId;
        copy.scale = this.scale;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BuilderToolSetEntityScale)) {
            return false;
        }
        BuilderToolSetEntityScale other = (BuilderToolSetEntityScale)obj;
        return this.entityId == other.entityId && this.scale == other.scale;
    }

    public int hashCode() {
        return Objects.hash(this.entityId, Float.valueOf(this.scale));
    }
}

