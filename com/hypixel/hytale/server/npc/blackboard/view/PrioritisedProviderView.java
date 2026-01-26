/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.blackboard.view;

import com.hypixel.hytale.server.npc.blackboard.view.IBlackboardView;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

public abstract class PrioritisedProviderView<T, ViewType extends IBlackboardView<ViewType>>
implements IBlackboardView<ViewType> {
    public static final int LOWEST_PRIORITY = Integer.MAX_VALUE;
    @Nonnull
    protected List<PrioritisedProvider<T>> providers = new ObjectArrayList<PrioritisedProvider<T>>();

    public void registerProvider(int priority, T provider) {
        this.providers.add(new PrioritisedProvider<T>(priority, provider));
        Collections.sort(this.providers);
    }

    public static class PrioritisedProvider<T>
    implements Comparable<PrioritisedProvider<T>> {
        private final int priority;
        private final T provider;

        public PrioritisedProvider(int priority, T provider) {
            this.priority = priority;
            this.provider = provider;
        }

        public T getProvider() {
            return this.provider;
        }

        @Override
        public int compareTo(@Nonnull PrioritisedProvider<T> other) {
            return Integer.compare(this.priority, other.priority);
        }
    }
}

