/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.item.config.container;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.common.map.IWeightedElement;
import com.hypixel.hytale.common.map.IWeightedMap;
import com.hypixel.hytale.common.map.WeightedMap;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemDrop;
import com.hypixel.hytale.server.core.asset.type.item.config.container.ItemDropContainer;
import com.hypixel.hytale.server.core.codec.WeightedMapCodec;
import java.util.List;
import java.util.Set;
import java.util.function.DoubleSupplier;
import javax.annotation.Nonnull;

public class ChoiceItemDropContainer
extends ItemDropContainer {
    public static final BuilderCodec<ChoiceItemDropContainer> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ChoiceItemDropContainer.class, ChoiceItemDropContainer::new, ItemDropContainer.DEFAULT_CODEC).addField(new KeyedCodec("Containers", new WeightedMapCodec(ItemDropContainer.CODEC, (IWeightedElement[])ItemDropContainer.EMPTY_ARRAY)), (choiceItemDropContainer, o) -> {
        choiceItemDropContainer.containers = o;
    }, choiceItemDropContainer -> choiceItemDropContainer.containers)).addField(new KeyedCodec<Integer>("RollsMin", Codec.INTEGER), (choiceItemDropContainer, i) -> {
        choiceItemDropContainer.rollsMin = i;
    }, choiceItemDropContainer -> choiceItemDropContainer.rollsMin)).addField(new KeyedCodec<Integer>("RollsMax", Codec.INTEGER), (choiceItemDropContainer, i) -> {
        choiceItemDropContainer.rollsMax = i;
    }, choiceItemDropContainer -> choiceItemDropContainer.rollsMax)).build();
    protected IWeightedMap<ItemDropContainer> containers;
    protected int rollsMin = 1;
    protected int rollsMax = 1;

    public ChoiceItemDropContainer(ItemDropContainer[] containers, double chance) {
        super(chance);
        this.containers = WeightedMap.builder(ItemDropContainer.EMPTY_ARRAY).putAll((ItemDropContainer[])containers, ItemDropContainer::getWeight).build();
    }

    public ChoiceItemDropContainer() {
    }

    @Override
    protected void populateDrops(List<ItemDrop> drops, DoubleSupplier chanceProvider, Set<String> droplistReferences) {
        int count = this.rollsMin + (int)(chanceProvider.getAsDouble() * (double)(this.rollsMax - this.rollsMin + 1));
        for (int i = 0; i < count; ++i) {
            ItemDropContainer drop = this.containers.get(chanceProvider);
            drop.populateDrops(drops, chanceProvider, droplistReferences);
        }
    }

    @Override
    public List<ItemDrop> getAllDrops(List<ItemDrop> list) {
        for (ItemDropContainer container : this.containers.internalKeys()) {
            container.getAllDrops(list);
        }
        return list;
    }

    @Nonnull
    public String toString() {
        return "ChoiceItemDropContainer{weight=" + this.weight + ", containers=" + String.valueOf(this.containers) + "}";
    }
}

