/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.crafting.window;

import com.google.gson.JsonArray;
import com.hypixel.hytale.builtin.crafting.CraftingPlugin;
import com.hypixel.hytale.builtin.crafting.component.CraftingManager;
import com.hypixel.hytale.builtin.crafting.state.BenchState;
import com.hypixel.hytale.builtin.crafting.window.CraftingWindow;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.event.EventRegistration;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.protocol.BenchRequirement;
import com.hypixel.hytale.protocol.ItemSoundEvent;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.protocol.packets.window.ChangeBlockAction;
import com.hypixel.hytale.protocol.packets.window.CraftRecipeAction;
import com.hypixel.hytale.protocol.packets.window.SelectSlotAction;
import com.hypixel.hytale.protocol.packets.window.WindowAction;
import com.hypixel.hytale.protocol.packets.window.WindowType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.StructuralCraftingBench;
import com.hypixel.hytale.server.core.asset.type.item.config.BlockGroup;
import com.hypixel.hytale.server.core.asset.type.item.config.CraftingRecipe;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.itemsound.config.ItemSoundSet;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.windows.ItemContainerWindow;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.MaterialQuantity;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.inventory.container.filter.FilterActionType;
import com.hypixel.hytale.server.core.inventory.container.filter.FilterType;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.lang.runtime.SwitchBootstraps;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StructuralCraftingWindow
extends CraftingWindow
implements ItemContainerWindow {
    private static final int MAX_OPTIONS = 64;
    private final SimpleItemContainer inputContainer;
    private final SimpleItemContainer optionsContainer;
    private final CombinedItemContainer combinedItemContainer;
    private final Int2ObjectMap<String> optionSlotToRecipeMap = new Int2ObjectOpenHashMap<String>();
    private int selectedSlot;
    @Nullable
    private EventRegistration inventoryRegistration;

    public StructuralCraftingWindow(BenchState benchState) {
        super(WindowType.StructuralCrafting, benchState);
        this.inputContainer = new SimpleItemContainer(1);
        this.inputContainer.registerChangeEvent(e -> this.updateRecipes());
        this.inputContainer.setSlotFilter(FilterActionType.ADD, (short)0, this::isValidInput);
        this.optionsContainer = new SimpleItemContainer(64);
        this.optionsContainer.setGlobalFilter(FilterType.DENY_ALL);
        this.combinedItemContainer = new CombinedItemContainer(this.inputContainer, this.optionsContainer);
        this.windowData.addProperty("selected", this.selectedSlot);
        StructuralCraftingBench structuralBench = (StructuralCraftingBench)this.bench;
        this.windowData.addProperty("allowBlockGroupCycling", structuralBench.shouldAllowBlockGroupCycling());
        this.windowData.addProperty("alwaysShowInventoryHints", structuralBench.shouldAlwaysShowInventoryHints());
    }

    private boolean isValidInput(FilterActionType filterActionType, ItemContainer itemContainer, short i, ItemStack itemStack) {
        if (filterActionType != FilterActionType.ADD) {
            return true;
        }
        ObjectList<CraftingRecipe> matchingRecipes = this.getMatchingRecipes(itemStack);
        return matchingRecipes != null && !matchingRecipes.isEmpty();
    }

    private static void sortRecipes(ObjectList<CraftingRecipe> matching, StructuralCraftingBench structuralBench) {
        matching.sort((a, b) -> {
            int categoryB;
            boolean bHasHeaderCategory;
            boolean aHasHeaderCategory = StructuralCraftingWindow.hasHeaderCategory(structuralBench, a);
            if (aHasHeaderCategory != (bHasHeaderCategory = StructuralCraftingWindow.hasHeaderCategory(structuralBench, b))) {
                return aHasHeaderCategory ? -1 : 1;
            }
            int categoryA = StructuralCraftingWindow.getSortingPriority(structuralBench, a);
            int categoryCompare = Integer.compare(categoryA, categoryB = StructuralCraftingWindow.getSortingPriority(structuralBench, b));
            return categoryCompare != 0 ? categoryCompare : a.getId().compareTo(b.getId());
        });
    }

    private static boolean hasHeaderCategory(StructuralCraftingBench bench, CraftingRecipe recipe) {
        for (BenchRequirement requirement : recipe.getBenchRequirement()) {
            if (requirement.type != bench.getType() || !requirement.id.equals(bench.getId()) || requirement.categories == null) continue;
            for (String category : requirement.categories) {
                if (!bench.isHeaderCategory(category)) continue;
                return true;
            }
        }
        return false;
    }

    private static int getSortingPriority(StructuralCraftingBench bench, CraftingRecipe recipe) {
        int priority = Integer.MAX_VALUE;
        for (BenchRequirement requirement : recipe.getBenchRequirement()) {
            if (requirement.type != bench.getType() || !requirement.id.equals(bench.getId()) || requirement.categories == null) continue;
            for (String category : requirement.categories) {
                priority = Math.min(priority, bench.getCategoryIndex(category));
            }
            break;
        }
        return priority;
    }

    @Override
    public void handleAction(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull WindowAction action) {
        CraftingManager craftingManager = store.getComponent(ref, CraftingManager.getComponentType());
        WindowAction windowAction = action;
        Objects.requireNonNull(windowAction);
        WindowAction windowAction2 = windowAction;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{SelectSlotAction.class, CraftRecipeAction.class, ChangeBlockAction.class}, (Object)windowAction2, n)) {
            case 0: {
                SelectSlotAction selectAction = (SelectSlotAction)windowAction2;
                int newSlot = MathUtil.clamp(selectAction.slot, 0, this.optionsContainer.getCapacity());
                if (newSlot == this.selectedSlot) break;
                this.selectedSlot = newSlot;
                this.windowData.addProperty("selected", this.selectedSlot);
                this.invalidate();
                break;
            }
            case 1: {
                Item primaryOutputItem;
                CraftRecipeAction craftAction = (CraftRecipeAction)windowAction2;
                ItemStack output = this.optionsContainer.getItemStack((short)this.selectedSlot);
                if (output == null) break;
                int quantity = craftAction.quantity;
                String recipeId = (String)this.optionSlotToRecipeMap.get(this.selectedSlot);
                if (recipeId == null) {
                    return;
                }
                CraftingRecipe recipe = CraftingRecipe.getAssetMap().getAsset(recipeId);
                if (recipe == null) {
                    return;
                }
                MaterialQuantity primaryOutput = recipe.getPrimaryOutput();
                String primaryOutputItemId = primaryOutput.getItemId();
                if (primaryOutputItemId != null && (primaryOutputItem = Item.getAssetMap().getAsset(primaryOutputItemId)) != null) {
                    this.playCraftSound(ref, store, primaryOutputItem);
                }
                craftingManager.queueCraft(ref, store, this, 0, recipe, quantity, this.inputContainer, CraftingManager.InputRemovalType.ORDERED);
                this.invalidate();
                break;
            }
            case 2: {
                ChangeBlockAction changeBlockAction = (ChangeBlockAction)windowAction2;
                if (!((StructuralCraftingBench)this.bench).shouldAllowBlockGroupCycling()) break;
                this.changeBlockType(ref, changeBlockAction.down, store);
                break;
            }
        }
    }

    private void playCraftSound(Ref<EntityStore> ref, Store<EntityStore> store, Item item) {
        ItemSoundSet soundSet = ItemSoundSet.getAssetMap().getAsset(item.getItemSoundSetIndex());
        if (soundSet == null) {
            return;
        }
        String dragSound = soundSet.getSoundEventIds().get((Object)ItemSoundEvent.Drop);
        if (dragSound == null) {
            return;
        }
        int dragSoundIndex = SoundEvent.getAssetMap().getIndex(dragSound);
        if (dragSoundIndex == 0) {
            return;
        }
        SoundUtil.playSoundEvent2d(ref, dragSoundIndex, SoundCategory.UI, store);
    }

    private void changeBlockType(@Nonnull Ref<EntityStore> ref, boolean down, @Nonnull Store<EntityStore> store) {
        ItemStack item = this.inputContainer.getItemStack((short)0);
        if (item == null) {
            return;
        }
        BlockGroup set = BlockGroup.findItemGroup(item.getItem());
        if (set == null) {
            return;
        }
        int currentIndex = -1;
        for (int i = 0; i < set.size(); ++i) {
            if (!set.get(i).equals(item.getItem().getId())) continue;
            currentIndex = i;
            break;
        }
        if (currentIndex == -1) {
            return;
        }
        int newIndex = down ? (currentIndex - 1 + set.size()) % set.size() : (currentIndex + 1) % set.size();
        String next = set.get(newIndex);
        Item desiredItem = Item.getAssetMap().getAsset(next);
        if (desiredItem == null) {
            return;
        }
        this.inputContainer.replaceItemStackInSlot((short)0, item, new ItemStack(next, item.getQuantity()));
        this.playCraftSound(ref, store, desiredItem);
    }

    @Override
    @Nonnull
    public ItemContainer getItemContainer() {
        return this.combinedItemContainer;
    }

    @Override
    public boolean onOpen0(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
        super.onOpen0(ref, store);
        Player playerComponent = store.getComponent(ref, Player.getComponentType());
        assert (playerComponent != null);
        Inventory inventory = playerComponent.getInventory();
        this.inventoryRegistration = inventory.getCombinedHotbarFirst().registerChangeEvent(event -> {
            this.windowData.add("inventoryHints", CraftingManager.generateInventoryHints(CraftingPlugin.getBenchRecipes(this.bench), 0, inventory.getCombinedHotbarFirst()));
            this.invalidate();
        });
        this.windowData.add("inventoryHints", CraftingManager.generateInventoryHints(CraftingPlugin.getBenchRecipes(this.bench), 0, inventory.getCombinedHotbarFirst()));
        return true;
    }

    @Override
    public void onClose0(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        super.onClose0(ref, componentAccessor);
        Player playerComponent = componentAccessor.getComponent(ref, Player.getComponentType());
        assert (playerComponent != null);
        List<ItemStack> itemStacks = this.inputContainer.dropAllItemStacks();
        SimpleItemContainer.addOrDropItemStacks(componentAccessor, ref, playerComponent.getInventory().getCombinedHotbarFirst(), itemStacks);
        CraftingManager craftingManagerComponent = componentAccessor.getComponent(ref, CraftingManager.getComponentType());
        assert (craftingManagerComponent != null);
        craftingManagerComponent.cancelAllCrafting(ref, componentAccessor);
        if (this.inventoryRegistration != null) {
            this.inventoryRegistration.unregister();
            this.inventoryRegistration = null;
        }
    }

    private void updateRecipes() {
        CraftingRecipe recipe;
        int dividerIndex;
        this.invalidate();
        this.optionsContainer.clear();
        this.optionSlotToRecipeMap.clear();
        ItemStack inputStack = this.inputContainer.getItemStack((short)0);
        ObjectList<CraftingRecipe> matchingRecipes = this.getMatchingRecipes(inputStack);
        if (matchingRecipes == null) {
            return;
        }
        StructuralCraftingBench structuralBench = (StructuralCraftingBench)this.bench;
        StructuralCraftingWindow.sortRecipes(matchingRecipes, structuralBench);
        for (dividerIndex = 0; dividerIndex < matchingRecipes.size() && StructuralCraftingWindow.hasHeaderCategory(structuralBench, recipe = (CraftingRecipe)matchingRecipes.get(dividerIndex)); ++dividerIndex) {
        }
        this.windowData.addProperty("dividerIndex", dividerIndex);
        this.optionsContainer.clear();
        short index = 0;
        int bound = matchingRecipes.size();
        for (int i = 0; i < bound; ++i) {
            CraftingRecipe match = (CraftingRecipe)matchingRecipes.get(i);
            for (BenchRequirement requirement : match.getBenchRequirement()) {
                if (requirement.type != this.bench.getType() || !requirement.id.equals(this.bench.getId())) continue;
                List<ItemStack> output = CraftingManager.getOutputItemStacks(match);
                this.optionsContainer.setItemStackForSlot(index, output.getFirst(), false);
                this.optionSlotToRecipeMap.put(index, match.getId());
                index = (short)(index + 1);
            }
        }
        JsonArray optionSlotRecipes = new JsonArray();
        for (int i = 0; i < this.optionsContainer.getCapacity(); ++i) {
            String recipeId = (String)this.optionSlotToRecipeMap.get(i);
            if (recipeId == null) continue;
            optionSlotRecipes.add(recipeId);
        }
        this.windowData.add("optionSlotRecipes", optionSlotRecipes);
    }

    @Nullable
    private ObjectList<CraftingRecipe> getMatchingRecipes(@Nullable ItemStack inputStack) {
        if (inputStack == null) {
            return null;
        }
        List<CraftingRecipe> recipes = CraftingPlugin.getBenchRecipes(this.bench.getType(), this.bench.getId());
        if (recipes.isEmpty()) {
            return null;
        }
        ObjectArrayList<CraftingRecipe> matchingRecipes = new ObjectArrayList<CraftingRecipe>();
        int bound = recipes.size();
        for (int i = 0; i < bound; ++i) {
            CraftingRecipe recipe = recipes.get(i);
            List<MaterialQuantity> inputMaterials = CraftingManager.getInputMaterials(recipe);
            if (inputMaterials.size() != 1 || !CraftingManager.matches(inputMaterials.getFirst(), inputStack)) continue;
            matchingRecipes.add(recipe);
        }
        if (matchingRecipes.isEmpty()) {
            return null;
        }
        return matchingRecipes;
    }
}

