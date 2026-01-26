/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.AbilityEffects;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.protocol.ModelParticle;
import com.hypixel.hytale.protocol.MovementEffects;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ApplicationEffects {
    public static final int NULLABLE_BIT_FIELD_SIZE = 2;
    public static final int FIXED_BLOCK_SIZE = 35;
    public static final int VARIABLE_FIELD_COUNT = 6;
    public static final int VARIABLE_BLOCK_START = 59;
    public static final int MAX_SIZE = 0x64000000;
    @Nullable
    public Color entityBottomTint;
    @Nullable
    public Color entityTopTint;
    @Nullable
    public String entityAnimationId;
    @Nullable
    public ModelParticle[] particles;
    @Nullable
    public ModelParticle[] firstPersonParticles;
    @Nullable
    public String screenEffect;
    public float horizontalSpeedMultiplier;
    public int soundEventIndexLocal;
    public int soundEventIndexWorld;
    @Nullable
    public String modelVFXId;
    @Nullable
    public MovementEffects movementEffects;
    public float mouseSensitivityAdjustmentTarget;
    public float mouseSensitivityAdjustmentDuration;
    @Nullable
    public AbilityEffects abilityEffects;

    public ApplicationEffects() {
    }

    public ApplicationEffects(@Nullable Color entityBottomTint, @Nullable Color entityTopTint, @Nullable String entityAnimationId, @Nullable ModelParticle[] particles, @Nullable ModelParticle[] firstPersonParticles, @Nullable String screenEffect, float horizontalSpeedMultiplier, int soundEventIndexLocal, int soundEventIndexWorld, @Nullable String modelVFXId, @Nullable MovementEffects movementEffects, float mouseSensitivityAdjustmentTarget, float mouseSensitivityAdjustmentDuration, @Nullable AbilityEffects abilityEffects) {
        this.entityBottomTint = entityBottomTint;
        this.entityTopTint = entityTopTint;
        this.entityAnimationId = entityAnimationId;
        this.particles = particles;
        this.firstPersonParticles = firstPersonParticles;
        this.screenEffect = screenEffect;
        this.horizontalSpeedMultiplier = horizontalSpeedMultiplier;
        this.soundEventIndexLocal = soundEventIndexLocal;
        this.soundEventIndexWorld = soundEventIndexWorld;
        this.modelVFXId = modelVFXId;
        this.movementEffects = movementEffects;
        this.mouseSensitivityAdjustmentTarget = mouseSensitivityAdjustmentTarget;
        this.mouseSensitivityAdjustmentDuration = mouseSensitivityAdjustmentDuration;
        this.abilityEffects = abilityEffects;
    }

    public ApplicationEffects(@Nonnull ApplicationEffects other) {
        this.entityBottomTint = other.entityBottomTint;
        this.entityTopTint = other.entityTopTint;
        this.entityAnimationId = other.entityAnimationId;
        this.particles = other.particles;
        this.firstPersonParticles = other.firstPersonParticles;
        this.screenEffect = other.screenEffect;
        this.horizontalSpeedMultiplier = other.horizontalSpeedMultiplier;
        this.soundEventIndexLocal = other.soundEventIndexLocal;
        this.soundEventIndexWorld = other.soundEventIndexWorld;
        this.modelVFXId = other.modelVFXId;
        this.movementEffects = other.movementEffects;
        this.mouseSensitivityAdjustmentTarget = other.mouseSensitivityAdjustmentTarget;
        this.mouseSensitivityAdjustmentDuration = other.mouseSensitivityAdjustmentDuration;
        this.abilityEffects = other.abilityEffects;
    }

    @Nonnull
    public static ApplicationEffects deserialize(@Nonnull ByteBuf buf, int offset) {
        int i;
        int elemPos;
        int varIntLen;
        ApplicationEffects obj = new ApplicationEffects();
        byte[] nullBits = PacketIO.readBytes(buf, offset, 2);
        if ((nullBits[0] & 1) != 0) {
            obj.entityBottomTint = Color.deserialize(buf, offset + 2);
        }
        if ((nullBits[0] & 2) != 0) {
            obj.entityTopTint = Color.deserialize(buf, offset + 5);
        }
        obj.horizontalSpeedMultiplier = buf.getFloatLE(offset + 8);
        obj.soundEventIndexLocal = buf.getIntLE(offset + 12);
        obj.soundEventIndexWorld = buf.getIntLE(offset + 16);
        if ((nullBits[0] & 4) != 0) {
            obj.movementEffects = MovementEffects.deserialize(buf, offset + 20);
        }
        obj.mouseSensitivityAdjustmentTarget = buf.getFloatLE(offset + 27);
        obj.mouseSensitivityAdjustmentDuration = buf.getFloatLE(offset + 31);
        if ((nullBits[0] & 8) != 0) {
            int varPos0 = offset + 59 + buf.getIntLE(offset + 35);
            int entityAnimationIdLen = VarInt.peek(buf, varPos0);
            if (entityAnimationIdLen < 0) {
                throw ProtocolException.negativeLength("EntityAnimationId", entityAnimationIdLen);
            }
            if (entityAnimationIdLen > 4096000) {
                throw ProtocolException.stringTooLong("EntityAnimationId", entityAnimationIdLen, 4096000);
            }
            obj.entityAnimationId = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
        }
        if ((nullBits[0] & 0x10) != 0) {
            int varPos1 = offset + 59 + buf.getIntLE(offset + 39);
            int particlesCount = VarInt.peek(buf, varPos1);
            if (particlesCount < 0) {
                throw ProtocolException.negativeLength("Particles", particlesCount);
            }
            if (particlesCount > 4096000) {
                throw ProtocolException.arrayTooLong("Particles", particlesCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos1);
            if ((long)(varPos1 + varIntLen) + (long)particlesCount * 34L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("Particles", varPos1 + varIntLen + particlesCount * 34, buf.readableBytes());
            }
            obj.particles = new ModelParticle[particlesCount];
            elemPos = varPos1 + varIntLen;
            for (i = 0; i < particlesCount; ++i) {
                obj.particles[i] = ModelParticle.deserialize(buf, elemPos);
                elemPos += ModelParticle.computeBytesConsumed(buf, elemPos);
            }
        }
        if ((nullBits[0] & 0x20) != 0) {
            int varPos2 = offset + 59 + buf.getIntLE(offset + 43);
            int firstPersonParticlesCount = VarInt.peek(buf, varPos2);
            if (firstPersonParticlesCount < 0) {
                throw ProtocolException.negativeLength("FirstPersonParticles", firstPersonParticlesCount);
            }
            if (firstPersonParticlesCount > 4096000) {
                throw ProtocolException.arrayTooLong("FirstPersonParticles", firstPersonParticlesCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos2);
            if ((long)(varPos2 + varIntLen) + (long)firstPersonParticlesCount * 34L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("FirstPersonParticles", varPos2 + varIntLen + firstPersonParticlesCount * 34, buf.readableBytes());
            }
            obj.firstPersonParticles = new ModelParticle[firstPersonParticlesCount];
            elemPos = varPos2 + varIntLen;
            for (i = 0; i < firstPersonParticlesCount; ++i) {
                obj.firstPersonParticles[i] = ModelParticle.deserialize(buf, elemPos);
                elemPos += ModelParticle.computeBytesConsumed(buf, elemPos);
            }
        }
        if ((nullBits[0] & 0x40) != 0) {
            int varPos3 = offset + 59 + buf.getIntLE(offset + 47);
            int screenEffectLen = VarInt.peek(buf, varPos3);
            if (screenEffectLen < 0) {
                throw ProtocolException.negativeLength("ScreenEffect", screenEffectLen);
            }
            if (screenEffectLen > 4096000) {
                throw ProtocolException.stringTooLong("ScreenEffect", screenEffectLen, 4096000);
            }
            obj.screenEffect = PacketIO.readVarString(buf, varPos3, PacketIO.UTF8);
        }
        if ((nullBits[0] & 0x80) != 0) {
            int varPos4 = offset + 59 + buf.getIntLE(offset + 51);
            int modelVFXIdLen = VarInt.peek(buf, varPos4);
            if (modelVFXIdLen < 0) {
                throw ProtocolException.negativeLength("ModelVFXId", modelVFXIdLen);
            }
            if (modelVFXIdLen > 4096000) {
                throw ProtocolException.stringTooLong("ModelVFXId", modelVFXIdLen, 4096000);
            }
            obj.modelVFXId = PacketIO.readVarString(buf, varPos4, PacketIO.UTF8);
        }
        if ((nullBits[1] & 1) != 0) {
            int varPos5 = offset + 59 + buf.getIntLE(offset + 55);
            obj.abilityEffects = AbilityEffects.deserialize(buf, varPos5);
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int i;
        int arrLen;
        int sl;
        byte[] nullBits = PacketIO.readBytes(buf, offset, 2);
        int maxEnd = 59;
        if ((nullBits[0] & 8) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 35);
            int pos0 = offset + 59 + fieldOffset0;
            sl = VarInt.peek(buf, pos0);
            if ((pos0 += VarInt.length(buf, pos0) + sl) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits[0] & 0x10) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 39);
            int pos1 = offset + 59 + fieldOffset1;
            arrLen = VarInt.peek(buf, pos1);
            pos1 += VarInt.length(buf, pos1);
            for (i = 0; i < arrLen; ++i) {
                pos1 += ModelParticle.computeBytesConsumed(buf, pos1);
            }
            if (pos1 - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        if ((nullBits[0] & 0x20) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 43);
            int pos2 = offset + 59 + fieldOffset2;
            arrLen = VarInt.peek(buf, pos2);
            pos2 += VarInt.length(buf, pos2);
            for (i = 0; i < arrLen; ++i) {
                pos2 += ModelParticle.computeBytesConsumed(buf, pos2);
            }
            if (pos2 - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        if ((nullBits[0] & 0x40) != 0) {
            int fieldOffset3 = buf.getIntLE(offset + 47);
            int pos3 = offset + 59 + fieldOffset3;
            sl = VarInt.peek(buf, pos3);
            if ((pos3 += VarInt.length(buf, pos3) + sl) - offset > maxEnd) {
                maxEnd = pos3 - offset;
            }
        }
        if ((nullBits[0] & 0x80) != 0) {
            int fieldOffset4 = buf.getIntLE(offset + 51);
            int pos4 = offset + 59 + fieldOffset4;
            sl = VarInt.peek(buf, pos4);
            if ((pos4 += VarInt.length(buf, pos4) + sl) - offset > maxEnd) {
                maxEnd = pos4 - offset;
            }
        }
        if ((nullBits[1] & 1) != 0) {
            int fieldOffset5 = buf.getIntLE(offset + 55);
            int pos5 = offset + 59 + fieldOffset5;
            if ((pos5 += AbilityEffects.computeBytesConsumed(buf, pos5)) - offset > maxEnd) {
                maxEnd = pos5 - offset;
            }
        }
        return maxEnd;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        byte[] nullBits = new byte[2];
        if (this.entityBottomTint != null) {
            nullBits[0] = (byte)(nullBits[0] | 1);
        }
        if (this.entityTopTint != null) {
            nullBits[0] = (byte)(nullBits[0] | 2);
        }
        if (this.movementEffects != null) {
            nullBits[0] = (byte)(nullBits[0] | 4);
        }
        if (this.entityAnimationId != null) {
            nullBits[0] = (byte)(nullBits[0] | 8);
        }
        if (this.particles != null) {
            nullBits[0] = (byte)(nullBits[0] | 0x10);
        }
        if (this.firstPersonParticles != null) {
            nullBits[0] = (byte)(nullBits[0] | 0x20);
        }
        if (this.screenEffect != null) {
            nullBits[0] = (byte)(nullBits[0] | 0x40);
        }
        if (this.modelVFXId != null) {
            nullBits[0] = (byte)(nullBits[0] | 0x80);
        }
        if (this.abilityEffects != null) {
            nullBits[1] = (byte)(nullBits[1] | 1);
        }
        buf.writeBytes(nullBits);
        if (this.entityBottomTint != null) {
            this.entityBottomTint.serialize(buf);
        } else {
            buf.writeZero(3);
        }
        if (this.entityTopTint != null) {
            this.entityTopTint.serialize(buf);
        } else {
            buf.writeZero(3);
        }
        buf.writeFloatLE(this.horizontalSpeedMultiplier);
        buf.writeIntLE(this.soundEventIndexLocal);
        buf.writeIntLE(this.soundEventIndexWorld);
        if (this.movementEffects != null) {
            this.movementEffects.serialize(buf);
        } else {
            buf.writeZero(7);
        }
        buf.writeFloatLE(this.mouseSensitivityAdjustmentTarget);
        buf.writeFloatLE(this.mouseSensitivityAdjustmentDuration);
        int entityAnimationIdOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int particlesOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int firstPersonParticlesOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int screenEffectOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int modelVFXIdOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int abilityEffectsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.entityAnimationId != null) {
            buf.setIntLE(entityAnimationIdOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.entityAnimationId, 4096000);
        } else {
            buf.setIntLE(entityAnimationIdOffsetSlot, -1);
        }
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
        if (this.screenEffect != null) {
            buf.setIntLE(screenEffectOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.screenEffect, 4096000);
        } else {
            buf.setIntLE(screenEffectOffsetSlot, -1);
        }
        if (this.modelVFXId != null) {
            buf.setIntLE(modelVFXIdOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.modelVFXId, 4096000);
        } else {
            buf.setIntLE(modelVFXIdOffsetSlot, -1);
        }
        if (this.abilityEffects != null) {
            buf.setIntLE(abilityEffectsOffsetSlot, buf.writerIndex() - varBlockStart);
            this.abilityEffects.serialize(buf);
        } else {
            buf.setIntLE(abilityEffectsOffsetSlot, -1);
        }
    }

    public int computeSize() {
        int size = 59;
        if (this.entityAnimationId != null) {
            size += PacketIO.stringSize(this.entityAnimationId);
        }
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
        if (this.screenEffect != null) {
            size += PacketIO.stringSize(this.screenEffect);
        }
        if (this.modelVFXId != null) {
            size += PacketIO.stringSize(this.modelVFXId);
        }
        if (this.abilityEffects != null) {
            size += this.abilityEffects.computeSize();
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        ValidationResult structResult;
        int i;
        int pos;
        if (buffer.readableBytes() - offset < 59) {
            return ValidationResult.error("Buffer too small: expected at least 59 bytes");
        }
        byte[] nullBits = PacketIO.readBytes(buffer, offset, 2);
        if ((nullBits[0] & 8) != 0) {
            int entityAnimationIdOffset = buffer.getIntLE(offset + 35);
            if (entityAnimationIdOffset < 0) {
                return ValidationResult.error("Invalid offset for EntityAnimationId");
            }
            pos = offset + 59 + entityAnimationIdOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for EntityAnimationId");
            }
            int entityAnimationIdLen = VarInt.peek(buffer, pos);
            if (entityAnimationIdLen < 0) {
                return ValidationResult.error("Invalid string length for EntityAnimationId");
            }
            if (entityAnimationIdLen > 4096000) {
                return ValidationResult.error("EntityAnimationId exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += entityAnimationIdLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading EntityAnimationId");
            }
        }
        if ((nullBits[0] & 0x10) != 0) {
            int particlesOffset = buffer.getIntLE(offset + 39);
            if (particlesOffset < 0) {
                return ValidationResult.error("Invalid offset for Particles");
            }
            pos = offset + 59 + particlesOffset;
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
        if ((nullBits[0] & 0x20) != 0) {
            int firstPersonParticlesOffset = buffer.getIntLE(offset + 43);
            if (firstPersonParticlesOffset < 0) {
                return ValidationResult.error("Invalid offset for FirstPersonParticles");
            }
            pos = offset + 59 + firstPersonParticlesOffset;
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
        if ((nullBits[0] & 0x40) != 0) {
            int screenEffectOffset = buffer.getIntLE(offset + 47);
            if (screenEffectOffset < 0) {
                return ValidationResult.error("Invalid offset for ScreenEffect");
            }
            pos = offset + 59 + screenEffectOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for ScreenEffect");
            }
            int screenEffectLen = VarInt.peek(buffer, pos);
            if (screenEffectLen < 0) {
                return ValidationResult.error("Invalid string length for ScreenEffect");
            }
            if (screenEffectLen > 4096000) {
                return ValidationResult.error("ScreenEffect exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += screenEffectLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading ScreenEffect");
            }
        }
        if ((nullBits[0] & 0x80) != 0) {
            int modelVFXIdOffset = buffer.getIntLE(offset + 51);
            if (modelVFXIdOffset < 0) {
                return ValidationResult.error("Invalid offset for ModelVFXId");
            }
            pos = offset + 59 + modelVFXIdOffset;
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
        if ((nullBits[1] & 1) != 0) {
            int abilityEffectsOffset = buffer.getIntLE(offset + 55);
            if (abilityEffectsOffset < 0) {
                return ValidationResult.error("Invalid offset for AbilityEffects");
            }
            pos = offset + 59 + abilityEffectsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for AbilityEffects");
            }
            ValidationResult abilityEffectsResult = AbilityEffects.validateStructure(buffer, pos);
            if (!abilityEffectsResult.isValid()) {
                return ValidationResult.error("Invalid AbilityEffects: " + abilityEffectsResult.error());
            }
            pos += AbilityEffects.computeBytesConsumed(buffer, pos);
        }
        return ValidationResult.OK;
    }

    public ApplicationEffects clone() {
        ApplicationEffects copy = new ApplicationEffects();
        copy.entityBottomTint = this.entityBottomTint != null ? this.entityBottomTint.clone() : null;
        copy.entityTopTint = this.entityTopTint != null ? this.entityTopTint.clone() : null;
        copy.entityAnimationId = this.entityAnimationId;
        copy.particles = this.particles != null ? (ModelParticle[])Arrays.stream(this.particles).map(e -> e.clone()).toArray(ModelParticle[]::new) : null;
        copy.firstPersonParticles = this.firstPersonParticles != null ? (ModelParticle[])Arrays.stream(this.firstPersonParticles).map(e -> e.clone()).toArray(ModelParticle[]::new) : null;
        copy.screenEffect = this.screenEffect;
        copy.horizontalSpeedMultiplier = this.horizontalSpeedMultiplier;
        copy.soundEventIndexLocal = this.soundEventIndexLocal;
        copy.soundEventIndexWorld = this.soundEventIndexWorld;
        copy.modelVFXId = this.modelVFXId;
        copy.movementEffects = this.movementEffects != null ? this.movementEffects.clone() : null;
        copy.mouseSensitivityAdjustmentTarget = this.mouseSensitivityAdjustmentTarget;
        copy.mouseSensitivityAdjustmentDuration = this.mouseSensitivityAdjustmentDuration;
        copy.abilityEffects = this.abilityEffects != null ? this.abilityEffects.clone() : null;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ApplicationEffects)) {
            return false;
        }
        ApplicationEffects other = (ApplicationEffects)obj;
        return Objects.equals(this.entityBottomTint, other.entityBottomTint) && Objects.equals(this.entityTopTint, other.entityTopTint) && Objects.equals(this.entityAnimationId, other.entityAnimationId) && Arrays.equals(this.particles, other.particles) && Arrays.equals(this.firstPersonParticles, other.firstPersonParticles) && Objects.equals(this.screenEffect, other.screenEffect) && this.horizontalSpeedMultiplier == other.horizontalSpeedMultiplier && this.soundEventIndexLocal == other.soundEventIndexLocal && this.soundEventIndexWorld == other.soundEventIndexWorld && Objects.equals(this.modelVFXId, other.modelVFXId) && Objects.equals(this.movementEffects, other.movementEffects) && this.mouseSensitivityAdjustmentTarget == other.mouseSensitivityAdjustmentTarget && this.mouseSensitivityAdjustmentDuration == other.mouseSensitivityAdjustmentDuration && Objects.equals(this.abilityEffects, other.abilityEffects);
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + Objects.hashCode(this.entityBottomTint);
        result = 31 * result + Objects.hashCode(this.entityTopTint);
        result = 31 * result + Objects.hashCode(this.entityAnimationId);
        result = 31 * result + Arrays.hashCode(this.particles);
        result = 31 * result + Arrays.hashCode(this.firstPersonParticles);
        result = 31 * result + Objects.hashCode(this.screenEffect);
        result = 31 * result + Float.hashCode(this.horizontalSpeedMultiplier);
        result = 31 * result + Integer.hashCode(this.soundEventIndexLocal);
        result = 31 * result + Integer.hashCode(this.soundEventIndexWorld);
        result = 31 * result + Objects.hashCode(this.modelVFXId);
        result = 31 * result + Objects.hashCode(this.movementEffects);
        result = 31 * result + Float.hashCode(this.mouseSensitivityAdjustmentTarget);
        result = 31 * result + Float.hashCode(this.mouseSensitivityAdjustmentDuration);
        result = 31 * result + Objects.hashCode(this.abilityEffects);
        return result;
    }
}

