/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.assets;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.TagPattern;
import com.hypixel.hytale.protocol.UpdateType;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UpdateTagPatterns
implements Packet {
    public static final int PACKET_ID = 84;
    public static final boolean IS_COMPRESSED = true;
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 6;
    public static final int VARIABLE_FIELD_COUNT = 1;
    public static final int VARIABLE_BLOCK_START = 6;
    public static final int MAX_SIZE = 0x64000000;
    @Nonnull
    public UpdateType type = UpdateType.Init;
    public int maxId;
    @Nullable
    public Map<Integer, TagPattern> patterns;

    @Override
    public int getId() {
        return 84;
    }

    public UpdateTagPatterns() {
    }

    public UpdateTagPatterns(@Nonnull UpdateType type, int maxId, @Nullable Map<Integer, TagPattern> patterns) {
        this.type = type;
        this.maxId = maxId;
        this.patterns = patterns;
    }

    public UpdateTagPatterns(@Nonnull UpdateTagPatterns other) {
        this.type = other.type;
        this.maxId = other.maxId;
        this.patterns = other.patterns;
    }

    @Nonnull
    public static UpdateTagPatterns deserialize(@Nonnull ByteBuf buf, int offset) {
        UpdateTagPatterns obj = new UpdateTagPatterns();
        byte nullBits = buf.getByte(offset);
        obj.type = UpdateType.fromValue(buf.getByte(offset + 1));
        obj.maxId = buf.getIntLE(offset + 2);
        int pos = offset + 6;
        if ((nullBits & 1) != 0) {
            int patternsCount = VarInt.peek(buf, pos);
            if (patternsCount < 0) {
                throw ProtocolException.negativeLength("Patterns", patternsCount);
            }
            if (patternsCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Patterns", patternsCount, 4096000);
            }
            pos += VarInt.size(patternsCount);
            obj.patterns = new HashMap<Integer, TagPattern>(patternsCount);
            for (int i = 0; i < patternsCount; ++i) {
                int key = buf.getIntLE(pos);
                TagPattern val = TagPattern.deserialize(buf, pos += 4);
                pos += TagPattern.computeBytesConsumed(buf, pos);
                if (obj.patterns.put(key, val) == null) continue;
                throw ProtocolException.duplicateKey("patterns", key);
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        byte nullBits = buf.getByte(offset);
        int pos = offset + 6;
        if ((nullBits & 1) != 0) {
            int dictLen = VarInt.peek(buf, pos);
            pos += VarInt.length(buf, pos);
            for (int i = 0; i < dictLen; ++i) {
                pos += 4;
                pos += TagPattern.computeBytesConsumed(buf, pos);
            }
        }
        return pos - offset;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        int nullBits = 0;
        if (this.patterns != null) {
            nullBits = (byte)(nullBits | 1);
        }
        buf.writeByte(nullBits);
        buf.writeByte(this.type.getValue());
        buf.writeIntLE(this.maxId);
        if (this.patterns != null) {
            if (this.patterns.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Patterns", this.patterns.size(), 4096000);
            }
            VarInt.write(buf, this.patterns.size());
            for (Map.Entry<Integer, TagPattern> e : this.patterns.entrySet()) {
                buf.writeIntLE(e.getKey());
                e.getValue().serialize(buf);
            }
        }
    }

    @Override
    public int computeSize() {
        int size = 6;
        if (this.patterns != null) {
            int patternsSize = 0;
            for (Map.Entry<Integer, TagPattern> kvp : this.patterns.entrySet()) {
                patternsSize += 4 + kvp.getValue().computeSize();
            }
            size += VarInt.size(this.patterns.size()) + patternsSize;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 6) {
            return ValidationResult.error("Buffer too small: expected at least 6 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        int pos = offset + 6;
        if ((nullBits & 1) != 0) {
            int patternsCount = VarInt.peek(buffer, pos);
            if (patternsCount < 0) {
                return ValidationResult.error("Invalid dictionary count for Patterns");
            }
            if (patternsCount > 4096000) {
                return ValidationResult.error("Patterns exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (int i = 0; i < patternsCount; ++i) {
                if ((pos += 4) > buffer.writerIndex()) {
                    return ValidationResult.error("Buffer overflow reading key");
                }
                pos += TagPattern.computeBytesConsumed(buffer, pos);
            }
        }
        return ValidationResult.OK;
    }

    public UpdateTagPatterns clone() {
        UpdateTagPatterns copy = new UpdateTagPatterns();
        copy.type = this.type;
        copy.maxId = this.maxId;
        if (this.patterns != null) {
            HashMap<Integer, TagPattern> m = new HashMap<Integer, TagPattern>();
            for (Map.Entry<Integer, TagPattern> e : this.patterns.entrySet()) {
                m.put(e.getKey(), e.getValue().clone());
            }
            copy.patterns = m;
        }
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UpdateTagPatterns)) {
            return false;
        }
        UpdateTagPatterns other = (UpdateTagPatterns)obj;
        return Objects.equals((Object)this.type, (Object)other.type) && this.maxId == other.maxId && Objects.equals(this.patterns, other.patterns);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.type, this.maxId, this.patterns});
    }
}

