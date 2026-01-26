/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.Modifier;
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

public class ItemUtility {
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 3;
    public static final int VARIABLE_FIELD_COUNT = 2;
    public static final int VARIABLE_BLOCK_START = 11;
    public static final int MAX_SIZE = 1626112021;
    public boolean usable;
    public boolean compatible;
    @Nullable
    public int[] entityStatsToClear;
    @Nullable
    public Map<Integer, Modifier[]> statModifiers;

    public ItemUtility() {
    }

    public ItemUtility(boolean usable, boolean compatible, @Nullable int[] entityStatsToClear, @Nullable Map<Integer, Modifier[]> statModifiers) {
        this.usable = usable;
        this.compatible = compatible;
        this.entityStatsToClear = entityStatsToClear;
        this.statModifiers = statModifiers;
    }

    public ItemUtility(@Nonnull ItemUtility other) {
        this.usable = other.usable;
        this.compatible = other.compatible;
        this.entityStatsToClear = other.entityStatsToClear;
        this.statModifiers = other.statModifiers;
    }

    @Nonnull
    public static ItemUtility deserialize(@Nonnull ByteBuf buf, int offset) {
        int varIntLen;
        ItemUtility obj = new ItemUtility();
        byte nullBits = buf.getByte(offset);
        obj.usable = buf.getByte(offset + 1) != 0;
        boolean bl = obj.compatible = buf.getByte(offset + 2) != 0;
        if ((nullBits & 1) != 0) {
            int varPos0 = offset + 11 + buf.getIntLE(offset + 3);
            int entityStatsToClearCount = VarInt.peek(buf, varPos0);
            if (entityStatsToClearCount < 0) {
                throw ProtocolException.negativeLength("EntityStatsToClear", entityStatsToClearCount);
            }
            if (entityStatsToClearCount > 4096000) {
                throw ProtocolException.arrayTooLong("EntityStatsToClear", entityStatsToClearCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos0);
            if ((long)(varPos0 + varIntLen) + (long)entityStatsToClearCount * 4L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("EntityStatsToClear", varPos0 + varIntLen + entityStatsToClearCount * 4, buf.readableBytes());
            }
            obj.entityStatsToClear = new int[entityStatsToClearCount];
            for (int i = 0; i < entityStatsToClearCount; ++i) {
                obj.entityStatsToClear[i] = buf.getIntLE(varPos0 + varIntLen + i * 4);
            }
        }
        if ((nullBits & 2) != 0) {
            int varPos1 = offset + 11 + buf.getIntLE(offset + 7);
            int statModifiersCount = VarInt.peek(buf, varPos1);
            if (statModifiersCount < 0) {
                throw ProtocolException.negativeLength("StatModifiers", statModifiersCount);
            }
            if (statModifiersCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("StatModifiers", statModifiersCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos1);
            obj.statModifiers = new HashMap<Integer, Modifier[]>(statModifiersCount);
            int dictPos = varPos1 + varIntLen;
            for (int i = 0; i < statModifiersCount; ++i) {
                int key = buf.getIntLE(dictPos);
                int valLen = VarInt.peek(buf, dictPos += 4);
                if (valLen < 0) {
                    throw ProtocolException.negativeLength("val", valLen);
                }
                if (valLen > 64) {
                    throw ProtocolException.arrayTooLong("val", valLen, 64);
                }
                int valVarLen = VarInt.length(buf, dictPos);
                if ((long)(dictPos + valVarLen) + (long)valLen * 6L > (long)buf.readableBytes()) {
                    throw ProtocolException.bufferTooSmall("val", dictPos + valVarLen + valLen * 6, buf.readableBytes());
                }
                dictPos += valVarLen;
                Modifier[] val = new Modifier[valLen];
                for (int valIdx = 0; valIdx < valLen; ++valIdx) {
                    val[valIdx] = Modifier.deserialize(buf, dictPos);
                    dictPos += Modifier.computeBytesConsumed(buf, dictPos);
                }
                if (obj.statModifiers.put(key, val) == null) continue;
                throw ProtocolException.duplicateKey("statModifiers", key);
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        byte nullBits = buf.getByte(offset);
        int maxEnd = 11;
        if ((nullBits & 1) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 3);
            int pos0 = offset + 11 + fieldOffset0;
            int arrLen = VarInt.peek(buf, pos0);
            if ((pos0 += VarInt.length(buf, pos0) + arrLen * 4) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 2) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 7);
            int pos1 = offset + 11 + fieldOffset1;
            int dictLen = VarInt.peek(buf, pos1);
            pos1 += VarInt.length(buf, pos1);
            for (int i = 0; i < dictLen; ++i) {
                int al = VarInt.peek(buf, pos1 += 4);
                pos1 += VarInt.length(buf, pos1);
                for (int j = 0; j < al; ++j) {
                    pos1 += Modifier.computeBytesConsumed(buf, pos1);
                }
            }
            if (pos1 - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        return maxEnd;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        byte nullBits = 0;
        if (this.entityStatsToClear != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.statModifiers != null) {
            nullBits = (byte)(nullBits | 2);
        }
        buf.writeByte(nullBits);
        buf.writeByte(this.usable ? 1 : 0);
        buf.writeByte(this.compatible ? 1 : 0);
        int entityStatsToClearOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int statModifiersOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.entityStatsToClear != null) {
            buf.setIntLE(entityStatsToClearOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.entityStatsToClear.length > 4096000) {
                throw ProtocolException.arrayTooLong("EntityStatsToClear", this.entityStatsToClear.length, 4096000);
            }
            VarInt.write(buf, this.entityStatsToClear.length);
            for (int item : this.entityStatsToClear) {
                buf.writeIntLE(item);
            }
        } else {
            buf.setIntLE(entityStatsToClearOffsetSlot, -1);
        }
        if (this.statModifiers != null) {
            buf.setIntLE(statModifiersOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.statModifiers.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("StatModifiers", this.statModifiers.size(), 4096000);
            }
            VarInt.write(buf, this.statModifiers.size());
            Object object = this.statModifiers.entrySet().iterator();
            while (object.hasNext()) {
                Map.Entry e = (Map.Entry)object.next();
                buf.writeIntLE((Integer)e.getKey());
                VarInt.write(buf, ((Modifier[])e.getValue()).length);
                for (Modifier arrItem : (Modifier[])e.getValue()) {
                    arrItem.serialize(buf);
                }
            }
        } else {
            buf.setIntLE(statModifiersOffsetSlot, -1);
        }
    }

    public int computeSize() {
        int size = 11;
        if (this.entityStatsToClear != null) {
            size += VarInt.size(this.entityStatsToClear.length) + this.entityStatsToClear.length * 4;
        }
        if (this.statModifiers != null) {
            int statModifiersSize = 0;
            for (Map.Entry<Integer, Modifier[]> kvp : this.statModifiers.entrySet()) {
                statModifiersSize += 4 + VarInt.size(kvp.getValue().length) + kvp.getValue().length * 6;
            }
            size += VarInt.size(this.statModifiers.size()) + statModifiersSize;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int pos;
        if (buffer.readableBytes() - offset < 11) {
            return ValidationResult.error("Buffer too small: expected at least 11 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 1) != 0) {
            int entityStatsToClearOffset = buffer.getIntLE(offset + 3);
            if (entityStatsToClearOffset < 0) {
                return ValidationResult.error("Invalid offset for EntityStatsToClear");
            }
            pos = offset + 11 + entityStatsToClearOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for EntityStatsToClear");
            }
            int entityStatsToClearCount = VarInt.peek(buffer, pos);
            if (entityStatsToClearCount < 0) {
                return ValidationResult.error("Invalid array count for EntityStatsToClear");
            }
            if (entityStatsToClearCount > 4096000) {
                return ValidationResult.error("EntityStatsToClear exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += entityStatsToClearCount * 4) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading EntityStatsToClear");
            }
        }
        if ((nullBits & 2) != 0) {
            int statModifiersOffset = buffer.getIntLE(offset + 7);
            if (statModifiersOffset < 0) {
                return ValidationResult.error("Invalid offset for StatModifiers");
            }
            pos = offset + 11 + statModifiersOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for StatModifiers");
            }
            int statModifiersCount = VarInt.peek(buffer, pos);
            if (statModifiersCount < 0) {
                return ValidationResult.error("Invalid dictionary count for StatModifiers");
            }
            if (statModifiersCount > 4096000) {
                return ValidationResult.error("StatModifiers exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (int i = 0; i < statModifiersCount; ++i) {
                if ((pos += 4) > buffer.writerIndex()) {
                    return ValidationResult.error("Buffer overflow reading key");
                }
                int valueArrCount = VarInt.peek(buffer, pos);
                if (valueArrCount < 0) {
                    return ValidationResult.error("Invalid array count for value");
                }
                pos += VarInt.length(buffer, pos);
                for (int valueArrIdx = 0; valueArrIdx < valueArrCount; ++valueArrIdx) {
                    pos += 6;
                }
            }
        }
        return ValidationResult.OK;
    }

    public ItemUtility clone() {
        ItemUtility copy = new ItemUtility();
        copy.usable = this.usable;
        copy.compatible = this.compatible;
        int[] nArray = copy.entityStatsToClear = this.entityStatsToClear != null ? Arrays.copyOf(this.entityStatsToClear, this.entityStatsToClear.length) : null;
        if (this.statModifiers != null) {
            HashMap<Integer, Modifier[]> m = new HashMap<Integer, Modifier[]>();
            for (Map.Entry<Integer, Modifier[]> e : this.statModifiers.entrySet()) {
                m.put(e.getKey(), (Modifier[])Arrays.stream(e.getValue()).map(x -> x.clone()).toArray(Modifier[]::new));
            }
            copy.statModifiers = m;
        }
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ItemUtility)) {
            return false;
        }
        ItemUtility other = (ItemUtility)obj;
        return this.usable == other.usable && this.compatible == other.compatible && Arrays.equals(this.entityStatsToClear, other.entityStatsToClear) && Objects.equals(this.statModifiers, other.statModifiers);
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + Boolean.hashCode(this.usable);
        result = 31 * result + Boolean.hashCode(this.compatible);
        result = 31 * result + Arrays.hashCode(this.entityStatsToClear);
        result = 31 * result + Objects.hashCode(this.statModifiers);
        return result;
    }
}

