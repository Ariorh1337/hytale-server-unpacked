/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.ApplyEffectInteraction;
import com.hypixel.hytale.protocol.ApplyForceInteraction;
import com.hypixel.hytale.protocol.BlockConditionInteraction;
import com.hypixel.hytale.protocol.BreakBlockInteraction;
import com.hypixel.hytale.protocol.BuilderToolInteraction;
import com.hypixel.hytale.protocol.CameraInteraction;
import com.hypixel.hytale.protocol.CancelChainInteraction;
import com.hypixel.hytale.protocol.ChainFlagInteraction;
import com.hypixel.hytale.protocol.ChainingInteraction;
import com.hypixel.hytale.protocol.ChangeActiveSlotInteraction;
import com.hypixel.hytale.protocol.ChangeBlockInteraction;
import com.hypixel.hytale.protocol.ChangeStatInteraction;
import com.hypixel.hytale.protocol.ChangeStateInteraction;
import com.hypixel.hytale.protocol.ChargingInteraction;
import com.hypixel.hytale.protocol.ClearEntityEffectInteraction;
import com.hypixel.hytale.protocol.ConditionInteraction;
import com.hypixel.hytale.protocol.CooldownConditionInteraction;
import com.hypixel.hytale.protocol.DamageEntityInteraction;
import com.hypixel.hytale.protocol.EffectConditionInteraction;
import com.hypixel.hytale.protocol.FirstClickInteraction;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.IncrementCooldownInteraction;
import com.hypixel.hytale.protocol.InteractionCameraSettings;
import com.hypixel.hytale.protocol.InteractionEffects;
import com.hypixel.hytale.protocol.InteractionRules;
import com.hypixel.hytale.protocol.InteractionSettings;
import com.hypixel.hytale.protocol.MemoriesConditionInteraction;
import com.hypixel.hytale.protocol.ModifyInventoryInteraction;
import com.hypixel.hytale.protocol.MovementConditionInteraction;
import com.hypixel.hytale.protocol.ParallelInteraction;
import com.hypixel.hytale.protocol.PickBlockInteraction;
import com.hypixel.hytale.protocol.PlaceBlockInteraction;
import com.hypixel.hytale.protocol.ProjectileInteraction;
import com.hypixel.hytale.protocol.RemoveEntityInteraction;
import com.hypixel.hytale.protocol.RepeatInteraction;
import com.hypixel.hytale.protocol.ReplaceInteraction;
import com.hypixel.hytale.protocol.ResetCooldownInteraction;
import com.hypixel.hytale.protocol.RunRootInteraction;
import com.hypixel.hytale.protocol.SelectInteraction;
import com.hypixel.hytale.protocol.SerialInteraction;
import com.hypixel.hytale.protocol.SimpleBlockInteraction;
import com.hypixel.hytale.protocol.SimpleInteraction;
import com.hypixel.hytale.protocol.SpawnDeployableFromRaycastInteraction;
import com.hypixel.hytale.protocol.StatsConditionInteraction;
import com.hypixel.hytale.protocol.ToggleGliderInteraction;
import com.hypixel.hytale.protocol.TriggerCooldownInteraction;
import com.hypixel.hytale.protocol.UseBlockInteraction;
import com.hypixel.hytale.protocol.UseEntityInteraction;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.protocol.WieldingInteraction;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class Interaction {
    public static final int MAX_SIZE = 1677721605;
    @Nonnull
    public WaitForDataFrom waitForDataFrom = WaitForDataFrom.Client;
    @Nullable
    public InteractionEffects effects;
    public float horizontalSpeedMultiplier;
    public float runTime;
    public boolean cancelOnItemChange;
    @Nullable
    public Map<GameMode, InteractionSettings> settings;
    @Nullable
    public InteractionRules rules;
    @Nullable
    public int[] tags;
    @Nullable
    public InteractionCameraSettings camera;

    @Nonnull
    public static Interaction deserialize(@Nonnull ByteBuf buf, int offset) {
        int typeId = VarInt.peek(buf, offset);
        int typeIdLen = VarInt.length(buf, offset);
        return switch (typeId) {
            case 0 -> SimpleBlockInteraction.deserialize(buf, offset + typeIdLen);
            case 1 -> SimpleInteraction.deserialize(buf, offset + typeIdLen);
            case 2 -> PlaceBlockInteraction.deserialize(buf, offset + typeIdLen);
            case 3 -> BreakBlockInteraction.deserialize(buf, offset + typeIdLen);
            case 4 -> PickBlockInteraction.deserialize(buf, offset + typeIdLen);
            case 5 -> UseBlockInteraction.deserialize(buf, offset + typeIdLen);
            case 6 -> UseEntityInteraction.deserialize(buf, offset + typeIdLen);
            case 7 -> BuilderToolInteraction.deserialize(buf, offset + typeIdLen);
            case 8 -> ModifyInventoryInteraction.deserialize(buf, offset + typeIdLen);
            case 9 -> ChargingInteraction.deserialize(buf, offset + typeIdLen);
            case 10 -> WieldingInteraction.deserialize(buf, offset + typeIdLen);
            case 11 -> ChainingInteraction.deserialize(buf, offset + typeIdLen);
            case 12 -> ConditionInteraction.deserialize(buf, offset + typeIdLen);
            case 13 -> StatsConditionInteraction.deserialize(buf, offset + typeIdLen);
            case 14 -> BlockConditionInteraction.deserialize(buf, offset + typeIdLen);
            case 15 -> ReplaceInteraction.deserialize(buf, offset + typeIdLen);
            case 16 -> ChangeBlockInteraction.deserialize(buf, offset + typeIdLen);
            case 17 -> ChangeStateInteraction.deserialize(buf, offset + typeIdLen);
            case 18 -> FirstClickInteraction.deserialize(buf, offset + typeIdLen);
            case 20 -> SelectInteraction.deserialize(buf, offset + typeIdLen);
            case 21 -> DamageEntityInteraction.deserialize(buf, offset + typeIdLen);
            case 22 -> RepeatInteraction.deserialize(buf, offset + typeIdLen);
            case 23 -> ParallelInteraction.deserialize(buf, offset + typeIdLen);
            case 24 -> ChangeActiveSlotInteraction.deserialize(buf, offset + typeIdLen);
            case 25 -> EffectConditionInteraction.deserialize(buf, offset + typeIdLen);
            case 26 -> ApplyForceInteraction.deserialize(buf, offset + typeIdLen);
            case 27 -> ApplyEffectInteraction.deserialize(buf, offset + typeIdLen);
            case 28 -> ClearEntityEffectInteraction.deserialize(buf, offset + typeIdLen);
            case 29 -> SerialInteraction.deserialize(buf, offset + typeIdLen);
            case 30 -> ChangeStatInteraction.deserialize(buf, offset + typeIdLen);
            case 31 -> MovementConditionInteraction.deserialize(buf, offset + typeIdLen);
            case 32 -> ProjectileInteraction.deserialize(buf, offset + typeIdLen);
            case 33 -> RemoveEntityInteraction.deserialize(buf, offset + typeIdLen);
            case 34 -> ResetCooldownInteraction.deserialize(buf, offset + typeIdLen);
            case 35 -> TriggerCooldownInteraction.deserialize(buf, offset + typeIdLen);
            case 36 -> CooldownConditionInteraction.deserialize(buf, offset + typeIdLen);
            case 37 -> ChainFlagInteraction.deserialize(buf, offset + typeIdLen);
            case 38 -> IncrementCooldownInteraction.deserialize(buf, offset + typeIdLen);
            case 39 -> CancelChainInteraction.deserialize(buf, offset + typeIdLen);
            case 40 -> RunRootInteraction.deserialize(buf, offset + typeIdLen);
            case 41 -> CameraInteraction.deserialize(buf, offset + typeIdLen);
            case 42 -> SpawnDeployableFromRaycastInteraction.deserialize(buf, offset + typeIdLen);
            case 43 -> MemoriesConditionInteraction.deserialize(buf, offset + typeIdLen);
            case 44 -> ToggleGliderInteraction.deserialize(buf, offset + typeIdLen);
            default -> throw ProtocolException.unknownPolymorphicType("Interaction", typeId);
        };
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int typeId = VarInt.peek(buf, offset);
        int typeIdLen = VarInt.length(buf, offset);
        return typeIdLen + (switch (typeId) {
            case 0 -> SimpleBlockInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 1 -> SimpleInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 2 -> PlaceBlockInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 3 -> BreakBlockInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 4 -> PickBlockInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 5 -> UseBlockInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 6 -> UseEntityInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 7 -> BuilderToolInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 8 -> ModifyInventoryInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 9 -> ChargingInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 10 -> WieldingInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 11 -> ChainingInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 12 -> ConditionInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 13 -> StatsConditionInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 14 -> BlockConditionInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 15 -> ReplaceInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 16 -> ChangeBlockInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 17 -> ChangeStateInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 18 -> FirstClickInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 20 -> SelectInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 21 -> DamageEntityInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 22 -> RepeatInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 23 -> ParallelInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 24 -> ChangeActiveSlotInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 25 -> EffectConditionInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 26 -> ApplyForceInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 27 -> ApplyEffectInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 28 -> ClearEntityEffectInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 29 -> SerialInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 30 -> ChangeStatInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 31 -> MovementConditionInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 32 -> ProjectileInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 33 -> RemoveEntityInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 34 -> ResetCooldownInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 35 -> TriggerCooldownInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 36 -> CooldownConditionInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 37 -> ChainFlagInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 38 -> IncrementCooldownInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 39 -> CancelChainInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 40 -> RunRootInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 41 -> CameraInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 42 -> SpawnDeployableFromRaycastInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 43 -> MemoriesConditionInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            case 44 -> ToggleGliderInteraction.computeBytesConsumed(buf, offset + typeIdLen);
            default -> throw ProtocolException.unknownPolymorphicType("Interaction", typeId);
        });
    }

    public int getTypeId() {
        Interaction interaction = this;
        if (interaction instanceof BreakBlockInteraction) {
            BreakBlockInteraction sub = (BreakBlockInteraction)interaction;
            return 3;
        }
        interaction = this;
        if (interaction instanceof PickBlockInteraction) {
            PickBlockInteraction sub = (PickBlockInteraction)interaction;
            return 4;
        }
        interaction = this;
        if (interaction instanceof UseBlockInteraction) {
            UseBlockInteraction sub = (UseBlockInteraction)interaction;
            return 5;
        }
        interaction = this;
        if (interaction instanceof BlockConditionInteraction) {
            BlockConditionInteraction sub = (BlockConditionInteraction)interaction;
            return 14;
        }
        interaction = this;
        if (interaction instanceof ChangeBlockInteraction) {
            ChangeBlockInteraction sub = (ChangeBlockInteraction)interaction;
            return 16;
        }
        interaction = this;
        if (interaction instanceof ChangeStateInteraction) {
            ChangeStateInteraction sub = (ChangeStateInteraction)interaction;
            return 17;
        }
        interaction = this;
        if (interaction instanceof SimpleBlockInteraction) {
            SimpleBlockInteraction sub = (SimpleBlockInteraction)interaction;
            return 0;
        }
        interaction = this;
        if (interaction instanceof PlaceBlockInteraction) {
            PlaceBlockInteraction sub = (PlaceBlockInteraction)interaction;
            return 2;
        }
        interaction = this;
        if (interaction instanceof UseEntityInteraction) {
            UseEntityInteraction sub = (UseEntityInteraction)interaction;
            return 6;
        }
        interaction = this;
        if (interaction instanceof BuilderToolInteraction) {
            BuilderToolInteraction sub = (BuilderToolInteraction)interaction;
            return 7;
        }
        interaction = this;
        if (interaction instanceof ModifyInventoryInteraction) {
            ModifyInventoryInteraction sub = (ModifyInventoryInteraction)interaction;
            return 8;
        }
        interaction = this;
        if (interaction instanceof WieldingInteraction) {
            WieldingInteraction sub = (WieldingInteraction)interaction;
            return 10;
        }
        interaction = this;
        if (interaction instanceof ConditionInteraction) {
            ConditionInteraction sub = (ConditionInteraction)interaction;
            return 12;
        }
        interaction = this;
        if (interaction instanceof StatsConditionInteraction) {
            StatsConditionInteraction sub = (StatsConditionInteraction)interaction;
            return 13;
        }
        interaction = this;
        if (interaction instanceof SelectInteraction) {
            SelectInteraction sub = (SelectInteraction)interaction;
            return 20;
        }
        interaction = this;
        if (interaction instanceof RepeatInteraction) {
            RepeatInteraction sub = (RepeatInteraction)interaction;
            return 22;
        }
        interaction = this;
        if (interaction instanceof EffectConditionInteraction) {
            EffectConditionInteraction sub = (EffectConditionInteraction)interaction;
            return 25;
        }
        interaction = this;
        if (interaction instanceof ApplyForceInteraction) {
            ApplyForceInteraction sub = (ApplyForceInteraction)interaction;
            return 26;
        }
        interaction = this;
        if (interaction instanceof ApplyEffectInteraction) {
            ApplyEffectInteraction sub = (ApplyEffectInteraction)interaction;
            return 27;
        }
        interaction = this;
        if (interaction instanceof ClearEntityEffectInteraction) {
            ClearEntityEffectInteraction sub = (ClearEntityEffectInteraction)interaction;
            return 28;
        }
        interaction = this;
        if (interaction instanceof ChangeStatInteraction) {
            ChangeStatInteraction sub = (ChangeStatInteraction)interaction;
            return 30;
        }
        interaction = this;
        if (interaction instanceof MovementConditionInteraction) {
            MovementConditionInteraction sub = (MovementConditionInteraction)interaction;
            return 31;
        }
        interaction = this;
        if (interaction instanceof ProjectileInteraction) {
            ProjectileInteraction sub = (ProjectileInteraction)interaction;
            return 32;
        }
        interaction = this;
        if (interaction instanceof RemoveEntityInteraction) {
            RemoveEntityInteraction sub = (RemoveEntityInteraction)interaction;
            return 33;
        }
        interaction = this;
        if (interaction instanceof ResetCooldownInteraction) {
            ResetCooldownInteraction sub = (ResetCooldownInteraction)interaction;
            return 34;
        }
        interaction = this;
        if (interaction instanceof TriggerCooldownInteraction) {
            TriggerCooldownInteraction sub = (TriggerCooldownInteraction)interaction;
            return 35;
        }
        interaction = this;
        if (interaction instanceof CooldownConditionInteraction) {
            CooldownConditionInteraction sub = (CooldownConditionInteraction)interaction;
            return 36;
        }
        interaction = this;
        if (interaction instanceof ChainFlagInteraction) {
            ChainFlagInteraction sub = (ChainFlagInteraction)interaction;
            return 37;
        }
        interaction = this;
        if (interaction instanceof IncrementCooldownInteraction) {
            IncrementCooldownInteraction sub = (IncrementCooldownInteraction)interaction;
            return 38;
        }
        interaction = this;
        if (interaction instanceof CancelChainInteraction) {
            CancelChainInteraction sub = (CancelChainInteraction)interaction;
            return 39;
        }
        interaction = this;
        if (interaction instanceof RunRootInteraction) {
            RunRootInteraction sub = (RunRootInteraction)interaction;
            return 40;
        }
        interaction = this;
        if (interaction instanceof CameraInteraction) {
            CameraInteraction sub = (CameraInteraction)interaction;
            return 41;
        }
        interaction = this;
        if (interaction instanceof SpawnDeployableFromRaycastInteraction) {
            SpawnDeployableFromRaycastInteraction sub = (SpawnDeployableFromRaycastInteraction)interaction;
            return 42;
        }
        interaction = this;
        if (interaction instanceof ToggleGliderInteraction) {
            ToggleGliderInteraction sub = (ToggleGliderInteraction)interaction;
            return 44;
        }
        interaction = this;
        if (interaction instanceof SimpleInteraction) {
            SimpleInteraction sub = (SimpleInteraction)interaction;
            return 1;
        }
        interaction = this;
        if (interaction instanceof ChargingInteraction) {
            ChargingInteraction sub = (ChargingInteraction)interaction;
            return 9;
        }
        interaction = this;
        if (interaction instanceof ChainingInteraction) {
            ChainingInteraction sub = (ChainingInteraction)interaction;
            return 11;
        }
        interaction = this;
        if (interaction instanceof ReplaceInteraction) {
            ReplaceInteraction sub = (ReplaceInteraction)interaction;
            return 15;
        }
        interaction = this;
        if (interaction instanceof FirstClickInteraction) {
            FirstClickInteraction sub = (FirstClickInteraction)interaction;
            return 18;
        }
        interaction = this;
        if (interaction instanceof DamageEntityInteraction) {
            DamageEntityInteraction sub = (DamageEntityInteraction)interaction;
            return 21;
        }
        interaction = this;
        if (interaction instanceof ParallelInteraction) {
            ParallelInteraction sub = (ParallelInteraction)interaction;
            return 23;
        }
        interaction = this;
        if (interaction instanceof ChangeActiveSlotInteraction) {
            ChangeActiveSlotInteraction sub = (ChangeActiveSlotInteraction)interaction;
            return 24;
        }
        interaction = this;
        if (interaction instanceof SerialInteraction) {
            SerialInteraction sub = (SerialInteraction)interaction;
            return 29;
        }
        interaction = this;
        if (interaction instanceof MemoriesConditionInteraction) {
            MemoriesConditionInteraction sub = (MemoriesConditionInteraction)interaction;
            return 43;
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
            case 0 -> SimpleBlockInteraction.validateStructure(buffer, offset + typeIdLen);
            case 1 -> SimpleInteraction.validateStructure(buffer, offset + typeIdLen);
            case 2 -> PlaceBlockInteraction.validateStructure(buffer, offset + typeIdLen);
            case 3 -> BreakBlockInteraction.validateStructure(buffer, offset + typeIdLen);
            case 4 -> PickBlockInteraction.validateStructure(buffer, offset + typeIdLen);
            case 5 -> UseBlockInteraction.validateStructure(buffer, offset + typeIdLen);
            case 6 -> UseEntityInteraction.validateStructure(buffer, offset + typeIdLen);
            case 7 -> BuilderToolInteraction.validateStructure(buffer, offset + typeIdLen);
            case 8 -> ModifyInventoryInteraction.validateStructure(buffer, offset + typeIdLen);
            case 9 -> ChargingInteraction.validateStructure(buffer, offset + typeIdLen);
            case 10 -> WieldingInteraction.validateStructure(buffer, offset + typeIdLen);
            case 11 -> ChainingInteraction.validateStructure(buffer, offset + typeIdLen);
            case 12 -> ConditionInteraction.validateStructure(buffer, offset + typeIdLen);
            case 13 -> StatsConditionInteraction.validateStructure(buffer, offset + typeIdLen);
            case 14 -> BlockConditionInteraction.validateStructure(buffer, offset + typeIdLen);
            case 15 -> ReplaceInteraction.validateStructure(buffer, offset + typeIdLen);
            case 16 -> ChangeBlockInteraction.validateStructure(buffer, offset + typeIdLen);
            case 17 -> ChangeStateInteraction.validateStructure(buffer, offset + typeIdLen);
            case 18 -> FirstClickInteraction.validateStructure(buffer, offset + typeIdLen);
            case 20 -> SelectInteraction.validateStructure(buffer, offset + typeIdLen);
            case 21 -> DamageEntityInteraction.validateStructure(buffer, offset + typeIdLen);
            case 22 -> RepeatInteraction.validateStructure(buffer, offset + typeIdLen);
            case 23 -> ParallelInteraction.validateStructure(buffer, offset + typeIdLen);
            case 24 -> ChangeActiveSlotInteraction.validateStructure(buffer, offset + typeIdLen);
            case 25 -> EffectConditionInteraction.validateStructure(buffer, offset + typeIdLen);
            case 26 -> ApplyForceInteraction.validateStructure(buffer, offset + typeIdLen);
            case 27 -> ApplyEffectInteraction.validateStructure(buffer, offset + typeIdLen);
            case 28 -> ClearEntityEffectInteraction.validateStructure(buffer, offset + typeIdLen);
            case 29 -> SerialInteraction.validateStructure(buffer, offset + typeIdLen);
            case 30 -> ChangeStatInteraction.validateStructure(buffer, offset + typeIdLen);
            case 31 -> MovementConditionInteraction.validateStructure(buffer, offset + typeIdLen);
            case 32 -> ProjectileInteraction.validateStructure(buffer, offset + typeIdLen);
            case 33 -> RemoveEntityInteraction.validateStructure(buffer, offset + typeIdLen);
            case 34 -> ResetCooldownInteraction.validateStructure(buffer, offset + typeIdLen);
            case 35 -> TriggerCooldownInteraction.validateStructure(buffer, offset + typeIdLen);
            case 36 -> CooldownConditionInteraction.validateStructure(buffer, offset + typeIdLen);
            case 37 -> ChainFlagInteraction.validateStructure(buffer, offset + typeIdLen);
            case 38 -> IncrementCooldownInteraction.validateStructure(buffer, offset + typeIdLen);
            case 39 -> CancelChainInteraction.validateStructure(buffer, offset + typeIdLen);
            case 40 -> RunRootInteraction.validateStructure(buffer, offset + typeIdLen);
            case 41 -> CameraInteraction.validateStructure(buffer, offset + typeIdLen);
            case 42 -> SpawnDeployableFromRaycastInteraction.validateStructure(buffer, offset + typeIdLen);
            case 43 -> MemoriesConditionInteraction.validateStructure(buffer, offset + typeIdLen);
            case 44 -> ToggleGliderInteraction.validateStructure(buffer, offset + typeIdLen);
            default -> ValidationResult.error("Unknown polymorphic type ID " + typeId + " for Interaction");
        };
    }
}

