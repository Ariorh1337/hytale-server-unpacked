/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.utils;

import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.PlaceFluidInteraction;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class FluidPatternHelper {
    private FluidPatternHelper() {
    }

    @Nullable
    public static FluidInfo getFluidInfo(@Nonnull String itemKey) {
        Item item = Item.getAssetMap().getAsset(itemKey);
        if (item == null) {
            return null;
        }
        Map<InteractionType, String> interactions = item.getInteractions();
        String secondaryRootId = interactions.get((Object)InteractionType.Secondary);
        if (secondaryRootId == null) {
            return null;
        }
        RootInteraction rootInteraction = (RootInteraction)RootInteraction.getAssetMap().getAsset(secondaryRootId);
        if (rootInteraction == null) {
            return null;
        }
        for (String interactionId : rootInteraction.getInteractionIds()) {
            int fluidId;
            PlaceFluidInteraction placeFluidInteraction;
            String fluidKey;
            Interaction interaction = (Interaction)Interaction.getAssetMap().getAsset(interactionId);
            if (!(interaction instanceof PlaceFluidInteraction) || (fluidKey = (placeFluidInteraction = (PlaceFluidInteraction)interaction).getFluidKey()) == null || (fluidId = Fluid.getAssetMap().getIndex(fluidKey)) < 0) continue;
            Fluid fluid = Fluid.getAssetMap().getAsset(fluidId);
            byte maxLevel = (byte)(fluid != null ? fluid.getMaxFluidLevel() : 8);
            return new FluidInfo(fluidId, maxLevel);
        }
        return null;
    }

    public static boolean isFluidItem(@Nonnull String itemKey) {
        return FluidPatternHelper.getFluidInfo(itemKey) != null;
    }

    @Nullable
    public static FluidInfo getFluidInfoFromBlockType(@Nonnull String blockTypeKey) {
        return FluidPatternHelper.getFluidInfo(blockTypeKey);
    }

    public record FluidInfo(int fluidId, byte fluidLevel) {
    }
}

