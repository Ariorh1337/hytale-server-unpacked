/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.ColorLight;
import com.hypixel.hytale.protocol.CombatTextUpdate;
import com.hypixel.hytale.protocol.ComponentUpdateType;
import com.hypixel.hytale.protocol.EntityEffectUpdate;
import com.hypixel.hytale.protocol.EntityStatUpdate;
import com.hypixel.hytale.protocol.Equipment;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.ItemWithAllMetadata;
import com.hypixel.hytale.protocol.Model;
import com.hypixel.hytale.protocol.ModelTransform;
import com.hypixel.hytale.protocol.MountedUpdate;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.protocol.Nameplate;
import com.hypixel.hytale.protocol.PlayerSkin;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ComponentUpdate {
    public static final int NULLABLE_BIT_FIELD_SIZE = 3;
    public static final int FIXED_BLOCK_SIZE = 159;
    public static final int VARIABLE_FIELD_COUNT = 13;
    public static final int VARIABLE_BLOCK_START = 211;
    public static final int MAX_SIZE = 0x64000000;
    @Nonnull
    public ComponentUpdateType type = ComponentUpdateType.Nameplate;
    @Nullable
    public Nameplate nameplate;
    @Nullable
    public int[] entityUIComponents;
    @Nullable
    public CombatTextUpdate combatTextUpdate;
    @Nullable
    public Model model;
    @Nullable
    public PlayerSkin skin;
    @Nullable
    public ItemWithAllMetadata item;
    public int blockId;
    public float entityScale;
    @Nullable
    public Equipment equipment;
    @Nullable
    public Map<Integer, EntityStatUpdate[]> entityStatUpdates;
    @Nullable
    public ModelTransform transform;
    @Nullable
    public MovementStates movementStates;
    @Nullable
    public EntityEffectUpdate[] entityEffectUpdates;
    @Nullable
    public Map<InteractionType, Integer> interactions;
    @Nullable
    public ColorLight dynamicLight;
    public int hitboxCollisionConfigIndex;
    public int repulsionConfigIndex;
    @Nonnull
    public UUID predictionId = new UUID(0L, 0L);
    @Nullable
    public int[] soundEventIds;
    @Nullable
    public String interactionHint;
    @Nullable
    public MountedUpdate mounted;
    @Nullable
    public String[] activeAnimations;

    public ComponentUpdate() {
    }

    public ComponentUpdate(@Nonnull ComponentUpdateType type, @Nullable Nameplate nameplate, @Nullable int[] entityUIComponents, @Nullable CombatTextUpdate combatTextUpdate, @Nullable Model model, @Nullable PlayerSkin skin, @Nullable ItemWithAllMetadata item, int blockId, float entityScale, @Nullable Equipment equipment, @Nullable Map<Integer, EntityStatUpdate[]> entityStatUpdates, @Nullable ModelTransform transform, @Nullable MovementStates movementStates, @Nullable EntityEffectUpdate[] entityEffectUpdates, @Nullable Map<InteractionType, Integer> interactions, @Nullable ColorLight dynamicLight, int hitboxCollisionConfigIndex, int repulsionConfigIndex, @Nonnull UUID predictionId, @Nullable int[] soundEventIds, @Nullable String interactionHint, @Nullable MountedUpdate mounted, @Nullable String[] activeAnimations) {
        this.type = type;
        this.nameplate = nameplate;
        this.entityUIComponents = entityUIComponents;
        this.combatTextUpdate = combatTextUpdate;
        this.model = model;
        this.skin = skin;
        this.item = item;
        this.blockId = blockId;
        this.entityScale = entityScale;
        this.equipment = equipment;
        this.entityStatUpdates = entityStatUpdates;
        this.transform = transform;
        this.movementStates = movementStates;
        this.entityEffectUpdates = entityEffectUpdates;
        this.interactions = interactions;
        this.dynamicLight = dynamicLight;
        this.hitboxCollisionConfigIndex = hitboxCollisionConfigIndex;
        this.repulsionConfigIndex = repulsionConfigIndex;
        this.predictionId = predictionId;
        this.soundEventIds = soundEventIds;
        this.interactionHint = interactionHint;
        this.mounted = mounted;
        this.activeAnimations = activeAnimations;
    }

    public ComponentUpdate(@Nonnull ComponentUpdate other) {
        this.type = other.type;
        this.nameplate = other.nameplate;
        this.entityUIComponents = other.entityUIComponents;
        this.combatTextUpdate = other.combatTextUpdate;
        this.model = other.model;
        this.skin = other.skin;
        this.item = other.item;
        this.blockId = other.blockId;
        this.entityScale = other.entityScale;
        this.equipment = other.equipment;
        this.entityStatUpdates = other.entityStatUpdates;
        this.transform = other.transform;
        this.movementStates = other.movementStates;
        this.entityEffectUpdates = other.entityEffectUpdates;
        this.interactions = other.interactions;
        this.dynamicLight = other.dynamicLight;
        this.hitboxCollisionConfigIndex = other.hitboxCollisionConfigIndex;
        this.repulsionConfigIndex = other.repulsionConfigIndex;
        this.predictionId = other.predictionId;
        this.soundEventIds = other.soundEventIds;
        this.interactionHint = other.interactionHint;
        this.mounted = other.mounted;
        this.activeAnimations = other.activeAnimations;
    }

    @Nonnull
    public static ComponentUpdate deserialize(@Nonnull ByteBuf buf, int offset) {
        int i;
        int dictPos;
        int i2;
        int varIntLen;
        ComponentUpdate obj = new ComponentUpdate();
        byte[] nullBits = PacketIO.readBytes(buf, offset, 3);
        obj.type = ComponentUpdateType.fromValue(buf.getByte(offset + 3));
        obj.blockId = buf.getIntLE(offset + 4);
        obj.entityScale = buf.getFloatLE(offset + 8);
        if ((nullBits[0] & 1) != 0) {
            obj.transform = ModelTransform.deserialize(buf, offset + 12);
        }
        if ((nullBits[0] & 2) != 0) {
            obj.movementStates = MovementStates.deserialize(buf, offset + 61);
        }
        if ((nullBits[0] & 4) != 0) {
            obj.dynamicLight = ColorLight.deserialize(buf, offset + 83);
        }
        obj.hitboxCollisionConfigIndex = buf.getIntLE(offset + 87);
        obj.repulsionConfigIndex = buf.getIntLE(offset + 91);
        obj.predictionId = PacketIO.readUUID(buf, offset + 95);
        if ((nullBits[0] & 8) != 0) {
            obj.mounted = MountedUpdate.deserialize(buf, offset + 111);
        }
        if ((nullBits[0] & 0x10) != 0) {
            int varPos0 = offset + 211 + buf.getIntLE(offset + 159);
            obj.nameplate = Nameplate.deserialize(buf, varPos0);
        }
        if ((nullBits[0] & 0x20) != 0) {
            int varPos1 = offset + 211 + buf.getIntLE(offset + 163);
            int entityUIComponentsCount = VarInt.peek(buf, varPos1);
            if (entityUIComponentsCount < 0) {
                throw ProtocolException.negativeLength("EntityUIComponents", entityUIComponentsCount);
            }
            if (entityUIComponentsCount > 4096000) {
                throw ProtocolException.arrayTooLong("EntityUIComponents", entityUIComponentsCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos1);
            if ((long)(varPos1 + varIntLen) + (long)entityUIComponentsCount * 4L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("EntityUIComponents", varPos1 + varIntLen + entityUIComponentsCount * 4, buf.readableBytes());
            }
            obj.entityUIComponents = new int[entityUIComponentsCount];
            for (i2 = 0; i2 < entityUIComponentsCount; ++i2) {
                obj.entityUIComponents[i2] = buf.getIntLE(varPos1 + varIntLen + i2 * 4);
            }
        }
        if ((nullBits[0] & 0x40) != 0) {
            int varPos2 = offset + 211 + buf.getIntLE(offset + 167);
            obj.combatTextUpdate = CombatTextUpdate.deserialize(buf, varPos2);
        }
        if ((nullBits[0] & 0x80) != 0) {
            int varPos3 = offset + 211 + buf.getIntLE(offset + 171);
            obj.model = Model.deserialize(buf, varPos3);
        }
        if ((nullBits[1] & 1) != 0) {
            int varPos4 = offset + 211 + buf.getIntLE(offset + 175);
            obj.skin = PlayerSkin.deserialize(buf, varPos4);
        }
        if ((nullBits[1] & 2) != 0) {
            int varPos5 = offset + 211 + buf.getIntLE(offset + 179);
            obj.item = ItemWithAllMetadata.deserialize(buf, varPos5);
        }
        if ((nullBits[1] & 4) != 0) {
            int varPos6 = offset + 211 + buf.getIntLE(offset + 183);
            obj.equipment = Equipment.deserialize(buf, varPos6);
        }
        if ((nullBits[1] & 8) != 0) {
            int varPos7 = offset + 211 + buf.getIntLE(offset + 187);
            int entityStatUpdatesCount = VarInt.peek(buf, varPos7);
            if (entityStatUpdatesCount < 0) {
                throw ProtocolException.negativeLength("EntityStatUpdates", entityStatUpdatesCount);
            }
            if (entityStatUpdatesCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("EntityStatUpdates", entityStatUpdatesCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos7);
            obj.entityStatUpdates = new HashMap<Integer, EntityStatUpdate[]>(entityStatUpdatesCount);
            dictPos = varPos7 + varIntLen;
            for (i = 0; i < entityStatUpdatesCount; ++i) {
                int key = buf.getIntLE(dictPos);
                int valLen = VarInt.peek(buf, dictPos += 4);
                if (valLen < 0) {
                    throw ProtocolException.negativeLength("val", valLen);
                }
                if (valLen > 64) {
                    throw ProtocolException.arrayTooLong("val", valLen, 64);
                }
                int valVarLen = VarInt.length(buf, dictPos);
                if ((long)(dictPos + valVarLen) + (long)valLen * 13L > (long)buf.readableBytes()) {
                    throw ProtocolException.bufferTooSmall("val", dictPos + valVarLen + valLen * 13, buf.readableBytes());
                }
                dictPos += valVarLen;
                EntityStatUpdate[] val = new EntityStatUpdate[valLen];
                for (int valIdx = 0; valIdx < valLen; ++valIdx) {
                    val[valIdx] = EntityStatUpdate.deserialize(buf, dictPos);
                    dictPos += EntityStatUpdate.computeBytesConsumed(buf, dictPos);
                }
                if (obj.entityStatUpdates.put(key, val) == null) continue;
                throw ProtocolException.duplicateKey("entityStatUpdates", key);
            }
        }
        if ((nullBits[1] & 0x10) != 0) {
            int varPos8 = offset + 211 + buf.getIntLE(offset + 191);
            int entityEffectUpdatesCount = VarInt.peek(buf, varPos8);
            if (entityEffectUpdatesCount < 0) {
                throw ProtocolException.negativeLength("EntityEffectUpdates", entityEffectUpdatesCount);
            }
            if (entityEffectUpdatesCount > 4096000) {
                throw ProtocolException.arrayTooLong("EntityEffectUpdates", entityEffectUpdatesCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos8);
            if ((long)(varPos8 + varIntLen) + (long)entityEffectUpdatesCount * 12L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("EntityEffectUpdates", varPos8 + varIntLen + entityEffectUpdatesCount * 12, buf.readableBytes());
            }
            obj.entityEffectUpdates = new EntityEffectUpdate[entityEffectUpdatesCount];
            int elemPos = varPos8 + varIntLen;
            for (i = 0; i < entityEffectUpdatesCount; ++i) {
                obj.entityEffectUpdates[i] = EntityEffectUpdate.deserialize(buf, elemPos);
                elemPos += EntityEffectUpdate.computeBytesConsumed(buf, elemPos);
            }
        }
        if ((nullBits[1] & 0x20) != 0) {
            int varPos9 = offset + 211 + buf.getIntLE(offset + 195);
            int interactionsCount = VarInt.peek(buf, varPos9);
            if (interactionsCount < 0) {
                throw ProtocolException.negativeLength("Interactions", interactionsCount);
            }
            if (interactionsCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Interactions", interactionsCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos9);
            obj.interactions = new HashMap<InteractionType, Integer>(interactionsCount);
            dictPos = varPos9 + varIntLen;
            for (i = 0; i < interactionsCount; ++i) {
                InteractionType key = InteractionType.fromValue(buf.getByte(dictPos));
                int val = buf.getIntLE(++dictPos);
                dictPos += 4;
                if (obj.interactions.put(key, val) == null) continue;
                throw ProtocolException.duplicateKey("interactions", (Object)key);
            }
        }
        if ((nullBits[1] & 0x40) != 0) {
            int varPos10 = offset + 211 + buf.getIntLE(offset + 199);
            int soundEventIdsCount = VarInt.peek(buf, varPos10);
            if (soundEventIdsCount < 0) {
                throw ProtocolException.negativeLength("SoundEventIds", soundEventIdsCount);
            }
            if (soundEventIdsCount > 4096000) {
                throw ProtocolException.arrayTooLong("SoundEventIds", soundEventIdsCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos10);
            if ((long)(varPos10 + varIntLen) + (long)soundEventIdsCount * 4L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("SoundEventIds", varPos10 + varIntLen + soundEventIdsCount * 4, buf.readableBytes());
            }
            obj.soundEventIds = new int[soundEventIdsCount];
            for (i2 = 0; i2 < soundEventIdsCount; ++i2) {
                obj.soundEventIds[i2] = buf.getIntLE(varPos10 + varIntLen + i2 * 4);
            }
        }
        if ((nullBits[1] & 0x80) != 0) {
            int varPos11 = offset + 211 + buf.getIntLE(offset + 203);
            int interactionHintLen = VarInt.peek(buf, varPos11);
            if (interactionHintLen < 0) {
                throw ProtocolException.negativeLength("InteractionHint", interactionHintLen);
            }
            if (interactionHintLen > 4096000) {
                throw ProtocolException.stringTooLong("InteractionHint", interactionHintLen, 4096000);
            }
            obj.interactionHint = PacketIO.readVarString(buf, varPos11, PacketIO.UTF8);
        }
        if ((nullBits[2] & 1) != 0) {
            int varPos12 = offset + 211 + buf.getIntLE(offset + 207);
            int activeAnimationsCount = VarInt.peek(buf, varPos12);
            if (activeAnimationsCount < 0) {
                throw ProtocolException.negativeLength("ActiveAnimations", activeAnimationsCount);
            }
            if (activeAnimationsCount > 4096000) {
                throw ProtocolException.arrayTooLong("ActiveAnimations", activeAnimationsCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos12);
            int activeAnimationsBitfieldSize = (activeAnimationsCount + 7) / 8;
            byte[] activeAnimationsBitfield = PacketIO.readBytes(buf, varPos12 + varIntLen, activeAnimationsBitfieldSize);
            obj.activeAnimations = new String[activeAnimationsCount];
            int elemPos = varPos12 + varIntLen + activeAnimationsBitfieldSize;
            for (int i3 = 0; i3 < activeAnimationsCount; ++i3) {
                if ((activeAnimationsBitfield[i3 / 8] & 1 << i3 % 8) == 0) continue;
                int strLen = VarInt.peek(buf, elemPos);
                if (strLen < 0) {
                    throw ProtocolException.negativeLength("activeAnimations[" + i3 + "]", strLen);
                }
                if (strLen > 4096000) {
                    throw ProtocolException.stringTooLong("activeAnimations[" + i3 + "]", strLen, 4096000);
                }
                int strVarLen = VarInt.length(buf, elemPos);
                obj.activeAnimations[i3] = PacketIO.readVarString(buf, elemPos);
                elemPos += strVarLen + strLen;
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int i;
        int dictLen;
        int arrLen;
        byte[] nullBits = PacketIO.readBytes(buf, offset, 3);
        int maxEnd = 211;
        if ((nullBits[0] & 0x10) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 159);
            int pos0 = offset + 211 + fieldOffset0;
            if ((pos0 += Nameplate.computeBytesConsumed(buf, pos0)) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits[0] & 0x20) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 163);
            int pos1 = offset + 211 + fieldOffset1;
            arrLen = VarInt.peek(buf, pos1);
            if ((pos1 += VarInt.length(buf, pos1) + arrLen * 4) - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        if ((nullBits[0] & 0x40) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 167);
            int pos2 = offset + 211 + fieldOffset2;
            if ((pos2 += CombatTextUpdate.computeBytesConsumed(buf, pos2)) - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        if ((nullBits[0] & 0x80) != 0) {
            int fieldOffset3 = buf.getIntLE(offset + 171);
            int pos3 = offset + 211 + fieldOffset3;
            if ((pos3 += Model.computeBytesConsumed(buf, pos3)) - offset > maxEnd) {
                maxEnd = pos3 - offset;
            }
        }
        if ((nullBits[1] & 1) != 0) {
            int fieldOffset4 = buf.getIntLE(offset + 175);
            int pos4 = offset + 211 + fieldOffset4;
            if ((pos4 += PlayerSkin.computeBytesConsumed(buf, pos4)) - offset > maxEnd) {
                maxEnd = pos4 - offset;
            }
        }
        if ((nullBits[1] & 2) != 0) {
            int fieldOffset5 = buf.getIntLE(offset + 179);
            int pos5 = offset + 211 + fieldOffset5;
            if ((pos5 += ItemWithAllMetadata.computeBytesConsumed(buf, pos5)) - offset > maxEnd) {
                maxEnd = pos5 - offset;
            }
        }
        if ((nullBits[1] & 4) != 0) {
            int fieldOffset6 = buf.getIntLE(offset + 183);
            int pos6 = offset + 211 + fieldOffset6;
            if ((pos6 += Equipment.computeBytesConsumed(buf, pos6)) - offset > maxEnd) {
                maxEnd = pos6 - offset;
            }
        }
        if ((nullBits[1] & 8) != 0) {
            int fieldOffset7 = buf.getIntLE(offset + 187);
            int pos7 = offset + 211 + fieldOffset7;
            dictLen = VarInt.peek(buf, pos7);
            pos7 += VarInt.length(buf, pos7);
            for (i = 0; i < dictLen; ++i) {
                int al = VarInt.peek(buf, pos7 += 4);
                pos7 += VarInt.length(buf, pos7);
                for (int j = 0; j < al; ++j) {
                    pos7 += EntityStatUpdate.computeBytesConsumed(buf, pos7);
                }
            }
            if (pos7 - offset > maxEnd) {
                maxEnd = pos7 - offset;
            }
        }
        if ((nullBits[1] & 0x10) != 0) {
            int fieldOffset8 = buf.getIntLE(offset + 191);
            int pos8 = offset + 211 + fieldOffset8;
            arrLen = VarInt.peek(buf, pos8);
            pos8 += VarInt.length(buf, pos8);
            for (i = 0; i < arrLen; ++i) {
                pos8 += EntityEffectUpdate.computeBytesConsumed(buf, pos8);
            }
            if (pos8 - offset > maxEnd) {
                maxEnd = pos8 - offset;
            }
        }
        if ((nullBits[1] & 0x20) != 0) {
            int fieldOffset9 = buf.getIntLE(offset + 195);
            int pos9 = offset + 211 + fieldOffset9;
            dictLen = VarInt.peek(buf, pos9);
            pos9 += VarInt.length(buf, pos9);
            for (i = 0; i < dictLen; ++i) {
                ++pos9;
                pos9 += 4;
            }
            if (pos9 - offset > maxEnd) {
                maxEnd = pos9 - offset;
            }
        }
        if ((nullBits[1] & 0x40) != 0) {
            int fieldOffset10 = buf.getIntLE(offset + 199);
            int pos10 = offset + 211 + fieldOffset10;
            arrLen = VarInt.peek(buf, pos10);
            if ((pos10 += VarInt.length(buf, pos10) + arrLen * 4) - offset > maxEnd) {
                maxEnd = pos10 - offset;
            }
        }
        if ((nullBits[1] & 0x80) != 0) {
            int fieldOffset11 = buf.getIntLE(offset + 203);
            int pos11 = offset + 211 + fieldOffset11;
            int sl = VarInt.peek(buf, pos11);
            if ((pos11 += VarInt.length(buf, pos11) + sl) - offset > maxEnd) {
                maxEnd = pos11 - offset;
            }
        }
        if ((nullBits[2] & 1) != 0) {
            int fieldOffset12 = buf.getIntLE(offset + 207);
            int pos12 = offset + 211 + fieldOffset12;
            arrLen = VarInt.peek(buf, pos12);
            pos12 += VarInt.length(buf, pos12);
            int bitfieldSize = (arrLen + 7) / 8;
            byte[] bitfield = PacketIO.readBytes(buf, pos12, bitfieldSize);
            pos12 += bitfieldSize;
            for (int i2 = 0; i2 < arrLen; ++i2) {
                if ((bitfield[i2 / 8] & 1 << i2 % 8) == 0) continue;
                int sl = VarInt.peek(buf, pos12);
                pos12 += VarInt.length(buf, pos12) + sl;
            }
            if (pos12 - offset > maxEnd) {
                maxEnd = pos12 - offset;
            }
        }
        return maxEnd;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        int n;
        int startPos = buf.writerIndex();
        byte[] nullBits = new byte[3];
        if (this.transform != null) {
            nullBits[0] = (byte)(nullBits[0] | 1);
        }
        if (this.movementStates != null) {
            nullBits[0] = (byte)(nullBits[0] | 2);
        }
        if (this.dynamicLight != null) {
            nullBits[0] = (byte)(nullBits[0] | 4);
        }
        if (this.mounted != null) {
            nullBits[0] = (byte)(nullBits[0] | 8);
        }
        if (this.nameplate != null) {
            nullBits[0] = (byte)(nullBits[0] | 0x10);
        }
        if (this.entityUIComponents != null) {
            nullBits[0] = (byte)(nullBits[0] | 0x20);
        }
        if (this.combatTextUpdate != null) {
            nullBits[0] = (byte)(nullBits[0] | 0x40);
        }
        if (this.model != null) {
            nullBits[0] = (byte)(nullBits[0] | 0x80);
        }
        if (this.skin != null) {
            nullBits[1] = (byte)(nullBits[1] | 1);
        }
        if (this.item != null) {
            nullBits[1] = (byte)(nullBits[1] | 2);
        }
        if (this.equipment != null) {
            nullBits[1] = (byte)(nullBits[1] | 4);
        }
        if (this.entityStatUpdates != null) {
            nullBits[1] = (byte)(nullBits[1] | 8);
        }
        if (this.entityEffectUpdates != null) {
            nullBits[1] = (byte)(nullBits[1] | 0x10);
        }
        if (this.interactions != null) {
            nullBits[1] = (byte)(nullBits[1] | 0x20);
        }
        if (this.soundEventIds != null) {
            nullBits[1] = (byte)(nullBits[1] | 0x40);
        }
        if (this.interactionHint != null) {
            nullBits[1] = (byte)(nullBits[1] | 0x80);
        }
        if (this.activeAnimations != null) {
            nullBits[2] = (byte)(nullBits[2] | 1);
        }
        buf.writeBytes(nullBits);
        buf.writeByte(this.type.getValue());
        buf.writeIntLE(this.blockId);
        buf.writeFloatLE(this.entityScale);
        if (this.transform != null) {
            this.transform.serialize(buf);
        } else {
            buf.writeZero(49);
        }
        if (this.movementStates != null) {
            this.movementStates.serialize(buf);
        } else {
            buf.writeZero(22);
        }
        if (this.dynamicLight != null) {
            this.dynamicLight.serialize(buf);
        } else {
            buf.writeZero(4);
        }
        buf.writeIntLE(this.hitboxCollisionConfigIndex);
        buf.writeIntLE(this.repulsionConfigIndex);
        PacketIO.writeUUID(buf, this.predictionId);
        if (this.mounted != null) {
            this.mounted.serialize(buf);
        } else {
            buf.writeZero(48);
        }
        int nameplateOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int entityUIComponentsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int combatTextUpdateOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int modelOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int skinOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int itemOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int equipmentOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int entityStatUpdatesOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int entityEffectUpdatesOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int interactionsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int soundEventIdsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int interactionHintOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int activeAnimationsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.nameplate != null) {
            buf.setIntLE(nameplateOffsetSlot, buf.writerIndex() - varBlockStart);
            this.nameplate.serialize(buf);
        } else {
            buf.setIntLE(nameplateOffsetSlot, -1);
        }
        if (this.entityUIComponents != null) {
            buf.setIntLE(entityUIComponentsOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.entityUIComponents.length > 4096000) {
                throw ProtocolException.arrayTooLong("EntityUIComponents", this.entityUIComponents.length, 4096000);
            }
            VarInt.write(buf, this.entityUIComponents.length);
            int[] object = this.entityUIComponents;
            int n2 = object.length;
            for (n = 0; n < n2; ++n) {
                int item = object[n];
                buf.writeIntLE(item);
            }
        } else {
            buf.setIntLE(entityUIComponentsOffsetSlot, -1);
        }
        if (this.combatTextUpdate != null) {
            buf.setIntLE(combatTextUpdateOffsetSlot, buf.writerIndex() - varBlockStart);
            this.combatTextUpdate.serialize(buf);
        } else {
            buf.setIntLE(combatTextUpdateOffsetSlot, -1);
        }
        if (this.model != null) {
            buf.setIntLE(modelOffsetSlot, buf.writerIndex() - varBlockStart);
            this.model.serialize(buf);
        } else {
            buf.setIntLE(modelOffsetSlot, -1);
        }
        if (this.skin != null) {
            buf.setIntLE(skinOffsetSlot, buf.writerIndex() - varBlockStart);
            this.skin.serialize(buf);
        } else {
            buf.setIntLE(skinOffsetSlot, -1);
        }
        if (this.item != null) {
            buf.setIntLE(itemOffsetSlot, buf.writerIndex() - varBlockStart);
            this.item.serialize(buf);
        } else {
            buf.setIntLE(itemOffsetSlot, -1);
        }
        if (this.equipment != null) {
            buf.setIntLE(equipmentOffsetSlot, buf.writerIndex() - varBlockStart);
            this.equipment.serialize(buf);
        } else {
            buf.setIntLE(equipmentOffsetSlot, -1);
        }
        if (this.entityStatUpdates != null) {
            buf.setIntLE(entityStatUpdatesOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.entityStatUpdates.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("EntityStatUpdates", this.entityStatUpdates.size(), 4096000);
            }
            VarInt.write(buf, this.entityStatUpdates.size());
            for (Map.Entry<Integer, EntityStatUpdate[]> entry : this.entityStatUpdates.entrySet()) {
                buf.writeIntLE(entry.getKey());
                VarInt.write(buf, entry.getValue().length);
                for (EntityStatUpdate arrItem : entry.getValue()) {
                    arrItem.serialize(buf);
                }
            }
        } else {
            buf.setIntLE(entityStatUpdatesOffsetSlot, -1);
        }
        if (this.entityEffectUpdates != null) {
            buf.setIntLE(entityEffectUpdatesOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.entityEffectUpdates.length > 4096000) {
                throw ProtocolException.arrayTooLong("EntityEffectUpdates", this.entityEffectUpdates.length, 4096000);
            }
            VarInt.write(buf, this.entityEffectUpdates.length);
            EntityEffectUpdate[] entityEffectUpdateArray = this.entityEffectUpdates;
            int n3 = entityEffectUpdateArray.length;
            for (n = 0; n < n3; ++n) {
                EntityEffectUpdate item = entityEffectUpdateArray[n];
                item.serialize(buf);
            }
        } else {
            buf.setIntLE(entityEffectUpdatesOffsetSlot, -1);
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
        if (this.soundEventIds != null) {
            buf.setIntLE(soundEventIdsOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.soundEventIds.length > 4096000) {
                throw ProtocolException.arrayTooLong("SoundEventIds", this.soundEventIds.length, 4096000);
            }
            VarInt.write(buf, this.soundEventIds.length);
            for (int item : this.soundEventIds) {
                buf.writeIntLE(item);
            }
        } else {
            buf.setIntLE(soundEventIdsOffsetSlot, -1);
        }
        if (this.interactionHint != null) {
            buf.setIntLE(interactionHintOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.interactionHint, 4096000);
        } else {
            buf.setIntLE(interactionHintOffsetSlot, -1);
        }
        if (this.activeAnimations != null) {
            int i;
            buf.setIntLE(activeAnimationsOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.activeAnimations.length > 4096000) {
                throw ProtocolException.arrayTooLong("ActiveAnimations", this.activeAnimations.length, 4096000);
            }
            VarInt.write(buf, this.activeAnimations.length);
            int n4 = (this.activeAnimations.length + 7) / 8;
            byte[] byArray = new byte[n4];
            for (i = 0; i < this.activeAnimations.length; ++i) {
                if (this.activeAnimations[i] == null) continue;
                int n5 = i / 8;
                byArray[n5] = (byte)(byArray[n5] | (byte)(1 << i % 8));
            }
            buf.writeBytes(byArray);
            for (i = 0; i < this.activeAnimations.length; ++i) {
                if (this.activeAnimations[i] == null) continue;
                PacketIO.writeVarString(buf, this.activeAnimations[i], 4096000);
            }
        } else {
            buf.setIntLE(activeAnimationsOffsetSlot, -1);
        }
    }

    public int computeSize() {
        int size = 211;
        if (this.nameplate != null) {
            size += this.nameplate.computeSize();
        }
        if (this.entityUIComponents != null) {
            size += VarInt.size(this.entityUIComponents.length) + this.entityUIComponents.length * 4;
        }
        if (this.combatTextUpdate != null) {
            size += this.combatTextUpdate.computeSize();
        }
        if (this.model != null) {
            size += this.model.computeSize();
        }
        if (this.skin != null) {
            size += this.skin.computeSize();
        }
        if (this.item != null) {
            size += this.item.computeSize();
        }
        if (this.equipment != null) {
            size += this.equipment.computeSize();
        }
        if (this.entityStatUpdates != null) {
            int entityStatUpdatesSize = 0;
            for (Map.Entry entry : this.entityStatUpdates.entrySet()) {
                entityStatUpdatesSize += 4 + VarInt.size(((EntityStatUpdate[])entry.getValue()).length) + Arrays.stream((EntityStatUpdate[])entry.getValue()).mapToInt(inner -> inner.computeSize()).sum();
            }
            size += VarInt.size(this.entityStatUpdates.size()) + entityStatUpdatesSize;
        }
        if (this.entityEffectUpdates != null) {
            int entityEffectUpdatesSize = 0;
            for (EntityEffectUpdate entityEffectUpdate : this.entityEffectUpdates) {
                entityEffectUpdatesSize += entityEffectUpdate.computeSize();
            }
            size += VarInt.size(this.entityEffectUpdates.length) + entityEffectUpdatesSize;
        }
        if (this.interactions != null) {
            size += VarInt.size(this.interactions.size()) + this.interactions.size() * 5;
        }
        if (this.soundEventIds != null) {
            size += VarInt.size(this.soundEventIds.length) + this.soundEventIds.length * 4;
        }
        if (this.interactionHint != null) {
            size += PacketIO.stringSize(this.interactionHint);
        }
        if (this.activeAnimations != null) {
            int activeAnimationsSize = 0;
            for (String string : this.activeAnimations) {
                if (string == null) continue;
                activeAnimationsSize += PacketIO.stringSize(string);
            }
            size += VarInt.size(this.activeAnimations.length) + (this.activeAnimations.length + 7) / 8 + activeAnimationsSize;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int i;
        int pos;
        if (buffer.readableBytes() - offset < 211) {
            return ValidationResult.error("Buffer too small: expected at least 211 bytes");
        }
        byte[] nullBits = PacketIO.readBytes(buffer, offset, 3);
        if ((nullBits[0] & 0x10) != 0) {
            int nameplateOffset = buffer.getIntLE(offset + 159);
            if (nameplateOffset < 0) {
                return ValidationResult.error("Invalid offset for Nameplate");
            }
            pos = offset + 211 + nameplateOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Nameplate");
            }
            ValidationResult nameplateResult = Nameplate.validateStructure(buffer, pos);
            if (!nameplateResult.isValid()) {
                return ValidationResult.error("Invalid Nameplate: " + nameplateResult.error());
            }
            pos += Nameplate.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits[0] & 0x20) != 0) {
            int entityUIComponentsOffset = buffer.getIntLE(offset + 163);
            if (entityUIComponentsOffset < 0) {
                return ValidationResult.error("Invalid offset for EntityUIComponents");
            }
            pos = offset + 211 + entityUIComponentsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for EntityUIComponents");
            }
            int entityUIComponentsCount = VarInt.peek(buffer, pos);
            if (entityUIComponentsCount < 0) {
                return ValidationResult.error("Invalid array count for EntityUIComponents");
            }
            if (entityUIComponentsCount > 4096000) {
                return ValidationResult.error("EntityUIComponents exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += entityUIComponentsCount * 4) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading EntityUIComponents");
            }
        }
        if ((nullBits[0] & 0x40) != 0) {
            int combatTextUpdateOffset = buffer.getIntLE(offset + 167);
            if (combatTextUpdateOffset < 0) {
                return ValidationResult.error("Invalid offset for CombatTextUpdate");
            }
            pos = offset + 211 + combatTextUpdateOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for CombatTextUpdate");
            }
            ValidationResult combatTextUpdateResult = CombatTextUpdate.validateStructure(buffer, pos);
            if (!combatTextUpdateResult.isValid()) {
                return ValidationResult.error("Invalid CombatTextUpdate: " + combatTextUpdateResult.error());
            }
            pos += CombatTextUpdate.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits[0] & 0x80) != 0) {
            int modelOffset = buffer.getIntLE(offset + 171);
            if (modelOffset < 0) {
                return ValidationResult.error("Invalid offset for Model");
            }
            pos = offset + 211 + modelOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Model");
            }
            ValidationResult modelResult = Model.validateStructure(buffer, pos);
            if (!modelResult.isValid()) {
                return ValidationResult.error("Invalid Model: " + modelResult.error());
            }
            pos += Model.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits[1] & 1) != 0) {
            int skinOffset = buffer.getIntLE(offset + 175);
            if (skinOffset < 0) {
                return ValidationResult.error("Invalid offset for Skin");
            }
            pos = offset + 211 + skinOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Skin");
            }
            ValidationResult skinResult = PlayerSkin.validateStructure(buffer, pos);
            if (!skinResult.isValid()) {
                return ValidationResult.error("Invalid Skin: " + skinResult.error());
            }
            pos += PlayerSkin.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits[1] & 2) != 0) {
            int itemOffset = buffer.getIntLE(offset + 179);
            if (itemOffset < 0) {
                return ValidationResult.error("Invalid offset for Item");
            }
            pos = offset + 211 + itemOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Item");
            }
            ValidationResult itemResult = ItemWithAllMetadata.validateStructure(buffer, pos);
            if (!itemResult.isValid()) {
                return ValidationResult.error("Invalid Item: " + itemResult.error());
            }
            pos += ItemWithAllMetadata.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits[1] & 4) != 0) {
            int equipmentOffset = buffer.getIntLE(offset + 183);
            if (equipmentOffset < 0) {
                return ValidationResult.error("Invalid offset for Equipment");
            }
            pos = offset + 211 + equipmentOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Equipment");
            }
            ValidationResult equipmentResult = Equipment.validateStructure(buffer, pos);
            if (!equipmentResult.isValid()) {
                return ValidationResult.error("Invalid Equipment: " + equipmentResult.error());
            }
            pos += Equipment.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits[1] & 8) != 0) {
            int entityStatUpdatesOffset = buffer.getIntLE(offset + 187);
            if (entityStatUpdatesOffset < 0) {
                return ValidationResult.error("Invalid offset for EntityStatUpdates");
            }
            pos = offset + 211 + entityStatUpdatesOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for EntityStatUpdates");
            }
            int entityStatUpdatesCount = VarInt.peek(buffer, pos);
            if (entityStatUpdatesCount < 0) {
                return ValidationResult.error("Invalid dictionary count for EntityStatUpdates");
            }
            if (entityStatUpdatesCount > 4096000) {
                return ValidationResult.error("EntityStatUpdates exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < entityStatUpdatesCount; ++i) {
                if ((pos += 4) > buffer.writerIndex()) {
                    return ValidationResult.error("Buffer overflow reading key");
                }
                int valueArrCount = VarInt.peek(buffer, pos);
                if (valueArrCount < 0) {
                    return ValidationResult.error("Invalid array count for value");
                }
                pos += VarInt.length(buffer, pos);
                for (int valueArrIdx = 0; valueArrIdx < valueArrCount; ++valueArrIdx) {
                    pos += EntityStatUpdate.computeBytesConsumed(buffer, pos);
                }
            }
        }
        if ((nullBits[1] & 0x10) != 0) {
            int entityEffectUpdatesOffset = buffer.getIntLE(offset + 191);
            if (entityEffectUpdatesOffset < 0) {
                return ValidationResult.error("Invalid offset for EntityEffectUpdates");
            }
            pos = offset + 211 + entityEffectUpdatesOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for EntityEffectUpdates");
            }
            int entityEffectUpdatesCount = VarInt.peek(buffer, pos);
            if (entityEffectUpdatesCount < 0) {
                return ValidationResult.error("Invalid array count for EntityEffectUpdates");
            }
            if (entityEffectUpdatesCount > 4096000) {
                return ValidationResult.error("EntityEffectUpdates exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < entityEffectUpdatesCount; ++i) {
                ValidationResult structResult = EntityEffectUpdate.validateStructure(buffer, pos);
                if (!structResult.isValid()) {
                    return ValidationResult.error("Invalid EntityEffectUpdate in EntityEffectUpdates[" + i + "]: " + structResult.error());
                }
                pos += EntityEffectUpdate.computeBytesConsumed(buffer, pos);
            }
        }
        if ((nullBits[1] & 0x20) != 0) {
            int interactionsOffset = buffer.getIntLE(offset + 195);
            if (interactionsOffset < 0) {
                return ValidationResult.error("Invalid offset for Interactions");
            }
            pos = offset + 211 + interactionsOffset;
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
        if ((nullBits[1] & 0x40) != 0) {
            int soundEventIdsOffset = buffer.getIntLE(offset + 199);
            if (soundEventIdsOffset < 0) {
                return ValidationResult.error("Invalid offset for SoundEventIds");
            }
            pos = offset + 211 + soundEventIdsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for SoundEventIds");
            }
            int soundEventIdsCount = VarInt.peek(buffer, pos);
            if (soundEventIdsCount < 0) {
                return ValidationResult.error("Invalid array count for SoundEventIds");
            }
            if (soundEventIdsCount > 4096000) {
                return ValidationResult.error("SoundEventIds exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += soundEventIdsCount * 4) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading SoundEventIds");
            }
        }
        if ((nullBits[1] & 0x80) != 0) {
            int interactionHintOffset = buffer.getIntLE(offset + 203);
            if (interactionHintOffset < 0) {
                return ValidationResult.error("Invalid offset for InteractionHint");
            }
            pos = offset + 211 + interactionHintOffset;
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
        if ((nullBits[2] & 1) != 0) {
            int activeAnimationsOffset = buffer.getIntLE(offset + 207);
            if (activeAnimationsOffset < 0) {
                return ValidationResult.error("Invalid offset for ActiveAnimations");
            }
            pos = offset + 211 + activeAnimationsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for ActiveAnimations");
            }
            int activeAnimationsCount = VarInt.peek(buffer, pos);
            if (activeAnimationsCount < 0) {
                return ValidationResult.error("Invalid array count for ActiveAnimations");
            }
            if (activeAnimationsCount > 4096000) {
                return ValidationResult.error("ActiveAnimations exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < activeAnimationsCount; ++i) {
                int strLen = VarInt.peek(buffer, pos);
                if (strLen < 0) {
                    return ValidationResult.error("Invalid string length in ActiveAnimations");
                }
                pos += VarInt.length(buffer, pos);
                if ((pos += strLen) <= buffer.writerIndex()) continue;
                return ValidationResult.error("Buffer overflow reading string in ActiveAnimations");
            }
        }
        return ValidationResult.OK;
    }

    public ComponentUpdate clone() {
        ComponentUpdate copy = new ComponentUpdate();
        copy.type = this.type;
        copy.nameplate = this.nameplate != null ? this.nameplate.clone() : null;
        copy.entityUIComponents = this.entityUIComponents != null ? Arrays.copyOf(this.entityUIComponents, this.entityUIComponents.length) : null;
        copy.combatTextUpdate = this.combatTextUpdate != null ? this.combatTextUpdate.clone() : null;
        copy.model = this.model != null ? this.model.clone() : null;
        copy.skin = this.skin != null ? this.skin.clone() : null;
        copy.item = this.item != null ? this.item.clone() : null;
        copy.blockId = this.blockId;
        copy.entityScale = this.entityScale;
        Equipment equipment = copy.equipment = this.equipment != null ? this.equipment.clone() : null;
        if (this.entityStatUpdates != null) {
            HashMap<Integer, EntityStatUpdate[]> m = new HashMap<Integer, EntityStatUpdate[]>();
            for (Map.Entry<Integer, EntityStatUpdate[]> e2 : this.entityStatUpdates.entrySet()) {
                m.put(e2.getKey(), (EntityStatUpdate[])Arrays.stream(e2.getValue()).map(x -> x.clone()).toArray(EntityStatUpdate[]::new));
            }
            copy.entityStatUpdates = m;
        }
        copy.transform = this.transform != null ? this.transform.clone() : null;
        copy.movementStates = this.movementStates != null ? this.movementStates.clone() : null;
        copy.entityEffectUpdates = this.entityEffectUpdates != null ? (EntityEffectUpdate[])Arrays.stream(this.entityEffectUpdates).map(e -> e.clone()).toArray(EntityEffectUpdate[]::new) : null;
        copy.interactions = this.interactions != null ? new HashMap<InteractionType, Integer>(this.interactions) : null;
        copy.dynamicLight = this.dynamicLight != null ? this.dynamicLight.clone() : null;
        copy.hitboxCollisionConfigIndex = this.hitboxCollisionConfigIndex;
        copy.repulsionConfigIndex = this.repulsionConfigIndex;
        copy.predictionId = this.predictionId;
        copy.soundEventIds = this.soundEventIds != null ? Arrays.copyOf(this.soundEventIds, this.soundEventIds.length) : null;
        copy.interactionHint = this.interactionHint;
        copy.mounted = this.mounted != null ? this.mounted.clone() : null;
        copy.activeAnimations = this.activeAnimations != null ? Arrays.copyOf(this.activeAnimations, this.activeAnimations.length) : null;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ComponentUpdate)) {
            return false;
        }
        ComponentUpdate other = (ComponentUpdate)obj;
        return Objects.equals((Object)this.type, (Object)other.type) && Objects.equals(this.nameplate, other.nameplate) && Arrays.equals(this.entityUIComponents, other.entityUIComponents) && Objects.equals(this.combatTextUpdate, other.combatTextUpdate) && Objects.equals(this.model, other.model) && Objects.equals(this.skin, other.skin) && Objects.equals(this.item, other.item) && this.blockId == other.blockId && this.entityScale == other.entityScale && Objects.equals(this.equipment, other.equipment) && Objects.equals(this.entityStatUpdates, other.entityStatUpdates) && Objects.equals(this.transform, other.transform) && Objects.equals(this.movementStates, other.movementStates) && Arrays.equals(this.entityEffectUpdates, other.entityEffectUpdates) && Objects.equals(this.interactions, other.interactions) && Objects.equals(this.dynamicLight, other.dynamicLight) && this.hitboxCollisionConfigIndex == other.hitboxCollisionConfigIndex && this.repulsionConfigIndex == other.repulsionConfigIndex && Objects.equals(this.predictionId, other.predictionId) && Arrays.equals(this.soundEventIds, other.soundEventIds) && Objects.equals(this.interactionHint, other.interactionHint) && Objects.equals(this.mounted, other.mounted) && Arrays.equals(this.activeAnimations, other.activeAnimations);
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + Objects.hashCode((Object)this.type);
        result = 31 * result + Objects.hashCode(this.nameplate);
        result = 31 * result + Arrays.hashCode(this.entityUIComponents);
        result = 31 * result + Objects.hashCode(this.combatTextUpdate);
        result = 31 * result + Objects.hashCode(this.model);
        result = 31 * result + Objects.hashCode(this.skin);
        result = 31 * result + Objects.hashCode(this.item);
        result = 31 * result + Integer.hashCode(this.blockId);
        result = 31 * result + Float.hashCode(this.entityScale);
        result = 31 * result + Objects.hashCode(this.equipment);
        result = 31 * result + Objects.hashCode(this.entityStatUpdates);
        result = 31 * result + Objects.hashCode(this.transform);
        result = 31 * result + Objects.hashCode(this.movementStates);
        result = 31 * result + Arrays.hashCode(this.entityEffectUpdates);
        result = 31 * result + Objects.hashCode(this.interactions);
        result = 31 * result + Objects.hashCode(this.dynamicLight);
        result = 31 * result + Integer.hashCode(this.hitboxCollisionConfigIndex);
        result = 31 * result + Integer.hashCode(this.repulsionConfigIndex);
        result = 31 * result + Objects.hashCode(this.predictionId);
        result = 31 * result + Arrays.hashCode(this.soundEventIds);
        result = 31 * result + Objects.hashCode(this.interactionHint);
        result = 31 * result + Objects.hashCode(this.mounted);
        result = 31 * result + Arrays.hashCode(this.activeAnimations);
        return result;
    }
}

