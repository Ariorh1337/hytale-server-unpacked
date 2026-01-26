/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.InteractionCameraSettings;
import com.hypixel.hytale.protocol.InteractionEffects;
import com.hypixel.hytale.protocol.InteractionRules;
import com.hypixel.hytale.protocol.InteractionSettings;
import com.hypixel.hytale.protocol.SimpleBlockInteraction;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.protocol.io.PacketIO;
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

public class ChangeStateInteraction
extends SimpleBlockInteraction {
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 20;
    public static final int VARIABLE_FIELD_COUNT = 6;
    public static final int VARIABLE_BLOCK_START = 44;
    public static final int MAX_SIZE = 0x64000000;
    @Nullable
    public Map<String, String> stateChanges;

    public ChangeStateInteraction() {
    }

    public ChangeStateInteraction(@Nonnull WaitForDataFrom waitForDataFrom, @Nullable InteractionEffects effects, float horizontalSpeedMultiplier, float runTime, boolean cancelOnItemChange, @Nullable Map<GameMode, InteractionSettings> settings, @Nullable InteractionRules rules, @Nullable int[] tags, @Nullable InteractionCameraSettings camera, int next, int failed, boolean useLatestTarget, @Nullable Map<String, String> stateChanges) {
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
        this.useLatestTarget = useLatestTarget;
        this.stateChanges = stateChanges;
    }

    public ChangeStateInteraction(@Nonnull ChangeStateInteraction other) {
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
        this.useLatestTarget = other.useLatestTarget;
        this.stateChanges = other.stateChanges;
    }

    @Nonnull
    public static ChangeStateInteraction deserialize(@Nonnull ByteBuf buf, int offset) {
        int i;
        int dictPos;
        int varIntLen;
        ChangeStateInteraction obj = new ChangeStateInteraction();
        byte nullBits = buf.getByte(offset);
        obj.waitForDataFrom = WaitForDataFrom.fromValue(buf.getByte(offset + 1));
        obj.horizontalSpeedMultiplier = buf.getFloatLE(offset + 2);
        obj.runTime = buf.getFloatLE(offset + 6);
        obj.cancelOnItemChange = buf.getByte(offset + 10) != 0;
        obj.next = buf.getIntLE(offset + 11);
        obj.failed = buf.getIntLE(offset + 15);
        boolean bl = obj.useLatestTarget = buf.getByte(offset + 19) != 0;
        if ((nullBits & 1) != 0) {
            int varPos0 = offset + 44 + buf.getIntLE(offset + 20);
            obj.effects = InteractionEffects.deserialize(buf, varPos0);
        }
        if ((nullBits & 2) != 0) {
            int varPos1 = offset + 44 + buf.getIntLE(offset + 24);
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
        if ((nullBits & 4) != 0) {
            int varPos2 = offset + 44 + buf.getIntLE(offset + 28);
            obj.rules = InteractionRules.deserialize(buf, varPos2);
        }
        if ((nullBits & 8) != 0) {
            int varPos3 = offset + 44 + buf.getIntLE(offset + 32);
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
            int varPos4 = offset + 44 + buf.getIntLE(offset + 36);
            obj.camera = InteractionCameraSettings.deserialize(buf, varPos4);
        }
        if ((nullBits & 0x20) != 0) {
            int varPos5 = offset + 44 + buf.getIntLE(offset + 40);
            int stateChangesCount = VarInt.peek(buf, varPos5);
            if (stateChangesCount < 0) {
                throw ProtocolException.negativeLength("StateChanges", stateChangesCount);
            }
            if (stateChangesCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("StateChanges", stateChangesCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos5);
            obj.stateChanges = new HashMap<String, String>(stateChangesCount);
            dictPos = varPos5 + varIntLen;
            for (i = 0; i < stateChangesCount; ++i) {
                int keyLen = VarInt.peek(buf, dictPos);
                if (keyLen < 0) {
                    throw ProtocolException.negativeLength("key", keyLen);
                }
                if (keyLen > 4096000) {
                    throw ProtocolException.stringTooLong("key", keyLen, 4096000);
                }
                int keyVarLen = VarInt.length(buf, dictPos);
                String key = PacketIO.readVarString(buf, dictPos);
                int valLen = VarInt.peek(buf, dictPos += keyVarLen + keyLen);
                if (valLen < 0) {
                    throw ProtocolException.negativeLength("val", valLen);
                }
                if (valLen > 4096000) {
                    throw ProtocolException.stringTooLong("val", valLen, 4096000);
                }
                int valVarLen = VarInt.length(buf, dictPos);
                String val = PacketIO.readVarString(buf, dictPos);
                dictPos += valVarLen + valLen;
                if (obj.stateChanges.put(key, val) == null) continue;
                throw ProtocolException.duplicateKey("stateChanges", key);
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int i;
        int dictLen;
        byte nullBits = buf.getByte(offset);
        int maxEnd = 44;
        if ((nullBits & 1) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 20);
            int pos0 = offset + 44 + fieldOffset0;
            if ((pos0 += InteractionEffects.computeBytesConsumed(buf, pos0)) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 2) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 24);
            int pos1 = offset + 44 + fieldOffset1;
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
        if ((nullBits & 4) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 28);
            int pos2 = offset + 44 + fieldOffset2;
            if ((pos2 += InteractionRules.computeBytesConsumed(buf, pos2)) - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        if ((nullBits & 8) != 0) {
            int fieldOffset3 = buf.getIntLE(offset + 32);
            int pos3 = offset + 44 + fieldOffset3;
            int arrLen = VarInt.peek(buf, pos3);
            if ((pos3 += VarInt.length(buf, pos3) + arrLen * 4) - offset > maxEnd) {
                maxEnd = pos3 - offset;
            }
        }
        if ((nullBits & 0x10) != 0) {
            int fieldOffset4 = buf.getIntLE(offset + 36);
            int pos4 = offset + 44 + fieldOffset4;
            if ((pos4 += InteractionCameraSettings.computeBytesConsumed(buf, pos4)) - offset > maxEnd) {
                maxEnd = pos4 - offset;
            }
        }
        if ((nullBits & 0x20) != 0) {
            int fieldOffset5 = buf.getIntLE(offset + 40);
            int pos5 = offset + 44 + fieldOffset5;
            dictLen = VarInt.peek(buf, pos5);
            pos5 += VarInt.length(buf, pos5);
            for (i = 0; i < dictLen; ++i) {
                int sl = VarInt.peek(buf, pos5);
                pos5 += VarInt.length(buf, pos5) + sl;
                sl = VarInt.peek(buf, pos5);
                pos5 += VarInt.length(buf, pos5) + sl;
            }
            if (pos5 - offset > maxEnd) {
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
        if (this.stateChanges != null) {
            nullBits = (byte)(nullBits | 0x20);
        }
        buf.writeByte(nullBits);
        buf.writeByte(this.waitForDataFrom.getValue());
        buf.writeFloatLE(this.horizontalSpeedMultiplier);
        buf.writeFloatLE(this.runTime);
        buf.writeByte(this.cancelOnItemChange ? 1 : 0);
        buf.writeIntLE(this.next);
        buf.writeIntLE(this.failed);
        buf.writeByte(this.useLatestTarget ? 1 : 0);
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
        int stateChangesOffsetSlot = buf.writerIndex();
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
            for (Object item : (Iterator<Map.Entry<String, String>>)this.tags) {
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
        if (this.stateChanges != null) {
            buf.setIntLE(stateChangesOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.stateChanges.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("StateChanges", this.stateChanges.size(), 4096000);
            }
            VarInt.write(buf, this.stateChanges.size());
            for (Map.Entry<String, String> e : this.stateChanges.entrySet()) {
                PacketIO.writeVarString(buf, e.getKey(), 4096000);
                PacketIO.writeVarString(buf, e.getValue(), 4096000);
            }
        } else {
            buf.setIntLE(stateChangesOffsetSlot, -1);
        }
        return buf.writerIndex() - startPos;
    }

    @Override
    public int computeSize() {
        int size = 44;
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
        if (this.stateChanges != null) {
            int stateChangesSize = 0;
            for (Map.Entry<String, String> kvp : this.stateChanges.entrySet()) {
                stateChangesSize += PacketIO.stringSize(kvp.getKey()) + PacketIO.stringSize(kvp.getValue());
            }
            size += VarInt.size(this.stateChanges.size()) + stateChangesSize;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int i;
        int pos;
        if (buffer.readableBytes() - offset < 44) {
            return ValidationResult.error("Buffer too small: expected at least 44 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 1) != 0) {
            int effectsOffset = buffer.getIntLE(offset + 20);
            if (effectsOffset < 0) {
                return ValidationResult.error("Invalid offset for Effects");
            }
            pos = offset + 44 + effectsOffset;
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
            int settingsOffset = buffer.getIntLE(offset + 24);
            if (settingsOffset < 0) {
                return ValidationResult.error("Invalid offset for Settings");
            }
            pos = offset + 44 + settingsOffset;
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
            int rulesOffset = buffer.getIntLE(offset + 28);
            if (rulesOffset < 0) {
                return ValidationResult.error("Invalid offset for Rules");
            }
            pos = offset + 44 + rulesOffset;
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
            int tagsOffset = buffer.getIntLE(offset + 32);
            if (tagsOffset < 0) {
                return ValidationResult.error("Invalid offset for Tags");
            }
            pos = offset + 44 + tagsOffset;
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
            int cameraOffset = buffer.getIntLE(offset + 36);
            if (cameraOffset < 0) {
                return ValidationResult.error("Invalid offset for Camera");
            }
            pos = offset + 44 + cameraOffset;
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
            int stateChangesOffset = buffer.getIntLE(offset + 40);
            if (stateChangesOffset < 0) {
                return ValidationResult.error("Invalid offset for StateChanges");
            }
            pos = offset + 44 + stateChangesOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for StateChanges");
            }
            int stateChangesCount = VarInt.peek(buffer, pos);
            if (stateChangesCount < 0) {
                return ValidationResult.error("Invalid dictionary count for StateChanges");
            }
            if (stateChangesCount > 4096000) {
                return ValidationResult.error("StateChanges exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < stateChangesCount; ++i) {
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
                int valueLen = VarInt.peek(buffer, pos);
                if (valueLen < 0) {
                    return ValidationResult.error("Invalid string length for value");
                }
                if (valueLen > 4096000) {
                    return ValidationResult.error("value exceeds max length 4096000");
                }
                pos += VarInt.length(buffer, pos);
                if ((pos += valueLen) <= buffer.writerIndex()) continue;
                return ValidationResult.error("Buffer overflow reading value");
            }
        }
        return ValidationResult.OK;
    }

    @Override
    public ChangeStateInteraction clone() {
        ChangeStateInteraction copy = new ChangeStateInteraction();
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
        copy.next = this.next;
        copy.failed = this.failed;
        copy.useLatestTarget = this.useLatestTarget;
        copy.stateChanges = this.stateChanges != null ? new HashMap<String, String>(this.stateChanges) : null;
        return copy;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ChangeStateInteraction)) {
            return false;
        }
        ChangeStateInteraction other = (ChangeStateInteraction)obj;
        return Objects.equals((Object)this.waitForDataFrom, (Object)other.waitForDataFrom) && Objects.equals(this.effects, other.effects) && this.horizontalSpeedMultiplier == other.horizontalSpeedMultiplier && this.runTime == other.runTime && this.cancelOnItemChange == other.cancelOnItemChange && Objects.equals(this.settings, other.settings) && Objects.equals(this.rules, other.rules) && Arrays.equals(this.tags, other.tags) && Objects.equals(this.camera, other.camera) && this.next == other.next && this.failed == other.failed && this.useLatestTarget == other.useLatestTarget && Objects.equals(this.stateChanges, other.stateChanges);
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
        result = 31 * result + Boolean.hashCode(this.useLatestTarget);
        result = 31 * result + Objects.hashCode(this.stateChanges);
        return result;
    }
}

