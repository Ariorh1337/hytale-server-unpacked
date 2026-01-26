/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.asseteditor;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorEditorType;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AssetEditorAssetType {
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 3;
    public static final int VARIABLE_FIELD_COUNT = 4;
    public static final int VARIABLE_BLOCK_START = 19;
    public static final int MAX_SIZE = 65536039;
    @Nullable
    public String id;
    @Nullable
    public String icon;
    public boolean isColoredIcon;
    @Nullable
    public String path;
    @Nullable
    public String fileExtension;
    @Nonnull
    public AssetEditorEditorType editorType = AssetEditorEditorType.None;

    public AssetEditorAssetType() {
    }

    public AssetEditorAssetType(@Nullable String id, @Nullable String icon, boolean isColoredIcon, @Nullable String path, @Nullable String fileExtension, @Nonnull AssetEditorEditorType editorType) {
        this.id = id;
        this.icon = icon;
        this.isColoredIcon = isColoredIcon;
        this.path = path;
        this.fileExtension = fileExtension;
        this.editorType = editorType;
    }

    public AssetEditorAssetType(@Nonnull AssetEditorAssetType other) {
        this.id = other.id;
        this.icon = other.icon;
        this.isColoredIcon = other.isColoredIcon;
        this.path = other.path;
        this.fileExtension = other.fileExtension;
        this.editorType = other.editorType;
    }

    @Nonnull
    public static AssetEditorAssetType deserialize(@Nonnull ByteBuf buf, int offset) {
        AssetEditorAssetType obj = new AssetEditorAssetType();
        byte nullBits = buf.getByte(offset);
        obj.isColoredIcon = buf.getByte(offset + 1) != 0;
        obj.editorType = AssetEditorEditorType.fromValue(buf.getByte(offset + 2));
        if ((nullBits & 1) != 0) {
            int varPos0 = offset + 19 + buf.getIntLE(offset + 3);
            int idLen = VarInt.peek(buf, varPos0);
            if (idLen < 0) {
                throw ProtocolException.negativeLength("Id", idLen);
            }
            if (idLen > 4096000) {
                throw ProtocolException.stringTooLong("Id", idLen, 4096000);
            }
            obj.id = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
        }
        if ((nullBits & 2) != 0) {
            int varPos1 = offset + 19 + buf.getIntLE(offset + 7);
            int iconLen = VarInt.peek(buf, varPos1);
            if (iconLen < 0) {
                throw ProtocolException.negativeLength("Icon", iconLen);
            }
            if (iconLen > 4096000) {
                throw ProtocolException.stringTooLong("Icon", iconLen, 4096000);
            }
            obj.icon = PacketIO.readVarString(buf, varPos1, PacketIO.UTF8);
        }
        if ((nullBits & 4) != 0) {
            int varPos2 = offset + 19 + buf.getIntLE(offset + 11);
            int pathLen = VarInt.peek(buf, varPos2);
            if (pathLen < 0) {
                throw ProtocolException.negativeLength("Path", pathLen);
            }
            if (pathLen > 4096000) {
                throw ProtocolException.stringTooLong("Path", pathLen, 4096000);
            }
            obj.path = PacketIO.readVarString(buf, varPos2, PacketIO.UTF8);
        }
        if ((nullBits & 8) != 0) {
            int varPos3 = offset + 19 + buf.getIntLE(offset + 15);
            int fileExtensionLen = VarInt.peek(buf, varPos3);
            if (fileExtensionLen < 0) {
                throw ProtocolException.negativeLength("FileExtension", fileExtensionLen);
            }
            if (fileExtensionLen > 4096000) {
                throw ProtocolException.stringTooLong("FileExtension", fileExtensionLen, 4096000);
            }
            obj.fileExtension = PacketIO.readVarString(buf, varPos3, PacketIO.UTF8);
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int sl;
        byte nullBits = buf.getByte(offset);
        int maxEnd = 19;
        if ((nullBits & 1) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 3);
            int pos0 = offset + 19 + fieldOffset0;
            sl = VarInt.peek(buf, pos0);
            if ((pos0 += VarInt.length(buf, pos0) + sl) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 2) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 7);
            int pos1 = offset + 19 + fieldOffset1;
            sl = VarInt.peek(buf, pos1);
            if ((pos1 += VarInt.length(buf, pos1) + sl) - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        if ((nullBits & 4) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 11);
            int pos2 = offset + 19 + fieldOffset2;
            sl = VarInt.peek(buf, pos2);
            if ((pos2 += VarInt.length(buf, pos2) + sl) - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        if ((nullBits & 8) != 0) {
            int fieldOffset3 = buf.getIntLE(offset + 15);
            int pos3 = offset + 19 + fieldOffset3;
            sl = VarInt.peek(buf, pos3);
            if ((pos3 += VarInt.length(buf, pos3) + sl) - offset > maxEnd) {
                maxEnd = pos3 - offset;
            }
        }
        return maxEnd;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        byte nullBits = 0;
        if (this.id != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.icon != null) {
            nullBits = (byte)(nullBits | 2);
        }
        if (this.path != null) {
            nullBits = (byte)(nullBits | 4);
        }
        if (this.fileExtension != null) {
            nullBits = (byte)(nullBits | 8);
        }
        buf.writeByte(nullBits);
        buf.writeByte(this.isColoredIcon ? 1 : 0);
        buf.writeByte(this.editorType.getValue());
        int idOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int iconOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int pathOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int fileExtensionOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.id != null) {
            buf.setIntLE(idOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.id, 4096000);
        } else {
            buf.setIntLE(idOffsetSlot, -1);
        }
        if (this.icon != null) {
            buf.setIntLE(iconOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.icon, 4096000);
        } else {
            buf.setIntLE(iconOffsetSlot, -1);
        }
        if (this.path != null) {
            buf.setIntLE(pathOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.path, 4096000);
        } else {
            buf.setIntLE(pathOffsetSlot, -1);
        }
        if (this.fileExtension != null) {
            buf.setIntLE(fileExtensionOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.fileExtension, 4096000);
        } else {
            buf.setIntLE(fileExtensionOffsetSlot, -1);
        }
    }

    public int computeSize() {
        int size = 19;
        if (this.id != null) {
            size += PacketIO.stringSize(this.id);
        }
        if (this.icon != null) {
            size += PacketIO.stringSize(this.icon);
        }
        if (this.path != null) {
            size += PacketIO.stringSize(this.path);
        }
        if (this.fileExtension != null) {
            size += PacketIO.stringSize(this.fileExtension);
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int pos;
        if (buffer.readableBytes() - offset < 19) {
            return ValidationResult.error("Buffer too small: expected at least 19 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 1) != 0) {
            int idOffset = buffer.getIntLE(offset + 3);
            if (idOffset < 0) {
                return ValidationResult.error("Invalid offset for Id");
            }
            pos = offset + 19 + idOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Id");
            }
            int idLen = VarInt.peek(buffer, pos);
            if (idLen < 0) {
                return ValidationResult.error("Invalid string length for Id");
            }
            if (idLen > 4096000) {
                return ValidationResult.error("Id exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += idLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading Id");
            }
        }
        if ((nullBits & 2) != 0) {
            int iconOffset = buffer.getIntLE(offset + 7);
            if (iconOffset < 0) {
                return ValidationResult.error("Invalid offset for Icon");
            }
            pos = offset + 19 + iconOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Icon");
            }
            int iconLen = VarInt.peek(buffer, pos);
            if (iconLen < 0) {
                return ValidationResult.error("Invalid string length for Icon");
            }
            if (iconLen > 4096000) {
                return ValidationResult.error("Icon exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += iconLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading Icon");
            }
        }
        if ((nullBits & 4) != 0) {
            int pathOffset = buffer.getIntLE(offset + 11);
            if (pathOffset < 0) {
                return ValidationResult.error("Invalid offset for Path");
            }
            pos = offset + 19 + pathOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Path");
            }
            int pathLen = VarInt.peek(buffer, pos);
            if (pathLen < 0) {
                return ValidationResult.error("Invalid string length for Path");
            }
            if (pathLen > 4096000) {
                return ValidationResult.error("Path exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += pathLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading Path");
            }
        }
        if ((nullBits & 8) != 0) {
            int fileExtensionOffset = buffer.getIntLE(offset + 15);
            if (fileExtensionOffset < 0) {
                return ValidationResult.error("Invalid offset for FileExtension");
            }
            pos = offset + 19 + fileExtensionOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for FileExtension");
            }
            int fileExtensionLen = VarInt.peek(buffer, pos);
            if (fileExtensionLen < 0) {
                return ValidationResult.error("Invalid string length for FileExtension");
            }
            if (fileExtensionLen > 4096000) {
                return ValidationResult.error("FileExtension exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += fileExtensionLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading FileExtension");
            }
        }
        return ValidationResult.OK;
    }

    public AssetEditorAssetType clone() {
        AssetEditorAssetType copy = new AssetEditorAssetType();
        copy.id = this.id;
        copy.icon = this.icon;
        copy.isColoredIcon = this.isColoredIcon;
        copy.path = this.path;
        copy.fileExtension = this.fileExtension;
        copy.editorType = this.editorType;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AssetEditorAssetType)) {
            return false;
        }
        AssetEditorAssetType other = (AssetEditorAssetType)obj;
        return Objects.equals(this.id, other.id) && Objects.equals(this.icon, other.icon) && this.isColoredIcon == other.isColoredIcon && Objects.equals(this.path, other.path) && Objects.equals(this.fileExtension, other.fileExtension) && Objects.equals((Object)this.editorType, (Object)other.editorType);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.id, this.icon, this.isColoredIcon, this.path, this.fileExtension, this.editorType});
    }
}

