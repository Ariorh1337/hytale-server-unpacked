/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.worldmap;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import com.hypixel.hytale.protocol.packets.worldmap.BiomeData;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UpdateWorldMapSettings
implements Packet {
    public static final int PACKET_ID = 240;
    public static final boolean IS_COMPRESSED = false;
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 16;
    public static final int VARIABLE_FIELD_COUNT = 1;
    public static final int VARIABLE_BLOCK_START = 16;
    public static final int MAX_SIZE = 0x64000000;
    public boolean enabled = true;
    @Nullable
    public Map<Short, BiomeData> biomeDataMap;
    public boolean allowTeleportToCoordinates;
    public boolean allowTeleportToMarkers;
    public float defaultScale = 32.0f;
    public float minScale = 2.0f;
    public float maxScale = 256.0f;

    @Override
    public int getId() {
        return 240;
    }

    public UpdateWorldMapSettings() {
    }

    public UpdateWorldMapSettings(boolean enabled, @Nullable Map<Short, BiomeData> biomeDataMap, boolean allowTeleportToCoordinates, boolean allowTeleportToMarkers, float defaultScale, float minScale, float maxScale) {
        this.enabled = enabled;
        this.biomeDataMap = biomeDataMap;
        this.allowTeleportToCoordinates = allowTeleportToCoordinates;
        this.allowTeleportToMarkers = allowTeleportToMarkers;
        this.defaultScale = defaultScale;
        this.minScale = minScale;
        this.maxScale = maxScale;
    }

    public UpdateWorldMapSettings(@Nonnull UpdateWorldMapSettings other) {
        this.enabled = other.enabled;
        this.biomeDataMap = other.biomeDataMap;
        this.allowTeleportToCoordinates = other.allowTeleportToCoordinates;
        this.allowTeleportToMarkers = other.allowTeleportToMarkers;
        this.defaultScale = other.defaultScale;
        this.minScale = other.minScale;
        this.maxScale = other.maxScale;
    }

    @Nonnull
    public static UpdateWorldMapSettings deserialize(@Nonnull ByteBuf buf, int offset) {
        UpdateWorldMapSettings obj = new UpdateWorldMapSettings();
        byte nullBits = buf.getByte(offset);
        obj.enabled = buf.getByte(offset + 1) != 0;
        obj.allowTeleportToCoordinates = buf.getByte(offset + 2) != 0;
        obj.allowTeleportToMarkers = buf.getByte(offset + 3) != 0;
        obj.defaultScale = buf.getFloatLE(offset + 4);
        obj.minScale = buf.getFloatLE(offset + 8);
        obj.maxScale = buf.getFloatLE(offset + 12);
        int pos = offset + 16;
        if ((nullBits & 1) != 0) {
            int biomeDataMapCount = VarInt.peek(buf, pos);
            if (biomeDataMapCount < 0) {
                throw ProtocolException.negativeLength("BiomeDataMap", biomeDataMapCount);
            }
            if (biomeDataMapCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("BiomeDataMap", biomeDataMapCount, 4096000);
            }
            pos += VarInt.size(biomeDataMapCount);
            obj.biomeDataMap = new HashMap<Short, BiomeData>(biomeDataMapCount);
            for (int i = 0; i < biomeDataMapCount; ++i) {
                short key = buf.getShortLE(pos);
                BiomeData val = BiomeData.deserialize(buf, pos += 2);
                pos += BiomeData.computeBytesConsumed(buf, pos);
                if (obj.biomeDataMap.put(key, val) == null) continue;
                throw ProtocolException.duplicateKey("biomeDataMap", key);
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        byte nullBits = buf.getByte(offset);
        int pos = offset + 16;
        if ((nullBits & 1) != 0) {
            int dictLen = VarInt.peek(buf, pos);
            pos += VarInt.length(buf, pos);
            for (int i = 0; i < dictLen; ++i) {
                pos += 2;
                pos += BiomeData.computeBytesConsumed(buf, pos);
            }
        }
        return pos - offset;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        int nullBits = 0;
        if (this.biomeDataMap != null) {
            nullBits = (byte)(nullBits | 1);
        }
        buf.writeByte(nullBits);
        buf.writeByte(this.enabled ? 1 : 0);
        buf.writeByte(this.allowTeleportToCoordinates ? 1 : 0);
        buf.writeByte(this.allowTeleportToMarkers ? 1 : 0);
        buf.writeFloatLE(this.defaultScale);
        buf.writeFloatLE(this.minScale);
        buf.writeFloatLE(this.maxScale);
        if (this.biomeDataMap != null) {
            if (this.biomeDataMap.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("BiomeDataMap", this.biomeDataMap.size(), 4096000);
            }
            VarInt.write(buf, this.biomeDataMap.size());
            for (Map.Entry<Short, BiomeData> e : this.biomeDataMap.entrySet()) {
                buf.writeShortLE(e.getKey().shortValue());
                e.getValue().serialize(buf);
            }
        }
    }

    @Override
    public int computeSize() {
        int size = 16;
        if (this.biomeDataMap != null) {
            int biomeDataMapSize = 0;
            for (Map.Entry<Short, BiomeData> kvp : this.biomeDataMap.entrySet()) {
                biomeDataMapSize += 2 + kvp.getValue().computeSize();
            }
            size += VarInt.size(this.biomeDataMap.size()) + biomeDataMapSize;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 16) {
            return ValidationResult.error("Buffer too small: expected at least 16 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        int pos = offset + 16;
        if ((nullBits & 1) != 0) {
            int biomeDataMapCount = VarInt.peek(buffer, pos);
            if (biomeDataMapCount < 0) {
                return ValidationResult.error("Invalid dictionary count for BiomeDataMap");
            }
            if (biomeDataMapCount > 4096000) {
                return ValidationResult.error("BiomeDataMap exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (int i = 0; i < biomeDataMapCount; ++i) {
                if ((pos += 2) > buffer.writerIndex()) {
                    return ValidationResult.error("Buffer overflow reading key");
                }
                pos += BiomeData.computeBytesConsumed(buffer, pos);
            }
        }
        return ValidationResult.OK;
    }

    public UpdateWorldMapSettings clone() {
        UpdateWorldMapSettings copy = new UpdateWorldMapSettings();
        copy.enabled = this.enabled;
        if (this.biomeDataMap != null) {
            HashMap<Short, BiomeData> m = new HashMap<Short, BiomeData>();
            for (Map.Entry<Short, BiomeData> e : this.biomeDataMap.entrySet()) {
                m.put(e.getKey(), e.getValue().clone());
            }
            copy.biomeDataMap = m;
        }
        copy.allowTeleportToCoordinates = this.allowTeleportToCoordinates;
        copy.allowTeleportToMarkers = this.allowTeleportToMarkers;
        copy.defaultScale = this.defaultScale;
        copy.minScale = this.minScale;
        copy.maxScale = this.maxScale;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UpdateWorldMapSettings)) {
            return false;
        }
        UpdateWorldMapSettings other = (UpdateWorldMapSettings)obj;
        return this.enabled == other.enabled && Objects.equals(this.biomeDataMap, other.biomeDataMap) && this.allowTeleportToCoordinates == other.allowTeleportToCoordinates && this.allowTeleportToMarkers == other.allowTeleportToMarkers && this.defaultScale == other.defaultScale && this.minScale == other.minScale && this.maxScale == other.maxScale;
    }

    public int hashCode() {
        return Objects.hash(this.enabled, this.biomeDataMap, this.allowTeleportToCoordinates, this.allowTeleportToMarkers, Float.valueOf(this.defaultScale), Float.valueOf(this.minScale), Float.valueOf(this.maxScale));
    }
}

