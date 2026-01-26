/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.util.condition;

import com.hypixel.hytale.procedurallib.condition.IIntCondition;
import com.hypixel.hytale.server.worldgen.util.condition.HashSetIntCondition;
import it.unimi.dsi.fastutil.ints.IntSets;
import javax.annotation.Nonnull;

public class FilteredIntCondition
implements IIntCondition {
    private final IIntCondition filter;
    private final IIntCondition condition;

    public FilteredIntCondition(int filterValue, IIntCondition condition) {
        this(new HashSetIntCondition(IntSets.singleton(filterValue)), condition);
    }

    public FilteredIntCondition(IIntCondition filter, IIntCondition condition) {
        this.filter = filter;
        this.condition = condition;
    }

    @Override
    public boolean eval(int value) {
        if (this.filter.eval(value)) {
            return false;
        }
        return this.condition.eval(value);
    }

    @Nonnull
    public String toString() {
        return "FilteredIntCondition{filter=" + String.valueOf(this.filter) + ", condition=" + String.valueOf(this.condition) + "}";
    }
}

