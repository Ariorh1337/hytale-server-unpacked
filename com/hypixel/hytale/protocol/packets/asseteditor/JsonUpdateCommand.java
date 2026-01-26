/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.asseteditor;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorRebuildCaches;
import com.hypixel.hytale.protocol.packets.asseteditor.JsonUpdateType;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class JsonUpdateCommand {
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 7;
    public static final int VARIABLE_FIELD_COUNT = 4;
    public static final int VARIABLE_BLOCK_START = 23;
    public static final int MAX_SIZE = 0x64000000;
    @Nonnull
    public JsonUpdateType type = JsonUpdateType.SetProperty;
    @Nullable
    public String[] path;
    @Nullable
    public String value;
    @Nullable
    public String previousValue;
    @Nullable
    public String[] firstCreatedProperty;
    @Nullable
    public AssetEditorRebuildCaches rebuildCaches;

    public JsonUpdateCommand() {
    }

    public JsonUpdateCommand(@Nonnull JsonUpdateType type, @Nullable String[] path, @Nullable String value, @Nullable String previousValue, @Nullable String[] firstCreatedProperty, @Nullable AssetEditorRebuildCaches rebuildCaches) {
        this.type = type;
        this.path = path;
        this.value = value;
        this.previousValue = previousValue;
        this.firstCreatedProperty = firstCreatedProperty;
        this.rebuildCaches = rebuildCaches;
    }

    public JsonUpdateCommand(@Nonnull JsonUpdateCommand other) {
        this.type = other.type;
        this.path = other.path;
        this.value = other.value;
        this.previousValue = other.previousValue;
        this.firstCreatedProperty = other.firstCreatedProperty;
        this.rebuildCaches = other.rebuildCaches;
    }

    @Nonnull
    public static JsonUpdateCommand deserialize(@Nonnull ByteBuf buf, int offset) {
        int strVarLen;
        int strLen;
        int i;
        int elemPos;
        int varIntLen;
        JsonUpdateCommand obj = new JsonUpdateCommand();
        byte nullBits = buf.getByte(offset);
        obj.type = JsonUpdateType.fromValue(buf.getByte(offset + 1));
        if ((nullBits & 1) != 0) {
            obj.rebuildCaches = AssetEditorRebuildCaches.deserialize(buf, offset + 2);
        }
        if ((nullBits & 2) != 0) {
            int varPos0 = offset + 23 + buf.getIntLE(offset + 7);
            int pathCount = VarInt.peek(buf, varPos0);
            if (pathCount < 0) {
                throw ProtocolException.negativeLength("Path", pathCount);
            }
            if (pathCount > 4096000) {
                throw ProtocolException.arrayTooLong("Path", pathCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos0);
            if ((long)(varPos0 + varIntLen) + (long)pathCount * 1L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("Path", varPos0 + varIntLen + pathCount * 1, buf.readableBytes());
            }
            obj.path = new String[pathCount];
            elemPos = varPos0 + varIntLen;
            for (i = 0; i < pathCount; ++i) {
                strLen = VarInt.peek(buf, elemPos);
                if (strLen < 0) {
                    throw ProtocolException.negativeLength("path[" + i + "]", strLen);
                }
                if (strLen > 4096000) {
                    throw ProtocolException.stringTooLong("path[" + i + "]", strLen, 4096000);
                }
                strVarLen = VarInt.length(buf, elemPos);
                obj.path[i] = PacketIO.readVarString(buf, elemPos);
                elemPos += strVarLen + strLen;
            }
        }
        if ((nullBits & 4) != 0) {
            int varPos1 = offset + 23 + buf.getIntLE(offset + 11);
            int valueLen = VarInt.peek(buf, varPos1);
            if (valueLen < 0) {
                throw ProtocolException.negativeLength("Value", valueLen);
            }
            if (valueLen > 4096000) {
                throw ProtocolException.stringTooLong("Value", valueLen, 4096000);
            }
            obj.value = PacketIO.readVarString(buf, varPos1, PacketIO.UTF8);
        }
        if ((nullBits & 8) != 0) {
            int varPos2 = offset + 23 + buf.getIntLE(offset + 15);
            int previousValueLen = VarInt.peek(buf, varPos2);
            if (previousValueLen < 0) {
                throw ProtocolException.negativeLength("PreviousValue", previousValueLen);
            }
            if (previousValueLen > 4096000) {
                throw ProtocolException.stringTooLong("PreviousValue", previousValueLen, 4096000);
            }
            obj.previousValue = PacketIO.readVarString(buf, varPos2, PacketIO.UTF8);
        }
        if ((nullBits & 0x10) != 0) {
            int varPos3 = offset + 23 + buf.getIntLE(offset + 19);
            int firstCreatedPropertyCount = VarInt.peek(buf, varPos3);
            if (firstCreatedPropertyCount < 0) {
                throw ProtocolException.negativeLength("FirstCreatedProperty", firstCreatedPropertyCount);
            }
            if (firstCreatedPropertyCount > 4096000) {
                throw ProtocolException.arrayTooLong("FirstCreatedProperty", firstCreatedPropertyCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos3);
            if ((long)(varPos3 + varIntLen) + (long)firstCreatedPropertyCount * 1L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("FirstCreatedProperty", varPos3 + varIntLen + firstCreatedPropertyCount * 1, buf.readableBytes());
            }
            obj.firstCreatedProperty = new String[firstCreatedPropertyCount];
            elemPos = varPos3 + varIntLen;
            for (i = 0; i < firstCreatedPropertyCount; ++i) {
                strLen = VarInt.peek(buf, elemPos);
                if (strLen < 0) {
                    throw ProtocolException.negativeLength("firstCreatedProperty[" + i + "]", strLen);
                }
                if (strLen > 4096000) {
                    throw ProtocolException.stringTooLong("firstCreatedProperty[" + i + "]", strLen, 4096000);
                }
                strVarLen = VarInt.length(buf, elemPos);
                obj.firstCreatedProperty[i] = PacketIO.readVarString(buf, elemPos);
                elemPos += strVarLen + strLen;
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int sl;
        int sl2;
        int i;
        int arrLen;
        byte nullBits = buf.getByte(offset);
        int maxEnd = 23;
        if ((nullBits & 2) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 7);
            int pos0 = offset + 23 + fieldOffset0;
            arrLen = VarInt.peek(buf, pos0);
            pos0 += VarInt.length(buf, pos0);
            for (i = 0; i < arrLen; ++i) {
                sl2 = VarInt.peek(buf, pos0);
                pos0 += VarInt.length(buf, pos0) + sl2;
            }
            if (pos0 - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 4) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 11);
            int pos1 = offset + 23 + fieldOffset1;
            sl = VarInt.peek(buf, pos1);
            if ((pos1 += VarInt.length(buf, pos1) + sl) - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        if ((nullBits & 8) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 15);
            int pos2 = offset + 23 + fieldOffset2;
            sl = VarInt.peek(buf, pos2);
            if ((pos2 += VarInt.length(buf, pos2) + sl) - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        if ((nullBits & 0x10) != 0) {
            int fieldOffset3 = buf.getIntLE(offset + 19);
            int pos3 = offset + 23 + fieldOffset3;
            arrLen = VarInt.peek(buf, pos3);
            pos3 += VarInt.length(buf, pos3);
            for (i = 0; i < arrLen; ++i) {
                sl2 = VarInt.peek(buf, pos3);
                pos3 += VarInt.length(buf, pos3) + sl2;
            }
            if (pos3 - offset > maxEnd) {
                maxEnd = pos3 - offset;
            }
        }
        return maxEnd;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        byte nullBits = 0;
        if (this.rebuildCaches != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.path != null) {
            nullBits = (byte)(nullBits | 2);
        }
        if (this.value != null) {
            nullBits = (byte)(nullBits | 4);
        }
        if (this.previousValue != null) {
            nullBits = (byte)(nullBits | 8);
        }
        if (this.firstCreatedProperty != null) {
            nullBits = (byte)(nullBits | 0x10);
        }
        buf.writeByte(nullBits);
        buf.writeByte(this.type.getValue());
        if (this.rebuildCaches != null) {
            this.rebuildCaches.serialize(buf);
        } else {
            buf.writeZero(5);
        }
        int pathOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int valueOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int previousValueOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int firstCreatedPropertyOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.path != null) {
            buf.setIntLE(pathOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.path.length > 4096000) {
                throw ProtocolException.arrayTooLong("Path", this.path.length, 4096000);
            }
            VarInt.write(buf, this.path.length);
            for (String item : this.path) {
                PacketIO.writeVarString(buf, item, 4096000);
            }
        } else {
            buf.setIntLE(pathOffsetSlot, -1);
        }
        if (this.value != null) {
            buf.setIntLE(valueOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.value, 4096000);
        } else {
            buf.setIntLE(valueOffsetSlot, -1);
        }
        if (this.previousValue != null) {
            buf.setIntLE(previousValueOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.previousValue, 4096000);
        } else {
            buf.setIntLE(previousValueOffsetSlot, -1);
        }
        if (this.firstCreatedProperty != null) {
            buf.setIntLE(firstCreatedPropertyOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.firstCreatedProperty.length > 4096000) {
                throw ProtocolException.arrayTooLong("FirstCreatedProperty", this.firstCreatedProperty.length, 4096000);
            }
            VarInt.write(buf, this.firstCreatedProperty.length);
            for (String item : this.firstCreatedProperty) {
                PacketIO.writeVarString(buf, item, 4096000);
            }
        } else {
            buf.setIntLE(firstCreatedPropertyOffsetSlot, -1);
        }
    }

    public int computeSize() {
        int size = 23;
        if (this.path != null) {
            int pathSize = 0;
            for (String elem : this.path) {
                pathSize += PacketIO.stringSize(elem);
            }
            size += VarInt.size(this.path.length) + pathSize;
        }
        if (this.value != null) {
            size += PacketIO.stringSize(this.value);
        }
        if (this.previousValue != null) {
            size += PacketIO.stringSize(this.previousValue);
        }
        if (this.firstCreatedProperty != null) {
            int firstCreatedPropertySize = 0;
            for (String elem : this.firstCreatedProperty) {
                firstCreatedPropertySize += PacketIO.stringSize(elem);
            }
            size += VarInt.size(this.firstCreatedProperty.length) + firstCreatedPropertySize;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int strLen;
        int i;
        int pos;
        if (buffer.readableBytes() - offset < 23) {
            return ValidationResult.error("Buffer too small: expected at least 23 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 2) != 0) {
            int pathOffset = buffer.getIntLE(offset + 7);
            if (pathOffset < 0) {
                return ValidationResult.error("Invalid offset for Path");
            }
            pos = offset + 23 + pathOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Path");
            }
            int pathCount = VarInt.peek(buffer, pos);
            if (pathCount < 0) {
                return ValidationResult.error("Invalid array count for Path");
            }
            if (pathCount > 4096000) {
                return ValidationResult.error("Path exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < pathCount; ++i) {
                strLen = VarInt.peek(buffer, pos);
                if (strLen < 0) {
                    return ValidationResult.error("Invalid string length in Path");
                }
                pos += VarInt.length(buffer, pos);
                if ((pos += strLen) <= buffer.writerIndex()) continue;
                return ValidationResult.error("Buffer overflow reading string in Path");
            }
        }
        if ((nullBits & 4) != 0) {
            int valueOffset = buffer.getIntLE(offset + 11);
            if (valueOffset < 0) {
                return ValidationResult.error("Invalid offset for Value");
            }
            pos = offset + 23 + valueOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Value");
            }
            int valueLen = VarInt.peek(buffer, pos);
            if (valueLen < 0) {
                return ValidationResult.error("Invalid string length for Value");
            }
            if (valueLen > 4096000) {
                return ValidationResult.error("Value exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += valueLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading Value");
            }
        }
        if ((nullBits & 8) != 0) {
            int previousValueOffset = buffer.getIntLE(offset + 15);
            if (previousValueOffset < 0) {
                return ValidationResult.error("Invalid offset for PreviousValue");
            }
            pos = offset + 23 + previousValueOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for PreviousValue");
            }
            int previousValueLen = VarInt.peek(buffer, pos);
            if (previousValueLen < 0) {
                return ValidationResult.error("Invalid string length for PreviousValue");
            }
            if (previousValueLen > 4096000) {
                return ValidationResult.error("PreviousValue exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += previousValueLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading PreviousValue");
            }
        }
        if ((nullBits & 0x10) != 0) {
            int firstCreatedPropertyOffset = buffer.getIntLE(offset + 19);
            if (firstCreatedPropertyOffset < 0) {
                return ValidationResult.error("Invalid offset for FirstCreatedProperty");
            }
            pos = offset + 23 + firstCreatedPropertyOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for FirstCreatedProperty");
            }
            int firstCreatedPropertyCount = VarInt.peek(buffer, pos);
            if (firstCreatedPropertyCount < 0) {
                return ValidationResult.error("Invalid array count for FirstCreatedProperty");
            }
            if (firstCreatedPropertyCount > 4096000) {
                return ValidationResult.error("FirstCreatedProperty exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < firstCreatedPropertyCount; ++i) {
                strLen = VarInt.peek(buffer, pos);
                if (strLen < 0) {
                    return ValidationResult.error("Invalid string length in FirstCreatedProperty");
                }
                pos += VarInt.length(buffer, pos);
                if ((pos += strLen) <= buffer.writerIndex()) continue;
                return ValidationResult.error("Buffer overflow reading string in FirstCreatedProperty");
            }
        }
        return ValidationResult.OK;
    }

    public JsonUpdateCommand clone() {
        JsonUpdateCommand copy = new JsonUpdateCommand();
        copy.type = this.type;
        copy.path = this.path != null ? Arrays.copyOf(this.path, this.path.length) : null;
        copy.value = this.value;
        copy.previousValue = this.previousValue;
        copy.firstCreatedProperty = this.firstCreatedProperty != null ? Arrays.copyOf(this.firstCreatedProperty, this.firstCreatedProperty.length) : null;
        copy.rebuildCaches = this.rebuildCaches != null ? this.rebuildCaches.clone() : null;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof JsonUpdateCommand)) {
            return false;
        }
        JsonUpdateCommand other = (JsonUpdateCommand)obj;
        return Objects.equals((Object)this.type, (Object)other.type) && Arrays.equals(this.path, other.path) && Objects.equals(this.value, other.value) && Objects.equals(this.previousValue, other.previousValue) && Arrays.equals(this.firstCreatedProperty, other.firstCreatedProperty) && Objects.equals(this.rebuildCaches, other.rebuildCaches);
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + Objects.hashCode((Object)this.type);
        result = 31 * result + Arrays.hashCode(this.path);
        result = 31 * result + Objects.hashCode(this.value);
        result = 31 * result + Objects.hashCode(this.previousValue);
        result = 31 * result + Arrays.hashCode(this.firstCreatedProperty);
        result = 31 * result + Objects.hashCode(this.rebuildCaches);
        return result;
    }
}

