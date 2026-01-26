/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemQuality {
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 7;
    public static final int VARIABLE_FIELD_COUNT = 7;
    public static final int VARIABLE_BLOCK_START = 35;
    public static final int MAX_SIZE = 114688070;
    @Nullable
    public String id;
    @Nullable
    public String itemTooltipTexture;
    @Nullable
    public String itemTooltipArrowTexture;
    @Nullable
    public String slotTexture;
    @Nullable
    public String blockSlotTexture;
    @Nullable
    public String specialSlotTexture;
    @Nullable
    public Color textColor;
    @Nullable
    public String localizationKey;
    public boolean visibleQualityLabel;
    public boolean renderSpecialSlot;
    public boolean hideFromSearch;

    public ItemQuality() {
    }

    public ItemQuality(@Nullable String id, @Nullable String itemTooltipTexture, @Nullable String itemTooltipArrowTexture, @Nullable String slotTexture, @Nullable String blockSlotTexture, @Nullable String specialSlotTexture, @Nullable Color textColor, @Nullable String localizationKey, boolean visibleQualityLabel, boolean renderSpecialSlot, boolean hideFromSearch) {
        this.id = id;
        this.itemTooltipTexture = itemTooltipTexture;
        this.itemTooltipArrowTexture = itemTooltipArrowTexture;
        this.slotTexture = slotTexture;
        this.blockSlotTexture = blockSlotTexture;
        this.specialSlotTexture = specialSlotTexture;
        this.textColor = textColor;
        this.localizationKey = localizationKey;
        this.visibleQualityLabel = visibleQualityLabel;
        this.renderSpecialSlot = renderSpecialSlot;
        this.hideFromSearch = hideFromSearch;
    }

    public ItemQuality(@Nonnull ItemQuality other) {
        this.id = other.id;
        this.itemTooltipTexture = other.itemTooltipTexture;
        this.itemTooltipArrowTexture = other.itemTooltipArrowTexture;
        this.slotTexture = other.slotTexture;
        this.blockSlotTexture = other.blockSlotTexture;
        this.specialSlotTexture = other.specialSlotTexture;
        this.textColor = other.textColor;
        this.localizationKey = other.localizationKey;
        this.visibleQualityLabel = other.visibleQualityLabel;
        this.renderSpecialSlot = other.renderSpecialSlot;
        this.hideFromSearch = other.hideFromSearch;
    }

    @Nonnull
    public static ItemQuality deserialize(@Nonnull ByteBuf buf, int offset) {
        ItemQuality obj = new ItemQuality();
        byte nullBits = buf.getByte(offset);
        if ((nullBits & 1) != 0) {
            obj.textColor = Color.deserialize(buf, offset + 1);
        }
        obj.visibleQualityLabel = buf.getByte(offset + 4) != 0;
        obj.renderSpecialSlot = buf.getByte(offset + 5) != 0;
        boolean bl = obj.hideFromSearch = buf.getByte(offset + 6) != 0;
        if ((nullBits & 2) != 0) {
            int varPos0 = offset + 35 + buf.getIntLE(offset + 7);
            int idLen = VarInt.peek(buf, varPos0);
            if (idLen < 0) {
                throw ProtocolException.negativeLength("Id", idLen);
            }
            if (idLen > 4096000) {
                throw ProtocolException.stringTooLong("Id", idLen, 4096000);
            }
            obj.id = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
        }
        if ((nullBits & 4) != 0) {
            int varPos1 = offset + 35 + buf.getIntLE(offset + 11);
            int itemTooltipTextureLen = VarInt.peek(buf, varPos1);
            if (itemTooltipTextureLen < 0) {
                throw ProtocolException.negativeLength("ItemTooltipTexture", itemTooltipTextureLen);
            }
            if (itemTooltipTextureLen > 4096000) {
                throw ProtocolException.stringTooLong("ItemTooltipTexture", itemTooltipTextureLen, 4096000);
            }
            obj.itemTooltipTexture = PacketIO.readVarString(buf, varPos1, PacketIO.UTF8);
        }
        if ((nullBits & 8) != 0) {
            int varPos2 = offset + 35 + buf.getIntLE(offset + 15);
            int itemTooltipArrowTextureLen = VarInt.peek(buf, varPos2);
            if (itemTooltipArrowTextureLen < 0) {
                throw ProtocolException.negativeLength("ItemTooltipArrowTexture", itemTooltipArrowTextureLen);
            }
            if (itemTooltipArrowTextureLen > 4096000) {
                throw ProtocolException.stringTooLong("ItemTooltipArrowTexture", itemTooltipArrowTextureLen, 4096000);
            }
            obj.itemTooltipArrowTexture = PacketIO.readVarString(buf, varPos2, PacketIO.UTF8);
        }
        if ((nullBits & 0x10) != 0) {
            int varPos3 = offset + 35 + buf.getIntLE(offset + 19);
            int slotTextureLen = VarInt.peek(buf, varPos3);
            if (slotTextureLen < 0) {
                throw ProtocolException.negativeLength("SlotTexture", slotTextureLen);
            }
            if (slotTextureLen > 4096000) {
                throw ProtocolException.stringTooLong("SlotTexture", slotTextureLen, 4096000);
            }
            obj.slotTexture = PacketIO.readVarString(buf, varPos3, PacketIO.UTF8);
        }
        if ((nullBits & 0x20) != 0) {
            int varPos4 = offset + 35 + buf.getIntLE(offset + 23);
            int blockSlotTextureLen = VarInt.peek(buf, varPos4);
            if (blockSlotTextureLen < 0) {
                throw ProtocolException.negativeLength("BlockSlotTexture", blockSlotTextureLen);
            }
            if (blockSlotTextureLen > 4096000) {
                throw ProtocolException.stringTooLong("BlockSlotTexture", blockSlotTextureLen, 4096000);
            }
            obj.blockSlotTexture = PacketIO.readVarString(buf, varPos4, PacketIO.UTF8);
        }
        if ((nullBits & 0x40) != 0) {
            int varPos5 = offset + 35 + buf.getIntLE(offset + 27);
            int specialSlotTextureLen = VarInt.peek(buf, varPos5);
            if (specialSlotTextureLen < 0) {
                throw ProtocolException.negativeLength("SpecialSlotTexture", specialSlotTextureLen);
            }
            if (specialSlotTextureLen > 4096000) {
                throw ProtocolException.stringTooLong("SpecialSlotTexture", specialSlotTextureLen, 4096000);
            }
            obj.specialSlotTexture = PacketIO.readVarString(buf, varPos5, PacketIO.UTF8);
        }
        if ((nullBits & 0x80) != 0) {
            int varPos6 = offset + 35 + buf.getIntLE(offset + 31);
            int localizationKeyLen = VarInt.peek(buf, varPos6);
            if (localizationKeyLen < 0) {
                throw ProtocolException.negativeLength("LocalizationKey", localizationKeyLen);
            }
            if (localizationKeyLen > 4096000) {
                throw ProtocolException.stringTooLong("LocalizationKey", localizationKeyLen, 4096000);
            }
            obj.localizationKey = PacketIO.readVarString(buf, varPos6, PacketIO.UTF8);
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int sl;
        byte nullBits = buf.getByte(offset);
        int maxEnd = 35;
        if ((nullBits & 2) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 7);
            int pos0 = offset + 35 + fieldOffset0;
            sl = VarInt.peek(buf, pos0);
            if ((pos0 += VarInt.length(buf, pos0) + sl) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 4) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 11);
            int pos1 = offset + 35 + fieldOffset1;
            sl = VarInt.peek(buf, pos1);
            if ((pos1 += VarInt.length(buf, pos1) + sl) - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        if ((nullBits & 8) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 15);
            int pos2 = offset + 35 + fieldOffset2;
            sl = VarInt.peek(buf, pos2);
            if ((pos2 += VarInt.length(buf, pos2) + sl) - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        if ((nullBits & 0x10) != 0) {
            int fieldOffset3 = buf.getIntLE(offset + 19);
            int pos3 = offset + 35 + fieldOffset3;
            sl = VarInt.peek(buf, pos3);
            if ((pos3 += VarInt.length(buf, pos3) + sl) - offset > maxEnd) {
                maxEnd = pos3 - offset;
            }
        }
        if ((nullBits & 0x20) != 0) {
            int fieldOffset4 = buf.getIntLE(offset + 23);
            int pos4 = offset + 35 + fieldOffset4;
            sl = VarInt.peek(buf, pos4);
            if ((pos4 += VarInt.length(buf, pos4) + sl) - offset > maxEnd) {
                maxEnd = pos4 - offset;
            }
        }
        if ((nullBits & 0x40) != 0) {
            int fieldOffset5 = buf.getIntLE(offset + 27);
            int pos5 = offset + 35 + fieldOffset5;
            sl = VarInt.peek(buf, pos5);
            if ((pos5 += VarInt.length(buf, pos5) + sl) - offset > maxEnd) {
                maxEnd = pos5 - offset;
            }
        }
        if ((nullBits & 0x80) != 0) {
            int fieldOffset6 = buf.getIntLE(offset + 31);
            int pos6 = offset + 35 + fieldOffset6;
            sl = VarInt.peek(buf, pos6);
            if ((pos6 += VarInt.length(buf, pos6) + sl) - offset > maxEnd) {
                maxEnd = pos6 - offset;
            }
        }
        return maxEnd;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        byte nullBits = 0;
        if (this.textColor != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.id != null) {
            nullBits = (byte)(nullBits | 2);
        }
        if (this.itemTooltipTexture != null) {
            nullBits = (byte)(nullBits | 4);
        }
        if (this.itemTooltipArrowTexture != null) {
            nullBits = (byte)(nullBits | 8);
        }
        if (this.slotTexture != null) {
            nullBits = (byte)(nullBits | 0x10);
        }
        if (this.blockSlotTexture != null) {
            nullBits = (byte)(nullBits | 0x20);
        }
        if (this.specialSlotTexture != null) {
            nullBits = (byte)(nullBits | 0x40);
        }
        if (this.localizationKey != null) {
            nullBits = (byte)(nullBits | 0x80);
        }
        buf.writeByte(nullBits);
        if (this.textColor != null) {
            this.textColor.serialize(buf);
        } else {
            buf.writeZero(3);
        }
        buf.writeByte(this.visibleQualityLabel ? 1 : 0);
        buf.writeByte(this.renderSpecialSlot ? 1 : 0);
        buf.writeByte(this.hideFromSearch ? 1 : 0);
        int idOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int itemTooltipTextureOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int itemTooltipArrowTextureOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int slotTextureOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int blockSlotTextureOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int specialSlotTextureOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int localizationKeyOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.id != null) {
            buf.setIntLE(idOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.id, 4096000);
        } else {
            buf.setIntLE(idOffsetSlot, -1);
        }
        if (this.itemTooltipTexture != null) {
            buf.setIntLE(itemTooltipTextureOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.itemTooltipTexture, 4096000);
        } else {
            buf.setIntLE(itemTooltipTextureOffsetSlot, -1);
        }
        if (this.itemTooltipArrowTexture != null) {
            buf.setIntLE(itemTooltipArrowTextureOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.itemTooltipArrowTexture, 4096000);
        } else {
            buf.setIntLE(itemTooltipArrowTextureOffsetSlot, -1);
        }
        if (this.slotTexture != null) {
            buf.setIntLE(slotTextureOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.slotTexture, 4096000);
        } else {
            buf.setIntLE(slotTextureOffsetSlot, -1);
        }
        if (this.blockSlotTexture != null) {
            buf.setIntLE(blockSlotTextureOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.blockSlotTexture, 4096000);
        } else {
            buf.setIntLE(blockSlotTextureOffsetSlot, -1);
        }
        if (this.specialSlotTexture != null) {
            buf.setIntLE(specialSlotTextureOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.specialSlotTexture, 4096000);
        } else {
            buf.setIntLE(specialSlotTextureOffsetSlot, -1);
        }
        if (this.localizationKey != null) {
            buf.setIntLE(localizationKeyOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.localizationKey, 4096000);
        } else {
            buf.setIntLE(localizationKeyOffsetSlot, -1);
        }
    }

    public int computeSize() {
        int size = 35;
        if (this.id != null) {
            size += PacketIO.stringSize(this.id);
        }
        if (this.itemTooltipTexture != null) {
            size += PacketIO.stringSize(this.itemTooltipTexture);
        }
        if (this.itemTooltipArrowTexture != null) {
            size += PacketIO.stringSize(this.itemTooltipArrowTexture);
        }
        if (this.slotTexture != null) {
            size += PacketIO.stringSize(this.slotTexture);
        }
        if (this.blockSlotTexture != null) {
            size += PacketIO.stringSize(this.blockSlotTexture);
        }
        if (this.specialSlotTexture != null) {
            size += PacketIO.stringSize(this.specialSlotTexture);
        }
        if (this.localizationKey != null) {
            size += PacketIO.stringSize(this.localizationKey);
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int pos;
        if (buffer.readableBytes() - offset < 35) {
            return ValidationResult.error("Buffer too small: expected at least 35 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 2) != 0) {
            int idOffset = buffer.getIntLE(offset + 7);
            if (idOffset < 0) {
                return ValidationResult.error("Invalid offset for Id");
            }
            pos = offset + 35 + idOffset;
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
        if ((nullBits & 4) != 0) {
            int itemTooltipTextureOffset = buffer.getIntLE(offset + 11);
            if (itemTooltipTextureOffset < 0) {
                return ValidationResult.error("Invalid offset for ItemTooltipTexture");
            }
            pos = offset + 35 + itemTooltipTextureOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for ItemTooltipTexture");
            }
            int itemTooltipTextureLen = VarInt.peek(buffer, pos);
            if (itemTooltipTextureLen < 0) {
                return ValidationResult.error("Invalid string length for ItemTooltipTexture");
            }
            if (itemTooltipTextureLen > 4096000) {
                return ValidationResult.error("ItemTooltipTexture exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += itemTooltipTextureLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading ItemTooltipTexture");
            }
        }
        if ((nullBits & 8) != 0) {
            int itemTooltipArrowTextureOffset = buffer.getIntLE(offset + 15);
            if (itemTooltipArrowTextureOffset < 0) {
                return ValidationResult.error("Invalid offset for ItemTooltipArrowTexture");
            }
            pos = offset + 35 + itemTooltipArrowTextureOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for ItemTooltipArrowTexture");
            }
            int itemTooltipArrowTextureLen = VarInt.peek(buffer, pos);
            if (itemTooltipArrowTextureLen < 0) {
                return ValidationResult.error("Invalid string length for ItemTooltipArrowTexture");
            }
            if (itemTooltipArrowTextureLen > 4096000) {
                return ValidationResult.error("ItemTooltipArrowTexture exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += itemTooltipArrowTextureLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading ItemTooltipArrowTexture");
            }
        }
        if ((nullBits & 0x10) != 0) {
            int slotTextureOffset = buffer.getIntLE(offset + 19);
            if (slotTextureOffset < 0) {
                return ValidationResult.error("Invalid offset for SlotTexture");
            }
            pos = offset + 35 + slotTextureOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for SlotTexture");
            }
            int slotTextureLen = VarInt.peek(buffer, pos);
            if (slotTextureLen < 0) {
                return ValidationResult.error("Invalid string length for SlotTexture");
            }
            if (slotTextureLen > 4096000) {
                return ValidationResult.error("SlotTexture exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += slotTextureLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading SlotTexture");
            }
        }
        if ((nullBits & 0x20) != 0) {
            int blockSlotTextureOffset = buffer.getIntLE(offset + 23);
            if (blockSlotTextureOffset < 0) {
                return ValidationResult.error("Invalid offset for BlockSlotTexture");
            }
            pos = offset + 35 + blockSlotTextureOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for BlockSlotTexture");
            }
            int blockSlotTextureLen = VarInt.peek(buffer, pos);
            if (blockSlotTextureLen < 0) {
                return ValidationResult.error("Invalid string length for BlockSlotTexture");
            }
            if (blockSlotTextureLen > 4096000) {
                return ValidationResult.error("BlockSlotTexture exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += blockSlotTextureLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading BlockSlotTexture");
            }
        }
        if ((nullBits & 0x40) != 0) {
            int specialSlotTextureOffset = buffer.getIntLE(offset + 27);
            if (specialSlotTextureOffset < 0) {
                return ValidationResult.error("Invalid offset for SpecialSlotTexture");
            }
            pos = offset + 35 + specialSlotTextureOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for SpecialSlotTexture");
            }
            int specialSlotTextureLen = VarInt.peek(buffer, pos);
            if (specialSlotTextureLen < 0) {
                return ValidationResult.error("Invalid string length for SpecialSlotTexture");
            }
            if (specialSlotTextureLen > 4096000) {
                return ValidationResult.error("SpecialSlotTexture exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += specialSlotTextureLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading SpecialSlotTexture");
            }
        }
        if ((nullBits & 0x80) != 0) {
            int localizationKeyOffset = buffer.getIntLE(offset + 31);
            if (localizationKeyOffset < 0) {
                return ValidationResult.error("Invalid offset for LocalizationKey");
            }
            pos = offset + 35 + localizationKeyOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for LocalizationKey");
            }
            int localizationKeyLen = VarInt.peek(buffer, pos);
            if (localizationKeyLen < 0) {
                return ValidationResult.error("Invalid string length for LocalizationKey");
            }
            if (localizationKeyLen > 4096000) {
                return ValidationResult.error("LocalizationKey exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += localizationKeyLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading LocalizationKey");
            }
        }
        return ValidationResult.OK;
    }

    public ItemQuality clone() {
        ItemQuality copy = new ItemQuality();
        copy.id = this.id;
        copy.itemTooltipTexture = this.itemTooltipTexture;
        copy.itemTooltipArrowTexture = this.itemTooltipArrowTexture;
        copy.slotTexture = this.slotTexture;
        copy.blockSlotTexture = this.blockSlotTexture;
        copy.specialSlotTexture = this.specialSlotTexture;
        copy.textColor = this.textColor != null ? this.textColor.clone() : null;
        copy.localizationKey = this.localizationKey;
        copy.visibleQualityLabel = this.visibleQualityLabel;
        copy.renderSpecialSlot = this.renderSpecialSlot;
        copy.hideFromSearch = this.hideFromSearch;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ItemQuality)) {
            return false;
        }
        ItemQuality other = (ItemQuality)obj;
        return Objects.equals(this.id, other.id) && Objects.equals(this.itemTooltipTexture, other.itemTooltipTexture) && Objects.equals(this.itemTooltipArrowTexture, other.itemTooltipArrowTexture) && Objects.equals(this.slotTexture, other.slotTexture) && Objects.equals(this.blockSlotTexture, other.blockSlotTexture) && Objects.equals(this.specialSlotTexture, other.specialSlotTexture) && Objects.equals(this.textColor, other.textColor) && Objects.equals(this.localizationKey, other.localizationKey) && this.visibleQualityLabel == other.visibleQualityLabel && this.renderSpecialSlot == other.renderSpecialSlot && this.hideFromSearch == other.hideFromSearch;
    }

    public int hashCode() {
        return Objects.hash(this.id, this.itemTooltipTexture, this.itemTooltipArrowTexture, this.slotTexture, this.blockSlotTexture, this.specialSlotTexture, this.textColor, this.localizationKey, this.visibleQualityLabel, this.renderSpecialSlot, this.hideFromSearch);
    }
}

