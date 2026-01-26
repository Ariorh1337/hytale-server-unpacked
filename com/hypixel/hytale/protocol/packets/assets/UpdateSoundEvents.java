/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.assets;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.SoundEvent;
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

public class UpdateSoundEvents
implements Packet {
    public static final int PACKET_ID = 65;
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
    public Map<Integer, SoundEvent> soundEvents;

    @Override
    public int getId() {
        return 65;
    }

    public UpdateSoundEvents() {
    }

    public UpdateSoundEvents(@Nonnull UpdateType type, int maxId, @Nullable Map<Integer, SoundEvent> soundEvents) {
        this.type = type;
        this.maxId = maxId;
        this.soundEvents = soundEvents;
    }

    public UpdateSoundEvents(@Nonnull UpdateSoundEvents other) {
        this.type = other.type;
        this.maxId = other.maxId;
        this.soundEvents = other.soundEvents;
    }

    @Nonnull
    public static UpdateSoundEvents deserialize(@Nonnull ByteBuf buf, int offset) {
        UpdateSoundEvents obj = new UpdateSoundEvents();
        byte nullBits = buf.getByte(offset);
        obj.type = UpdateType.fromValue(buf.getByte(offset + 1));
        obj.maxId = buf.getIntLE(offset + 2);
        int pos = offset + 6;
        if ((nullBits & 1) != 0) {
            int soundEventsCount = VarInt.peek(buf, pos);
            if (soundEventsCount < 0) {
                throw ProtocolException.negativeLength("SoundEvents", soundEventsCount);
            }
            if (soundEventsCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("SoundEvents", soundEventsCount, 4096000);
            }
            pos += VarInt.size(soundEventsCount);
            obj.soundEvents = new HashMap<Integer, SoundEvent>(soundEventsCount);
            for (int i = 0; i < soundEventsCount; ++i) {
                int key = buf.getIntLE(pos);
                SoundEvent val = SoundEvent.deserialize(buf, pos += 4);
                pos += SoundEvent.computeBytesConsumed(buf, pos);
                if (obj.soundEvents.put(key, val) == null) continue;
                throw ProtocolException.duplicateKey("soundEvents", key);
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
                pos += SoundEvent.computeBytesConsumed(buf, pos);
            }
        }
        return pos - offset;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        int nullBits = 0;
        if (this.soundEvents != null) {
            nullBits = (byte)(nullBits | 1);
        }
        buf.writeByte(nullBits);
        buf.writeByte(this.type.getValue());
        buf.writeIntLE(this.maxId);
        if (this.soundEvents != null) {
            if (this.soundEvents.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("SoundEvents", this.soundEvents.size(), 4096000);
            }
            VarInt.write(buf, this.soundEvents.size());
            for (Map.Entry<Integer, SoundEvent> e : this.soundEvents.entrySet()) {
                buf.writeIntLE(e.getKey());
                e.getValue().serialize(buf);
            }
        }
    }

    @Override
    public int computeSize() {
        int size = 6;
        if (this.soundEvents != null) {
            int soundEventsSize = 0;
            for (Map.Entry<Integer, SoundEvent> kvp : this.soundEvents.entrySet()) {
                soundEventsSize += 4 + kvp.getValue().computeSize();
            }
            size += VarInt.size(this.soundEvents.size()) + soundEventsSize;
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
            int soundEventsCount = VarInt.peek(buffer, pos);
            if (soundEventsCount < 0) {
                return ValidationResult.error("Invalid dictionary count for SoundEvents");
            }
            if (soundEventsCount > 4096000) {
                return ValidationResult.error("SoundEvents exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (int i = 0; i < soundEventsCount; ++i) {
                if ((pos += 4) > buffer.writerIndex()) {
                    return ValidationResult.error("Buffer overflow reading key");
                }
                pos += SoundEvent.computeBytesConsumed(buffer, pos);
            }
        }
        return ValidationResult.OK;
    }

    public UpdateSoundEvents clone() {
        UpdateSoundEvents copy = new UpdateSoundEvents();
        copy.type = this.type;
        copy.maxId = this.maxId;
        if (this.soundEvents != null) {
            HashMap<Integer, SoundEvent> m = new HashMap<Integer, SoundEvent>();
            for (Map.Entry<Integer, SoundEvent> e : this.soundEvents.entrySet()) {
                m.put(e.getKey(), e.getValue().clone());
            }
            copy.soundEvents = m;
        }
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UpdateSoundEvents)) {
            return false;
        }
        UpdateSoundEvents other = (UpdateSoundEvents)obj;
        return Objects.equals((Object)this.type, (Object)other.type) && this.maxId == other.maxId && Objects.equals(this.soundEvents, other.soundEvents);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.type, this.maxId, this.soundEvents});
    }
}

