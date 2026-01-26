/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.window;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import com.hypixel.hytale.protocol.packets.window.WindowAction;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class UpdateCategoryAction
extends WindowAction {
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 0;
    public static final int VARIABLE_FIELD_COUNT = 2;
    public static final int VARIABLE_BLOCK_START = 8;
    public static final int MAX_SIZE = 32768018;
    @Nonnull
    public String category = "";
    @Nonnull
    public String itemCategory = "";

    public UpdateCategoryAction() {
    }

    public UpdateCategoryAction(@Nonnull String category, @Nonnull String itemCategory) {
        this.category = category;
        this.itemCategory = itemCategory;
    }

    public UpdateCategoryAction(@Nonnull UpdateCategoryAction other) {
        this.category = other.category;
        this.itemCategory = other.itemCategory;
    }

    @Nonnull
    public static UpdateCategoryAction deserialize(@Nonnull ByteBuf buf, int offset) {
        UpdateCategoryAction obj = new UpdateCategoryAction();
        int varPos0 = offset + 8 + buf.getIntLE(offset + 0);
        int categoryLen = VarInt.peek(buf, varPos0);
        if (categoryLen < 0) {
            throw ProtocolException.negativeLength("Category", categoryLen);
        }
        if (categoryLen > 4096000) {
            throw ProtocolException.stringTooLong("Category", categoryLen, 4096000);
        }
        obj.category = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
        int varPos1 = offset + 8 + buf.getIntLE(offset + 4);
        int itemCategoryLen = VarInt.peek(buf, varPos1);
        if (itemCategoryLen < 0) {
            throw ProtocolException.negativeLength("ItemCategory", itemCategoryLen);
        }
        if (itemCategoryLen > 4096000) {
            throw ProtocolException.stringTooLong("ItemCategory", itemCategoryLen, 4096000);
        }
        obj.itemCategory = PacketIO.readVarString(buf, varPos1, PacketIO.UTF8);
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int maxEnd = 8;
        int fieldOffset0 = buf.getIntLE(offset + 0);
        int pos0 = offset + 8 + fieldOffset0;
        int sl = VarInt.peek(buf, pos0);
        if ((pos0 += VarInt.length(buf, pos0) + sl) - offset > maxEnd) {
            maxEnd = pos0 - offset;
        }
        int fieldOffset1 = buf.getIntLE(offset + 4);
        int pos1 = offset + 8 + fieldOffset1;
        sl = VarInt.peek(buf, pos1);
        if ((pos1 += VarInt.length(buf, pos1) + sl) - offset > maxEnd) {
            maxEnd = pos1 - offset;
        }
        return maxEnd;
    }

    @Override
    public int serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        int categoryOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int itemCategoryOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        buf.setIntLE(categoryOffsetSlot, buf.writerIndex() - varBlockStart);
        PacketIO.writeVarString(buf, this.category, 4096000);
        buf.setIntLE(itemCategoryOffsetSlot, buf.writerIndex() - varBlockStart);
        PacketIO.writeVarString(buf, this.itemCategory, 4096000);
        return buf.writerIndex() - startPos;
    }

    @Override
    public int computeSize() {
        int size = 8;
        size += PacketIO.stringSize(this.category);
        return size += PacketIO.stringSize(this.itemCategory);
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 8) {
            return ValidationResult.error("Buffer too small: expected at least 8 bytes");
        }
        int categoryOffset = buffer.getIntLE(offset + 0);
        if (categoryOffset < 0) {
            return ValidationResult.error("Invalid offset for Category");
        }
        int pos = offset + 8 + categoryOffset;
        if (pos >= buffer.writerIndex()) {
            return ValidationResult.error("Offset out of bounds for Category");
        }
        int categoryLen = VarInt.peek(buffer, pos);
        if (categoryLen < 0) {
            return ValidationResult.error("Invalid string length for Category");
        }
        if (categoryLen > 4096000) {
            return ValidationResult.error("Category exceeds max length 4096000");
        }
        pos += VarInt.length(buffer, pos);
        if ((pos += categoryLen) > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Category");
        }
        int itemCategoryOffset = buffer.getIntLE(offset + 4);
        if (itemCategoryOffset < 0) {
            return ValidationResult.error("Invalid offset for ItemCategory");
        }
        pos = offset + 8 + itemCategoryOffset;
        if (pos >= buffer.writerIndex()) {
            return ValidationResult.error("Offset out of bounds for ItemCategory");
        }
        int itemCategoryLen = VarInt.peek(buffer, pos);
        if (itemCategoryLen < 0) {
            return ValidationResult.error("Invalid string length for ItemCategory");
        }
        if (itemCategoryLen > 4096000) {
            return ValidationResult.error("ItemCategory exceeds max length 4096000");
        }
        pos += VarInt.length(buffer, pos);
        if ((pos += itemCategoryLen) > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading ItemCategory");
        }
        return ValidationResult.OK;
    }

    public UpdateCategoryAction clone() {
        UpdateCategoryAction copy = new UpdateCategoryAction();
        copy.category = this.category;
        copy.itemCategory = this.itemCategory;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UpdateCategoryAction)) {
            return false;
        }
        UpdateCategoryAction other = (UpdateCategoryAction)obj;
        return Objects.equals(this.category, other.category) && Objects.equals(this.itemCategory, other.itemCategory);
    }

    public int hashCode() {
        return Objects.hash(this.category, this.itemCategory);
    }
}

