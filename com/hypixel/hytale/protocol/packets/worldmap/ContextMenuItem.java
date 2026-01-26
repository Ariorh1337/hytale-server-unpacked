/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.worldmap;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class ContextMenuItem {
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 0;
    public static final int VARIABLE_FIELD_COUNT = 2;
    public static final int VARIABLE_BLOCK_START = 8;
    public static final int MAX_SIZE = 32768018;
    @Nonnull
    public String name = "";
    @Nonnull
    public String command = "";

    public ContextMenuItem() {
    }

    public ContextMenuItem(@Nonnull String name, @Nonnull String command) {
        this.name = name;
        this.command = command;
    }

    public ContextMenuItem(@Nonnull ContextMenuItem other) {
        this.name = other.name;
        this.command = other.command;
    }

    @Nonnull
    public static ContextMenuItem deserialize(@Nonnull ByteBuf buf, int offset) {
        ContextMenuItem obj = new ContextMenuItem();
        int varPos0 = offset + 8 + buf.getIntLE(offset + 0);
        int nameLen = VarInt.peek(buf, varPos0);
        if (nameLen < 0) {
            throw ProtocolException.negativeLength("Name", nameLen);
        }
        if (nameLen > 4096000) {
            throw ProtocolException.stringTooLong("Name", nameLen, 4096000);
        }
        obj.name = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
        int varPos1 = offset + 8 + buf.getIntLE(offset + 4);
        int commandLen = VarInt.peek(buf, varPos1);
        if (commandLen < 0) {
            throw ProtocolException.negativeLength("Command", commandLen);
        }
        if (commandLen > 4096000) {
            throw ProtocolException.stringTooLong("Command", commandLen, 4096000);
        }
        obj.command = PacketIO.readVarString(buf, varPos1, PacketIO.UTF8);
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int maxEnd = 8;
        int fieldOffset0 = buf.getIntLE(offset + 0);
        int pos0 = offset + 8 + fieldOffset0;
        int sl = VarInt.peek(buf, pos0);
        if ((pos0 += VarInt.length(buf, pos0) + sl) - offset > maxEnd) {
            maxEnd = pos0 - offset;
        }
        int fieldOffset1 = buf.getIntLE(offset + 4);
        int pos1 = offset + 8 + fieldOffset1;
        sl = VarInt.peek(buf, pos1);
        if ((pos1 += VarInt.length(buf, pos1) + sl) - offset > maxEnd) {
            maxEnd = pos1 - offset;
        }
        return maxEnd;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        int nameOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int commandOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        buf.setIntLE(nameOffsetSlot, buf.writerIndex() - varBlockStart);
        PacketIO.writeVarString(buf, this.name, 4096000);
        buf.setIntLE(commandOffsetSlot, buf.writerIndex() - varBlockStart);
        PacketIO.writeVarString(buf, this.command, 4096000);
    }

    public int computeSize() {
        int size = 8;
        size += PacketIO.stringSize(this.name);
        return size += PacketIO.stringSize(this.command);
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 8) {
            return ValidationResult.error("Buffer too small: expected at least 8 bytes");
        }
        int nameOffset = buffer.getIntLE(offset + 0);
        if (nameOffset < 0) {
            return ValidationResult.error("Invalid offset for Name");
        }
        int pos = offset + 8 + nameOffset;
        if (pos >= buffer.writerIndex()) {
            return ValidationResult.error("Offset out of bounds for Name");
        }
        int nameLen = VarInt.peek(buffer, pos);
        if (nameLen < 0) {
            return ValidationResult.error("Invalid string length for Name");
        }
        if (nameLen > 4096000) {
            return ValidationResult.error("Name exceeds max length 4096000");
        }
        pos += VarInt.length(buffer, pos);
        if ((pos += nameLen) > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Name");
        }
        int commandOffset = buffer.getIntLE(offset + 4);
        if (commandOffset < 0) {
            return ValidationResult.error("Invalid offset for Command");
        }
        pos = offset + 8 + commandOffset;
        if (pos >= buffer.writerIndex()) {
            return ValidationResult.error("Offset out of bounds for Command");
        }
        int commandLen = VarInt.peek(buffer, pos);
        if (commandLen < 0) {
            return ValidationResult.error("Invalid string length for Command");
        }
        if (commandLen > 4096000) {
            return ValidationResult.error("Command exceeds max length 4096000");
        }
        pos += VarInt.length(buffer, pos);
        if ((pos += commandLen) > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Command");
        }
        return ValidationResult.OK;
    }

    public ContextMenuItem clone() {
        ContextMenuItem copy = new ContextMenuItem();
        copy.name = this.name;
        copy.command = this.command;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ContextMenuItem)) {
            return false;
        }
        ContextMenuItem other = (ContextMenuItem)obj;
        return Objects.equals(this.name, other.name) && Objects.equals(this.command, other.command);
    }

    public int hashCode() {
        return Objects.hash(this.name, this.command);
    }
}

