/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component.query;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ComponentRegistry;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.query.AndQuery;
import com.hypixel.hytale.component.query.AnyQuery;
import com.hypixel.hytale.component.query.NotQuery;
import com.hypixel.hytale.component.query.OrQuery;
import javax.annotation.Nonnull;

public interface Query<ECS_TYPE> {
    @Nonnull
    public static <ECS_TYPE> AnyQuery<ECS_TYPE> any() {
        return AnyQuery.INSTANCE;
    }

    @Nonnull
    public static <ECS_TYPE> NotQuery<ECS_TYPE> not(@Nonnull Query<ECS_TYPE> query) {
        return new NotQuery<ECS_TYPE>(query);
    }

    @Nonnull
    @SafeVarargs
    public static <ECS_TYPE> AndQuery<ECS_TYPE> and(Query<ECS_TYPE> ... queries) {
        return new AndQuery<ECS_TYPE>(queries);
    }

    @Nonnull
    @SafeVarargs
    public static <ECS_TYPE> OrQuery<ECS_TYPE> or(Query<ECS_TYPE> ... queries) {
        return new OrQuery<ECS_TYPE>(queries);
    }

    public boolean test(Archetype<ECS_TYPE> var1);

    public boolean requiresComponentType(ComponentType<ECS_TYPE, ?> var1);

    public void validateRegistry(@Nonnull ComponentRegistry<ECS_TYPE> var1);

    public void validate();
}

