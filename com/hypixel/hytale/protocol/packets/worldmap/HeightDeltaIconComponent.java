/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.worldmap;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarkerComponent;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HeightDeltaIconComponent
extends MapMarkerComponent {
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 9;
    public static final int VARIABLE_FIELD_COUNT = 2;
    public static final int VARIABLE_BLOCK_START = 17;
    public static final int MAX_SIZE = 32768027;
    public int upDelta;
    @Nullable
    public String upImage;
    public int downDelta;
    @Nullable
    public String downImage;

    public HeightDeltaIconComponent() {
    }

    public HeightDeltaIconComponent(int upDelta, @Nullable String upImage, int downDelta, @Nullable String downImage) {
        this.upDelta = upDelta;
        this.upImage = upImage;
        this.downDelta = downDelta;
        this.downImage = downImage;
    }

    public HeightDeltaIconComponent(@Nonnull HeightDeltaIconComponent other) {
        this.upDelta = other.upDelta;
        this.upImage = other.upImage;
        this.downDelta = other.downDelta;
        this.downImage = other.downImage;
    }

    @Nonnull
    public static HeightDeltaIconComponent deserialize(@Nonnull ByteBuf buf, int offset) {
        HeightDeltaIconComponent obj = new HeightDeltaIconComponent();
        byte nullBits = buf.getByte(offset);
        obj.upDelta = buf.getIntLE(offset + 1);
        obj.downDelta = buf.getIntLE(offset + 5);
        if ((nullBits & 1) != 0) {
            int varPos0 = offset + 17 + buf.getIntLE(offset + 9);
            int upImageLen = VarInt.peek(buf, varPos0);
            if (upImageLen < 0) {
                throw ProtocolException.negativeLength("UpImage", upImageLen);
            }
            if (upImageLen > 4096000) {
                throw ProtocolException.stringTooLong("UpImage", upImageLen, 4096000);
            }
            obj.upImage = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
        }
        if ((nullBits & 2) != 0) {
            int varPos1 = offset + 17 + buf.getIntLE(offset + 13);
            int downImageLen = VarInt.peek(buf, varPos1);
            if (downImageLen < 0) {
                throw ProtocolException.negativeLength("DownImage", downImageLen);
            }
            if (downImageLen > 4096000) {
                throw ProtocolException.stringTooLong("DownImage", downImageLen, 4096000);
            }
            obj.downImage = PacketIO.readVarString(buf, varPos1, PacketIO.UTF8);
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int sl;
        byte nullBits = buf.getByte(offset);
        int maxEnd = 17;
        if ((nullBits & 1) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 9);
            int pos0 = offset + 17 + fieldOffset0;
            sl = VarInt.peek(buf, pos0);
            if ((pos0 += VarInt.length(buf, pos0) + sl) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 2) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 13);
            int pos1 = offset + 17 + fieldOffset1;
            sl = VarInt.peek(buf, pos1);
            if ((pos1 += VarInt.length(buf, pos1) + sl) - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        return maxEnd;
    }

    @Override
    public int serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        byte nullBits = 0;
        if (this.upImage != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.downImage != null) {
            nullBits = (byte)(nullBits | 2);
        }
        buf.writeByte(nullBits);
        buf.writeIntLE(this.upDelta);
        buf.writeIntLE(this.downDelta);
        int upImageOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int downImageOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.upImage != null) {
            buf.setIntLE(upImageOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.upImage, 4096000);
        } else {
            buf.setIntLE(upImageOffsetSlot, -1);
        }
        if (this.downImage != null) {
            buf.setIntLE(downImageOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.downImage, 4096000);
        } else {
            buf.setIntLE(downImageOffsetSlot, -1);
        }
        return buf.writerIndex() - startPos;
    }

    @Override
    public int computeSize() {
        int size = 17;
        if (this.upImage != null) {
            size += PacketIO.stringSize(this.upImage);
        }
        if (this.downImage != null) {
            size += PacketIO.stringSize(this.downImage);
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int pos;
        if (buffer.readableBytes() - offset < 17) {
            return ValidationResult.error("Buffer too small: expected at least 17 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 1) != 0) {
            int upImageOffset = buffer.getIntLE(offset + 9);
            if (upImageOffset < 0) {
                return ValidationResult.error("Invalid offset for UpImage");
            }
            pos = offset + 17 + upImageOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for UpImage");
            }
            int upImageLen = VarInt.peek(buffer, pos);
            if (upImageLen < 0) {
                return ValidationResult.error("Invalid string length for UpImage");
            }
            if (upImageLen > 4096000) {
                return ValidationResult.error("UpImage exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += upImageLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading UpImage");
            }
        }
        if ((nullBits & 2) != 0) {
            int downImageOffset = buffer.getIntLE(offset + 13);
            if (downImageOffset < 0) {
                return ValidationResult.error("Invalid offset for DownImage");
            }
            pos = offset + 17 + downImageOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for DownImage");
            }
            int downImageLen = VarInt.peek(buffer, pos);
            if (downImageLen < 0) {
                return ValidationResult.error("Invalid string length for DownImage");
            }
            if (downImageLen > 4096000) {
                return ValidationResult.error("DownImage exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += downImageLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading DownImage");
            }
        }
        return ValidationResult.OK;
    }

    public HeightDeltaIconComponent clone() {
        HeightDeltaIconComponent copy = new HeightDeltaIconComponent();
        copy.upDelta = this.upDelta;
        copy.upImage = this.upImage;
        copy.downDelta = this.downDelta;
        copy.downImage = this.downImage;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HeightDeltaIconComponent)) {
            return false;
        }
        HeightDeltaIconComponent other = (HeightDeltaIconComponent)obj;
        return this.upDelta == other.upDelta && Objects.equals(this.upImage, other.upImage) && this.downDelta == other.downDelta && Objects.equals(this.downImage, other.downImage);
    }

    public int hashCode() {
        return Objects.hash(this.upDelta, this.upImage, this.downDelta, this.downImage);
    }
}

