/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.assets;

import com.hypixel.hytale.protocol.BlockParticleSet;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.UpdateType;
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

public class UpdateBlockParticleSets
implements Packet {
    public static final int PACKET_ID = 44;
    public static final boolean IS_COMPRESSED = true;
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 2;
    public static final int VARIABLE_FIELD_COUNT = 1;
    public static final int VARIABLE_BLOCK_START = 2;
    public static final int MAX_SIZE = 0x64000000;
    @Nonnull
    public UpdateType type = UpdateType.Init;
    @Nullable
    public Map<String, BlockParticleSet> blockParticleSets;

    @Override
    public int getId() {
        return 44;
    }

    public UpdateBlockParticleSets() {
    }

    public UpdateBlockParticleSets(@Nonnull UpdateType type, @Nullable Map<String, BlockParticleSet> blockParticleSets) {
        this.type = type;
        this.blockParticleSets = blockParticleSets;
    }

    public UpdateBlockParticleSets(@Nonnull UpdateBlockParticleSets other) {
        this.type = other.type;
        this.blockParticleSets = other.blockParticleSets;
    }

    @Nonnull
    public static UpdateBlockParticleSets deserialize(@Nonnull ByteBuf buf, int offset) {
        UpdateBlockParticleSets obj = new UpdateBlockParticleSets();
        byte nullBits = buf.getByte(offset);
        obj.type = UpdateType.fromValue(buf.getByte(offset + 1));
        int pos = offset + 2;
        if ((nullBits & 1) != 0) {
            int blockParticleSetsCount = VarInt.peek(buf, pos);
            if (blockParticleSetsCount < 0) {
                throw ProtocolException.negativeLength("BlockParticleSets", blockParticleSetsCount);
            }
            if (blockParticleSetsCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("BlockParticleSets", blockParticleSetsCount, 4096000);
            }
            pos += VarInt.size(blockParticleSetsCount);
            obj.blockParticleSets = new HashMap<String, BlockParticleSet>(blockParticleSetsCount);
            for (int i = 0; i < blockParticleSetsCount; ++i) {
                int keyLen = VarInt.peek(buf, pos);
                if (keyLen < 0) {
                    throw ProtocolException.negativeLength("key", keyLen);
                }
                if (keyLen > 4096000) {
                    throw ProtocolException.stringTooLong("key", keyLen, 4096000);
                }
                int keyVarLen = VarInt.length(buf, pos);
                String key = PacketIO.readVarString(buf, pos);
                BlockParticleSet val = BlockParticleSet.deserialize(buf, pos += keyVarLen + keyLen);
                pos += BlockParticleSet.computeBytesConsumed(buf, pos);
                if (obj.blockParticleSets.put(key, val) == null) continue;
                throw ProtocolException.duplicateKey("blockParticleSets", key);
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        byte nullBits = buf.getByte(offset);
        int pos = offset + 2;
        if ((nullBits & 1) != 0) {
            int dictLen = VarInt.peek(buf, pos);
            pos += VarInt.length(buf, pos);
            for (int i = 0; i < dictLen; ++i) {
                int sl = VarInt.peek(buf, pos);
                pos += VarInt.length(buf, pos) + sl;
                pos += BlockParticleSet.computeBytesConsumed(buf, pos);
            }
        }
        return pos - offset;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        int nullBits = 0;
        if (this.blockParticleSets != null) {
            nullBits = (byte)(nullBits | 1);
        }
        buf.writeByte(nullBits);
        buf.writeByte(this.type.getValue());
        if (this.blockParticleSets != null) {
            if (this.blockParticleSets.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("BlockParticleSets", this.blockParticleSets.size(), 4096000);
            }
            VarInt.write(buf, this.blockParticleSets.size());
            for (Map.Entry<String, BlockParticleSet> e : this.blockParticleSets.entrySet()) {
                PacketIO.writeVarString(buf, e.getKey(), 4096000);
                e.getValue().serialize(buf);
            }
        }
    }

    @Override
    public int computeSize() {
        int size = 2;
        if (this.blockParticleSets != null) {
            int blockParticleSetsSize = 0;
            for (Map.Entry<String, BlockParticleSet> kvp : this.blockParticleSets.entrySet()) {
                blockParticleSetsSize += PacketIO.stringSize(kvp.getKey()) + kvp.getValue().computeSize();
            }
            size += VarInt.size(this.blockParticleSets.size()) + blockParticleSetsSize;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 2) {
            return ValidationResult.error("Buffer too small: expected at least 2 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        int pos = offset + 2;
        if ((nullBits & 1) != 0) {
            int blockParticleSetsCount = VarInt.peek(buffer, pos);
            if (blockParticleSetsCount < 0) {
                return ValidationResult.error("Invalid dictionary count for BlockParticleSets");
            }
            if (blockParticleSetsCount > 4096000) {
                return ValidationResult.error("BlockParticleSets exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (int i = 0; i < blockParticleSetsCount; ++i) {
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
                pos += BlockParticleSet.computeBytesConsumed(buffer, pos);
            }
        }
        return ValidationResult.OK;
    }

    public UpdateBlockParticleSets clone() {
        UpdateBlockParticleSets copy = new UpdateBlockParticleSets();
        copy.type = this.type;
        if (this.blockParticleSets != null) {
            HashMap<String, BlockParticleSet> m = new HashMap<String, BlockParticleSet>();
            for (Map.Entry<String, BlockParticleSet> e : this.blockParticleSets.entrySet()) {
                m.put(e.getKey(), e.getValue().clone());
            }
            copy.blockParticleSets = m;
        }
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UpdateBlockParticleSets)) {
            return false;
        }
        UpdateBlockParticleSets other = (UpdateBlockParticleSets)obj;
        return Objects.equals((Object)this.type, (Object)other.type) && Objects.equals(this.blockParticleSets, other.blockParticleSets);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.type, this.blockParticleSets});
    }
}

