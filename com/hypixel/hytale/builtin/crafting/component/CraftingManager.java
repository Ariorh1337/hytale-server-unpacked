/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.crafting.component;

import com.google.gson.JsonArray;
import com.hypixel.hytale.builtin.adventure.memories.MemoriesPlugin;
import com.hypixel.hytale.builtin.crafting.CraftingPlugin;
import com.hypixel.hytale.builtin.crafting.state.BenchState;
import com.hypixel.hytale.builtin.crafting.window.BenchWindow;
import com.hypixel.hytale.builtin.crafting.window.CraftingWindow;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.BenchRequirement;
import com.hypixel.hytale.protocol.BenchType;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.ItemQuantity;
import com.hypixel.hytale.protocol.ItemResourceType;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.protocol.packets.interface_.NotificationStyle;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.blockhitbox.BlockBoundingBoxes;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.Bench;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.BenchTierLevel;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.BenchUpgradeRequirement;
import com.hypixel.hytale.server.core.asset.type.item.config.CraftingRecipe;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerConfigData;
import com.hypixel.hytale.server.core.entity.entities.player.windows.MaterialExtraResourcesSection;
import com.hypixel.hytale.server.core.event.events.ecs.CraftRecipeEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerCraftEvent;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.MaterialQuantity;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.inventory.container.DelegateItemContainer;
import com.hypixel.hytale.server.core.inventory.container.EmptyItemContainer;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.inventory.container.filter.FilterType;
import com.hypixel.hytale.server.core.inventory.transaction.ListTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.MaterialSlotTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.MaterialTransaction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.universe.world.meta.BlockStateModule;
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerState;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.lang.invoke.LambdaMetafactory;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonDocument;

public class CraftingManager
implements Component<EntityStore> {
    @Nonnull
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    @Nonnull
    private final BlockingQueue<CraftingJob> queuedCraftingJobs = new LinkedBlockingQueue<CraftingJob>();
    @Nullable
    private BenchUpgradingJob upgradingJob;
    private int x;
    private int y;
    private int z;
    @Nullable
    private BlockType blockType;

    @Nonnull
    public static ComponentType<EntityStore, CraftingManager> getComponentType() {
        return CraftingPlugin.get().getCraftingManagerComponentType();
    }

    public CraftingManager() {
    }

    private CraftingManager(@Nonnull CraftingManager other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.blockType = other.blockType;
        this.queuedCraftingJobs.addAll(other.queuedCraftingJobs);
        this.upgradingJob = other.upgradingJob;
    }

    public boolean hasBenchSet() {
        return this.blockType != null;
    }

    public void setBench(int x, int y, int z, @Nonnull BlockType blockType) {
        Bench bench = blockType.getBench();
        Objects.requireNonNull(bench, "blockType isn't a bench!");
        if (bench.getType() != BenchType.Crafting && bench.getType() != BenchType.DiagramCrafting && bench.getType() != BenchType.StructuralCrafting && bench.getType() != BenchType.Processing) {
            throw new IllegalArgumentException("blockType isn't a crafting bench!");
        }
        if (this.blockType != null) {
            throw new IllegalArgumentException("Bench blockType is already set! Must be cleared (close UI).");
        }
        if (!this.queuedCraftingJobs.isEmpty()) {
            throw new IllegalArgumentException("Queue already has jobs!");
        }
        if (this.upgradingJob != null) {
            throw new IllegalArgumentException("Upgrading job is already set!");
        }
        this.x = x;
        this.y = y;
        this.z = z;
        this.blockType = blockType;
    }

    public boolean clearBench(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        boolean result = this.cancelAllCrafting(ref, componentAccessor);
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.blockType = null;
        this.upgradingJob = null;
        return result;
    }

    public boolean craftItem(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor, @Nonnull CraftingRecipe recipe, int quantity, @Nonnull ItemContainer itemContainer) {
        if (this.upgradingJob != null) {
            return false;
        }
        Objects.requireNonNull(recipe, "Recipe can't be null");
        CraftRecipeEvent.Pre preEvent = new CraftRecipeEvent.Pre(recipe, quantity);
        componentAccessor.invoke(ref, preEvent);
        if (preEvent.isCancelled()) {
            return false;
        }
        if (!this.isValidBenchForRecipe(ref, componentAccessor, recipe)) {
            return false;
        }
        World world = componentAccessor.getExternalData().getWorld();
        Player playerComponent = componentAccessor.getComponent(ref, Player.getComponentType());
        assert (playerComponent != null);
        if (playerComponent.getGameMode() == GameMode.Creative || CraftingManager.removeInputFromInventory(itemContainer, recipe, quantity)) {
            CraftRecipeEvent.Post postEvent = new CraftRecipeEvent.Post(recipe, quantity);
            componentAccessor.invoke(ref, postEvent);
            if (postEvent.isCancelled()) {
                return true;
            }
            CraftingManager.giveOutput(ref, componentAccessor, recipe, quantity);
            IEventDispatcher<PlayerCraftEvent, PlayerCraftEvent> dispatcher = HytaleServer.get().getEventBus().dispatchFor(PlayerCraftEvent.class, world.getName());
            if (dispatcher.hasListener()) {
                dispatcher.dispatch(new PlayerCraftEvent(ref, playerComponent, recipe, quantity));
            }
            return true;
        }
        PlayerRef playerRefComponent = componentAccessor.getComponent(ref, PlayerRef.getComponentType());
        assert (playerRefComponent != null);
        String translationKey = CraftingManager.getRecipeOutputTranslationKey(recipe);
        if (translationKey != null) {
            NotificationUtil.sendNotification(playerRefComponent.getPacketHandler(), Message.translation("server.general.crafting.missingIngredient").param("item", Message.translation(translationKey)), NotificationStyle.Danger);
        }
        LOGGER.at(Level.FINE).log("Missing items required to craft the item: %s", recipe);
        return false;
    }

    @Nullable
    private static String getRecipeOutputTranslationKey(@Nonnull CraftingRecipe recipe) {
        String itemId = recipe.getPrimaryOutput().getItemId();
        if (itemId == null) {
            return null;
        }
        Item itemAsset = Item.getAssetMap().getAsset(itemId);
        return itemAsset != null ? itemAsset.getTranslationKey() : null;
    }

    public boolean queueCraft(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor, @Nonnull CraftingWindow window, int transactionId, @Nonnull CraftingRecipe recipe, int quantity, @Nonnull ItemContainer inputItemContainer, @Nonnull InputRemovalType inputRemovalType) {
        BenchTierLevel tierLevelData;
        int level;
        if (this.upgradingJob != null) {
            return false;
        }
        Objects.requireNonNull(recipe, "Recipe can't be null");
        if (!this.isValidBenchForRecipe(ref, componentAccessor, recipe)) {
            return false;
        }
        float recipeTime = recipe.getTimeSeconds();
        if (recipeTime > 0.0f && (level = this.getBenchTierLevel(componentAccessor)) > 1 && (tierLevelData = this.getBenchTierLevelData(level)) != null) {
            recipeTime -= recipeTime * tierLevelData.getCraftingTimeReductionModifier();
        }
        this.queuedCraftingJobs.offer(new CraftingJob(window, transactionId, recipe, quantity, recipeTime, inputItemContainer, inputRemovalType));
        return true;
    }

    public void tick(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor, float dt) {
        if (this.upgradingJob != null) {
            if (dt > 0.0f) {
                this.upgradingJob.timeSecondsCompleted += dt;
            }
            this.upgradingJob.window.updateBenchUpgradeJob(this.upgradingJob.computeLoadingPercent());
            if (this.upgradingJob.timeSecondsCompleted >= this.upgradingJob.timeSeconds) {
                this.upgradingJob.window.updateBenchTierLevel(this.finishTierUpgrade(ref, componentAccessor));
                this.upgradingJob = null;
            }
            return;
        }
        Player playerComponent = componentAccessor.getComponent(ref, Player.getComponentType());
        assert (playerComponent != null);
        PlayerRef playerRefComponent = componentAccessor.getComponent(ref, PlayerRef.getComponentType());
        assert (playerRefComponent != null);
        while (dt > 0.0f && !this.queuedCraftingJobs.isEmpty()) {
            float percent;
            boolean isCreativeMode;
            CraftingJob currentJob = (CraftingJob)this.queuedCraftingJobs.peek();
            boolean bl = isCreativeMode = playerComponent.getGameMode() == GameMode.Creative;
            if (currentJob != null && currentJob.quantityStarted < currentJob.quantity && currentJob.quantityStarted <= currentJob.quantityCompleted) {
                int currentItemId;
                LOGGER.at(Level.FINE).log("Removing Items for next quantity: %s", currentJob);
                ++currentJob.quantityStarted;
                if (!isCreativeMode && !CraftingManager.removeInputFromInventory(currentJob, currentItemId)) {
                    String translationKey = CraftingManager.getRecipeOutputTranslationKey(currentJob.recipe);
                    if (translationKey != null) {
                        NotificationUtil.sendNotification(playerRefComponent.getPacketHandler(), Message.translation("server.general.crafting.missingIngredient").param("item", Message.translation(translationKey)), NotificationStyle.Danger);
                    }
                    LOGGER.at(Level.FINE).log("Missing items required to craft the item: %s", currentJob);
                    currentJob = null;
                    this.queuedCraftingJobs.poll();
                }
                if (!isCreativeMode && currentJob != null && currentJob.quantityStarted < currentJob.quantity && currentJob.quantityStarted <= currentJob.quantityCompleted) {
                    NotificationUtil.sendNotification(playerRefComponent.getPacketHandler(), Message.translation("server.general.crafting.failedTakingCorrectQuantity"), NotificationStyle.Danger);
                    LOGGER.at(Level.SEVERE).log("Failed to remove the correct quantity of input, removing crafting job %s", currentJob);
                    currentJob = null;
                    this.queuedCraftingJobs.poll();
                }
            }
            if (currentJob == null) continue;
            currentJob.timeSecondsCompleted += dt;
            float f = percent = currentJob.timeSeconds <= 0.0f ? 1.0f : currentJob.timeSecondsCompleted / currentJob.timeSeconds;
            if (percent > 1.0f) {
                percent = 1.0f;
            }
            currentJob.window.updateCraftingJob(percent);
            LOGGER.at(Level.FINEST).log("Update time: %s", currentJob);
            dt = 0.0f;
            if (!(currentJob.timeSecondsCompleted >= currentJob.timeSeconds)) continue;
            dt = currentJob.timeSecondsCompleted - currentJob.timeSeconds;
            ++currentJob.quantityCompleted;
            currentJob.timeSecondsCompleted = 0.0f;
            LOGGER.at(Level.FINE).log("Crafted 1 Quantity: %s", currentJob);
            if (currentJob.quantityCompleted == currentJob.quantity) {
                CraftingManager.giveOutput(ref, componentAccessor, currentJob, currentCompletedItemId);
                LOGGER.at(Level.FINE).log("Crafting Finished: %s", currentJob);
                this.queuedCraftingJobs.poll();
            } else {
                if (currentJob.quantityCompleted > currentJob.quantity) {
                    this.queuedCraftingJobs.poll();
                    throw new RuntimeException("QuantityCompleted is greater than the Quality! " + String.valueOf(currentJob));
                }
                CraftingManager.giveOutput(ref, componentAccessor, currentJob, currentCompletedItemId);
            }
            if (!this.queuedCraftingJobs.isEmpty()) continue;
            currentJob.window.setBlockInteractionState("default", componentAccessor.getExternalData().getWorld());
        }
    }

    public boolean cancelAllCrafting(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        LOGGER.at(Level.FINE).log("Cancel Crafting!");
        ObjectArrayList oldJobs = new ObjectArrayList(this.queuedCraftingJobs.size());
        this.queuedCraftingJobs.drainTo(oldJobs);
        if (!oldJobs.isEmpty()) {
            CraftingJob currentJob = (CraftingJob)oldJobs.getFirst();
            LOGGER.at(Level.FINE).log("Refunding Items for: %s", currentJob);
            CraftingManager.refundInputToInventory(ref, componentAccessor, currentJob, currentJob.quantityStarted - 1);
            return true;
        }
        return false;
    }

    private boolean isValidBenchForRecipe(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor, @Nonnull CraftingRecipe recipe) {
        String primaryOutputItemId;
        Player playerComponent = componentAccessor.getComponent(ref, Player.getComponentType());
        assert (playerComponent != null);
        PlayerConfigData playerConfigData = playerComponent.getPlayerConfigData();
        String string = primaryOutputItemId = recipe.getPrimaryOutput() != null ? recipe.getPrimaryOutput().getItemId() : null;
        if (recipe.isKnowledgeRequired() && (primaryOutputItemId == null || !playerConfigData.getKnownRecipes().contains(primaryOutputItemId))) {
            LOGGER.at(Level.WARNING).log("%s - Attempted to craft %s but doesn't know the recipe!", recipe.getId());
            return false;
        }
        World world = componentAccessor.getExternalData().getWorld();
        if (recipe.getRequiredMemoriesLevel() > 1 && MemoriesPlugin.get().getMemoriesLevel(world.getGameplayConfig()) < recipe.getRequiredMemoriesLevel()) {
            LOGGER.at(Level.WARNING).log("Attempted to craft %s but doesn't have the required world memories level!", recipe.getId());
            return false;
        }
        BenchType benchType = this.blockType != null ? this.blockType.getBench().getType() : BenchType.Crafting;
        String benchName = this.blockType != null ? this.blockType.getBench().getId() : "Fieldcraft";
        boolean meetsRequirements = false;
        BlockState state = world.getState(this.x, this.y, this.z, true);
        int benchTierLevel = state instanceof BenchState ? ((BenchState)state).getTierLevel() : 0;
        BenchRequirement[] requirements = recipe.getBenchRequirement();
        if (requirements != null) {
            for (BenchRequirement benchRequirement : requirements) {
                if (benchRequirement.type != benchType || !benchName.equals(benchRequirement.id) || benchRequirement.requiredTierLevel > benchTierLevel) continue;
                meetsRequirements = true;
                break;
            }
        }
        if (!meetsRequirements) {
            LOGGER.at(Level.WARNING).log("Attempted to craft %s using %s, %s but requires bench %s but a bench is NOT set!", recipe.getId(), (Object)benchType, benchName, requirements);
            return false;
        }
        if (benchType == BenchType.Crafting && !"Fieldcraft".equals(benchName)) {
            CraftingJob craftingJob = (CraftingJob)this.queuedCraftingJobs.peek();
            return craftingJob == null || craftingJob.recipe.getId().equals(recipe.getId());
        }
        return true;
    }

    private static void giveOutput(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor, @Nonnull CraftingJob job, int currentItemId) {
        job.removedItems.remove(currentItemId);
        String recipeId = job.recipe.getId();
        CraftingRecipe recipeAsset = CraftingRecipe.getAssetMap().getAsset(recipeId);
        if (recipeAsset == null) {
            throw new RuntimeException("A non-existent item ID was provided! " + recipeId);
        }
        CraftingManager.giveOutput(ref, componentAccessor, recipeAsset, 1);
    }

    private static void giveOutput(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor, @Nonnull CraftingRecipe craftingRecipe, int quantity) {
        Player playerComponent = componentAccessor.getComponent(ref, Player.getComponentType());
        if (playerComponent == null) {
            LOGGER.at(Level.WARNING).log("Attempted to give output to a non-player entity: %s", ref);
            return;
        }
        List<ItemStack> itemStacks = CraftingManager.getOutputItemStacks(craftingRecipe, quantity);
        Inventory inventory = playerComponent.getInventory();
        SimpleItemContainer.addOrDropItemStacks(componentAccessor, ref, inventory.getCombinedArmorHotbarStorage(), itemStacks);
    }

    private static boolean removeInputFromInventory(@Nonnull CraftingJob job, int currentItemId) {
        Objects.requireNonNull(job, "Job can't be null!");
        CraftingRecipe craftingRecipe = job.recipe;
        Objects.requireNonNull(craftingRecipe, "CraftingRecipe can't be null!");
        List<MaterialQuantity> materialsToRemove = CraftingManager.getInputMaterials(craftingRecipe);
        if (materialsToRemove.isEmpty()) {
            return true;
        }
        LOGGER.at(Level.FINEST).log("Removing Materials: %s - %s", (Object)job, materialsToRemove);
        ObjectArrayList itemStackList = new ObjectArrayList();
        boolean succeeded = switch (job.inputRemovalType.ordinal()) {
            case 0 -> {
                ListTransaction<MaterialTransaction> materialTransactions = job.inputItemContainer.removeMaterials(materialsToRemove, true, true, true);
                for (MaterialTransaction transaction : materialTransactions.getList()) {
                    for (MaterialSlotTransaction slotTransaction : transaction.getList()) {
                        if (ItemStack.isEmpty(slotTransaction.getOutput())) continue;
                        itemStackList.add(slotTransaction.getOutput());
                    }
                }
                yield materialTransactions.succeeded();
            }
            case 1 -> {
                ListTransaction<MaterialSlotTransaction> materialTransactions = job.inputItemContainer.removeMaterialsOrdered(materialsToRemove, true, true, true);
                for (MaterialSlotTransaction transaction : materialTransactions.getList()) {
                    if (ItemStack.isEmpty(transaction.getOutput())) continue;
                    itemStackList.add(transaction.getOutput());
                }
                yield materialTransactions.succeeded();
            }
            default -> throw new IllegalArgumentException("Unknown enum: " + String.valueOf((Object)job.inputRemovalType));
        };
        job.removedItems.put(currentItemId, (List<ItemStack>)itemStackList);
        job.window.invalidateExtraResources();
        return succeeded;
    }

    private static boolean removeInputFromInventory(@Nonnull ItemContainer itemContainer, @Nonnull CraftingRecipe craftingRecipe, int quantity) {
        List<MaterialQuantity> materialsToRemove = CraftingManager.getInputMaterials(craftingRecipe, quantity);
        if (materialsToRemove.isEmpty()) {
            return true;
        }
        LOGGER.at(Level.FINEST).log("Removing Materials: %s - %s", (Object)craftingRecipe, materialsToRemove);
        ListTransaction<MaterialTransaction> materialTransactions = itemContainer.removeMaterials(materialsToRemove, true, true, true);
        return materialTransactions.succeeded();
    }

    private static void refundInputToInventory(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor, @Nonnull CraftingJob job, int currentItemId) {
        Objects.requireNonNull(job, "Job can't be null!");
        List itemStacks = (List)job.removedItems.get(currentItemId);
        if (itemStacks == null) {
            return;
        }
        Player playerComponent = componentAccessor.getComponent(ref, Player.getComponentType());
        assert (playerComponent != null);
        SimpleItemContainer.addOrDropItemStacks(componentAccessor, ref, playerComponent.getInventory().getCombinedHotbarFirst(), itemStacks);
    }

    @Nonnull
    public static List<ItemStack> getOutputItemStacks(@Nonnull CraftingRecipe recipe) {
        return CraftingManager.getOutputItemStacks(recipe, 1);
    }

    @Nonnull
    public static List<ItemStack> getOutputItemStacks(@Nonnull CraftingRecipe recipe, int quantity) {
        Objects.requireNonNull(recipe);
        MaterialQuantity[] output = recipe.getOutputs();
        if (output == null) {
            return List.of();
        }
        ObjectArrayList<ItemStack> outputItemStacks = new ObjectArrayList<ItemStack>();
        for (MaterialQuantity outputMaterial : output) {
            ItemStack outputItemStack = CraftingManager.getOutputItemStack(outputMaterial, quantity);
            if (outputItemStack == null) continue;
            outputItemStacks.add(outputItemStack);
        }
        return outputItemStacks;
    }

    @Nullable
    public static ItemStack getOutputItemStack(@Nonnull MaterialQuantity outputMaterial, @Nonnull String id) {
        return CraftingManager.getOutputItemStack(outputMaterial, 1);
    }

    @Nullable
    public static ItemStack getOutputItemStack(@Nonnull MaterialQuantity outputMaterial, int quantity) {
        String itemId = outputMaterial.getItemId();
        if (itemId == null) {
            return null;
        }
        int materialQuantity = outputMaterial.getQuantity() <= 0 ? 1 : outputMaterial.getQuantity();
        return new ItemStack(itemId, materialQuantity * quantity, outputMaterial.getMetadata());
    }

    @Nonnull
    public static List<MaterialQuantity> getInputMaterials(@Nonnull CraftingRecipe recipe) {
        return CraftingManager.getInputMaterials(recipe, 1);
    }

    @Nonnull
    private static List<MaterialQuantity> getInputMaterials(@Nonnull MaterialQuantity[] input) {
        return CraftingManager.getInputMaterials(input, 1);
    }

    @Nonnull
    public static List<MaterialQuantity> getInputMaterials(@Nonnull CraftingRecipe recipe, int quantity) {
        Objects.requireNonNull(recipe);
        if (recipe.getInput() == null) {
            return Collections.emptyList();
        }
        return CraftingManager.getInputMaterials(recipe.getInput(), quantity);
    }

    @Nonnull
    private static List<MaterialQuantity> getInputMaterials(@Nonnull MaterialQuantity[] input, int quantity) {
        ObjectArrayList<MaterialQuantity> materials = new ObjectArrayList<MaterialQuantity>();
        for (MaterialQuantity craftingMaterial : input) {
            String itemId = craftingMaterial.getItemId();
            String resourceTypeId = craftingMaterial.getResourceTypeId();
            int materialQuantity = craftingMaterial.getQuantity();
            BsonDocument metadata = craftingMaterial.getMetadata();
            materials.add(new MaterialQuantity(itemId, resourceTypeId, null, materialQuantity * quantity, metadata));
        }
        return materials;
    }

    public static boolean matches(@Nonnull MaterialQuantity craftingMaterial, @Nonnull ItemStack itemStack) {
        String itemId = craftingMaterial.getItemId();
        if (itemId != null) {
            return itemId.equals(itemStack.getItemId());
        }
        String resourceTypeId = craftingMaterial.getResourceTypeId();
        if (resourceTypeId != null && itemStack.getItem().getResourceTypes() != null) {
            for (ItemResourceType itemResourceType : itemStack.getItem().getResourceTypes()) {
                if (!resourceTypeId.equals(itemResourceType.id)) continue;
                return true;
            }
        }
        return false;
    }

    @Nonnull
    public static JsonArray generateInventoryHints(@Nonnull List<CraftingRecipe> recipes, int inputSlotIndex, @Nonnull ItemContainer container) {
        JsonArray inventoryHints = new JsonArray();
        short bound = container.getCapacity();
        for (short storageSlotIndex = 0; storageSlotIndex < bound; storageSlotIndex = (short)(storageSlotIndex + 1)) {
            ItemStack itemStack = container.getItemStack(storageSlotIndex);
            if (itemStack == null || itemStack.isEmpty() || !CraftingManager.matchesAnyRecipe(recipes, inputSlotIndex, itemStack)) continue;
            inventoryHints.add(storageSlotIndex);
        }
        return inventoryHints;
    }

    public static boolean matchesAnyRecipe(@Nonnull List<CraftingRecipe> recipes, int inputSlotIndex, @Nonnull ItemStack slotItemStack) {
        for (CraftingRecipe recipe : recipes) {
            MaterialQuantity[] input = recipe.getInput();
            if (inputSlotIndex >= input.length) continue;
            MaterialQuantity slotCraftingMaterial = input[inputSlotIndex];
            if (slotCraftingMaterial.getItemId() != null && slotCraftingMaterial.getItemId().equals(slotItemStack.getItemId())) {
                return true;
            }
            if (slotCraftingMaterial.getResourceTypeId() == null || slotItemStack.getItem().getResourceTypes() == null) continue;
            for (ItemResourceType itemResourceType : slotItemStack.getItem().getResourceTypes()) {
                if (!slotCraftingMaterial.getResourceTypeId().equals(itemResourceType.id)) continue;
                return true;
            }
        }
        return false;
    }

    public boolean startTierUpgrade(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor, @Nonnull BenchWindow window) {
        CombinedItemContainer combined;
        if (this.upgradingJob != null) {
            return false;
        }
        BenchUpgradeRequirement requirements = this.getBenchUpgradeRequirement(this.getBenchTierLevel(componentAccessor));
        if (requirements == null) {
            return false;
        }
        List<MaterialQuantity> input = CraftingManager.getInputMaterials(requirements.getInput());
        if (input.isEmpty()) {
            return false;
        }
        Player playerComponent = componentAccessor.getComponent(ref, Player.getComponentType());
        assert (playerComponent != null);
        if (playerComponent.getGameMode() != GameMode.Creative && !(combined = new CombinedItemContainer(playerComponent.getInventory().getCombinedBackpackStorageHotbar(), window.getExtraResourcesSection().getItemContainer())).canRemoveMaterials(input)) {
            return false;
        }
        this.upgradingJob = new BenchUpgradingJob(window, requirements.getTimeSeconds());
        this.cancelAllCrafting(ref, componentAccessor);
        return true;
    }

    private int finishTierUpgrade(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        boolean canUpgrade;
        BenchState benchState;
        if (this.upgradingJob == null) {
            return 0;
        }
        World world = componentAccessor.getExternalData().getWorld();
        BlockState state = world.getState(this.x, this.y, this.z, true);
        BenchState benchState2 = benchState = state instanceof BenchState ? (BenchState)state : null;
        if (benchState == null || benchState.getTierLevel() == 0) {
            return 0;
        }
        BenchUpgradeRequirement requirements = this.getBenchUpgradeRequirement(benchState.getTierLevel());
        if (requirements == null) {
            return benchState.getTierLevel();
        }
        List<MaterialQuantity> input = CraftingManager.getInputMaterials(requirements.getInput());
        if (input.isEmpty()) {
            return benchState.getTierLevel();
        }
        Player playerComponent = componentAccessor.getComponent(ref, Player.getComponentType());
        assert (playerComponent != null);
        boolean bl = canUpgrade = playerComponent.getGameMode() == GameMode.Creative;
        if (!canUpgrade) {
            CombinedItemContainer combined = new CombinedItemContainer(playerComponent.getInventory().getCombinedBackpackStorageHotbar(), this.upgradingJob.window.getExtraResourcesSection().getItemContainer());
            ListTransaction<MaterialTransaction> materialTransactions = (combined = new CombinedItemContainer(combined, this.upgradingJob.window.getExtraResourcesSection().getItemContainer())).removeMaterials(input);
            if (materialTransactions.succeeded()) {
                ObjectArrayList<ItemStack> consumed = new ObjectArrayList<ItemStack>();
                for (MaterialTransaction transaction : materialTransactions.getList()) {
                    for (MaterialSlotTransaction matSlot : transaction.getList()) {
                        consumed.add(matSlot.getOutput());
                    }
                }
                benchState.addUpgradeItems(consumed);
                canUpgrade = true;
            }
        }
        if (canUpgrade) {
            benchState.setTierLevel(benchState.getTierLevel() + 1);
            if (benchState.getBench().getBenchUpgradeCompletedSoundEventIndex() != 0) {
                SoundUtil.playSoundEvent3d(benchState.getBench().getBenchUpgradeCompletedSoundEventIndex(), SoundCategory.SFX, (double)this.x + 0.5, (double)this.y + 0.5, (double)this.z + 0.5, componentAccessor);
            }
        }
        return benchState.getTierLevel();
    }

    @Nullable
    private BenchTierLevel getBenchTierLevelData(int level) {
        if (this.blockType == null) {
            return null;
        }
        Bench bench = this.blockType.getBench();
        return bench == null ? null : bench.getTierLevel(level);
    }

    @Nullable
    private BenchUpgradeRequirement getBenchUpgradeRequirement(int tierLevel) {
        BenchTierLevel tierData = this.getBenchTierLevelData(tierLevel);
        return tierData == null ? null : tierData.getUpgradeRequirement();
    }

    private int getBenchTierLevel(@Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        World world = componentAccessor.getExternalData().getWorld();
        BlockState state = world.getState(this.x, this.y, this.z, true);
        return state instanceof BenchState ? ((BenchState)state).getTierLevel() : 0;
    }

    public static int feedExtraResourcesSection(@Nonnull BenchState benchState, @Nonnull MaterialExtraResourcesSection extraResourcesSection) {
        ChestLookupResult result = CraftingManager.getContainersAroundBench(benchState);
        List<ItemContainer> chests = result.containers;
        List<ItemContainerState> chestStates = result.states;
        ItemContainer itemContainer = EmptyItemContainer.INSTANCE;
        if (!chests.isEmpty()) {
            itemContainer = new CombinedItemContainer((ItemContainer[])chests.stream().map(container -> {
                DelegateItemContainer<ItemContainer> delegate = new DelegateItemContainer<ItemContainer>((ItemContainer)container);
                delegate.setGlobalFilter(FilterType.ALLOW_OUTPUT_ONLY);
                return delegate;
            }).toArray(ItemContainer[]::new));
        }
        Object2ObjectOpenHashMap materials = new Object2ObjectOpenHashMap();
        for (ItemContainer chest : chests) {
            chest.forEach((i, itemStack) -> {
                if (!CraftingPlugin.isValidUpgradeMaterialForBench(benchState, itemStack) && !CraftingPlugin.isValidCraftingMaterialForBench(benchState, itemStack)) {
                    return;
                }
                materials.computeIfAbsent(itemStack.getItemId(), (Function<String, ItemQuantity>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, lambda$feedExtraResourcesSection$3(com.hypixel.hytale.server.core.inventory.ItemStack java.lang.String ), (Ljava/lang/String;)Lcom/hypixel/hytale/protocol/ItemQuantity;)((ItemStack)itemStack)).quantity += itemStack.getQuantity();
            });
        }
        extraResourcesSection.setItemContainer(itemContainer);
        extraResourcesSection.setExtraMaterials(materials.values().toArray(new ItemQuantity[0]));
        extraResourcesSection.setValid(true);
        return chestStates.size();
    }

    @Nonnull
    protected static ChestLookupResult getContainersAroundBench(@Nonnull BenchState benchState) {
        ObjectArrayList<ItemContainer> containers = new ObjectArrayList<ItemContainer>();
        ObjectArrayList<ItemContainerState> states = new ObjectArrayList<ItemContainerState>();
        ObjectArrayList<ItemContainerState> spatialResults = new ObjectArrayList<ItemContainerState>();
        ObjectArrayList<ItemContainerState> filteredOut = new ObjectArrayList<ItemContainerState>();
        World world = benchState.getChunk().getWorld();
        Store<ChunkStore> store = world.getChunkStore().getStore();
        int limit = world.getGameplayConfig().getCraftingConfig().getBenchMaterialChestLimit();
        double horizontalRadius = world.getGameplayConfig().getCraftingConfig().getBenchMaterialHorizontalChestSearchRadius();
        double verticalRadius = world.getGameplayConfig().getCraftingConfig().getBenchMaterialVerticalChestSearchRadius();
        Vector3d blockPos = benchState.getBlockPosition().toVector3d();
        BlockBoundingBoxes hitboxAsset = BlockBoundingBoxes.getAssetMap().getAsset(benchState.getBlockType().getHitboxTypeIndex());
        BlockBoundingBoxes.RotatedVariantBoxes rotatedHitbox = hitboxAsset.get(benchState.getRotationIndex());
        Box boundingBox = rotatedHitbox.getBoundingBox();
        double benchWidth = boundingBox.width();
        double benchHeight = boundingBox.height();
        double benchDepth = boundingBox.depth();
        double extraSearchRadius = Math.max(benchWidth, Math.max(benchDepth, benchHeight)) - 1.0;
        SpatialResource<Ref<ChunkStore>, ChunkStore> blockStateSpatialStructure = store.getResource(BlockStateModule.get().getItemContainerSpatialResourceType());
        ObjectList results = SpatialResource.getThreadLocalReferenceList();
        blockStateSpatialStructure.getSpatialStructure().ordered3DAxis(blockPos, horizontalRadius + extraSearchRadius, verticalRadius + extraSearchRadius, horizontalRadius + extraSearchRadius, results);
        if (!results.isEmpty()) {
            int benchMinBlockX = (int)Math.floor(boundingBox.min.x);
            int benchMinBlockY = (int)Math.floor(boundingBox.min.y);
            int benchMinBlockZ = (int)Math.floor(boundingBox.min.z);
            int benchMaxBlockX = (int)Math.ceil(boundingBox.max.x) - 1;
            int benchMaxBlockY = (int)Math.ceil(boundingBox.max.y) - 1;
            int benchMaxBlockZ = (int)Math.ceil(boundingBox.max.z) - 1;
            double minX = blockPos.x + (double)benchMinBlockX - horizontalRadius;
            double minY = blockPos.y + (double)benchMinBlockY - verticalRadius;
            double minZ = blockPos.z + (double)benchMinBlockZ - horizontalRadius;
            double maxX = blockPos.x + (double)benchMaxBlockX + horizontalRadius;
            double maxY = blockPos.y + (double)benchMaxBlockY + verticalRadius;
            double maxZ = blockPos.z + (double)benchMaxBlockZ + horizontalRadius;
            for (Ref ref : results) {
                BlockState state = BlockState.getBlockState(ref, ref.getStore());
                if (!(state instanceof ItemContainerState)) continue;
                ItemContainerState chest = (ItemContainerState)state;
                spatialResults.add(chest);
            }
            for (ItemContainerState itemContainerState : spatialResults) {
                Vector3d chestBlockPos = itemContainerState.getBlockPosition().toVector3d();
                if (chestBlockPos.x >= minX && chestBlockPos.x <= maxX && chestBlockPos.y >= minY && chestBlockPos.y <= maxY && chestBlockPos.z >= minZ && chestBlockPos.z <= maxZ) {
                    containers.add(itemContainerState.getItemContainer());
                    states.add(itemContainerState);
                    if (containers.size() < limit) continue;
                    break;
                }
                filteredOut.add(itemContainerState);
            }
        }
        return new ChestLookupResult(containers, states, spatialResults, filteredOut, blockPos);
    }

    @Nonnull
    public String toString() {
        return "CraftingManager{queuedCraftingJobs=" + String.valueOf(this.queuedCraftingJobs) + ", x=" + this.x + ", y=" + this.y + ", z=" + this.z + ", blockType=" + String.valueOf(this.blockType) + "}";
    }

    @Override
    @Nonnull
    public Component<EntityStore> clone() {
        return new CraftingManager(this);
    }

    private static /* synthetic */ ItemQuantity lambda$feedExtraResourcesSection$3(ItemStack itemStack, String k) {
        return new ItemQuantity(itemStack.getItemId(), 0);
    }

    private static class BenchUpgradingJob {
        @Nonnull
        private final BenchWindow window;
        private final float timeSeconds;
        private float timeSecondsCompleted;
        private float lastSentPercent;

        private BenchUpgradingJob(@Nonnull BenchWindow window, float timeSeconds) {
            this.window = window;
            this.timeSeconds = timeSeconds;
        }

        public String toString() {
            return "BenchUpgradingJob{window=" + String.valueOf(this.window) + ", timeSeconds=" + this.timeSeconds + "}";
        }

        public float computeLoadingPercent() {
            return this.timeSeconds <= 0.0f ? 1.0f : Math.min(this.timeSecondsCompleted / this.timeSeconds, 1.0f);
        }
    }

    private static class CraftingJob {
        @Nonnull
        private final CraftingWindow window;
        private final int transactionId;
        @Nonnull
        private final CraftingRecipe recipe;
        private final int quantity;
        private final float timeSeconds;
        @Nonnull
        private final ItemContainer inputItemContainer;
        @Nonnull
        private final InputRemovalType inputRemovalType;
        @Nonnull
        private final Int2ObjectMap<List<ItemStack>> removedItems = new Int2ObjectOpenHashMap<List<ItemStack>>();
        private int quantityStarted;
        private int quantityCompleted;
        private float timeSecondsCompleted;

        public CraftingJob(@Nonnull CraftingWindow window, int transactionId, @Nonnull CraftingRecipe recipe, int quantity, float timeSeconds, @Nonnull ItemContainer inputItemContainer, @Nonnull InputRemovalType inputRemovalType) {
            this.window = window;
            this.transactionId = transactionId;
            this.recipe = recipe;
            this.quantity = quantity;
            this.timeSeconds = timeSeconds;
            this.inputItemContainer = inputItemContainer;
            this.inputRemovalType = inputRemovalType;
        }

        @Nonnull
        public String toString() {
            return "CraftingJob{window=" + String.valueOf(this.window) + ", transactionId=" + this.transactionId + ", recipe=" + String.valueOf(this.recipe) + ", quantity=" + this.quantity + ", timeSeconds=" + this.timeSeconds + ", inputItemContainer=" + String.valueOf(this.inputItemContainer) + ", inputRemovalType=" + String.valueOf((Object)this.inputRemovalType) + ", removedItems=" + String.valueOf(this.removedItems) + ", quantityStarted=" + this.quantityStarted + ", quantityCompleted=" + this.quantityCompleted + ", timeSecondsCompleted=" + this.timeSecondsCompleted + "}";
        }
    }

    public static enum InputRemovalType {
        NORMAL,
        ORDERED;

    }

    protected record ChestLookupResult(List<ItemContainer> containers, List<ItemContainerState> states, List<ItemContainerState> spatialResults, List<ItemContainerState> filteredOut, Vector3d benchCenteredPos) {
    }
}

