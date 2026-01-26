/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.asseteditor;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class AssetEditorModifiedAssetsCount
implements Packet {
    public static final int PACKET_ID = 340;
    public static final boolean IS_COMPRESSED = false;
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 4;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 4;
    public static final int MAX_SIZE = 4;
    public int count;

    @Override
    public int getId() {
        return 340;
    }

    public AssetEditorModifiedAssetsCount() {
    }

    public AssetEditorModifiedAssetsCount(int count) {
        this.count = count;
    }

    public AssetEditorModifiedAssetsCount(@Nonnull AssetEditorModifiedAssetsCount other) {
        this.count = other.count;
    }

    @Nonnull
    public static AssetEditorModifiedAssetsCount deserialize(@Nonnull ByteBuf buf, int offset) {
        AssetEditorModifiedAssetsCount obj = new AssetEditorModifiedAssetsCount();
        obj.count = buf.getIntLE(offset + 0);
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 4;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        buf.writeIntLE(this.count);
    }

    @Override
    public int computeSize() {
        return 4;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 4) {
            return ValidationResult.error("Buffer too small: expected at least 4 bytes");
        }
        return ValidationResult.OK;
    }

    public AssetEditorModifiedAssetsCount clone() {
        AssetEditorModifiedAssetsCount copy = new AssetEditorModifiedAssetsCount();
        copy.count = this.count;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AssetEditorModifiedAssetsCount)) {
            return false;
        }
        AssetEditorModifiedAssetsCount other = (AssetEditorModifiedAssetsCount)obj;
        return this.count == other.count;
    }

    public int hashCode() {
        return Objects.hash(this.count);
    }
}

