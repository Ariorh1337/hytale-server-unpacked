/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.asseteditor;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetPath;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AssetEditorFetchJsonAssetWithParentsReply
implements Packet {
    public static final int PACKET_ID = 313;
    public static final boolean IS_COMPRESSED = true;
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 5;
    public static final int VARIABLE_FIELD_COUNT = 1;
    public static final int VARIABLE_BLOCK_START = 5;
    public static final int MAX_SIZE = 0x64000000;
    public int token;
    @Nullable
    public Map<AssetPath, String> assets;

    @Override
    public int getId() {
        return 313;
    }

    public AssetEditorFetchJsonAssetWithParentsReply() {
    }

    public AssetEditorFetchJsonAssetWithParentsReply(int token, @Nullable Map<AssetPath, String> assets) {
        this.token = token;
        this.assets = assets;
    }

    public AssetEditorFetchJsonAssetWithParentsReply(@Nonnull AssetEditorFetchJsonAssetWithParentsReply other) {
        this.token = other.token;
        this.assets = other.assets;
    }

    @Nonnull
    public static AssetEditorFetchJsonAssetWithParentsReply deserialize(@Nonnull ByteBuf buf, int offset) {
        AssetEditorFetchJsonAssetWithParentsReply obj = new AssetEditorFetchJsonAssetWithParentsReply();
        byte nullBits = buf.getByte(offset);
        obj.token = buf.getIntLE(offset + 1);
        int pos = offset + 5;
        if ((nullBits & 1) != 0) {
            int assetsCount = VarInt.peek(buf, pos);
            if (assetsCount < 0) {
                throw ProtocolException.negativeLength("Assets", assetsCount);
            }
            if (assetsCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Assets", assetsCount, 4096000);
            }
            pos += VarInt.size(assetsCount);
            obj.assets = new HashMap<AssetPath, String>(assetsCount);
            for (int i = 0; i < assetsCount; ++i) {
                int valLen;
                AssetPath key = AssetPath.deserialize(buf, pos);
                if ((valLen = VarInt.peek(buf, pos += AssetPath.computeBytesConsumed(buf, pos))) < 0) {
                    throw ProtocolException.negativeLength("val", valLen);
                }
                if (valLen > 4096000) {
                    throw ProtocolException.stringTooLong("val", valLen, 4096000);
                }
                int valVarLen = VarInt.length(buf, pos);
                String val = PacketIO.readVarString(buf, pos);
                pos += valVarLen + valLen;
                if (obj.assets.put(key, val) == null) continue;
                throw ProtocolException.duplicateKey("assets", key);
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        byte nullBits = buf.getByte(offset);
        int pos = offset + 5;
        if ((nullBits & 1) != 0) {
            int dictLen = VarInt.peek(buf, pos);
            pos += VarInt.length(buf, pos);
            for (int i = 0; i < dictLen; ++i) {
                pos += AssetPath.computeBytesConsumed(buf, pos);
                int sl = VarInt.peek(buf, pos);
                pos += VarInt.length(buf, pos) + sl;
            }
        }
        return pos - offset;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        int nullBits = 0;
        if (this.assets != null) {
            nullBits = (byte)(nullBits | 1);
        }
        buf.writeByte(nullBits);
        buf.writeIntLE(this.token);
        if (this.assets != null) {
            if (this.assets.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Assets", this.assets.size(), 4096000);
            }
            VarInt.write(buf, this.assets.size());
            for (Map.Entry<AssetPath, String> e : this.assets.entrySet()) {
                e.getKey().serialize(buf);
                PacketIO.writeVarString(buf, e.getValue(), 4096000);
            }
        }
    }

    @Override
    public int computeSize() {
        int size = 5;
        if (this.assets != null) {
            int assetsSize = 0;
            for (Map.Entry<AssetPath, String> kvp : this.assets.entrySet()) {
                assetsSize += kvp.getKey().computeSize() + PacketIO.stringSize(kvp.getValue());
            }
            size += VarInt.size(this.assets.size()) + assetsSize;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 5) {
            return ValidationResult.error("Buffer too small: expected at least 5 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        int pos = offset + 5;
        if ((nullBits & 1) != 0) {
            int assetsCount = VarInt.peek(buffer, pos);
            if (assetsCount < 0) {
                return ValidationResult.error("Invalid dictionary count for Assets");
            }
            if (assetsCount > 4096000) {
                return ValidationResult.error("Assets exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (int i = 0; i < assetsCount; ++i) {
                int valueLen;
                if ((valueLen = VarInt.peek(buffer, pos += AssetPath.computeBytesConsumed(buffer, pos))) < 0) {
                    return ValidationResult.error("Invalid string length for value");
                }
                if (valueLen > 4096000) {
                    return ValidationResult.error("value exceeds max length 4096000");
                }
                pos += VarInt.length(buffer, pos);
                if ((pos += valueLen) <= buffer.writerIndex()) continue;
                return ValidationResult.error("Buffer overflow reading value");
            }
        }
        return ValidationResult.OK;
    }

    public AssetEditorFetchJsonAssetWithParentsReply clone() {
        AssetEditorFetchJsonAssetWithParentsReply copy = new AssetEditorFetchJsonAssetWithParentsReply();
        copy.token = this.token;
        copy.assets = this.assets != null ? new HashMap<AssetPath, String>(this.assets) : null;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AssetEditorFetchJsonAssetWithParentsReply)) {
            return false;
        }
        AssetEditorFetchJsonAssetWithParentsReply other = (AssetEditorFetchJsonAssetWithParentsReply)obj;
        return this.token == other.token && Objects.equals(this.assets, other.assets);
    }

    public int hashCode() {
        return Objects.hash(this.token, this.assets);
    }
}

