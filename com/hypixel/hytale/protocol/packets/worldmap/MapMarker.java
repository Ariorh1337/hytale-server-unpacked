/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.worldmap;

import com.hypixel.hytale.protocol.FormattedMessage;
import com.hypixel.hytale.protocol.Transform;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import com.hypixel.hytale.protocol.packets.worldmap.ContextMenuItem;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarkerComponent;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MapMarker {
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 38;
    public static final int VARIABLE_FIELD_COUNT = 6;
    public static final int VARIABLE_BLOCK_START = 62;
    public static final int MAX_SIZE = 0x64000000;
    @Nonnull
    public String id = "";
    @Nullable
    public FormattedMessage name;
    @Nullable
    public String customName;
    @Nonnull
    public String markerImage = "";
    @Nonnull
    public Transform transform = new Transform();
    @Nullable
    public ContextMenuItem[] contextMenuItems;
    @Nullable
    public MapMarkerComponent[] components;

    public MapMarker() {
    }

    public MapMarker(@Nonnull String id, @Nullable FormattedMessage name, @Nullable String customName, @Nonnull String markerImage, @Nonnull Transform transform, @Nullable ContextMenuItem[] contextMenuItems, @Nullable MapMarkerComponent[] components) {
        this.id = id;
        this.name = name;
        this.customName = customName;
        this.markerImage = markerImage;
        this.transform = transform;
        this.contextMenuItems = contextMenuItems;
        this.components = components;
    }

    public MapMarker(@Nonnull MapMarker other) {
        this.id = other.id;
        this.name = other.name;
        this.customName = other.customName;
        this.markerImage = other.markerImage;
        this.transform = other.transform;
        this.contextMenuItems = other.contextMenuItems;
        this.components = other.components;
    }

    @Nonnull
    public static MapMarker deserialize(@Nonnull ByteBuf buf, int offset) {
        int i;
        int elemPos;
        int varIntLen;
        int varPos3;
        int markerImageLen;
        MapMarker obj = new MapMarker();
        byte nullBits = buf.getByte(offset);
        obj.transform = Transform.deserialize(buf, offset + 1);
        int varPos0 = offset + 62 + buf.getIntLE(offset + 38);
        int idLen = VarInt.peek(buf, varPos0);
        if (idLen < 0) {
            throw ProtocolException.negativeLength("Id", idLen);
        }
        if (idLen > 4096000) {
            throw ProtocolException.stringTooLong("Id", idLen, 4096000);
        }
        obj.id = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
        if ((nullBits & 1) != 0) {
            int varPos1 = offset + 62 + buf.getIntLE(offset + 42);
            obj.name = FormattedMessage.deserialize(buf, varPos1);
        }
        if ((nullBits & 2) != 0) {
            int varPos2 = offset + 62 + buf.getIntLE(offset + 46);
            int customNameLen = VarInt.peek(buf, varPos2);
            if (customNameLen < 0) {
                throw ProtocolException.negativeLength("CustomName", customNameLen);
            }
            if (customNameLen > 4096000) {
                throw ProtocolException.stringTooLong("CustomName", customNameLen, 4096000);
            }
            obj.customName = PacketIO.readVarString(buf, varPos2, PacketIO.UTF8);
        }
        if ((markerImageLen = VarInt.peek(buf, varPos3 = offset + 62 + buf.getIntLE(offset + 50))) < 0) {
            throw ProtocolException.negativeLength("MarkerImage", markerImageLen);
        }
        if (markerImageLen > 4096000) {
            throw ProtocolException.stringTooLong("MarkerImage", markerImageLen, 4096000);
        }
        obj.markerImage = PacketIO.readVarString(buf, varPos3, PacketIO.UTF8);
        if ((nullBits & 4) != 0) {
            int varPos4 = offset + 62 + buf.getIntLE(offset + 54);
            int contextMenuItemsCount = VarInt.peek(buf, varPos4);
            if (contextMenuItemsCount < 0) {
                throw ProtocolException.negativeLength("ContextMenuItems", contextMenuItemsCount);
            }
            if (contextMenuItemsCount > 4096000) {
                throw ProtocolException.arrayTooLong("ContextMenuItems", contextMenuItemsCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos4);
            if ((long)(varPos4 + varIntLen) + (long)contextMenuItemsCount * 0L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("ContextMenuItems", varPos4 + varIntLen + contextMenuItemsCount * 0, buf.readableBytes());
            }
            obj.contextMenuItems = new ContextMenuItem[contextMenuItemsCount];
            elemPos = varPos4 + varIntLen;
            for (i = 0; i < contextMenuItemsCount; ++i) {
                obj.contextMenuItems[i] = ContextMenuItem.deserialize(buf, elemPos);
                elemPos += ContextMenuItem.computeBytesConsumed(buf, elemPos);
            }
        }
        if ((nullBits & 8) != 0) {
            int varPos5 = offset + 62 + buf.getIntLE(offset + 58);
            int componentsCount = VarInt.peek(buf, varPos5);
            if (componentsCount < 0) {
                throw ProtocolException.negativeLength("Components", componentsCount);
            }
            if (componentsCount > 4096000) {
                throw ProtocolException.arrayTooLong("Components", componentsCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos5);
            if ((long)(varPos5 + varIntLen) + (long)componentsCount * 1L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("Components", varPos5 + varIntLen + componentsCount * 1, buf.readableBytes());
            }
            obj.components = new MapMarkerComponent[componentsCount];
            elemPos = varPos5 + varIntLen;
            for (i = 0; i < componentsCount; ++i) {
                obj.components[i] = MapMarkerComponent.deserialize(buf, elemPos);
                elemPos += MapMarkerComponent.computeBytesConsumed(buf, elemPos);
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int i;
        int arrLen;
        byte nullBits = buf.getByte(offset);
        int maxEnd = 62;
        int fieldOffset0 = buf.getIntLE(offset + 38);
        int pos0 = offset + 62 + fieldOffset0;
        int sl = VarInt.peek(buf, pos0);
        if ((pos0 += VarInt.length(buf, pos0) + sl) - offset > maxEnd) {
            maxEnd = pos0 - offset;
        }
        if ((nullBits & 1) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 42);
            int pos1 = offset + 62 + fieldOffset1;
            if ((pos1 += FormattedMessage.computeBytesConsumed(buf, pos1)) - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        if ((nullBits & 2) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 46);
            int pos2 = offset + 62 + fieldOffset2;
            sl = VarInt.peek(buf, pos2);
            if ((pos2 += VarInt.length(buf, pos2) + sl) - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        int fieldOffset3 = buf.getIntLE(offset + 50);
        int pos3 = offset + 62 + fieldOffset3;
        sl = VarInt.peek(buf, pos3);
        if ((pos3 += VarInt.length(buf, pos3) + sl) - offset > maxEnd) {
            maxEnd = pos3 - offset;
        }
        if ((nullBits & 4) != 0) {
            int fieldOffset4 = buf.getIntLE(offset + 54);
            int pos4 = offset + 62 + fieldOffset4;
            arrLen = VarInt.peek(buf, pos4);
            pos4 += VarInt.length(buf, pos4);
            for (i = 0; i < arrLen; ++i) {
                pos4 += ContextMenuItem.computeBytesConsumed(buf, pos4);
            }
            if (pos4 - offset > maxEnd) {
                maxEnd = pos4 - offset;
            }
        }
        if ((nullBits & 8) != 0) {
            int fieldOffset5 = buf.getIntLE(offset + 58);
            int pos5 = offset + 62 + fieldOffset5;
            arrLen = VarInt.peek(buf, pos5);
            pos5 += VarInt.length(buf, pos5);
            for (i = 0; i < arrLen; ++i) {
                pos5 += MapMarkerComponent.computeBytesConsumed(buf, pos5);
            }
            if (pos5 - offset > maxEnd) {
                maxEnd = pos5 - offset;
            }
        }
        return maxEnd;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        byte nullBits = 0;
        if (this.name != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.customName != null) {
            nullBits = (byte)(nullBits | 2);
        }
        if (this.contextMenuItems != null) {
            nullBits = (byte)(nullBits | 4);
        }
        if (this.components != null) {
            nullBits = (byte)(nullBits | 8);
        }
        buf.writeByte(nullBits);
        this.transform.serialize(buf);
        int idOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int nameOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int customNameOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int markerImageOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int contextMenuItemsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int componentsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        buf.setIntLE(idOffsetSlot, buf.writerIndex() - varBlockStart);
        PacketIO.writeVarString(buf, this.id, 4096000);
        if (this.name != null) {
            buf.setIntLE(nameOffsetSlot, buf.writerIndex() - varBlockStart);
            this.name.serialize(buf);
        } else {
            buf.setIntLE(nameOffsetSlot, -1);
        }
        if (this.customName != null) {
            buf.setIntLE(customNameOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.customName, 4096000);
        } else {
            buf.setIntLE(customNameOffsetSlot, -1);
        }
        buf.setIntLE(markerImageOffsetSlot, buf.writerIndex() - varBlockStart);
        PacketIO.writeVarString(buf, this.markerImage, 4096000);
        if (this.contextMenuItems != null) {
            buf.setIntLE(contextMenuItemsOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.contextMenuItems.length > 4096000) {
                throw ProtocolException.arrayTooLong("ContextMenuItems", this.contextMenuItems.length, 4096000);
            }
            VarInt.write(buf, this.contextMenuItems.length);
            for (ContextMenuItem contextMenuItem : this.contextMenuItems) {
                contextMenuItem.serialize(buf);
            }
        } else {
            buf.setIntLE(contextMenuItemsOffsetSlot, -1);
        }
        if (this.components != null) {
            buf.setIntLE(componentsOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.components.length > 4096000) {
                throw ProtocolException.arrayTooLong("Components", this.components.length, 4096000);
            }
            VarInt.write(buf, this.components.length);
            for (MapMarkerComponent mapMarkerComponent : this.components) {
                mapMarkerComponent.serializeWithTypeId(buf);
            }
        } else {
            buf.setIntLE(componentsOffsetSlot, -1);
        }
    }

    public int computeSize() {
        int size = 62;
        size += PacketIO.stringSize(this.id);
        if (this.name != null) {
            size += this.name.computeSize();
        }
        if (this.customName != null) {
            size += PacketIO.stringSize(this.customName);
        }
        size += PacketIO.stringSize(this.markerImage);
        if (this.contextMenuItems != null) {
            int contextMenuItemsSize = 0;
            for (ContextMenuItem contextMenuItem : this.contextMenuItems) {
                contextMenuItemsSize += contextMenuItem.computeSize();
            }
            size += VarInt.size(this.contextMenuItems.length) + contextMenuItemsSize;
        }
        if (this.components != null) {
            int componentsSize = 0;
            for (MapMarkerComponent mapMarkerComponent : this.components) {
                componentsSize += mapMarkerComponent.computeSizeWithTypeId();
            }
            size += VarInt.size(this.components.length) + componentsSize;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        ValidationResult structResult;
        int i;
        int markerImageOffset;
        if (buffer.readableBytes() - offset < 62) {
            return ValidationResult.error("Buffer too small: expected at least 62 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        int idOffset = buffer.getIntLE(offset + 38);
        if (idOffset < 0) {
            return ValidationResult.error("Invalid offset for Id");
        }
        int pos = offset + 62 + idOffset;
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
        if ((nullBits & 1) != 0) {
            int nameOffset = buffer.getIntLE(offset + 42);
            if (nameOffset < 0) {
                return ValidationResult.error("Invalid offset for Name");
            }
            pos = offset + 62 + nameOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Name");
            }
            ValidationResult nameResult = FormattedMessage.validateStructure(buffer, pos);
            if (!nameResult.isValid()) {
                return ValidationResult.error("Invalid Name: " + nameResult.error());
            }
            pos += FormattedMessage.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits & 2) != 0) {
            int customNameOffset = buffer.getIntLE(offset + 46);
            if (customNameOffset < 0) {
                return ValidationResult.error("Invalid offset for CustomName");
            }
            pos = offset + 62 + customNameOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for CustomName");
            }
            int customNameLen = VarInt.peek(buffer, pos);
            if (customNameLen < 0) {
                return ValidationResult.error("Invalid string length for CustomName");
            }
            if (customNameLen > 4096000) {
                return ValidationResult.error("CustomName exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += customNameLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading CustomName");
            }
        }
        if ((markerImageOffset = buffer.getIntLE(offset + 50)) < 0) {
            return ValidationResult.error("Invalid offset for MarkerImage");
        }
        pos = offset + 62 + markerImageOffset;
        if (pos >= buffer.writerIndex()) {
            return ValidationResult.error("Offset out of bounds for MarkerImage");
        }
        int markerImageLen = VarInt.peek(buffer, pos);
        if (markerImageLen < 0) {
            return ValidationResult.error("Invalid string length for MarkerImage");
        }
        if (markerImageLen > 4096000) {
            return ValidationResult.error("MarkerImage exceeds max length 4096000");
        }
        pos += VarInt.length(buffer, pos);
        if ((pos += markerImageLen) > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading MarkerImage");
        }
        if ((nullBits & 4) != 0) {
            int contextMenuItemsOffset = buffer.getIntLE(offset + 54);
            if (contextMenuItemsOffset < 0) {
                return ValidationResult.error("Invalid offset for ContextMenuItems");
            }
            pos = offset + 62 + contextMenuItemsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for ContextMenuItems");
            }
            int contextMenuItemsCount = VarInt.peek(buffer, pos);
            if (contextMenuItemsCount < 0) {
                return ValidationResult.error("Invalid array count for ContextMenuItems");
            }
            if (contextMenuItemsCount > 4096000) {
                return ValidationResult.error("ContextMenuItems exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < contextMenuItemsCount; ++i) {
                structResult = ContextMenuItem.validateStructure(buffer, pos);
                if (!structResult.isValid()) {
                    return ValidationResult.error("Invalid ContextMenuItem in ContextMenuItems[" + i + "]: " + structResult.error());
                }
                pos += ContextMenuItem.computeBytesConsumed(buffer, pos);
            }
        }
        if ((nullBits & 8) != 0) {
            int componentsOffset = buffer.getIntLE(offset + 58);
            if (componentsOffset < 0) {
                return ValidationResult.error("Invalid offset for Components");
            }
            pos = offset + 62 + componentsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Components");
            }
            int componentsCount = VarInt.peek(buffer, pos);
            if (componentsCount < 0) {
                return ValidationResult.error("Invalid array count for Components");
            }
            if (componentsCount > 4096000) {
                return ValidationResult.error("Components exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < componentsCount; ++i) {
                structResult = MapMarkerComponent.validateStructure(buffer, pos);
                if (!structResult.isValid()) {
                    return ValidationResult.error("Invalid MapMarkerComponent in Components[" + i + "]: " + structResult.error());
                }
                pos += MapMarkerComponent.computeBytesConsumed(buffer, pos);
            }
        }
        return ValidationResult.OK;
    }

    public MapMarker clone() {
        MapMarker copy = new MapMarker();
        copy.id = this.id;
        copy.name = this.name != null ? this.name.clone() : null;
        copy.customName = this.customName;
        copy.markerImage = this.markerImage;
        copy.transform = this.transform.clone();
        copy.contextMenuItems = this.contextMenuItems != null ? (ContextMenuItem[])Arrays.stream(this.contextMenuItems).map(e -> e.clone()).toArray(ContextMenuItem[]::new) : null;
        copy.components = this.components != null ? Arrays.copyOf(this.components, this.components.length) : null;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof MapMarker)) {
            return false;
        }
        MapMarker other = (MapMarker)obj;
        return Objects.equals(this.id, other.id) && Objects.equals(this.name, other.name) && Objects.equals(this.customName, other.customName) && Objects.equals(this.markerImage, other.markerImage) && Objects.equals(this.transform, other.transform) && Arrays.equals(this.contextMenuItems, other.contextMenuItems) && Arrays.equals(this.components, other.components);
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + Objects.hashCode(this.id);
        result = 31 * result + Objects.hashCode(this.name);
        result = 31 * result + Objects.hashCode(this.customName);
        result = 31 * result + Objects.hashCode(this.markerImage);
        result = 31 * result + Objects.hashCode(this.transform);
        result = 31 * result + Arrays.hashCode(this.contextMenuItems);
        result = 31 * result + Arrays.hashCode(this.components);
        return result;
    }
}

