/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.ChargingDelay;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.Interaction;
import com.hypixel.hytale.protocol.InteractionCameraSettings;
import com.hypixel.hytale.protocol.InteractionEffects;
import com.hypixel.hytale.protocol.InteractionRules;
import com.hypixel.hytale.protocol.InteractionSettings;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChargingInteraction
extends Interaction {
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 47;
    public static final int VARIABLE_FIELD_COUNT = 7;
    public static final int VARIABLE_BLOCK_START = 75;
    public static final int MAX_SIZE = 0x64000000;
    public int failed = Integer.MIN_VALUE;
    public boolean allowIndefiniteHold;
    public boolean displayProgress;
    public boolean cancelOnOtherClick;
    public boolean failOnDamage;
    public float mouseSensitivityAdjustmentTarget;
    public float mouseSensitivityAdjustmentDuration;
    @Nullable
    public Map<Float, Integer> chargedNext;
    @Nullable
    public Map<InteractionType, Integer> forks;
    @Nullable
    public ChargingDelay chargingDelay;

    public ChargingInteraction() {
    }

    public ChargingInteraction(@Nonnull WaitForDataFrom waitForDataFrom, @Nullable InteractionEffects effects, float horizontalSpeedMultiplier, float runTime, boolean cancelOnItemChange, @Nullable Map<GameMode, InteractionSettings> settings, @Nullable InteractionRules rules, @Nullable int[] tags, @Nullable InteractionCameraSettings camera, int failed, boolean allowIndefiniteHold, boolean displayProgress, boolean cancelOnOtherClick, boolean failOnDamage, float mouseSensitivityAdjustmentTarget, float mouseSensitivityAdjustmentDuration, @Nullable Map<Float, Integer> chargedNext, @Nullable Map<InteractionType, Integer> forks, @Nullable ChargingDelay chargingDelay) {
        this.waitForDataFrom = waitForDataFrom;
        this.effects = effects;
        this.horizontalSpeedMultiplier = horizontalSpeedMultiplier;
        this.runTime = runTime;
        this.cancelOnItemChange = cancelOnItemChange;
        this.settings = settings;
        this.rules = rules;
        this.tags = tags;
        this.camera = camera;
        this.failed = failed;
        this.allowIndefiniteHold = allowIndefiniteHold;
        this.displayProgress = displayProgress;
        this.cancelOnOtherClick = cancelOnOtherClick;
        this.failOnDamage = failOnDamage;
        this.mouseSensitivityAdjustmentTarget = mouseSensitivityAdjustmentTarget;
        this.mouseSensitivityAdjustmentDuration = mouseSensitivityAdjustmentDuration;
        this.chargedNext = chargedNext;
        this.forks = forks;
        this.chargingDelay = chargingDelay;
    }

    public ChargingInteraction(@Nonnull ChargingInteraction other) {
        this.waitForDataFrom = other.waitForDataFrom;
        this.effects = other.effects;
        this.horizontalSpeedMultiplier = other.horizontalSpeedMultiplier;
        this.runTime = other.runTime;
        this.cancelOnItemChange = other.cancelOnItemChange;
        this.settings = other.settings;
        this.rules = other.rules;
        this.tags = other.tags;
        this.camera = other.camera;
        this.failed = other.failed;
        this.allowIndefiniteHold = other.allowIndefiniteHold;
        this.displayProgress = other.displayProgress;
        this.cancelOnOtherClick = other.cancelOnOtherClick;
        this.failOnDamage = other.failOnDamage;
        this.mouseSensitivityAdjustmentTarget = other.mouseSensitivityAdjustmentTarget;
        this.mouseSensitivityAdjustmentDuration = other.mouseSensitivityAdjustmentDuration;
        this.chargedNext = other.chargedNext;
        this.forks = other.forks;
        this.chargingDelay = other.chargingDelay;
    }

    @Nonnull
    public static ChargingInteraction deserialize(@Nonnull ByteBuf buf, int offset) {
        int i;
        int dictPos;
        int varIntLen;
        ChargingInteraction obj = new ChargingInteraction();
        byte nullBits = buf.getByte(offset);
        obj.waitForDataFrom = WaitForDataFrom.fromValue(buf.getByte(offset + 1));
        obj.horizontalSpeedMultiplier = buf.getFloatLE(offset + 2);
        obj.runTime = buf.getFloatLE(offset + 6);
        obj.cancelOnItemChange = buf.getByte(offset + 10) != 0;
        obj.failed = buf.getIntLE(offset + 11);
        obj.allowIndefiniteHold = buf.getByte(offset + 15) != 0;
        obj.displayProgress = buf.getByte(offset + 16) != 0;
        obj.cancelOnOtherClick = buf.getByte(offset + 17) != 0;
        obj.failOnDamage = buf.getByte(offset + 18) != 0;
        obj.mouseSensitivityAdjustmentTarget = buf.getFloatLE(offset + 19);
        obj.mouseSensitivityAdjustmentDuration = buf.getFloatLE(offset + 23);
        if ((nullBits & 1) != 0) {
            obj.chargingDelay = ChargingDelay.deserialize(buf, offset + 27);
        }
        if ((nullBits & 2) != 0) {
            int varPos0 = offset + 75 + buf.getIntLE(offset + 47);
            obj.effects = InteractionEffects.deserialize(buf, varPos0);
        }
        if ((nullBits & 4) != 0) {
            int varPos1 = offset + 75 + buf.getIntLE(offset + 51);
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
        if ((nullBits & 8) != 0) {
            int varPos2 = offset + 75 + buf.getIntLE(offset + 55);
            obj.rules = InteractionRules.deserialize(buf, varPos2);
        }
        if ((nullBits & 0x10) != 0) {
            int varPos3 = offset + 75 + buf.getIntLE(offset + 59);
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
        if ((nullBits & 0x20) != 0) {
            int varPos4 = offset + 75 + buf.getIntLE(offset + 63);
            obj.camera = InteractionCameraSettings.deserialize(buf, varPos4);
        }
        if ((nullBits & 0x40) != 0) {
            int varPos5 = offset + 75 + buf.getIntLE(offset + 67);
            int chargedNextCount = VarInt.peek(buf, varPos5);
            if (chargedNextCount < 0) {
                throw ProtocolException.negativeLength("ChargedNext", chargedNextCount);
            }
            if (chargedNextCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("ChargedNext", chargedNextCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos5);
            obj.chargedNext = new HashMap<Float, Integer>(chargedNextCount);
            dictPos = varPos5 + varIntLen;
            for (i = 0; i < chargedNextCount; ++i) {
                float key = buf.getFloatLE(dictPos);
                int val = buf.getIntLE(dictPos += 4);
                dictPos += 4;
                if (obj.chargedNext.put(Float.valueOf(key), val) == null) continue;
                throw ProtocolException.duplicateKey("chargedNext", Float.valueOf(key));
            }
        }
        if ((nullBits & 0x80) != 0) {
            int varPos6 = offset + 75 + buf.getIntLE(offset + 71);
            int forksCount = VarInt.peek(buf, varPos6);
            if (forksCount < 0) {
                throw ProtocolException.negativeLength("Forks", forksCount);
            }
            if (forksCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Forks", forksCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos6);
            obj.forks = new HashMap<InteractionType, Integer>(forksCount);
            dictPos = varPos6 + varIntLen;
            for (i = 0; i < forksCount; ++i) {
                InteractionType key = InteractionType.fromValue(buf.getByte(dictPos));
                int val = buf.getIntLE(++dictPos);
                dictPos += 4;
                if (obj.forks.put(key, val) == null) continue;
                throw ProtocolException.duplicateKey("forks", (Object)key);
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int i;
        int dictLen;
        byte nullBits = buf.getByte(offset);
        int maxEnd = 75;
        if ((nullBits & 2) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 47);
            int pos0 = offset + 75 + fieldOffset0;
            if ((pos0 += InteractionEffects.computeBytesConsumed(buf, pos0)) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 4) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 51);
            int pos1 = offset + 75 + fieldOffset1;
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
        if ((nullBits & 8) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 55);
            int pos2 = offset + 75 + fieldOffset2;
            if ((pos2 += InteractionRules.computeBytesConsumed(buf, pos2)) - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        if ((nullBits & 0x10) != 0) {
            int fieldOffset3 = buf.getIntLE(offset + 59);
            int pos3 = offset + 75 + fieldOffset3;
            int arrLen = VarInt.peek(buf, pos3);
            if ((pos3 += VarInt.length(buf, pos3) + arrLen * 4) - offset > maxEnd) {
                maxEnd = pos3 - offset;
            }
        }
        if ((nullBits & 0x20) != 0) {
            int fieldOffset4 = buf.getIntLE(offset + 63);
            int pos4 = offset + 75 + fieldOffset4;
            if ((pos4 += InteractionCameraSettings.computeBytesConsumed(buf, pos4)) - offset > maxEnd) {
                maxEnd = pos4 - offset;
            }
        }
        if ((nullBits & 0x40) != 0) {
            int fieldOffset5 = buf.getIntLE(offset + 67);
            int pos5 = offset + 75 + fieldOffset5;
            dictLen = VarInt.peek(buf, pos5);
            pos5 += VarInt.length(buf, pos5);
            for (i = 0; i < dictLen; ++i) {
                pos5 += 4;
                pos5 += 4;
            }
            if (pos5 - offset > maxEnd) {
                maxEnd = pos5 - offset;
            }
        }
        if ((nullBits & 0x80) != 0) {
            int fieldOffset6 = buf.getIntLE(offset + 71);
            int pos6 = offset + 75 + fieldOffset6;
            dictLen = VarInt.peek(buf, pos6);
            pos6 += VarInt.length(buf, pos6);
            for (i = 0; i < dictLen; ++i) {
                ++pos6;
                pos6 += 4;
            }
            if (pos6 - offset > maxEnd) {
                maxEnd = pos6 - offset;
            }
        }
        return maxEnd;
    }

    @Override
    public int serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        byte nullBits = 0;
        if (this.chargingDelay != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.effects != null) {
            nullBits = (byte)(nullBits | 2);
        }
        if (this.settings != null) {
            nullBits = (byte)(nullBits | 4);
        }
        if (this.rules != null) {
            nullBits = (byte)(nullBits | 8);
        }
        if (this.tags != null) {
            nullBits = (byte)(nullBits | 0x10);
        }
        if (this.camera != null) {
            nullBits = (byte)(nullBits | 0x20);
        }
        if (this.chargedNext != null) {
            nullBits = (byte)(nullBits | 0x40);
        }
        if (this.forks != null) {
            nullBits = (byte)(nullBits | 0x80);
        }
        buf.writeByte(nullBits);
        buf.writeByte(this.waitForDataFrom.getValue());
        buf.writeFloatLE(this.horizontalSpeedMultiplier);
        buf.writeFloatLE(this.runTime);
        buf.writeByte(this.cancelOnItemChange ? 1 : 0);
        buf.writeIntLE(this.failed);
        buf.writeByte(this.allowIndefiniteHold ? 1 : 0);
        buf.writeByte(this.displayProgress ? 1 : 0);
        buf.writeByte(this.cancelOnOtherClick ? 1 : 0);
        buf.writeByte(this.failOnDamage ? 1 : 0);
        buf.writeFloatLE(this.mouseSensitivityAdjustmentTarget);
        buf.writeFloatLE(this.mouseSensitivityAdjustmentDuration);
        if (this.chargingDelay != null) {
            this.chargingDelay.serialize(buf);
        } else {
            buf.writeZero(20);
        }
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
        int chargedNextOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int forksOffsetSlot = buf.writerIndex();
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
            for (Map.Entry e : this.settings.entrySet()) {
                buf.writeByte(((GameMode)((Object)e.getKey())).getValue());
                ((InteractionSettings)e.getValue()).serialize(buf);
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
            for (Object item : (Iterator<Map.Entry<Object, Integer>>)this.tags) {
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
        if (this.chargedNext != null) {
            buf.setIntLE(chargedNextOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.chargedNext.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("ChargedNext", this.chargedNext.size(), 4096000);
            }
            VarInt.write(buf, this.chargedNext.size());
            for (Map.Entry<Float, Integer> e : this.chargedNext.entrySet()) {
                buf.writeFloatLE(e.getKey().floatValue());
                buf.writeIntLE(e.getValue());
            }
        } else {
            buf.setIntLE(chargedNextOffsetSlot, -1);
        }
        if (this.forks != null) {
            buf.setIntLE(forksOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.forks.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Forks", this.forks.size(), 4096000);
            }
            VarInt.write(buf, this.forks.size());
            for (Map.Entry<Object, Integer> e : this.forks.entrySet()) {
                buf.writeByte(((InteractionType)((Object)e.getKey())).getValue());
                buf.writeIntLE(e.getValue());
            }
        } else {
            buf.setIntLE(forksOffsetSlot, -1);
        }
        return buf.writerIndex() - startPos;
    }

    @Override
    public int computeSize() {
        int size = 75;
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
        if (this.chargedNext != null) {
            size += VarInt.size(this.chargedNext.size()) + this.chargedNext.size() * 8;
        }
        if (this.forks != null) {
            size += VarInt.size(this.forks.size()) + this.forks.size() * 5;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int i;
        int pos;
        if (buffer.readableBytes() - offset < 75) {
            return ValidationResult.error("Buffer too small: expected at least 75 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 2) != 0) {
            int effectsOffset = buffer.getIntLE(offset + 47);
            if (effectsOffset < 0) {
                return ValidationResult.error("Invalid offset for Effects");
            }
            pos = offset + 75 + effectsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Effects");
            }
            ValidationResult effectsResult = InteractionEffects.validateStructure(buffer, pos);
            if (!effectsResult.isValid()) {
                return ValidationResult.error("Invalid Effects: " + effectsResult.error());
            }
            pos += InteractionEffects.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits & 4) != 0) {
            int settingsOffset = buffer.getIntLE(offset + 51);
            if (settingsOffset < 0) {
                return ValidationResult.error("Invalid offset for Settings");
            }
            pos = offset + 75 + settingsOffset;
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
        if ((nullBits & 8) != 0) {
            int rulesOffset = buffer.getIntLE(offset + 55);
            if (rulesOffset < 0) {
                return ValidationResult.error("Invalid offset for Rules");
            }
            pos = offset + 75 + rulesOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Rules");
            }
            ValidationResult rulesResult = InteractionRules.validateStructure(buffer, pos);
            if (!rulesResult.isValid()) {
                return ValidationResult.error("Invalid Rules: " + rulesResult.error());
            }
            pos += InteractionRules.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits & 0x10) != 0) {
            int tagsOffset = buffer.getIntLE(offset + 59);
            if (tagsOffset < 0) {
                return ValidationResult.error("Invalid offset for Tags");
            }
            pos = offset + 75 + tagsOffset;
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
        if ((nullBits & 0x20) != 0) {
            int cameraOffset = buffer.getIntLE(offset + 63);
            if (cameraOffset < 0) {
                return ValidationResult.error("Invalid offset for Camera");
            }
            pos = offset + 75 + cameraOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Camera");
            }
            ValidationResult cameraResult = InteractionCameraSettings.validateStructure(buffer, pos);
            if (!cameraResult.isValid()) {
                return ValidationResult.error("Invalid Camera: " + cameraResult.error());
            }
            pos += InteractionCameraSettings.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits & 0x40) != 0) {
            int chargedNextOffset = buffer.getIntLE(offset + 67);
            if (chargedNextOffset < 0) {
                return ValidationResult.error("Invalid offset for ChargedNext");
            }
            pos = offset + 75 + chargedNextOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for ChargedNext");
            }
            int chargedNextCount = VarInt.peek(buffer, pos);
            if (chargedNextCount < 0) {
                return ValidationResult.error("Invalid dictionary count for ChargedNext");
            }
            if (chargedNextCount > 4096000) {
                return ValidationResult.error("ChargedNext exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < chargedNextCount; ++i) {
                if ((pos += 4) > buffer.writerIndex()) {
                    return ValidationResult.error("Buffer overflow reading key");
                }
                if ((pos += 4) <= buffer.writerIndex()) continue;
                return ValidationResult.error("Buffer overflow reading value");
            }
        }
        if ((nullBits & 0x80) != 0) {
            int forksOffset = buffer.getIntLE(offset + 71);
            if (forksOffset < 0) {
                return ValidationResult.error("Invalid offset for Forks");
            }
            pos = offset + 75 + forksOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Forks");
            }
            int forksCount = VarInt.peek(buffer, pos);
            if (forksCount < 0) {
                return ValidationResult.error("Invalid dictionary count for Forks");
            }
            if (forksCount > 4096000) {
                return ValidationResult.error("Forks exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < forksCount; ++i) {
                ++pos;
                if ((pos += 4) <= buffer.writerIndex()) continue;
                return ValidationResult.error("Buffer overflow reading value");
            }
        }
        return ValidationResult.OK;
    }

    public ChargingInteraction clone() {
        ChargingInteraction copy = new ChargingInteraction();
        copy.waitForDataFrom = this.waitForDataFrom;
        copy.effects = this.effects != null ? this.effects.clone() : null;
        copy.horizontalSpeedMultiplier = this.horizontalSpeedMultiplier;
        copy.runTime = this.runTime;
        copy.cancelOnItemChange = this.cancelOnItemChange;
        if (this.settings != null) {
            HashMap<GameMode, InteractionSettings> m = new HashMap<GameMode, InteractionSettings>();
            for (Map.Entry e : this.settings.entrySet()) {
                m.put((GameMode)((Object)e.getKey()), ((InteractionSettings)e.getValue()).clone());
            }
            copy.settings = m;
        }
        copy.rules = this.rules != null ? this.rules.clone() : null;
        copy.tags = this.tags != null ? Arrays.copyOf(this.tags, this.tags.length) : null;
        copy.camera = this.camera != null ? this.camera.clone() : null;
        copy.failed = this.failed;
        copy.allowIndefiniteHold = this.allowIndefiniteHold;
        copy.displayProgress = this.displayProgress;
        copy.cancelOnOtherClick = this.cancelOnOtherClick;
        copy.failOnDamage = this.failOnDamage;
        copy.mouseSensitivityAdjustmentTarget = this.mouseSensitivityAdjustmentTarget;
        copy.mouseSensitivityAdjustmentDuration = this.mouseSensitivityAdjustmentDuration;
        copy.chargedNext = this.chargedNext != null ? new HashMap<Float, Integer>(this.chargedNext) : null;
        copy.forks = this.forks != null ? new HashMap<InteractionType, Integer>(this.forks) : null;
        copy.chargingDelay = this.chargingDelay != null ? this.chargingDelay.clone() : null;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ChargingInteraction)) {
            return false;
        }
        ChargingInteraction other = (ChargingInteraction)obj;
        return Objects.equals((Object)this.waitForDataFrom, (Object)other.waitForDataFrom) && Objects.equals(this.effects, other.effects) && this.horizontalSpeedMultiplier == other.horizontalSpeedMultiplier && this.runTime == other.runTime && this.cancelOnItemChange == other.cancelOnItemChange && Objects.equals(this.settings, other.settings) && Objects.equals(this.rules, other.rules) && Arrays.equals(this.tags, other.tags) && Objects.equals(this.camera, other.camera) && this.failed == other.failed && this.allowIndefiniteHold == other.allowIndefiniteHold && this.displayProgress == other.displayProgress && this.cancelOnOtherClick == other.cancelOnOtherClick && this.failOnDamage == other.failOnDamage && this.mouseSensitivityAdjustmentTarget == other.mouseSensitivityAdjustmentTarget && this.mouseSensitivityAdjustmentDuration == other.mouseSensitivityAdjustmentDuration && Objects.equals(this.chargedNext, other.chargedNext) && Objects.equals(this.forks, other.forks) && Objects.equals(this.chargingDelay, other.chargingDelay);
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
        result = 31 * result + Integer.hashCode(this.failed);
        result = 31 * result + Boolean.hashCode(this.allowIndefiniteHold);
        result = 31 * result + Boolean.hashCode(this.displayProgress);
        result = 31 * result + Boolean.hashCode(this.cancelOnOtherClick);
        result = 31 * result + Boolean.hashCode(this.failOnDamage);
        result = 31 * result + Float.hashCode(this.mouseSensitivityAdjustmentTarget);
        result = 31 * result + Float.hashCode(this.mouseSensitivityAdjustmentDuration);
        result = 31 * result + Objects.hashCode(this.chargedNext);
        result = 31 * result + Objects.hashCode(this.forks);
        result = 31 * result + Objects.hashCode(this.chargingDelay);
        return result;
    }
}

