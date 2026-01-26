/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.stash;

import com.hypixel.hytale.builtin.adventure.stash.StashGameplayConfig;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.ListTransaction;
import com.hypixel.hytale.server.core.modules.item.ItemModule;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.meta.BlockStateModule;
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerState;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortLists;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StashPlugin
extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public StashPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        this.getChunkStoreRegistry().registerSystem(new StashSystem(BlockStateModule.get().getComponentType(ItemContainerState.class)));
        this.getCodecRegistry(GameplayConfig.PLUGIN_CODEC).register(StashGameplayConfig.class, "Stash", StashGameplayConfig.CODEC);
    }

    @Nullable
    public static ListTransaction<ItemStackTransaction> stash(@Nonnull ItemContainerState containerState, boolean clearDropList) {
        String droplist = containerState.getDroplist();
        if (droplist == null) {
            return null;
        }
        List<ItemStack> stacks = ItemModule.get().getRandomItemDrops(droplist);
        if (!stacks.isEmpty()) {
            ItemContainer itemContainer = containerState.getItemContainer();
            short capacity = itemContainer.getCapacity();
            ShortArrayList slots = new ShortArrayList(capacity);
            for (short s = 0; s < capacity; s = (short)(s + 1)) {
                slots.add(s);
            }
            Vector3i blockPosition = containerState.getBlockPosition();
            long positionHash = blockPosition.hashCode();
            Random rnd = new Random(positionHash);
            ShortLists.shuffle(slots, rnd);
            boolean anySucceeded = false;
            for (int idx = 0; idx < stacks.size() && idx < slots.size(); ++idx) {
                short slot = slots.getShort(idx);
                ItemStackSlotTransaction transaction = itemContainer.addItemStackToSlot(slot, stacks.get(idx));
                if (transaction.getRemainder() == null || transaction.getRemainder().isEmpty()) {
                    anySucceeded = true;
                    continue;
                }
                LOGGER.at(Level.WARNING).log("Could not add Item to Stash at %d, %d, %d: %s", blockPosition.x, blockPosition.y, blockPosition.z, transaction.getRemainder());
            }
            if (clearDropList && anySucceeded) {
                containerState.setDroplist(null);
            }
            return new ListTransaction<ItemStackTransaction>(anySucceeded, new ObjectArrayList());
        }
        return ListTransaction.getEmptyTransaction(true);
    }

    private static class StashSystem
    extends RefSystem<ChunkStore> {
        private final ComponentType<ChunkStore, ItemContainerState> componentType;
        @Nonnull
        private final Set<Dependency<ChunkStore>> dependencies;

        public StashSystem(ComponentType<ChunkStore, ItemContainerState> componentType) {
            this.componentType = componentType;
            this.dependencies = Set.of(new SystemDependency(Order.AFTER, BlockStateModule.LegacyBlockStateRefSystem.class));
        }

        @Override
        public Query<ChunkStore> getQuery() {
            return this.componentType;
        }

        @Override
        public void onEntityAdded(@Nonnull Ref<ChunkStore> ref, @Nonnull AddReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
            World world = store.getExternalData().getWorld();
            if (world.getWorldConfig().getGameMode() == GameMode.Creative) {
                return;
            }
            StashGameplayConfig stashGameplayConfig = StashGameplayConfig.getOrDefault(world.getGameplayConfig());
            boolean clearContainerDropList = stashGameplayConfig.isClearContainerDropList();
            StashPlugin.stash(store.getComponent(ref, this.componentType), clearContainerDropList);
        }

        @Override
        public void onEntityRemove(@Nonnull Ref<ChunkStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
        }

        @Override
        @Nonnull
        public Set<Dependency<ChunkStore>> getDependencies() {
            return this.dependencies;
        }
    }
}

