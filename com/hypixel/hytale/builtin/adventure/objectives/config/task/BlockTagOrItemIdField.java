/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.objectives.config.task;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockTagOrItemIdField {
    public static final BuilderCodec<BlockTagOrItemIdField> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(BlockTagOrItemIdField.class, BlockTagOrItemIdField::new).append(new KeyedCodec<String>("BlockTag", Codec.STRING), (blockTagOrItemIdField, s) -> {
        blockTagOrItemIdField.blockTag = s;
    }, blockTagOrItemIdField -> blockTagOrItemIdField.blockTag).add()).append(new KeyedCodec<String>("ItemId", Codec.STRING), (blockTagOrItemIdField, blockTypeKey) -> {
        blockTagOrItemIdField.itemId = blockTypeKey;
    }, blockTagOrItemIdField -> blockTagOrItemIdField.itemId).addValidator(Item.VALIDATOR_CACHE.getValidator()).add()).validator((task, validationResults) -> {
        if (task.blockTag == null && task.itemId == null) {
            validationResults.fail("One and only one of BlockTag or ItemId must be set!");
        }
    })).afterDecode(blockTagOrItemIdField -> {
        if (blockTagOrItemIdField.blockTag != null) {
            blockTagOrItemIdField.blockTagIndex = AssetRegistry.getOrCreateTagIndex(blockTagOrItemIdField.blockTag);
        }
    })).build();
    protected String blockTag;
    protected int blockTagIndex = Integer.MIN_VALUE;
    protected String itemId;

    public BlockTagOrItemIdField(String blockTag, String itemId) {
        this.blockTag = blockTag;
        this.itemId = itemId;
    }

    protected BlockTagOrItemIdField() {
    }

    public int getBlockTagIndex() {
        return this.blockTagIndex;
    }

    public String getItemId() {
        return this.itemId;
    }

    public boolean isBlockTypeIncluded(String blockTypeToCheck) {
        if (this.blockTagIndex != Integer.MIN_VALUE) {
            return Item.getAssetMap().getKeysForTag(this.blockTagIndex).contains(blockTypeToCheck);
        }
        if (this.itemId != null) {
            return this.itemId.equals(blockTypeToCheck);
        }
        return false;
    }

    public void consumeItemStacks(@Nonnull ItemContainer container, int quantity) {
        if (this.itemId != null) {
            container.removeItemStack(new ItemStack(this.itemId, quantity));
        } else if (this.blockTagIndex != Integer.MIN_VALUE) {
            container.removeTag(this.blockTagIndex, quantity);
        }
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BlockTagOrItemIdField that = (BlockTagOrItemIdField)o;
        if (this.blockTag != null ? !this.blockTag.equals(that.blockTag) : that.blockTag != null) {
            return false;
        }
        return this.itemId != null ? this.itemId.equals(that.itemId) : that.itemId == null;
    }

    public int hashCode() {
        int result = this.blockTag != null ? this.blockTag.hashCode() : 0;
        result = 31 * result + (this.itemId != null ? this.itemId.hashCode() : 0);
        return result;
    }

    @Nonnull
    public String toString() {
        return "BlockTagOrItemIdField{blockTag='" + this.blockTag + "', itemId=" + this.itemId + "}";
    }
}

