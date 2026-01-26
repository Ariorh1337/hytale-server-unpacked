/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.buildertools;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolBlockArg;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolBoolArg;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolBrushAxisArg;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolBrushOriginArg;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolBrushShapeArg;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolIntArg;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolMaskArg;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolRotationArg;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolStringArg;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderToolBrushData {
    public static final int NULLABLE_BIT_FIELD_SIZE = 3;
    public static final int FIXED_BLOCK_SIZE = 48;
    public static final int VARIABLE_FIELD_COUNT = 9;
    public static final int VARIABLE_BLOCK_START = 84;
    public static final int MAX_SIZE = 0x64000000;
    @Nullable
    public BuilderToolIntArg width;
    @Nullable
    public BuilderToolIntArg height;
    @Nullable
    public BuilderToolIntArg thickness;
    @Nullable
    public BuilderToolBoolArg capped;
    @Nullable
    public BuilderToolBrushShapeArg shape;
    @Nullable
    public BuilderToolBrushOriginArg origin;
    @Nullable
    public BuilderToolBoolArg originRotation;
    @Nullable
    public BuilderToolBrushAxisArg rotationAxis;
    @Nullable
    public BuilderToolRotationArg rotationAngle;
    @Nullable
    public BuilderToolBrushAxisArg mirrorAxis;
    @Nullable
    public BuilderToolBlockArg material;
    @Nullable
    public BuilderToolBlockArg[] favoriteMaterials;
    @Nullable
    public BuilderToolMaskArg mask;
    @Nullable
    public BuilderToolMaskArg maskAbove;
    @Nullable
    public BuilderToolMaskArg maskNot;
    @Nullable
    public BuilderToolMaskArg maskBelow;
    @Nullable
    public BuilderToolMaskArg maskAdjacent;
    @Nullable
    public BuilderToolMaskArg maskNeighbor;
    @Nullable
    public BuilderToolStringArg[] maskCommands;
    @Nullable
    public BuilderToolBoolArg useMaskCommands;
    @Nullable
    public BuilderToolBoolArg invertMask;

    public BuilderToolBrushData() {
    }

    public BuilderToolBrushData(@Nullable BuilderToolIntArg width, @Nullable BuilderToolIntArg height, @Nullable BuilderToolIntArg thickness, @Nullable BuilderToolBoolArg capped, @Nullable BuilderToolBrushShapeArg shape, @Nullable BuilderToolBrushOriginArg origin, @Nullable BuilderToolBoolArg originRotation, @Nullable BuilderToolBrushAxisArg rotationAxis, @Nullable BuilderToolRotationArg rotationAngle, @Nullable BuilderToolBrushAxisArg mirrorAxis, @Nullable BuilderToolBlockArg material, @Nullable BuilderToolBlockArg[] favoriteMaterials, @Nullable BuilderToolMaskArg mask, @Nullable BuilderToolMaskArg maskAbove, @Nullable BuilderToolMaskArg maskNot, @Nullable BuilderToolMaskArg maskBelow, @Nullable BuilderToolMaskArg maskAdjacent, @Nullable BuilderToolMaskArg maskNeighbor, @Nullable BuilderToolStringArg[] maskCommands, @Nullable BuilderToolBoolArg useMaskCommands, @Nullable BuilderToolBoolArg invertMask) {
        this.width = width;
        this.height = height;
        this.thickness = thickness;
        this.capped = capped;
        this.shape = shape;
        this.origin = origin;
        this.originRotation = originRotation;
        this.rotationAxis = rotationAxis;
        this.rotationAngle = rotationAngle;
        this.mirrorAxis = mirrorAxis;
        this.material = material;
        this.favoriteMaterials = favoriteMaterials;
        this.mask = mask;
        this.maskAbove = maskAbove;
        this.maskNot = maskNot;
        this.maskBelow = maskBelow;
        this.maskAdjacent = maskAdjacent;
        this.maskNeighbor = maskNeighbor;
        this.maskCommands = maskCommands;
        this.useMaskCommands = useMaskCommands;
        this.invertMask = invertMask;
    }

    public BuilderToolBrushData(@Nonnull BuilderToolBrushData other) {
        this.width = other.width;
        this.height = other.height;
        this.thickness = other.thickness;
        this.capped = other.capped;
        this.shape = other.shape;
        this.origin = other.origin;
        this.originRotation = other.originRotation;
        this.rotationAxis = other.rotationAxis;
        this.rotationAngle = other.rotationAngle;
        this.mirrorAxis = other.mirrorAxis;
        this.material = other.material;
        this.favoriteMaterials = other.favoriteMaterials;
        this.mask = other.mask;
        this.maskAbove = other.maskAbove;
        this.maskNot = other.maskNot;
        this.maskBelow = other.maskBelow;
        this.maskAdjacent = other.maskAdjacent;
        this.maskNeighbor = other.maskNeighbor;
        this.maskCommands = other.maskCommands;
        this.useMaskCommands = other.useMaskCommands;
        this.invertMask = other.invertMask;
    }

    @Nonnull
    public static BuilderToolBrushData deserialize(@Nonnull ByteBuf buf, int offset) {
        int i;
        int elemPos;
        int varIntLen;
        BuilderToolBrushData obj = new BuilderToolBrushData();
        byte[] nullBits = PacketIO.readBytes(buf, offset, 3);
        if ((nullBits[0] & 1) != 0) {
            obj.width = BuilderToolIntArg.deserialize(buf, offset + 3);
        }
        if ((nullBits[0] & 2) != 0) {
            obj.height = BuilderToolIntArg.deserialize(buf, offset + 15);
        }
        if ((nullBits[0] & 4) != 0) {
            obj.thickness = BuilderToolIntArg.deserialize(buf, offset + 27);
        }
        if ((nullBits[0] & 8) != 0) {
            obj.capped = BuilderToolBoolArg.deserialize(buf, offset + 39);
        }
        if ((nullBits[0] & 0x10) != 0) {
            obj.shape = BuilderToolBrushShapeArg.deserialize(buf, offset + 40);
        }
        if ((nullBits[0] & 0x20) != 0) {
            obj.origin = BuilderToolBrushOriginArg.deserialize(buf, offset + 41);
        }
        if ((nullBits[0] & 0x40) != 0) {
            obj.originRotation = BuilderToolBoolArg.deserialize(buf, offset + 42);
        }
        if ((nullBits[0] & 0x80) != 0) {
            obj.rotationAxis = BuilderToolBrushAxisArg.deserialize(buf, offset + 43);
        }
        if ((nullBits[1] & 1) != 0) {
            obj.rotationAngle = BuilderToolRotationArg.deserialize(buf, offset + 44);
        }
        if ((nullBits[1] & 2) != 0) {
            obj.mirrorAxis = BuilderToolBrushAxisArg.deserialize(buf, offset + 45);
        }
        if ((nullBits[1] & 4) != 0) {
            obj.useMaskCommands = BuilderToolBoolArg.deserialize(buf, offset + 46);
        }
        if ((nullBits[1] & 8) != 0) {
            obj.invertMask = BuilderToolBoolArg.deserialize(buf, offset + 47);
        }
        if ((nullBits[1] & 0x10) != 0) {
            int varPos0 = offset + 84 + buf.getIntLE(offset + 48);
            obj.material = BuilderToolBlockArg.deserialize(buf, varPos0);
        }
        if ((nullBits[1] & 0x20) != 0) {
            int varPos1 = offset + 84 + buf.getIntLE(offset + 52);
            int favoriteMaterialsCount = VarInt.peek(buf, varPos1);
            if (favoriteMaterialsCount < 0) {
                throw ProtocolException.negativeLength("FavoriteMaterials", favoriteMaterialsCount);
            }
            if (favoriteMaterialsCount > 4096000) {
                throw ProtocolException.arrayTooLong("FavoriteMaterials", favoriteMaterialsCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos1);
            if ((long)(varPos1 + varIntLen) + (long)favoriteMaterialsCount * 2L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("FavoriteMaterials", varPos1 + varIntLen + favoriteMaterialsCount * 2, buf.readableBytes());
            }
            obj.favoriteMaterials = new BuilderToolBlockArg[favoriteMaterialsCount];
            elemPos = varPos1 + varIntLen;
            for (i = 0; i < favoriteMaterialsCount; ++i) {
                obj.favoriteMaterials[i] = BuilderToolBlockArg.deserialize(buf, elemPos);
                elemPos += BuilderToolBlockArg.computeBytesConsumed(buf, elemPos);
            }
        }
        if ((nullBits[1] & 0x40) != 0) {
            int varPos2 = offset + 84 + buf.getIntLE(offset + 56);
            obj.mask = BuilderToolMaskArg.deserialize(buf, varPos2);
        }
        if ((nullBits[1] & 0x80) != 0) {
            int varPos3 = offset + 84 + buf.getIntLE(offset + 60);
            obj.maskAbove = BuilderToolMaskArg.deserialize(buf, varPos3);
        }
        if ((nullBits[2] & 1) != 0) {
            int varPos4 = offset + 84 + buf.getIntLE(offset + 64);
            obj.maskNot = BuilderToolMaskArg.deserialize(buf, varPos4);
        }
        if ((nullBits[2] & 2) != 0) {
            int varPos5 = offset + 84 + buf.getIntLE(offset + 68);
            obj.maskBelow = BuilderToolMaskArg.deserialize(buf, varPos5);
        }
        if ((nullBits[2] & 4) != 0) {
            int varPos6 = offset + 84 + buf.getIntLE(offset + 72);
            obj.maskAdjacent = BuilderToolMaskArg.deserialize(buf, varPos6);
        }
        if ((nullBits[2] & 8) != 0) {
            int varPos7 = offset + 84 + buf.getIntLE(offset + 76);
            obj.maskNeighbor = BuilderToolMaskArg.deserialize(buf, varPos7);
        }
        if ((nullBits[2] & 0x10) != 0) {
            int varPos8 = offset + 84 + buf.getIntLE(offset + 80);
            int maskCommandsCount = VarInt.peek(buf, varPos8);
            if (maskCommandsCount < 0) {
                throw ProtocolException.negativeLength("MaskCommands", maskCommandsCount);
            }
            if (maskCommandsCount > 4096000) {
                throw ProtocolException.arrayTooLong("MaskCommands", maskCommandsCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos8);
            if ((long)(varPos8 + varIntLen) + (long)maskCommandsCount * 1L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("MaskCommands", varPos8 + varIntLen + maskCommandsCount * 1, buf.readableBytes());
            }
            obj.maskCommands = new BuilderToolStringArg[maskCommandsCount];
            elemPos = varPos8 + varIntLen;
            for (i = 0; i < maskCommandsCount; ++i) {
                obj.maskCommands[i] = BuilderToolStringArg.deserialize(buf, elemPos);
                elemPos += BuilderToolStringArg.computeBytesConsumed(buf, elemPos);
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int i;
        int arrLen;
        byte[] nullBits = PacketIO.readBytes(buf, offset, 3);
        int maxEnd = 84;
        if ((nullBits[1] & 0x10) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 48);
            int pos0 = offset + 84 + fieldOffset0;
            if ((pos0 += BuilderToolBlockArg.computeBytesConsumed(buf, pos0)) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits[1] & 0x20) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 52);
            int pos1 = offset + 84 + fieldOffset1;
            arrLen = VarInt.peek(buf, pos1);
            pos1 += VarInt.length(buf, pos1);
            for (i = 0; i < arrLen; ++i) {
                pos1 += BuilderToolBlockArg.computeBytesConsumed(buf, pos1);
            }
            if (pos1 - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        if ((nullBits[1] & 0x40) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 56);
            int pos2 = offset + 84 + fieldOffset2;
            if ((pos2 += BuilderToolMaskArg.computeBytesConsumed(buf, pos2)) - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        if ((nullBits[1] & 0x80) != 0) {
            int fieldOffset3 = buf.getIntLE(offset + 60);
            int pos3 = offset + 84 + fieldOffset3;
            if ((pos3 += BuilderToolMaskArg.computeBytesConsumed(buf, pos3)) - offset > maxEnd) {
                maxEnd = pos3 - offset;
            }
        }
        if ((nullBits[2] & 1) != 0) {
            int fieldOffset4 = buf.getIntLE(offset + 64);
            int pos4 = offset + 84 + fieldOffset4;
            if ((pos4 += BuilderToolMaskArg.computeBytesConsumed(buf, pos4)) - offset > maxEnd) {
                maxEnd = pos4 - offset;
            }
        }
        if ((nullBits[2] & 2) != 0) {
            int fieldOffset5 = buf.getIntLE(offset + 68);
            int pos5 = offset + 84 + fieldOffset5;
            if ((pos5 += BuilderToolMaskArg.computeBytesConsumed(buf, pos5)) - offset > maxEnd) {
                maxEnd = pos5 - offset;
            }
        }
        if ((nullBits[2] & 4) != 0) {
            int fieldOffset6 = buf.getIntLE(offset + 72);
            int pos6 = offset + 84 + fieldOffset6;
            if ((pos6 += BuilderToolMaskArg.computeBytesConsumed(buf, pos6)) - offset > maxEnd) {
                maxEnd = pos6 - offset;
            }
        }
        if ((nullBits[2] & 8) != 0) {
            int fieldOffset7 = buf.getIntLE(offset + 76);
            int pos7 = offset + 84 + fieldOffset7;
            if ((pos7 += BuilderToolMaskArg.computeBytesConsumed(buf, pos7)) - offset > maxEnd) {
                maxEnd = pos7 - offset;
            }
        }
        if ((nullBits[2] & 0x10) != 0) {
            int fieldOffset8 = buf.getIntLE(offset + 80);
            int pos8 = offset + 84 + fieldOffset8;
            arrLen = VarInt.peek(buf, pos8);
            pos8 += VarInt.length(buf, pos8);
            for (i = 0; i < arrLen; ++i) {
                pos8 += BuilderToolStringArg.computeBytesConsumed(buf, pos8);
            }
            if (pos8 - offset > maxEnd) {
                maxEnd = pos8 - offset;
            }
        }
        return maxEnd;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        byte[] nullBits = new byte[3];
        if (this.width != null) {
            nullBits[0] = (byte)(nullBits[0] | 1);
        }
        if (this.height != null) {
            nullBits[0] = (byte)(nullBits[0] | 2);
        }
        if (this.thickness != null) {
            nullBits[0] = (byte)(nullBits[0] | 4);
        }
        if (this.capped != null) {
            nullBits[0] = (byte)(nullBits[0] | 8);
        }
        if (this.shape != null) {
            nullBits[0] = (byte)(nullBits[0] | 0x10);
        }
        if (this.origin != null) {
            nullBits[0] = (byte)(nullBits[0] | 0x20);
        }
        if (this.originRotation != null) {
            nullBits[0] = (byte)(nullBits[0] | 0x40);
        }
        if (this.rotationAxis != null) {
            nullBits[0] = (byte)(nullBits[0] | 0x80);
        }
        if (this.rotationAngle != null) {
            nullBits[1] = (byte)(nullBits[1] | 1);
        }
        if (this.mirrorAxis != null) {
            nullBits[1] = (byte)(nullBits[1] | 2);
        }
        if (this.useMaskCommands != null) {
            nullBits[1] = (byte)(nullBits[1] | 4);
        }
        if (this.invertMask != null) {
            nullBits[1] = (byte)(nullBits[1] | 8);
        }
        if (this.material != null) {
            nullBits[1] = (byte)(nullBits[1] | 0x10);
        }
        if (this.favoriteMaterials != null) {
            nullBits[1] = (byte)(nullBits[1] | 0x20);
        }
        if (this.mask != null) {
            nullBits[1] = (byte)(nullBits[1] | 0x40);
        }
        if (this.maskAbove != null) {
            nullBits[1] = (byte)(nullBits[1] | 0x80);
        }
        if (this.maskNot != null) {
            nullBits[2] = (byte)(nullBits[2] | 1);
        }
        if (this.maskBelow != null) {
            nullBits[2] = (byte)(nullBits[2] | 2);
        }
        if (this.maskAdjacent != null) {
            nullBits[2] = (byte)(nullBits[2] | 4);
        }
        if (this.maskNeighbor != null) {
            nullBits[2] = (byte)(nullBits[2] | 8);
        }
        if (this.maskCommands != null) {
            nullBits[2] = (byte)(nullBits[2] | 0x10);
        }
        buf.writeBytes(nullBits);
        if (this.width != null) {
            this.width.serialize(buf);
        } else {
            buf.writeZero(12);
        }
        if (this.height != null) {
            this.height.serialize(buf);
        } else {
            buf.writeZero(12);
        }
        if (this.thickness != null) {
            this.thickness.serialize(buf);
        } else {
            buf.writeZero(12);
        }
        if (this.capped != null) {
            this.capped.serialize(buf);
        } else {
            buf.writeZero(1);
        }
        if (this.shape != null) {
            this.shape.serialize(buf);
        } else {
            buf.writeZero(1);
        }
        if (this.origin != null) {
            this.origin.serialize(buf);
        } else {
            buf.writeZero(1);
        }
        if (this.originRotation != null) {
            this.originRotation.serialize(buf);
        } else {
            buf.writeZero(1);
        }
        if (this.rotationAxis != null) {
            this.rotationAxis.serialize(buf);
        } else {
            buf.writeZero(1);
        }
        if (this.rotationAngle != null) {
            this.rotationAngle.serialize(buf);
        } else {
            buf.writeZero(1);
        }
        if (this.mirrorAxis != null) {
            this.mirrorAxis.serialize(buf);
        } else {
            buf.writeZero(1);
        }
        if (this.useMaskCommands != null) {
            this.useMaskCommands.serialize(buf);
        } else {
            buf.writeZero(1);
        }
        if (this.invertMask != null) {
            this.invertMask.serialize(buf);
        } else {
            buf.writeZero(1);
        }
        int materialOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int favoriteMaterialsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int maskOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int maskAboveOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int maskNotOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int maskBelowOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int maskAdjacentOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int maskNeighborOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int maskCommandsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.material != null) {
            buf.setIntLE(materialOffsetSlot, buf.writerIndex() - varBlockStart);
            this.material.serialize(buf);
        } else {
            buf.setIntLE(materialOffsetSlot, -1);
        }
        if (this.favoriteMaterials != null) {
            buf.setIntLE(favoriteMaterialsOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.favoriteMaterials.length > 4096000) {
                throw ProtocolException.arrayTooLong("FavoriteMaterials", this.favoriteMaterials.length, 4096000);
            }
            VarInt.write(buf, this.favoriteMaterials.length);
            for (BuilderToolBlockArg builderToolBlockArg : this.favoriteMaterials) {
                builderToolBlockArg.serialize(buf);
            }
        } else {
            buf.setIntLE(favoriteMaterialsOffsetSlot, -1);
        }
        if (this.mask != null) {
            buf.setIntLE(maskOffsetSlot, buf.writerIndex() - varBlockStart);
            this.mask.serialize(buf);
        } else {
            buf.setIntLE(maskOffsetSlot, -1);
        }
        if (this.maskAbove != null) {
            buf.setIntLE(maskAboveOffsetSlot, buf.writerIndex() - varBlockStart);
            this.maskAbove.serialize(buf);
        } else {
            buf.setIntLE(maskAboveOffsetSlot, -1);
        }
        if (this.maskNot != null) {
            buf.setIntLE(maskNotOffsetSlot, buf.writerIndex() - varBlockStart);
            this.maskNot.serialize(buf);
        } else {
            buf.setIntLE(maskNotOffsetSlot, -1);
        }
        if (this.maskBelow != null) {
            buf.setIntLE(maskBelowOffsetSlot, buf.writerIndex() - varBlockStart);
            this.maskBelow.serialize(buf);
        } else {
            buf.setIntLE(maskBelowOffsetSlot, -1);
        }
        if (this.maskAdjacent != null) {
            buf.setIntLE(maskAdjacentOffsetSlot, buf.writerIndex() - varBlockStart);
            this.maskAdjacent.serialize(buf);
        } else {
            buf.setIntLE(maskAdjacentOffsetSlot, -1);
        }
        if (this.maskNeighbor != null) {
            buf.setIntLE(maskNeighborOffsetSlot, buf.writerIndex() - varBlockStart);
            this.maskNeighbor.serialize(buf);
        } else {
            buf.setIntLE(maskNeighborOffsetSlot, -1);
        }
        if (this.maskCommands != null) {
            buf.setIntLE(maskCommandsOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.maskCommands.length > 4096000) {
                throw ProtocolException.arrayTooLong("MaskCommands", this.maskCommands.length, 4096000);
            }
            VarInt.write(buf, this.maskCommands.length);
            for (BuilderToolStringArg builderToolStringArg : this.maskCommands) {
                builderToolStringArg.serialize(buf);
            }
        } else {
            buf.setIntLE(maskCommandsOffsetSlot, -1);
        }
    }

    public int computeSize() {
        int size = 84;
        if (this.material != null) {
            size += this.material.computeSize();
        }
        if (this.favoriteMaterials != null) {
            int favoriteMaterialsSize = 0;
            for (BuilderToolBlockArg builderToolBlockArg : this.favoriteMaterials) {
                favoriteMaterialsSize += builderToolBlockArg.computeSize();
            }
            size += VarInt.size(this.favoriteMaterials.length) + favoriteMaterialsSize;
        }
        if (this.mask != null) {
            size += this.mask.computeSize();
        }
        if (this.maskAbove != null) {
            size += this.maskAbove.computeSize();
        }
        if (this.maskNot != null) {
            size += this.maskNot.computeSize();
        }
        if (this.maskBelow != null) {
            size += this.maskBelow.computeSize();
        }
        if (this.maskAdjacent != null) {
            size += this.maskAdjacent.computeSize();
        }
        if (this.maskNeighbor != null) {
            size += this.maskNeighbor.computeSize();
        }
        if (this.maskCommands != null) {
            int maskCommandsSize = 0;
            for (BuilderToolStringArg builderToolStringArg : this.maskCommands) {
                maskCommandsSize += builderToolStringArg.computeSize();
            }
            size += VarInt.size(this.maskCommands.length) + maskCommandsSize;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        ValidationResult structResult;
        int i;
        int pos;
        if (buffer.readableBytes() - offset < 84) {
            return ValidationResult.error("Buffer too small: expected at least 84 bytes");
        }
        byte[] nullBits = PacketIO.readBytes(buffer, offset, 3);
        if ((nullBits[1] & 0x10) != 0) {
            int materialOffset = buffer.getIntLE(offset + 48);
            if (materialOffset < 0) {
                return ValidationResult.error("Invalid offset for Material");
            }
            pos = offset + 84 + materialOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Material");
            }
            ValidationResult materialResult = BuilderToolBlockArg.validateStructure(buffer, pos);
            if (!materialResult.isValid()) {
                return ValidationResult.error("Invalid Material: " + materialResult.error());
            }
            pos += BuilderToolBlockArg.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits[1] & 0x20) != 0) {
            int favoriteMaterialsOffset = buffer.getIntLE(offset + 52);
            if (favoriteMaterialsOffset < 0) {
                return ValidationResult.error("Invalid offset for FavoriteMaterials");
            }
            pos = offset + 84 + favoriteMaterialsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for FavoriteMaterials");
            }
            int favoriteMaterialsCount = VarInt.peek(buffer, pos);
            if (favoriteMaterialsCount < 0) {
                return ValidationResult.error("Invalid array count for FavoriteMaterials");
            }
            if (favoriteMaterialsCount > 4096000) {
                return ValidationResult.error("FavoriteMaterials exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < favoriteMaterialsCount; ++i) {
                structResult = BuilderToolBlockArg.validateStructure(buffer, pos);
                if (!structResult.isValid()) {
                    return ValidationResult.error("Invalid BuilderToolBlockArg in FavoriteMaterials[" + i + "]: " + structResult.error());
                }
                pos += BuilderToolBlockArg.computeBytesConsumed(buffer, pos);
            }
        }
        if ((nullBits[1] & 0x40) != 0) {
            int maskOffset = buffer.getIntLE(offset + 56);
            if (maskOffset < 0) {
                return ValidationResult.error("Invalid offset for Mask");
            }
            pos = offset + 84 + maskOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Mask");
            }
            ValidationResult maskResult = BuilderToolMaskArg.validateStructure(buffer, pos);
            if (!maskResult.isValid()) {
                return ValidationResult.error("Invalid Mask: " + maskResult.error());
            }
            pos += BuilderToolMaskArg.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits[1] & 0x80) != 0) {
            int maskAboveOffset = buffer.getIntLE(offset + 60);
            if (maskAboveOffset < 0) {
                return ValidationResult.error("Invalid offset for MaskAbove");
            }
            pos = offset + 84 + maskAboveOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for MaskAbove");
            }
            ValidationResult maskAboveResult = BuilderToolMaskArg.validateStructure(buffer, pos);
            if (!maskAboveResult.isValid()) {
                return ValidationResult.error("Invalid MaskAbove: " + maskAboveResult.error());
            }
            pos += BuilderToolMaskArg.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits[2] & 1) != 0) {
            int maskNotOffset = buffer.getIntLE(offset + 64);
            if (maskNotOffset < 0) {
                return ValidationResult.error("Invalid offset for MaskNot");
            }
            pos = offset + 84 + maskNotOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for MaskNot");
            }
            ValidationResult maskNotResult = BuilderToolMaskArg.validateStructure(buffer, pos);
            if (!maskNotResult.isValid()) {
                return ValidationResult.error("Invalid MaskNot: " + maskNotResult.error());
            }
            pos += BuilderToolMaskArg.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits[2] & 2) != 0) {
            int maskBelowOffset = buffer.getIntLE(offset + 68);
            if (maskBelowOffset < 0) {
                return ValidationResult.error("Invalid offset for MaskBelow");
            }
            pos = offset + 84 + maskBelowOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for MaskBelow");
            }
            ValidationResult maskBelowResult = BuilderToolMaskArg.validateStructure(buffer, pos);
            if (!maskBelowResult.isValid()) {
                return ValidationResult.error("Invalid MaskBelow: " + maskBelowResult.error());
            }
            pos += BuilderToolMaskArg.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits[2] & 4) != 0) {
            int maskAdjacentOffset = buffer.getIntLE(offset + 72);
            if (maskAdjacentOffset < 0) {
                return ValidationResult.error("Invalid offset for MaskAdjacent");
            }
            pos = offset + 84 + maskAdjacentOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for MaskAdjacent");
            }
            ValidationResult maskAdjacentResult = BuilderToolMaskArg.validateStructure(buffer, pos);
            if (!maskAdjacentResult.isValid()) {
                return ValidationResult.error("Invalid MaskAdjacent: " + maskAdjacentResult.error());
            }
            pos += BuilderToolMaskArg.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits[2] & 8) != 0) {
            int maskNeighborOffset = buffer.getIntLE(offset + 76);
            if (maskNeighborOffset < 0) {
                return ValidationResult.error("Invalid offset for MaskNeighbor");
            }
            pos = offset + 84 + maskNeighborOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for MaskNeighbor");
            }
            ValidationResult maskNeighborResult = BuilderToolMaskArg.validateStructure(buffer, pos);
            if (!maskNeighborResult.isValid()) {
                return ValidationResult.error("Invalid MaskNeighbor: " + maskNeighborResult.error());
            }
            pos += BuilderToolMaskArg.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits[2] & 0x10) != 0) {
            int maskCommandsOffset = buffer.getIntLE(offset + 80);
            if (maskCommandsOffset < 0) {
                return ValidationResult.error("Invalid offset for MaskCommands");
            }
            pos = offset + 84 + maskCommandsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for MaskCommands");
            }
            int maskCommandsCount = VarInt.peek(buffer, pos);
            if (maskCommandsCount < 0) {
                return ValidationResult.error("Invalid array count for MaskCommands");
            }
            if (maskCommandsCount > 4096000) {
                return ValidationResult.error("MaskCommands exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < maskCommandsCount; ++i) {
                structResult = BuilderToolStringArg.validateStructure(buffer, pos);
                if (!structResult.isValid()) {
                    return ValidationResult.error("Invalid BuilderToolStringArg in MaskCommands[" + i + "]: " + structResult.error());
                }
                pos += BuilderToolStringArg.computeBytesConsumed(buffer, pos);
            }
        }
        return ValidationResult.OK;
    }

    public BuilderToolBrushData clone() {
        BuilderToolBrushData copy = new BuilderToolBrushData();
        copy.width = this.width != null ? this.width.clone() : null;
        copy.height = this.height != null ? this.height.clone() : null;
        copy.thickness = this.thickness != null ? this.thickness.clone() : null;
        copy.capped = this.capped != null ? this.capped.clone() : null;
        copy.shape = this.shape != null ? this.shape.clone() : null;
        copy.origin = this.origin != null ? this.origin.clone() : null;
        copy.originRotation = this.originRotation != null ? this.originRotation.clone() : null;
        copy.rotationAxis = this.rotationAxis != null ? this.rotationAxis.clone() : null;
        copy.rotationAngle = this.rotationAngle != null ? this.rotationAngle.clone() : null;
        copy.mirrorAxis = this.mirrorAxis != null ? this.mirrorAxis.clone() : null;
        copy.material = this.material != null ? this.material.clone() : null;
        copy.favoriteMaterials = this.favoriteMaterials != null ? (BuilderToolBlockArg[])Arrays.stream(this.favoriteMaterials).map(e -> e.clone()).toArray(BuilderToolBlockArg[]::new) : null;
        copy.mask = this.mask != null ? this.mask.clone() : null;
        copy.maskAbove = this.maskAbove != null ? this.maskAbove.clone() : null;
        copy.maskNot = this.maskNot != null ? this.maskNot.clone() : null;
        copy.maskBelow = this.maskBelow != null ? this.maskBelow.clone() : null;
        copy.maskAdjacent = this.maskAdjacent != null ? this.maskAdjacent.clone() : null;
        copy.maskNeighbor = this.maskNeighbor != null ? this.maskNeighbor.clone() : null;
        copy.maskCommands = this.maskCommands != null ? (BuilderToolStringArg[])Arrays.stream(this.maskCommands).map(e -> e.clone()).toArray(BuilderToolStringArg[]::new) : null;
        copy.useMaskCommands = this.useMaskCommands != null ? this.useMaskCommands.clone() : null;
        copy.invertMask = this.invertMask != null ? this.invertMask.clone() : null;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BuilderToolBrushData)) {
            return false;
        }
        BuilderToolBrushData other = (BuilderToolBrushData)obj;
        return Objects.equals(this.width, other.width) && Objects.equals(this.height, other.height) && Objects.equals(this.thickness, other.thickness) && Objects.equals(this.capped, other.capped) && Objects.equals(this.shape, other.shape) && Objects.equals(this.origin, other.origin) && Objects.equals(this.originRotation, other.originRotation) && Objects.equals(this.rotationAxis, other.rotationAxis) && Objects.equals(this.rotationAngle, other.rotationAngle) && Objects.equals(this.mirrorAxis, other.mirrorAxis) && Objects.equals(this.material, other.material) && Arrays.equals(this.favoriteMaterials, other.favoriteMaterials) && Objects.equals(this.mask, other.mask) && Objects.equals(this.maskAbove, other.maskAbove) && Objects.equals(this.maskNot, other.maskNot) && Objects.equals(this.maskBelow, other.maskBelow) && Objects.equals(this.maskAdjacent, other.maskAdjacent) && Objects.equals(this.maskNeighbor, other.maskNeighbor) && Arrays.equals(this.maskCommands, other.maskCommands) && Objects.equals(this.useMaskCommands, other.useMaskCommands) && Objects.equals(this.invertMask, other.invertMask);
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + Objects.hashCode(this.width);
        result = 31 * result + Objects.hashCode(this.height);
        result = 31 * result + Objects.hashCode(this.thickness);
        result = 31 * result + Objects.hashCode(this.capped);
        result = 31 * result + Objects.hashCode(this.shape);
        result = 31 * result + Objects.hashCode(this.origin);
        result = 31 * result + Objects.hashCode(this.originRotation);
        result = 31 * result + Objects.hashCode(this.rotationAxis);
        result = 31 * result + Objects.hashCode(this.rotationAngle);
        result = 31 * result + Objects.hashCode(this.mirrorAxis);
        result = 31 * result + Objects.hashCode(this.material);
        result = 31 * result + Arrays.hashCode(this.favoriteMaterials);
        result = 31 * result + Objects.hashCode(this.mask);
        result = 31 * result + Objects.hashCode(this.maskAbove);
        result = 31 * result + Objects.hashCode(this.maskNot);
        result = 31 * result + Objects.hashCode(this.maskBelow);
        result = 31 * result + Objects.hashCode(this.maskAdjacent);
        result = 31 * result + Objects.hashCode(this.maskNeighbor);
        result = 31 * result + Arrays.hashCode(this.maskCommands);
        result = 31 * result + Objects.hashCode(this.useMaskCommands);
        result = 31 * result + Objects.hashCode(this.invertMask);
        return result;
    }
}

