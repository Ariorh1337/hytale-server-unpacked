/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.AnimationSet;
import com.hypixel.hytale.protocol.CameraSettings;
import com.hypixel.hytale.protocol.ColorLight;
import com.hypixel.hytale.protocol.DetailBox;
import com.hypixel.hytale.protocol.Hitbox;
import com.hypixel.hytale.protocol.ModelAttachment;
import com.hypixel.hytale.protocol.ModelParticle;
import com.hypixel.hytale.protocol.ModelTrail;
import com.hypixel.hytale.protocol.Phobia;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Model {
    public static final int NULLABLE_BIT_FIELD_SIZE = 2;
    public static final int FIXED_BLOCK_SIZE = 43;
    public static final int VARIABLE_FIELD_COUNT = 12;
    public static final int VARIABLE_BLOCK_START = 91;
    public static final int MAX_SIZE = 0x64000000;
    @Nullable
    public String assetId;
    @Nullable
    public String path;
    @Nullable
    public String texture;
    @Nullable
    public String gradientSet;
    @Nullable
    public String gradientId;
    @Nullable
    public CameraSettings camera;
    public float scale;
    public float eyeHeight;
    public float crouchOffset;
    @Nullable
    public Map<String, AnimationSet> animationSets;
    @Nullable
    public ModelAttachment[] attachments;
    @Nullable
    public Hitbox hitbox;
    @Nullable
    public ModelParticle[] particles;
    @Nullable
    public ModelTrail[] trails;
    @Nullable
    public ColorLight light;
    @Nullable
    public Map<String, DetailBox[]> detailBoxes;
    @Nonnull
    public Phobia phobia = Phobia.None;
    @Nullable
    public Model phobiaModel;

    public Model() {
    }

    public Model(@Nullable String assetId, @Nullable String path, @Nullable String texture, @Nullable String gradientSet, @Nullable String gradientId, @Nullable CameraSettings camera, float scale, float eyeHeight, float crouchOffset, @Nullable Map<String, AnimationSet> animationSets, @Nullable ModelAttachment[] attachments, @Nullable Hitbox hitbox, @Nullable ModelParticle[] particles, @Nullable ModelTrail[] trails, @Nullable ColorLight light, @Nullable Map<String, DetailBox[]> detailBoxes, @Nonnull Phobia phobia, @Nullable Model phobiaModel) {
        this.assetId = assetId;
        this.path = path;
        this.texture = texture;
        this.gradientSet = gradientSet;
        this.gradientId = gradientId;
        this.camera = camera;
        this.scale = scale;
        this.eyeHeight = eyeHeight;
        this.crouchOffset = crouchOffset;
        this.animationSets = animationSets;
        this.attachments = attachments;
        this.hitbox = hitbox;
        this.particles = particles;
        this.trails = trails;
        this.light = light;
        this.detailBoxes = detailBoxes;
        this.phobia = phobia;
        this.phobiaModel = phobiaModel;
    }

    public Model(@Nonnull Model other) {
        this.assetId = other.assetId;
        this.path = other.path;
        this.texture = other.texture;
        this.gradientSet = other.gradientSet;
        this.gradientId = other.gradientId;
        this.camera = other.camera;
        this.scale = other.scale;
        this.eyeHeight = other.eyeHeight;
        this.crouchOffset = other.crouchOffset;
        this.animationSets = other.animationSets;
        this.attachments = other.attachments;
        this.hitbox = other.hitbox;
        this.particles = other.particles;
        this.trails = other.trails;
        this.light = other.light;
        this.detailBoxes = other.detailBoxes;
        this.phobia = other.phobia;
        this.phobiaModel = other.phobiaModel;
    }

    @Nonnull
    public static Model deserialize(@Nonnull ByteBuf buf, int offset) {
        int elemPos;
        String key;
        int keyVarLen;
        int keyLen;
        int i;
        int dictPos;
        int varIntLen;
        Model obj = new Model();
        byte[] nullBits = PacketIO.readBytes(buf, offset, 2);
        obj.scale = buf.getFloatLE(offset + 2);
        obj.eyeHeight = buf.getFloatLE(offset + 6);
        obj.crouchOffset = buf.getFloatLE(offset + 10);
        if ((nullBits[0] & 1) != 0) {
            obj.hitbox = Hitbox.deserialize(buf, offset + 14);
        }
        if ((nullBits[0] & 2) != 0) {
            obj.light = ColorLight.deserialize(buf, offset + 38);
        }
        obj.phobia = Phobia.fromValue(buf.getByte(offset + 42));
        if ((nullBits[0] & 4) != 0) {
            int varPos0 = offset + 91 + buf.getIntLE(offset + 43);
            int assetIdLen = VarInt.peek(buf, varPos0);
            if (assetIdLen < 0) {
                throw ProtocolException.negativeLength("AssetId", assetIdLen);
            }
            if (assetIdLen > 4096000) {
                throw ProtocolException.stringTooLong("AssetId", assetIdLen, 4096000);
            }
            obj.assetId = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
        }
        if ((nullBits[0] & 8) != 0) {
            int varPos1 = offset + 91 + buf.getIntLE(offset + 47);
            int pathLen = VarInt.peek(buf, varPos1);
            if (pathLen < 0) {
                throw ProtocolException.negativeLength("Path", pathLen);
            }
            if (pathLen > 4096000) {
                throw ProtocolException.stringTooLong("Path", pathLen, 4096000);
            }
            obj.path = PacketIO.readVarString(buf, varPos1, PacketIO.UTF8);
        }
        if ((nullBits[0] & 0x10) != 0) {
            int varPos2 = offset + 91 + buf.getIntLE(offset + 51);
            int textureLen = VarInt.peek(buf, varPos2);
            if (textureLen < 0) {
                throw ProtocolException.negativeLength("Texture", textureLen);
            }
            if (textureLen > 4096000) {
                throw ProtocolException.stringTooLong("Texture", textureLen, 4096000);
            }
            obj.texture = PacketIO.readVarString(buf, varPos2, PacketIO.UTF8);
        }
        if ((nullBits[0] & 0x20) != 0) {
            int varPos3 = offset + 91 + buf.getIntLE(offset + 55);
            int gradientSetLen = VarInt.peek(buf, varPos3);
            if (gradientSetLen < 0) {
                throw ProtocolException.negativeLength("GradientSet", gradientSetLen);
            }
            if (gradientSetLen > 4096000) {
                throw ProtocolException.stringTooLong("GradientSet", gradientSetLen, 4096000);
            }
            obj.gradientSet = PacketIO.readVarString(buf, varPos3, PacketIO.UTF8);
        }
        if ((nullBits[0] & 0x40) != 0) {
            int varPos4 = offset + 91 + buf.getIntLE(offset + 59);
            int gradientIdLen = VarInt.peek(buf, varPos4);
            if (gradientIdLen < 0) {
                throw ProtocolException.negativeLength("GradientId", gradientIdLen);
            }
            if (gradientIdLen > 4096000) {
                throw ProtocolException.stringTooLong("GradientId", gradientIdLen, 4096000);
            }
            obj.gradientId = PacketIO.readVarString(buf, varPos4, PacketIO.UTF8);
        }
        if ((nullBits[0] & 0x80) != 0) {
            int varPos5 = offset + 91 + buf.getIntLE(offset + 63);
            obj.camera = CameraSettings.deserialize(buf, varPos5);
        }
        if ((nullBits[1] & 1) != 0) {
            int varPos6 = offset + 91 + buf.getIntLE(offset + 67);
            int animationSetsCount = VarInt.peek(buf, varPos6);
            if (animationSetsCount < 0) {
                throw ProtocolException.negativeLength("AnimationSets", animationSetsCount);
            }
            if (animationSetsCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("AnimationSets", animationSetsCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos6);
            obj.animationSets = new HashMap<String, AnimationSet>(animationSetsCount);
            dictPos = varPos6 + varIntLen;
            for (i = 0; i < animationSetsCount; ++i) {
                keyLen = VarInt.peek(buf, dictPos);
                if (keyLen < 0) {
                    throw ProtocolException.negativeLength("key", keyLen);
                }
                if (keyLen > 4096000) {
                    throw ProtocolException.stringTooLong("key", keyLen, 4096000);
                }
                keyVarLen = VarInt.length(buf, dictPos);
                key = PacketIO.readVarString(buf, dictPos);
                AnimationSet val = AnimationSet.deserialize(buf, dictPos += keyVarLen + keyLen);
                dictPos += AnimationSet.computeBytesConsumed(buf, dictPos);
                if (obj.animationSets.put(key, val) == null) continue;
                throw ProtocolException.duplicateKey("animationSets", key);
            }
        }
        if ((nullBits[1] & 2) != 0) {
            int varPos7 = offset + 91 + buf.getIntLE(offset + 71);
            int attachmentsCount = VarInt.peek(buf, varPos7);
            if (attachmentsCount < 0) {
                throw ProtocolException.negativeLength("Attachments", attachmentsCount);
            }
            if (attachmentsCount > 4096000) {
                throw ProtocolException.arrayTooLong("Attachments", attachmentsCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos7);
            if ((long)(varPos7 + varIntLen) + (long)attachmentsCount * 1L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("Attachments", varPos7 + varIntLen + attachmentsCount * 1, buf.readableBytes());
            }
            obj.attachments = new ModelAttachment[attachmentsCount];
            elemPos = varPos7 + varIntLen;
            for (i = 0; i < attachmentsCount; ++i) {
                obj.attachments[i] = ModelAttachment.deserialize(buf, elemPos);
                elemPos += ModelAttachment.computeBytesConsumed(buf, elemPos);
            }
        }
        if ((nullBits[1] & 4) != 0) {
            int varPos8 = offset + 91 + buf.getIntLE(offset + 75);
            int particlesCount = VarInt.peek(buf, varPos8);
            if (particlesCount < 0) {
                throw ProtocolException.negativeLength("Particles", particlesCount);
            }
            if (particlesCount > 4096000) {
                throw ProtocolException.arrayTooLong("Particles", particlesCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos8);
            if ((long)(varPos8 + varIntLen) + (long)particlesCount * 34L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("Particles", varPos8 + varIntLen + particlesCount * 34, buf.readableBytes());
            }
            obj.particles = new ModelParticle[particlesCount];
            elemPos = varPos8 + varIntLen;
            for (i = 0; i < particlesCount; ++i) {
                obj.particles[i] = ModelParticle.deserialize(buf, elemPos);
                elemPos += ModelParticle.computeBytesConsumed(buf, elemPos);
            }
        }
        if ((nullBits[1] & 8) != 0) {
            int varPos9 = offset + 91 + buf.getIntLE(offset + 79);
            int trailsCount = VarInt.peek(buf, varPos9);
            if (trailsCount < 0) {
                throw ProtocolException.negativeLength("Trails", trailsCount);
            }
            if (trailsCount > 4096000) {
                throw ProtocolException.arrayTooLong("Trails", trailsCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos9);
            if ((long)(varPos9 + varIntLen) + (long)trailsCount * 27L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("Trails", varPos9 + varIntLen + trailsCount * 27, buf.readableBytes());
            }
            obj.trails = new ModelTrail[trailsCount];
            elemPos = varPos9 + varIntLen;
            for (i = 0; i < trailsCount; ++i) {
                obj.trails[i] = ModelTrail.deserialize(buf, elemPos);
                elemPos += ModelTrail.computeBytesConsumed(buf, elemPos);
            }
        }
        if ((nullBits[1] & 0x10) != 0) {
            int varPos10 = offset + 91 + buf.getIntLE(offset + 83);
            int detailBoxesCount = VarInt.peek(buf, varPos10);
            if (detailBoxesCount < 0) {
                throw ProtocolException.negativeLength("DetailBoxes", detailBoxesCount);
            }
            if (detailBoxesCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("DetailBoxes", detailBoxesCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos10);
            obj.detailBoxes = new HashMap<String, DetailBox[]>(detailBoxesCount);
            dictPos = varPos10 + varIntLen;
            for (i = 0; i < detailBoxesCount; ++i) {
                keyLen = VarInt.peek(buf, dictPos);
                if (keyLen < 0) {
                    throw ProtocolException.negativeLength("key", keyLen);
                }
                if (keyLen > 4096000) {
                    throw ProtocolException.stringTooLong("key", keyLen, 4096000);
                }
                keyVarLen = VarInt.length(buf, dictPos);
                key = PacketIO.readVarString(buf, dictPos);
                int valLen = VarInt.peek(buf, dictPos += keyVarLen + keyLen);
                if (valLen < 0) {
                    throw ProtocolException.negativeLength("val", valLen);
                }
                if (valLen > 64) {
                    throw ProtocolException.arrayTooLong("val", valLen, 64);
                }
                int valVarLen = VarInt.length(buf, dictPos);
                if ((long)(dictPos + valVarLen) + (long)valLen * 37L > (long)buf.readableBytes()) {
                    throw ProtocolException.bufferTooSmall("val", dictPos + valVarLen + valLen * 37, buf.readableBytes());
                }
                dictPos += valVarLen;
                DetailBox[] val = new DetailBox[valLen];
                for (int valIdx = 0; valIdx < valLen; ++valIdx) {
                    val[valIdx] = DetailBox.deserialize(buf, dictPos);
                    dictPos += DetailBox.computeBytesConsumed(buf, dictPos);
                }
                if (obj.detailBoxes.put(key, val) == null) continue;
                throw ProtocolException.duplicateKey("detailBoxes", key);
            }
        }
        if ((nullBits[1] & 0x20) != 0) {
            int varPos11 = offset + 91 + buf.getIntLE(offset + 87);
            obj.phobiaModel = Model.deserialize(buf, varPos11);
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int arrLen;
        int sl;
        int i;
        int dictLen;
        int sl2;
        byte[] nullBits = PacketIO.readBytes(buf, offset, 2);
        int maxEnd = 91;
        if ((nullBits[0] & 4) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 43);
            int pos0 = offset + 91 + fieldOffset0;
            sl2 = VarInt.peek(buf, pos0);
            if ((pos0 += VarInt.length(buf, pos0) + sl2) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits[0] & 8) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 47);
            int pos1 = offset + 91 + fieldOffset1;
            sl2 = VarInt.peek(buf, pos1);
            if ((pos1 += VarInt.length(buf, pos1) + sl2) - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        if ((nullBits[0] & 0x10) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 51);
            int pos2 = offset + 91 + fieldOffset2;
            sl2 = VarInt.peek(buf, pos2);
            if ((pos2 += VarInt.length(buf, pos2) + sl2) - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        if ((nullBits[0] & 0x20) != 0) {
            int fieldOffset3 = buf.getIntLE(offset + 55);
            int pos3 = offset + 91 + fieldOffset3;
            sl2 = VarInt.peek(buf, pos3);
            if ((pos3 += VarInt.length(buf, pos3) + sl2) - offset > maxEnd) {
                maxEnd = pos3 - offset;
            }
        }
        if ((nullBits[0] & 0x40) != 0) {
            int fieldOffset4 = buf.getIntLE(offset + 59);
            int pos4 = offset + 91 + fieldOffset4;
            sl2 = VarInt.peek(buf, pos4);
            if ((pos4 += VarInt.length(buf, pos4) + sl2) - offset > maxEnd) {
                maxEnd = pos4 - offset;
            }
        }
        if ((nullBits[0] & 0x80) != 0) {
            int fieldOffset5 = buf.getIntLE(offset + 63);
            int pos5 = offset + 91 + fieldOffset5;
            if ((pos5 += CameraSettings.computeBytesConsumed(buf, pos5)) - offset > maxEnd) {
                maxEnd = pos5 - offset;
            }
        }
        if ((nullBits[1] & 1) != 0) {
            int fieldOffset6 = buf.getIntLE(offset + 67);
            int pos6 = offset + 91 + fieldOffset6;
            dictLen = VarInt.peek(buf, pos6);
            pos6 += VarInt.length(buf, pos6);
            for (i = 0; i < dictLen; ++i) {
                sl = VarInt.peek(buf, pos6);
                pos6 += VarInt.length(buf, pos6) + sl;
                pos6 += AnimationSet.computeBytesConsumed(buf, pos6);
            }
            if (pos6 - offset > maxEnd) {
                maxEnd = pos6 - offset;
            }
        }
        if ((nullBits[1] & 2) != 0) {
            int fieldOffset7 = buf.getIntLE(offset + 71);
            int pos7 = offset + 91 + fieldOffset7;
            arrLen = VarInt.peek(buf, pos7);
            pos7 += VarInt.length(buf, pos7);
            for (i = 0; i < arrLen; ++i) {
                pos7 += ModelAttachment.computeBytesConsumed(buf, pos7);
            }
            if (pos7 - offset > maxEnd) {
                maxEnd = pos7 - offset;
            }
        }
        if ((nullBits[1] & 4) != 0) {
            int fieldOffset8 = buf.getIntLE(offset + 75);
            int pos8 = offset + 91 + fieldOffset8;
            arrLen = VarInt.peek(buf, pos8);
            pos8 += VarInt.length(buf, pos8);
            for (i = 0; i < arrLen; ++i) {
                pos8 += ModelParticle.computeBytesConsumed(buf, pos8);
            }
            if (pos8 - offset > maxEnd) {
                maxEnd = pos8 - offset;
            }
        }
        if ((nullBits[1] & 8) != 0) {
            int fieldOffset9 = buf.getIntLE(offset + 79);
            int pos9 = offset + 91 + fieldOffset9;
            arrLen = VarInt.peek(buf, pos9);
            pos9 += VarInt.length(buf, pos9);
            for (i = 0; i < arrLen; ++i) {
                pos9 += ModelTrail.computeBytesConsumed(buf, pos9);
            }
            if (pos9 - offset > maxEnd) {
                maxEnd = pos9 - offset;
            }
        }
        if ((nullBits[1] & 0x10) != 0) {
            int fieldOffset10 = buf.getIntLE(offset + 83);
            int pos10 = offset + 91 + fieldOffset10;
            dictLen = VarInt.peek(buf, pos10);
            pos10 += VarInt.length(buf, pos10);
            for (i = 0; i < dictLen; ++i) {
                sl = VarInt.peek(buf, pos10);
                pos10 += VarInt.length(buf, pos10) + sl;
                int al = VarInt.peek(buf, pos10);
                pos10 += VarInt.length(buf, pos10);
                for (int j = 0; j < al; ++j) {
                    pos10 += DetailBox.computeBytesConsumed(buf, pos10);
                }
            }
            if (pos10 - offset > maxEnd) {
                maxEnd = pos10 - offset;
            }
        }
        if ((nullBits[1] & 0x20) != 0) {
            int fieldOffset11 = buf.getIntLE(offset + 87);
            int pos11 = offset + 91 + fieldOffset11;
            if ((pos11 += Model.computeBytesConsumed(buf, pos11)) - offset > maxEnd) {
                maxEnd = pos11 - offset;
            }
        }
        return maxEnd;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        byte[] nullBits = new byte[2];
        if (this.hitbox != null) {
            nullBits[0] = (byte)(nullBits[0] | 1);
        }
        if (this.light != null) {
            nullBits[0] = (byte)(nullBits[0] | 2);
        }
        if (this.assetId != null) {
            nullBits[0] = (byte)(nullBits[0] | 4);
        }
        if (this.path != null) {
            nullBits[0] = (byte)(nullBits[0] | 8);
        }
        if (this.texture != null) {
            nullBits[0] = (byte)(nullBits[0] | 0x10);
        }
        if (this.gradientSet != null) {
            nullBits[0] = (byte)(nullBits[0] | 0x20);
        }
        if (this.gradientId != null) {
            nullBits[0] = (byte)(nullBits[0] | 0x40);
        }
        if (this.camera != null) {
            nullBits[0] = (byte)(nullBits[0] | 0x80);
        }
        if (this.animationSets != null) {
            nullBits[1] = (byte)(nullBits[1] | 1);
        }
        if (this.attachments != null) {
            nullBits[1] = (byte)(nullBits[1] | 2);
        }
        if (this.particles != null) {
            nullBits[1] = (byte)(nullBits[1] | 4);
        }
        if (this.trails != null) {
            nullBits[1] = (byte)(nullBits[1] | 8);
        }
        if (this.detailBoxes != null) {
            nullBits[1] = (byte)(nullBits[1] | 0x10);
        }
        if (this.phobiaModel != null) {
            nullBits[1] = (byte)(nullBits[1] | 0x20);
        }
        buf.writeBytes(nullBits);
        buf.writeFloatLE(this.scale);
        buf.writeFloatLE(this.eyeHeight);
        buf.writeFloatLE(this.crouchOffset);
        if (this.hitbox != null) {
            this.hitbox.serialize(buf);
        } else {
            buf.writeZero(24);
        }
        if (this.light != null) {
            this.light.serialize(buf);
        } else {
            buf.writeZero(4);
        }
        buf.writeByte(this.phobia.getValue());
        int assetIdOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int pathOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int textureOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int gradientSetOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int gradientIdOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int cameraOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int animationSetsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int attachmentsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int particlesOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int trailsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int detailBoxesOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int phobiaModelOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.assetId != null) {
            buf.setIntLE(assetIdOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.assetId, 4096000);
        } else {
            buf.setIntLE(assetIdOffsetSlot, -1);
        }
        if (this.path != null) {
            buf.setIntLE(pathOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.path, 4096000);
        } else {
            buf.setIntLE(pathOffsetSlot, -1);
        }
        if (this.texture != null) {
            buf.setIntLE(textureOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.texture, 4096000);
        } else {
            buf.setIntLE(textureOffsetSlot, -1);
        }
        if (this.gradientSet != null) {
            buf.setIntLE(gradientSetOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.gradientSet, 4096000);
        } else {
            buf.setIntLE(gradientSetOffsetSlot, -1);
        }
        if (this.gradientId != null) {
            buf.setIntLE(gradientIdOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.gradientId, 4096000);
        } else {
            buf.setIntLE(gradientIdOffsetSlot, -1);
        }
        if (this.camera != null) {
            buf.setIntLE(cameraOffsetSlot, buf.writerIndex() - varBlockStart);
            this.camera.serialize(buf);
        } else {
            buf.setIntLE(cameraOffsetSlot, -1);
        }
        if (this.animationSets != null) {
            buf.setIntLE(animationSetsOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.animationSets.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("AnimationSets", this.animationSets.size(), 4096000);
            }
            VarInt.write(buf, this.animationSets.size());
            for (Map.Entry<String, AnimationSet> entry : this.animationSets.entrySet()) {
                PacketIO.writeVarString(buf, entry.getKey(), 4096000);
                entry.getValue().serialize(buf);
            }
        } else {
            buf.setIntLE(animationSetsOffsetSlot, -1);
        }
        if (this.attachments != null) {
            buf.setIntLE(attachmentsOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.attachments.length > 4096000) {
                throw ProtocolException.arrayTooLong("Attachments", this.attachments.length, 4096000);
            }
            VarInt.write(buf, this.attachments.length);
            for (ModelAttachment modelAttachment : this.attachments) {
                modelAttachment.serialize(buf);
            }
        } else {
            buf.setIntLE(attachmentsOffsetSlot, -1);
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
        if (this.detailBoxes != null) {
            buf.setIntLE(detailBoxesOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.detailBoxes.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("DetailBoxes", this.detailBoxes.size(), 4096000);
            }
            VarInt.write(buf, this.detailBoxes.size());
            for (Map.Entry entry : this.detailBoxes.entrySet()) {
                PacketIO.writeVarString(buf, (String)entry.getKey(), 4096000);
                VarInt.write(buf, ((DetailBox[])entry.getValue()).length);
                for (DetailBox arrItem : (DetailBox[])entry.getValue()) {
                    arrItem.serialize(buf);
                }
            }
        } else {
            buf.setIntLE(detailBoxesOffsetSlot, -1);
        }
        if (this.phobiaModel != null) {
            buf.setIntLE(phobiaModelOffsetSlot, buf.writerIndex() - varBlockStart);
            this.phobiaModel.serialize(buf);
        } else {
            buf.setIntLE(phobiaModelOffsetSlot, -1);
        }
    }

    public int computeSize() {
        int size = 91;
        if (this.assetId != null) {
            size += PacketIO.stringSize(this.assetId);
        }
        if (this.path != null) {
            size += PacketIO.stringSize(this.path);
        }
        if (this.texture != null) {
            size += PacketIO.stringSize(this.texture);
        }
        if (this.gradientSet != null) {
            size += PacketIO.stringSize(this.gradientSet);
        }
        if (this.gradientId != null) {
            size += PacketIO.stringSize(this.gradientId);
        }
        if (this.camera != null) {
            size += this.camera.computeSize();
        }
        if (this.animationSets != null) {
            int animationSetsSize = 0;
            for (Map.Entry entry : this.animationSets.entrySet()) {
                animationSetsSize += PacketIO.stringSize((String)entry.getKey()) + ((AnimationSet)entry.getValue()).computeSize();
            }
            size += VarInt.size(this.animationSets.size()) + animationSetsSize;
        }
        if (this.attachments != null) {
            int attachmentsSize = 0;
            for (ModelAttachment modelAttachment : this.attachments) {
                attachmentsSize += modelAttachment.computeSize();
            }
            size += VarInt.size(this.attachments.length) + attachmentsSize;
        }
        if (this.particles != null) {
            int particlesSize = 0;
            for (ModelParticle modelParticle : this.particles) {
                particlesSize += modelParticle.computeSize();
            }
            size += VarInt.size(this.particles.length) + particlesSize;
        }
        if (this.trails != null) {
            int trailsSize = 0;
            for (ModelTrail modelTrail : this.trails) {
                trailsSize += modelTrail.computeSize();
            }
            size += VarInt.size(this.trails.length) + trailsSize;
        }
        if (this.detailBoxes != null) {
            int detailBoxesSize = 0;
            for (Map.Entry entry : this.detailBoxes.entrySet()) {
                detailBoxesSize += PacketIO.stringSize((String)entry.getKey()) + VarInt.size(((DetailBox[])entry.getValue()).length) + ((DetailBox[])entry.getValue()).length * 37;
            }
            size += VarInt.size(this.detailBoxes.size()) + detailBoxesSize;
        }
        if (this.phobiaModel != null) {
            size += this.phobiaModel.computeSize();
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int i;
        int pos;
        if (buffer.readableBytes() - offset < 91) {
            return ValidationResult.error("Buffer too small: expected at least 91 bytes");
        }
        byte[] nullBits = PacketIO.readBytes(buffer, offset, 2);
        if ((nullBits[0] & 4) != 0) {
            int assetIdOffset = buffer.getIntLE(offset + 43);
            if (assetIdOffset < 0) {
                return ValidationResult.error("Invalid offset for AssetId");
            }
            pos = offset + 91 + assetIdOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for AssetId");
            }
            int assetIdLen = VarInt.peek(buffer, pos);
            if (assetIdLen < 0) {
                return ValidationResult.error("Invalid string length for AssetId");
            }
            if (assetIdLen > 4096000) {
                return ValidationResult.error("AssetId exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += assetIdLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading AssetId");
            }
        }
        if ((nullBits[0] & 8) != 0) {
            int pathOffset = buffer.getIntLE(offset + 47);
            if (pathOffset < 0) {
                return ValidationResult.error("Invalid offset for Path");
            }
            pos = offset + 91 + pathOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Path");
            }
            int pathLen = VarInt.peek(buffer, pos);
            if (pathLen < 0) {
                return ValidationResult.error("Invalid string length for Path");
            }
            if (pathLen > 4096000) {
                return ValidationResult.error("Path exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += pathLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading Path");
            }
        }
        if ((nullBits[0] & 0x10) != 0) {
            int textureOffset = buffer.getIntLE(offset + 51);
            if (textureOffset < 0) {
                return ValidationResult.error("Invalid offset for Texture");
            }
            pos = offset + 91 + textureOffset;
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
        if ((nullBits[0] & 0x20) != 0) {
            int gradientSetOffset = buffer.getIntLE(offset + 55);
            if (gradientSetOffset < 0) {
                return ValidationResult.error("Invalid offset for GradientSet");
            }
            pos = offset + 91 + gradientSetOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for GradientSet");
            }
            int gradientSetLen = VarInt.peek(buffer, pos);
            if (gradientSetLen < 0) {
                return ValidationResult.error("Invalid string length for GradientSet");
            }
            if (gradientSetLen > 4096000) {
                return ValidationResult.error("GradientSet exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += gradientSetLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading GradientSet");
            }
        }
        if ((nullBits[0] & 0x40) != 0) {
            int gradientIdOffset = buffer.getIntLE(offset + 59);
            if (gradientIdOffset < 0) {
                return ValidationResult.error("Invalid offset for GradientId");
            }
            pos = offset + 91 + gradientIdOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for GradientId");
            }
            int gradientIdLen = VarInt.peek(buffer, pos);
            if (gradientIdLen < 0) {
                return ValidationResult.error("Invalid string length for GradientId");
            }
            if (gradientIdLen > 4096000) {
                return ValidationResult.error("GradientId exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += gradientIdLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading GradientId");
            }
        }
        if ((nullBits[0] & 0x80) != 0) {
            int cameraOffset = buffer.getIntLE(offset + 63);
            if (cameraOffset < 0) {
                return ValidationResult.error("Invalid offset for Camera");
            }
            pos = offset + 91 + cameraOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Camera");
            }
            ValidationResult cameraResult = CameraSettings.validateStructure(buffer, pos);
            if (!cameraResult.isValid()) {
                return ValidationResult.error("Invalid Camera: " + cameraResult.error());
            }
            pos += CameraSettings.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits[1] & 1) != 0) {
            int animationSetsOffset = buffer.getIntLE(offset + 67);
            if (animationSetsOffset < 0) {
                return ValidationResult.error("Invalid offset for AnimationSets");
            }
            pos = offset + 91 + animationSetsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for AnimationSets");
            }
            int animationSetsCount = VarInt.peek(buffer, pos);
            if (animationSetsCount < 0) {
                return ValidationResult.error("Invalid dictionary count for AnimationSets");
            }
            if (animationSetsCount > 4096000) {
                return ValidationResult.error("AnimationSets exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < animationSetsCount; ++i) {
                int keyLen = VarInt.peek(buffer, pos);
                if (keyLen < 0) {
                    return ValidationResult.error("Invalid string length for key");
                }
                if (keyLen > 4096000) {
                    return ValidationResult.error("key exceeds max length 4096000");
                }
                pos += VarInt.length(buffer, pos);
                if ((pos += keyLen) > buffer.writerIndex()) {
                    return ValidationResult.error("Buffer overflow reading key");
                }
                pos += AnimationSet.computeBytesConsumed(buffer, pos);
            }
        }
        if ((nullBits[1] & 2) != 0) {
            int attachmentsOffset = buffer.getIntLE(offset + 71);
            if (attachmentsOffset < 0) {
                return ValidationResult.error("Invalid offset for Attachments");
            }
            pos = offset + 91 + attachmentsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Attachments");
            }
            int attachmentsCount = VarInt.peek(buffer, pos);
            if (attachmentsCount < 0) {
                return ValidationResult.error("Invalid array count for Attachments");
            }
            if (attachmentsCount > 4096000) {
                return ValidationResult.error("Attachments exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < attachmentsCount; ++i) {
                ValidationResult structResult = ModelAttachment.validateStructure(buffer, pos);
                if (!structResult.isValid()) {
                    return ValidationResult.error("Invalid ModelAttachment in Attachments[" + i + "]: " + structResult.error());
                }
                pos += ModelAttachment.computeBytesConsumed(buffer, pos);
            }
        }
        if ((nullBits[1] & 4) != 0) {
            int particlesOffset = buffer.getIntLE(offset + 75);
            if (particlesOffset < 0) {
                return ValidationResult.error("Invalid offset for Particles");
            }
            pos = offset + 91 + particlesOffset;
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
                ValidationResult structResult = ModelParticle.validateStructure(buffer, pos);
                if (!structResult.isValid()) {
                    return ValidationResult.error("Invalid ModelParticle in Particles[" + i + "]: " + structResult.error());
                }
                pos += ModelParticle.computeBytesConsumed(buffer, pos);
            }
        }
        if ((nullBits[1] & 8) != 0) {
            int trailsOffset = buffer.getIntLE(offset + 79);
            if (trailsOffset < 0) {
                return ValidationResult.error("Invalid offset for Trails");
            }
            pos = offset + 91 + trailsOffset;
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
                ValidationResult structResult = ModelTrail.validateStructure(buffer, pos);
                if (!structResult.isValid()) {
                    return ValidationResult.error("Invalid ModelTrail in Trails[" + i + "]: " + structResult.error());
                }
                pos += ModelTrail.computeBytesConsumed(buffer, pos);
            }
        }
        if ((nullBits[1] & 0x10) != 0) {
            int detailBoxesOffset = buffer.getIntLE(offset + 83);
            if (detailBoxesOffset < 0) {
                return ValidationResult.error("Invalid offset for DetailBoxes");
            }
            pos = offset + 91 + detailBoxesOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for DetailBoxes");
            }
            int detailBoxesCount = VarInt.peek(buffer, pos);
            if (detailBoxesCount < 0) {
                return ValidationResult.error("Invalid dictionary count for DetailBoxes");
            }
            if (detailBoxesCount > 4096000) {
                return ValidationResult.error("DetailBoxes exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < detailBoxesCount; ++i) {
                int keyLen = VarInt.peek(buffer, pos);
                if (keyLen < 0) {
                    return ValidationResult.error("Invalid string length for key");
                }
                if (keyLen > 4096000) {
                    return ValidationResult.error("key exceeds max length 4096000");
                }
                pos += VarInt.length(buffer, pos);
                if ((pos += keyLen) > buffer.writerIndex()) {
                    return ValidationResult.error("Buffer overflow reading key");
                }
                int valueArrCount = VarInt.peek(buffer, pos);
                if (valueArrCount < 0) {
                    return ValidationResult.error("Invalid array count for value");
                }
                pos += VarInt.length(buffer, pos);
                for (int valueArrIdx = 0; valueArrIdx < valueArrCount; ++valueArrIdx) {
                    pos += 37;
                }
            }
        }
        if ((nullBits[1] & 0x20) != 0) {
            int phobiaModelOffset = buffer.getIntLE(offset + 87);
            if (phobiaModelOffset < 0) {
                return ValidationResult.error("Invalid offset for PhobiaModel");
            }
            pos = offset + 91 + phobiaModelOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for PhobiaModel");
            }
            ValidationResult phobiaModelResult = Model.validateStructure(buffer, pos);
            if (!phobiaModelResult.isValid()) {
                return ValidationResult.error("Invalid PhobiaModel: " + phobiaModelResult.error());
            }
            pos += Model.computeBytesConsumed(buffer, pos);
        }
        return ValidationResult.OK;
    }

    public Model clone() {
        HashMap<String, AnimationSet> m;
        Model copy = new Model();
        copy.assetId = this.assetId;
        copy.path = this.path;
        copy.texture = this.texture;
        copy.gradientSet = this.gradientSet;
        copy.gradientId = this.gradientId;
        copy.camera = this.camera != null ? this.camera.clone() : null;
        copy.scale = this.scale;
        copy.eyeHeight = this.eyeHeight;
        copy.crouchOffset = this.crouchOffset;
        if (this.animationSets != null) {
            m = new HashMap<String, AnimationSet>();
            for (Map.Entry<String, AnimationSet> entry : this.animationSets.entrySet()) {
                m.put(entry.getKey(), entry.getValue().clone());
            }
            copy.animationSets = m;
        }
        copy.attachments = this.attachments != null ? (ModelAttachment[])Arrays.stream(this.attachments).map(e -> e.clone()).toArray(ModelAttachment[]::new) : null;
        copy.hitbox = this.hitbox != null ? this.hitbox.clone() : null;
        copy.particles = this.particles != null ? (ModelParticle[])Arrays.stream(this.particles).map(e -> e.clone()).toArray(ModelParticle[]::new) : null;
        copy.trails = this.trails != null ? (ModelTrail[])Arrays.stream(this.trails).map(e -> e.clone()).toArray(ModelTrail[]::new) : null;
        ColorLight colorLight = copy.light = this.light != null ? this.light.clone() : null;
        if (this.detailBoxes != null) {
            m = new HashMap();
            for (Map.Entry<String, AnimationSet> entry : this.detailBoxes.entrySet()) {
                m.put(entry.getKey(), (AnimationSet)((DetailBox[])Arrays.stream((DetailBox[])entry.getValue()).map(x -> x.clone()).toArray(DetailBox[]::new)));
            }
            copy.detailBoxes = m;
        }
        copy.phobia = this.phobia;
        copy.phobiaModel = this.phobiaModel != null ? this.phobiaModel.clone() : null;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Model)) {
            return false;
        }
        Model other = (Model)obj;
        return Objects.equals(this.assetId, other.assetId) && Objects.equals(this.path, other.path) && Objects.equals(this.texture, other.texture) && Objects.equals(this.gradientSet, other.gradientSet) && Objects.equals(this.gradientId, other.gradientId) && Objects.equals(this.camera, other.camera) && this.scale == other.scale && this.eyeHeight == other.eyeHeight && this.crouchOffset == other.crouchOffset && Objects.equals(this.animationSets, other.animationSets) && Arrays.equals(this.attachments, other.attachments) && Objects.equals(this.hitbox, other.hitbox) && Arrays.equals(this.particles, other.particles) && Arrays.equals(this.trails, other.trails) && Objects.equals(this.light, other.light) && Objects.equals(this.detailBoxes, other.detailBoxes) && Objects.equals((Object)this.phobia, (Object)other.phobia) && Objects.equals(this.phobiaModel, other.phobiaModel);
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + Objects.hashCode(this.assetId);
        result = 31 * result + Objects.hashCode(this.path);
        result = 31 * result + Objects.hashCode(this.texture);
        result = 31 * result + Objects.hashCode(this.gradientSet);
        result = 31 * result + Objects.hashCode(this.gradientId);
        result = 31 * result + Objects.hashCode(this.camera);
        result = 31 * result + Float.hashCode(this.scale);
        result = 31 * result + Float.hashCode(this.eyeHeight);
        result = 31 * result + Float.hashCode(this.crouchOffset);
        result = 31 * result + Objects.hashCode(this.animationSets);
        result = 31 * result + Arrays.hashCode(this.attachments);
        result = 31 * result + Objects.hashCode(this.hitbox);
        result = 31 * result + Arrays.hashCode(this.particles);
        result = 31 * result + Arrays.hashCode(this.trails);
        result = 31 * result + Objects.hashCode(this.light);
        result = 31 * result + Objects.hashCode(this.detailBoxes);
        result = 31 * result + Objects.hashCode((Object)this.phobia);
        result = 31 * result + Objects.hashCode(this.phobiaModel);
        return result;
    }
}

