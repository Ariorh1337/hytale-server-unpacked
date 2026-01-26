/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.world;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class PlaySoundEventEntity
implements Packet {
    public static final int PACKET_ID = 156;
    public static final boolean IS_COMPRESSED = false;
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 16;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 16;
    public static final int MAX_SIZE = 16;
    public int soundEventIndex;
    public int networkId;
    public float volumeModifier;
    public float pitchModifier;

    @Override
    public int getId() {
        return 156;
    }

    public PlaySoundEventEntity() {
    }

    public PlaySoundEventEntity(int soundEventIndex, int networkId, float volumeModifier, float pitchModifier) {
        this.soundEventIndex = soundEventIndex;
        this.networkId = networkId;
        this.volumeModifier = volumeModifier;
        this.pitchModifier = pitchModifier;
    }

    public PlaySoundEventEntity(@Nonnull PlaySoundEventEntity other) {
        this.soundEventIndex = other.soundEventIndex;
        this.networkId = other.networkId;
        this.volumeModifier = other.volumeModifier;
        this.pitchModifier = other.pitchModifier;
    }

    @Nonnull
    public static PlaySoundEventEntity deserialize(@Nonnull ByteBuf buf, int offset) {
        PlaySoundEventEntity obj = new PlaySoundEventEntity();
        obj.soundEventIndex = buf.getIntLE(offset + 0);
        obj.networkId = buf.getIntLE(offset + 4);
        obj.volumeModifier = buf.getFloatLE(offset + 8);
        obj.pitchModifier = buf.getFloatLE(offset + 12);
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 16;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        buf.writeIntLE(this.soundEventIndex);
        buf.writeIntLE(this.networkId);
        buf.writeFloatLE(this.volumeModifier);
        buf.writeFloatLE(this.pitchModifier);
    }

    @Override
    public int computeSize() {
        return 16;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 16) {
            return ValidationResult.error("Buffer too small: expected at least 16 bytes");
        }
        return ValidationResult.OK;
    }

    public PlaySoundEventEntity clone() {
        PlaySoundEventEntity copy = new PlaySoundEventEntity();
        copy.soundEventIndex = this.soundEventIndex;
        copy.networkId = this.networkId;
        copy.volumeModifier = this.volumeModifier;
        copy.pitchModifier = this.pitchModifier;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PlaySoundEventEntity)) {
            return false;
        }
        PlaySoundEventEntity other = (PlaySoundEventEntity)obj;
        return this.soundEventIndex == other.soundEventIndex && this.networkId == other.networkId && this.volumeModifier == other.volumeModifier && this.pitchModifier == other.pitchModifier;
    }

    public int hashCode() {
        return Objects.hash(this.soundEventIndex, this.networkId, Float.valueOf(this.volumeModifier), Float.valueOf(this.pitchModifier));
    }
}

