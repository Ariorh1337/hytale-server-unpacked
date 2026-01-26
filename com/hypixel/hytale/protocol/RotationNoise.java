/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.NoiseConfig;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RotationNoise {
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 1;
    public static final int VARIABLE_FIELD_COUNT = 3;
    public static final int VARIABLE_BLOCK_START = 13;
    public static final int MAX_SIZE = 282624028;
    @Nullable
    public NoiseConfig[] pitch;
    @Nullable
    public NoiseConfig[] yaw;
    @Nullable
    public NoiseConfig[] roll;

    public RotationNoise() {
    }

    public RotationNoise(@Nullable NoiseConfig[] pitch, @Nullable NoiseConfig[] yaw, @Nullable NoiseConfig[] roll) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }

    public RotationNoise(@Nonnull RotationNoise other) {
        this.pitch = other.pitch;
        this.yaw = other.yaw;
        this.roll = other.roll;
    }

    @Nonnull
    public static RotationNoise deserialize(@Nonnull ByteBuf buf, int offset) {
        int i;
        int elemPos;
        int varIntLen;
        RotationNoise obj = new RotationNoise();
        byte nullBits = buf.getByte(offset);
        if ((nullBits & 1) != 0) {
            int varPos0 = offset + 13 + buf.getIntLE(offset + 1);
            int pitchCount = VarInt.peek(buf, varPos0);
            if (pitchCount < 0) {
                throw ProtocolException.negativeLength("Pitch", pitchCount);
            }
            if (pitchCount > 4096000) {
                throw ProtocolException.arrayTooLong("Pitch", pitchCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos0);
            if ((long)(varPos0 + varIntLen) + (long)pitchCount * 23L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("Pitch", varPos0 + varIntLen + pitchCount * 23, buf.readableBytes());
            }
            obj.pitch = new NoiseConfig[pitchCount];
            elemPos = varPos0 + varIntLen;
            for (i = 0; i < pitchCount; ++i) {
                obj.pitch[i] = NoiseConfig.deserialize(buf, elemPos);
                elemPos += NoiseConfig.computeBytesConsumed(buf, elemPos);
            }
        }
        if ((nullBits & 2) != 0) {
            int varPos1 = offset + 13 + buf.getIntLE(offset + 5);
            int yawCount = VarInt.peek(buf, varPos1);
            if (yawCount < 0) {
                throw ProtocolException.negativeLength("Yaw", yawCount);
            }
            if (yawCount > 4096000) {
                throw ProtocolException.arrayTooLong("Yaw", yawCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos1);
            if ((long)(varPos1 + varIntLen) + (long)yawCount * 23L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("Yaw", varPos1 + varIntLen + yawCount * 23, buf.readableBytes());
            }
            obj.yaw = new NoiseConfig[yawCount];
            elemPos = varPos1 + varIntLen;
            for (i = 0; i < yawCount; ++i) {
                obj.yaw[i] = NoiseConfig.deserialize(buf, elemPos);
                elemPos += NoiseConfig.computeBytesConsumed(buf, elemPos);
            }
        }
        if ((nullBits & 4) != 0) {
            int varPos2 = offset + 13 + buf.getIntLE(offset + 9);
            int rollCount = VarInt.peek(buf, varPos2);
            if (rollCount < 0) {
                throw ProtocolException.negativeLength("Roll", rollCount);
            }
            if (rollCount > 4096000) {
                throw ProtocolException.arrayTooLong("Roll", rollCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos2);
            if ((long)(varPos2 + varIntLen) + (long)rollCount * 23L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("Roll", varPos2 + varIntLen + rollCount * 23, buf.readableBytes());
            }
            obj.roll = new NoiseConfig[rollCount];
            elemPos = varPos2 + varIntLen;
            for (i = 0; i < rollCount; ++i) {
                obj.roll[i] = NoiseConfig.deserialize(buf, elemPos);
                elemPos += NoiseConfig.computeBytesConsumed(buf, elemPos);
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int i;
        int arrLen;
        byte nullBits = buf.getByte(offset);
        int maxEnd = 13;
        if ((nullBits & 1) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 1);
            int pos0 = offset + 13 + fieldOffset0;
            arrLen = VarInt.peek(buf, pos0);
            pos0 += VarInt.length(buf, pos0);
            for (i = 0; i < arrLen; ++i) {
                pos0 += NoiseConfig.computeBytesConsumed(buf, pos0);
            }
            if (pos0 - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 2) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 5);
            int pos1 = offset + 13 + fieldOffset1;
            arrLen = VarInt.peek(buf, pos1);
            pos1 += VarInt.length(buf, pos1);
            for (i = 0; i < arrLen; ++i) {
                pos1 += NoiseConfig.computeBytesConsumed(buf, pos1);
            }
            if (pos1 - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        if ((nullBits & 4) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 9);
            int pos2 = offset + 13 + fieldOffset2;
            arrLen = VarInt.peek(buf, pos2);
            pos2 += VarInt.length(buf, pos2);
            for (i = 0; i < arrLen; ++i) {
                pos2 += NoiseConfig.computeBytesConsumed(buf, pos2);
            }
            if (pos2 - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        return maxEnd;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        byte nullBits = 0;
        if (this.pitch != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.yaw != null) {
            nullBits = (byte)(nullBits | 2);
        }
        if (this.roll != null) {
            nullBits = (byte)(nullBits | 4);
        }
        buf.writeByte(nullBits);
        int pitchOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int yawOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int rollOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.pitch != null) {
            buf.setIntLE(pitchOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.pitch.length > 4096000) {
                throw ProtocolException.arrayTooLong("Pitch", this.pitch.length, 4096000);
            }
            VarInt.write(buf, this.pitch.length);
            for (NoiseConfig item : this.pitch) {
                item.serialize(buf);
            }
        } else {
            buf.setIntLE(pitchOffsetSlot, -1);
        }
        if (this.yaw != null) {
            buf.setIntLE(yawOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.yaw.length > 4096000) {
                throw ProtocolException.arrayTooLong("Yaw", this.yaw.length, 4096000);
            }
            VarInt.write(buf, this.yaw.length);
            for (NoiseConfig item : this.yaw) {
                item.serialize(buf);
            }
        } else {
            buf.setIntLE(yawOffsetSlot, -1);
        }
        if (this.roll != null) {
            buf.setIntLE(rollOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.roll.length > 4096000) {
                throw ProtocolException.arrayTooLong("Roll", this.roll.length, 4096000);
            }
            VarInt.write(buf, this.roll.length);
            for (NoiseConfig item : this.roll) {
                item.serialize(buf);
            }
        } else {
            buf.setIntLE(rollOffsetSlot, -1);
        }
    }

    public int computeSize() {
        int size = 13;
        if (this.pitch != null) {
            size += VarInt.size(this.pitch.length) + this.pitch.length * 23;
        }
        if (this.yaw != null) {
            size += VarInt.size(this.yaw.length) + this.yaw.length * 23;
        }
        if (this.roll != null) {
            size += VarInt.size(this.roll.length) + this.roll.length * 23;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int pos;
        if (buffer.readableBytes() - offset < 13) {
            return ValidationResult.error("Buffer too small: expected at least 13 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 1) != 0) {
            int pitchOffset = buffer.getIntLE(offset + 1);
            if (pitchOffset < 0) {
                return ValidationResult.error("Invalid offset for Pitch");
            }
            pos = offset + 13 + pitchOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Pitch");
            }
            int pitchCount = VarInt.peek(buffer, pos);
            if (pitchCount < 0) {
                return ValidationResult.error("Invalid array count for Pitch");
            }
            if (pitchCount > 4096000) {
                return ValidationResult.error("Pitch exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += pitchCount * 23) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading Pitch");
            }
        }
        if ((nullBits & 2) != 0) {
            int yawOffset = buffer.getIntLE(offset + 5);
            if (yawOffset < 0) {
                return ValidationResult.error("Invalid offset for Yaw");
            }
            pos = offset + 13 + yawOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Yaw");
            }
            int yawCount = VarInt.peek(buffer, pos);
            if (yawCount < 0) {
                return ValidationResult.error("Invalid array count for Yaw");
            }
            if (yawCount > 4096000) {
                return ValidationResult.error("Yaw exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += yawCount * 23) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading Yaw");
            }
        }
        if ((nullBits & 4) != 0) {
            int rollOffset = buffer.getIntLE(offset + 9);
            if (rollOffset < 0) {
                return ValidationResult.error("Invalid offset for Roll");
            }
            pos = offset + 13 + rollOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Roll");
            }
            int rollCount = VarInt.peek(buffer, pos);
            if (rollCount < 0) {
                return ValidationResult.error("Invalid array count for Roll");
            }
            if (rollCount > 4096000) {
                return ValidationResult.error("Roll exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += rollCount * 23) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading Roll");
            }
        }
        return ValidationResult.OK;
    }

    public RotationNoise clone() {
        RotationNoise copy = new RotationNoise();
        copy.pitch = this.pitch != null ? (NoiseConfig[])Arrays.stream(this.pitch).map(e -> e.clone()).toArray(NoiseConfig[]::new) : null;
        copy.yaw = this.yaw != null ? (NoiseConfig[])Arrays.stream(this.yaw).map(e -> e.clone()).toArray(NoiseConfig[]::new) : null;
        copy.roll = this.roll != null ? (NoiseConfig[])Arrays.stream(this.roll).map(e -> e.clone()).toArray(NoiseConfig[]::new) : null;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RotationNoise)) {
            return false;
        }
        RotationNoise other = (RotationNoise)obj;
        return Arrays.equals(this.pitch, other.pitch) && Arrays.equals(this.yaw, other.yaw) && Arrays.equals(this.roll, other.roll);
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + Arrays.hashCode(this.pitch);
        result = 31 * result + Arrays.hashCode(this.yaw);
        result = 31 * result + Arrays.hashCode(this.roll);
        return result;
    }
}

