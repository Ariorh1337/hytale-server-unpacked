/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.blocktype.config.bench;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.Bench;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collections;
import javax.annotation.Nonnull;

public class StructuralCraftingBench
extends Bench {
    public static final BuilderCodec<StructuralCraftingBench> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(StructuralCraftingBench.class, StructuralCraftingBench::new, Bench.BASE_CODEC).append(new KeyedCodec<T[]>("Categories", new ArrayCodec<String>(Codec.STRING, String[]::new)), (bench, categories) -> {
        bench.sortedCategories = categories;
    }, bench -> bench.sortedCategories).add()).append(new KeyedCodec<T[]>("HeaderCategories", new ArrayCodec<String>(Codec.STRING, String[]::new)), (bench, headerCategories) -> {
        bench.headerCategories = headerCategories;
    }, bench -> bench.headerCategories).add()).append(new KeyedCodec<Boolean>("AlwaysShowInventoryHints", Codec.BOOLEAN), (bench, alwaysShowInventoryHints) -> {
        bench.alwaysShowInventoryHints = alwaysShowInventoryHints;
    }, bench -> bench.alwaysShowInventoryHints).add()).append(new KeyedCodec<Boolean>("AllowBlockGroupCycling", Codec.BOOLEAN), (bench, allowBlockGroupCycling) -> {
        bench.allowBlockGroupCycling = allowBlockGroupCycling;
    }, bench -> bench.allowBlockGroupCycling).add()).afterDecode(StructuralCraftingBench::processConfig)).build();
    private String[] headerCategories;
    private ObjectOpenHashSet<String> headerCategoryMap;
    private String[] sortedCategories;
    private Object2IntMap<String> categoryToIndexMap;
    private boolean allowBlockGroupCycling;
    private boolean alwaysShowInventoryHints;

    private void processConfig() {
        if (this.headerCategories != null) {
            this.headerCategoryMap = new ObjectOpenHashSet();
            Collections.addAll(this.headerCategoryMap, this.headerCategories);
        }
        if (this.sortedCategories == null) {
            return;
        }
        this.categoryToIndexMap = new Object2IntOpenHashMap<String>();
        for (int i = 0; i < this.sortedCategories.length; ++i) {
            this.categoryToIndexMap.put(this.sortedCategories[i], i);
        }
    }

    public boolean isHeaderCategory(@Nonnull String category) {
        return this.headerCategoryMap != null && this.headerCategoryMap.contains(category);
    }

    public int getCategoryIndex(@Nonnull String category) {
        return this.categoryToIndexMap.getOrDefault((Object)category, Integer.MAX_VALUE);
    }

    public boolean shouldAllowBlockGroupCycling() {
        return this.allowBlockGroupCycling;
    }

    public boolean shouldAlwaysShowInventoryHints() {
        return this.alwaysShowInventoryHints;
    }

    @Override
    public String toString() {
        return "StructuralCraftingBench{}";
    }
}

