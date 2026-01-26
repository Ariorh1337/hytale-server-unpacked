/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.Cosmetic;
import com.hypixel.hytale.protocol.ItemArmorSlot;
import com.hypixel.hytale.protocol.Modifier;
import com.hypixel.hytale.protocol.io.PacketIO;
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

public class ItemArmor {
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 10;
    public static final int VARIABLE_FIELD_COUNT = 5;
    public static final int VARIABLE_BLOCK_START = 30;
    public static final int MAX_SIZE = 0x64000000;
    @Nonnull
    public ItemArmorSlot armorSlot = ItemArmorSlot.Head;
    @Nullable
    public Cosmetic[] cosmeticsToHide;
    @Nullable
    public Map<Integer, Modifier[]> statModifiers;
    public double baseDamageResistance;
    @Nullable
    public Map<String, Modifier[]> damageResistance;
    @Nullable
    public Map<String, Modifier[]> damageEnhancement;
    @Nullable
    public Map<String, Modifier[]> damageClassEnhancement;

    public ItemArmor() {
    }

    public ItemArmor(@Nonnull ItemArmorSlot armorSlot, @Nullable Cosmetic[] cosmeticsToHide, @Nullable Map<Integer, Modifier[]> statModifiers, double baseDamageResistance, @Nullable Map<String, Modifier[]> damageResistance, @Nullable Map<String, Modifier[]> damageEnhancement, @Nullable Map<String, Modifier[]> damageClassEnhancement) {
        this.armorSlot = armorSlot;
        this.cosmeticsToHide = cosmeticsToHide;
        this.statModifiers = statModifiers;
        this.baseDamageResistance = baseDamageResistance;
        this.damageResistance = damageResistance;
        this.damageEnhancement = damageEnhancement;
        this.damageClassEnhancement = damageClassEnhancement;
    }

    public ItemArmor(@Nonnull ItemArmor other) {
        this.armorSlot = other.armorSlot;
        this.cosmeticsToHide = other.cosmeticsToHide;
        this.statModifiers = other.statModifiers;
        this.baseDamageResistance = other.baseDamageResistance;
        this.damageResistance = other.damageResistance;
        this.damageEnhancement = other.damageEnhancement;
        this.damageClassEnhancement = other.damageClassEnhancement;
    }

    @Nonnull
    public static ItemArmor deserialize(@Nonnull ByteBuf buf, int offset) {
        int valIdx;
        Modifier[] val;
        int valVarLen;
        int keyVarLen;
        int keyLen;
        int dictPos;
        int i;
        int varIntLen;
        ItemArmor obj = new ItemArmor();
        byte nullBits = buf.getByte(offset);
        obj.armorSlot = ItemArmorSlot.fromValue(buf.getByte(offset + 1));
        obj.baseDamageResistance = buf.getDoubleLE(offset + 2);
        if ((nullBits & 1) != 0) {
            int varPos0 = offset + 30 + buf.getIntLE(offset + 10);
            int cosmeticsToHideCount = VarInt.peek(buf, varPos0);
            if (cosmeticsToHideCount < 0) {
                throw ProtocolException.negativeLength("CosmeticsToHide", cosmeticsToHideCount);
            }
            if (cosmeticsToHideCount > 4096000) {
                throw ProtocolException.arrayTooLong("CosmeticsToHide", cosmeticsToHideCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos0);
            if ((long)(varPos0 + varIntLen) + (long)cosmeticsToHideCount * 1L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("CosmeticsToHide", varPos0 + varIntLen + cosmeticsToHideCount * 1, buf.readableBytes());
            }
            obj.cosmeticsToHide = new Cosmetic[cosmeticsToHideCount];
            int elemPos = varPos0 + varIntLen;
            for (i = 0; i < cosmeticsToHideCount; ++i) {
                obj.cosmeticsToHide[i] = Cosmetic.fromValue(buf.getByte(elemPos));
                ++elemPos;
            }
        }
        if ((nullBits & 2) != 0) {
            int varPos1 = offset + 30 + buf.getIntLE(offset + 14);
            int statModifiersCount = VarInt.peek(buf, varPos1);
            if (statModifiersCount < 0) {
                throw ProtocolException.negativeLength("StatModifiers", statModifiersCount);
            }
            if (statModifiersCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("StatModifiers", statModifiersCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos1);
            obj.statModifiers = new HashMap<Integer, Modifier[]>(statModifiersCount);
            dictPos = varPos1 + varIntLen;
            for (i = 0; i < statModifiersCount; ++i) {
                int key = buf.getIntLE(dictPos);
                int valLen = VarInt.peek(buf, dictPos += 4);
                if (valLen < 0) {
                    throw ProtocolException.negativeLength("val", valLen);
                }
                if (valLen > 64) {
                    throw ProtocolException.arrayTooLong("val", valLen, 64);
                }
                int valVarLen2 = VarInt.length(buf, dictPos);
                if ((long)(dictPos + valVarLen2) + (long)valLen * 6L > (long)buf.readableBytes()) {
                    throw ProtocolException.bufferTooSmall("val", dictPos + valVarLen2 + valLen * 6, buf.readableBytes());
                }
                dictPos += valVarLen2;
                Modifier[] val2 = new Modifier[valLen];
                for (int valIdx2 = 0; valIdx2 < valLen; ++valIdx2) {
                    val2[valIdx2] = Modifier.deserialize(buf, dictPos);
                    dictPos += Modifier.computeBytesConsumed(buf, dictPos);
                }
                if (obj.statModifiers.put(key, val2) == null) continue;
                throw ProtocolException.duplicateKey("statModifiers", key);
            }
        }
        if ((nullBits & 4) != 0) {
            int varPos2 = offset + 30 + buf.getIntLE(offset + 18);
            int damageResistanceCount = VarInt.peek(buf, varPos2);
            if (damageResistanceCount < 0) {
                throw ProtocolException.negativeLength("DamageResistance", damageResistanceCount);
            }
            if (damageResistanceCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("DamageResistance", damageResistanceCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos2);
            obj.damageResistance = new HashMap<String, Modifier[]>(damageResistanceCount);
            dictPos = varPos2 + varIntLen;
            for (i = 0; i < damageResistanceCount; ++i) {
                keyLen = VarInt.peek(buf, dictPos);
                if (keyLen < 0) {
                    throw ProtocolException.negativeLength("key", keyLen);
                }
                if (keyLen > 4096000) {
                    throw ProtocolException.stringTooLong("key", keyLen, 4096000);
                }
                keyVarLen = VarInt.length(buf, dictPos);
                String key = PacketIO.readVarString(buf, dictPos);
                int valLen = VarInt.peek(buf, dictPos += keyVarLen + keyLen);
                if (valLen < 0) {
                    throw ProtocolException.negativeLength("val", valLen);
                }
                if (valLen > 64) {
                    throw ProtocolException.arrayTooLong("val", valLen, 64);
                }
                valVarLen = VarInt.length(buf, dictPos);
                if ((long)(dictPos + valVarLen) + (long)valLen * 6L > (long)buf.readableBytes()) {
                    throw ProtocolException.bufferTooSmall("val", dictPos + valVarLen + valLen * 6, buf.readableBytes());
                }
                dictPos += valVarLen;
                val = new Modifier[valLen];
                for (valIdx = 0; valIdx < valLen; ++valIdx) {
                    val[valIdx] = Modifier.deserialize(buf, dictPos);
                    dictPos += Modifier.computeBytesConsumed(buf, dictPos);
                }
                if (obj.damageResistance.put(key, val) == null) continue;
                throw ProtocolException.duplicateKey("damageResistance", key);
            }
        }
        if ((nullBits & 8) != 0) {
            int varPos3 = offset + 30 + buf.getIntLE(offset + 22);
            int damageEnhancementCount = VarInt.peek(buf, varPos3);
            if (damageEnhancementCount < 0) {
                throw ProtocolException.negativeLength("DamageEnhancement", damageEnhancementCount);
            }
            if (damageEnhancementCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("DamageEnhancement", damageEnhancementCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos3);
            obj.damageEnhancement = new HashMap<String, Modifier[]>(damageEnhancementCount);
            dictPos = varPos3 + varIntLen;
            for (i = 0; i < damageEnhancementCount; ++i) {
                keyLen = VarInt.peek(buf, dictPos);
                if (keyLen < 0) {
                    throw ProtocolException.negativeLength("key", keyLen);
                }
                if (keyLen > 4096000) {
                    throw ProtocolException.stringTooLong("key", keyLen, 4096000);
                }
                keyVarLen = VarInt.length(buf, dictPos);
                String key = PacketIO.readVarString(buf, dictPos);
                int valLen = VarInt.peek(buf, dictPos += keyVarLen + keyLen);
                if (valLen < 0) {
                    throw ProtocolException.negativeLength("val", valLen);
                }
                if (valLen > 64) {
                    throw ProtocolException.arrayTooLong("val", valLen, 64);
                }
                valVarLen = VarInt.length(buf, dictPos);
                if ((long)(dictPos + valVarLen) + (long)valLen * 6L > (long)buf.readableBytes()) {
                    throw ProtocolException.bufferTooSmall("val", dictPos + valVarLen + valLen * 6, buf.readableBytes());
                }
                dictPos += valVarLen;
                val = new Modifier[valLen];
                for (valIdx = 0; valIdx < valLen; ++valIdx) {
                    val[valIdx] = Modifier.deserialize(buf, dictPos);
                    dictPos += Modifier.computeBytesConsumed(buf, dictPos);
                }
                if (obj.damageEnhancement.put(key, val) == null) continue;
                throw ProtocolException.duplicateKey("damageEnhancement", key);
            }
        }
        if ((nullBits & 0x10) != 0) {
            int varPos4 = offset + 30 + buf.getIntLE(offset + 26);
            int damageClassEnhancementCount = VarInt.peek(buf, varPos4);
            if (damageClassEnhancementCount < 0) {
                throw ProtocolException.negativeLength("DamageClassEnhancement", damageClassEnhancementCount);
            }
            if (damageClassEnhancementCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("DamageClassEnhancement", damageClassEnhancementCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos4);
            obj.damageClassEnhancement = new HashMap<String, Modifier[]>(damageClassEnhancementCount);
            dictPos = varPos4 + varIntLen;
            for (i = 0; i < damageClassEnhancementCount; ++i) {
                keyLen = VarInt.peek(buf, dictPos);
                if (keyLen < 0) {
                    throw ProtocolException.negativeLength("key", keyLen);
                }
                if (keyLen > 4096000) {
                    throw ProtocolException.stringTooLong("key", keyLen, 4096000);
                }
                keyVarLen = VarInt.length(buf, dictPos);
                String key = PacketIO.readVarString(buf, dictPos);
                int valLen = VarInt.peek(buf, dictPos += keyVarLen + keyLen);
                if (valLen < 0) {
                    throw ProtocolException.negativeLength("val", valLen);
                }
                if (valLen > 64) {
                    throw ProtocolException.arrayTooLong("val", valLen, 64);
                }
                valVarLen = VarInt.length(buf, dictPos);
                if ((long)(dictPos + valVarLen) + (long)valLen * 6L > (long)buf.readableBytes()) {
                    throw ProtocolException.bufferTooSmall("val", dictPos + valVarLen + valLen * 6, buf.readableBytes());
                }
                dictPos += valVarLen;
                val = new Modifier[valLen];
                for (valIdx = 0; valIdx < valLen; ++valIdx) {
                    val[valIdx] = Modifier.deserialize(buf, dictPos);
                    dictPos += Modifier.computeBytesConsumed(buf, dictPos);
                }
                if (obj.damageClassEnhancement.put(key, val) == null) continue;
                throw ProtocolException.duplicateKey("damageClassEnhancement", key);
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int sl;
        int j;
        int al;
        int i;
        int dictLen;
        byte nullBits = buf.getByte(offset);
        int maxEnd = 30;
        if ((nullBits & 1) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 10);
            int pos0 = offset + 30 + fieldOffset0;
            int arrLen = VarInt.peek(buf, pos0);
            if ((pos0 += VarInt.length(buf, pos0) + arrLen * 1) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 2) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 14);
            int pos1 = offset + 30 + fieldOffset1;
            dictLen = VarInt.peek(buf, pos1);
            pos1 += VarInt.length(buf, pos1);
            for (i = 0; i < dictLen; ++i) {
                al = VarInt.peek(buf, pos1 += 4);
                pos1 += VarInt.length(buf, pos1);
                for (j = 0; j < al; ++j) {
                    pos1 += Modifier.computeBytesConsumed(buf, pos1);
                }
            }
            if (pos1 - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        if ((nullBits & 4) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 18);
            int pos2 = offset + 30 + fieldOffset2;
            dictLen = VarInt.peek(buf, pos2);
            pos2 += VarInt.length(buf, pos2);
            for (i = 0; i < dictLen; ++i) {
                sl = VarInt.peek(buf, pos2);
                pos2 += VarInt.length(buf, pos2) + sl;
                al = VarInt.peek(buf, pos2);
                pos2 += VarInt.length(buf, pos2);
                for (j = 0; j < al; ++j) {
                    pos2 += Modifier.computeBytesConsumed(buf, pos2);
                }
            }
            if (pos2 - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        if ((nullBits & 8) != 0) {
            int fieldOffset3 = buf.getIntLE(offset + 22);
            int pos3 = offset + 30 + fieldOffset3;
            dictLen = VarInt.peek(buf, pos3);
            pos3 += VarInt.length(buf, pos3);
            for (i = 0; i < dictLen; ++i) {
                sl = VarInt.peek(buf, pos3);
                pos3 += VarInt.length(buf, pos3) + sl;
                al = VarInt.peek(buf, pos3);
                pos3 += VarInt.length(buf, pos3);
                for (j = 0; j < al; ++j) {
                    pos3 += Modifier.computeBytesConsumed(buf, pos3);
                }
            }
            if (pos3 - offset > maxEnd) {
                maxEnd = pos3 - offset;
            }
        }
        if ((nullBits & 0x10) != 0) {
            int fieldOffset4 = buf.getIntLE(offset + 26);
            int pos4 = offset + 30 + fieldOffset4;
            dictLen = VarInt.peek(buf, pos4);
            pos4 += VarInt.length(buf, pos4);
            for (i = 0; i < dictLen; ++i) {
                sl = VarInt.peek(buf, pos4);
                pos4 += VarInt.length(buf, pos4) + sl;
                al = VarInt.peek(buf, pos4);
                pos4 += VarInt.length(buf, pos4);
                for (j = 0; j < al; ++j) {
                    pos4 += Modifier.computeBytesConsumed(buf, pos4);
                }
            }
            if (pos4 - offset > maxEnd) {
                maxEnd = pos4 - offset;
            }
        }
        return maxEnd;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        Modifier arrItem2;
        int n;
        int startPos = buf.writerIndex();
        byte nullBits = 0;
        if (this.cosmeticsToHide != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.statModifiers != null) {
            nullBits = (byte)(nullBits | 2);
        }
        if (this.damageResistance != null) {
            nullBits = (byte)(nullBits | 4);
        }
        if (this.damageEnhancement != null) {
            nullBits = (byte)(nullBits | 8);
        }
        if (this.damageClassEnhancement != null) {
            nullBits = (byte)(nullBits | 0x10);
        }
        buf.writeByte(nullBits);
        buf.writeByte(this.armorSlot.getValue());
        buf.writeDoubleLE(this.baseDamageResistance);
        int cosmeticsToHideOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int statModifiersOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int damageResistanceOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int damageEnhancementOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int damageClassEnhancementOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.cosmeticsToHide != null) {
            buf.setIntLE(cosmeticsToHideOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.cosmeticsToHide.length > 4096000) {
                throw ProtocolException.arrayTooLong("CosmeticsToHide", this.cosmeticsToHide.length, 4096000);
            }
            VarInt.write(buf, this.cosmeticsToHide.length);
            for (Cosmetic item : this.cosmeticsToHide) {
                buf.writeByte(item.getValue());
            }
        } else {
            buf.setIntLE(cosmeticsToHideOffsetSlot, -1);
        }
        if (this.statModifiers != null) {
            buf.setIntLE(statModifiersOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.statModifiers.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("StatModifiers", this.statModifiers.size(), 4096000);
            }
            VarInt.write(buf, this.statModifiers.size());
            for (Map.Entry entry : this.statModifiers.entrySet()) {
                buf.writeIntLE((Integer)entry.getKey());
                VarInt.write(buf, ((Modifier[])entry.getValue()).length);
                Modifier[] modifierArray = (Modifier[])entry.getValue();
                int n2 = modifierArray.length;
                for (n = 0; n < n2; ++n) {
                    Modifier arrItem2 = modifierArray[n];
                    arrItem2.serialize(buf);
                }
            }
        } else {
            buf.setIntLE(statModifiersOffsetSlot, -1);
        }
        if (this.damageResistance != null) {
            buf.setIntLE(damageResistanceOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.damageResistance.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("DamageResistance", this.damageResistance.size(), 4096000);
            }
            VarInt.write(buf, this.damageResistance.size());
            for (Map.Entry entry : this.damageResistance.entrySet()) {
                PacketIO.writeVarString(buf, (String)entry.getKey(), 4096000);
                VarInt.write(buf, ((Modifier[])entry.getValue()).length);
                Modifier[] modifierArray = (Modifier[])entry.getValue();
                int n3 = modifierArray.length;
                for (n = 0; n < n3; ++n) {
                    arrItem2 = modifierArray[n];
                    arrItem2.serialize(buf);
                }
            }
        } else {
            buf.setIntLE(damageResistanceOffsetSlot, -1);
        }
        if (this.damageEnhancement != null) {
            buf.setIntLE(damageEnhancementOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.damageEnhancement.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("DamageEnhancement", this.damageEnhancement.size(), 4096000);
            }
            VarInt.write(buf, this.damageEnhancement.size());
            for (Map.Entry entry : this.damageEnhancement.entrySet()) {
                PacketIO.writeVarString(buf, (String)entry.getKey(), 4096000);
                VarInt.write(buf, ((Modifier[])entry.getValue()).length);
                Modifier[] modifierArray = (Modifier[])entry.getValue();
                int n4 = modifierArray.length;
                for (n = 0; n < n4; ++n) {
                    arrItem2 = modifierArray[n];
                    arrItem2.serialize(buf);
                }
            }
        } else {
            buf.setIntLE(damageEnhancementOffsetSlot, -1);
        }
        if (this.damageClassEnhancement != null) {
            buf.setIntLE(damageClassEnhancementOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.damageClassEnhancement.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("DamageClassEnhancement", this.damageClassEnhancement.size(), 4096000);
            }
            VarInt.write(buf, this.damageClassEnhancement.size());
            for (Map.Entry entry : this.damageClassEnhancement.entrySet()) {
                PacketIO.writeVarString(buf, (String)entry.getKey(), 4096000);
                VarInt.write(buf, ((Modifier[])entry.getValue()).length);
                for (Modifier arrItem2 : (Modifier[])entry.getValue()) {
                    arrItem2.serialize(buf);
                }
            }
        } else {
            buf.setIntLE(damageClassEnhancementOffsetSlot, -1);
        }
    }

    public int computeSize() {
        int size = 30;
        if (this.cosmeticsToHide != null) {
            size += VarInt.size(this.cosmeticsToHide.length) + this.cosmeticsToHide.length * 1;
        }
        if (this.statModifiers != null) {
            int statModifiersSize = 0;
            for (Map.Entry<Object, Modifier[]> entry : this.statModifiers.entrySet()) {
                statModifiersSize += 4 + VarInt.size(entry.getValue().length) + entry.getValue().length * 6;
            }
            size += VarInt.size(this.statModifiers.size()) + statModifiersSize;
        }
        if (this.damageResistance != null) {
            int damageResistanceSize = 0;
            for (Map.Entry<Object, Modifier[]> entry : this.damageResistance.entrySet()) {
                damageResistanceSize += PacketIO.stringSize((String)entry.getKey()) + VarInt.size(entry.getValue().length) + entry.getValue().length * 6;
            }
            size += VarInt.size(this.damageResistance.size()) + damageResistanceSize;
        }
        if (this.damageEnhancement != null) {
            int damageEnhancementSize = 0;
            for (Map.Entry<Object, Modifier[]> entry : this.damageEnhancement.entrySet()) {
                damageEnhancementSize += PacketIO.stringSize((String)entry.getKey()) + VarInt.size(entry.getValue().length) + entry.getValue().length * 6;
            }
            size += VarInt.size(this.damageEnhancement.size()) + damageEnhancementSize;
        }
        if (this.damageClassEnhancement != null) {
            int damageClassEnhancementSize = 0;
            for (Map.Entry<Object, Modifier[]> entry : this.damageClassEnhancement.entrySet()) {
                damageClassEnhancementSize += PacketIO.stringSize((String)entry.getKey()) + VarInt.size(entry.getValue().length) + entry.getValue().length * 6;
            }
            size += VarInt.size(this.damageClassEnhancement.size()) + damageClassEnhancementSize;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int valueArrIdx;
        int valueArrCount;
        int keyLen;
        int i;
        int pos;
        if (buffer.readableBytes() - offset < 30) {
            return ValidationResult.error("Buffer too small: expected at least 30 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 1) != 0) {
            int cosmeticsToHideOffset = buffer.getIntLE(offset + 10);
            if (cosmeticsToHideOffset < 0) {
                return ValidationResult.error("Invalid offset for CosmeticsToHide");
            }
            pos = offset + 30 + cosmeticsToHideOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for CosmeticsToHide");
            }
            int cosmeticsToHideCount = VarInt.peek(buffer, pos);
            if (cosmeticsToHideCount < 0) {
                return ValidationResult.error("Invalid array count for CosmeticsToHide");
            }
            if (cosmeticsToHideCount > 4096000) {
                return ValidationResult.error("CosmeticsToHide exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += cosmeticsToHideCount * 1) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading CosmeticsToHide");
            }
        }
        if ((nullBits & 2) != 0) {
            int statModifiersOffset = buffer.getIntLE(offset + 14);
            if (statModifiersOffset < 0) {
                return ValidationResult.error("Invalid offset for StatModifiers");
            }
            pos = offset + 30 + statModifiersOffset;
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
            for (i = 0; i < statModifiersCount; ++i) {
                if ((pos += 4) > buffer.writerIndex()) {
                    return ValidationResult.error("Buffer overflow reading key");
                }
                int valueArrCount2 = VarInt.peek(buffer, pos);
                if (valueArrCount2 < 0) {
                    return ValidationResult.error("Invalid array count for value");
                }
                pos += VarInt.length(buffer, pos);
                for (int valueArrIdx2 = 0; valueArrIdx2 < valueArrCount2; ++valueArrIdx2) {
                    pos += 6;
                }
            }
        }
        if ((nullBits & 4) != 0) {
            int damageResistanceOffset = buffer.getIntLE(offset + 18);
            if (damageResistanceOffset < 0) {
                return ValidationResult.error("Invalid offset for DamageResistance");
            }
            pos = offset + 30 + damageResistanceOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for DamageResistance");
            }
            int damageResistanceCount = VarInt.peek(buffer, pos);
            if (damageResistanceCount < 0) {
                return ValidationResult.error("Invalid dictionary count for DamageResistance");
            }
            if (damageResistanceCount > 4096000) {
                return ValidationResult.error("DamageResistance exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < damageResistanceCount; ++i) {
                keyLen = VarInt.peek(buffer, pos);
                if (keyLen < 0) {
                    return ValidationResult.error("Invalid string length for key");
                }
                if (keyLen > 4096000) {
                    return ValidationResult.error("key exceeds max length 4096000");
                }
                pos += VarInt.length(buffer, pos);
                if ((pos += keyLen) > buffer.writerIndex()) {
                    return ValidationResult.error("Buffer overflow reading key");
                }
                valueArrCount = VarInt.peek(buffer, pos);
                if (valueArrCount < 0) {
                    return ValidationResult.error("Invalid array count for value");
                }
                pos += VarInt.length(buffer, pos);
                for (valueArrIdx = 0; valueArrIdx < valueArrCount; ++valueArrIdx) {
                    pos += 6;
                }
            }
        }
        if ((nullBits & 8) != 0) {
            int damageEnhancementOffset = buffer.getIntLE(offset + 22);
            if (damageEnhancementOffset < 0) {
                return ValidationResult.error("Invalid offset for DamageEnhancement");
            }
            pos = offset + 30 + damageEnhancementOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for DamageEnhancement");
            }
            int damageEnhancementCount = VarInt.peek(buffer, pos);
            if (damageEnhancementCount < 0) {
                return ValidationResult.error("Invalid dictionary count for DamageEnhancement");
            }
            if (damageEnhancementCount > 4096000) {
                return ValidationResult.error("DamageEnhancement exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < damageEnhancementCount; ++i) {
                keyLen = VarInt.peek(buffer, pos);
                if (keyLen < 0) {
                    return ValidationResult.error("Invalid string length for key");
                }
                if (keyLen > 4096000) {
                    return ValidationResult.error("key exceeds max length 4096000");
                }
                pos += VarInt.length(buffer, pos);
                if ((pos += keyLen) > buffer.writerIndex()) {
                    return ValidationResult.error("Buffer overflow reading key");
                }
                valueArrCount = VarInt.peek(buffer, pos);
                if (valueArrCount < 0) {
                    return ValidationResult.error("Invalid array count for value");
                }
                pos += VarInt.length(buffer, pos);
                for (valueArrIdx = 0; valueArrIdx < valueArrCount; ++valueArrIdx) {
                    pos += 6;
                }
            }
        }
        if ((nullBits & 0x10) != 0) {
            int damageClassEnhancementOffset = buffer.getIntLE(offset + 26);
            if (damageClassEnhancementOffset < 0) {
                return ValidationResult.error("Invalid offset for DamageClassEnhancement");
            }
            pos = offset + 30 + damageClassEnhancementOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for DamageClassEnhancement");
            }
            int damageClassEnhancementCount = VarInt.peek(buffer, pos);
            if (damageClassEnhancementCount < 0) {
                return ValidationResult.error("Invalid dictionary count for DamageClassEnhancement");
            }
            if (damageClassEnhancementCount > 4096000) {
                return ValidationResult.error("DamageClassEnhancement exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < damageClassEnhancementCount; ++i) {
                keyLen = VarInt.peek(buffer, pos);
                if (keyLen < 0) {
                    return ValidationResult.error("Invalid string length for key");
                }
                if (keyLen > 4096000) {
                    return ValidationResult.error("key exceeds max length 4096000");
                }
                pos += VarInt.length(buffer, pos);
                if ((pos += keyLen) > buffer.writerIndex()) {
                    return ValidationResult.error("Buffer overflow reading key");
                }
                valueArrCount = VarInt.peek(buffer, pos);
                if (valueArrCount < 0) {
                    return ValidationResult.error("Invalid array count for value");
                }
                pos += VarInt.length(buffer, pos);
                for (valueArrIdx = 0; valueArrIdx < valueArrCount; ++valueArrIdx) {
                    pos += 6;
                }
            }
        }
        return ValidationResult.OK;
    }

    public ItemArmor clone() {
        HashMap<Integer, Object> m;
        ItemArmor copy = new ItemArmor();
        copy.armorSlot = this.armorSlot;
        Cosmetic[] cosmeticArray = copy.cosmeticsToHide = this.cosmeticsToHide != null ? Arrays.copyOf(this.cosmeticsToHide, this.cosmeticsToHide.length) : null;
        if (this.statModifiers != null) {
            m = new HashMap<Integer, Modifier[]>();
            for (Map.Entry<Integer, Modifier[]> entry : this.statModifiers.entrySet()) {
                m.put(entry.getKey(), (Modifier[])Arrays.stream(entry.getValue()).map(x -> x.clone()).toArray(Modifier[]::new));
            }
            copy.statModifiers = m;
        }
        copy.baseDamageResistance = this.baseDamageResistance;
        if (this.damageResistance != null) {
            m = new HashMap();
            for (Map.Entry<Object, Modifier[]> entry : this.damageResistance.entrySet()) {
                m.put((Integer)((Object)((String)entry.getKey())), (Modifier[])Arrays.stream(entry.getValue()).map(x -> x.clone()).toArray(Modifier[]::new));
            }
            copy.damageResistance = m;
        }
        if (this.damageEnhancement != null) {
            m = new HashMap();
            for (Map.Entry<Object, Modifier[]> entry : this.damageEnhancement.entrySet()) {
                m.put((Integer)((Object)((String)entry.getKey())), (Modifier[])Arrays.stream(entry.getValue()).map(x -> x.clone()).toArray(Modifier[]::new));
            }
            copy.damageEnhancement = m;
        }
        if (this.damageClassEnhancement != null) {
            m = new HashMap();
            for (Map.Entry<Object, Modifier[]> entry : this.damageClassEnhancement.entrySet()) {
                m.put((Integer)((Object)((String)entry.getKey())), (Modifier[])Arrays.stream(entry.getValue()).map(x -> x.clone()).toArray(Modifier[]::new));
            }
            copy.damageClassEnhancement = m;
        }
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ItemArmor)) {
            return false;
        }
        ItemArmor other = (ItemArmor)obj;
        return Objects.equals((Object)this.armorSlot, (Object)other.armorSlot) && Arrays.equals((Object[])this.cosmeticsToHide, (Object[])other.cosmeticsToHide) && Objects.equals(this.statModifiers, other.statModifiers) && this.baseDamageResistance == other.baseDamageResistance && Objects.equals(this.damageResistance, other.damageResistance) && Objects.equals(this.damageEnhancement, other.damageEnhancement) && Objects.equals(this.damageClassEnhancement, other.damageClassEnhancement);
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + Objects.hashCode((Object)this.armorSlot);
        result = 31 * result + Arrays.hashCode((Object[])this.cosmeticsToHide);
        result = 31 * result + Objects.hashCode(this.statModifiers);
        result = 31 * result + Double.hashCode(this.baseDamageResistance);
        result = 31 * result + Objects.hashCode(this.damageResistance);
        result = 31 * result + Objects.hashCode(this.damageEnhancement);
        result = 31 * result + Objects.hashCode(this.damageClassEnhancement);
        return result;
    }
}

