/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.Bench;
import com.hypixel.hytale.protocol.BlockFaceSupport;
import com.hypixel.hytale.protocol.BlockFlags;
import com.hypixel.hytale.protocol.BlockGathering;
import com.hypixel.hytale.protocol.BlockMaterial;
import com.hypixel.hytale.protocol.BlockMovementSettings;
import com.hypixel.hytale.protocol.BlockNeighbor;
import com.hypixel.hytale.protocol.BlockPlacementSettings;
import com.hypixel.hytale.protocol.BlockSupportsRequiredForType;
import com.hypixel.hytale.protocol.BlockTextures;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.protocol.ColorLight;
import com.hypixel.hytale.protocol.ConnectedBlockRuleSet;
import com.hypixel.hytale.protocol.DrawType;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.ModelDisplay;
import com.hypixel.hytale.protocol.ModelParticle;
import com.hypixel.hytale.protocol.ModelTexture;
import com.hypixel.hytale.protocol.Opacity;
import com.hypixel.hytale.protocol.RailConfig;
import com.hypixel.hytale.protocol.RandomRotation;
import com.hypixel.hytale.protocol.RequiredBlockFaceSupport;
import com.hypixel.hytale.protocol.Rotation;
import com.hypixel.hytale.protocol.ShaderType;
import com.hypixel.hytale.protocol.ShadingMode;
import com.hypixel.hytale.protocol.Tint;
import com.hypixel.hytale.protocol.VariantRotation;
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

public class BlockType {
    public static final int NULLABLE_BIT_FIELD_SIZE = 4;
    public static final int FIXED_BLOCK_SIZE = 163;
    public static final int VARIABLE_FIELD_COUNT = 24;
    public static final int VARIABLE_BLOCK_START = 259;
    public static final int MAX_SIZE = 0x64000000;
    @Nullable
    public String item;
    @Nullable
    public String name;
    public boolean unknown;
    @Nonnull
    public DrawType drawType = DrawType.Empty;
    @Nonnull
    public BlockMaterial material = BlockMaterial.Empty;
    @Nonnull
    public Opacity opacity = Opacity.Solid;
    @Nullable
    public ShaderType[] shaderEffect;
    public int hitbox;
    public int interactionHitbox;
    @Nullable
    public String model;
    @Nullable
    public ModelTexture[] modelTexture;
    public float modelScale;
    @Nullable
    public String modelAnimation;
    public boolean looping;
    public int maxSupportDistance;
    @Nonnull
    public BlockSupportsRequiredForType blockSupportsRequiredFor = BlockSupportsRequiredForType.Any;
    @Nullable
    public Map<BlockNeighbor, RequiredBlockFaceSupport[]> support;
    @Nullable
    public Map<BlockNeighbor, BlockFaceSupport[]> supporting;
    public boolean requiresAlphaBlending;
    @Nullable
    public BlockTextures[] cubeTextures;
    @Nullable
    public String cubeSideMaskTexture;
    @Nonnull
    public ShadingMode cubeShadingMode = ShadingMode.Standard;
    @Nonnull
    public RandomRotation randomRotation = RandomRotation.None;
    @Nonnull
    public VariantRotation variantRotation = VariantRotation.None;
    @Nonnull
    public Rotation rotationYawPlacementOffset = Rotation.None;
    public int blockSoundSetIndex;
    public int ambientSoundEventIndex;
    @Nullable
    public ModelParticle[] particles;
    @Nullable
    public String blockParticleSetId;
    @Nullable
    public String blockBreakingDecalId;
    @Nullable
    public Color particleColor;
    @Nullable
    public ColorLight light;
    @Nullable
    public Tint tint;
    @Nullable
    public Tint biomeTint;
    public int group;
    @Nullable
    public String transitionTexture;
    @Nullable
    public int[] transitionToGroups;
    @Nullable
    public BlockMovementSettings movementSettings;
    @Nullable
    public BlockFlags flags;
    @Nullable
    public String interactionHint;
    @Nullable
    public BlockGathering gathering;
    @Nullable
    public BlockPlacementSettings placementSettings;
    @Nullable
    public ModelDisplay display;
    @Nullable
    public RailConfig rail;
    public boolean ignoreSupportWhenPlaced;
    @Nullable
    public Map<InteractionType, Integer> interactions;
    @Nullable
    public Map<String, Integer> states;
    public int transitionToTag;
    @Nullable
    public int[] tagIndexes;
    @Nullable
    public Bench bench;
    @Nullable
    public ConnectedBlockRuleSet connectedBlockRuleSet;

    public BlockType() {
    }

    public BlockType(@Nullable String item, @Nullable String name, boolean unknown, @Nonnull DrawType drawType, @Nonnull BlockMaterial material, @Nonnull Opacity opacity, @Nullable ShaderType[] shaderEffect, int hitbox, int interactionHitbox, @Nullable String model, @Nullable ModelTexture[] modelTexture, float modelScale, @Nullable String modelAnimation, boolean looping, int maxSupportDistance, @Nonnull BlockSupportsRequiredForType blockSupportsRequiredFor, @Nullable Map<BlockNeighbor, RequiredBlockFaceSupport[]> support, @Nullable Map<BlockNeighbor, BlockFaceSupport[]> supporting, boolean requiresAlphaBlending, @Nullable BlockTextures[] cubeTextures, @Nullable String cubeSideMaskTexture, @Nonnull ShadingMode cubeShadingMode, @Nonnull RandomRotation randomRotation, @Nonnull VariantRotation variantRotation, @Nonnull Rotation rotationYawPlacementOffset, int blockSoundSetIndex, int ambientSoundEventIndex, @Nullable ModelParticle[] particles, @Nullable String blockParticleSetId, @Nullable String blockBreakingDecalId, @Nullable Color particleColor, @Nullable ColorLight light, @Nullable Tint tint, @Nullable Tint biomeTint, int group, @Nullable String transitionTexture, @Nullable int[] transitionToGroups, @Nullable BlockMovementSettings movementSettings, @Nullable BlockFlags flags, @Nullable String interactionHint, @Nullable BlockGathering gathering, @Nullable BlockPlacementSettings placementSettings, @Nullable ModelDisplay display, @Nullable RailConfig rail, boolean ignoreSupportWhenPlaced, @Nullable Map<InteractionType, Integer> interactions, @Nullable Map<String, Integer> states, int transitionToTag, @Nullable int[] tagIndexes, @Nullable Bench bench, @Nullable ConnectedBlockRuleSet connectedBlockRuleSet) {
        this.item = item;
        this.name = name;
        this.unknown = unknown;
        this.drawType = drawType;
        this.material = material;
        this.opacity = opacity;
        this.shaderEffect = shaderEffect;
        this.hitbox = hitbox;
        this.interactionHitbox = interactionHitbox;
        this.model = model;
        this.modelTexture = modelTexture;
        this.modelScale = modelScale;
        this.modelAnimation = modelAnimation;
        this.looping = looping;
        this.maxSupportDistance = maxSupportDistance;
        this.blockSupportsRequiredFor = blockSupportsRequiredFor;
        this.support = support;
        this.supporting = supporting;
        this.requiresAlphaBlending = requiresAlphaBlending;
        this.cubeTextures = cubeTextures;
        this.cubeSideMaskTexture = cubeSideMaskTexture;
        this.cubeShadingMode = cubeShadingMode;
        this.randomRotation = randomRotation;
        this.variantRotation = variantRotation;
        this.rotationYawPlacementOffset = rotationYawPlacementOffset;
        this.blockSoundSetIndex = blockSoundSetIndex;
        this.ambientSoundEventIndex = ambientSoundEventIndex;
        this.particles = particles;
        this.blockParticleSetId = blockParticleSetId;
        this.blockBreakingDecalId = blockBreakingDecalId;
        this.particleColor = particleColor;
        this.light = light;
        this.tint = tint;
        this.biomeTint = biomeTint;
        this.group = group;
        this.transitionTexture = transitionTexture;
        this.transitionToGroups = transitionToGroups;
        this.movementSettings = movementSettings;
        this.flags = flags;
        this.interactionHint = interactionHint;
        this.gathering = gathering;
        this.placementSettings = placementSettings;
        this.display = display;
        this.rail = rail;
        this.ignoreSupportWhenPlaced = ignoreSupportWhenPlaced;
        this.interactions = interactions;
        this.states = states;
        this.transitionToTag = transitionToTag;
        this.tagIndexes = tagIndexes;
        this.bench = bench;
        this.connectedBlockRuleSet = connectedBlockRuleSet;
    }

    public BlockType(@Nonnull BlockType other) {
        this.item = other.item;
        this.name = other.name;
        this.unknown = other.unknown;
        this.drawType = other.drawType;
        this.material = other.material;
        this.opacity = other.opacity;
        this.shaderEffect = other.shaderEffect;
        this.hitbox = other.hitbox;
        this.interactionHitbox = other.interactionHitbox;
        this.model = other.model;
        this.modelTexture = other.modelTexture;
        this.modelScale = other.modelScale;
        this.modelAnimation = other.modelAnimation;
        this.looping = other.looping;
        this.maxSupportDistance = other.maxSupportDistance;
        this.blockSupportsRequiredFor = other.blockSupportsRequiredFor;
        this.support = other.support;
        this.supporting = other.supporting;
        this.requiresAlphaBlending = other.requiresAlphaBlending;
        this.cubeTextures = other.cubeTextures;
        this.cubeSideMaskTexture = other.cubeSideMaskTexture;
        this.cubeShadingMode = other.cubeShadingMode;
        this.randomRotation = other.randomRotation;
        this.variantRotation = other.variantRotation;
        this.rotationYawPlacementOffset = other.rotationYawPlacementOffset;
        this.blockSoundSetIndex = other.blockSoundSetIndex;
        this.ambientSoundEventIndex = other.ambientSoundEventIndex;
        this.particles = other.particles;
        this.blockParticleSetId = other.blockParticleSetId;
        this.blockBreakingDecalId = other.blockBreakingDecalId;
        this.particleColor = other.particleColor;
        this.light = other.light;
        this.tint = other.tint;
        this.biomeTint = other.biomeTint;
        this.group = other.group;
        this.transitionTexture = other.transitionTexture;
        this.transitionToGroups = other.transitionToGroups;
        this.movementSettings = other.movementSettings;
        this.flags = other.flags;
        this.interactionHint = other.interactionHint;
        this.gathering = other.gathering;
        this.placementSettings = other.placementSettings;
        this.display = other.display;
        this.rail = other.rail;
        this.ignoreSupportWhenPlaced = other.ignoreSupportWhenPlaced;
        this.interactions = other.interactions;
        this.states = other.states;
        this.transitionToTag = other.transitionToTag;
        this.tagIndexes = other.tagIndexes;
        this.bench = other.bench;
        this.connectedBlockRuleSet = other.connectedBlockRuleSet;
    }

    @Nonnull
    public static BlockType deserialize(@Nonnull ByteBuf buf, int offset) {
        int i;
        int valIdx;
        Object[] val;
        int valVarLen;
        int valLen;
        Enum key;
        int dictPos;
        int i2;
        int elemPos;
        int varIntLen;
        BlockType obj = new BlockType();
        byte[] nullBits = PacketIO.readBytes(buf, offset, 4);
        obj.unknown = buf.getByte(offset + 4) != 0;
        obj.drawType = DrawType.fromValue(buf.getByte(offset + 5));
        obj.material = BlockMaterial.fromValue(buf.getByte(offset + 6));
        obj.opacity = Opacity.fromValue(buf.getByte(offset + 7));
        obj.hitbox = buf.getIntLE(offset + 8);
        obj.interactionHitbox = buf.getIntLE(offset + 12);
        obj.modelScale = buf.getFloatLE(offset + 16);
        obj.looping = buf.getByte(offset + 20) != 0;
        obj.maxSupportDistance = buf.getIntLE(offset + 21);
        obj.blockSupportsRequiredFor = BlockSupportsRequiredForType.fromValue(buf.getByte(offset + 25));
        obj.requiresAlphaBlending = buf.getByte(offset + 26) != 0;
        obj.cubeShadingMode = ShadingMode.fromValue(buf.getByte(offset + 27));
        obj.randomRotation = RandomRotation.fromValue(buf.getByte(offset + 28));
        obj.variantRotation = VariantRotation.fromValue(buf.getByte(offset + 29));
        obj.rotationYawPlacementOffset = Rotation.fromValue(buf.getByte(offset + 30));
        obj.blockSoundSetIndex = buf.getIntLE(offset + 31);
        obj.ambientSoundEventIndex = buf.getIntLE(offset + 35);
        if ((nullBits[0] & 1) != 0) {
            obj.particleColor = Color.deserialize(buf, offset + 39);
        }
        if ((nullBits[0] & 2) != 0) {
            obj.light = ColorLight.deserialize(buf, offset + 42);
        }
        if ((nullBits[0] & 4) != 0) {
            obj.tint = Tint.deserialize(buf, offset + 46);
        }
        if ((nullBits[0] & 8) != 0) {
            obj.biomeTint = Tint.deserialize(buf, offset + 70);
        }
        obj.group = buf.getIntLE(offset + 94);
        if ((nullBits[0] & 0x10) != 0) {
            obj.movementSettings = BlockMovementSettings.deserialize(buf, offset + 98);
        }
        if ((nullBits[0] & 0x20) != 0) {
            obj.flags = BlockFlags.deserialize(buf, offset + 140);
        }
        if ((nullBits[0] & 0x40) != 0) {
            obj.placementSettings = BlockPlacementSettings.deserialize(buf, offset + 142);
        }
        obj.ignoreSupportWhenPlaced = buf.getByte(offset + 158) != 0;
        obj.transitionToTag = buf.getIntLE(offset + 159);
        if ((nullBits[0] & 0x80) != 0) {
            int varPos0 = offset + 259 + buf.getIntLE(offset + 163);
            int itemLen = VarInt.peek(buf, varPos0);
            if (itemLen < 0) {
                throw ProtocolException.negativeLength("Item", itemLen);
            }
            if (itemLen > 4096000) {
                throw ProtocolException.stringTooLong("Item", itemLen, 4096000);
            }
            obj.item = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
        }
        if ((nullBits[1] & 1) != 0) {
            int varPos1 = offset + 259 + buf.getIntLE(offset + 167);
            int nameLen = VarInt.peek(buf, varPos1);
            if (nameLen < 0) {
                throw ProtocolException.negativeLength("Name", nameLen);
            }
            if (nameLen > 4096000) {
                throw ProtocolException.stringTooLong("Name", nameLen, 4096000);
            }
            obj.name = PacketIO.readVarString(buf, varPos1, PacketIO.UTF8);
        }
        if ((nullBits[1] & 2) != 0) {
            int varPos2 = offset + 259 + buf.getIntLE(offset + 171);
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
            for (i2 = 0; i2 < shaderEffectCount; ++i2) {
                obj.shaderEffect[i2] = ShaderType.fromValue(buf.getByte(elemPos));
                ++elemPos;
            }
        }
        if ((nullBits[1] & 4) != 0) {
            int varPos3 = offset + 259 + buf.getIntLE(offset + 175);
            int modelLen = VarInt.peek(buf, varPos3);
            if (modelLen < 0) {
                throw ProtocolException.negativeLength("Model", modelLen);
            }
            if (modelLen > 4096000) {
                throw ProtocolException.stringTooLong("Model", modelLen, 4096000);
            }
            obj.model = PacketIO.readVarString(buf, varPos3, PacketIO.UTF8);
        }
        if ((nullBits[1] & 8) != 0) {
            int varPos4 = offset + 259 + buf.getIntLE(offset + 179);
            int modelTextureCount = VarInt.peek(buf, varPos4);
            if (modelTextureCount < 0) {
                throw ProtocolException.negativeLength("ModelTexture", modelTextureCount);
            }
            if (modelTextureCount > 4096000) {
                throw ProtocolException.arrayTooLong("ModelTexture", modelTextureCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos4);
            if ((long)(varPos4 + varIntLen) + (long)modelTextureCount * 5L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("ModelTexture", varPos4 + varIntLen + modelTextureCount * 5, buf.readableBytes());
            }
            obj.modelTexture = new ModelTexture[modelTextureCount];
            elemPos = varPos4 + varIntLen;
            for (i2 = 0; i2 < modelTextureCount; ++i2) {
                obj.modelTexture[i2] = ModelTexture.deserialize(buf, elemPos);
                elemPos += ModelTexture.computeBytesConsumed(buf, elemPos);
            }
        }
        if ((nullBits[1] & 0x10) != 0) {
            int varPos5 = offset + 259 + buf.getIntLE(offset + 183);
            int modelAnimationLen = VarInt.peek(buf, varPos5);
            if (modelAnimationLen < 0) {
                throw ProtocolException.negativeLength("ModelAnimation", modelAnimationLen);
            }
            if (modelAnimationLen > 4096000) {
                throw ProtocolException.stringTooLong("ModelAnimation", modelAnimationLen, 4096000);
            }
            obj.modelAnimation = PacketIO.readVarString(buf, varPos5, PacketIO.UTF8);
        }
        if ((nullBits[1] & 0x20) != 0) {
            int varPos6 = offset + 259 + buf.getIntLE(offset + 187);
            int supportCount = VarInt.peek(buf, varPos6);
            if (supportCount < 0) {
                throw ProtocolException.negativeLength("Support", supportCount);
            }
            if (supportCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Support", supportCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos6);
            obj.support = new HashMap<BlockNeighbor, RequiredBlockFaceSupport[]>(supportCount);
            dictPos = varPos6 + varIntLen;
            for (i2 = 0; i2 < supportCount; ++i2) {
                key = BlockNeighbor.fromValue(buf.getByte(dictPos));
                if ((valLen = VarInt.peek(buf, ++dictPos)) < 0) {
                    throw ProtocolException.negativeLength("val", valLen);
                }
                if (valLen > 64) {
                    throw ProtocolException.arrayTooLong("val", valLen, 64);
                }
                valVarLen = VarInt.length(buf, dictPos);
                if ((long)(dictPos + valVarLen) + (long)valLen * 17L > (long)buf.readableBytes()) {
                    throw ProtocolException.bufferTooSmall("val", dictPos + valVarLen + valLen * 17, buf.readableBytes());
                }
                dictPos += valVarLen;
                val = new RequiredBlockFaceSupport[valLen];
                for (valIdx = 0; valIdx < valLen; ++valIdx) {
                    val[valIdx] = RequiredBlockFaceSupport.deserialize(buf, dictPos);
                    dictPos += RequiredBlockFaceSupport.computeBytesConsumed(buf, dictPos);
                }
                if (obj.support.put((BlockNeighbor)key, (RequiredBlockFaceSupport[])val) == null) continue;
                throw ProtocolException.duplicateKey("support", key);
            }
        }
        if ((nullBits[1] & 0x40) != 0) {
            int varPos7 = offset + 259 + buf.getIntLE(offset + 191);
            int supportingCount = VarInt.peek(buf, varPos7);
            if (supportingCount < 0) {
                throw ProtocolException.negativeLength("Supporting", supportingCount);
            }
            if (supportingCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Supporting", supportingCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos7);
            obj.supporting = new HashMap<BlockNeighbor, BlockFaceSupport[]>(supportingCount);
            dictPos = varPos7 + varIntLen;
            for (i2 = 0; i2 < supportingCount; ++i2) {
                key = BlockNeighbor.fromValue(buf.getByte(dictPos));
                if ((valLen = VarInt.peek(buf, ++dictPos)) < 0) {
                    throw ProtocolException.negativeLength("val", valLen);
                }
                if (valLen > 64) {
                    throw ProtocolException.arrayTooLong("val", valLen, 64);
                }
                valVarLen = VarInt.length(buf, dictPos);
                if ((long)(dictPos + valVarLen) + (long)valLen * 1L > (long)buf.readableBytes()) {
                    throw ProtocolException.bufferTooSmall("val", dictPos + valVarLen + valLen * 1, buf.readableBytes());
                }
                dictPos += valVarLen;
                val = new BlockFaceSupport[valLen];
                for (valIdx = 0; valIdx < valLen; ++valIdx) {
                    val[valIdx] = BlockFaceSupport.deserialize(buf, dictPos);
                    dictPos += BlockFaceSupport.computeBytesConsumed(buf, dictPos);
                }
                if (obj.supporting.put((BlockNeighbor)key, (BlockFaceSupport[])val) == null) continue;
                throw ProtocolException.duplicateKey("supporting", key);
            }
        }
        if ((nullBits[1] & 0x80) != 0) {
            int varPos8 = offset + 259 + buf.getIntLE(offset + 195);
            int cubeTexturesCount = VarInt.peek(buf, varPos8);
            if (cubeTexturesCount < 0) {
                throw ProtocolException.negativeLength("CubeTextures", cubeTexturesCount);
            }
            if (cubeTexturesCount > 4096000) {
                throw ProtocolException.arrayTooLong("CubeTextures", cubeTexturesCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos8);
            if ((long)(varPos8 + varIntLen) + (long)cubeTexturesCount * 5L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("CubeTextures", varPos8 + varIntLen + cubeTexturesCount * 5, buf.readableBytes());
            }
            obj.cubeTextures = new BlockTextures[cubeTexturesCount];
            elemPos = varPos8 + varIntLen;
            for (i2 = 0; i2 < cubeTexturesCount; ++i2) {
                obj.cubeTextures[i2] = BlockTextures.deserialize(buf, elemPos);
                elemPos += BlockTextures.computeBytesConsumed(buf, elemPos);
            }
        }
        if ((nullBits[2] & 1) != 0) {
            int varPos9 = offset + 259 + buf.getIntLE(offset + 199);
            int cubeSideMaskTextureLen = VarInt.peek(buf, varPos9);
            if (cubeSideMaskTextureLen < 0) {
                throw ProtocolException.negativeLength("CubeSideMaskTexture", cubeSideMaskTextureLen);
            }
            if (cubeSideMaskTextureLen > 4096000) {
                throw ProtocolException.stringTooLong("CubeSideMaskTexture", cubeSideMaskTextureLen, 4096000);
            }
            obj.cubeSideMaskTexture = PacketIO.readVarString(buf, varPos9, PacketIO.UTF8);
        }
        if ((nullBits[2] & 2) != 0) {
            int varPos10 = offset + 259 + buf.getIntLE(offset + 203);
            int particlesCount = VarInt.peek(buf, varPos10);
            if (particlesCount < 0) {
                throw ProtocolException.negativeLength("Particles", particlesCount);
            }
            if (particlesCount > 4096000) {
                throw ProtocolException.arrayTooLong("Particles", particlesCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos10);
            if ((long)(varPos10 + varIntLen) + (long)particlesCount * 34L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("Particles", varPos10 + varIntLen + particlesCount * 34, buf.readableBytes());
            }
            obj.particles = new ModelParticle[particlesCount];
            elemPos = varPos10 + varIntLen;
            for (i2 = 0; i2 < particlesCount; ++i2) {
                obj.particles[i2] = ModelParticle.deserialize(buf, elemPos);
                elemPos += ModelParticle.computeBytesConsumed(buf, elemPos);
            }
        }
        if ((nullBits[2] & 4) != 0) {
            int varPos11 = offset + 259 + buf.getIntLE(offset + 207);
            int blockParticleSetIdLen = VarInt.peek(buf, varPos11);
            if (blockParticleSetIdLen < 0) {
                throw ProtocolException.negativeLength("BlockParticleSetId", blockParticleSetIdLen);
            }
            if (blockParticleSetIdLen > 4096000) {
                throw ProtocolException.stringTooLong("BlockParticleSetId", blockParticleSetIdLen, 4096000);
            }
            obj.blockParticleSetId = PacketIO.readVarString(buf, varPos11, PacketIO.UTF8);
        }
        if ((nullBits[2] & 8) != 0) {
            int varPos12 = offset + 259 + buf.getIntLE(offset + 211);
            int blockBreakingDecalIdLen = VarInt.peek(buf, varPos12);
            if (blockBreakingDecalIdLen < 0) {
                throw ProtocolException.negativeLength("BlockBreakingDecalId", blockBreakingDecalIdLen);
            }
            if (blockBreakingDecalIdLen > 4096000) {
                throw ProtocolException.stringTooLong("BlockBreakingDecalId", blockBreakingDecalIdLen, 4096000);
            }
            obj.blockBreakingDecalId = PacketIO.readVarString(buf, varPos12, PacketIO.UTF8);
        }
        if ((nullBits[2] & 0x10) != 0) {
            int varPos13 = offset + 259 + buf.getIntLE(offset + 215);
            int transitionTextureLen = VarInt.peek(buf, varPos13);
            if (transitionTextureLen < 0) {
                throw ProtocolException.negativeLength("TransitionTexture", transitionTextureLen);
            }
            if (transitionTextureLen > 4096000) {
                throw ProtocolException.stringTooLong("TransitionTexture", transitionTextureLen, 4096000);
            }
            obj.transitionTexture = PacketIO.readVarString(buf, varPos13, PacketIO.UTF8);
        }
        if ((nullBits[2] & 0x20) != 0) {
            int varPos14 = offset + 259 + buf.getIntLE(offset + 219);
            int transitionToGroupsCount = VarInt.peek(buf, varPos14);
            if (transitionToGroupsCount < 0) {
                throw ProtocolException.negativeLength("TransitionToGroups", transitionToGroupsCount);
            }
            if (transitionToGroupsCount > 4096000) {
                throw ProtocolException.arrayTooLong("TransitionToGroups", transitionToGroupsCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos14);
            if ((long)(varPos14 + varIntLen) + (long)transitionToGroupsCount * 4L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("TransitionToGroups", varPos14 + varIntLen + transitionToGroupsCount * 4, buf.readableBytes());
            }
            obj.transitionToGroups = new int[transitionToGroupsCount];
            for (i = 0; i < transitionToGroupsCount; ++i) {
                obj.transitionToGroups[i] = buf.getIntLE(varPos14 + varIntLen + i * 4);
            }
        }
        if ((nullBits[2] & 0x40) != 0) {
            int varPos15 = offset + 259 + buf.getIntLE(offset + 223);
            int interactionHintLen = VarInt.peek(buf, varPos15);
            if (interactionHintLen < 0) {
                throw ProtocolException.negativeLength("InteractionHint", interactionHintLen);
            }
            if (interactionHintLen > 4096000) {
                throw ProtocolException.stringTooLong("InteractionHint", interactionHintLen, 4096000);
            }
            obj.interactionHint = PacketIO.readVarString(buf, varPos15, PacketIO.UTF8);
        }
        if ((nullBits[2] & 0x80) != 0) {
            int varPos16 = offset + 259 + buf.getIntLE(offset + 227);
            obj.gathering = BlockGathering.deserialize(buf, varPos16);
        }
        if ((nullBits[3] & 1) != 0) {
            int varPos17 = offset + 259 + buf.getIntLE(offset + 231);
            obj.display = ModelDisplay.deserialize(buf, varPos17);
        }
        if ((nullBits[3] & 2) != 0) {
            int varPos18 = offset + 259 + buf.getIntLE(offset + 235);
            obj.rail = RailConfig.deserialize(buf, varPos18);
        }
        if ((nullBits[3] & 4) != 0) {
            int varPos19 = offset + 259 + buf.getIntLE(offset + 239);
            int interactionsCount = VarInt.peek(buf, varPos19);
            if (interactionsCount < 0) {
                throw ProtocolException.negativeLength("Interactions", interactionsCount);
            }
            if (interactionsCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Interactions", interactionsCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos19);
            obj.interactions = new HashMap<InteractionType, Integer>(interactionsCount);
            dictPos = varPos19 + varIntLen;
            for (i2 = 0; i2 < interactionsCount; ++i2) {
                key = InteractionType.fromValue(buf.getByte(dictPos));
                int val2 = buf.getIntLE(++dictPos);
                dictPos += 4;
                if (obj.interactions.put((InteractionType)key, val2) == null) continue;
                throw ProtocolException.duplicateKey("interactions", key);
            }
        }
        if ((nullBits[3] & 8) != 0) {
            int varPos20 = offset + 259 + buf.getIntLE(offset + 243);
            int statesCount = VarInt.peek(buf, varPos20);
            if (statesCount < 0) {
                throw ProtocolException.negativeLength("States", statesCount);
            }
            if (statesCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("States", statesCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos20);
            obj.states = new HashMap<String, Integer>(statesCount);
            dictPos = varPos20 + varIntLen;
            for (i2 = 0; i2 < statesCount; ++i2) {
                int keyLen = VarInt.peek(buf, dictPos);
                if (keyLen < 0) {
                    throw ProtocolException.negativeLength("key", keyLen);
                }
                if (keyLen > 4096000) {
                    throw ProtocolException.stringTooLong("key", keyLen, 4096000);
                }
                int keyVarLen = VarInt.length(buf, dictPos);
                String key2 = PacketIO.readVarString(buf, dictPos);
                int val3 = buf.getIntLE(dictPos += keyVarLen + keyLen);
                dictPos += 4;
                if (obj.states.put(key2, val3) == null) continue;
                throw ProtocolException.duplicateKey("states", key2);
            }
        }
        if ((nullBits[3] & 0x10) != 0) {
            int varPos21 = offset + 259 + buf.getIntLE(offset + 247);
            int tagIndexesCount = VarInt.peek(buf, varPos21);
            if (tagIndexesCount < 0) {
                throw ProtocolException.negativeLength("TagIndexes", tagIndexesCount);
            }
            if (tagIndexesCount > 4096000) {
                throw ProtocolException.arrayTooLong("TagIndexes", tagIndexesCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos21);
            if ((long)(varPos21 + varIntLen) + (long)tagIndexesCount * 4L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("TagIndexes", varPos21 + varIntLen + tagIndexesCount * 4, buf.readableBytes());
            }
            obj.tagIndexes = new int[tagIndexesCount];
            for (i = 0; i < tagIndexesCount; ++i) {
                obj.tagIndexes[i] = buf.getIntLE(varPos21 + varIntLen + i * 4);
            }
        }
        if ((nullBits[3] & 0x20) != 0) {
            int varPos22 = offset + 259 + buf.getIntLE(offset + 251);
            obj.bench = Bench.deserialize(buf, varPos22);
        }
        if ((nullBits[3] & 0x40) != 0) {
            int varPos23 = offset + 259 + buf.getIntLE(offset + 255);
            obj.connectedBlockRuleSet = ConnectedBlockRuleSet.deserialize(buf, varPos23);
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int j;
        int al;
        int dictLen;
        int i;
        int arrLen;
        int sl;
        byte[] nullBits = PacketIO.readBytes(buf, offset, 4);
        int maxEnd = 259;
        if ((nullBits[0] & 0x80) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 163);
            int pos0 = offset + 259 + fieldOffset0;
            sl = VarInt.peek(buf, pos0);
            if ((pos0 += VarInt.length(buf, pos0) + sl) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits[1] & 1) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 167);
            int pos1 = offset + 259 + fieldOffset1;
            sl = VarInt.peek(buf, pos1);
            if ((pos1 += VarInt.length(buf, pos1) + sl) - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        if ((nullBits[1] & 2) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 171);
            int pos2 = offset + 259 + fieldOffset2;
            arrLen = VarInt.peek(buf, pos2);
            if ((pos2 += VarInt.length(buf, pos2) + arrLen * 1) - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        if ((nullBits[1] & 4) != 0) {
            int fieldOffset3 = buf.getIntLE(offset + 175);
            int pos3 = offset + 259 + fieldOffset3;
            sl = VarInt.peek(buf, pos3);
            if ((pos3 += VarInt.length(buf, pos3) + sl) - offset > maxEnd) {
                maxEnd = pos3 - offset;
            }
        }
        if ((nullBits[1] & 8) != 0) {
            int fieldOffset4 = buf.getIntLE(offset + 179);
            int pos4 = offset + 259 + fieldOffset4;
            arrLen = VarInt.peek(buf, pos4);
            pos4 += VarInt.length(buf, pos4);
            for (i = 0; i < arrLen; ++i) {
                pos4 += ModelTexture.computeBytesConsumed(buf, pos4);
            }
            if (pos4 - offset > maxEnd) {
                maxEnd = pos4 - offset;
            }
        }
        if ((nullBits[1] & 0x10) != 0) {
            int fieldOffset5 = buf.getIntLE(offset + 183);
            int pos5 = offset + 259 + fieldOffset5;
            sl = VarInt.peek(buf, pos5);
            if ((pos5 += VarInt.length(buf, pos5) + sl) - offset > maxEnd) {
                maxEnd = pos5 - offset;
            }
        }
        if ((nullBits[1] & 0x20) != 0) {
            int fieldOffset6 = buf.getIntLE(offset + 187);
            int pos6 = offset + 259 + fieldOffset6;
            dictLen = VarInt.peek(buf, pos6);
            pos6 += VarInt.length(buf, pos6);
            for (i = 0; i < dictLen; ++i) {
                al = VarInt.peek(buf, ++pos6);
                pos6 += VarInt.length(buf, pos6);
                for (j = 0; j < al; ++j) {
                    pos6 += RequiredBlockFaceSupport.computeBytesConsumed(buf, pos6);
                }
            }
            if (pos6 - offset > maxEnd) {
                maxEnd = pos6 - offset;
            }
        }
        if ((nullBits[1] & 0x40) != 0) {
            int fieldOffset7 = buf.getIntLE(offset + 191);
            int pos7 = offset + 259 + fieldOffset7;
            dictLen = VarInt.peek(buf, pos7);
            pos7 += VarInt.length(buf, pos7);
            for (i = 0; i < dictLen; ++i) {
                al = VarInt.peek(buf, ++pos7);
                pos7 += VarInt.length(buf, pos7);
                for (j = 0; j < al; ++j) {
                    pos7 += BlockFaceSupport.computeBytesConsumed(buf, pos7);
                }
            }
            if (pos7 - offset > maxEnd) {
                maxEnd = pos7 - offset;
            }
        }
        if ((nullBits[1] & 0x80) != 0) {
            int fieldOffset8 = buf.getIntLE(offset + 195);
            int pos8 = offset + 259 + fieldOffset8;
            arrLen = VarInt.peek(buf, pos8);
            pos8 += VarInt.length(buf, pos8);
            for (i = 0; i < arrLen; ++i) {
                pos8 += BlockTextures.computeBytesConsumed(buf, pos8);
            }
            if (pos8 - offset > maxEnd) {
                maxEnd = pos8 - offset;
            }
        }
        if ((nullBits[2] & 1) != 0) {
            int fieldOffset9 = buf.getIntLE(offset + 199);
            int pos9 = offset + 259 + fieldOffset9;
            sl = VarInt.peek(buf, pos9);
            if ((pos9 += VarInt.length(buf, pos9) + sl) - offset > maxEnd) {
                maxEnd = pos9 - offset;
            }
        }
        if ((nullBits[2] & 2) != 0) {
            int fieldOffset10 = buf.getIntLE(offset + 203);
            int pos10 = offset + 259 + fieldOffset10;
            arrLen = VarInt.peek(buf, pos10);
            pos10 += VarInt.length(buf, pos10);
            for (i = 0; i < arrLen; ++i) {
                pos10 += ModelParticle.computeBytesConsumed(buf, pos10);
            }
            if (pos10 - offset > maxEnd) {
                maxEnd = pos10 - offset;
            }
        }
        if ((nullBits[2] & 4) != 0) {
            int fieldOffset11 = buf.getIntLE(offset + 207);
            int pos11 = offset + 259 + fieldOffset11;
            sl = VarInt.peek(buf, pos11);
            if ((pos11 += VarInt.length(buf, pos11) + sl) - offset > maxEnd) {
                maxEnd = pos11 - offset;
            }
        }
        if ((nullBits[2] & 8) != 0) {
            int fieldOffset12 = buf.getIntLE(offset + 211);
            int pos12 = offset + 259 + fieldOffset12;
            sl = VarInt.peek(buf, pos12);
            if ((pos12 += VarInt.length(buf, pos12) + sl) - offset > maxEnd) {
                maxEnd = pos12 - offset;
            }
        }
        if ((nullBits[2] & 0x10) != 0) {
            int fieldOffset13 = buf.getIntLE(offset + 215);
            int pos13 = offset + 259 + fieldOffset13;
            sl = VarInt.peek(buf, pos13);
            if ((pos13 += VarInt.length(buf, pos13) + sl) - offset > maxEnd) {
                maxEnd = pos13 - offset;
            }
        }
        if ((nullBits[2] & 0x20) != 0) {
            int fieldOffset14 = buf.getIntLE(offset + 219);
            int pos14 = offset + 259 + fieldOffset14;
            arrLen = VarInt.peek(buf, pos14);
            if ((pos14 += VarInt.length(buf, pos14) + arrLen * 4) - offset > maxEnd) {
                maxEnd = pos14 - offset;
            }
        }
        if ((nullBits[2] & 0x40) != 0) {
            int fieldOffset15 = buf.getIntLE(offset + 223);
            int pos15 = offset + 259 + fieldOffset15;
            sl = VarInt.peek(buf, pos15);
            if ((pos15 += VarInt.length(buf, pos15) + sl) - offset > maxEnd) {
                maxEnd = pos15 - offset;
            }
        }
        if ((nullBits[2] & 0x80) != 0) {
            int fieldOffset16 = buf.getIntLE(offset + 227);
            int pos16 = offset + 259 + fieldOffset16;
            if ((pos16 += BlockGathering.computeBytesConsumed(buf, pos16)) - offset > maxEnd) {
                maxEnd = pos16 - offset;
            }
        }
        if ((nullBits[3] & 1) != 0) {
            int fieldOffset17 = buf.getIntLE(offset + 231);
            int pos17 = offset + 259 + fieldOffset17;
            if ((pos17 += ModelDisplay.computeBytesConsumed(buf, pos17)) - offset > maxEnd) {
                maxEnd = pos17 - offset;
            }
        }
        if ((nullBits[3] & 2) != 0) {
            int fieldOffset18 = buf.getIntLE(offset + 235);
            int pos18 = offset + 259 + fieldOffset18;
            if ((pos18 += RailConfig.computeBytesConsumed(buf, pos18)) - offset > maxEnd) {
                maxEnd = pos18 - offset;
            }
        }
        if ((nullBits[3] & 4) != 0) {
            int fieldOffset19 = buf.getIntLE(offset + 239);
            int pos19 = offset + 259 + fieldOffset19;
            dictLen = VarInt.peek(buf, pos19);
            pos19 += VarInt.length(buf, pos19);
            for (i = 0; i < dictLen; ++i) {
                ++pos19;
                pos19 += 4;
            }
            if (pos19 - offset > maxEnd) {
                maxEnd = pos19 - offset;
            }
        }
        if ((nullBits[3] & 8) != 0) {
            int fieldOffset20 = buf.getIntLE(offset + 243);
            int pos20 = offset + 259 + fieldOffset20;
            dictLen = VarInt.peek(buf, pos20);
            pos20 += VarInt.length(buf, pos20);
            for (i = 0; i < dictLen; ++i) {
                int sl2 = VarInt.peek(buf, pos20);
                pos20 += VarInt.length(buf, pos20) + sl2;
                pos20 += 4;
            }
            if (pos20 - offset > maxEnd) {
                maxEnd = pos20 - offset;
            }
        }
        if ((nullBits[3] & 0x10) != 0) {
            int fieldOffset21 = buf.getIntLE(offset + 247);
            int pos21 = offset + 259 + fieldOffset21;
            arrLen = VarInt.peek(buf, pos21);
            if ((pos21 += VarInt.length(buf, pos21) + arrLen * 4) - offset > maxEnd) {
                maxEnd = pos21 - offset;
            }
        }
        if ((nullBits[3] & 0x20) != 0) {
            int fieldOffset22 = buf.getIntLE(offset + 251);
            int pos22 = offset + 259 + fieldOffset22;
            if ((pos22 += Bench.computeBytesConsumed(buf, pos22)) - offset > maxEnd) {
                maxEnd = pos22 - offset;
            }
        }
        if ((nullBits[3] & 0x40) != 0) {
            int fieldOffset23 = buf.getIntLE(offset + 255);
            int pos23 = offset + 259 + fieldOffset23;
            if ((pos23 += ConnectedBlockRuleSet.computeBytesConsumed(buf, pos23)) - offset > maxEnd) {
                maxEnd = pos23 - offset;
            }
        }
        return maxEnd;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        int n;
        int startPos = buf.writerIndex();
        byte[] nullBits = new byte[4];
        if (this.particleColor != null) {
            nullBits[0] = (byte)(nullBits[0] | 1);
        }
        if (this.light != null) {
            nullBits[0] = (byte)(nullBits[0] | 2);
        }
        if (this.tint != null) {
            nullBits[0] = (byte)(nullBits[0] | 4);
        }
        if (this.biomeTint != null) {
            nullBits[0] = (byte)(nullBits[0] | 8);
        }
        if (this.movementSettings != null) {
            nullBits[0] = (byte)(nullBits[0] | 0x10);
        }
        if (this.flags != null) {
            nullBits[0] = (byte)(nullBits[0] | 0x20);
        }
        if (this.placementSettings != null) {
            nullBits[0] = (byte)(nullBits[0] | 0x40);
        }
        if (this.item != null) {
            nullBits[0] = (byte)(nullBits[0] | 0x80);
        }
        if (this.name != null) {
            nullBits[1] = (byte)(nullBits[1] | 1);
        }
        if (this.shaderEffect != null) {
            nullBits[1] = (byte)(nullBits[1] | 2);
        }
        if (this.model != null) {
            nullBits[1] = (byte)(nullBits[1] | 4);
        }
        if (this.modelTexture != null) {
            nullBits[1] = (byte)(nullBits[1] | 8);
        }
        if (this.modelAnimation != null) {
            nullBits[1] = (byte)(nullBits[1] | 0x10);
        }
        if (this.support != null) {
            nullBits[1] = (byte)(nullBits[1] | 0x20);
        }
        if (this.supporting != null) {
            nullBits[1] = (byte)(nullBits[1] | 0x40);
        }
        if (this.cubeTextures != null) {
            nullBits[1] = (byte)(nullBits[1] | 0x80);
        }
        if (this.cubeSideMaskTexture != null) {
            nullBits[2] = (byte)(nullBits[2] | 1);
        }
        if (this.particles != null) {
            nullBits[2] = (byte)(nullBits[2] | 2);
        }
        if (this.blockParticleSetId != null) {
            nullBits[2] = (byte)(nullBits[2] | 4);
        }
        if (this.blockBreakingDecalId != null) {
            nullBits[2] = (byte)(nullBits[2] | 8);
        }
        if (this.transitionTexture != null) {
            nullBits[2] = (byte)(nullBits[2] | 0x10);
        }
        if (this.transitionToGroups != null) {
            nullBits[2] = (byte)(nullBits[2] | 0x20);
        }
        if (this.interactionHint != null) {
            nullBits[2] = (byte)(nullBits[2] | 0x40);
        }
        if (this.gathering != null) {
            nullBits[2] = (byte)(nullBits[2] | 0x80);
        }
        if (this.display != null) {
            nullBits[3] = (byte)(nullBits[3] | 1);
        }
        if (this.rail != null) {
            nullBits[3] = (byte)(nullBits[3] | 2);
        }
        if (this.interactions != null) {
            nullBits[3] = (byte)(nullBits[3] | 4);
        }
        if (this.states != null) {
            nullBits[3] = (byte)(nullBits[3] | 8);
        }
        if (this.tagIndexes != null) {
            nullBits[3] = (byte)(nullBits[3] | 0x10);
        }
        if (this.bench != null) {
            nullBits[3] = (byte)(nullBits[3] | 0x20);
        }
        if (this.connectedBlockRuleSet != null) {
            nullBits[3] = (byte)(nullBits[3] | 0x40);
        }
        buf.writeBytes(nullBits);
        buf.writeByte(this.unknown ? 1 : 0);
        buf.writeByte(this.drawType.getValue());
        buf.writeByte(this.material.getValue());
        buf.writeByte(this.opacity.getValue());
        buf.writeIntLE(this.hitbox);
        buf.writeIntLE(this.interactionHitbox);
        buf.writeFloatLE(this.modelScale);
        buf.writeByte(this.looping ? 1 : 0);
        buf.writeIntLE(this.maxSupportDistance);
        buf.writeByte(this.blockSupportsRequiredFor.getValue());
        buf.writeByte(this.requiresAlphaBlending ? 1 : 0);
        buf.writeByte(this.cubeShadingMode.getValue());
        buf.writeByte(this.randomRotation.getValue());
        buf.writeByte(this.variantRotation.getValue());
        buf.writeByte(this.rotationYawPlacementOffset.getValue());
        buf.writeIntLE(this.blockSoundSetIndex);
        buf.writeIntLE(this.ambientSoundEventIndex);
        if (this.particleColor != null) {
            this.particleColor.serialize(buf);
        } else {
            buf.writeZero(3);
        }
        if (this.light != null) {
            this.light.serialize(buf);
        } else {
            buf.writeZero(4);
        }
        if (this.tint != null) {
            this.tint.serialize(buf);
        } else {
            buf.writeZero(24);
        }
        if (this.biomeTint != null) {
            this.biomeTint.serialize(buf);
        } else {
            buf.writeZero(24);
        }
        buf.writeIntLE(this.group);
        if (this.movementSettings != null) {
            this.movementSettings.serialize(buf);
        } else {
            buf.writeZero(42);
        }
        if (this.flags != null) {
            this.flags.serialize(buf);
        } else {
            buf.writeZero(2);
        }
        if (this.placementSettings != null) {
            this.placementSettings.serialize(buf);
        } else {
            buf.writeZero(16);
        }
        buf.writeByte(this.ignoreSupportWhenPlaced ? 1 : 0);
        buf.writeIntLE(this.transitionToTag);
        int itemOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int nameOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int shaderEffectOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int modelOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int modelTextureOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int modelAnimationOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int supportOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int supportingOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int cubeTexturesOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int cubeSideMaskTextureOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int particlesOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int blockParticleSetIdOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int blockBreakingDecalIdOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int transitionTextureOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int transitionToGroupsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int interactionHintOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int gatheringOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int displayOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int railOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int interactionsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int statesOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int tagIndexesOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int benchOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int connectedBlockRuleSetOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.item != null) {
            buf.setIntLE(itemOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.item, 4096000);
        } else {
            buf.setIntLE(itemOffsetSlot, -1);
        }
        if (this.name != null) {
            buf.setIntLE(nameOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.name, 4096000);
        } else {
            buf.setIntLE(nameOffsetSlot, -1);
        }
        if (this.shaderEffect != null) {
            buf.setIntLE(shaderEffectOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.shaderEffect.length > 4096000) {
                throw ProtocolException.arrayTooLong("ShaderEffect", this.shaderEffect.length, 4096000);
            }
            VarInt.write(buf, this.shaderEffect.length);
            ShaderType[] shaderTypeArray = this.shaderEffect;
            int n2 = shaderTypeArray.length;
            for (n = 0; n < n2; ++n) {
                ShaderType shaderType = shaderTypeArray[n];
                buf.writeByte(shaderType.getValue());
            }
        } else {
            buf.setIntLE(shaderEffectOffsetSlot, -1);
        }
        if (this.model != null) {
            buf.setIntLE(modelOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.model, 4096000);
        } else {
            buf.setIntLE(modelOffsetSlot, -1);
        }
        if (this.modelTexture != null) {
            buf.setIntLE(modelTextureOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.modelTexture.length > 4096000) {
                throw ProtocolException.arrayTooLong("ModelTexture", this.modelTexture.length, 4096000);
            }
            VarInt.write(buf, this.modelTexture.length);
            ModelTexture[] modelTextureArray = this.modelTexture;
            int n3 = modelTextureArray.length;
            for (n = 0; n < n3; ++n) {
                ModelTexture modelTexture = modelTextureArray[n];
                modelTexture.serialize(buf);
            }
        } else {
            buf.setIntLE(modelTextureOffsetSlot, -1);
        }
        if (this.modelAnimation != null) {
            buf.setIntLE(modelAnimationOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.modelAnimation, 4096000);
        } else {
            buf.setIntLE(modelAnimationOffsetSlot, -1);
        }
        if (this.support != null) {
            buf.setIntLE(supportOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.support.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Support", this.support.size(), 4096000);
            }
            VarInt.write(buf, this.support.size());
            for (Map.Entry<BlockNeighbor, RequiredBlockFaceSupport[]> entry : this.support.entrySet()) {
                buf.writeByte(entry.getKey().getValue());
                VarInt.write(buf, entry.getValue().length);
                RequiredBlockFaceSupport[] requiredBlockFaceSupportArray = entry.getValue();
                int n4 = requiredBlockFaceSupportArray.length;
                for (int i = 0; i < n4; ++i) {
                    RequiredBlockFaceSupport requiredBlockFaceSupport = requiredBlockFaceSupportArray[i];
                    requiredBlockFaceSupport.serialize(buf);
                }
            }
        } else {
            buf.setIntLE(supportOffsetSlot, -1);
        }
        if (this.supporting != null) {
            buf.setIntLE(supportingOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.supporting.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Supporting", this.supporting.size(), 4096000);
            }
            VarInt.write(buf, this.supporting.size());
            for (Map.Entry<BlockNeighbor, BlockFaceSupport[]> entry : this.supporting.entrySet()) {
                buf.writeByte(entry.getKey().getValue());
                VarInt.write(buf, entry.getValue().length);
                for (BlockFaceSupport blockFaceSupport : entry.getValue()) {
                    blockFaceSupport.serialize(buf);
                }
            }
        } else {
            buf.setIntLE(supportingOffsetSlot, -1);
        }
        if (this.cubeTextures != null) {
            buf.setIntLE(cubeTexturesOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.cubeTextures.length > 4096000) {
                throw ProtocolException.arrayTooLong("CubeTextures", this.cubeTextures.length, 4096000);
            }
            VarInt.write(buf, this.cubeTextures.length);
            BlockTextures[] blockTexturesArray = this.cubeTextures;
            int n5 = blockTexturesArray.length;
            for (n = 0; n < n5; ++n) {
                BlockTextures blockTextures = blockTexturesArray[n];
                blockTextures.serialize(buf);
            }
        } else {
            buf.setIntLE(cubeTexturesOffsetSlot, -1);
        }
        if (this.cubeSideMaskTexture != null) {
            buf.setIntLE(cubeSideMaskTextureOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.cubeSideMaskTexture, 4096000);
        } else {
            buf.setIntLE(cubeSideMaskTextureOffsetSlot, -1);
        }
        if (this.particles != null) {
            buf.setIntLE(particlesOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.particles.length > 4096000) {
                throw ProtocolException.arrayTooLong("Particles", this.particles.length, 4096000);
            }
            VarInt.write(buf, this.particles.length);
            ModelParticle[] modelParticleArray = this.particles;
            int n6 = modelParticleArray.length;
            for (n = 0; n < n6; ++n) {
                ModelParticle modelParticle = modelParticleArray[n];
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
        if (this.blockBreakingDecalId != null) {
            buf.setIntLE(blockBreakingDecalIdOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.blockBreakingDecalId, 4096000);
        } else {
            buf.setIntLE(blockBreakingDecalIdOffsetSlot, -1);
        }
        if (this.transitionTexture != null) {
            buf.setIntLE(transitionTextureOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.transitionTexture, 4096000);
        } else {
            buf.setIntLE(transitionTextureOffsetSlot, -1);
        }
        if (this.transitionToGroups != null) {
            buf.setIntLE(transitionToGroupsOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.transitionToGroups.length > 4096000) {
                throw ProtocolException.arrayTooLong("TransitionToGroups", this.transitionToGroups.length, 4096000);
            }
            VarInt.write(buf, this.transitionToGroups.length);
            int[] nArray = this.transitionToGroups;
            int n7 = nArray.length;
            for (n = 0; n < n7; ++n) {
                int n8 = nArray[n];
                buf.writeIntLE(n8);
            }
        } else {
            buf.setIntLE(transitionToGroupsOffsetSlot, -1);
        }
        if (this.interactionHint != null) {
            buf.setIntLE(interactionHintOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.interactionHint, 4096000);
        } else {
            buf.setIntLE(interactionHintOffsetSlot, -1);
        }
        if (this.gathering != null) {
            buf.setIntLE(gatheringOffsetSlot, buf.writerIndex() - varBlockStart);
            this.gathering.serialize(buf);
        } else {
            buf.setIntLE(gatheringOffsetSlot, -1);
        }
        if (this.display != null) {
            buf.setIntLE(displayOffsetSlot, buf.writerIndex() - varBlockStart);
            this.display.serialize(buf);
        } else {
            buf.setIntLE(displayOffsetSlot, -1);
        }
        if (this.rail != null) {
            buf.setIntLE(railOffsetSlot, buf.writerIndex() - varBlockStart);
            this.rail.serialize(buf);
        } else {
            buf.setIntLE(railOffsetSlot, -1);
        }
        if (this.interactions != null) {
            buf.setIntLE(interactionsOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.interactions.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Interactions", this.interactions.size(), 4096000);
            }
            VarInt.write(buf, this.interactions.size());
            for (Map.Entry<InteractionType, Integer> entry : this.interactions.entrySet()) {
                buf.writeByte(entry.getKey().getValue());
                buf.writeIntLE(entry.getValue());
            }
        } else {
            buf.setIntLE(interactionsOffsetSlot, -1);
        }
        if (this.states != null) {
            buf.setIntLE(statesOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.states.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("States", this.states.size(), 4096000);
            }
            VarInt.write(buf, this.states.size());
            for (Map.Entry<String, Integer> entry : this.states.entrySet()) {
                PacketIO.writeVarString(buf, entry.getKey(), 4096000);
                buf.writeIntLE(entry.getValue());
            }
        } else {
            buf.setIntLE(statesOffsetSlot, -1);
        }
        if (this.tagIndexes != null) {
            buf.setIntLE(tagIndexesOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.tagIndexes.length > 4096000) {
                throw ProtocolException.arrayTooLong("TagIndexes", this.tagIndexes.length, 4096000);
            }
            VarInt.write(buf, this.tagIndexes.length);
            for (int n9 : this.tagIndexes) {
                buf.writeIntLE(n9);
            }
        } else {
            buf.setIntLE(tagIndexesOffsetSlot, -1);
        }
        if (this.bench != null) {
            buf.setIntLE(benchOffsetSlot, buf.writerIndex() - varBlockStart);
            this.bench.serialize(buf);
        } else {
            buf.setIntLE(benchOffsetSlot, -1);
        }
        if (this.connectedBlockRuleSet != null) {
            buf.setIntLE(connectedBlockRuleSetOffsetSlot, buf.writerIndex() - varBlockStart);
            this.connectedBlockRuleSet.serialize(buf);
        } else {
            buf.setIntLE(connectedBlockRuleSetOffsetSlot, -1);
        }
    }

    public int computeSize() {
        int size = 259;
        if (this.item != null) {
            size += PacketIO.stringSize(this.item);
        }
        if (this.name != null) {
            size += PacketIO.stringSize(this.name);
        }
        if (this.shaderEffect != null) {
            size += VarInt.size(this.shaderEffect.length) + this.shaderEffect.length * 1;
        }
        if (this.model != null) {
            size += PacketIO.stringSize(this.model);
        }
        if (this.modelTexture != null) {
            int modelTextureSize = 0;
            for (ModelTexture modelTexture : this.modelTexture) {
                modelTextureSize += modelTexture.computeSize();
            }
            size += VarInt.size(this.modelTexture.length) + modelTextureSize;
        }
        if (this.modelAnimation != null) {
            size += PacketIO.stringSize(this.modelAnimation);
        }
        if (this.support != null) {
            int supportSize = 0;
            for (Map.Entry entry : this.support.entrySet()) {
                supportSize += 1 + VarInt.size(((RequiredBlockFaceSupport[])entry.getValue()).length) + Arrays.stream((RequiredBlockFaceSupport[])entry.getValue()).mapToInt(inner -> inner.computeSize()).sum();
            }
            size += VarInt.size(this.support.size()) + supportSize;
        }
        if (this.supporting != null) {
            int supportingSize = 0;
            for (Map.Entry entry : this.supporting.entrySet()) {
                supportingSize += 1 + VarInt.size(((BlockFaceSupport[])entry.getValue()).length) + Arrays.stream((BlockFaceSupport[])entry.getValue()).mapToInt(inner -> inner.computeSize()).sum();
            }
            size += VarInt.size(this.supporting.size()) + supportingSize;
        }
        if (this.cubeTextures != null) {
            int cubeTexturesSize = 0;
            for (BlockTextures blockTextures : this.cubeTextures) {
                cubeTexturesSize += blockTextures.computeSize();
            }
            size += VarInt.size(this.cubeTextures.length) + cubeTexturesSize;
        }
        if (this.cubeSideMaskTexture != null) {
            size += PacketIO.stringSize(this.cubeSideMaskTexture);
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
        if (this.blockBreakingDecalId != null) {
            size += PacketIO.stringSize(this.blockBreakingDecalId);
        }
        if (this.transitionTexture != null) {
            size += PacketIO.stringSize(this.transitionTexture);
        }
        if (this.transitionToGroups != null) {
            size += VarInt.size(this.transitionToGroups.length) + this.transitionToGroups.length * 4;
        }
        if (this.interactionHint != null) {
            size += PacketIO.stringSize(this.interactionHint);
        }
        if (this.gathering != null) {
            size += this.gathering.computeSize();
        }
        if (this.display != null) {
            size += this.display.computeSize();
        }
        if (this.rail != null) {
            size += this.rail.computeSize();
        }
        if (this.interactions != null) {
            size += VarInt.size(this.interactions.size()) + this.interactions.size() * 5;
        }
        if (this.states != null) {
            int statesSize = 0;
            for (Map.Entry entry : this.states.entrySet()) {
                statesSize += PacketIO.stringSize((String)entry.getKey()) + 4;
            }
            size += VarInt.size(this.states.size()) + statesSize;
        }
        if (this.tagIndexes != null) {
            size += VarInt.size(this.tagIndexes.length) + this.tagIndexes.length * 4;
        }
        if (this.bench != null) {
            size += this.bench.computeSize();
        }
        if (this.connectedBlockRuleSet != null) {
            size += this.connectedBlockRuleSet.computeSize();
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int valueArrIdx;
        int i;
        int pos;
        if (buffer.readableBytes() - offset < 259) {
            return ValidationResult.error("Buffer too small: expected at least 259 bytes");
        }
        byte[] nullBits = PacketIO.readBytes(buffer, offset, 4);
        if ((nullBits[0] & 0x80) != 0) {
            int itemOffset = buffer.getIntLE(offset + 163);
            if (itemOffset < 0) {
                return ValidationResult.error("Invalid offset for Item");
            }
            pos = offset + 259 + itemOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Item");
            }
            int itemLen = VarInt.peek(buffer, pos);
            if (itemLen < 0) {
                return ValidationResult.error("Invalid string length for Item");
            }
            if (itemLen > 4096000) {
                return ValidationResult.error("Item exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += itemLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading Item");
            }
        }
        if ((nullBits[1] & 1) != 0) {
            int nameOffset = buffer.getIntLE(offset + 167);
            if (nameOffset < 0) {
                return ValidationResult.error("Invalid offset for Name");
            }
            pos = offset + 259 + nameOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Name");
            }
            int nameLen = VarInt.peek(buffer, pos);
            if (nameLen < 0) {
                return ValidationResult.error("Invalid string length for Name");
            }
            if (nameLen > 4096000) {
                return ValidationResult.error("Name exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += nameLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading Name");
            }
        }
        if ((nullBits[1] & 2) != 0) {
            int shaderEffectOffset = buffer.getIntLE(offset + 171);
            if (shaderEffectOffset < 0) {
                return ValidationResult.error("Invalid offset for ShaderEffect");
            }
            pos = offset + 259 + shaderEffectOffset;
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
        if ((nullBits[1] & 4) != 0) {
            int modelOffset = buffer.getIntLE(offset + 175);
            if (modelOffset < 0) {
                return ValidationResult.error("Invalid offset for Model");
            }
            pos = offset + 259 + modelOffset;
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
        if ((nullBits[1] & 8) != 0) {
            int modelTextureOffset = buffer.getIntLE(offset + 179);
            if (modelTextureOffset < 0) {
                return ValidationResult.error("Invalid offset for ModelTexture");
            }
            pos = offset + 259 + modelTextureOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for ModelTexture");
            }
            int modelTextureCount = VarInt.peek(buffer, pos);
            if (modelTextureCount < 0) {
                return ValidationResult.error("Invalid array count for ModelTexture");
            }
            if (modelTextureCount > 4096000) {
                return ValidationResult.error("ModelTexture exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < modelTextureCount; ++i) {
                ValidationResult structResult = ModelTexture.validateStructure(buffer, pos);
                if (!structResult.isValid()) {
                    return ValidationResult.error("Invalid ModelTexture in ModelTexture[" + i + "]: " + structResult.error());
                }
                pos += ModelTexture.computeBytesConsumed(buffer, pos);
            }
        }
        if ((nullBits[1] & 0x10) != 0) {
            int modelAnimationOffset = buffer.getIntLE(offset + 183);
            if (modelAnimationOffset < 0) {
                return ValidationResult.error("Invalid offset for ModelAnimation");
            }
            pos = offset + 259 + modelAnimationOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for ModelAnimation");
            }
            int modelAnimationLen = VarInt.peek(buffer, pos);
            if (modelAnimationLen < 0) {
                return ValidationResult.error("Invalid string length for ModelAnimation");
            }
            if (modelAnimationLen > 4096000) {
                return ValidationResult.error("ModelAnimation exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += modelAnimationLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading ModelAnimation");
            }
        }
        if ((nullBits[1] & 0x20) != 0) {
            int supportOffset = buffer.getIntLE(offset + 187);
            if (supportOffset < 0) {
                return ValidationResult.error("Invalid offset for Support");
            }
            pos = offset + 259 + supportOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Support");
            }
            int supportCount = VarInt.peek(buffer, pos);
            if (supportCount < 0) {
                return ValidationResult.error("Invalid dictionary count for Support");
            }
            if (supportCount > 4096000) {
                return ValidationResult.error("Support exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < supportCount; ++i) {
                int valueArrCount;
                if ((valueArrCount = VarInt.peek(buffer, ++pos)) < 0) {
                    return ValidationResult.error("Invalid array count for value");
                }
                pos += VarInt.length(buffer, pos);
                for (valueArrIdx = 0; valueArrIdx < valueArrCount; ++valueArrIdx) {
                    pos += RequiredBlockFaceSupport.computeBytesConsumed(buffer, pos);
                }
            }
        }
        if ((nullBits[1] & 0x40) != 0) {
            int supportingOffset = buffer.getIntLE(offset + 191);
            if (supportingOffset < 0) {
                return ValidationResult.error("Invalid offset for Supporting");
            }
            pos = offset + 259 + supportingOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Supporting");
            }
            int supportingCount = VarInt.peek(buffer, pos);
            if (supportingCount < 0) {
                return ValidationResult.error("Invalid dictionary count for Supporting");
            }
            if (supportingCount > 4096000) {
                return ValidationResult.error("Supporting exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < supportingCount; ++i) {
                int valueArrCount;
                if ((valueArrCount = VarInt.peek(buffer, ++pos)) < 0) {
                    return ValidationResult.error("Invalid array count for value");
                }
                pos += VarInt.length(buffer, pos);
                for (valueArrIdx = 0; valueArrIdx < valueArrCount; ++valueArrIdx) {
                    pos += BlockFaceSupport.computeBytesConsumed(buffer, pos);
                }
            }
        }
        if ((nullBits[1] & 0x80) != 0) {
            int cubeTexturesOffset = buffer.getIntLE(offset + 195);
            if (cubeTexturesOffset < 0) {
                return ValidationResult.error("Invalid offset for CubeTextures");
            }
            pos = offset + 259 + cubeTexturesOffset;
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
                ValidationResult structResult = BlockTextures.validateStructure(buffer, pos);
                if (!structResult.isValid()) {
                    return ValidationResult.error("Invalid BlockTextures in CubeTextures[" + i + "]: " + structResult.error());
                }
                pos += BlockTextures.computeBytesConsumed(buffer, pos);
            }
        }
        if ((nullBits[2] & 1) != 0) {
            int cubeSideMaskTextureOffset = buffer.getIntLE(offset + 199);
            if (cubeSideMaskTextureOffset < 0) {
                return ValidationResult.error("Invalid offset for CubeSideMaskTexture");
            }
            pos = offset + 259 + cubeSideMaskTextureOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for CubeSideMaskTexture");
            }
            int cubeSideMaskTextureLen = VarInt.peek(buffer, pos);
            if (cubeSideMaskTextureLen < 0) {
                return ValidationResult.error("Invalid string length for CubeSideMaskTexture");
            }
            if (cubeSideMaskTextureLen > 4096000) {
                return ValidationResult.error("CubeSideMaskTexture exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += cubeSideMaskTextureLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading CubeSideMaskTexture");
            }
        }
        if ((nullBits[2] & 2) != 0) {
            int particlesOffset = buffer.getIntLE(offset + 203);
            if (particlesOffset < 0) {
                return ValidationResult.error("Invalid offset for Particles");
            }
            pos = offset + 259 + particlesOffset;
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
        if ((nullBits[2] & 4) != 0) {
            int blockParticleSetIdOffset = buffer.getIntLE(offset + 207);
            if (blockParticleSetIdOffset < 0) {
                return ValidationResult.error("Invalid offset for BlockParticleSetId");
            }
            pos = offset + 259 + blockParticleSetIdOffset;
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
        if ((nullBits[2] & 8) != 0) {
            int blockBreakingDecalIdOffset = buffer.getIntLE(offset + 211);
            if (blockBreakingDecalIdOffset < 0) {
                return ValidationResult.error("Invalid offset for BlockBreakingDecalId");
            }
            pos = offset + 259 + blockBreakingDecalIdOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for BlockBreakingDecalId");
            }
            int blockBreakingDecalIdLen = VarInt.peek(buffer, pos);
            if (blockBreakingDecalIdLen < 0) {
                return ValidationResult.error("Invalid string length for BlockBreakingDecalId");
            }
            if (blockBreakingDecalIdLen > 4096000) {
                return ValidationResult.error("BlockBreakingDecalId exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += blockBreakingDecalIdLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading BlockBreakingDecalId");
            }
        }
        if ((nullBits[2] & 0x10) != 0) {
            int transitionTextureOffset = buffer.getIntLE(offset + 215);
            if (transitionTextureOffset < 0) {
                return ValidationResult.error("Invalid offset for TransitionTexture");
            }
            pos = offset + 259 + transitionTextureOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for TransitionTexture");
            }
            int transitionTextureLen = VarInt.peek(buffer, pos);
            if (transitionTextureLen < 0) {
                return ValidationResult.error("Invalid string length for TransitionTexture");
            }
            if (transitionTextureLen > 4096000) {
                return ValidationResult.error("TransitionTexture exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += transitionTextureLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading TransitionTexture");
            }
        }
        if ((nullBits[2] & 0x20) != 0) {
            int transitionToGroupsOffset = buffer.getIntLE(offset + 219);
            if (transitionToGroupsOffset < 0) {
                return ValidationResult.error("Invalid offset for TransitionToGroups");
            }
            pos = offset + 259 + transitionToGroupsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for TransitionToGroups");
            }
            int transitionToGroupsCount = VarInt.peek(buffer, pos);
            if (transitionToGroupsCount < 0) {
                return ValidationResult.error("Invalid array count for TransitionToGroups");
            }
            if (transitionToGroupsCount > 4096000) {
                return ValidationResult.error("TransitionToGroups exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += transitionToGroupsCount * 4) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading TransitionToGroups");
            }
        }
        if ((nullBits[2] & 0x40) != 0) {
            int interactionHintOffset = buffer.getIntLE(offset + 223);
            if (interactionHintOffset < 0) {
                return ValidationResult.error("Invalid offset for InteractionHint");
            }
            pos = offset + 259 + interactionHintOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for InteractionHint");
            }
            int interactionHintLen = VarInt.peek(buffer, pos);
            if (interactionHintLen < 0) {
                return ValidationResult.error("Invalid string length for InteractionHint");
            }
            if (interactionHintLen > 4096000) {
                return ValidationResult.error("InteractionHint exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += interactionHintLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading InteractionHint");
            }
        }
        if ((nullBits[2] & 0x80) != 0) {
            int gatheringOffset = buffer.getIntLE(offset + 227);
            if (gatheringOffset < 0) {
                return ValidationResult.error("Invalid offset for Gathering");
            }
            pos = offset + 259 + gatheringOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Gathering");
            }
            ValidationResult gatheringResult = BlockGathering.validateStructure(buffer, pos);
            if (!gatheringResult.isValid()) {
                return ValidationResult.error("Invalid Gathering: " + gatheringResult.error());
            }
            pos += BlockGathering.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits[3] & 1) != 0) {
            int displayOffset = buffer.getIntLE(offset + 231);
            if (displayOffset < 0) {
                return ValidationResult.error("Invalid offset for Display");
            }
            pos = offset + 259 + displayOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Display");
            }
            ValidationResult displayResult = ModelDisplay.validateStructure(buffer, pos);
            if (!displayResult.isValid()) {
                return ValidationResult.error("Invalid Display: " + displayResult.error());
            }
            pos += ModelDisplay.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits[3] & 2) != 0) {
            int railOffset = buffer.getIntLE(offset + 235);
            if (railOffset < 0) {
                return ValidationResult.error("Invalid offset for Rail");
            }
            pos = offset + 259 + railOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Rail");
            }
            ValidationResult railResult = RailConfig.validateStructure(buffer, pos);
            if (!railResult.isValid()) {
                return ValidationResult.error("Invalid Rail: " + railResult.error());
            }
            pos += RailConfig.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits[3] & 4) != 0) {
            int interactionsOffset = buffer.getIntLE(offset + 239);
            if (interactionsOffset < 0) {
                return ValidationResult.error("Invalid offset for Interactions");
            }
            pos = offset + 259 + interactionsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Interactions");
            }
            int interactionsCount = VarInt.peek(buffer, pos);
            if (interactionsCount < 0) {
                return ValidationResult.error("Invalid dictionary count for Interactions");
            }
            if (interactionsCount > 4096000) {
                return ValidationResult.error("Interactions exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < interactionsCount; ++i) {
                ++pos;
                if ((pos += 4) <= buffer.writerIndex()) continue;
                return ValidationResult.error("Buffer overflow reading value");
            }
        }
        if ((nullBits[3] & 8) != 0) {
            int statesOffset = buffer.getIntLE(offset + 243);
            if (statesOffset < 0) {
                return ValidationResult.error("Invalid offset for States");
            }
            pos = offset + 259 + statesOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for States");
            }
            int statesCount = VarInt.peek(buffer, pos);
            if (statesCount < 0) {
                return ValidationResult.error("Invalid dictionary count for States");
            }
            if (statesCount > 4096000) {
                return ValidationResult.error("States exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < statesCount; ++i) {
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
                if ((pos += 4) <= buffer.writerIndex()) continue;
                return ValidationResult.error("Buffer overflow reading value");
            }
        }
        if ((nullBits[3] & 0x10) != 0) {
            int tagIndexesOffset = buffer.getIntLE(offset + 247);
            if (tagIndexesOffset < 0) {
                return ValidationResult.error("Invalid offset for TagIndexes");
            }
            pos = offset + 259 + tagIndexesOffset;
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
        if ((nullBits[3] & 0x20) != 0) {
            int benchOffset = buffer.getIntLE(offset + 251);
            if (benchOffset < 0) {
                return ValidationResult.error("Invalid offset for Bench");
            }
            pos = offset + 259 + benchOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Bench");
            }
            ValidationResult benchResult = Bench.validateStructure(buffer, pos);
            if (!benchResult.isValid()) {
                return ValidationResult.error("Invalid Bench: " + benchResult.error());
            }
            pos += Bench.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits[3] & 0x40) != 0) {
            int connectedBlockRuleSetOffset = buffer.getIntLE(offset + 255);
            if (connectedBlockRuleSetOffset < 0) {
                return ValidationResult.error("Invalid offset for ConnectedBlockRuleSet");
            }
            pos = offset + 259 + connectedBlockRuleSetOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for ConnectedBlockRuleSet");
            }
            ValidationResult connectedBlockRuleSetResult = ConnectedBlockRuleSet.validateStructure(buffer, pos);
            if (!connectedBlockRuleSetResult.isValid()) {
                return ValidationResult.error("Invalid ConnectedBlockRuleSet: " + connectedBlockRuleSetResult.error());
            }
            pos += ConnectedBlockRuleSet.computeBytesConsumed(buffer, pos);
        }
        return ValidationResult.OK;
    }

    public BlockType clone() {
        HashMap<BlockNeighbor, RequiredBlockFaceSupport[]> m;
        BlockType copy = new BlockType();
        copy.item = this.item;
        copy.name = this.name;
        copy.unknown = this.unknown;
        copy.drawType = this.drawType;
        copy.material = this.material;
        copy.opacity = this.opacity;
        copy.shaderEffect = this.shaderEffect != null ? Arrays.copyOf(this.shaderEffect, this.shaderEffect.length) : null;
        copy.hitbox = this.hitbox;
        copy.interactionHitbox = this.interactionHitbox;
        copy.model = this.model;
        copy.modelTexture = this.modelTexture != null ? (ModelTexture[])Arrays.stream(this.modelTexture).map(e -> e.clone()).toArray(ModelTexture[]::new) : null;
        copy.modelScale = this.modelScale;
        copy.modelAnimation = this.modelAnimation;
        copy.looping = this.looping;
        copy.maxSupportDistance = this.maxSupportDistance;
        copy.blockSupportsRequiredFor = this.blockSupportsRequiredFor;
        if (this.support != null) {
            m = new HashMap<BlockNeighbor, RequiredBlockFaceSupport[]>();
            for (Map.Entry<BlockNeighbor, Object[]> entry : this.support.entrySet()) {
                m.put(entry.getKey(), (RequiredBlockFaceSupport[])Arrays.stream((RequiredBlockFaceSupport[])entry.getValue()).map(x -> x.clone()).toArray(RequiredBlockFaceSupport[]::new));
            }
            copy.support = m;
        }
        if (this.supporting != null) {
            m = new HashMap();
            for (Map.Entry<BlockNeighbor, Object[]> entry : this.supporting.entrySet()) {
                m.put(entry.getKey(), (RequiredBlockFaceSupport[])((BlockFaceSupport[])Arrays.stream((BlockFaceSupport[])entry.getValue()).map(x -> x.clone()).toArray(BlockFaceSupport[]::new)));
            }
            copy.supporting = m;
        }
        copy.requiresAlphaBlending = this.requiresAlphaBlending;
        copy.cubeTextures = this.cubeTextures != null ? (BlockTextures[])Arrays.stream(this.cubeTextures).map(e -> e.clone()).toArray(BlockTextures[]::new) : null;
        copy.cubeSideMaskTexture = this.cubeSideMaskTexture;
        copy.cubeShadingMode = this.cubeShadingMode;
        copy.randomRotation = this.randomRotation;
        copy.variantRotation = this.variantRotation;
        copy.rotationYawPlacementOffset = this.rotationYawPlacementOffset;
        copy.blockSoundSetIndex = this.blockSoundSetIndex;
        copy.ambientSoundEventIndex = this.ambientSoundEventIndex;
        copy.particles = this.particles != null ? (ModelParticle[])Arrays.stream(this.particles).map(e -> e.clone()).toArray(ModelParticle[]::new) : null;
        copy.blockParticleSetId = this.blockParticleSetId;
        copy.blockBreakingDecalId = this.blockBreakingDecalId;
        copy.particleColor = this.particleColor != null ? this.particleColor.clone() : null;
        copy.light = this.light != null ? this.light.clone() : null;
        copy.tint = this.tint != null ? this.tint.clone() : null;
        copy.biomeTint = this.biomeTint != null ? this.biomeTint.clone() : null;
        copy.group = this.group;
        copy.transitionTexture = this.transitionTexture;
        copy.transitionToGroups = this.transitionToGroups != null ? Arrays.copyOf(this.transitionToGroups, this.transitionToGroups.length) : null;
        copy.movementSettings = this.movementSettings != null ? this.movementSettings.clone() : null;
        copy.flags = this.flags != null ? this.flags.clone() : null;
        copy.interactionHint = this.interactionHint;
        copy.gathering = this.gathering != null ? this.gathering.clone() : null;
        copy.placementSettings = this.placementSettings != null ? this.placementSettings.clone() : null;
        copy.display = this.display != null ? this.display.clone() : null;
        copy.rail = this.rail != null ? this.rail.clone() : null;
        copy.ignoreSupportWhenPlaced = this.ignoreSupportWhenPlaced;
        copy.interactions = this.interactions != null ? new HashMap<InteractionType, Integer>(this.interactions) : null;
        copy.states = this.states != null ? new HashMap<String, Integer>(this.states) : null;
        copy.transitionToTag = this.transitionToTag;
        copy.tagIndexes = this.tagIndexes != null ? Arrays.copyOf(this.tagIndexes, this.tagIndexes.length) : null;
        copy.bench = this.bench != null ? this.bench.clone() : null;
        copy.connectedBlockRuleSet = this.connectedBlockRuleSet != null ? this.connectedBlockRuleSet.clone() : null;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BlockType)) {
            return false;
        }
        BlockType other = (BlockType)obj;
        return Objects.equals(this.item, other.item) && Objects.equals(this.name, other.name) && this.unknown == other.unknown && Objects.equals((Object)this.drawType, (Object)other.drawType) && Objects.equals((Object)this.material, (Object)other.material) && Objects.equals((Object)this.opacity, (Object)other.opacity) && Arrays.equals((Object[])this.shaderEffect, (Object[])other.shaderEffect) && this.hitbox == other.hitbox && this.interactionHitbox == other.interactionHitbox && Objects.equals(this.model, other.model) && Arrays.equals(this.modelTexture, other.modelTexture) && this.modelScale == other.modelScale && Objects.equals(this.modelAnimation, other.modelAnimation) && this.looping == other.looping && this.maxSupportDistance == other.maxSupportDistance && Objects.equals((Object)this.blockSupportsRequiredFor, (Object)other.blockSupportsRequiredFor) && Objects.equals(this.support, other.support) && Objects.equals(this.supporting, other.supporting) && this.requiresAlphaBlending == other.requiresAlphaBlending && Arrays.equals(this.cubeTextures, other.cubeTextures) && Objects.equals(this.cubeSideMaskTexture, other.cubeSideMaskTexture) && Objects.equals((Object)this.cubeShadingMode, (Object)other.cubeShadingMode) && Objects.equals((Object)this.randomRotation, (Object)other.randomRotation) && Objects.equals((Object)this.variantRotation, (Object)other.variantRotation) && Objects.equals((Object)this.rotationYawPlacementOffset, (Object)other.rotationYawPlacementOffset) && this.blockSoundSetIndex == other.blockSoundSetIndex && this.ambientSoundEventIndex == other.ambientSoundEventIndex && Arrays.equals(this.particles, other.particles) && Objects.equals(this.blockParticleSetId, other.blockParticleSetId) && Objects.equals(this.blockBreakingDecalId, other.blockBreakingDecalId) && Objects.equals(this.particleColor, other.particleColor) && Objects.equals(this.light, other.light) && Objects.equals(this.tint, other.tint) && Objects.equals(this.biomeTint, other.biomeTint) && this.group == other.group && Objects.equals(this.transitionTexture, other.transitionTexture) && Arrays.equals(this.transitionToGroups, other.transitionToGroups) && Objects.equals(this.movementSettings, other.movementSettings) && Objects.equals(this.flags, other.flags) && Objects.equals(this.interactionHint, other.interactionHint) && Objects.equals(this.gathering, other.gathering) && Objects.equals(this.placementSettings, other.placementSettings) && Objects.equals(this.display, other.display) && Objects.equals(this.rail, other.rail) && this.ignoreSupportWhenPlaced == other.ignoreSupportWhenPlaced && Objects.equals(this.interactions, other.interactions) && Objects.equals(this.states, other.states) && this.transitionToTag == other.transitionToTag && Arrays.equals(this.tagIndexes, other.tagIndexes) && Objects.equals(this.bench, other.bench) && Objects.equals(this.connectedBlockRuleSet, other.connectedBlockRuleSet);
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + Objects.hashCode(this.item);
        result = 31 * result + Objects.hashCode(this.name);
        result = 31 * result + Boolean.hashCode(this.unknown);
        result = 31 * result + Objects.hashCode((Object)this.drawType);
        result = 31 * result + Objects.hashCode((Object)this.material);
        result = 31 * result + Objects.hashCode((Object)this.opacity);
        result = 31 * result + Arrays.hashCode((Object[])this.shaderEffect);
        result = 31 * result + Integer.hashCode(this.hitbox);
        result = 31 * result + Integer.hashCode(this.interactionHitbox);
        result = 31 * result + Objects.hashCode(this.model);
        result = 31 * result + Arrays.hashCode(this.modelTexture);
        result = 31 * result + Float.hashCode(this.modelScale);
        result = 31 * result + Objects.hashCode(this.modelAnimation);
        result = 31 * result + Boolean.hashCode(this.looping);
        result = 31 * result + Integer.hashCode(this.maxSupportDistance);
        result = 31 * result + Objects.hashCode((Object)this.blockSupportsRequiredFor);
        result = 31 * result + Objects.hashCode(this.support);
        result = 31 * result + Objects.hashCode(this.supporting);
        result = 31 * result + Boolean.hashCode(this.requiresAlphaBlending);
        result = 31 * result + Arrays.hashCode(this.cubeTextures);
        result = 31 * result + Objects.hashCode(this.cubeSideMaskTexture);
        result = 31 * result + Objects.hashCode((Object)this.cubeShadingMode);
        result = 31 * result + Objects.hashCode((Object)this.randomRotation);
        result = 31 * result + Objects.hashCode((Object)this.variantRotation);
        result = 31 * result + Objects.hashCode((Object)this.rotationYawPlacementOffset);
        result = 31 * result + Integer.hashCode(this.blockSoundSetIndex);
        result = 31 * result + Integer.hashCode(this.ambientSoundEventIndex);
        result = 31 * result + Arrays.hashCode(this.particles);
        result = 31 * result + Objects.hashCode(this.blockParticleSetId);
        result = 31 * result + Objects.hashCode(this.blockBreakingDecalId);
        result = 31 * result + Objects.hashCode(this.particleColor);
        result = 31 * result + Objects.hashCode(this.light);
        result = 31 * result + Objects.hashCode(this.tint);
        result = 31 * result + Objects.hashCode(this.biomeTint);
        result = 31 * result + Integer.hashCode(this.group);
        result = 31 * result + Objects.hashCode(this.transitionTexture);
        result = 31 * result + Arrays.hashCode(this.transitionToGroups);
        result = 31 * result + Objects.hashCode(this.movementSettings);
        result = 31 * result + Objects.hashCode(this.flags);
        result = 31 * result + Objects.hashCode(this.interactionHint);
        result = 31 * result + Objects.hashCode(this.gathering);
        result = 31 * result + Objects.hashCode(this.placementSettings);
        result = 31 * result + Objects.hashCode(this.display);
        result = 31 * result + Objects.hashCode(this.rail);
        result = 31 * result + Boolean.hashCode(this.ignoreSupportWhenPlaced);
        result = 31 * result + Objects.hashCode(this.interactions);
        result = 31 * result + Objects.hashCode(this.states);
        result = 31 * result + Integer.hashCode(this.transitionToTag);
        result = 31 * result + Arrays.hashCode(this.tagIndexes);
        result = 31 * result + Objects.hashCode(this.bench);
        result = 31 * result + Objects.hashCode(this.connectedBlockRuleSet);
        return result;
    }
}

