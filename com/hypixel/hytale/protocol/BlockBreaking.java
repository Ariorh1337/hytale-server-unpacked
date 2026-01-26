/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockBreaking {
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 13;
    public static final int VARIABLE_FIELD_COUNT = 3;
    public static final int VARIABLE_BLOCK_START = 25;
    public static final int MAX_SIZE = 49152040;
    @Nullable
    public String gatherType;
    public float health;
    public int quantity = 1;
    public int quality;
    @Nullable
    public String itemId;
    @Nullable
    public String dropListId;

    public BlockBreaking() {
    }

    public BlockBreaking(@Nullable String gatherType, float health, int quantity, int quality, @Nullable String itemId, @Nullable String dropListId) {
        this.gatherType = gatherType;
        this.health = health;
        this.quantity = quantity;
        this.quality = quality;
        this.itemId = itemId;
        this.dropListId = dropListId;
    }

    public BlockBreaking(@Nonnull BlockBreaking other) {
        this.gatherType = other.gatherType;
        this.health = other.health;
        this.quantity = other.quantity;
        this.quality = other.quality;
        this.itemId = other.itemId;
        this.dropListId = other.dropListId;
    }

    @Nonnull
    public static BlockBreaking deserialize(@Nonnull ByteBuf buf, int offset) {
        BlockBreaking obj = new BlockBreaking();
        byte nullBits = buf.getByte(offset);
        obj.health = buf.getFloatLE(offset + 1);
        obj.quantity = buf.getIntLE(offset + 5);
        obj.quality = buf.getIntLE(offset + 9);
        if ((nullBits & 1) != 0) {
            int varPos0 = offset + 25 + buf.getIntLE(offset + 13);
            int gatherTypeLen = VarInt.peek(buf, varPos0);
            if (gatherTypeLen < 0) {
                throw ProtocolException.negativeLength("GatherType", gatherTypeLen);
            }
            if (gatherTypeLen > 4096000) {
                throw ProtocolException.stringTooLong("GatherType", gatherTypeLen, 4096000);
            }
            obj.gatherType = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
        }
        if ((nullBits & 2) != 0) {
            int varPos1 = offset + 25 + buf.getIntLE(offset + 17);
            int itemIdLen = VarInt.peek(buf, varPos1);
            if (itemIdLen < 0) {
                throw ProtocolException.negativeLength("ItemId", itemIdLen);
            }
            if (itemIdLen > 4096000) {
                throw ProtocolException.stringTooLong("ItemId", itemIdLen, 4096000);
            }
            obj.itemId = PacketIO.readVarString(buf, varPos1, PacketIO.UTF8);
        }
        if ((nullBits & 4) != 0) {
            int varPos2 = offset + 25 + buf.getIntLE(offset + 21);
            int dropListIdLen = VarInt.peek(buf, varPos2);
            if (dropListIdLen < 0) {
                throw ProtocolException.negativeLength("DropListId", dropListIdLen);
            }
            if (dropListIdLen > 4096000) {
                throw ProtocolException.stringTooLong("DropListId", dropListIdLen, 4096000);
            }
            obj.dropListId = PacketIO.readVarString(buf, varPos2, PacketIO.UTF8);
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int sl;
        byte nullBits = buf.getByte(offset);
        int maxEnd = 25;
        if ((nullBits & 1) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 13);
            int pos0 = offset + 25 + fieldOffset0;
            sl = VarInt.peek(buf, pos0);
            if ((pos0 += VarInt.length(buf, pos0) + sl) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 2) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 17);
            int pos1 = offset + 25 + fieldOffset1;
            sl = VarInt.peek(buf, pos1);
            if ((pos1 += VarInt.length(buf, pos1) + sl) - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        if ((nullBits & 4) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 21);
            int pos2 = offset + 25 + fieldOffset2;
            sl = VarInt.peek(buf, pos2);
            if ((pos2 += VarInt.length(buf, pos2) + sl) - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        return maxEnd;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        byte nullBits = 0;
        if (this.gatherType != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.itemId != null) {
            nullBits = (byte)(nullBits | 2);
        }
        if (this.dropListId != null) {
            nullBits = (byte)(nullBits | 4);
        }
        buf.writeByte(nullBits);
        buf.writeFloatLE(this.health);
        buf.writeIntLE(this.quantity);
        buf.writeIntLE(this.quality);
        int gatherTypeOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int itemIdOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int dropListIdOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.gatherType != null) {
            buf.setIntLE(gatherTypeOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.gatherType, 4096000);
        } else {
            buf.setIntLE(gatherTypeOffsetSlot, -1);
        }
        if (this.itemId != null) {
            buf.setIntLE(itemIdOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.itemId, 4096000);
        } else {
            buf.setIntLE(itemIdOffsetSlot, -1);
        }
        if (this.dropListId != null) {
            buf.setIntLE(dropListIdOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.dropListId, 4096000);
        } else {
            buf.setIntLE(dropListIdOffsetSlot, -1);
        }
    }

    public int computeSize() {
        int size = 25;
        if (this.gatherType != null) {
            size += PacketIO.stringSize(this.gatherType);
        }
        if (this.itemId != null) {
            size += PacketIO.stringSize(this.itemId);
        }
        if (this.dropListId != null) {
            size += PacketIO.stringSize(this.dropListId);
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int pos;
        if (buffer.readableBytes() - offset < 25) {
            return ValidationResult.error("Buffer too small: expected at least 25 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 1) != 0) {
            int gatherTypeOffset = buffer.getIntLE(offset + 13);
            if (gatherTypeOffset < 0) {
                return ValidationResult.error("Invalid offset for GatherType");
            }
            pos = offset + 25 + gatherTypeOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for GatherType");
            }
            int gatherTypeLen = VarInt.peek(buffer, pos);
            if (gatherTypeLen < 0) {
                return ValidationResult.error("Invalid string length for GatherType");
            }
            if (gatherTypeLen > 4096000) {
                return ValidationResult.error("GatherType exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += gatherTypeLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading GatherType");
            }
        }
        if ((nullBits & 2) != 0) {
            int itemIdOffset = buffer.getIntLE(offset + 17);
            if (itemIdOffset < 0) {
                return ValidationResult.error("Invalid offset for ItemId");
            }
            pos = offset + 25 + itemIdOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for ItemId");
            }
            int itemIdLen = VarInt.peek(buffer, pos);
            if (itemIdLen < 0) {
                return ValidationResult.error("Invalid string length for ItemId");
            }
            if (itemIdLen > 4096000) {
                return ValidationResult.error("ItemId exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += itemIdLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading ItemId");
            }
        }
        if ((nullBits & 4) != 0) {
            int dropListIdOffset = buffer.getIntLE(offset + 21);
            if (dropListIdOffset < 0) {
                return ValidationResult.error("Invalid offset for DropListId");
            }
            pos = offset + 25 + dropListIdOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for DropListId");
            }
            int dropListIdLen = VarInt.peek(buffer, pos);
            if (dropListIdLen < 0) {
                return ValidationResult.error("Invalid string length for DropListId");
            }
            if (dropListIdLen > 4096000) {
                return ValidationResult.error("DropListId exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += dropListIdLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading DropListId");
            }
        }
        return ValidationResult.OK;
    }

    public BlockBreaking clone() {
        BlockBreaking copy = new BlockBreaking();
        copy.gatherType = this.gatherType;
        copy.health = this.health;
        copy.quantity = this.quantity;
        copy.quality = this.quality;
        copy.itemId = this.itemId;
        copy.dropListId = this.dropListId;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BlockBreaking)) {
            return false;
        }
        BlockBreaking other = (BlockBreaking)obj;
        return Objects.equals(this.gatherType, other.gatherType) && this.health == other.health && this.quantity == other.quantity && this.quality == other.quality && Objects.equals(this.itemId, other.itemId) && Objects.equals(this.dropListId, other.dropListId);
    }

    public int hashCode() {
        return Objects.hash(this.gatherType, Float.valueOf(this.health), this.quantity, this.quality, this.itemId, this.dropListId);
    }
}

