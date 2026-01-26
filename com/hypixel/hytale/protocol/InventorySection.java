/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.ItemWithAllMetadata;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InventorySection {
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 3;
    public static final int VARIABLE_FIELD_COUNT = 1;
    public static final int VARIABLE_BLOCK_START = 3;
    public static final int MAX_SIZE = 0x64000000;
    @Nullable
    public Map<Integer, ItemWithAllMetadata> items;
    public short capacity;

    public InventorySection() {
    }

    public InventorySection(@Nullable Map<Integer, ItemWithAllMetadata> items, short capacity) {
        this.items = items;
        this.capacity = capacity;
    }

    public InventorySection(@Nonnull InventorySection other) {
        this.items = other.items;
        this.capacity = other.capacity;
    }

    @Nonnull
    public static InventorySection deserialize(@Nonnull ByteBuf buf, int offset) {
        InventorySection obj = new InventorySection();
        byte nullBits = buf.getByte(offset);
        obj.capacity = buf.getShortLE(offset + 1);
        int pos = offset + 3;
        if ((nullBits & 1) != 0) {
            int itemsCount = VarInt.peek(buf, pos);
            if (itemsCount < 0) {
                throw ProtocolException.negativeLength("Items", itemsCount);
            }
            if (itemsCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Items", itemsCount, 4096000);
            }
            pos += VarInt.size(itemsCount);
            obj.items = new HashMap<Integer, ItemWithAllMetadata>(itemsCount);
            for (int i = 0; i < itemsCount; ++i) {
                int key = buf.getIntLE(pos);
                ItemWithAllMetadata val = ItemWithAllMetadata.deserialize(buf, pos += 4);
                pos += ItemWithAllMetadata.computeBytesConsumed(buf, pos);
                if (obj.items.put(key, val) == null) continue;
                throw ProtocolException.duplicateKey("items", key);
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        byte nullBits = buf.getByte(offset);
        int pos = offset + 3;
        if ((nullBits & 1) != 0) {
            int dictLen = VarInt.peek(buf, pos);
            pos += VarInt.length(buf, pos);
            for (int i = 0; i < dictLen; ++i) {
                pos += 4;
                pos += ItemWithAllMetadata.computeBytesConsumed(buf, pos);
            }
        }
        return pos - offset;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        int nullBits = 0;
        if (this.items != null) {
            nullBits = (byte)(nullBits | 1);
        }
        buf.writeByte(nullBits);
        buf.writeShortLE(this.capacity);
        if (this.items != null) {
            if (this.items.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Items", this.items.size(), 4096000);
            }
            VarInt.write(buf, this.items.size());
            for (Map.Entry<Integer, ItemWithAllMetadata> e : this.items.entrySet()) {
                buf.writeIntLE(e.getKey());
                e.getValue().serialize(buf);
            }
        }
    }

    public int computeSize() {
        int size = 3;
        if (this.items != null) {
            int itemsSize = 0;
            for (Map.Entry<Integer, ItemWithAllMetadata> kvp : this.items.entrySet()) {
                itemsSize += 4 + kvp.getValue().computeSize();
            }
            size += VarInt.size(this.items.size()) + itemsSize;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 3) {
            return ValidationResult.error("Buffer too small: expected at least 3 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        int pos = offset + 3;
        if ((nullBits & 1) != 0) {
            int itemsCount = VarInt.peek(buffer, pos);
            if (itemsCount < 0) {
                return ValidationResult.error("Invalid dictionary count for Items");
            }
            if (itemsCount > 4096000) {
                return ValidationResult.error("Items exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (int i = 0; i < itemsCount; ++i) {
                if ((pos += 4) > buffer.writerIndex()) {
                    return ValidationResult.error("Buffer overflow reading key");
                }
                pos += ItemWithAllMetadata.computeBytesConsumed(buffer, pos);
            }
        }
        return ValidationResult.OK;
    }

    public InventorySection clone() {
        InventorySection copy = new InventorySection();
        if (this.items != null) {
            HashMap<Integer, ItemWithAllMetadata> m = new HashMap<Integer, ItemWithAllMetadata>();
            for (Map.Entry<Integer, ItemWithAllMetadata> e : this.items.entrySet()) {
                m.put(e.getKey(), e.getValue().clone());
            }
            copy.items = m;
        }
        copy.capacity = this.capacity;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof InventorySection)) {
            return false;
        }
        InventorySection other = (InventorySection)obj;
        return Objects.equals(this.items, other.items) && this.capacity == other.capacity;
    }

    public int hashCode() {
        return Objects.hash(this.items, this.capacity);
    }
}

