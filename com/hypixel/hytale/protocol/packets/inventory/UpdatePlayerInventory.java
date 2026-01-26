/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.inventory;

import com.hypixel.hytale.protocol.InventorySection;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.SortType;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UpdatePlayerInventory
implements Packet {
    public static final int PACKET_ID = 170;
    public static final boolean IS_COMPRESSED = true;
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 2;
    public static final int VARIABLE_FIELD_COUNT = 7;
    public static final int VARIABLE_BLOCK_START = 30;
    public static final int MAX_SIZE = 0x64000000;
    @Nullable
    public InventorySection storage;
    @Nullable
    public InventorySection armor;
    @Nullable
    public InventorySection hotbar;
    @Nullable
    public InventorySection utility;
    @Nullable
    public InventorySection builderMaterial;
    @Nullable
    public InventorySection tools;
    @Nullable
    public InventorySection backpack;
    @Nonnull
    public SortType sortType = SortType.Name;

    @Override
    public int getId() {
        return 170;
    }

    public UpdatePlayerInventory() {
    }

    public UpdatePlayerInventory(@Nullable InventorySection storage, @Nullable InventorySection armor, @Nullable InventorySection hotbar, @Nullable InventorySection utility, @Nullable InventorySection builderMaterial, @Nullable InventorySection tools, @Nullable InventorySection backpack, @Nonnull SortType sortType) {
        this.storage = storage;
        this.armor = armor;
        this.hotbar = hotbar;
        this.utility = utility;
        this.builderMaterial = builderMaterial;
        this.tools = tools;
        this.backpack = backpack;
        this.sortType = sortType;
    }

    public UpdatePlayerInventory(@Nonnull UpdatePlayerInventory other) {
        this.storage = other.storage;
        this.armor = other.armor;
        this.hotbar = other.hotbar;
        this.utility = other.utility;
        this.builderMaterial = other.builderMaterial;
        this.tools = other.tools;
        this.backpack = other.backpack;
        this.sortType = other.sortType;
    }

    @Nonnull
    public static UpdatePlayerInventory deserialize(@Nonnull ByteBuf buf, int offset) {
        UpdatePlayerInventory obj = new UpdatePlayerInventory();
        byte nullBits = buf.getByte(offset);
        obj.sortType = SortType.fromValue(buf.getByte(offset + 1));
        if ((nullBits & 1) != 0) {
            int varPos0 = offset + 30 + buf.getIntLE(offset + 2);
            obj.storage = InventorySection.deserialize(buf, varPos0);
        }
        if ((nullBits & 2) != 0) {
            int varPos1 = offset + 30 + buf.getIntLE(offset + 6);
            obj.armor = InventorySection.deserialize(buf, varPos1);
        }
        if ((nullBits & 4) != 0) {
            int varPos2 = offset + 30 + buf.getIntLE(offset + 10);
            obj.hotbar = InventorySection.deserialize(buf, varPos2);
        }
        if ((nullBits & 8) != 0) {
            int varPos3 = offset + 30 + buf.getIntLE(offset + 14);
            obj.utility = InventorySection.deserialize(buf, varPos3);
        }
        if ((nullBits & 0x10) != 0) {
            int varPos4 = offset + 30 + buf.getIntLE(offset + 18);
            obj.builderMaterial = InventorySection.deserialize(buf, varPos4);
        }
        if ((nullBits & 0x20) != 0) {
            int varPos5 = offset + 30 + buf.getIntLE(offset + 22);
            obj.tools = InventorySection.deserialize(buf, varPos5);
        }
        if ((nullBits & 0x40) != 0) {
            int varPos6 = offset + 30 + buf.getIntLE(offset + 26);
            obj.backpack = InventorySection.deserialize(buf, varPos6);
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        byte nullBits = buf.getByte(offset);
        int maxEnd = 30;
        if ((nullBits & 1) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 2);
            int pos0 = offset + 30 + fieldOffset0;
            if ((pos0 += InventorySection.computeBytesConsumed(buf, pos0)) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 2) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 6);
            int pos1 = offset + 30 + fieldOffset1;
            if ((pos1 += InventorySection.computeBytesConsumed(buf, pos1)) - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        if ((nullBits & 4) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 10);
            int pos2 = offset + 30 + fieldOffset2;
            if ((pos2 += InventorySection.computeBytesConsumed(buf, pos2)) - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        if ((nullBits & 8) != 0) {
            int fieldOffset3 = buf.getIntLE(offset + 14);
            int pos3 = offset + 30 + fieldOffset3;
            if ((pos3 += InventorySection.computeBytesConsumed(buf, pos3)) - offset > maxEnd) {
                maxEnd = pos3 - offset;
            }
        }
        if ((nullBits & 0x10) != 0) {
            int fieldOffset4 = buf.getIntLE(offset + 18);
            int pos4 = offset + 30 + fieldOffset4;
            if ((pos4 += InventorySection.computeBytesConsumed(buf, pos4)) - offset > maxEnd) {
                maxEnd = pos4 - offset;
            }
        }
        if ((nullBits & 0x20) != 0) {
            int fieldOffset5 = buf.getIntLE(offset + 22);
            int pos5 = offset + 30 + fieldOffset5;
            if ((pos5 += InventorySection.computeBytesConsumed(buf, pos5)) - offset > maxEnd) {
                maxEnd = pos5 - offset;
            }
        }
        if ((nullBits & 0x40) != 0) {
            int fieldOffset6 = buf.getIntLE(offset + 26);
            int pos6 = offset + 30 + fieldOffset6;
            if ((pos6 += InventorySection.computeBytesConsumed(buf, pos6)) - offset > maxEnd) {
                maxEnd = pos6 - offset;
            }
        }
        return maxEnd;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        byte nullBits = 0;
        if (this.storage != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.armor != null) {
            nullBits = (byte)(nullBits | 2);
        }
        if (this.hotbar != null) {
            nullBits = (byte)(nullBits | 4);
        }
        if (this.utility != null) {
            nullBits = (byte)(nullBits | 8);
        }
        if (this.builderMaterial != null) {
            nullBits = (byte)(nullBits | 0x10);
        }
        if (this.tools != null) {
            nullBits = (byte)(nullBits | 0x20);
        }
        if (this.backpack != null) {
            nullBits = (byte)(nullBits | 0x40);
        }
        buf.writeByte(nullBits);
        buf.writeByte(this.sortType.getValue());
        int storageOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int armorOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int hotbarOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int utilityOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int builderMaterialOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int toolsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int backpackOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.storage != null) {
            buf.setIntLE(storageOffsetSlot, buf.writerIndex() - varBlockStart);
            this.storage.serialize(buf);
        } else {
            buf.setIntLE(storageOffsetSlot, -1);
        }
        if (this.armor != null) {
            buf.setIntLE(armorOffsetSlot, buf.writerIndex() - varBlockStart);
            this.armor.serialize(buf);
        } else {
            buf.setIntLE(armorOffsetSlot, -1);
        }
        if (this.hotbar != null) {
            buf.setIntLE(hotbarOffsetSlot, buf.writerIndex() - varBlockStart);
            this.hotbar.serialize(buf);
        } else {
            buf.setIntLE(hotbarOffsetSlot, -1);
        }
        if (this.utility != null) {
            buf.setIntLE(utilityOffsetSlot, buf.writerIndex() - varBlockStart);
            this.utility.serialize(buf);
        } else {
            buf.setIntLE(utilityOffsetSlot, -1);
        }
        if (this.builderMaterial != null) {
            buf.setIntLE(builderMaterialOffsetSlot, buf.writerIndex() - varBlockStart);
            this.builderMaterial.serialize(buf);
        } else {
            buf.setIntLE(builderMaterialOffsetSlot, -1);
        }
        if (this.tools != null) {
            buf.setIntLE(toolsOffsetSlot, buf.writerIndex() - varBlockStart);
            this.tools.serialize(buf);
        } else {
            buf.setIntLE(toolsOffsetSlot, -1);
        }
        if (this.backpack != null) {
            buf.setIntLE(backpackOffsetSlot, buf.writerIndex() - varBlockStart);
            this.backpack.serialize(buf);
        } else {
            buf.setIntLE(backpackOffsetSlot, -1);
        }
    }

    @Override
    public int computeSize() {
        int size = 30;
        if (this.storage != null) {
            size += this.storage.computeSize();
        }
        if (this.armor != null) {
            size += this.armor.computeSize();
        }
        if (this.hotbar != null) {
            size += this.hotbar.computeSize();
        }
        if (this.utility != null) {
            size += this.utility.computeSize();
        }
        if (this.builderMaterial != null) {
            size += this.builderMaterial.computeSize();
        }
        if (this.tools != null) {
            size += this.tools.computeSize();
        }
        if (this.backpack != null) {
            size += this.backpack.computeSize();
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int pos;
        if (buffer.readableBytes() - offset < 30) {
            return ValidationResult.error("Buffer too small: expected at least 30 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 1) != 0) {
            int storageOffset = buffer.getIntLE(offset + 2);
            if (storageOffset < 0) {
                return ValidationResult.error("Invalid offset for Storage");
            }
            pos = offset + 30 + storageOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Storage");
            }
            ValidationResult storageResult = InventorySection.validateStructure(buffer, pos);
            if (!storageResult.isValid()) {
                return ValidationResult.error("Invalid Storage: " + storageResult.error());
            }
            pos += InventorySection.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits & 2) != 0) {
            int armorOffset = buffer.getIntLE(offset + 6);
            if (armorOffset < 0) {
                return ValidationResult.error("Invalid offset for Armor");
            }
            pos = offset + 30 + armorOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Armor");
            }
            ValidationResult armorResult = InventorySection.validateStructure(buffer, pos);
            if (!armorResult.isValid()) {
                return ValidationResult.error("Invalid Armor: " + armorResult.error());
            }
            pos += InventorySection.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits & 4) != 0) {
            int hotbarOffset = buffer.getIntLE(offset + 10);
            if (hotbarOffset < 0) {
                return ValidationResult.error("Invalid offset for Hotbar");
            }
            pos = offset + 30 + hotbarOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Hotbar");
            }
            ValidationResult hotbarResult = InventorySection.validateStructure(buffer, pos);
            if (!hotbarResult.isValid()) {
                return ValidationResult.error("Invalid Hotbar: " + hotbarResult.error());
            }
            pos += InventorySection.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits & 8) != 0) {
            int utilityOffset = buffer.getIntLE(offset + 14);
            if (utilityOffset < 0) {
                return ValidationResult.error("Invalid offset for Utility");
            }
            pos = offset + 30 + utilityOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Utility");
            }
            ValidationResult utilityResult = InventorySection.validateStructure(buffer, pos);
            if (!utilityResult.isValid()) {
                return ValidationResult.error("Invalid Utility: " + utilityResult.error());
            }
            pos += InventorySection.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits & 0x10) != 0) {
            int builderMaterialOffset = buffer.getIntLE(offset + 18);
            if (builderMaterialOffset < 0) {
                return ValidationResult.error("Invalid offset for BuilderMaterial");
            }
            pos = offset + 30 + builderMaterialOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for BuilderMaterial");
            }
            ValidationResult builderMaterialResult = InventorySection.validateStructure(buffer, pos);
            if (!builderMaterialResult.isValid()) {
                return ValidationResult.error("Invalid BuilderMaterial: " + builderMaterialResult.error());
            }
            pos += InventorySection.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits & 0x20) != 0) {
            int toolsOffset = buffer.getIntLE(offset + 22);
            if (toolsOffset < 0) {
                return ValidationResult.error("Invalid offset for Tools");
            }
            pos = offset + 30 + toolsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Tools");
            }
            ValidationResult toolsResult = InventorySection.validateStructure(buffer, pos);
            if (!toolsResult.isValid()) {
                return ValidationResult.error("Invalid Tools: " + toolsResult.error());
            }
            pos += InventorySection.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits & 0x40) != 0) {
            int backpackOffset = buffer.getIntLE(offset + 26);
            if (backpackOffset < 0) {
                return ValidationResult.error("Invalid offset for Backpack");
            }
            pos = offset + 30 + backpackOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Backpack");
            }
            ValidationResult backpackResult = InventorySection.validateStructure(buffer, pos);
            if (!backpackResult.isValid()) {
                return ValidationResult.error("Invalid Backpack: " + backpackResult.error());
            }
            pos += InventorySection.computeBytesConsumed(buffer, pos);
        }
        return ValidationResult.OK;
    }

    public UpdatePlayerInventory clone() {
        UpdatePlayerInventory copy = new UpdatePlayerInventory();
        copy.storage = this.storage != null ? this.storage.clone() : null;
        copy.armor = this.armor != null ? this.armor.clone() : null;
        copy.hotbar = this.hotbar != null ? this.hotbar.clone() : null;
        copy.utility = this.utility != null ? this.utility.clone() : null;
        copy.builderMaterial = this.builderMaterial != null ? this.builderMaterial.clone() : null;
        copy.tools = this.tools != null ? this.tools.clone() : null;
        copy.backpack = this.backpack != null ? this.backpack.clone() : null;
        copy.sortType = this.sortType;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UpdatePlayerInventory)) {
            return false;
        }
        UpdatePlayerInventory other = (UpdatePlayerInventory)obj;
        return Objects.equals(this.storage, other.storage) && Objects.equals(this.armor, other.armor) && Objects.equals(this.hotbar, other.hotbar) && Objects.equals(this.utility, other.utility) && Objects.equals(this.builderMaterial, other.builderMaterial) && Objects.equals(this.tools, other.tools) && Objects.equals(this.backpack, other.backpack) && Objects.equals((Object)this.sortType, (Object)other.sortType);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.storage, this.armor, this.hotbar, this.utility, this.builderMaterial, this.tools, this.backpack, this.sortType});
    }
}

