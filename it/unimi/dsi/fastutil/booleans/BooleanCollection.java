/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.booleans.BooleanIterable;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanPredicate;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterators;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

public interface BooleanCollection
extends Collection<Boolean>,
BooleanIterable {
    @Override
    public BooleanIterator iterator();

    @Override
    default public BooleanSpliterator spliterator() {
        return BooleanSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 320);
    }

    @Override
    public boolean add(boolean var1);

    public boolean contains(boolean var1);

    public boolean rem(boolean var1);

    @Override
    @Deprecated
    default public boolean add(Boolean key) {
        return this.add((boolean)key);
    }

    @Override
    @Deprecated
    default public boolean contains(Object key) {
        if (key == null) {
            return false;
        }
        return this.contains((Boolean)key);
    }

    @Override
    @Deprecated
    default public boolean remove(Object key) {
        if (key == null) {
            return false;
        }
        return this.rem((Boolean)key);
    }

    public boolean[] toBooleanArray();

    @Deprecated
    default public boolean[] toBooleanArray(boolean[] a) {
        return this.toArray(a);
    }

    public boolean[] toArray(boolean[] var1);

    public boolean addAll(BooleanCollection var1);

    public boolean containsAll(BooleanCollection var1);

    public boolean removeAll(BooleanCollection var1);

    @Override
    @Deprecated
    default public boolean removeIf(Predicate<? super Boolean> filter) {
        return this.removeIf(filter instanceof BooleanPredicate ? (BooleanPredicate)filter : key -> filter.test(key));
    }

    default public boolean removeIf(BooleanPredicate filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        BooleanIterator each = this.iterator();
        while (each.hasNext()) {
            if (!filter.test(each.nextBoolean())) continue;
            each.remove();
            removed = true;
        }
        return removed;
    }

    public boolean retainAll(BooleanCollection var1);
}

