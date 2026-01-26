/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder.validators.asset;

import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemDropList;
import com.hypixel.hytale.server.npc.asset.builder.validators.AssetValidator;
import com.hypixel.hytale.server.npc.util.InventoryHelper;
import java.util.EnumSet;
import javax.annotation.Nonnull;

public class ItemExistsValidator
extends AssetValidator {
    private static final ItemExistsValidator DEFAULT_INSTANCE = new ItemExistsValidator();
    public static final String DROPLIST_PREFIX = "Droplist:";
    private boolean requireBlock;
    private boolean allowDroplist;

    private ItemExistsValidator() {
    }

    private ItemExistsValidator(boolean requireBlock, boolean allowDroplist) {
        this.requireBlock = requireBlock;
        this.allowDroplist = allowDroplist;
    }

    private ItemExistsValidator(EnumSet<AssetValidator.Config> config, boolean requireBlock, boolean allowDroplist) {
        super(config);
        this.requireBlock = requireBlock;
        this.allowDroplist = allowDroplist;
    }

    @Override
    @Nonnull
    public String getDomain() {
        return "Item";
    }

    @Override
    public boolean test(String item) {
        if (item == null || item.isEmpty()) {
            return false;
        }
        if (item.startsWith(DROPLIST_PREFIX)) {
            return ItemDropList.getAssetMap().getAsset(item.substring(DROPLIST_PREFIX.length())) != null;
        }
        return this.requireBlock ? InventoryHelper.itemKeyIsBlockType(item) : InventoryHelper.itemKeyExists(item);
    }

    @Override
    @Nonnull
    public String errorMessage(String item, String attributeName) {
        return "The item " + (this.allowDroplist ? "or droplist " : "") + "with the name \"" + item + "\" does not exist" + (this.requireBlock ? " or is not a block" : "") + " for attribute \"" + attributeName + "\"";
    }

    @Override
    @Nonnull
    public String getAssetName() {
        return Item.class.getSimpleName();
    }

    public static ItemExistsValidator required() {
        return DEFAULT_INSTANCE;
    }

    @Nonnull
    public static ItemExistsValidator requireBlock() {
        return new ItemExistsValidator(true, false);
    }

    @Nonnull
    public static ItemExistsValidator orDroplist() {
        return new ItemExistsValidator(false, true);
    }

    @Nonnull
    public static ItemExistsValidator withConfig(EnumSet<AssetValidator.Config> config) {
        return new ItemExistsValidator(config, false, false);
    }

    @Nonnull
    public static ItemExistsValidator orDroplistWithConfig(EnumSet<AssetValidator.Config> config) {
        return new ItemExistsValidator(config, false, true);
    }
}

