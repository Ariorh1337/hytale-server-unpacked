/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.assets;

import com.hypixel.hytale.protocol.EqualizerEffect;
import com.hypixel.hytale.protocol.Packet;
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

public class UpdateEqualizerEffects
implements Packet {
    public static final int PACKET_ID = 82;
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
    public Map<Integer, EqualizerEffect> effects;

    @Override
    public int getId() {
        return 82;
    }

    public UpdateEqualizerEffects() {
    }

    public UpdateEqualizerEffects(@Nonnull UpdateType type, int maxId, @Nullable Map<Integer, EqualizerEffect> effects) {
        this.type = type;
        this.maxId = maxId;
        this.effects = effects;
    }

    public UpdateEqualizerEffects(@Nonnull UpdateEqualizerEffects other) {
        this.type = other.type;
        this.maxId = other.maxId;
        this.effects = other.effects;
    }

    @Nonnull
    public static UpdateEqualizerEffects deserialize(@Nonnull ByteBuf buf, int offset) {
        UpdateEqualizerEffects obj = new UpdateEqualizerEffects();
        byte nullBits = buf.getByte(offset);
        obj.type = UpdateType.fromValue(buf.getByte(offset + 1));
        obj.maxId = buf.getIntLE(offset + 2);
        int pos = offset + 6;
        if ((nullBits & 1) != 0) {
            int effectsCount = VarInt.peek(buf, pos);
            if (effectsCount < 0) {
                throw ProtocolException.negativeLength("Effects", effectsCount);
            }
            if (effectsCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Effects", effectsCount, 4096000);
            }
            pos += VarInt.size(effectsCount);
            obj.effects = new HashMap<Integer, EqualizerEffect>(effectsCount);
            for (int i = 0; i < effectsCount; ++i) {
                int key = buf.getIntLE(pos);
                EqualizerEffect val = EqualizerEffect.deserialize(buf, pos += 4);
                pos += EqualizerEffect.computeBytesConsumed(buf, pos);
                if (obj.effects.put(key, val) == null) continue;
                throw ProtocolException.duplicateKey("effects", key);
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
                pos += EqualizerEffect.computeBytesConsumed(buf, pos);
            }
        }
        return pos - offset;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        int nullBits = 0;
        if (this.effects != null) {
            nullBits = (byte)(nullBits | 1);
        }
        buf.writeByte(nullBits);
        buf.writeByte(this.type.getValue());
        buf.writeIntLE(this.maxId);
        if (this.effects != null) {
            if (this.effects.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Effects", this.effects.size(), 4096000);
            }
            VarInt.write(buf, this.effects.size());
            for (Map.Entry<Integer, EqualizerEffect> e : this.effects.entrySet()) {
                buf.writeIntLE(e.getKey());
                e.getValue().serialize(buf);
            }
        }
    }

    @Override
    public int computeSize() {
        int size = 6;
        if (this.effects != null) {
            int effectsSize = 0;
            for (Map.Entry<Integer, EqualizerEffect> kvp : this.effects.entrySet()) {
                effectsSize += 4 + kvp.getValue().computeSize();
            }
            size += VarInt.size(this.effects.size()) + effectsSize;
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
            int effectsCount = VarInt.peek(buffer, pos);
            if (effectsCount < 0) {
                return ValidationResult.error("Invalid dictionary count for Effects");
            }
            if (effectsCount > 4096000) {
                return ValidationResult.error("Effects exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (int i = 0; i < effectsCount; ++i) {
                if ((pos += 4) > buffer.writerIndex()) {
                    return ValidationResult.error("Buffer overflow reading key");
                }
                pos += EqualizerEffect.computeBytesConsumed(buffer, pos);
            }
        }
        return ValidationResult.OK;
    }

    public UpdateEqualizerEffects clone() {
        UpdateEqualizerEffects copy = new UpdateEqualizerEffects();
        copy.type = this.type;
        copy.maxId = this.maxId;
        if (this.effects != null) {
            HashMap<Integer, EqualizerEffect> m = new HashMap<Integer, EqualizerEffect>();
            for (Map.Entry<Integer, EqualizerEffect> e : this.effects.entrySet()) {
                m.put(e.getKey(), e.getValue().clone());
            }
            copy.effects = m;
        }
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UpdateEqualizerEffects)) {
            return false;
        }
        UpdateEqualizerEffects other = (UpdateEqualizerEffects)obj;
        return Objects.equals((Object)this.type, (Object)other.type) && this.maxId == other.maxId && Objects.equals(this.effects, other.effects);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.type, this.maxId, this.effects});
    }
}

