/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.buildertools;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class BuilderToolsSetSoundSet
implements Packet {
    public static final int PACKET_ID = 418;
    public static final boolean IS_COMPRESSED = false;
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 4;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 4;
    public static final int MAX_SIZE = 4;
    public int soundSetIndex;

    @Override
    public int getId() {
        return 418;
    }

    public BuilderToolsSetSoundSet() {
    }

    public BuilderToolsSetSoundSet(int soundSetIndex) {
        this.soundSetIndex = soundSetIndex;
    }

    public BuilderToolsSetSoundSet(@Nonnull BuilderToolsSetSoundSet other) {
        this.soundSetIndex = other.soundSetIndex;
    }

    @Nonnull
    public static BuilderToolsSetSoundSet deserialize(@Nonnull ByteBuf buf, int offset) {
        BuilderToolsSetSoundSet obj = new BuilderToolsSetSoundSet();
        obj.soundSetIndex = buf.getIntLE(offset + 0);
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 4;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        buf.writeIntLE(this.soundSetIndex);
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

    public BuilderToolsSetSoundSet clone() {
        BuilderToolsSetSoundSet copy = new BuilderToolsSetSoundSet();
        copy.soundSetIndex = this.soundSetIndex;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BuilderToolsSetSoundSet)) {
            return false;
        }
        BuilderToolsSetSoundSet other = (BuilderToolsSetSoundSet)obj;
        return this.soundSetIndex == other.soundSetIndex;
    }

    public int hashCode() {
        return Objects.hash(this.soundSetIndex);
    }
}

