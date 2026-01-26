/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component.system;

import com.hypixel.hytale.component.ComponentRegistry;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.DependencyGraph;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ISystem<ECS_TYPE> {
    public static final ISystem[] EMPTY_ARRAY = new ISystem[0];

    default public void onSystemRegistered() {
    }

    default public void onSystemUnregistered() {
    }

    @Nullable
    default public SystemGroup<ECS_TYPE> getGroup() {
        return null;
    }

    @Nonnull
    default public Set<Dependency<ECS_TYPE>> getDependencies() {
        return Collections.emptySet();
    }

    public static <ECS_TYPE> void calculateOrder(@Nonnull ComponentRegistry<ECS_TYPE> registry, @Nonnull ISystem<ECS_TYPE>[] sortedSystems, int systemSize) {
        DependencyGraph<ECS_TYPE> graph = new DependencyGraph<ECS_TYPE>(Arrays.copyOf(sortedSystems, systemSize));
        graph.resolveEdges(registry);
        graph.sort(sortedSystems);
    }
}

