/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.FloatRange;
import com.hypixel.hytale.protocol.ModelParticle;
import com.hypixel.hytale.protocol.ValueType;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemAppearanceCondition {
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 18;
    public static final int VARIABLE_FIELD_COUNT = 5;
    public static final int VARIABLE_BLOCK_START = 38;
    public static final int MAX_SIZE = 0x64000000;
    @Nullable
    public ModelParticle[] particles;
    @Nullable
    public ModelParticle[] firstPersonParticles;
    @Nullable
    public String model;
    @Nullable
    public String texture;
    @Nullable
    public String modelVFXId;
    @Nullable
    public FloatRange condition;
    @Nonnull
    public ValueType conditionValueType = ValueType.Percent;
    public int localSoundEventId;
    public int worldSoundEventId;

    public ItemAppearanceCondition() {
    }

    public ItemAppearanceCondition(@Nullable ModelParticle[] particles, @Nullable ModelParticle[] firstPersonParticles, @Nullable String model, @Nullable String texture, @Nullable String modelVFXId, @Nullable FloatRange condition, @Nonnull ValueType conditionValueType, int localSoundEventId, int worldSoundEventId) {
        this.particles = particles;
        this.firstPersonParticles = firstPersonParticles;
        this.model = model;
        this.texture = texture;
        this.modelVFXId = modelVFXId;
        this.condition = condition;
        this.conditionValueType = conditionValueType;
        this.localSoundEventId = localSoundEventId;
        this.worldSoundEventId = worldSoundEventId;
    }

    public ItemAppearanceCondition(@Nonnull ItemAppearanceCondition other) {
        this.particles = other.particles;
        this.firstPersonParticles = other.firstPersonParticles;
        this.model = other.model;
        this.texture = other.texture;
        this.modelVFXId = other.modelVFXId;
        this.condition = other.condition;
        this.conditionValueType = other.conditionValueType;
        this.localSoundEventId = other.localSoundEventId;
        this.worldSoundEventId = other.worldSoundEventId;
    }

    @Nonnull
    public static ItemAppearanceCondition deserialize(@Nonnull ByteBuf buf, int offset) {
        int i;
        int elemPos;
        int varIntLen;
        ItemAppearanceCondition obj = new ItemAppearanceCondition();
        byte nullBits = buf.getByte(offset);
        if ((nullBits & 1) != 0) {
            obj.condition = FloatRange.deserialize(buf, offset + 1);
        }
        obj.conditionValueType = ValueType.fromValue(buf.getByte(offset + 9));
        obj.localSoundEventId = buf.getIntLE(offset + 10);
        obj.worldSoundEventId = buf.getIntLE(offset + 14);
        if ((nullBits & 2) != 0) {
            int varPos0 = offset + 38 + buf.getIntLE(offset + 18);
            int particlesCount = VarInt.peek(buf, varPos0);
            if (particlesCount < 0) {
                throw ProtocolException.negativeLength("Particles", particlesCount);
            }
            if (particlesCount > 4096000) {
                throw ProtocolException.arrayTooLong("Particles", particlesCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos0);
            if ((long)(varPos0 + varIntLen) + (long)particlesCount * 34L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("Particles", varPos0 + varIntLen + particlesCount * 34, buf.readableBytes());
            }
            obj.particles = new ModelParticle[particlesCount];
            elemPos = varPos0 + varIntLen;
            for (i = 0; i < particlesCount; ++i) {
                obj.particles[i] = ModelParticle.deserialize(buf, elemPos);
                elemPos += ModelParticle.computeBytesConsumed(buf, elemPos);
            }
        }
        if ((nullBits & 4) != 0) {
            int varPos1 = offset + 38 + buf.getIntLE(offset + 22);
            int firstPersonParticlesCount = VarInt.peek(buf, varPos1);
            if (firstPersonParticlesCount < 0) {
                throw ProtocolException.negativeLength("FirstPersonParticles", firstPersonParticlesCount);
            }
            if (firstPersonParticlesCount > 4096000) {
                throw ProtocolException.arrayTooLong("FirstPersonParticles", firstPersonParticlesCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos1);
            if ((long)(varPos1 + varIntLen) + (long)firstPersonParticlesCount * 34L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("FirstPersonParticles", varPos1 + varIntLen + firstPersonParticlesCount * 34, buf.readableBytes());
            }
            obj.firstPersonParticles = new ModelParticle[firstPersonParticlesCount];
            elemPos = varPos1 + varIntLen;
            for (i = 0; i < firstPersonParticlesCount; ++i) {
                obj.firstPersonParticles[i] = ModelParticle.deserialize(buf, elemPos);
                elemPos += ModelParticle.computeBytesConsumed(buf, elemPos);
            }
        }
        if ((nullBits & 8) != 0) {
            int varPos2 = offset + 38 + buf.getIntLE(offset + 26);
            int modelLen = VarInt.peek(buf, varPos2);
            if (modelLen < 0) {
                throw ProtocolException.negativeLength("Model", modelLen);
            }
            if (modelLen > 4096000) {
                throw ProtocolException.stringTooLong("Model", modelLen, 4096000);
            }
            obj.model = PacketIO.readVarString(buf, varPos2, PacketIO.UTF8);
        }
        if ((nullBits & 0x10) != 0) {
            int varPos3 = offset + 38 + buf.getIntLE(offset + 30);
            int textureLen = VarInt.peek(buf, varPos3);
            if (textureLen < 0) {
                throw ProtocolException.negativeLength("Texture", textureLen);
            }
            if (textureLen > 4096000) {
                throw ProtocolException.stringTooLong("Texture", textureLen, 4096000);
            }
            obj.texture = PacketIO.readVarString(buf, varPos3, PacketIO.UTF8);
        }
        if ((nullBits & 0x20) != 0) {
            int varPos4 = offset + 38 + buf.getIntLE(offset + 34);
            int modelVFXIdLen = VarInt.peek(buf, varPos4);
            if (modelVFXIdLen < 0) {
                throw ProtocolException.negativeLength("ModelVFXId", modelVFXIdLen);
            }
            if (modelVFXIdLen > 4096000) {
                throw ProtocolException.stringTooLong("ModelVFXId", modelVFXIdLen, 4096000);
            }
            obj.modelVFXId = PacketIO.readVarString(buf, varPos4, PacketIO.UTF8);
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int sl;
        int i;
        int arrLen;
        byte nullBits = buf.getByte(offset);
        int maxEnd = 38;
        if ((nullBits & 2) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 18);
            int pos0 = offset + 38 + fieldOffset0;
            arrLen = VarInt.peek(buf, pos0);
            pos0 += VarInt.length(buf, pos0);
            for (i = 0; i < arrLen; ++i) {
                pos0 += ModelParticle.computeBytesConsumed(buf, pos0);
            }
            if (pos0 - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 4) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 22);
            int pos1 = offset + 38 + fieldOffset1;
            arrLen = VarInt.peek(buf, pos1);
            pos1 += VarInt.length(buf, pos1);
            for (i = 0; i < arrLen; ++i) {
                pos1 += ModelParticle.computeBytesConsumed(buf, pos1);
            }
            if (pos1 - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        if ((nullBits & 8) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 26);
            int pos2 = offset + 38 + fieldOffset2;
            sl = VarInt.peek(buf, pos2);
            if ((pos2 += VarInt.length(buf, pos2) + sl) - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        if ((nullBits & 0x10) != 0) {
            int fieldOffset3 = buf.getIntLE(offset + 30);
            int pos3 = offset + 38 + fieldOffset3;
            sl = VarInt.peek(buf, pos3);
            if ((pos3 += VarInt.length(buf, pos3) + sl) - offset > maxEnd) {
                maxEnd = pos3 - offset;
            }
        }
        if ((nullBits & 0x20) != 0) {
            int fieldOffset4 = buf.getIntLE(offset + 34);
            int pos4 = offset + 38 + fieldOffset4;
            sl = VarInt.peek(buf, pos4);
            if ((pos4 += VarInt.length(buf, pos4) + sl) - offset > maxEnd) {
                maxEnd = pos4 - offset;
            }
        }
        return maxEnd;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        byte nullBits = 0;
        if (this.condition != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.particles != null) {
            nullBits = (byte)(nullBits | 2);
        }
        if (this.firstPersonParticles != null) {
            nullBits = (byte)(nullBits | 4);
        }
        if (this.model != null) {
            nullBits = (byte)(nullBits | 8);
        }
        if (this.texture != null) {
            nullBits = (byte)(nullBits | 0x10);
        }
        if (this.modelVFXId != null) {
            nullBits = (byte)(nullBits | 0x20);
        }
        buf.writeByte(nullBits);
        if (this.condition != null) {
            this.condition.serialize(buf);
        } else {
            buf.writeZero(8);
        }
        buf.writeByte(this.conditionValueType.getValue());
        buf.writeIntLE(this.localSoundEventId);
        buf.writeIntLE(this.worldSoundEventId);
        int particlesOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int firstPersonParticlesOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int modelOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int textureOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int modelVFXIdOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.particles != null) {
            buf.setIntLE(particlesOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.particles.length > 4096000) {
                throw ProtocolException.arrayTooLong("Particles", this.particles.length, 4096000);
            }
            VarInt.write(buf, this.particles.length);
            for (ModelParticle item : this.particles) {
                item.serialize(buf);
            }
        } else {
            buf.setIntLE(particlesOffsetSlot, -1);
        }
        if (this.firstPersonParticles != null) {
            buf.setIntLE(firstPersonParticlesOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.firstPersonParticles.length > 4096000) {
                throw ProtocolException.arrayTooLong("FirstPersonParticles", this.firstPersonParticles.length, 4096000);
            }
            VarInt.write(buf, this.firstPersonParticles.length);
            for (ModelParticle item : this.firstPersonParticles) {
                item.serialize(buf);
            }
        } else {
            buf.setIntLE(firstPersonParticlesOffsetSlot, -1);
        }
        if (this.model != null) {
            buf.setIntLE(modelOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.model, 4096000);
        } else {
            buf.setIntLE(modelOffsetSlot, -1);
        }
        if (this.texture != null) {
            buf.setIntLE(textureOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.texture, 4096000);
        } else {
            buf.setIntLE(textureOffsetSlot, -1);
        }
        if (this.modelVFXId != null) {
            buf.setIntLE(modelVFXIdOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.modelVFXId, 4096000);
        } else {
            buf.setIntLE(modelVFXIdOffsetSlot, -1);
        }
    }

    public int computeSize() {
        int size = 38;
        if (this.particles != null) {
            int particlesSize = 0;
            for (ModelParticle elem : this.particles) {
                particlesSize += elem.computeSize();
            }
            size += VarInt.size(this.particles.length) + particlesSize;
        }
        if (this.firstPersonParticles != null) {
            int firstPersonParticlesSize = 0;
            for (ModelParticle elem : this.firstPersonParticles) {
                firstPersonParticlesSize += elem.computeSize();
            }
            size += VarInt.size(this.firstPersonParticles.length) + firstPersonParticlesSize;
        }
        if (this.model != null) {
            size += PacketIO.stringSize(this.model);
        }
        if (this.texture != null) {
            size += PacketIO.stringSize(this.texture);
        }
        if (this.modelVFXId != null) {
            size += PacketIO.stringSize(this.modelVFXId);
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        ValidationResult structResult;
        int i;
        int pos;
        if (buffer.readableBytes() - offset < 38) {
            return ValidationResult.error("Buffer too small: expected at least 38 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 2) != 0) {
            int particlesOffset = buffer.getIntLE(offset + 18);
            if (particlesOffset < 0) {
                return ValidationResult.error("Invalid offset for Particles");
            }
            pos = offset + 38 + particlesOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Particles");
            }
            int particlesCount = VarInt.peek(buffer, pos);
            if (particlesCount < 0) {
                return ValidationResult.error("Invalid array count for Particles");
            }
            if (particlesCount > 4096000) {
                return ValidationResult.error("Particles exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < particlesCount; ++i) {
                structResult = ModelParticle.validateStructure(buffer, pos);
                if (!structResult.isValid()) {
                    return ValidationResult.error("Invalid ModelParticle in Particles[" + i + "]: " + structResult.error());
                }
                pos += ModelParticle.computeBytesConsumed(buffer, pos);
            }
        }
        if ((nullBits & 4) != 0) {
            int firstPersonParticlesOffset = buffer.getIntLE(offset + 22);
            if (firstPersonParticlesOffset < 0) {
                return ValidationResult.error("Invalid offset for FirstPersonParticles");
            }
            pos = offset + 38 + firstPersonParticlesOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for FirstPersonParticles");
            }
            int firstPersonParticlesCount = VarInt.peek(buffer, pos);
            if (firstPersonParticlesCount < 0) {
                return ValidationResult.error("Invalid array count for FirstPersonParticles");
            }
            if (firstPersonParticlesCount > 4096000) {
                return ValidationResult.error("FirstPersonParticles exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < firstPersonParticlesCount; ++i) {
                structResult = ModelParticle.validateStructure(buffer, pos);
                if (!structResult.isValid()) {
                    return ValidationResult.error("Invalid ModelParticle in FirstPersonParticles[" + i + "]: " + structResult.error());
                }
                pos += ModelParticle.computeBytesConsumed(buffer, pos);
            }
        }
        if ((nullBits & 8) != 0) {
            int modelOffset = buffer.getIntLE(offset + 26);
            if (modelOffset < 0) {
                return ValidationResult.error("Invalid offset for Model");
            }
            pos = offset + 38 + modelOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Model");
            }
            int modelLen = VarInt.peek(buffer, pos);
            if (modelLen < 0) {
                return ValidationResult.error("Invalid string length for Model");
            }
            if (modelLen > 4096000) {
                return ValidationResult.error("Model exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += modelLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading Model");
            }
        }
        if ((nullBits & 0x10) != 0) {
            int textureOffset = buffer.getIntLE(offset + 30);
            if (textureOffset < 0) {
                return ValidationResult.error("Invalid offset for Texture");
            }
            pos = offset + 38 + textureOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Texture");
            }
            int textureLen = VarInt.peek(buffer, pos);
            if (textureLen < 0) {
                return ValidationResult.error("Invalid string length for Texture");
            }
            if (textureLen > 4096000) {
                return ValidationResult.error("Texture exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += textureLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading Texture");
            }
        }
        if ((nullBits & 0x20) != 0) {
            int modelVFXIdOffset = buffer.getIntLE(offset + 34);
            if (modelVFXIdOffset < 0) {
                return ValidationResult.error("Invalid offset for ModelVFXId");
            }
            pos = offset + 38 + modelVFXIdOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for ModelVFXId");
            }
            int modelVFXIdLen = VarInt.peek(buffer, pos);
            if (modelVFXIdLen < 0) {
                return ValidationResult.error("Invalid string length for ModelVFXId");
            }
            if (modelVFXIdLen > 4096000) {
                return ValidationResult.error("ModelVFXId exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += modelVFXIdLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading ModelVFXId");
            }
        }
        return ValidationResult.OK;
    }

    public ItemAppearanceCondition clone() {
        ItemAppearanceCondition copy = new ItemAppearanceCondition();
        copy.particles = this.particles != null ? (ModelParticle[])Arrays.stream(this.particles).map(e -> e.clone()).toArray(ModelParticle[]::new) : null;
        copy.firstPersonParticles = this.firstPersonParticles != null ? (ModelParticle[])Arrays.stream(this.firstPersonParticles).map(e -> e.clone()).toArray(ModelParticle[]::new) : null;
        copy.model = this.model;
        copy.texture = this.texture;
        copy.modelVFXId = this.modelVFXId;
        copy.condition = this.condition != null ? this.condition.clone() : null;
        copy.conditionValueType = this.conditionValueType;
        copy.localSoundEventId = this.localSoundEventId;
        copy.worldSoundEventId = this.worldSoundEventId;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ItemAppearanceCondition)) {
            return false;
        }
        ItemAppearanceCondition other = (ItemAppearanceCondition)obj;
        return Arrays.equals(this.particles, other.particles) && Arrays.equals(this.firstPersonParticles, other.firstPersonParticles) && Objects.equals(this.model, other.model) && Objects.equals(this.texture, other.texture) && Objects.equals(this.modelVFXId, other.modelVFXId) && Objects.equals(this.condition, other.condition) && Objects.equals((Object)this.conditionValueType, (Object)other.conditionValueType) && this.localSoundEventId == other.localSoundEventId && this.worldSoundEventId == other.worldSoundEventId;
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + Arrays.hashCode(this.particles);
        result = 31 * result + Arrays.hashCode(this.firstPersonParticles);
        result = 31 * result + Objects.hashCode(this.model);
        result = 31 * result + Objects.hashCode(this.texture);
        result = 31 * result + Objects.hashCode(this.modelVFXId);
        result = 31 * result + Objects.hashCode(this.condition);
        result = 31 * result + Objects.hashCode((Object)this.conditionValueType);
        result = 31 * result + Integer.hashCode(this.localSoundEventId);
        result = 31 * result + Integer.hashCode(this.worldSoundEventId);
        return result;
    }
}

