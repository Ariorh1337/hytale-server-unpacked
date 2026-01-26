/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.ColorAlpha;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Cloud {
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 1;
    public static final int VARIABLE_FIELD_COUNT = 3;
    public static final int VARIABLE_BLOCK_START = 13;
    public static final int MAX_SIZE = 81920028;
    @Nullable
    public String texture;
    @Nullable
    public Map<Float, Float> speeds;
    @Nullable
    public Map<Float, ColorAlpha> colors;

    public Cloud() {
    }

    public Cloud(@Nullable String texture, @Nullable Map<Float, Float> speeds, @Nullable Map<Float, ColorAlpha> colors) {
        this.texture = texture;
        this.speeds = speeds;
        this.colors = colors;
    }

    public Cloud(@Nonnull Cloud other) {
        this.texture = other.texture;
        this.speeds = other.speeds;
        this.colors = other.colors;
    }

    @Nonnull
    public static Cloud deserialize(@Nonnull ByteBuf buf, int offset) {
        float key;
        int i;
        int dictPos;
        int varIntLen;
        Cloud obj = new Cloud();
        byte nullBits = buf.getByte(offset);
        if ((nullBits & 1) != 0) {
            int varPos0 = offset + 13 + buf.getIntLE(offset + 1);
            int textureLen = VarInt.peek(buf, varPos0);
            if (textureLen < 0) {
                throw ProtocolException.negativeLength("Texture", textureLen);
            }
            if (textureLen > 4096000) {
                throw ProtocolException.stringTooLong("Texture", textureLen, 4096000);
            }
            obj.texture = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
        }
        if ((nullBits & 2) != 0) {
            int varPos1 = offset + 13 + buf.getIntLE(offset + 5);
            int speedsCount = VarInt.peek(buf, varPos1);
            if (speedsCount < 0) {
                throw ProtocolException.negativeLength("Speeds", speedsCount);
            }
            if (speedsCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Speeds", speedsCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos1);
            obj.speeds = new HashMap<Float, Float>(speedsCount);
            dictPos = varPos1 + varIntLen;
            for (i = 0; i < speedsCount; ++i) {
                key = buf.getFloatLE(dictPos);
                float val = buf.getFloatLE(dictPos += 4);
                dictPos += 4;
                if (obj.speeds.put(Float.valueOf(key), Float.valueOf(val)) == null) continue;
                throw ProtocolException.duplicateKey("speeds", Float.valueOf(key));
            }
        }
        if ((nullBits & 4) != 0) {
            int varPos2 = offset + 13 + buf.getIntLE(offset + 9);
            int colorsCount = VarInt.peek(buf, varPos2);
            if (colorsCount < 0) {
                throw ProtocolException.negativeLength("Colors", colorsCount);
            }
            if (colorsCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Colors", colorsCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos2);
            obj.colors = new HashMap<Float, ColorAlpha>(colorsCount);
            dictPos = varPos2 + varIntLen;
            for (i = 0; i < colorsCount; ++i) {
                key = buf.getFloatLE(dictPos);
                ColorAlpha val = ColorAlpha.deserialize(buf, dictPos += 4);
                dictPos += ColorAlpha.computeBytesConsumed(buf, dictPos);
                if (obj.colors.put(Float.valueOf(key), val) == null) continue;
                throw ProtocolException.duplicateKey("colors", Float.valueOf(key));
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int i;
        int dictLen;
        byte nullBits = buf.getByte(offset);
        int maxEnd = 13;
        if ((nullBits & 1) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 1);
            int pos0 = offset + 13 + fieldOffset0;
            int sl = VarInt.peek(buf, pos0);
            if ((pos0 += VarInt.length(buf, pos0) + sl) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 2) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 5);
            int pos1 = offset + 13 + fieldOffset1;
            dictLen = VarInt.peek(buf, pos1);
            pos1 += VarInt.length(buf, pos1);
            for (i = 0; i < dictLen; ++i) {
                pos1 += 4;
                pos1 += 4;
            }
            if (pos1 - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        if ((nullBits & 4) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 9);
            int pos2 = offset + 13 + fieldOffset2;
            dictLen = VarInt.peek(buf, pos2);
            pos2 += VarInt.length(buf, pos2);
            for (i = 0; i < dictLen; ++i) {
                pos2 += 4;
                pos2 += ColorAlpha.computeBytesConsumed(buf, pos2);
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
        if (this.texture != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.speeds != null) {
            nullBits = (byte)(nullBits | 2);
        }
        if (this.colors != null) {
            nullBits = (byte)(nullBits | 4);
        }
        buf.writeByte(nullBits);
        int textureOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int speedsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int colorsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.texture != null) {
            buf.setIntLE(textureOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.texture, 4096000);
        } else {
            buf.setIntLE(textureOffsetSlot, -1);
        }
        if (this.speeds != null) {
            buf.setIntLE(speedsOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.speeds.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Speeds", this.speeds.size(), 4096000);
            }
            VarInt.write(buf, this.speeds.size());
            for (Map.Entry<Float, Float> entry : this.speeds.entrySet()) {
                buf.writeFloatLE(entry.getKey().floatValue());
                buf.writeFloatLE(entry.getValue().floatValue());
            }
        } else {
            buf.setIntLE(speedsOffsetSlot, -1);
        }
        if (this.colors != null) {
            buf.setIntLE(colorsOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.colors.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Colors", this.colors.size(), 4096000);
            }
            VarInt.write(buf, this.colors.size());
            for (Map.Entry<Float, Object> entry : this.colors.entrySet()) {
                buf.writeFloatLE(entry.getKey().floatValue());
                ((ColorAlpha)entry.getValue()).serialize(buf);
            }
        } else {
            buf.setIntLE(colorsOffsetSlot, -1);
        }
    }

    public int computeSize() {
        int size = 13;
        if (this.texture != null) {
            size += PacketIO.stringSize(this.texture);
        }
        if (this.speeds != null) {
            size += VarInt.size(this.speeds.size()) + this.speeds.size() * 8;
        }
        if (this.colors != null) {
            size += VarInt.size(this.colors.size()) + this.colors.size() * 8;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int i;
        int pos;
        if (buffer.readableBytes() - offset < 13) {
            return ValidationResult.error("Buffer too small: expected at least 13 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 1) != 0) {
            int textureOffset = buffer.getIntLE(offset + 1);
            if (textureOffset < 0) {
                return ValidationResult.error("Invalid offset for Texture");
            }
            pos = offset + 13 + textureOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Texture");
            }
            int textureLen = VarInt.peek(buffer, pos);
            if (textureLen < 0) {
                return ValidationResult.error("Invalid string length for Texture");
            }
            if (textureLen > 4096000) {
                return ValidationResult.error("Texture exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += textureLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading Texture");
            }
        }
        if ((nullBits & 2) != 0) {
            int speedsOffset = buffer.getIntLE(offset + 5);
            if (speedsOffset < 0) {
                return ValidationResult.error("Invalid offset for Speeds");
            }
            pos = offset + 13 + speedsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Speeds");
            }
            int speedsCount = VarInt.peek(buffer, pos);
            if (speedsCount < 0) {
                return ValidationResult.error("Invalid dictionary count for Speeds");
            }
            if (speedsCount > 4096000) {
                return ValidationResult.error("Speeds exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < speedsCount; ++i) {
                if ((pos += 4) > buffer.writerIndex()) {
                    return ValidationResult.error("Buffer overflow reading key");
                }
                if ((pos += 4) <= buffer.writerIndex()) continue;
                return ValidationResult.error("Buffer overflow reading value");
            }
        }
        if ((nullBits & 4) != 0) {
            int colorsOffset = buffer.getIntLE(offset + 9);
            if (colorsOffset < 0) {
                return ValidationResult.error("Invalid offset for Colors");
            }
            pos = offset + 13 + colorsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Colors");
            }
            int colorsCount = VarInt.peek(buffer, pos);
            if (colorsCount < 0) {
                return ValidationResult.error("Invalid dictionary count for Colors");
            }
            if (colorsCount > 4096000) {
                return ValidationResult.error("Colors exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < colorsCount; ++i) {
                if ((pos += 4) > buffer.writerIndex()) {
                    return ValidationResult.error("Buffer overflow reading key");
                }
                pos += 4;
            }
        }
        return ValidationResult.OK;
    }

    public Cloud clone() {
        Cloud copy = new Cloud();
        copy.texture = this.texture;
        HashMap<Float, Float> hashMap = copy.speeds = this.speeds != null ? new HashMap<Float, Float>(this.speeds) : null;
        if (this.colors != null) {
            HashMap<Float, ColorAlpha> m = new HashMap<Float, ColorAlpha>();
            for (Map.Entry<Float, ColorAlpha> e : this.colors.entrySet()) {
                m.put(e.getKey(), e.getValue().clone());
            }
            copy.colors = m;
        }
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Cloud)) {
            return false;
        }
        Cloud other = (Cloud)obj;
        return Objects.equals(this.texture, other.texture) && Objects.equals(this.speeds, other.speeds) && Objects.equals(this.colors, other.colors);
    }

    public int hashCode() {
        return Objects.hash(this.texture, this.speeds, this.colors);
    }
}

