/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.interaction;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChain;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class SyncInteractionChains
implements Packet {
    public static final int PACKET_ID = 290;
    public static final boolean IS_COMPRESSED = false;
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 0;
    public static final int VARIABLE_FIELD_COUNT = 1;
    public static final int VARIABLE_BLOCK_START = 0;
    public static final int MAX_SIZE = 0x64000000;
    @Nonnull
    public SyncInteractionChain[] updates = new SyncInteractionChain[0];

    @Override
    public int getId() {
        return 290;
    }

    public SyncInteractionChains() {
    }

    public SyncInteractionChains(@Nonnull SyncInteractionChain[] updates) {
        this.updates = updates;
    }

    public SyncInteractionChains(@Nonnull SyncInteractionChains other) {
        this.updates = other.updates;
    }

    @Nonnull
    public static SyncInteractionChains deserialize(@Nonnull ByteBuf buf, int offset) {
        SyncInteractionChains obj = new SyncInteractionChains();
        int pos = offset + 0;
        int updatesCount = VarInt.peek(buf, pos);
        if (updatesCount < 0) {
            throw ProtocolException.negativeLength("Updates", updatesCount);
        }
        if (updatesCount > 4096000) {
            throw ProtocolException.arrayTooLong("Updates", updatesCount, 4096000);
        }
        int updatesVarLen = VarInt.size(updatesCount);
        if ((long)(pos + updatesVarLen) + (long)updatesCount * 33L > (long)buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Updates", pos + updatesVarLen + updatesCount * 33, buf.readableBytes());
        }
        pos += updatesVarLen;
        obj.updates = new SyncInteractionChain[updatesCount];
        for (int i = 0; i < updatesCount; ++i) {
            obj.updates[i] = SyncInteractionChain.deserialize(buf, pos);
            pos += SyncInteractionChain.computeBytesConsumed(buf, pos);
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int pos = offset + 0;
        int arrLen = VarInt.peek(buf, pos);
        pos += VarInt.length(buf, pos);
        for (int i = 0; i < arrLen; ++i) {
            pos += SyncInteractionChain.computeBytesConsumed(buf, pos);
        }
        return pos - offset;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        if (this.updates.length > 4096000) {
            throw ProtocolException.arrayTooLong("Updates", this.updates.length, 4096000);
        }
        VarInt.write(buf, this.updates.length);
        for (SyncInteractionChain item : this.updates) {
            item.serialize(buf);
        }
    }

    @Override
    public int computeSize() {
        int size = 0;
        int updatesSize = 0;
        for (SyncInteractionChain elem : this.updates) {
            updatesSize += elem.computeSize();
        }
        return size += VarInt.size(this.updates.length) + updatesSize;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 0) {
            return ValidationResult.error("Buffer too small: expected at least 0 bytes");
        }
        int pos = offset + 0;
        int updatesCount = VarInt.peek(buffer, pos);
        if (updatesCount < 0) {
            return ValidationResult.error("Invalid array count for Updates");
        }
        if (updatesCount > 4096000) {
            return ValidationResult.error("Updates exceeds max length 4096000");
        }
        pos += VarInt.length(buffer, pos);
        for (int i = 0; i < updatesCount; ++i) {
            ValidationResult structResult = SyncInteractionChain.validateStructure(buffer, pos);
            if (!structResult.isValid()) {
                return ValidationResult.error("Invalid SyncInteractionChain in Updates[" + i + "]: " + structResult.error());
            }
            pos += SyncInteractionChain.computeBytesConsumed(buffer, pos);
        }
        return ValidationResult.OK;
    }

    public SyncInteractionChains clone() {
        SyncInteractionChains copy = new SyncInteractionChains();
        copy.updates = (SyncInteractionChain[])Arrays.stream(this.updates).map(e -> e.clone()).toArray(SyncInteractionChain[]::new);
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SyncInteractionChains)) {
            return false;
        }
        SyncInteractionChains other = (SyncInteractionChains)obj;
        return Arrays.equals(this.updates, other.updates);
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + Arrays.hashCode(this.updates);
        return result;
    }
}

