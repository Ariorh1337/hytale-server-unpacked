/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.crafting.window;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hypixel.hytale.builtin.crafting.CraftingPlugin;
import com.hypixel.hytale.builtin.crafting.component.CraftingManager;
import com.hypixel.hytale.builtin.crafting.state.BenchState;
import com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState;
import com.hypixel.hytale.builtin.crafting.window.BenchWindow;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.event.EventRegistration;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.protocol.packets.window.SetActiveAction;
import com.hypixel.hytale.protocol.packets.window.TierUpgradeAction;
import com.hypixel.hytale.protocol.packets.window.WindowAction;
import com.hypixel.hytale.protocol.packets.window.WindowType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.Bench;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.ProcessingBench;
import com.hypixel.hytale.server.core.asset.type.item.config.CraftingRecipe;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.windows.ItemContainerWindow;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.lang.runtime.SwitchBootstraps;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ProcessingBenchWindow
extends BenchWindow
implements ItemContainerWindow {
    private CombinedItemContainer itemContainer;
    @Nullable
    private EventRegistration<?, ?> inventoryRegistration;
    private float fuelTime;
    private int maxFuel;
    private float progress;
    private boolean active;
    private final Set<Short> processingSlots = new HashSet<Short>();
    private final Set<Short> processingFuelSlots = new HashSet<Short>();

    public ProcessingBenchWindow(ProcessingBenchState benchState) {
        super(WindowType.Processing, benchState);
        ProcessingBench processingBench = (ProcessingBench)this.blockType.getBench();
        CraftingRecipe recipe = benchState.getRecipe();
        float inputProgress = benchState.getInputProgress();
        float progress = recipe != null && recipe.getTimeSeconds() > 0.0f ? inputProgress / recipe.getTimeSeconds() : 0.0f;
        this.itemContainer = benchState.getItemContainer();
        this.active = benchState.isActive();
        this.progress = progress;
        this.windowData.addProperty("active", this.active);
        this.windowData.addProperty("progress", Float.valueOf(progress));
        if (processingBench.getFuel() != null && processingBench.getFuel().length > 0) {
            JsonArray fuelArray = new JsonArray();
            for (ProcessingBench.ProcessingSlot benchSlot : processingBench.getFuel()) {
                JsonObject fuelObj = new JsonObject();
                fuelObj.addProperty("icon", benchSlot.getIcon());
                fuelObj.addProperty("resourceTypeId", benchSlot.getResourceTypeId());
                fuelArray.add(fuelObj);
            }
            this.windowData.add("fuel", fuelArray);
        }
        if (processingBench.getMaxFuel() > 0) {
            this.maxFuel = processingBench.getMaxFuel();
        }
        this.windowData.addProperty("maxFuel", this.maxFuel);
        this.windowData.addProperty("fuelTime", Float.valueOf(this.fuelTime));
        this.windowData.addProperty("progress", Float.valueOf(progress));
        this.windowData.addProperty("processingFuelSlots", 0);
        this.windowData.addProperty("processingSlots", 0);
        int tierLevel = this.getBenchTierLevel();
        this.updateInputSlots(tierLevel);
        this.updateOutputSlots(tierLevel);
    }

    @Override
    @Nonnull
    public JsonObject getData() {
        return this.windowData;
    }

    @Override
    @Nonnull
    public CombinedItemContainer getItemContainer() {
        return this.itemContainer;
    }

    public void setActive(boolean active) {
        if (this.active != active) {
            this.active = active;
            this.windowData.addProperty("active", active);
            this.invalidate();
        }
    }

    public void setFuelTime(float fuelTime) {
        if (Float.isInfinite(fuelTime)) {
            throw new IllegalArgumentException("Infinite fuelTime");
        }
        if (Float.isNaN(fuelTime)) {
            throw new IllegalArgumentException("Nan fuelTime");
        }
        if (this.fuelTime != fuelTime) {
            this.fuelTime = fuelTime;
            this.windowData.addProperty("fuelTime", Float.valueOf(fuelTime));
            this.invalidate();
        }
    }

    public void setMaxFuel(int maxFuel) {
        this.maxFuel = maxFuel;
        this.windowData.addProperty("maxFuel", maxFuel);
        this.invalidate();
    }

    public void setProgress(float progress) {
        if (Float.isInfinite(progress)) {
            throw new IllegalArgumentException("Infinite progress");
        }
        if (Float.isNaN(progress)) {
            throw new IllegalArgumentException("Nan fuelTime");
        }
        if (this.progress != progress) {
            this.progress = progress;
            this.windowData.addProperty("progress", Float.valueOf(progress));
            this.invalidate();
        }
    }

    public void setProcessingSlots(Set<Short> slots) {
        if (this.processingSlots.equals(slots)) {
            return;
        }
        this.processingSlots.clear();
        this.processingSlots.addAll(slots);
        int bitMask = 0;
        for (Short processingSlot : slots) {
            bitMask |= 1 << processingSlot.intValue();
        }
        this.windowData.addProperty("processingSlots", (byte)bitMask);
        this.invalidate();
    }

    public void setProcessingFuelSlots(Set<Short> slots) {
        if (this.processingFuelSlots.equals(slots)) {
            return;
        }
        this.processingFuelSlots.clear();
        this.processingFuelSlots.addAll(slots);
        int bitMask = 0;
        for (Short processingFuelSlots : slots) {
            bitMask |= 1 << processingFuelSlots.intValue();
        }
        this.windowData.addProperty("processingFuelSlots", (byte)bitMask);
        this.invalidate();
    }

    @Override
    public void handleAction(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull WindowAction action) {
        World world = store.getExternalData().getWorld();
        BlockState blockState = ((WorldChunk)world.getChunk(ChunkUtil.indexChunkFromBlock(this.x, this.z))).getState(this.x, this.y, this.z);
        if (!(blockState instanceof ProcessingBenchState)) {
            return;
        }
        ProcessingBenchState benchState = (ProcessingBenchState)blockState;
        WindowAction windowAction = action;
        Objects.requireNonNull(windowAction);
        WindowAction windowAction2 = windowAction;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{SetActiveAction.class, TierUpgradeAction.class}, (Object)windowAction2, n)) {
            case 0: {
                SetActiveAction setActiveAction = (SetActiveAction)windowAction2;
                if (benchState.setActive(setActiveAction.state)) break;
                this.invalidate();
                break;
            }
            case 1: {
                TierUpgradeAction ignored = (TierUpgradeAction)windowAction2;
                CraftingManager craftingManager = store.getComponent(ref, CraftingManager.getComponentType());
                if (craftingManager == null) {
                    return;
                }
                if (!craftingManager.startTierUpgrade(ref, store, this) || this.bench.getBenchUpgradeSoundEventIndex() == 0) break;
                SoundUtil.playSoundEvent3d(this.bench.getBenchUpgradeSoundEventIndex(), SoundCategory.SFX, (double)this.x + 0.5, (double)this.y + 0.5, (double)this.z + 0.5, store);
                break;
            }
        }
    }

    @Override
    protected boolean onOpen0(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
        super.onOpen0(ref, store);
        Player playerComponent = store.getComponent(ref, Player.getComponentType());
        assert (playerComponent != null);
        Inventory inventory = playerComponent.getInventory();
        this.inventoryRegistration = inventory.getCombinedHotbarFirst().registerChangeEvent(event -> {
            this.windowData.add("inventoryHints", ProcessingBenchWindow.generateInventoryHints(this.bench, inventory.getCombinedHotbarFirst()));
            this.invalidate();
        });
        this.windowData.add("inventoryHints", ProcessingBenchWindow.generateInventoryHints(this.bench, inventory.getCombinedHotbarFirst()));
        return true;
    }

    private void updateOutputSlots(int tierLevel) {
        this.windowData.addProperty("outputSlotsCount", ((ProcessingBench)this.blockType.getBench()).getOutputSlotsCount(tierLevel));
    }

    private void updateInputSlots(int tierLevel) {
        ProcessingBench.ProcessingSlot[] input = ((ProcessingBench)this.blockType.getBench()).getInput(tierLevel);
        if (input != null && input.length > 0) {
            JsonArray inputArr = new JsonArray();
            for (ProcessingBench.ProcessingSlot benchSlot : input) {
                if (benchSlot == null) continue;
                JsonObject slotObj = new JsonObject();
                slotObj.addProperty("icon", benchSlot.getIcon());
                inputArr.add(slotObj);
            }
            this.windowData.add("input", inputArr);
        }
    }

    @Override
    public void updateBenchTierLevel(int newValue) {
        super.updateBenchTierLevel(newValue);
        this.updateInputSlots(newValue);
        this.updateOutputSlots(newValue);
        BenchState benchState = this.benchState;
        if (benchState instanceof ProcessingBenchState) {
            ProcessingBenchState processingBenchState = (ProcessingBenchState)benchState;
            this.itemContainer = processingBenchState.getItemContainer();
        }
    }

    @Override
    public void onClose0(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        super.onClose0(ref, componentAccessor);
        if (this.inventoryRegistration != null) {
            this.inventoryRegistration.unregister();
            this.inventoryRegistration = null;
        }
    }

    @Nonnull
    private static JsonArray generateInventoryHints(@Nonnull Bench bench, @Nonnull CombinedItemContainer combinedInputItemContainer) {
        return CraftingManager.generateInventoryHints(CraftingPlugin.getBenchRecipes(bench), 0, combinedInputItemContainer);
    }
}

