/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.AngledDamage;
import com.hypixel.hytale.protocol.DamageEffects;
import com.hypixel.hytale.protocol.EntityStatOnHit;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.Interaction;
import com.hypixel.hytale.protocol.InteractionCameraSettings;
import com.hypixel.hytale.protocol.InteractionEffects;
import com.hypixel.hytale.protocol.InteractionRules;
import com.hypixel.hytale.protocol.InteractionSettings;
import com.hypixel.hytale.protocol.TargetedDamage;
import com.hypixel.hytale.protocol.WaitForDataFrom;
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

public class DamageEntityInteraction
extends Interaction {
    public static final int NULLABLE_BIT_FIELD_SIZE = 2;
    public static final int FIXED_BLOCK_SIZE = 24;
    public static final int VARIABLE_FIELD_COUNT = 9;
    public static final int VARIABLE_BLOCK_START = 60;
    public static final int MAX_SIZE = 0x64000000;
    public int next = Integer.MIN_VALUE;
    public int failed = Integer.MIN_VALUE;
    public int blocked = Integer.MIN_VALUE;
    @Nullable
    public DamageEffects damageEffects;
    @Nullable
    public AngledDamage[] angledDamage;
    @Nullable
    public Map<String, TargetedDamage> targetedDamage;
    @Nullable
    public EntityStatOnHit[] entityStatsOnHit;

    public DamageEntityInteraction() {
    }

    public DamageEntityInteraction(@Nonnull WaitForDataFrom waitForDataFrom, @Nullable InteractionEffects effects, float horizontalSpeedMultiplier, float runTime, boolean cancelOnItemChange, @Nullable Map<GameMode, InteractionSettings> settings, @Nullable InteractionRules rules, @Nullable int[] tags, @Nullable InteractionCameraSettings camera, int next, int failed, int blocked, @Nullable DamageEffects damageEffects, @Nullable AngledDamage[] angledDamage, @Nullable Map<String, TargetedDamage> targetedDamage, @Nullable EntityStatOnHit[] entityStatsOnHit) {
        this.waitForDataFrom = waitForDataFrom;
        this.effects = effects;
        this.horizontalSpeedMultiplier = horizontalSpeedMultiplier;
        this.runTime = runTime;
        this.cancelOnItemChange = cancelOnItemChange;
        this.settings = settings;
        this.rules = rules;
        this.tags = tags;
        this.camera = camera;
        this.next = next;
        this.failed = failed;
        this.blocked = blocked;
        this.damageEffects = damageEffects;
        this.angledDamage = angledDamage;
        this.targetedDamage = targetedDamage;
        this.entityStatsOnHit = entityStatsOnHit;
    }

    public DamageEntityInteraction(@Nonnull DamageEntityInteraction other) {
        this.waitForDataFrom = other.waitForDataFrom;
        this.effects = other.effects;
        this.horizontalSpeedMultiplier = other.horizontalSpeedMultiplier;
        this.runTime = other.runTime;
        this.cancelOnItemChange = other.cancelOnItemChange;
        this.settings = other.settings;
        this.rules = other.rules;
        this.tags = other.tags;
        this.camera = other.camera;
        this.next = other.next;
        this.failed = other.failed;
        this.blocked = other.blocked;
        this.damageEffects = other.damageEffects;
        this.angledDamage = other.angledDamage;
        this.targetedDamage = other.targetedDamage;
        this.entityStatsOnHit = other.entityStatsOnHit;
    }

    @Nonnull
    public static DamageEntityInteraction deserialize(@Nonnull ByteBuf buf, int offset) {
        int elemPos;
        int i;
        int dictPos;
        int varIntLen;
        DamageEntityInteraction obj = new DamageEntityInteraction();
        byte[] nullBits = PacketIO.readBytes(buf, offset, 2);
        obj.waitForDataFrom = WaitForDataFrom.fromValue(buf.getByte(offset + 2));
        obj.horizontalSpeedMultiplier = buf.getFloatLE(offset + 3);
        obj.runTime = buf.getFloatLE(offset + 7);
        obj.cancelOnItemChange = buf.getByte(offset + 11) != 0;
        obj.next = buf.getIntLE(offset + 12);
        obj.failed = buf.getIntLE(offset + 16);
        obj.blocked = buf.getIntLE(offset + 20);
        if ((nullBits[0] & 1) != 0) {
            int varPos0 = offset + 60 + buf.getIntLE(offset + 24);
            obj.effects = InteractionEffects.deserialize(buf, varPos0);
        }
        if ((nullBits[0] & 2) != 0) {
            int varPos1 = offset + 60 + buf.getIntLE(offset + 28);
            int settingsCount = VarInt.peek(buf, varPos1);
            if (settingsCount < 0) {
                throw ProtocolException.negativeLength("Settings", settingsCount);
            }
            if (settingsCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Settings", settingsCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos1);
            obj.settings = new HashMap(settingsCount);
            dictPos = varPos1 + varIntLen;
            for (i = 0; i < settingsCount; ++i) {
                GameMode key = GameMode.fromValue(buf.getByte(dictPos));
                InteractionSettings val = InteractionSettings.deserialize(buf, ++dictPos);
                dictPos += InteractionSettings.computeBytesConsumed(buf, dictPos);
                if (obj.settings.put(key, val) == null) continue;
                throw ProtocolException.duplicateKey("settings", (Object)key);
            }
        }
        if ((nullBits[0] & 4) != 0) {
            int varPos2 = offset + 60 + buf.getIntLE(offset + 32);
            obj.rules = InteractionRules.deserialize(buf, varPos2);
        }
        if ((nullBits[0] & 8) != 0) {
            int varPos3 = offset + 60 + buf.getIntLE(offset + 36);
            int tagsCount = VarInt.peek(buf, varPos3);
            if (tagsCount < 0) {
                throw ProtocolException.negativeLength("Tags", tagsCount);
            }
            if (tagsCount > 4096000) {
                throw ProtocolException.arrayTooLong("Tags", tagsCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos3);
            if ((long)(varPos3 + varIntLen) + (long)tagsCount * 4L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("Tags", varPos3 + varIntLen + tagsCount * 4, buf.readableBytes());
            }
            obj.tags = new int[tagsCount];
            for (int i2 = 0; i2 < tagsCount; ++i2) {
                obj.tags[i2] = buf.getIntLE(varPos3 + varIntLen + i2 * 4);
            }
        }
        if ((nullBits[0] & 0x10) != 0) {
            int varPos4 = offset + 60 + buf.getIntLE(offset + 40);
            obj.camera = InteractionCameraSettings.deserialize(buf, varPos4);
        }
        if ((nullBits[0] & 0x20) != 0) {
            int varPos5 = offset + 60 + buf.getIntLE(offset + 44);
            obj.damageEffects = DamageEffects.deserialize(buf, varPos5);
        }
        if ((nullBits[0] & 0x40) != 0) {
            int varPos6 = offset + 60 + buf.getIntLE(offset + 48);
            int angledDamageCount = VarInt.peek(buf, varPos6);
            if (angledDamageCount < 0) {
                throw ProtocolException.negativeLength("AngledDamage", angledDamageCount);
            }
            if (angledDamageCount > 4096000) {
                throw ProtocolException.arrayTooLong("AngledDamage", angledDamageCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos6);
            if ((long)(varPos6 + varIntLen) + (long)angledDamageCount * 21L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("AngledDamage", varPos6 + varIntLen + angledDamageCount * 21, buf.readableBytes());
            }
            obj.angledDamage = new AngledDamage[angledDamageCount];
            elemPos = varPos6 + varIntLen;
            for (i = 0; i < angledDamageCount; ++i) {
                obj.angledDamage[i] = AngledDamage.deserialize(buf, elemPos);
                elemPos += AngledDamage.computeBytesConsumed(buf, elemPos);
            }
        }
        if ((nullBits[0] & 0x80) != 0) {
            int varPos7 = offset + 60 + buf.getIntLE(offset + 52);
            int targetedDamageCount = VarInt.peek(buf, varPos7);
            if (targetedDamageCount < 0) {
                throw ProtocolException.negativeLength("TargetedDamage", targetedDamageCount);
            }
            if (targetedDamageCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("TargetedDamage", targetedDamageCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos7);
            obj.targetedDamage = new HashMap<String, TargetedDamage>(targetedDamageCount);
            dictPos = varPos7 + varIntLen;
            for (i = 0; i < targetedDamageCount; ++i) {
                int keyLen = VarInt.peek(buf, dictPos);
                if (keyLen < 0) {
                    throw ProtocolException.negativeLength("key", keyLen);
                }
                if (keyLen > 4096000) {
                    throw ProtocolException.stringTooLong("key", keyLen, 4096000);
                }
                int keyVarLen = VarInt.length(buf, dictPos);
                String key = PacketIO.readVarString(buf, dictPos);
                TargetedDamage val = TargetedDamage.deserialize(buf, dictPos += keyVarLen + keyLen);
                dictPos += TargetedDamage.computeBytesConsumed(buf, dictPos);
                if (obj.targetedDamage.put(key, val) == null) continue;
                throw ProtocolException.duplicateKey("targetedDamage", key);
            }
        }
        if ((nullBits[1] & 1) != 0) {
            int varPos8 = offset + 60 + buf.getIntLE(offset + 56);
            int entityStatsOnHitCount = VarInt.peek(buf, varPos8);
            if (entityStatsOnHitCount < 0) {
                throw ProtocolException.negativeLength("EntityStatsOnHit", entityStatsOnHitCount);
            }
            if (entityStatsOnHitCount > 4096000) {
                throw ProtocolException.arrayTooLong("EntityStatsOnHit", entityStatsOnHitCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos8);
            if ((long)(varPos8 + varIntLen) + (long)entityStatsOnHitCount * 13L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("EntityStatsOnHit", varPos8 + varIntLen + entityStatsOnHitCount * 13, buf.readableBytes());
            }
            obj.entityStatsOnHit = new EntityStatOnHit[entityStatsOnHitCount];
            elemPos = varPos8 + varIntLen;
            for (i = 0; i < entityStatsOnHitCount; ++i) {
                obj.entityStatsOnHit[i] = EntityStatOnHit.deserialize(buf, elemPos);
                elemPos += EntityStatOnHit.computeBytesConsumed(buf, elemPos);
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int arrLen;
        int i;
        int dictLen;
        byte[] nullBits = PacketIO.readBytes(buf, offset, 2);
        int maxEnd = 60;
        if ((nullBits[0] & 1) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 24);
            int pos0 = offset + 60 + fieldOffset0;
            if ((pos0 += InteractionEffects.computeBytesConsumed(buf, pos0)) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits[0] & 2) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 28);
            int pos1 = offset + 60 + fieldOffset1;
            dictLen = VarInt.peek(buf, pos1);
            pos1 += VarInt.length(buf, pos1);
            for (i = 0; i < dictLen; ++i) {
                ++pos1;
                pos1 += InteractionSettings.computeBytesConsumed(buf, pos1);
            }
            if (pos1 - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        if ((nullBits[0] & 4) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 32);
            int pos2 = offset + 60 + fieldOffset2;
            if ((pos2 += InteractionRules.computeBytesConsumed(buf, pos2)) - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        if ((nullBits[0] & 8) != 0) {
            int fieldOffset3 = buf.getIntLE(offset + 36);
            int pos3 = offset + 60 + fieldOffset3;
            arrLen = VarInt.peek(buf, pos3);
            if ((pos3 += VarInt.length(buf, pos3) + arrLen * 4) - offset > maxEnd) {
                maxEnd = pos3 - offset;
            }
        }
        if ((nullBits[0] & 0x10) != 0) {
            int fieldOffset4 = buf.getIntLE(offset + 40);
            int pos4 = offset + 60 + fieldOffset4;
            if ((pos4 += InteractionCameraSettings.computeBytesConsumed(buf, pos4)) - offset > maxEnd) {
                maxEnd = pos4 - offset;
            }
        }
        if ((nullBits[0] & 0x20) != 0) {
            int fieldOffset5 = buf.getIntLE(offset + 44);
            int pos5 = offset + 60 + fieldOffset5;
            if ((pos5 += DamageEffects.computeBytesConsumed(buf, pos5)) - offset > maxEnd) {
                maxEnd = pos5 - offset;
            }
        }
        if ((nullBits[0] & 0x40) != 0) {
            int fieldOffset6 = buf.getIntLE(offset + 48);
            int pos6 = offset + 60 + fieldOffset6;
            arrLen = VarInt.peek(buf, pos6);
            pos6 += VarInt.length(buf, pos6);
            for (i = 0; i < arrLen; ++i) {
                pos6 += AngledDamage.computeBytesConsumed(buf, pos6);
            }
            if (pos6 - offset > maxEnd) {
                maxEnd = pos6 - offset;
            }
        }
        if ((nullBits[0] & 0x80) != 0) {
            int fieldOffset7 = buf.getIntLE(offset + 52);
            int pos7 = offset + 60 + fieldOffset7;
            dictLen = VarInt.peek(buf, pos7);
            pos7 += VarInt.length(buf, pos7);
            for (i = 0; i < dictLen; ++i) {
                int sl = VarInt.peek(buf, pos7);
                pos7 += VarInt.length(buf, pos7) + sl;
                pos7 += TargetedDamage.computeBytesConsumed(buf, pos7);
            }
            if (pos7 - offset > maxEnd) {
                maxEnd = pos7 - offset;
            }
        }
        if ((nullBits[1] & 1) != 0) {
            int fieldOffset8 = buf.getIntLE(offset + 56);
            int pos8 = offset + 60 + fieldOffset8;
            arrLen = VarInt.peek(buf, pos8);
            pos8 += VarInt.length(buf, pos8);
            for (i = 0; i < arrLen; ++i) {
                pos8 += EntityStatOnHit.computeBytesConsumed(buf, pos8);
            }
            if (pos8 - offset > maxEnd) {
                maxEnd = pos8 - offset;
            }
        }
        return maxEnd;
    }

    @Override
    public int serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        byte[] nullBits = new byte[2];
        if (this.effects != null) {
            nullBits[0] = (byte)(nullBits[0] | 1);
        }
        if (this.settings != null) {
            nullBits[0] = (byte)(nullBits[0] | 2);
        }
        if (this.rules != null) {
            nullBits[0] = (byte)(nullBits[0] | 4);
        }
        if (this.tags != null) {
            nullBits[0] = (byte)(nullBits[0] | 8);
        }
        if (this.camera != null) {
            nullBits[0] = (byte)(nullBits[0] | 0x10);
        }
        if (this.damageEffects != null) {
            nullBits[0] = (byte)(nullBits[0] | 0x20);
        }
        if (this.angledDamage != null) {
            nullBits[0] = (byte)(nullBits[0] | 0x40);
        }
        if (this.targetedDamage != null) {
            nullBits[0] = (byte)(nullBits[0] | 0x80);
        }
        if (this.entityStatsOnHit != null) {
            nullBits[1] = (byte)(nullBits[1] | 1);
        }
        buf.writeBytes(nullBits);
        buf.writeByte(this.waitForDataFrom.getValue());
        buf.writeFloatLE(this.horizontalSpeedMultiplier);
        buf.writeFloatLE(this.runTime);
        buf.writeByte(this.cancelOnItemChange ? 1 : 0);
        buf.writeIntLE(this.next);
        buf.writeIntLE(this.failed);
        buf.writeIntLE(this.blocked);
        int effectsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int settingsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int rulesOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int tagsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int cameraOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int damageEffectsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int angledDamageOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int targetedDamageOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int entityStatsOnHitOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.effects != null) {
            buf.setIntLE(effectsOffsetSlot, buf.writerIndex() - varBlockStart);
            this.effects.serialize(buf);
        } else {
            buf.setIntLE(effectsOffsetSlot, -1);
        }
        if (this.settings != null) {
            buf.setIntLE(settingsOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.settings.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Settings", this.settings.size(), 4096000);
            }
            VarInt.write(buf, this.settings.size());
            for (Map.Entry entry : this.settings.entrySet()) {
                buf.writeByte(((GameMode)((Object)entry.getKey())).getValue());
                ((InteractionSettings)entry.getValue()).serialize(buf);
            }
        } else {
            buf.setIntLE(settingsOffsetSlot, -1);
        }
        if (this.rules != null) {
            buf.setIntLE(rulesOffsetSlot, buf.writerIndex() - varBlockStart);
            this.rules.serialize(buf);
        } else {
            buf.setIntLE(rulesOffsetSlot, -1);
        }
        if (this.tags != null) {
            buf.setIntLE(tagsOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.tags.length > 4096000) {
                throw ProtocolException.arrayTooLong("Tags", this.tags.length, 4096000);
            }
            VarInt.write(buf, this.tags.length);
            for (Object item : (Object)this.tags) {
                buf.writeIntLE((int)item);
            }
        } else {
            buf.setIntLE(tagsOffsetSlot, -1);
        }
        if (this.camera != null) {
            buf.setIntLE(cameraOffsetSlot, buf.writerIndex() - varBlockStart);
            this.camera.serialize(buf);
        } else {
            buf.setIntLE(cameraOffsetSlot, -1);
        }
        if (this.damageEffects != null) {
            buf.setIntLE(damageEffectsOffsetSlot, buf.writerIndex() - varBlockStart);
            this.damageEffects.serialize(buf);
        } else {
            buf.setIntLE(damageEffectsOffsetSlot, -1);
        }
        if (this.angledDamage != null) {
            buf.setIntLE(angledDamageOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.angledDamage.length > 4096000) {
                throw ProtocolException.arrayTooLong("AngledDamage", this.angledDamage.length, 4096000);
            }
            VarInt.write(buf, this.angledDamage.length);
            for (AngledDamage item : this.angledDamage) {
                item.serialize(buf);
            }
        } else {
            buf.setIntLE(angledDamageOffsetSlot, -1);
        }
        if (this.targetedDamage != null) {
            buf.setIntLE(targetedDamageOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.targetedDamage.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("TargetedDamage", this.targetedDamage.size(), 4096000);
            }
            VarInt.write(buf, this.targetedDamage.size());
            for (Map.Entry entry : this.targetedDamage.entrySet()) {
                PacketIO.writeVarString(buf, (String)entry.getKey(), 4096000);
                ((TargetedDamage)entry.getValue()).serialize(buf);
            }
        } else {
            buf.setIntLE(targetedDamageOffsetSlot, -1);
        }
        if (this.entityStatsOnHit != null) {
            buf.setIntLE(entityStatsOnHitOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.entityStatsOnHit.length > 4096000) {
                throw ProtocolException.arrayTooLong("EntityStatsOnHit", this.entityStatsOnHit.length, 4096000);
            }
            VarInt.write(buf, this.entityStatsOnHit.length);
            for (EntityStatOnHit item : this.entityStatsOnHit) {
                item.serialize(buf);
            }
        } else {
            buf.setIntLE(entityStatsOnHitOffsetSlot, -1);
        }
        return buf.writerIndex() - startPos;
    }

    @Override
    public int computeSize() {
        int size = 60;
        if (this.effects != null) {
            size += this.effects.computeSize();
        }
        if (this.settings != null) {
            size += VarInt.size(this.settings.size()) + this.settings.size() * 2;
        }
        if (this.rules != null) {
            size += this.rules.computeSize();
        }
        if (this.tags != null) {
            size += VarInt.size(this.tags.length) + this.tags.length * 4;
        }
        if (this.camera != null) {
            size += this.camera.computeSize();
        }
        if (this.damageEffects != null) {
            size += this.damageEffects.computeSize();
        }
        if (this.angledDamage != null) {
            int angledDamageSize = 0;
            for (AngledDamage angledDamage : this.angledDamage) {
                angledDamageSize += angledDamage.computeSize();
            }
            size += VarInt.size(this.angledDamage.length) + angledDamageSize;
        }
        if (this.targetedDamage != null) {
            int targetedDamageSize = 0;
            for (Map.Entry entry : this.targetedDamage.entrySet()) {
                targetedDamageSize += PacketIO.stringSize((String)entry.getKey()) + ((TargetedDamage)entry.getValue()).computeSize();
            }
            size += VarInt.size(this.targetedDamage.size()) + targetedDamageSize;
        }
        if (this.entityStatsOnHit != null) {
            int entityStatsOnHitSize = 0;
            for (EntityStatOnHit entityStatOnHit : this.entityStatsOnHit) {
                entityStatsOnHitSize += entityStatOnHit.computeSize();
            }
            size += VarInt.size(this.entityStatsOnHit.length) + entityStatsOnHitSize;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int i;
        int pos;
        if (buffer.readableBytes() - offset < 60) {
            return ValidationResult.error("Buffer too small: expected at least 60 bytes");
        }
        byte[] nullBits = PacketIO.readBytes(buffer, offset, 2);
        if ((nullBits[0] & 1) != 0) {
            int effectsOffset = buffer.getIntLE(offset + 24);
            if (effectsOffset < 0) {
                return ValidationResult.error("Invalid offset for Effects");
            }
            pos = offset + 60 + effectsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Effects");
            }
            ValidationResult effectsResult = InteractionEffects.validateStructure(buffer, pos);
            if (!effectsResult.isValid()) {
                return ValidationResult.error("Invalid Effects: " + effectsResult.error());
            }
            pos += InteractionEffects.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits[0] & 2) != 0) {
            int settingsOffset = buffer.getIntLE(offset + 28);
            if (settingsOffset < 0) {
                return ValidationResult.error("Invalid offset for Settings");
            }
            pos = offset + 60 + settingsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Settings");
            }
            int settingsCount = VarInt.peek(buffer, pos);
            if (settingsCount < 0) {
                return ValidationResult.error("Invalid dictionary count for Settings");
            }
            if (settingsCount > 4096000) {
                return ValidationResult.error("Settings exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < settingsCount; ++i) {
                ++pos;
                ++pos;
            }
        }
        if ((nullBits[0] & 4) != 0) {
            int rulesOffset = buffer.getIntLE(offset + 32);
            if (rulesOffset < 0) {
                return ValidationResult.error("Invalid offset for Rules");
            }
            pos = offset + 60 + rulesOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Rules");
            }
            ValidationResult rulesResult = InteractionRules.validateStructure(buffer, pos);
            if (!rulesResult.isValid()) {
                return ValidationResult.error("Invalid Rules: " + rulesResult.error());
            }
            pos += InteractionRules.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits[0] & 8) != 0) {
            int tagsOffset = buffer.getIntLE(offset + 36);
            if (tagsOffset < 0) {
                return ValidationResult.error("Invalid offset for Tags");
            }
            pos = offset + 60 + tagsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Tags");
            }
            int tagsCount = VarInt.peek(buffer, pos);
            if (tagsCount < 0) {
                return ValidationResult.error("Invalid array count for Tags");
            }
            if (tagsCount > 4096000) {
                return ValidationResult.error("Tags exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += tagsCount * 4) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading Tags");
            }
        }
        if ((nullBits[0] & 0x10) != 0) {
            int cameraOffset = buffer.getIntLE(offset + 40);
            if (cameraOffset < 0) {
                return ValidationResult.error("Invalid offset for Camera");
            }
            pos = offset + 60 + cameraOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Camera");
            }
            ValidationResult cameraResult = InteractionCameraSettings.validateStructure(buffer, pos);
            if (!cameraResult.isValid()) {
                return ValidationResult.error("Invalid Camera: " + cameraResult.error());
            }
            pos += InteractionCameraSettings.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits[0] & 0x20) != 0) {
            int damageEffectsOffset = buffer.getIntLE(offset + 44);
            if (damageEffectsOffset < 0) {
                return ValidationResult.error("Invalid offset for DamageEffects");
            }
            pos = offset + 60 + damageEffectsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for DamageEffects");
            }
            ValidationResult damageEffectsResult = DamageEffects.validateStructure(buffer, pos);
            if (!damageEffectsResult.isValid()) {
                return ValidationResult.error("Invalid DamageEffects: " + damageEffectsResult.error());
            }
            pos += DamageEffects.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits[0] & 0x40) != 0) {
            int angledDamageOffset = buffer.getIntLE(offset + 48);
            if (angledDamageOffset < 0) {
                return ValidationResult.error("Invalid offset for AngledDamage");
            }
            pos = offset + 60 + angledDamageOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for AngledDamage");
            }
            int angledDamageCount = VarInt.peek(buffer, pos);
            if (angledDamageCount < 0) {
                return ValidationResult.error("Invalid array count for AngledDamage");
            }
            if (angledDamageCount > 4096000) {
                return ValidationResult.error("AngledDamage exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < angledDamageCount; ++i) {
                ValidationResult structResult = AngledDamage.validateStructure(buffer, pos);
                if (!structResult.isValid()) {
                    return ValidationResult.error("Invalid AngledDamage in AngledDamage[" + i + "]: " + structResult.error());
                }
                pos += AngledDamage.computeBytesConsumed(buffer, pos);
            }
        }
        if ((nullBits[0] & 0x80) != 0) {
            int targetedDamageOffset = buffer.getIntLE(offset + 52);
            if (targetedDamageOffset < 0) {
                return ValidationResult.error("Invalid offset for TargetedDamage");
            }
            pos = offset + 60 + targetedDamageOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for TargetedDamage");
            }
            int targetedDamageCount = VarInt.peek(buffer, pos);
            if (targetedDamageCount < 0) {
                return ValidationResult.error("Invalid dictionary count for TargetedDamage");
            }
            if (targetedDamageCount > 4096000) {
                return ValidationResult.error("TargetedDamage exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < targetedDamageCount; ++i) {
                int keyLen = VarInt.peek(buffer, pos);
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
                pos += TargetedDamage.computeBytesConsumed(buffer, pos);
            }
        }
        if ((nullBits[1] & 1) != 0) {
            int entityStatsOnHitOffset = buffer.getIntLE(offset + 56);
            if (entityStatsOnHitOffset < 0) {
                return ValidationResult.error("Invalid offset for EntityStatsOnHit");
            }
            pos = offset + 60 + entityStatsOnHitOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for EntityStatsOnHit");
            }
            int entityStatsOnHitCount = VarInt.peek(buffer, pos);
            if (entityStatsOnHitCount < 0) {
                return ValidationResult.error("Invalid array count for EntityStatsOnHit");
            }
            if (entityStatsOnHitCount > 4096000) {
                return ValidationResult.error("EntityStatsOnHit exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < entityStatsOnHitCount; ++i) {
                ValidationResult structResult = EntityStatOnHit.validateStructure(buffer, pos);
                if (!structResult.isValid()) {
                    return ValidationResult.error("Invalid EntityStatOnHit in EntityStatsOnHit[" + i + "]: " + structResult.error());
                }
                pos += EntityStatOnHit.computeBytesConsumed(buffer, pos);
            }
        }
        return ValidationResult.OK;
    }

    public DamageEntityInteraction clone() {
        HashMap<String, TargetedDamage> m;
        DamageEntityInteraction copy = new DamageEntityInteraction();
        copy.waitForDataFrom = this.waitForDataFrom;
        copy.effects = this.effects != null ? this.effects.clone() : null;
        copy.horizontalSpeedMultiplier = this.horizontalSpeedMultiplier;
        copy.runTime = this.runTime;
        copy.cancelOnItemChange = this.cancelOnItemChange;
        if (this.settings != null) {
            m = new HashMap<String, TargetedDamage>();
            for (Map.Entry entry : this.settings.entrySet()) {
                m.put((String)((Object)((GameMode)((Object)entry.getKey()))), (TargetedDamage)((Object)((InteractionSettings)entry.getValue()).clone()));
            }
            copy.settings = m;
        }
        copy.rules = this.rules != null ? this.rules.clone() : null;
        copy.tags = this.tags != null ? Arrays.copyOf(this.tags, this.tags.length) : null;
        copy.camera = this.camera != null ? this.camera.clone() : null;
        copy.next = this.next;
        copy.failed = this.failed;
        copy.blocked = this.blocked;
        copy.damageEffects = this.damageEffects != null ? this.damageEffects.clone() : null;
        AngledDamage[] angledDamageArray = copy.angledDamage = this.angledDamage != null ? (AngledDamage[])Arrays.stream(this.angledDamage).map(e -> e.clone()).toArray(AngledDamage[]::new) : null;
        if (this.targetedDamage != null) {
            m = new HashMap();
            for (Map.Entry<Object, Object> entry : this.targetedDamage.entrySet()) {
                m.put((String)entry.getKey(), ((TargetedDamage)entry.getValue()).clone());
            }
            copy.targetedDamage = m;
        }
        copy.entityStatsOnHit = this.entityStatsOnHit != null ? (EntityStatOnHit[])Arrays.stream(this.entityStatsOnHit).map(e -> e.clone()).toArray(EntityStatOnHit[]::new) : null;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DamageEntityInteraction)) {
            return false;
        }
        DamageEntityInteraction other = (DamageEntityInteraction)obj;
        return Objects.equals((Object)this.waitForDataFrom, (Object)other.waitForDataFrom) && Objects.equals(this.effects, other.effects) && this.horizontalSpeedMultiplier == other.horizontalSpeedMultiplier && this.runTime == other.runTime && this.cancelOnItemChange == other.cancelOnItemChange && Objects.equals(this.settings, other.settings) && Objects.equals(this.rules, other.rules) && Arrays.equals(this.tags, other.tags) && Objects.equals(this.camera, other.camera) && this.next == other.next && this.failed == other.failed && this.blocked == other.blocked && Objects.equals(this.damageEffects, other.damageEffects) && Arrays.equals(this.angledDamage, other.angledDamage) && Objects.equals(this.targetedDamage, other.targetedDamage) && Arrays.equals(this.entityStatsOnHit, other.entityStatsOnHit);
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + Objects.hashCode((Object)this.waitForDataFrom);
        result = 31 * result + Objects.hashCode(this.effects);
        result = 31 * result + Float.hashCode(this.horizontalSpeedMultiplier);
        result = 31 * result + Float.hashCode(this.runTime);
        result = 31 * result + Boolean.hashCode(this.cancelOnItemChange);
        result = 31 * result + Objects.hashCode(this.settings);
        result = 31 * result + Objects.hashCode(this.rules);
        result = 31 * result + Arrays.hashCode(this.tags);
        result = 31 * result + Objects.hashCode(this.camera);
        result = 31 * result + Integer.hashCode(this.next);
        result = 31 * result + Integer.hashCode(this.failed);
        result = 31 * result + Integer.hashCode(this.blocked);
        result = 31 * result + Objects.hashCode(this.damageEffects);
        result = 31 * result + Arrays.hashCode(this.angledDamage);
        result = 31 * result + Objects.hashCode(this.targetedDamage);
        result = 31 * result + Arrays.hashCode(this.entityStatsOnHit);
        return result;
    }
}

