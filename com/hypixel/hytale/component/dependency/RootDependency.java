/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component.dependency;

import com.hypixel.hytale.component.ComponentRegistry;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.DependencyGraph;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.OrderPriority;
import com.hypixel.hytale.component.system.ISystem;
import java.util.Set;
import javax.annotation.Nonnull;

public class RootDependency<ECS_TYPE>
extends Dependency<ECS_TYPE> {
    private static final RootDependency<?> FIRST = new RootDependency(OrderPriority.CLOSEST);
    private static final RootDependency<?> LAST = new RootDependency(OrderPriority.FURTHEST);
    private static final Set<Dependency<?>> FIRST_SET = Set.of(FIRST);
    private static final Set<Dependency<?>> LAST_SET = Set.of(LAST);

    public static <ECS_TYPE> RootDependency<ECS_TYPE> first() {
        return FIRST;
    }

    public static <ECS_TYPE> RootDependency<ECS_TYPE> last() {
        return LAST;
    }

    public static <ECS_TYPE> Set<Dependency<ECS_TYPE>> firstSet() {
        return FIRST_SET;
    }

    public static <ECS_TYPE> Set<Dependency<ECS_TYPE>> lastSet() {
        return LAST_SET;
    }

    public RootDependency(int priority) {
        super(Order.AFTER, priority);
    }

    public RootDependency(@Nonnull OrderPriority priority) {
        super(Order.AFTER, priority);
    }

    @Override
    public void validate(@Nonnull ComponentRegistry<ECS_TYPE> registry) {
    }

    @Override
    public void resolveGraphEdge(@Nonnull ComponentRegistry<ECS_TYPE> registry, @Nonnull ISystem<ECS_TYPE> thisSystem, @Nonnull DependencyGraph<ECS_TYPE> graph) {
        if (this.order == Order.BEFORE) {
            throw new UnsupportedOperationException("RootDependency can't have Order.BEFORE!");
        }
        graph.addEdgeFromRoot(thisSystem, this.priority);
    }

    @Override
    @Nonnull
    public String toString() {
        return "SystemDependency{} " + super.toString();
    }
}

