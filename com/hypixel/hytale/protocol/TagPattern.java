/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.TagPatternType;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TagPattern {
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 6;
    public static final int VARIABLE_FIELD_COUNT = 2;
    public static final int VARIABLE_BLOCK_START = 14;
    public static final int MAX_SIZE = 0x64000000;
    @Nonnull
    public TagPatternType type = TagPatternType.Equals;
    public int tagIndex;
    @Nullable
    public TagPattern[] operands;
    @Nullable
    public TagPattern not;

    public TagPattern() {
    }

    public TagPattern(@Nonnull TagPatternType type, int tagIndex, @Nullable TagPattern[] operands, @Nullable TagPattern not) {
        this.type = type;
        this.tagIndex = tagIndex;
        this.operands = operands;
        this.not = not;
    }

    public TagPattern(@Nonnull TagPattern other) {
        this.type = other.type;
        this.tagIndex = other.tagIndex;
        this.operands = other.operands;
        this.not = other.not;
    }

    @Nonnull
    public static TagPattern deserialize(@Nonnull ByteBuf buf, int offset) {
        TagPattern obj = new TagPattern();
        byte nullBits = buf.getByte(offset);
        obj.type = TagPatternType.fromValue(buf.getByte(offset + 1));
        obj.tagIndex = buf.getIntLE(offset + 2);
        if ((nullBits & 1) != 0) {
            int varPos0 = offset + 14 + buf.getIntLE(offset + 6);
            int operandsCount = VarInt.peek(buf, varPos0);
            if (operandsCount < 0) {
                throw ProtocolException.negativeLength("Operands", operandsCount);
            }
            if (operandsCount > 4096000) {
                throw ProtocolException.arrayTooLong("Operands", operandsCount, 4096000);
            }
            int varIntLen = VarInt.length(buf, varPos0);
            if ((long)(varPos0 + varIntLen) + (long)operandsCount * 6L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("Operands", varPos0 + varIntLen + operandsCount * 6, buf.readableBytes());
            }
            obj.operands = new TagPattern[operandsCount];
            int elemPos = varPos0 + varIntLen;
            for (int i = 0; i < operandsCount; ++i) {
                obj.operands[i] = TagPattern.deserialize(buf, elemPos);
                elemPos += TagPattern.computeBytesConsumed(buf, elemPos);
            }
        }
        if ((nullBits & 2) != 0) {
            int varPos1 = offset + 14 + buf.getIntLE(offset + 10);
            obj.not = TagPattern.deserialize(buf, varPos1);
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        byte nullBits = buf.getByte(offset);
        int maxEnd = 14;
        if ((nullBits & 1) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 6);
            int pos0 = offset + 14 + fieldOffset0;
            int arrLen = VarInt.peek(buf, pos0);
            pos0 += VarInt.length(buf, pos0);
            for (int i = 0; i < arrLen; ++i) {
                pos0 += TagPattern.computeBytesConsumed(buf, pos0);
            }
            if (pos0 - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 2) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 10);
            int pos1 = offset + 14 + fieldOffset1;
            if ((pos1 += TagPattern.computeBytesConsumed(buf, pos1)) - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        return maxEnd;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        byte nullBits = 0;
        if (this.operands != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.not != null) {
            nullBits = (byte)(nullBits | 2);
        }
        buf.writeByte(nullBits);
        buf.writeByte(this.type.getValue());
        buf.writeIntLE(this.tagIndex);
        int operandsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int notOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.operands != null) {
            buf.setIntLE(operandsOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.operands.length > 4096000) {
                throw ProtocolException.arrayTooLong("Operands", this.operands.length, 4096000);
            }
            VarInt.write(buf, this.operands.length);
            for (TagPattern item : this.operands) {
                item.serialize(buf);
            }
        } else {
            buf.setIntLE(operandsOffsetSlot, -1);
        }
        if (this.not != null) {
            buf.setIntLE(notOffsetSlot, buf.writerIndex() - varBlockStart);
            this.not.serialize(buf);
        } else {
            buf.setIntLE(notOffsetSlot, -1);
        }
    }

    public int computeSize() {
        int size = 14;
        if (this.operands != null) {
            int operandsSize = 0;
            for (TagPattern elem : this.operands) {
                operandsSize += elem.computeSize();
            }
            size += VarInt.size(this.operands.length) + operandsSize;
        }
        if (this.not != null) {
            size += this.not.computeSize();
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int pos;
        if (buffer.readableBytes() - offset < 14) {
            return ValidationResult.error("Buffer too small: expected at least 14 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 1) != 0) {
            int operandsOffset = buffer.getIntLE(offset + 6);
            if (operandsOffset < 0) {
                return ValidationResult.error("Invalid offset for Operands");
            }
            pos = offset + 14 + operandsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Operands");
            }
            int operandsCount = VarInt.peek(buffer, pos);
            if (operandsCount < 0) {
                return ValidationResult.error("Invalid array count for Operands");
            }
            if (operandsCount > 4096000) {
                return ValidationResult.error("Operands exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (int i = 0; i < operandsCount; ++i) {
                ValidationResult structResult = TagPattern.validateStructure(buffer, pos);
                if (!structResult.isValid()) {
                    return ValidationResult.error("Invalid TagPattern in Operands[" + i + "]: " + structResult.error());
                }
                pos += TagPattern.computeBytesConsumed(buffer, pos);
            }
        }
        if ((nullBits & 2) != 0) {
            int notOffset = buffer.getIntLE(offset + 10);
            if (notOffset < 0) {
                return ValidationResult.error("Invalid offset for Not");
            }
            pos = offset + 14 + notOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Not");
            }
            ValidationResult notResult = TagPattern.validateStructure(buffer, pos);
            if (!notResult.isValid()) {
                return ValidationResult.error("Invalid Not: " + notResult.error());
            }
            pos += TagPattern.computeBytesConsumed(buffer, pos);
        }
        return ValidationResult.OK;
    }

    public TagPattern clone() {
        TagPattern copy = new TagPattern();
        copy.type = this.type;
        copy.tagIndex = this.tagIndex;
        copy.operands = this.operands != null ? (TagPattern[])Arrays.stream(this.operands).map(e -> e.clone()).toArray(TagPattern[]::new) : null;
        copy.not = this.not != null ? this.not.clone() : null;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TagPattern)) {
            return false;
        }
        TagPattern other = (TagPattern)obj;
        return Objects.equals((Object)this.type, (Object)other.type) && this.tagIndex == other.tagIndex && Arrays.equals(this.operands, other.operands) && Objects.equals(this.not, other.not);
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + Objects.hashCode((Object)this.type);
        result = 31 * result + Integer.hashCode(this.tagIndex);
        result = 31 * result + Arrays.hashCode(this.operands);
        result = 31 * result + Objects.hashCode(this.not);
        return result;
    }
}

