/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.crafting.interaction;

import com.hypixel.hytale.builtin.crafting.component.CraftingManager;
import com.hypixel.hytale.builtin.crafting.state.BenchState;
import com.hypixel.hytale.builtin.crafting.window.CraftingWindow;
import com.hypixel.hytale.builtin.crafting.window.DiagramCraftingWindow;
import com.hypixel.hytale.builtin.crafting.window.SimpleCraftingWindow;
import com.hypixel.hytale.builtin.crafting.window.StructuralCraftingWindow;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OpenBenchPageInteraction
extends SimpleBlockInteraction {
    public static final OpenBenchPageInteraction SIMPLE_CRAFTING = new OpenBenchPageInteraction("*Simple_Crafting_Default", PageType.SIMPLE_CRAFTING);
    public static final RootInteraction SIMPLE_CRAFTING_ROOT = new RootInteraction(SIMPLE_CRAFTING.getId(), SIMPLE_CRAFTING.getId());
    public static final OpenBenchPageInteraction DIAGRAM_CRAFTING = new OpenBenchPageInteraction("*Diagram_Crafting_Default", PageType.DIAGRAM_CRAFTING);
    public static final RootInteraction DIAGRAM_CRAFTING_ROOT = new RootInteraction(DIAGRAM_CRAFTING.getId(), DIAGRAM_CRAFTING.getId());
    public static final OpenBenchPageInteraction STRUCTURAL_CRAFTING = new OpenBenchPageInteraction("*Structural_Crafting_Default", PageType.STRUCTURAL_CRAFTING);
    public static final RootInteraction STRUCTURAL_CRAFTING_ROOT = new RootInteraction(STRUCTURAL_CRAFTING.getId(), STRUCTURAL_CRAFTING.getId());
    @Nonnull
    public static final BuilderCodec<OpenBenchPageInteraction> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(OpenBenchPageInteraction.class, OpenBenchPageInteraction::new, SimpleBlockInteraction.CODEC).documentation("Opens the given crafting bench page.")).appendInherited(new KeyedCodec<PageType>("Page", new EnumCodec<PageType>(PageType.class)), (o, v) -> {
        o.pageType = v;
    }, o -> o.pageType, (o, p) -> {
        o.pageType = p.pageType;
    }).addValidator(Validators.nonNull()).add()).build();
    @Nonnull
    private PageType pageType = PageType.SIMPLE_CRAFTING;

    public OpenBenchPageInteraction(@Nonnull String id, @Nonnull PageType pageType) {
        super(id);
        this.pageType = pageType;
    }

    protected OpenBenchPageInteraction() {
    }

    @Override
    protected void interactWithBlock(@Nonnull World world, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull Vector3i targetBlock, @Nonnull CooldownHandler cooldownHandler) {
        Ref<EntityStore> ref = context.getEntity();
        Store<EntityStore> store = ref.getStore();
        Player playerComponent = commandBuffer.getComponent(ref, Player.getComponentType());
        if (playerComponent == null) {
            return;
        }
        CraftingManager craftingManagerComponent = commandBuffer.getComponent(ref, CraftingManager.getComponentType());
        assert (craftingManagerComponent != null);
        if (craftingManagerComponent.hasBenchSet()) {
            return;
        }
        BlockState blockState = world.getState(targetBlock.x, targetBlock.y, targetBlock.z, true);
        if (blockState instanceof BenchState) {
            BenchState benchState = (BenchState)blockState;
            CraftingWindow benchWindow = switch (this.pageType.ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> new SimpleCraftingWindow(benchState);
                case 1 -> new DiagramCraftingWindow(ref, commandBuffer, benchState);
                case 2 -> new StructuralCraftingWindow(benchState);
            };
            UUIDComponent uuidComponent = commandBuffer.getComponent(ref, UUIDComponent.getComponentType());
            assert (uuidComponent != null);
            UUID uuid = uuidComponent.getUuid();
            if (benchState.getWindows().putIfAbsent(uuid, benchWindow) == null) {
                benchWindow.registerCloseEvent(event -> benchState.getWindows().remove(uuid, benchWindow));
            }
            playerComponent.getPageManager().setPageWithWindows(ref, store, Page.Bench, true, benchWindow);
        }
    }

    @Override
    protected void simulateInteractWithBlock(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull World world, @Nonnull Vector3i targetBlock) {
    }

    private static enum PageType {
        SIMPLE_CRAFTING,
        DIAGRAM_CRAFTING,
        STRUCTURAL_CRAFTING;

    }
}

