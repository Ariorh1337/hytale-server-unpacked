/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.FailOnType;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.HitEntity;
import com.hypixel.hytale.protocol.InteractionCameraSettings;
import com.hypixel.hytale.protocol.InteractionEffects;
import com.hypixel.hytale.protocol.InteractionRules;
import com.hypixel.hytale.protocol.InteractionSettings;
import com.hypixel.hytale.protocol.Selector;
import com.hypixel.hytale.protocol.SimpleInteraction;
import com.hypixel.hytale.protocol.WaitForDataFrom;
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

public class SelectInteraction
extends SimpleInteraction {
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 25;
    public static final int VARIABLE_FIELD_COUNT = 7;
    public static final int VARIABLE_BLOCK_START = 53;
    public static final int MAX_SIZE = 0x64000000;
    @Nullable
    public Selector selector;
    public boolean ignoreOwner;
    public int hitEntity;
    @Nullable
    public HitEntity[] hitEntityRules;
    @Nonnull
    public FailOnType failOn = FailOnType.Neither;

    public SelectInteraction() {
    }

    public SelectInteraction(@Nonnull WaitForDataFrom waitForDataFrom, @Nullable InteractionEffects effects, float horizontalSpeedMultiplier, float runTime, boolean cancelOnItemChange, @Nullable Map<GameMode, InteractionSettings> settings, @Nullable InteractionRules rules, @Nullable int[] tags, @Nullable InteractionCameraSettings camera, int next, int failed, @Nullable Selector selector, boolean ignoreOwner, int hitEntity, @Nullable HitEntity[] hitEntityRules, @Nonnull FailOnType failOn) {
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
        this.selector = selector;
        this.ignoreOwner = ignoreOwner;
        this.hitEntity = hitEntity;
        this.hitEntityRules = hitEntityRules;
        this.failOn = failOn;
    }

    public SelectInteraction(@Nonnull SelectInteraction other) {
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
        this.selector = other.selector;
        this.ignoreOwner = other.ignoreOwner;
        this.hitEntity = other.hitEntity;
        this.hitEntityRules = other.hitEntityRules;
        this.failOn = other.failOn;
    }

    @Nonnull
    public static SelectInteraction deserialize(@Nonnull ByteBuf buf, int offset) {
        int i;
        int varIntLen;
        SelectInteraction obj = new SelectInteraction();
        byte nullBits = buf.getByte(offset);
        obj.waitForDataFrom = WaitForDataFrom.fromValue(buf.getByte(offset + 1));
        obj.horizontalSpeedMultiplier = buf.getFloatLE(offset + 2);
        obj.runTime = buf.getFloatLE(offset + 6);
        obj.cancelOnItemChange = buf.getByte(offset + 10) != 0;
        obj.next = buf.getIntLE(offset + 11);
        obj.failed = buf.getIntLE(offset + 15);
        obj.ignoreOwner = buf.getByte(offset + 19) != 0;
        obj.hitEntity = buf.getIntLE(offset + 20);
        obj.failOn = FailOnType.fromValue(buf.getByte(offset + 24));
        if ((nullBits & 1) != 0) {
            int varPos0 = offset + 53 + buf.getIntLE(offset + 25);
            obj.effects = InteractionEffects.deserialize(buf, varPos0);
        }
        if ((nullBits & 2) != 0) {
            int varPos1 = offset + 53 + buf.getIntLE(offset + 29);
            int settingsCount = VarInt.peek(buf, varPos1);
            if (settingsCount < 0) {
                throw ProtocolException.negativeLength("Settings", settingsCount);
            }
            if (settingsCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Settings", settingsCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos1);
            obj.settings = new HashMap(settingsCount);
            int dictPos = varPos1 + varIntLen;
            for (i = 0; i < settingsCount; ++i) {
                GameMode key = GameMode.fromValue(buf.getByte(dictPos));
                InteractionSettings val = InteractionSettings.deserialize(buf, ++dictPos);
                dictPos += InteractionSettings.computeBytesConsumed(buf, dictPos);
                if (obj.settings.put(key, val) == null) continue;
                throw ProtocolException.duplicateKey("settings", (Object)key);
            }
        }
        if ((nullBits & 4) != 0) {
            int varPos2 = offset + 53 + buf.getIntLE(offset + 33);
            obj.rules = InteractionRules.deserialize(buf, varPos2);
        }
        if ((nullBits & 8) != 0) {
            int varPos3 = offset + 53 + buf.getIntLE(offset + 37);
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
        if ((nullBits & 0x10) != 0) {
            int varPos4 = offset + 53 + buf.getIntLE(offset + 41);
            obj.camera = InteractionCameraSettings.deserialize(buf, varPos4);
        }
        if ((nullBits & 0x20) != 0) {
            int varPos5 = offset + 53 + buf.getIntLE(offset + 45);
            obj.selector = Selector.deserialize(buf, varPos5);
        }
        if ((nullBits & 0x40) != 0) {
            int varPos6 = offset + 53 + buf.getIntLE(offset + 49);
            int hitEntityRulesCount = VarInt.peek(buf, varPos6);
            if (hitEntityRulesCount < 0) {
                throw ProtocolException.negativeLength("HitEntityRules", hitEntityRulesCount);
            }
            if (hitEntityRulesCount > 4096000) {
                throw ProtocolException.arrayTooLong("HitEntityRules", hitEntityRulesCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos6);
            if ((long)(varPos6 + varIntLen) + (long)hitEntityRulesCount * 5L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("HitEntityRules", varPos6 + varIntLen + hitEntityRulesCount * 5, buf.readableBytes());
            }
            obj.hitEntityRules = new HitEntity[hitEntityRulesCount];
            int elemPos = varPos6 + varIntLen;
            for (i = 0; i < hitEntityRulesCount; ++i) {
                obj.hitEntityRules[i] = HitEntity.deserialize(buf, elemPos);
                elemPos += HitEntity.computeBytesConsumed(buf, elemPos);
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int arrLen;
        int i;
        byte nullBits = buf.getByte(offset);
        int maxEnd = 53;
        if ((nullBits & 1) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 25);
            int pos0 = offset + 53 + fieldOffset0;
            if ((pos0 += InteractionEffects.computeBytesConsumed(buf, pos0)) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 2) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 29);
            int pos1 = offset + 53 + fieldOffset1;
            int dictLen = VarInt.peek(buf, pos1);
            pos1 += VarInt.length(buf, pos1);
            for (i = 0; i < dictLen; ++i) {
                ++pos1;
                pos1 += InteractionSettings.computeBytesConsumed(buf, pos1);
            }
            if (pos1 - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        if ((nullBits & 4) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 33);
            int pos2 = offset + 53 + fieldOffset2;
            if ((pos2 += InteractionRules.computeBytesConsumed(buf, pos2)) - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        if ((nullBits & 8) != 0) {
            int fieldOffset3 = buf.getIntLE(offset + 37);
            int pos3 = offset + 53 + fieldOffset3;
            arrLen = VarInt.peek(buf, pos3);
            if ((pos3 += VarInt.length(buf, pos3) + arrLen * 4) - offset > maxEnd) {
                maxEnd = pos3 - offset;
            }
        }
        if ((nullBits & 0x10) != 0) {
            int fieldOffset4 = buf.getIntLE(offset + 41);
            int pos4 = offset + 53 + fieldOffset4;
            if ((pos4 += InteractionCameraSettings.computeBytesConsumed(buf, pos4)) - offset > maxEnd) {
                maxEnd = pos4 - offset;
            }
        }
        if ((nullBits & 0x20) != 0) {
            int fieldOffset5 = buf.getIntLE(offset + 45);
            int pos5 = offset + 53 + fieldOffset5;
            if ((pos5 += Selector.computeBytesConsumed(buf, pos5)) - offset > maxEnd) {
                maxEnd = pos5 - offset;
            }
        }
        if ((nullBits & 0x40) != 0) {
            int fieldOffset6 = buf.getIntLE(offset + 49);
            int pos6 = offset + 53 + fieldOffset6;
            arrLen = VarInt.peek(buf, pos6);
            pos6 += VarInt.length(buf, pos6);
            for (i = 0; i < arrLen; ++i) {
                pos6 += HitEntity.computeBytesConsumed(buf, pos6);
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
        if (this.effects != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.settings != null) {
            nullBits = (byte)(nullBits | 2);
        }
        if (this.rules != null) {
            nullBits = (byte)(nullBits | 4);
        }
        if (this.tags != null) {
            nullBits = (byte)(nullBits | 8);
        }
        if (this.camera != null) {
            nullBits = (byte)(nullBits | 0x10);
        }
        if (this.selector != null) {
            nullBits = (byte)(nullBits | 0x20);
        }
        if (this.hitEntityRules != null) {
            nullBits = (byte)(nullBits | 0x40);
        }
        buf.writeByte(nullBits);
        buf.writeByte(this.waitForDataFrom.getValue());
        buf.writeFloatLE(this.horizontalSpeedMultiplier);
        buf.writeFloatLE(this.runTime);
        buf.writeByte(this.cancelOnItemChange ? 1 : 0);
        buf.writeIntLE(this.next);
        buf.writeIntLE(this.failed);
        buf.writeByte(this.ignoreOwner ? 1 : 0);
        buf.writeIntLE(this.hitEntity);
        buf.writeByte(this.failOn.getValue());
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
        int selectorOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int hitEntityRulesOffsetSlot = buf.writerIndex();
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
            for (HitEntity item : (HitEntity[])this.tags) {
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
        if (this.selector != null) {
            buf.setIntLE(selectorOffsetSlot, buf.writerIndex() - varBlockStart);
            this.selector.serializeWithTypeId(buf);
        } else {
            buf.setIntLE(selectorOffsetSlot, -1);
        }
        if (this.hitEntityRules != null) {
            buf.setIntLE(hitEntityRulesOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.hitEntityRules.length > 4096000) {
                throw ProtocolException.arrayTooLong("HitEntityRules", this.hitEntityRules.length, 4096000);
            }
            VarInt.write(buf, this.hitEntityRules.length);
            for (HitEntity item : this.hitEntityRules) {
                item.serialize(buf);
            }
        } else {
            buf.setIntLE(hitEntityRulesOffsetSlot, -1);
        }
        return buf.writerIndex() - startPos;
    }

    @Override
    public int computeSize() {
        int size = 53;
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
        if (this.selector != null) {
            size += this.selector.computeSizeWithTypeId();
        }
        if (this.hitEntityRules != null) {
            int hitEntityRulesSize = 0;
            for (HitEntity elem : this.hitEntityRules) {
                hitEntityRulesSize += elem.computeSize();
            }
            size += VarInt.size(this.hitEntityRules.length) + hitEntityRulesSize;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int i;
        int pos;
        if (buffer.readableBytes() - offset < 53) {
            return ValidationResult.error("Buffer too small: expected at least 53 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 1) != 0) {
            int effectsOffset = buffer.getIntLE(offset + 25);
            if (effectsOffset < 0) {
                return ValidationResult.error("Invalid offset for Effects");
            }
            pos = offset + 53 + effectsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Effects");
            }
            ValidationResult effectsResult = InteractionEffects.validateStructure(buffer, pos);
            if (!effectsResult.isValid()) {
                return ValidationResult.error("Invalid Effects: " + effectsResult.error());
            }
            pos += InteractionEffects.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits & 2) != 0) {
            int settingsOffset = buffer.getIntLE(offset + 29);
            if (settingsOffset < 0) {
                return ValidationResult.error("Invalid offset for Settings");
            }
            pos = offset + 53 + settingsOffset;
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
        if ((nullBits & 4) != 0) {
            int rulesOffset = buffer.getIntLE(offset + 33);
            if (rulesOffset < 0) {
                return ValidationResult.error("Invalid offset for Rules");
            }
            pos = offset + 53 + rulesOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Rules");
            }
            ValidationResult rulesResult = InteractionRules.validateStructure(buffer, pos);
            if (!rulesResult.isValid()) {
                return ValidationResult.error("Invalid Rules: " + rulesResult.error());
            }
            pos += InteractionRules.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits & 8) != 0) {
            int tagsOffset = buffer.getIntLE(offset + 37);
            if (tagsOffset < 0) {
                return ValidationResult.error("Invalid offset for Tags");
            }
            pos = offset + 53 + tagsOffset;
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
        if ((nullBits & 0x10) != 0) {
            int cameraOffset = buffer.getIntLE(offset + 41);
            if (cameraOffset < 0) {
                return ValidationResult.error("Invalid offset for Camera");
            }
            pos = offset + 53 + cameraOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Camera");
            }
            ValidationResult cameraResult = InteractionCameraSettings.validateStructure(buffer, pos);
            if (!cameraResult.isValid()) {
                return ValidationResult.error("Invalid Camera: " + cameraResult.error());
            }
            pos += InteractionCameraSettings.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits & 0x20) != 0) {
            int selectorOffset = buffer.getIntLE(offset + 45);
            if (selectorOffset < 0) {
                return ValidationResult.error("Invalid offset for Selector");
            }
            pos = offset + 53 + selectorOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Selector");
            }
            ValidationResult selectorResult = Selector.validateStructure(buffer, pos);
            if (!selectorResult.isValid()) {
                return ValidationResult.error("Invalid Selector: " + selectorResult.error());
            }
            pos += Selector.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits & 0x40) != 0) {
            int hitEntityRulesOffset = buffer.getIntLE(offset + 49);
            if (hitEntityRulesOffset < 0) {
                return ValidationResult.error("Invalid offset for HitEntityRules");
            }
            pos = offset + 53 + hitEntityRulesOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for HitEntityRules");
            }
            int hitEntityRulesCount = VarInt.peek(buffer, pos);
            if (hitEntityRulesCount < 0) {
                return ValidationResult.error("Invalid array count for HitEntityRules");
            }
            if (hitEntityRulesCount > 4096000) {
                return ValidationResult.error("HitEntityRules exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < hitEntityRulesCount; ++i) {
                ValidationResult structResult = HitEntity.validateStructure(buffer, pos);
                if (!structResult.isValid()) {
                    return ValidationResult.error("Invalid HitEntity in HitEntityRules[" + i + "]: " + structResult.error());
                }
                pos += HitEntity.computeBytesConsumed(buffer, pos);
            }
        }
        return ValidationResult.OK;
    }

    @Override
    public SelectInteraction clone() {
        SelectInteraction copy = new SelectInteraction();
        copy.waitForDataFrom = this.waitForDataFrom;
        copy.effects = this.effects != null ? this.effects.clone() : null;
        copy.horizontalSpeedMultiplier = this.horizontalSpeedMultiplier;
        copy.runTime = this.runTime;
        copy.cancelOnItemChange = this.cancelOnItemChange;
        if (this.settings != null) {
            HashMap<GameMode, InteractionSettings> m = new HashMap<GameMode, InteractionSettings>();
            for (Map.Entry e2 : this.settings.entrySet()) {
                m.put((GameMode)((Object)e2.getKey()), ((InteractionSettings)e2.getValue()).clone());
            }
            copy.settings = m;
        }
        copy.rules = this.rules != null ? this.rules.clone() : null;
        copy.tags = this.tags != null ? Arrays.copyOf(this.tags, this.tags.length) : null;
        copy.camera = this.camera != null ? this.camera.clone() : null;
        copy.next = this.next;
        copy.failed = this.failed;
        copy.selector = this.selector;
        copy.ignoreOwner = this.ignoreOwner;
        copy.hitEntity = this.hitEntity;
        copy.hitEntityRules = this.hitEntityRules != null ? (HitEntity[])Arrays.stream(this.hitEntityRules).map(e -> e.clone()).toArray(HitEntity[]::new) : null;
        copy.failOn = this.failOn;
        return copy;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SelectInteraction)) {
            return false;
        }
        SelectInteraction other = (SelectInteraction)obj;
        return Objects.equals((Object)this.waitForDataFrom, (Object)other.waitForDataFrom) && Objects.equals(this.effects, other.effects) && this.horizontalSpeedMultiplier == other.horizontalSpeedMultiplier && this.runTime == other.runTime && this.cancelOnItemChange == other.cancelOnItemChange && Objects.equals(this.settings, other.settings) && Objects.equals(this.rules, other.rules) && Arrays.equals(this.tags, other.tags) && Objects.equals(this.camera, other.camera) && this.next == other.next && this.failed == other.failed && Objects.equals(this.selector, other.selector) && this.ignoreOwner == other.ignoreOwner && this.hitEntity == other.hitEntity && Arrays.equals(this.hitEntityRules, other.hitEntityRules) && Objects.equals((Object)this.failOn, (Object)other.failOn);
    }

    @Override
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
        result = 31 * result + Objects.hashCode(this.selector);
        result = 31 * result + Boolean.hashCode(this.ignoreOwner);
        result = 31 * result + Integer.hashCode(this.hitEntity);
        result = 31 * result + Arrays.hashCode(this.hitEntityRules);
        result = 31 * result + Objects.hashCode((Object)this.failOn);
        return result;
    }
}

