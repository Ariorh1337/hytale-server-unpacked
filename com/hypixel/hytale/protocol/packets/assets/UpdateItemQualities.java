/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.assets;

import com.hypixel.hytale.protocol.ItemQuality;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.UpdateType;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UpdateItemQualities
implements Packet {
    public static final int PACKET_ID = 55;
    public static final boolean IS_COMPRESSED = true;
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 6;
    public static final int VARIABLE_FIELD_COUNT = 1;
    public static final int VARIABLE_BLOCK_START = 6;
    public static final int MAX_SIZE = 0x64000000;
    @Nonnull
    public UpdateType type = UpdateType.Init;
    public int maxId;
    @Nullable
    public Map<Integer, ItemQuality> itemQualities;

    @Override
    public int getId() {
        return 55;
    }

    public UpdateItemQualities() {
    }

    public UpdateItemQualities(@Nonnull UpdateType type, int maxId, @Nullable Map<Integer, ItemQuality> itemQualities) {
        this.type = type;
        this.maxId = maxId;
        this.itemQualities = itemQualities;
    }

    public UpdateItemQualities(@Nonnull UpdateItemQualities other) {
        this.type = other.type;
        this.maxId = other.maxId;
        this.itemQualities = other.itemQualities;
    }

    @Nonnull
    public static UpdateItemQualities deserialize(@Nonnull ByteBuf buf, int offset) {
        UpdateItemQualities obj = new UpdateItemQualities();
        byte nullBits = buf.getByte(offset);
        obj.type = UpdateType.fromValue(buf.getByte(offset + 1));
        obj.maxId = buf.getIntLE(offset + 2);
        int pos = offset + 6;
        if ((nullBits & 1) != 0) {
            int itemQualitiesCount = VarInt.peek(buf, pos);
            if (itemQualitiesCount < 0) {
                throw ProtocolException.negativeLength("ItemQualities", itemQualitiesCount);
            }
            if (itemQualitiesCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("ItemQualities", itemQualitiesCount, 4096000);
            }
            pos += VarInt.size(itemQualitiesCount);
            obj.itemQualities = new HashMap<Integer, ItemQuality>(itemQualitiesCount);
            for (int i = 0; i < itemQualitiesCount; ++i) {
                int key = buf.getIntLE(pos);
                ItemQuality val = ItemQuality.deserialize(buf, pos += 4);
                pos += ItemQuality.computeBytesConsumed(buf, pos);
                if (obj.itemQualities.put(key, val) == null) continue;
                throw ProtocolException.duplicateKey("itemQualities", key);
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        byte nullBits = buf.getByte(offset);
        int pos = offset + 6;
        if ((nullBits & 1) != 0) {
            int dictLen = VarInt.peek(buf, pos);
            pos += VarInt.length(buf, pos);
            for (int i = 0; i < dictLen; ++i) {
                pos += 4;
                pos += ItemQuality.computeBytesConsumed(buf, pos);
            }
        }
        return pos - offset;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        int nullBits = 0;
        if (this.itemQualities != null) {
            nullBits = (byte)(nullBits | 1);
        }
        buf.writeByte(nullBits);
        buf.writeByte(this.type.getValue());
        buf.writeIntLE(this.maxId);
        if (this.itemQualities != null) {
            if (this.itemQualities.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("ItemQualities", this.itemQualities.size(), 4096000);
            }
            VarInt.write(buf, this.itemQualities.size());
            for (Map.Entry<Integer, ItemQuality> e : this.itemQualities.entrySet()) {
                buf.writeIntLE(e.getKey());
                e.getValue().serialize(buf);
            }
        }
    }

    @Override
    public int computeSize() {
        int size = 6;
        if (this.itemQualities != null) {
            int itemQualitiesSize = 0;
            for (Map.Entry<Integer, ItemQuality> kvp : this.itemQualities.entrySet()) {
                itemQualitiesSize += 4 + kvp.getValue().computeSize();
            }
            size += VarInt.size(this.itemQualities.size()) + itemQualitiesSize;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 6) {
            return ValidationResult.error("Buffer too small: expected at least 6 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        int pos = offset + 6;
        if ((nullBits & 1) != 0) {
            int itemQualitiesCount = VarInt.peek(buffer, pos);
            if (itemQualitiesCount < 0) {
                return ValidationResult.error("Invalid dictionary count for ItemQualities");
            }
            if (itemQualitiesCount > 4096000) {
                return ValidationResult.error("ItemQualities exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (int i = 0; i < itemQualitiesCount; ++i) {
                if ((pos += 4) > buffer.writerIndex()) {
                    return ValidationResult.error("Buffer overflow reading key");
                }
                pos += ItemQuality.computeBytesConsumed(buffer, pos);
            }
        }
        return ValidationResult.OK;
    }

    public UpdateItemQualities clone() {
        UpdateItemQualities copy = new UpdateItemQualities();
        copy.type = this.type;
        copy.maxId = this.maxId;
        if (this.itemQualities != null) {
            HashMap<Integer, ItemQuality> m = new HashMap<Integer, ItemQuality>();
            for (Map.Entry<Integer, ItemQuality> e : this.itemQualities.entrySet()) {
                m.put(e.getKey(), e.getValue().clone());
            }
            copy.itemQualities = m;
        }
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UpdateItemQualities)) {
            return false;
        }
        UpdateItemQualities other = (UpdateItemQualities)obj;
        return Objects.equals((Object)this.type, (Object)other.type) && this.maxId == other.maxId && Objects.equals(this.itemQualities, other.itemQualities);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.type, this.maxId, this.itemQualities});
    }
}

