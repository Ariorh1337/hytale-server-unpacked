/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.assets;

import com.hypixel.hytale.protocol.Hitbox;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.UpdateType;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UpdateBlockHitboxes
implements Packet {
    public static final int PACKET_ID = 41;
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
    public Map<Integer, Hitbox[]> blockBaseHitboxes;

    @Override
    public int getId() {
        return 41;
    }

    public UpdateBlockHitboxes() {
    }

    public UpdateBlockHitboxes(@Nonnull UpdateType type, int maxId, @Nullable Map<Integer, Hitbox[]> blockBaseHitboxes) {
        this.type = type;
        this.maxId = maxId;
        this.blockBaseHitboxes = blockBaseHitboxes;
    }

    public UpdateBlockHitboxes(@Nonnull UpdateBlockHitboxes other) {
        this.type = other.type;
        this.maxId = other.maxId;
        this.blockBaseHitboxes = other.blockBaseHitboxes;
    }

    @Nonnull
    public static UpdateBlockHitboxes deserialize(@Nonnull ByteBuf buf, int offset) {
        UpdateBlockHitboxes obj = new UpdateBlockHitboxes();
        byte nullBits = buf.getByte(offset);
        obj.type = UpdateType.fromValue(buf.getByte(offset + 1));
        obj.maxId = buf.getIntLE(offset + 2);
        int pos = offset + 6;
        if ((nullBits & 1) != 0) {
            int blockBaseHitboxesCount = VarInt.peek(buf, pos);
            if (blockBaseHitboxesCount < 0) {
                throw ProtocolException.negativeLength("BlockBaseHitboxes", blockBaseHitboxesCount);
            }
            if (blockBaseHitboxesCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("BlockBaseHitboxes", blockBaseHitboxesCount, 4096000);
            }
            pos += VarInt.size(blockBaseHitboxesCount);
            obj.blockBaseHitboxes = new HashMap<Integer, Hitbox[]>(blockBaseHitboxesCount);
            for (int i = 0; i < blockBaseHitboxesCount; ++i) {
                int key = buf.getIntLE(pos);
                int valLen = VarInt.peek(buf, pos += 4);
                if (valLen < 0) {
                    throw ProtocolException.negativeLength("val", valLen);
                }
                if (valLen > 64) {
                    throw ProtocolException.arrayTooLong("val", valLen, 64);
                }
                int valVarLen = VarInt.length(buf, pos);
                if ((long)(pos + valVarLen) + (long)valLen * 24L > (long)buf.readableBytes()) {
                    throw ProtocolException.bufferTooSmall("val", pos + valVarLen + valLen * 24, buf.readableBytes());
                }
                pos += valVarLen;
                Hitbox[] val = new Hitbox[valLen];
                for (int valIdx = 0; valIdx < valLen; ++valIdx) {
                    val[valIdx] = Hitbox.deserialize(buf, pos);
                    pos += Hitbox.computeBytesConsumed(buf, pos);
                }
                if (obj.blockBaseHitboxes.put(key, val) == null) continue;
                throw ProtocolException.duplicateKey("blockBaseHitboxes", key);
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
                int al = VarInt.peek(buf, pos += 4);
                pos += VarInt.length(buf, pos);
                for (int j = 0; j < al; ++j) {
                    pos += Hitbox.computeBytesConsumed(buf, pos);
                }
            }
        }
        return pos - offset;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        int nullBits = 0;
        if (this.blockBaseHitboxes != null) {
            nullBits = (byte)(nullBits | 1);
        }
        buf.writeByte(nullBits);
        buf.writeByte(this.type.getValue());
        buf.writeIntLE(this.maxId);
        if (this.blockBaseHitboxes != null) {
            if (this.blockBaseHitboxes.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("BlockBaseHitboxes", this.blockBaseHitboxes.size(), 4096000);
            }
            VarInt.write(buf, this.blockBaseHitboxes.size());
            for (Map.Entry<Integer, Hitbox[]> e : this.blockBaseHitboxes.entrySet()) {
                buf.writeIntLE(e.getKey());
                VarInt.write(buf, e.getValue().length);
                for (Hitbox arrItem : e.getValue()) {
                    arrItem.serialize(buf);
                }
            }
        }
    }

    @Override
    public int computeSize() {
        int size = 6;
        if (this.blockBaseHitboxes != null) {
            int blockBaseHitboxesSize = 0;
            for (Map.Entry<Integer, Hitbox[]> kvp : this.blockBaseHitboxes.entrySet()) {
                blockBaseHitboxesSize += 4 + VarInt.size(kvp.getValue().length) + kvp.getValue().length * 24;
            }
            size += VarInt.size(this.blockBaseHitboxes.size()) + blockBaseHitboxesSize;
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
            int blockBaseHitboxesCount = VarInt.peek(buffer, pos);
            if (blockBaseHitboxesCount < 0) {
                return ValidationResult.error("Invalid dictionary count for BlockBaseHitboxes");
            }
            if (blockBaseHitboxesCount > 4096000) {
                return ValidationResult.error("BlockBaseHitboxes exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (int i = 0; i < blockBaseHitboxesCount; ++i) {
                if ((pos += 4) > buffer.writerIndex()) {
                    return ValidationResult.error("Buffer overflow reading key");
                }
                int valueArrCount = VarInt.peek(buffer, pos);
                if (valueArrCount < 0) {
                    return ValidationResult.error("Invalid array count for value");
                }
                pos += VarInt.length(buffer, pos);
                for (int valueArrIdx = 0; valueArrIdx < valueArrCount; ++valueArrIdx) {
                    pos += 24;
                }
            }
        }
        return ValidationResult.OK;
    }

    public UpdateBlockHitboxes clone() {
        UpdateBlockHitboxes copy = new UpdateBlockHitboxes();
        copy.type = this.type;
        copy.maxId = this.maxId;
        if (this.blockBaseHitboxes != null) {
            HashMap<Integer, Hitbox[]> m = new HashMap<Integer, Hitbox[]>();
            for (Map.Entry<Integer, Hitbox[]> e : this.blockBaseHitboxes.entrySet()) {
                m.put(e.getKey(), (Hitbox[])Arrays.stream(e.getValue()).map(x -> x.clone()).toArray(Hitbox[]::new));
            }
            copy.blockBaseHitboxes = m;
        }
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UpdateBlockHitboxes)) {
            return false;
        }
        UpdateBlockHitboxes other = (UpdateBlockHitboxes)obj;
        return Objects.equals((Object)this.type, (Object)other.type) && this.maxId == other.maxId && Objects.equals(this.blockBaseHitboxes, other.blockBaseHitboxes);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.type, this.maxId, this.blockBaseHitboxes});
    }
}

