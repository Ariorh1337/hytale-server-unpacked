/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.Rotation;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class BlockRotation {
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 3;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 3;
    public static final int MAX_SIZE = 3;
    @Nonnull
    public Rotation rotationYaw = Rotation.None;
    @Nonnull
    public Rotation rotationPitch = Rotation.None;
    @Nonnull
    public Rotation rotationRoll = Rotation.None;

    public BlockRotation() {
    }

    public BlockRotation(@Nonnull Rotation rotationYaw, @Nonnull Rotation rotationPitch, @Nonnull Rotation rotationRoll) {
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
        this.rotationRoll = rotationRoll;
    }

    public BlockRotation(@Nonnull BlockRotation other) {
        this.rotationYaw = other.rotationYaw;
        this.rotationPitch = other.rotationPitch;
        this.rotationRoll = other.rotationRoll;
    }

    @Nonnull
    public static BlockRotation deserialize(@Nonnull ByteBuf buf, int offset) {
        BlockRotation obj = new BlockRotation();
        obj.rotationYaw = Rotation.fromValue(buf.getByte(offset + 0));
        obj.rotationPitch = Rotation.fromValue(buf.getByte(offset + 1));
        obj.rotationRoll = Rotation.fromValue(buf.getByte(offset + 2));
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 3;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        buf.writeByte(this.rotationYaw.getValue());
        buf.writeByte(this.rotationPitch.getValue());
        buf.writeByte(this.rotationRoll.getValue());
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

    public BlockRotation clone() {
        BlockRotation copy = new BlockRotation();
        copy.rotationYaw = this.rotationYaw;
        copy.rotationPitch = this.rotationPitch;
        copy.rotationRoll = this.rotationRoll;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BlockRotation)) {
            return false;
        }
        BlockRotation other = (BlockRotation)obj;
        return Objects.equals((Object)this.rotationYaw, (Object)other.rotationYaw) && Objects.equals((Object)this.rotationPitch, (Object)other.rotationPitch) && Objects.equals((Object)this.rotationRoll, (Object)other.rotationRoll);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.rotationYaw, this.rotationPitch, this.rotationRoll});
    }
}

