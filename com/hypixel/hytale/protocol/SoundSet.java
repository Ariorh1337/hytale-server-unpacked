/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SoundSet {
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 2;
    public static final int VARIABLE_FIELD_COUNT = 2;
    public static final int VARIABLE_BLOCK_START = 10;
    public static final int MAX_SIZE = 0x64000000;
    @Nullable
    public String id;
    @Nullable
    public Map<String, Integer> sounds;
    @Nonnull
    public SoundCategory category = SoundCategory.Music;

    public SoundSet() {
    }

    public SoundSet(@Nullable String id, @Nullable Map<String, Integer> sounds, @Nonnull SoundCategory category) {
        this.id = id;
        this.sounds = sounds;
        this.category = category;
    }

    public SoundSet(@Nonnull SoundSet other) {
        this.id = other.id;
        this.sounds = other.sounds;
        this.category = other.category;
    }

    @Nonnull
    public static SoundSet deserialize(@Nonnull ByteBuf buf, int offset) {
        SoundSet obj = new SoundSet();
        byte nullBits = buf.getByte(offset);
        obj.category = SoundCategory.fromValue(buf.getByte(offset + 1));
        if ((nullBits & 1) != 0) {
            int varPos0 = offset + 10 + buf.getIntLE(offset + 2);
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
            int varPos1 = offset + 10 + buf.getIntLE(offset + 6);
            int soundsCount = VarInt.peek(buf, varPos1);
            if (soundsCount < 0) {
                throw ProtocolException.negativeLength("Sounds", soundsCount);
            }
            if (soundsCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Sounds", soundsCount, 4096000);
            }
            int varIntLen = VarInt.length(buf, varPos1);
            obj.sounds = new HashMap<String, Integer>(soundsCount);
            int dictPos = varPos1 + varIntLen;
            for (int i = 0; i < soundsCount; ++i) {
                int keyLen = VarInt.peek(buf, dictPos);
                if (keyLen < 0) {
                    throw ProtocolException.negativeLength("key", keyLen);
                }
                if (keyLen > 4096000) {
                    throw ProtocolException.stringTooLong("key", keyLen, 4096000);
                }
                int keyVarLen = VarInt.length(buf, dictPos);
                String key = PacketIO.readVarString(buf, dictPos);
                int val = buf.getIntLE(dictPos += keyVarLen + keyLen);
                dictPos += 4;
                if (obj.sounds.put(key, val) == null) continue;
                throw ProtocolException.duplicateKey("sounds", key);
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        byte nullBits = buf.getByte(offset);
        int maxEnd = 10;
        if ((nullBits & 1) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 2);
            int pos0 = offset + 10 + fieldOffset0;
            int sl = VarInt.peek(buf, pos0);
            if ((pos0 += VarInt.length(buf, pos0) + sl) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 2) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 6);
            int pos1 = offset + 10 + fieldOffset1;
            int dictLen = VarInt.peek(buf, pos1);
            pos1 += VarInt.length(buf, pos1);
            for (int i = 0; i < dictLen; ++i) {
                int sl = VarInt.peek(buf, pos1);
                pos1 += VarInt.length(buf, pos1) + sl;
                pos1 += 4;
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
        if (this.id != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.sounds != null) {
            nullBits = (byte)(nullBits | 2);
        }
        buf.writeByte(nullBits);
        buf.writeByte(this.category.getValue());
        int idOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int soundsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.id != null) {
            buf.setIntLE(idOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.id, 4096000);
        } else {
            buf.setIntLE(idOffsetSlot, -1);
        }
        if (this.sounds != null) {
            buf.setIntLE(soundsOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.sounds.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Sounds", this.sounds.size(), 4096000);
            }
            VarInt.write(buf, this.sounds.size());
            for (Map.Entry<String, Integer> e : this.sounds.entrySet()) {
                PacketIO.writeVarString(buf, e.getKey(), 4096000);
                buf.writeIntLE(e.getValue());
            }
        } else {
            buf.setIntLE(soundsOffsetSlot, -1);
        }
    }

    public int computeSize() {
        int size = 10;
        if (this.id != null) {
            size += PacketIO.stringSize(this.id);
        }
        if (this.sounds != null) {
            int soundsSize = 0;
            for (Map.Entry<String, Integer> kvp : this.sounds.entrySet()) {
                soundsSize += PacketIO.stringSize(kvp.getKey()) + 4;
            }
            size += VarInt.size(this.sounds.size()) + soundsSize;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int pos;
        if (buffer.readableBytes() - offset < 10) {
            return ValidationResult.error("Buffer too small: expected at least 10 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 1) != 0) {
            int idOffset = buffer.getIntLE(offset + 2);
            if (idOffset < 0) {
                return ValidationResult.error("Invalid offset for Id");
            }
            pos = offset + 10 + idOffset;
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
            int soundsOffset = buffer.getIntLE(offset + 6);
            if (soundsOffset < 0) {
                return ValidationResult.error("Invalid offset for Sounds");
            }
            pos = offset + 10 + soundsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Sounds");
            }
            int soundsCount = VarInt.peek(buffer, pos);
            if (soundsCount < 0) {
                return ValidationResult.error("Invalid dictionary count for Sounds");
            }
            if (soundsCount > 4096000) {
                return ValidationResult.error("Sounds exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (int i = 0; i < soundsCount; ++i) {
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
                if ((pos += 4) <= buffer.writerIndex()) continue;
                return ValidationResult.error("Buffer overflow reading value");
            }
        }
        return ValidationResult.OK;
    }

    public SoundSet clone() {
        SoundSet copy = new SoundSet();
        copy.id = this.id;
        copy.sounds = this.sounds != null ? new HashMap<String, Integer>(this.sounds) : null;
        copy.category = this.category;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SoundSet)) {
            return false;
        }
        SoundSet other = (SoundSet)obj;
        return Objects.equals(this.id, other.id) && Objects.equals(this.sounds, other.sounds) && Objects.equals((Object)this.category, (Object)other.category);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.id, this.sounds, this.category});
    }
}

