/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.BlockTextures;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.protocol.ColorLight;
import com.hypixel.hytale.protocol.FluidDrawType;
import com.hypixel.hytale.protocol.ModelParticle;
import com.hypixel.hytale.protocol.Opacity;
import com.hypixel.hytale.protocol.ShaderType;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Fluid {
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 23;
    public static final int VARIABLE_FIELD_COUNT = 6;
    public static final int VARIABLE_BLOCK_START = 47;
    public static final int MAX_SIZE = 0x64000000;
    @Nullable
    public String id;
    public int maxFluidLevel;
    @Nullable
    public BlockTextures[] cubeTextures;
    public boolean requiresAlphaBlending;
    @Nonnull
    public Opacity opacity = Opacity.Solid;
    @Nullable
    public ShaderType[] shaderEffect;
    @Nullable
    public ColorLight light;
    @Nullable
    public ModelParticle[] particles;
    @Nonnull
    public FluidDrawType drawType = FluidDrawType.None;
    public int fluidFXIndex;
    public int blockSoundSetIndex;
    @Nullable
    public String blockParticleSetId;
    @Nullable
    public Color particleColor;
    @Nullable
    public int[] tagIndexes;

    public Fluid() {
    }

    public Fluid(@Nullable String id, int maxFluidLevel, @Nullable BlockTextures[] cubeTextures, boolean requiresAlphaBlending, @Nonnull Opacity opacity, @Nullable ShaderType[] shaderEffect, @Nullable ColorLight light, @Nullable ModelParticle[] particles, @Nonnull FluidDrawType drawType, int fluidFXIndex, int blockSoundSetIndex, @Nullable String blockParticleSetId, @Nullable Color particleColor, @Nullable int[] tagIndexes) {
        this.id = id;
        this.maxFluidLevel = maxFluidLevel;
        this.cubeTextures = cubeTextures;
        this.requiresAlphaBlending = requiresAlphaBlending;
        this.opacity = opacity;
        this.shaderEffect = shaderEffect;
        this.light = light;
        this.particles = particles;
        this.drawType = drawType;
        this.fluidFXIndex = fluidFXIndex;
        this.blockSoundSetIndex = blockSoundSetIndex;
        this.blockParticleSetId = blockParticleSetId;
        this.particleColor = particleColor;
        this.tagIndexes = tagIndexes;
    }

    public Fluid(@Nonnull Fluid other) {
        this.id = other.id;
        this.maxFluidLevel = other.maxFluidLevel;
        this.cubeTextures = other.cubeTextures;
        this.requiresAlphaBlending = other.requiresAlphaBlending;
        this.opacity = other.opacity;
        this.shaderEffect = other.shaderEffect;
        this.light = other.light;
        this.particles = other.particles;
        this.drawType = other.drawType;
        this.fluidFXIndex = other.fluidFXIndex;
        this.blockSoundSetIndex = other.blockSoundSetIndex;
        this.blockParticleSetId = other.blockParticleSetId;
        this.particleColor = other.particleColor;
        this.tagIndexes = other.tagIndexes;
    }

    @Nonnull
    public static Fluid deserialize(@Nonnull ByteBuf buf, int offset) {
        int i;
        int elemPos;
        int varIntLen;
        Fluid obj = new Fluid();
        byte nullBits = buf.getByte(offset);
        obj.maxFluidLevel = buf.getIntLE(offset + 1);
        obj.requiresAlphaBlending = buf.getByte(offset + 5) != 0;
        obj.opacity = Opacity.fromValue(buf.getByte(offset + 6));
        if ((nullBits & 1) != 0) {
            obj.light = ColorLight.deserialize(buf, offset + 7);
        }
        obj.drawType = FluidDrawType.fromValue(buf.getByte(offset + 11));
        obj.fluidFXIndex = buf.getIntLE(offset + 12);
        obj.blockSoundSetIndex = buf.getIntLE(offset + 16);
        if ((nullBits & 2) != 0) {
            obj.particleColor = Color.deserialize(buf, offset + 20);
        }
        if ((nullBits & 4) != 0) {
            int varPos0 = offset + 47 + buf.getIntLE(offset + 23);
            int idLen = VarInt.peek(buf, varPos0);
            if (idLen < 0) {
                throw ProtocolException.negativeLength("Id", idLen);
            }
            if (idLen > 4096000) {
                throw ProtocolException.stringTooLong("Id", idLen, 4096000);
            }
            obj.id = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
        }
        if ((nullBits & 8) != 0) {
            int varPos1 = offset + 47 + buf.getIntLE(offset + 27);
            int cubeTexturesCount = VarInt.peek(buf, varPos1);
            if (cubeTexturesCount < 0) {
                throw ProtocolException.negativeLength("CubeTextures", cubeTexturesCount);
            }
            if (cubeTexturesCount > 4096000) {
                throw ProtocolException.arrayTooLong("CubeTextures", cubeTexturesCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos1);
            if ((long)(varPos1 + varIntLen) + (long)cubeTexturesCount * 5L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("CubeTextures", varPos1 + varIntLen + cubeTexturesCount * 5, buf.readableBytes());
            }
            obj.cubeTextures = new BlockTextures[cubeTexturesCount];
            elemPos = varPos1 + varIntLen;
            for (i = 0; i < cubeTexturesCount; ++i) {
                obj.cubeTextures[i] = BlockTextures.deserialize(buf, elemPos);
                elemPos += BlockTextures.computeBytesConsumed(buf, elemPos);
            }
        }
        if ((nullBits & 0x10) != 0) {
            int varPos2 = offset + 47 + buf.getIntLE(offset + 31);
            int shaderEffectCount = VarInt.peek(buf, varPos2);
            if (shaderEffectCount < 0) {
                throw ProtocolException.negativeLength("ShaderEffect", shaderEffectCount);
            }
            if (shaderEffectCount > 4096000) {
                throw ProtocolException.arrayTooLong("ShaderEffect", shaderEffectCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos2);
            if ((long)(varPos2 + varIntLen) + (long)shaderEffectCount * 1L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("ShaderEffect", varPos2 + varIntLen + shaderEffectCount * 1, buf.readableBytes());
            }
            obj.shaderEffect = new ShaderType[shaderEffectCount];
            elemPos = varPos2 + varIntLen;
            for (i = 0; i < shaderEffectCount; ++i) {
                obj.shaderEffect[i] = ShaderType.fromValue(buf.getByte(elemPos));
                ++elemPos;
            }
        }
        if ((nullBits & 0x20) != 0) {
            int varPos3 = offset + 47 + buf.getIntLE(offset + 35);
            int particlesCount = VarInt.peek(buf, varPos3);
            if (particlesCount < 0) {
                throw ProtocolException.negativeLength("Particles", particlesCount);
            }
            if (particlesCount > 4096000) {
                throw ProtocolException.arrayTooLong("Particles", particlesCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos3);
            if ((long)(varPos3 + varIntLen) + (long)particlesCount * 34L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("Particles", varPos3 + varIntLen + particlesCount * 34, buf.readableBytes());
            }
            obj.particles = new ModelParticle[particlesCount];
            elemPos = varPos3 + varIntLen;
            for (i = 0; i < particlesCount; ++i) {
                obj.particles[i] = ModelParticle.deserialize(buf, elemPos);
                elemPos += ModelParticle.computeBytesConsumed(buf, elemPos);
            }
        }
        if ((nullBits & 0x40) != 0) {
            int varPos4 = offset + 47 + buf.getIntLE(offset + 39);
            int blockParticleSetIdLen = VarInt.peek(buf, varPos4);
            if (blockParticleSetIdLen < 0) {
                throw ProtocolException.negativeLength("BlockParticleSetId", blockParticleSetIdLen);
            }
            if (blockParticleSetIdLen > 4096000) {
                throw ProtocolException.stringTooLong("BlockParticleSetId", blockParticleSetIdLen, 4096000);
            }
            obj.blockParticleSetId = PacketIO.readVarString(buf, varPos4, PacketIO.UTF8);
        }
        if ((nullBits & 0x80) != 0) {
            int varPos5 = offset + 47 + buf.getIntLE(offset + 43);
            int tagIndexesCount = VarInt.peek(buf, varPos5);
            if (tagIndexesCount < 0) {
                throw ProtocolException.negativeLength("TagIndexes", tagIndexesCount);
            }
            if (tagIndexesCount > 4096000) {
                throw ProtocolException.arrayTooLong("TagIndexes", tagIndexesCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos5);
            if ((long)(varPos5 + varIntLen) + (long)tagIndexesCount * 4L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("TagIndexes", varPos5 + varIntLen + tagIndexesCount * 4, buf.readableBytes());
            }
            obj.tagIndexes = new int[tagIndexesCount];
            for (int i2 = 0; i2 < tagIndexesCount; ++i2) {
                obj.tagIndexes[i2] = buf.getIntLE(varPos5 + varIntLen + i2 * 4);
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int i;
        int arrLen;
        int sl;
        byte nullBits = buf.getByte(offset);
        int maxEnd = 47;
        if ((nullBits & 4) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 23);
            int pos0 = offset + 47 + fieldOffset0;
            sl = VarInt.peek(buf, pos0);
            if ((pos0 += VarInt.length(buf, pos0) + sl) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 8) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 27);
            int pos1 = offset + 47 + fieldOffset1;
            arrLen = VarInt.peek(buf, pos1);
            pos1 += VarInt.length(buf, pos1);
            for (i = 0; i < arrLen; ++i) {
                pos1 += BlockTextures.computeBytesConsumed(buf, pos1);
            }
            if (pos1 - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        if ((nullBits & 0x10) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 31);
            int pos2 = offset + 47 + fieldOffset2;
            arrLen = VarInt.peek(buf, pos2);
            if ((pos2 += VarInt.length(buf, pos2) + arrLen * 1) - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        if ((nullBits & 0x20) != 0) {
            int fieldOffset3 = buf.getIntLE(offset + 35);
            int pos3 = offset + 47 + fieldOffset3;
            arrLen = VarInt.peek(buf, pos3);
            pos3 += VarInt.length(buf, pos3);
            for (i = 0; i < arrLen; ++i) {
                pos3 += ModelParticle.computeBytesConsumed(buf, pos3);
            }
            if (pos3 - offset > maxEnd) {
                maxEnd = pos3 - offset;
            }
        }
        if ((nullBits & 0x40) != 0) {
            int fieldOffset4 = buf.getIntLE(offset + 39);
            int pos4 = offset + 47 + fieldOffset4;
            sl = VarInt.peek(buf, pos4);
            if ((pos4 += VarInt.length(buf, pos4) + sl) - offset > maxEnd) {
                maxEnd = pos4 - offset;
            }
        }
        if ((nullBits & 0x80) != 0) {
            int fieldOffset5 = buf.getIntLE(offset + 43);
            int pos5 = offset + 47 + fieldOffset5;
            arrLen = VarInt.peek(buf, pos5);
            if ((pos5 += VarInt.length(buf, pos5) + arrLen * 4) - offset > maxEnd) {
                maxEnd = pos5 - offset;
            }
        }
        return maxEnd;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        byte nullBits = 0;
        if (this.light != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.particleColor != null) {
            nullBits = (byte)(nullBits | 2);
        }
        if (this.id != null) {
            nullBits = (byte)(nullBits | 4);
        }
        if (this.cubeTextures != null) {
            nullBits = (byte)(nullBits | 8);
        }
        if (this.shaderEffect != null) {
            nullBits = (byte)(nullBits | 0x10);
        }
        if (this.particles != null) {
            nullBits = (byte)(nullBits | 0x20);
        }
        if (this.blockParticleSetId != null) {
            nullBits = (byte)(nullBits | 0x40);
        }
        if (this.tagIndexes != null) {
            nullBits = (byte)(nullBits | 0x80);
        }
        buf.writeByte(nullBits);
        buf.writeIntLE(this.maxFluidLevel);
        buf.writeByte(this.requiresAlphaBlending ? 1 : 0);
        buf.writeByte(this.opacity.getValue());
        if (this.light != null) {
            this.light.serialize(buf);
        } else {
            buf.writeZero(4);
        }
        buf.writeByte(this.drawType.getValue());
        buf.writeIntLE(this.fluidFXIndex);
        buf.writeIntLE(this.blockSoundSetIndex);
        if (this.particleColor != null) {
            this.particleColor.serialize(buf);
        } else {
            buf.writeZero(3);
        }
        int idOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int cubeTexturesOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int shaderEffectOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int particlesOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int blockParticleSetIdOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int tagIndexesOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.id != null) {
            buf.setIntLE(idOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.id, 4096000);
        } else {
            buf.setIntLE(idOffsetSlot, -1);
        }
        if (this.cubeTextures != null) {
            buf.setIntLE(cubeTexturesOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.cubeTextures.length > 4096000) {
                throw ProtocolException.arrayTooLong("CubeTextures", this.cubeTextures.length, 4096000);
            }
            VarInt.write(buf, this.cubeTextures.length);
            for (BlockTextures blockTextures : this.cubeTextures) {
                blockTextures.serialize(buf);
            }
        } else {
            buf.setIntLE(cubeTexturesOffsetSlot, -1);
        }
        if (this.shaderEffect != null) {
            buf.setIntLE(shaderEffectOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.shaderEffect.length > 4096000) {
                throw ProtocolException.arrayTooLong("ShaderEffect", this.shaderEffect.length, 4096000);
            }
            VarInt.write(buf, this.shaderEffect.length);
            for (ShaderType shaderType : this.shaderEffect) {
                buf.writeByte(shaderType.getValue());
            }
        } else {
            buf.setIntLE(shaderEffectOffsetSlot, -1);
        }
        if (this.particles != null) {
            buf.setIntLE(particlesOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.particles.length > 4096000) {
                throw ProtocolException.arrayTooLong("Particles", this.particles.length, 4096000);
            }
            VarInt.write(buf, this.particles.length);
            for (ModelParticle modelParticle : this.particles) {
                modelParticle.serialize(buf);
            }
        } else {
            buf.setIntLE(particlesOffsetSlot, -1);
        }
        if (this.blockParticleSetId != null) {
            buf.setIntLE(blockParticleSetIdOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.blockParticleSetId, 4096000);
        } else {
            buf.setIntLE(blockParticleSetIdOffsetSlot, -1);
        }
        if (this.tagIndexes != null) {
            buf.setIntLE(tagIndexesOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.tagIndexes.length > 4096000) {
                throw ProtocolException.arrayTooLong("TagIndexes", this.tagIndexes.length, 4096000);
            }
            VarInt.write(buf, this.tagIndexes.length);
            for (int n : this.tagIndexes) {
                buf.writeIntLE(n);
            }
        } else {
            buf.setIntLE(tagIndexesOffsetSlot, -1);
        }
    }

    public int computeSize() {
        int size = 47;
        if (this.id != null) {
            size += PacketIO.stringSize(this.id);
        }
        if (this.cubeTextures != null) {
            int cubeTexturesSize = 0;
            for (BlockTextures blockTextures : this.cubeTextures) {
                cubeTexturesSize += blockTextures.computeSize();
            }
            size += VarInt.size(this.cubeTextures.length) + cubeTexturesSize;
        }
        if (this.shaderEffect != null) {
            size += VarInt.size(this.shaderEffect.length) + this.shaderEffect.length * 1;
        }
        if (this.particles != null) {
            int particlesSize = 0;
            for (ModelParticle modelParticle : this.particles) {
                particlesSize += modelParticle.computeSize();
            }
            size += VarInt.size(this.particles.length) + particlesSize;
        }
        if (this.blockParticleSetId != null) {
            size += PacketIO.stringSize(this.blockParticleSetId);
        }
        if (this.tagIndexes != null) {
            size += VarInt.size(this.tagIndexes.length) + this.tagIndexes.length * 4;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        ValidationResult structResult;
        int i;
        int pos;
        if (buffer.readableBytes() - offset < 47) {
            return ValidationResult.error("Buffer too small: expected at least 47 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 4) != 0) {
            int idOffset = buffer.getIntLE(offset + 23);
            if (idOffset < 0) {
                return ValidationResult.error("Invalid offset for Id");
            }
            pos = offset + 47 + idOffset;
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
        if ((nullBits & 8) != 0) {
            int cubeTexturesOffset = buffer.getIntLE(offset + 27);
            if (cubeTexturesOffset < 0) {
                return ValidationResult.error("Invalid offset for CubeTextures");
            }
            pos = offset + 47 + cubeTexturesOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for CubeTextures");
            }
            int cubeTexturesCount = VarInt.peek(buffer, pos);
            if (cubeTexturesCount < 0) {
                return ValidationResult.error("Invalid array count for CubeTextures");
            }
            if (cubeTexturesCount > 4096000) {
                return ValidationResult.error("CubeTextures exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < cubeTexturesCount; ++i) {
                structResult = BlockTextures.validateStructure(buffer, pos);
                if (!structResult.isValid()) {
                    return ValidationResult.error("Invalid BlockTextures in CubeTextures[" + i + "]: " + structResult.error());
                }
                pos += BlockTextures.computeBytesConsumed(buffer, pos);
            }
        }
        if ((nullBits & 0x10) != 0) {
            int shaderEffectOffset = buffer.getIntLE(offset + 31);
            if (shaderEffectOffset < 0) {
                return ValidationResult.error("Invalid offset for ShaderEffect");
            }
            pos = offset + 47 + shaderEffectOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for ShaderEffect");
            }
            int shaderEffectCount = VarInt.peek(buffer, pos);
            if (shaderEffectCount < 0) {
                return ValidationResult.error("Invalid array count for ShaderEffect");
            }
            if (shaderEffectCount > 4096000) {
                return ValidationResult.error("ShaderEffect exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += shaderEffectCount * 1) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading ShaderEffect");
            }
        }
        if ((nullBits & 0x20) != 0) {
            int particlesOffset = buffer.getIntLE(offset + 35);
            if (particlesOffset < 0) {
                return ValidationResult.error("Invalid offset for Particles");
            }
            pos = offset + 47 + particlesOffset;
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
        if ((nullBits & 0x40) != 0) {
            int blockParticleSetIdOffset = buffer.getIntLE(offset + 39);
            if (blockParticleSetIdOffset < 0) {
                return ValidationResult.error("Invalid offset for BlockParticleSetId");
            }
            pos = offset + 47 + blockParticleSetIdOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for BlockParticleSetId");
            }
            int blockParticleSetIdLen = VarInt.peek(buffer, pos);
            if (blockParticleSetIdLen < 0) {
                return ValidationResult.error("Invalid string length for BlockParticleSetId");
            }
            if (blockParticleSetIdLen > 4096000) {
                return ValidationResult.error("BlockParticleSetId exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += blockParticleSetIdLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading BlockParticleSetId");
            }
        }
        if ((nullBits & 0x80) != 0) {
            int tagIndexesOffset = buffer.getIntLE(offset + 43);
            if (tagIndexesOffset < 0) {
                return ValidationResult.error("Invalid offset for TagIndexes");
            }
            pos = offset + 47 + tagIndexesOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for TagIndexes");
            }
            int tagIndexesCount = VarInt.peek(buffer, pos);
            if (tagIndexesCount < 0) {
                return ValidationResult.error("Invalid array count for TagIndexes");
            }
            if (tagIndexesCount > 4096000) {
                return ValidationResult.error("TagIndexes exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += tagIndexesCount * 4) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading TagIndexes");
            }
        }
        return ValidationResult.OK;
    }

    public Fluid clone() {
        Fluid copy = new Fluid();
        copy.id = this.id;
        copy.maxFluidLevel = this.maxFluidLevel;
        copy.cubeTextures = this.cubeTextures != null ? (BlockTextures[])Arrays.stream(this.cubeTextures).map(e -> e.clone()).toArray(BlockTextures[]::new) : null;
        copy.requiresAlphaBlending = this.requiresAlphaBlending;
        copy.opacity = this.opacity;
        copy.shaderEffect = this.shaderEffect != null ? Arrays.copyOf(this.shaderEffect, this.shaderEffect.length) : null;
        copy.light = this.light != null ? this.light.clone() : null;
        copy.particles = this.particles != null ? (ModelParticle[])Arrays.stream(this.particles).map(e -> e.clone()).toArray(ModelParticle[]::new) : null;
        copy.drawType = this.drawType;
        copy.fluidFXIndex = this.fluidFXIndex;
        copy.blockSoundSetIndex = this.blockSoundSetIndex;
        copy.blockParticleSetId = this.blockParticleSetId;
        copy.particleColor = this.particleColor != null ? this.particleColor.clone() : null;
        copy.tagIndexes = this.tagIndexes != null ? Arrays.copyOf(this.tagIndexes, this.tagIndexes.length) : null;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Fluid)) {
            return false;
        }
        Fluid other = (Fluid)obj;
        return Objects.equals(this.id, other.id) && this.maxFluidLevel == other.maxFluidLevel && Arrays.equals(this.cubeTextures, other.cubeTextures) && this.requiresAlphaBlending == other.requiresAlphaBlending && Objects.equals((Object)this.opacity, (Object)other.opacity) && Arrays.equals((Object[])this.shaderEffect, (Object[])other.shaderEffect) && Objects.equals(this.light, other.light) && Arrays.equals(this.particles, other.particles) && Objects.equals((Object)this.drawType, (Object)other.drawType) && this.fluidFXIndex == other.fluidFXIndex && this.blockSoundSetIndex == other.blockSoundSetIndex && Objects.equals(this.blockParticleSetId, other.blockParticleSetId) && Objects.equals(this.particleColor, other.particleColor) && Arrays.equals(this.tagIndexes, other.tagIndexes);
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + Objects.hashCode(this.id);
        result = 31 * result + Integer.hashCode(this.maxFluidLevel);
        result = 31 * result + Arrays.hashCode(this.cubeTextures);
        result = 31 * result + Boolean.hashCode(this.requiresAlphaBlending);
        result = 31 * result + Objects.hashCode((Object)this.opacity);
        result = 31 * result + Arrays.hashCode((Object[])this.shaderEffect);
        result = 31 * result + Objects.hashCode(this.light);
        result = 31 * result + Arrays.hashCode(this.particles);
        result = 31 * result + Objects.hashCode((Object)this.drawType);
        result = 31 * result + Integer.hashCode(this.fluidFXIndex);
        result = 31 * result + Integer.hashCode(this.blockSoundSetIndex);
        result = 31 * result + Objects.hashCode(this.blockParticleSetId);
        result = 31 * result + Objects.hashCode(this.particleColor);
        result = 31 * result + Arrays.hashCode(this.tagIndexes);
        return result;
    }
}

