/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.Selector;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class StabSelector
extends Selector {
    public static final int NULLABLE_BIT_FIELD_SIZE = 0;
    public static final int FIXED_BLOCK_SIZE = 37;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 37;
    public static final int MAX_SIZE = 37;
    public float extendTop;
    public float extendBottom;
    public float extendLeft;
    public float extendRight;
    public float yawOffset;
    public float pitchOffset;
    public float rollOffset;
    public float startDistance;
    public float endDistance;
    public boolean testLineOfSight;

    public StabSelector() {
    }

    public StabSelector(float extendTop, float extendBottom, float extendLeft, float extendRight, float yawOffset, float pitchOffset, float rollOffset, float startDistance, float endDistance, boolean testLineOfSight) {
        this.extendTop = extendTop;
        this.extendBottom = extendBottom;
        this.extendLeft = extendLeft;
        this.extendRight = extendRight;
        this.yawOffset = yawOffset;
        this.pitchOffset = pitchOffset;
        this.rollOffset = rollOffset;
        this.startDistance = startDistance;
        this.endDistance = endDistance;
        this.testLineOfSight = testLineOfSight;
    }

    public StabSelector(@Nonnull StabSelector other) {
        this.extendTop = other.extendTop;
        this.extendBottom = other.extendBottom;
        this.extendLeft = other.extendLeft;
        this.extendRight = other.extendRight;
        this.yawOffset = other.yawOffset;
        this.pitchOffset = other.pitchOffset;
        this.rollOffset = other.rollOffset;
        this.startDistance = other.startDistance;
        this.endDistance = other.endDistance;
        this.testLineOfSight = other.testLineOfSight;
    }

    @Nonnull
    public static StabSelector deserialize(@Nonnull ByteBuf buf, int offset) {
        StabSelector obj = new StabSelector();
        obj.extendTop = buf.getFloatLE(offset + 0);
        obj.extendBottom = buf.getFloatLE(offset + 4);
        obj.extendLeft = buf.getFloatLE(offset + 8);
        obj.extendRight = buf.getFloatLE(offset + 12);
        obj.yawOffset = buf.getFloatLE(offset + 16);
        obj.pitchOffset = buf.getFloatLE(offset + 20);
        obj.rollOffset = buf.getFloatLE(offset + 24);
        obj.startDistance = buf.getFloatLE(offset + 28);
        obj.endDistance = buf.getFloatLE(offset + 32);
        obj.testLineOfSight = buf.getByte(offset + 36) != 0;
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 37;
    }

    @Override
    public int serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        buf.writeFloatLE(this.extendTop);
        buf.writeFloatLE(this.extendBottom);
        buf.writeFloatLE(this.extendLeft);
        buf.writeFloatLE(this.extendRight);
        buf.writeFloatLE(this.yawOffset);
        buf.writeFloatLE(this.pitchOffset);
        buf.writeFloatLE(this.rollOffset);
        buf.writeFloatLE(this.startDistance);
        buf.writeFloatLE(this.endDistance);
        buf.writeByte(this.testLineOfSight ? 1 : 0);
        return buf.writerIndex() - startPos;
    }

    @Override
    public int computeSize() {
        return 37;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 37) {
            return ValidationResult.error("Buffer too small: expected at least 37 bytes");
        }
        return ValidationResult.OK;
    }

    public StabSelector clone() {
        StabSelector copy = new StabSelector();
        copy.extendTop = this.extendTop;
        copy.extendBottom = this.extendBottom;
        copy.extendLeft = this.extendLeft;
        copy.extendRight = this.extendRight;
        copy.yawOffset = this.yawOffset;
        copy.pitchOffset = this.pitchOffset;
        copy.rollOffset = this.rollOffset;
        copy.startDistance = this.startDistance;
        copy.endDistance = this.endDistance;
        copy.testLineOfSight = this.testLineOfSight;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof StabSelector)) {
            return false;
        }
        StabSelector other = (StabSelector)obj;
        return this.extendTop == other.extendTop && this.extendBottom == other.extendBottom && this.extendLeft == other.extendLeft && this.extendRight == other.extendRight && this.yawOffset == other.yawOffset && this.pitchOffset == other.pitchOffset && this.rollOffset == other.rollOffset && this.startDistance == other.startDistance && this.endDistance == other.endDistance && this.testLineOfSight == other.testLineOfSight;
    }

    public int hashCode() {
        return Objects.hash(Float.valueOf(this.extendTop), Float.valueOf(this.extendBottom), Float.valueOf(this.extendLeft), Float.valueOf(this.extendRight), Float.valueOf(this.yawOffset), Float.valueOf(this.pitchOffset), Float.valueOf(this.rollOffset), Float.valueOf(this.startDistance), Float.valueOf(this.endDistance), this.testLineOfSight);
    }
}

