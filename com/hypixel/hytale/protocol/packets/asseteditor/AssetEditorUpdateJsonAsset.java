/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.asseteditor;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetPath;
import com.hypixel.hytale.protocol.packets.asseteditor.JsonUpdateCommand;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AssetEditorUpdateJsonAsset
implements Packet {
    public static final int PACKET_ID = 323;
    public static final boolean IS_COMPRESSED = true;
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 9;
    public static final int VARIABLE_FIELD_COUNT = 3;
    public static final int VARIABLE_BLOCK_START = 21;
    public static final int MAX_SIZE = 0x64000000;
    public int token;
    @Nullable
    public String assetType;
    @Nullable
    public AssetPath path;
    public int assetIndex = Integer.MIN_VALUE;
    @Nullable
    public JsonUpdateCommand[] commands;

    @Override
    public int getId() {
        return 323;
    }

    public AssetEditorUpdateJsonAsset() {
    }

    public AssetEditorUpdateJsonAsset(int token, @Nullable String assetType, @Nullable AssetPath path, int assetIndex, @Nullable JsonUpdateCommand[] commands) {
        this.token = token;
        this.assetType = assetType;
        this.path = path;
        this.assetIndex = assetIndex;
        this.commands = commands;
    }

    public AssetEditorUpdateJsonAsset(@Nonnull AssetEditorUpdateJsonAsset other) {
        this.token = other.token;
        this.assetType = other.assetType;
        this.path = other.path;
        this.assetIndex = other.assetIndex;
        this.commands = other.commands;
    }

    @Nonnull
    public static AssetEditorUpdateJsonAsset deserialize(@Nonnull ByteBuf buf, int offset) {
        AssetEditorUpdateJsonAsset obj = new AssetEditorUpdateJsonAsset();
        byte nullBits = buf.getByte(offset);
        obj.token = buf.getIntLE(offset + 1);
        obj.assetIndex = buf.getIntLE(offset + 5);
        if ((nullBits & 1) != 0) {
            int varPos0 = offset + 21 + buf.getIntLE(offset + 9);
            int assetTypeLen = VarInt.peek(buf, varPos0);
            if (assetTypeLen < 0) {
                throw ProtocolException.negativeLength("AssetType", assetTypeLen);
            }
            if (assetTypeLen > 4096000) {
                throw ProtocolException.stringTooLong("AssetType", assetTypeLen, 4096000);
            }
            obj.assetType = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
        }
        if ((nullBits & 2) != 0) {
            int varPos1 = offset + 21 + buf.getIntLE(offset + 13);
            obj.path = AssetPath.deserialize(buf, varPos1);
        }
        if ((nullBits & 4) != 0) {
            int varPos2 = offset + 21 + buf.getIntLE(offset + 17);
            int commandsCount = VarInt.peek(buf, varPos2);
            if (commandsCount < 0) {
                throw ProtocolException.negativeLength("Commands", commandsCount);
            }
            if (commandsCount > 4096000) {
                throw ProtocolException.arrayTooLong("Commands", commandsCount, 4096000);
            }
            int varIntLen = VarInt.length(buf, varPos2);
            if ((long)(varPos2 + varIntLen) + (long)commandsCount * 7L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("Commands", varPos2 + varIntLen + commandsCount * 7, buf.readableBytes());
            }
            obj.commands = new JsonUpdateCommand[commandsCount];
            int elemPos = varPos2 + varIntLen;
            for (int i = 0; i < commandsCount; ++i) {
                obj.commands[i] = JsonUpdateCommand.deserialize(buf, elemPos);
                elemPos += JsonUpdateCommand.computeBytesConsumed(buf, elemPos);
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        byte nullBits = buf.getByte(offset);
        int maxEnd = 21;
        if ((nullBits & 1) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 9);
            int pos0 = offset + 21 + fieldOffset0;
            int sl = VarInt.peek(buf, pos0);
            if ((pos0 += VarInt.length(buf, pos0) + sl) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 2) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 13);
            int pos1 = offset + 21 + fieldOffset1;
            if ((pos1 += AssetPath.computeBytesConsumed(buf, pos1)) - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        if ((nullBits & 4) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 17);
            int pos2 = offset + 21 + fieldOffset2;
            int arrLen = VarInt.peek(buf, pos2);
            pos2 += VarInt.length(buf, pos2);
            for (int i = 0; i < arrLen; ++i) {
                pos2 += JsonUpdateCommand.computeBytesConsumed(buf, pos2);
            }
            if (pos2 - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        return maxEnd;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        byte nullBits = 0;
        if (this.assetType != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.path != null) {
            nullBits = (byte)(nullBits | 2);
        }
        if (this.commands != null) {
            nullBits = (byte)(nullBits | 4);
        }
        buf.writeByte(nullBits);
        buf.writeIntLE(this.token);
        buf.writeIntLE(this.assetIndex);
        int assetTypeOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int pathOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int commandsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.assetType != null) {
            buf.setIntLE(assetTypeOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.assetType, 4096000);
        } else {
            buf.setIntLE(assetTypeOffsetSlot, -1);
        }
        if (this.path != null) {
            buf.setIntLE(pathOffsetSlot, buf.writerIndex() - varBlockStart);
            this.path.serialize(buf);
        } else {
            buf.setIntLE(pathOffsetSlot, -1);
        }
        if (this.commands != null) {
            buf.setIntLE(commandsOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.commands.length > 4096000) {
                throw ProtocolException.arrayTooLong("Commands", this.commands.length, 4096000);
            }
            VarInt.write(buf, this.commands.length);
            for (JsonUpdateCommand item : this.commands) {
                item.serialize(buf);
            }
        } else {
            buf.setIntLE(commandsOffsetSlot, -1);
        }
    }

    @Override
    public int computeSize() {
        int size = 21;
        if (this.assetType != null) {
            size += PacketIO.stringSize(this.assetType);
        }
        if (this.path != null) {
            size += this.path.computeSize();
        }
        if (this.commands != null) {
            int commandsSize = 0;
            for (JsonUpdateCommand elem : this.commands) {
                commandsSize += elem.computeSize();
            }
            size += VarInt.size(this.commands.length) + commandsSize;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int pos;
        if (buffer.readableBytes() - offset < 21) {
            return ValidationResult.error("Buffer too small: expected at least 21 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 1) != 0) {
            int assetTypeOffset = buffer.getIntLE(offset + 9);
            if (assetTypeOffset < 0) {
                return ValidationResult.error("Invalid offset for AssetType");
            }
            pos = offset + 21 + assetTypeOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for AssetType");
            }
            int assetTypeLen = VarInt.peek(buffer, pos);
            if (assetTypeLen < 0) {
                return ValidationResult.error("Invalid string length for AssetType");
            }
            if (assetTypeLen > 4096000) {
                return ValidationResult.error("AssetType exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += assetTypeLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading AssetType");
            }
        }
        if ((nullBits & 2) != 0) {
            int pathOffset = buffer.getIntLE(offset + 13);
            if (pathOffset < 0) {
                return ValidationResult.error("Invalid offset for Path");
            }
            pos = offset + 21 + pathOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Path");
            }
            ValidationResult pathResult = AssetPath.validateStructure(buffer, pos);
            if (!pathResult.isValid()) {
                return ValidationResult.error("Invalid Path: " + pathResult.error());
            }
            pos += AssetPath.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits & 4) != 0) {
            int commandsOffset = buffer.getIntLE(offset + 17);
            if (commandsOffset < 0) {
                return ValidationResult.error("Invalid offset for Commands");
            }
            pos = offset + 21 + commandsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Commands");
            }
            int commandsCount = VarInt.peek(buffer, pos);
            if (commandsCount < 0) {
                return ValidationResult.error("Invalid array count for Commands");
            }
            if (commandsCount > 4096000) {
                return ValidationResult.error("Commands exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (int i = 0; i < commandsCount; ++i) {
                ValidationResult structResult = JsonUpdateCommand.validateStructure(buffer, pos);
                if (!structResult.isValid()) {
                    return ValidationResult.error("Invalid JsonUpdateCommand in Commands[" + i + "]: " + structResult.error());
                }
                pos += JsonUpdateCommand.computeBytesConsumed(buffer, pos);
            }
        }
        return ValidationResult.OK;
    }

    public AssetEditorUpdateJsonAsset clone() {
        AssetEditorUpdateJsonAsset copy = new AssetEditorUpdateJsonAsset();
        copy.token = this.token;
        copy.assetType = this.assetType;
        copy.path = this.path != null ? this.path.clone() : null;
        copy.assetIndex = this.assetIndex;
        copy.commands = this.commands != null ? (JsonUpdateCommand[])Arrays.stream(this.commands).map(e -> e.clone()).toArray(JsonUpdateCommand[]::new) : null;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AssetEditorUpdateJsonAsset)) {
            return false;
        }
        AssetEditorUpdateJsonAsset other = (AssetEditorUpdateJsonAsset)obj;
        return this.token == other.token && Objects.equals(this.assetType, other.assetType) && Objects.equals(this.path, other.path) && this.assetIndex == other.assetIndex && Arrays.equals(this.commands, other.commands);
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + Integer.hashCode(this.token);
        result = 31 * result + Objects.hashCode(this.assetType);
        result = 31 * result + Objects.hashCode(this.path);
        result = 31 * result + Integer.hashCode(this.assetIndex);
        result = 31 * result + Arrays.hashCode(this.commands);
        return result;
    }
}

