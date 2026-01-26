/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.ParticleCollisionAction;
import com.hypixel.hytale.protocol.ParticleCollisionBlockType;
import com.hypixel.hytale.protocol.ParticleRotationInfluence;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class ParticleCollision {
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 3;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 3;
    public static final int MAX_SIZE = 3;
    @Nonnull
    public ParticleCollisionBlockType blockType = ParticleCollisionBlockType.None;
    @Nonnull
    public ParticleCollisionAction action = ParticleCollisionAction.Expire;
    @Nonnull
    public ParticleRotationInfluence particleRotationInfluence = ParticleRotationInfluence.None;

    public ParticleCollision() {
    }

    public ParticleCollision(@Nonnull ParticleCollisionBlockType blockType, @Nonnull ParticleCollisionAction action, @Nonnull ParticleRotationInfluence particleRotationInfluence) {
        this.blockType = blockType;
        this.action = action;
        this.particleRotationInfluence = particleRotationInfluence;
    }

    public ParticleCollision(@Nonnull ParticleCollision other) {
        this.blockType = other.blockType;
        this.action = other.action;
        this.particleRotationInfluence = other.particleRotationInfluence;
    }

    @Nonnull
    public static ParticleCollision deserialize(@Nonnull ByteBuf buf, int offset) {
        ParticleCollision obj = new ParticleCollision();
        obj.blockType = ParticleCollisionBlockType.fromValue(buf.getByte(offset + 0));
        obj.action = ParticleCollisionAction.fromValue(buf.getByte(offset + 1));
        obj.particleRotationInfluence = ParticleRotationInfluence.fromValue(buf.getByte(offset + 2));
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 3;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        buf.writeByte(this.blockType.getValue());
        buf.writeByte(this.action.getValue());
        buf.writeByte(this.particleRotationInfluence.getValue());
    }

    public int computeSize() {
        return 3;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 3) {
            return ValidationResult.error("Buffer too small: expected at least 3 bytes");
        }
        return ValidationResult.OK;
    }

    public ParticleCollision clone() {
        ParticleCollision copy = new ParticleCollision();
        copy.blockType = this.blockType;
        copy.action = this.action;
        copy.particleRotationInfluence = this.particleRotationInfluence;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ParticleCollision)) {
            return false;
        }
        ParticleCollision other = (ParticleCollision)obj;
        return Objects.equals((Object)this.blockType, (Object)other.blockType) && Objects.equals((Object)this.action, (Object)other.action) && Objects.equals((Object)this.particleRotationInfluence, (Object)other.particleRotationInfluence);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.blockType, this.action, this.particleRotationInfluence});
    }
}

