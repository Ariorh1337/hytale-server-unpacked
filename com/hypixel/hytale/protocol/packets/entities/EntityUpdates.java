/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.entities;

import com.hypixel.hytale.protocol.EntityUpdate;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntityUpdates
implements Packet {
    public static final int PACKET_ID = 161;
    public static final boolean IS_COMPRESSED = true;
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 1;
    public static final int VARIABLE_FIELD_COUNT = 2;
    public static final int VARIABLE_BLOCK_START = 9;
    public static final int MAX_SIZE = 0x64000000;
    @Nullable
    public int[] removed;
    @Nullable
    public EntityUpdate[] updates;

    @Override
    public int getId() {
        return 161;
    }

    public EntityUpdates() {
    }

    public EntityUpdates(@Nullable int[] removed, @Nullable EntityUpdate[] updates) {
        this.removed = removed;
        this.updates = updates;
    }

    public EntityUpdates(@Nonnull EntityUpdates other) {
        this.removed = other.removed;
        this.updates = other.updates;
    }

    @Nonnull
    public static EntityUpdates deserialize(@Nonnull ByteBuf buf, int offset) {
        int varIntLen;
        EntityUpdates obj = new EntityUpdates();
        byte nullBits = buf.getByte(offset);
        if ((nullBits & 1) != 0) {
            int varPos0 = offset + 9 + buf.getIntLE(offset + 1);
            int removedCount = VarInt.peek(buf, varPos0);
            if (removedCount < 0) {
                throw ProtocolException.negativeLength("Removed", removedCount);
            }
            if (removedCount > 4096000) {
                throw ProtocolException.arrayTooLong("Removed", removedCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos0);
            if ((long)(varPos0 + varIntLen) + (long)removedCount * 4L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("Removed", varPos0 + varIntLen + removedCount * 4, buf.readableBytes());
            }
            obj.removed = new int[removedCount];
            for (int i = 0; i < removedCount; ++i) {
                obj.removed[i] = buf.getIntLE(varPos0 + varIntLen + i * 4);
            }
        }
        if ((nullBits & 2) != 0) {
            int varPos1 = offset + 9 + buf.getIntLE(offset + 5);
            int updatesCount = VarInt.peek(buf, varPos1);
            if (updatesCount < 0) {
                throw ProtocolException.negativeLength("Updates", updatesCount);
            }
            if (updatesCount > 4096000) {
                throw ProtocolException.arrayTooLong("Updates", updatesCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos1);
            if ((long)(varPos1 + varIntLen) + (long)updatesCount * 5L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("Updates", varPos1 + varIntLen + updatesCount * 5, buf.readableBytes());
            }
            obj.updates = new EntityUpdate[updatesCount];
            int elemPos = varPos1 + varIntLen;
            for (int i = 0; i < updatesCount; ++i) {
                obj.updates[i] = EntityUpdate.deserialize(buf, elemPos);
                elemPos += EntityUpdate.computeBytesConsumed(buf, elemPos);
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int arrLen;
        byte nullBits = buf.getByte(offset);
        int maxEnd = 9;
        if ((nullBits & 1) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 1);
            int pos0 = offset + 9 + fieldOffset0;
            arrLen = VarInt.peek(buf, pos0);
            if ((pos0 += VarInt.length(buf, pos0) + arrLen * 4) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 2) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 5);
            int pos1 = offset + 9 + fieldOffset1;
            arrLen = VarInt.peek(buf, pos1);
            pos1 += VarInt.length(buf, pos1);
            for (int i = 0; i < arrLen; ++i) {
                pos1 += EntityUpdate.computeBytesConsumed(buf, pos1);
            }
            if (pos1 - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        return maxEnd;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        byte nullBits = 0;
        if (this.removed != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.updates != null) {
            nullBits = (byte)(nullBits | 2);
        }
        buf.writeByte(nullBits);
        int removedOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int updatesOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.removed != null) {
            buf.setIntLE(removedOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.removed.length > 4096000) {
                throw ProtocolException.arrayTooLong("Removed", this.removed.length, 4096000);
            }
            VarInt.write(buf, this.removed.length);
            for (int item : this.removed) {
                buf.writeIntLE(item);
            }
        } else {
            buf.setIntLE(removedOffsetSlot, -1);
        }
        if (this.updates != null) {
            buf.setIntLE(updatesOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.updates.length > 4096000) {
                throw ProtocolException.arrayTooLong("Updates", this.updates.length, 4096000);
            }
            VarInt.write(buf, this.updates.length);
            for (EntityUpdate item : this.updates) {
                item.serialize(buf);
            }
        } else {
            buf.setIntLE(updatesOffsetSlot, -1);
        }
    }

    @Override
    public int computeSize() {
        int size = 9;
        if (this.removed != null) {
            size += VarInt.size(this.removed.length) + this.removed.length * 4;
        }
        if (this.updates != null) {
            int updatesSize = 0;
            for (EntityUpdate elem : this.updates) {
                updatesSize += elem.computeSize();
            }
            size += VarInt.size(this.updates.length) + updatesSize;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int pos;
        if (buffer.readableBytes() - offset < 9) {
            return ValidationResult.error("Buffer too small: expected at least 9 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 1) != 0) {
            int removedOffset = buffer.getIntLE(offset + 1);
            if (removedOffset < 0) {
                return ValidationResult.error("Invalid offset for Removed");
            }
            pos = offset + 9 + removedOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Removed");
            }
            int removedCount = VarInt.peek(buffer, pos);
            if (removedCount < 0) {
                return ValidationResult.error("Invalid array count for Removed");
            }
            if (removedCount > 4096000) {
                return ValidationResult.error("Removed exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += removedCount * 4) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading Removed");
            }
        }
        if ((nullBits & 2) != 0) {
            int updatesOffset = buffer.getIntLE(offset + 5);
            if (updatesOffset < 0) {
                return ValidationResult.error("Invalid offset for Updates");
            }
            pos = offset + 9 + updatesOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Updates");
            }
            int updatesCount = VarInt.peek(buffer, pos);
            if (updatesCount < 0) {
                return ValidationResult.error("Invalid array count for Updates");
            }
            if (updatesCount > 4096000) {
                return ValidationResult.error("Updates exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (int i = 0; i < updatesCount; ++i) {
                ValidationResult structResult = EntityUpdate.validateStructure(buffer, pos);
                if (!structResult.isValid()) {
                    return ValidationResult.error("Invalid EntityUpdate in Updates[" + i + "]: " + structResult.error());
                }
                pos += EntityUpdate.computeBytesConsumed(buffer, pos);
            }
        }
        return ValidationResult.OK;
    }

    public EntityUpdates clone() {
        EntityUpdates copy = new EntityUpdates();
        copy.removed = this.removed != null ? Arrays.copyOf(this.removed, this.removed.length) : null;
        copy.updates = this.updates != null ? (EntityUpdate[])Arrays.stream(this.updates).map(e -> e.clone()).toArray(EntityUpdate[]::new) : null;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof EntityUpdates)) {
            return false;
        }
        EntityUpdates other = (EntityUpdates)obj;
        return Arrays.equals(this.removed, other.removed) && Arrays.equals(this.updates, other.updates);
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + Arrays.hashCode(this.removed);
        result = 31 * result + Arrays.hashCode(this.updates);
        return result;
    }
}

