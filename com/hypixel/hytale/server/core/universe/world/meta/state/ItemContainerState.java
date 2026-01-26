/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.meta.state;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.StateData;
import com.hypixel.hytale.server.core.entity.entities.player.windows.ContainerBlockWindow;
import com.hypixel.hytale.server.core.entity.entities.player.windows.WindowManager;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.universe.world.meta.state.DestroyableBlockState;
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerBlockState;
import com.hypixel.hytale.server.core.universe.world.meta.state.MarkerBlockState;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemContainerState
extends BlockState
implements ItemContainerBlockState,
DestroyableBlockState,
MarkerBlockState {
    public static final Codec<ItemContainerState> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ItemContainerState.class, ItemContainerState::new, BlockState.BASE_CODEC).addField(new KeyedCodec<Boolean>("Custom", Codec.BOOLEAN), (state, o) -> {
        state.custom = o;
    }, state -> state.custom)).addField(new KeyedCodec<Boolean>("AllowViewing", Codec.BOOLEAN), (state, o) -> {
        state.allowViewing = o;
    }, state -> state.allowViewing)).addField(new KeyedCodec<String>("Droplist", Codec.STRING), (state, o) -> {
        state.droplist = o;
    }, state -> state.droplist)).addField(new KeyedCodec<WorldMapManager.MarkerReference>("Marker", WorldMapManager.MarkerReference.CODEC), (state, o) -> {
        state.marker = o;
    }, state -> state.marker)).addField(new KeyedCodec<SimpleItemContainer>("ItemContainer", SimpleItemContainer.CODEC), (state, o) -> {
        state.itemContainer = o;
    }, state -> state.itemContainer)).build();
    private final Map<UUID, ContainerBlockWindow> windows = new ConcurrentHashMap<UUID, ContainerBlockWindow>();
    protected boolean custom;
    protected boolean allowViewing = true;
    @Nullable
    protected String droplist;
    protected SimpleItemContainer itemContainer;
    protected WorldMapManager.MarkerReference marker;

    @Override
    public boolean initialize(@Nonnull BlockType blockType) {
        if (!super.initialize(blockType)) {
            return false;
        }
        if (this.custom) {
            return true;
        }
        short capacity = 20;
        StateData stateData = blockType.getState();
        if (stateData instanceof ItemContainerStateData) {
            ItemContainerStateData itemContainerStateData = (ItemContainerStateData)stateData;
            capacity = itemContainerStateData.getCapacity();
        }
        ObjectArrayList<ItemStack> remainder = new ObjectArrayList<ItemStack>();
        this.itemContainer = ItemContainer.ensureContainerCapacity(this.itemContainer, capacity, SimpleItemContainer::new, remainder);
        this.itemContainer.registerChangeEvent(EventPriority.LAST, this::onItemChange);
        if (!remainder.isEmpty()) {
            WorldChunk chunk = this.getChunk();
            World world = chunk.getWorld();
            Store<EntityStore> store = world.getEntityStore().getStore();
            ((HytaleLogger.Api)HytaleLogger.getLogger().at(Level.WARNING).withCause(new Throwable())).log("Dropping %d excess items from item container: %s at world: %s, chunk: %s, block: %s", remainder.size(), blockType.getId(), chunk.getWorld().getName(), chunk, this.getPosition());
            Vector3i blockPosition = this.getBlockPosition();
            Holder<EntityStore>[] itemEntityHolders = ItemComponent.generateItemDrops(store, remainder, blockPosition.toVector3d(), Vector3f.ZERO);
            store.addEntities(itemEntityHolders, AddReason.SPAWN);
        }
        return true;
    }

    public boolean canOpen(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        return true;
    }

    public void onOpen(@Nonnull Ref<EntityStore> ref, @Nonnull World world, @Nonnull Store<EntityStore> store) {
    }

    @Override
    public void onDestroy() {
        WindowManager.closeAndRemoveAll(this.windows);
        WorldChunk chunk = this.getChunk();
        World world = chunk.getWorld();
        Store<EntityStore> store = world.getEntityStore().getStore();
        List<ItemStack> allItemStacks = this.itemContainer.dropAllItemStacks();
        Vector3d dropPosition = this.getBlockPosition().toVector3d().add(0.5, 0.0, 0.5);
        Holder[] itemEntityHolders = ItemComponent.generateItemDrops(store, allItemStacks, dropPosition, Vector3f.ZERO);
        if (itemEntityHolders.length > 0) {
            world.execute(() -> store.addEntities(itemEntityHolders, AddReason.SPAWN));
        }
        if (this.marker != null) {
            this.marker.remove();
        }
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
        this.markNeedsSave();
    }

    public void setAllowViewing(boolean allowViewing) {
        this.allowViewing = allowViewing;
        this.markNeedsSave();
    }

    public boolean isAllowViewing() {
        return this.allowViewing;
    }

    public void setItemContainer(SimpleItemContainer itemContainer) {
        this.itemContainer = itemContainer;
        this.markNeedsSave();
    }

    @Nullable
    public String getDroplist() {
        return this.droplist;
    }

    public void setDroplist(@Nullable String droplist) {
        this.droplist = droplist;
        this.markNeedsSave();
    }

    @Override
    public void setMarker(WorldMapManager.MarkerReference marker) {
        this.marker = marker;
        this.markNeedsSave();
    }

    @Nonnull
    public Map<UUID, ContainerBlockWindow> getWindows() {
        return this.windows;
    }

    @Override
    public ItemContainer getItemContainer() {
        return this.itemContainer;
    }

    public void onItemChange(ItemContainer.ItemContainerChangeEvent event) {
        this.markNeedsSave();
    }

    public static class ItemContainerStateData
    extends StateData {
        public static final BuilderCodec<ItemContainerStateData> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(ItemContainerStateData.class, ItemContainerStateData::new, StateData.DEFAULT_CODEC).appendInherited(new KeyedCodec<Integer>("Capacity", Codec.INTEGER), (t, i) -> {
            t.capacity = i.shortValue();
        }, t -> t.capacity, (o, p) -> {
            o.capacity = p.capacity;
        }).add()).build();
        private short capacity = (short)20;

        protected ItemContainerStateData() {
        }

        public short getCapacity() {
            return this.capacity;
        }

        @Override
        @Nonnull
        public String toString() {
            return "ItemContainerStateData{capacity=" + this.capacity + "} " + super.toString();
        }
    }
}

