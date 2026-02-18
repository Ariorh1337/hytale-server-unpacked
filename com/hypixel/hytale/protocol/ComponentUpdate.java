/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.ActiveAnimationsUpdate;
import com.hypixel.hytale.protocol.AudioUpdate;
import com.hypixel.hytale.protocol.BlockUpdate;
import com.hypixel.hytale.protocol.CombatTextUpdate;
import com.hypixel.hytale.protocol.DynamicLightUpdate;
import com.hypixel.hytale.protocol.EntityEffectsUpdate;
import com.hypixel.hytale.protocol.EntityStatsUpdate;
import com.hypixel.hytale.protocol.EquipmentUpdate;
import com.hypixel.hytale.protocol.HitboxCollisionUpdate;
import com.hypixel.hytale.protocol.IntangibleUpdate;
import com.hypixel.hytale.protocol.InteractableUpdate;
import com.hypixel.hytale.protocol.InteractionsUpdate;
import com.hypixel.hytale.protocol.InvulnerableUpdate;
import com.hypixel.hytale.protocol.ItemUpdate;
import com.hypixel.hytale.protocol.ModelUpdate;
import com.hypixel.hytale.protocol.MountedUpdate;
import com.hypixel.hytale.protocol.MovementStatesUpdate;
import com.hypixel.hytale.protocol.NameplateUpdate;
import com.hypixel.hytale.protocol.NewSpawnUpdate;
import com.hypixel.hytale.protocol.PlayerSkinUpdate;
import com.hypixel.hytale.protocol.PredictionUpdate;
import com.hypixel.hytale.protocol.PropUpdate;
import com.hypixel.hytale.protocol.RepulsionUpdate;
import com.hypixel.hytale.protocol.RespondToHitUpdate;
import com.hypixel.hytale.protocol.TransformUpdate;
import com.hypixel.hytale.protocol.UIComponentsUpdate;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;

public abstract class ComponentUpdate {
    public static final int MAX_SIZE = 1677721605;

    @Nonnull
    public static ComponentUpdate deserialize(@Nonnull ByteBuf buf, int offset) {
        int typeId = VarInt.peek(buf, offset);
        int typeIdLen = VarInt.length(buf, offset);
        return switch (typeId) {
            case 0 -> NameplateUpdate.deserialize(buf, offset + typeIdLen);
            case 1 -> UIComponentsUpdate.deserialize(buf, offset + typeIdLen);
            case 2 -> CombatTextUpdate.deserialize(buf, offset + typeIdLen);
            case 3 -> ModelUpdate.deserialize(buf, offset + typeIdLen);
            case 4 -> PlayerSkinUpdate.deserialize(buf, offset + typeIdLen);
            case 5 -> ItemUpdate.deserialize(buf, offset + typeIdLen);
            case 6 -> BlockUpdate.deserialize(buf, offset + typeIdLen);
            case 7 -> EquipmentUpdate.deserialize(buf, offset + typeIdLen);
            case 8 -> EntityStatsUpdate.deserialize(buf, offset + typeIdLen);
            case 9 -> TransformUpdate.deserialize(buf, offset + typeIdLen);
            case 10 -> MovementStatesUpdate.deserialize(buf, offset + typeIdLen);
            case 11 -> EntityEffectsUpdate.deserialize(buf, offset + typeIdLen);
            case 12 -> InteractionsUpdate.deserialize(buf, offset + typeIdLen);
            case 13 -> DynamicLightUpdate.deserialize(buf, offset + typeIdLen);
            case 14 -> InteractableUpdate.deserialize(buf, offset + typeIdLen);
            case 15 -> IntangibleUpdate.deserialize(buf, offset + typeIdLen);
            case 16 -> InvulnerableUpdate.deserialize(buf, offset + typeIdLen);
            case 17 -> RespondToHitUpdate.deserialize(buf, offset + typeIdLen);
            case 18 -> HitboxCollisionUpdate.deserialize(buf, offset + typeIdLen);
            case 19 -> RepulsionUpdate.deserialize(buf, offset + typeIdLen);
            case 20 -> PredictionUpdate.deserialize(buf, offset + typeIdLen);
            case 21 -> AudioUpdate.deserialize(buf, offset + typeIdLen);
            case 22 -> MountedUpdate.deserialize(buf, offset + typeIdLen);
            case 23 -> NewSpawnUpdate.deserialize(buf, offset + typeIdLen);
            case 24 -> ActiveAnimationsUpdate.deserialize(buf, offset + typeIdLen);
            case 25 -> PropUpdate.deserialize(buf, offset + typeIdLen);
            default -> throw ProtocolException.unknownPolymorphicType("ComponentUpdate", typeId);
        };
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int typeId = VarInt.peek(buf, offset);
        int typeIdLen = VarInt.length(buf, offset);
        return typeIdLen + (switch (typeId) {
            case 0 -> NameplateUpdate.computeBytesConsumed(buf, offset + typeIdLen);
            case 1 -> UIComponentsUpdate.computeBytesConsumed(buf, offset + typeIdLen);
            case 2 -> CombatTextUpdate.computeBytesConsumed(buf, offset + typeIdLen);
            case 3 -> ModelUpdate.computeBytesConsumed(buf, offset + typeIdLen);
            case 4 -> PlayerSkinUpdate.computeBytesConsumed(buf, offset + typeIdLen);
            case 5 -> ItemUpdate.computeBytesConsumed(buf, offset + typeIdLen);
            case 6 -> BlockUpdate.computeBytesConsumed(buf, offset + typeIdLen);
            case 7 -> EquipmentUpdate.computeBytesConsumed(buf, offset + typeIdLen);
            case 8 -> EntityStatsUpdate.computeBytesConsumed(buf, offset + typeIdLen);
            case 9 -> TransformUpdate.computeBytesConsumed(buf, offset + typeIdLen);
            case 10 -> MovementStatesUpdate.computeBytesConsumed(buf, offset + typeIdLen);
            case 11 -> EntityEffectsUpdate.computeBytesConsumed(buf, offset + typeIdLen);
            case 12 -> InteractionsUpdate.computeBytesConsumed(buf, offset + typeIdLen);
            case 13 -> DynamicLightUpdate.computeBytesConsumed(buf, offset + typeIdLen);
            case 14 -> InteractableUpdate.computeBytesConsumed(buf, offset + typeIdLen);
            case 15 -> IntangibleUpdate.computeBytesConsumed(buf, offset + typeIdLen);
            case 16 -> InvulnerableUpdate.computeBytesConsumed(buf, offset + typeIdLen);
            case 17 -> RespondToHitUpdate.computeBytesConsumed(buf, offset + typeIdLen);
            case 18 -> HitboxCollisionUpdate.computeBytesConsumed(buf, offset + typeIdLen);
            case 19 -> RepulsionUpdate.computeBytesConsumed(buf, offset + typeIdLen);
            case 20 -> PredictionUpdate.computeBytesConsumed(buf, offset + typeIdLen);
            case 21 -> AudioUpdate.computeBytesConsumed(buf, offset + typeIdLen);
            case 22 -> MountedUpdate.computeBytesConsumed(buf, offset + typeIdLen);
            case 23 -> NewSpawnUpdate.computeBytesConsumed(buf, offset + typeIdLen);
            case 24 -> ActiveAnimationsUpdate.computeBytesConsumed(buf, offset + typeIdLen);
            case 25 -> PropUpdate.computeBytesConsumed(buf, offset + typeIdLen);
            default -> throw ProtocolException.unknownPolymorphicType("ComponentUpdate", typeId);
        });
    }

    public int getTypeId() {
        ComponentUpdate componentUpdate = this;
        if (componentUpdate instanceof NameplateUpdate) {
            NameplateUpdate sub = (NameplateUpdate)componentUpdate;
            return 0;
        }
        componentUpdate = this;
        if (componentUpdate instanceof UIComponentsUpdate) {
            UIComponentsUpdate sub = (UIComponentsUpdate)componentUpdate;
            return 1;
        }
        componentUpdate = this;
        if (componentUpdate instanceof CombatTextUpdate) {
            CombatTextUpdate sub = (CombatTextUpdate)componentUpdate;
            return 2;
        }
        componentUpdate = this;
        if (componentUpdate instanceof ModelUpdate) {
            ModelUpdate sub = (ModelUpdate)componentUpdate;
            return 3;
        }
        componentUpdate = this;
        if (componentUpdate instanceof PlayerSkinUpdate) {
            PlayerSkinUpdate sub = (PlayerSkinUpdate)componentUpdate;
            return 4;
        }
        componentUpdate = this;
        if (componentUpdate instanceof ItemUpdate) {
            ItemUpdate sub = (ItemUpdate)componentUpdate;
            return 5;
        }
        componentUpdate = this;
        if (componentUpdate instanceof BlockUpdate) {
            BlockUpdate sub = (BlockUpdate)componentUpdate;
            return 6;
        }
        componentUpdate = this;
        if (componentUpdate instanceof EquipmentUpdate) {
            EquipmentUpdate sub = (EquipmentUpdate)componentUpdate;
            return 7;
        }
        componentUpdate = this;
        if (componentUpdate instanceof EntityStatsUpdate) {
            EntityStatsUpdate sub = (EntityStatsUpdate)componentUpdate;
            return 8;
        }
        componentUpdate = this;
        if (componentUpdate instanceof TransformUpdate) {
            TransformUpdate sub = (TransformUpdate)componentUpdate;
            return 9;
        }
        componentUpdate = this;
        if (componentUpdate instanceof MovementStatesUpdate) {
            MovementStatesUpdate sub = (MovementStatesUpdate)componentUpdate;
            return 10;
        }
        componentUpdate = this;
        if (componentUpdate instanceof EntityEffectsUpdate) {
            EntityEffectsUpdate sub = (EntityEffectsUpdate)componentUpdate;
            return 11;
        }
        componentUpdate = this;
        if (componentUpdate instanceof InteractionsUpdate) {
            InteractionsUpdate sub = (InteractionsUpdate)componentUpdate;
            return 12;
        }
        componentUpdate = this;
        if (componentUpdate instanceof DynamicLightUpdate) {
            DynamicLightUpdate sub = (DynamicLightUpdate)componentUpdate;
            return 13;
        }
        componentUpdate = this;
        if (componentUpdate instanceof InteractableUpdate) {
            InteractableUpdate sub = (InteractableUpdate)componentUpdate;
            return 14;
        }
        componentUpdate = this;
        if (componentUpdate instanceof IntangibleUpdate) {
            IntangibleUpdate sub = (IntangibleUpdate)componentUpdate;
            return 15;
        }
        componentUpdate = this;
        if (componentUpdate instanceof InvulnerableUpdate) {
            InvulnerableUpdate sub = (InvulnerableUpdate)componentUpdate;
            return 16;
        }
        componentUpdate = this;
        if (componentUpdate instanceof RespondToHitUpdate) {
            RespondToHitUpdate sub = (RespondToHitUpdate)componentUpdate;
            return 17;
        }
        componentUpdate = this;
        if (componentUpdate instanceof HitboxCollisionUpdate) {
            HitboxCollisionUpdate sub = (HitboxCollisionUpdate)componentUpdate;
            return 18;
        }
        componentUpdate = this;
        if (componentUpdate instanceof RepulsionUpdate) {
            RepulsionUpdate sub = (RepulsionUpdate)componentUpdate;
            return 19;
        }
        componentUpdate = this;
        if (componentUpdate instanceof PredictionUpdate) {
            PredictionUpdate sub = (PredictionUpdate)componentUpdate;
            return 20;
        }
        componentUpdate = this;
        if (componentUpdate instanceof AudioUpdate) {
            AudioUpdate sub = (AudioUpdate)componentUpdate;
            return 21;
        }
        componentUpdate = this;
        if (componentUpdate instanceof MountedUpdate) {
            MountedUpdate sub = (MountedUpdate)componentUpdate;
            return 22;
        }
        componentUpdate = this;
        if (componentUpdate instanceof NewSpawnUpdate) {
            NewSpawnUpdate sub = (NewSpawnUpdate)componentUpdate;
            return 23;
        }
        componentUpdate = this;
        if (componentUpdate instanceof ActiveAnimationsUpdate) {
            ActiveAnimationsUpdate sub = (ActiveAnimationsUpdate)componentUpdate;
            return 24;
        }
        componentUpdate = this;
        if (componentUpdate instanceof PropUpdate) {
            PropUpdate sub = (PropUpdate)componentUpdate;
            return 25;
        }
        throw new IllegalStateException("Unknown subtype: " + this.getClass().getName());
    }

    public abstract int serialize(@Nonnull ByteBuf var1);

    public abstract int computeSize();

    public int serializeWithTypeId(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        VarInt.write(buf, this.getTypeId());
        this.serialize(buf);
        return buf.writerIndex() - startPos;
    }

    public int computeSizeWithTypeId() {
        return VarInt.size(this.getTypeId()) + this.computeSize();
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int typeId = VarInt.peek(buffer, offset);
        int typeIdLen = VarInt.length(buffer, offset);
        return switch (typeId) {
            case 0 -> NameplateUpdate.validateStructure(buffer, offset + typeIdLen);
            case 1 -> UIComponentsUpdate.validateStructure(buffer, offset + typeIdLen);
            case 2 -> CombatTextUpdate.validateStructure(buffer, offset + typeIdLen);
            case 3 -> ModelUpdate.validateStructure(buffer, offset + typeIdLen);
            case 4 -> PlayerSkinUpdate.validateStructure(buffer, offset + typeIdLen);
            case 5 -> ItemUpdate.validateStructure(buffer, offset + typeIdLen);
            case 6 -> BlockUpdate.validateStructure(buffer, offset + typeIdLen);
            case 7 -> EquipmentUpdate.validateStructure(buffer, offset + typeIdLen);
            case 8 -> EntityStatsUpdate.validateStructure(buffer, offset + typeIdLen);
            case 9 -> TransformUpdate.validateStructure(buffer, offset + typeIdLen);
            case 10 -> MovementStatesUpdate.validateStructure(buffer, offset + typeIdLen);
            case 11 -> EntityEffectsUpdate.validateStructure(buffer, offset + typeIdLen);
            case 12 -> InteractionsUpdate.validateStructure(buffer, offset + typeIdLen);
            case 13 -> DynamicLightUpdate.validateStructure(buffer, offset + typeIdLen);
            case 14 -> InteractableUpdate.validateStructure(buffer, offset + typeIdLen);
            case 15 -> IntangibleUpdate.validateStructure(buffer, offset + typeIdLen);
            case 16 -> InvulnerableUpdate.validateStructure(buffer, offset + typeIdLen);
            case 17 -> RespondToHitUpdate.validateStructure(buffer, offset + typeIdLen);
            case 18 -> HitboxCollisionUpdate.validateStructure(buffer, offset + typeIdLen);
            case 19 -> RepulsionUpdate.validateStructure(buffer, offset + typeIdLen);
            case 20 -> PredictionUpdate.validateStructure(buffer, offset + typeIdLen);
            case 21 -> AudioUpdate.validateStructure(buffer, offset + typeIdLen);
            case 22 -> MountedUpdate.validateStructure(buffer, offset + typeIdLen);
            case 23 -> NewSpawnUpdate.validateStructure(buffer, offset + typeIdLen);
            case 24 -> ActiveAnimationsUpdate.validateStructure(buffer, offset + typeIdLen);
            case 25 -> PropUpdate.validateStructure(buffer, offset + typeIdLen);
            default -> ValidationResult.error("Unknown polymorphic type ID " + typeId + " for ComponentUpdate");
        };
    }
}

