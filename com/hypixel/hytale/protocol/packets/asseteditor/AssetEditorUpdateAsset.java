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
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AssetEditorUpdateAsset
implements Packet {
    public static final int PACKET_ID = 324;
    public static final boolean IS_COMPRESSED = false;
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 9;
    public static final int VARIABLE_FIELD_COUNT = 3;
    public static final int VARIABLE_BLOCK_START = 21;
    public static final int MAX_SIZE = 53248050;
    public int token;
    @Nullable
    public String assetType;
    @Nullable
    public AssetPath path;
    public int assetIndex = Integer.MIN_VALUE;
    @Nullable
    public byte[] data;

    @Override
    public int getId() {
        return 324;
    }

    public AssetEditorUpdateAsset() {
    }

    public AssetEditorUpdateAsset(int token, @Nullable String assetType, @Nullable AssetPath path, int assetIndex, @Nullable byte[] data) {
        this.token = token;
        this.assetType = assetType;
        this.path = path;
        this.assetIndex = assetIndex;
        this.data = data;
    }

    public AssetEditorUpdateAsset(@Nonnull AssetEditorUpdateAsset other) {
        this.token = other.token;
        this.assetType = other.assetType;
        this.path = other.path;
        this.assetIndex = other.assetIndex;
        this.data = other.data;
    }

    @Nonnull
    public static AssetEditorUpdateAsset deserialize(@Nonnull ByteBuf buf, int offset) {
        AssetEditorUpdateAsset obj = new AssetEditorUpdateAsset();
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
            int dataCount = VarInt.peek(buf, varPos2);
            if (dataCount < 0) {
                throw ProtocolException.negativeLength("Data", dataCount);
            }
            if (dataCount > 4096000) {
                throw ProtocolException.arrayTooLong("Data", dataCount, 4096000);
            }
            int varIntLen = VarInt.length(buf, varPos2);
            if ((long)(varPos2 + varIntLen) + (long)dataCount * 1L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("Data", varPos2 + varIntLen + dataCount * 1, buf.readableBytes());
            }
            obj.data = new byte[dataCount];
            for (int i = 0; i < dataCount; ++i) {
                obj.data[i] = buf.getByte(varPos2 + varIntLen + i * 1);
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
            if ((pos2 += VarInt.length(buf, pos2) + arrLen * 1) - offset > maxEnd) {
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
        if (this.data != null) {
            nullBits = (byte)(nullBits | 4);
        }
        buf.writeByte(nullBits);
        buf.writeIntLE(this.token);
        buf.writeIntLE(this.assetIndex);
        int assetTypeOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int pathOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int dataOffsetSlot = buf.writerIndex();
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
        if (this.data != null) {
            buf.setIntLE(dataOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.data.length > 4096000) {
                throw ProtocolException.arrayTooLong("Data", this.data.length, 4096000);
            }
            VarInt.write(buf, this.data.length);
            for (byte item : this.data) {
                buf.writeByte(item);
            }
        } else {
            buf.setIntLE(dataOffsetSlot, -1);
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
        if (this.data != null) {
            size += VarInt.size(this.data.length) + this.data.length * 1;
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
            int dataOffset = buffer.getIntLE(offset + 17);
            if (dataOffset < 0) {
                return ValidationResult.error("Invalid offset for Data");
            }
            pos = offset + 21 + dataOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Data");
            }
            int dataCount = VarInt.peek(buffer, pos);
            if (dataCount < 0) {
                return ValidationResult.error("Invalid array count for Data");
            }
            if (dataCount > 4096000) {
                return ValidationResult.error("Data exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += dataCount * 1) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading Data");
            }
        }
        return ValidationResult.OK;
    }

    public AssetEditorUpdateAsset clone() {
        AssetEditorUpdateAsset copy = new AssetEditorUpdateAsset();
        copy.token = this.token;
        copy.assetType = this.assetType;
        copy.path = this.path != null ? this.path.clone() : null;
        copy.assetIndex = this.assetIndex;
        copy.data = this.data != null ? Arrays.copyOf(this.data, this.data.length) : null;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AssetEditorUpdateAsset)) {
            return false;
        }
        AssetEditorUpdateAsset other = (AssetEditorUpdateAsset)obj;
        return this.token == other.token && Objects.equals(this.assetType, other.assetType) && Objects.equals(this.path, other.path) && this.assetIndex == other.assetIndex && Arrays.equals(this.data, other.data);
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + Integer.hashCode(this.token);
        result = 31 * result + Objects.hashCode(this.assetType);
        result = 31 * result + Objects.hashCode(this.path);
        result = 31 * result + Integer.hashCode(this.assetIndex);
        result = 31 * result + Arrays.hashCode(this.data);
        return result;
    }
}

