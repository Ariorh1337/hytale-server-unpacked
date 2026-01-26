/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.buildertools;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class BuilderToolSetEntityPickupEnabled
implements Packet {
    public static final int PACKET_ID = 421;
    public static final boolean IS_COMPRESSED = false;
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 5;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 5;
    public static final int MAX_SIZE = 5;
    public int entityId;
    public boolean enabled;

    @Override
    public int getId() {
        return 421;
    }

    public BuilderToolSetEntityPickupEnabled() {
    }

    public BuilderToolSetEntityPickupEnabled(int entityId, boolean enabled) {
        this.entityId = entityId;
        this.enabled = enabled;
    }

    public BuilderToolSetEntityPickupEnabled(@Nonnull BuilderToolSetEntityPickupEnabled other) {
        this.entityId = other.entityId;
        this.enabled = other.enabled;
    }

    @Nonnull
    public static BuilderToolSetEntityPickupEnabled deserialize(@Nonnull ByteBuf buf, int offset) {
        BuilderToolSetEntityPickupEnabled obj = new BuilderToolSetEntityPickupEnabled();
        obj.entityId = buf.getIntLE(offset + 0);
        obj.enabled = buf.getByte(offset + 4) != 0;
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 5;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        buf.writeIntLE(this.entityId);
        buf.writeByte(this.enabled ? 1 : 0);
    }

    @Override
    public int computeSize() {
        return 5;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 5) {
            return ValidationResult.error("Buffer too small: expected at least 5 bytes");
        }
        return ValidationResult.OK;
    }

    public BuilderToolSetEntityPickupEnabled clone() {
        BuilderToolSetEntityPickupEnabled copy = new BuilderToolSetEntityPickupEnabled();
        copy.entityId = this.entityId;
        copy.enabled = this.enabled;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BuilderToolSetEntityPickupEnabled)) {
            return false;
        }
        BuilderToolSetEntityPickupEnabled other = (BuilderToolSetEntityPickupEnabled)obj;
        return this.entityId == other.entityId && this.enabled == other.enabled;
    }

    public int hashCode() {
        return Objects.hash(this.entityId, this.enabled);
    }
}

