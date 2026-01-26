/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.CollisionType;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class HitboxCollisionConfig {
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 5;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 5;
    public static final int MAX_SIZE = 5;
    @Nonnull
    public CollisionType collisionType = CollisionType.Hard;
    public float softCollisionOffsetRatio;

    public HitboxCollisionConfig() {
    }

    public HitboxCollisionConfig(@Nonnull CollisionType collisionType, float softCollisionOffsetRatio) {
        this.collisionType = collisionType;
        this.softCollisionOffsetRatio = softCollisionOffsetRatio;
    }

    public HitboxCollisionConfig(@Nonnull HitboxCollisionConfig other) {
        this.collisionType = other.collisionType;
        this.softCollisionOffsetRatio = other.softCollisionOffsetRatio;
    }

    @Nonnull
    public static HitboxCollisionConfig deserialize(@Nonnull ByteBuf buf, int offset) {
        HitboxCollisionConfig obj = new HitboxCollisionConfig();
        obj.collisionType = CollisionType.fromValue(buf.getByte(offset + 0));
        obj.softCollisionOffsetRatio = buf.getFloatLE(offset + 1);
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 5;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        buf.writeByte(this.collisionType.getValue());
        buf.writeFloatLE(this.softCollisionOffsetRatio);
    }

    public int computeSize() {
        return 5;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 5) {
            return ValidationResult.error("Buffer too small: expected at least 5 bytes");
        }
        return ValidationResult.OK;
    }

    public HitboxCollisionConfig clone() {
        HitboxCollisionConfig copy = new HitboxCollisionConfig();
        copy.collisionType = this.collisionType;
        copy.softCollisionOffsetRatio = this.softCollisionOffsetRatio;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HitboxCollisionConfig)) {
            return false;
        }
        HitboxCollisionConfig other = (HitboxCollisionConfig)obj;
        return Objects.equals((Object)this.collisionType, (Object)other.collisionType) && this.softCollisionOffsetRatio == other.softCollisionOffsetRatio;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.collisionType, Float.valueOf(this.softCollisionOffsetRatio)});
    }
}

