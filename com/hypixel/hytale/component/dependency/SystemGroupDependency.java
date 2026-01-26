/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component.dependency;

import com.hypixel.hytale.component.ComponentRegistry;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.DependencyGraph;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.OrderPriority;
import com.hypixel.hytale.component.system.ISystem;
import javax.annotation.Nonnull;

public class SystemGroupDependency<ECS_TYPE>
extends Dependency<ECS_TYPE> {
    @Nonnull
    private final SystemGroup<ECS_TYPE> group;

    public SystemGroupDependency(@Nonnull Order order, @Nonnull SystemGroup<ECS_TYPE> group) {
        this(order, group, OrderPriority.NORMAL);
    }

    public SystemGroupDependency(@Nonnull Order order, @Nonnull SystemGroup<ECS_TYPE> group, int priority) {
        super(order, priority);
        this.group = group;
    }

    public SystemGroupDependency(@Nonnull Order order, @Nonnull SystemGroup<ECS_TYPE> group, @Nonnull OrderPriority priority) {
        super(order, priority);
        this.group = group;
    }

    @Nonnull
    public SystemGroup<ECS_TYPE> getGroup() {
        return this.group;
    }

    @Override
    public void validate(@Nonnull ComponentRegistry<ECS_TYPE> registry) {
        if (!registry.hasSystemGroup(this.group)) {
            throw new IllegalArgumentException("System dependency isn't registered: " + String.valueOf(this.group));
        }
    }

    @Override
    public void resolveGraphEdge(@Nonnull ComponentRegistry<ECS_TYPE> registry, @Nonnull ISystem<ECS_TYPE> thisSystem, @Nonnull DependencyGraph<ECS_TYPE> graph) {
        switch (this.order) {
            case BEFORE: {
                for (ISystem<ECS_TYPE> system : graph.getSystems()) {
                    if (!this.group.equals(system.getGroup())) continue;
                    graph.addEdge(thisSystem, system, -this.priority);
                }
                break;
            }
            case AFTER: {
                for (ISystem<ECS_TYPE> system : graph.getSystems()) {
                    if (!this.group.equals(system.getGroup())) continue;
                    graph.addEdge(system, thisSystem, this.priority);
                }
                break;
            }
        }
    }

    @Override
    @Nonnull
    public String toString() {
        return "SystemGroupDependency{group=" + String.valueOf(this.group) + "} " + super.toString();
    }
}

