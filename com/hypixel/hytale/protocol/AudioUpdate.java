/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.ComponentUpdate;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class AudioUpdate
extends ComponentUpdate {
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 0;
    public static final int VARIABLE_FIELD_COUNT = 1;
    public static final int VARIABLE_BLOCK_START = 0;
    public static final int MAX_SIZE = 16384005;
    @Nonnull
    public int[] soundEventIds = new int[0];

    public AudioUpdate() {
    }

    public AudioUpdate(@Nonnull int[] soundEventIds) {
        this.soundEventIds = soundEventIds;
    }

    public AudioUpdate(@Nonnull AudioUpdate other) {
        this.soundEventIds = other.soundEventIds;
    }

    @Nonnull
    public static AudioUpdate deserialize(@Nonnull ByteBuf buf, int offset) {
        AudioUpdate obj = new AudioUpdate();
        int pos = offset + 0;
        int soundEventIdsCount = VarInt.peek(buf, pos);
        if (soundEventIdsCount < 0) {
            throw ProtocolException.negativeLength("SoundEventIds", soundEventIdsCount);
        }
        if (soundEventIdsCount > 4096000) {
            throw ProtocolException.arrayTooLong("SoundEventIds", soundEventIdsCount, 4096000);
        }
        int soundEventIdsVarLen = VarInt.size(soundEventIdsCount);
        if ((long)(pos + soundEventIdsVarLen) + (long)soundEventIdsCount * 4L > (long)buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("SoundEventIds", pos + soundEventIdsVarLen + soundEventIdsCount * 4, buf.readableBytes());
        }
        pos += soundEventIdsVarLen;
        obj.soundEventIds = new int[soundEventIdsCount];
        for (int i = 0; i < soundEventIdsCount; ++i) {
            obj.soundEventIds[i] = buf.getIntLE(pos + i * 4);
        }
        pos += soundEventIdsCount * 4;
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int pos = offset + 0;
        int arrLen = VarInt.peek(buf, pos);
        pos += VarInt.length(buf, pos) + arrLen * 4;
        return pos - offset;
    }

    @Override
    public int serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        if (this.soundEventIds.length > 4096000) {
            throw ProtocolException.arrayTooLong("SoundEventIds", this.soundEventIds.length, 4096000);
        }
        VarInt.write(buf, this.soundEventIds.length);
        for (int item : this.soundEventIds) {
            buf.writeIntLE(item);
        }
        return buf.writerIndex() - startPos;
    }

    @Override
    public int computeSize() {
        int size = 0;
        return size += VarInt.size(this.soundEventIds.length) + this.soundEventIds.length * 4;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 0) {
            return ValidationResult.error("Buffer too small: expected at least 0 bytes");
        }
        int pos = offset + 0;
        int soundEventIdsCount = VarInt.peek(buffer, pos);
        if (soundEventIdsCount < 0) {
            return ValidationResult.error("Invalid array count for SoundEventIds");
        }
        if (soundEventIdsCount > 4096000) {
            return ValidationResult.error("SoundEventIds exceeds max length 4096000");
        }
        pos += VarInt.length(buffer, pos);
        if ((pos += soundEventIdsCount * 4) > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading SoundEventIds");
        }
        return ValidationResult.OK;
    }

    public AudioUpdate clone() {
        AudioUpdate copy = new AudioUpdate();
        copy.soundEventIds = Arrays.copyOf(this.soundEventIds, this.soundEventIds.length);
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AudioUpdate)) {
            return false;
        }
        AudioUpdate other = (AudioUpdate)obj;
        return Arrays.equals(this.soundEventIds, other.soundEventIds);
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + Arrays.hashCode(this.soundEventIds);
        return result;
    }
}

