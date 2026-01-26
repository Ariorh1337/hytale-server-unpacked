/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.interface_;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UpdateServerPlayerListPing
implements Packet {
    public static final int PACKET_ID = 227;
    public static final boolean IS_COMPRESSED = false;
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 1;
    public static final int VARIABLE_FIELD_COUNT = 1;
    public static final int VARIABLE_BLOCK_START = 1;
    public static final int MAX_SIZE = 81920006;
    @Nullable
    public Map<UUID, Integer> players;

    @Override
    public int getId() {
        return 227;
    }

    public UpdateServerPlayerListPing() {
    }

    public UpdateServerPlayerListPing(@Nullable Map<UUID, Integer> players) {
        this.players = players;
    }

    public UpdateServerPlayerListPing(@Nonnull UpdateServerPlayerListPing other) {
        this.players = other.players;
    }

    @Nonnull
    public static UpdateServerPlayerListPing deserialize(@Nonnull ByteBuf buf, int offset) {
        UpdateServerPlayerListPing obj = new UpdateServerPlayerListPing();
        byte nullBits = buf.getByte(offset);
        int pos = offset + 1;
        if ((nullBits & 1) != 0) {
            int playersCount = VarInt.peek(buf, pos);
            if (playersCount < 0) {
                throw ProtocolException.negativeLength("Players", playersCount);
            }
            if (playersCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Players", playersCount, 4096000);
            }
            pos += VarInt.size(playersCount);
            obj.players = new HashMap<UUID, Integer>(playersCount);
            for (int i = 0; i < playersCount; ++i) {
                UUID key = PacketIO.readUUID(buf, pos);
                int val = buf.getIntLE(pos += 16);
                pos += 4;
                if (obj.players.put(key, val) == null) continue;
                throw ProtocolException.duplicateKey("players", key);
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        byte nullBits = buf.getByte(offset);
        int pos = offset + 1;
        if ((nullBits & 1) != 0) {
            int dictLen = VarInt.peek(buf, pos);
            pos += VarInt.length(buf, pos);
            for (int i = 0; i < dictLen; ++i) {
                pos += 16;
                pos += 4;
            }
        }
        return pos - offset;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        int nullBits = 0;
        if (this.players != null) {
            nullBits = (byte)(nullBits | 1);
        }
        buf.writeByte(nullBits);
        if (this.players != null) {
            if (this.players.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Players", this.players.size(), 4096000);
            }
            VarInt.write(buf, this.players.size());
            for (Map.Entry<UUID, Integer> e : this.players.entrySet()) {
                PacketIO.writeUUID(buf, e.getKey());
                buf.writeIntLE(e.getValue());
            }
        }
    }

    @Override
    public int computeSize() {
        int size = 1;
        if (this.players != null) {
            size += VarInt.size(this.players.size()) + this.players.size() * 20;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 1) {
            return ValidationResult.error("Buffer too small: expected at least 1 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        int pos = offset + 1;
        if ((nullBits & 1) != 0) {
            int playersCount = VarInt.peek(buffer, pos);
            if (playersCount < 0) {
                return ValidationResult.error("Invalid dictionary count for Players");
            }
            if (playersCount > 4096000) {
                return ValidationResult.error("Players exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (int i = 0; i < playersCount; ++i) {
                if ((pos += 16) > buffer.writerIndex()) {
                    return ValidationResult.error("Buffer overflow reading key");
                }
                if ((pos += 4) <= buffer.writerIndex()) continue;
                return ValidationResult.error("Buffer overflow reading value");
            }
        }
        return ValidationResult.OK;
    }

    public UpdateServerPlayerListPing clone() {
        UpdateServerPlayerListPing copy = new UpdateServerPlayerListPing();
        copy.players = this.players != null ? new HashMap<UUID, Integer>(this.players) : null;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UpdateServerPlayerListPing)) {
            return false;
        }
        UpdateServerPlayerListPing other = (UpdateServerPlayerListPing)obj;
        return Objects.equals(this.players, other.players);
    }

    public int hashCode() {
        return Objects.hash(this.players);
    }
}

