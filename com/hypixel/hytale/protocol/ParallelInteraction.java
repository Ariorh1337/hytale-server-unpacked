/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.Interaction;
import com.hypixel.hytale.protocol.InteractionCameraSettings;
import com.hypixel.hytale.protocol.InteractionEffects;
import com.hypixel.hytale.protocol.InteractionRules;
import com.hypixel.hytale.protocol.InteractionSettings;
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

public class ParallelInteraction
extends Interaction {
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 11;
    public static final int VARIABLE_FIELD_COUNT = 6;
    public static final int VARIABLE_BLOCK_START = 35;
    public static final int MAX_SIZE = 0x64000000;
    @Nullable
    public int[] next;

    public ParallelInteraction() {
    }

    public ParallelInteraction(@Nonnull WaitForDataFrom waitForDataFrom, @Nullable InteractionEffects effects, float horizontalSpeedMultiplier, float runTime, boolean cancelOnItemChange, @Nullable Map<GameMode, InteractionSettings> settings, @Nullable InteractionRules rules, @Nullable int[] tags, @Nullable InteractionCameraSettings camera, @Nullable int[] next) {
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
    }

    public ParallelInteraction(@Nonnull ParallelInteraction other) {
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
    }

    @Nonnull
    public static ParallelInteraction deserialize(@Nonnull ByteBuf buf, int offset) {
        int i;
        int varIntLen;
        ParallelInteraction obj = new ParallelInteraction();
        byte nullBits = buf.getByte(offset);
        obj.waitForDataFrom = WaitForDataFrom.fromValue(buf.getByte(offset + 1));
        obj.horizontalSpeedMultiplier = buf.getFloatLE(offset + 2);
        obj.runTime = buf.getFloatLE(offset + 6);
        boolean bl = obj.cancelOnItemChange = buf.getByte(offset + 10) != 0;
        if ((nullBits & 1) != 0) {
            int varPos0 = offset + 35 + buf.getIntLE(offset + 11);
            obj.effects = InteractionEffects.deserialize(buf, varPos0);
        }
        if ((nullBits & 2) != 0) {
            int varPos1 = offset + 35 + buf.getIntLE(offset + 15);
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
            for (int i2 = 0; i2 < settingsCount; ++i2) {
                GameMode key = GameMode.fromValue(buf.getByte(dictPos));
                InteractionSettings val = InteractionSettings.deserialize(buf, ++dictPos);
                dictPos += InteractionSettings.computeBytesConsumed(buf, dictPos);
                if (obj.settings.put(key, val) == null) continue;
                throw ProtocolException.duplicateKey("settings", (Object)key);
            }
        }
        if ((nullBits & 4) != 0) {
            int varPos2 = offset + 35 + buf.getIntLE(offset + 19);
            obj.rules = InteractionRules.deserialize(buf, varPos2);
        }
        if ((nullBits & 8) != 0) {
            int varPos3 = offset + 35 + buf.getIntLE(offset + 23);
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
            for (i = 0; i < tagsCount; ++i) {
                obj.tags[i] = buf.getIntLE(varPos3 + varIntLen + i * 4);
            }
        }
        if ((nullBits & 0x10) != 0) {
            int varPos4 = offset + 35 + buf.getIntLE(offset + 27);
            obj.camera = InteractionCameraSettings.deserialize(buf, varPos4);
        }
        if ((nullBits & 0x20) != 0) {
            int varPos5 = offset + 35 + buf.getIntLE(offset + 31);
            int nextCount = VarInt.peek(buf, varPos5);
            if (nextCount < 0) {
                throw ProtocolException.negativeLength("Next", nextCount);
            }
            if (nextCount > 4096000) {
                throw ProtocolException.arrayTooLong("Next", nextCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos5);
            if ((long)(varPos5 + varIntLen) + (long)nextCount * 4L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("Next", varPos5 + varIntLen + nextCount * 4, buf.readableBytes());
            }
            obj.next = new int[nextCount];
            for (i = 0; i < nextCount; ++i) {
                obj.next[i] = buf.getIntLE(varPos5 + varIntLen + i * 4);
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int arrLen;
        byte nullBits = buf.getByte(offset);
        int maxEnd = 35;
        if ((nullBits & 1) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 11);
            int pos0 = offset + 35 + fieldOffset0;
            if ((pos0 += InteractionEffects.computeBytesConsumed(buf, pos0)) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 2) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 15);
            int pos1 = offset + 35 + fieldOffset1;
            int dictLen = VarInt.peek(buf, pos1);
            pos1 += VarInt.length(buf, pos1);
            for (int i = 0; i < dictLen; ++i) {
                ++pos1;
                pos1 += InteractionSettings.computeBytesConsumed(buf, pos1);
            }
            if (pos1 - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        if ((nullBits & 4) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 19);
            int pos2 = offset + 35 + fieldOffset2;
            if ((pos2 += InteractionRules.computeBytesConsumed(buf, pos2)) - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        if ((nullBits & 8) != 0) {
            int fieldOffset3 = buf.getIntLE(offset + 23);
            int pos3 = offset + 35 + fieldOffset3;
            arrLen = VarInt.peek(buf, pos3);
            if ((pos3 += VarInt.length(buf, pos3) + arrLen * 4) - offset > maxEnd) {
                maxEnd = pos3 - offset;
            }
        }
        if ((nullBits & 0x10) != 0) {
            int fieldOffset4 = buf.getIntLE(offset + 27);
            int pos4 = offset + 35 + fieldOffset4;
            if ((pos4 += InteractionCameraSettings.computeBytesConsumed(buf, pos4)) - offset > maxEnd) {
                maxEnd = pos4 - offset;
            }
        }
        if ((nullBits & 0x20) != 0) {
            int fieldOffset5 = buf.getIntLE(offset + 31);
            int pos5 = offset + 35 + fieldOffset5;
            arrLen = VarInt.peek(buf, pos5);
            if ((pos5 += VarInt.length(buf, pos5) + arrLen * 4) - offset > maxEnd) {
                maxEnd = pos5 - offset;
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
        if (this.next != null) {
            nullBits = (byte)(nullBits | 0x20);
        }
        buf.writeByte(nullBits);
        buf.writeByte(this.waitForDataFrom.getValue());
        buf.writeFloatLE(this.horizontalSpeedMultiplier);
        buf.writeFloatLE(this.runTime);
        buf.writeByte(this.cancelOnItemChange ? 1 : 0);
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
        int nextOffsetSlot = buf.writerIndex();
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
        if (this.next != null) {
            buf.setIntLE(nextOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.next.length > 4096000) {
                throw ProtocolException.arrayTooLong("Next", this.next.length, 4096000);
            }
            VarInt.write(buf, this.next.length);
            for (Object item : (Object)this.next) {
                buf.writeIntLE((int)item);
            }
        } else {
            buf.setIntLE(nextOffsetSlot, -1);
        }
        return buf.writerIndex() - startPos;
    }

    @Override
    public int computeSize() {
        int size = 35;
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
        if (this.next != null) {
            size += VarInt.size(this.next.length) + this.next.length * 4;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int pos;
        if (buffer.readableBytes() - offset < 35) {
            return ValidationResult.error("Buffer too small: expected at least 35 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 1) != 0) {
            int effectsOffset = buffer.getIntLE(offset + 11);
            if (effectsOffset < 0) {
                return ValidationResult.error("Invalid offset for Effects");
            }
            pos = offset + 35 + effectsOffset;
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
            int settingsOffset = buffer.getIntLE(offset + 15);
            if (settingsOffset < 0) {
                return ValidationResult.error("Invalid offset for Settings");
            }
            pos = offset + 35 + settingsOffset;
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
            for (int i = 0; i < settingsCount; ++i) {
                ++pos;
                ++pos;
            }
        }
        if ((nullBits & 4) != 0) {
            int rulesOffset = buffer.getIntLE(offset + 19);
            if (rulesOffset < 0) {
                return ValidationResult.error("Invalid offset for Rules");
            }
            pos = offset + 35 + rulesOffset;
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
            int tagsOffset = buffer.getIntLE(offset + 23);
            if (tagsOffset < 0) {
                return ValidationResult.error("Invalid offset for Tags");
            }
            pos = offset + 35 + tagsOffset;
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
            int cameraOffset = buffer.getIntLE(offset + 27);
            if (cameraOffset < 0) {
                return ValidationResult.error("Invalid offset for Camera");
            }
            pos = offset + 35 + cameraOffset;
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
            int nextOffset = buffer.getIntLE(offset + 31);
            if (nextOffset < 0) {
                return ValidationResult.error("Invalid offset for Next");
            }
            pos = offset + 35 + nextOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Next");
            }
            int nextCount = VarInt.peek(buffer, pos);
            if (nextCount < 0) {
                return ValidationResult.error("Invalid array count for Next");
            }
            if (nextCount > 4096000) {
                return ValidationResult.error("Next exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += nextCount * 4) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading Next");
            }
        }
        return ValidationResult.OK;
    }

    public ParallelInteraction clone() {
        ParallelInteraction copy = new ParallelInteraction();
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
        copy.next = this.next != null ? Arrays.copyOf(this.next, this.next.length) : null;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ParallelInteraction)) {
            return false;
        }
        ParallelInteraction other = (ParallelInteraction)obj;
        return Objects.equals((Object)this.waitForDataFrom, (Object)other.waitForDataFrom) && Objects.equals(this.effects, other.effects) && this.horizontalSpeedMultiplier == other.horizontalSpeedMultiplier && this.runTime == other.runTime && this.cancelOnItemChange == other.cancelOnItemChange && Objects.equals(this.settings, other.settings) && Objects.equals(this.rules, other.rules) && Arrays.equals(this.tags, other.tags) && Objects.equals(this.camera, other.camera) && Arrays.equals(this.next, other.next);
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
        result = 31 * result + Arrays.hashCode(this.next);
        return result;
    }
}

