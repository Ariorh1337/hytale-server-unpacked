/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.item;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.lookup.Priority;
import com.hypixel.hytale.common.plugin.PluginManifest;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemCategory;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemDrop;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemDropList;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.EmptyItemContainer;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.modules.item.commands.SpawnItemCommand;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemModule
extends JavaPlugin {
    public static final PluginManifest MANIFEST = PluginManifest.corePlugin(ItemModule.class).build();
    private static ItemModule instance;

    public static ItemModule get() {
        return instance;
    }

    public ItemModule(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    @Override
    protected void setup() {
        this.getCommandRegistry().registerCommand(new SpawnItemCommand());
        ItemContainer.CODEC.register(Priority.DEFAULT, "Simple", (Class)SimpleItemContainer.class, (Codec)SimpleItemContainer.CODEC);
        ItemContainer.CODEC.register("Empty", (Class<ItemContainer>)EmptyItemContainer.class, (Codec<ItemContainer>)EmptyItemContainer.CODEC);
    }

    @Nonnull
    public List<String> getFlatItemCategoryList() {
        ItemCategory[] itemCategories;
        ObjectArrayList<String> ids = new ObjectArrayList<String>();
        for (ItemCategory category : itemCategories = (ItemCategory[])ItemCategory.getAssetMap().getAssetMap().values().toArray(ItemCategory[]::new)) {
            ItemCategory[] children = category.getChildren();
            if (children == null) continue;
            this.flattenCategories(category.getId() + ".", children, ids);
        }
        return ids;
    }

    private void flattenCategories(String parent, @Nonnull ItemCategory[] itemCategories, @Nonnull List<String> categoryIds) {
        for (ItemCategory category : itemCategories) {
            String id = parent + category.getId();
            categoryIds.add(id);
            ItemCategory[] children = category.getChildren();
            if (children == null) continue;
            this.flattenCategories(id + ".", children, categoryIds);
        }
    }

    @Nonnull
    public List<ItemStack> getRandomItemDrops(@Nullable String dropListId) {
        if (this.isDisabled()) {
            return Collections.emptyList();
        }
        if (dropListId == null) {
            return Collections.emptyList();
        }
        ItemDropList itemDropList = ItemDropList.getAssetMap().getAsset(dropListId);
        if (itemDropList == null || itemDropList.getContainer() == null) {
            return Collections.emptyList();
        }
        ObjectArrayList<ItemStack> generatedItemDrops = new ObjectArrayList<ItemStack>();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        ObjectArrayList<ItemDrop> configuredItemDrops = new ObjectArrayList<ItemDrop>();
        itemDropList.getContainer().populateDrops(configuredItemDrops, random::nextDouble, dropListId);
        for (ItemDrop drop : configuredItemDrops) {
            if (drop == null || drop.getItemId() == null) {
                ((HytaleLogger.Api)this.getLogger().atWarning()).log("ItemModule::getRandomItemDrops - Tried to create ItemDrop for non-existent item in drop list id '%s'", dropListId);
                continue;
            }
            int amount = drop.getRandomQuantity(random);
            if (amount <= 0) continue;
            generatedItemDrops.add(new ItemStack(drop.getItemId(), amount, drop.getMetadata()));
        }
        return generatedItemDrops;
    }

    public static boolean exists(String key) {
        if ("Empty".equals(key)) {
            return true;
        }
        if ("Unknown".equals(key)) {
            return true;
        }
        return Item.getAssetMap().getAsset(key) != null;
    }
}

