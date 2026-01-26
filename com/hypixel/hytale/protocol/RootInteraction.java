/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.InteractionCooldown;
import com.hypixel.hytale.protocol.InteractionRules;
import com.hypixel.hytale.protocol.RootInteractionSettings;
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

public class RootInteraction {
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 6;
    public static final int VARIABLE_FIELD_COUNT = 6;
    public static final int VARIABLE_BLOCK_START = 30;
    public static final int MAX_SIZE = 0x64000000;
    @Nullable
    public String id;
    @Nullable
    public int[] interactions;
    @Nullable
    public InteractionCooldown cooldown;
    @Nullable
    public Map<GameMode, RootInteractionSettings> settings;
    @Nullable
    public InteractionRules rules;
    @Nullable
    public int[] tags;
    public float clickQueuingTimeout;
    public boolean requireNewClick;

    public RootInteraction() {
    }

    public RootInteraction(@Nullable String id, @Nullable int[] interactions, @Nullable InteractionCooldown cooldown, @Nullable Map<GameMode, RootInteractionSettings> settings, @Nullable InteractionRules rules, @Nullable int[] tags, float clickQueuingTimeout, boolean requireNewClick) {
        this.id = id;
        this.interactions = interactions;
        this.cooldown = cooldown;
        this.settings = settings;
        this.rules = rules;
        this.tags = tags;
        this.clickQueuingTimeout = clickQueuingTimeout;
        this.requireNewClick = requireNewClick;
    }

    public RootInteraction(@Nonnull RootInteraction other) {
        this.id = other.id;
        this.interactions = other.interactions;
        this.cooldown = other.cooldown;
        this.settings = other.settings;
        this.rules = other.rules;
        this.tags = other.tags;
        this.clickQueuingTimeout = other.clickQueuingTimeout;
        this.requireNewClick = other.requireNewClick;
    }

    @Nonnull
    public static RootInteraction deserialize(@Nonnull ByteBuf buf, int offset) {
        int i;
        int varIntLen;
        RootInteraction obj = new RootInteraction();
        byte nullBits = buf.getByte(offset);
        obj.clickQueuingTimeout = buf.getFloatLE(offset + 1);
        boolean bl = obj.requireNewClick = buf.getByte(offset + 5) != 0;
        if ((nullBits & 1) != 0) {
            int varPos0 = offset + 30 + buf.getIntLE(offset + 6);
            int idLen = VarInt.peek(buf, varPos0);
            if (idLen < 0) {
                throw ProtocolException.negativeLength("Id", idLen);
            }
            if (idLen > 4096000) {
                throw ProtocolException.stringTooLong("Id", idLen, 4096000);
            }
            obj.id = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
        }
        if ((nullBits & 2) != 0) {
            int varPos1 = offset + 30 + buf.getIntLE(offset + 10);
            int interactionsCount = VarInt.peek(buf, varPos1);
            if (interactionsCount < 0) {
                throw ProtocolException.negativeLength("Interactions", interactionsCount);
            }
            if (interactionsCount > 4096000) {
                throw ProtocolException.arrayTooLong("Interactions", interactionsCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos1);
            if ((long)(varPos1 + varIntLen) + (long)interactionsCount * 4L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("Interactions", varPos1 + varIntLen + interactionsCount * 4, buf.readableBytes());
            }
            obj.interactions = new int[interactionsCount];
            for (i = 0; i < interactionsCount; ++i) {
                obj.interactions[i] = buf.getIntLE(varPos1 + varIntLen + i * 4);
            }
        }
        if ((nullBits & 4) != 0) {
            int varPos2 = offset + 30 + buf.getIntLE(offset + 14);
            obj.cooldown = InteractionCooldown.deserialize(buf, varPos2);
        }
        if ((nullBits & 8) != 0) {
            int varPos3 = offset + 30 + buf.getIntLE(offset + 18);
            int settingsCount = VarInt.peek(buf, varPos3);
            if (settingsCount < 0) {
                throw ProtocolException.negativeLength("Settings", settingsCount);
            }
            if (settingsCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Settings", settingsCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos3);
            obj.settings = new HashMap<GameMode, RootInteractionSettings>(settingsCount);
            int dictPos = varPos3 + varIntLen;
            for (int i2 = 0; i2 < settingsCount; ++i2) {
                GameMode key = GameMode.fromValue(buf.getByte(dictPos));
                RootInteractionSettings val = RootInteractionSettings.deserialize(buf, ++dictPos);
                dictPos += RootInteractionSettings.computeBytesConsumed(buf, dictPos);
                if (obj.settings.put(key, val) == null) continue;
                throw ProtocolException.duplicateKey("settings", (Object)key);
            }
        }
        if ((nullBits & 0x10) != 0) {
            int varPos4 = offset + 30 + buf.getIntLE(offset + 22);
            obj.rules = InteractionRules.deserialize(buf, varPos4);
        }
        if ((nullBits & 0x20) != 0) {
            int varPos5 = offset + 30 + buf.getIntLE(offset + 26);
            int tagsCount = VarInt.peek(buf, varPos5);
            if (tagsCount < 0) {
                throw ProtocolException.negativeLength("Tags", tagsCount);
            }
            if (tagsCount > 4096000) {
                throw ProtocolException.arrayTooLong("Tags", tagsCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos5);
            if ((long)(varPos5 + varIntLen) + (long)tagsCount * 4L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("Tags", varPos5 + varIntLen + tagsCount * 4, buf.readableBytes());
            }
            obj.tags = new int[tagsCount];
            for (i = 0; i < tagsCount; ++i) {
                obj.tags[i] = buf.getIntLE(varPos5 + varIntLen + i * 4);
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int arrLen;
        byte nullBits = buf.getByte(offset);
        int maxEnd = 30;
        if ((nullBits & 1) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 6);
            int pos0 = offset + 30 + fieldOffset0;
            int sl = VarInt.peek(buf, pos0);
            if ((pos0 += VarInt.length(buf, pos0) + sl) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 2) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 10);
            int pos1 = offset + 30 + fieldOffset1;
            arrLen = VarInt.peek(buf, pos1);
            if ((pos1 += VarInt.length(buf, pos1) + arrLen * 4) - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        if ((nullBits & 4) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 14);
            int pos2 = offset + 30 + fieldOffset2;
            if ((pos2 += InteractionCooldown.computeBytesConsumed(buf, pos2)) - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        if ((nullBits & 8) != 0) {
            int fieldOffset3 = buf.getIntLE(offset + 18);
            int pos3 = offset + 30 + fieldOffset3;
            int dictLen = VarInt.peek(buf, pos3);
            pos3 += VarInt.length(buf, pos3);
            for (int i = 0; i < dictLen; ++i) {
                ++pos3;
                pos3 += RootInteractionSettings.computeBytesConsumed(buf, pos3);
            }
            if (pos3 - offset > maxEnd) {
                maxEnd = pos3 - offset;
            }
        }
        if ((nullBits & 0x10) != 0) {
            int fieldOffset4 = buf.getIntLE(offset + 22);
            int pos4 = offset + 30 + fieldOffset4;
            if ((pos4 += InteractionRules.computeBytesConsumed(buf, pos4)) - offset > maxEnd) {
                maxEnd = pos4 - offset;
            }
        }
        if ((nullBits & 0x20) != 0) {
            int fieldOffset5 = buf.getIntLE(offset + 26);
            int pos5 = offset + 30 + fieldOffset5;
            arrLen = VarInt.peek(buf, pos5);
            if ((pos5 += VarInt.length(buf, pos5) + arrLen * 4) - offset > maxEnd) {
                maxEnd = pos5 - offset;
            }
        }
        return maxEnd;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        byte nullBits = 0;
        if (this.id != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.interactions != null) {
            nullBits = (byte)(nullBits | 2);
        }
        if (this.cooldown != null) {
            nullBits = (byte)(nullBits | 4);
        }
        if (this.settings != null) {
            nullBits = (byte)(nullBits | 8);
        }
        if (this.rules != null) {
            nullBits = (byte)(nullBits | 0x10);
        }
        if (this.tags != null) {
            nullBits = (byte)(nullBits | 0x20);
        }
        buf.writeByte(nullBits);
        buf.writeFloatLE(this.clickQueuingTimeout);
        buf.writeByte(this.requireNewClick ? 1 : 0);
        int idOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int interactionsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int cooldownOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int settingsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int rulesOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int tagsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.id != null) {
            buf.setIntLE(idOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.id, 4096000);
        } else {
            buf.setIntLE(idOffsetSlot, -1);
        }
        if (this.interactions != null) {
            buf.setIntLE(interactionsOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.interactions.length > 4096000) {
                throw ProtocolException.arrayTooLong("Interactions", this.interactions.length, 4096000);
            }
            VarInt.write(buf, this.interactions.length);
            for (int item : this.interactions) {
                buf.writeIntLE(item);
            }
        } else {
            buf.setIntLE(interactionsOffsetSlot, -1);
        }
        if (this.cooldown != null) {
            buf.setIntLE(cooldownOffsetSlot, buf.writerIndex() - varBlockStart);
            this.cooldown.serialize(buf);
        } else {
            buf.setIntLE(cooldownOffsetSlot, -1);
        }
        if (this.settings != null) {
            buf.setIntLE(settingsOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.settings.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Settings", this.settings.size(), 4096000);
            }
            VarInt.write(buf, this.settings.size());
            for (Map.Entry<GameMode, RootInteractionSettings> e : this.settings.entrySet()) {
                buf.writeByte(e.getKey().getValue());
                e.getValue().serialize(buf);
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
            for (int item : this.tags) {
                buf.writeIntLE(item);
            }
        } else {
            buf.setIntLE(tagsOffsetSlot, -1);
        }
    }

    public int computeSize() {
        int size = 30;
        if (this.id != null) {
            size += PacketIO.stringSize(this.id);
        }
        if (this.interactions != null) {
            size += VarInt.size(this.interactions.length) + this.interactions.length * 4;
        }
        if (this.cooldown != null) {
            size += this.cooldown.computeSize();
        }
        if (this.settings != null) {
            int settingsSize = 0;
            for (Map.Entry<GameMode, RootInteractionSettings> kvp : this.settings.entrySet()) {
                settingsSize += 1 + kvp.getValue().computeSize();
            }
            size += VarInt.size(this.settings.size()) + settingsSize;
        }
        if (this.rules != null) {
            size += this.rules.computeSize();
        }
        if (this.tags != null) {
            size += VarInt.size(this.tags.length) + this.tags.length * 4;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int pos;
        if (buffer.readableBytes() - offset < 30) {
            return ValidationResult.error("Buffer too small: expected at least 30 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 1) != 0) {
            int idOffset = buffer.getIntLE(offset + 6);
            if (idOffset < 0) {
                return ValidationResult.error("Invalid offset for Id");
            }
            pos = offset + 30 + idOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Id");
            }
            int idLen = VarInt.peek(buffer, pos);
            if (idLen < 0) {
                return ValidationResult.error("Invalid string length for Id");
            }
            if (idLen > 4096000) {
                return ValidationResult.error("Id exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += idLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading Id");
            }
        }
        if ((nullBits & 2) != 0) {
            int interactionsOffset = buffer.getIntLE(offset + 10);
            if (interactionsOffset < 0) {
                return ValidationResult.error("Invalid offset for Interactions");
            }
            pos = offset + 30 + interactionsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Interactions");
            }
            int interactionsCount = VarInt.peek(buffer, pos);
            if (interactionsCount < 0) {
                return ValidationResult.error("Invalid array count for Interactions");
            }
            if (interactionsCount > 4096000) {
                return ValidationResult.error("Interactions exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += interactionsCount * 4) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading Interactions");
            }
        }
        if ((nullBits & 4) != 0) {
            int cooldownOffset = buffer.getIntLE(offset + 14);
            if (cooldownOffset < 0) {
                return ValidationResult.error("Invalid offset for Cooldown");
            }
            pos = offset + 30 + cooldownOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Cooldown");
            }
            ValidationResult cooldownResult = InteractionCooldown.validateStructure(buffer, pos);
            if (!cooldownResult.isValid()) {
                return ValidationResult.error("Invalid Cooldown: " + cooldownResult.error());
            }
            pos += InteractionCooldown.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits & 8) != 0) {
            int settingsOffset = buffer.getIntLE(offset + 18);
            if (settingsOffset < 0) {
                return ValidationResult.error("Invalid offset for Settings");
            }
            pos = offset + 30 + settingsOffset;
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
                pos += RootInteractionSettings.computeBytesConsumed(buffer, pos);
            }
        }
        if ((nullBits & 0x10) != 0) {
            int rulesOffset = buffer.getIntLE(offset + 22);
            if (rulesOffset < 0) {
                return ValidationResult.error("Invalid offset for Rules");
            }
            pos = offset + 30 + rulesOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Rules");
            }
            ValidationResult rulesResult = InteractionRules.validateStructure(buffer, pos);
            if (!rulesResult.isValid()) {
                return ValidationResult.error("Invalid Rules: " + rulesResult.error());
            }
            pos += InteractionRules.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits & 0x20) != 0) {
            int tagsOffset = buffer.getIntLE(offset + 26);
            if (tagsOffset < 0) {
                return ValidationResult.error("Invalid offset for Tags");
            }
            pos = offset + 30 + tagsOffset;
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
        return ValidationResult.OK;
    }

    public RootInteraction clone() {
        RootInteraction copy = new RootInteraction();
        copy.id = this.id;
        copy.interactions = this.interactions != null ? Arrays.copyOf(this.interactions, this.interactions.length) : null;
        InteractionCooldown interactionCooldown = copy.cooldown = this.cooldown != null ? this.cooldown.clone() : null;
        if (this.settings != null) {
            HashMap<GameMode, RootInteractionSettings> m = new HashMap<GameMode, RootInteractionSettings>();
            for (Map.Entry<GameMode, RootInteractionSettings> e : this.settings.entrySet()) {
                m.put(e.getKey(), e.getValue().clone());
            }
            copy.settings = m;
        }
        copy.rules = this.rules != null ? this.rules.clone() : null;
        copy.tags = this.tags != null ? Arrays.copyOf(this.tags, this.tags.length) : null;
        copy.clickQueuingTimeout = this.clickQueuingTimeout;
        copy.requireNewClick = this.requireNewClick;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RootInteraction)) {
            return false;
        }
        RootInteraction other = (RootInteraction)obj;
        return Objects.equals(this.id, other.id) && Arrays.equals(this.interactions, other.interactions) && Objects.equals(this.cooldown, other.cooldown) && Objects.equals(this.settings, other.settings) && Objects.equals(this.rules, other.rules) && Arrays.equals(this.tags, other.tags) && this.clickQueuingTimeout == other.clickQueuingTimeout && this.requireNewClick == other.requireNewClick;
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + Objects.hashCode(this.id);
        result = 31 * result + Arrays.hashCode(this.interactions);
        result = 31 * result + Objects.hashCode(this.cooldown);
        result = 31 * result + Objects.hashCode(this.settings);
        result = 31 * result + Objects.hashCode(this.rules);
        result = 31 * result + Arrays.hashCode(this.tags);
        result = 31 * result + Float.hashCode(this.clickQueuingTimeout);
        result = 31 * result + Boolean.hashCode(this.requireNewClick);
        return result;
    }
}

