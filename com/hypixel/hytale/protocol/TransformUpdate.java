/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.ComponentUpdate;
import com.hypixel.hytale.protocol.ModelTransform;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class TransformUpdate
extends ComponentUpdate {
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 49;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 49;
    public static final int MAX_SIZE = 49;
    @Nonnull
    public ModelTransform transform = new ModelTransform();

    public TransformUpdate() {
    }

    public TransformUpdate(@Nonnull ModelTransform transform) {
        this.transform = transform;
    }

    public TransformUpdate(@Nonnull TransformUpdate other) {
        this.transform = other.transform;
    }

    @Nonnull
    public static TransformUpdate deserialize(@Nonnull ByteBuf buf, int offset) {
        TransformUpdate obj = new TransformUpdate();
        obj.transform = ModelTransform.deserialize(buf, offset + 0);
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 49;
    }

    @Override
    public int serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        this.transform.serialize(buf);
        return buf.writerIndex() - startPos;
    }

    @Override
    public int computeSize() {
        return 49;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 49) {
            return ValidationResult.error("Buffer too small: expected at least 49 bytes");
        }
        return ValidationResult.OK;
    }

    public TransformUpdate clone() {
        TransformUpdate copy = new TransformUpdate();
        copy.transform = this.transform.clone();
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TransformUpdate)) {
            return false;
        }
        TransformUpdate other = (TransformUpdate)obj;
        return Objects.equals(this.transform, other.transform);
    }

    public int hashCode() {
        return Objects.hash(this.transform);
    }
}

