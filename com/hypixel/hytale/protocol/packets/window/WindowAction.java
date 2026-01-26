/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.window;

import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import com.hypixel.hytale.protocol.packets.window.CancelCraftingAction;
import com.hypixel.hytale.protocol.packets.window.ChangeBlockAction;
import com.hypixel.hytale.protocol.packets.window.CraftItemAction;
import com.hypixel.hytale.protocol.packets.window.CraftRecipeAction;
import com.hypixel.hytale.protocol.packets.window.SelectSlotAction;
import com.hypixel.hytale.protocol.packets.window.SetActiveAction;
import com.hypixel.hytale.protocol.packets.window.SortItemsAction;
import com.hypixel.hytale.protocol.packets.window.TierUpgradeAction;
import com.hypixel.hytale.protocol.packets.window.UpdateCategoryAction;
import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;

public abstract class WindowAction {
    public static final int MAX_SIZE = 32768023;

    @Nonnull
    public static WindowAction deserialize(@Nonnull ByteBuf buf, int offset) {
        int typeId = VarInt.peek(buf, offset);
        int typeIdLen = VarInt.length(buf, offset);
        return switch (typeId) {
            case 0 -> CraftRecipeAction.deserialize(buf, offset + typeIdLen);
            case 1 -> TierUpgradeAction.deserialize(buf, offset + typeIdLen);
            case 2 -> SelectSlotAction.deserialize(buf, offset + typeIdLen);
            case 3 -> ChangeBlockAction.deserialize(buf, offset + typeIdLen);
            case 4 -> SetActiveAction.deserialize(buf, offset + typeIdLen);
            case 5 -> CraftItemAction.deserialize(buf, offset + typeIdLen);
            case 6 -> UpdateCategoryAction.deserialize(buf, offset + typeIdLen);
            case 7 -> CancelCraftingAction.deserialize(buf, offset + typeIdLen);
            case 8 -> SortItemsAction.deserialize(buf, offset + typeIdLen);
            default -> throw ProtocolException.unknownPolymorphicType("WindowAction", typeId);
        };
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int typeId = VarInt.peek(buf, offset);
        int typeIdLen = VarInt.length(buf, offset);
        return typeIdLen + (switch (typeId) {
            case 0 -> CraftRecipeAction.computeBytesConsumed(buf, offset + typeIdLen);
            case 1 -> TierUpgradeAction.computeBytesConsumed(buf, offset + typeIdLen);
            case 2 -> SelectSlotAction.computeBytesConsumed(buf, offset + typeIdLen);
            case 3 -> ChangeBlockAction.computeBytesConsumed(buf, offset + typeIdLen);
            case 4 -> SetActiveAction.computeBytesConsumed(buf, offset + typeIdLen);
            case 5 -> CraftItemAction.computeBytesConsumed(buf, offset + typeIdLen);
            case 6 -> UpdateCategoryAction.computeBytesConsumed(buf, offset + typeIdLen);
            case 7 -> CancelCraftingAction.computeBytesConsumed(buf, offset + typeIdLen);
            case 8 -> SortItemsAction.computeBytesConsumed(buf, offset + typeIdLen);
            default -> throw ProtocolException.unknownPolymorphicType("WindowAction", typeId);
        });
    }

    public int getTypeId() {
        WindowAction windowAction = this;
        if (windowAction instanceof CraftRecipeAction) {
            CraftRecipeAction sub = (CraftRecipeAction)windowAction;
            return 0;
        }
        windowAction = this;
        if (windowAction instanceof TierUpgradeAction) {
            TierUpgradeAction sub = (TierUpgradeAction)windowAction;
            return 1;
        }
        windowAction = this;
        if (windowAction instanceof SelectSlotAction) {
            SelectSlotAction sub = (SelectSlotAction)windowAction;
            return 2;
        }
        windowAction = this;
        if (windowAction instanceof ChangeBlockAction) {
            ChangeBlockAction sub = (ChangeBlockAction)windowAction;
            return 3;
        }
        windowAction = this;
        if (windowAction instanceof SetActiveAction) {
            SetActiveAction sub = (SetActiveAction)windowAction;
            return 4;
        }
        windowAction = this;
        if (windowAction instanceof CraftItemAction) {
            CraftItemAction sub = (CraftItemAction)windowAction;
            return 5;
        }
        windowAction = this;
        if (windowAction instanceof UpdateCategoryAction) {
            UpdateCategoryAction sub = (UpdateCategoryAction)windowAction;
            return 6;
        }
        windowAction = this;
        if (windowAction instanceof CancelCraftingAction) {
            CancelCraftingAction sub = (CancelCraftingAction)windowAction;
            return 7;
        }
        windowAction = this;
        if (windowAction instanceof SortItemsAction) {
            SortItemsAction sub = (SortItemsAction)windowAction;
            return 8;
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
            case 0 -> CraftRecipeAction.validateStructure(buffer, offset + typeIdLen);
            case 1 -> TierUpgradeAction.validateStructure(buffer, offset + typeIdLen);
            case 2 -> SelectSlotAction.validateStructure(buffer, offset + typeIdLen);
            case 3 -> ChangeBlockAction.validateStructure(buffer, offset + typeIdLen);
            case 4 -> SetActiveAction.validateStructure(buffer, offset + typeIdLen);
            case 5 -> CraftItemAction.validateStructure(buffer, offset + typeIdLen);
            case 6 -> UpdateCategoryAction.validateStructure(buffer, offset + typeIdLen);
            case 7 -> CancelCraftingAction.validateStructure(buffer, offset + typeIdLen);
            case 8 -> SortItemsAction.validateStructure(buffer, offset + typeIdLen);
            default -> ValidationResult.error("Unknown polymorphic type ID " + typeId + " for WindowAction");
        };
    }
}

