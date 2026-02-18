/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.interface_;

import com.hypixel.hytale.protocol.NetworkChannel;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.ToClientPacket;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import com.hypixel.hytale.protocol.packets.interface_.CustomUICommand;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBinding;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UpdateAnchorUI
implements Packet,
ToClientPacket {
    public static final int PACKET_ID = 235;
    public static final boolean IS_COMPRESSED = true;
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 2;
    public static final int VARIABLE_FIELD_COUNT = 3;
    public static final int VARIABLE_BLOCK_START = 14;
    public static final int MAX_SIZE = 0x64000000;
    @Nullable
    public String anchorId;
    public boolean clear;
    @Nullable
    public CustomUICommand[] commands;
    @Nullable
    public CustomUIEventBinding[] eventBindings;

    @Override
    public int getId() {
        return 235;
    }

    @Override
    public NetworkChannel getChannel() {
        return NetworkChannel.Default;
    }

    public UpdateAnchorUI() {
    }

    public UpdateAnchorUI(@Nullable String anchorId, boolean clear, @Nullable CustomUICommand[] commands, @Nullable CustomUIEventBinding[] eventBindings) {
        this.anchorId = anchorId;
        this.clear = clear;
        this.commands = commands;
        this.eventBindings = eventBindings;
    }

    public UpdateAnchorUI(@Nonnull UpdateAnchorUI other) {
        this.anchorId = other.anchorId;
        this.clear = other.clear;
        this.commands = other.commands;
        this.eventBindings = other.eventBindings;
    }

    @Nonnull
    public static UpdateAnchorUI deserialize(@Nonnull ByteBuf buf, int offset) {
        int i;
        int elemPos;
        int varIntLen;
        UpdateAnchorUI obj = new UpdateAnchorUI();
        byte nullBits = buf.getByte(offset);
        boolean bl = obj.clear = buf.getByte(offset + 1) != 0;
        if ((nullBits & 1) != 0) {
            int varPos0 = offset + 14 + buf.getIntLE(offset + 2);
            int anchorIdLen = VarInt.peek(buf, varPos0);
            if (anchorIdLen < 0) {
                throw ProtocolException.negativeLength("AnchorId", anchorIdLen);
            }
            if (anchorIdLen > 4096000) {
                throw ProtocolException.stringTooLong("AnchorId", anchorIdLen, 4096000);
            }
            obj.anchorId = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
        }
        if ((nullBits & 2) != 0) {
            int varPos1 = offset + 14 + buf.getIntLE(offset + 6);
            int commandsCount = VarInt.peek(buf, varPos1);
            if (commandsCount < 0) {
                throw ProtocolException.negativeLength("Commands", commandsCount);
            }
            if (commandsCount > 4096000) {
                throw ProtocolException.arrayTooLong("Commands", commandsCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos1);
            if ((long)(varPos1 + varIntLen) + (long)commandsCount * 2L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("Commands", varPos1 + varIntLen + commandsCount * 2, buf.readableBytes());
            }
            obj.commands = new CustomUICommand[commandsCount];
            elemPos = varPos1 + varIntLen;
            for (i = 0; i < commandsCount; ++i) {
                obj.commands[i] = CustomUICommand.deserialize(buf, elemPos);
                elemPos += CustomUICommand.computeBytesConsumed(buf, elemPos);
            }
        }
        if ((nullBits & 4) != 0) {
            int varPos2 = offset + 14 + buf.getIntLE(offset + 10);
            int eventBindingsCount = VarInt.peek(buf, varPos2);
            if (eventBindingsCount < 0) {
                throw ProtocolException.negativeLength("EventBindings", eventBindingsCount);
            }
            if (eventBindingsCount > 4096000) {
                throw ProtocolException.arrayTooLong("EventBindings", eventBindingsCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos2);
            if ((long)(varPos2 + varIntLen) + (long)eventBindingsCount * 3L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("EventBindings", varPos2 + varIntLen + eventBindingsCount * 3, buf.readableBytes());
            }
            obj.eventBindings = new CustomUIEventBinding[eventBindingsCount];
            elemPos = varPos2 + varIntLen;
            for (i = 0; i < eventBindingsCount; ++i) {
                obj.eventBindings[i] = CustomUIEventBinding.deserialize(buf, elemPos);
                elemPos += CustomUIEventBinding.computeBytesConsumed(buf, elemPos);
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int i;
        int arrLen;
        byte nullBits = buf.getByte(offset);
        int maxEnd = 14;
        if ((nullBits & 1) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 2);
            int pos0 = offset + 14 + fieldOffset0;
            int sl = VarInt.peek(buf, pos0);
            if ((pos0 += VarInt.length(buf, pos0) + sl) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 2) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 6);
            int pos1 = offset + 14 + fieldOffset1;
            arrLen = VarInt.peek(buf, pos1);
            pos1 += VarInt.length(buf, pos1);
            for (i = 0; i < arrLen; ++i) {
                pos1 += CustomUICommand.computeBytesConsumed(buf, pos1);
            }
            if (pos1 - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        if ((nullBits & 4) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 10);
            int pos2 = offset + 14 + fieldOffset2;
            arrLen = VarInt.peek(buf, pos2);
            pos2 += VarInt.length(buf, pos2);
            for (i = 0; i < arrLen; ++i) {
                pos2 += CustomUIEventBinding.computeBytesConsumed(buf, pos2);
            }
            if (pos2 - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        return maxEnd;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        byte nullBits = 0;
        if (this.anchorId != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.commands != null) {
            nullBits = (byte)(nullBits | 2);
        }
        if (this.eventBindings != null) {
            nullBits = (byte)(nullBits | 4);
        }
        buf.writeByte(nullBits);
        buf.writeByte(this.clear ? 1 : 0);
        int anchorIdOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int commandsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int eventBindingsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.anchorId != null) {
            buf.setIntLE(anchorIdOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.anchorId, 4096000);
        } else {
            buf.setIntLE(anchorIdOffsetSlot, -1);
        }
        if (this.commands != null) {
            buf.setIntLE(commandsOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.commands.length > 4096000) {
                throw ProtocolException.arrayTooLong("Commands", this.commands.length, 4096000);
            }
            VarInt.write(buf, this.commands.length);
            for (CustomUICommand customUICommand : this.commands) {
                customUICommand.serialize(buf);
            }
        } else {
            buf.setIntLE(commandsOffsetSlot, -1);
        }
        if (this.eventBindings != null) {
            buf.setIntLE(eventBindingsOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.eventBindings.length > 4096000) {
                throw ProtocolException.arrayTooLong("EventBindings", this.eventBindings.length, 4096000);
            }
            VarInt.write(buf, this.eventBindings.length);
            for (CustomUIEventBinding customUIEventBinding : this.eventBindings) {
                customUIEventBinding.serialize(buf);
            }
        } else {
            buf.setIntLE(eventBindingsOffsetSlot, -1);
        }
    }

    @Override
    public int computeSize() {
        int size = 14;
        if (this.anchorId != null) {
            size += PacketIO.stringSize(this.anchorId);
        }
        if (this.commands != null) {
            int commandsSize = 0;
            for (CustomUICommand customUICommand : this.commands) {
                commandsSize += customUICommand.computeSize();
            }
            size += VarInt.size(this.commands.length) + commandsSize;
        }
        if (this.eventBindings != null) {
            int eventBindingsSize = 0;
            for (CustomUIEventBinding customUIEventBinding : this.eventBindings) {
                eventBindingsSize += customUIEventBinding.computeSize();
            }
            size += VarInt.size(this.eventBindings.length) + eventBindingsSize;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        ValidationResult structResult;
        int i;
        int pos;
        if (buffer.readableBytes() - offset < 14) {
            return ValidationResult.error("Buffer too small: expected at least 14 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 1) != 0) {
            int anchorIdOffset = buffer.getIntLE(offset + 2);
            if (anchorIdOffset < 0) {
                return ValidationResult.error("Invalid offset for AnchorId");
            }
            pos = offset + 14 + anchorIdOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for AnchorId");
            }
            int anchorIdLen = VarInt.peek(buffer, pos);
            if (anchorIdLen < 0) {
                return ValidationResult.error("Invalid string length for AnchorId");
            }
            if (anchorIdLen > 4096000) {
                return ValidationResult.error("AnchorId exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += anchorIdLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading AnchorId");
            }
        }
        if ((nullBits & 2) != 0) {
            int commandsOffset = buffer.getIntLE(offset + 6);
            if (commandsOffset < 0) {
                return ValidationResult.error("Invalid offset for Commands");
            }
            pos = offset + 14 + commandsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Commands");
            }
            int commandsCount = VarInt.peek(buffer, pos);
            if (commandsCount < 0) {
                return ValidationResult.error("Invalid array count for Commands");
            }
            if (commandsCount > 4096000) {
                return ValidationResult.error("Commands exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < commandsCount; ++i) {
                structResult = CustomUICommand.validateStructure(buffer, pos);
                if (!structResult.isValid()) {
                    return ValidationResult.error("Invalid CustomUICommand in Commands[" + i + "]: " + structResult.error());
                }
                pos += CustomUICommand.computeBytesConsumed(buffer, pos);
            }
        }
        if ((nullBits & 4) != 0) {
            int eventBindingsOffset = buffer.getIntLE(offset + 10);
            if (eventBindingsOffset < 0) {
                return ValidationResult.error("Invalid offset for EventBindings");
            }
            pos = offset + 14 + eventBindingsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for EventBindings");
            }
            int eventBindingsCount = VarInt.peek(buffer, pos);
            if (eventBindingsCount < 0) {
                return ValidationResult.error("Invalid array count for EventBindings");
            }
            if (eventBindingsCount > 4096000) {
                return ValidationResult.error("EventBindings exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < eventBindingsCount; ++i) {
                structResult = CustomUIEventBinding.validateStructure(buffer, pos);
                if (!structResult.isValid()) {
                    return ValidationResult.error("Invalid CustomUIEventBinding in EventBindings[" + i + "]: " + structResult.error());
                }
                pos += CustomUIEventBinding.computeBytesConsumed(buffer, pos);
            }
        }
        return ValidationResult.OK;
    }

    public UpdateAnchorUI clone() {
        UpdateAnchorUI copy = new UpdateAnchorUI();
        copy.anchorId = this.anchorId;
        copy.clear = this.clear;
        copy.commands = this.commands != null ? (CustomUICommand[])Arrays.stream(this.commands).map(e -> e.clone()).toArray(CustomUICommand[]::new) : null;
        copy.eventBindings = this.eventBindings != null ? (CustomUIEventBinding[])Arrays.stream(this.eventBindings).map(e -> e.clone()).toArray(CustomUIEventBinding[]::new) : null;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UpdateAnchorUI)) {
            return false;
        }
        UpdateAnchorUI other = (UpdateAnchorUI)obj;
        return Objects.equals(this.anchorId, other.anchorId) && this.clear == other.clear && Arrays.equals(this.commands, other.commands) && Arrays.equals(this.eventBindings, other.eventBindings);
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + Objects.hashCode(this.anchorId);
        result = 31 * result + Boolean.hashCode(this.clear);
        result = 31 * result + Arrays.hashCode(this.commands);
        result = 31 * result + Arrays.hashCode(this.eventBindings);
        return result;
    }
}

