/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.ComponentUpdate;
import com.hypixel.hytale.protocol.EntityStatUpdate;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

public class EntityStatsUpdate
extends ComponentUpdate {
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 0;
    public static final int VARIABLE_FIELD_COUNT = 1;
    public static final int VARIABLE_BLOCK_START = 0;
    public static final int MAX_SIZE = 0x64000000;
    @Nonnull
    public Map<Integer, EntityStatUpdate[]> entityStatUpdates = new HashMap<Integer, EntityStatUpdate[]>();

    public EntityStatsUpdate() {
    }

    public EntityStatsUpdate(@Nonnull Map<Integer, EntityStatUpdate[]> entityStatUpdates) {
        this.entityStatUpdates = entityStatUpdates;
    }

    public EntityStatsUpdate(@Nonnull EntityStatsUpdate other) {
        this.entityStatUpdates = other.entityStatUpdates;
    }

    @Nonnull
    public static EntityStatsUpdate deserialize(@Nonnull ByteBuf buf, int offset) {
        EntityStatsUpdate obj = new EntityStatsUpdate();
        int pos = offset + 0;
        int entityStatUpdatesCount = VarInt.peek(buf, pos);
        if (entityStatUpdatesCount < 0) {
            throw ProtocolException.negativeLength("EntityStatUpdates", entityStatUpdatesCount);
        }
        if (entityStatUpdatesCount > 4096000) {
            throw ProtocolException.dictionaryTooLarge("EntityStatUpdates", entityStatUpdatesCount, 4096000);
        }
        pos += VarInt.size(entityStatUpdatesCount);
        obj.entityStatUpdates = new HashMap<Integer, EntityStatUpdate[]>(entityStatUpdatesCount);
        for (int i = 0; i < entityStatUpdatesCount; ++i) {
            int key = buf.getIntLE(pos);
            int valLen = VarInt.peek(buf, pos += 4);
            if (valLen < 0) {
                throw ProtocolException.negativeLength("val", valLen);
            }
            if (valLen > 64) {
                throw ProtocolException.arrayTooLong("val", valLen, 64);
            }
            int valVarLen = VarInt.length(buf, pos);
            if ((long)(pos + valVarLen) + (long)valLen * 13L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("val", pos + valVarLen + valLen * 13, buf.readableBytes());
            }
            pos += valVarLen;
            EntityStatUpdate[] val = new EntityStatUpdate[valLen];
            for (int valIdx = 0; valIdx < valLen; ++valIdx) {
                val[valIdx] = EntityStatUpdate.deserialize(buf, pos);
                pos += EntityStatUpdate.computeBytesConsumed(buf, pos);
            }
            if (obj.entityStatUpdates.put(key, val) == null) continue;
            throw ProtocolException.duplicateKey("entityStatUpdates", key);
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int pos = offset + 0;
        int dictLen = VarInt.peek(buf, pos);
        pos += VarInt.length(buf, pos);
        for (int i = 0; i < dictLen; ++i) {
            int al = VarInt.peek(buf, pos += 4);
            pos += VarInt.length(buf, pos);
            for (int j = 0; j < al; ++j) {
                pos += EntityStatUpdate.computeBytesConsumed(buf, pos);
            }
        }
        return pos - offset;
    }

    @Override
    public int serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        if (this.entityStatUpdates.size() > 4096000) {
            throw ProtocolException.dictionaryTooLarge("EntityStatUpdates", this.entityStatUpdates.size(), 4096000);
        }
        VarInt.write(buf, this.entityStatUpdates.size());
        for (Map.Entry<Integer, EntityStatUpdate[]> e : this.entityStatUpdates.entrySet()) {
            buf.writeIntLE(e.getKey());
            VarInt.write(buf, e.getValue().length);
            for (EntityStatUpdate arrItem : e.getValue()) {
                arrItem.serialize(buf);
            }
        }
        return buf.writerIndex() - startPos;
    }

    @Override
    public int computeSize() {
        int size = 0;
        int entityStatUpdatesSize = 0;
        for (Map.Entry<Integer, EntityStatUpdate[]> kvp : this.entityStatUpdates.entrySet()) {
            entityStatUpdatesSize += 4 + VarInt.size(kvp.getValue().length) + Arrays.stream(kvp.getValue()).mapToInt(inner -> inner.computeSize()).sum();
        }
        return size += VarInt.size(this.entityStatUpdates.size()) + entityStatUpdatesSize;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 0) {
            return ValidationResult.error("Buffer too small: expected at least 0 bytes");
        }
        int pos = offset + 0;
        int entityStatUpdatesCount = VarInt.peek(buffer, pos);
        if (entityStatUpdatesCount < 0) {
            return ValidationResult.error("Invalid dictionary count for EntityStatUpdates");
        }
        if (entityStatUpdatesCount > 4096000) {
            return ValidationResult.error("EntityStatUpdates exceeds max length 4096000");
        }
        pos += VarInt.length(buffer, pos);
        for (int i = 0; i < entityStatUpdatesCount; ++i) {
            if ((pos += 4) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading key");
            }
            int valueArrCount = VarInt.peek(buffer, pos);
            if (valueArrCount < 0) {
                return ValidationResult.error("Invalid array count for value");
            }
            pos += VarInt.length(buffer, pos);
            for (int valueArrIdx = 0; valueArrIdx < valueArrCount; ++valueArrIdx) {
                pos += EntityStatUpdate.computeBytesConsumed(buffer, pos);
            }
        }
        return ValidationResult.OK;
    }

    public EntityStatsUpdate clone() {
        EntityStatsUpdate copy = new EntityStatsUpdate();
        HashMap<Integer, EntityStatUpdate[]> m = new HashMap<Integer, EntityStatUpdate[]>();
        for (Map.Entry<Integer, EntityStatUpdate[]> e : this.entityStatUpdates.entrySet()) {
            m.put(e.getKey(), (EntityStatUpdate[])Arrays.stream(e.getValue()).map(x -> x.clone()).toArray(EntityStatUpdate[]::new));
        }
        copy.entityStatUpdates = m;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof EntityStatsUpdate)) {
            return false;
        }
        EntityStatsUpdate other = (EntityStatsUpdate)obj;
        return Objects.equals(this.entityStatUpdates, other.entityStatUpdates);
    }

    public int hashCode() {
        return Objects.hash(this.entityStatUpdates);
    }
}

