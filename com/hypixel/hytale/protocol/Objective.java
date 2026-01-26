/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.ObjectiveTask;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Objective {
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 17;
    public static final int VARIABLE_FIELD_COUNT = 4;
    public static final int VARIABLE_BLOCK_START = 33;
    public static final int MAX_SIZE = 0x64000000;
    @Nonnull
    public UUID objectiveUuid = new UUID(0L, 0L);
    @Nullable
    public String objectiveTitleKey;
    @Nullable
    public String objectiveDescriptionKey;
    @Nullable
    public String objectiveLineId;
    @Nullable
    public ObjectiveTask[] tasks;

    public Objective() {
    }

    public Objective(@Nonnull UUID objectiveUuid, @Nullable String objectiveTitleKey, @Nullable String objectiveDescriptionKey, @Nullable String objectiveLineId, @Nullable ObjectiveTask[] tasks) {
        this.objectiveUuid = objectiveUuid;
        this.objectiveTitleKey = objectiveTitleKey;
        this.objectiveDescriptionKey = objectiveDescriptionKey;
        this.objectiveLineId = objectiveLineId;
        this.tasks = tasks;
    }

    public Objective(@Nonnull Objective other) {
        this.objectiveUuid = other.objectiveUuid;
        this.objectiveTitleKey = other.objectiveTitleKey;
        this.objectiveDescriptionKey = other.objectiveDescriptionKey;
        this.objectiveLineId = other.objectiveLineId;
        this.tasks = other.tasks;
    }

    @Nonnull
    public static Objective deserialize(@Nonnull ByteBuf buf, int offset) {
        Objective obj = new Objective();
        byte nullBits = buf.getByte(offset);
        obj.objectiveUuid = PacketIO.readUUID(buf, offset + 1);
        if ((nullBits & 1) != 0) {
            int varPos0 = offset + 33 + buf.getIntLE(offset + 17);
            int objectiveTitleKeyLen = VarInt.peek(buf, varPos0);
            if (objectiveTitleKeyLen < 0) {
                throw ProtocolException.negativeLength("ObjectiveTitleKey", objectiveTitleKeyLen);
            }
            if (objectiveTitleKeyLen > 4096000) {
                throw ProtocolException.stringTooLong("ObjectiveTitleKey", objectiveTitleKeyLen, 4096000);
            }
            obj.objectiveTitleKey = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
        }
        if ((nullBits & 2) != 0) {
            int varPos1 = offset + 33 + buf.getIntLE(offset + 21);
            int objectiveDescriptionKeyLen = VarInt.peek(buf, varPos1);
            if (objectiveDescriptionKeyLen < 0) {
                throw ProtocolException.negativeLength("ObjectiveDescriptionKey", objectiveDescriptionKeyLen);
            }
            if (objectiveDescriptionKeyLen > 4096000) {
                throw ProtocolException.stringTooLong("ObjectiveDescriptionKey", objectiveDescriptionKeyLen, 4096000);
            }
            obj.objectiveDescriptionKey = PacketIO.readVarString(buf, varPos1, PacketIO.UTF8);
        }
        if ((nullBits & 4) != 0) {
            int varPos2 = offset + 33 + buf.getIntLE(offset + 25);
            int objectiveLineIdLen = VarInt.peek(buf, varPos2);
            if (objectiveLineIdLen < 0) {
                throw ProtocolException.negativeLength("ObjectiveLineId", objectiveLineIdLen);
            }
            if (objectiveLineIdLen > 4096000) {
                throw ProtocolException.stringTooLong("ObjectiveLineId", objectiveLineIdLen, 4096000);
            }
            obj.objectiveLineId = PacketIO.readVarString(buf, varPos2, PacketIO.UTF8);
        }
        if ((nullBits & 8) != 0) {
            int varPos3 = offset + 33 + buf.getIntLE(offset + 29);
            int tasksCount = VarInt.peek(buf, varPos3);
            if (tasksCount < 0) {
                throw ProtocolException.negativeLength("Tasks", tasksCount);
            }
            if (tasksCount > 4096000) {
                throw ProtocolException.arrayTooLong("Tasks", tasksCount, 4096000);
            }
            int varIntLen = VarInt.length(buf, varPos3);
            if ((long)(varPos3 + varIntLen) + (long)tasksCount * 9L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("Tasks", varPos3 + varIntLen + tasksCount * 9, buf.readableBytes());
            }
            obj.tasks = new ObjectiveTask[tasksCount];
            int elemPos = varPos3 + varIntLen;
            for (int i = 0; i < tasksCount; ++i) {
                obj.tasks[i] = ObjectiveTask.deserialize(buf, elemPos);
                elemPos += ObjectiveTask.computeBytesConsumed(buf, elemPos);
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int sl;
        byte nullBits = buf.getByte(offset);
        int maxEnd = 33;
        if ((nullBits & 1) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 17);
            int pos0 = offset + 33 + fieldOffset0;
            sl = VarInt.peek(buf, pos0);
            if ((pos0 += VarInt.length(buf, pos0) + sl) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 2) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 21);
            int pos1 = offset + 33 + fieldOffset1;
            sl = VarInt.peek(buf, pos1);
            if ((pos1 += VarInt.length(buf, pos1) + sl) - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        if ((nullBits & 4) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 25);
            int pos2 = offset + 33 + fieldOffset2;
            sl = VarInt.peek(buf, pos2);
            if ((pos2 += VarInt.length(buf, pos2) + sl) - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        if ((nullBits & 8) != 0) {
            int fieldOffset3 = buf.getIntLE(offset + 29);
            int pos3 = offset + 33 + fieldOffset3;
            int arrLen = VarInt.peek(buf, pos3);
            pos3 += VarInt.length(buf, pos3);
            for (int i = 0; i < arrLen; ++i) {
                pos3 += ObjectiveTask.computeBytesConsumed(buf, pos3);
            }
            if (pos3 - offset > maxEnd) {
                maxEnd = pos3 - offset;
            }
        }
        return maxEnd;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        byte nullBits = 0;
        if (this.objectiveTitleKey != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.objectiveDescriptionKey != null) {
            nullBits = (byte)(nullBits | 2);
        }
        if (this.objectiveLineId != null) {
            nullBits = (byte)(nullBits | 4);
        }
        if (this.tasks != null) {
            nullBits = (byte)(nullBits | 8);
        }
        buf.writeByte(nullBits);
        PacketIO.writeUUID(buf, this.objectiveUuid);
        int objectiveTitleKeyOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int objectiveDescriptionKeyOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int objectiveLineIdOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int tasksOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.objectiveTitleKey != null) {
            buf.setIntLE(objectiveTitleKeyOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.objectiveTitleKey, 4096000);
        } else {
            buf.setIntLE(objectiveTitleKeyOffsetSlot, -1);
        }
        if (this.objectiveDescriptionKey != null) {
            buf.setIntLE(objectiveDescriptionKeyOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.objectiveDescriptionKey, 4096000);
        } else {
            buf.setIntLE(objectiveDescriptionKeyOffsetSlot, -1);
        }
        if (this.objectiveLineId != null) {
            buf.setIntLE(objectiveLineIdOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.objectiveLineId, 4096000);
        } else {
            buf.setIntLE(objectiveLineIdOffsetSlot, -1);
        }
        if (this.tasks != null) {
            buf.setIntLE(tasksOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.tasks.length > 4096000) {
                throw ProtocolException.arrayTooLong("Tasks", this.tasks.length, 4096000);
            }
            VarInt.write(buf, this.tasks.length);
            for (ObjectiveTask item : this.tasks) {
                item.serialize(buf);
            }
        } else {
            buf.setIntLE(tasksOffsetSlot, -1);
        }
    }

    public int computeSize() {
        int size = 33;
        if (this.objectiveTitleKey != null) {
            size += PacketIO.stringSize(this.objectiveTitleKey);
        }
        if (this.objectiveDescriptionKey != null) {
            size += PacketIO.stringSize(this.objectiveDescriptionKey);
        }
        if (this.objectiveLineId != null) {
            size += PacketIO.stringSize(this.objectiveLineId);
        }
        if (this.tasks != null) {
            int tasksSize = 0;
            for (ObjectiveTask elem : this.tasks) {
                tasksSize += elem.computeSize();
            }
            size += VarInt.size(this.tasks.length) + tasksSize;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int pos;
        if (buffer.readableBytes() - offset < 33) {
            return ValidationResult.error("Buffer too small: expected at least 33 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 1) != 0) {
            int objectiveTitleKeyOffset = buffer.getIntLE(offset + 17);
            if (objectiveTitleKeyOffset < 0) {
                return ValidationResult.error("Invalid offset for ObjectiveTitleKey");
            }
            pos = offset + 33 + objectiveTitleKeyOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for ObjectiveTitleKey");
            }
            int objectiveTitleKeyLen = VarInt.peek(buffer, pos);
            if (objectiveTitleKeyLen < 0) {
                return ValidationResult.error("Invalid string length for ObjectiveTitleKey");
            }
            if (objectiveTitleKeyLen > 4096000) {
                return ValidationResult.error("ObjectiveTitleKey exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += objectiveTitleKeyLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading ObjectiveTitleKey");
            }
        }
        if ((nullBits & 2) != 0) {
            int objectiveDescriptionKeyOffset = buffer.getIntLE(offset + 21);
            if (objectiveDescriptionKeyOffset < 0) {
                return ValidationResult.error("Invalid offset for ObjectiveDescriptionKey");
            }
            pos = offset + 33 + objectiveDescriptionKeyOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for ObjectiveDescriptionKey");
            }
            int objectiveDescriptionKeyLen = VarInt.peek(buffer, pos);
            if (objectiveDescriptionKeyLen < 0) {
                return ValidationResult.error("Invalid string length for ObjectiveDescriptionKey");
            }
            if (objectiveDescriptionKeyLen > 4096000) {
                return ValidationResult.error("ObjectiveDescriptionKey exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += objectiveDescriptionKeyLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading ObjectiveDescriptionKey");
            }
        }
        if ((nullBits & 4) != 0) {
            int objectiveLineIdOffset = buffer.getIntLE(offset + 25);
            if (objectiveLineIdOffset < 0) {
                return ValidationResult.error("Invalid offset for ObjectiveLineId");
            }
            pos = offset + 33 + objectiveLineIdOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for ObjectiveLineId");
            }
            int objectiveLineIdLen = VarInt.peek(buffer, pos);
            if (objectiveLineIdLen < 0) {
                return ValidationResult.error("Invalid string length for ObjectiveLineId");
            }
            if (objectiveLineIdLen > 4096000) {
                return ValidationResult.error("ObjectiveLineId exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += objectiveLineIdLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading ObjectiveLineId");
            }
        }
        if ((nullBits & 8) != 0) {
            int tasksOffset = buffer.getIntLE(offset + 29);
            if (tasksOffset < 0) {
                return ValidationResult.error("Invalid offset for Tasks");
            }
            pos = offset + 33 + tasksOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Tasks");
            }
            int tasksCount = VarInt.peek(buffer, pos);
            if (tasksCount < 0) {
                return ValidationResult.error("Invalid array count for Tasks");
            }
            if (tasksCount > 4096000) {
                return ValidationResult.error("Tasks exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (int i = 0; i < tasksCount; ++i) {
                ValidationResult structResult = ObjectiveTask.validateStructure(buffer, pos);
                if (!structResult.isValid()) {
                    return ValidationResult.error("Invalid ObjectiveTask in Tasks[" + i + "]: " + structResult.error());
                }
                pos += ObjectiveTask.computeBytesConsumed(buffer, pos);
            }
        }
        return ValidationResult.OK;
    }

    public Objective clone() {
        Objective copy = new Objective();
        copy.objectiveUuid = this.objectiveUuid;
        copy.objectiveTitleKey = this.objectiveTitleKey;
        copy.objectiveDescriptionKey = this.objectiveDescriptionKey;
        copy.objectiveLineId = this.objectiveLineId;
        copy.tasks = this.tasks != null ? (ObjectiveTask[])Arrays.stream(this.tasks).map(e -> e.clone()).toArray(ObjectiveTask[]::new) : null;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Objective)) {
            return false;
        }
        Objective other = (Objective)obj;
        return Objects.equals(this.objectiveUuid, other.objectiveUuid) && Objects.equals(this.objectiveTitleKey, other.objectiveTitleKey) && Objects.equals(this.objectiveDescriptionKey, other.objectiveDescriptionKey) && Objects.equals(this.objectiveLineId, other.objectiveLineId) && Arrays.equals(this.tasks, other.tasks);
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + Objects.hashCode(this.objectiveUuid);
        result = 31 * result + Objects.hashCode(this.objectiveTitleKey);
        result = 31 * result + Objects.hashCode(this.objectiveDescriptionKey);
        result = 31 * result + Objects.hashCode(this.objectiveLineId);
        result = 31 * result + Arrays.hashCode(this.tasks);
        return result;
    }
}

