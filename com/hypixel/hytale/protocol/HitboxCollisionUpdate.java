/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.ComponentUpdate;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class HitboxCollisionUpdate
extends ComponentUpdate {
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 4;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 4;
    public static final int MAX_SIZE = 4;
    public int hitboxCollisionConfigIndex;

    public HitboxCollisionUpdate() {
    }

    public HitboxCollisionUpdate(int hitboxCollisionConfigIndex) {
        this.hitboxCollisionConfigIndex = hitboxCollisionConfigIndex;
    }

    public HitboxCollisionUpdate(@Nonnull HitboxCollisionUpdate other) {
        this.hitboxCollisionConfigIndex = other.hitboxCollisionConfigIndex;
    }

    @Nonnull
    public static HitboxCollisionUpdate deserialize(@Nonnull ByteBuf buf, int offset) {
        HitboxCollisionUpdate obj = new HitboxCollisionUpdate();
        obj.hitboxCollisionConfigIndex = buf.getIntLE(offset + 0);
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 4;
    }

    @Override
    public int serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        buf.writeIntLE(this.hitboxCollisionConfigIndex);
        return buf.writerIndex() - startPos;
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

    public HitboxCollisionUpdate clone() {
        HitboxCollisionUpdate copy = new HitboxCollisionUpdate();
        copy.hitboxCollisionConfigIndex = this.hitboxCollisionConfigIndex;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HitboxCollisionUpdate)) {
            return false;
        }
        HitboxCollisionUpdate other = (HitboxCollisionUpdate)obj;
        return this.hitboxCollisionConfigIndex == other.hitboxCollisionConfigIndex;
    }

    public int hashCode() {
        return Objects.hash(this.hitboxCollisionConfigIndex);
    }
}

