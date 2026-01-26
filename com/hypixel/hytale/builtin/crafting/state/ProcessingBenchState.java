/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.crafting.state;

import com.google.common.flogger.LazyArgs;
import com.hypixel.hytale.builtin.crafting.CraftingPlugin;
import com.hypixel.hytale.builtin.crafting.component.CraftingManager;
import com.hypixel.hytale.builtin.crafting.state.BenchState;
import com.hypixel.hytale.builtin.crafting.window.ProcessingBenchWindow;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.protocol.Transform;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.server.core.asset.type.blockhitbox.BlockBoundingBoxes;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.BenchTierLevel;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.ProcessingBench;
import com.hypixel.hytale.server.core.asset.type.item.config.CraftingRecipe;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.MaterialQuantity;
import com.hypixel.hytale.server.core.inventory.ResourceQuantity;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.inventory.container.InternalContainerUtilMaterial;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.inventory.container.TestRemoveItemSlotResult;
import com.hypixel.hytale.server.core.inventory.container.filter.FilterActionType;
import com.hypixel.hytale.server.core.inventory.container.filter.FilterType;
import com.hypixel.hytale.server.core.inventory.container.filter.ResourceFilter;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.ListTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.MaterialSlotTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.MaterialTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.ResourceTransaction;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.accessor.BlockAccessor;
import com.hypixel.hytale.server.core.universe.world.chunk.state.TickableBlockState;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.universe.world.meta.state.DestroyableBlockState;
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerBlockState;
import com.hypixel.hytale.server.core.universe.world.meta.state.MarkerBlockState;
import com.hypixel.hytale.server.core.universe.world.meta.state.PlacedByBlockState;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
import com.hypixel.hytale.server.core.util.PositionUtil;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonDocument;

public class ProcessingBenchState
extends BenchState
implements TickableBlockState,
ItemContainerBlockState,
DestroyableBlockState,
MarkerBlockState,
PlacedByBlockState {
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    public static final boolean EXACT_RESOURCE_AMOUNTS = true;
    public static final Codec<ProcessingBenchState> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ProcessingBenchState.class, ProcessingBenchState::new, BenchState.CODEC).append(new KeyedCodec<ItemContainer>("InputContainer", ItemContainer.CODEC), (state, o) -> {
        state.inputContainer = o;
    }, state -> state.inputContainer).add()).append(new KeyedCodec<ItemContainer>("FuelContainer", ItemContainer.CODEC), (state, o) -> {
        state.fuelContainer = o;
    }, state -> state.fuelContainer).add()).append(new KeyedCodec<ItemContainer>("OutputContainer", ItemContainer.CODEC), (state, o) -> {
        state.outputContainer = o;
    }, state -> state.outputContainer).add()).append(new KeyedCodec<Double>("Progress", Codec.DOUBLE), (state, d) -> {
        state.inputProgress = d.floatValue();
    }, state -> state.inputProgress).add()).append(new KeyedCodec<Double>("FuelTime", Codec.DOUBLE), (state, d) -> {
        state.fuelTime = d.floatValue();
    }, state -> state.fuelTime).add()).append(new KeyedCodec<Boolean>("Active", Codec.BOOLEAN), (state, b) -> {
        state.active = b;
    }, state -> state.active).add()).append(new KeyedCodec<Integer>("NextExtra", Codec.INTEGER), (state, b) -> {
        state.nextExtra = b;
    }, state -> state.nextExtra).add()).append(new KeyedCodec<WorldMapManager.MarkerReference>("Marker", WorldMapManager.MarkerReference.CODEC), (state, o) -> {
        state.marker = o;
    }, state -> state.marker).add()).append(new KeyedCodec<String>("RecipeId", Codec.STRING), (state, o) -> {
        state.recipeId = o;
    }, state -> state.recipeId).add()).build();
    private static final float EJECT_VELOCITY = 2.0f;
    private static final float EJECT_SPREAD_VELOCITY = 1.0f;
    private static final float EJECT_VERTICAL_VELOCITY = 3.25f;
    public static final String PROCESSING = "Processing";
    public static final String PROCESS_COMPLETED = "ProcessCompleted";
    protected WorldMapManager.MarkerReference marker;
    private ProcessingBench processingBench;
    private ItemContainer inputContainer;
    private ItemContainer fuelContainer;
    private ItemContainer outputContainer;
    private CombinedItemContainer combinedItemContainer;
    private float inputProgress;
    private float fuelTime;
    private int lastConsumedFuelTotal;
    private int nextExtra = -1;
    private final Set<Short> processingSlots = new HashSet<Short>();
    private final Set<Short> processingFuelSlots = new HashSet<Short>();
    @Nullable
    private String recipeId;
    @Nullable
    private CraftingRecipe recipe;
    private boolean active = false;

    @Override
    public boolean initialize(@Nonnull BlockType blockType) {
        if (!super.initialize(blockType)) {
            if (this.bench == null) {
                World world;
                Store<EntityStore> store;
                Holder[] itemEntityHolders;
                ObjectArrayList<ItemStack> itemStacks = new ObjectArrayList<ItemStack>();
                if (this.inputContainer != null) {
                    itemStacks.addAll(this.inputContainer.dropAllItemStacks());
                }
                if (this.fuelContainer != null) {
                    itemStacks.addAll(this.fuelContainer.dropAllItemStacks());
                }
                if (this.outputContainer != null) {
                    itemStacks.addAll(this.outputContainer.dropAllItemStacks());
                }
                if ((itemEntityHolders = this.ejectItems(store = (world = this.getChunk().getWorld()).getEntityStore().getStore(), itemStacks)).length > 0) {
                    world.execute(() -> store.addEntities(itemEntityHolders, AddReason.SPAWN));
                }
            }
            return false;
        }
        if (!(this.bench instanceof ProcessingBench)) {
            LOGGER.at(Level.SEVERE).log("Wrong bench type for processing. Got %s", this.bench.getClass().getName());
            return false;
        }
        this.processingBench = (ProcessingBench)this.bench;
        if (this.nextExtra == -1) {
            this.nextExtra = this.processingBench.getExtraOutput() != null ? this.processingBench.getExtraOutput().getPerFuelItemsConsumed() : 0;
        }
        this.setupSlots();
        return true;
    }

    private void setupSlots() {
        String resourceTypeId;
        ObjectArrayList<ItemStack> remainder = new ObjectArrayList<ItemStack>();
        int tierLevel = this.getTierLevel();
        ProcessingBench.ProcessingSlot[] input = this.processingBench.getInput(tierLevel);
        short inputSlotsCount = (short)input.length;
        this.inputContainer = ItemContainer.ensureContainerCapacity(this.inputContainer, inputSlotsCount, SimpleItemContainer::getNewContainer, remainder);
        this.inputContainer.registerChangeEvent(EventPriority.LAST, this::onItemChange);
        for (short slot = 0; slot < inputSlotsCount; slot = (short)(slot + 1)) {
            ProcessingBench.ProcessingSlot inputSlot = input[slot];
            resourceTypeId = inputSlot.getResourceTypeId();
            boolean shouldFilterValidIngredients = inputSlot.shouldFilterValidIngredients();
            if (resourceTypeId != null) {
                this.inputContainer.setSlotFilter(FilterActionType.ADD, slot, new ResourceFilter(new ResourceQuantity(resourceTypeId, 1)));
                continue;
            }
            if (!shouldFilterValidIngredients) continue;
            ObjectArrayList<MaterialQuantity> validIngredients = new ObjectArrayList<MaterialQuantity>();
            List<CraftingRecipe> recipes = CraftingPlugin.getBenchRecipes(this.bench.getType(), this.bench.getId());
            for (CraftingRecipe recipe : recipes) {
                if (recipe.isRestrictedByBenchTierLevel(this.bench.getId(), tierLevel)) continue;
                List<MaterialQuantity> inputMaterials = CraftingManager.getInputMaterials(recipe);
                validIngredients.addAll((Collection<MaterialQuantity>)inputMaterials);
            }
            this.inputContainer.setSlotFilter(FilterActionType.ADD, slot, (actionType, container, slotIndex, itemStack) -> {
                if (itemStack == null) {
                    return true;
                }
                for (MaterialQuantity ingredient : validIngredients) {
                    if (!CraftingManager.matches(ingredient, itemStack)) continue;
                    return true;
                }
                return false;
            });
        }
        ProcessingBench.ProcessingSlot[] benchFuel = this.processingBench.getFuel();
        short fuelCapacity = (short)(benchFuel != null ? benchFuel.length : 0);
        this.fuelContainer = ItemContainer.ensureContainerCapacity(this.fuelContainer, fuelCapacity, SimpleItemContainer::getNewContainer, remainder);
        this.fuelContainer.registerChangeEvent(EventPriority.LAST, this::onItemChange);
        if (fuelCapacity > 0) {
            for (int i = 0; i < benchFuel.length; ++i) {
                ProcessingBench.ProcessingSlot fuel = benchFuel[i];
                resourceTypeId = fuel.getResourceTypeId();
                if (resourceTypeId == null) continue;
                this.fuelContainer.setSlotFilter(FilterActionType.ADD, (short)i, new ResourceFilter(new ResourceQuantity(resourceTypeId, 1)));
            }
        }
        short outputSlotsCount = (short)this.processingBench.getOutputSlotsCount(tierLevel);
        this.outputContainer = ItemContainer.ensureContainerCapacity(this.outputContainer, outputSlotsCount, SimpleItemContainer::getNewContainer, remainder);
        this.outputContainer.registerChangeEvent(EventPriority.LAST, this::onItemChange);
        if (outputSlotsCount > 0) {
            this.outputContainer.setGlobalFilter(FilterType.ALLOW_OUTPUT_ONLY);
        }
        this.combinedItemContainer = new CombinedItemContainer(this.fuelContainer, this.inputContainer, this.outputContainer);
        World world = this.getChunk().getWorld();
        Store<EntityStore> store = world.getEntityStore().getStore();
        Holder[] itemEntityHolders = this.ejectItems(store, remainder);
        if (itemEntityHolders.length > 0) {
            world.execute(() -> store.addEntities(itemEntityHolders, AddReason.SPAWN));
        }
        this.inputContainer.registerChangeEvent(EventPriority.LAST, event -> this.updateRecipe());
        if (this.processingBench.getFuel() == null) {
            this.setActive(true);
        }
    }

    @Override
    public void tick(float dt, int index, ArchetypeChunk<ChunkStore> archetypeChunk, @Nonnull Store<ChunkStore> store, CommandBuffer<ChunkStore> commandBuffer) {
        ProcessingBench.ProcessingSlot[] fuelSlots;
        boolean hasFuelSlots;
        World world = store.getExternalData().getWorld();
        Store<EntityStore> entityStore = world.getEntityStore().getStore();
        BlockType blockType = this.getBlockType();
        String currentState = BlockAccessor.getCurrentInteractionState(blockType);
        List<ItemStack> outputItemStacks = null;
        List<MaterialQuantity> inputMaterials = null;
        this.processingSlots.clear();
        this.checkForRecipeUpdate();
        if (this.recipe != null) {
            outputItemStacks = CraftingManager.getOutputItemStacks(this.recipe);
            if (!this.outputContainer.canAddItemStacks(outputItemStacks, false, false)) {
                if (PROCESSING.equals(currentState)) {
                    this.setBlockInteractionState("default", blockType);
                    this.playSound(world, this.processingBench.getFailedSoundEventIndex(), entityStore);
                } else if (PROCESS_COMPLETED.equals(currentState)) {
                    this.setBlockInteractionState("default", blockType);
                    this.playSound(world, this.processingBench.getEndSoundEventIndex(), entityStore);
                }
                this.setActive(false);
                return;
            }
            inputMaterials = CraftingManager.getInputMaterials(this.recipe);
            List<TestRemoveItemSlotResult> result = this.inputContainer.getSlotMaterialsToRemove(inputMaterials, true, true);
            if (result.isEmpty()) {
                if (PROCESSING.equals(currentState)) {
                    this.setBlockInteractionState("default", blockType);
                    this.playSound(world, this.processingBench.getFailedSoundEventIndex(), entityStore);
                } else if (PROCESS_COMPLETED.equals(currentState)) {
                    this.setBlockInteractionState("default", blockType);
                    this.playSound(world, this.processingBench.getEndSoundEventIndex(), entityStore);
                }
                this.inputProgress = 0.0f;
                this.setActive(false);
                this.recipeId = null;
                this.recipe = null;
                return;
            }
            for (TestRemoveItemSlotResult item : result) {
                this.processingSlots.addAll(item.getPickedSlots());
            }
            this.sendProcessingSlots();
        } else {
            if (this.processingBench.getFuel() == null) {
                if (PROCESSING.equals(currentState)) {
                    this.setBlockInteractionState("default", blockType);
                    this.playSound(world, this.processingBench.getFailedSoundEventIndex(), entityStore);
                } else if (PROCESS_COMPLETED.equals(currentState)) {
                    this.setBlockInteractionState("default", blockType);
                    this.playSound(world, this.processingBench.getEndSoundEventIndex(), entityStore);
                }
                return;
            }
            boolean allowNoInputProcessing = this.processingBench.shouldAllowNoInputProcessing();
            if (!allowNoInputProcessing && PROCESSING.equals(currentState)) {
                this.setBlockInteractionState("default", blockType);
                this.playSound(world, this.processingBench.getFailedSoundEventIndex(), entityStore);
            } else if (PROCESS_COMPLETED.equals(currentState)) {
                this.setBlockInteractionState("default", blockType);
                this.playSound(world, this.processingBench.getEndSoundEventIndex(), entityStore);
                this.setActive(false);
                this.sendProgress(0.0f);
                return;
            }
            this.sendProgress(0.0f);
            if (!allowNoInputProcessing) {
                this.setActive(false);
                return;
            }
        }
        boolean needsUpdate = false;
        if (this.fuelTime > 0.0f && this.active) {
            this.fuelTime -= dt;
            if (this.fuelTime < 0.0f) {
                this.fuelTime = 0.0f;
            }
            needsUpdate = true;
        }
        boolean bl = hasFuelSlots = (fuelSlots = this.processingBench.getFuel()) != null && fuelSlots.length > 0;
        if ((this.processingBench.getMaxFuel() <= 0 || this.fuelTime < (float)this.processingBench.getMaxFuel()) && !this.fuelContainer.isEmpty()) {
            if (!hasFuelSlots) {
                return;
            }
            if (this.active) {
                if (this.fuelTime > 0.0f) {
                    for (i = 0; i < fuelSlots.length; ++i) {
                        ItemStack itemInSlot = this.fuelContainer.getItemStack((short)i);
                        if (itemInSlot == null) continue;
                        this.processingFuelSlots.add((short)i);
                        break;
                    }
                } else {
                    if (this.fuelTime < 0.0f) {
                        this.fuelTime = 0.0f;
                    }
                    this.processingFuelSlots.clear();
                    for (i = 0; i < fuelSlots.length; ++i) {
                        ProcessingBench.ProcessingSlot fuelSlot = fuelSlots[i];
                        String resourceTypeId = fuelSlot.getResourceTypeId() != null ? fuelSlot.getResourceTypeId() : "Fuel";
                        ResourceQuantity resourceQuantity = new ResourceQuantity(resourceTypeId, 1);
                        ItemStack slot = this.fuelContainer.getItemStack((short)i);
                        if (slot == null) continue;
                        double fuelQuality = slot.getItem().getFuelQuality();
                        ResourceTransaction transaction = this.fuelContainer.removeResource(resourceQuantity, true, true, true);
                        this.processingFuelSlots.add((short)i);
                        if (transaction.getRemainder() > 0) continue;
                        ProcessingBench.ExtraOutput extra = this.processingBench.getExtraOutput();
                        if (extra != null && !extra.isIgnoredFuelSource(slot.getItem())) {
                            --this.nextExtra;
                            if (this.nextExtra <= 0) {
                                this.nextExtra = extra.getPerFuelItemsConsumed();
                                ObjectArrayList<ItemStack> extraItemStacks = new ObjectArrayList<ItemStack>(extra.getOutputs().length);
                                for (MaterialQuantity e : extra.getOutputs()) {
                                    extraItemStacks.add(e.toItemStack());
                                }
                                ListTransaction<ItemStackTransaction> addTransaction = this.outputContainer.addItemStacks(extraItemStacks, false, false, false);
                                ObjectArrayList<ItemStack> remainderItems = new ObjectArrayList<ItemStack>();
                                for (ItemStackTransaction itemStackTransaction : addTransaction.getList()) {
                                    ItemStack remainder = itemStackTransaction.getRemainder();
                                    if (remainder == null || remainder.isEmpty()) continue;
                                    remainderItems.add(remainder);
                                }
                                if (!remainderItems.isEmpty()) {
                                    LOGGER.at(Level.WARNING).log("Dropping excess items at %s", this.getBlockPosition());
                                    Holder<EntityStore>[] itemEntityHolders = this.ejectItems(entityStore, remainderItems);
                                    entityStore.addEntities(itemEntityHolders, AddReason.SPAWN);
                                }
                            }
                        }
                        this.fuelTime = (float)((double)this.fuelTime + (double)transaction.getConsumed() * fuelQuality);
                        needsUpdate = true;
                        break;
                    }
                }
            }
        }
        if (needsUpdate) {
            this.updateFuelValues();
        }
        if (hasFuelSlots && (!this.active || this.fuelTime <= 0.0f)) {
            this.lastConsumedFuelTotal = 0;
            if (PROCESSING.equals(currentState)) {
                this.setBlockInteractionState("default", blockType);
                this.playSound(world, this.processingBench.getFailedSoundEventIndex(), entityStore);
                if (this.processingBench.getFuel() != null) {
                    this.setActive(false);
                }
            } else if (PROCESS_COMPLETED.equals(currentState)) {
                this.setBlockInteractionState("default", blockType);
                this.playSound(world, this.processingBench.getFailedSoundEventIndex(), entityStore);
                if (this.processingBench.getFuel() != null) {
                    this.setActive(false);
                }
            }
            return;
        }
        if (!PROCESSING.equals(currentState)) {
            this.setBlockInteractionState(PROCESSING, blockType);
        }
        if (this.recipe != null && (this.fuelTime > 0.0f || this.processingBench.getFuel() == null)) {
            this.inputProgress += dt;
        }
        if (this.recipe != null) {
            float recipeTime = this.recipe.getTimeSeconds();
            float craftingTimeReductionModifier = this.getCraftingTimeReductionModifier();
            if (craftingTimeReductionModifier > 0.0f) {
                recipeTime -= recipeTime * craftingTimeReductionModifier;
            }
            if (this.inputProgress > recipeTime) {
                if (recipeTime > 0.0f) {
                    this.inputProgress -= recipeTime;
                    float progressPercent = this.inputProgress / recipeTime;
                    this.sendProgress(progressPercent);
                } else {
                    this.inputProgress = 0.0f;
                    this.sendProgress(0.0f);
                }
                LOGGER.at(Level.FINE).log("Do Process for %s %s", (Object)this.recipeId, (Object)this.recipe);
                if (inputMaterials != null) {
                    ObjectArrayList<ItemStack> remainderItems = new ObjectArrayList<ItemStack>();
                    int success = 0;
                    IntArrayList slots = new IntArrayList();
                    for (int j = 0; j < this.inputContainer.getCapacity(); ++j) {
                        slots.add(j);
                    }
                    block6: for (MaterialQuantity materialQuantity : inputMaterials) {
                        for (int i = 0; i < slots.size(); ++i) {
                            int slot = slots.getInt(i);
                            MaterialSlotTransaction transaction = this.inputContainer.removeMaterialFromSlot((short)slot, materialQuantity, true, true, true);
                            if (!transaction.succeeded()) continue;
                            ++success;
                            slots.removeInt(i);
                            continue block6;
                        }
                    }
                    ListTransaction<ItemStackTransaction> addTransaction = this.outputContainer.addItemStacks(outputItemStacks, false, false, false);
                    if (!addTransaction.succeeded()) {
                        return;
                    }
                    for (ItemStackTransaction itemStackTransaction : addTransaction.getList()) {
                        ItemStack remainder = itemStackTransaction.getRemainder();
                        if (remainder == null || remainder.isEmpty()) continue;
                        remainderItems.add(remainder);
                    }
                    if (success == inputMaterials.size()) {
                        this.setBlockInteractionState(PROCESS_COMPLETED, blockType);
                        this.playSound(world, this.bench.getCompletedSoundEventIndex(), entityStore);
                        if (!remainderItems.isEmpty()) {
                            LOGGER.at(Level.WARNING).log("Dropping excess items at %s", this.getBlockPosition());
                            Holder<EntityStore>[] holderArray = this.ejectItems(entityStore, remainderItems);
                            entityStore.addEntities(holderArray, AddReason.SPAWN);
                        }
                        return;
                    }
                }
                ObjectArrayList<ItemStack> remainderItems = new ObjectArrayList<ItemStack>();
                ListTransaction<MaterialTransaction> transaction = this.inputContainer.removeMaterials(inputMaterials, true, true, true);
                if (!transaction.succeeded()) {
                    LOGGER.at(Level.WARNING).log("Failed to remove input materials at %s", this.getBlockPosition());
                    this.setBlockInteractionState("default", blockType);
                    this.playSound(world, this.processingBench.getFailedSoundEventIndex(), entityStore);
                    return;
                }
                this.setBlockInteractionState(PROCESS_COMPLETED, blockType);
                this.playSound(world, this.bench.getCompletedSoundEventIndex(), entityStore);
                ListTransaction<ItemStackTransaction> addTransaction = this.outputContainer.addItemStacks(outputItemStacks, false, false, false);
                if (addTransaction.succeeded()) {
                    return;
                }
                LOGGER.at(Level.WARNING).log("Dropping excess items at %s", this.getBlockPosition());
                for (ItemStackTransaction itemStackTransaction : addTransaction.getList()) {
                    ItemStack remainder = itemStackTransaction.getRemainder();
                    if (remainder == null || remainder.isEmpty()) continue;
                    remainderItems.add(remainder);
                }
                Holder<EntityStore>[] itemEntityHolders = this.ejectItems(entityStore, remainderItems);
                entityStore.addEntities(itemEntityHolders, AddReason.SPAWN);
            } else if (this.recipe != null && recipeTime > 0.0f) {
                float progressPercent = this.inputProgress / recipeTime;
                this.sendProgress(progressPercent);
            } else {
                this.sendProgress(0.0f);
            }
        }
    }

    private float getCraftingTimeReductionModifier() {
        BenchTierLevel levelData = this.bench.getTierLevel(this.getTierLevel());
        return levelData != null ? levelData.getCraftingTimeReductionModifier() : 0.0f;
    }

    @Nonnull
    private Holder<EntityStore>[] ejectItems(@Nonnull ComponentAccessor<EntityStore> accessor, @Nonnull List<ItemStack> itemStacks) {
        Vector3d dropPosition;
        if (itemStacks.isEmpty()) {
            return Holder.emptyArray();
        }
        RotationTuple rotation = RotationTuple.get(this.getRotationIndex());
        Vector3d frontDir = new Vector3d(0.0, 0.0, 1.0);
        rotation.yaw().rotateY(frontDir, frontDir);
        BlockType blockType = this.getBlockType();
        if (blockType == null) {
            dropPosition = this.getBlockPosition().toVector3d().add(0.5, 0.0, 0.5);
        } else {
            BlockBoundingBoxes hitboxAsset = BlockBoundingBoxes.getAssetMap().getAsset(blockType.getHitboxTypeIndex());
            if (hitboxAsset == null) {
                dropPosition = this.getBlockPosition().toVector3d().add(0.5, 0.0, 0.5);
            } else {
                double depth = hitboxAsset.get(0).getBoundingBox().depth();
                double frontOffset = depth / 2.0 + (double)0.1f;
                dropPosition = this.getCenteredBlockPosition();
                dropPosition.add(frontDir.x * frontOffset, 0.0, frontDir.z * frontOffset);
            }
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        ObjectArrayList<Holder<EntityStore>> result = new ObjectArrayList<Holder<EntityStore>>(itemStacks.size());
        for (ItemStack item : itemStacks) {
            float velocityZ;
            float velocityX;
            Holder<EntityStore> holder = ItemComponent.generateItemDrop(accessor, item, dropPosition, Vector3f.ZERO, velocityX = (float)(frontDir.x * 2.0 + 2.0 * (random.nextDouble() - 0.5)), 3.25f, velocityZ = (float)(frontDir.z * 2.0 + 2.0 * (random.nextDouble() - 0.5)));
            if (holder == null) continue;
            result.add(holder);
        }
        return (Holder[])result.toArray(Holder[]::new);
    }

    private void sendProgress(float progress) {
        this.windows.forEach((uuid, window) -> ((ProcessingBenchWindow)window).setProgress(progress));
    }

    private void sendProcessingSlots() {
        this.windows.forEach((uuid, window) -> ((ProcessingBenchWindow)window).setProcessingSlots(this.processingSlots));
    }

    private void sendProcessingFuelSlots() {
        this.windows.forEach((uuid, window) -> ((ProcessingBenchWindow)window).setProcessingFuelSlots(this.processingFuelSlots));
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean setActive(boolean active) {
        if (this.active != active) {
            if (active && this.processingBench.getFuel() != null && this.fuelContainer.isEmpty()) {
                return false;
            }
            this.active = active;
            if (!active) {
                this.processingSlots.clear();
                this.processingFuelSlots.clear();
                this.sendProcessingSlots();
                this.sendProcessingFuelSlots();
            }
            this.updateRecipe();
            this.windows.forEach((uuid, window) -> ((ProcessingBenchWindow)window).setActive(active));
            this.markNeedsSave();
            return true;
        }
        return false;
    }

    public void updateFuelValues() {
        if (this.fuelTime > (float)this.lastConsumedFuelTotal) {
            this.lastConsumedFuelTotal = MathUtil.ceil(this.fuelTime);
        }
        float fuelPercent = this.lastConsumedFuelTotal > 0 ? this.fuelTime / (float)this.lastConsumedFuelTotal : 0.0f;
        this.windows.forEach((uuid, window) -> {
            ProcessingBenchWindow processingBenchWindow = (ProcessingBenchWindow)window;
            processingBenchWindow.setFuelTime(fuelPercent);
            processingBenchWindow.setMaxFuel(this.lastConsumedFuelTotal);
            processingBenchWindow.setProcessingFuelSlots(this.processingFuelSlots);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.combinedItemContainer != null) {
            List<ItemStack> itemStacks = this.combinedItemContainer.dropAllItemStacks();
            this.dropFuelItems(itemStacks);
            World world = this.getChunk().getWorld();
            Store<EntityStore> entityStore = world.getEntityStore().getStore();
            Vector3d dropPosition = this.getBlockPosition().toVector3d().add(0.5, 0.0, 0.5);
            Holder[] itemEntityHolders = ItemComponent.generateItemDrops(entityStore, itemStacks, dropPosition, Vector3f.ZERO);
            if (itemEntityHolders.length > 0) {
                world.execute(() -> entityStore.addEntities(itemEntityHolders, AddReason.SPAWN));
            }
        }
        if (this.marker != null) {
            this.marker.remove();
        }
    }

    @Override
    public CombinedItemContainer getItemContainer() {
        return this.combinedItemContainer;
    }

    private void checkForRecipeUpdate() {
        if (this.recipe == null && this.recipeId != null) {
            this.updateRecipe();
        }
    }

    private void updateRecipe() {
        List<CraftingRecipe> recipes = CraftingPlugin.getBenchRecipes(this.bench.getType(), this.bench.getId());
        if (recipes.isEmpty()) {
            this.clearRecipe();
            return;
        }
        ObjectArrayList matching = new ObjectArrayList();
        for (CraftingRecipe recipe : recipes) {
            if (recipe.isRestrictedByBenchTierLevel(this.bench.getId(), this.getTierLevel())) continue;
            MaterialQuantity[] input = recipe.getInput();
            int matches = 0;
            IntArrayList slots = new IntArrayList();
            for (int j = 0; j < this.inputContainer.getCapacity(); ++j) {
                slots.add(j);
            }
            block2: for (MaterialQuantity craftingMaterial : input) {
                String itemId = craftingMaterial.getItemId();
                String resourceTypeId = craftingMaterial.getResourceTypeId();
                int materialQuantity = craftingMaterial.getQuantity();
                BsonDocument metadata = craftingMaterial.getMetadata();
                MaterialQuantity material = new MaterialQuantity(itemId, resourceTypeId, null, materialQuantity, metadata);
                for (int k = 0; k < slots.size(); ++k) {
                    int j = slots.getInt(k);
                    int out = InternalContainerUtilMaterial.testRemoveMaterialFromSlot(this.inputContainer, (short)j, material, material.getQuantity(), true);
                    if (out != 0) continue;
                    ++matches;
                    slots.removeInt(k);
                    continue block2;
                }
            }
            if (matches != input.length) continue;
            matching.add(recipe);
        }
        if (matching.isEmpty()) {
            this.clearRecipe();
            return;
        }
        matching.sort(Comparator.comparingInt(o -> CraftingManager.getInputMaterials(o).size()));
        Collections.reverse(matching);
        if (this.recipeId != null) {
            for (CraftingRecipe rec : matching) {
                if (!Objects.equals(this.recipeId, rec.getId())) continue;
                LOGGER.at(Level.FINE).log("%s - Keeping existing Recipe %s %s", LazyArgs.lazy(this::getBlockPosition), this.recipeId, rec);
                this.recipe = rec;
                return;
            }
        }
        CraftingRecipe recipe = (CraftingRecipe)matching.getFirst();
        if (this.recipeId == null || !Objects.equals(this.recipeId, recipe.getId())) {
            this.inputProgress = 0.0f;
            this.sendProgress(0.0f);
        }
        this.recipeId = recipe.getId();
        this.recipe = recipe;
        LOGGER.at(Level.FINE).log("%s - Found Recipe %s %s", LazyArgs.lazy(this::getBlockPosition), this.recipeId, this.recipe);
    }

    private void clearRecipe() {
        this.recipeId = null;
        this.recipe = null;
        this.lastConsumedFuelTotal = 0;
        this.inputProgress = 0.0f;
        this.sendProgress(0.0f);
        LOGGER.at(Level.FINE).log("%s - Cleared Recipe", LazyArgs.lazy(this::getBlockPosition));
    }

    public void dropFuelItems(@Nonnull List<ItemStack> itemStacks) {
        String fuelDropItemId = this.processingBench.getFuelDropItemId();
        if (fuelDropItemId != null) {
            int quantity;
            Item item = Item.getAssetMap().getAsset(fuelDropItemId);
            this.fuelTime = 0.0f;
            for (int dropAmount = (int)this.fuelTime; dropAmount > 0; dropAmount -= quantity) {
                quantity = Math.min(dropAmount, item.getMaxStack());
                itemStacks.add(new ItemStack(fuelDropItemId, quantity));
            }
        } else {
            LOGGER.at(Level.WARNING).log("No FuelDropItemId defined for %s fuel value of %s will be lost!", (Object)this.bench.getId(), this.fuelTime);
        }
    }

    @Nullable
    public CraftingRecipe getRecipe() {
        return this.recipe;
    }

    public float getInputProgress() {
        return this.inputProgress;
    }

    public void onItemChange(ItemContainer.ItemContainerChangeEvent event) {
        this.markNeedsSave();
    }

    public void setBlockInteractionState(@Nonnull String state, @Nonnull BlockType blockType) {
        this.getChunk().setBlockInteractionState(this.getBlockPosition(), blockType, state);
    }

    @Override
    public void setMarker(WorldMapManager.MarkerReference marker) {
        this.marker = marker;
        this.markNeedsSave();
    }

    @Override
    public void placedBy(@Nonnull Ref<EntityStore> playerRef, @Nonnull String blockTypeKey, @Nonnull BlockState blockState, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        if (blockTypeKey.equals(this.processingBench.getIconItem()) && this.processingBench.getIcon() != null) {
            Player playerComponent = componentAccessor.getComponent(playerRef, Player.getComponentType());
            assert (playerComponent != null);
            TransformComponent transformComponent = componentAccessor.getComponent(playerRef, TransformComponent.getComponentType());
            assert (transformComponent != null);
            Transform transformPacket = PositionUtil.toTransformPacket(transformComponent.getTransform());
            transformPacket.orientation.yaw = 0.0f;
            transformPacket.orientation.pitch = 0.0f;
            transformPacket.orientation.roll = 0.0f;
            MapMarker marker = new MapMarker(this.processingBench.getIconId() + "-" + String.valueOf(UUID.randomUUID()), this.processingBench.getIconName(), this.processingBench.getIcon(), transformPacket, null);
            ((MarkerBlockState)((Object)blockState)).setMarker(WorldMapManager.createPlayerMarker(playerRef, marker, componentAccessor));
        }
    }

    private void playSound(@Nonnull World world, int soundEventIndex, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        if (soundEventIndex == 0) {
            return;
        }
        Vector3i pos = this.getBlockPosition();
        SoundUtil.playSoundEvent3d(soundEventIndex, SoundCategory.SFX, (double)pos.x + 0.5, (double)pos.y + 0.5, (double)pos.z + 0.5, componentAccessor);
    }

    @Override
    protected void onTierLevelChange() {
        super.onTierLevelChange();
        this.setupSlots();
    }
}

