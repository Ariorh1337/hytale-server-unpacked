/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.BenchRequirement;
import com.hypixel.hytale.protocol.MaterialQuantity;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CraftingRecipe {
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 10;
    public static final int VARIABLE_FIELD_COUNT = 5;
    public static final int VARIABLE_BLOCK_START = 30;
    public static final int MAX_SIZE = 0x64000000;
    @Nullable
    public String id;
    @Nullable
    public MaterialQuantity[] inputs;
    @Nullable
    public MaterialQuantity[] outputs;
    @Nullable
    public MaterialQuantity primaryOutput;
    @Nullable
    public BenchRequirement[] benchRequirement;
    public boolean knowledgeRequired;
    public float timeSeconds;
    public int requiredMemoriesLevel;

    public CraftingRecipe() {
    }

    public CraftingRecipe(@Nullable String id, @Nullable MaterialQuantity[] inputs, @Nullable MaterialQuantity[] outputs, @Nullable MaterialQuantity primaryOutput, @Nullable BenchRequirement[] benchRequirement, boolean knowledgeRequired, float timeSeconds, int requiredMemoriesLevel) {
        this.id = id;
        this.inputs = inputs;
        this.outputs = outputs;
        this.primaryOutput = primaryOutput;
        this.benchRequirement = benchRequirement;
        this.knowledgeRequired = knowledgeRequired;
        this.timeSeconds = timeSeconds;
        this.requiredMemoriesLevel = requiredMemoriesLevel;
    }

    public CraftingRecipe(@Nonnull CraftingRecipe other) {
        this.id = other.id;
        this.inputs = other.inputs;
        this.outputs = other.outputs;
        this.primaryOutput = other.primaryOutput;
        this.benchRequirement = other.benchRequirement;
        this.knowledgeRequired = other.knowledgeRequired;
        this.timeSeconds = other.timeSeconds;
        this.requiredMemoriesLevel = other.requiredMemoriesLevel;
    }

    @Nonnull
    public static CraftingRecipe deserialize(@Nonnull ByteBuf buf, int offset) {
        int i;
        int elemPos;
        int varIntLen;
        CraftingRecipe obj = new CraftingRecipe();
        byte nullBits = buf.getByte(offset);
        obj.knowledgeRequired = buf.getByte(offset + 1) != 0;
        obj.timeSeconds = buf.getFloatLE(offset + 2);
        obj.requiredMemoriesLevel = buf.getIntLE(offset + 6);
        if ((nullBits & 1) != 0) {
            int varPos0 = offset + 30 + buf.getIntLE(offset + 10);
            int idLen = VarInt.peek(buf, varPos0);
            if (idLen < 0) {
                throw ProtocolException.negativeLength("Id", idLen);
            }
            if (idLen > 4096000) {
                throw ProtocolException.stringTooLong("Id", idLen, 4096000);
            }
            obj.id = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
        }
        if ((nullBits & 2) != 0) {
            int varPos1 = offset + 30 + buf.getIntLE(offset + 14);
            int inputsCount = VarInt.peek(buf, varPos1);
            if (inputsCount < 0) {
                throw ProtocolException.negativeLength("Inputs", inputsCount);
            }
            if (inputsCount > 4096000) {
                throw ProtocolException.arrayTooLong("Inputs", inputsCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos1);
            if ((long)(varPos1 + varIntLen) + (long)inputsCount * 9L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("Inputs", varPos1 + varIntLen + inputsCount * 9, buf.readableBytes());
            }
            obj.inputs = new MaterialQuantity[inputsCount];
            elemPos = varPos1 + varIntLen;
            for (i = 0; i < inputsCount; ++i) {
                obj.inputs[i] = MaterialQuantity.deserialize(buf, elemPos);
                elemPos += MaterialQuantity.computeBytesConsumed(buf, elemPos);
            }
        }
        if ((nullBits & 4) != 0) {
            int varPos2 = offset + 30 + buf.getIntLE(offset + 18);
            int outputsCount = VarInt.peek(buf, varPos2);
            if (outputsCount < 0) {
                throw ProtocolException.negativeLength("Outputs", outputsCount);
            }
            if (outputsCount > 4096000) {
                throw ProtocolException.arrayTooLong("Outputs", outputsCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos2);
            if ((long)(varPos2 + varIntLen) + (long)outputsCount * 9L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("Outputs", varPos2 + varIntLen + outputsCount * 9, buf.readableBytes());
            }
            obj.outputs = new MaterialQuantity[outputsCount];
            elemPos = varPos2 + varIntLen;
            for (i = 0; i < outputsCount; ++i) {
                obj.outputs[i] = MaterialQuantity.deserialize(buf, elemPos);
                elemPos += MaterialQuantity.computeBytesConsumed(buf, elemPos);
            }
        }
        if ((nullBits & 8) != 0) {
            int varPos3 = offset + 30 + buf.getIntLE(offset + 22);
            obj.primaryOutput = MaterialQuantity.deserialize(buf, varPos3);
        }
        if ((nullBits & 0x10) != 0) {
            int varPos4 = offset + 30 + buf.getIntLE(offset + 26);
            int benchRequirementCount = VarInt.peek(buf, varPos4);
            if (benchRequirementCount < 0) {
                throw ProtocolException.negativeLength("BenchRequirement", benchRequirementCount);
            }
            if (benchRequirementCount > 4096000) {
                throw ProtocolException.arrayTooLong("BenchRequirement", benchRequirementCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos4);
            if ((long)(varPos4 + varIntLen) + (long)benchRequirementCount * 6L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("BenchRequirement", varPos4 + varIntLen + benchRequirementCount * 6, buf.readableBytes());
            }
            obj.benchRequirement = new BenchRequirement[benchRequirementCount];
            elemPos = varPos4 + varIntLen;
            for (i = 0; i < benchRequirementCount; ++i) {
                obj.benchRequirement[i] = BenchRequirement.deserialize(buf, elemPos);
                elemPos += BenchRequirement.computeBytesConsumed(buf, elemPos);
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int i;
        int arrLen;
        byte nullBits = buf.getByte(offset);
        int maxEnd = 30;
        if ((nullBits & 1) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 10);
            int pos0 = offset + 30 + fieldOffset0;
            int sl = VarInt.peek(buf, pos0);
            if ((pos0 += VarInt.length(buf, pos0) + sl) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 2) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 14);
            int pos1 = offset + 30 + fieldOffset1;
            arrLen = VarInt.peek(buf, pos1);
            pos1 += VarInt.length(buf, pos1);
            for (i = 0; i < arrLen; ++i) {
                pos1 += MaterialQuantity.computeBytesConsumed(buf, pos1);
            }
            if (pos1 - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        if ((nullBits & 4) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 18);
            int pos2 = offset + 30 + fieldOffset2;
            arrLen = VarInt.peek(buf, pos2);
            pos2 += VarInt.length(buf, pos2);
            for (i = 0; i < arrLen; ++i) {
                pos2 += MaterialQuantity.computeBytesConsumed(buf, pos2);
            }
            if (pos2 - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        if ((nullBits & 8) != 0) {
            int fieldOffset3 = buf.getIntLE(offset + 22);
            int pos3 = offset + 30 + fieldOffset3;
            if ((pos3 += MaterialQuantity.computeBytesConsumed(buf, pos3)) - offset > maxEnd) {
                maxEnd = pos3 - offset;
            }
        }
        if ((nullBits & 0x10) != 0) {
            int fieldOffset4 = buf.getIntLE(offset + 26);
            int pos4 = offset + 30 + fieldOffset4;
            arrLen = VarInt.peek(buf, pos4);
            pos4 += VarInt.length(buf, pos4);
            for (i = 0; i < arrLen; ++i) {
                pos4 += BenchRequirement.computeBytesConsumed(buf, pos4);
            }
            if (pos4 - offset > maxEnd) {
                maxEnd = pos4 - offset;
            }
        }
        return maxEnd;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        byte nullBits = 0;
        if (this.id != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.inputs != null) {
            nullBits = (byte)(nullBits | 2);
        }
        if (this.outputs != null) {
            nullBits = (byte)(nullBits | 4);
        }
        if (this.primaryOutput != null) {
            nullBits = (byte)(nullBits | 8);
        }
        if (this.benchRequirement != null) {
            nullBits = (byte)(nullBits | 0x10);
        }
        buf.writeByte(nullBits);
        buf.writeByte(this.knowledgeRequired ? 1 : 0);
        buf.writeFloatLE(this.timeSeconds);
        buf.writeIntLE(this.requiredMemoriesLevel);
        int idOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int inputsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int outputsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int primaryOutputOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int benchRequirementOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.id != null) {
            buf.setIntLE(idOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.id, 4096000);
        } else {
            buf.setIntLE(idOffsetSlot, -1);
        }
        if (this.inputs != null) {
            buf.setIntLE(inputsOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.inputs.length > 4096000) {
                throw ProtocolException.arrayTooLong("Inputs", this.inputs.length, 4096000);
            }
            VarInt.write(buf, this.inputs.length);
            for (MaterialQuantity materialQuantity : this.inputs) {
                materialQuantity.serialize(buf);
            }
        } else {
            buf.setIntLE(inputsOffsetSlot, -1);
        }
        if (this.outputs != null) {
            buf.setIntLE(outputsOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.outputs.length > 4096000) {
                throw ProtocolException.arrayTooLong("Outputs", this.outputs.length, 4096000);
            }
            VarInt.write(buf, this.outputs.length);
            for (MaterialQuantity materialQuantity : this.outputs) {
                materialQuantity.serialize(buf);
            }
        } else {
            buf.setIntLE(outputsOffsetSlot, -1);
        }
        if (this.primaryOutput != null) {
            buf.setIntLE(primaryOutputOffsetSlot, buf.writerIndex() - varBlockStart);
            this.primaryOutput.serialize(buf);
        } else {
            buf.setIntLE(primaryOutputOffsetSlot, -1);
        }
        if (this.benchRequirement != null) {
            buf.setIntLE(benchRequirementOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.benchRequirement.length > 4096000) {
                throw ProtocolException.arrayTooLong("BenchRequirement", this.benchRequirement.length, 4096000);
            }
            VarInt.write(buf, this.benchRequirement.length);
            for (BenchRequirement benchRequirement : this.benchRequirement) {
                benchRequirement.serialize(buf);
            }
        } else {
            buf.setIntLE(benchRequirementOffsetSlot, -1);
        }
    }

    public int computeSize() {
        int size = 30;
        if (this.id != null) {
            size += PacketIO.stringSize(this.id);
        }
        if (this.inputs != null) {
            int inputsSize = 0;
            for (MaterialQuantity materialQuantity : this.inputs) {
                inputsSize += materialQuantity.computeSize();
            }
            size += VarInt.size(this.inputs.length) + inputsSize;
        }
        if (this.outputs != null) {
            int outputsSize = 0;
            for (MaterialQuantity materialQuantity : this.outputs) {
                outputsSize += materialQuantity.computeSize();
            }
            size += VarInt.size(this.outputs.length) + outputsSize;
        }
        if (this.primaryOutput != null) {
            size += this.primaryOutput.computeSize();
        }
        if (this.benchRequirement != null) {
            int benchRequirementSize = 0;
            for (BenchRequirement benchRequirement : this.benchRequirement) {
                benchRequirementSize += benchRequirement.computeSize();
            }
            size += VarInt.size(this.benchRequirement.length) + benchRequirementSize;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        ValidationResult structResult;
        int i;
        int pos;
        if (buffer.readableBytes() - offset < 30) {
            return ValidationResult.error("Buffer too small: expected at least 30 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 1) != 0) {
            int idOffset = buffer.getIntLE(offset + 10);
            if (idOffset < 0) {
                return ValidationResult.error("Invalid offset for Id");
            }
            pos = offset + 30 + idOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Id");
            }
            int idLen = VarInt.peek(buffer, pos);
            if (idLen < 0) {
                return ValidationResult.error("Invalid string length for Id");
            }
            if (idLen > 4096000) {
                return ValidationResult.error("Id exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += idLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading Id");
            }
        }
        if ((nullBits & 2) != 0) {
            int inputsOffset = buffer.getIntLE(offset + 14);
            if (inputsOffset < 0) {
                return ValidationResult.error("Invalid offset for Inputs");
            }
            pos = offset + 30 + inputsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Inputs");
            }
            int inputsCount = VarInt.peek(buffer, pos);
            if (inputsCount < 0) {
                return ValidationResult.error("Invalid array count for Inputs");
            }
            if (inputsCount > 4096000) {
                return ValidationResult.error("Inputs exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < inputsCount; ++i) {
                structResult = MaterialQuantity.validateStructure(buffer, pos);
                if (!structResult.isValid()) {
                    return ValidationResult.error("Invalid MaterialQuantity in Inputs[" + i + "]: " + structResult.error());
                }
                pos += MaterialQuantity.computeBytesConsumed(buffer, pos);
            }
        }
        if ((nullBits & 4) != 0) {
            int outputsOffset = buffer.getIntLE(offset + 18);
            if (outputsOffset < 0) {
                return ValidationResult.error("Invalid offset for Outputs");
            }
            pos = offset + 30 + outputsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Outputs");
            }
            int outputsCount = VarInt.peek(buffer, pos);
            if (outputsCount < 0) {
                return ValidationResult.error("Invalid array count for Outputs");
            }
            if (outputsCount > 4096000) {
                return ValidationResult.error("Outputs exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < outputsCount; ++i) {
                structResult = MaterialQuantity.validateStructure(buffer, pos);
                if (!structResult.isValid()) {
                    return ValidationResult.error("Invalid MaterialQuantity in Outputs[" + i + "]: " + structResult.error());
                }
                pos += MaterialQuantity.computeBytesConsumed(buffer, pos);
            }
        }
        if ((nullBits & 8) != 0) {
            int primaryOutputOffset = buffer.getIntLE(offset + 22);
            if (primaryOutputOffset < 0) {
                return ValidationResult.error("Invalid offset for PrimaryOutput");
            }
            pos = offset + 30 + primaryOutputOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for PrimaryOutput");
            }
            ValidationResult primaryOutputResult = MaterialQuantity.validateStructure(buffer, pos);
            if (!primaryOutputResult.isValid()) {
                return ValidationResult.error("Invalid PrimaryOutput: " + primaryOutputResult.error());
            }
            pos += MaterialQuantity.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits & 0x10) != 0) {
            int benchRequirementOffset = buffer.getIntLE(offset + 26);
            if (benchRequirementOffset < 0) {
                return ValidationResult.error("Invalid offset for BenchRequirement");
            }
            pos = offset + 30 + benchRequirementOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for BenchRequirement");
            }
            int benchRequirementCount = VarInt.peek(buffer, pos);
            if (benchRequirementCount < 0) {
                return ValidationResult.error("Invalid array count for BenchRequirement");
            }
            if (benchRequirementCount > 4096000) {
                return ValidationResult.error("BenchRequirement exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < benchRequirementCount; ++i) {
                structResult = BenchRequirement.validateStructure(buffer, pos);
                if (!structResult.isValid()) {
                    return ValidationResult.error("Invalid BenchRequirement in BenchRequirement[" + i + "]: " + structResult.error());
                }
                pos += BenchRequirement.computeBytesConsumed(buffer, pos);
            }
        }
        return ValidationResult.OK;
    }

    public CraftingRecipe clone() {
        CraftingRecipe copy = new CraftingRecipe();
        copy.id = this.id;
        copy.inputs = this.inputs != null ? (MaterialQuantity[])Arrays.stream(this.inputs).map(e -> e.clone()).toArray(MaterialQuantity[]::new) : null;
        copy.outputs = this.outputs != null ? (MaterialQuantity[])Arrays.stream(this.outputs).map(e -> e.clone()).toArray(MaterialQuantity[]::new) : null;
        copy.primaryOutput = this.primaryOutput != null ? this.primaryOutput.clone() : null;
        copy.benchRequirement = this.benchRequirement != null ? (BenchRequirement[])Arrays.stream(this.benchRequirement).map(e -> e.clone()).toArray(BenchRequirement[]::new) : null;
        copy.knowledgeRequired = this.knowledgeRequired;
        copy.timeSeconds = this.timeSeconds;
        copy.requiredMemoriesLevel = this.requiredMemoriesLevel;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CraftingRecipe)) {
            return false;
        }
        CraftingRecipe other = (CraftingRecipe)obj;
        return Objects.equals(this.id, other.id) && Arrays.equals(this.inputs, other.inputs) && Arrays.equals(this.outputs, other.outputs) && Objects.equals(this.primaryOutput, other.primaryOutput) && Arrays.equals(this.benchRequirement, other.benchRequirement) && this.knowledgeRequired == other.knowledgeRequired && this.timeSeconds == other.timeSeconds && this.requiredMemoriesLevel == other.requiredMemoriesLevel;
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + Objects.hashCode(this.id);
        result = 31 * result + Arrays.hashCode(this.inputs);
        result = 31 * result + Arrays.hashCode(this.outputs);
        result = 31 * result + Objects.hashCode(this.primaryOutput);
        result = 31 * result + Arrays.hashCode(this.benchRequirement);
        result = 31 * result + Boolean.hashCode(this.knowledgeRequired);
        result = 31 * result + Float.hashCode(this.timeSeconds);
        result = 31 * result + Integer.hashCode(this.requiredMemoriesLevel);
        return result;
    }
}

