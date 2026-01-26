/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.datastructures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public class TieredList<E> {
    @Nonnull
    private final Map<Integer, ArrayList<E>> elements;
    private final int tiers;
    private List<Integer> sortedTierList;

    public TieredList() {
        this(0);
    }

    public TieredList(int tiers) {
        if (tiers < 0) {
            throw new IllegalArgumentException("negative number of tiers");
        }
        this.tiers = tiers;
        this.elements = new HashMap<Integer, ArrayList<E>>();
        for (int tier = 0; tier < tiers; ++tier) {
            this.elements.put(tier, new ArrayList());
        }
        this.updateSortedTierList();
    }

    @Nonnull
    public TieredList<E> addTier(int tier) {
        if (this.tierExists(tier)) {
            throw new IllegalArgumentException("tier already exists " + tier);
        }
        this.elements.put(tier, new ArrayList());
        this.updateSortedTierList();
        return this;
    }

    @Nonnull
    public TieredList<E> removeTier(int tier) {
        if (!this.tierExists(tier)) {
            return this;
        }
        this.elements.remove(tier);
        this.updateSortedTierList();
        return this;
    }

    public void add(@Nonnull E element, int tier) {
        if (element == null) {
            throw new NullPointerException();
        }
        if (!this.tierExists(tier)) {
            this.addTier(tier);
        }
        this.elements.get(tier).add(element);
    }

    public boolean isEmpty() {
        for (List list : this.elements.values()) {
            if (list.isEmpty()) continue;
            return false;
        }
        return true;
    }

    public E peek() {
        for (int tier = 0; tier < this.tiers; ++tier) {
            List tierElements = this.elements.get(tier);
            if (tierElements.isEmpty()) continue;
            return tierElements.getFirst();
        }
        throw new IllegalStateException("queue is empty");
    }

    public E remove() {
        for (int tier = 0; tier < this.tiers; ++tier) {
            List tierElements = this.elements.get(tier);
            if (tierElements.isEmpty()) continue;
            return tierElements.removeFirst();
        }
        throw new IllegalStateException("queue is empty");
    }

    public int size() {
        int size = 0;
        for (List list : this.elements.values()) {
            size += list.size();
        }
        return size;
    }

    public int size(int tier) {
        if (!this.tierExists(tier)) {
            return 0;
        }
        return this.elements.get(tier).size();
    }

    @Nonnull
    public TieredList<E> forEach(int tier, @Nonnull Consumer<? super E> consumer) {
        if (!this.tierExists(tier)) {
            return this;
        }
        this.elements.get(tier).forEach(consumer);
        return this;
    }

    @Nonnull
    public TieredList<E> removeEach(int tier, @Nonnull Consumer<? super E> consumer) {
        if (!this.tierExists(tier)) {
            return this;
        }
        ArrayList tierList = this.elements.get(tier);
        for (Object e : tierList) {
            consumer.accept(e);
        }
        tierList = new ArrayList();
        return this;
    }

    @Nonnull
    public TieredList<E> forEach(@Nonnull Consumer<? super E> consumer) {
        ArrayList<Integer> tiers = new ArrayList<Integer>(this.getTiers());
        tiers.sort(Comparator.naturalOrder());
        for (int tier : tiers) {
            this.forEach(tier, consumer);
        }
        return this;
    }

    @Nonnull
    public TieredList<E> removeEach(@Nonnull Consumer<? super E> consumer) {
        for (int tier : this.getTiers()) {
            this.removeEach(tier, consumer);
        }
        return this;
    }

    @Nonnull
    public Iterator<E> iterator(int tier) {
        if (!this.tierExists(tier)) {
            throw new IllegalArgumentException("tier doesn't exist");
        }
        return this.elements.get(tier).iterator();
    }

    @Nonnull
    public List<E> listOf(int tier) {
        if (!this.tierExists(tier)) {
            throw new IllegalArgumentException("tier doesn't exist");
        }
        return Collections.unmodifiableList((List)this.elements.get(tier));
    }

    public boolean tierExists(int tier) {
        return this.elements.containsKey(tier);
    }

    public List<Integer> getTiers() {
        return this.sortedTierList;
    }

    private void updateSortedTierList() {
        List<Integer> tierList = new ArrayList<Integer>(this.elements.keySet());
        tierList.sort(Comparator.naturalOrder());
        tierList = Collections.unmodifiableList(tierList);
        this.sortedTierList = tierList;
    }

    @Nonnull
    public String toString() {
        return "TieredList{elements=" + String.valueOf(this.elements) + ", tiers=" + this.tiers + ", sortedTierList=" + String.valueOf(this.sortedTierList) + "}";
    }
}

