/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator;

import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import it.unimi.dsi.fastutil.ints.AbstractIntSet;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

public class MaterialSet
implements Predicate<Material> {
    private final boolean isInclusive;
    private final IntSet mask;

    public MaterialSet() {
        this.isInclusive = true;
        this.mask = IntSet.of();
    }

    public MaterialSet(boolean isInclusive, @Nonnull List<Material> elements) {
        this.isInclusive = isInclusive;
        int size = elements.size();
        if (size == 0) {
            this.mask = IntSet.of();
            return;
        }
        if (size == 1) {
            Material first = elements.getFirst();
            if (first == null) {
                throw new IllegalArgumentException("element array contains null at index 0");
            }
            this.mask = IntSet.of(first.hashMaterialIds());
            return;
        }
        AbstractIntSet innerSet = size <= 4 ? new IntArraySet(size) : new IntOpenHashSet(size, 0.99f);
        for (int i = 0; i < size; ++i) {
            Material element = elements.get(i);
            if (element == null) {
                throw new IllegalArgumentException("element array contains null at index " + i);
            }
            innerSet.add(element.hashMaterialIds());
        }
        this.mask = IntSets.unmodifiable(innerSet);
    }

    @Override
    public boolean test(Material value) {
        if (value == null) {
            return false;
        }
        boolean contains = this.mask.contains(value.hashMaterialIds());
        return contains && this.isInclusive || !contains && !this.isInclusive;
    }

    @Override
    public boolean test(int hashMaterialIds) {
        boolean contains = this.mask.contains(hashMaterialIds);
        return contains && this.isInclusive || !contains && !this.isInclusive;
    }
}

