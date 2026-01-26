/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.InteractionPriority;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InteractionConfiguration {
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 4;
    public static final int VARIABLE_FIELD_COUNT = 2;
    public static final int VARIABLE_BLOCK_START = 12;
    public static final int MAX_SIZE = 0x64000000;
    public boolean displayOutlines = true;
    public boolean debugOutlines;
    @Nullable
    public Map<GameMode, Float> useDistance;
    public boolean allEntities;
    @Nullable
    public Map<InteractionType, InteractionPriority> priorities;

    public InteractionConfiguration() {
    }

    public InteractionConfiguration(boolean displayOutlines, boolean debugOutlines, @Nullable Map<GameMode, Float> useDistance, boolean allEntities, @Nullable Map<InteractionType, InteractionPriority> priorities) {
        this.displayOutlines = displayOutlines;
        this.debugOutlines = debugOutlines;
        this.useDistance = useDistance;
        this.allEntities = allEntities;
        this.priorities = priorities;
    }

    public InteractionConfiguration(@Nonnull InteractionConfiguration other) {
        this.displayOutlines = other.displayOutlines;
        this.debugOutlines = other.debugOutlines;
        this.useDistance = other.useDistance;
        this.allEntities = other.allEntities;
        this.priorities = other.priorities;
    }

    @Nonnull
    public static InteractionConfiguration deserialize(@Nonnull ByteBuf buf, int offset) {
        Enum key;
        int i;
        int dictPos;
        int varIntLen;
        InteractionConfiguration obj = new InteractionConfiguration();
        byte nullBits = buf.getByte(offset);
        obj.displayOutlines = buf.getByte(offset + 1) != 0;
        obj.debugOutlines = buf.getByte(offset + 2) != 0;
        boolean bl = obj.allEntities = buf.getByte(offset + 3) != 0;
        if ((nullBits & 1) != 0) {
            int varPos0 = offset + 12 + buf.getIntLE(offset + 4);
            int useDistanceCount = VarInt.peek(buf, varPos0);
            if (useDistanceCount < 0) {
                throw ProtocolException.negativeLength("UseDistance", useDistanceCount);
            }
            if (useDistanceCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("UseDistance", useDistanceCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos0);
            obj.useDistance = new HashMap<GameMode, Float>(useDistanceCount);
            dictPos = varPos0 + varIntLen;
            for (i = 0; i < useDistanceCount; ++i) {
                key = GameMode.fromValue(buf.getByte(dictPos));
                float val = buf.getFloatLE(++dictPos);
                dictPos += 4;
                if (obj.useDistance.put((GameMode)key, Float.valueOf(val)) == null) continue;
                throw ProtocolException.duplicateKey("useDistance", key);
            }
        }
        if ((nullBits & 2) != 0) {
            int varPos1 = offset + 12 + buf.getIntLE(offset + 8);
            int prioritiesCount = VarInt.peek(buf, varPos1);
            if (prioritiesCount < 0) {
                throw ProtocolException.negativeLength("Priorities", prioritiesCount);
            }
            if (prioritiesCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Priorities", prioritiesCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos1);
            obj.priorities = new HashMap<InteractionType, InteractionPriority>(prioritiesCount);
            dictPos = varPos1 + varIntLen;
            for (i = 0; i < prioritiesCount; ++i) {
                key = InteractionType.fromValue(buf.getByte(dictPos));
                InteractionPriority val = InteractionPriority.deserialize(buf, ++dictPos);
                dictPos += InteractionPriority.computeBytesConsumed(buf, dictPos);
                if (obj.priorities.put((InteractionType)key, val) == null) continue;
                throw ProtocolException.duplicateKey("priorities", key);
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int i;
        int dictLen;
        byte nullBits = buf.getByte(offset);
        int maxEnd = 12;
        if ((nullBits & 1) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 4);
            int pos0 = offset + 12 + fieldOffset0;
            dictLen = VarInt.peek(buf, pos0);
            pos0 += VarInt.length(buf, pos0);
            for (i = 0; i < dictLen; ++i) {
                ++pos0;
                pos0 += 4;
            }
            if (pos0 - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 2) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 8);
            int pos1 = offset + 12 + fieldOffset1;
            dictLen = VarInt.peek(buf, pos1);
            pos1 += VarInt.length(buf, pos1);
            for (i = 0; i < dictLen; ++i) {
                ++pos1;
                pos1 += InteractionPriority.computeBytesConsumed(buf, pos1);
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
        if (this.useDistance != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.priorities != null) {
            nullBits = (byte)(nullBits | 2);
        }
        buf.writeByte(nullBits);
        buf.writeByte(this.displayOutlines ? 1 : 0);
        buf.writeByte(this.debugOutlines ? 1 : 0);
        buf.writeByte(this.allEntities ? 1 : 0);
        int useDistanceOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int prioritiesOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.useDistance != null) {
            buf.setIntLE(useDistanceOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.useDistance.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("UseDistance", this.useDistance.size(), 4096000);
            }
            VarInt.write(buf, this.useDistance.size());
            for (Map.Entry<GameMode, Float> entry : this.useDistance.entrySet()) {
                buf.writeByte(entry.getKey().getValue());
                buf.writeFloatLE(entry.getValue().floatValue());
            }
        } else {
            buf.setIntLE(useDistanceOffsetSlot, -1);
        }
        if (this.priorities != null) {
            buf.setIntLE(prioritiesOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.priorities.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Priorities", this.priorities.size(), 4096000);
            }
            VarInt.write(buf, this.priorities.size());
            for (Map.Entry<Enum, Object> entry : this.priorities.entrySet()) {
                buf.writeByte(((InteractionType)entry.getKey()).getValue());
                ((InteractionPriority)entry.getValue()).serialize(buf);
            }
        } else {
            buf.setIntLE(prioritiesOffsetSlot, -1);
        }
    }

    public int computeSize() {
        int size = 12;
        if (this.useDistance != null) {
            size += VarInt.size(this.useDistance.size()) + this.useDistance.size() * 5;
        }
        if (this.priorities != null) {
            int prioritiesSize = 0;
            for (Map.Entry<InteractionType, InteractionPriority> kvp : this.priorities.entrySet()) {
                prioritiesSize += 1 + kvp.getValue().computeSize();
            }
            size += VarInt.size(this.priorities.size()) + prioritiesSize;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int i;
        int pos;
        if (buffer.readableBytes() - offset < 12) {
            return ValidationResult.error("Buffer too small: expected at least 12 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 1) != 0) {
            int useDistanceOffset = buffer.getIntLE(offset + 4);
            if (useDistanceOffset < 0) {
                return ValidationResult.error("Invalid offset for UseDistance");
            }
            pos = offset + 12 + useDistanceOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for UseDistance");
            }
            int useDistanceCount = VarInt.peek(buffer, pos);
            if (useDistanceCount < 0) {
                return ValidationResult.error("Invalid dictionary count for UseDistance");
            }
            if (useDistanceCount > 4096000) {
                return ValidationResult.error("UseDistance exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < useDistanceCount; ++i) {
                ++pos;
                if ((pos += 4) <= buffer.writerIndex()) continue;
                return ValidationResult.error("Buffer overflow reading value");
            }
        }
        if ((nullBits & 2) != 0) {
            int prioritiesOffset = buffer.getIntLE(offset + 8);
            if (prioritiesOffset < 0) {
                return ValidationResult.error("Invalid offset for Priorities");
            }
            pos = offset + 12 + prioritiesOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Priorities");
            }
            int prioritiesCount = VarInt.peek(buffer, pos);
            if (prioritiesCount < 0) {
                return ValidationResult.error("Invalid dictionary count for Priorities");
            }
            if (prioritiesCount > 4096000) {
                return ValidationResult.error("Priorities exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < prioritiesCount; ++i) {
                ++pos;
                pos += InteractionPriority.computeBytesConsumed(buffer, pos);
            }
        }
        return ValidationResult.OK;
    }

    public InteractionConfiguration clone() {
        InteractionConfiguration copy = new InteractionConfiguration();
        copy.displayOutlines = this.displayOutlines;
        copy.debugOutlines = this.debugOutlines;
        copy.useDistance = this.useDistance != null ? new HashMap<GameMode, Float>(this.useDistance) : null;
        copy.allEntities = this.allEntities;
        if (this.priorities != null) {
            HashMap<InteractionType, InteractionPriority> m = new HashMap<InteractionType, InteractionPriority>();
            for (Map.Entry<InteractionType, InteractionPriority> e : this.priorities.entrySet()) {
                m.put(e.getKey(), e.getValue().clone());
            }
            copy.priorities = m;
        }
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof InteractionConfiguration)) {
            return false;
        }
        InteractionConfiguration other = (InteractionConfiguration)obj;
        return this.displayOutlines == other.displayOutlines && this.debugOutlines == other.debugOutlines && Objects.equals(this.useDistance, other.useDistance) && this.allEntities == other.allEntities && Objects.equals(this.priorities, other.priorities);
    }

    public int hashCode() {
        return Objects.hash(this.displayOutlines, this.debugOutlines, this.useDistance, this.allEntities, this.priorities);
    }
}

