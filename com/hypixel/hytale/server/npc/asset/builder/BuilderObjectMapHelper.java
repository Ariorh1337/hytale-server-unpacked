/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderContext;
import com.hypixel.hytale.server.npc.asset.builder.BuilderManager;
import com.hypixel.hytale.server.npc.asset.builder.BuilderObjectArrayHelper;
import com.hypixel.hytale.server.npc.asset.builder.BuilderObjectReferenceHelper;
import com.hypixel.hytale.server.npc.asset.builder.BuilderParameters;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.BuilderValidationHelper;
import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderObjectMapHelper<K, V>
extends BuilderObjectArrayHelper<Map<K, V>, V> {
    private Function<V, K> id;

    public BuilderObjectMapHelper(Class classType, Function<V, K> id, BuilderContext owner) {
        super(classType, owner);
        this.id = id;
    }

    @Override
    @Nullable
    public Map<K, V> build(@Nonnull BuilderSupport builderSupport) {
        if (this.hasNoElements()) {
            return null;
        }
        Object2ObjectLinkedOpenHashMap objects = new Object2ObjectLinkedOpenHashMap();
        for (BuilderObjectReferenceHelper builderObjectReferenceHelper : this.builders) {
            if (builderObjectReferenceHelper.excludeFromRegularBuild()) continue;
            Object value = builderObjectReferenceHelper.build(builderSupport);
            K key = this.id.apply(value);
            if (objects.containsKey(key)) {
                throw new IllegalArgumentException("Duplicate key \"" + String.valueOf(key) + "\" at " + this.getBreadCrumbs() + ": " + this.builderParameters.getFileName());
            }
            objects.put(key, value);
        }
        return objects;
    }

    @Override
    public void readConfig(@Nonnull JsonElement data, @Nonnull BuilderManager builderManager, @Nonnull BuilderParameters builderParameters, @Nonnull BuilderValidationHelper builderValidationHelper) {
        super.readConfig(data, builderManager, builderParameters, builderValidationHelper);
    }

    @Nullable
    public <T, U> T testEach(@Nonnull BiFunction<Builder<V>, U, T> test, @Nonnull BuilderManager builderManager, ExecutionContext executionContext, U meta, T successResult, T emptyResult, Builder<?> parentSpawnable) {
        if (this.hasNoElements()) {
            return emptyResult;
        }
        for (BuilderObjectReferenceHelper builderObjectReferenceHelper : this.builders) {
            T result = test.apply(builderObjectReferenceHelper.getBuilder(builderManager, executionContext, parentSpawnable), meta);
            if (Objects.equals(result, successResult)) continue;
            return result;
        }
        return successResult;
    }
}

