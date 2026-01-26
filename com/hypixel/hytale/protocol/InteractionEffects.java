/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.ModelParticle;
import com.hypixel.hytale.protocol.ModelTrail;
import com.hypixel.hytale.protocol.MovementEffects;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import com.hypixel.hytale.protocol.packets.camera.CameraShakeEffect;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InteractionEffects {
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 32;
    public static final int VARIABLE_FIELD_COUNT = 5;
    public static final int VARIABLE_BLOCK_START = 52;
    public static final int MAX_SIZE = 0x64000000;
    @Nullable
    public ModelParticle[] particles;
    @Nullable
    public ModelParticle[] firstPersonParticles;
    public int worldSoundEventIndex;
    public int localSoundEventIndex;
    @Nullable
    public ModelTrail[] trails;
    public boolean waitForAnimationToFinish = true;
    @Nullable
    public String itemPlayerAnimationsId;
    @Nullable
    public String itemAnimationId;
    public boolean clearAnimationOnFinish;
    public boolean clearSoundEventOnFinish;
    @Nullable
    public CameraShakeEffect cameraShake;
    @Nullable
    public MovementEffects movementEffects;
    public float startDelay;

    public InteractionEffects() {
    }

    public InteractionEffects(@Nullable ModelParticle[] particles, @Nullable ModelParticle[] firstPersonParticles, int worldSoundEventIndex, int localSoundEventIndex, @Nullable ModelTrail[] trails, boolean waitForAnimationToFinish, @Nullable String itemPlayerAnimationsId, @Nullable String itemAnimationId, boolean clearAnimationOnFinish, boolean clearSoundEventOnFinish, @Nullable CameraShakeEffect cameraShake, @Nullable MovementEffects movementEffects, float startDelay) {
        this.particles = particles;
        this.firstPersonParticles = firstPersonParticles;
        this.worldSoundEventIndex = worldSoundEventIndex;
        this.localSoundEventIndex = localSoundEventIndex;
        this.trails = trails;
        this.waitForAnimationToFinish = waitForAnimationToFinish;
        this.itemPlayerAnimationsId = itemPlayerAnimationsId;
        this.itemAnimationId = itemAnimationId;
        this.clearAnimationOnFinish = clearAnimationOnFinish;
        this.clearSoundEventOnFinish = clearSoundEventOnFinish;
        this.cameraShake = cameraShake;
        this.movementEffects = movementEffects;
        this.startDelay = startDelay;
    }

    public InteractionEffects(@Nonnull InteractionEffects other) {
        this.particles = other.particles;
        this.firstPersonParticles = other.firstPersonParticles;
        this.worldSoundEventIndex = other.worldSoundEventIndex;
        this.localSoundEventIndex = other.localSoundEventIndex;
        this.trails = other.trails;
        this.waitForAnimationToFinish = other.waitForAnimationToFinish;
        this.itemPlayerAnimationsId = other.itemPlayerAnimationsId;
        this.itemAnimationId = other.itemAnimationId;
        this.clearAnimationOnFinish = other.clearAnimationOnFinish;
        this.clearSoundEventOnFinish = other.clearSoundEventOnFinish;
        this.cameraShake = other.cameraShake;
        this.movementEffects = other.movementEffects;
        this.startDelay = other.startDelay;
    }

    @Nonnull
    public static InteractionEffects deserialize(@Nonnull ByteBuf buf, int offset) {
        int i;
        int elemPos;
        int varIntLen;
        InteractionEffects obj = new InteractionEffects();
        byte nullBits = buf.getByte(offset);
        obj.worldSoundEventIndex = buf.getIntLE(offset + 1);
        obj.localSoundEventIndex = buf.getIntLE(offset + 5);
        obj.waitForAnimationToFinish = buf.getByte(offset + 9) != 0;
        obj.clearAnimationOnFinish = buf.getByte(offset + 10) != 0;
        boolean bl = obj.clearSoundEventOnFinish = buf.getByte(offset + 11) != 0;
        if ((nullBits & 1) != 0) {
            obj.cameraShake = CameraShakeEffect.deserialize(buf, offset + 12);
        }
        if ((nullBits & 2) != 0) {
            obj.movementEffects = MovementEffects.deserialize(buf, offset + 21);
        }
        obj.startDelay = buf.getFloatLE(offset + 28);
        if ((nullBits & 4) != 0) {
            int varPos0 = offset + 52 + buf.getIntLE(offset + 32);
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
        if ((nullBits & 8) != 0) {
            int varPos1 = offset + 52 + buf.getIntLE(offset + 36);
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
        if ((nullBits & 0x10) != 0) {
            int varPos2 = offset + 52 + buf.getIntLE(offset + 40);
            int trailsCount = VarInt.peek(buf, varPos2);
            if (trailsCount < 0) {
                throw ProtocolException.negativeLength("Trails", trailsCount);
            }
            if (trailsCount > 4096000) {
                throw ProtocolException.arrayTooLong("Trails", trailsCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos2);
            if ((long)(varPos2 + varIntLen) + (long)trailsCount * 27L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("Trails", varPos2 + varIntLen + trailsCount * 27, buf.readableBytes());
            }
            obj.trails = new ModelTrail[trailsCount];
            elemPos = varPos2 + varIntLen;
            for (i = 0; i < trailsCount; ++i) {
                obj.trails[i] = ModelTrail.deserialize(buf, elemPos);
                elemPos += ModelTrail.computeBytesConsumed(buf, elemPos);
            }
        }
        if ((nullBits & 0x20) != 0) {
            int varPos3 = offset + 52 + buf.getIntLE(offset + 44);
            int itemPlayerAnimationsIdLen = VarInt.peek(buf, varPos3);
            if (itemPlayerAnimationsIdLen < 0) {
                throw ProtocolException.negativeLength("ItemPlayerAnimationsId", itemPlayerAnimationsIdLen);
            }
            if (itemPlayerAnimationsIdLen > 4096000) {
                throw ProtocolException.stringTooLong("ItemPlayerAnimationsId", itemPlayerAnimationsIdLen, 4096000);
            }
            obj.itemPlayerAnimationsId = PacketIO.readVarString(buf, varPos3, PacketIO.UTF8);
        }
        if ((nullBits & 0x40) != 0) {
            int varPos4 = offset + 52 + buf.getIntLE(offset + 48);
            int itemAnimationIdLen = VarInt.peek(buf, varPos4);
            if (itemAnimationIdLen < 0) {
                throw ProtocolException.negativeLength("ItemAnimationId", itemAnimationIdLen);
            }
            if (itemAnimationIdLen > 4096000) {
                throw ProtocolException.stringTooLong("ItemAnimationId", itemAnimationIdLen, 4096000);
            }
            obj.itemAnimationId = PacketIO.readVarString(buf, varPos4, PacketIO.UTF8);
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int sl;
        int i;
        int arrLen;
        byte nullBits = buf.getByte(offset);
        int maxEnd = 52;
        if ((nullBits & 4) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 32);
            int pos0 = offset + 52 + fieldOffset0;
            arrLen = VarInt.peek(buf, pos0);
            pos0 += VarInt.length(buf, pos0);
            for (i = 0; i < arrLen; ++i) {
                pos0 += ModelParticle.computeBytesConsumed(buf, pos0);
            }
            if (pos0 - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 8) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 36);
            int pos1 = offset + 52 + fieldOffset1;
            arrLen = VarInt.peek(buf, pos1);
            pos1 += VarInt.length(buf, pos1);
            for (i = 0; i < arrLen; ++i) {
                pos1 += ModelParticle.computeBytesConsumed(buf, pos1);
            }
            if (pos1 - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        if ((nullBits & 0x10) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 40);
            int pos2 = offset + 52 + fieldOffset2;
            arrLen = VarInt.peek(buf, pos2);
            pos2 += VarInt.length(buf, pos2);
            for (i = 0; i < arrLen; ++i) {
                pos2 += ModelTrail.computeBytesConsumed(buf, pos2);
            }
            if (pos2 - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        if ((nullBits & 0x20) != 0) {
            int fieldOffset3 = buf.getIntLE(offset + 44);
            int pos3 = offset + 52 + fieldOffset3;
            sl = VarInt.peek(buf, pos3);
            if ((pos3 += VarInt.length(buf, pos3) + sl) - offset > maxEnd) {
                maxEnd = pos3 - offset;
            }
        }
        if ((nullBits & 0x40) != 0) {
            int fieldOffset4 = buf.getIntLE(offset + 48);
            int pos4 = offset + 52 + fieldOffset4;
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
        if (this.cameraShake != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.movementEffects != null) {
            nullBits = (byte)(nullBits | 2);
        }
        if (this.particles != null) {
            nullBits = (byte)(nullBits | 4);
        }
        if (this.firstPersonParticles != null) {
            nullBits = (byte)(nullBits | 8);
        }
        if (this.trails != null) {
            nullBits = (byte)(nullBits | 0x10);
        }
        if (this.itemPlayerAnimationsId != null) {
            nullBits = (byte)(nullBits | 0x20);
        }
        if (this.itemAnimationId != null) {
            nullBits = (byte)(nullBits | 0x40);
        }
        buf.writeByte(nullBits);
        buf.writeIntLE(this.worldSoundEventIndex);
        buf.writeIntLE(this.localSoundEventIndex);
        buf.writeByte(this.waitForAnimationToFinish ? 1 : 0);
        buf.writeByte(this.clearAnimationOnFinish ? 1 : 0);
        buf.writeByte(this.clearSoundEventOnFinish ? 1 : 0);
        if (this.cameraShake != null) {
            this.cameraShake.serialize(buf);
        } else {
            buf.writeZero(9);
        }
        if (this.movementEffects != null) {
            this.movementEffects.serialize(buf);
        } else {
            buf.writeZero(7);
        }
        buf.writeFloatLE(this.startDelay);
        int particlesOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int firstPersonParticlesOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int trailsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int itemPlayerAnimationsIdOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int itemAnimationIdOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
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
        if (this.firstPersonParticles != null) {
            buf.setIntLE(firstPersonParticlesOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.firstPersonParticles.length > 4096000) {
                throw ProtocolException.arrayTooLong("FirstPersonParticles", this.firstPersonParticles.length, 4096000);
            }
            VarInt.write(buf, this.firstPersonParticles.length);
            for (ModelParticle modelParticle : this.firstPersonParticles) {
                modelParticle.serialize(buf);
            }
        } else {
            buf.setIntLE(firstPersonParticlesOffsetSlot, -1);
        }
        if (this.trails != null) {
            buf.setIntLE(trailsOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.trails.length > 4096000) {
                throw ProtocolException.arrayTooLong("Trails", this.trails.length, 4096000);
            }
            VarInt.write(buf, this.trails.length);
            for (ModelTrail modelTrail : this.trails) {
                modelTrail.serialize(buf);
            }
        } else {
            buf.setIntLE(trailsOffsetSlot, -1);
        }
        if (this.itemPlayerAnimationsId != null) {
            buf.setIntLE(itemPlayerAnimationsIdOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.itemPlayerAnimationsId, 4096000);
        } else {
            buf.setIntLE(itemPlayerAnimationsIdOffsetSlot, -1);
        }
        if (this.itemAnimationId != null) {
            buf.setIntLE(itemAnimationIdOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.itemAnimationId, 4096000);
        } else {
            buf.setIntLE(itemAnimationIdOffsetSlot, -1);
        }
    }

    public int computeSize() {
        int size = 52;
        if (this.particles != null) {
            int particlesSize = 0;
            for (ModelParticle modelParticle : this.particles) {
                particlesSize += modelParticle.computeSize();
            }
            size += VarInt.size(this.particles.length) + particlesSize;
        }
        if (this.firstPersonParticles != null) {
            int firstPersonParticlesSize = 0;
            for (ModelParticle modelParticle : this.firstPersonParticles) {
                firstPersonParticlesSize += modelParticle.computeSize();
            }
            size += VarInt.size(this.firstPersonParticles.length) + firstPersonParticlesSize;
        }
        if (this.trails != null) {
            int trailsSize = 0;
            for (ModelTrail modelTrail : this.trails) {
                trailsSize += modelTrail.computeSize();
            }
            size += VarInt.size(this.trails.length) + trailsSize;
        }
        if (this.itemPlayerAnimationsId != null) {
            size += PacketIO.stringSize(this.itemPlayerAnimationsId);
        }
        if (this.itemAnimationId != null) {
            size += PacketIO.stringSize(this.itemAnimationId);
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        ValidationResult structResult;
        int i;
        int pos;
        if (buffer.readableBytes() - offset < 52) {
            return ValidationResult.error("Buffer too small: expected at least 52 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 4) != 0) {
            int particlesOffset = buffer.getIntLE(offset + 32);
            if (particlesOffset < 0) {
                return ValidationResult.error("Invalid offset for Particles");
            }
            pos = offset + 52 + particlesOffset;
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
        if ((nullBits & 8) != 0) {
            int firstPersonParticlesOffset = buffer.getIntLE(offset + 36);
            if (firstPersonParticlesOffset < 0) {
                return ValidationResult.error("Invalid offset for FirstPersonParticles");
            }
            pos = offset + 52 + firstPersonParticlesOffset;
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
        if ((nullBits & 0x10) != 0) {
            int trailsOffset = buffer.getIntLE(offset + 40);
            if (trailsOffset < 0) {
                return ValidationResult.error("Invalid offset for Trails");
            }
            pos = offset + 52 + trailsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Trails");
            }
            int trailsCount = VarInt.peek(buffer, pos);
            if (trailsCount < 0) {
                return ValidationResult.error("Invalid array count for Trails");
            }
            if (trailsCount > 4096000) {
                return ValidationResult.error("Trails exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < trailsCount; ++i) {
                structResult = ModelTrail.validateStructure(buffer, pos);
                if (!structResult.isValid()) {
                    return ValidationResult.error("Invalid ModelTrail in Trails[" + i + "]: " + structResult.error());
                }
                pos += ModelTrail.computeBytesConsumed(buffer, pos);
            }
        }
        if ((nullBits & 0x20) != 0) {
            int itemPlayerAnimationsIdOffset = buffer.getIntLE(offset + 44);
            if (itemPlayerAnimationsIdOffset < 0) {
                return ValidationResult.error("Invalid offset for ItemPlayerAnimationsId");
            }
            pos = offset + 52 + itemPlayerAnimationsIdOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for ItemPlayerAnimationsId");
            }
            int itemPlayerAnimationsIdLen = VarInt.peek(buffer, pos);
            if (itemPlayerAnimationsIdLen < 0) {
                return ValidationResult.error("Invalid string length for ItemPlayerAnimationsId");
            }
            if (itemPlayerAnimationsIdLen > 4096000) {
                return ValidationResult.error("ItemPlayerAnimationsId exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += itemPlayerAnimationsIdLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading ItemPlayerAnimationsId");
            }
        }
        if ((nullBits & 0x40) != 0) {
            int itemAnimationIdOffset = buffer.getIntLE(offset + 48);
            if (itemAnimationIdOffset < 0) {
                return ValidationResult.error("Invalid offset for ItemAnimationId");
            }
            pos = offset + 52 + itemAnimationIdOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for ItemAnimationId");
            }
            int itemAnimationIdLen = VarInt.peek(buffer, pos);
            if (itemAnimationIdLen < 0) {
                return ValidationResult.error("Invalid string length for ItemAnimationId");
            }
            if (itemAnimationIdLen > 4096000) {
                return ValidationResult.error("ItemAnimationId exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += itemAnimationIdLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading ItemAnimationId");
            }
        }
        return ValidationResult.OK;
    }

    public InteractionEffects clone() {
        InteractionEffects copy = new InteractionEffects();
        copy.particles = this.particles != null ? (ModelParticle[])Arrays.stream(this.particles).map(e -> e.clone()).toArray(ModelParticle[]::new) : null;
        copy.firstPersonParticles = this.firstPersonParticles != null ? (ModelParticle[])Arrays.stream(this.firstPersonParticles).map(e -> e.clone()).toArray(ModelParticle[]::new) : null;
        copy.worldSoundEventIndex = this.worldSoundEventIndex;
        copy.localSoundEventIndex = this.localSoundEventIndex;
        copy.trails = this.trails != null ? (ModelTrail[])Arrays.stream(this.trails).map(e -> e.clone()).toArray(ModelTrail[]::new) : null;
        copy.waitForAnimationToFinish = this.waitForAnimationToFinish;
        copy.itemPlayerAnimationsId = this.itemPlayerAnimationsId;
        copy.itemAnimationId = this.itemAnimationId;
        copy.clearAnimationOnFinish = this.clearAnimationOnFinish;
        copy.clearSoundEventOnFinish = this.clearSoundEventOnFinish;
        copy.cameraShake = this.cameraShake != null ? this.cameraShake.clone() : null;
        copy.movementEffects = this.movementEffects != null ? this.movementEffects.clone() : null;
        copy.startDelay = this.startDelay;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof InteractionEffects)) {
            return false;
        }
        InteractionEffects other = (InteractionEffects)obj;
        return Arrays.equals(this.particles, other.particles) && Arrays.equals(this.firstPersonParticles, other.firstPersonParticles) && this.worldSoundEventIndex == other.worldSoundEventIndex && this.localSoundEventIndex == other.localSoundEventIndex && Arrays.equals(this.trails, other.trails) && this.waitForAnimationToFinish == other.waitForAnimationToFinish && Objects.equals(this.itemPlayerAnimationsId, other.itemPlayerAnimationsId) && Objects.equals(this.itemAnimationId, other.itemAnimationId) && this.clearAnimationOnFinish == other.clearAnimationOnFinish && this.clearSoundEventOnFinish == other.clearSoundEventOnFinish && Objects.equals(this.cameraShake, other.cameraShake) && Objects.equals(this.movementEffects, other.movementEffects) && this.startDelay == other.startDelay;
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + Arrays.hashCode(this.particles);
        result = 31 * result + Arrays.hashCode(this.firstPersonParticles);
        result = 31 * result + Integer.hashCode(this.worldSoundEventIndex);
        result = 31 * result + Integer.hashCode(this.localSoundEventIndex);
        result = 31 * result + Arrays.hashCode(this.trails);
        result = 31 * result + Boolean.hashCode(this.waitForAnimationToFinish);
        result = 31 * result + Objects.hashCode(this.itemPlayerAnimationsId);
        result = 31 * result + Objects.hashCode(this.itemAnimationId);
        result = 31 * result + Boolean.hashCode(this.clearAnimationOnFinish);
        result = 31 * result + Boolean.hashCode(this.clearSoundEventOnFinish);
        result = 31 * result + Objects.hashCode(this.cameraShake);
        result = 31 * result + Objects.hashCode(this.movementEffects);
        result = 31 * result + Float.hashCode(this.startDelay);
        return result;
    }
}

