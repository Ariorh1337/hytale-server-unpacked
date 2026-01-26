/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.Vector3f;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RailPoint {
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 25;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 25;
    public static final int MAX_SIZE = 25;
    @Nullable
    public Vector3f point;
    @Nullable
    public Vector3f normal;

    public RailPoint() {
    }

    public RailPoint(@Nullable Vector3f point, @Nullable Vector3f normal) {
        this.point = point;
        this.normal = normal;
    }

    public RailPoint(@Nonnull RailPoint other) {
        this.point = other.point;
        this.normal = other.normal;
    }

    @Nonnull
    public static RailPoint deserialize(@Nonnull ByteBuf buf, int offset) {
        RailPoint obj = new RailPoint();
        byte nullBits = buf.getByte(offset);
        if ((nullBits & 1) != 0) {
            obj.point = Vector3f.deserialize(buf, offset + 1);
        }
        if ((nullBits & 2) != 0) {
            obj.normal = Vector3f.deserialize(buf, offset + 13);
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 25;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        byte nullBits = 0;
        if (this.point != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.normal != null) {
            nullBits = (byte)(nullBits | 2);
        }
        buf.writeByte(nullBits);
        if (this.point != null) {
            this.point.serialize(buf);
        } else {
            buf.writeZero(12);
        }
        if (this.normal != null) {
            this.normal.serialize(buf);
        } else {
            buf.writeZero(12);
        }
    }

    public int computeSize() {
        return 25;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 25) {
            return ValidationResult.error("Buffer too small: expected at least 25 bytes");
        }
        return ValidationResult.OK;
    }

    public RailPoint clone() {
        RailPoint copy = new RailPoint();
        copy.point = this.point != null ? this.point.clone() : null;
        copy.normal = this.normal != null ? this.normal.clone() : null;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RailPoint)) {
            return false;
        }
        RailPoint other = (RailPoint)obj;
        return Objects.equals(this.point, other.point) && Objects.equals(this.normal, other.normal);
    }

    public int hashCode() {
        return Objects.hash(this.point, this.normal);
    }
}

