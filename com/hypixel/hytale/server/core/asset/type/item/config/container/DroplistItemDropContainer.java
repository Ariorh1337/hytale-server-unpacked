/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.item.config.container;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemDrop;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemDropList;
import com.hypixel.hytale.server.core.asset.type.item.config.container.ItemDropContainer;
import java.util.List;
import java.util.Set;
import java.util.function.DoubleSupplier;

public class DroplistItemDropContainer
extends ItemDropContainer {
    public static final BuilderCodec<DroplistItemDropContainer> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(DroplistItemDropContainer.class, DroplistItemDropContainer::new, ItemDropContainer.DEFAULT_CODEC).append(new KeyedCodec<String>("DroplistId", Codec.STRING), (droplistItemDropContainer, s) -> {
        droplistItemDropContainer.droplistId = s;
    }, droplistItemDropContainer -> droplistItemDropContainer.droplistId).addValidator(Validators.nonNull()).addValidatorLate(() -> ItemDropList.VALIDATOR_CACHE.getValidator().late()).add()).build();
    String droplistId;

    @Override
    protected void populateDrops(List<ItemDrop> drops, DoubleSupplier chanceProvider, Set<String> droplistReferences) {
        if (!droplistReferences.add(this.droplistId)) {
            return;
        }
        ItemDropList droplist = ItemDropList.getAssetMap().getAsset(this.droplistId);
        if (droplist == null) {
            return;
        }
        droplist.getContainer().populateDrops(drops, chanceProvider, droplistReferences);
    }

    @Override
    public List<ItemDrop> getAllDrops(List<ItemDrop> list) {
        ItemDropList droplist = ItemDropList.getAssetMap().getAsset(this.droplistId);
        if (droplist == null) {
            return list;
        }
        droplist.getContainer().getAllDrops(list);
        return list;
    }

    public String toString() {
        return "DroplistItemDropContainer{droplistId='" + this.droplistId + "', weight=" + this.weight + "}";
    }
}

