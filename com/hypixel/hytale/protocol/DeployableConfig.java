/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.Model;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DeployableConfig {
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 2;
    public static final int VARIABLE_FIELD_COUNT = 2;
    public static final int VARIABLE_BLOCK_START = 10;
    public static final int MAX_SIZE = 2058;
    @Nullable
    public Model model;
    @Nullable
    public Model modelPreview;
    public boolean allowPlaceOnWalls;

    public DeployableConfig() {
    }

    public DeployableConfig(@Nullable Model model, @Nullable Model modelPreview, boolean allowPlaceOnWalls) {
        this.model = model;
        this.modelPreview = modelPreview;
        this.allowPlaceOnWalls = allowPlaceOnWalls;
    }

    public DeployableConfig(@Nonnull DeployableConfig other) {
        this.model = other.model;
        this.modelPreview = other.modelPreview;
        this.allowPlaceOnWalls = other.allowPlaceOnWalls;
    }

    @Nonnull
    public static DeployableConfig deserialize(@Nonnull ByteBuf buf, int offset) {
        DeployableConfig obj = new DeployableConfig();
        byte nullBits = buf.getByte(offset);
        boolean bl = obj.allowPlaceOnWalls = buf.getByte(offset + 1) != 0;
        if ((nullBits & 1) != 0) {
            int varPos0 = offset + 10 + buf.getIntLE(offset + 2);
            obj.model = Model.deserialize(buf, varPos0);
        }
        if ((nullBits & 2) != 0) {
            int varPos1 = offset + 10 + buf.getIntLE(offset + 6);
            obj.modelPreview = Model.deserialize(buf, varPos1);
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        byte nullBits = buf.getByte(offset);
        int maxEnd = 10;
        if ((nullBits & 1) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 2);
            int pos0 = offset + 10 + fieldOffset0;
            if ((pos0 += Model.computeBytesConsumed(buf, pos0)) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 2) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 6);
            int pos1 = offset + 10 + fieldOffset1;
            if ((pos1 += Model.computeBytesConsumed(buf, pos1)) - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        return maxEnd;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        byte nullBits = 0;
        if (this.model != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.modelPreview != null) {
            nullBits = (byte)(nullBits | 2);
        }
        buf.writeByte(nullBits);
        buf.writeByte(this.allowPlaceOnWalls ? 1 : 0);
        int modelOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int modelPreviewOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.model != null) {
            buf.setIntLE(modelOffsetSlot, buf.writerIndex() - varBlockStart);
            this.model.serialize(buf);
        } else {
            buf.setIntLE(modelOffsetSlot, -1);
        }
        if (this.modelPreview != null) {
            buf.setIntLE(modelPreviewOffsetSlot, buf.writerIndex() - varBlockStart);
            this.modelPreview.serialize(buf);
        } else {
            buf.setIntLE(modelPreviewOffsetSlot, -1);
        }
    }

    public int computeSize() {
        int size = 10;
        if (this.model != null) {
            size += this.model.computeSize();
        }
        if (this.modelPreview != null) {
            size += this.modelPreview.computeSize();
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int pos;
        if (buffer.readableBytes() - offset < 10) {
            return ValidationResult.error("Buffer too small: expected at least 10 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 1) != 0) {
            int modelOffset = buffer.getIntLE(offset + 2);
            if (modelOffset < 0) {
                return ValidationResult.error("Invalid offset for Model");
            }
            pos = offset + 10 + modelOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Model");
            }
            ValidationResult modelResult = Model.validateStructure(buffer, pos);
            if (!modelResult.isValid()) {
                return ValidationResult.error("Invalid Model: " + modelResult.error());
            }
            pos += Model.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits & 2) != 0) {
            int modelPreviewOffset = buffer.getIntLE(offset + 6);
            if (modelPreviewOffset < 0) {
                return ValidationResult.error("Invalid offset for ModelPreview");
            }
            pos = offset + 10 + modelPreviewOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for ModelPreview");
            }
            ValidationResult modelPreviewResult = Model.validateStructure(buffer, pos);
            if (!modelPreviewResult.isValid()) {
                return ValidationResult.error("Invalid ModelPreview: " + modelPreviewResult.error());
            }
            pos += Model.computeBytesConsumed(buffer, pos);
        }
        return ValidationResult.OK;
    }

    public DeployableConfig clone() {
        DeployableConfig copy = new DeployableConfig();
        copy.model = this.model != null ? this.model.clone() : null;
        copy.modelPreview = this.modelPreview != null ? this.modelPreview.clone() : null;
        copy.allowPlaceOnWalls = this.allowPlaceOnWalls;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DeployableConfig)) {
            return false;
        }
        DeployableConfig other = (DeployableConfig)obj;
        return Objects.equals(this.model, other.model) && Objects.equals(this.modelPreview, other.modelPreview) && this.allowPlaceOnWalls == other.allowPlaceOnWalls;
    }

    public int hashCode() {
        return Objects.hash(this.model, this.modelPreview, this.allowPlaceOnWalls);
    }
}

