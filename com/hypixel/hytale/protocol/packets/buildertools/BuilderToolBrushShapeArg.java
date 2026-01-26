/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.buildertools;

import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.packets.buildertools.BrushShape;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class BuilderToolBrushShapeArg {
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 1;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 1;
    public static final int MAX_SIZE = 1;
    @Nonnull
    public BrushShape defaultValue = BrushShape.Cube;

    public BuilderToolBrushShapeArg() {
    }

    public BuilderToolBrushShapeArg(@Nonnull BrushShape defaultValue) {
        this.defaultValue = defaultValue;
    }

    public BuilderToolBrushShapeArg(@Nonnull BuilderToolBrushShapeArg other) {
        this.defaultValue = other.defaultValue;
    }

    @Nonnull
    public static BuilderToolBrushShapeArg deserialize(@Nonnull ByteBuf buf, int offset) {
        BuilderToolBrushShapeArg obj = new BuilderToolBrushShapeArg();
        obj.defaultValue = BrushShape.fromValue(buf.getByte(offset + 0));
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 1;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        buf.writeByte(this.defaultValue.getValue());
    }

    public int computeSize() {
        return 1;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 1) {
            return ValidationResult.error("Buffer too small: expected at least 1 bytes");
        }
        return ValidationResult.OK;
    }

    public BuilderToolBrushShapeArg clone() {
        BuilderToolBrushShapeArg copy = new BuilderToolBrushShapeArg();
        copy.defaultValue = this.defaultValue;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BuilderToolBrushShapeArg)) {
            return false;
        }
        BuilderToolBrushShapeArg other = (BuilderToolBrushShapeArg)obj;
        return Objects.equals((Object)this.defaultValue, (Object)other.defaultValue);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.defaultValue});
    }
}

