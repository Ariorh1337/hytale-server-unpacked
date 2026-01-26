/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.setup;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import com.hypixel.hytale.protocol.packets.setup.ClientFeature;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UpdateFeatures
implements Packet {
    public static final int PACKET_ID = 31;
    public static final boolean IS_COMPRESSED = false;
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 1;
    public static final int VARIABLE_FIELD_COUNT = 1;
    public static final int VARIABLE_BLOCK_START = 1;
    public static final int MAX_SIZE = 8192006;
    @Nullable
    public Map<ClientFeature, Boolean> features;

    @Override
    public int getId() {
        return 31;
    }

    public UpdateFeatures() {
    }

    public UpdateFeatures(@Nullable Map<ClientFeature, Boolean> features) {
        this.features = features;
    }

    public UpdateFeatures(@Nonnull UpdateFeatures other) {
        this.features = other.features;
    }

    @Nonnull
    public static UpdateFeatures deserialize(@Nonnull ByteBuf buf, int offset) {
        UpdateFeatures obj = new UpdateFeatures();
        byte nullBits = buf.getByte(offset);
        int pos = offset + 1;
        if ((nullBits & 1) != 0) {
            int featuresCount = VarInt.peek(buf, pos);
            if (featuresCount < 0) {
                throw ProtocolException.negativeLength("Features", featuresCount);
            }
            if (featuresCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Features", featuresCount, 4096000);
            }
            pos += VarInt.size(featuresCount);
            obj.features = new HashMap<ClientFeature, Boolean>(featuresCount);
            for (int i = 0; i < featuresCount; ++i) {
                ClientFeature key = ClientFeature.fromValue(buf.getByte(pos));
                boolean val = buf.getByte(++pos) != 0;
                ++pos;
                if (obj.features.put(key, val) == null) continue;
                throw ProtocolException.duplicateKey("features", (Object)key);
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
                ++pos;
                ++pos;
            }
        }
        return pos - offset;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        int nullBits = 0;
        if (this.features != null) {
            nullBits = (byte)(nullBits | 1);
        }
        buf.writeByte(nullBits);
        if (this.features != null) {
            if (this.features.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Features", this.features.size(), 4096000);
            }
            VarInt.write(buf, this.features.size());
            for (Map.Entry<ClientFeature, Boolean> e : this.features.entrySet()) {
                buf.writeByte(e.getKey().getValue());
                buf.writeByte(e.getValue() != false ? 1 : 0);
            }
        }
    }

    @Override
    public int computeSize() {
        int size = 1;
        if (this.features != null) {
            size += VarInt.size(this.features.size()) + this.features.size() * 2;
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
            int featuresCount = VarInt.peek(buffer, pos);
            if (featuresCount < 0) {
                return ValidationResult.error("Invalid dictionary count for Features");
            }
            if (featuresCount > 4096000) {
                return ValidationResult.error("Features exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (int i = 0; i < featuresCount; ++i) {
                ++pos;
                if (++pos <= buffer.writerIndex()) continue;
                return ValidationResult.error("Buffer overflow reading value");
            }
        }
        return ValidationResult.OK;
    }

    public UpdateFeatures clone() {
        UpdateFeatures copy = new UpdateFeatures();
        copy.features = this.features != null ? new HashMap<ClientFeature, Boolean>(this.features) : null;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UpdateFeatures)) {
            return false;
        }
        UpdateFeatures other = (UpdateFeatures)obj;
        return Objects.equals(this.features, other.features);
    }

    public int hashCode() {
        return Objects.hash(this.features);
    }
}

