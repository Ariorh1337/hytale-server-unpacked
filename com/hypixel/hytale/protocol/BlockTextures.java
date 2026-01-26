/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockTextures {
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 5;
    public static final int VARIABLE_FIELD_COUNT = 6;
    public static final int VARIABLE_BLOCK_START = 29;
    public static final int MAX_SIZE = 98304059;
    @Nullable
    public String top;
    @Nullable
    public String bottom;
    @Nullable
    public String front;
    @Nullable
    public String back;
    @Nullable
    public String left;
    @Nullable
    public String right;
    public float weight;

    public BlockTextures() {
    }

    public BlockTextures(@Nullable String top, @Nullable String bottom, @Nullable String front, @Nullable String back, @Nullable String left, @Nullable String right, float weight) {
        this.top = top;
        this.bottom = bottom;
        this.front = front;
        this.back = back;
        this.left = left;
        this.right = right;
        this.weight = weight;
    }

    public BlockTextures(@Nonnull BlockTextures other) {
        this.top = other.top;
        this.bottom = other.bottom;
        this.front = other.front;
        this.back = other.back;
        this.left = other.left;
        this.right = other.right;
        this.weight = other.weight;
    }

    @Nonnull
    public static BlockTextures deserialize(@Nonnull ByteBuf buf, int offset) {
        BlockTextures obj = new BlockTextures();
        byte nullBits = buf.getByte(offset);
        obj.weight = buf.getFloatLE(offset + 1);
        if ((nullBits & 1) != 0) {
            int varPos0 = offset + 29 + buf.getIntLE(offset + 5);
            int topLen = VarInt.peek(buf, varPos0);
            if (topLen < 0) {
                throw ProtocolException.negativeLength("Top", topLen);
            }
            if (topLen > 4096000) {
                throw ProtocolException.stringTooLong("Top", topLen, 4096000);
            }
            obj.top = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
        }
        if ((nullBits & 2) != 0) {
            int varPos1 = offset + 29 + buf.getIntLE(offset + 9);
            int bottomLen = VarInt.peek(buf, varPos1);
            if (bottomLen < 0) {
                throw ProtocolException.negativeLength("Bottom", bottomLen);
            }
            if (bottomLen > 4096000) {
                throw ProtocolException.stringTooLong("Bottom", bottomLen, 4096000);
            }
            obj.bottom = PacketIO.readVarString(buf, varPos1, PacketIO.UTF8);
        }
        if ((nullBits & 4) != 0) {
            int varPos2 = offset + 29 + buf.getIntLE(offset + 13);
            int frontLen = VarInt.peek(buf, varPos2);
            if (frontLen < 0) {
                throw ProtocolException.negativeLength("Front", frontLen);
            }
            if (frontLen > 4096000) {
                throw ProtocolException.stringTooLong("Front", frontLen, 4096000);
            }
            obj.front = PacketIO.readVarString(buf, varPos2, PacketIO.UTF8);
        }
        if ((nullBits & 8) != 0) {
            int varPos3 = offset + 29 + buf.getIntLE(offset + 17);
            int backLen = VarInt.peek(buf, varPos3);
            if (backLen < 0) {
                throw ProtocolException.negativeLength("Back", backLen);
            }
            if (backLen > 4096000) {
                throw ProtocolException.stringTooLong("Back", backLen, 4096000);
            }
            obj.back = PacketIO.readVarString(buf, varPos3, PacketIO.UTF8);
        }
        if ((nullBits & 0x10) != 0) {
            int varPos4 = offset + 29 + buf.getIntLE(offset + 21);
            int leftLen = VarInt.peek(buf, varPos4);
            if (leftLen < 0) {
                throw ProtocolException.negativeLength("Left", leftLen);
            }
            if (leftLen > 4096000) {
                throw ProtocolException.stringTooLong("Left", leftLen, 4096000);
            }
            obj.left = PacketIO.readVarString(buf, varPos4, PacketIO.UTF8);
        }
        if ((nullBits & 0x20) != 0) {
            int varPos5 = offset + 29 + buf.getIntLE(offset + 25);
            int rightLen = VarInt.peek(buf, varPos5);
            if (rightLen < 0) {
                throw ProtocolException.negativeLength("Right", rightLen);
            }
            if (rightLen > 4096000) {
                throw ProtocolException.stringTooLong("Right", rightLen, 4096000);
            }
            obj.right = PacketIO.readVarString(buf, varPos5, PacketIO.UTF8);
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int sl;
        byte nullBits = buf.getByte(offset);
        int maxEnd = 29;
        if ((nullBits & 1) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 5);
            int pos0 = offset + 29 + fieldOffset0;
            sl = VarInt.peek(buf, pos0);
            if ((pos0 += VarInt.length(buf, pos0) + sl) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 2) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 9);
            int pos1 = offset + 29 + fieldOffset1;
            sl = VarInt.peek(buf, pos1);
            if ((pos1 += VarInt.length(buf, pos1) + sl) - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        if ((nullBits & 4) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 13);
            int pos2 = offset + 29 + fieldOffset2;
            sl = VarInt.peek(buf, pos2);
            if ((pos2 += VarInt.length(buf, pos2) + sl) - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        if ((nullBits & 8) != 0) {
            int fieldOffset3 = buf.getIntLE(offset + 17);
            int pos3 = offset + 29 + fieldOffset3;
            sl = VarInt.peek(buf, pos3);
            if ((pos3 += VarInt.length(buf, pos3) + sl) - offset > maxEnd) {
                maxEnd = pos3 - offset;
            }
        }
        if ((nullBits & 0x10) != 0) {
            int fieldOffset4 = buf.getIntLE(offset + 21);
            int pos4 = offset + 29 + fieldOffset4;
            sl = VarInt.peek(buf, pos4);
            if ((pos4 += VarInt.length(buf, pos4) + sl) - offset > maxEnd) {
                maxEnd = pos4 - offset;
            }
        }
        if ((nullBits & 0x20) != 0) {
            int fieldOffset5 = buf.getIntLE(offset + 25);
            int pos5 = offset + 29 + fieldOffset5;
            sl = VarInt.peek(buf, pos5);
            if ((pos5 += VarInt.length(buf, pos5) + sl) - offset > maxEnd) {
                maxEnd = pos5 - offset;
            }
        }
        return maxEnd;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        byte nullBits = 0;
        if (this.top != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.bottom != null) {
            nullBits = (byte)(nullBits | 2);
        }
        if (this.front != null) {
            nullBits = (byte)(nullBits | 4);
        }
        if (this.back != null) {
            nullBits = (byte)(nullBits | 8);
        }
        if (this.left != null) {
            nullBits = (byte)(nullBits | 0x10);
        }
        if (this.right != null) {
            nullBits = (byte)(nullBits | 0x20);
        }
        buf.writeByte(nullBits);
        buf.writeFloatLE(this.weight);
        int topOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int bottomOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int frontOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int backOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int leftOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int rightOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.top != null) {
            buf.setIntLE(topOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.top, 4096000);
        } else {
            buf.setIntLE(topOffsetSlot, -1);
        }
        if (this.bottom != null) {
            buf.setIntLE(bottomOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.bottom, 4096000);
        } else {
            buf.setIntLE(bottomOffsetSlot, -1);
        }
        if (this.front != null) {
            buf.setIntLE(frontOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.front, 4096000);
        } else {
            buf.setIntLE(frontOffsetSlot, -1);
        }
        if (this.back != null) {
            buf.setIntLE(backOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.back, 4096000);
        } else {
            buf.setIntLE(backOffsetSlot, -1);
        }
        if (this.left != null) {
            buf.setIntLE(leftOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.left, 4096000);
        } else {
            buf.setIntLE(leftOffsetSlot, -1);
        }
        if (this.right != null) {
            buf.setIntLE(rightOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.right, 4096000);
        } else {
            buf.setIntLE(rightOffsetSlot, -1);
        }
    }

    public int computeSize() {
        int size = 29;
        if (this.top != null) {
            size += PacketIO.stringSize(this.top);
        }
        if (this.bottom != null) {
            size += PacketIO.stringSize(this.bottom);
        }
        if (this.front != null) {
            size += PacketIO.stringSize(this.front);
        }
        if (this.back != null) {
            size += PacketIO.stringSize(this.back);
        }
        if (this.left != null) {
            size += PacketIO.stringSize(this.left);
        }
        if (this.right != null) {
            size += PacketIO.stringSize(this.right);
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int pos;
        if (buffer.readableBytes() - offset < 29) {
            return ValidationResult.error("Buffer too small: expected at least 29 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 1) != 0) {
            int topOffset = buffer.getIntLE(offset + 5);
            if (topOffset < 0) {
                return ValidationResult.error("Invalid offset for Top");
            }
            pos = offset + 29 + topOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Top");
            }
            int topLen = VarInt.peek(buffer, pos);
            if (topLen < 0) {
                return ValidationResult.error("Invalid string length for Top");
            }
            if (topLen > 4096000) {
                return ValidationResult.error("Top exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += topLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading Top");
            }
        }
        if ((nullBits & 2) != 0) {
            int bottomOffset = buffer.getIntLE(offset + 9);
            if (bottomOffset < 0) {
                return ValidationResult.error("Invalid offset for Bottom");
            }
            pos = offset + 29 + bottomOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Bottom");
            }
            int bottomLen = VarInt.peek(buffer, pos);
            if (bottomLen < 0) {
                return ValidationResult.error("Invalid string length for Bottom");
            }
            if (bottomLen > 4096000) {
                return ValidationResult.error("Bottom exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += bottomLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading Bottom");
            }
        }
        if ((nullBits & 4) != 0) {
            int frontOffset = buffer.getIntLE(offset + 13);
            if (frontOffset < 0) {
                return ValidationResult.error("Invalid offset for Front");
            }
            pos = offset + 29 + frontOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Front");
            }
            int frontLen = VarInt.peek(buffer, pos);
            if (frontLen < 0) {
                return ValidationResult.error("Invalid string length for Front");
            }
            if (frontLen > 4096000) {
                return ValidationResult.error("Front exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += frontLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading Front");
            }
        }
        if ((nullBits & 8) != 0) {
            int backOffset = buffer.getIntLE(offset + 17);
            if (backOffset < 0) {
                return ValidationResult.error("Invalid offset for Back");
            }
            pos = offset + 29 + backOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Back");
            }
            int backLen = VarInt.peek(buffer, pos);
            if (backLen < 0) {
                return ValidationResult.error("Invalid string length for Back");
            }
            if (backLen > 4096000) {
                return ValidationResult.error("Back exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += backLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading Back");
            }
        }
        if ((nullBits & 0x10) != 0) {
            int leftOffset = buffer.getIntLE(offset + 21);
            if (leftOffset < 0) {
                return ValidationResult.error("Invalid offset for Left");
            }
            pos = offset + 29 + leftOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Left");
            }
            int leftLen = VarInt.peek(buffer, pos);
            if (leftLen < 0) {
                return ValidationResult.error("Invalid string length for Left");
            }
            if (leftLen > 4096000) {
                return ValidationResult.error("Left exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += leftLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading Left");
            }
        }
        if ((nullBits & 0x20) != 0) {
            int rightOffset = buffer.getIntLE(offset + 25);
            if (rightOffset < 0) {
                return ValidationResult.error("Invalid offset for Right");
            }
            pos = offset + 29 + rightOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Right");
            }
            int rightLen = VarInt.peek(buffer, pos);
            if (rightLen < 0) {
                return ValidationResult.error("Invalid string length for Right");
            }
            if (rightLen > 4096000) {
                return ValidationResult.error("Right exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += rightLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading Right");
            }
        }
        return ValidationResult.OK;
    }

    public BlockTextures clone() {
        BlockTextures copy = new BlockTextures();
        copy.top = this.top;
        copy.bottom = this.bottom;
        copy.front = this.front;
        copy.back = this.back;
        copy.left = this.left;
        copy.right = this.right;
        copy.weight = this.weight;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BlockTextures)) {
            return false;
        }
        BlockTextures other = (BlockTextures)obj;
        return Objects.equals(this.top, other.top) && Objects.equals(this.bottom, other.bottom) && Objects.equals(this.front, other.front) && Objects.equals(this.back, other.back) && Objects.equals(this.left, other.left) && Objects.equals(this.right, other.right) && this.weight == other.weight;
    }

    public int hashCode() {
        return Objects.hash(this.top, this.bottom, this.front, this.back, this.left, this.right, Float.valueOf(this.weight));
    }
}

